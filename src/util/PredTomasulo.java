package util;

import java.io.IOException;

public class PredTomasulo {
	
	public static Cdb cdb = new Cdb();
	public static RS[] rs = new RS[7];
	private int N_RS = 7;
	private int _clock = 0;
	public static Instruction inst;
	private static boolean[] ticked = new boolean[7];
	private Predictor predictor;
	private ReorderBuffer rob;
	
	public PredTomasulo(String path, Predictor p) throws IOException {
		rs[0] = new RsLoad(0);
		rs[1] = new RsLoad(1);
		rs[2] = new RsAdd(2);
		rs[3] = new RsAdd(3);
		rs[4] = new RsAdd(4);
		rs[5] = new RsMult(5);
		rs[6] = new RsMult(6);
		Arch.p = new Program(path);
		predictor = p;
		rob = new ReorderBuffer();
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

    		inst = Arch.p.getNextInstruction();
			inst.setPC(Arch.p.getPC());
			rob.addInstruction(inst);
			//inst.print();
			boolean findRS = false;
			rStates();
			for (int i=0;i<rs.length && !findRS;i++){
	    		switch (inst.getMnemonic()) {
	    		case Instruction.ADD: case Instruction.SUB:
	    			if (!rs[i].isBusy() && rs[i].type()==RS.TYPE.ADD){ 
	    				rs[i].tick(inst);
	    				ticked[i]=true;
	    				rob.updateState(rs[i]);
	    				findRS = true;
	    			}
	    			break;
	    		case Instruction.MUL:
	    			if (!rs[i].isBusy() && rs[i].type()==RS.TYPE.MULT){ 
	    				rs[i].tick(inst);
	    				ticked[i]=true;
	    				rob.updateState(rs[i]);
	    				findRS = true;
	    			}
	    			break;
	    		case Instruction.LW: case Instruction.SW: case Instruction.ADDI:
	    			if (!rs[i].isBusy() && rs[i].type()==RS.TYPE.LOAD){ 
	    				rs[i].tick(inst);
	    				ticked[i]=true;
	    				rob.updateState(rs[i]);
	    				findRS = true;
	    			}
	    			break;
	    		case Instruction.JMP: case Instruction.NOP:
	    			if (!rs[i].isBusy()){ 
	    				rs[i].tick(inst);
	    				ticked[i]=true;
	    				rob.updateState(rs[i]);
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
				rob.tail--;
				System.out.println("Não há estação de reserva disponível");
			}
			rStates();

    		//done = ula.tick();
    		for (int i=0;i<rs.length;i++){
    			if(ticked[i]==false)
    				rs[i].tick(rob);
    		}
    		
    		if (rob.headIsBranch()){
    			boolean realBranch = rob.evaluateHeadBranch();
    			boolean predictedBranch = rob.getPredictedBranch();
				rob.validate();
    			if (realBranch != predictedBranch){
    				rob.tail = rob.head;
    				Arch.p.setPC(rob.retrievePredictionPC());
    				rob.resetPredictionQueue();
    				predictor.updateState(false);
    			} else {
    				rob.popPrediction();
    				predictor.updateState(true);
    			}
    		}
    		else {rob.validate();}
    		
    		if (rob.tailIsBranch()){
    			rob.queuePrediction(Arch.p.getPC(), predictor.executeBranch());
    			if (predictor.executeBranch()){
    				Instruction tail = rob.getTail();
    				switch (tail.instr_mnemonic_) {
					case Instruction.BEQ:
						Arch.p.setPC(Arch.p.getPC() + 4 + tail.immediate);
						break;
					case Instruction.BLE:
						Arch.p.setPC(tail.immediate);
						break;
					case Instruction.BNE:
						Arch.p.setPC(Arch.p.getPC() + 4 + tail.immediate);
						break;
					default:
						break;
					}
    			}
    		}
    		
    		System.out.println();
    	}
    	else {
    		System.out.println("Encerrando...");
	    	System.out.println("R2 = " + Arch.r.rInt(2));
    	}
    }

	public RS[] getRS() {
		return rs;
	}
	public int getNumberOfRS() {
		return N_RS;
	}

	public Object[] getProgramInfo() {
		return new Object[] {_clock, Arch.p.getPC(), "TODO", "TODO"};
	}
}
