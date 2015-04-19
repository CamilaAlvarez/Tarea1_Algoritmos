package nodes;

import java.io.IOException;

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
	public RectangleContainer insertNoReinsert(MyRectangle r, RTree rTree) throws IOException;
	public boolean buscar(MyRectangle r);
	public boolean isRoot();
	public byte[] writeBuffer();
	public long getPosition();
	public void setFilePosition(long position);

}
