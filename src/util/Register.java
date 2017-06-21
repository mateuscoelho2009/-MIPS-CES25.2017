package util;

public class Register {
	private String[] r;
	private int[] whoIsUsing;
	public Register(){
		r = new String[32];
		whoIsUsing = new int[32];
		for(int i=0; i<r.length;i++) {
			r[i]="00000000000000000000000000000000";
			whoIsUsing[i] = -1;
		}
    	System.out.println("Inicializando os registradores.");
	}
	public void write(int pos, String value){
		if (pos!=0)
			r[pos] = value;
		//System.out.println("r["+pos+"] = "+r[pos]);
	}	
	public String read(int pos){
		return r[pos];
	}
	public void wInt(int pos, int number){
		if (pos!=0)
			r[pos] = String.format("%16s", Integer.toBinaryString(number)).replace(' ', '0');
		//System.out.println("r["+pos+"] = "+r[pos]);
	}
	public int rInt(int pos){
		return Integer.parseInt(r[pos], 2);
	}
	public boolean rBeingUsed (int pos) {
		return (whoIsUsing[pos] != -1);
	}
	public void setUsed (int pos, int instr) {
		whoIsUsing[pos] = instr;
	}
	public void clearUsed (int pos) {
		whoIsUsing[pos] = -1;
	}
}
