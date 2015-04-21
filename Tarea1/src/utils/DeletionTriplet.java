package utils;

import nodes.INode;

public class DeletionTriplet {
	public INode node;
	public long childPos;
	public int nodeHeight;
	
	public DeletionTriplet(INode node, long childPos, int height){
		this.node = node;
		this.childPos = childPos;
		this.nodeHeight = height;
	}
}
