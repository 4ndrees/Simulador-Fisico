package simulator.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.factories.Builder;
import simulator.factories.BuilderBasedFactory;
import simulator.factories.Factory;
import simulator.factories.MovingBodyBuilder;
import simulator.factories.MovingTowardsFixedPointBuilder;
import simulator.factories.NewtonUniversalGravitationBuilder;
import simulator.factories.NoForceBuilder;
import simulator.factories.StationaryBodyBuilder;
import simulator.model.Body;
import simulator.model.ForceLaws;
import simulator.model.PhysicsSimulator;
import simulator.view.MainWindow;


public class Main {

	// default values for some parameters
	//
	private final static Integer _stepsDefaultValue = 150;
	private final static Double _dtimeDefaultValue = 2500.0;
	private final static String _forceLawsDefaultValue = "nlug";

	// some attributes to stores values corresponding to command-line parameters
	//
	private static Integer _steps = null;
	private static Double _dtime = null;
	private static String _inFile = null;
	private static String _outFile = null;
	private static JSONObject _forceLawsInfo = null;
	private static boolean GUIMode = true;

	// factories
	private static Factory<Body> _bodyFactory;
	private static Factory<ForceLaws> _forceLawsFactory;

	private static void initFactories() {

		Builder<ForceLaws> f1 = new NoForceBuilder();
		Builder<ForceLaws> f2 = new NewtonUniversalGravitationBuilder();
		Builder<ForceLaws> f3 = new MovingTowardsFixedPointBuilder();
		
		List<Builder<ForceLaws>> l1 = new ArrayList<>();
		l1.add(f1);
		l1.add(f2);
		l1.add(f3);		
		
		_forceLawsFactory = new BuilderBasedFactory<ForceLaws>(l1);
		
		Builder<Body> b1 = new MovingBodyBuilder();
		Builder<Body> b2 = new StationaryBodyBuilder();
		List<Builder<Body>> l2 = new ArrayList<>();
		l2.add(b1);
		l2.add(b2);
		
		_bodyFactory = new BuilderBasedFactory<Body>(l2);
	}

	private static void parseArgs(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = buildOptions();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parseHelpOption(line, cmdLineOptions);
			parseInFileOption(line);
			parseDeltaTimeOption(line);
			parseForceLawsOption(line);
			parseStepsOption(line);
			parseOutFileOption(line);
			parseModeOption(line);

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options buildOptions() {
		Options cmdLineOptions = new Options();

		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("Bodies JSON input file.").build());

		// delta-time
		cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg()
				.desc("A double representing actual time, in seconds, per simulation step. Default value: "
						+ _dtimeDefaultValue + ".")
				.build());

		// force laws
		cmdLineOptions.addOption(Option.builder("fl").longOpt("force-laws").hasArg()
				.desc("Force laws to be used in the simulator. Possible values: "
						+ factoryPossibleValues(_forceLawsFactory) + ". Default value: '" + _forceLawsDefaultValue
						+ "'.")
				.build());

