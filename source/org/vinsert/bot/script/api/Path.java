package org.vinsert.bot.script.api;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.util.Perspective;

/**
 * Represents a path consisting of tiles
 * <p>
 * This class retains the active traversal state, but does no traversal inside.
 * The actual traversal of the path is done by the <code>navigate()</code> method inside the navigation class
 * @author tommo
 *
 */
public class Path {
	
	private Tile[] tiles;
	private int current = 0;
	private Tile target;
	private int targetThreshold = -1;
	
	/**
	 * Constructs a new path with a given amount of tiles
	 * @param tiles The tiles which make up the path. Note: the tiles must be a reachable distance from
	 * each other, else the path will not be traversable
	 */
	public Path(Tile ... tiles) {
		this.tiles = tiles;
		this.current = 0;
	}
	
	/**
	 * Resets the path, ready to re-traversal
	 */
	public void reset() {
		this.current = 0;
		this.target = null;
		this.targetThreshold = -1;
	}
	
	/**
	 * Advances the tile index
	 */
	public void advance() {
		current++;
	}
	
	/**
	 * @return The previous tile, or null if out of bounds
	 */
	public Tile getPrevious() {
		if (current - 1 < 0) {
			return null;
		}
		return tiles[current - 1];
	}
	
	/**
	 * @return The next tile in the path, or null if the path is finished
	 */
	public Tile getNext() {
		if (current >= tiles.length) {
			return null;
		}
		return tiles[current];
	}
	
	/**
	 * @return The tile at the end of the path
	 */
	public Tile getFinish() {
		return tiles[tiles.length - 1];
	}
	
	/**
	 * @return The active tile target, or null if none
	 */
	public Tile getTarget() {
		return target;
	}

	/**
	 * Sets the new active tile target
	 * @param target
	 * @param the transition threshold
	 */
	public void setTarget(Tile target, int threshold) {
		this.target = target;
		this.targetThreshold = threshold;
	}

	/**
	 * @return The tiles which make up this path
	 */
	public Tile[] getTiles() {
		return tiles;
	}

	public int getTargetThreshold() {
		return targetThreshold;
	}
	
	public void setCurrent(int current) {
		this.current = current;
	}

	/**
	 * Returns the index of a tile in the path
	 * @param tile
	 * @return
	 */
	public int indexOf(Tile tile) {
		for (int i = 0; i < tiles.length; i++) {
			Tile t = tiles[i];
			if (t.equals(tile)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Returns a new path, which is a randomized reverse of the original path
	 * <p>
	 * Note: The original path isn't modified
	 * @return The newly reversed path
	 */
	public Path reverse() {
		List<Tile> reversed = new ArrayList<Tile>();
		for (int i = tiles.length - 1; i >= 0; i--) {
			reversed.add(tiles[i]);
		}
		return new Path(reversed.toArray(new Tile[reversed.size()]));
	}

	/**
	 * Draws the path on the screen
	 * @param g The graphics
	 * @param ctx The current script context
	 */
	public void draw(Graphics2D g, ScriptContext ctx) {
		for (Tile t : tiles) {
			t.draw(g, ctx);
		}
	}
	
	/**
	 * Draws the path on the minimap
	 * @param g The Graphics
	 * @param ctx The current script context
	 */
	public void drawOnMinimap(Graphics2D g, ScriptContext ctx) {
		if (tiles.length <= 0) {
			return;
		}
		for (int i = 1; i < tiles.length - 1; i++) {
			tiles[i - 1].drawOnMinimap(g, ctx);
			if (i == tiles.length - 1) {
				tiles[i].drawOnMinimap(g, ctx);
			}
			g.setColor(new Color(0, 255, 0, 50));
			Point current = tiles[i - 1].getMinimapPoint(ctx);
			Point next = tiles[i].getMinimapPoint(ctx);
			if (!(Perspective.on_minimap(current) && Perspective.on_minimap(next))) {
				g.drawLine(current.x, current.y, next.x, next.y);
			}
		}
	}
}
