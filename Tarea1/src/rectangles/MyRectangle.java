package rectangles;

public class MyRectangle extends AbstractRectangle{
	
	public MyRectangle(double[] x, double[]y){
		this.x_coords=x;
		this.y_coords=y;
	}
	
	public String toString(){
		String s =  (x_coords[0]+","+ x_coords[1]+","+y_coords[0]+","+y_coords[1]);
		return s;
	}

}
