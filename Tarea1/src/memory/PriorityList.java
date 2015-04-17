package memory;

import java.util.LinkedList;

public class PriorityList {
private LinkedList<Integer> list;
	
	public PriorityList(int size) {
		list = new LinkedList<Integer>();
		for (int i = size-1; i >= 0; i--) {
			list.add(i);
		}
	}
	
	public int getLastPriority(){
		int res =  list.getLast();
		list.remove((Integer) res); 
		list.addFirst(res);
		return res;
	}
	
	public void improvePriority(int elem){
		list.remove((Integer)elem);
		list.addFirst(elem);
	}

}
