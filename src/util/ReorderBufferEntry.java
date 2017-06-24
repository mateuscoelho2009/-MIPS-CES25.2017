package util;

public class ReorderBufferEntry {
	public static enum STATE {ISSUE, EXECUTE, WRITE, COMMIT};

	private Instruction instruction;
	private int result;
	private boolean isValid;
	private STATE state;
	
	public ReorderBufferEntry(Instruction instr){
		this.instruction = instr;
		this.isValid = false;
	}
	
	public void validate(){
		isValid = true;
	}
	
	public boolean isValid(){
		return isValid;
	}
	
	public void setState(STATE s){
		state = s;
	}
	
	public STATE getState(){
		return state;
	}

	public void setResult(int result) {
		this.result = result;		
	}
	
	public int getResult(){
		return result;
	}
	
	public Instruction getInstruction(){
		return instruction;
	}
}
