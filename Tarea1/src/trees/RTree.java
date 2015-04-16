package trees;

import rectangles.MyRectangle;
import nodes.Leaf;

public class RTree extends AbstractRTree {
	public static int t;
	
	public RTree(int t){
		// true indica que se es raiz
		RTree.t= t;
		root = new Leaf(t,true);
	}

	/**
	 * Permite insertar en el arbol, partiendo desde la raiz
	 */
	@Override
	public void insertar(MyRectangle r) {
		root.insertNoReinsert(r,this);
		
		
		
	}


}
