package rectangles;

public interface IRectangle {

	double getArea();

	double[] getX();

	double[] getY();

	boolean equals(Object r);

	double intersectionArea(IRectangle iRectangle);


}
