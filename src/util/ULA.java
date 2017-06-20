package util;

public class ULA {
	private int rs,rt,rd,target,immediate,ticker;
	private String mnemonic;
	public ULA(){
		// Empty
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

}
