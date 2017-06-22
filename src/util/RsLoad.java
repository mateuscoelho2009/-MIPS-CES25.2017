package util;

import util.Instruction.INSTR_TYPE;
import util.RS.STATE;
import util.RS.TYPE;

public class RsLoad extends RS {

	public RsLoad(int id) {
		super(id);
		_type = TYPE.LOAD;
	}
	public STATE issue(Instruction inst) {
		if (inst.instr_mnemonic_.equals(Instruction.NOP)
				|| inst.instr_mnemonic_.equals(Instruction.JMP)) {
			ula.set(inst);
			return STATE.EXECUTE;
		}
		else if (inst.instr_mnemonic_.equals(Instruction.LW)
				|| inst.instr_mnemonic_.equals(Instruction.SW)
				|| inst.instr_mnemonic_.equals(Instruction.ADDI)) {
			if(Arch.r.rBeingUsed(inst.rs))
				Qj = Arch.r.rBeingUsedBy(inst.rs);
			else {
				Vj = Arch.r.rInt(inst.rs);
				Qj = -1;
			}
			address = inst.immediate;
			if (!hasDependencies()) {
				if(inst.getMnemonic()=="100011"
						|| inst.instr_mnemonic_.equals(Instruction.ADDI)) { // LW
					Arch.r.setUsed(inst.rt,id_);
				}
				if(inst.getMnemonic()=="101011") { // SW
					if(Arch.r.rBeingUsed(inst.rt)){
						Qk = Arch.r.rBeingUsedBy(inst.rt);
						return STATE.ISSUE;
					} else {
						Vk = Arch.r.rInt(inst.rt);
						Qk = -1;
					}
				}
				ula.set(inst, Vj, Vk);
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
	public STATE write(){
		
		if(ula.mnemonic=="101011"){
			Arch.m.write(address,String.format("%16s", Integer.toBinaryString(Vk)).replace(' ', '0'));
		}
		else{
			for(int x=0;x<32;x++){
				if(Arch.r.rBeingUsedBy(x)==id_){
					Arch.r.wInt(x,ula.result);
					Arch.r.setUsed(x, -1);
				}
				if(Qj==id_){
					Vj = ula.result;
					Qj = -1;
				}
				if(Qk==id_){
					Vk = ula.result;
					Qk = -1;
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
		return STATE.FREE;
	}
}
