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
	int rs, rt, rd;
	
	public Instruction (String byteCode) {
		String opcode = byteCode.substring(0, 6);
		switch(opcode) {
			case TYPER: type_ = INSTR_TYPE.R; break;
			case ADDI: case BEQ: case BLE: case LW: case SW:
				type_ = INSTR_TYPE.I; instr_mnemonic_ = opcode;
				break;
			case JMP: type_ = INSTR_TYPE.J; instr_mnemonic_ = opcode;
				break;
			default: type_ = INSTR_TYPE.UNDEFINED; instr_mnemonic_ = opcode;
		}
	}
	
	String getMnemonic () {
		return instr_mnemonic_;
	}
	
	INSTR_TYPE getType () {
		return type_;
	}
	
	int getRS() {
		return rs;
	}
	
	int getRT() {
		return rt;
	}
	
	int getRD() {
		return rd;
	}
}
