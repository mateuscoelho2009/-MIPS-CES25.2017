package util;

public class UlaTomasulo extends ULA {
	int Vj, Vk, A;
	
	public void set(Instruction inst, int Vj, int Vk){
		switch (inst.getType()) {
			case R:	arithmetic(Vj, Vk, inst.getRD(), inst.getMnemonic());
					break;
			case I: immediate(inst.getRS(), inst.getRT(), inst.getImmediate(), inst.getMnemonic());
					break;
			case J: jump(inst.getTargetAddress(), inst.getMnemonic());
					break;
			default: System.out.println("Instrução Incorreta");
					break;
		}
		System.out.println("Inst Carregada:"+mnemonic +" PC:"+Arch.p.getPC() );
	}
	
	protected void jump(int targetAddress, String mnemonic) {
		this.target = targetAddress;
		this.mnemonic = mnemonic;
		ticker = 1;
	}
	
	protected void arithmetic(int Vj, int Vk, int rd, String mnemonic){
			this.Vj = Vj; this.Vk = Vk; this.rd = rd;
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
		switch (mnemonic){
			case Instruction.ADD:
				Arch.r.wInt(rd, (Vj+Vk));
				System.out.print("ADD/ R"+rd+" = "+Arch.r.read(rd) + " = "+ Arch.r.rInt(rd));
				break;
			case Instruction.MUL:
				Arch.r.wInt(rd, (Vj*Vk));
				System.out.print("MUL/ R"+rd+" = "+Arch.r.read(rd) + " = "+ Arch.r.rInt(rd));
				break;
			case Instruction.NOP:
				System.out.print("NOP");
				break;
			case Instruction.SUB:
				Arch.r.wInt(rd, (Vj-Vk));
				System.out.print("SUB/ R"+rd+" = "+Arch.r.read(rd) + " = "+ Arch.r.rInt(rd));
				break;
			case Instruction.ADDI:
				Arch.r.wInt(Vk, (Vj+immediate));
				System.out.print("ADDI/ R"+Vk+" = "+Arch.r.read(Vk) + " = "+ Arch.r.rInt(Vk));
				break;
			case Instruction.BEQ:
				//If(R[rs]=R[rt]) { PC=PC+4+Imm}
				if(Arch.r.rInt(rs)==Arch.r.rInt(rt)) 
					Arch.p.setPC(Arch.p.getPC()+4+immediate);
				System.out.print("BEQ/ PC = "+ Arch.p.getPC());
				break;
			case Instruction.BLE:
				//If(R[rs]<=R[rt]) { PC=Imm }
				if(Arch.r.rInt(rs)<=Arch.r.rInt(rt)) 
					Arch.p.setPC(immediate);
				System.out.print("BLE/ PC = "+ Arch.p.getPC());
				break;
			case Instruction.BNE:
				//If(R[rs]!=R[rt]) { PC=PC+4+Imm}
				if(Arch.r.rInt(rs)!=Arch.r.rInt(rt)) 
					Arch.p.setPC(Arch.p.getPC()+4+immediate);
				System.out.print("BNE/ PC = "+ Arch.p.getPC());
				break;
			case Instruction.LW:
				//R[rt]=MEM[R[rs]+ImmExt]
				System.out.print("LW/ R"+Vk+" = MEM["+Vj+"+"+immediate+"] = ");
				Arch.r.write(Vk, Arch.m.read(Vj+immediate));
				System.out.print(Arch.r.read(Vk));
				break;
			case Instruction.SW:
				//MEM[R[rs]+ImmExt]=R[rt]
				Arch.m.write(Vj+immediate, Arch.r.read(Vk));
				System.out.print("SW/ MEM["+Vj+"+"+immediate+"] = R"+Vk+" = "+ Arch.r.read(Vk));
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
