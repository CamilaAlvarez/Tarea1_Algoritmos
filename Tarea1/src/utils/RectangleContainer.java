package utils;

import rectangles.IRectangle;

/**
 * Clase usada para pasar informacion entre llamadas recursivas
 * @author camo
 *
 */
public class RectangleContainer {
	public IRectangle r;
	public long childPos;
	
	public RectangleContainer(IRectangle r, long childPos){
		this.r=r;
		this.childPos=childPos;
	}

}
