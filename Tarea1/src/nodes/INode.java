package nodes;

import gui.DrawRectangle;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import rectangles.IRectangle;
import rectangles.MyRectangle;
import trees.RTree;
import utils.MyInteger;
import utils.Pair;
import utils.RectangleContainer;

/**
 * Interfaz de nodos
 * 
 *
 */
public interface INode{
	
	public void setParentMBR(IRectangle parentMBR);
	public boolean isLeaf();
	public RectangleContainer insertNoReinsert(MyRectangle r, RTree rTree) throws IOException;
	public LinkedList<IRectangle> buscar(MyRectangle r) throws IOException;
	public boolean isRoot();
	public void writeBuffer(byte[] data);
	public long getPosition();
	public void setFilePosition(long position);
	public Pair getNewMBR();
	public void draw(LinkedList<DrawRectangle> r, int profundidad) throws IOException;
	/**
	 * Inserta un rect�ngulo (o mbr) con restricci�n de altura
	 * @param pair objeto a insertar
	 * @param dict diccionario para determinar si se usa reinsert o split
	 * @param target altura ojetivo en donde insertar
	 * @param current altura actual en donde se esta insertando
	 * @param toReinsert Lista en donde agregar rectangulos a reinsertar
	 * @param h Integer en donde guardar la altura objetivo en caso de reinsertar
	 * @return contenedor con los pares en caso de split
	 * @throws IOException
	 */
	public RectangleContainer insertInHeight(Pair pair, HashMap<Integer,Integer> dict, int target, int current,
			LinkedList<Pair> toReinsert, MyInteger h) throws IOException;

}
