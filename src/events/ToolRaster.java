package events;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import Jama.Matrix;

public class ToolRaster {

	private GenericTool activetool;
	Color foreground;
	Color background;

	// Load Tools
	Pencil pencil;
	Brush brush;
	Select select;
	Fill fill;
	Line line;
	Spline spline;

	public ToolRaster(Layer t, Layer a) {
		foreground = Color.BLACK;
		background = Color.WHITE;
		pencil = new Pencil(t, a);
		brush = new Brush(t, a);
		select = new Select(t, a);
		fill = new Fill(t, a);
		line = new Line(t, a);
		spline = new Spline(t, a);
		setActiveTool("Pencil");
	}

	public void setActiveTool(String t) {
		switch (t) {
		case "Pencil":
			activetool = pencil;
			break;
		case "Brush":
			activetool = brush;
			break;
		case "Select":
			activetool = select;
			break;
		case "Fill":
			activetool = fill;
			break;
		case "Line":
			activetool = line;
			break;
		case "Spline":
			activetool = spline;
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

	private class GenericTool {
		protected Layer target, annotation;
		protected Color current;

		public void mouseDown(Point2D p, Color c, MouseEvent e) {

		}

		public void mouseDragged(Point2D p, MouseEvent e) {

		}

		public void mouseUp() {

		}
	}

	private class Pencil extends GenericTool {
		private Point2D last;
		private boolean drawingStroke = false;

		public Pencil(Layer t, Layer a) {
			target = t;
			annotation = a;
		}

		public void mouseDown(Point2D p, Color c, MouseEvent e) {
			current = c;
			target.dP((int) p.getX(), (int) p.getY(), c);
			last = p;
			drawingStroke = true;
		}

		public void mouseDragged(Point2D p, MouseEvent e) {
			if (drawingStroke) {
				target.draw.setColor(current);
				target.draw.drawLine((int) last.getX(), (int) last.getY(), (int) p.getX(), (int) p.getY());
				last = p;
			}
		}

		public void mouseUp() {
			drawingStroke = false;
		}
	}

	private class Brush extends GenericTool {
		private Point2D last;
		private boolean drawingStroke = false;
		private int brush = 5;

		public Brush(Layer t, Layer a) {
			target = t;
			annotation = a;

		}

		public void mouseDown(Point2D p, Color c, MouseEvent e) {
			current = c;
			last = p;
			drawStroke(p);
			drawingStroke = true;
		}

		public void mouseDragged(Point2D p, MouseEvent e) {
			if (drawingStroke) {
				drawStroke(p);
				last = p;
			}
		}

		public void mouseUp() {
			drawingStroke = false;
		}

		private void drawStroke(Point2D p) {
			target.draw2D.setColor(current);
			target.draw2D.setStroke(new BasicStroke(brush));
			target.draw.drawLine((int) last.getX(), (int) last.getY(), (int) p.getX(), (int) p.getY());
		}
	}

	private class Select extends GenericTool {
		public Select(Layer t, Layer a) {
			target = t;
			annotation = a;

		}
	}

	private class Fill extends GenericTool {
		public Fill(Layer t, Layer a) {
			target = t;
			annotation = a;

		}
	}

	private class Line extends GenericTool {

		private Point2D start;
		private Point2D end;
		private boolean drawingStroke = false;
		private int brush = 2;
		MouseEvent event;

		public Line(Layer t, Layer a) {
			target = t;
			annotation = a;
		}

		public void mouseDown(Point2D p, Color c, MouseEvent e) {
			event = e;
			current = c;
			start = p;
			drawStroke(p);
			drawingStroke = true;
		}

		public void mouseDragged(Point2D p, MouseEvent e) {
			event = e;
			if (drawingStroke) {
				annotation.setLayerColor(new Color(0, 0, 0, 0));
				drawStroke(p);
			}
		}

		public void mouseUp() {
			annotation.setLayerColor(new Color(0, 0, 0, 0));
			drawingStroke = false;
			target.draw2D.setColor(current);
			target.draw2D.setStroke(new BasicStroke(brush));
			target.draw.drawLine((int) start.getX(), (int) start.getY(), (int) end.getX(), (int) end.getY());
		}

		private void drawStroke(Point2D p) {
			if ((event.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
				end = snap(p);
			} else
				end = p;
			annotation.draw2D.setColor(current);
			annotation.draw2D.setStroke(new BasicStroke(brush));
			annotation.draw.drawLine((int) start.getX(), (int) start.getY(), (int) end.getX(), (int) end.getY());
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

	private class Spline extends GenericTool {

		private ArrayList<Point2D> points = new ArrayList<Point2D>();
		private ArrayList<Point> fitdata = new ArrayList<Point>();
		private boolean drawingStroke = false;
		private int brush = 2;
		MouseEvent event;

		public Spline(Layer t, Layer a) {
			target = t;
			annotation = a;
		}

		public void mouseDown(Point2D p, Color c, MouseEvent e) {
			event = e;
			current = c;
			points.add(p);
			annotation.setLayerColor(new Color(0, 0, 0, 0));
			drawStroke();
			drawingStroke = true;
		}

		public void mouseDragged(Point2D p, MouseEvent e) {
			event = e;
			if (drawingStroke) {
				points.set(points.size() - 1, p);
				annotation.setLayerColor(new Color(0, 0, 0, 0));
				drawStroke();
				if (e.getClickCount() == 2) {
					drawingStroke = false;
					target.draw2D.setColor(current);
					target.draw2D.setStroke(new BasicStroke(brush));
					for (int i = 1; i < fitdata.size(); i++) {
						target.draw2D.drawLine(fitdata.get(i - 1).x, fitdata.get(i - 1).y, fitdata.get(i).x,
								fitdata.get(i).y);
					}
					annotation.setLayerColor(new Color(0, 0, 0, 0));
				}
			}
		}

		private void drawStroke() {
			splineFit();
			annotation.draw2D.setColor(current);
			annotation.draw2D.setStroke(new BasicStroke(brush));
			for (int i = 1; i < fitdata.size(); i++) {
				annotation.draw2D.drawLine(fitdata.get(i - 1).x, fitdata.get(i - 1).y, fitdata.get(i).x,
						fitdata.get(i).y);
			}
			annotation.draw2D.setBackground(Color.RED);
			for (Point2D p : points)
				annotation.draw2D.drawLine((int) p.getX(), (int) p.getY(), (int) p.getX(), (int) p.getY());
		}

		private void splineFit() {
			int n = points.size();
			int xa = (int) points.get(0).getX();
			int xb = (int) points.get(n - 1).getX();
			
			//Matrix with 4's on diagonal, 1's everywhere else, and a 2 in the upper-left and lower-right corners
			double[][] M = new double[n][n];
			for (int i = 0; i < n; i++)
				for (int j = 0; j < n; j++) {
					M[i][j] = 0;
					if (i == j)
						M[i][j] = 4;
					if (((int) Math.abs(i - j)) == 1)
						M[i][j] = 1;
				}
			M[0][0] = 2;
			M[n - 1][n - 1] = 2;
			
			double[][] bv = new double[n][1];
			for (int i = 1; i < n; i++) {
				bv[i][0] = 3 * (points.get(i - 1).getY() - points.get(i).getY());
			}

			Matrix A = new Matrix(M);
			Matrix B = new Matrix(bv);
			Matrix D = A.solve(B);
			fitdata.clear();
			int pc = 0;
			double t = 0;
			long c;
			long d;
			for (int x = xa; x < xb; x += 1) {
				if (x >= points.get(pc + 1).getX())
					pc++;
				t = ((double) (x - points.get(pc).getX()))
						/ ((double) (points.get(pc + 1).getX() - points.get(pc).getX()));
				c = (int) (3 * (points.get(pc + 1).getY() - points.get(pc).getY()) - 2 * D.get(pc, 0)
						- D.get(pc + 1, 0));
				d = (int) (2 * (points.get(pc).getY() - points.get(pc + 1).getY()) + D.get(pc, 0) + D.get(pc + 1, 0));
				fitdata.add(new Point(x,
						(int) (points.get(pc).getY() + D.get(pc, 0) * t + c * Math.pow(t, 2) + d * Math.pow(t, 3))));
			}
		}

	}

}
