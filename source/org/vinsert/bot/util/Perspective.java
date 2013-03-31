package org.vinsert.bot.util;

import java.awt.Point;
import java.awt.Rectangle;

import org.vinsert.bot.script.api.Tile;
import org.vinsert.insertion.IClient;
import org.vinsert.insertion.IWidget;


/**
 * Represents the different perspective utilities that we use for things such as
 * converting points between 3D and 2D space.
 * 
 * @author `Discardedx2
 * @author tommo
 */
public class Perspective {

	/**
	 * The effective screen viewport
	 */
	public static final Rectangle GAMESCREEN = new Rectangle(4, 4, 512, 334);

	/**
	 * Precalculated sine lookup table
	 */
	public static int[] SINE = new int[2048];

	/**
	 * Precalculated cosine lookup table
	 */
	public static int[] COSINE = new int[2048];

	/**
	 * Translates a point in the perspective matrix to a point on the camera
	 * matrix.
	 * 
	 * @param client
	 *            The client to translate.
	 * @param x
	 *            The x value where x = local << 7
	 * @param y
	 *            The y value where y = local << 7
	 * @param height
	 *            The z value (NOT PLANE!)
	 * @return The translated point on the camera matrix.
	 */
	public static final Point trans_tile_cam(IClient client, int x, int y,
			int height) {
		if (x < 128 || y < 128 || x > 13056 || y > 13056) {
			return new Point(-1, -1);
		}

		int z = get_tile_height(client, client.getPlane(), x, y) - height;
		x -= client.getCameraX();
		z -= client.getCameraZ();
		y -= client.getCameraY();

		int pitch_sin = SINE[client.getCamPitch()];
		int pitch_cos = COSINE[client.getCamPitch()];
		int yaw_sin = SINE[client.getCamYaw()];
		int yaw_cos = COSINE[client.getCamYaw()];

		int _angle = y * yaw_sin + x * yaw_cos >> 16;

		y = y * yaw_cos - x * yaw_sin >> 16;
		x = _angle;
		_angle = z * pitch_cos - y * pitch_sin >> 16;
		y = z * pitch_sin + y * pitch_cos >> 16;
		z = _angle;

		if (y >= 50) {
			return new Point(256 + (x << 9) / y, (_angle << 9) / y + 167);
		}

		return new Point(-1, -1);
	}
	
	
	/**
	 * Translates a tile in the 3D space to 2D minimap coordinates.
	 * @param client The client to translate from.
	 * @param x The tile x position.
	 * @param y The tile y positon.
	 * @return The 2D minimap coordinates
	 */
	public static Point trans_tile_minimap(IClient client, int x, int y) {
		IWidget minimap = client.getWidgets()[548][85];
		
		if (minimap != null) {
			x -= client.getOriginX();
			y -= client.getOriginY();
			
			final int xx = x * 4 + 2 - client.getLocalPlayer().getGridX() / 32;
			final int yy = 2 + y * 4 - client.getLocalPlayer().getGridY() / 32;
			
			int degree = client.getMapScale() + client.getCompassAngle() & 0x7FF;
			int dist = (int) (Math.pow(xx, 2) + Math.pow(yy, 2));
			
			if(dist <= 6400) {
				int sin = SINE[degree];
				int cos = COSINE[degree];
				
				cos = cos * 256 / (client.getMapOffset() + 256);
				sin = sin * 256 / (client.getMapOffset() + 256);
				
				int mx = yy * sin + cos * xx >> 16;
				int my = sin * xx - yy * cos >> 16;
				if(dist < 2500) {	
	
					final int sx = 18 + ((minimap.getX() + minimap.getHeight() / 2) + mx);
					final int sy = (minimap.getY() + minimap.getHeight() / 2 - 1) + my;
	
					return new Point(sx, sy);
				}
				
				final int screenx = 18 + ((minimap.getX() + minimap.getWidth() / 2) + mx);
				final int screeny = (minimap.getY() + minimap.getWidth() / 2 - 1) + my;
	
				return new Point(screenx, screeny);
			}
		}
		return new Point(-1, -1);
	}

