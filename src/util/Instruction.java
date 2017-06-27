package util;

public class Instruction implements Cloneable{
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
	String mnemonic;
	INSTR_TYPE type_;
	STATE state;
	public boolean done, lw_step2,branched;
	int rs, rt, rd, targetAddress, shamt, imm, pc, b, h, d, ticker, result;
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
	public void issue (int r) {
		if (Arch.RegisterStat.isBusy(rs)){
			h = Arch.RegisterStat.reorder[rs];
			if(Arch.ROB.ready(h)){
				Arch.rs[r].Vj = Arch.ROB.value(h); // troquei h por r
				Arch.rs[r].Qj = -1;
			} else {
				Arch.rs[r].Qj = h;
			}
		} else {
			Arch.rs[r].Vj = Arch.Regs(rs);
			Arch.rs[r].Qj = -1;
		}
		Arch.rs[r].setBusy(true);
		b = Arch.ROB.addInstruction(this,r);
		Arch.rs[r].setDest(b);
		specific_issue(r,b);
		state = STATE.ISSUE;
		done = true;
	}
	
	public void specific_issue(int r, int b){
		if(mnemonic.equals(ADD)||
				//mnemonic.equals(ADDI)||
				mnemonic.equals(MUL)||
				mnemonic.equals(NOP)||
				mnemonic.equals(SUB)||
				mnemonic.equals(SW)||
				mnemonic.equals(BEQ)||
				mnemonic.equals(BLE)||
				mnemonic.equals(BNE)){
			if (Arch.RegisterStat.isBusy(rt)){
				h = Arch.RegisterStat.reorder[rt];
				if(Arch.ROB.ready(b)){	
					Arch.rs[r].Vk = Arch.ROB.value(h);
					Arch.rs[r].Qk = -1;
				} else {
					Arch.rs[r].Qk = h;
				}
			} else {
				Arch.rs[r].Vk = Arch.Regs(rt);
				Arch.rs[r].Qk = -1;
			}
		}
		if(mnemonic.equals(ADD)||
				mnemonic.equals(MUL)||
				mnemonic.equals(NOP)||
				mnemonic.equals(SUB)){
			Arch.RegisterStat.setReorder(rd,b);
			Arch.RegisterStat.setBusy(rd);
			Arch.ROB.setDest(b,rd);
		}	
		if(mnemonic.equals(ADDI)||mnemonic.equals(LW)){
			lw_step2 = false;
			Arch.rs[r].A = imm;
			Arch.RegisterStat.reorder[rt] = b;
			Arch.RegisterStat.setBusy(rt);
			Arch.ROB.setDest(b, rt);
		}	
		if(mnemonic.equals(SW)){
			Arch.rs[r].A = imm;
		}	
	}
	
