package trees;

import java.io.IOException;

import memory.IMemoryManager;
import memory.PMManager;
import nodes.Leaf;
import rectangles.MyRectangle;

public class RTree extends AbstractRTree {
	public static int t;
	public static IMemoryManager memManager;
	
	public RTree(int t) throws IOException{
		// true indica que se es raiz
		RTree.t= t;
		root = new Leaf(t,true);
		root.setFilePosition(0);
		RTree.memManager = new PMManager();
		memManager.saveNode(root);
	}

	/**
	 * Permite insertar en el arbol, partiendo desde la raiz
	 * @throws IOException 
	 */
	@Override
	public void insertar(MyRectangle r) throws IOException {
		root.insertNoReinsert(r,this);
		
		
		
	}


}
