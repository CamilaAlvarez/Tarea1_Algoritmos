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
		return new Double(r1.getY()[i]).compareTo(new Double(r2.getY()[i]));
	}
}