	public boolean execute(int Vj, int Vk, int A, int rs_id){
		h = Arch.ROB.getHead();
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
				System.out.println("h:"+h+"Vj+a:"+(Vj+A));
				Arch.ROB.setAddress(h,Vj+A);
				done=true;
			}
			//Branches
			if(getMnemonic().equals(BEQ)||getMnemonic().equals(BNE)||getMnemonic().equals(BLE)){
				boolean go;
				if(Arch.rs[rs_id].Qj!=-1 && Arch.rs[rs_id].Qk!=-1){
					go = doBranch(Vj,Vk,getMnemonic());
				} else {
					go = Arch.predictor.executeBranch();
				}
				if(go){
					if(getMnemonic().equals(BLE)){
						Arch.p.setPC(imm);
					} else {
						Arch.p.setPC(Arch.p.getPC()+4+imm); 
					}
					branched = true;
					Arch.lastBranched = true;
				} else {
					branched = false;
				}
				done=true;
			}
		}	
		return done;
	}
	
	public boolean doBranch(int rs, int rt, String type){
		switch(type){
		case BEQ:
			return (Arch.RegisterStat.rInt(rs)==Arch.RegisterStat.rInt(rt));
		case BNE:
			return (Arch.RegisterStat.rInt(rs)!=Arch.RegisterStat.rInt(rt));
		case BLE:
			return (Arch.RegisterStat.rInt(rs)<=Arch.RegisterStat.rInt(rt));
		}
		return false;
	}
	
	public void write(int r){
		state = STATE.WRITE;
		done = false;
		h = Arch.ROB.getHead();
		if(!getMnemonic().equals(SW)){
			b = Arch.rs[r].dest;
			Arch.rs[r].setBusy(false);
			for(int x=0;x<Arch.rs.length;x++){
				if(Arch.rs[x].Qj==b){
					Arch.rs[x].Vj = result;
					Arch.rs[x].Qj = -1;
				}
				if(Arch.rs[x].Qk==b){
					Arch.rs[x].Vk = result;
					Arch.rs[x].Qk = -1;
				}
			}
			Arch.ROB.setValue(b,result);
			Arch.ROB.setReady(b);
			done = true;
		} else {
			Arch.ROB.setValue(h, Arch.rs[r].Vk);
			Arch.ROB.setReady(h);
			done = true;
		}
	}
	
	public boolean commit(){
		state = STATE.COMMIT;
		done = false;
		boolean ret = true;
		h=Arch.ROB.getHead();
		if(Arch.ROB.getDest(h)!=-1){
			d = Arch.ROB.getDest(h);
		}
		if(Arch.ROB.isBranch(h)){
			if(branched != doBranch(rs,rt,getMnemonic())) {
				Arch.predictor.updateState(false);
				Arch.ROB.clear(h);
				if(doBranch(rs,rt,getMnemonic())){
					if(getMnemonic().equals(BEQ)||getMnemonic().equals(BNE))
						Arch.p.setPC(Arch.p.getPC()+4+imm);
					else
						Arch.p.setPC(imm);
				} 
				else {
					Arch.p.setPC(pc );
				}
				Arch.ROB.setBusy(h, false);
				ret = false;
			}
			Arch.predictor.updateState(true);
		} else if(Arch.ROB.getInstruction(h).getMnemonic().equals(SW)) { //ROB[h].Instruction==Store
			Arch.Mem.write(Arch.ROB.getAddress(h), Arch.ROB.getValue(h));
		} else {
			Arch.Regs(d,Arch.ROB.getValue(h));
		}
		Arch.ROB.setBusy(h,false);
		if(Arch.RegisterStat.reorder[d]==h){
			Arch.RegisterStat.setNotBusy(d);
			Arch.RegisterStat.reorder[d]=-1;
		}
		return ret;
	}
	
	public Instruction (String byteCode) {
		String opcode = byteCode.substring(0, 6);
		switch(opcode) {
			case TYPER: type_ = INSTR_TYPE.R; break;
			case ADDI: case BEQ: case BLE: case LW: case SW:
				type_ = INSTR_TYPE.I; mnemonic = opcode;
				break;
			case JMP: type_ = INSTR_TYPE.J; mnemonic = opcode;
				break;
			//Caso qualquer outra opção, considera LI e processa como ADDI
			default: type_ = INSTR_TYPE.I; mnemonic = ADDI;
		}
		
		if (type_ == INSTR_TYPE.R)
			mnemonic = byteCode.substring(26,32);
		
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
		pc = 0;
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
	
	private int convBinStr2Int (String binStr) {
		int res = 0;
		
		for (int i = 0; i < binStr.length(); i++) {
			res *= 2;
			if (binStr.charAt(i) == '1') res += 1;
		}
		
		return res;
	}
	
	String getMnemonic () {
		return mnemonic;
	}
	
	int getPC() {
		return pc;
	}
	
	void setPC(int pc_) {
		pc = pc_;
	}

	public String toString() {
		String mnemonic_s;
		switch(mnemonic){
		case ADD:
			mnemonic_s = "ADD R"+rd+",R"+rs+",R"+rt;
			break;
		case ADDI:
			mnemonic_s = "ADDI R"+rt+",R"+rs+","+imm;
			break;
		case BEQ:
			mnemonic_s = "BEQ R"+rs+",R"+rt+","+imm;
			break;
		case BLE:
			mnemonic_s = "BLE R"+rs+",R"+rt+","+imm;
			break;
		case BNE:
			mnemonic_s = "BNE R"+rs+",R"+rt+","+imm;
			break;
		case JMP:
			mnemonic_s = "JMP "+imm;
			break;
		case LW:
			mnemonic_s = "LW R"+rt+","+imm+"(R"+rs+")";
			break;
		case MUL:
			mnemonic_s = "MUL R"+rd+",R"+rs+",R"+rt;
			break;
		case NOP:
			mnemonic_s = "NOP";
			break;
		case SUB:
			mnemonic_s = "SUB R"+rd+",R"+rs+",R"+rt;
			break;
		case SW:
			mnemonic_s = "SW R"+rt+","+imm+"(R"+rs+")";
			break;
		default:
			mnemonic_s = "invalid";
			break;
		}
		return mnemonic_s;
	}

	public STATE getState() {
		return state;
	}

	public void clearInst() {
		setTicker();
		state = STATE.ISSUE;
		done = false;
		result = -1;
		lw_step2=false;
	}
}
