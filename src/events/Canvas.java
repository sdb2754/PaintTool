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

public class Canvas extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
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
	
	//Tools
	ToolRaster rastertools;

	// Canvas Properties
	private double magnification = 1;
	private String name;

	// Paint
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

		at = new AffineTransform();
		setCanvasSize(x, y);
		layers.add(new LayerRaster(x, y, at));
		active = layers.get(0);
		annotations = new LayerAnnotation(x, y, at);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
		centerCanvas();
		rastertools = new ToolRaster(active,annotations);
	}

	// public set methods for canvas properties
	public void setZoom() {
		try {
			int zoom = Integer.parseInt(JOptionPane.showInputDialog(Main.frame, "Enter zoom: ", null));
			setCanvasMagnification(zoom);
		} catch (RuntimeException e) {
		}
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
		rastertools.background = c;
	}

	public void setCanvasForeground(Color c) {
		rastertools.foreground = c;
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

	public Point2D getAnnotationPoint(MouseEvent e) {
		Point p = new Point(e.getX(), e.getY());
		return getAnnotationPoint(p);
	}

	public Point2D getAnnotationPoint(Point2D e) {

		return new Point((int) (e.getX() - active.getLayerBounds().getX()),
				(int) (e.getY() - active.getLayerBounds().getY()));

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
		rastertools.mouseDragged(p,e);
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
		rastertools.mouseDown(p, e);
	}

	@Override
	public void mouseReleased(MouseEvent r) {
		rastertools.mouseUp();

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

		if ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
			Point2D pc = getCanvasPoint(e);
			adjustCanvasMagnification(-e.getPreciseWheelRotation() / 10);
			Point2D pf = getFramePoint(pc);
			shiftCanvasLocation(e.getX() - (int) pf.getX(), e.getY() - (int) pf.getY());
		}
		else if ((e.getModifiers() & ActionEvent.SHIFT_MASK) ==ActionEvent.SHIFT_MASK) {
			shiftCanvasLocation(-10*e.getWheelRotation(),0);
		}
		else {
			shiftCanvasLocation(0,-10*e.getWheelRotation());
		}

	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
