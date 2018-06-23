package interfaces;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class AutoDialog extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	FlowLayout flowlayout = new FlowLayout();
	JFrame parent;
	private String name;
	ArrayList<actionComponent> elements = new ArrayList<actionComponent>();
	private int lastresult = 0;

	public AutoDialog(String n, JFrame p) {
		parent = p;
		name = n;
		setLayout(flowlayout);
	}

	// The main add_item method
	public void addItem(String name, String type, Consumer<JComponent> ca, String[] options) {
		switch (type) {
		case "Text":
			JTextField cf = new JTextField(name);
			this.add(cf);
			elements.add(new actionComponent(cf, ca));
			break;
		case "Combo":
			JComboBox cc = new JComboBox(options);
			this.add(cc);
			elements.add(new actionComponent(cc, ca));
			break;
		case "Button":
			JButton cb = new JButton(name);
			this.add(cb);
			elements.add(new actionComponent(cb, ca));
			break;
		}
	}

	public void popup() {
		lastresult = JOptionPane.showConfirmDialog(null, this, name, JOptionPane.OK_CANCEL_OPTION);
		if (lastresult == JOptionPane.OK_OPTION) {
			for (actionComponent c : elements)
				act(c);
		}
	}

	private void act(actionComponent c) {
		for (actionComponent a : elements)
			if (a.component.equals(c))
				a.act(a.component);
	}

	private static class actionComponent {
		JComponent component;
		Consumer<JComponent> action;

		public actionComponent(JComponent c, Consumer<JComponent> a) {
			component = c;
			action = a;
		}

		// Run click action
		public void act(JComponent b) {
			action.accept(b);
		}
	}

}
