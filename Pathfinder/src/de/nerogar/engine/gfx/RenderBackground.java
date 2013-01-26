package de.nerogar.engine.gfx;

public class RenderBackground {
	public int w;
	public int h;
	private Render render;

	public RenderBackground(int w, int h) {
		this.w = w;
		this.h = h;
		render = new Render(w, h);
	}

	public void render(int[] pixels, int xPos, int yPos, int x, int y, TileSheet sheet) {
		for (int i = -1; i < (int) (w / sheet.tileSize) + 1; i++) {
			for (int j = -1; j < (int) (h / sheet.tileSize) + 1; j++) {
				int xPosT=xPos%sheet.tileSize;
				int yPosT=yPos%sheet.tileSize;
				render.renderTile(pixels, x, y, (i * sheet.tileSize)+xPosT, (j * sheet.tileSize)+yPosT, sheet);
			}
		}
	}
}
