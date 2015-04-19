package nodes;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import rectangles.IRectangle;
import rectangles.MBR;
import rectangles.MyRectangle;
import rectangles.RectangleComparatorX;
import rectangles.RectangleComparatorY;
import trees.RTree;
import utils.RectangleContainer;
import utils.Pair;

/**
 * Clase que representa una hoja
 *
 */
public class Leaf extends AbstractNode{
	private LinkedList<IRectangle> rects;

	public Leaf(int t, boolean isRoot){
		maxChildNumber = 2*t;
		this.isRoot = isRoot;
		this.parentMBR = null;
		this.rects= new LinkedList<IRectangle>();
		this.keyNumber = 0;
	}
	
	public Leaf(int t, boolean isRoot, MBR mbr){
		maxChildNumber = 2*t;
		this.isRoot = isRoot;
		this.parentMBR = mbr;
		this.rects= new LinkedList<IRectangle>();
		this.keyNumber = 0;
	}
	
	public Leaf(int t, boolean isRoot, MBR mbr, LinkedList<IRectangle> rects){
		maxChildNumber = 2*t;
		this.rects= new LinkedList<IRectangle>(rects);
		this.isRoot = isRoot;
		this.parentMBR = mbr;
		this.keyNumber = rects.size();
	}
	
	@Override
	public boolean isLeaf() {
		return true;
	}

	/**
	 * Permite insertar en hoja
	 * @throws IOException 
	 */
	@Override
	public RectangleContainer insertNoReinsert(MyRectangle r, RTree t) throws IOException {
		if(this.keyNumber>=maxChildNumber){
			return this.split(r,t);
		}
		else{
			rects.add(keyNumber, r);
			this.addKey();
		}
		return null;
		
	}

	/**
	 * Realiza split de un Nodo
	 * @param r rectangulo a insertar
	 * @param t arbol al que pertenece el nodo
	 * @return RectangleContainer posee los dos nuevos MBR, con referencias a sus hijos
	 * y el viejo MBR que será eliminado
	 * @throws IOException 
	 */
	private RectangleContainer split(IRectangle r, RTree t) throws IOException {
		LinkedList<Pair> children = generateSplit(r);
		
		return generalSplit(children, t);
	}
	
	private LinkedList<Pair> generateSplit(IRectangle r) throws IOException{
		LinkedList<IRectangle> aux_rects = new LinkedList<IRectangle>(rects);
		aux_rects.add(r);
		int m = 4*(2*RTree.t+1)/10;
		int end = 6*(2*RTree.t +1)/10;
		
		double margenX_0 = this.margen(aux_rects, new RectangleComparatorX(0),m, end);
		double margenY_0 = this.margen(aux_rects, new RectangleComparatorY(0),m, end);
		
		double margenX_1 = this.margen(aux_rects, new RectangleComparatorX(1),m, end);
		double margenY_1 = this.margen(aux_rects, new RectangleComparatorY(1),m, end);
		
		double min_margin0 = Math.min(margenX_0, margenY_0);
		double min_margin1 = Math.min(margenX_1, margenY_1);
		double min_margin = Math.min(min_margin0, min_margin1);
		
		LinkedList<Pair> children;
		if(min_margin==margenX_0 || min_margin==margenX_1)
			children = getNewChildren(aux_rects, new RectangleComparatorX(0),m, end);
		else
			children = getNewChildren(aux_rects, new RectangleComparatorY(0),m, end);
		
		return children;
	}

	private LinkedList<Pair> getNewChildren(LinkedList<IRectangle> aux_rects, Comparator<IRectangle> rectangleComparator,
			int m, int end) throws IOException {
		Collections.sort(aux_rects, rectangleComparator);
		
		double intersection = Double.MAX_VALUE;
		HashMap<Integer, MBR[]> hash_min_index= new HashMap<Integer, MBR[]>();
		MBR first_mbr, second_mbr;
		for(int i = 0; i<end; i++){
			double minX, maxX, minY, maxY;
			double[] mbr_x = new double[2];
			double[] mbr_y = new double[2];
			minX = minY = Double.MAX_VALUE;
			maxX = maxY = Double.MIN_VALUE;
			
			/* iterar para la primera parte de la distribucion */
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
			mbr_x[0] = minX;
			mbr_x[1] = maxX;
			mbr_y[0] = minY;
			mbr_y[1] = maxY;
			first_mbr = new MBR(mbr_x,mbr_y);
			
			minX = minY = Double.MAX_VALUE;
			maxX = maxY = Double.MIN_VALUE;
		
			for(int j=m-1+i; j<2*RTree.t; j++){
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
			
			mbr_x[0] = minX;
			mbr_x[1] = maxX;
			mbr_y[0] = minY;
			mbr_y[1] = maxY;
			second_mbr = new MBR(mbr_x,mbr_y);
			
			double inter = first_mbr.intersectionArea(second_mbr);
			if(intersection>inter){
				intersection = inter;
				hash_min_index = new HashMap<Integer, MBR[]>();
				MBR[] val = {first_mbr, second_mbr};
				hash_min_index.put(i, val);
			}
			else if(intersection==inter){
				MBR[] val = {first_mbr, second_mbr};
				hash_min_index.put(i, val);
			}
		}
		
		MBR[] vals;
		INode n1, n2;
		if(hash_min_index.size()==1){
			/* Unica parte que cambia un poco con respecto a InternalNode */
			int key = (int) hash_min_index.keySet().toArray()[0];
			vals = hash_min_index.get(key);
			LinkedList<IRectangle> r = new LinkedList<IRectangle>();
			for(int i=0; i<m-1+key; i++){
				r.add(aux_rects.get(i));
			}
			n1 = new Leaf(RTree.t, false, vals[0],r);
			r = new LinkedList<IRectangle>();
			for(int i=m-1+key; i<aux_rects.size(); i++){
				r.add((MyRectangle) aux_rects.get(i));
			}
			n2 = new Leaf(RTree.t, false, vals[1],r);
			/*agregar elementos hojas, y luego guardar nuevos nodos en hijos*/
		}
		else{
			/* Aqui revisar cual distribucion genera mbrs de menor area */
			Set<Integer> keys = hash_min_index.keySet();
			double min_area = Double.MAX_VALUE;
			int min_area_key = -1;
			MBR[] val;
			for(Integer k : keys){
				val = hash_min_index.get(k);
				double area = val[0].getArea() + val[1].getArea();
				
				if(area<min_area){
					min_area = area;
					min_area_key = k;
				}
			}
			
			vals = hash_min_index.get(min_area_key);
			LinkedList<IRectangle> r = new LinkedList<IRectangle>();
			for(int i=0; i<m-1+min_area_key; i++){
				r.add(aux_rects.get(i));
			}
			
			n1 = new Leaf(RTree.t, false, vals[0],r);
			
			r = new LinkedList<IRectangle>();
			for(int i=m-1+min_area_key; i<aux_rects.size(); i++){
				r.add(aux_rects.get(i));
			}
			
			n2 = new Leaf(RTree.t, false, vals[1],r);			
			/*agregar elementos hojas, y luego guardar nuevos nodos en hijos*/
		}
		
		n1.setFilePosition(this.getPosition());
		n2.setFilePosition(RTree.memManager.getNewPosition());
		RTree.memManager.saveNode(n1);
		RTree.memManager.saveNode(n2);
		LinkedList<Pair> ret = new LinkedList<Pair>();
		ret.add(new Pair(vals[0],1));
		ret.add( new Pair(vals[1],1));
		return ret;
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

	@Override
	public byte[] writeBuffer() {
		// TODO Auto-generated method stub
		return null;
	}

}
