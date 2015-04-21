package utils;

import java.util.LinkedList;



public class DeletionPasser {
	public LinkedList<DeletionTriplet> deletedNodes = new LinkedList<DeletionTriplet>();
	public Pair newMBRPair=null;
	public boolean needToRemove=false; 
	
	public void setMBRPair(Pair newMBR){
		this.newMBRPair=newMBR;
	}
}
