package util;

import java.io.IOException;

public class Arch {

	//public static enum STATION_ID {LOAD1, LOAD2, ADD1, ADD2, ADD3, MULT1, MULT2, NONE};

	public static Predictor predictor;
	public static Register RegisterStat = new Register();
	public static Memory Mem = new Memory(4000);
	public static Program p;
	protected static int concludedInstructions = 0;
	public static Cdb cdb = new Cdb();
	public static Rs[] rs = new Rs[7];
	protected static int N_RS = 7;
	protected static int _clock = 0;
	public static Instruction inst;
	protected static boolean[] ticked= new boolean[7];
	protected static Rob ROB;
	//private static int reorder_uid = -1;
	public Arch(String path, Predictor pred) throws IOException {
		//Arch.restart();
		concludedInstructions = 0;
		rs[0] = new Rs(0,Rs.TYPE.LOAD);
		rs[1] = new Rs(1,Rs.TYPE.LOAD);
		rs[2] = new Rs(2,Rs.TYPE.ADD);
		rs[3] = new Rs(3,Rs.TYPE.ADD);
		rs[4] = new Rs(4,Rs.TYPE.ADD);
		rs[5] = new Rs(5,Rs.TYPE.MULT);
		rs[6] = new Rs(6,Rs.TYPE.MULT);
		Arch.p = new Program(path);
		ROB = new Rob();
		predictor = pred;
	}
	/*public static int reorderUID(){
		reorder_uid++;
		return reorder_uid;
	}*/
	public static int Regs(int pos){
		return Arch.RegisterStat.rInt(pos);
	}
	public static void Regs(int d, int value) {
		Arch.RegisterStat.wInt(d, value);
	}
	
	static boolean isAnyOneBusy () {
		for (int i = 0; i < rs.length; i++) {
			if (rs[i].isBusy()) return true;
		}
		return false;
	}
	
	public static void run () throws IOException, CloneNotSupportedException{   	
    	_clock++;
    	if(!p.end()||!ROB.isEmpty()||p.getPC()<p.maxPC()){
    		for(int j=0;j<ticked.length;j++){
    			ticked[j]=false;
    		}
    		System.out.println(_clock + ":");
    		inst = Arch.p.getNextInstruction();
    		System.out.println(inst);
			inst.setPC(Arch.p.getPC());
			int findRS = -1;
			if(ROB.commit()){
				System.out.println("Hey");
				for(int i=0;i<ticked.length && findRS==-1;i++){
					switch (inst.getMnemonic()) {
		    		case Instruction.ADD: case Instruction.SUB: case Instruction.ADDI:
		    			if (!rs[i].isBusy() && rs[i].getType()==Rs.TYPE.ADD){ 
		    				findRS = i;
		    			}
		    			break;
		    		case Instruction.MUL:
		    			if (!rs[i].isBusy() && rs[i].getType()==Rs.TYPE.MULT){ 
		    				findRS = i;
		    			}
		    			break;
		    		case Instruction.LW: case Instruction.SW:
		    			if (!rs[i].isBusy() && rs[i].getType()==Rs.TYPE.LOAD){
		    				findRS = i;
		    			}
		    			break;
		    		case Instruction.JMP: case Instruction.NOP:case Instruction.BEQ:
		    		case Instruction.BLE:case Instruction.BNE:
		    			if (!rs[i].isBusy()){ 
		    				findRS = i;
		    			}
		    			break;
		    		}
	    		}
	
				if (findRS==-1){
					Arch.p.setPC(Arch.p.getPC() - 4);
					System.out.println("Não há estação de reserva disponí­vel");
				}
				
				for (int i=0;i<rs.length;i++){
	    			if(findRS!=i){
	    				rs[i].tick();
	    			}
	    			rs[i].updateState();
	    		}
				if(findRS!=-1)
					rs[findRS].issue(inst);
			}
			
    	}
    	else {
    		System.out.println("Encerrando...");
	    	System.out.println("R2 = " + Arch.RegisterStat.rInt(2));
    	}
    }

	public static Rs[] getRS() {
		return rs;
	}
	public static int getNumberOfRS() {
		return N_RS;
	}

	public static Object[] getProgramInfo() {
		return new Object[] {_clock, Arch.p.getPC(), concludedInstructions, (double)_clock/(double)concludedInstructions, ROB.getHead()};
	}

	public static void incrementInstructions() {
		concludedInstructions++;
		
	}

	public static void runAll() throws IOException, CloneNotSupportedException {
		while(!p.end()||!ROB.isEmpty()||p.getPC()<p.maxPC())
			run();
	}

	public static void run20() throws IOException, CloneNotSupportedException {
		for(int i=0;i<20;i++)
			run();
	}

	public static Rob getReorderBuffer() {
		return ROB;
	}

}
