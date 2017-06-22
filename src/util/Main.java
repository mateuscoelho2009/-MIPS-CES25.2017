package util;

import java.io.IOException;

import GUI.GUI;

public class Main {
	public static void main(String[] args) throws IOException {
		System.out.println("Inicializando...");
		ArchTomassulo arch = new ArchTomassulo();
		GUI UI = new GUI(arch);
		arch.run("test_without_comments2.txt");
	
	}

}