	/**
	 * Applies the Euclidean Distance algorithm in 2 dimensions
	 * on a pair of {@link Tile}'s.
	 * 
	 * sqrt((x1-x2) * (x1 - x2) + (y1 - y2) * (y1 - y2))
	 * 
	 * @param src The source tile.
	 * @param dst The destination tile.
	 * @return The distance as a double.
	 */
	public static double edist_tile(Tile src, Tile dst) {
		return Math.sqrt(Math.pow(src.getX() - dst.getX(), 2) + Math.pow(src.getY() - dst.getY(), 2));
	}

	/**
	 * Converts a 3D tile to 2D coordinates.
	 * 
	 * This method works with deviation, where 0,0 represents south-westerly
	 * most corner of the tile, and 1,1 represents the most north-easterly corner of the tile.
	 * 
	 * @param client The client to convert for.
	 * @param tile The tile to convert.
	 * @param dX The deviation on the x axis where 0 <= x <= 1
	 * @param dY The deviation on the x axis where 0 <= y <= 1
	 * @param height The height of the tile.
	 * @return The translated point.
	 */
	public static Point trans_tile_screen(IClient client, Tile tile, double dX, double dY, int height) {
		return trans_tile_cam(client, (int) ((tile.getX() - client.getOriginX() + dX) * 128), (int) ((tile.getY() - client.getOriginY() + dY) * 128), height);
	}

	/**
	 * Gets the height of a tile in the current perspective.
	 * 
	 * @param client
	 *            The client we are currently using.
	 * @param plane
	 *            The plane the tile is located on.
	 * @param x
	 *            The baseX of the tile.
	 * @param y
	 *            The baseY of the tile.
	 * @return The height of a tile at a given position.
	 */
	public static final int get_tile_height(IClient client, int plane, int x,
			int y) {
		int xx = x >> 7;
		int yy = y >> 7;
		if (xx < 0 || yy < 0 || xx > 103 || yy > 103) {
			return 0;
		}

		int planea = plane;
		if (client.getSceneFlags() != null) {
			//TODO causing me fucking errors motherfucker
//			if (planea < 3 && (client.getSceneFlags()[1][xx][yy] & 0x2) == 2) {
//				planea++;
//			}
		}

		int aa = client.getVertexHeights()[planea][xx][yy] * (128 - (x & 0x7F))
				+ client.getVertexHeights()[planea][xx + 1][yy] * (x & 0x7F) >> 7;
		int ab = client.getVertexHeights()[planea][xx][yy + 1]
				* (128 - (x & 0x7F))
				+ client.getVertexHeights()[planea][xx + 1][yy + 1]
				* (x & 0x7F) >> 7;
		return aa * (128 - (y & 0x7F)) + ab * (y & 0x7F) >> 7;
	}

	/**
	 * Returns the angle to a given tile in degrees anti-clockwise from the
	 * positive x axis (where the x-axis is from west to east).
	 * 
	 * @param origin The origin position (generally player position)
	 * @param t The target tile
	 * @return The angle in degrees
	 */
	public static int tile_angle(Tile origin, Tile tile) {
		int degree = (int) Math.toDegrees(Math.atan2(tile.getY() - origin.getY(), tile.getX() - origin.getX()));
		return degree >= 0 ? degree : 360 + degree;
	}
	
	/**
	 * Checks to see if an {@link Tile} is on screen.
	 * @param client The client we are checking for.
	 * @param tile The tile.
	 * @return {@code true} if the tile is on screen.
	 */
	public static boolean on_screen(IClient client, Tile tile) {
		return on_screen(trans_tile_screen(client, tile, 0.5, 0.5, 0));
	}

	
	/**
	 * Checks to see if a 2D {@link Point} is on screen.
	 * @param client The client we are checking for.
	 * @param tile The tile.
	 * @return {@code true} if the tile is on screen.
	 */
	public static boolean on_screen(Point point) {
		return GAMESCREEN.contains(point);
	}

	/**
	 * Checks to see if a set of 2D coordinates is on screen.
	 * @param client The client we are checking for.
	 * @param tile The tile.
	 * @return {@code true} if the tile is on screen.
	 */
	public static boolean on_screen(int x, int y) {
		return GAMESCREEN.contains(x, y);
	}

	static {
		for (int i = 0; i < SINE.length; i++) {
			SINE[i] = (int) (65536.0D * Math.sin((double) i * 0.0030679615D));
			COSINE[i] = (int) (65536.0D * Math.cos((double) i * 0.0030679615D));
		}
	}

}
