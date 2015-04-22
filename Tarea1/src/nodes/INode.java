package nodes;

import gui.DrawRectangle;

import java.io.IOException;
import java.util.LinkedList;

import rectangles.IRectangle;
import rectangles.MyRectangle;
import trees.RTree;
import utils.DeletionPasser;
import utils.Pair;
import utils.RectangleContainer;

/**
 * Interfaz de nodos
 * 
 *
 */
public interface INode{
	
	public boolean isLeaf();
	public RectangleContainer insertNoReinsert(MyRectangle r, RTree rTree) throws IOException;
	public boolean buscar(MyRectangle r) throws IOException;
	public boolean isRoot();
	public void writeBuffer(byte[] data);
	public long getPosition();
	public void setFilePosition(long position);
	public DeletionPasser borrar(IRectangle r, int height) throws IOException;
	public Pair getNewMBR();
	public void draw(LinkedList<DrawRectangle> r, int profundidad) throws IOException;

}
