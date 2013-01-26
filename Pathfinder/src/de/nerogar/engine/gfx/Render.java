package de.nerogar.engine.gfx;

public class Render {
	public int w;
	public int h;

	public void renderClear(int[] pixels) {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 0xffffff;
		}
	}

	private Camera camera = Camera.instance;

	public void renderTile(int[] pixels, int xOffs, int yOffs, int xCount, int yCount, TileSheet sheet) {
		for (int xSource = 0; xSource < sheet.tileSize; xSource++) {
			int xS = xSource + (xCount * sheet.tileSize);
			int xT = xSource + xOffs - camera.x;
			for (int ySource = 0; ySource < sheet.tileSize; ySource++) {
				int yS = ySource + (yCount * sheet.tileSize);
				int yT = ySource + yOffs - camera.y;
				if (xT >= 0 && xT <= w - 1 && yT >= 0 && yT <= h - 1 && sheet.pixels[sheet.w * yS + xS] != 0xffff00ff) {
					pixels[w * yT + xT] = sheet.pixels[sheet.w * yS + xS];

				}
			}
		}
	}

	public void renderPixel(int[] pixels, int x, int y, int color) {
		pixels[y * w + x] = color;
	}

	public void switchPixels(int[] pixels, int xS, int yS, int xT, int yT) {

		if (xT >= 0 && xT <= w - 1 && yT >= 0 && yT <= h - 1 && xS >= 0 && xS <= w - 1 && yS >= 0 && yS <= h - 1) {
			int buffer;
			buffer = pixels[w * yS + xS];
			pixels[w * yS + xS] = pixels[w * yT + xT];
			pixels[w * yT + xT] = buffer;
		}
	}

	public void renderFont(int[] pixels, String text, int xOffs, int yOffs, FontSheet sheet) {
		for (int i = 0; i < text.length(); i++) {
			int xCount = (sheet.getCharIndex(text.charAt(i))) % sheet.xCount;
			for (int xSource = 0; xSource < sheet.tileSize; xSource++) {
				int xS = xSource + (xCount * sheet.tileSize);
				int xT = xSource + xOffs - camera.x;
				for (int ySource = 0; ySource < sheet.tileSize; ySource++) {
					int yCount = (int) (sheet.getCharIndex(text.charAt(i))) / sheet.xCount;
					int yS = ySource + (yCount * sheet.tileSize);
					int yT = ySource + yOffs - camera.y;
					if (sheet.pixels[sheet.w * yS + xS] != 0xff000000) {
						if (xT >= 0 && xT <= w - 1 && yT >= 0 && yT <= h - 1) {
							pixels[(w * yT + xT) + i * sheet.tileSize] = sheet.pixels[sheet.w * yS + xS];
						}
					}
				}
			}
		}
	}

	public Render(int w, int h) {
		this.w = w;
		this.h = h;
	}
}