package util;

public class Memory {
	private String[] mem;
	public Memory(int size){
		mem = new String[size];
	}
	public void storePosition(int pos, String value){
		mem[pos] = value;
	}	
	public String loadPosition(int pos){
		return mem[pos];
	}
}
