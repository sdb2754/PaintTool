/*     */ package events;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Consumer;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import interfaces.AutoDialog;
import interfaces.AutoMenuBar;
import interfaces.AutoToolbar;
import layers.Layer;
import layers.LayerVector;
import vectors.GenericVector;

public class Main extends JPanel implements WindowStateListener, KeyListener {
	private static final long serialVersionUID = 1L;

	// Windows
	public static JFrame frame;
	static Main newContentPane;

	static AutoToolbar tools;
	static AutoToolbar colors;
	static AutoToolbar layerslist;

	public static AutoMenuBar ribbon;

	static AutoDialog canvassettings;

	static long dt;

	public static final int width = 1400;
	public static final int height = 1000;

	static Easel easel = new Easel();

	public static void main(String[] args) {
		frame = new JFrame("Draw");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		newContentPane = new Main();
		newContentPane.setOpaque(true);
		frame.setContentPane(newContentPane);
		frame.setPreferredSize(new Dimension(width, height));
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		configureRibbons();
		init();
		// loop
		dt = 10;
		while (true) {
			update();
			render();
			sleep();
		}
	}

	public Main() {
		setLayout(new BorderLayout());
		setFocusable(true);
		addKeyListener(this);
		frame.addWindowStateListener(this);
		add(easel, BorderLayout.CENTER);
		configureMenu();
	}

	private static void init() {
		easel.addNew("Untitled", 500, 500);
	}

	private static void update() {

	}

	private static void render() {
		newContentPane.repaint();
	}

	private static void sleep() {
		try {
			Thread.sleep(dt);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void configureRibbons() {

		layerslist = new AutoToolbar("Layers", 1250, 290, 120, 300, frame);

		tools = new AutoToolbar("Tools", 10, 80, 120, 300, frame);
		String[] names = { "Fill", "Brush", "Select", "Pencil", "Line", "Spline", "Dropper", "Eraser" };
		tools.add(names, true, 50, 50);
		for (String n : names)
			tools.setAction(n, (y) -> {
				easel.setActiveTool(n);
			});

		colors = new AutoToolbar("Palette", 1250, 80, 10, 200, frame);
		Color[] col = { new Color(255, 0, 151, 255), new Color(162, 0, 255, 255), new Color(0, 171, 169, 255),
				new Color(40, 191, 38, 255), new Color(160, 80, 0, 255), new Color(230, 113, 184, 255),
				new Color(240, 150, 9, 255), new Color(27, 161, 226, 255), new Color(229, 20, 0, 255),
				new Color(51, 153, 51, 255), new Color(0, 0, 0, 255), new Color(255, 255, 255, 255) };
		for (int i = 0; i < col.length; i++) {
			final int j = i;
			colors.addItem("def_col_" + i, false, false, col[i], 30, 30, true, (y) -> {
				easel.setCanvasForeground(col[j]);
			}, (y) -> {
				easel.setCanvasBackground(col[j]);
			});
		}
	}

	public static void arrangeLayerList() {
		layerslist.removeAll();
		ArrayList<Layer> layers = easel.getLayers();
		Color c;
		if (layers != null)
			for (int i = 0; i < layers.size(); i++) {
				final int j = i;
				if (layers.get(i) instanceof LayerVector)
					c = Color.DARK_GRAY;
				else
					c = Color.GRAY;
				layerslist.addItem("Layer " + i, false, true, c, 100, 30, true, (y) -> {
					easel.setActiveLayer(j);
				}, (y) -> {
				});
			}
	}

	private void configureMenu() {
		ribbon = new AutoMenuBar(new String[] { "File", "Edit", "View", "Canvas", "Layers", "Help" });
		add(ribbon, BorderLayout.NORTH);

		// Items
		ribbon.addItem("Zoom", "Canvas", "item");
		ribbon.addItem("Size", "Canvas", "item");
		ribbon.addItem("New", "Layers", "item");
		ribbon.addItem("Center", "Canvas", "item");
		ribbon.addItem("Undo", "Edit", "item");
		ribbon.addItem("Redo", "Edit", "item");
		// Checkboxes
		ribbon.addItem("Tools", "View", "check");
		ribbon.addItem("Palette", "View", "check");
		ribbon.addItem("Layers", "View", "check");

		// actions
		ribbon.setAction("Zoom", (y) -> {
			easel.setZoom();
		});
		ribbon.setAction("Size", (y) -> {
			easel.setSize();
		});
		ribbon.setAction("New", (y) -> {
			easel.addLayerToActive();
		});
		ribbon.setAction("Center", (y) -> {
			easel.centerCanvas();
		});
		ribbon.setAction("Undo", (y) -> {
			easel.undo();
		});
		ribbon.setAction("redo", (y) -> {
			easel.redo();
		});

		// state changes
		ribbon.setStateChange("Tools", (y) -> {
			tools.setVisible(y);
		});
		ribbon.setStateChange("Palette", (y) -> {
			colors.setVisible(y);
		});
		ribbon.setStateChange("Layers", (y) -> {
			layerslist.setVisible(y);
		});
	}

	private void configureDialogs() {

		canvassettings = new AutoDialog("Canvas Settings", frame);
		canvassettings.addItem("new", "Combo", (c) -> {
			JComboBox cc = ((JComboBox)c);
		}, new String[] {"Vector", "Raster"});

	}

	@Override
	public void windowStateChanged(WindowEvent e) {
		// TODO Auto-generated method stub
		tools.setVisible(frame.getState() == JFrame.NORMAL);
		colors.setVisible(frame.getState() == JFrame.NORMAL);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
			if (e.getKeyCode() == KeyEvent.VK_Z) {
				easel.undo();
			}
			if (e.getKeyCode() == KeyEvent.VK_Y) {
				easel.redo();
			}
		}

		if (e.getKeyCode() == KeyEvent.VK_DELETE) {
			Layer l = easel.getActiveLayer();
			if (l instanceof LayerVector) {
				Iterator<GenericVector> i = ((LayerVector) l).vectors.iterator();
				ArrayList<GenericVector> selected = ((LayerVector) l).selectedVectors;
				while (i.hasNext()) {
					GenericVector v = i.next();
					if (selected.contains(v))
						i.remove();
				}
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
