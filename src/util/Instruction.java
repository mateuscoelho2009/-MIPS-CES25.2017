package util;

public class Instruction {
	static public String TYPER = "000000",
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
	static public enum INTR_TYPE {R, I, J, UNDEFINED};
	
	String intr_mnemonic_;
	INTR_TYPE type_;
}
