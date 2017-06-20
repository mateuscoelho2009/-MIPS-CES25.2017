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
		System.out.println("Inst Carregada:"+mnemonic );
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
		if(mnemonic.equals(Instruction.LW)||!mnemonic.equals(Instruction.SW))
			ticker = 4;
		else
			ticker = 1;
	}
	
	public boolean tick(){
		ticker--;
		if(ticker==0)
			return false;
		switch (mnemonic){
			case Instruction.ADD:
				Arch.r.wInt(rd, (Arch.r.rInt(rs)+Arch.r.rInt(rt)));
				System.out.println("ADD");
				break;
			case Instruction.ADDI:
				Arch.r.wInt(rt, (Arch.r.rInt(rs)+immediate));
				System.out.println("ADDI");
				break;
			case Instruction.MUL:
				Arch.r.wInt(rd, (Arch.r.rInt(rs)*Arch.r.rInt(rt)));
				System.out.println("MUL");
				break;
			case Instruction.NOP:
				System.out.println("NOP");
				break;
			case Instruction.SUB:
				Arch.r.wInt(rd, (Arch.r.rInt(rs)-Arch.r.rInt(rt)));
				System.out.println("SUB");
				break;
				
			default: 
				System.out.println("Bad instruction!");
		}		
		return true;
	}

}
