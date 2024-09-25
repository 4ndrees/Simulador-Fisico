package simulator.model;

import org.json.*;

import simulator.misc.*;

public abstract class Body {

	protected String id;
	protected String gid;
	protected Vector2D v;
	protected  Vector2D f;
	protected Vector2D p;
	protected double m;
	
	//Constructor
	public Body(String id, String gid, Vector2D p, Vector2D v, double m) throws IllegalArgumentException{
		
		if(p == null || v == null || id == null || gid == null) {
			
			throw new IllegalArgumentException();
		}
		else if(id.trim().length() <= 0 || gid.trim().length() <= 0) {
			
			throw new IllegalArgumentException();
		}
		else if(m <= 0) {
			
			throw new IllegalArgumentException();
		}
		else {
			
			this.id = id;
			this.gid = gid;
			this.v = v;
			this.p = p;
			this.m = m;
			this.f = new Vector2D();
		}
	}
	
	//Devuelve el identificador del cuerpo
	public String getId() {return this.id;}
	
	//devuelve el identificador del grupo al que pertenece el cuerpo
	public String getgId() {return this.gid;}
	
	// devuelve el vector de velocidad
	public Vector2D getVelocity() {return this.v;}
	
	//devuelve el vector de fuerza
	public Vector2D getForce() {return this.f;}
	
	//devuelve el vector de posición
	public Vector2D getPosition() {return this.p;}
	
	//devuelve la masa del cuerpo
	public double getMass() {return this.m;}
	
	//añade la fuerza f al vector de fuerza del cuerpo 
	public void addForce(Vector2D f) {this.f = this.f.plus(f);}
	
	//pone el valor del vector de fuerza a (0, 0)
	public void resetForce() {this.f = new Vector2D();}
	
	//mueve el cuerpo durante dt segundos
	public abstract void advance(double dt);
	
	//devuelve la siguiente información del cuerpo en formato JSON
	public JSONObject getState() {
		
		JSONArray p = new JSONArray();
			p.put(this.p.getX());
			p.put(this.p.getY());
	
		JSONArray v = new JSONArray();
			v.put(this.v.getX());
			v.put(this.v.getY());
	
		JSONArray f = new JSONArray();
			f.put(this.f.getX());
			f.put(this.f.getY());
		
		JSONObject jo = new JSONObject();
		
		jo.put("p", p);
		jo.put("v", v);
		jo.put("f", f);
		jo.put("id", this.id);
		jo.put("m", this.m);
		
		return jo;
	}
	
	//devuelve getState().toString()
	public String toString() {return getState().toString();};
	
}
