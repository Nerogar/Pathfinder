package de.nerogar.engine.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FontSheet{
	public BufferedImage image;
	public int[] pixels;
	public int tileSize;
	public int w;
	public int h;
	
	public int xCount;
	public int yCount;
	
	public final String font = "ABCDEFGHIJKLMNOPQRSTUVWXYZ      " +
			"abcdefghijklmnopqrstuvwxyz      " +
			"0123456789                      " + 
			".:,;/\\()!?";
	
	
	public int getCharIndex(char c){
		return font.indexOf(c);
	}
	
	public FontSheet(String filename, int tileSize) {
		try {
			image = ImageIO.read(Render.class.getResourceAsStream(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		this.tileSize = tileSize;
		w = image.getWidth();
		h = image.getHeight();
		
		pixels = image.getRGB(0,0,w,h,null,0,w);
		
		xCount = image.getWidth()/tileSize;
		yCount = image.getHeight()/tileSize;
	}

}
