package util;

import java.io.IOException;

public class Arch {

	
	public static Register r = new Register();
	public static Memory m = new Memory(4000);
	public static Program p;
	public static ULA ula = new ULA();
	

	
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
    		else{
        		done = ula.tick();
    			System.out.println();
			}
    		clock++;
    	}
    	System.out.println("Encerrando...");
    }
}

