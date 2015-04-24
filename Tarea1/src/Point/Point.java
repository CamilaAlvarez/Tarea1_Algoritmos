package Point;

public class Point {
	
	private double x, y;
	
	public Point(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public double distance(Point p){
		return (Math.sqrt(Math.pow(this.x - p.x, 2) + Math.pow(this.y - p.y, 2)));
	}

}
