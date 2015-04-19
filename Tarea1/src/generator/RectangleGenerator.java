package generator;

import java.util.ArrayList;

import rectangles.MyRectangle;

public class RectangleGenerator {
	
	public static ArrayList<MyRectangle> generateRandom(int quantity){
		int size = 500000;
		double[] x = new double[2];
		double[] y = new double[2];
		
		ArrayList<MyRectangle> rects = new ArrayList<MyRectangle>();
		for(int i=0; i<quantity; i++){
			x[0]=Math.random()*size;
			x[1]=Math.random()*size;
			y[0]=Math.random()*size;
			y[1]=Math.random()*size;
			rects.add(new MyRectangle(x, y));
		}
		return rects;
		
	}
}

