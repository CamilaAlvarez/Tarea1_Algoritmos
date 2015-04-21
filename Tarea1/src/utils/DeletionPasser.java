package utils;

import java.util.LinkedList;



public class DeletionPasser {
	public LinkedList<DeletionPair> deletedNodes = new LinkedList<DeletionPair>();
	public Pair newMBRPair;
	
	public void setMBRPair(Pair newMBR){
		this.newMBRPair=newMBR;
	}
}
