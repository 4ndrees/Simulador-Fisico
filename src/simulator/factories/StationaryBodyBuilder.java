package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.Body;
import simulator.model.StationaryBody;

public class StationaryBodyBuilder extends Builder<Body>{

	public StationaryBodyBuilder() throws IllegalArgumentException {
		super("st_body", "j");
	}

	@Override
	protected Body createInstance(JSONObject data) {
		
		Body b = null;
		
		if(!data.has("id") || !data.has("gid") || !data.has("p") || !data.has("m")) {	
			throw new IllegalArgumentException();
		}
		else {
			
			JSONArray j1 = data.getJSONArray("p");
			
			if(j1.length() != 2) {
				throw new IllegalArgumentException();
			}
			else {
				
				String id = data.getString("id");
				String gid = data.getString("gid");
				Vector2D p = new Vector2D(j1.getDouble(0), j1.getDouble(1));
				double m = data.getDouble("m");
			
				b = new StationaryBody(id, gid, p, m);
			}
		}
		
		return b;
	}
}
