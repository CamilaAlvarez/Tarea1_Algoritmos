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
		
		MBR mbr = new rectangles.MBR(x,y);
		
		if(isLeaf){
			LinkedList<IRectangle> r = new LinkedList<IRectangle>();
			Leaf node = new Leaf(keyNumber, isRoot, mbr, filePos, r);
			return node;
		}
		else{
			LinkedList<Pair> p = new LinkedList<Pair>();
			InternalNode node = new InternalNode(keyNumber, isRoot, mbr, filePos, p, childIsLeaf);
			return node;
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		double[] x = {1.23 , 1.32};
		double[] y = {1.78 , 10};
		LinkedList<IRectangle> r = new LinkedList<IRectangle>();
		INode l = new Leaf(10, false, new MBR(x,y), 3000, r);
		byte[] data = new byte[4096];
		l.writeBuffer(data);
		
		SMManager manager = new SMManager(4096, 10);		
		manager.writeToFile(data, l.getPosition());
		
		manager.readFromFile(data, l.getPosition());
		INode l2 = Utils.loadNode(data);
		System.out.println(l.equals(l2));
		//---------------------------------------------
		data = new byte[4096];
		x[1] = 63.12;
		y[0] = 23.98;
		l = new Leaf(21, true, new MBR(x,y), 200, r);
		l.writeBuffer(data);
		l2 = Utils.loadNode(data);
		System.out.println(l.equals(l2));
	}
	
	public static MBR toMBR(IRectangle r){
		return new MBR(r.getX(),r.getY());
	}
	
	public static MyRectangle toMyRectangle(IRectangle r){
		return new MyRectangle(r.getX(),r.getY());
	}

}
