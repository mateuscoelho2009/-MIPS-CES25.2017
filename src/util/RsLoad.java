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
		Op = inst.getType();
		atuInst = inst;
		if (inst.instr_mnemonic_.equals(Instruction.NOP)
				|| inst.instr_mnemonic_.equals(Instruction.JMP)) {
			ula.set(inst);
			return STATE.EXECUTE;
		}
		else if (inst.instr_mnemonic_.equals(Instruction.LW)
				|| inst.instr_mnemonic_.equals(Instruction.SW)
				|| inst.instr_mnemonic_.equals(Instruction.ADDI)) {
			if (firstTimeIssue) {
				if(Arch.r.rBeingUsed(inst.rs))
					Qj = Arch.r.rBeingUsedBy(inst.rs);
				else {
					Vj = Arch.r.rInt(inst.rs);
					Qj = -1;
				}
			}
			if(inst.getMnemonic().equals("100011")
					|| inst.getMnemonic().equals(Instruction.ADDI)) { // LW
				if (firstTimeIssue) {
					Arch.r.setUsed(inst.rt,id_);
					firstTimeIssue = false;
				}
				Vk = inst.rt;
				if(inst.getMnemonic().equals("100011")){
					address = inst.immediate;
					address = Vj+address;
				}
			}
			if(inst.getMnemonic().equals("101011")) { // SW
				if (firstTimeIssue) {
					if(Arch.r.rBeingUsed(inst.rt)){
						Qk = Arch.r.rBeingUsedBy(inst.rt);
						return STATE.ISSUE;
					} else {
						Vk = Arch.r.rInt(inst.rt);
						Qk = -1;
						address = inst.immediate;
						address += Vj;
					}
					firstTimeIssue = false;
				}
			}
			if (!hasDependencies()) {
				address = Vj + atuInst.immediate;
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
		
		if(ula.mnemonic.equals("101011")){
			if(Qk == -1) {
				Arch.m.write(Vj + atuInst.immediate,String.format("%16s", Integer.toBinaryString(Vk)).replace(' ', '0'));
				System.out.println("MEM[" + address + "] = " + String.format("%16s", Integer.toBinaryString(Vk)).replace(' ', '0'));
			}
			else 
				return STATE.WRITE;
		}
		else{
			for(int x=0;x<32;x++){
				if(Arch.r.rBeingUsedBy(x)==id_){
					Arch.r.wInt(x,ula.result);
					Arch.r.setUsed(x, -1);
				}
			}
			for (int i=0;i<ArchTomasulo.rs.length;i++){
				if(ArchTomasulo.rs[i].Qj==id_){
					ArchTomasulo.rs[i].Vj = ula.result;
					ArchTomasulo.rs[i].Qj = -1;
				}
				if(ArchTomasulo.rs[i].Qk==id_){
					ArchTomasulo.rs[i].Vk = ula.result;
					ArchTomasulo.rs[i].Qk = -1;
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
		firstTimeIssue = true;
		return STATE.FREE;
	}
	

	public STATE execute() {
		if (Qj == -1 && Qk == -1) {
			if (!ula.tick())
				return STATE.EXECUTE;
			if(ula.mnemonic.equals("100011")){
				address = Vj+atuInst.immediate;
				//ula.result = Integer.parseInt(Arch.m.read(address), 2);
			}
			//RS[r].A ← RS[r].Vj + RS[r].A; 
			//Read from Mem[RS[r].A]
			// Terminou Operacao

			return STATE.WRITE;
		}
		return STATE.WRITE;
	}
}
