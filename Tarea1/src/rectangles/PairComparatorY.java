package rectangles;

import java.util.Comparator;

import utils.Pair;

public class PairComparatorY implements Comparator<Pair> {
	int i;
	
	public PairComparatorY(int i){
		this.i=i;
	}
	
	public void setI(int i){
		this.i=i;
	}

	@Override
	public int compare(Pair p1, Pair p2) {
		IRectangle r1 = p1.r;
		IRectangle r2 = p2.r;
		
		double d1 = r1.getY()[i];
		double d2 = r2.getY()[i];
		if(d1>d2) return 1;
		if(d1<d2) return -1;
		
		return 1;
	}
}
