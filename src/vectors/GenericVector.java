package vectors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public abstract class GenericVector {
	Stroke stroke = new BasicStroke(1);
	Color foreground, background;
	public ArrayList<Node> nodes = new ArrayList<Node>();
	public abstract void drawVector(Graphics2D g2d,AffineTransform at);
	public abstract void drawSelected(Graphics2D g2d,AffineTransform at);
	
	public abstract Rectangle getBounds();

	public void moveLast(Point2D p){
		nodes.get(nodes.size()-1).setLocation(p);
	}
	
	public void replaceNode(Node t, Node r){
		if(t.equals(r))
			return;
		for(int i=0;i<nodes.size();i++){
			if(nodes.get(i).equals(t)){
				nodes.set(i, r);
			}
		}
	}

}