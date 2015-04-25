package main;

import generator.RectangleGenerator;
import gui.GUI;

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
		testGUIReinsert();
	}
	
	public static void testDistancia(){
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
	
	public static void testInsertar() throws IOException{
		int t = 10;
		RTree tree = new RTree(t);
		int i=1;
		ArrayList<MyRectangle> rects = RectangleGenerator.generateRandom(1000);
		for(MyRectangle r : rects){
			System.out.println("insertando n°: "+i);
			tree.insertar(r);
			i++;
		}
		System.out.println("Terminé!");
	}
	
	public static void testGUI1() throws IOException{
		RTree tree = new RTree(2);
		int n = 10;
		ArrayList<MyRectangle> rects = RectangleGenerator.generateRandom(n);
		for(int i=0; i<n; i++){
			MyRectangle r = rects.get(i);
			tree.insertar(r);
			//System.out.println("Se inserta: "+r);
			//System.out.println("En arbol: "+tree.root);
			if(i == n-2){
				GUI.draw(tree);
			}
		}
		//System.out.println(tree.root.isLeaf());
		GUI.draw(tree);
	}
	
	public static void testGUIReinsert() throws IOException{
		RTree tree = new RTree(2);
		int n = 5;
		ArrayList<MyRectangle> rects = RectangleGenerator.generateRandom(n);
		for(int i=0; i<n; i++){
			MyRectangle r = rects.get(i);
			tree.insertWithReinsert(r);
			//System.out.println("Se inserta: "+r);
			//System.out.println("En arbol: "+tree.root);
			if(i == n-2){
				GUI.draw(tree);
			}
		}
		//System.out.println(tree.root.isLeaf());
		GUI.draw(tree);
	}
}
