package util;

import java.io.IOException;

public class PredTomasulo extends ArchTomasulo{
	
	private Predictor predictor;
	
	public PredTomasulo(String path, Predictor p) throws IOException {
		super(path);
		predictor = p;
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
			boolean findRS = false;
			for(int i=0;i<ticked.length;i++){
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
    	}
    	else {
    		System.out.println("Encerrando...");
	    	System.out.println("R2 = " + Arch.r.rInt(2));
    	}
    }
}
