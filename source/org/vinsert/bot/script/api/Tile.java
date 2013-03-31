package org.vinsert.bot.script.api;

import org.vinsert.bot.util.Utils;

/**
 * Represents a tile location in the game world
 * @author tommo
 *
 */
public class Tile {
	
	private int x;
	private int y;
	private int gx;
	private int gy;
	
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Tile(int x, int y, int gx, int gy) {
		this.x = x;
		this.y = y;
		this.gx = gx;
		this.gy = gy;
	}

	/**
	 * @return The absolute x coordinate
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * The absolute y coordinate
	 * @return
	 */
	public int getY() {
		return y;
	}

	public int getGx() {
		return gx;
	}

	public int getGy() {
		return gy;
	}
	
	/**
	 * Returns the estimated distance to another location
	 * @param other The tile to calculate the distance to
	 * @return The distance between this and the other tile in tiles
	 */
	public int distanceTo(Tile other) {
		return (int) Math.sqrt(Math.pow(other.getX() - x, 2) + Math.pow(other.getY() - y, 2));
	}
	
	/**
	 * Deviates a tile by a random offset
	 * @param tile
	 * @param deviationX The maximum x offset
	 * @param deviationY The maximum y offset
	 * @return The new, randomized tile
	 */
	public Tile deviate(Tile tile, int deviationX, int deviationY) {
		if (deviationX == 0 && deviationY == 0) return tile;
		int x = tile.getX() + (-deviationX + Utils.random(0, deviationX * 2));
		int y = tile.getY() + (-deviationY + Utils.random(0, deviationY * 2));
		return new Tile(x, y);
	}
	
	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Tile)) return false;
		Tile o = (Tile) other;
		return (o.getX() == x && o.getY() == y);
	}
	
}
