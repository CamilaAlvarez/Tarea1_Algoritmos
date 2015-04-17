package utils;

import rectangles.IRectangle;

/**
 * Clase usada para pasar informacion entre llamadas recursivas
 * 
 *
 */
public class RectangleContainer {
	public Pair p1, p2;
	public IRectangle r;
	
	public RectangleContainer(Pair p1, Pair p2, IRectangle r){
		this.p1=p1;
		this.p2=p2;
		this.r = r;
	}

}
