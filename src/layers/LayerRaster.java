package layers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class LayerRaster extends Layer{
	
	ArrayList<BufferedImage> previous = new ArrayList<BufferedImage>();
	int index = 0;

	public LayerRaster(int x, int y, AffineTransform at) {
		super(x, y,at);
		commit();
	}
	
	public void undo(){
		if(previous.size()>=1&&index>0){
			if(previous.size()==index)
				index--;
			index--;
			setImage(previous.get(index));
		}
	}
	public void commit(){
		while(previous.size()>index)
			previous.remove(previous.size()-1);
		if(previous.size()<10){
			previous.add(copyImage(image));
			index++;
		}
		else {
			previous.remove(0);
			previous.add(copyImage(image));
		}
	}
	public void redo(){
		if(previous.size()>index+1){
			index++;
			setImage(previous.get(index));
		}
	}
	
	public void dP(int x, int y, Color c) {
		if (x < x_dim && y < y_dim && x>=0 && y>=0)
			image.setRGB(x, y, c.getRGB());
	}
	public void dP(Point2D p, Color c){
		dP((int)p.getX(),(int)p.getY(),c);
	}
	
	public Color cP(int x, int y){
		if (x < x_dim && y < y_dim && x>=0 && y>=0)
			return new Color(image.getRGB(x, y));
		return null;
	}
	public Color cP(double x, double y){
		return cP((int)x,(int)y);
	}
	
	private static BufferedImage copyImage(BufferedImage source){
	    BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
	    Graphics g = b.getGraphics();
	    g.drawImage(source, 0, 0, null);
	    g.dispose();
	    return b;
	}
	
	@Override
	public void draw(Graphics2D g) {
		g.drawImage(image(), transform(), null);
	}
	

}
