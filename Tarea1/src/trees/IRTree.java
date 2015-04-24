package trees;

import java.io.IOException;
import java.util.LinkedList;

import rectangles.IRectangle;
import rectangles.MyRectangle;

public interface IRTree {
	
	public void insertar(MyRectangle r) throws IOException;
	public LinkedList<IRectangle> buscar(MyRectangle r) throws IOException;
	public void insertar2(MyRectangle r) throws IOException;
}
