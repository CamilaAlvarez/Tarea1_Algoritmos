package generator;

import java.util.ArrayList;

import rectangles.MyRectangle;

public class RectangleGenerator {
	
	public static ArrayList<MyRectangle> generateRandom(double quantity, int s){
		int size = s;
		
		ArrayList<MyRectangle> rects = new ArrayList<MyRectangle>();
		for(int i=0; i<quantity; i++){
			double[] x = new double[2];
			double[] y = new double[2];
			double area = Double.MAX_VALUE;
			while(!(area>=1 && area<=100)){
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
	
	public static ArrayList<MyRectangle> generateRandom2(double quantity, int size){
		
		ArrayList<MyRectangle> rects = new ArrayList<MyRectangle>();
		for(int i=0; i<quantity; i++){
			double area = Math.random()*100;
			while(area<1)
				area = Math.random()*100;
			double xa = Math.random()*size;
			double ya = Math.random()*size;
			double cuadrante = Math.random();

			double xb = -1;
			double yb = -1;
			
			double d = Math.sqrt(area);
			double dx = 0;
			double dy = 0;
			while(dy == 0 && dx == 0){
				if(cuadrante<0.25 && xa+d<size && ya+d<size){
					dx = d;
					dy = d;
				}
				else if(cuadrante<0.50 && xa+d<size && ya-d>0){
					dx = d;
					dy = -d;
				}
				else if(cuadrante<0.75 && xa-d>0 && ya-d>0){
					dx = -d;
					dy = -d;
				}
				else if(xa-d>0 && ya+d<size){
					dx = -d;
					dy = d;
				}
				else{
					cuadrante = Math.random();
				}
			}
			

			
			double p = Math.random();
			double scale = Math.random();
			while(scale==0){
				scale = Math.random();
			}
			if(p<0.5){
				xb = xa + scale*dx;
				yb = ya + dy/scale;
			}
			else{
				xb = xa + dx/scale;
				yb = ya + dy*scale;
			}
			
			while(xb>size || yb>size || xb<0 || yb<0){
				scale = Math.random();
				while(scale==0){
					scale = Math.random();
				}
				p = Math.random();
				if(p<0.5){
					xb = xa + scale*dx;
					yb = ya + dy/scale;
				}
				else{
					xb = xa + dx/scale;
					yb = ya + dy*scale;
				}
			}
			
			double[] x = {xa,xb};
			double[] y = {ya,yb};
			
			if(xa>xb){
				x[0] = xb;
				x[1] = xa;
			}
			if(ya>yb){
				y[0] = yb;
				y[1] = ya;
			}
			
			
			rects.add(new MyRectangle(x, y));
		}
		return rects;
		
	}
}

