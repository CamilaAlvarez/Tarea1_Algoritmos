package nodes;

import rectangles.MyRectangle;
import trees.RTree;
import utils.RectangleContainer;

/**
 * Interfaz de nodos
 * 
 *
 */
public interface INode{
	
	public boolean isLeaf();
	public RectangleContainer insertNoReinsert(MyRectangle r, RTree rTree);
	public boolean buscar(MyRectangle r);
	public boolean isRoot();

}
