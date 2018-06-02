package events;

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

public class Layer {

	protected BufferedImage image;
	public Graphics draw;
	public Graphics2D draw2D;
	private int x_dim, y_dim;

	protected AffineTransform transform;

	public Layer(int x, int y, AffineTransform at) {
		transform = at;
		setLayerSize(x, y);
	}

	public void setLayerSize(int x, int y) {
		x_dim = x;
		y_dim = y;
		image = new BufferedImage(x, y, 2);
		draw = image.getGraphics();
		draw2D = (Graphics2D) draw;
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, x, y);
	}

	public Rectangle getLayerBounds() {

		Point2D ul = transform.transform(new Point2D.Double(0, 0), null);
		Point2D ll = transform.transform(new Point2D.Double(x_dim, y_dim), null);

		return new Rectangle((int) ul.getX(), (int) ul.getY(), (int) (ll.getX() - ul.getX()),
				(int) (ll.getY() - ul.getY()));
	}

	public void dP(int x, int y, Color c) {
		if (x < x_dim && y < y_dim)
			image.setRGB(x, y, c.getRGB());
	}

	protected void setLayerColor(Color c) {
		Dimension d = layerSize();
		for (int i = 0; i < d.getWidth(); i++)
			for (int j = 0; j < d.getHeight(); j++) {
				dP(i, j, c);
			}
	}

	protected Dimension layerSize() {
		return new Dimension(x_dim, y_dim);
	}

	public BufferedImage renderedImage() {
		return image;
	}

	public AffineTransform transform() {
		return transform;
	}

	public void draw(Graphics2D g) {
		g.drawImage(image, transform, null);
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

}
