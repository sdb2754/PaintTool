package events;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import layers.Layer;
import layers.LayerAnnotation;
import layers.LayerRaster;
import layers.LayerVector;
import vectors.GenericVector;
import vectors.LineVector;
import vectors.Node;
import vectors.PathVector;
import vectors.SplineVector;

public class Tools {

	private GenericTool activetool;
	Color foreground;
	Color background;
	private LayerAnnotation a;
	private Layer target;
	String toolname = "Pencil";
	// Load Tools
	RasterPencilTool rpencil;
	RasterBrushTool rbrush;
	RasterSelectTool rselect;
	RasterFillTool rfill;
	RasterLineTool rline;
	RasterSplineTool rspline;
	RasterDropperTool rdropper;

	VectorLineTool vline;
	VectorSelectTool vselect;
	VectorSplineTool vspline;
	VectorPathTool vpath;
	VectorEraserTool veraser;

	public Tools(LayerAnnotation a) {
		foreground = Color.BLACK;
		background = Color.WHITE;
		this.a = a;
		// temporary target layer
		setTargetLayer(new LayerRaster(10, 10, new AffineTransform()));
	}

	public void setTargetLayer(Layer t) {
		target = t;
		if (t instanceof LayerRaster) {
			rpencil = new RasterPencilTool((LayerRaster) t, a);
			rbrush = new RasterBrushTool((LayerRaster) t, a);
			rselect = new RasterSelectTool((LayerRaster) t, a);
			rfill = new RasterFillTool((LayerRaster) t, a);
			rline = new RasterLineTool((LayerRaster) t, a);
			rspline = new RasterSplineTool((LayerRaster) t, a);
			rdropper = new RasterDropperTool((LayerRaster) t, a);
		}
		if (t instanceof LayerVector) {
			vline = new VectorLineTool((LayerVector) t, a);
			vselect = new VectorSelectTool((LayerVector) t, a);
			vspline = new VectorSplineTool((LayerVector) t, a);
			vpath = new VectorPathTool((LayerVector) t, a);
			veraser = new VectorEraserTool((LayerVector) t, a);
		}
		setActiveTool(toolname);
	}

	public void setActiveTool(String t) {
		toolname = t;
		if (target instanceof LayerRaster)
			switch (t) {
			case "Pencil":
				activetool = rpencil;
				break;
			case "Brush":
				activetool = rbrush;
				break;
			case "Select":
				activetool = rselect;
				break;
			case "Fill":
				activetool = rfill;
				break;
			case "Line":
				activetool = rline;
				break;
			case "Spline":
				activetool = rspline;
				break;
			case "Dropper":
				activetool = rdropper;
				break;
			}
		else
			switch (t) {
			case "Line":
				activetool = vline;
				break;
			case "Select":
				activetool = vselect;
				break;
			case "Spline":
				activetool = vspline;
				break;
			case "Pencil":
				activetool = vpath;
				break;
			case "Eraser":
				activetool = veraser;
				break;
			}
		
	}

