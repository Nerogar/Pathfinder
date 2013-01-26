package de.nerogar.engine.pathfinder;

import java.util.ArrayList;

import de.nerogar.engine.gfx.*;

public class Pathfinder {

	private int renderCycle = 0;
	private boolean imageSaved = false;
	//////////////////
	boolean[][] walkableMap;

	public PathNode[][] nodeMap;
	public TileSheet tilesheetSource;
	public TileSheet tilesheetDraw;
	public ArrayList<PathNode> openList = new ArrayList<PathNode>();
	public ArrayList<PathNode> closedList = new ArrayList<PathNode>();
	public ArrayList<PathNode> finalPath = new ArrayList<PathNode>();
	public boolean goalFound = false;
	public double pathLength = 0;

	/*public int aX = 0;
	public int aY = 0;
	public int bX = 252;
	public int bY = 252;*/

	public int aX = 252;
	public int aY = 252;
	public int bX = 0;
	public int bY = 0;

	public Pathfinder(TileSheet tilesheetSource, TileSheet tilesheetDraw) {
		walkableMap = tilesheetSource.getAsBooleanArray();
		this.tilesheetSource = tilesheetSource;
		this.tilesheetDraw = tilesheetDraw;
		calcNodeMap();
		//renderCells();
	}

	public void startFinding() {
		PathNode tempNode = nodeMap[aX][aY];
		openList = new ArrayList<PathNode>();
		closedList = new ArrayList<PathNode>();
		finalPath = new ArrayList<PathNode>();
		goalFound = false;
		openList.add(tempNode);
	}

	public void calcNodeMap() { //fertig
		nodeMap = new PathNode[walkableMap.length][walkableMap[0].length];
		for (int i = 0; i < walkableMap.length; i++) {
			for (int j = 0; j < walkableMap.length; j++) {
				PathNode newNode = new PathNode(i, j);
				newNode.walkable = walkableMap[i][j];
				nodeMap[i][j] = newNode;
			}
		}

		int iteration = 2;
		while (iteration <= walkableMap.length) {

			for (int i = 0; i < walkableMap.length; i += iteration) {
				for (int j = 0; j < walkableMap.length; j += iteration) {
					boolean m00 = nodeMap[i][j].mergeable;
					boolean m01 = nodeMap[i][j + (iteration / 2)].mergeable;
					boolean m10 = nodeMap[i + (iteration / 2)][j].mergeable;
					boolean m11 = nodeMap[i + (iteration / 2)][j + (iteration / 2)].mergeable;

					if (m00 && m01 && m10 && m11) {
						//calc node-connection
						boolean w00 = nodeMap[i][j].walkable;
						boolean w01 = nodeMap[i][j + (iteration / 2)].walkable;
						boolean w10 = nodeMap[i + (iteration / 2)][j].walkable;
						boolean w11 = nodeMap[i + (iteration / 2)][j + (iteration / 2)].walkable;

						if (w00 == w01 && w01 == w10 && w10 == w11) {
							PathNode newNode = new PathNode(i, j);
							newNode.size = iteration;
							newNode.mergeable = true;
							newNode.walkable = w00;

							for (int i2 = 0; i2 < iteration; i2++) {
								for (int j2 = 0; j2 < iteration; j2++) {
									nodeMap[i + i2][j + j2] = newNode;
								}
							}

						} else {
							nodeMap[i][j].mergeable = false;
							nodeMap[i][j + (iteration / 2)].mergeable = false;
							nodeMap[i + (iteration / 2)][j].mergeable = false;
							nodeMap[i + (iteration / 2)][j + (iteration / 2)].mergeable = false;
						}
					} else {
						for (int i2 = 0; i2 < iteration; i2++) {
							for (int j2 = 0; j2 < iteration; j2++) {
								nodeMap[i + i2][j + j2].mergeable = false;
							}
						}
					}
				}
			}

			iteration *= 2;
		}

		for (int i = 0; i < nodeMap.length; i++) {
			for (int j = 0; j < nodeMap[i].length; j = nodeMap[i][j].y + nodeMap[i][j].size) {
				if (!nodeMap[i][j].neighborsProcessed) {
					PathNode node = nodeMap[i][j];

					int x0 = node.x - 1;
					int x1 = node.x + node.size;
					int y0 = node.y - 1;
					int y1 = node.y + node.size;

					x0 = x0 < 0 ? 0 : x0;
					x1 = x1 < nodeMap.length ? x1 : nodeMap.length - 1;
					y0 = y0 < 0 ? 0 : y0;
					y1 = y1 < nodeMap.length ? y1 : nodeMap.length - 1;

					if (y0 >= 0 && y0 != node.y) {
						for (int i1 = x0; i1 <= x1; i1 = nodeMap[i1][y0].x + nodeMap[i1][y0].size) {
							node.addNeighbor(nodeMap[i1][y0]);
						}
					}

					if (y1 < nodeMap.length && y1 != node.y + node.size - 1) {
						for (int i1 = x0; i1 <= x1; i1 = nodeMap[i1][y1].x + nodeMap[i1][y1].size) {
							node.addNeighbor(nodeMap[i1][y1]);
						}
					}

					if (x0 >= 0 && x0 != node.x) {
						for (int i1 = y0 + 1; i1 < y1; i1 = nodeMap[x0][i1].y + nodeMap[x0][i1].size) {
							node.addNeighbor(nodeMap[x0][i1]);
						}
					}

					if (x1 < nodeMap.length && x1 != node.x + node.size - 1) {
						for (int i1 = y0 + 1; i1 < y1; i1 = nodeMap[x1][i1].y + nodeMap[x1][i1].size) {
							node.addNeighbor(nodeMap[x1][i1]);
						}
					}

					nodeMap[i][j].neighborsProcessed = true;
					node.finalizeNeighbors();
				}
			}
		}
	}

