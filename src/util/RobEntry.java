package util;

public class RobEntry {
	public static enum STATE {ISSUE, EXECUTE, WRITE, COMMIT};
	public static enum TYPE {BRANCH, STORE, REGISTER, JUMP};
	private int uid;
	private Instruction instruction;
	private STATE state;
	private int dest;
	private int address;
	private int result;
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

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
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
}
