package org.vinsert.bot.script.api;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

/*
 * Creates a polygonal or rectangular area based on two or more tiles.
 * 
 * @author Spring
 * 
 */
public class Area {

	private Polygon area;

	/*
	 * Creates an area based on multiple tiles allowing polygonal areas
	 */

	public Area(Tile... tiles) {
		area = toPolygon(tiles);
	}

    /*
	 * Creates an area based on multiple tiles allowing polygonal areas
	 */

    public Area(Tile sw, Tile ne) {
        area = toPolygon(sw, ne);
    }

	/**
	 * Converts a tile array to a java.awt.Polygon;
	 */

	private Polygon toPolygon(Tile... tiles) {
		final Polygon area = new Polygon();
		for (Tile tile : tiles)
			area.addPoint(tile.getX(), tile.getY());
		return area;
	}

	/**
	 * Returns a java.awt.Rectangle that surrounds the polygon
	 */
	
	public Rectangle getBounds() {
		if (area.npoints < 1)
			return null;
		return new Rectangle(area.getBounds().x, area.getBounds().y,
				area.getBounds().width, area.getBounds().height);
	}

	/**
	 * Checks if an actor is contained in this area
	 */
	public boolean contains(Actor actor) {
		return contains(actor.getLocation());
	}

	/**
	 * Checks if an object is contained in this area
	 */
	public boolean contains(GameObject object) {
		return contains(object.getLocation());
	}

	/**
	 * Checks if a ground item is contained in this area
	 */
	public boolean contains(GroundItem item) {
		return contains(item.getLocation());
	}

	/**
	 * Checks if the area contains a tile
	 */
	public boolean contains(Tile loc) {
		return area.contains(new Point(loc.getX(), loc.getY()));
	}

	/**
	 * Gets the tile at the center of this area.
	 */
	public Tile getCenter() {
		if (area.npoints < 1)
			return null;
		int centerX = 0;
		int centerY = 0;
		for (int i = 0; i < area.npoints; i++) {
			centerX += area.xpoints[i];
			centerY += area.ypoints[i];
		}
		return new Tile(Math.round(centerX / area.npoints), Math.round(centerY
				/ area.npoints));
	}

	/**
	 * Returns the bottom left tile (south-west) based on the bounding box of
	 * the polygon (or rectangle)
	 */

	public Tile getBottomLeft() {
		return new Tile(getBounds().x, getBounds().y);
	}

	/**
	 * Returns the top right tile (north-east) based on the bounding box of the
	 * polygon (or rectangle)
	 */

	public Tile getTopRight() {
		return new Tile(getBounds().x + getBounds().width, getBounds().y
				+ getBounds().height);
	}

	@Override
	public String toString() {
		return "Bounding box: [" + getBottomLeft().toString() + ", "
				+ getTopRight().toString() + "]";
	}

}
