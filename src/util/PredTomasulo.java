package util;

import java.io.IOException;

public class PredTomasulo extends ArchTomasulo{
	
	static public Predictor predictor;
	
	public PredTomasulo(String path, Predictor p) throws IOException {
		super(path);
		predictor = p;
	}
	
	@Override
	public void run () throws IOException{   	
    	_clock++;
    	if(!Arch.p.end()||!ArchTomasulo.rob.isEmpty()){
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
	    			if (!rs[i].isBusy() && rs[i].type()==RS.TYPE.ADD){ 
	    				rs[i].tick(inst);
	    				rob.addInstruction(inst, i);
	    				rob.setInstructionState(rs[i]);
	    				ticked[i] = true;
	    				findRS = true;
	    			}
	    			break;
	    		case Instruction.MUL:
	    			if (!rs[i].isBusy() && rs[i].type()==RS.TYPE.MULT){ 
	    				rs[i].tick(inst);
	    				rob.addInstruction(inst, i);
	    				rob.setInstructionState(rs[i]);
	    				ticked[i] = true;
	    				findRS = true;
	    			}
	    			break;
	    		case Instruction.LW: case Instruction.SW:
	    			if (!rs[i].isBusy() && rs[i].type()==RS.TYPE.LOAD){ 
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
	    	System.out.println("R2 = " + Arch.r.rInt(2));
    	}
    }
}
