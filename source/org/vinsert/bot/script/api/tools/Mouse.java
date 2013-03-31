package org.vinsert.bot.script.api.tools;

import java.awt.Point;
import java.awt.Polygon;

import org.vinsert.bot.InputHandler;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Actor;
import org.vinsert.bot.util.MouseUtils;
import org.vinsert.bot.util.Utils;


/**
 * Represents a virtual bot mouse
 * @author tommo
 *
 */
public class Mouse {
	
	@SuppressWarnings("unused")
	private ScriptContext ctx;
	private InputHandler handler;
	
	/**
	 * The mouse speed
	 */
	private int speed = MouseUtils.DEFAULT_MOUSE_SPEED;
	
	public Mouse(ScriptContext ctx) {
		this.ctx = ctx;
		this.handler = ctx.getBot().getInputHandler();
	}
	
	/**
	 * Clicks the mouse at the current location
	 */
	public void click() {
		click(false);
	}
	
	public void click(boolean right) {
		handler.pressMouse(right);
		Utils.sleep(Utils.random(50, 100));
		handler.releaseMouse();
	}

	/**
	 * Moves the mouse to the given coordinates
	 * @param x The x coordinate
	 * @param y The y coordinate
	 */
	public void move(int x, int y) {
		handler.moveMouse(speed, handler.getPosition().x, handler.getPosition().y, x, y, 0, 0);
	}
	
	/**
	 * Moves the mouse to a position within the given bounds
	 * @param minX The minimum possible X
	 * @param minY The minimum possible Y
	 * @param maxX The maximum possible X
	 * @param maxY The maximum possible Y
	 */
	public void move(int minX, int minY, int maxX, int maxY) {
		move(Utils.random(minX, maxX), Utils.random(minY, maxY));
	}
	
	/**
	 * Moves the mouse and clicks at the specified position
	 * @param x The x coordinate
	 * @param y The y coordinate
	 */
	public void click(int x, int y) {
		move(x, y);
		Utils.sleep(Utils.random(50, 150));
		click();
	}
	
	/**
	 * Moves the mouse and clicks at the specified position
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @param rightClick should the mouse right click
	 */
	public void click(int x, int y, boolean rightClick) {
		move(x, y);
		Utils.sleep(Utils.random(50, 150));
		click(true);
	}
	
	/**
	 * Moves and clicks the mouse at a position within the given bounds
	 * @param minX The minimum possible X
	 * @param minY The minimum possible Y
	 * @param maxX The maximum possible X
	 * @param maxY The maximum possible Y
	 */
	public void click(int minX, int minY, int maxX, int maxY) {
		move(Utils.random(minX, maxX), Utils.random(minY, maxY));
		click();
	}
	
	/**
	 * Moves and clicks the mouse on an actor
	 * @param actor The actor to click on
	 */
	public void click(Actor actor, boolean right) {
		if (hover(actor)) {
			click(right);
		}
	}
	
	/**
	 * Moves and clicks the mouse on an actor
	 * @param actor The actor to click on
	 */
	public void click(Actor actor) {
		if (hover(actor)) {
			click(false);
		}
	}
	
	/**
	 * Moves the mouse and hovers over an actor
	 * @param actor The actor to hover over
	 * @return true if the mouse hovered over the actor, false if something went wrong
	 */
	public boolean hover(Actor actor) {
		Polygon poly = actor.hull();
		if (poly != null && !poly.contains(handler.getPosition())) {
			Point p = actor.hullPoint(poly);
			move(p.x, p.y);
			return true;
		}
		return false;
	}
	
	/**
	 * Moves the mouse randomly between a minimum and maximum deviation from origin
	 * @param minDeviation The minimum deviation
	 * @param maxDeviation The maximum deviation
	 */
	public void moveRandomly(int minDeviation, int maxDeviation) {
		if (minDeviation == 0) minDeviation = 2;
		if (minDeviation > maxDeviation) {
			int temp = maxDeviation;
			maxDeviation = minDeviation;
			minDeviation = temp;
		}
		int randX = handler.getPosition().x + Utils.random(-minDeviation, maxDeviation);
		int randY = handler.getPosition().y + Utils.random(-minDeviation, maxDeviation);
		move(randX, randY);
	}
	
	/**
	 * Sets the mouse speed
	 * @param speed Normally a value between 1-10, lower is faster
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	/**
	 * @return The mouse speed, normally in range 1-10, lower is faster
	 */
	public int getSpeed() {
		return speed;
	}
	
	/**
	 * @return The position of the mouse
	 */
	public Point getPosition() {
		return handler.getPosition();
	}
	
}
