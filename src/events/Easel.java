package events;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import layers.Layer;

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
		Main.arrangeLayerList();
	}
	
	private void setActive(Canvas c){
		setSelectedComponent(c);
		active=c;
	}
	
	public Layer getActiveLayer(){
		return active.active;
	}
	
	public ArrayList<Layer> getLayers(){
		if(active!=null)
			return active.layers;
		return null;
	}
	public void setActiveLayer(int i){
		active.setActive(i);
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
		active.tools.setActiveTool(t);
	}
	public void undo(){
		active.active.undo();
	}
	public void redo(){
		active.active.redo();
	}
	
	public void addLayerToActive(){
		active.addLayer();
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		active = (Canvas) this.getSelectedComponent();
	}

}
