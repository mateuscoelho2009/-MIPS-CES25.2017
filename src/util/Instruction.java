package util;

public class Instruction {
	static public final String TYPER = "000000",
						ADD = "100000", 
						ADDI = "001000",
						BEQ = "000101",
						BLE = "000111",
						BNE = "000100",
						JMP = "000010",
						LW = "100011",
						MUL = "011000",
						NOP = "000000",
						SUB = "100010",
						SW = "101011";
						// O que é o LI??
	static public enum INSTR_TYPE {R, I, J, UNDEFINED};
	
	String instr_mnemonic_;
	INSTR_TYPE type_;
	
	public Instruction (String byteCode) {
		String opcode = byteCode.substring(0, 6);
		switch(opcode) {
			case TYPER: type_ = INSTR_TYPE.R; break;
			case ADDI: case BEQ: case BLE: case LW: case SW:
				//type_ = INS
		}
	}
	
	String getMnemonic () {
		return instr_mnemonic_;
	}
	
	INSTR_TYPE getType () {
		return type_;
	}
}
