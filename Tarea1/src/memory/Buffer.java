package memory;

import nodes.INode;
import memory.PriorityList;

public class Buffer {
	private INode[] loaded;
	private PriorityList priority;
	private boolean[] modify;
	private long[] dirBuffer;
	private int bufferSize;
	private int used;
	
	public Buffer(int bufferSize) {
		this.loaded = new INode[bufferSize];
		this.priority = new PriorityList(bufferSize);
		this.modify = new boolean[bufferSize];
		this.dirBuffer = new long[bufferSize];
		this.bufferSize = bufferSize;
		this.used = 0;
	}
	
	public INode getNode(int pos){
		return loaded[pos];
	}
	
	public boolean wasModified(int pos){
		return modify[pos];
	}
	
	public void addNode(INode n, int pos, boolean val){
		if(used<bufferSize) used++;
		modify[pos]=val;
		dirBuffer[pos]=n.getPosition();
		loaded[pos]=n;
	}
	
	public int getBufferPos(){
		return used;
	}
	
	public boolean isFull(){
		return used==bufferSize;
	}
	
	public boolean updateNode(INode n){
		for (int i = 0; i < bufferSize; i++) {
			if(dirBuffer[i] == n.getPosition()){
				loaded[i] = n;
				priority.improvePriority(i);
				modify[i] = true;
				return true;
			}		
		}
		return false;
		
	}
	
	public int getLastPriority(){
		return priority.getLastPriority();
	}

	public long getFilePosition(int toFree) {
		return dirBuffer[toFree];
	}

	public INode findNode(long pos) {
		for (int i = 0; i < bufferSize; i++) {
			if(dirBuffer[i] == pos){
				priority.improvePriority(i);
				return loaded[i];
			}		
		}
		return null;
	}

}
