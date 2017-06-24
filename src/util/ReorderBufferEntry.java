package util;

public class ReorderBufferEntry {
	public static enum STATE {ISSUE, EXECUTE, WRITE, COMMIT};

	private Instruction instruction;
	private STATE state;
	private int destination;
	private int result;
	private boolean ready;
	
	public ReorderBufferEntry(Instruction instr){
		setInstruction(instr);
		switch (instr.type_) {
		case R:
			setDestination(instr.rd);
			break;
		case I:
			switch (instr.instr_mnemonic_) {
			case Instruction.ADDI: case Instruction.LW:
				setDestination(instr.rt);
				break;
			case Instruction.BEQ: case Instruction.BLE: case Instruction.BNE:
				setDestination(instr.immediate);
				break;
			case Instruction.SW:
				setDestination(Arch.r.rInt(instr.rs)+instr.immediate);
				break;
			}
			break;
		case J:
			setDestination(instr.targetAddress);
			break;
		default:
			break;
		}
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

	public void setReady(boolean ready) {
		this.ready = ready;
	}
}
