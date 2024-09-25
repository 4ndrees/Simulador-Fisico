package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class BodiesGroup {

	private String id;
	private ForceLaws fl;
	private List<Body> bs;
	private List<Body> _bodiesRO;
	
	public BodiesGroup(String id, ForceLaws fl) throws IllegalArgumentException{
		
		if(id == null || fl == null)
			throw new IllegalArgumentException();
		else if (id.trim().length() <= 0)
			throw new IllegalArgumentException();
		else {
		
			this.id = id;
			this.fl = fl;
			this.bs = new ArrayList<Body>();
			this._bodiesRO = Collections.unmodifiableList(bs);

		}
	}
	
	public String getId() {return this.id;}
	
	protected void setForceLaws(ForceLaws fl) throws IllegalArgumentException{
	
		if(fl != null)
			this.fl = fl;
		else
			throw new IllegalArgumentException();
	}
	
	protected void addBody(Body b) throws IllegalArgumentException{
		
		if (b == null)
			throw new IllegalArgumentException();
		else {
			boolean eq = false;
		
			for (Body b1 : bs) {
			
				if(b1.id.equals(b.id))
					eq = true;
			}
		
			if(eq)
				throw new IllegalArgumentException();
			else 
				bs.add(b);
		}	
	}
	
	protected void advance(double dt) throws IllegalArgumentException{
		
		if(dt <= 0)
			throw new IllegalArgumentException();
		else {
			
			for(Body b : bs)
				b.resetForce();

			fl.apply(bs);
		
			for (Body b : bs)
				b.advance(dt);
		}
	}
	
	public JSONObject getState() {
		
		JSONArray bodies = new JSONArray();
		
		for (Body b : bs) {
			
			bodies.put(b.getState());
		}
		
		JSONObject jo = new JSONObject();
		
		jo.put("id", this.id);
		jo.put("bodies", bodies);
		
		return jo;
	}
	
	public String toString() {return getState().toString();}
	
	public String getForceLawsInfo() {return fl.toString();}
	
	public class Conjunto implements Iterable<Body>{

		@Override
		public Iterator<Body> iterator() {
			
			return _bodiesRO.iterator();
		}
	}
}
