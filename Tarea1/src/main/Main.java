package main;

import generator.RectangleGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import Point.Point;

import rectangles.DistanceComparator;
import rectangles.MyRectangle;
import trees.RTree;

public class Main {

	
	public static void main(String[] args) throws IOException {
//		int t = 10;
//		RTree tree = new RTree(t);
//		int i=1;
//		ArrayList<MyRectangle> rects = RectangleGenerator.generateRandom(1000);
//		for(MyRectangle r : rects){
//			System.out.println("insertando n°: "+i);
//			tree.insertar(r);
//			i++;
//		}
//		System.out.println("Terminé!");
		
		Point c = new Point(0, 0);
		LinkedList<MyRectangle> r = new LinkedList<MyRectangle>();
		DistanceComparator comp = new DistanceComparator(c);
		for(int i = 9; i>0; i--){
			double[] x = {i, i+1};
			double[] y = {i, i+1};
			r.add(new MyRectangle(x,y));
		}
		
		Collections.sort(r,comp);
		for(MyRectangle mr : r){
			System.out.println(mr.toString());
		}
		
	}
}
