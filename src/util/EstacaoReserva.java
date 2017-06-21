package util;

import util.ArchTomassulo.STATION_ID;
import util.Instruction.INSTR_TYPE;

public class EstacaoReserva {
	ArchTomassulo.STATION_ID id_;
	UlaTomasulo ula;
	Instruction atuInst;
	boolean busy;
	boolean hasJump;
	INSTR_TYPE Op;
	public int Vj, Vk, address;
	public ArchTomassulo.STATION_ID Qj, Qk;

	public EstacaoReserva() {
		// TODO Auto-generated constructor stub
		ula = new UlaTomasulo();
		busy = false;
		Op = INSTR_TYPE.UNDEFINED;
		Vj = -1;
		Vk = -1;
		Qj = STATION_ID.NONE;
		Qk = STATION_ID.NONE;
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
			if (atuInst.instr_mnemonic_.equals(Instruction.NOP)) return false;
			return (Qj != STATION_ID.NONE || Qk != STATION_ID.NONE);
		case I:
			if (atuInst.instr_mnemonic_.equals(Instruction.LW)
					|| atuInst.instr_mnemonic_.equals(Instruction.SW)
					|| atuInst.instr_mnemonic_.equals(Instruction.ADDI))
				return (Qj != STATION_ID.NONE);
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
			if (atuInst.instr_mnemonic_.equals(Instruction.NOP)) {
				break;
			}
			if (Arch.r.rBeingUsed(atuInst.rs) && Vj == -1)
				Qj = Arch.r.rBeingUsedBy(atuInst.rs);
			else {
				Qj = STATION_ID.NONE;
				Vj = Arch.r.rInt(atuInst.rs);
			}
			if (Arch.r.rBeingUsed(atuInst.rt) && Vk == -1)
				Qk = Arch.r.rBeingUsedBy(atuInst.rt);
			else {
				Qk = STATION_ID.NONE;
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
					Qj = STATION_ID.NONE;
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
					Qj = STATION_ID.NONE;
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
					Qj = STATION_ID.NONE;
					Vj = Arch.r.rInt(atuInst.rs);
				}
				if (Arch.r.rBeingUsed(atuInst.rt) && Vj == -1)
					Qk = Arch.r.rBeingUsedBy(atuInst.rt);
				else {
					Qk = STATION_ID.NONE;
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
					if (atuInst.instr_mnemonic_.equals(Instruction.NOP)) break;
					for (int i = 0; i < 32; i++) {
						if (Arch.r.rBeingUsedBy(i) == id_) {
							Arch.r.clearUsed(i);
							Arch.r.wInt(i, ula.result);
							System.out.print("R Op/ R"+i+" = "+Arch.r.read(i) + " = "+ Arch.r.rInt(i));
						}
					}
					for (int i = 0; i < 2; i++) {
						if (ArchTomassulo.add[i].Qj == id_) {
							ArchTomassulo.add[i].Qj = STATION_ID.NONE;
							ArchTomassulo.add[i].Vj = ula.result;
						}
						if (ArchTomassulo.add[i].Qk == id_) {
							ArchTomassulo.add[i].Qk = STATION_ID.NONE;
							ArchTomassulo.add[i].Vk = ula.result;
						}
						if (ArchTomassulo.mult[i].Qj == id_) {
							ArchTomassulo.mult[i].Qj = STATION_ID.NONE;
							ArchTomassulo.mult[i].Vj = ula.result;
						}
						if (ArchTomassulo.mult[i].Qk == id_) {
							ArchTomassulo.mult[i].Qk = STATION_ID.NONE;
							ArchTomassulo.mult[i].Vk = ula.result;
						}
					}
					if (ArchTomassulo.add[2].Qj == id_) {
						ArchTomassulo.add[2].Qj = STATION_ID.NONE;
						ArchTomassulo.add[2].Vj = ula.result;
					}
					if (ArchTomassulo.add[2].Qk == id_) {
						ArchTomassulo.add[2].Qk = STATION_ID.NONE;
						ArchTomassulo.add[2].Vk = ula.result;
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
						for (int i = 0; i < 2; i++) {
							if (ArchTomassulo.add[i].Qj == id_) {
								ArchTomassulo.add[i].Qj = STATION_ID.NONE;
								ArchTomassulo.add[i].Vj = ula.result;
							}
							if (ArchTomassulo.add[i].Qk == id_) {
								ArchTomassulo.add[i].Qk = STATION_ID.NONE;
								ArchTomassulo.add[i].Vk = ula.result;
							}
							if (ArchTomassulo.mult[i].Qj == id_) {
								ArchTomassulo.mult[i].Qj = STATION_ID.NONE;
								ArchTomassulo.mult[i].Vj = ula.result;
							}
							if (ArchTomassulo.mult[i].Qk == id_) {
								ArchTomassulo.mult[i].Qk = STATION_ID.NONE;
								ArchTomassulo.mult[i].Vk = ula.result;
							}
							if (ArchTomassulo.load[i].Qj == id_) {
								ArchTomassulo.load[i].Qj = STATION_ID.NONE;
								ArchTomassulo.load[i].Vj = ula.result;
							}
							if (ArchTomassulo.load[i].Qk == id_) {
								ArchTomassulo.load[i].Qk = STATION_ID.NONE;
								ArchTomassulo.load[i].Vk = ula.result;
							}
						}
						if (ArchTomassulo.add[2].Qj == id_) {
							ArchTomassulo.add[2].Qj = STATION_ID.NONE;
							ArchTomassulo.add[2].Vj = ula.result;
						}
						if (ArchTomassulo.add[2].Qk == id_) {
							ArchTomassulo.add[2].Qk = STATION_ID.NONE;
							ArchTomassulo.add[2].Vk = ula.result;
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
							if (ArchTomassulo.load[i].Qj == id_) {
								ArchTomassulo.load[i].Qj = STATION_ID.NONE;
								ArchTomassulo.load[i].Vj = ula.result;
							}
							if (ArchTomassulo.load[i].Qk == id_) {
								ArchTomassulo.load[i].Qk = STATION_ID.NONE;
								ArchTomassulo.load[i].Vk = ula.result;
							}
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
				Qj = STATION_ID.NONE;
				Qk = STATION_ID.NONE;
				address = -1;
				hasJump = false;
			}
		}
	}
	
	boolean hasJump () {
		return hasJump;
	}
}
