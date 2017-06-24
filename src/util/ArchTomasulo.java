package util;

import java.io.IOException;

public class ArchTomasulo {

	//public static enum STATION_ID {LOAD1, LOAD2, ADD1, ADD2, ADD3, MULT1, MULT2, NONE};
	
	// J� usa mem�ria e registradores do Arch
	//public static Register r = new Register();
	//public static Memory m = new Memory(4000);
	//public static Program p;
	protected static int concludedInstructions = 0;
	public static Cdb cdb = new Cdb();
	public static RS[] rs = new RS[7];
	protected int N_RS = 7;
	protected int _clock = 0;
	public static Instruction inst;
	protected static boolean[] ticked= new boolean[7];
	public ArchTomasulo(String path) throws IOException {
		Arch.restart();
		concludedInstructions = 0;
		rs[0] = new RsLoad(0);
		rs[1] = new RsLoad(1);
		rs[2] = new RsAdd(2);
		rs[3] = new RsAdd(3);
		rs[4] = new RsAdd(4);
		rs[5] = new RsMult(5);
		rs[6] = new RsMult(6);
		Arch.p = new Program(path);
	}
	
	public static void rStates(){
		for (int i=0; i< rs.length;i++)
			System.out.print(i+":"+rs[i].getState()+"/");
		System.out.println();
	}
	
	static boolean isAnyOneBusy () {
		for (int i = 0; i < rs.length; i++) {
			if (rs[i].isBusy()) return true;
		}
		return false;
	}
	
	public void run () throws IOException{   	
    	_clock++;
    	if(!Arch.p.end()){
    		for(int j=0;j<ticked.length;j++){
    			ticked[j]=false;
    		}
    		System.out.print(_clock + " - ");

    		if (hasNoBranchInst()) {
    			inst = Arch.p.getNextInstruction();
    			inst.setPC(Arch.p.getPC());
    			//inst.print();
    			boolean findRS = false;
    			rStates();
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
    			rStates();
    				

    		}

    		//done = ula.tick();
    		for (int i=0;i<rs.length;i++){
    			if(ticked[i]==false)
    				rs[i].tick();
    		}
    		
    		/*try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
    		System.out.println();
    	}
    	else {
    		System.out.println("Encerrando...");
	    	System.out.println("R2 = " + Arch.r.rInt(2));
    	}
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

	public Object[] getProgramInfo() {
		return new Object[] {_clock, Arch.p.getPC(), concludedInstructions, (double)_clock/(double)concludedInstructions};
	}

	public static void incrementInstructions() {
		concludedInstructions++;
		
	}

	public void runAll() throws IOException {
		while(!Arch.p.end())
			run();
	}

	public void run20() throws IOException {
		for(int i=0;i<20;i++)
			run();
	}
}
