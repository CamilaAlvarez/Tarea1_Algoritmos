package trees;

import rectangles.MyRectangle;
import nodes.INode;

public abstract class AbstractRTree implements IRTree {
	public INode root;
	
	/**
	 * Permite buscar en el arbol, partiendo de la raiz
	 */
	@Override
	public boolean buscar(MyRectangle r) {
		return root.buscar(r);
		
	}
}
