package events;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class LayerAnnotation extends Layer {

	ArrayList<Path2D> marquee = new ArrayList<Path2D>();

	public LayerAnnotation(int x, int y, AffineTransform at) {
		super(x, y, at);
		setLayerColor(new Color(0, 0, 0, 0));
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

	@Override
	public void draw(Graphics2D g) {
		g.drawImage(image, transform, null);
		drawAnnotations(g);
	}

}
