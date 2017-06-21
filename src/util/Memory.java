package util;

public class Memory {
	private String[] mem;
	public Memory(int size){
		mem = new String[size];
		for(int i=0; i<mem.length;i++)
			mem[i]="00000000000000000000000000000000";
    	System.out.println("Inicializando a memória.");
	}
	public void write(int pos, String value){
		mem[pos] = value;
		//System.out.println("mem["+pos+"] = "+mem[pos]);
			
	}	
	public String read(int pos){
		//System.out.println("mem["+pos+"] = "+mem[pos]);
		return mem[pos];
	}
}
