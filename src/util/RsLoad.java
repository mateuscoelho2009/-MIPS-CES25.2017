package util;

import util.RS.STATE;
import util.RS.TYPE;

public class RsLoad extends RS {

	public RsLoad(int id) {
		super(id);
		_type = TYPE.LOAD;
	}
	public STATE issue(Instruction inst) {
		/*atuInst = inst;
		Op = inst.getType();
		checkDependencies();
		ula.set(inst,Vj,Vk);*/
		if(Arch.r.rBeingUsed(inst.rs))
			Qj = Arch.r.rBeingUsedBy(inst.rs);
		else {
			Vj = Arch.r.rInt(inst.rs);
			Qj = -1;
		}
		address = inst.immediate;
		if(inst.getMnemonic()=="100011") { // LW
			Arch.r.setUsed(inst.rd,id_);
		}
		if(inst.getMnemonic()=="101011") { // SW
			if(Arch.r.rBeingUsed(inst.rs)){
				Qk = Arch.r.rBeingUsedBy(inst.rs);
			} else {
					Vk = Arch.r.rInt(inst.rt);
					Qk = -1;
				}
		}		
		
		ula.set(inst,Vj,Vk);
		return STATE.ISSUE;
		/*

		if (RegisterStat[rs].Qi≠0)
			{RS[r].Qj ← RegisterStat[rs].Qi}
		else 
			{RS[r].Vj ← Regs[rs]; 
			RS[r].Qj ← 0}; 
		RS[r].A ← imm; RS[r].Busy ← yes;
		
		//Load only
		RegisterStat[rt].Qi ← r;
		
		//Store only
		if (RegisterStat[rt].Qi≠0)
			{RS[r].Qk ← RegisterStat[rs].Qi}
		else 
			{RS[r].Vk ← Regs[rt]; 
			RS[r].Qk ← 0};
		 */
	}
}
