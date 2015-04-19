package memory;

import java.io.IOException;
import java.util.LinkedList;

import nodes.INode;

public class PMManager implements IMemoryManager{
	
	private LinkedList<INode> nodes;
	
	public PMManager(){
		nodes = new LinkedList<INode>();
	}

	
	public INode loadNode(long filePosition) throws IOException {
		return nodes.get((int) filePosition);
	}

	
	public void saveNode(INode n) throws IOException {
		long pos = n.getPosition();
		nodes.remove((int) pos);
		nodes.add((int) pos, n);
		
	}


	@Override
	public long getPosition() {
		return nodes.size();
	}

}
