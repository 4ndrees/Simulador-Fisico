package simulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import simulator.control.Controller;
import simulator.model.BodiesGroup;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

class StatusBar extends JPanel implements SimulatorObserver {
	
	// TODO Añadir los atributos necesarios, si hace falta …
	private JLabel _time;
	private JLabel _timeNumber;
	private double _t = 0;
	private JLabel _groups;
	private JLabel _groupsNumber;
	private int _g = 0;
	
	StatusBar(Controller ctrl) {
		initGUI();
		ctrl.addObserver(this);
	}
	
	private void initGUI() {
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(BorderFactory.createBevelBorder(1));
	
		// TODO Crear una etiqueta de tiempo y añadirla al panel
		_time = new JLabel();
		_time.setText("Time: ");
		this.add(_time);
		
		_timeNumber = new JLabel();
		_timeNumber.setText(String.valueOf(_t));
		this.add(_timeNumber);
		
		// TODO Utilizar el siguiente código para añadir un separador vertical
		JSeparator s1 = new JSeparator(JSeparator.VERTICAL);
		s1.setPreferredSize(new Dimension(10, 20));
		this.add(s1);
		
		// TODO Crear la etiqueta de número de grupos y añadirla al panel
		_groups = new JLabel();
		_groups.setText("Groups: ");
		this.add(_groups);
		
		_groupsNumber = new JLabel();
		_groupsNumber.setText(String.valueOf(_g));
		this.add(_groupsNumber);
		
		// TODO Utilizar el siguiente código para añadir un separador vertical
		JSeparator s2 = new JSeparator(JSeparator.VERTICAL);
		s2.setPreferredSize(new Dimension(10, 20));
		this.add(s2);
	}

	@Override
	public void onAdvance(Map<String, BodiesGroup> groups, double time) {
		
		_t = time;
		_timeNumber.setText(String.valueOf(_t));
	}

	@Override
	public void onReset(Map<String, BodiesGroup> groups, double time, double dt) {
		
		_t = 0;
		_timeNumber.setText(String.valueOf(_t));
		_g = 0;
		_groupsNumber.setText(String.valueOf(_g));
	}

	@Override
	public void onRegister(Map<String, BodiesGroup> groups, double time, double dt) {
		
		_t = time;
		_timeNumber.setText(String.valueOf(_t));
		
		if(groups.isEmpty())
			_groupsNumber.setText(String.valueOf(0));
		else {
			_g = groups.size();
			_groupsNumber.setText(String.valueOf(_g));
		}
	}

	@Override
	public void onGroupAdded(Map<String, BodiesGroup> groups, BodiesGroup g) {
		
		_g = groups.size();
		_groupsNumber.setText(String.valueOf(_g));
	}

	@Override
	public void onBodyAdded(Map<String, BodiesGroup> groups, Body b) {}

	@Override
	public void onDeltaTimeChanged(double dt) {}

	@Override
	public void onForceLawsChanged(BodiesGroup g) {}
}
