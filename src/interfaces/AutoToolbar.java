package interfaces;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import events.Main;

public class AutoToolbar extends JDialog implements WindowListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	FlowLayout flowlayout = new FlowLayout();

	private String name;
	ArrayList<cButton> buttons = new ArrayList<cButton>();

	private CustomMouseListener cml = new CustomMouseListener();
	
	private JFrame parent;

	// Constructor
	public AutoToolbar(String n, int x, int y, int w, int h,JFrame p) {
		parent = p;
		name = n;
		setPreferredSize(new Dimension(w, h));
		setLayout(flowlayout);
		pack();
		setResizable(false);
		setVisible(true);
		setVisible(true);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setTitle(n);
		addWindowListener(this);
		setRelativeToParent(x, y);
	}

	public void setRelativeToParent(int x, int y) {
		Point loc = parent.getLocationOnScreen();
		setLocation(loc.x + x, loc.y + y);
	}

	// The main add_item method
	public void addItem(String name, boolean hasSprite, boolean showText, Color c, int w, int h,
			boolean hasRightClickAction, Consumer<Boolean> action, Consumer<Boolean> rightclick) {
		cButton b = new cButton(name);
		b.addActionListener(this);
		if (hasRightClickAction) {
			b.addMouseListener(cml);
			b.setRightClick(rightclick);
		} else
			b.setRightClick((y) -> {
			});
		b.setAction(action);
		if (c == null)
			b.setBackground(Color.LIGHT_GRAY);
		else
			b.setBackground(c);
		b.setPreferredSize(new Dimension(w, h));
		add(b);
		if (showText)
			b.setText(name);
		if (hasSprite)
			b.setSprite();
		buttons.add(b);

	}

	public void addItem(String name, Color c, int w, int h) {
		addItem(name, false, false, c, w, h, false, (y) -> {
		}, (y) -> {
		});
	}

	public void addItem(String name, int w, int h) {
		addItem(name, null, w, h);
	}

	public void addItem(String name) {
		addItem(name, 10, 10);
	}

	public void addItem(String name, boolean hasSprite, Color c, int w, int h) {
		addItem(name, hasSprite, false, c, w, h, false, (y) -> {
		}, (y) -> {
		});
	}

	public void addItem(String name, boolean hasSprite, int w, int h) {
		addItem(name, hasSprite, null, w, h);
	}

	public void addItem(String name, boolean hasSprite) {
		addItem(name, hasSprite, 10, 10);
	}

	// Add top-level menus to the MenuBar
	public void add(String[] names, int w, int h) {
		for (String n : names) {
			addItem(n, w, h);
		}
	}

	public void add(String[] names, boolean hasSprite, int w, int h) {

		for (String n : names) {
			addItem(n, hasSprite, w, h);
		}
	}

	public void removeItem(String name) {
		// Get the Component named "name"
		Component c = get(name);
		//Remove it
		buttons.remove(c);
		this.remove(c);
	}
	
	public void removeAll(){
		for(cButton b : buttons)
			this.remove(b);
		buttons.clear();
	}

	// Set the right click action for an item
	public void setRightAction(String name, Consumer<Boolean> a) {
		// Get the Component named "name"
		Component c = get(name);
		// Calls the set_action method for the menu item
		((cButton) c).setRightClick(a);
	}

	// Set the click action for an item
	public void setAction(String name, Consumer<Boolean> a) {
		// Get the Component named "name"
		Component c = get(name);
		// Calls the set_action method for the menu item
		((cButton) c).setAction(a);
	}

	public void setBackgroundColor(String name, Color b) {
		Component c = get(name);
		((cButton) c).setBackground(b);
	}

	public void setSprite(String name) {
		Component c = get(name);
		((cButton) c).setSprite();
	}

	// Get item by name
	private Component get(String name) {

		// return the MenuBar
		if (name == this.name)
			return this;
		// Check if item is in top level menus
		for (cButton cb : buttons) {
			if (cb.name.equals(name))
				return cb;
		}
		// Nothing found. Return null
		return null;
	}

	public Color getButtonColor(String name) {
		Component c = get(name);
		return ((cButton) c).getBackground();
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		Main.ribbon.setState(name, false);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent e) {
		Main.ribbon.setState(name, true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Get the display name of the clicked item
		cButton b = (cButton) e.getSource();
		// Get the item by name
		Component c = get(b.name);
		((cButton) c).act(true);
	}

	// Custom JButton class
	private static class cButton extends JButton {
		private final long serialVersionUID = 1L;
		// Display name
		String name;
		// Click action
		Consumer<Boolean> action;
		Consumer<Boolean> rightclick;

		public cButton(String s) {
			name = s;
			action = (y) -> {
			};
			rightclick = (y) -> {
			};
		}

		private void setSprite() {
			Image sprite;
			try {
				sprite = ImageIO.read(getClass().getClassLoader().getResource("assets/" + name + ".png"));
			} catch (IOException e) {
				e.printStackTrace();
				sprite = null;
			}
			super.setIcon(new ImageIcon(sprite));
		}

		// Set click action
		public void setAction(Consumer<Boolean> c) {
			action = c;
		}

		public void setRightClick(Consumer<Boolean> c) {
			rightclick = c;
		}

		// Run click action
		public void act(boolean b) {
			action.accept(b);
		}

		public void actRight(boolean b) {
			rightclick.accept(b);
		}

		@Override
		public String getName() {
			return name;
		}
	}

	public class CustomMouseListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) { // right click
				// Get the display name of the clicked item
				cButton b = (cButton) e.getSource();
				String cmd = b.getName();
				// Get the item by name
				Component c = get(cmd);
				((cButton) c).actRight(true);
			}
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}

}
