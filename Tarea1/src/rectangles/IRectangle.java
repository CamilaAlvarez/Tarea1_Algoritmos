package rectangles;

import Point.Point;

public interface IRectangle {

	double getArea();

	double[] getX();

	double[] getY();

	boolean equals(Object r);

	double intersectionArea(IRectangle iRectangle);
	
	int writeBuffer(byte[] data, int pos);
	
	Point getCenter();


}
