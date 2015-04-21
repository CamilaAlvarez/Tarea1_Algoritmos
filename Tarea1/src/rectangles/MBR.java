package rectangles;


public class MBR extends AbstractRectangle{

	public MBR(double[] x, double[]y){
		this.x_coords=x;
		this.y_coords=y;
	}
	
	/**
	 * Calcula cambio de area del MBR al agregar r
	 * @param r Rectangulo a agregar
	 * @return cambio de area
	 */
	public double getAreaChange(MyRectangle r) {
		double area = this.getArea();
		double[] r_x = r.getX();
		double[] r_y = r.getY();
		double[] aux_x = this.x_coords;
		double[] aux_y = this.y_coords;
		
		if(r_x[0]<aux_x[0])
			aux_x[0] = r_x[0];
		if(r_x[1]>aux_x[1])
			aux_x[1] = r_x[1];
		if(r_y[0]<aux_y[0])
			aux_y[0] = r_y[0];
		if(r_y[1]>aux_y[1])
			aux_y[1] = r_y[1];
		
		double new_area = (aux_x[1]-aux_x[0])*(aux_y[1]-aux_y[0]);
		return new_area-area;
	}
	
	/**
	 * Chequea si el MBR contiene a r
	 * @param r Rectagulo a chequear
	 * @return true si el MBR lo contiene
	 */
	public boolean contains(MyRectangle r) {
		double[] r_x = r.getX();
		double[] r_y = r.getY();
		double[] aux_x = this.x_coords;
		double[] aux_y = this.y_coords;
		
		return r_x[0]>=aux_x[0] && r_x[1]<=aux_x[1]
				&& r_y[0]>=aux_y[0] && r_y[1]<=aux_y[1];
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MBR){
			MBR mbr = (MBR)obj;
			boolean r = true;
			r = r && (this.x_coords[0]==mbr.x_coords[0]);
			r = r && (this.x_coords[1]==mbr.x_coords[1]);
			r = r && (this.y_coords[0]==mbr.y_coords[0]);
			r = r && (this.y_coords[1]==mbr.y_coords[1]);
			return r;
		}
		return false;
	}


}
