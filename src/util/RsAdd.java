package util;

import util.Instruction.INSTR_TYPE;
import util.RS.STATE;

public class RsAdd extends RS {
	public RsAdd(int id) {
		super(id);
		_type = TYPE.ADD;
	}
	public STATE issue(Instruction inst) {
		Op = inst.getType();
		atuInst = inst;
		if (inst.instr_mnemonic_.equals(Instruction.NOP)
				|| inst.instr_mnemonic_.equals(Instruction.JMP)) {
			ula.set(inst);
			return STATE.EXECUTE;
		}
		else if (inst.instr_mnemonic_.equals(Instruction.ADD)
				|| inst.instr_mnemonic_.equals(Instruction.SUB)) {
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
		if(Qj == -1 && Qk == -1){
			if (!ula.tick())
				return STATE.EXECUTE;
		}
		return STATE.WRITE;
	}	
}
