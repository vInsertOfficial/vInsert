package org.vinsert.bot.script.api;

/**
 * A rectangular area defined by 2 points, the bottom left and bottom right
 * @author tommo
 *
 */
public class Area {
	
	private Tile bottomLeft;
	private Tile topRight;
	
	/**
	 * Creates a new rectangular area defined by the 2 given points
	 * @param bottomLeft The south-western corner tile
	 * @param topRight The north-eastern corner tile
	 */
	public Area(Tile bottomLeft, Tile topRight) {
		this.bottomLeft = bottomLeft;
		this.topRight = topRight;
	}
	
	/**
	 * Checks if an actor is within this area
	 * @return true if the actor is inside, false if not
	 */
	public boolean contains(Actor actor) {
		return contains(actor.getLocation());
	}
	
	/**
	 * Checks if an object is within this area
	 * @return true if the object is inside, false if not
	 */
	public boolean contains(GameObject object) {
		return contains(object.getLocation());
	}
	
	/**
	 * Checks if a ground item is within this area
	 * @return true if the ground item is inside, false if not
	 */
	public boolean contains(GroundItem item) {
		return contains(item.getLocation());
	}
	
	/**
	 * Checks if a tile is within this area
	 * @return true if the tile is inside, false if not
	 */
	public boolean contains(Tile loc) {
		if (loc.getX() >= bottomLeft.getX() && loc.getX() <= topRight.getX()
				&& loc.getY() >= bottomLeft.getY() && loc.getY() <= topRight.getY()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the tile at the center of this area.
	 * @return The center of this area.
	 */
	public Tile getCenter() { 		
		return new Tile(bottomLeft.getX() + Math.round((topRight.getX() - bottomLeft.getX()) / 2), bottomLeft.getY() + Math.round((topRight.getY() - bottomLeft.getY()) / 2)); 	}
	
	/**
	 * Calculates the effective area covered
	 * @return The area, in tiles
	 */
	public int getArea() {
		int width = topRight.getX() - bottomLeft.getX();
		int height = topRight.getY() - bottomLeft.getX();
		return width * height;
	}

	public Tile getBottomLeft() {
		return bottomLeft;
	}

	public void setBottomLeft(Tile bottomLeft) {
		this.bottomLeft = bottomLeft;
	}

	public Tile getTopRight() {
		return topRight;
	}

	public void setTopRight(Tile topRight) {
		this.topRight = topRight;
	}

}
