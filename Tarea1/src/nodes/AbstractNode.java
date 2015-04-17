package nodes;

import rectangles.MBR;


public abstract class AbstractNode implements INode{
	protected int keyNumber;
	protected String fileName;
	protected boolean isRoot;
	protected int maxChildNumber;
	protected MBR parentMBR;
	
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
	
	/**
	 * Define si un nodo es raiz
	 */
	public boolean isRoot(){
		return isRoot;
	}
	

}
