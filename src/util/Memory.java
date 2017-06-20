package util;

public class Memory {
	private String[] mem;
	public Memory(int size){
		mem = new String[size];
    	System.out.println("Inicializando a memória.");
	}
	public void write(int pos, String value){
		mem[pos/4] = value;
	}	
	public String read(int pos){
		return mem[pos/4];
	}
}
