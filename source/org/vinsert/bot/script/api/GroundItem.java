package org.vinsert.bot.script.api;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.generic.Hullable;
import org.vinsert.bot.script.api.generic.Interactable;
import org.vinsert.bot.script.callback.PersistentModelCache;
import org.vinsert.bot.util.ConvexHull;
import org.vinsert.bot.util.Perspective;
import org.vinsert.bot.util.Utils;
import org.vinsert.bot.util.Vec3;
import org.vinsert.insertion.IGroundLayer;
import org.vinsert.insertion.IRenderable;
import org.vinsert.insertion.ISceneTile;

/**
 * A representation of an item laying on the ground.
 * @author `Discardedx2
 */
public class GroundItem extends Item implements Hullable, Interactable {

	/**
	 * The tile that this ground item is laying on.
	 */
	private Tile location;
	/**
	 * The height of this model.
	 */
	private int height;
	/**
	 * The script context.
	 */
	private ScriptContext ctx;

	public GroundItem(ScriptContext ctx, int id, int amount, Tile tile) {
		super(id, amount);
		this.ctx = ctx;
		this.location = tile;
	}

	/**
	 * Represents the different model types, as if a pile of models was a stack.
	 * The top type is the model on the top of the stack, where the bottom is the least
	 * prioritized model in the stack.
	 * @author `Discardedx2
	 */
	public static enum ModelStackType {
		TOP, MIDDLE, BOTTOM;
	}

	/**
	 * @return The floor model representing this ground item
	 */
	public Model getModel(ModelStackType type) {
		int x = location.getX() - ctx.getClient().getOriginX();
		int y = location.getY() - ctx.getClient().getOriginY();

		ISceneTile tile = ctx.getClient().getScene().getSceneTiles()[ctx.getClient().getPlane()][x][y];

		if (tile == null) {
			return null;
		}

		IGroundLayer layer = tile.getGroundLayer();
		if (layer == null) {
			return null;
		}

		IRenderable rend = null;

		switch(type) {
		case TOP:
			rend = layer.getTop();
			break;
		case MIDDLE:
			rend = layer.getMiddle();
			break;
		case BOTTOM:
			rend = layer.getBottom();
			break;
		}

		if (rend == null) {
			return null;
		}

		this.height = rend.getModelHeight();
		return new Model(PersistentModelCache.table.get(rend));
	}

	/**
	 * Gets all the points in the model.
	 * @return The model points.
	 */
	public Point[] getPoints() {
		Model model = getModel(ModelStackType.TOP);

		if (model == null || !model.isValid()) {
			return null;
		}
		List<Point> points = new ArrayList<Point>();
		/*
		 * Generate a list of all model vertices
		 */
		//instead of using getvectors we can deal directly with vertex arrays, but no point yet
		Vec3[][] vectors = getModel(ModelStackType.TOP).getVectors();

		int gx = (location.getGx() << 7) + 64;
		int gy = (location.getGy() << 7) + 64;
		for (Vec3[] vecs : vectors) {
			Vec3 pa = vecs[0];
			Vec3 pb = vecs[1];
			Vec3 pc = vecs[2];

			Point a = Perspective.trans_tile_cam(ctx
					.getClient(), gx + (int) pa.x, gy + (int) pc.x,
					0 - (int) pb.x);
			Point b = Perspective.trans_tile_cam(ctx
					.getClient(), gx + (int) pa.y, gy + (int) pc.y,
					0 - (int) pb.y);
			Point c = Perspective.trans_tile_cam(ctx
					.getClient(), gx + (int) pa.z, gy + (int) pc.z,
					0 - (int) pb.z);
			points.add(a);
			points.add(b);
			points.add(c);
		}
		return points.toArray(new Point[0]);
	}

	/**
	 * Generates a convex hull outlining the click radius of this actor's model
	 * @return
	 */
	@Override
	public Polygon hull() {
		Point[] points = getPoints();
		Model model = getModel(ModelStackType.TOP);
		if (points == null || model == null || !model.isValid()) return null;

		/*
		 * Generate a convex hull from the model vertices
		 */
		Point[] hull = ConvexHull.getConvexHull(points);
		if (hull == null || hull.length == 0) {
			return null;
		}

		/*
		 * Convert the hull into a polygon
		 */
		int[] x = new int[hull.length];
		int[] y = new int[hull.length];
		for (int i = 0; i < hull.length; i++) {
			Point p = hull[i];
			x[i] = p.x;
			y[i] = p.y;
		}

		//create the polygon
		Polygon poly = new Polygon(x, y, x.length);
		return poly;
	}

	@Override
	public Point hullPoint(Polygon poly) {
		/*
		 * Find the minimum x, y and maximum x, y vertices
		 */
		int minX = -1;
		int minY = -1;
		int maxX = -1;
		int maxY = -1;
		for (int i = 0; i < poly.npoints; i++) {
			int px = poly.xpoints[i];
			int py = poly.ypoints[i];
			if (minX == -1 || minY == -1 || maxX == -1 || maxY == -1) {
				minX = px;
				maxX = px;
				minY = py;
				maxY = py;
			}

			if (px < minX) minX = px;
			if (px > maxX) maxX = px;
			if (py < minY) minY = py;
			if (py > maxY) maxY = py;
		}

		//safety checks
		if (minX <= 0) minX = 1;
		if (minY <= 0) minY = 1;
		if (maxX <= 0) maxX = 2;
		if (maxY <= 0) maxY = 2;

		/*
		 * Generate a random point within the polygon in regards
		 * to the min/max vertices of the polygon
		 */
		Point gen = null;
		long start = System.currentTimeMillis();
		while (gen == null) {
			int rndX = Utils.random(minX, maxX);
			int rndY = Utils.random(minY, maxY);
			if (poly.contains(rndX, rndY)) {
				gen = new Point(rndX, rndY);
			}
			if (System.currentTimeMillis() - start > 10) {
				//to avoid deadlocks for uncalculateable points
				gen = new Point(-1, -1);
			}
		}

		return gen;
	}

	/**
	 * @return The tile this ground item is located on
	 */
	public Tile getLocation() {
		return location;
	}

	/**
	 * Gets the height of this ground item.
	 * @return The height.
	 */
	public int getHeight() {
		return height;
	}

	@Override
	public boolean interact(String action) {
		
		int speed = ctx.mouse.getSpeed();
		ctx.mouse.setSpeed(speed - 4);
		
		Point point = hullPoint(hull());
		ctx.mouse.move(point.x, point.y);
		Utils.sleep(Utils.random(15, 35));

		int index = ctx.menu.getIndex(action);
		
		if (index == 0) {
			ctx.mouse.click();
			Utils.sleep(Utils.random(200, 400));
			ctx.mouse.setSpeed(speed);
			return true;
		}
		
		ctx.mouse.click(true);

		if (index != -1) {
			Point menuPoint = ctx.menu.getClickPoint(index);
			ctx.mouse.click(menuPoint.x, menuPoint.y);
			Utils.sleep(Utils.random(350, 650));
			ctx.mouse.setSpeed(speed);
			return true;
		}
		
		ctx.mouse.setSpeed(speed);
		return false;
	}

}
