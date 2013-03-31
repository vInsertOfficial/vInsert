package org.vinsert.bot.script.api.tools;

import java.awt.event.KeyEvent;

import org.vinsert.bot.InputHandler;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Actor;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.GroundItem;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.util.Perspective;
import org.vinsert.bot.util.Utils;


/**
 * Camera utilities
 * 
 * @author tommo
 * 
 */
public class Camera {

	private ScriptContext ctx;
	private InputHandler handler;

	public Camera(ScriptContext ctx) {
		this.ctx = ctx;
		this.handler = ctx.getBot().getInputHandler();
	}

	/**
	 * Turns the camera to face an actor
	 * 
	 * @param actor
	 *            The actor to face
	 */
	public void rotateToActor(Actor actor) {
		if (actor == null || actor.getLocation() == null) return;
		int angle = getAngleToTile(actor.getLocation());
		rotateAngleTo(angle);
	}
	
	/**
	 * Turns the camera to face an object
	 * 
	 * @param object
	 *            The object to face
	 */
	public void rotateToObject(GameObject object) {
		if (object == null || object.getLocation() == null) return;
		int angle = getAngleToTile(object.getLocation());
		rotateAngleTo(angle);
	}
	
	/**
	 * Turns the camera to face a tile
	 * 
	 * @param tile
	 * 			The tile to face
	 */
	public void rotateToTile(Tile tile) {
		int angle = getAngleToTile(tile);
		rotateAngleTo(angle);
	}

	/**
	 * Rotates the camera to the given angle in degrees
	 * 
	 * @param degrees The rotation in degrees to rotate to
	 */
	public void rotateAngleTo(int degrees) {
		if (getAngleTo(degrees) > 5) {
			handler.pressKey(KeyEvent.VK_LEFT);
			while (getAngleTo(degrees) > 5) {
				Utils.sleep(10);
			}
			handler.releaseKey(KeyEvent.VK_LEFT);
		} else if (getAngleTo(degrees) < -5) {
			handler.pressKey(KeyEvent.VK_RIGHT);
			while (getAngleTo(degrees) < -5) {
				Utils.sleep(10);
			}
			handler.releaseKey(KeyEvent.VK_RIGHT);
		}
	}

	/**
	 * Rotates the camera in a random direction, by a random amount
	 */
	public void rotateRandomly() {
		int targ = Utils.random(30, 330);
		rotateAngleTo(targ);
	}
	
	/**
	 * Set the camera to a certain percentage of the maximum pitch. Don't rely
	 * on the return value too much - it should return whether the camera was
	 * successfully set, but it isn't very accurate near the very extremes of
	 * the height.
	 * <p/>
	 * <p/>
	 * This also depends on the maximum camera angle in a region, as it changes
	 * depending on situation and surroundings. So in some areas, 68% might be
	 * the maximum altitude. This method will do the best it can to switch the
	 * camera altitude to what you want, but if it hits the maximum or stops
	 * moving for any reason, it will return.
	 * <p/>
	 * <p/>
	 * <p/>
	 * Mess around a little to find the altitude percentage you like. In later
	 * versions, there will be easier-to-work-with methods regarding altitude.
	 * 
	 * @param percent
	 *            The percentage of the maximum pitch to set the camera to.
	 * @return true if the camera was successfully moved; otherwise false.
	 */
	public boolean adjustPitch(int percent) {
		int curAlt = getPitch();
		int lastAlt = 0;
		if (curAlt == percent)
			return true;
		else if (curAlt < percent) {
			handler.pressKey(KeyEvent.VK_UP);
			long start = System.currentTimeMillis();
			while (curAlt < percent
					&& System.currentTimeMillis() - start < Utils.random(50,
							100)) {
				if (lastAlt != curAlt) {
					start = System.currentTimeMillis();
				}
				lastAlt = curAlt;

				Utils.sleep(Utils.random(5, 10));
				curAlt = getPitch();
			}
			handler.releaseKey(KeyEvent.VK_UP);
			return true;
		} else {
			handler.pressKey(KeyEvent.VK_DOWN);
			long start = System.currentTimeMillis();
			while (curAlt > percent
					&& System.currentTimeMillis() - start < Utils.random(50,
							100)) {
				if (lastAlt != curAlt) {
					start = System.currentTimeMillis();
				}
				lastAlt = curAlt;
				Utils.sleep(Utils.random(5, 10));
				curAlt = getPitch();
			}
			handler.releaseKey(KeyEvent.VK_DOWN);
			return true;
		}
	}

