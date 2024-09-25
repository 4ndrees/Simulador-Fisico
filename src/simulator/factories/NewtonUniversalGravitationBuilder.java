package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.ForceLaws;
import simulator.model.NewtonUniversalGravitation;

public class NewtonUniversalGravitationBuilder extends Builder<ForceLaws>{

	public NewtonUniversalGravitationBuilder() throws IllegalArgumentException {

		super("nlug", "Newton's law of universal gravitation");	
		
		this._data = new JSONObject();
		
		this._data.put("G", "the gravitational constant (a number)");
	}

	@Override
	protected ForceLaws createInstance(JSONObject data) throws IllegalArgumentException{
		
		ForceLaws fl = null;
		
		if(!data.has("G"))
			fl = new NewtonUniversalGravitation(6.67E-11);
		else
			fl = new NewtonUniversalGravitation(data.getDouble("G"));
		
		return fl;
	}
}
