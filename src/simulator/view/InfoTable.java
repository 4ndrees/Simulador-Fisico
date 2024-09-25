package simulator.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;

public class InfoTable extends JPanel {
	
	String _title;
	TableModel _tableModel;
	JScrollPane js;
	
	InfoTable(String title, TableModel tableModel) {
		_title = title;
		_tableModel = tableModel;
		initGUI();		
	}
	
	private void initGUI() {
		
		// TODO cambiar el layout del panel a BorderLayout()
		this.setLayout(new BorderLayout());
		
		// TODO añadir un borde con título al JPanel, con el texto _title
		TitledBorder t = new TitledBorder(_title);
		
		// TODO añadir un JTable (con barra de desplazamiento vertical) que use
		// _tableModel
		JTable j = new JTable(_tableModel);
		
		if(_title.equals("Groups"))
		j.getColumnModel().getColumn(1).setPreferredWidth(400);
		j.getTableHeader().setResizingAllowed(false);
		j.getTableHeader().setReorderingAllowed(false);
		
		js = new JScrollPane(j);
		
		this.add(js);
		this.setBorder(t);
	}	
}
