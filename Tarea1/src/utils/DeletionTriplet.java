package utils;

import nodes.INode;

public class DeletionTriplet {
	public INode node;
	public long myPos;
	public int nodeHeight;
	
	public DeletionTriplet(INode node, long myPos, int height){
		this.node = node;
		this.myPos = myPos;
		this.nodeHeight = height;
	}
}
