package util;

public class Ula {
	protected int rs,rt,rd,target,immediate,ticker=1;
	public int Vj, Vk, A, result;
	public String auxRes;
	protected String mnemonic;
	public Ula(){
    	System.out.println("Inicializando a ULA.");
	}
	public void set(Instruction inst){
		switch (inst.getType()) {
			case R:	arithmetic(inst.getRS(), inst.getRT(), inst.getRD(), inst.getMnemonic());
					break;
			case I: immediate(inst.getRS(), inst.getRT(), inst.getImmediate(), inst.getMnemonic());
					break;
			case J: jump(inst.getTargetAddress(), inst.getMnemonic());
					break;
			default: System.out.println("Instrução Incorreta");
					break;
		}
		System.out.println("Inst Carregada:"+mnemonic +" PC:"+Arch.p.getPC());
	}
	public void set(Instruction inst, int Vj, int Vk){
		switch (inst.getType()) {
			case R:	arithmetic(Vj, Vk, inst.getRD(), inst.getMnemonic());
					break;
			case I: immediate(Vj, Vk, inst.getImmediate(), inst.getMnemonic());
					break;
			case J: jump(inst.getTargetAddress(), inst.getMnemonic());
					break;
			default: System.out.println("Instrução Incorreta");
					break;
		}
	}
	
	protected void jump(int targetAddress, String mnemonic) {
		this.target = targetAddress;
		this.mnemonic = mnemonic;
		ticker = 1;
	}
	
	protected void arithmetic(int Vj, int Vk, int rd, String mnemonic){
		this.Vj = Vj; this.Vk = Vk; this.rd = rd;
		//this.rs = Vj; this.rt = Vk;
		this.mnemonic = mnemonic;
		if(mnemonic.equals(Instruction.MUL))
			ticker = 3;
		else
			ticker = 1;
}
	
	protected void immediate(int rs, int rt, int immediate, String mnemonic){
		this.rs = rs; this.rt = rt; this.immediate = immediate;
		this.Vj = rs; this.Vk = rt;
		this.mnemonic = mnemonic;
		if(mnemonic.equals(Instruction.LW)||mnemonic.equals(Instruction.SW))
			ticker = 4;
		else
			ticker = 1;
	}
	public boolean tick(){
		ticker--;
		if(ticker>0)
			return false;
		//System.out.println("UlaT: "+this.mnemonic);
		switch (this.mnemonic){
			case Instruction.ADD:
				//Arch.RegisterStat.wInt(rd, (Vj+Vk));
				result = Vj + Vk;
				break;
			case Instruction.MUL:
				//Arch.RegisterStat.wInt(rd, (Vj*Vk));
				result = Vj * Vk;
				break;
			case Instruction.NOP:
				System.out.print("NOP");
				break;
			case Instruction.SUB:
				//Arch.RegisterStat.wInt(rd, (Vj-Vk));
				result = Vj - Vk;
				break;
			case Instruction.ADDI:
				//Arch.RegisterStat.wInt(Vk, (Vj+immediate));
				result = Vj + immediate;
				break;
			case Instruction.BEQ:
				//If(R[rs]=R[rt]) { PC=PC+4+Imm}
				result = Arch.RegisterStat.rInt(rs)==Arch.RegisterStat.rInt(rt) ? 1 : 0;
				System.out.print("BEQ/ PC = "+ Arch.p.getPC());
				break;
			case Instruction.BLE:
				//If(R[rs]<=R[rt]) { PC=Imm }
				result = Arch.RegisterStat.rInt(rs)<=Arch.RegisterStat.rInt(rt) ? 1 : 0; 
				System.out.print("BLE/ PC = "+ Arch.p.getPC());
				break;
			case Instruction.BNE:
				//If(R[rs]!=R[rt]) { PC=PC+4+Imm}
				result = Arch.RegisterStat.rInt(rs)!=Arch.RegisterStat.rInt(rt) ? 1 : 0;
				System.out.print("BNE/ PC = "+ Arch.p.getPC());
				break;
			case Instruction.LW:
				//R[rt]=MEM[R[rs]+ImmExt]
				System.out.print("LW/ R"+Vk+" = MEM["+Vj+"+"+immediate+"] = ");
				//Arch.RegisterStat.write(Vk, Arch.Mem.read(Vj+immediate));
				auxRes = Arch.Mem.read(Vj+immediate);
				result = Integer.parseInt (auxRes, 2);
				break;
			case Instruction.SW:
				//MEM[R[rs]+ImmExt]=R[rt]
				//Arch.Mem.write(Vj+immediate, Arch.RegisterStat.read(Vk));
				//System.out.print("SW/ MEM["+Vj+"+"+immediate+"] = R"+Vk+" = "+ Arch.RegisterStat.read(Vk));
				result =  Integer.parseInt(Arch.RegisterStat.read(Vk),2);
				break;
			case Instruction.JMP:
				Arch.p.setPC(target);
				System.out.print("JMP/ PC = "+ Arch.p.getPC());
				break;
				
			default: 
				System.out.print("Bad instruction!");
		}		
		return true;
	}

}
