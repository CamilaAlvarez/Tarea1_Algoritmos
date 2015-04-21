package utils;

import nodes.INode;

public class DeletionPair {
	public INode node;
	public long childPos;
	
	public DeletionPair(INode node, long childPos){
		this.node = node;
		this.childPos = childPos;
	}
}
