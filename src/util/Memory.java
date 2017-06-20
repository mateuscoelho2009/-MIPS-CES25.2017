package util;

public class Memory {
	private String[] mem;
	public Memory(int size){
		mem = new String[size];
	}
	public void write(int pos, String value){
		mem[pos] = value;
	}	
	public String read(int pos){
		return mem[pos];
	}
}
