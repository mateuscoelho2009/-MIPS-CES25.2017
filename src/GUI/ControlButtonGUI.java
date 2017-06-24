package GUI;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import util.ArchTomasulo;
import util.PredTomasulo;
import util.Predictor1Bit;
import util.Predictor2Bit;

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
		System.out.println(predictionType);
		switch (predictionType) {
		case "No prediction":
			arch = new ArchTomasulo(path);
			if (!arch.equals(null))
				System.out.println("no pred");
			break;
		case "1-bit prediction":
			arch = new PredTomasulo(path, new Predictor1Bit());
			if (!arch.equals(null))
				System.out.println("1 pred");
			break;
		case "2-bit prediction":
			arch = new PredTomasulo(path, new Predictor2Bit());
			if (!arch.equals(null))
				System.out.println("2 pred");
			break;
		default:
			System.out.println("default");
			break;
		}
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
		Button button2 = new Button("Run All");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!_running){
					executeAction(arch);
				}
			}
		});
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!_running){
					executeAll(arch);
				}
			}
		});
		contentPane.add(button, BorderLayout.NORTH);
		contentPane.add(button2, BorderLayout.CENTER);
		
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
	public static void executeAll(ArchTomasulo arch) {
		try {
			arch.runAll();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		_userInterface.runCycle();
	}
}
