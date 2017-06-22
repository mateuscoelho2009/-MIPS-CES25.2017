package util;

import java.io.IOException;

import org.hamcrest.core.IsAnything;

public class ArchTomassulo {

	//public static enum STATION_ID {LOAD1, LOAD2, ADD1, ADD2, ADD3, MULT1, MULT2, NONE};
	
	// J� usa mem�ria e registradores do Arch
	//public static Register r = new Register();
	//public static Memory m = new Memory(4000);
	//public static Program p;
	public static Cdb cdb = new Cdb();
	public static RS[] rs = new RS[7];
	
	public static void init() {
		rs[0] = new RsLoad(0);
		rs[1] = new RsLoad(1);
		rs[2] = new RsAdd(2);
		rs[3] = new RsAdd(3);
		rs[4] = new RsAdd(4);
		rs[5] = new RsMult(5);
		rs[6] = new RsMult(6);
		rs[7] = new RsMult(7);
	}
	
	static boolean isAnyOneBusy () {
		for (int i = 0; i < rs.length; i++) {
			if (rs[i].isBusy()) return true;
		}
		return false;
	}
	
	public static void main(String[] args) throws IOException {
    	System.out.println("Inicializando...");
    	Arch.p = new Program("test_without_comments2.txt");
    	init();
    	long clock = 0;
    	while(!Arch.p.end()){
    		System.out.print(clock + " - ");
    		if (hasNoBranchInst()) {
    			Instruction inst = Arch.p.getNextInstruction();
    			inst.setPC(Arch.p.getPC());
    			boolean findRS = false;
    			for (int i=0;i<rs.length && !findRS;i++){
    	    		switch (inst.getMnemonic()) {
    	    		case Instruction.ADD: case Instruction.SUB: case Instruction.ADDI:
    	    			if (!rs[i].isBusy()) 
    	    				rs[i].issue(inst);
    	    			else {
    	    				System.out.println("N�o h� espa�o na esta��o de reserva ADD");
    	    				Arch.p.setPC(Arch.p.getPC() - 4);
    	    			}
    	    			break;
    	    		case Instruction.MUL:
    	    			if (!mult[0].isBusy()) mult[0].passInstruction(inst);
    	    			else if (!mult[1].isBusy()) mult[1].passInstruction(inst);
    	    			else {
    	    				System.out.println("N�o h� espa�o na esta��o de reserva MULT");
    	    				Arch.p.setPC(Arch.p.getPC() - 4);
    	    			}
    	    			break;
    	    		case Instruction.LW: case Instruction.SW:
    	    			if (!load[0].isBusy()) load[0].passInstruction(inst);
    	    			else if (!load[1].isBusy()) load[1].passInstruction(inst);
    	    			else {
    	    				System.out.println("N�o h� espa�o na esta��o de reserva LOAD");
    	    				Arch.p.setPC(Arch.p.getPC() - 4);
    	    			}
    	    			break;
    	    		case Instruction.NOP: case Instruction.JMP:
    	    			if (!add[0].isBusy()) add[0].passInstruction(inst);
    	    			else if (!add[1].isBusy()) add[1].passInstruction(inst);
    	    			else if (!add[2].isBusy()) add[2].passInstruction(inst);
    	    			else if (!mult[0].isBusy()) mult[0].passInstruction(inst);
    	    			else if (!mult[1].isBusy()) mult[1].passInstruction(inst);
    	    			else if (!load[0].isBusy()) load[0].passInstruction(inst);
    	    			else if (!load[1].isBusy()) load[1].passInstruction(inst);
    	    			else {
    	    				System.out.println("N�o h� espa�o nas esta��es de reserva para NOP");
    	    				Arch.p.setPC(Arch.p.getPC() - 4);
    	    			}
    	    			break;
    	    		default:
    	    			if (isAnyOneBusy()) { // Caso seja Jump, espera todos acabarem suas respectivas opera��es
    	    				System.out.println("Instru��o JUMP: esperando outras instru��es acabarem a execu��o");
    	    				Arch.p.setPC(Arch.p.getPC() - 4);
    	    				break;
    	    			}
    	    			add[0].passInstruction(inst);
    	    			break;
    	    		}   				
    			}

    		}

    		//done = ula.tick();
    		add[0].tick();
    		add[1].tick();
    		add[2].tick();
    		
    		mult[0].tick();
    		mult[1].tick();
    		
    		if (load[0].isBusy())
    			load[0].tick();
    		else load[1].tick();
    		
    		System.out.println();
    		clock++;
    	}
    	System.out.println("Encerrando...");
    	System.out.println("R2 = " + Arch.r.rInt(2));
    }

	private static boolean hasNoBranchInst() {
		for (int i = 0; i < rs.length; i++) {
			if (rs[i].hasJump()) return false;
		}
		return true;
	}
}
