package util;

//import util.Instruction.INSTR_TYPE;

public class Rs {
	
	public static enum TYPE {NONE,LOAD,ADD,MULT}
	public static enum STATE {FREE,ISSUE,EXECUTE,WRITE}
	int id_;
	Ula ula;
	Instruction atuInst;
	boolean busy;
	//STATE state;
	//boolean hasJump;
	//INSTR_TYPE Op;
	public int Vj, Vk, A;
	public int Qj, Qk, dest;
	//boolean firstTimeIssue = true;
	protected TYPE _type = TYPE.NONE;

	public Rs(int id, TYPE type) {
		_type = type;
		id_ = id;
		ula = new Ula();
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

	public boolean isBusy() {
		return busy;
	}
	public void setBusy(boolean b) {
		busy = b;	
	}

	public void setDest(int b) {
		dest = b;
		
	}
	//public STATE getState(){
	//	return state;
	//}
	public TYPE getType() {
		return _type;
	}
	/*public void tick(Instruction inst){
		if(state==STATE.FREE) {
			state=issue(inst);
			atuInst = inst;
		}
		else
			System.out.println("Erro!");
	}
	
	public void tick(){
		switch(state){
		case FREE:
			//System.out.println("NÃ£o era pra estar aqui!");
			//state=issue(ArchTomassulo.inst);
			break;
		case ISSUE:
			state=issue(atuInst);
			break;
		case EXECUTE:
			state=execute();
			break;		
		case WRITE:
			state=write();
			Op = INSTR_TYPE.UNDEFINED;
			Vj = -1;
			Vk = -1;
			Qj = -1;
			Qk = -1;
			A = -1;
			hasJump = false;
			firstTimeIssue = true;
			atuInst = null;
		}			
	}
	
	public void tick(ReorderBuffer rob){
		switch(state){
		case FREE:
			//System.out.println("NÃ£o era pra estar aqui!");
			//state=issue(ArchTomassulo.inst);
			break;
		case ISSUE:
			state=issue(atuInst);
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
			atuInst = null;
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
	
	public Object[] getInfo() {
		return new Object[] {id_, _type, isBusy(), (atuInst != null)? atuInst.instr_mnemonic_:"",  Vj, Vk, Qj, Qk, A, state};
	}

	public STATE issue(Instruction inst) {
		atuInst = inst;
		Op = inst.getType();
		checkDependencies();
		//ula.set(inst,Vj,Vk);
		return STATE.ISSUE;
	}

	boolean hasDependencies () {
		switch(Op) {
		case R:
			if (atuInst.instr_mnemonic_.equals(Instruction.NOP)) return false;
			return (Qj != -1 || Qk != -1);
		case I:
			if (atuInst.instr_mnemonic_.equals(Instruction.LW)
					|| atuInst.instr_mnemonic_.equals(Instruction.ADDI))
				return (Qj != -1);
			if (atuInst.instr_mnemonic_.equals(Instruction.SW)||
					atuInst.instr_mnemonic_.equals(Instruction.BLE)||
					atuInst.instr_mnemonic_.equals(Instruction.BNE)||
					atuInst.instr_mnemonic_.equals(Instruction.BEQ))
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
			if (atuInst.instr_mnemonic_.equals(Instruction.NOP)) {
				break;
			}
			if (Arch.RegisterStat.rBeingUsed(atuInst.rs) && Vj == -1)
				Qj = Arch.RegisterStat.rBeingUsedBy(atuInst.rs);
			else {
				Qj = -1;
				Vj = Arch.RegisterStat.rInt(atuInst.rs);
			}
			if (Arch.RegisterStat.rBeingUsed(atuInst.rt) && Vk == -1)
				Qk = Arch.RegisterStat.rBeingUsedBy(atuInst.rt);
			else {
				Qk = -1;
				Vk = Arch.RegisterStat.rInt(atuInst.rt);
			}
			if (!hasDependencies()) {
				ula.set(atuInst, Vj, Vk);
				Arch.RegisterStat.setUsed(atuInst.rd, id_);
			}
			break;
		case I:
			if (atuInst.instr_mnemonic_.equals(Instruction.ADDI)) {
				if (Arch.RegisterStat.rBeingUsed(atuInst.rs) && Vj == -1)
					Qj = Arch.RegisterStat.rBeingUsedBy(atuInst.rs);
				else {
					Qj = -1;
					Vj = Arch.RegisterStat.rInt(atuInst.rs);
				}
				if (!hasDependencies()) {
					Vk = atuInst.rt;
					ula.set(atuInst, Vj, Vk);
					Arch.RegisterStat.setUsed(Vk, id_);
				}
			} else if (atuInst.instr_mnemonic_.equals(Instruction.LW)) {
				if (Arch.RegisterStat.rBeingUsed(atuInst.rs) && Vj == -1)
					Qj = Arch.RegisterStat.rBeingUsedBy(atuInst.rs);
				else {
					Qj = -1;
					Vj = Arch.RegisterStat.rInt(atuInst.rs);
				}
				if (!hasDependencies()) {
					A = (Vj+atuInst.immediate);
					Vk = atuInst.rt;
					ula.set(atuInst, Vj, Vk);
					Arch.RegisterStat.setUsed(atuInst.rt, id_);
				}
			} else if (atuInst.instr_mnemonic_.equals(Instruction.SW)) {
				if (Arch.RegisterStat.rBeingUsed(atuInst.rs) && Vj == -1)
					Qj = Arch.RegisterStat.rBeingUsedBy(atuInst.rs);
				else {
					Qj = -1;
					Vj = Arch.RegisterStat.rInt(atuInst.rs);
				}
				if (Arch.RegisterStat.rBeingUsed(atuInst.rt) && Vj == -1)
					Qk = Arch.RegisterStat.rBeingUsedBy(atuInst.rt);
				else {
					Qk = -1;
					Vk = Arch.RegisterStat.rInt(atuInst.rt);
				}
				if (!hasDependencies()) {
					A = (Vj+atuInst.immediate);
					Vk = atuInst.rt;
					ula.set(atuInst, Vj, Vk);
					Arch.Mem.setUsed(Vj + atuInst.immediate, id_);
				}
			}
			else {
				ula.set(atuInst);
				hasJump = true;
			}
			break;
		case J:
			ula.set(atuInst);
			hasJump = true;
			break;
		default:
			ula.set(atuInst);
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



}
