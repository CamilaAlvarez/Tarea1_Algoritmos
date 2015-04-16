package rectangles;

public abstract class AbstractRectangle implements IRectangle{
	protected double[] x_coords;
	protected double[] y_coords;
	
	@Override
	public double getArea() {
		double x = x_coords[1]-x_coords[0];
		double y = y_coords[1]-y_coords[0];
		
		return x*y;
	}
	
	public double[] getX(){
		return x_coords;
	}
	
	public double[] getY(){
		return y_coords;
	}

	@Override
	public boolean equals(Object r){
		if (!(r instanceof IRectangle))
            return false;
		
		IRectangle rect = (IRectangle)r;
		double[] r_x = rect.getX();
		double[] r_y = rect.getY();
		double[] aux_x = this.x_coords;
		double[] aux_y = this.y_coords;
		
		return r_x[0]==aux_x[0] && r_x[1]==aux_x[1]
				&& r_y[0]==aux_y[0] && r_y[1]==aux_y[1];
	}
	
	/**
	 * Retorna area de interseccion entre el objeto y el
	 * rectangulo r
	 */
	@Override
	public double intersectionArea(IRectangle r){
		double[] r_x = r.getX();
		double[] r_y = r.getY();
		double[] aux_x = this.x_coords;
		double[] aux_y = this.y_coords;
		
		if(r_x[0]>aux_x[0])
			aux_x[0]=r_x[0];
		if(r_x[1]<aux_x[1])
			aux_x[1]=r_x[1];
		if(r_y[0]>aux_y[0])
			aux_y[0]=r_y[0];
		if(r_y[1]<aux_y[1])
			aux_y[1]=r_y[1];
		
		return (aux_x[1]-aux_x[0])*(aux_y[1]-aux_y[0]);
		
	}
}
