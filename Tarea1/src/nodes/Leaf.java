package nodes;

import gui.DrawRectangle;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import point.Point;

import rectangles.DistanceComparator;
import rectangles.IRectangle;
import rectangles.MBR;
import rectangles.MyRectangle;
import rectangles.RectangleComparatorX;
import rectangles.RectangleComparatorY;
import trees.RTree;
import utils.MyInteger;
import utils.Pair;
import utils.RectangleContainer;

/**
 * Clase que representa una hoja
 *
 */
public class Leaf extends AbstractNode{
	private LinkedList<IRectangle> rects;

	public Leaf(int keyNumber, boolean isRoot, IRectangle parentMBR,
			long filePos, LinkedList<IRectangle> r){
		this.rects=new LinkedList<IRectangle>(r);		
		this.constructor(keyNumber,isRoot,parentMBR,filePos,true,false);		
	}
	
	public LinkedList<IRectangle> getRects(){
		return rects;
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
		Pair p1;
		if(this.keyNumber>=maxChildNumber){
			return this.split(r);
		}
		else{
			rects.add(r);
			this.addKey();
			p1 = getNewMBR();
			this.parentMBR = p1.r;
			/* TODO aqui guarde nodo */
			RTree.memManager.saveNode(this);
		}
		return new RectangleContainer(p1, null, null);
		
	}

	public Pair getNewMBR() {
		double minX,minY,maxX,maxY;
		minX = minY = Double.MAX_VALUE;
		maxX = maxY = Double.MIN_VALUE;
		
		for(IRectangle r : rects){
			double x[] = r.getX();
			double y[] = r.getY();
			if(x[0]<minX) minX=x[0];
			if(x[1]>maxX) maxX=x[1];
			if(y[0]<minY) minY=y[0];
			if(y[1]>maxY) maxY=y[1];
		}
		double[] newX = {minX, maxX};
		double[] newY = {minY, maxY};
		return new Pair(new MBR(newX, newY), this.getPosition());
	}

	/**
	 * Realiza split de un Nodo
	 * @param r rectangulo a insertar
	 * @param t arbol al que pertenece el nodo
	 * @return RectangleContainer posee los dos nuevos MBR, con referencias a sus hijos
	 * y el viejo MBR que será eliminado
	 * @throws IOException 
	 */
	private RectangleContainer split(IRectangle r) throws IOException {
		LinkedList<Pair> children = generateSplit(r);
		
		return generalSplit(children);
	}
	
	private LinkedList<Pair> generateSplit(IRectangle r) throws IOException{
		LinkedList<IRectangle> aux_rects = new LinkedList<IRectangle>(rects);
		aux_rects.add(r);
		int m = 4*(2*RTree.t)/10;
		int end = 2*RTree.t+2-2*m;
		
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
		
		for(int i = 1; i<=end; i++){
			MBR first_mbr, second_mbr;
			double minX, maxX, minY, maxY,minX2, maxX2, minY2, maxY2;
			minX = Double.MAX_VALUE;
			minY = Double.MAX_VALUE;
			maxX = Double.MIN_NORMAL;
			maxY = Double.MIN_VALUE;
			
			/* iterar para la primera parte de la distribucion */
			for(int j=0; j<m-1+i; j++){
				IRectangle r = aux_rects.get(j);
				double[] x1 = r.getX();
				double[] y1 = r.getY();
				if(x1[0]<minX)
					minX = x1[0];
				if(x1[1]>maxX)
					maxX = x1[1];
				if(y1[0]<minY)
					minY = y1[0];
				if(y1[1]>maxY)
					maxY = y1[1];
			}
			double[] mbr_x={minX, maxX};
			double[] mbr_y={minY, maxY};
			first_mbr = new MBR(mbr_x,mbr_y);
			
			minX2 = Double.MAX_VALUE;
			minY2 = Double.MAX_VALUE;
			maxX2 = Double.MIN_VALUE;
			maxY2 = Double.MIN_VALUE;
			
			for(int j=m-1+i; j<2*RTree.t+1; j++){
				IRectangle r = aux_rects.get(j);
				double[] x = r.getX();
				double[] y = r.getY();
				if(x[0]<minX2)
					minX2 = x[0];
				if(x[1]>maxX2)
					maxX2 = x[1];
				if(y[0]<minY2)
					minY2 = y[0];
				if(y[1]>maxY2)
					maxY2 = y[1];
			}
			
			double[] mbr_x2={minX2, maxX2};
			double[] mbr_y2={minY2, maxY2};
			second_mbr = new MBR(mbr_x2,mbr_y2);
			
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
			n1 = new Leaf(r.size(), false, vals[0], this.getPosition(), r);
			
			r = new LinkedList<IRectangle>();
			for(int i=m-1+key; i<aux_rects.size(); i++){
				r.add(aux_rects.get(i));
			}
			n2 = new Leaf(r.size(), false, vals[1], RTree.memManager.getNewPosition(), r);
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
			
			n1 = new Leaf(r.size(), false, vals[0], this.getPosition(), r);
			
			r = new LinkedList<IRectangle>();
			for(int i=m-1+min_area_key; i<aux_rects.size(); i++){
				r.add(aux_rects.get(i));
			}

			n2 = new Leaf(r.size(), false, vals[1], RTree.memManager.getNewPosition(), r);
			/*agregar elementos hojas, y luego guardar nuevos nodos en hijos*/
		}
		
		
		RTree.memManager.saveNode(n1);
		RTree.memManager.saveNode(n2);
		LinkedList<Pair> ret = new LinkedList<Pair>();
		ret.add(new Pair(vals[0],n1.getPosition()));
		ret.add(new Pair(vals[1],n2.getPosition()));
		return ret;
	}


