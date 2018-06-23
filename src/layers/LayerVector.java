package layers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import vectors.GenericVector;
import vectors.Node;

public class LayerVector extends Layer {

	public ArrayList<GenericVector> vectors = new ArrayList<GenericVector>();
	public ArrayList<GenericVector> selectedVectors = new ArrayList<GenericVector>();
	public Node selectedNode=null;
	public LayerVector(int x, int y, AffineTransform at) {
		super(x, y, at);
	}

	public void addVector(GenericVector v) {
		vectors.add(v);
	}

	@Override
	public void draw(Graphics2D g2d) {
		g2d.setClip(getLayerBounds());
		//g2d.setColor(Color.WHITE);
		//g2d.fill(getLayerBounds());
		for (GenericVector v : vectors)
			v.drawVector(g2d, transform());
		for (GenericVector v : selectedVectors)
			v.drawSelected(g2d, transform());
		if(selectedNode!=null)
			selectedNode.drawNode(g2d, transform(),true);
	}

	public void deselectNode() {
		selectedNode=null;
	}
	public void deselectVectors() {
		selectedVectors.clear();
	}
	public void Select(Node n){
		selectedNode = n;
	}
	public void Select(GenericVector v){
		selectedVectors.add(v);
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub

	}

	@Override
	public void redo() {
		// TODO Auto-generated method stub

	}

}
