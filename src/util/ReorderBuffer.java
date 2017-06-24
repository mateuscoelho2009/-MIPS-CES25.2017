package util;

import java.util.*;

import util.ReorderBufferEntry.STATE;

public class ReorderBuffer {
	private Map<Instruction, Integer> map = new HashMap<>();
	private List<ReorderBufferEntry> entries = new ArrayList<>();
	
	public void addInstruction(Instruction instr, int instrCount){
		entries.add(new ReorderBufferEntry(instr));
		map.put(instr, instrCount);
		entries.get(instrCount-1).setState(STATE.READ);
	}
	
	public void updateResult(Instruction instr, int result){
		entries.get(map.get(instr)-1).setResult(result);
	}
	
	public void updateState(RS register){
		switch(register.state){
		case ISSUE:
			entries.get(map.get(register.atuInst)-1).setState(STATE.ISSUE);
			break;
		case EXECUTE:
			entries.get(map.get(register.atuInst)-1).setState(STATE.EXECUTE);
			break;
		case WRITE:
			entries.get(map.get(register.atuInst)-1).setState(STATE.WRITE);
			break;
		default:
			break;
		}
	}
	
	public void validate(Instruction instr){
		entries.get(map.get(instr)-1).validate();
		entries.get(map.get(instr)-1).setState(STATE.COMMIT);
	}
}
