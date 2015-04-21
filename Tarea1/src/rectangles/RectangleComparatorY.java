package rectangles;

import java.util.Comparator;

public class RectangleComparatorY implements Comparator<IRectangle> {
	int i;
	
	public RectangleComparatorY(int i){
		this.i=i;
	}
	
	public void setI(int i){
		this.i=i;
	}
	@Override
	public int compare(IRectangle r1, IRectangle r2) {
		double d1 = r1.getY()[i];
		double d2 = r2.getY()[i];
		if(d1>d2) return 1;
		if(d1<d2) return -1;
		
		return 1;
	}
}
