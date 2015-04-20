package trees;

import java.io.IOException;
import java.util.LinkedList;

import memory.IMemoryManager;
import memory.PMManager;
import nodes.INode;
import nodes.Leaf;
import rectangles.IRectangle;
import rectangles.MyRectangle;

public class RTree implements IRTree {
	public static int t;
	public static IMemoryManager memManager;	
	public INode root;
	
	public RTree(int t) throws IOException{
		// true indica que se es raiz
		RTree.t= t;
		root = new Leaf(0, true, null, 0, new LinkedList<IRectangle>());
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
	
	@Override
	public boolean buscar(MyRectangle r) throws IOException {
		return root.buscar(r);
		
	}


}
