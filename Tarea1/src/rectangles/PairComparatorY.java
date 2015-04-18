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
		return new Double(r1.getY()[i]).compareTo(new Double(r2.getY()[i]));
	}
}
