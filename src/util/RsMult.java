package util;

import util.Instruction.INSTR_TYPE;
import util.RS.STATE;
import util.RS.TYPE;

public class RsMult extends RS {

	public RsMult(int id) {
		super(id);
		_type = TYPE.MULT;
	}

	public STATE issue(Instruction inst) {
		Op = inst.getType();
		atuInst = inst;
		if (inst.instr_mnemonic_.equals(Instruction.NOP)
				|| inst.instr_mnemonic_.equals(Instruction.JMP)) {
			ula.set(inst);
			return STATE.EXECUTE;
		}
		else if (inst.instr_mnemonic_.equals(Instruction.MUL)) {
			if (firstTimeIssue) {
				if(Arch.r.rBeingUsed(inst.rs))
					Qj = Arch.r.rBeingUsedBy(inst.rs);
				else {
					Vj = Arch.r.rInt(inst.rs);
					Qj = -1;
				}
				if(Arch.r.rBeingUsed(inst.rt))
					Qk = Arch.r.rBeingUsedBy(inst.rt);
				else {
					Vk = Arch.r.rInt(inst.rt);
					Qk = -1;
				}
				Arch.r.setUsed(inst.rd,id_);
				firstTimeIssue = false;
			}
			if (!hasDependencies()) {
				ula.set(inst,Vj,Vk);
				return STATE.EXECUTE;
			}
			return STATE.ISSUE;
		}
		else {
			hasJump = true;
			ula.set(inst);
			return STATE.EXECUTE;
		}
	}
	public STATE execute() {
		System.out.println("Qj: " + Qj + " - Qk: " + Qk);
		if(Qj == -1 && Qk == -1){
			System.out.println("Vj: " + Vj + " - Vk: " + Vk);
			if (!ula.tick())
				return STATE.EXECUTE;
			return STATE.WRITE;
		}
		return STATE.EXECUTE;
	}
}
