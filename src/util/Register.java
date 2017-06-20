package util;

public class Register {
	private String[] r;
	public Register(){
		r = new String[32];
		for(int i=0; i<r.length;i++)
			r[i]="00000000000000000000000000000000";
    	System.out.println("Inicializando os registradores.");
	}
	public void write(int pos, String value){
		r[pos] = value;
	}	
	public String read(int pos){
		return r[pos];
	}
	public void wInt(int pos, int number){
		r[pos] = Integer.toBinaryString(number);
	}
	public int rInt(int pos){
		return Integer.parseInt(r[pos], 2);
	}
}