	public void mouseDown(Point2D p, MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e))
			activetool.mouseDown(p, background, e);
		else
			activetool.mouseDown(p, foreground, e);
	}

	public void mouseDragged(Point2D p, MouseEvent e) {
		activetool.mouseDragged(p, e);
	}

	public void mouseUp() {
		activetool.mouseUp();
	}

	// Tools
	private abstract class GenericTool {
		protected LayerAnnotation annotation;
		protected Color current;
		MouseEvent event;
		protected boolean drawingStroke = false;

		public abstract void mouseDown(Point2D p, Color c, MouseEvent e);

		public abstract void mouseDragged(Point2D p, MouseEvent e);

		public abstract void mouseUp();

		public abstract boolean isRaster();

	}
	// Raster Tools

	private class GenericRasterTool extends GenericTool {
		protected LayerRaster target;

		public GenericRasterTool(LayerRaster t, LayerAnnotation a) {
			target = t;
			annotation = a;
		}

		public void mouseDown(Point2D p, Color c, MouseEvent e) {
			current = c;
			event = e;
			drawingStroke = true;
		}

		public void mouseDragged(Point2D p, MouseEvent e) {
			event = e;
		}

		public void mouseUp() {

		}

		@Override
		public boolean isRaster() {
			return true;
		}
	}

	private class RasterPencilTool extends GenericRasterTool {
		private Point2D last;

		public RasterPencilTool(LayerRaster t, LayerAnnotation a) {
			super(t, a);
		}

		public void mouseDown(Point2D p, Color c, MouseEvent e) {
			super.mouseDown(p, c, e);
			target.dP((int) p.getX(), (int) p.getY(), c);
			target.draw2D().setStroke(new BasicStroke(1));
			last = p;
		}

		public void mouseDragged(Point2D p, MouseEvent e) {
			super.mouseDragged(p, e);
			if (drawingStroke) {
				target.draw().setColor(current);
				target.draw().drawLine((int) last.getX(), (int) last.getY(), (int) p.getX(), (int) p.getY());
				last = p;
			}
		}

		public void mouseUp() {
			super.mouseUp();
			drawingStroke = false;
			target.commit();
		}
	}

	private class RasterBrushTool extends GenericRasterTool {
		private Point2D last;
		private int brush = 5;

		public RasterBrushTool(LayerRaster t, LayerAnnotation a) {
			super(t, a);
		}

		public void mouseDown(Point2D p, Color c, MouseEvent e) {
			super.mouseDown(p, c, e);
			last = p;
			target.draw2D().setStroke(new BasicStroke(brush));
			drawStroke(p);
		}

		public void mouseDragged(Point2D p, MouseEvent e) {
			super.mouseDragged(p, e);
			if (drawingStroke) {
				drawStroke(p);
				last = p;
			}
		}

		public void mouseUp() {
			super.mouseUp();
			drawingStroke = false;
			target.commit();
		}

		private void drawStroke(Point2D p) {
			target.draw2D().setColor(current);
			target.draw().drawLine((int) last.getX(), (int) last.getY(), (int) p.getX(), (int) p.getY());
		}
	}

	private class RasterSelectTool extends GenericRasterTool {
		public RasterSelectTool(LayerRaster t, LayerAnnotation a) {
			super(t, a);
		}
	}

	private class RasterDropperTool extends GenericRasterTool {

		public RasterDropperTool(LayerRaster t, LayerAnnotation a) {
			super(t, a);
		}

		public void mouseDown(Point2D p, Color c, MouseEvent e) {
			super.mouseDown(p, c, e);
			if (SwingUtilities.isRightMouseButton(e))
				Main.easel.active.tools.background = target.cP(p.getX(), p.getY());
			else
				Main.easel.active.tools.foreground = target.cP(p.getX(), p.getY());
		}

	}

	private class RasterFillTool extends GenericRasterTool {
		public boolean doGlobal = false;

		public RasterFillTool(LayerRaster t, LayerAnnotation a) {
			super(t, a);
		}

		public void mouseDown(Point2D p, Color c, MouseEvent e) {
			super.mouseDown(p, c, e);
			if (doGlobal)
				global(p, c, e);
			else
				contiguous(p, c, e);
			target.commit();
		}

		private void global(Point2D p, Color c, MouseEvent e) {
			Color fill = target.cP(p.getX(), p.getY());
			if (c.equals(fill))
				return;
			for (int i = 0; i < target.image().getWidth(); i++)
				for (int j = 0; j < target.image().getHeight(); j++)
					if (fill.equals(target.cP(i, j)))
						target.dP(i, j, c);
		}

		private void contiguous(Point2D p, Color c, MouseEvent e) {
			Color fill = target.cP(p.getX(), p.getY());
			if (c.equals(fill))
				return;
			ArrayList<Point2D> tocheck = new ArrayList<Point2D>();
			tocheck.add(p);
			target.dP(p, c);
			Point2D current;
			Point2D next;
			while (!tocheck.isEmpty()) {
				current = tocheck.get(0);

				// Badly written code
				next = new Point2D.Double(current.getX() + 1, current.getY());
				if (fill.equals((target.cP(next.getX(), next.getY())))) {
					tocheck.add(new Point2D.Double(next.getX(), next.getY()));
					target.dP(next, c);
				}
				next = new Point2D.Double(current.getX() - 1, current.getY());
				if (fill.equals((target.cP(next.getX(), next.getY())))) {
					tocheck.add(new Point2D.Double(next.getX(), next.getY()));
					target.dP(next, c);
				}
				next = new Point2D.Double(current.getX(), current.getY() + 1);
				if (fill.equals((target.cP(next.getX(), next.getY())))) {
					tocheck.add(new Point2D.Double(next.getX(), next.getY()));
					target.dP(next, c);
				}
				next = new Point2D.Double(current.getX(), current.getY() - 1);
				if (fill.equals((target.cP(next.getX(), next.getY())))) {
					tocheck.add(new Point2D.Double(next.getX(), next.getY()));
					target.dP(next, c);
				}
				tocheck.remove(0);
			}
		}

	}

	private class RasterLineTool extends GenericRasterTool {

		private Point2D start;
		private Point2D end;
		private int brush = 2;

		public RasterLineTool(LayerRaster t, LayerAnnotation a) {
			super(t, a);
			target = t;
			annotation = a;
		}

		public void mouseDown(Point2D p, Color c, MouseEvent e) {
			super.mouseDown(p, c, e);
			start = p;
			target.draw2D().setStroke(new BasicStroke(brush));
			annotation.draw2D().setStroke(new BasicStroke(brush));
			drawStroke(p);
		}

		public void mouseDragged(Point2D p, MouseEvent e) {
			super.mouseDragged(p, e);
			if (drawingStroke) {
				annotation.clearLayer();
				drawStroke(p);
			}
		}

		public void mouseUp() {
			super.mouseUp();
			annotation.clearLayer();
			drawingStroke = false;
			target.draw2D().setColor(current);
			target.draw().drawLine((int) start.getX(), (int) start.getY(), (int) end.getX(), (int) end.getY());
			target.commit();
		}

		private void drawStroke(Point2D p) {
			if ((event.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
				end = snap(p);
			} else
				end = p;
			annotation.draw2D().setColor(current);
			annotation.draw().drawLine((int) start.getX(), (int) start.getY(), (int) end.getX(), (int) end.getY());
		}

		private Point2D snap(Point2D p) {
			int theta = (int) Math
					.round(4 * Math.atan2((p.getY() - start.getY()), (p.getX() - start.getX())) / (Math.PI));
			switch (theta) {
			case 0:
				return new Point2D.Double(p.getX(), start.getY());
			case 1:
				return new Point2D.Double(p.getX(), start.getY() + p.getX() - start.getX());
			case 2:
				return new Point2D.Double(start.getX(), p.getY());
			case 3:
				return new Point2D.Double(p.getX(), start.getY() - p.getX() + start.getX());
			case 4:
				return new Point2D.Double(p.getX(), start.getY());
			case -1:
				return new Point2D.Double(p.getX(), start.getY() - p.getX() + start.getX());
			case -2:
				return new Point2D.Double(start.getX(), p.getY());
			case -3:
				return new Point2D.Double(p.getX(), start.getY() + p.getX() - start.getX());
			case -4:
				return new Point2D.Double(p.getX(), start.getY());
			default:
				return p;
			}
		}
	}

	private class RasterSplineTool extends GenericRasterTool {

		private ArrayList<Point2D> points = new ArrayList<Point2D>();
		private int brush = 2;

		public RasterSplineTool(LayerRaster t, LayerAnnotation a) {
			super(t, a);
		}

		public void mouseDown(Point2D p, Color c, MouseEvent e) {
			super.mouseDown(p, c, e);
			points.add(p);
			annotation.clearLayer();
			drawStroke();
		}

		public void mouseDragged(Point2D p, MouseEvent e) {
			super.mouseDragged(p, e);
			if (drawingStroke) {
				points.set(points.size() - 1, p);
				annotation.clearLayer();
				drawStroke();
			}
		}

		public void mouseUp() {
			super.mouseUp();
			if (points.size() > 3) {
				annotation.clearLayer();
				drawingStroke = false;
				target.draw2D().setColor(current);
				target.draw2D().setStroke(new BasicStroke(brush));
				target.draw2D()
						.draw(new CubicCurve2D.Double(points.get(0).getX(), points.get(0).getY(), points.get(1).getX(),
								points.get(1).getY(), points.get(2).getX(), points.get(2).getY(), points.get(3).getX(),
								points.get(3).getY()));
				points.clear();
				target.commit();
			}
		}

		private void drawStroke() {
			annotation.draw2D().setColor(Color.lightGray);
			annotation.draw2D().setStroke(new BasicStroke(1));
			if (points.size() == 1)
				annotation.draw2D().drawLine((int) points.get(0).getX(), (int) points.get(0).getY(),
						(int) points.get(0).getX(), (int) points.get(0).getY());
			else if (points.size() > 1)
				for (int i = 1; i < points.size(); i++) {
					annotation.draw2D().drawLine((int) points.get(i - 1).getX(), (int) points.get(i - 1).getY(),
							(int) points.get(i).getX(), (int) points.get(i).getY());
				}
			annotation.draw2D().setColor(current);
			annotation.draw2D().setStroke(new BasicStroke(brush));
			if (points.size() > 3)
				annotation.draw2D()
						.draw(new CubicCurve2D.Double(points.get(0).getX(), points.get(0).getY(), points.get(1).getX(),
								points.get(1).getY(), points.get(2).getX(), points.get(2).getY(), points.get(3).getX(),
								points.get(3).getY()));
			annotation.draw2D().setColor(Color.RED);
			for (Point2D p : points)
				annotation.draw2D().drawLine((int) p.getX(), (int) p.getY(), (int) p.getX(), (int) p.getY());
		}

	}

	// Vector Tools

	private class GenericVectorTool extends GenericTool {
		protected LayerVector target;

		public GenericVectorTool(LayerVector t, LayerAnnotation a) {
			target = t;
			annotation = a;
		}

		public void mouseDown(Point2D p, Color c, MouseEvent e) {
			current = c;
			event = e;
		}

		public void mouseDragged(Point2D p, MouseEvent e) {
			event = e;
		}

		public void mouseUp() {

		}

		@Override
		public boolean isRaster() {
			return false;
		}
	}

	private class VectorLineTool extends GenericVectorTool {
		LineVector current;

		public VectorLineTool(LayerVector t, LayerAnnotation a) {
			super(t, a);
		}

		public void mouseDown(Point2D p, Color c, MouseEvent e) {
			super.mouseDown(p, c, e);
			drawingStroke = true;
			current = new LineVector(p.getX(), p.getY(), foreground);
			target.addVector(current);
		}

		public void mouseDragged(Point2D p, MouseEvent e) {
			super.mouseDragged(p, e);
			current.moveLast(p);
		}

		public void mouseUp() {
			super.mouseUp();

		}
	}

	private class VectorSplineTool extends GenericVectorTool {
		SplineVector current;

		public VectorSplineTool(LayerVector t, LayerAnnotation a) {
			super(t, a);
		}

		public void mouseDown(Point2D p, Color c, MouseEvent e) {
			super.mouseDown(p, c, e);
			if (drawingStroke)
				current.addNode(p);
			else {
				current = new SplineVector(p.getX(), p.getY(), foreground);
				target.Select(current);
				target.addVector(current);
				drawingStroke = true;
			}
		}

		public void mouseDragged(Point2D p, MouseEvent e) {
			super.mouseDragged(p, e);
			current.moveLast(p);
		}

		public void mouseUp() {
			super.mouseUp();
			if (current.nodes.size() == 4)
				drawingStroke = false;
		}
	}

	private class VectorPathTool extends GenericVectorTool {
		PathVector current;
		Point2D pl;
		int count = 0;

		public VectorPathTool(LayerVector t, LayerAnnotation a) {
			super(t, a);
		}

		public void mouseDown(Point2D p, Color c, MouseEvent e) {
			super.mouseDown(p, c, e);
			drawingStroke = true;
			current = new PathVector(p.getX(), p.getY(), foreground);
			target.addVector(current);
			pl = p;
		}

		public void mouseDragged(Point2D p, MouseEvent e) {
			if (p.distance(pl) > 10 || (p.distance(pl) > 1 && e.getWhen() - event.getWhen() > 5)) {
				current.addNode(p);
				pl = p;
				count = 0;
			}
			count++;
			super.mouseDragged(p, e);
		}

		public void mouseUp() {
			super.mouseUp();
			drawingStroke = false;
		}
	}

	private class VectorSelectTool extends GenericVectorTool {
		public VectorSelectTool(LayerVector t, LayerAnnotation a) {
			super(t, a);
		}

		public void mouseDown(Point2D p, Color c, MouseEvent e) {
			super.mouseDown(p, c, e);
			/* First check if selecting a node */
			double min = 100;
			Node current = null;
			// Check if combining nodes (currently have a node and Control is
			// pressed)
			if ((event.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
				// search all vectors for nearby nodes
				for (GenericVector v : target.vectors) {
					for (Node n : v.nodes) {
						double dist = n.distance(p);
						if (dist < 10) {
							min = dist;
							v.replaceNode(n, target.selectedNode);
						}
					}
				}
				if (min < 10) {// If a closeby node was found found
					return;// done
				}
			}
			min = 100;
			// search selected vectors for nearby nodes
			for (GenericVector v : target.selectedVectors) {
				for (Node n : v.nodes) {
					double dist = n.distance(p);
					if (dist < min) {
						min = dist;
						current = n;
					}
				}
			}
			if (min < 10) {// If a closeby node was found found
				target.Select(current);
				return;// done
			}

			// if none were found
			// if control key isn't pressed, deselect everything
			if ((event.getModifiers() & ActionEvent.CTRL_MASK) != ActionEvent.CTRL_MASK) {
				target.deselectNode();
				target.deselectVectors();
			}
			// Select everything at the click location
			for (GenericVector v : target.vectors) {
				if (v.getBounds().contains(p))
					target.Select(v);
			}
		}

		public void mouseDragged(Point2D p, MouseEvent e) {
			super.mouseDragged(p, e);
			if (target.selectedNode != null) {
				target.selectedNode.setLocation(p);
			}

		}
	}

	private class VectorEraserTool extends GenericVectorTool {
		public VectorEraserTool(LayerVector t, LayerAnnotation a) {
			super(t, a);
		}

		public void mouseDown(Point2D p, Color c, MouseEvent e) {
			super.mouseDown(p, c, e);
			Iterator<GenericVector> i = target.vectors.iterator();
			while (i.hasNext()) {
				GenericVector v = i.next();
				if (v.getBounds().contains(p))
					i.remove();
			}
		}

		public void mouseDragged(Point2D p, MouseEvent e) {
			super.mouseDragged(p, e);
		}
	}

}
