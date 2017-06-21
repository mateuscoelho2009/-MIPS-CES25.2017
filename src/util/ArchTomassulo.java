package util;

import java.io.IOException;

import org.hamcrest.core.IsAnything;

public class ArchTomassulo {

	public static enum STATION_ID {LOAD1, LOAD2, ADD1, ADD2, ADD3, MULT1, MULT2, NONE};
	
	// Já usa memória e registradores do Arch
	//public static Register r = new Register();
	//public static Memory m = new Memory(4000);
	//public static Program p;
	public static EstacaoReserva[] load = new EstacaoReserva[2], 
									add = new EstacaoReserva[3],
									mult = new EstacaoReserva[2];
	
	public static void init() {
		for (int i = 0; i < 2; i++) {
			mult[i] = new EstacaoReserva();
			add[i] = new EstacaoReserva();
			load[i] = new EstacaoReserva();
		}
		add[2] = new EstacaoReserva();
		
		load[0].setId(STATION_ID.LOAD1);
		load[1].setId(STATION_ID.LOAD2);
		add[0].setId(STATION_ID.ADD1);
		add[1].setId(STATION_ID.ADD2);
		add[2].setId(STATION_ID.ADD3);
		mult[0].setId(STATION_ID.MULT1);
		mult[1].setId(STATION_ID.MULT2);
	}
	
	static boolean isAnyOneBusy () {
		for (int i = 0; i < 2; i++) {
			if (mult[i].isBusy()) return true;
			if (add[i].isBusy()) return true;
			if (load[i].isBusy()) return true;
		}
		if (add[2].isBusy()) return true;
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
	    		switch (inst.getMnemonic()) {
	    		case Instruction.ADD: case Instruction.SUB:
	    			if (!add[0].isBusy()) add[0].passInstruction(inst);
	    			else if (!add[1].isBusy()) add[1].passInstruction(inst);
	    			else if (!add[2].isBusy()) add[2].passInstruction(inst);
	    			else Arch.p.setPC(Arch.p.getPC() - 4);
	    			break;
	    		case Instruction.MUL:
	    			if (!mult[0].isBusy()) mult[0].passInstruction(inst);
	    			else if (!mult[1].isBusy()) mult[1].passInstruction(inst);
	    			else Arch.p.setPC(Arch.p.getPC() - 4);
	    			break;
	    		case Instruction.LW: case Instruction.SW:
	    			if (!load[0].isBusy()) load[0].passInstruction(inst);
	    			else if (!load[1].isBusy()) load[1].passInstruction(inst);
	    			else Arch.p.setPC(Arch.p.getPC() - 4);
	    			break;
	    		default:
	    			if (isAnyOneBusy()) { // Caso seja Jump, espera todos acabarem suas respectivas operações
	    				Arch.p.setPC(Arch.p.getPC() - 4);
	    				break;
	    			}
	    			add[0].passInstruction(inst);
	    			break;
	    		}
    		}

    		//done = ula.tick();
    		add[0].tick();
    		add[1].tick();
    		add[2].tick();
    		
    		mult[0].tick();
    		mult[1].tick();
    		
    		load[0].tick();
    		load[1].tick();
    		System.out.println();

    		clock++;
    	}
    	System.out.println("Encerrando...");
    	System.out.println("R2 = " + Arch.r.rInt(2));
    }

	private static boolean hasNoBranchInst() {
		for (int i = 0; i < 2; i++) {
			if (mult[i].hasJump()) return false;
			if (add[i].hasJump()) return false;
			if (load[i].hasJump()) return false;
		}
		if (add[2].hasJump()) return false;
		return true;
	}
}
