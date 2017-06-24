package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import util.ReorderBufferEntry.STATE;

public class ReorderBuffer {
	
	class intBoolPair extends Object{
		private int pc;
		private boolean branch;
		
		public intBoolPair(int pc, boolean branch){
			this.pc = pc;
			this.branch = branch;
		}
	}
	
	private Map<Instruction, Integer> map = new HashMap<>();
	private List<ReorderBufferEntry> entries = new ArrayList<>();
	private Queue<intBoolPair> predictions = new PriorityQueue<>();
	int head = 0, tail = 0;
	
	public void addInstruction(Instruction instr){
		if (tail == entries.size())
			entries.add(new ReorderBufferEntry(instr));
		else
			entries.set(tail, new ReorderBufferEntry(instr));
		map.put(instr, tail);
		tail++;
	}
	
	public void updateResult(Instruction instr, int result){
		entries.get(map.get(instr)-1).setResult(result);
	}
	
	public int getResult(Instruction instr) {
		return entries.get(map.get(instr) - 1).getResult();
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
	
	public void validate(){
		if (entries.get(head).getState() == STATE.WRITE){
			entries.get(head).validate();
			head++;
		}
	}
	
	public boolean isValid(Instruction instr) {
		return entries.get(map.get(instr) - 1).isValid();
	}
	
	public void queuePrediction(int pc, boolean jump){
		predictions.add(new intBoolPair(pc, jump));
	}

	public boolean tailIsBranch() {
		Instruction instr = entries.get(tail-1).getInstruction();
		switch (instr.instr_mnemonic_) {
		case Instruction.BEQ: case Instruction.BLE: case Instruction.BNE:
			return true;
		default:
			return false;
		}
	}
	
	public Instruction getTail(){
		return entries.get(tail-1).getInstruction();
	}
	
	public boolean headIsBranch() {
		Instruction instr = entries.get(head).getInstruction();
		switch (instr.instr_mnemonic_) {
		case Instruction.BEQ: case Instruction.BLE: case Instruction.BNE:
			return true;
		default:
			return false;
		}
	}
	
	public boolean evaluateHeadBranch(){
		Instruction instr = entries.get(head).getInstruction();
		switch (instr.instr_mnemonic_) {
		case Instruction.BEQ:
			return instr.rs == instr.rt;
		case Instruction.BLE:
			return instr.rs <= instr.rt;
		case Instruction.BNE:
			return instr.rs != instr.rt;
		default:
			return true;
		}
	}

	public boolean getPredictedBranch() {
		return predictions.peek().branch;
	}

	public void resetPredictionQueue() {
		predictions = new PriorityQueue<>();
	}

	public void popPrediction() {
		predictions.poll();
	}

	public int retrievePredictionPC() {
		return predictions.peek().pc;
	}
}
