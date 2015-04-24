package rectangles;

import java.util.Comparator;

import Point.Point;

public class DistanceComparator implements Comparator<IRectangle>{
	
	private Point center;
	
	public DistanceComparator(Point p){
		center=p;
	}

	@Override
	public int compare(IRectangle r0, IRectangle r1) {
		Point c0 = r0.getCenter();
		Point c1 = r1.getCenter();
		double d0 = center.distance(c0);
		double d1 = center.distance(c1);
		if(d0>d1){
			return 1;
		}
		else if(d0<d1){
			return -1;
		}
		return 0;
		
	}


}
