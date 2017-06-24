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
		System.out.format("head: %d, tail: %d, size: %d\n", head, tail, entries.size());
		if (tail == entries.size())
			entries.add(new ReorderBufferEntry(instr));
		else
			entries.set(tail, new ReorderBufferEntry(instr));
		map.put(instr, tail);
		tail++;
	}
	
	public void updateResult(Instruction instr, int result){
		entries.get(map.get(instr)).setResult(result);
	}
	
	public int getResult(Instruction instr) {
		return entries.get(map.get(instr)).getResult();
	}
	
	public void updateState(RS register){
		switch(register.state){
		case ISSUE:
			entries.get(map.get(register.atuInst)).setState(STATE.ISSUE);
			break;
		case EXECUTE:
			entries.get(map.get(register.atuInst)).setState(STATE.EXECUTE);
			break;
		case WRITE:
			entries.get(map.get(register.atuInst)).setState(STATE.WRITE);
			break;
		default:
			break;
		}
	}
	
	public boolean validate(){
		System.out.println(entries.get(head).getState());
		if (entries.get(head).getState() == STATE.WRITE){
			entries.get(head).validate();
			head++;
			return true;
		}
		return false;
	}
	
	public boolean isValid(Instruction instr) {
		return entries.get(map.get(instr)).isValid();
	}
	
	public void queuePrediction(int pc, boolean jump){
		predictions.add(new intBoolPair(pc, jump));
	}

	public boolean tailIsBranch() {
		Instruction instr = entries.get(tail-1).getInstruction();
		switch (instr.instr_mnemonic_) {
		case Instruction.BEQ: case Instruction.BLE: case Instruction.BNE:
			System.out.println("tail has branch");
			return true;
		default:
			System.out.println("tail isn't branch");
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
			System.out.println("head has branch");
			return true;
		default:
			System.out.println("head isn't branch");
			return false;
		}
	}
	
	public Instruction getHead(){
		return entries.get(head).getInstruction();
	}
	
	public boolean evaluateHeadBranch(){
		Instruction instr = entries.get(head).getInstruction();
		switch (instr.instr_mnemonic_) {
		case Instruction.BEQ:
			return Arch.r.rInt(instr.rs) == Arch.r.rInt(instr.rt);
		case Instruction.BLE:
			return Arch.r.rInt(instr.rs) <= Arch.r.rInt(instr.rt);
		case Instruction.BNE:
			return Arch.r.rInt(instr.rs) != Arch.r.rInt(instr.rt);
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
