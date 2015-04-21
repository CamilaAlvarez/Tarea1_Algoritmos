package rectangles;

import java.nio.ByteBuffer;

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
		
//		Rectangle2D r1 = new Rectangle2D.Double(this.x_coords[0],this.y_coords[1],
//				Math.abs(this.x_coords[1]-this.x_coords[0]), 
//				Math.abs(this.y_coords[1]-this.y_coords[0]));
//		
//		Rectangle2D r2 = new Rectangle2D.Double(r.getX()[0],r.getY()[1],
//				Math.abs(r.getX()[1]-r.getX()[0]), 
//				Math.abs(r.getY()[1]-r.getY()[0]));
//		
//		Rectangle2D inter = r1.createIntersection(r2);
//
//		double w = inter.getWidth();
//		double h = inter.getHeight();
//		
//		return Math.abs(w*h);
		
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
		
		return (Math.max(0D, aux_x[1]-aux_x[0])*(aux_y[1]-aux_y[0]));
		
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
}
