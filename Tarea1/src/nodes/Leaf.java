package nodes;

import rectangles.IRectangle;
import rectangles.MyRectangle;
import trees.RTree;
import utils.RectangleContainer;

/**
 * Clase que representa una hoja
 *
 */
public class Leaf extends AbstractNode{

	public Leaf(int t, boolean isRoot){
		NodeTuples = new MyRectangle[2*t];
		maxChildNumber = 2*t;
		this.isRoot = isRoot;
	}
	
	protected Leaf(IRectangle[] elements, String nodeName, int keyNumber){
		NodeTuples = elements;
		fileName = nodeName;
		this.keyNumber = keyNumber;
	}
	
	
	@Override
	public boolean isLeaf() {
		return true;
	}

	/**
	 * Permite insertar en hoja
	 */
	@Override
	public RectangleContainer insertNoReinsert(MyRectangle r, RTree t) {
		if(this.keyNumber>=maxChildNumber){
			return this.split(r,t);
		}
		else{
			NodeTuples[keyNumber] = r;
			keyNumber++;
		}
		return null;
		
	}

	/**
	 * Realiza split de la hoja
	 * @param r rectangulo a insertar
	 * @param t arbol al que pertenece la hoja
	 * @return RectangleContainer que posee nuevo MBR y referencia a nuevo hijo
	 */
	private RectangleContainer split(MyRectangle r, RTree t) {
		if(isRoot){
			this.isRoot=false;
			INode newRoot = new InternalNode(RTree.t, true);
			t.root = newRoot;
			
			return null;
		}
		return new RectangleContainer(r, 1); //no es asi
	}

	/**
	 * Busca rectangulo r en la hoja
	 */
	@Override
	public boolean buscar(MyRectangle r) {
		for(IRectangle rect : NodeTuples){
			if(rect.equals(r))
				return true;
		}
		return false;
	}

}
