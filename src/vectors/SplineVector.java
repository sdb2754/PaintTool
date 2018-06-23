package vectors;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;


public class SplineVector extends GenericVector {

	public SplineVector(double x, double y, Color c) {
		foreground = c;
		addNode(x,y);
	}
	
	public void addNode(double x, double y){
		Point2D p = new Point2D.Double(x,y);
		addNode(p);
	}
	public void addNode(Point2D p){
		if(nodes.size()==4)
			return;
		nodes.add(new Node(p));
	}

	private CubicCurve2D spline() {
		if(nodes.size()==4)
			return new CubicCurve2D.Double(nodes.get(0).getX(), nodes.get(0).getY(), nodes.get(1).getX(),
					nodes.get(1).getY(), nodes.get(2).getX(), nodes.get(2).getY(), nodes.get(3).getX(),
					nodes.get(3).getY());
		return null;
	}
	

	@Override
	public void drawVector(Graphics2D g2d, AffineTransform at) {
		g2d.setColor(foreground);
		g2d.setStroke(stroke);
		if(nodes.size()==4)
			g2d.draw(at.createTransformedShape(spline()));

	}
	@Override
	public void drawSelected(Graphics2D g2d, AffineTransform at) {
		g2d.setColor(Color.lightGray);
		Polyline2D pl = new Polyline2D();
		for(Node n : nodes)
			pl.addPoint(n);
		if(nodes.size()>1)
			for (int i = 1; i < nodes.size(); i++) {
				g2d.draw(at.createTransformedShape(pl));
			}
		for (Node n : nodes)
			n.drawNode(g2d,at);
	}
	
	
	@Override
	public Rectangle getBounds() {
		return spline().getBounds();
	}



}
