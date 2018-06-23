package vectors;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;


public class LineVector extends GenericVector {

	public LineVector(double x, double y, Color c) {
		foreground = c;
		nodes.add(new Node(x, y));
		nodes.add(new Node(x, y));
	}

	private Line2D line() {
		return new Line2D.Double(nodes.get(0).getX(), nodes.get(0).getY(), nodes.get(1).getX(), nodes.get(1).getY());
	}

	@Override
	public void drawVector(Graphics2D g2d, AffineTransform at) {
		g2d.setColor(foreground);
		g2d.setStroke(stroke);
		g2d.draw(at.createTransformedShape(line()));

	}
	@Override
	public void drawSelected(Graphics2D g2d, AffineTransform at) {
		for (Node n : nodes)
			n.drawNode(g2d,at);
	}
	
	
	@Override
	public Rectangle getBounds() {
		return line().getBounds();
	}



}
