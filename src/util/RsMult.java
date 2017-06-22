package util;

import util.RS.STATE;
import util.RS.TYPE;

public class RsMult extends RS {

	public RsMult(int id) {
		super(id);
		_type = TYPE.MULT;
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
		if(Arch.r.rBeingUsed(inst.rt))
			Qk = Arch.r.rBeingUsedBy(inst.rt);
		else {
			Vk = Arch.r.rInt(inst.rt);
			Qk = -1;
		}
		Arch.r.setUsed(inst.rd,id_);
		ula.set(inst,Vj,Vk);
		return STATE.ISSUE;
		/*
			if (RegisterStat[rs].Qi≠0){
				RS[r].Qj ← RegisterStat[rs].Qi
			} else {
				RS[r].Vj ← Regs[rs]; 
				RS[r].Qj ← 0
				}; 
			if (RegisterStat[rt].Qi≠0)
				{RS[r].Qk ← RegisterStat[rt].Qi
			else {
				RS[r].Vk ← Regs[rt]; 
				RS[r].Qk ← 0}; 
			RS[r].Busy ← yes; 
			RegisterStat[rd].Q ← r;
		 */
	}
}
