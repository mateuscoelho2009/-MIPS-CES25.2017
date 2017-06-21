package util;

import util.Instruction.INSTR_TYPE;

public class EstacaoReserva {
	ArchTomassulo.STATION_ID id_;
	UlaTomasulo ula;
	Instruction atuInst;
	boolean busy;
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
		return (Qj == -1) || (Qk == -1);
	}

	void checkDependencies () {
		// TODO: Checar dependencias.
		if (!isBusy()) return;

		switch(Op) {
		case R:
			if (Arch.r.rBeingUsed(atuInst.rs))
				Qj = atuInst.rs;
			else {
				Qj = -1;
				Vj = Arch.r.rInt(atuInst.rs);
			}
			if (Arch.r.rBeingUsed(atuInst.rt))
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

			break;
		case J:
			break;
		default:
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
					
					break;
				case J:
					break;
				default:
					break;
				}
			}
		}
	}
}
