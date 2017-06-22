package util;


public class Register {
	private String[] r;
	private int[] Qi;
	public Register(){
		r = new String[32];
		Qi = new int[32];
		for(int i=0; i<r.length;i++) {
			r[i]="00000000000000000000000000000000";
			Qi[i] = -1;
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
		return (Qi[pos] != -1);
	}
	public void setUsed (int pos, int instr) {
		Qi[pos] = instr;
	}
	public void clearUsed (int pos) {
		Qi[pos] = -1;
	}
	public int rBeingUsedBy(int pos) {
		// TODO Auto-generated method stub
		return Qi[pos];
	}
}
