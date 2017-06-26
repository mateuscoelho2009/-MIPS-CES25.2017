package util;

import java.util.LinkedList;

import util.RobEntry.STATE;

public class Rob {
	private LinkedList<RobEntry> entries = new LinkedList<>();
	
	public int addInstruction(Instruction instr, int RSid){
		int b = Arch.reorderUID();
		entries.add(new RobEntry(instr,b));
		entries.getLast().setRSid(RSid);
		entries.getLast().setState(STATE.ISSUE);
		entries.getLast().setReady(false);
		return b;
	}
	public boolean isEmpty(){
		return (entries.size()<=0);
	}
	public void setBranching(boolean branched){
		entries.getLast().setBranch(branched);
	}
	public Object[][] getListInfo() {
		Object[][] data = new Object[entries.size()][];
		for (int i = 0; i < entries.size(); i++) {
			data[i] = new Object[] {entries.get(i).getInstruction(),
					entries.get(i).getState(),
					entries.get(i).getDestination(),
					entries.get(i).getResult(),
					entries.get(i).isReady(),
					entries.get(i).isIn(),
					entries.get(i).type(),
					entries.get(i).hasBranched()}; 
		}
		return data;
	}
	/*public void setInstructionState(Rs rs){
		for (RobEntry entry : entries){
			if (rs.id_ == entry.isIn()){
				switch (rs.state) {
				case ISSUE:
					entry.setState(STATE.ISSUE);
					break;
				case EXECUTE:
					entry.setState(STATE.EXECUTE);
					break;
				case WRITE:
					entry.setState(STATE.WRITE);
					break;
				default:
					break;
				}
				return;
			}
		}
	}*/
	
	/*public void getResult(Rs rs){
		for (RobEntry entry : entries){
			if (rs.id_ == entry.isIn()){
				entry.setResult(rs.ula.result);
				entry.makeReady();
				entry.setState(STATE.COMMIT);
				return;
			}
		}
	}*/
	
	public void commit(){
		if(!entries.isEmpty() && entries.getFirst().isReady()){
			RobEntry entry = entries.removeFirst();
			switch (entry.type()) {
			case JUMP:
				break;
			case REGISTER:
				Arch.RegisterStat.wInt(entry.getDestination(), entry.getResult());
				Arch.RegisterStat.setUsed(entry.getDestination(), -1);
				break;
			case STORE:
				Arch.Mem.write(entry.getDestination(),
						String.format("%16s", Integer.toBinaryString(entry.getResult())).replace(' ', '0'));
				Arch.Mem.setUsed(entry.getDestination(), -1);
			case BRANCH:
				if (entry.getResult() == 1 && !entry.hasBranched()){
					Arch.p.setPC(entry.getDestination());
					entries.clear();
					Arch.predictor.updateState(false);
				} else if (entry.getResult() == 0 && entry.hasBranched()) {
					Arch.p.setPC(entry.getInstruction().pc);
					entries.clear();
					Arch.predictor.updateState(false);
				} else if (entry.getResult() == 1 && entry.hasBranched()) {
					Arch.predictor.updateState(true);
				} else if (entry.getResult() == 0 && !entry.hasBranched()) {
					Arch.predictor.updateState(true);
				}
				break;
			default:
				break;
			}
		}
	}
	public boolean ready(int h) {
		return entries.get(h).isReady();
	}
	public int value(int h) {
		return entries.get(h).getResult();
	}
	public int getRealId(int b){
		int real = -1;
		for(int i=0;i<entries.size();i++)
			if (entries.get(i).getUid()==b)
				real=i;
		return real;
	}
	public void setDest(int b, int rd) {
		entries.get(b).setDestination(rd);
	}
	public void setAddress(int h, int a) {
		entries.get(h).setAddress(a);
		
	}
	public void setValue(int b, int result) {
		entries.get(b).setValue(result);
	}
	public void setReady(int b) {
		entries.get(b).setReady(true);
	}
	public int getDest(int h) {
		return entries.get(h).getDestination();
	}
	public boolean isBranch(int h) {
		return entries.get(h).isBranch();
	}
	public void clear() {
		entries.clear();
	}
	public RobEntry getFirst() {
		return entries.getFirst();
	}
	public void delFirst() {
		entries.removeFirst();
	}
	public boolean isHeadStore(){
		return entries.getFirst().getInstruction().getMnemonic().equals(Instruction.SW);
	}
	public boolean haveStoreWithAddress(int a) {
		for(int i=0;i<entries.size();i++){
			if(entries.get(i).getAddress()==a)
				return true;
		}
		return false;
	}
}
