package util;

//import util.Instruction.INSTR_TYPE;

public class Rs {
	
	public static enum TYPE {NONE,LOAD,ADD,MULT}
	int id_;
	//Ula ula;
	Instruction inst;
	boolean busy;
	Instruction.STATE state;
	//boolean hasJump;
	//INSTR_TYPE Op;
	public int Vj, Vk, A;
	public int Qj, Qk, dest;
	//boolean firstTimeIssue = true;
	protected TYPE _type = TYPE.NONE;

	public Rs(int id, TYPE type) {
		_type = type;
		id_ = id;
		//ula = new Ula();
		//state = STATE.FREE;
		busy = false;
		//Op = INSTR_TYPE.UNDEFINED;
		Vj = -1;
		Vk = -1;
		Qj = -1;
		Qk = -1;
		A = -1;
		//hasJump = false;
	}
	public void issue(Instruction inst) {
		this.inst = inst;
		inst.issue(id_);
	}
	public boolean isBusy() {
		return busy;
	}
	public void setBusy(boolean b) {
		busy = b;	
	}

	public void setDest(int b) {
		dest = b;
	}
	public Object[] getInfo() {
		return new Object[] {id_, _type, isBusy(), (inst != null)? inst.getMnemonic():"",  Vj, Vk, Qj, Qk, A, state};
	}
	public TYPE getType() {
		return _type;
	}
	public void tick(){
		updateState();
		switch(state){
		case FREE:
			System.out.println("RS Livre!");
			break;
		case ISSUE:
			if(inst.done) {
				if(inst.getMnemonic().equals(Instruction.ADD)||
						inst.getMnemonic().equals(Instruction.ADDI)||
						inst.getMnemonic().equals(Instruction.MUL)||
						inst.getMnemonic().equals(Instruction.NOP)||
						inst.getMnemonic().equals(Instruction.SUB)){
					if(Qj==-1 && Qk==-1)
						inst.execute(Vj, Vk, A, id_);
				}
				else if(inst.getMnemonic().equals(Instruction.LW)){
					//Step1
					if(!inst.lw_step2){
						if(Qj==-1 && !Arch.ROB.isHeadStore()){
							inst.execute(Vj, Vk, A, id_);
						}
					}else{
					//Step 2
						if(!Arch.ROB.haveStoreWithAddress(A)){
							inst.execute(Vj, Vk, A, id_);
						}
					}
				}
				else if(inst.getMnemonic().equals(Instruction.SW)){
					if(Qj==-1)
						inst.execute(Vj, Vk, A, id_);
				}
				else {
					inst.execute(Vj, Vk, A, id_);
				}
			} else {System.out.println("Erro no Issue!");}
			break;
		case EXECUTE:
			if(!inst.done) {
				if(inst.getMnemonic().equals(Instruction.ADD)||
						inst.getMnemonic().equals(Instruction.ADDI)||
						inst.getMnemonic().equals(Instruction.MUL)||
						inst.getMnemonic().equals(Instruction.NOP)||
						inst.getMnemonic().equals(Instruction.SUB)){
					if(Qj==-1 && Qk==-1)
						inst.execute(Vj, Vk, A, id_);
				}
				else if(inst.getMnemonic().equals(Instruction.LW)){
					//Step1
					if(!inst.lw_step2){
						if(Qj==-1 && !Arch.ROB.isHeadStore()){
							inst.execute(Vj, Vk, A, id_);
						}
					}else{
					//Step 2
						if(!Arch.ROB.haveStoreWithAddress(A)){
							inst.execute(Vj, Vk, A, id_);
						}
					}
				}
				else if(inst.getMnemonic().equals(Instruction.SW)){
					if(Qj==-1)
						inst.execute(Vj, Vk, A, id_);
				}
				else {
					inst.execute(Vj, Vk, A, id_);
				}
			} else {
				if(!inst.getMnemonic().equals(Instruction.SW)){
					inst.write(id_);
				}else{
					if(Qk==-1)
						inst.write(id_);
				}
				setBusy(false);
				inst=null;
			}
			break;
		//case WRITE:
		default:
			System.out.println("Lost! 404");
			break;
		}			
		
	}
	/*public void tick(Instruction inst){
		if(state==STATE.FREE) {
			state=issue(inst);
			inst = inst;
		}
		else
			System.out.println("Erro!");
	}

	
	public void tick(ReorderBuffer rob){
		switch(state){
		case FREE:
			//System.out.println("NÃ£o era pra estar aqui!");
			//state=issue(ArchTomassulo.inst);
			break;
		case ISSUE:
			state=issue(inst);
			break;
		case EXECUTE:
			state=execute();
			break;		
		case WRITE:
			state=write(rob);
			Op = INSTR_TYPE.UNDEFINED;
			Vj = -1;
			Vk = -1;
			Qj = -1;
			Qk = -1;
			A = -1;
			hasJump = false;
			firstTimeIssue = true;
			inst = null;
		}			
	}

	public STATE write(){
		for(int x=0;x<32;x++){
			if(Arch.RegisterStat.rBeingUsedBy(x)==id_){
				Arch.RegisterStat.wInt(x,ula.result);
				Arch.RegisterStat.setUsed(x, -1);
			}
			for (int i=0;i<Arch.rs.length;i++){
				if(Arch.rs[i].Qj==id_){
					Arch.rs[i].Vj = ula.result;
					Arch.rs[i].Qj = -1;
				}
				if(Arch.rs[i].Qk==id_){
					Arch.rs[i].Vk = ula.result;
					Arch.rs[i].Qk = -1;
				}
    		}			
		}

		Op = INSTR_TYPE.UNDEFINED;
		Vj = -1;
		Vk = -1;
		Qj = -1;
		Qk = -1;
		A = -1;
		hasJump = false;
		Arch.incrementInstructions();
		return STATE.FREE;
	}
	
	public STATE write(ReorderBuffer rob){
		rob.getResult(this);
		for(int x=0;x<32;x++){
			if(Arch.RegisterStat.rBeingUsedBy(x)==id_){
				Arch.RegisterStat.setUsed(x, 0);
			}
			for (int i=0;i<Arch.rs.length;i++){
				if(Arch.rs[i].Qj==id_){
					Arch.rs[i].Vj = ula.result;
					Arch.rs[i].Qj = -1;
				}
				if(Arch.rs[i].Qk==id_){
					Arch.rs[i].Vk = ula.result;
					Arch.rs[i].Qk = -1;
				}
    		}
			
		}

		Op = INSTR_TYPE.UNDEFINED;
		Vj = -1;
		Vk = -1;
		Qj = -1;
		Qk = -1;
		A = -1;
		hasJump = false;
		Arch.incrementInstructions();
		return STATE.FREE;
	}
	


	public STATE issue(Instruction inst) {
		inst = inst;
		Op = inst.getType();
		checkDependencies();
		//ula.set(inst,Vj,Vk);
		return STATE.ISSUE;
	}

	boolean hasDependencies () {
		switch(Op) {
		case R:
			if (inst.instr_mnemonic_.equals(Instruction.NOP)) return false;
			return (Qj != -1 || Qk != -1);
		case I:
			if (inst.instr_mnemonic_.equals(Instruction.LW)
					|| inst.instr_mnemonic_.equals(Instruction.ADDI))
				return (Qj != -1);
			if (inst.instr_mnemonic_.equals(Instruction.SW)||
					inst.instr_mnemonic_.equals(Instruction.BLE)||
					inst.instr_mnemonic_.equals(Instruction.BNE)||
					inst.instr_mnemonic_.equals(Instruction.BEQ))
				return (Qj != -1 || Qk != -1);
			return false;
		case J: default:
			return false;
		}
	}

	public void checkDependencies () {
		if (!isBusy()) return;

		switch(Op) {
		case R:
			if (inst.instr_mnemonic_.equals(Instruction.NOP)) {
				break;
			}
			if (Arch.RegisterStat.rBeingUsed(inst.rs) && Vj == -1)
				Qj = Arch.RegisterStat.rBeingUsedBy(inst.rs);
			else {
				Qj = -1;
				Vj = Arch.RegisterStat.rInt(inst.rs);
			}
			if (Arch.RegisterStat.rBeingUsed(inst.rt) && Vk == -1)
				Qk = Arch.RegisterStat.rBeingUsedBy(inst.rt);
			else {
				Qk = -1;
				Vk = Arch.RegisterStat.rInt(inst.rt);
			}
			if (!hasDependencies()) {
				ula.set(inst, Vj, Vk);
				Arch.RegisterStat.setUsed(inst.rd, id_);
			}
			break;
		case I:
			if (inst.instr_mnemonic_.equals(Instruction.ADDI)) {
				if (Arch.RegisterStat.rBeingUsed(inst.rs) && Vj == -1)
					Qj = Arch.RegisterStat.rBeingUsedBy(inst.rs);
				else {
					Qj = -1;
					Vj = Arch.RegisterStat.rInt(inst.rs);
				}
				if (!hasDependencies()) {
					Vk = inst.rt;
					ula.set(inst, Vj, Vk);
					Arch.RegisterStat.setUsed(Vk, id_);
				}
			} else if (inst.instr_mnemonic_.equals(Instruction.LW)) {
				if (Arch.RegisterStat.rBeingUsed(inst.rs) && Vj == -1)
					Qj = Arch.RegisterStat.rBeingUsedBy(inst.rs);
				else {
					Qj = -1;
					Vj = Arch.RegisterStat.rInt(inst.rs);
				}
				if (!hasDependencies()) {
					A = (Vj+inst.immediate);
					Vk = inst.rt;
					ula.set(inst, Vj, Vk);
					Arch.RegisterStat.setUsed(inst.rt, id_);
				}
			} else if (inst.instr_mnemonic_.equals(Instruction.SW)) {
				if (Arch.RegisterStat.rBeingUsed(inst.rs) && Vj == -1)
					Qj = Arch.RegisterStat.rBeingUsedBy(inst.rs);
				else {
					Qj = -1;
					Vj = Arch.RegisterStat.rInt(inst.rs);
				}
				if (Arch.RegisterStat.rBeingUsed(inst.rt) && Vj == -1)
					Qk = Arch.RegisterStat.rBeingUsedBy(inst.rt);
				else {
					Qk = -1;
					Vk = Arch.RegisterStat.rInt(inst.rt);
				}
				if (!hasDependencies()) {
					A = (Vj+inst.immediate);
					Vk = inst.rt;
					ula.set(inst, Vj, Vk);
					Arch.Mem.setUsed(Vj + inst.immediate, id_);
				}
			}
			else {
				ula.set(inst);
				hasJump = true;
			}
			break;
		case J:
			ula.set(inst);
			hasJump = true;
			break;
		default:
			ula.set(inst);
			break;
		}
	}

	public STATE execute() {
		System.out.println("RS.execute()");
		System.out.println("Qj: " + Qj + " - Qk: " + Qk);
		System.out.println("Vj: " + Vj + " - Vk: " + Vk);
		if (!ula.tick())
			return STATE.EXECUTE;
		return STATE.WRITE;		
	}
	
	boolean hasJump () {
		return hasJump;
	}
	
	public void putResult(int id, int result){
		if (this.Qj == id) {
			this.Qj = -1;
			this.Vj = result;
		}
		if (this.Qk == id) {
			this.Qk = -1;
			this.Vk = result;
		}
	}
	*/
	public void updateState() {
		if(inst!=null)
			state = inst.getState();
		else
			state = Instruction.STATE.FREE;
	}
}
