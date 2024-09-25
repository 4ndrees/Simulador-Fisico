package simulator.model;

import java.util.List;

import simulator.misc.*;

public class MovingTowardsFixedPoint implements ForceLaws{

	private Vector2D c;
	private double g;
	
	public MovingTowardsFixedPoint(Vector2D c, double g) throws IllegalArgumentException{
		
		if(c == null || g <= 0) 
			throw new IllegalArgumentException();
		else {
			
			this.c = c;
			this.g = g;
		}
	}
	
	@Override
	public void apply(List<Body> bs) {
		
		for(Body b : bs) {
			
			if(b.p.distanceTo(c) != 0) {
				
				b.f = (c.minus(b.p)).scale(1/(b.p.distanceTo(c)));
			
				b.f = b.f.scale(g*b.m);
			}
		}
	}
	
	public String toString(){return "Moving towards " + this.c + " with constant acceleration" + this.g; }
	
}
