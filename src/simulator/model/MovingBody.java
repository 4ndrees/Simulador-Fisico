package simulator.model;

import simulator.misc.Vector2D;

public class MovingBody extends Body{

	public MovingBody(String id, String gid, Vector2D p, Vector2D v, double m) throws IllegalArgumentException{
		super(id, gid, p, v, m);
	}

	@Override
	public void advance(double dt) {
		
		Vector2D a = new Vector2D();
		Vector2D aux1 = new Vector2D();
		Vector2D aux2 = new Vector2D();
		
		if (this.m != 0)
			a = this.f.scale(1/this.m);
		
		aux1 = this.v.scale(dt);
		aux2 = (a.scale(Math.pow(dt, 2)/2));
		this.p = this.p.plus(aux1.plus(aux2));
		
		this.v = this.v.plus(a.scale(dt));
	}
}
