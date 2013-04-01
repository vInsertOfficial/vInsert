package org.vinsert.bot.script.api;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.generic.Hullable;
import org.vinsert.bot.script.api.generic.Interactable;
import org.vinsert.bot.script.callback.ModelCallback;
import org.vinsert.bot.script.callback.PersistentModelCache;
import org.vinsert.bot.util.ConvexHull;
import org.vinsert.bot.util.Perspective;
import org.vinsert.bot.util.Utils;
import org.vinsert.bot.util.Vec3;
import org.vinsert.insertion.IFloorDecoration;
import org.vinsert.insertion.IModel;
import org.vinsert.insertion.IOffScreenModel;
import org.vinsert.insertion.ISceneObject;
import org.vinsert.insertion.IWall;
import org.vinsert.insertion.IWallDecoration;


/**
 * Wraps the SceneObject class in the client
 * @author tommo
 * @author `Discardedx2
 */
public class GameObject implements Interactable, Hullable {

	private ScriptContext ctx;
	private ISceneObject object;
	private IFloorDecoration floorDecoration;
	private IWallDecoration wallDecoration;
	private IWall boundary;
	private Type type;
	private int plane;
	private Tile location;
	private int id;

	public GameObject(ScriptContext ctx, ISceneObject obj, Type type, int plane, Tile location) {
		this(ctx, type, plane, location);
		this.id = (obj.getObjectHash() >> 14) & 0x7FFF;
		this.object = obj;
	}

	public GameObject(ScriptContext ctx, IFloorDecoration dec, Type type, int plane, Tile location) {
		this(ctx, type, plane, location);
		this.id = (dec.getObjectHash() >> 14) & 0x7FFF;
		this.floorDecoration = dec;
	}

	public GameObject(ScriptContext ctx, IWallDecoration dec, Type type, int plane, Tile location) {
		this(ctx, type, plane, location);
		this.id = (dec.getObjectHash() >> 14) & 0x7FFF;
		this.wallDecoration = dec;
	}

	public GameObject(ScriptContext ctx, IWall boundary, Type type, int plane, Tile location) {
		this(ctx, type, plane, location);
		this.id = (boundary.getObjectHash() >> 14) & 0x7FFF;
		this.boundary = boundary;
	}

	public GameObject(ScriptContext ctx, Type type, int plane, Tile location) {
		this.ctx = ctx;
		this.type = type;
		this.plane = plane;
		this.location = location;
	}

	/**
	 * @return The object's model
	 */
	public Model getModel() {
		switch(type) {
		case INTERACTABLE:
			if (object.getRenderable() instanceof IOffScreenModel) {
				ModelCallback.callback(object.getRenderable(), (IOffScreenModel) object.getRenderable());
			} else if (object.getRenderable() instanceof IModel) {
				ModelCallback.callback(object.getRenderable(), (IModel) object.getRenderable());
			}
			return new Model(PersistentModelCache.table.get(object.getRenderable()));
		case FLOOR_DECORATION:
			if (floorDecoration.getRenderable() instanceof IModel) {
				ModelCallback.callback(floorDecoration.getRenderable(), (IModel) floorDecoration.getRenderable());
			}
			return new Model(PersistentModelCache.table.get(floorDecoration.getRenderable()));
		case WALL_DECORATION:
			if (wallDecoration.getRenderable() instanceof IModel) {
				ModelCallback.callback(wallDecoration.getRenderable(), (IModel) wallDecoration.getRenderable());
			}
			return new Model(PersistentModelCache.table.get(wallDecoration.getRenderable()));
		case BOUNDARY:
			if (boundary.getRenderable() instanceof IModel) {
				ModelCallback.callback(boundary.getRenderable(), (IModel) boundary.getRenderable());
			}
			return new Model(PersistentModelCache.table.get(boundary.getRenderable()));
		}		
		return null;
	}

	/**
	 * Gets the object's id.
	 * @return the id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return The location of the object
	 */
	public Tile getLocation() {
		return location;
	}

	/**
	 * Gets the collision flags of this object.
	 * @return The collision flags.
	 */
	public int getFlags() {
		return object.getFlags();
	}

	/**
	 * Gets the object's orientation.
	 * @return
	 */
	public int getOrientation() {
		return object.getOrientation();
	}

	/**
	 * Gets this object's hashed data.
	 * @return The hashed data.
	 */
	public int getHash() {
		return object.getObjectHash();
	}

	/**
	 * @return The object {@link Type}
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return The plane the object is located on
	 */
	public int getPlane() {
		return plane;
	}

	public Point[] getPoints() {
		Model model = getModel();

		if (model == null || !model.isValid()) {
			return null;
		}
		List<Point> points = new ArrayList<Point>();
		/*
		 * Generate a list of all model vertices
		 */
		//instead of using getvectors we can deal directly with vertex arrays, but no point yet
		Vec3[][] vectors = getModel().getVectors();
		int gx = getLocation().getGx();
		int gy = getLocation().getGy();
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
	public Polygon hull() {
		Point[] points = getPoints();
		Model model = getModel();
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
			if (poly.contains(rndX, rndY) && Perspective.on_screen(rndX, rndY)) {
				gen = new Point(rndX, rndY);
			}
			if (System.currentTimeMillis() - start > 20) {
				//to avoid deadlocks for uncalculateable points
				gen = new Point(-1, -1);
			}
		}

		return gen;
	}
	
	/**
	 * Left clicks the game object, useful because menu interaction takes a bit of time to sync
	 */
	public void click() {
		Point p = hullPoint(hull());
		ctx.mouse.click(p.x, p.y);
	}

	@Override
	public boolean interact(String action) {
		Point p = hullPoint(hull());
		ctx.mouse.move(p.x, p.y);
		Utils.sleep(150, 250);
		int index = ctx.menu.getIndex(action);
		if (index == 0) {
			ctx.mouse.click(p.x, p.y);
			Utils.sleep(Utils.random(350, 650));
			return true;
		} else if (index > 0) {
			ctx.menu.click(index);
			return true;
		}

		return false;
	}

	/**
	 * Represents an {@link GameObject} type
	 * @author tommo
	 *
	 */
	public static enum Type {
		INTERACTABLE, FLOOR_DECORATION, BOUNDARY, WALL_DECORATION;
	}

}
