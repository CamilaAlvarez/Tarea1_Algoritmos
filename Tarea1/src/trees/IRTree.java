package trees;

import java.io.IOException;

import rectangles.MyRectangle;

public interface IRTree {
	
	public void insertar(MyRectangle r) throws IOException;
	public boolean buscar(MyRectangle r) throws IOException;
}
