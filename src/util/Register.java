package util;


public class Register {
	private String[] r;
	private int[] Qi;
	private boolean[] busy;
	public int[] reorder;
	public Register(){
		r = new String[32];
		Qi = new int[32];
		busy = new boolean[32];
		reorder = new int[32];
		for(int i=0; i<r.length;i++) {
			r[i]="00000000000000000000000000000000";
			Qi[i] = -1;
			reorder[i] = -1;
			busy[i]=false;
		}
		
    	System.out.println("Inicializando os registradores.");
	}
	public void write(int pos, String value){
		if (pos!=0){
			r[pos] = value;
			Qi[pos] = -1;
			busy[pos] = false;
		}
		//System.out.println("r["+pos+"] = "+r[pos]);
	}	
	public void setBusy(int pos){
		busy[pos]=true;
	}
	public boolean isBusy(int pos){
		return busy[pos];
	}
	public String read(int pos){
		return r[pos];
	}
	public void wInt(int pos, int number){
		if (pos!=0)
			r[pos] = String.format("%16s", Integer.toBinaryString(number)).replace(' ', '0');
		System.out.println("r["+pos+"] = "+r[pos]);
	}
	public int rInt(int pos){
		return Integer.parseInt(r[pos], 2);
	}
	public boolean rBeingUsed (int pos) {
		//return (Qi[pos] != -1);
		return busy[pos];
	}
	public void setUsed (int pos, int instr) {
		Qi[pos] = instr;
		System.out.println("R" + pos + " Used now by " + instr);
	}
	public void clearUsed (int pos) {
		//Qi[pos] = -1;
	}
	public int rBeingUsedBy(int pos) {
		return Qi[pos];
	}
	public Object[] getInfo(int rID) {
		return new Object[] {rID, Qi[rID], rInt(rID),reorder[rID],busy[rID]};
	}
	public void setReorder(int rd, int b) {
		reorder[rd]=b;
	}
	public void setNotBusy(int d) {
		busy[d]=false;
	}
}
