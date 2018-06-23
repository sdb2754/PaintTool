package vectors;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class Node extends Point2D.Double {
	public Node(double x, double y) {
		this.setLocation(x, y);
	}

	public Node(Point2D p) {
		this.setLocation(p);
	}

	public void drawNode(Graphics2D g2d, AffineTransform at,boolean selected) {
		if(selected)
			g2d.setColor(Color.RED);
		else
			g2d.setColor(Color.GRAY);
		Shape r = new Rectangle((int)at.transform(this, null).getX()-4, (int)at.transform(this, null).getY()  - 4, 8, 8);
		g2d.draw(r);
	}
	public void drawNode(Graphics2D g2d, AffineTransform at){
		drawNode(g2d,at,false);
	}
	
	
}