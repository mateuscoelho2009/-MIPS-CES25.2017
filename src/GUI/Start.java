package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JButton;
import javax.swing.JComboBox;

public class Start extends JFrame {
	private final JFileChooser fc = new JFileChooser();
	private JPanel contentPane;
	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Start frame = new Start();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public Start() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 357, 201);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		
		String[] predictionTypes = {"Static prediction", "1-bit prediction", "2-bit prediction"};
		final JComboBox comboBox = new JComboBox(predictionTypes);
		contentPane.add(comboBox, BorderLayout.NORTH);
		JButton btnChooseFile = new JButton("Choose File & Start");
		contentPane.add(btnChooseFile, BorderLayout.SOUTH);
		btnChooseFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				 int returnVal = fc.showOpenDialog(null);
				 if (returnVal == JFileChooser.APPROVE_OPTION) {
					 File file = fc.getSelectedFile();					 
					 try {
						ControlButtonGUI.run(file.toString(), comboBox.getSelectedItem().toString());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				 }
			}
		});
	}
}
