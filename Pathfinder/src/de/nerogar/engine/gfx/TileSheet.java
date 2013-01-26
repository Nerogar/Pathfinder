package de.nerogar.engine.gfx;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TileSheet {
	public BufferedImage image;
	public int[] pixels;
	public int tileSize;
	public int w;
	public int h;
	public String filename;

	public TileSheet(String filename, int tileSize) {
		try {
			image = ImageIO.read(TileSheet.class.getResourceAsStream(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.tileSize = tileSize;
		this.filename = filename;
		w = image.getWidth();
		h = image.getHeight();

		pixels = image.getRGB(0, 0, w, h, null, 0, w);

		/*
		for (int i = 0; i < pixels.length; i++) {
			if (((i + (i / h)) % 2) == 0) {
				pixels[i] = 0xffff00ff;
			}
		}
		image.setRGB(0, 0, w, h, pixels, 0, w);
		try {
			FileOutputStream out = new FileOutputStream("test.png");
			ImageIO.write(image, "PNG", out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/

	}

	public void saveTileSheet(String newFileName) {
		image.setRGB(0, 0, w, h, pixels, 0, w);
		try {
			FileOutputStream out = new FileOutputStream(newFileName);
			ImageIO.write(image, "PNG", out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setPixel(int x, int y, int color) {
		pixels[x + y * h] = color;
	}

	public void clear() {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 0xffffff;
		}
	}

	public boolean[][] getAsBooleanArray() {
		boolean[][] retArray = new boolean[w][h];
		for (int i = 0; i < pixels.length; i++) {
			if ((pixels[i] & 0x00ffffff) == 0x000000) {
				retArray[i % w][i / h] = false;
			} else {
				retArray[i % w][i / h] = true;
			}
		}
		return retArray;
	}
}