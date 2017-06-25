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

	public String toString() {
		String mnemonic;
		switch(instr_mnemonic_){
		case ADD:
			mnemonic = "ADD R"+rd+",R"+rs+",R"+rt;
			break;
		case ADDI:
			mnemonic = "ADDI R"+rt+",R"+rs+","+immediate;
			break;
		case BEQ:
			mnemonic = "BEQ R"+rs+",R"+rt+","+targetAddress;
			break;
		case BLE:
			mnemonic = "BLE R"+rs+",R"+rt+","+targetAddress;
			break;
		case BNE:
			mnemonic = "BNE R"+rs+",R"+rt+","+targetAddress;
			break;
		case JMP:
			mnemonic = "JMP "+immediate;
			break;
		case LW:
			mnemonic = "LW R"+rt+","+immediate+"(R"+rs+")";
			break;
		case MUL:
			mnemonic = "MUL R"+rd+",R"+rs+",R"+rt;
			break;
		case NOP:
			mnemonic = "NOP";
			break;
		case SUB:
			mnemonic = "SUB R"+rd+",R"+rs+",R"+rt;
			break;
		case SW:
			mnemonic = "SW R"+rt+","+immediate+"(R"+rs+")";
			break;
		default:
			mnemonic = "invalid";
			break;
		}
		return mnemonic;
	}
}
