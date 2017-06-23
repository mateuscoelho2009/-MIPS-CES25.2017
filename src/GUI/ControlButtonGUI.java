package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import util.ArchTomasulo;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.Button;
import java.awt.Container;

import javax.swing.JSeparator;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;

public class ControlButtonGUI extends JFrame {

	private JPanel contentPane;
	private static GUI _userInterface;
	private final JFileChooser fc = new JFileChooser();
	private static boolean _running;
	private static ArchTomasulo arch = null;

	/**
	 * Launch the application.
	 * @throws IOException 
	 */
	
	public static void run(String path, String predictionType) throws IOException {
		System.out.println("Inicializando...");
		_running = false;
		arch = new ArchTomasulo(path);
		_userInterface = new GUI(arch);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ControlButtonGUI frame = new ControlButtonGUI(_userInterface);
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
	public ControlButtonGUI(GUI userInterface) {
		_userInterface = userInterface;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 357, 201);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		if (_running) executeAction(arch);
		Button button = new Button("Click to Clock");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!_running){
					executeAction(arch);
				}
			}
		});
		contentPane.add(button, BorderLayout.CENTER);
		
	}
	
	public static void executeAction(ArchTomasulo arch) {
		try {
			arch.run();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		_userInterface.runCycle();
		
	}

}
