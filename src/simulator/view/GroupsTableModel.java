package simulator.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.BodiesGroup;
import simulator.model.Body;
import simulator.model.NoForce;
import simulator.model.SimulatorObserver;
import simulator.model.BodiesGroup.Conjunto;

class GroupsTableModel extends AbstractTableModel implements SimulatorObserver {
	
	private String[] _header = { "Id", "Force Laws", "Bodies" };
	private List<BodiesGroup> _groups;
	private Controller _ctrl;
	
	GroupsTableModel(Controller ctrl) {
		
		_ctrl = ctrl;
		_groups = new ArrayList<>();
		_ctrl.addObserver(this);
	}
	
	@Override
	public int getRowCount() {
		
		return _groups.size();
	}
	
	@Override
	public int getColumnCount() {
		
		return _header.length;
	}
	
	public String getColumnName(int column) {
		
		return _header[column];
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		switch (columnIndex) {
			
			case 0:
				return _groups.get(rowIndex).getId();
			case 1:
				return _groups.get(rowIndex).getForceLawsInfo();
			case 2:
				
				String bodies = "";
				
				BodiesGroup.Conjunto c = _groups.get(rowIndex).new Conjunto();
					
				for(Body b : c) {
						
					bodies += b.getId() + " ";
				}
				
				return bodies;
		}
		
		return null;
	}
	
	@Override
	public void onAdvance(Map<String, BodiesGroup> groups, double time) {}
	
	@Override
	public void onReset(Map<String, BodiesGroup> groups, double time, double dt) {
		_groups.clear();
	}
	
	@Override
	public void onRegister(Map<String, BodiesGroup> groups, double time, double dt) {
		for(BodiesGroup g : groups.values()) {
			if(!_groups.contains(g))
				_groups.add(g);
		}
	}
	
	@Override
	public void onGroupAdded(Map<String, BodiesGroup> groups, BodiesGroup g) {
		
		if(!_groups.contains(g))
			_groups.add(g);
		
		this.fireTableDataChanged();
	}
	
	@Override
	public void onBodyAdded(Map<String, BodiesGroup> groups, Body b) {
		
		_groups.clear();
		
		for(BodiesGroup bg : groups.values()) {
			
			_groups.add(bg);
		}
	}
	
	@Override
	public void onDeltaTimeChanged(double dt) {}
	
	@Override
	public void onForceLawsChanged(BodiesGroup g) {
		
		_groups.removeIf(e -> e.getId().equals(g.getId()));
		_groups.add(g);
		
		this.fireTableDataChanged();
	}
}