	/**
	 * Returns the angle between the current camera angle and the given angle in
	 * degrees.
	 * 
	 * @param degrees
	 *            The target angle.
	 * @return The angle between the who angles in degrees.
	 */
	public int getAngleTo(int degrees) {
		int ca = getAngle();
		if (ca < degrees) {
			ca += 360;
		}
		int da = ca - degrees;
		if (da > 180) {
			da -= 360;
		}
		return da;
	}

	/**
	 * Returns the current camera angle, with North at 0, increasing
	 * counter-clockwise to 360
	 * 
	 * @return The angle in degrees
	 */
	public int getAngle() {
		// the client uses fixed point radians 0 - 2^14
		// degrees = yaw * 360 / 2^14 = yaw / 45.5111...
		//int angle = (int) (ctx.getClient().getCamYaw() / 45.51);
//		int angle = (int) (ctx.getClient().getCamYaw() / 45.51);
//		System.out.println("angle: " + angle + ", yaw1: " + ctx.getClient().getCamYaw());
//		return angle;
		double angle = ctx.getClient().getCamYaw();
		angle /= 2048;
		angle *= 360;
		return (int) angle;
	}

	/**
	 * Returns the current percentage of the maximum pitch of the camera in an
	 * open area.
	 * 
	 * @return The current camera altitude percentage.
	 */
	public int getPitch() {
		return (int) ((ctx.getClient().getCamPitch() - 1024) / 20.48);
	}

	/**
	 * Calculates the angle needed for the camera to face a tile
	 * 
	 * @param tile
	 *            The face to face
	 * @return The angle
	 */
	public int getAngleToTile(Tile tile) {
		int a = (Perspective.tile_angle(ctx.players.getLocalPlayer()
				.getLocation(), tile) - 90) % 360;
		return a < 0 ? a + 360 : a;
	}
	
	/**
	 * Checks if an actor is currently visible on screen
	 * @param actor The actor to check
	 * @return true if the actor is visible on screen, false if not
	 */
	public boolean isVisible(Actor actor) {
		return Perspective.on_screen(ctx.getClient(), actor.getLocation());
	}
	
	/**
	 * Checks if a tile is currently visible on screen
	 * @param tile The tile to check
	 * @return true if the tile is visible on screen, false if not
	 */
	public boolean isVisible(Tile tile) {
		return Perspective.on_screen(ctx.getClient(), tile);
	}
	
	/**
	 * Checks if an object is currently visible on screen
	 * @param object The object to check
	 * @return true if the object is visible on screen, false if not
	 */
	public boolean isVisible(GameObject object) {
		return Perspective.on_screen(ctx.getClient(), object.getLocation());
	}
	
	/**
	 * Checks if a ground item is currently visible on screen
	 * @param item The item to check
	 * @return true if the item is visible on screen, false if not
	 */
	public boolean isVisible(GroundItem item) {
		return Perspective.on_screen(ctx.getClient(), item.getLocation());
	}
	
	/**
	 * @return The camera's x position
	 */
	public int getX() {
		return ctx.getClient().getCameraX();
	}
	
	/**
	 * @return The camera's y position
	 */
	public int getY() {
		return ctx.getClient().getCameraY();
	}
	
	/**
	 * @return The camera's z position
	 */
	public int getZ() {
		return ctx.getClient().getCameraZ();
	}
	
	/**
	 * @return The compass angle
	 */
	public int getCompassAngle() {
		return ctx.getClient().getCompassAngle();
	}

}
