package util;

//import util.ArchTomassulo.STATION_ID;
import util.Instruction.INSTR_TYPE;
import util.RS.TYPE;

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
	private TYPE _type = TYPE.NONE;

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
	
	public void tick(){
		switch(state){
		case FREE:
			state=issue(atuInst);
			break;
		case ISSUE:
			state=execute();
			break;
		case EXECUTE:
			state=write();				
			break;		
		case WRITE:
			state=STATE.FREE;
		}			

			
	}

	public STATE write() {
		// TODO Auto-generated method stub
		return STATE.WRITE;
	}

	public STATE issue(Instruction inst) {
		atuInst = inst;
		Op = inst.getType();
		checkDependencies();
		return STATE.ISSUE;
	}

	boolean hasDependencies () {
		switch(Op) {
		case R:
			if (atuInst.instr_mnemonic_.equals(Instruction.NOP)) return false;
			return (Qj != -1 || Qk != -1);
		case I:
			if (atuInst.instr_mnemonic_.equals(Instruction.LW)
					|| atuInst.instr_mnemonic_.equals(Instruction.SW)
					|| atuInst.instr_mnemonic_.equals(Instruction.ADDI))
				return (Qj != -1);
			return false;
		case J:
			return false;
		default:
			return false;
		}
	}

	void checkDependencies () {
		// TODO: ???.
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
					Vk = Arch.r.rInt(atuInst.rs);
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
		if (hasDependencies()) {
			checkDependencies();
		}

		if (isBusy() && !hasDependencies()) {
			busy = !ula.tick();

			// Terminou Operacao
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
			}
		}
	}
	
	boolean hasJump () {
		return hasJump;
	}
	
	public void putResult(ArchTomassulo.STATION_ID id, int result){
		if (this.Qj == id) {
			this.Qj = -1;
			this.Vj = result;
		}
		if (this.Qk == id) {
			this.Qk = -1;
			this.Vk = result;
		}
	}

	public TYPE type() {
		return _type;
	}
}
