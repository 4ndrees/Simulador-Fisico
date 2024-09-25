package simulator.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.misc.Vector2D;
import simulator.model.BodiesGroup;
import simulator.model.BodiesGroup.Conjunto;
import simulator.model.Body;
import simulator.model.SimulatorObserver;
import simulator.model.StationaryBody;

class BodiesTableModel extends AbstractTableModel implements SimulatorObserver {
	
	private String[] _header = { "Id", "gId", "Mass", "Velocity", "Position", "Force" };
	private List<Body> _bodies;
	private Controller _ctrl;
	
	BodiesTableModel(Controller ctrl) {
		
		_ctrl = ctrl;
		_bodies = new ArrayList<>();
		_ctrl.addObserver(this);
	}

	public String getColumnName(int column) {
		
		return _header[column];
	}
	
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return _bodies.size();
	}
	
	@Override
	public int getColumnCount() {
	
		return _header.length;
	}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		switch (columnIndex) {
		
			case 0:
				return _bodies.get(rowIndex).getId();
			case 1:
				return _bodies.get(rowIndex).getgId();
			case 2:
				return _bodies.get(rowIndex).getMass();
			case 3:
				return _bodies.get(rowIndex).getVelocity();
			case 4:
				return _bodies.get(rowIndex).getPosition();
			case 5:
				return _bodies.get(rowIndex).getForce();
		}
	
		return null;
	}
	
	@Override
	public void onAdvance(Map<String, BodiesGroup> groups, double time) {
		
		_bodies.clear();
		
		for(BodiesGroup g : groups.values()) {
			
			BodiesGroup.Conjunto c = g.new Conjunto();
			
			for(Body b : c) {
				
				_bodies.add(b);
			}
		}
		
		this.fireTableDataChanged();
	}

	@Override
	public void onReset(Map<String, BodiesGroup> groups, double time, double dt) {
		_bodies.clear();
	}

	@Override
	public void onRegister(Map<String, BodiesGroup> groups, double time, double dt) {
		
		for(BodiesGroup g : groups.values()) {
		
			BodiesGroup.Conjunto c = g.new Conjunto();
			
			for(Body b : c) {
				
				_bodies.add(b);
			}
		}
	}

	@Override
	public void onGroupAdded(Map<String, BodiesGroup> groups, BodiesGroup g) {}

	@Override
	public void onBodyAdded(Map<String, BodiesGroup> groups, Body b) {
		if(!_bodies.contains(b))
			_bodies.add(b);
		
		this.fireTableDataChanged();
	}

	@Override
	public void onDeltaTimeChanged(double dt) {}
	
	@Override
	public void onForceLawsChanged(BodiesGroup g) {}
}