package layers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class LayerAnnotation extends Layer {

	ArrayList<Path2D> marquee = new ArrayList<Path2D>();

	public LayerAnnotation(int x, int y, AffineTransform at) {
		super(x, y, at);
		clearLayer();
	}

	public void addMarquee(int x, int y) {
		Rectangle r = new Rectangle(x, y, 1, 1);
		marquee.add(new Path2D.Double(r));
	}

	public void sizeMarquee(int x, int y) {
		Polygon p = new Polygon();
		Rectangle r = marquee.get(marquee.size() - 1).getBounds();
		p.addPoint(r.x, r.y);
		p.addPoint(x, r.y);
		p.addPoint(x, y);
		p.addPoint(r.x, y);
		marquee.set(marquee.size() - 1, new Path2D.Double(p));
	}

	private void drawAnnotations(Graphics2D g) {
		g.setColor(Color.RED);
		for (Path2D m : marquee) {
			g.draw(m.createTransformedShape(transform));
		}
	}
	
	public void dP(int x, int y, Color c) {
		if (x < x_dim && y < y_dim && x>=0 && y>=0)
			image.setRGB(x, y, c.getRGB());
	}
	public void dP(Point2D p, Color c){
		dP((int)p.getX(),(int)p.getY(),c);
	}
	
	public Color cP(int x, int y){
		if (x < x_dim && y < y_dim && x>=0 && y>=0)
			return new Color(image.getRGB(x, y));
		return null;
	}
	public Color cP(double x, double y){
		return cP((int)x,(int)y);
	}

	@Override
	public void draw(Graphics2D g) {
		g.drawImage(image, transform, null);
		drawAnnotations(g);
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void redo() {
		// TODO Auto-generated method stub
		
	}

}
