package vectors;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;


public class PathVector extends GenericVector {

	public PathVector(double x, double y, Color c) {
		foreground = c;
		addNode(x,y);
	}

	private Polyline2D path() {
		Polyline2D p = new Polyline2D();
		for(Node n : nodes)
			p.addPoint((int)n.getX(),(int)n.getY());
		return p;
	}
	
	public void addNode(double x, double y){
		Point2D p = new Point2D.Double(x,y);
		addNode(p);
	}
	
	public void addNode(Point2D p){
		nodes.add(new Node(p));
	}

	@Override
	public void drawVector(Graphics2D g2d, AffineTransform at) {
		g2d.setColor(foreground);
		g2d.setStroke(stroke);
		g2d.draw(at.createTransformedShape(path()));

	}
	@Override
	public void drawSelected(Graphics2D g2d, AffineTransform at) {
		for (Node n : nodes)
			n.drawNode(g2d,at);
	}
	
	
	@Override
	public Rectangle getBounds() {
		return path().getBounds();
	}



}
