package nodes;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import rectangles.IRectangle;
import trees.RTree;
import utils.Pair;
import utils.RectangleContainer;


public abstract class AbstractNode implements INode{
	protected int keyNumber;
	protected String fileName;
	protected boolean isRoot;
	protected int maxChildNumber;
	protected IRectangle parentMBR;
	protected long filePos;
	
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
		for(int i = 0; i<end; i++){
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
	
	protected RectangleContainer generalSplit(LinkedList<Pair> children, RTree t){
		if(this.isRoot){
			this.isRoot=false;
			INode newRoot = new InternalNode(RTree.t, true, children);
			/* se debe guardar la raiz en memoria secundaria */
			t.root = newRoot;
			
			return null;
		}
		Pair p_1, p_2;
		p_1 = children.getFirst();
		p_2 = children.getLast();
		return new RectangleContainer(p_1,p_2,this.parentMBR); 
		
	}


}
