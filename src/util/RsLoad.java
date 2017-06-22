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
	public STATE write(){
		
		
		if(ula.mnemonic=="101011"){
			if(Qk == -1)
				Arch.m.write(address,String.format("%16s", Integer.toBinaryString(Vk)).replace(' ', '0'));
			else 
				return STATE.WRITE;
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
		return STATE.FREE;
	}
	

	public STATE execute() {
		if (Qj == -1 && Qk == -1) {
			if (!ula.tick() && ula.mnemonic!="100011")
				return STATE.EXECUTE;
			if(ula.mnemonic=="100011"){
				address = Vj+address;
				ula.result = Integer.parseInt(Arch.m.read(address), 2);
			}
			//RS[r].A ← RS[r].Vj + RS[r].A; 
			//Read from Mem[RS[r].A]
			// Terminou Operacao

				return STATE.WRITE;
		}
		return STATE.EXECUTE;
	}
}
