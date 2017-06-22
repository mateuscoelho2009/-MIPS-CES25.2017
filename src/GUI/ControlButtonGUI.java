package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.Button;
import javax.swing.JSeparator;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

public class ControlButtonGUI extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	/*
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ControlButtonGUI frame = new ControlButtonGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the frame.
	 */
	public ControlButtonGUI(final GUI userInterface) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 442, 90);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		Button button = new Button("Play");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userInterface.runCycle();
			}
		});
		contentPane.add(button, BorderLayout.WEST);
		
		Button button_1 = new Button("Pause");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userInterface.setRunning(false);
			}
		});
		contentPane.add(button_1, BorderLayout.EAST);
		
		Button button_2 = new Button("Fast Foward");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userInterface.setRunning(true);
			}
		});
		contentPane.add(button_2, BorderLayout.CENTER);
	}

}
