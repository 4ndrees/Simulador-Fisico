package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.misc.Vector2D;
import simulator.model.BodiesGroup;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

class ForceLawsDialog extends JDialog implements SimulatorObserver {

	private DefaultComboBoxModel<String> _lawsModel;
	private DefaultComboBoxModel<String> _groupsModel;
	private DefaultTableModel _dataTableModel;
	private Controller _ctrl;
	private List<JSONObject> _forceLawsInfo;
	private String[] _headers = { "Key", "Value", "Description" };
	private int _status = 1;
	private JTextArea description1;
	private JTextArea description2;
	private JLabel _fl;
	private JLabel _g;
	private JButton _ok;
	private JButton _cancel;
	private JComboBox _groups;
	private JComboBox _laws;
	private JTable _lawsTable;

	// TODO en caso de ser necesario, añadir los atributos aquí…
	ForceLawsDialog(Frame parent, Controller ctrl) {

		super(parent, true);
		_ctrl = ctrl;	
		initGUI();
		_ctrl.addObserver(this);
	}
	
	private void initGUI() {

		setTitle("Force Laws Selection");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);
		
		
		JPanel descPanel = new JPanel();
		descPanel.setLayout(new BoxLayout(descPanel, BoxLayout.Y_AXIS));
		mainPanel.add(descPanel, BorderLayout.PAGE_START);
		
		description1 = new JTextArea("Select a force law and provide values for the parameters in the Value column (default values are used for"); 
		description2 = new JTextArea("parameters with no value)");
		description1.setOpaque(false);
		description1.setEditable(false);
		description1.setFont(new Font("Arial", Font.BOLD, 12));
		description2.setOpaque(false);
		description2.setEditable(false);
		description2.setFont(new Font("Arial", Font.BOLD, 12));
		
		descPanel.add(description1);
		descPanel.add(description2);
		
		
		this.add(Box.createVerticalStrut(20));
		
		// _forceLawsInfo se usará para establecer la información en la tabla
		_forceLawsInfo = _ctrl.getForceLawsInfo();
		
		// TODO crear un JTable que use _dataTableModel, y añadirla al panel
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		mainPanel.add(contentPanel, BorderLayout.CENTER);
		
		_dataTableModel = new DefaultTableModel() {

			@Override
			public boolean isCellEditable(int row, int column) {
				
				switch (column) {
				
					case 0:
						return false;
					case 1:
						return true;
					case 2:
						return false;
				}	
				return true;
			}
		};
		
		_dataTableModel.setColumnIdentifiers(_headers);
		_lawsTable = new JTable(_dataTableModel);
		_lawsTable.getTableHeader().setResizingAllowed(false);
		_lawsTable.getTableHeader().setReorderingAllowed(false);
		JScrollPane js = new JScrollPane(_lawsTable);
		js.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		js.setViewportBorder(new EmptyBorder(0, 0, 0, 0));
		contentPanel.add(js);
		
		//Panel secundario para las ComboBox
		
		this.add(Box.createVerticalStrut(20));
		
		JPanel boxPanel = new JPanel();
		mainPanel.add(boxPanel, BorderLayout.PAGE_END);
		
		// TODO añadir la descripción de todas las leyes de fuerza a _lawsModel
		String[] lawsDesc = new String[_forceLawsInfo.size()];
		
		int cont = 0;
		for(JSONObject jo : _forceLawsInfo) {
			
			lawsDesc[cont] = jo.getString("desc");
			cont++;
		}
		
		_lawsModel = new DefaultComboBoxModel<String>(lawsDesc);
		
		// TODO crear un combobox que use _lawsModel y añadirlo al panel
		_fl = new JLabel("Force law: ");
		boxPanel.add(_fl);
		
		_laws = new JComboBox(_lawsModel);
		_laws.setPreferredSize(new Dimension(250, 20));
		_laws.addActionListener((e) -> {
			
			
			for(JSONObject fl : _forceLawsInfo) {
				
				if(_laws.getSelectedItem().equals(fl.getString("desc")))
					modifyTable(fl);
			}
		});
		boxPanel.add(_laws);
		
		// TODO crear un combobox que use _groupsModel y añadirlo al panel
		_g = new JLabel("Group: ");
		boxPanel.add(_g);
		
		_groupsModel = new DefaultComboBoxModel<String>();
		
		_groups = new JComboBox(_groupsModel);
		_groups.setPreferredSize(new Dimension(50, 20));
		
		boxPanel.add(_groups);
		
		// TODO crear los botones OK y Cancel y añadirlos al panel
		
