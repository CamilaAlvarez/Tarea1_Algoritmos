package utils;

import rectangles.MBR;

public class Pair {
	public MBR r;
	public long childPos;
	
	public Pair(MBR r, long childPos){
		this.r = r;
		this.childPos = childPos;
	}
	
}
