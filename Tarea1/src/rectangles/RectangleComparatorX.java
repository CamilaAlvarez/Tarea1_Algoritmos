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
		return new Double(r1.getX()[i]).compareTo(new Double(r2.getX()[i]));
	}
}
