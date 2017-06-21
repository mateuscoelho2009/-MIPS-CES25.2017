package util;

public class UlaTomasulo extends ULA {
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
}
