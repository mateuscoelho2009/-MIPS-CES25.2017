package util;

public class RobEntry {
	//public static enum STATE {ISSUE, EXECUTE, WRITE, COMMIT};
	//public static enum TYPE {BRANCH, STORE, REGISTER, JUMP};
	private int uid;
	private Instruction instruction;
	//private STATE state;
	private int dest;
	private int address;
	private int value;
	private boolean ready;
	private int isIn;
	//private TYPE type;
	private boolean branch;
	private boolean busy;
	
	public RobEntry(Instruction instr){
		setInstruction(instr);
		//if(instr.rd!=0)
		setDestination(instr.rd);
		//else
			//setDestination(instr.rt);
		setReady(false);
		setBusy(true);
		//this.uid = uid;
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

	public Instruction.STATE getState() {
		return instruction.getState();
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
	public boolean isBusy() {
		return busy;
	}
	public void setBusy(boolean busy) {
		this.busy = busy;
	}
}
