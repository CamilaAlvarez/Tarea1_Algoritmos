package nodes;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import rectangles.IRectangle;
import trees.RTree;
import utils.DeletionPasser;
import utils.DeletionTriplet;
import utils.Pair;
import utils.RectangleContainer;


public abstract class AbstractNode implements INode{
	protected int keyNumber;
	protected boolean isRoot;
	protected int maxChildNumber;
	protected IRectangle parentMBR;
	protected long filePos;
	protected boolean isLeaf;
	protected boolean childIsLeaf;
	
	protected void constructor(int keyNumber, boolean isRoot, IRectangle parentMBR,
			long filePos, boolean isLeaf, boolean childIsLeaf){
		
		this.keyNumber=keyNumber;
		this.isRoot=isRoot;
		this.maxChildNumber=2*RTree.t;
		this.parentMBR=parentMBR;
		this.filePos=filePos;
		this.isLeaf=isLeaf;
		this.childIsLeaf=childIsLeaf;		
	}
	
	/**
	 * Aumenta la variable de numero de llaves en 1
	 */
	public void addKey(){
		keyNumber++;
	}
	
	/**
	 * Funcion que entrega el numero de llaves que contiene 
	 * un nodo
	 * @return keyNumber
	 */
	public int getKeyNumber(){
		return keyNumber;
	}
	
	public void setFilePosition(long pos){
		this.filePos=pos;
	} 
	
	/**
	 * Define si un nodo es raiz
	 */
	public boolean isRoot(){
		return isRoot;
	}
	
	public long getPosition() {
		return filePos;
	}
	
	protected double margen(LinkedList<IRectangle> aux_rects,
			Comparator<IRectangle> rectangleComparator, int m, int end) {
		Collections.sort(aux_rects, rectangleComparator);
		double margen=0;
		for(int i = 1; i<=end; i++){
			margen += this.calcularMargen(m,i, aux_rects);
		}
		return margen;
	}

	protected double calcularMargen(int m, int i, LinkedList<IRectangle> rects) {
		
		double minX, maxX, minY, maxY;
		minX = minY = Double.MAX_VALUE;
		maxX = maxY = Double.MIN_VALUE;
		double margen;
		for(int j=0; j<m-1+i; j++){
			IRectangle r = rects.get(j);
			double[] x = r.getX();
			double[] y = r.getY();
			if(x[0]<minX)
				minX = x[0];
			if(x[1]>maxX)
				maxX = x[1];
			if(y[0]<minY)
				minY = y[0];
			if(y[1]>maxY)
				maxY = y[1];
		}
		margen = 2*(maxX-minX)+2*(maxY-minY);
		
		minX = minY = Double.MAX_VALUE;
		maxX = maxY = Double.MIN_VALUE;
	
		for(int j=m-1+i; j<2*RTree.t+1; j++){
			IRectangle r = rects.get(j);
			double[] x = r.getX();
			double[] y = r.getY();
			if(x[0]<minX)
				minX = x[0];
			if(x[1]>maxX)
				maxX = x[1];
			if(y[0]<minY)
				minY = y[0];
			if(y[1]>maxY)
				maxY = y[1];
		}
		margen += 2*(maxX-minX)+2*(maxY-minY);
		return margen;
	}
	
	protected RectangleContainer generalSplit(LinkedList<Pair> children, RTree t, boolean childIsLeaf) throws IOException{
		if(this.isRoot){
			this.isRoot=false;
			InternalNode newRoot = new InternalNode(children.size(), true, null, RTree.memManager.getNewPosition(), children, childIsLeaf);
			Pair p = newRoot.getNewMBR();
			newRoot.parentMBR = p.r;
			/* se debe guardar la raiz en memoria secundaria */
			t.root = newRoot;
			RTree.memManager.saveNode(newRoot);
			
			return null;
		}
		Pair p_1, p_2;
		p_1 = children.getFirst();
		p_2 = children.getLast();
		return new RectangleContainer(p_1,p_2,this.parentMBR); 
		
	}
	@Override
	public void writeBuffer(byte[] data){
		int pos = 0;
		
		ByteBuffer.wrap(data,pos,4).putInt(keyNumber);
		pos += 4;
		byte root = isRoot?(byte)1:(byte)0;
		ByteBuffer.wrap(data, pos, 1).put(root);
		pos += 1;
		byte leaf = isLeaf?(byte)1:(byte)0;
		ByteBuffer.wrap(data, pos, 1).put(leaf);
		pos += 1;
		byte child = childIsLeaf?(byte)1:(byte)0;
		ByteBuffer.wrap(data, pos, 1).put(child);
		pos += 1;
		ByteBuffer.wrap(data, pos, 8).putLong(filePos);
		pos += 8;
		
		pos = parentMBR.writeBuffer(data, pos);		
		
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof AbstractNode){
			AbstractNode node = (AbstractNode)obj;
			boolean result = true;
			result = result && (this.getKeyNumber() == node.getKeyNumber());
			result = result && (this.isLeaf == node.isLeaf);
			result = result && (this.isRoot == node.isRoot);
			result = result && (this.childIsLeaf == node.childIsLeaf);
			result = result && (this.filePos == node.filePos);
			result = result && (this.parentMBR.equals(node.parentMBR));			
			result = result && eq(node);			
			
			return result;
		}
		return false;
	}
	
	public abstract boolean eq (Object o);

	public DeletionPasser condensar(DeletionPasser d, int height) {
		if(this.isRoot())/*reinsertar*/
			this.ReinsertDeletedNodes(d,this);
		if(this.keyNumber<RTree.t){
			d.deletedNodes.push(new DeletionTriplet(this, this.getPosition(),height));
			d.needToRemove=true;
		}
		else{
			Pair p = this.getNewMBR();
			d.setMBRPair(p);
		}
		return d;
	}

	protected void ReinsertDeletedNodes(DeletionPasser d, INode root) {
		LinkedList<Pair> mbrs;
		LinkedList<IRectangle> rect;
		for(DeletionTriplet del : d.deletedNodes){
			INode node = del.node;
			int height = del.nodeHeight;
			if(node.isLeaf()){
				rect = ((Leaf)node).getRects();
				for(IRectangle r : rect){
					root.insertReinsert(r);
				}
			}
			else{
				mbrs = ((InternalNode)node).getRects();
				for(Pair r : mbrs){
					root.insertInHeight(r,height);
				}
			}	
		}
		
	}


}
