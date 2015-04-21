package rectangles;

import java.util.Comparator;

public class RectangleComparatorX implements Comparator<IRectangle> {
	int i;
	
	public RectangleComparatorX(int i){
		this.i=i;
	}
	
	public void setI(int i){
		this.i=i;
	}

	@Override
	public int compare(IRectangle r1, IRectangle r2) {
		double d1 = r1.getX()[i];
		double d2 = r2.getX()[i];
		if(d1>d2) return 1;
		if(d1<d2) return -1;
		
		return 1;
	}
}
