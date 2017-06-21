package util;

public class ULA {
	private int rs,rt,rd,target,immediate,ticker=1;
	private String mnemonic;
	public ULA(){
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
		System.out.println("Inst Carregada:"+mnemonic +" PC:"+Arch.p.getPC() );
	}
	
	private void jump(int targetAddress, String mnemonic) {
		this.target = targetAddress;
		this.mnemonic = mnemonic;
		ticker = 1;
	}
	
	private void arithmetic(int rs, int rt, int rd, String mnemonic){
			this.rs = rs; this.rt = rt; this.rd = rd;
			this.mnemonic = mnemonic;
			if(mnemonic.equals(Instruction.MUL))
				ticker = 3;
			else
				ticker = 1;
	}
	
	private void immediate(int rs, int rt, int immediate, String mnemonic){
		this.rs = rs; this.rt = rt; this.immediate = immediate;
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
				Arch.r.wInt(rd, (Arch.r.rInt(rs)+Arch.r.rInt(rt)));
				System.out.print("ADD/ R"+rd+" = "+Arch.r.read(rd) + " = "+ Arch.r.rInt(rd));
				break;
			case Instruction.MUL:
				Arch.r.wInt(rd, (Arch.r.rInt(rs)*Arch.r.rInt(rt)));
				System.out.print("MUL/ R"+rd+" = "+Arch.r.read(rd) + " = "+ Arch.r.rInt(rd));
				break;
			case Instruction.NOP:
				System.out.print("NOP");
				break;
			case Instruction.SUB:
				Arch.r.wInt(rd, (Arch.r.rInt(rs)-Arch.r.rInt(rt)));
				System.out.print("SUB/ R"+rd+" = "+Arch.r.read(rd) + " = "+ Arch.r.rInt(rd));
				break;
			case Instruction.ADDI:
				Arch.r.wInt(rt, (Arch.r.rInt(rs)+immediate));
				System.out.print("ADDI/ R"+rt+" = "+Arch.r.read(rt) + " = "+ Arch.r.rInt(rt));
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
				System.out.print("LW/ R"+rt+" = MEM["+Arch.r.rInt(rs)+"+"+immediate+"] = ");
				Arch.r.write(rt, Arch.m.read(Arch.r.rInt(rs)+immediate));
				System.out.print(Arch.r.read(rt));
				break;
			case Instruction.SW:
				//MEM[R[rs]+ImmExt]=R[rt]
				Arch.m.write(Arch.r.rInt(rs)+immediate, Arch.r.read(rt));
				System.out.print("SW/ MEM["+Arch.r.rInt(rs)+"+"+immediate+"] = R"+rt+" = "+ Arch.r.read(rt));
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
