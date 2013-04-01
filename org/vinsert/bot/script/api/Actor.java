package org.vinsert.bot.script.api;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.generic.Interactable;
import org.vinsert.bot.util.ConvexHull;
import org.vinsert.bot.util.Perspective;
import org.vinsert.bot.util.Utils;
import org.vinsert.bot.util.Vec3;
import org.vinsert.insertion.IActor;


/**
 * Wraps the {@link IActor} class in the client.
 * @author tommo
 * @author `Discardedx2
 *
 */
public abstract class Actor extends Renderable implements Interactable {

	private ScriptContext ctx;
	private IActor actor;

	public Actor(ScriptContext ctx, IActor actor) {
		super(ctx, actor);
		this.ctx = ctx;
		this.actor = actor;
	}
	
	/**
	 * @return The actor's orientation
	 */
	public int getOrientation() {
		return actor.getOrientation();
	}

	/**
	 * Note: only works when the combat bar is visible
	 * @return The actor's health
	 */
	public int getHealth() {
		return actor.getHealth();
	}

	/**
	 * Note: only works when the combat bar is visible
	 * @return The actor's current health percentage
	 */
	public int getHealthPercent() {
		return 100 / (actor.getMaxHealth() / actor.getHealth());
	}

	/**
	 * Note: only works when the combat bar is visible
	 * @return The actor's max health
	 */
	public int getMaxHealth() {
		return actor.getMaxHealth();
	}

	/**
	 * Checks if this actor is dead.
	 * @return {@code true} if the actor is dead.
	 */
	public boolean isDead() {
		return getHealth() == 0 || getHealthPercent() <= 0;
	}

	/**
	 * Checks if the actor is currently in combat
	 * NOTE: Currently broken
	 * @return true if the actor is in combat, false if not
	 */
	public boolean isInCombat() {
		return ctx.getClient().getCycle() < actor.getCycle();
	}

	/**
	 * @return <b>true</b> if the actor is currently animating, <b>false</b> of not
	 */
	public boolean isAnimating() {
		return actor.getAnimation() != -1;
	}

	/**
	 * @return The current animation id, if applicable
	 */
	public int getAnimation() {
		return actor.getAnimation();
	}

	/**
	 * @return The actor name
	 */
	public abstract String getName();

	/**
	 * @return The actor's current overhead speech, if applicable.
	 */
	public String getSpeech() {
		return actor.getSpeech();
	}

	/**
	 * Checks if the actor is moving, that is, if it's walking or running.
	 * @return true if so, false if not
	 */
	public boolean isMoving() {
		return actor.getQueueXPos() != 0 || actor.getQueueYPos() != 0;
	}

	/**
	 * @return The walking queue x array
	 */
	public int[] getWalkingQueueX() {
		return actor.getQueueX();
	}

	/**
	 * @return The walking queue y array
	 */
	public int[] getWalkingQueueY() {
		return actor.getQueueY();
	}

	/**
	 * @return The walking queue x position
	 */
	public int getWalkingQueueXPos() {
		return actor.getQueueXPos();
	}

	/**
	 * @return The walking queue y position
	 */
	public int getWalkingQueueYPos() {
		return actor.getQueueYPos();
	}

	/**
	 * @return <b>true</b> if the actor is interacting with another actor, <b>false</b> if not.
	 */
	public boolean isInteracting() {
		return getInteractingIndex() != -1;
	}

	/**
	 * @return The {@link Actor} this actor is interacting with, if applicable
	 */
	public Actor getInteracting() {
		if (actor.getInteracting() == -1) {
			return null;
		}

		if (actor.getInteracting() < 32768) {
			return new Npc(ctx, ctx.getClient().getNpcs()[actor.getInteracting()]);
		}
		return new Player(ctx, ctx.getClient().getPlayers()[actor.getInteracting() - 32768]);
	}
	
	/**
	 * Gets the current interacting index.
	 * @return The interacting index.
	 */
	public int getInteractingIndex() {
		return actor.getInteracting();
	}

	/**
	 * @return The {@link Tile} the actor is currently located on.
	 */
	public Tile getLocation() {
		return new Tile((actor.getGridX() >> 7) + ctx.getClient().getOriginX(), (actor.getGridY() >> 7) + ctx.getClient().getOriginY(), actor.getGridX(), actor.getGridY());
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
		 * Convert the hull into a polygon, and whilst doing so, find the minimum and maximum coordinates
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


	/**
	 * Generates a random click point within the given convex hull polygon
	 * @param poly
	 * @return
	 */
	public Point hullPoint(Polygon poly) {
		if (poly == null) return new Point(-1, -1);
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
			if (System.currentTimeMillis() - start > 10) {
				//to avoid deadlocks for uncalculateable points
				gen = new Point(-1, -1);
			}
		}

		return gen;
	}

	@Override
	public boolean interact(String action) {
		Point point = hullPoint(hull());
		ctx.mouse.move(point.x, point.y);
		Utils.sleep(Utils.random(150, 250));

		int index = ctx.menu.getIndex(action);
		
		if (index == 0) {
			ctx.mouse.click();
			Utils.sleep(Utils.random(200, 400));
			return true;
		}
		
		ctx.mouse.click(true);
		if (index != -1) {
			Point menuPoint = ctx.menu.getClickPoint(index);
			ctx.mouse.click(menuPoint.x, menuPoint.y);
			Utils.sleep(Utils.random(350, 650));
			return true;
		}
		
		return false;
	}

}
