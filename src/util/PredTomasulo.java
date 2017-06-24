package util;

import java.io.IOException;

public class PredTomasulo extends ArchTomasulo{
	
	private Predictor predictor;
	private ReorderBuffer rob;
	
	public PredTomasulo(String path, Predictor p) throws IOException {
		super(path);
		predictor = p;
		rob = new ReorderBuffer();
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
}
