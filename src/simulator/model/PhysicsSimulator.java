package simulator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class PhysicsSimulator implements Observable<SimulatorObserver>{

	private double t;
	private double dt;
	private ForceLaws fl;
	private Map<String, BodiesGroup> bsg;
	private List<String> ids;
	private List<SimulatorObserver> so;
	
	public PhysicsSimulator(ForceLaws fl, double dt) throws IllegalArgumentException{
		
		if(fl == null || dt <= 0)
			throw new IllegalArgumentException();
		else {
			
			this.t = 0;
			this.dt = dt;
			this.fl = fl;
			bsg = new HashMap<String, BodiesGroup>();
			ids = new ArrayList<String>();
			so = new ArrayList<SimulatorObserver>();
		}
	}
	
	public void reset() {
		
		bsg.clear();
		ids.clear();
		
		t = 0;
		
		for (SimulatorObserver s : so)
			s.onReset(bsg, t, dt);
	}
	
	public void setDeltaTime(double dt) throws IllegalArgumentException {
		
		if(dt <= 0)
			throw new IllegalArgumentException();
		else {
			this.dt = dt;
		
			for (SimulatorObserver s : so)
				s.onDeltaTimeChanged(dt);
		}
	}
	
	public void advance(){
		
		for(BodiesGroup bg : bsg.values())
			bg.advance(this.dt);
		
		t += dt;
		
		for (SimulatorObserver s : so)
			s.onAdvance(bsg, t);
	}
	
	public void addGroup(String id) throws IllegalArgumentException{
		
		if(id.trim().length() <= 0)
			throw new IllegalArgumentException();
		else { 
			
			BodiesGroup g = new BodiesGroup(id, this.fl);
			
			if (!bsg.containsKey(id)) {
				bsg.put(id, g);
				ids.add(id);
				
				for (SimulatorObserver s : so)
					s.onGroupAdded(bsg, g);
			}
			else
				throw new IllegalArgumentException();
		}
	}
	
	public void addBody(Body b) throws IllegalArgumentException{
		
		if(!bsg.containsKey(b.getgId()))
			throw new IllegalArgumentException();
		else {
			
			bsg.get(b.getgId()).addBody(b);
			
			for (SimulatorObserver s : so)
				s.onBodyAdded(bsg, b);
		}
	}
	
	public void setForceLaws(String id, ForceLaws fl) throws IllegalArgumentException{
		
		if(id == null || fl == null || id.trim().length() <= 0)
			throw new IllegalArgumentException();
		else {
			
			bsg.get(id).setForceLaws(fl);
			
			for (SimulatorObserver s : so)
				s.onForceLawsChanged(bsg.get(id));
		}
	}
	
	//esta mal
	public JSONObject getState() {
		
		JSONArray ja = new JSONArray();
		
		for (String id : ids) {
			
			ja.put(bsg.get(id).getState());
		}
		
		JSONObject jo = new JSONObject();
		
		jo.put("time", this.t);
		jo.put("groups", ja);
		
		return jo;
	}
	
	public String toString() {return getState().toString();}

	@Override
	public void addObserver(SimulatorObserver o) throws IllegalArgumentException{
		
		if(so.contains(o))
			throw new IllegalArgumentException();
		else
			
			so.add(o);
		
		o.onRegister(bsg, t, dt);
	}

	@Override
	public void removeObserver(SimulatorObserver o) throws IllegalArgumentException{
		
		if(!so.contains(o))
			throw new IllegalArgumentException();
		else
			so.remove(o);
	}
}
