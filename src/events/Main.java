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
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JPanel implements MouseMotionListener, MouseListener, KeyListener, WindowStateListener {
	private static final long serialVersionUID = 1L;

	// Windows
	static JFrame frame;
	static Main newContentPane;
	static WindowToolbar tools;
	static WindowToolbar colors;

	static long dt;

	public static final int width = 1400;
	public static final int height = 1000;

	static AutoMenuBar ribbon;
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
		easel.addNew("test", 20, 20);
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

		tools = new WindowToolbar("Tools", 10, 80, 120, 300);
		String[] names = { "Paint", "Brush", "Select", "Pencil","Line","Spline"};
		tools.add(names, true, 50, 50);
		for(String n : names)
			tools.setAction(n, (y) -> {easel.setActiveTool(n);});

		colors = new WindowToolbar("Palette", 1250, 80, 10, 200);
		Color[] col = { new Color(255, 0, 151, 255), new Color(162, 0, 255, 255), new Color(0, 171, 169, 255),
				new Color(40, 191, 38, 255), new Color(160, 80, 0, 255), new Color(230, 113, 184, 255),
				new Color(240, 150, 9, 255), new Color(27, 161, 226, 255), new Color(229, 20, 0, 255),
				new Color(51, 153, 51, 255), new Color(0, 0, 0, 255), new Color(255, 255, 255, 255) };
		for (int i = 0; i < col.length; i++){
			final int j = i;
			colors.addItem("def_col_" + i, false, col[i], 30, 30, true, (y) -> {
				easel.setCanvasForeground(col[j]);
			}, (y) -> {
				easel.setCanvasBackground(col[j]);
			});
		}
	}

	private void configureMenu() {
		ribbon = new AutoMenuBar(new String[] { "File", "Edit", "View", "Canvas", "Layers", "Help" });
		add(ribbon, BorderLayout.NORTH);

		// Items
		ribbon.addItem("Zoom", "Canvas", "item");
		ribbon.addItem("Canvas Size", "Canvas", "item");
		ribbon.addItem("Center", "Canvas", "item");
		// Checkboxes
		ribbon.addItem("Tools", "View", "check");
		ribbon.addItem("Palette", "View", "check");

		// actions
		ribbon.setAction("Zoom", (y) -> {
			easel.setZoom();
		});
		ribbon.setAction("Canvas Size", (y) -> {
			easel.setSize();
		});
		ribbon.setAction("Center", (y) -> {
			easel.centerCanvas();
		});

		// state changes
		ribbon.setStateChange("Tools", (y) -> {
			tools.setVisible(y);
		});
		ribbon.setStateChange("Palette", (y) -> {
			colors.setVisible(y);
		});
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseClicked(MouseEvent e) {
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
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void windowStateChanged(WindowEvent e) {
		// TODO Auto-generated method stub
		tools.setVisible(frame.getState() == JFrame.NORMAL);
		colors.setVisible(frame.getState() == JFrame.NORMAL);
	}

}
