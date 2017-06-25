package util;

import java.io.IOException;

public class Arch {

	//public static enum STATION_ID {LOAD1, LOAD2, ADD1, ADD2, ADD3, MULT1, MULT2, NONE};

	static public Predictor predictor;
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
	protected static Rob rob;
	private static int reorder_uid = 0;
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
		rob = new Rob();
		predictor = pred;
	}
	public static int reorderUID(){
		reorder_uid++;
		return reorder_uid;
	}
	public static int Regs(int pos){
		return Arch.RegisterStat.rInt(pos);
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
	
	public static void run () throws IOException{   	
    	_clock++;
    	if(!p.end()||!rob.isEmpty()){
    		for(int j=0;j<ticked.length;j++){
    			ticked[j]=false;
    		}
    		System.out.println(_clock + ":");

    		inst = Arch.p.getNextInstruction();
			inst.setPC(Arch.p.getPC());
			//rStates();
			boolean findRS = false;
			for(int i=0;i<ticked.length && !findRS;i++){
				switch (inst.getMnemonic()) {
	    		case Instruction.ADD: case Instruction.SUB: case Instruction.ADDI:
	    			if (!rs[i].isBusy() && rs[i].type()==Rs.TYPE.ADD){ 
	    				rs[i].tick(inst);
	    				rob.addInstruction(inst, i);
	    				rob.setInstructionState(rs[i]);
	    				ticked[i] = true;
	    				findRS = true;
	    			}
	    			break;
	    		case Instruction.MUL:
	    			if (!rs[i].isBusy() && rs[i].type()==Rs.TYPE.MULT){ 
	    				rs[i].tick(inst);
	    				rob.addInstruction(inst, i);
	    				rob.setInstructionState(rs[i]);
	    				ticked[i] = true;
	    				findRS = true;
	    			}
	    			break;
	    		case Instruction.LW: case Instruction.SW:
	    			if (!rs[i].isBusy() && rs[i].type()==Rs.TYPE.LOAD){ 
	    				rs[i].tick(inst);
	    				rob.addInstruction(inst, i);
	    				rob.setInstructionState(rs[i]);
	    				ticked[i] = true;
	    				findRS = true;
	    			}
	    			break;
	    		case Instruction.JMP: case Instruction.NOP:
	    			if (!rs[i].isBusy()){ 
	    				rs[i].tick(inst);
	    				rob.addInstruction(inst, i);
	    				rob.setInstructionState(rs[i]);
	    				ticked[i]=true;
	    				findRS = true;
	    			}
	    			break;
	    		case Instruction.BEQ:
	    			if (!rs[i].isBusy()){
	    				rs[i].tick(inst);
	    				rob.addInstruction(inst, i);
	    				rob.setInstructionState(rs[i]);
	    				ticked[i] = true;
	    				findRS = true;
	    				if (predictor.executeBranch()){
	    					Arch.p.setPC(Arch.p.getPC()+inst.immediate);
	    				}
	    				rob.setBranching(predictor.executeBranch());
	    			}
	    			break;
	    		case Instruction.BLE:
	    			if (!rs[i].isBusy()){
	    				rs[i].tick(inst);
	    				rob.addInstruction(inst, i);
	    				rob.setInstructionState(rs[i]);
	    				ticked[i] = true;
	    				findRS = true;
	    				if (predictor.executeBranch()){
	    					Arch.p.setPC(inst.immediate);
	    				}
	    				rob.setBranching(predictor.executeBranch());
	    			}
	    			break;
	    		case Instruction.BNE:
	    			if (!rs[i].isBusy()){
	    				rs[i].tick(inst);
	    				rob.addInstruction(inst, i);
	    				rob.setInstructionState(rs[i]);
	    				ticked[i] = true;
	    				findRS = true;
	    				if (predictor.executeBranch()){
	    					Arch.p.setPC(Arch.p.getPC()+inst.immediate);
	    				}
	    				rob.setBranching(predictor.executeBranch());
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
			
			for (int i=0;i<rs.length;i++){
    			if(ticked[i]==false){
    				rs[i].tick(rob);
    				rob.setInstructionState(rs[i]);
    			}
    		}
			
			rob.commit();
    	}
    	else {
    		System.out.println("Encerrando...");
	    	System.out.println("R2 = " + Arch.RegisterStat.rInt(2));
    	}
    }

	private static boolean hasNoBranchInst() {
		for (int i = 0; i < rs.length; i++) {
			if (rs[i].hasJump()) return false;
		}
		return true;
	}

	public static Rs[] getRS() {
		return rs;
	}
	public static int getNumberOfRS() {
		return N_RS;
	}

	public static Object[] getProgramInfo() {
		return new Object[] {_clock, Arch.p.getPC(), concludedInstructions, (double)_clock/(double)concludedInstructions};
	}

	public static void incrementInstructions() {
		concludedInstructions++;
		
	}

	public static void runAll() throws IOException {
		while(!Arch.p.end())
			run();
	}

	public static void run20() throws IOException {
		for(int i=0;i<20;i++)
			run();
	}

	public static Rob getReorderBuffer() {
		return rob;
	}
}
