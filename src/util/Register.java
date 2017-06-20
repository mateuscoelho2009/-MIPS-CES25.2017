package util;

public class Register {
	private String[] r;
	public Register(){
		r = new String[32];
	}
	public void write(int pos, String value){
		r[pos] = value;
	}	
	public String read(int pos){
		return r[pos];
	}
}
