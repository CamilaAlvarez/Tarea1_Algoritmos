package nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import com.sun.xml.internal.fastinfoset.algorithm.IntEncodingAlgorithm;

import rectangles.IRectangle;
import rectangles.MBR;
import rectangles.MyRectangle;
import rectangles.PairComparatorX;
import rectangles.PairComparatorY;
import rectangles.RectangleComparatorX;
import rectangles.RectangleComparatorY;
import trees.RTree;
import utils.Pair;
import utils.RectangleContainer;

/**
 * Clase que representa nodo interno
 * 
 *
 */
public class InternalNode extends AbstractNode{
	private boolean childIsLeaf = true;
	//TODO los hijos no son nodos,son direcciones a disco (un número)
	private LinkedList<Pair> mbrList;
	
	
	public InternalNode(int t, boolean isRoot){
		maxChildNumber = 2*t;
		this.isRoot = isRoot;
		this.parentMBR = null;
		this.mbrList= new LinkedList<Pair>();
	}
	
	public InternalNode(int t, boolean isRoot, MBR mbr){
		maxChildNumber = 2*t;
		this.isRoot = isRoot;
		this.parentMBR = mbr;
		this.mbrList= new LinkedList<Pair>();
	}
	
	public InternalNode(int t, boolean isRoot, LinkedList<Pair> pairs){
		maxChildNumber = 2*t;
		this.isRoot = isRoot;
		this.parentMBR = null;
		this.mbrList= new LinkedList<Pair>(pairs);
	}
	
	public InternalNode(int t, boolean isRoot, MBR mbr, LinkedList<Pair> pairs){
		maxChildNumber = 2*t;
		this.isRoot = isRoot;
		this.parentMBR = null;
		this.mbrList= new LinkedList<Pair>(pairs);
	}
	
	/*protected InternalNode(IRectangle[] elements, String nodeName, int keyNumber,
			boolean childIsLeaf){
		this.childIsLeaf = childIsLeaf;
		NodeTuples = elements;
		fileName = nodeName;
		this.keyNumber = keyNumber;
	}*/
	

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
	 */
	@Override
	public RectangleContainer insertNoReinsert(MyRectangle r, RTree t){
		INode insertPlace = this.obtainInsertNode(r);
		RectangleContainer aux = insertPlace.insertNoReinsert(r,t);
		
		if(aux==null)
			return null;
		
		/* se remueve el mbr anterior */
		for(Pair p : mbrList)
			if(p.r.equals(aux.r))
				mbrList.remove(p);
		
		/* se deberia borrar el hijo correpondiente a aux.r del archivo */
		keyNumber--;
		
		if(keyNumber+2>=maxChildNumber){
			return this.split(aux.p1, aux.p2,t);
			
		}
		
		/* se guardar los dos mbr nuevos */
		mbrList.add(aux.p1);
		mbrList.add(aux.p2);
		keyNumber+=2;
		return null;
	}
	
