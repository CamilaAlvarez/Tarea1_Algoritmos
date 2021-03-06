package nodes;

import gui.DrawRectangle;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import point.Point;

import rectangles.DistanceComparatorPairs;
import rectangles.IRectangle;
import rectangles.MBR;
import rectangles.MyRectangle;
import rectangles.PairComparatorX;
import rectangles.PairComparatorY;
import rectangles.RectangleComparatorX;
import rectangles.RectangleComparatorY;
import trees.RTree;
import utils.MyInteger;
import utils.Pair;
import utils.RectangleContainer;
import utils.Utils;

/**
 * Clase que representa nodo interno
 * 
 *
 */
public class InternalNode extends AbstractNode{
	private boolean childIsLeaf = true;
	//TODO los hijos no son nodos,son direcciones a disco (un número)
	private LinkedList<Pair> mbrList;
	
	public InternalNode(int keyNumber, boolean isRoot, IRectangle parentMBR,
			long filePos, LinkedList<Pair> p, boolean childIsLeaf){
		this.mbrList=p;
		this.constructor(keyNumber,isRoot,parentMBR,filePos,false,childIsLeaf);	
		
		
	}	

	public LinkedList<Pair> getRects(){
		return mbrList;
	}
	
	/**
	 * Permite saber si el nodo es hoja
	 */
	@Override
	public boolean isLeaf() {
		return false;
	}
	
	/**
	 * Método que permite insertar en el arbol sin la operacion de reinsertar.
	 * @param r Rectangulo a insertar
	 * @param t arbol al que pertenece el nodo. Permite conocer y modificar la raiz
	 * @return RectangleContainer que tiene dentro un rectangulo y una posicion de 
	 * un nuevo hijo. La idea es que esto se pase hacia arriba de manera recursiva, 
	 * pudiendo actualizar el arbol
	 * @throws IOException 
	 */
	@Override
	public RectangleContainer insertNoReinsert(MyRectangle r, RTree t) throws IOException{
		INode insertPlace = this.obtainInsertNode(r);
		RectangleContainer aux = insertPlace.insertNoReinsert(r,t);
		
		if(aux.p2==null){
			Pair p1 = aux.p1;
			for(Pair p : mbrList)
				if(p.childPos==p1.childPos){
					p.r = p1.r;
					break;
				}
			
			Pair p_aux = getNewMBR();
			this.parentMBR = p_aux.r;
			
			RTree.memManager.saveNode(this);
			return new RectangleContainer(p_aux, null, null);
		}
		
		else{
			for(Pair p : mbrList)
				if(p.r.equals(aux.r)){
					mbrList.remove(p);
					break;
				}
			
			/* se deberia borrar el hijo correpondiente a aux.r del archivo */
			keyNumber--;
			
			if(keyNumber+2>maxChildNumber){
				LinkedList<Pair> pairs = new LinkedList<Pair>();
				pairs.add(aux.p1);
				pairs.add(aux.p2);
				return this.split(pairs);
				
			}
			
			/* se guardan los dos mbr nuevos */
			mbrList.add(aux.p1);
			mbrList.add(aux.p2);
			keyNumber+=2;
			Pair p_aux = getNewMBR();
			this.parentMBR = p_aux.r;
			RTree.memManager.saveNode(this);
			return new RectangleContainer(p_aux, null, null);
		}
	}
	
