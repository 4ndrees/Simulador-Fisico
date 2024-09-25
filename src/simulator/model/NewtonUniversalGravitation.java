package simulator.model;

import java.util.List;

import simulator.misc.Vector2D;

public class NewtonUniversalGravitation implements ForceLaws{

	private double G;
	
	//Contructor
	public NewtonUniversalGravitation(double G) throws IllegalArgumentException{
		
		if(G <= 0)
			throw new IllegalArgumentException();
		else {
			this.G = G;
		}
	}
	
	@Override
	public void apply(List<Body> bodies) {
		
		double fuerza;
		
		for(Body b1: bodies) {
			if(b1.m != 0) {
				for(Body b2: bodies) {
					if((b1.p.minus(b2.p)).getX() != 0 && (b1.p.minus(b2.p)).getY() != 0) {
					
						Vector2D d = new Vector2D(b2.getPosition().minus(b1.getPosition()));
						
						d = d.direction();
						
						double r = b2.getPosition().distanceTo(b1.getPosition());
						
						if (r != 0) {
						
							fuerza = G * (b1.m * b2.m)/(r*r);
						
							b1.addForce(d.scale(fuerza));
						}
					}
				}
			}
		}
	}
	
	public String toString() {return "Newton's Universal Gravitation with G = " + this.G;}
}
