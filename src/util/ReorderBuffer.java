package util;

import java.util.LinkedList;

import util.ReorderBufferEntry.STATE;

public class ReorderBuffer {
	private LinkedList<ReorderBufferEntry> entries = new LinkedList<>();
	
	public void addInstruction(Instruction instr, int RSid){
		System.out.println("Size: " + entries.size());
		entries.add(new ReorderBufferEntry(instr));
		System.out.println("Size2: " + entries.size());
		System.out.println("");
		entries.getLast().setRSid(RSid);
		entries.getLast().setState(STATE.ISSUE);
	}
	public boolean isEmpty(){
		return (entries.size()<=0);
	}
	public void setBranching(boolean branched){
		entries.getLast().setBranch(branched);
	}
	public Object[][] getListInfo() {
		Object[][] data = new Object[entries.size()][];
		for (int i = 0; i < entries.size(); i++) {
			data[i] = new Object[] {entries.get(i).getInstruction(),
					entries.get(i).getState(),
					entries.get(i).getDestination(),
					entries.get(i).getResult(),
					entries.get(i).isReady(),
					entries.get(i).isIn(),
					entries.get(i).type(),
					entries.get(i).hasBranched()}; 
		}
		return data;
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
					break;
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
				entry.setState(STATE.COMMIT);
				return;
			}
		}
	}
	
	public void commit(){
		if(!entries.isEmpty() && entries.getFirst().isReady()){
			ReorderBufferEntry entry = entries.removeFirst();
			switch (entry.type()) {
			case JUMP:
				break;
			case REGISTER:
				Arch.r.wInt(entry.getDestination(), entry.getResult());
				Arch.r.setUsed(entry.getDestination(), -1);
				break;
			case STORE:
				Arch.m.write(entry.getDestination(),
						String.format("%16s", Integer.toBinaryString(entry.getResult())).replace(' ', '0'));
				Arch.m.setUsed(entry.getDestination(), -1);
			case BRANCH:
				if (entry.getResult() == 1 && !entry.hasBranched()){
					Arch.p.setPC(entry.getDestination());
					entries.clear();
					PredTomasulo.predictor.updateState(false);
				} else if (entry.getResult() == 0 && entry.hasBranched()) {
					Arch.p.setPC(entry.getInstruction().pc);
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
