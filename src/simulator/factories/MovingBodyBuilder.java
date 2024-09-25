package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.Body;
import simulator.model.MovingBody;

public class MovingBodyBuilder extends Builder<Body>{

	public MovingBodyBuilder() throws IllegalArgumentException {
		super("mv_body", "j");
	}

	@Override
	protected Body createInstance(JSONObject data) throws IllegalArgumentException{
		
		Body b = null;
		
		if(!data.has("id") || !data.has("gid") || !data.has("p") || !data.has("v") || !data.has("m")) {	
			throw new IllegalArgumentException();
		}
		else {
			
			JSONArray j1 = data.getJSONArray("p");
			JSONArray j2 = data.getJSONArray("v");
			
			if(j1.length() != 2 || j2.length() != 2) {
				throw new IllegalArgumentException();
			}
			else {
				
				String id = data.getString("id");
				String gid = data.getString("gid");
				Vector2D p = new Vector2D(j1.getDouble(0), j1.getDouble(1));
				Vector2D v = new Vector2D(j2.getDouble(0), j2.getDouble(1));
				double m = data.getDouble("m");
			
				b = new MovingBody(id, gid, p, v, m);
			}
		}
		
		return b;
	}
}
