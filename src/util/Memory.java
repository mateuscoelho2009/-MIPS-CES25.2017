package util;

public class Memory {
	private String[] mem;
	private int[] Qi;
	
	public Memory(int size){
		mem = new String[size];
		Qi = new int[size];
		for(int i=0; i<mem.length;i++) {
			mem[i]="00000000000000000000000000000000";
			Qi[i] = -1;
		}
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
	public boolean mBeingUsed (int pos) {
		return (Qi[pos] != -1);
	}
	public void setUsed (int pos, int instr) {
		Qi[pos] = instr;
	}
	public void clearUsed (int pos) {
		Qi[pos] = -1;
	}
	public int mBeingUsedBy(int pos) {
		// TODO Auto-generated method stub
		return Qi[pos];
	}
}
