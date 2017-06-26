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
	
	public void commit(){
		if(!entries.isEmpty() && entries.getFirst().isReady()){
			entries.getFirst().getInstruction().commit();
		}
	}
	public boolean ready(int h) {
		return entries.get(getRealId(h)).isReady();
	}
	public int value(int h) {
		return entries.get(getRealId(h)).getResult();
	}
	public int getRealId(int b){
		int real = -1;
		for(int i=0;i<entries.size();i++)
			if (entries.get(i).getUid()==b)
				real=i;
		return real;
	}
	public void setDest(int h, int rd) {
		entries.get(getRealId(h)).setDestination(rd);
	}
	public void setAddress(int h, int a) {
		entries.get(h).setAddress(a);
		
	}
	public void setValue(int h, int result) {
		entries.get(getRealId(h)).setValue(result);
	}
	public void setReady(int h) {
		entries.get(getRealId(h)).setReady(true);
	}
	public int getDest(int h) {
		return entries.get(getRealId(h)).getDestination();
	}
	public boolean isBranch(int h) {
		return entries.get(getRealId(h)).isBranch();
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
