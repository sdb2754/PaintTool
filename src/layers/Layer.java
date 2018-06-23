package layers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import events.Main;

public abstract class Layer {

	protected BufferedImage image;
	protected Graphics draw;
	protected Graphics2D draw2D;
	protected int x_dim, y_dim;

	protected AffineTransform transform;

	public Layer(int x, int y, AffineTransform at) {
		transform = at;
		setLayerSize(x, y);
	}

	public void setLayerSize(int x, int y) {
		x_dim = x;
		y_dim = y;
		setImage(new BufferedImage(x, y, 2));
	}

	public Rectangle getLayerBounds() {

		Point2D ul = transform.transform(new Point2D.Double(0, 0), null);
		Point2D ll = transform.transform(new Point2D.Double(x_dim, y_dim), null);
		return new Rectangle((int) ul.getX(), (int) ul.getY(), (int) (ll.getX() - ul.getX()),
				(int) (ll.getY() - ul.getY()));
	}
	
	public void clearLayer() {
		draw2D().setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
		draw2D().fill(new Rectangle(0,0,x_dim,y_dim));
		draw2D().setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
	}
	public void setLayerColor(Color c) {
		draw2D().setColor(c);
		draw2D().fill(new Rectangle(0,0,x_dim,y_dim));
	}
	
	protected Dimension layerSize() {
		return new Dimension(x_dim, y_dim);
	}

	public BufferedImage image() {
		return image;
	}
	public Graphics draw(){
		return draw;
	}
	public Graphics2D draw2D(){
		return draw2D;
	}

	public AffineTransform transform() {
		return transform;
	}
	public abstract void draw(Graphics2D g);
	
	public void setImage(BufferedImage im){
		image = im;
		draw = image.getGraphics();
		draw2D = (Graphics2D) draw;
	}

	// Add image from file
	public void addimage() {
		BufferedImage im;
		FileDialog fd = new FileDialog(Main.frame, "Open", FileDialog.LOAD);
		fd.setVisible(true);
		try {
			String path = fd.getDirectory() + fd.getFile();
			im = ImageIO.read(new File(path));
		} catch (IOException e) {
		}
	}

	/**
	 * Get an image off the system clipboard.
	 * 
	 * @return Returns an Image if successful; otherwise returns null.
	 */
	public void getImageFromClipboard() {
		BufferedImage im;
		Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
		if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
			try {
				im = (BufferedImage) transferable.getTransferData(DataFlavor.imageFlavor);
			} catch (UnsupportedFlavorException e) {
				// handle this as desired
				e.printStackTrace();
			} catch (IOException e) {
				// handle this as desired
			}
		} else {
		}
		return;
	}
	
	public abstract void undo();
	public abstract void redo();

}
