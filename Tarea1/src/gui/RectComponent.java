package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import javax.swing.JPanel;

public class RectComponent extends JPanel{
	
	private LinkedList<DrawRectangle> r;
	
	public RectComponent(LinkedList<DrawRectangle> r){
		this.r=r;
	}

	@Override
    protected void paintComponent(Graphics g) {
		 Graphics2D g2 = (Graphics2D) g;
		 for(DrawRectangle rect: r){
			 g2.setColor(rect.c); 
			 double x = Math.min(rect.x0, rect.x1);
	         double y = Math.min(rect.y0, rect.y1);
	         double h = Math.abs(rect.y1-rect.y0);
	         double w = Math.abs(rect.x1-rect.x0);
	         g2.draw(new Rectangle2D.Double(x, y, w, h));
		 }
         //g2.draw(new Rectangle2D.Double(100, 350, 100, 250));
	}
}