	public void calcNewIteration() {
		if (goalFound) return;

		for (int i = 0; i < openList.size(); i++) {
			openList.get(i).calcF(bX, bY);
		}

		int lowestF = 0;

		for (int i = 0; i < openList.size(); i++) {
			if (openList.get(i).f <= openList.get(lowestF).f) lowestF = i;
		}

		updateAroundNode(lowestF);
	}

	public void updateAroundNode(int nodeIndex) {
		PathNode node = openList.get(nodeIndex);

		//node.open = false;
		openList.remove(nodeIndex);
		closedList.add(node);

		for (int i = 0; i < node.neighbors.length; i++) {
			PathNode tempNode = nodeMap[node.neighbors[i].x][node.neighbors[i].y];
			int posOpenList = positionInList(openList, tempNode);
			int posClosedList = positionInList(closedList, tempNode);
			if (isWalkable(node, tempNode)) {
				if (posClosedList == -1 && posOpenList == -1) {

					tempNode.g = node.g + node.neighborDistance[i];
					tempNode.parent = node;
					openList.add(tempNode);

					if (tempNode.x == bX && tempNode.y == bY) {
						goalFound = true;
						calcFinalPath(tempNode);
					}

				} else if (posClosedList == -1 && posOpenList != -1) {
					if (tempNode.g > node.g + node.neighborDistance[i]) {
						tempNode.g = node.g + node.neighborDistance[i];
						tempNode.parent = node;
						openList.add(tempNode);

						if (tempNode.x == bX && tempNode.y == bY) {
							goalFound = true;
							calcFinalPath(tempNode);
						}
					}
				}
			}
		}

		/*for (int i = x1; i < x2; i++) {
			for (int j = y1; j < y2; j++) {
				if (isWalkable(node.x, node.y, i, j)) {
					int newG;
					if (i - node.x != 0 && j - node.y != 0) {
						newG = node.g + 14;
					} else {
						newG = node.g + 10;
					}

					if (nodeMap[i][j] == null) {
						PathNode tempNode = new PathNode(i, j);
						tempNode.g = newG;
						tempNode.parent = node;
						nodeMap[i][j] = tempNode;
						openList.add(tempNode);
						if (tempNode.x == bX && tempNode.y == bY) {
							goalFound = true;
							calcFinalPath(tempNode);
						}
					} else if (nodeMap[i][j].open) {
						if (nodeMap[i][j].g > newG) {
							nodeMap[i][j].g = newG;
							nodeMap[i][j].parent = node;
						}
					}
				}
			}
		}*/
	}

