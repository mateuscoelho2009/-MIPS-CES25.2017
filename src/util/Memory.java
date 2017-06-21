package util;

import util.ArchTomassulo.STATION_ID;

public class Memory {
	private String[] mem;
	private ArchTomassulo.STATION_ID[] Qi;
	
	public Memory(int size){
		mem = new String[size];
		Qi = new ArchTomassulo.STATION_ID[size];
		for(int i=0; i<mem.length;i++) {
			mem[i]="00000000000000000000000000000000";
			Qi[i] = STATION_ID.NONE;
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
		return (Qi[pos] != STATION_ID.NONE);
	}
	public void setUsed (int pos, ArchTomassulo.STATION_ID instr) {
		Qi[pos] = instr;
	}
	public void clearUsed (int pos) {
		Qi[pos] = STATION_ID.NONE;
	}
	public STATION_ID mBeingUsedBy(int pos) {
		// TODO Auto-generated method stub
		return Qi[pos];
	}
}
