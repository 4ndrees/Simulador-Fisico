package simulator.control;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.factories.Factory;
import simulator.model.Body;
import simulator.model.ForceLaws;
import simulator.model.PhysicsSimulator;
import simulator.model.SimulatorObserver;

public class Controller {

	private PhysicsSimulator ps;
	private Factory<Body> F_b;
	private Factory<ForceLaws> F_fl;
	
	public Controller(PhysicsSimulator ps, Factory<Body> F_b, Factory<ForceLaws> F_fl) throws IllegalArgumentException{
		
		if(ps == null || F_b == null || F_fl == null)
			throw new IllegalArgumentException();
		else {
			
			this.ps = ps;
			this.F_b = F_b;
			this.F_fl = F_fl;
		}
	}
	
	public void loadData(InputStream in) {
		
		JSONObject jsonInput = new JSONObject(new JSONTokener(in));
		
		//grupos
		
		JSONArray jsonGroups = jsonInput.getJSONArray("groups");
		
		for(int i = 0; i < jsonGroups.length(); i++) {
			
			ps.addGroup(jsonGroups.getString(i));
		}
		
		//leyes asociadas a grupos
		if (jsonInput.has("laws")) {
			
			JSONArray jsonLaws = jsonInput.getJSONArray("laws");
			
			for(int j = 0; j < jsonLaws.length(); j++) {
			
				JSONObject jo = jsonLaws.getJSONObject(j);
			
				if (jo.get("laws") != null) {
				
					JSONObject info = jo.getJSONObject("laws");
				
					ForceLaws fl = null;
					fl = F_fl.createInstance(info);
				
					ps.setForceLaws(jo.getString("id"), fl);
				}
				else {
				
					ForceLaws fl = null;
					fl = F_fl.createInstance(new JSONObject("{ \"type\": \"nlug\"}")); 
				
					ps.setForceLaws(jo.getString("id"), fl);
				}
			}
		}
		
		//cuerpos dentro de grupos
		
		JSONArray jsonBodies = jsonInput.getJSONArray("bodies");
		
		for(int k = 0; k < jsonBodies.length(); k++) {
			
			JSONObject info = jsonBodies.getJSONObject(k);
			
			Body b = null;
			b = F_b.createInstance(info);
			
			ps.addBody(b);
		}
	}
	
	public void run(int n, OutputStream out) {
			
		PrintStream p = new PrintStream(out);
			
		p.println("{");
		p.println("\"states\": [");
			
		p.println(ps.getState().toString());
		p.print(",");
		
		for (int i = 0; i < n; i++) {
			
			ps.advance();
			p.println(ps.getState().toString());
			
			if(i < n - 1) {
				p.print(",");
			}
		}
				
		p.println("]");
		p.println("}");
	}
	
	public void reset() {ps.reset();}
	
	public void setDeltaTime(double dt) {ps.setDeltaTime(dt);}
	
	public void addObserver(SimulatorObserver o) {ps.addObserver(o);}
	
	public void removeObserver(SimulatorObserver o) {ps.removeObserver(o);}
	
	public List<JSONObject> getForceLawsInfo(){
		
		return F_fl.getInfo();
	}
	
	public void setForceLaws(String gId, JSONObject info) {
				
		ForceLaws fl = F_fl.createInstance(info);
		
		ps.setForceLaws(gId, fl);
	}

	public void run(int n) {
		
		for (int i = 0; i < n; i++) {
			ps.advance();
		}
	}
}
