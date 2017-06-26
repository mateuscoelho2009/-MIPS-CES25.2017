package util;

public class RobEntry {
	public static enum STATE {ISSUE, EXECUTE, WRITE, COMMIT};
	public static enum TYPE {BRANCH, STORE, REGISTER, JUMP};
	private int uid;
	private Instruction instruction;
	private STATE state;
	private int dest;
	private int address;
	private int value;
	private boolean ready;
	private int isIn;
	private TYPE type;
	private boolean branch;
	
	public RobEntry(Instruction instr, int uid){
		setInstruction(instr);
		setDestination(instr.rd);
		setReady(false);
		this.uid = uid;
	}
	public int getUid(){
		return uid;
	}
	public Instruction getInstruction() {
		return instruction;
	}

	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}

	public STATE getState() {
		return state;
	}

	public void setState(STATE state) {
		this.state = state;
	}

	public int getDestination() {
		return dest;
	}

	public void setDestination(int destination) {
		this.dest = destination;
	}

	public int getValue() {
		return value;
	}
	public int getResult() {
		return value;
	}
	public void setValue(int result) {
		value = result;
	}

	public boolean isReady() {
		return ready;
	}
	
	public void makeReady() {
		this.ready = true;
	}

	public int isIn() {
		return isIn;
	}

	public void setRSid(int isIn) {
		this.isIn = isIn;
	}
	
	public TYPE type(){
		return type;
	}

	public boolean hasBranched() {
		return branch;
	}

	public void setBranch(boolean branch) {
		this.branch = branch;
	}

	public void setReady(boolean b) {
		ready = b;
	}
	public int getAddress() {
		return address;
	}
	public void setAddress(int address) {
		this.address = address;
	}
	public boolean isBranch() {
		if(instruction.getMnemonic().equals(Instruction.BEQ)||
				instruction.getMnemonic().equals(Instruction.BLE)||
				instruction.getMnemonic().equals(Instruction.BNE))
			return true;
		return false;
	}
}
