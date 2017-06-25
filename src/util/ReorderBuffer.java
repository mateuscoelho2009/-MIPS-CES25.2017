package util;

import java.util.LinkedList;

import util.ReorderBufferEntry.STATE;

public class ReorderBuffer {
	private LinkedList<ReorderBufferEntry> entries = new LinkedList<>();
	
	public void addInstruction(Instruction instr, int RSid){
		entries.add(new ReorderBufferEntry(instr));
		entries.getLast().setRSid(RSid);
		entries.getLast().setState(STATE.ISSUE);
	}
	
	public void setBranching(boolean branched){
		entries.getLast().setBranch(branched);
	}
	
	public void setInstructionState(RS rs){
		for (ReorderBufferEntry entry : entries){
			if (rs.id_ == entry.isIn()){
				switch (rs.state) {
				case ISSUE:
					entry.setState(STATE.ISSUE);
					break;
				case EXECUTE:
					entry.setState(STATE.EXECUTE);
				case WRITE:
					entry.setState(STATE.WRITE);
					break;
				default:
					break;
				}
				return;
			}
		}
	}
	
	public void getResult(RS rs){
		for (ReorderBufferEntry entry : entries){
			if (rs.id_ == entry.isIn()){
				entry.setResult(rs.ula.result);
				entry.makeReady();
			}
		}
	}
	
	public void commit(){
		if(entries.getFirst().isReady()){
			ReorderBufferEntry entry = entries.removeFirst();
			switch (entry.type()) {
			case JUMP:
				Arch.p.setPC(entry.getDestination());
				break;
			case REGISTER:
				Arch.r.wInt(entry.getDestination(), entry.getResult());
				break;
			case STORE:
				Arch.m.write(entry.getDestination(),
						String.format("%16s", Integer.toBinaryString(entry.getResult())).replace(' ', '0'));
			case BRANCH:
				if (entry.getResult() == 1 && !entry.hasBranched()){
					Arch.p.setPC(entry.getDestination());
					entries.clear();
					PredTomasulo.predictor.updateState(false);
				} else if (entry.getResult() == 0 && entry.hasBranched()) {
					Arch.p.setPC(entry.getInstruction().pc + 4);
					entries.clear();
					PredTomasulo.predictor.updateState(false);
				} else if (entry.getResult() == 1 && entry.hasBranched()) {
					PredTomasulo.predictor.updateState(true);
				} else if (entry.getResult() == 0 && !entry.hasBranched()) {
					PredTomasulo.predictor.updateState(true);
				}
				break;
			default:
				break;
			}
		}
	}
}
