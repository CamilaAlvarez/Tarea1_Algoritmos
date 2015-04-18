package utils;

import rectangles.IRectangle;

/**
 * Clase usada para pasar informacion entre llamadas recursivas
 * 
 *
 */
public class RectangleContainer {
	public Pair p1, p2; /* corresponden a los dos nuevos nodos que salen */
	public IRectangle r; /* mbr que se debe eliminar */
	
	public RectangleContainer(Pair p1, Pair p2, IRectangle r){
		this.p1=p1;
		this.p2=p2;
		this.r = r;
	}

}
