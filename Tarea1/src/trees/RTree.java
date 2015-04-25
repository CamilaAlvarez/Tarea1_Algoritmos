package trees;

import gui.DrawRectangle;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import memory.IMemoryManager;
import memory.PMManager;
import nodes.INode;
import nodes.InternalNode;
import nodes.Leaf;
import rectangles.IRectangle;
import rectangles.MBR;
import rectangles.MyRectangle;
import utils.Pair;
import utils.RectangleContainer;

public class RTree implements IRTree {
	public static int t;
	public static int p;
	public static IMemoryManager memManager;	
	public INode root;
	
	public RTree(int t) throws IOException{
		// true indica que se es raiz
		RTree.t= t;
		RTree.p = 30;
		double[] x = {0,0};
		double[] y = {0,0};

		root = new Leaf(0, true, new MBR(x, y), 0, new LinkedList<IRectangle>());
		RTree.memManager = new PMManager();
		memManager.saveNode(root);
	}

	/**
	 * Permite insertar en el arbol, partiendo desde la raiz
	 * @throws IOException 
	 */
	@Override
	public void insertar(MyRectangle r) throws IOException {
		RectangleContainer c = root.insertNoReinsert(r,this);
		if (c.p2!=null){
			LinkedList<Pair> children = new LinkedList<Pair>();
			children.add(c.p1);
			children.add(c.p2);
			boolean b = root.isLeaf();
			InternalNode newRoot = new InternalNode(2, true, null, RTree.memManager.getNewPosition(), children, b);
			Pair p = newRoot.getNewMBR();
			newRoot.setParentMBR(p.r);
			/* se debe guardar la raiz en memoria secundaria */
			this.root = newRoot;
			RTree.memManager.saveNode(newRoot);
		}
		
	}
	
	@Override
	public LinkedList<IRectangle> buscar(MyRectangle r) throws IOException {
		return root.buscar(r);
		
	}
	
	private void borrar(MyRectangle r) throws IOException{
		root.borrar(r, 0, root);
	}

	public void draw(LinkedList<DrawRectangle> r) throws IOException {
		root.draw(r,1);
	}

	@Override
	public void insertar2(MyRectangle r) throws IOException {
		HashMap<Integer, Integer> dict = new HashMap<Integer, Integer>();
		root.insertReinsert(r,this,dict,1);
		
	}



}
