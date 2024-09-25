package simulator.factories;

import org.json.JSONObject;

import simulator.model.ForceLaws;
import simulator.model.NoForce;

public class NoForceBuilder extends Builder<ForceLaws>{

	public NoForceBuilder() throws IllegalArgumentException {
		super("nf", "No force");
		
		this._data = new JSONObject();
	}

	@Override
	protected ForceLaws createInstance(JSONObject data) {
		
		ForceLaws fl = new NoForce();
		
		return fl;
	}
}
