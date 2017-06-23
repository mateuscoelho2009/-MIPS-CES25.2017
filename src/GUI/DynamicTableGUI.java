package GUI;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DynamicTableGUI extends JFrame {
	private JTable _mainTable;
	private DefaultTableModel _modelTable;
	private String[] _rowsTitle;
	public DynamicTableGUI(String title, String[] rowsTitle, Object[][] data) {
		setTitle(title);
		
		_rowsTitle = rowsTitle;
		init(rowsTitle, data);
		add(new JScrollPane(_mainTable));
		pack();
		setVisible(true);
	}
	
	private void init(String[] rowsTitle, Object[][] data) {
		_mainTable = new JTable();
		_mainTable.setGridColor(Color.BLACK);
		_mainTable.setAutoResizeMode(2);
		_modelTable = new DefaultTableModel(new Object[][]{} , rowsTitle);
	    _mainTable.setModel(_modelTable);
	    for(int i =0;i<data.length;i++){
	        _modelTable.addRow(data[i]);
	    }
	}
	
	public void updateTable(Object[][] data) {
		_modelTable = new DefaultTableModel (new Object[][]{}, _rowsTitle);
		_mainTable.setModel(_modelTable);
	    for(int i =0;i<data.length;i++){
	        _modelTable.addRow(data[i]);
	    }
		
	}
	
}
