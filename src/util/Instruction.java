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
	public static enum TYPE2 {NONE,LOAD,ADD,MULT}
	public static enum STATE {FREE,ISSUE,EXECUTE,WRITE,COMMIT}
	String instr_mnemonic_;
	INSTR_TYPE type_;
	STATE state;
	private boolean done,lw_step2;
	int rs, rt, rd, targetAddress, shamt, imm, pc, b, h, ticker, result;
	
	public void issue (int r) {
		if (Arch.RegisterStat.isBusy(rs)){
			h = Arch.RegisterStat.reorder[rs];
			if(Arch.rob.ready(h)){
				Arch.rs[r].Vj = Arch.rob.value(h);
				Arch.rs[r].Qj = -1;
			} else {
				Arch.rs[r].Qj = h;
			}
		} else {
			Arch.rs[r].Vj = Arch.Regs(rs);
			Arch.rs[r].Qj = -1;
		}
		Arch.rs[r].setBusy(true);
		b = Arch.rob.addInstruction(this,r);
		Arch.rs[r].setDest(b);
		specific_issue(r,b);
		state = STATE.ISSUE;
		done = true;
	}
	
	public void specific_issue(int r, int b){
		if(instr_mnemonic_.equals(ADD)||
				instr_mnemonic_.equals(ADDI)||
				instr_mnemonic_.equals(MUL)||
				instr_mnemonic_.equals(NOP)||
				instr_mnemonic_.equals(SUB)||
				instr_mnemonic_.equals(SW)){
			if (Arch.RegisterStat.isBusy(rt)){
				h = Arch.RegisterStat.reorder[rt];
				if(Arch.rob.ready(h)){
					Arch.rs[r].Vk = Arch.rob.value(h);
					Arch.rs[r].Qk = -1;
				} else {
					Arch.rs[r].Qk = h;
				}
			} else {
				Arch.rs[r].Vk = Arch.Regs(rt);
				Arch.rs[r].Qk = -1;
			}
		}
		if(instr_mnemonic_.equals(ADD)||
				instr_mnemonic_.equals(ADDI)||
				instr_mnemonic_.equals(MUL)||
				instr_mnemonic_.equals(NOP)||
				instr_mnemonic_.equals(SUB)){
			Arch.RegisterStat.setReorder(rd,b);
			Arch.RegisterStat.setBusy(rd);
			Arch.rob.setDest(b,rd);
		}	
		if(instr_mnemonic_.equals(LW)){
			lw_step2 = false;
			Arch.rs[r].A = imm;
			Arch.RegisterStat.reorder[rt] = b;
			Arch.RegisterStat.setBusy(rt);
			Arch.rob.setDest(b, rt);
		}	
		if(instr_mnemonic_.equals(SW)){
			Arch.rs[r].A = imm;
		}	
	}
	
	public boolean execute(int Vj, int Vk, int A, int rs_id){
		state = STATE.EXECUTE;
		done = false;
		if (tick()){
			// Caso FP
			if(getMnemonic().equals(ADD)){
				result = Vj+Vk;
				done=true;
			}
			if(getMnemonic().equals(ADDI)){
				result = Vj+A;
				done=true;
			}
			if(getMnemonic().equals(MUL)){
				result = Vj*Vk;
				done=true;
			}
			if(getMnemonic().equals(SUB)){
				result = Vj-Vk;
				done=true;
			}
			if(getMnemonic().equals(NOP)){
				done=true;
			}
			//Caso Load
			if(getMnemonic().equals(LW)){
				if(!lw_step2){
					Arch.rs[rs_id].A = Vj+A;
					done=false;
					lw_step2 = true;
				} else {
					result = Arch.Mem.rInt(A);
					done=true;
				}
			}
			//Caso Store
			if(getMnemonic().equals(SW)){
				Arch.rob.setAddress(h,Vj+A);
				done=true;
			}
		}	
		return done;
	}
	
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
				imm = convBinStr2Int(byteCode.substring(17,32));
				if (imm_sgn==1)
					imm = imm*-1;
				
				break;
			case J:
				targetAddress = convBinStr2Int(byteCode.substring(6,32));
				break;
			default: break;
		}
		setTicker();
		state = STATE.ISSUE;
		done = false;
		//pc = 0;
	}
	
	public void setTicker(){
		if(getMnemonic().equals(SW))
			ticker=4;
		else if(getMnemonic().equals(LW)||getMnemonic().equals(MUL)) // Load usa 2 clocks de fato
			ticker=3;
		else
			ticker=1;
	}
	
	private boolean tick(){
		ticker--;
		if(ticker<=0)
			return true;
		else
			return false;
	}
	
	/*public Instruction (String byteCode, int pc) {
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
				imm = convBinStr2Int(byteCode.substring(17,32));
				if (imm_sgn==1)
					imm = imm*-1;
				
				break;
			case J:
				targetAddress = convBinStr2Int(byteCode.substring(6,32));
				break;
			default: break;
		}
	}*/
	
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
		return imm;
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
			mnemonic = "ADDI R"+rt+",R"+rs+","+imm;
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
			mnemonic = "JMP "+imm;
			break;
		case LW:
			mnemonic = "LW R"+rt+","+imm+"(R"+rs+")";
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
			mnemonic = "SW R"+rt+","+imm+"(R"+rs+")";
			break;
		default:
			mnemonic = "invalid";
			break;
		}
		return mnemonic;
	}
}