	public int positionInList(ArrayList<PathNode> list, PathNode node) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).x == node.x && list.get(i).y == node.y) return i;
		}

		return -1;

	}

	public boolean isWalkable(PathNode source, PathNode target) {

		if (!(source.walkable && target.walkable)) return false;

		if (target.x >= source.x && target.x < source.x + source.size) {
			return true;
		} else if (target.y >= source.y && target.y < source.y + source.size) {
			return true;
		} else {
			for (int i = (source.x < target.x ? source.x : target.x); i <= (source.x < target.x ? target.x : source.x); i++) {
				for (int j = (source.y < target.y ? source.y : target.y); j <= (source.y < target.y ? target.y : source.y); j += nodeMap[i][j].size) {
					if (!walkableMap[i][j]) { return false; }
				}
			}
		}

		return true;
	}

	/*public boolean isWalkable(int sX, int sY, int tX, int tY) {
		for (int i = (sX < tX ? sX : tX); i <= (sX < tX ? tX : sX); i++) {
			for (int j = (sY < tY ? sY : tY); j <= (sY < tY ? tY : sY); j++) {
				if (!walkableMap[i][j]) { return false; }
			}
		}
		return true;
	}*/

	public void calcFinalPath(PathNode node) {
		pathLength = node.g;
		while (node.parent != null) {
			finalPath.add(node);
			node = node.parent;
		}

	}

	////////////////////////////////////////////////////////////////
	//RENDER
	////////////////////////////////////////////////////////////////

	public void renderCells() {
		for (int i = 0; i < nodeMap.length; i++) {
			for (int j = 0; j < nodeMap.length; j++) {
				if (walkableMap[i][j]) {
					renderCell(nodeMap[i][j], 0xffffff);
				} else {
					renderCell(nodeMap[i][j], 0x000000);
				}
			}
		}
	}

	public void renderCell(PathNode node, int color) {
		renderCell(node, color, false);
	}

	public void renderCell(PathNode node, int color, boolean force) {
		if (node.drawn == renderCycle && !force) {
			return;
		} else {
			node.drawn = renderCycle;
		}

		for (int i = node.x * 4; i < (node.x + node.size) * 4; i++) {
			for (int j = node.y * 4; j < (node.y + node.size) * 4; j++) {
				tilesheetDraw.setPixel(i, j, color);
			}
		}

		for (int i = node.x * 4; i < (node.x + node.size) * 4; i++) {
			tilesheetDraw.setPixel(i, node.y * 4, 0xaaaaaa);
		}

		for (int i = node.y * 4; i < (node.y + node.size) * 4; i++) {
			tilesheetDraw.setPixel(node.x * 4, i, 0xaaaaaa);
		}
		renderCycle++;
	}

	public void renderLists() {
		for (int i = 0; i < openList.size(); i++) {
			renderCell(openList.get(i), 0xaaaaff, true);
		}

		for (int i = 0; i < closedList.size(); i++) {
			renderCell(closedList.get(i), 0xaaffaa, true);
		}

		for (int i = 0; i < finalPath.size(); i++) {
			renderCell(finalPath.get(i), 0xffaaff, true);
		}
	}

	private void setPixel(int x, int y, int color) {
		for (int i2 = x * 4; i2 < (x + 1) * 4; i2++) {
			for (int j2 = y * 4; j2 < (y + 1) * 4; j2++) {
				tilesheetDraw.setPixel(i2, j2, color);
			}
		}
	}

	public void render(Render render, int[] pixels) {
		renderCells();
		renderLists();

		setPixel(aX, aY, 0x00ff00);
		setPixel(bX, bY, 0xff0000);
		/*setPixel(56, 112, 0xaaaa00);
		setPixel(64, 116, 0xaaaa00);
		System.out.println(isWalkable(nodeMap[56][112], nodeMap[64][116]));*/

		if (!imageSaved) {
			tilesheetDraw.saveTileSheet("test.png");
			imageSaved = true;
		}

	}

}
