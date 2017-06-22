package util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.hamcrest.core.IsAnything;

import GUI.GUI;

public class ArchTomassulo {

	//public static enum STATION_ID {LOAD1, LOAD2, ADD1, ADD2, ADD3, MULT1, MULT2, NONE};
	
	// J� usa mem�ria e registradores do Arch
	//public static Register r = new Register();
	//public static Memory m = new Memory(4000);
	//public static Program p;
	public static Cdb cdb = new Cdb();
	public static RS[] rs = new RS[7];
	private int N_RS = 7;
	public static Instruction inst;
	private static boolean[] ticked= new boolean[7];
	public ArchTomassulo() {
		rs[0] = new RsLoad(0);
		rs[1] = new RsLoad(1);
		rs[2] = new RsAdd(2);
		rs[3] = new RsAdd(3);
		rs[4] = new RsAdd(4);
		rs[5] = new RsMult(5);
		rs[6] = new RsMult(6);
	}
	
	public static void rStates(){
		for (int i=0; i< rs.length;i++)
			System.out.println(rs[i].getState());
	}
	
	static boolean isAnyOneBusy () {
		for (int i = 0; i < rs.length; i++) {
			if (rs[i].isBusy()) return true;
		}
		return false;
	}
	
	public void run (String path) throws IOException{
    	
    	Arch.p = new Program(path);
    	long clock = 0;
    	while(!Arch.p.end()){
    		for(int j=0;j<ticked.length;j++){
    			ticked[j]=false;
    		}
    		System.out.print(clock + " - ");
    		if (hasNoBranchInst()) {
    			inst = Arch.p.getNextInstruction();
    			inst.setPC(Arch.p.getPC());
    			boolean findRS = false;
    			//rStates();
    			for (int i=0;i<rs.length && !findRS;i++){
    	    		switch (inst.getMnemonic()) {
    	    		case Instruction.ADD: case Instruction.SUB:
    	    			if (!rs[i].isBusy() && rs[i].type()==RS.TYPE.ADD){ 
    	    				rs[i].tick(inst);
    	    				ticked[i]=true;
    	    				findRS = true;
    	    			}
    	    			break;
    	    		case Instruction.MUL:
    	    			if (!rs[i].isBusy() && rs[i].type()==RS.TYPE.MULT){ 
    	    				rs[i].tick(inst);
    	    				ticked[i]=true;
    	    				findRS = true;
    	    			}
    	    			break;
    	    		case Instruction.LW: case Instruction.SW: case Instruction.ADDI:
    	    			if (!rs[i].isBusy() && rs[i].type()==RS.TYPE.LOAD){ 
    	    				rs[i].tick(inst);
    	    				ticked[i]=true;
    	    				findRS = true;
    	    			}
    	    			break;
    	    		case Instruction.JMP: case Instruction.NOP:
    	    			if (!rs[i].isBusy()){ 
    	    				rs[i].tick(inst);
    	    				ticked[i]=true;
    	    				findRS = true;
    	    			}
    	    			break;
    	    		default:
    	    			if (!isAnyOneBusy()) {
    	    				rs[i].tick(inst);
    	    				ticked[i]=true;
    	    				findRS = true;
    	    			}
    	    			break;
    	    		}   				
    			}
    			if (!findRS){
    				Arch.p.setPC(Arch.p.getPC() - 4);
    				System.out.println("Não há estação de reserva disponível");
    			}
    			//rStates();
    				

    		}

    		//done = ula.tick();
    		for (int i=0;i<rs.length;i++){
    			if(ticked[i]==false)
    				rs[i].tick();
    		}
    		
    		try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

	public RS[] getRS() {
		return rs;
	}
	public int getNumberOfRS() {
		return N_RS;
	}
}
