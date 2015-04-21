package generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import rectangles.MyRectangle;

public class RectangleGenerator {
	
	public static ArrayList<MyRectangle> generateRandom(int quantity){
		int size = 700;
		
		ArrayList<MyRectangle> rects = new ArrayList<MyRectangle>();
		for(int i=0; i<quantity; i++){
			double[] x = new double[2];
			double[] y = new double[2];
			double area = Double.MAX_VALUE;
			while(area>3000 || area<2000){
				double xa=Math.random()*size;
				double xb=Math.random()*size;
				double ya=Math.random()*size;
				double yb=Math.random()*size;
				
				x[0] = Math.min(xa, xb);
				x[1] = Math.max(xa, xb);
				y[0] = Math.min(ya, yb);
				y[1] = Math.max(ya, yb);
				
				MyRectangle r = new MyRectangle(x, y);
				area = r.getArea();
			}
			rects.add(new MyRectangle(x, y));
		}
		return rects;
		
	}
}

