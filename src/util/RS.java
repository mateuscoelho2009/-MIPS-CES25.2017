package util;

//import util.ArchTomassulo.STATION_ID;
import util.Instruction.INSTR_TYPE;
import util.RS.STATE;

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

	public STATE write(){
		for(int x=0;x<32;x++){
			if(Arch.r.rBeingUsedBy(x)==id_){
				Arch.r.wInt(x,ula.result);
				Arch.r.setUsed(x, -1);
			}
			for (int i=0;i<ArchTomassulo.rs.length;i++){
				if(ArchTomassulo.rs[i].Qj==id_){
					ArchTomassulo.rs[i].Vj = ula.result;
					ArchTomassulo.rs[i].Qj = -1;
				}
				if(ArchTomassulo.rs[i].Qk==id_){
					ArchTomassulo.rs[i].Vk = ula.result;
					ArchTomassulo.rs[i].Qk = -1;
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
		ArchTomassulo.incrementInstructions();
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
			if (atuInst.instr_mnemonic_.equals(Instruction.SW))
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
		/*if (hasDependencies()) {
			checkDependencies();
		}*/

		//if (isBusy()/* && !hasDependencies()*/) {
		if (!ula.tick())
			return STATE.EXECUTE;
		return STATE.WRITE;

			/*// Terminou Operacao
			if (!isBusy()) {
				switch(Op) {
				case R:
					if (atuInst.instr_mnemonic_.equals(Instruction.NOP)) break;
					for (int i = 0; i < 32; i++) {
						if (Arch.r.rBeingUsedBy(i) == id_) {
							Arch.r.clearUsed(i);
							Arch.r.wInt(i, ula.result);
							System.out.print("R Op/ R"+i+" = "+Arch.r.read(i) + " = "+ Arch.r.rInt(i));
						}
					}
					for (int i = 0; i < ArchTomassulo.rs.length; i++) {
						ArchTomassulo.rs[i].putResult(id_,ula.result);
					}
					break;
				case I:
					if (atuInst.instr_mnemonic_.equals(Instruction.ADDI)) {
						for (int i = 0; i < 32; i++) {
							if (Arch.r.rBeingUsedBy(i) == id_) {
								Arch.r.clearUsed(i);
								Arch.r.wInt(i, ula.result);
								System.out.print("ADDI/ R"+Vk+" = "+Arch.r.read(i) + " = "+ Arch.r.rInt(i));
							}
						}
						for (int i = 0; i < ArchTomassulo.rs.length; i++) {
							ArchTomassulo.rs[i].putResult(id_,ula.result);
						}
					} else if (atuInst.instr_mnemonic_.equals(Instruction.LW)) {
						if (Arch.r.rBeingUsedBy(atuInst.rt) == id_) {
							Arch.r.clearUsed(atuInst.rt);
							System.out.print("LW/ R"+Vk+" = MEM["+Vj+"+"+address+"] = ");
							Arch.r.write(Vk, Arch.m.read(Vj+address));
							System.out.print(Arch.r.read(Vk));
						}
						
						for (int i = 0; i < 32; i++) {
							if (Arch.r.rBeingUsedBy(i) == id_) {
								Arch.r.clearUsed(i);
								Arch.r.write(i, ula.auxRes);
							}
						}
						for (int i = 0; i < 2; i++) {

						}
					} else if (atuInst.instr_mnemonic_.equals(Instruction.SW)) {
						//if (Arch.m.mBeingUsedBy(Vj + atuInst.immediate) == id_)
							//Arch.m.clearUsed(Vj + atuInst.immediate);
					}
					break;
				case J:
					break;
				default:
					break;
				}
				Op = INSTR_TYPE.UNDEFINED;
				Vj = -1;
				Vk = -1;
				Qj = -1;
				Qk = -1;
				address = -1;
				hasJump = false;

				return STATE.WRITE;
			}

		}*/
		
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
