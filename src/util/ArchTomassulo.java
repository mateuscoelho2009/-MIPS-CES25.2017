package util;

import java.io.IOException;

public class ArchTomassulo {

	public static enum STATION_ID {LOAD1, LOAD2, ADD1, ADD2, ADD3, MULT1, MULT2, NONE};
	
	// Já usa memória e registradores do Arch
	public static Register r = new Register();
	public static Memory m = new Memory(4000);
	public static Program p;
	public static EstacaoReserva[] load = new EstacaoReserva[2], 
									add = new EstacaoReserva[3],
									mult = new EstacaoReserva[2];
	
	public ArchTomassulo() {
		// TODO Auto-generated constructor stub
		load[0].setId(STATION_ID.LOAD1);
		load[1].setId(STATION_ID.LOAD2);
		add[0].setId(STATION_ID.ADD1);
		add[1].setId(STATION_ID.ADD2);
		add[2].setId(STATION_ID.ADD3);
		mult[0].setId(STATION_ID.MULT1);
		mult[1].setId(STATION_ID.MULT2);
	}
	
	public static void main(String[] args) throws IOException {
    	System.out.println("Inicializando...");
    	p = new Program("test_without_comments2.txt");
    	long clock = 0;
    	while(!p.end()){
    		System.out.print(clock + " - ");

    		Instruction inst = p.getNextInstruction();
    		switch (inst.getMnemonic()) {
    		case Instruction.ADD: case Instruction.SUB:
    			if (!add[0].isBusy()) add[0].passInstruction(inst);
    			else if (!add[1].isBusy()) add[1].passInstruction(inst);
    			else if (!add[2].isBusy()) add[2].passInstruction(inst);
    			else p.setPC(p.getPC() - 4);
    			break;
    		case Instruction.MUL:
    			if (!mult[0].isBusy()) mult[0].passInstruction(inst);
    			else if (!mult[1].isBusy()) mult[1].passInstruction(inst);
    			else if (!mult[2].isBusy()) mult[2].passInstruction(inst);
    			else p.setPC(p.getPC() - 4);
    			break;
    		case Instruction.LW: case Instruction.SW:
    			if (!load[0].isBusy()) load[0].passInstruction(inst);
    			else if (!load[1].isBusy()) load[1].passInstruction(inst);
    			else if (!load[2].isBusy()) load[2].passInstruction(inst);
    			else p.setPC(p.getPC() - 4);
    			break;
    		default: break;
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
    }
}
