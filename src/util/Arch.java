package util;

import java.util.Vector;

public class Arch {
	Vector<Instruction> program;
	int pc_ = 0;
	boolean terminated = false;
	
	public static Register r = new Register();
	public static Memory m = new Memory(4000);
	public static ULA ula = new ULA();
	public Instruction getNextInstruction () {
		if (pc_ < 0 || pc_ > program.size()) {
			pc_ = 0;
			terminated = true;
		}
		
		Instruction next = program.get(pc_);
		pc_++;
		
		return next;
	}
	
    public static void main(String[] args) {}
}

