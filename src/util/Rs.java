package util;

//import util.Instruction.INSTR_TYPE;

public class Rs {
	
	public static enum TYPE {NONE,LOAD,ADD,MULT}
	int id_;
	//Ula ula;
	Instruction inst;
	boolean busy;
	Instruction.STATE state;
	public int Vj, Vk, A;
	public int Qj, Qk, dest;
	protected TYPE _type = TYPE.NONE;

	public Rs(int id, TYPE type) {
		_type = type;
		id_ = id;
		busy = false;
		inst = null;
		state = Instruction.STATE.FREE;
		Vj = -1;
		Vk = -1;
		Qj = -1;
		Qk = -1;
		A = -1;
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
	public void clearRS(){
		busy = false;
		inst = null;
		state = Instruction.STATE.FREE;
		Vj = -1;
		Vk = -1;
		Qj = -1;
		Qk = -1;
		A = -1;
	}
	public void updateState() {
		if(inst!=null)
			state = inst.getState();
		else
			state = Instruction.STATE.FREE;
	}
}
