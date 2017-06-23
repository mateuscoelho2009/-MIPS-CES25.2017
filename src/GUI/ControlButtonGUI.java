package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import util.ArchTomassulo;

import javax.swing.JButton;
import java.awt.Button;
import javax.swing.JSeparator;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class ControlButtonGUI extends JFrame {

	private JPanel contentPane;
	private static GUI _userInterface;
	private static boolean _running;

	/**
	 * Launch the application.
	 * @throws IOException 
	 */
	
	public static void main(String[] args) throws IOException {
		System.out.println("Inicializando...");
		_running = false;
		final ArchTomassulo arch = new ArchTomassulo("test_without_comments2.txt");
		_userInterface = new GUI(arch);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ControlButtonGUI frame = new ControlButtonGUI(_userInterface, arch);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	public ControlButtonGUI(GUI userInterface, final ArchTomassulo arch) {
		_userInterface = userInterface;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 442, 90);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		if (_running) executeAction(arch);
		Button button = new Button("Clock");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!_running){
					executeAction(arch);
				}
			}
		});
		contentPane.add(button, BorderLayout.CENTER);
	}
	
	public static void executeAction(ArchTomassulo arch) {
		try {
			arch.run();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		_userInterface.runCycle();
		
	}

}
