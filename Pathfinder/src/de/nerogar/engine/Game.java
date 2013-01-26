package de.nerogar.engine;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;

import javax.swing.JFrame;
import de.nerogar.engine.gfx.*;
import de.nerogar.engine.pathfinder.PathNode;
import de.nerogar.engine.pathfinder.Pathfinder;

public class Game extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;

	public static String NAME = "Game";
	public static int HEIGHT = 1024;
	public static int WIDTH = 1024;
	public static int SCALE = 1;

	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

	public int tick = 0;
	private Render render = new Render(WIDTH, HEIGHT);
	public FontSheet fontsheet = new FontSheet("/font.png", 8);
	public TileSheet pathSheet7 = new TileSheet("/path7.png", 403);
	public TileSheet pathSheet6 = new TileSheet("/path6.png", 1003);
	public TileSheet pathSheet1 = new TileSheet("/path1.png", 20);
	public TileSheet pathClear = new TileSheet("/pathClear.png", 500);
	public TileSheet pathOpt = new TileSheet("/pathOpt.png", 256);
	public TileSheet pathOptDraw = new TileSheet("/pathOptDraw.png", 256 * 4);
	public Pathfinder pathfinder = new Pathfinder(pathOpt, pathOptDraw);
	public boolean pathInfoPrinted = false;

	public void start() {
		new Thread(this).start();
	}

	public void run() {
		pathfinder = new Pathfinder(pathOpt, pathOptDraw);
		pathfinder.startFinding();
		while (true) {
			System.gc();
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tick();
			render();
		}
	}

	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}

		render.renderClear(pixels);

		long time1 = System.nanoTime();
		renderPixels();
		long time2 = System.nanoTime();
		System.out.println("Render time: " + (time2 - time1) / 1000000000D);

		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
		g.dispose();
		bs.show();
	}

	public void tick() {
		tick++;
		long time1 = System.nanoTime();

		pathfinder = new Pathfinder(pathOpt, pathOptDraw);
		pathfinder.startFinding();

		long time2 = System.nanoTime();

		while (!pathfinder.goalFound) {
			//for (int i = 0; i < 10; i++) {
			pathfinder.calcNewIteration();
			//}
		}

		long time3 = System.nanoTime();

		System.out.println("Map time: " + (time2 - time1) / 1000000000D);
		System.out.println("Path time: " + (time3 - time2) / 1000000000D);
		System.out.println("Pathlength: " + pathfinder.pathLength);
		System.out.println("---------------------------");
		if (pathfinder.goalFound && !pathInfoPrinted) {
			//System.out.println("Tick time: " + (time2 - time1) / 1000000000D);
			//System.out.println("Found Path!");
			//System.out.println("Path Length: " + pathfinder.pathLength);
			//pathInfoPrinted = true;
		}
	}

	public void renderPixels() {
		//render.renderFont(pixels, "TEST", 0, 0, fontsheet);
		pathfinder.tilesheetDraw.clear();
		pathfinder.render(render, pixels);

		render.renderTile(pixels, 0, 0, 0, 0, pathfinder.tilesheetDraw);
	}

	public static void main(String[] args) {
		Game game = new Game();
		game.setFocusable(false);
		game.setSize(new Dimension((WIDTH * SCALE) - 10, (HEIGHT * SCALE) - 10));

		JFrame frame = new JFrame(Game.NAME);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(game);
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.requestFocus();

		System.out.println("Width = " + game.getWidth() + " px");
		System.out.println("Height = " + game.getHeight() + " px");

		game.start();
	}

}