	public Pair getNewMBR() {
		double minX,minY,maxX,maxY;
		minX = minY = Double.MAX_VALUE;
		maxX = maxY = Double.MIN_VALUE;
		
		for(Pair p : mbrList){
			MBR r = p.r;
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
	 * M�todo encargado de hacer split de un nodo al hacer reinsert
	 * @param p1 par a agregar
	 * @param t arbol al que pertenece el nodo
	 * @return RectangleContainer con el rectangulo que debe subir, y
	 * la posicion del hijo que se debe guardar
	 * @throws IOException 
	 */
	private RectangleContainer split(LinkedList<Pair> pairs) throws IOException {
		LinkedList<Pair> aux_list = new LinkedList<Pair>(mbrList);
		for(Pair p : pairs){
			aux_list.add(p);			
		}
		LinkedList<Pair> children = generateSplit(aux_list);
		return generalSplit(children); 
		
	}
	
	private LinkedList<Pair> generateSplit(LinkedList<Pair> aux_list) throws IOException{
		LinkedList<IRectangle> aux_rects = new LinkedList<IRectangle>();
		for(Pair p : aux_list)
			aux_rects.add(p.r);
		
		int m = 4*(2*RTree.t)/10;
		int end = 2*RTree.t + 2 -2*m;
		
		double margenX_0 = this.margen(aux_rects, new RectangleComparatorX(0),m, end);
		double margenY_0 = this.margen(aux_rects, new RectangleComparatorY(0),m, end);
		
		double margenX_1 = this.margen(aux_rects, new RectangleComparatorX(1),m, end);
		double margenY_1 = this.margen(aux_rects, new RectangleComparatorY(1),m, end);
		
		double min_margin0 = Math.min(margenX_0, margenY_0);
		double min_margin1 = Math.min(margenX_1, margenY_1);
		double min_margin = Math.min(min_margin0, min_margin1);
		
		LinkedList<Pair> children;
		if(min_margin==margenX_0)
			children = getNewChildren(aux_list, new PairComparatorX(0),m, end);
		else if(min_margin==margenX_1)
			children = getNewChildren(aux_list, new PairComparatorX(1),m, end);
		else if(min_margin==margenY_1)
			children = getNewChildren(aux_list, new PairComparatorY(1),m, end);
		else 
			children = getNewChildren(aux_list, new PairComparatorY(0),m, end);
		
		return children;
	}

	private LinkedList<Pair> getNewChildren(LinkedList<Pair> aux_list, Comparator<Pair> PairComparator,
			int m, int end) throws IOException {
		Collections.sort(aux_list, PairComparator);
		
		double intersection = Double.MAX_VALUE;
		HashMap<Integer, MBR[]> hash_min_index= new HashMap<Integer, MBR[]>();
		double minX, maxX, minY, maxY,minX2, maxX2, minY2, maxY2;
		MBR first_mbr, second_mbr;
		for(int i = 1; i<=end; i++){
			
			minX = minY = Double.MAX_VALUE;
			maxX = maxY = Double.MIN_VALUE;
			
			/* iterar para la primera parte de la distribucion */
			for(int j=0; j<m-1+i; j++){
				IRectangle r = aux_list.get(j).r;
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

			double[] mbr_x={minX, maxX};
			double[] mbr_y={minY, maxY};
			first_mbr = new MBR(mbr_x,mbr_y);
			
			minX2= minY2 = Double.MAX_VALUE;
			maxX2 = maxY2 = Double.MIN_VALUE;
		
			for(int j=m-1+i; j<2*RTree.t+1; j++){
				IRectangle r = aux_list.get(j).r;
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
			LinkedList<Pair> r = new LinkedList<Pair>();
			for(int i=0; i<m-1+key; i++){
				r.add(aux_list.get(i));
			}
			n1 = new InternalNode(r.size(), false, vals[0], this.getPosition(),
					r, childIsLeaf);
			r = new LinkedList<Pair>();
			for(int i=m-1+key; i<aux_list.size(); i++){
				r.add(aux_list.get(i));
			}
			n2 = new InternalNode(r.size(), false, vals[1], RTree.memManager.getNewPosition(),
					r, childIsLeaf);
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
			LinkedList<Pair> r = new LinkedList<Pair>();
			for(int i=0; i<m-1+min_area_key; i++){
				r.add(aux_list.get(i));
			}
			n1 = new InternalNode(r.size(), false, vals[0], this.getPosition(),
					r, childIsLeaf);
			r = new LinkedList<Pair>();
			for(int i=m-1+min_area_key; i<aux_list.size(); i++){
				r.add(aux_list.get(i));
			}
			n2 = new InternalNode(r.size(), false, vals[1], RTree.memManager.getNewPosition(),
					r, childIsLeaf);
			/*agregar elementos hojas, y luego guardar nuevos nodos en hijos*/

		}
		RTree.memManager.saveNode(n1);
		RTree.memManager.saveNode(n2);
		LinkedList<Pair> ret = new LinkedList<Pair>();
		ret.add(new Pair(vals[0],n1.getPosition()));
		ret.add( new Pair(vals[1],n2.getPosition()));
		return ret;
	}

	/**
	 * Entrega el Nodo contenido en el MBR con el menor aumento de overlap, 
	 * si hay empate se sigue con lo que dice el enunciado
	 * @param r Rectangulo que se va a agregar
	 * @return Nodo donde se va a insertar
	 * @throws IOException 
	 */
	private INode obtainInsertNode(MyRectangle r) throws IOException{
		if(!childIsLeaf){
			return this.getNodeMinAreaChange(r); 
		}
		
		return obtainFromMinOverlap(r);
	}

	/**
	 * Entrega el nodo donde se va a insertar, de acuerdo al minimo
	 * overlap 
	 * @param r rectangulo a insertar
	 * @return nodo donde se insertara
	 * @throws IOException 
	 */
	private INode obtainFromMinOverlap(MyRectangle r) throws IOException {
		ArrayList<Integer> minMBROverlapIndex = new ArrayList<Integer>();
		double minOverlapChange = Double.MAX_VALUE;

		for(int i=0 ; i<keyNumber ; i++){
			double change = this.getOverlapChange(i, r);
			if(change<minOverlapChange){
				minOverlapChange = change;
				minMBROverlapIndex = new ArrayList<Integer>();
				minMBROverlapIndex.add(i);
			}
			else if(change == minOverlapChange){
				minMBROverlapIndex.add(i);
			}
		}
		
		//Si no hay empates
		if(minMBROverlapIndex.size()==1){
			Pair p = mbrList.get(minMBROverlapIndex.get(0));
			INode n = RTree.memManager.loadNode(p.childPos);
			return n;
		}
		
		return this.getNodeMinAreaChange(r);
	}

	/**
	 * Permite calcular el cambio de overlap para un rectangulo del nodo
	 * en particular
	 * @param i indice del rectangulo Ek, en el arreglo de rectangulos del nodo
	 * @param r rectangulo a insertar
	 * @return overlap
	 */
	private double getOverlapChange(int i, MyRectangle r) {
		double overlap = 0;
		IRectangle me, other;
		me = mbrList.get(i).r;
		for(int j =0; j<keyNumber; j++){
			if(j==i)
				continue;
			other = mbrList.get(j).r;
			double o = me.intersectionArea(other);
			overlap = overlap + o;
		}
		double[] r_x = r.getX();
		double[] r_y = r.getY();
		double[] aux_x = me.getX();
		double[] aux_y =  me.getY();
		double[] x = new double[2];
		double[] y = new double[2];
		
		if(r_x[0]<aux_x[0])
			x[0] = r_x[0];
		else x[0] = aux_x[0];
		if(r_x[1]>aux_x[1])
			x[1] = r_x[1];
		else x[1] = aux_x[1];
		if(r_y[0]<aux_y[0])
			y[0] = r_y[0];
		else y[0]=aux_y[0];
		if(r_y[1]>aux_y[1])
			y[1] = r_y[1];
		else y[1]=aux_y[1];
		
		IRectangle aux_rect = new MBR(x,y);
		double new_overlap = 0;
		for(int j =0; j<keyNumber; j++){
			if(j==i)
				continue;
			other = mbrList.get(j).r;
			new_overlap += aux_rect.intersectionArea(other);
		}
		return new_overlap-overlap;
	}

	/**
	 * Calcula minimo cambio de area al agregar un rectangulo
	 * @param r rectangulo a agregar
	 * @return hijo del MBR que sufre menor cambio de area
	 * @throws IOException 
	 */
	private INode getNodeMinAreaChange(MyRectangle r) throws IOException{
		double minAreaChange = Double.MAX_VALUE;
		MBR rect;
		ArrayList<Integer> minMBRIndex = new ArrayList<Integer>();
		for(int i =0; i< keyNumber; i++){
			rect = mbrList.get(i).r;
			double change = rect.getAreaChange(r);
			if(change<minAreaChange){
				minAreaChange=change;
				minMBRIndex = new ArrayList<Integer>();
				minMBRIndex.add(i);
			}
			else if(change == minAreaChange){
				minMBRIndex.add(i);
			}
		}
		
		if(minMBRIndex.size()==1){
			Pair a = mbrList.get(minMBRIndex.get(0));
			INode n = RTree.memManager.loadNode(a.childPos);
			return n;
		}
		
		double minArea = Double.MAX_VALUE;
		int minAreaIndex = -1;
		for(int i = 0; i<minMBRIndex.size(); i++){
			rect = mbrList.get(minMBRIndex.get(i)).r;
			double area = rect.getArea();
			if(minArea>area){
				minArea=area;
				minAreaIndex=i;
			}
		}
		Pair p = mbrList.get(minAreaIndex);
		INode n = RTree.memManager.loadNode(p.childPos);
		return n;
	}

	/**
	 * Busca un rectangulo en el nodo
	 * @throws IOException 
	 */
	@Override 
	public LinkedList<IRectangle> buscar(MyRectangle r) throws IOException {
		LinkedList<IRectangle> res = new LinkedList<IRectangle>();
		MBR rect;
		for(int i = 0; i<keyNumber; i++){
			rect = mbrList.get(i).r;
			if(!rect.contains(r))
				continue;
			Pair p = mbrList.get(i);
			INode n = RTree.memManager.loadNode(p.childPos);
			LinkedList<IRectangle> inter_rects = n.buscar(r);
			res.addAll(inter_rects);
		}
		return res;
	}


	@Override
	public boolean eq(Object o) {
		if(o instanceof InternalNode){
			InternalNode l = (InternalNode)o;
			if(this.mbrList.size()==l.mbrList.size()){
				boolean r = true;
				for(int i=0; i<this.mbrList.size();i++){
					Pair r1 = mbrList.get(i);
					Pair r2 = l.mbrList.get(i);
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
	public void draw(LinkedList<DrawRectangle> r, int prof) throws IOException {
		for(Pair p : mbrList){
			MBR mbr = p.r;
			double[] x = mbr.getX();
			double[] y = mbr.getY();
			INode n = RTree.memManager.loadNode(p.childPos);
			n.draw(r, prof+1);
			r.add(new DrawRectangle(x[0], x[1], y[0], y[1], prof));
		}
		if(this.isRoot){
			IRectangle mbr = this.parentMBR;
			double[] x = mbr.getX();
			double[] y = mbr.getY();
			r.add(new DrawRectangle(x[0], x[1], y[0], y[1], 0));
		}
		
	}

	public LinkedList<Pair> reinsert(LinkedList<Pair> pairs) throws IOException {
		LinkedList<Pair> aux_rects = new LinkedList<Pair>(mbrList);
		for(Pair pair: pairs){
			aux_rects.add(pair);
		}
		
		Point p = this.parentMBR.getCenter();
		DistanceComparatorPairs comp = new DistanceComparatorPairs(p);
		Collections.sort(aux_rects, comp);
		long cant = RTree.p;
		
		LinkedList<Pair> removed = new LinkedList<Pair>();
		
		for(int i = 0; i<cant; i++){
			Pair rect = aux_rects.remove();
			removed.add(rect);
		}
		
		this.mbrList = aux_rects;
		this.keyNumber=mbrList.size();
		Pair pair = this.getNewMBR();
		this.parentMBR=pair.r;
		RTree.memManager.saveNode(this); 
		
		return removed;
	}
	
	public RectangleContainer overflowTreatment(int current, HashMap<Integer, Integer> dict,
			LinkedList<Pair> toReinsert, LinkedList<Pair> pairs, MyInteger h) throws IOException{
		if(dict.get((Integer)current)==null && !this.isRoot){
			dict.put((Integer)current, (Integer)1);
			LinkedList<Pair> tr = reinsert(pairs);
			for(Pair p : tr){
				toReinsert.add(p);
			}
			h.setInt(current);
		}
		else{
			return this.split(pairs);
		}
		Pair p1 = getNewMBR();
		this.parentMBR = p1.r;
		/* TODO aqui acabo de guardar nodo*/
		RTree.memManager.saveNode(this);

		return new RectangleContainer(p1, null, null);
	}

	@Override
	public RectangleContainer insertInHeight(Pair pair,
			HashMap<Integer, Integer> dict, int target, int current,
			LinkedList<Pair> toReinsert, MyInteger h) throws IOException {
		
		if(target!=current){//Insertar mas abajo
			MyRectangle r = Utils.toMyRectangle(pair.r);
			INode insertPlace = this.obtainInsertNode(r);
			RectangleContainer aux = insertPlace.insertInHeight(pair, dict, target, current+1, toReinsert, h);
			if(aux.p2==null){
				Pair p1 = aux.p1;
				for(Pair p : mbrList)
					if(p.childPos==p1.childPos){
						p.r = p1.r;
						break;
					}
				
				Pair p_aux = getNewMBR();
				this.parentMBR = p_aux.r;
				/* TODO aqui guarde nodo */
				RTree.memManager.saveNode(this);
				return new RectangleContainer(p_aux, null, null);
			}
			
			else{
				for(Pair p : mbrList)
					if(p.r.equals(aux.r)){
						mbrList.remove(p);
						break;
					}
				
				/* se deberia borrar el hijo correpondiente a aux.r del archivo */
				keyNumber--;
				
				if(keyNumber+2>maxChildNumber){
					LinkedList<Pair> pairs = new LinkedList<Pair>();
					pairs.add(aux.p1);
					pairs.add(aux.p2);
					return overflowTreatment(current, dict, toReinsert, pairs, h);
				}
				
				/* se guardan los dos mbr nuevos */
				mbrList.add(aux.p1);
				mbrList.add(aux.p2);
				keyNumber+=2;
				Pair p_aux = getNewMBR();
				this.parentMBR = p_aux.r;
				/* TODO aqui guarde nodo */
				RTree.memManager.saveNode(this);
				return new RectangleContainer(p_aux, null, null);
			}
		}
		else{//Insertar en este nivel
			System.out.println(current);
			if(this.keyNumber>=maxChildNumber){
				System.out.println("inside");
				LinkedList<Pair> pairs = new LinkedList<Pair>();
				pairs.add(pair);
				return overflowTreatment(current, dict, toReinsert, pairs, h);
			}
			System.out.println("outside");
			mbrList.add(pair);
			this.addKey();
			Pair p1 = getNewMBR();
			this.parentMBR = p1.r;
			/* TODO aqui guarde nodo */
			RTree.memManager.saveNode(this);
			return new RectangleContainer(p1, null, null);
		}
	}

	@Override
	protected int writeBuffer(byte[] data, int p){
		int pos = p;
		for(int i=0; i<this.keyNumber; i++){
			Pair pair = mbrList.get(i);
			pos = pair.r.writeBuffer(data, pos);
			ByteBuffer.wrap(data, pos, 8).putLong(pair.childPos);
			pos += 8;
		}	
		return pos;
		
	}

	

	
}
