package main;

import generator.RectangleGenerator;

import java.io.IOException;
import java.util.ArrayList;

import rectangles.MyRectangle;
import trees.RTree;

public class Main {

	
	public static void main(String[] args) throws IOException {
		int t = 10;
		RTree tree = new RTree(t);
		int i=1;
		ArrayList<MyRectangle> rects = RectangleGenerator.generateRandom(1000);
		for(MyRectangle r : rects){
			System.out.println("insrtando n°: "+i);
			tree.insertar(r);
			i++;
		}
		System.out.println("Terminé!");
		
	}
}
