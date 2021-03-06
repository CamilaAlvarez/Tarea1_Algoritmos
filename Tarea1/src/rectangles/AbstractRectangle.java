package rectangles;

import java.nio.ByteBuffer;

import point.Point;


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
		if(!this.intersects(r)){
			return 0;
		}
		double[] r_x = r.getX();
		double[] r_y = r.getY();
		double[] aux_x = this.x_coords;
		double[] aux_y = this.y_coords;
		double[] auxX = new double[2];
		auxX[0]=aux_x[0];
		auxX[1]=aux_x[1];
		double[] auxY = new double[2];
		auxY[0]=aux_y[0];
		auxY[1]=aux_y[1];
		
		if(r_x[0]>aux_x[0])
			auxX[0]=r_x[0];
		if(r_x[1]<aux_x[1])
			auxX[1]=r_x[1];
		if(r_y[0]>aux_y[0])
			auxY[0]=r_y[0];
		if(r_y[1]<aux_y[1])
			auxY[1]=r_y[1];
		
		return (Math.max(0D, (auxX[1]-auxX[0])*(auxY[1]-auxY[0])));
		
	}
	
	@Override
	public int writeBuffer(byte[] data, int pos) {
		double x1 = x_coords[0];
		double x2 = x_coords[1];
		double y1 = y_coords[0];
		double y2 = y_coords[1];
		
		ByteBuffer.wrap(data, pos, 8).putDouble(x1);
		pos += 8;
		ByteBuffer.wrap(data, pos, 8).putDouble(x2);
		pos += 8;
		ByteBuffer.wrap(data, pos, 8).putDouble(y1);
		pos += 8;
		ByteBuffer.wrap(data, pos, 8).putDouble(y2);
		pos += 8;
		
		return pos;
		
	}
	
	@Override
	public Point getCenter(){
		double x = (this.x_coords[0]+this.x_coords[1])/2;
		double y = (this.y_coords[0]+this.y_coords[1])/2;
		return new Point(x,y);
	}
	
	@Override
	public boolean intersects(IRectangle r){
		double x0 = this.x_coords[0];
		double x1 = this.x_coords[1];
		double y0 = this.y_coords[0];
		double y1 = this.y_coords[1];
		
		double x0p = r.getX()[0];
		double x1p = r.getX()[1];
		double y0p = r.getY()[0];
		double y1p = r.getY()[1];
		
		if(x0p>=x1 || x1p<=x0 || y0p>=y1 || y1p<=y0){
			return false;	
		}
		return true;
	}
}
