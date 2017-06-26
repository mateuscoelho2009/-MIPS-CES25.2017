package util;

import java.util.LinkedList;


public class Rob {
	private LinkedList<RobEntry> entries = new LinkedList<RobEntry>();
	//private int head=0;
	public int addInstruction(Instruction instr, int RSid){
		//int b = Arch.reorderUID();
		RobEntry novo = new RobEntry(instr);
		novo.setRSid(RSid);
		novo.setReady(false);
		entries.add(novo);
		return entries.indexOf(novo);
	}
	public boolean isEmpty(){
		return (entries.size()<=0);
	}
	public int getHead(){
		//return head;
		for(int i=0;i<entries.size();i++){
			if(entries.get(i).isBusy())
				return i;
		}
		return -1;
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
					entries.get(i).isBusy(),
					entries.get(i).hasBranched(),
					entries.get(i).getAddress()}; 
		}
		return data;
	}
	
	public boolean commit(){
		if(!entries.isEmpty() && getHead()!=-1 && entries.get(getHead()).isReady()){
			Arch.concludedInstructions++;
			return entries.get(getHead()).getInstruction().commit();
		}
		return true;
	}
	public boolean ready(int h) {
		return entries.get(h).isReady();
	}
	public int value(int h) {
		return entries.get(h).getResult();
	}
	public void setDest(int h, int rd) {
		entries.get(h).setDestination(rd);
	}
	public void setAddress(int h, int a) {
		entries.get(h).setAddress(a);
	}
	public void setValue(int h, int result) {
		entries.get(h).setValue(result);
	}
	public void setReady(int h) {
		entries.get(h).setReady(true);
	}
	public int getDest(int h) {
		return entries.get(h).getDestination();
	}
	public int getValue(int h) {
		return entries.get(h).getValue();
	}
	public Instruction getInstruction(int h) {
		return entries.get(h).getInstruction();
	}
	public boolean isBranch(int h) {
		return entries.get(h).isBranch();
	}
	public void clear(int h) {
		for(int i = entries.size()-1;i>h;i--){
			entries.remove(i).getInstruction().clearInst();
		}
		for(int i=0;i<Arch.rs.length;i++){
			Arch.rs[i].clearRS();
		}
	}
	public RobEntry getFirst() {
		return entries.get(getHead());
	}
	public void delFirst() {
		entries.removeFirst();
	}
	public boolean isHeadStore(){
		return entries.get(getHead()).getInstruction().getMnemonic().equals(Instruction.SW);
	}
	public boolean haveStoreWithAddress(int a) {
		for(int i=0;i<entries.size();i++){
			if(entries.get(i).getAddress()==a)
				return true;
		}
		return false;
	}
	public void setBusy(int h, boolean b) {
		if(entries.size()>h)
			entries.get(h).setBusy(b);
	}
	public int getAddress(int h) {
		return entries.get(h).getAddress();
	}
}
