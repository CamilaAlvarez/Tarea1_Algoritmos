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
		if(pos==nodes.size()){
			nodes.add(n);
		}
		else{
			nodes.remove((int) pos);
			nodes.add((int) pos, n);
		}		
	}


	@Override
	public long getNewPosition() {
		return nodes.size();
	}


	@Override
	public int getVisitados() {
		// TODO Auto-generated method stub
		return 0;
	}

}
