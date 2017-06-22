package util;

import util.Instruction.INSTR_TYPE;

public class RsAdd extends RS {
	public RsAdd(int id) {
		super(id);
		// TODO Auto-generated constructor stub
	}

	public void tick() {
		if (hasDependencies()) {
			checkDependencies();
		}
		if (isBusy() && !hasDependencies()) {
			busy = !ula.tick();
			// Terminou Operacao
			if (!isBusy()) {
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
}
