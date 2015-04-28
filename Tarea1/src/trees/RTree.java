package trees;

import gui.DrawRectangle;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import memory.IMemoryManager;
import nodes.INode;
import nodes.InternalNode;
import nodes.Leaf;
import rectangles.IRectangle;
import rectangles.MBR;
import rectangles.MyRectangle;
import utils.MyInteger;
import utils.Pair;
import utils.RectangleContainer;

public class RTree implements IRTree {
	public static int t;
	public static long p;
	public static IMemoryManager memManager;	
	public INode root;
	
	public RTree(int t, IMemoryManager mem) throws IOException{
		// true indica que se es raiz
		RTree.t= t;
		RTree.p = Math.round(2*t*0.3);
		double[] x = {0,0};
		double[] y = {0,0};
		RTree.memManager = mem;
		root = new Leaf(0, true, new MBR(x, y), mem.getNewPosition(), new LinkedList<IRectangle>());
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
			createNewRoot(c);
		}
	}
	
	@Override
	public LinkedList<IRectangle> buscar(MyRectangle r) throws IOException {
		return root.buscar(r);
		
	}

	public void draw(LinkedList<DrawRectangle> r) throws IOException {
		root.draw(r,1);
	}

	@Override
	public void insertWithReinsert(MyRectangle r) throws IOException {
		HashMap<Integer, Integer> dict = new HashMap<Integer, Integer>();
		
		MBR mbr = new MBR(r.getX(), r.getY());
		Pair pair = new Pair(mbr, -1L); 
		insertar2(pair, dict, -1);
		
	}
	public void insertar2(Pair r, HashMap<Integer, Integer> dict, int target) throws IOException{
		LinkedList<Pair> toReinsert = new LinkedList<Pair>();
		MyInteger h = new MyInteger(-1);
		RectangleContainer c = root.insertInHeight(r, dict, target, 0, toReinsert, h);
		//Ocurri� un split
		if (c.p2!=null){
			createNewRoot(c);
			if(h.getInt()!=-1) h.setInt(h.getInt()+1);
		}
		//Ocurri� un reinsert
		if(!toReinsert.isEmpty()){
			for(Pair p : toReinsert){
				insertar2(p,dict,h.getInt());
			}
		}
	}
	
	private void createNewRoot(RectangleContainer c) throws IOException{
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

