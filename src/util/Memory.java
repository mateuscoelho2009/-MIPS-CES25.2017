package util;

import java.util.ArrayList;

public class Memory {
	private String[] mem;
	private int[] Qi;
	private int _memSize = 0;
	public Memory(int size){
		mem = new String[size];
		_memSize = size;
		Qi = new int[size];
		for(int i=0; i<mem.length;i++) {
			mem[i]="00000000000000000000000000000000";
			Qi[i] = -1;
		}
    	System.out.println("Inicializando a memória.");
	}
	public void write(int pos, String value){
		setUsed(pos, 1);
		mem[pos] = value;
		System.out.println("Save mem["+pos+"] = "+mem[pos]);
			
	}	
	public void write(int pos, int value){
		setUsed(pos, 1);
		mem[pos] = String.format("%16s", Integer.toBinaryString(value)).replace(' ', '0');
		System.out.println("Save mem["+pos+"] = "+mem[pos]);
			
	}	
	public String read(int pos){
		setUsed(pos, 1);
		System.out.println("Read mem["+pos+"] = "+mem[pos]);
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
	
	public Object[][] getMemoryInfo() {
		ArrayList<Integer> usedMem = new ArrayList<Integer>();
		for (int i = 0; i < _memSize; i++) {
			if (mBeingUsed(i)) usedMem.add(i);
		}
		System.out.println("MEM SIZE USED: " + usedMem.size());
		Object[][] data = new Object[usedMem.size()][];
		for (int i = 0; i < usedMem.size(); i++) {
			data[i] = new Object[] {usedMem.get(i), mem[usedMem.get(i)], Integer.parseInt(mem[usedMem.get(i)],2)}; 
		}
		return data;
		
	}
	public int rInt(int pos) {
		return Integer.parseInt(mem[pos], 2);
	}
}
