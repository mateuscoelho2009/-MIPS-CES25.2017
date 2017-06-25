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
				
			default:
				break;
			}
		}
	}
}
