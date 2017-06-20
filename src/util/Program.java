package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class Program {
	private Vector<Instruction> program = new Vector<Instruction>();
	private int pc_ = 0;
	private boolean terminated = false;
	
	public Program(String filename) throws IOException {
		// Open the file
		FileInputStream fstream = new FileInputStream(filename);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine;
		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   {
		  program.add(new Instruction(strLine));
		  //System.out.println(strLine);
		}
		//Close the input stream
		br.close();
	}
	public Instruction getNextInstruction () {
		if (pc_ < 0 || pc_ >= program.size()) {
			pc_ = 0;
			terminated = true;
		}
		Instruction next = program.get(pc_);
		pc_++;
		return next;
	}
	public boolean end(){
		return terminated;
	}

}
