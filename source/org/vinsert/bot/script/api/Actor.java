package org.vinsert.bot.script.api;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.generic.Interactable;
import org.vinsert.bot.script.api.tools.Navigation.Directions;
import org.vinsert.bot.util.ConvexHull;
import org.vinsert.bot.util.Perspective;
import org.vinsert.bot.util.Utils;
import org.vinsert.bot.util.Vec3;
import org.vinsert.insertion.IActor;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Wraps the {@link IActor} class in the client.
 *
 * @author tommo
 * @author `Discardedx2
 */
public abstract class Actor extends Renderable implements Interactable {

    private ScriptContext ctx;
    private IActor actor;

    public Actor(ScriptContext ctx, IActor actor) {
        super(ctx, actor);
        this.ctx = ctx;
        this.actor = actor;
    }


    public boolean isFacing(int Direction) {
        /**
         * @Author Soxs
         * Checks if an Actor (entity?) is facing a navigation direction.
         * Useful for minigames such as sorceress garden.
         */
        Player me = new Player(ctx, ctx.getClient().getLocalPlayer());
        if (actor != null && isMoving()) {
            int dis = getLocation().distanceTo(me.getLocation());
            long now = System.currentTimeMillis();
            while (actor != null && dis == getLocation().distanceTo(me.getLocation())) {
                Utils.sleep(75, 100);
                //-NPC n did not move.
                if (System.currentTimeMillis() - now > 2000) {
                    return false; //waited for more than 2 seconds, times out. (lag etc)
                }
            }
            if (getDirectionTo(getLocation()) == Direction) {
                return true;
            }
        }
        return false;
    }

    public int getDirectionTo(Tile t) {
        /**
         * @author Soxs
         * Gets the direction to a specified tile with an int.
         * See Navigation.
         */
        int currentX = getLocation().getX();
        int currentY = getLocation().getY();

        int X = t.getX();
        int Y = t.getY();

        if (X > currentX && Y == currentY) {
            return Directions.EAST.value;
        } else if (X < currentX && Y == currentY) {
            return Directions.WEST.value;

        } else if (X == currentX && Y > currentY) {
            return Directions.NORTH.value;
        } else if (X == currentX && Y < currentY) {
            return Directions.SOUTH.value;

        } else if (X > currentX && Y > currentY) {
            return Directions.NORTHE.value;
        } else if (X < currentX && Y > currentY) {
            return Directions.NORTHW.value;

        } else if (X > currentX && Y < currentY) {
            return Directions.SOUTHE.value;
        } else if (X < currentX && Y < currentY) {
            return Directions.SOUTHW.value;
        }
        return 0;
    }

    /**
     * @return The actor's orientation
     */
    public int getOrientation() {
        return actor.getOrientation();
    }

    /**
     * Note: only works when the combat bar is visible
     *
     * @return The actor's health
     */
    public int getHealth() {
        return actor.getHealth();
    }

    /**
     * Note: only works when the combat bar is visible
     *
     * @return The actor's current health percentage
     */
    public int getHealthPercent() {

        if (getMaxHealth() == 0)
            return 0;

        return 100 * (getHealth() / getMaxHealth());
    }

    /**
     * Note: only works when the combat bar is visible
     *
     * @return The actor's max health
     */
    public int getMaxHealth() {
        return actor.getMaxHealth();
    }

    /**
     * Checks if this actor is dead.
     *
     * @return {@code true} if the actor is dead.
     */
    public boolean isDead() {
        return getHealth() == 0 || getHealthPercent() <= 0;
    }

    /**
     * Checks if the actor is currently in combat
     * NOTE: Currently broken
     *
     * @return true if the actor is in combat, false if not
     */
    public boolean isInCombat() {
        return actor.getCycle() > ctx.getClient().getCycle();
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
     *
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
    	int index = getInteractingIndex();
        if (index == -1) {
            return null;
        }
        if (index < 32767) {
            return new Npc(ctx, ctx.getClient().getNpcs()[index]);
        }
        return (index - 32767 > 32767 ? null : new Player(ctx, ctx.getClient().getPlayers()[index - 32767]));
    }

    /**
     * Gets the current interacting index.
     *
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
     *
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
     * Calculates the centroid point of the polygon
     *
     * @param poly
     * @return
     */
    public Point centerPoint(Polygon poly) {
        if (poly == null) return new Point(-1, -1);
        Point2D[] points = new Point2D.Double[poly.npoints];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point2D.Double(poly.xpoints[i], poly.ypoints[i]);
        }
        Point2D center = Utils.centerOfMass(points);
        return new Point((int) center.getX(), (int) center.getY());
    }

    /**
     * Generates a random click point within the given convex hull polygon
     *
     * @param poly
     * @return
     */
    public Point hullPoint(Polygon poly) {
        if (poly == null) return new Point(-1, -1);
        //Point centroid = centerPoint(poly);
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
        if (maxX <= 1) maxX = 2;
        if (maxY <= 1) maxY = 2;

		/*
		 * Generate a random point within the polygon in regards
		 * to the min/max vertices of the polygon
		 */
        Point gen = null;
        long start = System.currentTimeMillis();
        Random rand = new Random();
        int wdev = (maxX - minX) / 4;
        int hdev = (maxY - minY) / 4;
        Point centroid = centerPoint(poly);
        while (gen == null) {
            int dx = (int) Math.round(rand.nextGaussian() * wdev + centroid.x);
            int dy = (int) Math.round(rand.nextGaussian() * hdev + centroid.y);
            if (poly.contains(dx, dy) && Perspective.on_screen(dx, dy)) {
                gen = new Point(dx, dy);
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
        Utils.sleep(Utils.random(250, 400));

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
