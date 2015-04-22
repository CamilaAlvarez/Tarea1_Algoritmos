package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import javax.swing.JPanel;

public class RectComponent extends JPanel{
	
	private LinkedList<DrawRectangle> r;
	private LinkedList<Color> color;
	
	public RectComponent(LinkedList<DrawRectangle> r){
		this.r=r;
		this.color = new LinkedList<Color>();
		color.add(Color.GREEN);
		color.add(Color.RED);
		color.add(Color.BLUE);
		color.add(Color.BLACK);
		color.add(Color.ORANGE);
		color.add(Color.MAGENTA);
	}

	@Override
    protected void paintComponent(Graphics g) {
		 Graphics2D g2 = (Graphics2D) g;
		 for(DrawRectangle rect: r){
			 g2.setColor(color.get(rect.c)); 
			 double x = Math.min(rect.x0, rect.x1);
	         double y = Math.min(rect.y0, rect.y1);
	         double h = Math.abs(rect.y1-rect.y0);
	         double w = Math.abs(rect.x1-rect.x0);
	         g2.draw(new Rectangle2D.Double(x, y, w, h));
		 }
         //g2.draw(new Rectangle2D.Double(100, 350, 100, 250));
	}
}
