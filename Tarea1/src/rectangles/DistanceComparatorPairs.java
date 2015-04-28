package rectangles;

import java.util.Comparator;

import point.Point;

import utils.Pair;

/**
 * Ordena de mayor a menor
 * @author Agustin
 *
 */
public class DistanceComparatorPairs implements Comparator<Pair>{
	
	private Point center;
	
	public DistanceComparatorPairs(Point p){
		center=p;
	}

	@Override
	public int compare(Pair r0, Pair r1) {
		Point c0 = r0.r.getCenter();
		Point c1 = r1.r.getCenter();
		double d0 = center.distance(c0);
		double d1 = center.distance(c1);
		if(d0<d1){
			return 1;
		}
		else if(d0>d1){
			return -1;
		}
		return 0;
		
	}


}
