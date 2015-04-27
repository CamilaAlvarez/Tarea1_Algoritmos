package utils;

import rectangles.MBR;

public class Pair {
	public MBR r;
	public long childPos;
	
	public Pair(MBR r, long childPos){
		this.r = r;
		this.childPos = childPos;
	}
	
	@Override
	public boolean equals(Object r){
		if (!(r instanceof Pair))
            return false;
		
		Pair rect = (Pair)r;
		boolean res = true;
		res = res & rect.r.equals(this.r);
		res = res & rect.childPos==this.childPos;
		return res;
	}
	
}


