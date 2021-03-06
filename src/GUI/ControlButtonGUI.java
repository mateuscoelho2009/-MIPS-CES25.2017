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

import util.Arch;
import util.Predictor1Bit;
import util.Predictor2Bit;
import util.PredictorDumb;

public class ControlButtonGUI extends JFrame {

	private JPanel contentPane;
	private static GUI _userInterface;
	private final JFileChooser fc = new JFileChooser();
	private static boolean _running;
	private static Arch arch = null;

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
			arch = new Arch(path, new PredictorDumb());
			if (!arch.equals(null))
				System.out.println("no pred");
			break;
		case "1-bit prediction":
			arch = new Arch(path, new Predictor1Bit());
			if (!arch.equals(null))
				System.out.println("1 pred");
			break;
		case "2-bit prediction":
			arch = new Arch(path, new Predictor2Bit());
			if (!arch.equals(null))
				System.out.println("2 pred");
			break;
		default:
			System.out.println("default");
			break;
		}
		_userInterface = new GUI();
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
	 * @throws CloneNotSupportedException 
	 */
	public ControlButtonGUI(GUI userInterface) throws CloneNotSupportedException {
		_userInterface = userInterface;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 357, 201);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		if (_running) executeAction();
		Button button = new Button("Click to Clock");
		Button button2 = new Button("Run All");
		Button button3 = new Button("Run 20 clocks");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!_running){
					try {
						executeAction();
					} catch (CloneNotSupportedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!_running){
					try {
						executeAll();
					} catch (CloneNotSupportedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		button3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!_running){
					try {
						execute20();
					} catch (CloneNotSupportedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		contentPane.add(button, BorderLayout.NORTH);
		contentPane.add(button2, BorderLayout.CENTER);
		contentPane.add(button3, BorderLayout.SOUTH);
		
	}
	public static void execute20() throws CloneNotSupportedException {
		try {
			Arch.run20();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		_userInterface.runCycle();
	}	
	public static void executeAction() throws CloneNotSupportedException {
		try {
			Arch.run();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		_userInterface.runCycle();
	}
	public static void executeAll() throws CloneNotSupportedException {
		try {
			Arch.runAll();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		_userInterface.runCycle();
	}
}
