package de.nerogar.engine.gfx;

public class Camera {
	public int x;
	public int y;
	public static final Camera instance = new Camera(0, 0);

	public Camera(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