	/**
	 * Método encargado de hacer split de un nodo
	 * @param p1 par a agregar
	 * @param p2 par a agregar
	 * @param t arbol al que pertenece el nodo
	 * @return RectangleContainer con el rectangulo que debe subir, y
	 * la posicion del hijo que se debe guardar
	 */
	private RectangleContainer split(Pair p1, Pair p2, RTree t) {
		LinkedList<Pair> aux_list = new LinkedList<Pair>(mbrList);
		aux_list.add(p1);
		aux_list.add(p2);
		LinkedList<Pair> children = generateSplit(aux_list);
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
	
	private LinkedList<Pair> generateSplit(LinkedList<Pair> aux_list){
		LinkedList<IRectangle> aux_rects = new LinkedList<IRectangle>();
		for(Pair p : aux_list)
			aux_rects.add(p.r);
		
		int m = 4*(2*RTree.t+1)/100;
		int end = (192*RTree.t +196)/100;
		
		double margenX_0 = this.margen(aux_rects, new RectangleComparatorX(0),m, end);
		double margenY_0 = this.margen(aux_rects, new RectangleComparatorY(0),m, end);
		
		double margenX_1 = this.margen(aux_rects, new RectangleComparatorX(1),m, end);
		double margenY_1 = this.margen(aux_rects, new RectangleComparatorY(1),m, end);
		
		double min_margin0 = Math.min(margenX_0, margenY_0);
		double min_margin1 = Math.min(margenX_1, margenY_1);
		double min_margin = Math.min(min_margin0, min_margin1);
		
		LinkedList<Pair> children;
		if(min_margin==margenX_0 || min_margin==margenX_1)
			children = getNewChildren(aux_list, new PairComparatorX(0),m, end);
		else 
			children = getNewChildren(aux_list, new PairComparatorY(0),m, end);
		
		return children;
	}

	private LinkedList<Pair> getNewChildren(LinkedList<Pair> aux_list, Comparator<Pair> PairComparator,
			int m, int end) {
		Collections.sort(aux_list, PairComparator);
		
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
				IRectangle r = mbrList.get(j).r;
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
		
			for(int j=m-1+i; j<2*RTree.t+1; j++){
				IRectangle r = mbrList.get(j).r;
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
		
		if(hash_min_index.size()==1){
			/* Unica parte que cambia un poco con respecto a InternalNode */
			int key = (int) hash_min_index.keySet().toArray()[0];
			MBR[] vals = hash_min_index.get(key);
			LinkedList<Pair> r = new LinkedList<Pair>();
			for(int i=0; i<m-1+key; i++){
				r.add(aux_list.get(i));
			}
			INode n1 = new InternalNode(RTree.t, false, vals[0],r);
			r = new LinkedList<Pair>();
			for(int i=m-1+key; i<aux_list.size(); i++){
				r.add(aux_list.get(i));
			}
			INode n2 = new InternalNode(RTree.t, false, vals[1],r);
			/*agregar elementos hojas, y luego guardar nuevos nodos en hijos*/
			LinkedList<Pair> ret = new LinkedList<Pair>();
			ret.add(new Pair(vals[0],1));
			ret.add( new Pair(vals[0],1));
			return ret;
		}
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
		
		MBR[] vals = hash_min_index.get(min_area_key);
		LinkedList<Pair> r = new LinkedList<Pair>();
		for(int i=0; i<m-1+min_area_key; i++){
			r.add(aux_list.get(i));
		}
		INode n1 = new InternalNode(RTree.t, false, vals[0],r);
		r = new LinkedList<Pair>();
		for(int i=m-1+min_area_key; i<aux_list.size(); i++){
			r.add(aux_list.get(i));
		}
		INode n2 = new InternalNode(RTree.t, false, vals[1],r);
		/*agregar elementos hojas, y luego guardar nuevos nodos en hijos*/
		LinkedList<Pair> ret = new LinkedList<Pair>();
		ret.add(new Pair(vals[0],1));
		ret.add( new Pair(vals[0],1));
		return ret;
	}

	/**
	 * Entrega el Nodo contenido en el MBR con el menor aumento de overlap, 
	 * si hay empate se sigue con lo que dice el enunciado
	 * @param r Rectangulo que se va a agregar
	 * @return Nodo donde se va a insertar
	 */
	private INode obtainInsertNode(MyRectangle r){
		if(!childIsLeaf){
			/* Se carga el hijo en un nodo interno
			   en verdad children va a guardar las posiciones de los hijos en 
			   el archivo, por ahora dejemoslo como INode*/
			return this.getNodeMinAreaChange(r); 
		}
		
		return obtainFromMinOverlap(r);
	}

	/**
	 * Entrega el nodo donde se va a insertar, de acuerdo al minimo
	 * overlap 
	 * @param r rectangulo a insertar
	 * @return nodo donde se insertara
	 */
	private INode obtainFromMinOverlap(MyRectangle r) {
		ArrayList<Integer> minMBROverlapIndex = new ArrayList<Integer>();
		double minOverlapChange = Double.MAX_VALUE;
		for(int i=0 ; i<keyNumber ; i++){
			double change = this.getOverlapChange(i, r);
			if(change<minOverlapChange){
				minOverlapChange = change;
				minMBROverlapIndex = new ArrayList<Integer>(i);
			}
			else if(change == minOverlapChange){
				minMBROverlapIndex.add(i);
			}
		}
		
		if(minMBROverlapIndex.size()==1){
			return children[minMBROverlapIndex.get(0)];
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
			overlap += me.intersectionArea(other);
		}
		double[] r_x = r.getX();
		double[] r_y = r.getY();
		double[] aux_x = me.getX();
		double[] aux_y =  me.getY();
		
		if(r_x[0]<aux_x[0])
			aux_x[0] = r_x[0];
		if(r_x[1]>aux_x[1])
			aux_x[1] = r_x[1];
		if(r_y[0]<aux_y[0])
			aux_y[0] = r_y[0];
		if(r_y[1]>aux_y[1])
			aux_y[1] = r_y[1];
		
		IRectangle aux_rect = new MBR(aux_x,aux_y);
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
	 */
	private INode getNodeMinAreaChange(MyRectangle r){
		double minAreaChange = Double.MAX_VALUE;
		MBR rect;
		ArrayList<Integer> minMBRIndex = new ArrayList<Integer>();
		for(int i =0; i< keyNumber; i++){
			rect = mbrList.get(i).r;
			double change = rect.getAreaChange(r);
			if(change<minAreaChange){
				minAreaChange=change;
				minMBRIndex = new ArrayList<Integer>(i);
			}
			else if(change == minAreaChange){
				minMBRIndex.add(i);
			}
		}
		if(minMBRIndex.size()==1)
			return children[minMBRIndex.get(0)];
		
		double minArea = Double.MAX_VALUE;
		int minAreaIndex = -1;
		for(int i = 0; i<minMBRIndex.size(); i++){
			rect = mbrList.get(minMBRIndex.get(i)).r;
			double area = rect.getArea();
			if(minArea<area){
				minArea=area;
				minAreaIndex=i;
			}
		}
		return children[minAreaIndex];
	}

	/**
	 * Busca un rectangulo en el nodo
	 */
	@Override
	public boolean buscar(MyRectangle r) {
		boolean res=false;
		MBR rect;
		for(int i = 0; i<keyNumber; i++){
			rect = mbrList.get(i).r;
			if(!rect.contains(r))
				continue;
			if(res)
				return true;
			res = children[i].buscar(r);
		}
		return res;
	}

	@Override
	public byte[] writeBuffer() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