	/**
	 * Busca rectangulo r en la hoja
	 */
	@Override
	public LinkedList<IRectangle> buscar(MyRectangle r) {
		LinkedList<IRectangle> res = new LinkedList<IRectangle>();
		for(IRectangle rect : rects){
			if(rect.intersects(r))
				res.add(rect);
		}
		return res;
	}

	@Override
	public boolean eq(Object o) {
		if(o instanceof Leaf){
			Leaf l = (Leaf)o;
			if(this.rects.size()==l.rects.size()){
				boolean r = true;
				for(int i=0; i<this.rects.size();i++){
					IRectangle r1 = rects.get(i);
					IRectangle r2 = l.rects.get(i);
					r = r && (r1.equals(r2));
				}
				return r;
			}
			else{
				return false;
			}
		}
		return false;
	}

	@Override
	public void draw(LinkedList<DrawRectangle> rectangles, int prof) {
		for(IRectangle r: rects){
			double[] x = r.getX();
			double[] y = r.getY();
			//System.out.println(x[0]+","+ x[1]+","+y[0]+","+y[1]);
			rectangles.add(new DrawRectangle(x[0], x[1], y[0], y[1], prof));
		}
		if(this.isRoot){
			IRectangle mbr = this.parentMBR;
			double[] x = mbr.getX();
			double[] y = mbr.getY();
			rectangles.add(new DrawRectangle(x[0], x[1], y[0], y[1], 0));
		}
		
	}
	
	public String toString(){
		String s = "";
		for(IRectangle r: rects){
			s = s + r.toString();
		}
		return s;
	}

	public LinkedList<Pair> reinsert(Pair r) {
		LinkedList<IRectangle> aux_rects = new LinkedList<IRectangle>(rects);
		aux_rects.add(r.r);
		
		Point p = this.parentMBR.getCenter();
		DistanceComparator comp = new DistanceComparator(p);
		Collections.sort(aux_rects, comp);
		long cant = RTree.p;
		
		LinkedList<Pair> removed = new LinkedList<Pair>();
		
		for(int i = 0; i<cant; i++){
			IRectangle rect = aux_rects.remove();
			MBR mbr = new MBR(rect.getX(), rect.getY());
			removed.add(new Pair(mbr, -1L));
		}
		
		this.rects = aux_rects;
		this.keyNumber=rects.size();
		Pair p1 = this.getNewMBR();
		this.parentMBR=p1.r;
		
		return removed;
		
	}

	@Override
	public RectangleContainer insertInHeight(Pair pair,
			HashMap<Integer, Integer> dict, int target, int current, 
			LinkedList<Pair> toReinsert, MyInteger h) throws IOException {
		Pair p1;
		if(this.keyNumber>=maxChildNumber){
			if(dict.get((Integer)current)==null && !this.isRoot){
				dict.put((Integer)current, (Integer)1);
				LinkedList<Pair> tr = reinsert(pair);
				for(Pair p : tr){
					toReinsert.add(p);
				}
				h = new MyInteger(current);
			}
			else{
				return this.split(pair.r);
			}
		}
		else{
			rects.add(pair.r);
			this.addKey();
		}
		p1 = getNewMBR();
		this.parentMBR = p1.r;
		/* TODO aqui guarde nodo */
		RTree.memManager.saveNode(this);
		return new RectangleContainer(p1, null, null);
	}

	@Override
	protected int writeBuffer(byte[] data, int p) {
		int pos = p;
		for(int i=0; i<this.keyNumber; i++){
			IRectangle r = rects.get(i);
			pos = r.writeBuffer(data, pos);
		}	
		return pos;
	}

	

}
