package util;

public class ReorderBufferEntry {
	public static enum STATE {READ, ISSUE, EXECUTE, WRITE, COMMIT};

	private Instruction instruction;
	private int destination;
	private int result;
	private boolean isValid;
	private STATE state;
	
	public ReorderBufferEntry(Instruction instr){
		this.instruction = instr;
		this.destination = instr.targetAddress;
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

	public void setResult(int result) {
		this.result = result;		
	}
}
