package gui;

import generator.RectangleGenerator;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JFrame;

import rectangles.MyRectangle;
import trees.RTree;

public class GUI{
	
	public static void draw(RTree t) throws IOException {
		JFrame f = new JFrame("Draw");
		f.setSize(700, 700);
		
		LinkedList<DrawRectangle> r = new LinkedList<DrawRectangle>(); 
		t.draw(r);
		f.add(new RectComponent(r),BorderLayout.CENTER);
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
	
	public static void main(String[] args) throws IOException {
		RTree tree = new RTree(2);
		int n = 5;
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
	
}
