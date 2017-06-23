package util;

import java.io.IOException;

public class Arch {

	
	public static Register r = new Register();
	public static Memory m = new Memory(4000);
	public static Program p;
	public static Ula ula = new Ula();
	

	
    public static void main(String[] args) throws IOException {
    	System.out.println("Inicializando...");
    	p = new Program("test_without_comments2.txt");
    	long clock = 0;
    	boolean done = true;
    	while(!p.end()||!done){
    		System.out.print(clock + " - ");
    		if(done) {
    			ula.set(p.getNextInstruction());
    			done = false;
    		}
        	
    		done = ula.tick();
    		System.out.println();
    		clock++;
    	}
    	System.out.println("Encerrando...");
    	System.out.println("R2 = " + Arch.r.rInt(2));
    }



	public static void restart() {
		r = new Register();
		m = new Memory(4000);
		ula = new Ula();
		
	}
}

