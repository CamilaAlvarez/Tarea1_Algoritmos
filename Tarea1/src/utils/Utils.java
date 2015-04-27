package utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import memory.SMManager;
import nodes.INode;
import nodes.InternalNode;
import nodes.Leaf;
import rectangles.IRectangle;
import rectangles.MBR;
import rectangles.MyRectangle;

public class Utils {
	public static INode loadNode(byte[] data){
		int pos = 0;
		int keyNumber = ByteBuffer.wrap(data, pos, 4).getInt();
		pos += 4;
		byte root = ByteBuffer.wrap(data, pos, 1).get();
		pos += 1;
		byte leaf = ByteBuffer.wrap(data, pos, 1).get();
		pos += 1;
		byte childLeaf = ByteBuffer.wrap(data, pos, 1).get();
		pos += 1;
		long filePos = ByteBuffer.wrap(data, pos, 8).getLong();
		pos += 8;
		
		boolean isRoot = root==(byte)1?true:false;
		boolean isLeaf = leaf==(byte)1?true:false;
		boolean childIsLeaf = childLeaf==(byte)1?true:false;
		
		double x1 = ByteBuffer.wrap(data, pos, 8).getDouble();
		pos += 8;
		double x2 = ByteBuffer.wrap(data, pos, 8).getDouble();
		pos += 8;
		double y1 = ByteBuffer.wrap(data, pos, 8).getDouble();
		pos += 8;
		double y2 = ByteBuffer.wrap(data, pos, 8).getDouble();
		pos += 8;
		
		double[] x = {x1 ,x2};
		double[] y = {y1 ,y2};
		
		MBR mbr = new MBR(x,y);
		
		if(isLeaf){
			LinkedList<IRectangle> r = getLeafCont(pos, keyNumber, data);
			Leaf node = new Leaf(keyNumber, isRoot, mbr, filePos, r);
			return node;
		}
		else{
			LinkedList<Pair> p = getInternalCont(pos, keyNumber, data);
			InternalNode node = new InternalNode(keyNumber, isRoot, mbr, filePos, p, childIsLeaf);
			return node;
		}
		
	}
	
	private static LinkedList<IRectangle> getLeafCont(int pos, int keyNumber, byte[] data){
		LinkedList<IRectangle> rects = new LinkedList<IRectangle>();
		for(int i=0; i<keyNumber; i++){
			double x1 = ByteBuffer.wrap(data, pos, 8).getDouble();
			pos += 8;
			double x2 = ByteBuffer.wrap(data, pos, 8).getDouble();
			pos += 8;
			double y1 = ByteBuffer.wrap(data, pos, 8).getDouble();
			pos += 8;
			double y2 = ByteBuffer.wrap(data, pos, 8).getDouble();
			pos += 8;
			
			double[] x = {x1 ,x2};
			double[] y = {y1 ,y2};
			
			IRectangle r = new MyRectangle(x, y);
			rects.add(r);
		}
		return rects;
	}
	
	private static LinkedList<Pair> getInternalCont(int pos, int keyNumber, byte[] data){
		LinkedList<Pair> pairs = new LinkedList<Pair>();
		for(int i=0; i<keyNumber; i++){
			double x1 = ByteBuffer.wrap(data, pos, 8).getDouble();
			pos += 8;
			double x2 = ByteBuffer.wrap(data, pos, 8).getDouble();
			pos += 8;
			double y1 = ByteBuffer.wrap(data, pos, 8).getDouble();
			pos += 8;
			double y2 = ByteBuffer.wrap(data, pos, 8).getDouble();
			pos += 8;
			
			double[] x = {x1 ,x2};
			double[] y = {y1 ,y2};
			
			MBR r = new MBR(x, y);
			long filePos = ByteBuffer.wrap(data, pos, 8).getLong();
			pos += 8;
			
			Pair pair = new Pair(r, filePos);
			pairs.add(pair);
		}
		return pairs;
	}
	
	public static void main(String[] args) throws IOException {
		double[] x = {1.23 , 1.32};
		double[] y = {1.78 , 10};
		LinkedList<IRectangle> r = new LinkedList<IRectangle>();
		INode l = new Leaf(r.size(), false, new MBR(x,y), 3000, r);
		byte[] data = new byte[4096];
		l.writeBuffer(data);
		
		SMManager manager = new SMManager(4096, 10);		
		manager.writeToFile(data, l.getPosition());
		
		manager.readFromFile(data, l.getPosition());
		INode l2 = Utils.loadNode(data);
		//System.out.println(l.equals(l2));
		//---------------------------------------------
		data = new byte[4096];
		r = new LinkedList<IRectangle>();
		for(int i = 0; i<10;i++){
			double[] x1 = {1+i ,1+i};
			double[] y1 = {1+i ,1+i};
			r.add(new MyRectangle(x1, y1));			
		}
		l = new Leaf(r.size(), true, null, 200, r);
		l.setParentMBR(l.getNewMBR().r);
		l.writeBuffer(data);
		l2 = Utils.loadNode(data);
		System.out.println(l.equals(l));
		System.out.println(l.equals(l2));
		//---------------------------------------------
		data = new byte[4096];
		LinkedList<Pair> pairs = new LinkedList<Pair>();
		for(int i = 0; i<10;i++){
			double[] x1 = {1+i ,1+i};
			double[] y1 = {1+i ,1+i};
			pairs.add(new Pair(new MBR(x1, y1), i));			
		}
		l = new InternalNode(pairs.size(), true, null, 100, pairs, true);
		l.setParentMBR(l.getNewMBR().r);
		l.writeBuffer(data);
		l2 = Utils.loadNode(data);
		System.out.println(l.equals(l));
		System.out.println(l.equals(l2));
		
	}
	
	public static MBR toMBR(IRectangle r){
		return new MBR(r.getX(),r.getY());
	}
	
	public static MyRectangle toMyRectangle(IRectangle r){
		return new MyRectangle(r.getX(),r.getY());
	}

}
