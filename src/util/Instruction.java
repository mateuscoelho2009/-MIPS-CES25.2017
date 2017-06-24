package util;

public class Instruction {
	static public final String TYPER = "000000",
								ADD =  "100000", 
								ADDI = "001000",
								BEQ =  "000101",
								BLE =  "000111",
								BNE =  "000100",
								JMP =  "000010",
								LW  =  "100011",
								MUL =  "011000",
								NOP =  "000000",
								SUB =  "100010",
								SW  =  "101011";
						// O que ï¿½ o LI??
	static public enum INSTR_TYPE {R, I, J, UNDEFINED};
	
	String instr_mnemonic_;
	INSTR_TYPE type_;
	int rs, rt, rd, targetAddress, shamt, immediate, pc;
	
	public Instruction (String byteCode) {
		String opcode = byteCode.substring(0, 6);
		switch(opcode) {
			case TYPER: type_ = INSTR_TYPE.R; break;
			case ADDI: case BEQ: case BLE: case LW: case SW:
				type_ = INSTR_TYPE.I; instr_mnemonic_ = opcode;
				break;
			case JMP: type_ = INSTR_TYPE.J; instr_mnemonic_ = opcode;
				break;
			//Caso qualquer outra opção, considera LI e processa como ADDI
			default: type_ = INSTR_TYPE.I; instr_mnemonic_ = ADDI;
		}
		
		if (type_ == INSTR_TYPE.R)
			instr_mnemonic_ = byteCode.substring(26,32);
		
		switch (type_) {
			case R:
				rs = convBinStr2Int(byteCode.substring(6, 11));
				rt = convBinStr2Int(byteCode.substring(11, 16));
				rd = convBinStr2Int(byteCode.substring(16, 21));
				shamt = convBinStr2Int(byteCode.substring(21, 26));
				break;
			case I:
				rs = convBinStr2Int(byteCode.substring(6, 11));
				rt = convBinStr2Int(byteCode.substring(11, 16));
				int imm_sgn = convBinStr2Int(byteCode.substring(16,17));
				immediate = convBinStr2Int(byteCode.substring(17,32));
				if (imm_sgn==1)
					immediate = immediate*-1;
				
				break;
			case J:
				targetAddress = convBinStr2Int(byteCode.substring(6,32));
				break;
			default: break;
		}
		//System.out.println(getMnemonic());
		pc = 0;
	}
	
	public Instruction (String byteCode, int pc) {
		this.pc = pc;
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
		
		if (type_ == INSTR_TYPE.R)
			instr_mnemonic_ = byteCode.substring(26,32);
		
		switch (type_) {
			case R:
				rs = convBinStr2Int(byteCode.substring(6, 11));
				rt = convBinStr2Int(byteCode.substring(11, 16));
				rd = convBinStr2Int(byteCode.substring(16, 21));
				shamt = convBinStr2Int(byteCode.substring(21, 26));
				break;
			case I:
				rs = convBinStr2Int(byteCode.substring(6, 11));
				rt = convBinStr2Int(byteCode.substring(11, 16));
				int imm_sgn = convBinStr2Int(byteCode.substring(16,17));
				immediate = convBinStr2Int(byteCode.substring(17,32));
				if (imm_sgn==1)
					immediate = immediate*-1;
				
				break;
			case J:
				targetAddress = convBinStr2Int(byteCode.substring(6,32));
				break;
			default: break;
		}
		//System.out.println(getMnemonic());
	}
	
	private int convBinStr2Int (String binStr) {
		int res = 0;
		
		for (int i = 0; i < binStr.length(); i++) {
			res *= 2;
			if (binStr.charAt(i) == '1') res += 1;
		}
		
		return res;
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
	
	int getShamt () {
		return shamt;
	}
	
	int getTargetAddress () {
		return targetAddress;
	}
	
	int getImmediate () {
		return immediate;
	}
	
	int getPC() {
		return pc;
	}
	
	void setPC(int pc_) {
		pc = pc_;
	}

	public void print() {
		System.out.println(instr_mnemonic_);
		if (instr_mnemonic_.equals(ADD)) System.out.println("ADD");
		if (instr_mnemonic_.equals(ADDI)) System.out.println("ADDI");
		if (instr_mnemonic_.equals(BEQ)) System.out.println("BEQ");
		if (instr_mnemonic_.equals(BLE)) System.out.println("BLE");
		if (instr_mnemonic_.equals(BNE)) System.out.println("BNE");
		if (instr_mnemonic_.equals(JMP)) System.out.println("JMP");
		if (instr_mnemonic_.equals(LW)) System.out.println("LW");
		if (instr_mnemonic_.equals(MUL)) System.out.println("MUL");
		if (instr_mnemonic_.equals(NOP)) System.out.println("NOP");
		if (instr_mnemonic_.equals(SUB)) System.out.println("SUB");
		if (instr_mnemonic_.equals(SW)) System.out.println("SW");
	}
}
