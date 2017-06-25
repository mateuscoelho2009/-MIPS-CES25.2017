package util;

//import util.ArchTomassulo.STATION_ID;
import util.Instruction.INSTR_TYPE;

public class RS {
	
	public static enum TYPE {NONE,LOAD,ADD,MULT}
	public static enum STATE {FREE,ISSUE,EXECUTE,WRITE}
	int id_;
	UlaT ula;
	Instruction atuInst;
	//boolean busy;
	STATE state;
	boolean hasJump;
	INSTR_TYPE Op;
	public int Vj, Vk, address;
	public int Qj, Qk;
	boolean firstTimeIssue = true;
	protected TYPE _type = TYPE.NONE;

	public RS(int id) {
		id_ = id;
		ula = new UlaT();
		state = STATE.FREE;
		//busy = false;
		Op = INSTR_TYPE.UNDEFINED;
		Vj = -1;
		Vk = -1;
		Qj = -1;
		Qk = -1;
		address = -1;
		hasJump = false;
	}

	public boolean isBusy() {
		if(state==STATE.FREE)
			return false;
		return true;
	}
	public void tick(Instruction inst){
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
			address = -1;
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
			address = -1;
			hasJump = false;
			firstTimeIssue = true;
			atuInst = null;
		}			
	}

	public STATE write(){
		for(int x=0;x<32;x++){
			if(Arch.r.rBeingUsedBy(x)==id_){
				Arch.r.wInt(x,ula.result);
				Arch.r.setUsed(x, -1);
			}
			for (int i=0;i<ArchTomasulo.rs.length;i++){
				if(ArchTomasulo.rs[i].Qj==id_){
					ArchTomasulo.rs[i].Vj = ula.result;
					ArchTomasulo.rs[i].Qj = -1;
				}
				if(ArchTomasulo.rs[i].Qk==id_){
					ArchTomasulo.rs[i].Vk = ula.result;
					ArchTomasulo.rs[i].Qk = -1;
				}
    		}			
		}

		Op = INSTR_TYPE.UNDEFINED;
		Vj = -1;
		Vk = -1;
		Qj = -1;
		Qk = -1;
		address = -1;
		hasJump = false;
		ArchTomasulo.incrementInstructions();
		return STATE.FREE;
	}
	
	public STATE write(ReorderBuffer rob){
		rob.getResult(this);
		for(int x=0;x<32;x++){
			if(Arch.r.rBeingUsedBy(x)==id_){
				Arch.r.setUsed(x, 0);
			}
			for (int i=0;i<ArchTomasulo.rs.length;i++){
				if(ArchTomasulo.rs[i].Qj==id_){
					ArchTomasulo.rs[i].Vj = ula.result;
					ArchTomasulo.rs[i].Qj = -1;
				}
				if(ArchTomasulo.rs[i].Qk==id_){
					ArchTomasulo.rs[i].Vk = ula.result;
					ArchTomasulo.rs[i].Qk = -1;
				}
    		}
			
		}

		Op = INSTR_TYPE.UNDEFINED;
		Vj = -1;
		Vk = -1;
		Qj = -1;
		Qk = -1;
		address = -1;
		hasJump = false;
		ArchTomasulo.incrementInstructions();
		return STATE.FREE;
	}
	
	public Object[] getInfo() {
		return new Object[] {id_, _type, isBusy(), (atuInst != null)? atuInst.instr_mnemonic_:"",  Vj, Vk, Qj, Qk, address, state};
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
			if (Arch.r.rBeingUsed(atuInst.rs) && Vj == -1)
				Qj = Arch.r.rBeingUsedBy(atuInst.rs);
			else {
				Qj = -1;
				Vj = Arch.r.rInt(atuInst.rs);
			}
			if (Arch.r.rBeingUsed(atuInst.rt) && Vk == -1)
				Qk = Arch.r.rBeingUsedBy(atuInst.rt);
			else {
				Qk = -1;
				Vk = Arch.r.rInt(atuInst.rt);
			}
			if (!hasDependencies()) {
				ula.set(atuInst, Vj, Vk);
				Arch.r.setUsed(atuInst.rd, id_);
			}
			break;
		case I:
			if (atuInst.instr_mnemonic_.equals(Instruction.ADDI)) {
				if (Arch.r.rBeingUsed(atuInst.rs) && Vj == -1)
					Qj = Arch.r.rBeingUsedBy(atuInst.rs);
				else {
					Qj = -1;
					Vj = Arch.r.rInt(atuInst.rs);
				}
				if (!hasDependencies()) {
					Vk = atuInst.rt;
					ula.set(atuInst, Vj, Vk);
					Arch.r.setUsed(Vk, id_);
				}
			} else if (atuInst.instr_mnemonic_.equals(Instruction.LW)) {
				if (Arch.r.rBeingUsed(atuInst.rs) && Vj == -1)
					Qj = Arch.r.rBeingUsedBy(atuInst.rs);
				else {
					Qj = -1;
					Vj = Arch.r.rInt(atuInst.rs);
				}
				if (!hasDependencies()) {
					address = (Vj+atuInst.immediate);
					Vk = atuInst.rt;
					ula.set(atuInst, Vj, Vk);
					Arch.r.setUsed(atuInst.rt, id_);
				}
			} else if (atuInst.instr_mnemonic_.equals(Instruction.SW)) {
				if (Arch.r.rBeingUsed(atuInst.rs) && Vj == -1)
					Qj = Arch.r.rBeingUsedBy(atuInst.rs);
				else {
					Qj = -1;
					Vj = Arch.r.rInt(atuInst.rs);
				}
				if (Arch.r.rBeingUsed(atuInst.rt) && Vj == -1)
					Qk = Arch.r.rBeingUsedBy(atuInst.rt);
				else {
					Qk = -1;
					Vk = Arch.r.rInt(atuInst.rt);
				}
				if (!hasDependencies()) {
					address = (Vj+atuInst.immediate);
					Vk = atuInst.rt;
					ula.set(atuInst, Vj, Vk);
					Arch.m.setUsed(Vj + atuInst.immediate, id_);
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
	public STATE getState(){
		return state;
	}
	public TYPE type() {
		return _type;
	}
}
