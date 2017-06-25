package util;

public class ReorderBufferEntry {
	public static enum STATE {ISSUE, EXECUTE, WRITE, COMMIT};
	public static enum TYPE {BRANCH, STORE, REGISTER, JUMP};

	private Instruction instruction;
	private STATE state;
	private int destination;
	private int result;
	private boolean ready;
	private int isIn;
	private TYPE type;
	private boolean branch;
	
	public ReorderBufferEntry(Instruction instr){
		setInstruction(instr);
		switch (instr.type_) {
		case R:
			setDestination(instr.rd);
			type = TYPE.REGISTER;
			break;
		case I:
			switch (instr.instr_mnemonic_) {
			case Instruction.ADDI:
				setDestination(instr.rt);
				type = TYPE.REGISTER;
				break;
			case Instruction.BEQ: case Instruction.BNE:
				setDestination(instr.immediate+instr.pc);
				type = TYPE.BRANCH;
				break;
			case Instruction.BLE:
				setDestination(instr.immediate);
				type = TYPE.BRANCH;
				break;
			case Instruction.SW: case Instruction.LW:
				setDestination(Arch.r.rInt(instr.rs)+instr.immediate);
				type = TYPE.STORE;
				break;
			}
			break;
		case J:
			setDestination(instr.targetAddress);
			type = TYPE.JUMP;
			break;
		default:
			break;
		}
		ready = false;
		setResult(0);
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
		return destination;
	}

	public void setDestination(int destination) {
		this.destination = destination;
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
}
