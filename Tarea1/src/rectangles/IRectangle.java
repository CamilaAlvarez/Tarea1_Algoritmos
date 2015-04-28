package rectangles;

import point.Point;

public interface IRectangle {

	double getArea();

	double[] getX();

	double[] getY();

	boolean equals(Object r);

	double intersectionArea(IRectangle iRectangle);
	
	int writeBuffer(byte[] data, int pos);
	
	Point getCenter();
	
	boolean intersects(IRectangle r);


}