		// output
		cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg()
				.desc("Output file, where output is written. Default\r\n"
						+ "value: the standard output.")
				.build());
		
		//steps
		cmdLineOptions.addOption(Option.builder("s").longOpt("steps").hasArg()
				.desc("An integer representing the number of simulation\r\n"
						+ "steps. Default value: " + _stepsDefaultValue)
				.build());
		
		cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg()
				.desc("Execution Mode. Possible values: 'batch' (Batch\r\n"
				+ "mode), 'gui' (Graphical User Interface mode).\r\n"
				+ "Default value: 'gui'.\r\n"
				+ "").build());
		
		return cmdLineOptions;
	}

	public static String factoryPossibleValues(Factory<?> factory) {
		String s = "";
		if (factory != null) {

			for (JSONObject fe : factory.getInfo()) {
				if (s.length() > 0) {
					s = s + ", ";
				}
				s = s + "'" + fe.getString("type") + "' (" + fe.getString("desc") + ")";
			}

			s = s + ". You can provide the 'data' json attaching :{...} to the tag, but without spaces.";
		} else {
			s = "No values found";
		}
		return s;
	}

	private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parseModeOption(CommandLine line) {
		
		if(line.hasOption("m")) {
			
			String s = line.getOptionValue("m");
		
			if(s.equals("batch"))
				GUIMode = false;
			else
				GUIMode = true;
		}
		else
			GUIMode = true;
	}
	
	private static void parseInFileOption(CommandLine line) throws ParseException {
		_inFile = line.getOptionValue("i");
		if (_inFile == null && !GUIMode) {
			throw new ParseException("In batch mode an input file of bodies is required");
		}
	}
	
	private static void parseOutFileOption(CommandLine line) throws ParseException{
		_outFile = line.getOptionValue("o");
		if (_outFile == null) {
			_outFile = "System.out";
		}
	}

	private static void parseDeltaTimeOption(CommandLine line) throws ParseException {
		String dt = line.getOptionValue("dt", _dtimeDefaultValue.toString());
		try {
			_dtime = Double.parseDouble(dt);
			assert (_dtime > 0);
		} catch (Exception e) {
			throw new ParseException("Invalid delta-time value: " + dt);
		}
	}
	
	private static void parseStepsOption(CommandLine line) throws ParseException {
		String s = line.getOptionValue("s", _stepsDefaultValue.toString());
		try {
			_steps = Integer.parseInt(s);
			assert (_steps > 0);
		} catch (Exception e) {
			throw new ParseException("Invalid steps value: " + s);
		}
	}

	private static JSONObject parseWRTFactory(String v, Factory<?> factory) {

		// the value of v is either a tag for the type, or a tag:data where data is a
		// JSON structure corresponding to the data of that type. We split this
		// information
		// into variables 'type' and 'data'
		//
		int i = v.indexOf(":");
		String type = null;
		String data = null;
		if (i != -1) {
			type = v.substring(0, i);
			data = v.substring(i + 1);
		} else {
			type = v;
			data = "{}";
		}

		// look if the type is supported by the factory
		boolean found = false;
		if (factory != null) {
			for (JSONObject fe : factory.getInfo()) {
				if (type.equals(fe.getString("type"))) {
					found = true;
					break;
				}
			}
		}

		// build a corresponding JSON for that data, if found
		JSONObject jo = null;
		if (found) {
			jo = new JSONObject();
			jo.put("type", type);
			jo.put("data", new JSONObject(data));
		}
		return jo;
	}

	private static void parseForceLawsOption(CommandLine line) throws ParseException {
		String fl = line.getOptionValue("fl", _forceLawsDefaultValue);
		_forceLawsInfo = parseWRTFactory(fl, _forceLawsFactory);
		if (_forceLawsInfo == null) {
			throw new ParseException("Invalid force laws: " + fl);
		}
	}

	private static void startBatchMode() throws Exception {
		
		ForceLaws fl = _forceLawsFactory.createInstance(_forceLawsInfo);
		PhysicsSimulator ps = new PhysicsSimulator(fl, _dtime);
		
		File initialFile = new File(_inFile);
		InputStream in = new FileInputStream(initialFile);
		
		OutputStream out = null;
		
		if(!_outFile.equals("System.out")) {
			File outFile = new File(_outFile);
			out = new FileOutputStream(outFile);
		}
		else {
			out = System.out;
		}
			
		Controller c = new Controller(ps, _bodyFactory, _forceLawsFactory);
		
		c.loadData(in);
		in.close();
		
		
		c.run(_steps, out);
		out.close();
	}

	private static void startGUIMode() throws Exception{
		
		ForceLaws fl = _forceLawsFactory.createInstance(_forceLawsInfo);
		PhysicsSimulator ps = new PhysicsSimulator(fl, _dtime);
		
		File initialFile=null;
		InputStream in=null;
		if(!GUIMode) {
		 initialFile = new File(_inFile);
		 in = new FileInputStream(initialFile);
		}
		Controller c = new Controller(ps, _bodyFactory, _forceLawsFactory);
		
		if(!GUIMode) {
		c.loadData(in);
		in.close();
		}
		
		SwingUtilities.invokeAndWait(() -> new MainWindow(c));
	}
	
	private static void start(String[] args) throws Exception {
		parseArgs(args);
		
		if(GUIMode)
			startGUIMode();
		else
			startBatchMode();
	}

	public static void main(String[] args) {
		try {
			initFactories();
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}
}