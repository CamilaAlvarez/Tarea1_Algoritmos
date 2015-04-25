package gui;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.JFrame;

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
	
}
