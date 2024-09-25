package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import simulator.control.Controller;
import simulator.model.BodiesGroup;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

class ControlPanel extends JPanel implements SimulatorObserver {

	private Controller _ctrl;
	private JToolBar _toolaBar;
	private JFileChooser _fc;
	private boolean _stopped = true; // utilizado en los botones de run/stop
	private JButton _quitButton;
	private JButton _openButton;
	private JButton _physicsButton;
	private JButton _runButton;
	private JButton _stopButton;
	private JButton _viewerButton;
	private JLabel _steps;
	private JLabel _dt;
	private JSpinner _stepsS;
	private JTextField _dtValue;
	private ForceLawsDialog _flDialog = null;
	
	ControlPanel(Controller ctrl) {

		_ctrl = ctrl;
		initGUI();
		_ctrl.addObserver(this);
	}

	private void initGUI() {

		setLayout(new BorderLayout());
		_toolaBar = new JToolBar();
		add(_toolaBar, BorderLayout.PAGE_START);

		// TODO crear los diferentes botones/atributos y añadirlos a _toolaBar.
		// Todos ellos han de tener su correspondiente tooltip. Puedes utilizar
		// _toolaBar.addSeparator() para añadir la línea de separación vertical
		// entre las componentes que lo necesiten
		
		// Open Button
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("solo recibe archivos json", "json");
		
		_fc = new JFileChooser("resources/examples/input");
		_fc.setFileFilter(filter);
		
		_openButton = new JButton();
		_openButton.setToolTipText("Load an input file into the simulator");
		_openButton.setIcon(new ImageIcon("resources/icons/open.png"));
		_openButton.addActionListener((e) -> {
			
			int result = _fc.showOpenDialog(Utils.getWindow(this));
			
			if(result == _fc.APPROVE_OPTION) {
				
				File selectedFile = _fc.getSelectedFile();
				
				InputStream in;
				
				try {
					in = new FileInputStream(selectedFile);
					_ctrl.reset();
					_ctrl.loadData(in);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
		_toolaBar.add(_openButton);
		
		_toolaBar.addSeparator();
		
		//Physics Button
		_physicsButton = new JButton();
		_physicsButton.setToolTipText("Select force laws for groups");
		_physicsButton.setIcon(new ImageIcon("resources/icons/physics.png"));
		_physicsButton.addActionListener((e) -> {
			
			if(_flDialog == null)
				_flDialog = new ForceLawsDialog(new JFrame(), _ctrl);
			
			_flDialog.open();
		});
		_toolaBar.add(_physicsButton);
		
		//Viewer Button
		_viewerButton = new JButton();
		_viewerButton.setToolTipText("Open a viewer window");
		_viewerButton.setIcon(new ImageIcon("resources/icons/viewer.png"));
		_viewerButton.addActionListener((e) -> new ViewerWindow(new JFrame(), _ctrl));
		_toolaBar.add(_viewerButton);
		
		_toolaBar.addSeparator();
		
		//Run Button
		_runButton = new JButton();
		_runButton.setToolTipText("Run the simulator");
		_runButton.setIcon(new ImageIcon("resources/icons/run.png"));
		_runButton.addActionListener((e) -> {
				
			_quitButton.setEnabled(false);
			_openButton.setEnabled(false);
			_physicsButton.setEnabled(false);
		    _viewerButton.setEnabled(false);
		    _dtValue.setEnabled(false);
			_stepsS.setEnabled(false);
			_runButton.setEnabled(false);
			
			_stopped = false;
				
			_ctrl.setDeltaTime(Double.parseDouble(_dtValue.getText()));
			
			int _currentSteps = Integer.parseInt(_stepsS.getValue().toString());
		
			run_sim(_currentSteps);
		});
		_toolaBar.add(_runButton);
		
		// Stop Button
		_stopButton = new JButton();
		_stopButton.setToolTipText("Stop the simulator");
		_stopButton.setIcon(new ImageIcon("resources/icons/stop.png"));
		_stopButton.addActionListener((e) -> _stopped = true);
		_toolaBar.add(_stopButton);
		
		//Steps
		_steps = new JLabel();
		_steps.setText("steps:");
		_toolaBar.add(_steps);
		
		SpinnerNumberModel m = new SpinnerNumberModel(0, 0, 10000000, 100);
		_stepsS = new JSpinner(m);
		_stepsS.setPreferredSize(new Dimension(60, 40));
		_stepsS.setMaximumSize(new Dimension(60, 40));
		_toolaBar.add(_stepsS);
		
		//Delta Time
		_dt = new JLabel();
		_dt.setText("Delta-Time:");
		_toolaBar.add(_dt);
		
		_dtValue = new JTextField();
		_dtValue.addActionListener((e) -> {
			
			_ctrl.setDeltaTime(Double.parseDouble(_dtValue.getText()));
		});
		_dtValue.setPreferredSize(new Dimension(60, 20));
		_dtValue.setMaximumSize(new Dimension(60, 40));
		_toolaBar.add(_dtValue);
		
		// Quit Button
		_toolaBar.add(Box.createGlue()); // this aligns the button to the right
		_toolaBar.addSeparator();
		
		_quitButton = new JButton();
		_quitButton.setToolTipText("Quit");
		_quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
		_quitButton.addActionListener((e) -> Utils.quit(this));
		_toolaBar.add(_quitButton);
	}
		
	private void run_sim(int n) {
			
		if (n > 0 && !_stopped) {
		
			try {
			
				_ctrl.run(1);
			} catch (Exception e) {
			
				// TODO llamar a Utils.showErrorMsg con el mensaje de error que
				// corresponda
				Utils.showErrorMsg("Run error");
				
				// TODO activar todos los botones
				_quitButton.setEnabled(true);
				_openButton.setEnabled(true);
				_physicsButton.setEnabled(true);
			    _viewerButton.setEnabled(true);
			    _dtValue.setEnabled(true);
				_stepsS.setEnabled(true);
				_runButton.setEnabled(true);

			    
				_stopped = true;
				return;
			}
			
			SwingUtilities.invokeLater(() -> run_sim(n - 1));
			
		}
		else {
			
			// TODO activar todos los botones
			_quitButton.setEnabled(true);
			_openButton.setEnabled(true);
			_physicsButton.setEnabled(true);
		    _viewerButton.setEnabled(true);
			_dtValue.setEnabled(true);
			_stepsS.setEnabled(true);
			_runButton.setEnabled(true);

		    
			_stopped = true;
		}	
		
			
	}
	
	@Override
	public void onAdvance(Map<String, BodiesGroup> groups, double time) {}

	@Override
	public void onReset(Map<String, BodiesGroup> groups, double time, double dt) {}

	@Override
	public void onRegister(Map<String, BodiesGroup> groups, double time, double dt) {
		
			_dtValue.setText(String.valueOf(dt));
			_stepsS.setValue(1000);
	}

	@Override
	public void onGroupAdded(Map<String, BodiesGroup> groups, BodiesGroup g) {}

	@Override
	public void onBodyAdded(Map<String, BodiesGroup> groups, Body b) {}

	@Override
	public void onDeltaTimeChanged(double dt) {
		
		_dtValue.setText(String.valueOf(dt));
	}

	@Override
	public void onForceLawsChanged(BodiesGroup g) {}
}

