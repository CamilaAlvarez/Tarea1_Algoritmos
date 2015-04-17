package nodes;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import rectangles.IRectangle;
import rectangles.MBR;
import rectangles.MyRectangle;
import rectangles.RectangleComparatorX;
import rectangles.RectangleComparatorY;
import rectangles.RectangleComparatos;
import trees.RTree;
import utils.RectangleContainer;

/**
 * Clase que representa una hoja
 *
 */
public class Leaf extends AbstractNode{
	private LinkedList<MyRectangle> rects= new LinkedList<MyRectangle>();

	public Leaf(int t, boolean isRoot){
		maxChildNumber = 2*t;
		this.isRoot = isRoot;
		this.parentMBR = null;
	}
	
	public Leaf(int t, boolean isRoot, MBR mbr){
		maxChildNumber = 2*t;
		this.isRoot = isRoot;
		this.parentMBR = mbr;
	}
	
	/*protected Leaf(IRectangle[] elements, String nodeName, int keyNumber){
		NodeTuples = elements;
		fileName = nodeName;
		this.keyNumber = keyNumber;
	}*/
	
	
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
			rects.add(keyNumber, r);
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
			
			LinkedList<MyRectangle> aux_rects = new LinkedList<MyRectangle>(rects);
			aux_rects.add(r);
			int m = 4*(2*RTree.t+1)/100;
			int end = (192*RTree.t +196)/100;
			
			double margenX_0 = this.margen(aux_rects, new RectangleComparatorX(0),m, end);
			double margenY_0 = this.margen(aux_rects, new RectangleComparatorY(0),m, end);
			
			double margenX_1 = this.margen(aux_rects, new RectangleComparatorX(1),m, end);
			double margenY_1 = this.margen(aux_rects, new RectangleComparatorY(1),m, end);
	
			INode newRoot = new InternalNode(RTree.t, true);
			t.root = newRoot;
			
			return null;
		}
		return new RectangleContainer(r, 1); //no es asi
	}

	private double margen(LinkedList<MyRectangle> aux_rects,
			Comparator<IRectangle> rectangleComparator, int m, int end) {
		Collections.sort(aux_rects, rectangleComparator);
		double margen=0;
		for(int i = 0; i<end; i++){
			margen += this.calcularMargen(m,i, aux_rects);
		}
		return margen;
	}

	private double calcularMargen(int m, int i, LinkedList<MyRectangle> rects) {
		double minX, maxX, minY, maxY;
		minX = minY = Double.MAX_VALUE;
		maxX = maxY = Double.MIN_VALUE;
		double margen;
		for(int j=0; j<m-1+i; j++){
			MyRectangle r = rects.get(j);
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
			MyRectangle r = rects.get(j);
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

	/**
	 * Busca rectangulo r en la hoja
	 */
	@Override
	public boolean buscar(MyRectangle r) {
		for(IRectangle rect : rects){
			if(rect.equals(r))
				return true;
		}
		return false;
	}

}
