package util;

import util.Instruction.INSTR_TYPE;

public class EstacaoReserva {
	ArchTomassulo.STATION_ID id_;
	UlaTomasulo ula;
	Instruction atuInst;
	boolean busy;
	boolean hasJump;
	INSTR_TYPE Op;
	int Vj, Vk, Qj, Qk, address;

	public EstacaoReserva() {
		// TODO Auto-generated constructor stub
		ula = new UlaTomasulo();
		busy = false;
		Op = INSTR_TYPE.UNDEFINED;
		Vj = -1;
		Vk = -1;
		Qj = -1;
		Qk = -1;
		address = -1;
		hasJump = false;
	}

	public void setId(ArchTomassulo.STATION_ID id) {
		id_ = id;
	}

	public boolean isBusy() {
		return busy;
	}

	public void passInstruction(Instruction inst) {
		atuInst = inst;
		Op = inst.getType();
		busy = true;

		checkDependencies();
	}

	boolean hasDependencies () {
		switch(Op) {
		case R:
			return (Qj != -1 || Qk != -1);
		case I:
			if (atuInst.instr_mnemonic_.equals(Instruction.LW)
					|| atuInst.instr_mnemonic_.equals(Instruction.SW))
				return (Qj != -1);
			return false;
		case J:
			return false;
		default:
			return false;
		}
	}

	void checkDependencies () {
		// TODO: Checar dependencias.
		if (!isBusy()) return;

		switch(Op) {
		case R:
			if (Arch.r.rBeingUsed(atuInst.rs) && Vj == -1)
				Qj = atuInst.rs;
			else {
				Qj = -1;
				Vj = Arch.r.rInt(atuInst.rs);
			}
			if (Arch.r.rBeingUsed(atuInst.rt) && Vk == -1)
				Qk = atuInst.rt;
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
			if (atuInst.instr_mnemonic_.equals(Instruction.LW)) {
				if (Arch.m.mBeingUsed(Arch.r.rInt(atuInst.rs) + atuInst.immediate) && Vj == -1)
					Qj = Arch.r.rInt(atuInst.rs) + atuInst.immediate;
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
					Qj = atuInst.rs;
				else {
					Qj = -1;
					Vj = Arch.r.rInt(atuInst.rs);
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

	public void tick() {
		if (hasDependencies()) {
			checkDependencies();
		}

		// TODO Auto-generated method stub
		if (isBusy() && !hasDependencies()) {
			busy = !ula.tick();

			// Terminou Operacao
			if (!isBusy()) {
				switch(Op) {
				case R:
					if (Arch.r.rBeingUsedBy(atuInst.rd) == id_)
						Arch.r.clearUsed(atuInst.rd);
					break;
				case I:
					if (atuInst.instr_mnemonic_.equals(Instruction.LW)) {
						Arch.r.clearUsed(atuInst.rt);
					} else if (atuInst.instr_mnemonic_.equals(Instruction.SW)) {
						Arch.m.clearUsed(Vj + atuInst.immediate);
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
}
