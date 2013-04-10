package org.vinsert.bot.script.api;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.util.Perspective;
import org.vinsert.bot.util.Utils;
import org.vinsert.insertion.IClient;

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
	 * @param ctx The current script context
	 * @return The tile's onscreen polygon
	 */
	
	public Polygon getPolygon(ScriptContext ctx) {
		IClient client = ctx.getClient();
		int plane = client.getPlane();
		
		Point p1 = Perspective.trans_tile_screen(client, this, 0, 0, plane);
		Point p2 = Perspective.trans_tile_screen(client, this, 1, 0, plane);
		Point p3 = Perspective.trans_tile_screen(client, this, 1, 1, plane);
		Point p4 = Perspective.trans_tile_screen(client, this, 0, 1, plane);
		
		Polygon p = new Polygon();
		p.addPoint(p1.x, p1.y);
		p.addPoint(p2.x, p2.y);
		p.addPoint(p3.x, p3.y);
		p.addPoint(p4.x, p4.y);
		
		return p;
	}
	
	/**
	 * Draws the tile on screen, if visible
	 * <tt>Method for use in render method</tt>
	 * @param g The Graphics
	 * @param ctx The current script context
	 */
	public void draw(Graphics2D g, ScriptContext ctx) {
		if (!Perspective.on_screen(ctx.getClient(), this)) {
			return;
		}
		g.setColor(new Color(0, 255, 0, 150));
		g.draw(getPolygon(ctx));
		g.setColor(new Color(0, 255, 0, 50));
		g.fill(getPolygon(ctx));
	}
	
	/**
	 * Draws the tile on the minimap, if visible
	 * @param g The Graphics
	 * @param ctx The current script context
	 */
	public void drawOnMinimap(Graphics2D g, ScriptContext ctx) {
		if (!Perspective.on_minimap(ctx.getClient(), this)) {
			return;
		}
		Point p = getMinimapPoint(ctx);
		Rectangle r = new Rectangle(p.x - 1, p.y - 1 , 3, 3);
		g.setColor(new Color(0, 255, 0, 150));
		g.drawRect(r.x, r.y, r.width, r.height);
		g.setColor(new Color(0, 255, 0, 50));
		g.fillRect(r.x, r.y, r.width, r.height);
	}
	
	/**
	 * Gets the tile point on the minimap
	 * @param ctx The current script context
	 * @return The point on the map
	 */
	public Point getMinimapPoint(ScriptContext ctx) {
		return Perspective.trans_tile_minimap(ctx.getClient(), x, y);
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
