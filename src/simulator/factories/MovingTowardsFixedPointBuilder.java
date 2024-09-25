package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.ForceLaws;
import simulator.model.MovingTowardsFixedPoint;

public class MovingTowardsFixedPointBuilder extends Builder<ForceLaws>{

	public MovingTowardsFixedPointBuilder() throws IllegalArgumentException {
		
		super("mtfp", "Moving towards a fixed point");
		
		this._data = new JSONObject();
		
		this._data.put("c", "the point towards which bodies move (e.g., [100.0,50.0])");
		this._data.put("g", "the length of the acceleration vector (a number)");
	}

	@Override
	protected ForceLaws createInstance(JSONObject data) {
	
		ForceLaws fl = null;
		
		if(!data.has("c") && !data.has("g"))
			fl = new MovingTowardsFixedPoint(new Vector2D (0.0, 0.0), 9.81);
		else if (!data.has("c") && data.has("g")) {
			fl = new MovingTowardsFixedPoint(new Vector2D (0.0, 0.0), data.getDouble("g"));
		}
		else if(data.has("c") && !data.has("g")) {

			JSONArray j1 = data.getJSONArray("c");
			
			Vector2D c = new Vector2D(j1.getDouble(0), j1.getDouble(1));
			
			fl = new MovingTowardsFixedPoint(c, 9.81);
		}
		else {
			
			JSONArray j1 = data.getJSONArray("c");
			
			Vector2D c = new Vector2D(j1.getDouble(0), j1.getDouble(1));
			
			fl = new MovingTowardsFixedPoint(c, data.getDouble("g"));
		}
			
		return fl;
	}
}
