package nodes;

import java.util.ArrayList;
import java.util.LinkedList;

import rectangles.IRectangle;
import rectangles.MBR;
import rectangles.MyRectangle;
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
	private LinkedList<Pair> mbrList= new LinkedList<Pair>();
	
	
	public InternalNode(int t, boolean isRoot){
		maxChildNumber = 2*t;
		this.isRoot = isRoot;
		this.parentMBR = null;
	}
	
	public InternalNode(int t, boolean isRoot, MBR mbr){
		maxChildNumber = 2*t;
		this.isRoot = isRoot;
		this.parentMBR = mbr;
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
		if(keyNumber==maxChildNumber){
			return this.split(r,t);
			
		}
		/* se remueve el mbr anterior */
		for(Pair p : mbrList)
			if(p.r.equals(aux.r))
				mbrList.remove(p);
		/* se guardar los dos mbr nuevos */
		mbrList.add(aux.p1);
		mbrList.add(aux.p2);
		keyNumber++;
		return null;
	}
	
	/**
	 * Método encargado de hacer split de un nodo
	 * @param r rectangulo a agregar
	 * @param t arbol al que pertenece el nodo
	 * @return RectangleContainer con el rectangulo que debe subir, y
	 * la posicion del hijo que se debe guardar
	 */
	private RectangleContainer split(MyRectangle r, RTree t) {
		if(this.isRoot)
			return null;
		return null;
		// TODO Auto-generated method stub
		
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