		this.add(Box.createVerticalStrut(10));
		
		JPanel buttonPanel = new JPanel();
		
		mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
		
		_cancel = new JButton("Cancel");
		_cancel.addActionListener((e) -> this.setVisible(false));
		_ok = new JButton("OK");
		_ok.addActionListener((e) -> {
			
			okButton();
			this.setVisible(false);
		});
		
		buttonPanel.add(_cancel);
		buttonPanel.add(_ok);
		if(_groups.getItemCount()==0)
			_ok.setEnabled(false);
		

		
		this.add(Box.createVerticalStrut(10));
		
		setPreferredSize(new Dimension(700, 400));
		pack();
		setResizable(false);
		setVisible(false);
	}
	
	public int open() {

		// TODO Establecer la posición de la ventana de diálogo de tal manera que se
		// abra en el centro de la ventana principal
		pack();
		setLocationRelativeTo(null);
		if(_groups.getItemCount()>0)
			_ok.setEnabled(true);
		setVisible(true);
		
		

		return _status;
	}
	
	private void modifyTable(JSONObject info) {
		
		//Eliminar las filas que existen en la tabla
		int num_row = _dataTableModel.getRowCount(); 
		
		for(int i = 0; i < num_row; i++)
			_dataTableModel.removeRow(i);
		
		//Añadir las nuevas filas en funcion de la ley
		
		JSONObject js = info.getJSONObject("data");
		
		if(info.getString("type").equals("nlug")) {
			
			String row[] = {"G", "", js.getString("G")};
			
			_dataTableModel.addRow(row);
		}
		else if(info.getString("type").equals("mtfp")) {
			
			String row1[] = {"c", "", js.getString("c")};
			String row2[] = {"g", "", js.getString("g")};
			
			_dataTableModel.addRow(row1);
			_dataTableModel.addRow(row2);
		}
	}

	private void okButton() {
		

		JSONObject info = new JSONObject();
		JSONObject data = new JSONObject();
		
		for(JSONObject fl : _forceLawsInfo) {
			
			if(_laws.getSelectedItem().equals(fl.getString("desc"))) {
				info.put("type", fl.getString("type"));
				info.put("desc", fl.getString("desc"));
				
				if(fl.getString("type").equals("nlug")) {
					if(isValidValue(_dataTableModel.getValueAt(0, 1).toString()))
						data.put("G", _dataTableModel.getValueAt(0, 1));
				}
				else if(fl.getString("type").equals("mtfp")) {

					if(isValidValue(_dataTableModel.getValueAt(1, 1).toString()))
						data.put("g", _dataTableModel.getValueAt(1, 1));
						
					JSONArray ja = new JSONArray();
					
					if(isValidArray(_dataTableModel.getValueAt(0, 1).toString())) {
						
						String s = _dataTableModel.getValueAt(0, 1).toString();
						
						s = s.replace("[", "").replace("]", "").replace(" ", "");
						String[] values = s.split(",");
						
						ja.put(Double.parseDouble(values[0]));
						ja.put(Double.parseDouble(values[1]));
						
						data.put("c", ja);
					}
				}
				
				info.put("data", data);
			}
		}
		
		_ctrl.setForceLaws(_groups.getSelectedItem().toString(), info);
	}
	
	private boolean isValidArray(String s) {
		
		if(s.equals("")) {
			return false;
		}
		else {
			s = s.replace("[", "").replace("]", "").replace(" ", "");
			String[] values = s.split(",");
		
			return isValidValue(values[0]) && isValidValue(values[1]);
		}
	}
	
	private boolean isValidValue(String s) {
		
		try {
			Double.parseDouble(s);
			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
	
	@Override
	public void onAdvance(Map<String, BodiesGroup> groups, double time) {}

	@Override
	public void onReset(Map<String, BodiesGroup> groups, double time, double dt) {
		
		_groups.removeAllItems();
	}

	@Override
	public void onRegister(Map<String, BodiesGroup> groups, double time, double dt) {
		
		for(BodiesGroup g : groups.values()) {
			
			_groups.addItem(g.getId());
		}
	}

	@Override
	public void onGroupAdded(Map<String, BodiesGroup> groups, BodiesGroup g) {
		
		_groups.addItem(g.getId());
	}

	@Override
	public void onBodyAdded(Map<String, BodiesGroup> groups, Body b) {}

	@Override
	public void onDeltaTimeChanged(double dt) {}

	@Override
	public void onForceLawsChanged(BodiesGroup g) {}
}