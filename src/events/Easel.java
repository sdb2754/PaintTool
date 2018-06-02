package events;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Easel extends JTabbedPane implements ChangeListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static ArrayList<Canvas> canvases = new ArrayList<Canvas>();
	static Canvas active;

	public Easel(){
		
	}
	
	public void addNew(String name, int x, int y){
		addChangeListener(this);
		Canvas c = new Canvas(x,y);
		add(name, c);
		canvases.add(c);
		setActive(c);
	}
	
	private void setActive(Canvas c){
		setSelectedComponent(c);
		active=c;
	}
	
	//public methods to reach Active
	
	public void setZoom(){
		active.setZoom();
	}
	public void setSize(){
		active.setSize();
	}
	public void centerCanvas(){
		active.centerCanvas();
	}
	public void setCanvasForeground(Color c){
		active.setCanvasForeground(c);
	}
	public void setCanvasBackground(Color c){
		active.setCanvasBackground(c);
	}
	public void setActiveTool(String t){
		active.rastertools.setActiveTool(t);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		active = (Canvas) this.getSelectedComponent();
	}

}
