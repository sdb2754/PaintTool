package events;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import layers.Layer;
import layers.LayerAnnotation;
import layers.LayerRaster;
import layers.LayerVector;

public class Canvas extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
	private static final long serialVersionUID = 1L;
	public static int x_dim;
	public static int y_dim;

	// drawing stuff
	private BufferedImage cleanse;

	// Layers
	ArrayList<Layer> layers = new ArrayList<Layer>();
	Layer active;
	LayerAnnotation annotations;
	AffineTransform at;

	// Tools
	Tools tools;

	// Canvas Properties
	private double magnification = 1;
	private String name;

	// Paint
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(cleanse, active.transform(), null);
		drawLayers(g2);
		annotations.draw(g2);
	}

	private void drawLayers(Graphics2D g) {
		for (Layer l : layers) {
			l.draw(g);
		}
	}

	// Constructor
	public Canvas(int x, int y) {
		x_dim = x;
		y_dim = y;
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		at = new AffineTransform();
		annotations = new LayerAnnotation(x, y, at);
		addLayer("Raster");
		addLayer("Vector");
		tools = new Tools(annotations);
		setActive(0);
		setCanvasSize(x, y);
		layers.get(0).setLayerColor(Color.WHITE);
		centerCanvas();
	}

	private void addLayer(String t) {
		if(t=="Vector")
			layers.add(new LayerVector(x_dim,y_dim,at));
		if(t=="Raster")
			layers.add(new LayerRaster(x_dim,y_dim,at));
		Main.arrangeLayerList();
	}

	// public set methods for canvas properties
	public void setZoom() {
		try {
			int zoom = Integer.parseInt(JOptionPane.showInputDialog(Main.frame, "Enter zoom: ", null));
			setCanvasMagnification(zoom);
		} catch (RuntimeException e) {
		}
	}

	public void addLayer() {
		Object[] possibilities = { "Vector", "Raster" };
		String s = (String) JOptionPane.showInputDialog(Main.frame, "What Type of Layer Would you Like to add?:\n",
				"Select Origin", JOptionPane.PLAIN_MESSAGE, null, possibilities, "move");
		addLayer(s);
	}

	public void setSize() {
		try {
			int w = Integer.parseInt(JOptionPane.showInputDialog(Main.frame, "Enter canvas width: ", null));
			int h = Integer.parseInt(JOptionPane.showInputDialog(Main.frame, "Enter canvas height: ", null));
			setCanvasSize(w, h);
		} catch (RuntimeException e) {
		}
	}

	// set methods for canvas properties
	private void setCanvasSize(int x, int y) {
		// Must be first since methods are called that reference them
		x_dim = x;
		y_dim = y;
		// change size of drawing components
		cleanse = new BufferedImage(x, y, 2);
		// change size of layers
		for (Layer l : layers)
			l.setLayerSize(x, y);
		// change size of annotation layer
		annotations.setLayerSize(x, y);
	}

	private void setCanvasLocation(int x, int y) {
		shiftCanvasLocation(x - (int) at.getTranslateX(), y - (int) at.getTranslateY());
	}

	public void centerCanvas() {
		setCanvasLocation((Main.frame.getWidth() - (int) active.getLayerBounds().getWidth()) / 2,
				(Main.frame.getHeight() - (int) active.getLayerBounds().getHeight()) / 2);
	}

	private void shiftCanvasLocation(int dx, int dy) {
		double mag = magnification;
		setCanvasMagnification(1);
		at.translate(dx, dy);
		setCanvasMagnification(mag);
	}

	private void setCanvasMagnification(double m) {
		if (m > 0)
			magnification = m;
		else
			magnification = 0.01;
		at.scale(magnification * 1 / at.getScaleX(), magnification * 1 / at.getScaleY());
	}

	private void adjustCanvasMagnification(double dm) {
		setCanvasMagnification(magnification + dm);
	}

	public void setCanvasBackground(Color c) {
		tools.background = c;
	}

	public void setCanvasForeground(Color c) {
		tools.foreground = c;
	}

	public void setActive(int i) {
		if (i < layers.size()) {
			active = layers.get(i);
			tools.setTargetLayer(active);
		}
	}

	// functional methods

	// Coordinate System Transformss
	public Point2D getCanvasPoint(MouseEvent e) {
		Point p = new Point(e.getX(), e.getY());
		return getCanvasPoint(p);
	}

	public Point2D getCanvasPoint(Point2D p) {
		try {
			return at.inverseTransform(p, null);
		} catch (NoninvertibleTransformException e1) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	public Point2D getFramePoint(Point2D p) {
		return at.transform(p, null);
	}

	public String getName() {
		return name;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Point2D p = getCanvasPoint(e);
		tools.mouseDragged(p, e);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point2D p = getCanvasPoint(e);
		tools.mouseDown(p, e);
	}

	@Override
	public void mouseReleased(MouseEvent r) {
		tools.mouseUp();

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

		if ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
			Point2D pc = getCanvasPoint(e);
			adjustCanvasMagnification(-e.getPreciseWheelRotation() / 10);
			Point2D pf = getFramePoint(pc);
			shiftCanvasLocation(e.getX() - (int) pf.getX(), e.getY() - (int) pf.getY());
		} else if ((e.getModifiers() & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK) {
			shiftCanvasLocation(-10 * e.getWheelRotation(), 0);
		} else {
			shiftCanvasLocation(0, -10 * e.getWheelRotation());
		}

	}

}
