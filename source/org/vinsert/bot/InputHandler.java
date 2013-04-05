package org.vinsert.bot;

import java.awt.Canvas;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


/**
 * Controls a bot's mouse
 * 
 * @author tommo
 * @author BenLand100
 * 
 */
public class InputHandler implements MouseListener, MouseMotionListener, KeyListener {

	/**
	 * The bot instance
	 */
	@SuppressWarnings("unused")
	private Bot bot;

	/**
	 * The canvas instance
	 */
	private Canvas canvas;

	/**
	 * The latest mouse position
	 */
	private Point position = new Point(-1, -1);

	/**
	 * Is human input currently active
	 */
	private boolean humanInput = true;

	/**
	 * Is the mouse being pressed
	 */
	@SuppressWarnings("unused")
	private boolean pressed = false;

	/**
	 * Is the mouse present in the window
	 */
	private boolean present = true;

	/**
	 * The client's mouse listener
	 */
	private MouseListener mouseListener;

	/**
	 * The client's key listenr
	 */
	private KeyListener keyListener;

	/**
	 * The client's mouse motion listener
	 */
	private MouseMotionListener motionListener;

	public InputHandler(Bot bot, Canvas canvas) {
		this.bot = bot;
		this.canvas = canvas;

		/*
		 * Store and remove the client's listeners and replace them with
		 * our own so we can reroute events
		 */
		keyListener = canvas.getKeyListeners()[0];
		mouseListener = canvas.getMouseListeners()[0];
		motionListener = canvas.getMouseMotionListeners()[0];
		canvas.removeKeyListener(keyListener);
		canvas.removeMouseListener(mouseListener);
		canvas.removeMouseMotionListener(motionListener);

		canvas.addMouseListener((MouseListener) this);
		canvas.addMouseMotionListener((MouseMotionListener) this);
		canvas.addKeyListener((KeyListener) this);
	}
	
	/**
	 * Dispatches a key typed event (a non-letter key)
	 * @param keycode
	 */
	public void typeNonKey(int keycode) {
		KeyEvent typed = new KeyEvent(canvas, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, (char) keycode, KeyEvent.CHAR_UNDEFINED);
		keyListener.keyPressed(typed);
	}

	/**
	 * Dispatches a key pressed event, must release manually
	 * @param keycode The keycode, such as KeyEvent.VK_X
	 */
	public void pressKey(int keycode) {
		KeyEvent pressed = new KeyEvent(canvas, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0,
				keycode, (char)keycode, KeyEvent.KEY_LOCATION_STANDARD);
		keyListener.keyPressed(pressed);
	}

	/**
	 * Dispatches a key typed event
	 * @param keycode
	 */
	public void typeKey(int keycode) {
		KeyEvent typed = new KeyEvent(canvas, KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0,
				KeyEvent.VK_UNDEFINED, (char)keycode, KeyEvent.KEY_LOCATION_UNKNOWN);
		keyListener.keyTyped(typed);
	}

	/**
	 * Dispatches a key released event
	 * @param keycode The keycode, such as KeyEvent.VK_X
	 */
	public void releaseKey(int keycode) {
		KeyEvent released = new KeyEvent(canvas, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0,
				keycode, (char)keycode, KeyEvent.KEY_LOCATION_STANDARD);
		keyListener.keyReleased(released);
	}

	/**
	 * Dispatches a mouse pressed event, must release manually
	 */
	public void pressMouse(boolean right) {
		MouseEvent pressed = new MouseEvent(canvas, MouseEvent.MOUSE_PRESSED,
				System.currentTimeMillis(), right ? InputEvent.BUTTON3_MASK : 0, (int) position.getX(),
				(int) position.getY(), 1, false);
		
		mouseListener.mousePressed(pressed);
	}
	
	public void pressMouse() {
		pressMouse(false);
	}

	/**
	 * Dispatches a mouse released event
	 */
	public void releaseMouse() {
		MouseEvent released = new MouseEvent(canvas, MouseEvent.MOUSE_RELEASED,
				System.currentTimeMillis(), 0, (int) position.getX(),
				(int) position.getY(), 1, false);
		mouseListener.mouseReleased(released);
	}

	/**
	 * Moves the mouse to the given position and dispatches a mouse moved event
	 * @param x The x coordinate
	 * @param y The y coordinate
	 */
	public void moveMouse(final int x, final int y) {
		final MouseEvent me = new MouseEvent(canvas,
				MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, x, y, 0,
				false);
		motionListener.mouseMoved(me);
		position.setLocation(x, y);
	}
	
	/**
	 * Moves the mouse to the given position and dispatches a mouse moved event
	 * @param x The x coordinate
	 * @param y The y coordinate
	 */
	public void mouseEnter(final int x, final int y) {
		final MouseEvent me = new MouseEvent(canvas,
				MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, x, y, 0,
				false);
		motionListener.mouseMoved(me);
		position.setLocation(x, y);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (humanInput) keyListener.keyTyped(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (humanInput) keyListener.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (humanInput) keyListener.keyReleased(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (humanInput) mouseListener.mouseClicked(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (humanInput) mouseListener.mousePressed(e);
		pressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (humanInput) mouseListener.mouseReleased(e);
		pressed = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (humanInput) mouseListener.mouseEntered(e);
		present = true;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (humanInput) mouseListener.mouseExited(e);
		present = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (humanInput) motionListener.mouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (humanInput) {
			position.setLocation(e.getX(), e.getY());
			motionListener.mouseMoved(e);
		}
	}
	
	public Point getPosition() {
		return position;
	}

	public boolean isPresent() {
		return present;
	}

	public boolean isHumanInput() {
		return humanInput;
	}

	public void setHumanInput(boolean humanInput) {
		this.humanInput = humanInput;
	}

	/**
	 * Moves the mouse from the current position to the specified position.
	 * Approximates human movement in a way where smoothness and accuracy are
	 * relative to speed, as it should be.
	 * 
	 * @param x
	 *            The x destination
	 * @param y
	 *            The y destination
	 * @param speedFactor 
	 * 			   The speed factor
	 * @result The actual end point
	 */
	public synchronized Point windMouse(int x, int y, final double speedFactor) {
		double speed = (org.vinsert.bot.util.Utils.randomD() * 15D + 15D) / 10D;
		speed *= speedFactor;
		return windMouseImpl(position.x, position.y, x, y, 9D, 3D, 5D / speed, 10D / speed,
					10D * speed, 8D * speed);
	}

	/**
	 * Internal mouse movement algorithm. Do not use this without credit to
	 * either Benjamin J. Land or BenLand100. This is synchronized to prevent
	 * multiple motions and bannage.
	 * 
	 * @param xs
	 *            The x start
	 * @param ys
	 *            The y start
	 * @param xe
	 *            The x destination
	 * @param ye
	 *            The y destination
	 * @param gravity
	 *            Strength pulling the position towards the destination
	 * @param wind
	 *            Strength pulling the position in random directions
	 * @param minWait
	 *            Minimum relative time per step
	 * @param maxWait
	 *            Maximum relative time per step
	 * @param maxStep
	 *            Maximum size of a step, prevents out of control motion
	 * @param targetArea
	 *            Radius of area around the destination that should trigger
	 *            slowing, prevents spiraling
	 * @result The actual end point
	 */
	private synchronized Point windMouseImpl(double xs, double ys, double xe,
			double ye, double gravity, double wind, double minWait,
			double maxWait, double maxStep, double targetArea) {
		// System.out.println(targetArea);
		final double sqrta = Math.sqrt(5);
		final double sqrtb = Math.sqrt(7);
		double dist, veloX = 0, veloY = 0, windX = 0, windY = 0;
		while ((dist = Math.hypot(xe - xs, ys - ye)) >= 0) {
			wind = Math.min(wind, dist);
			if (dist >= targetArea) {
				windX = windX / sqrta
						+ (Math.random() * (wind * 2D + 1D) - wind) / sqrta;
				windY = windY / sqrtb
						+ (Math.random() * (wind * 2D + 1D) - wind) / sqrtb;
			} else {
				windX /= sqrtb;
				windY /= sqrta;
				if (maxStep < 4) {
					maxStep = Math.random() * 3 + 3D;
				} else {
					maxStep /= sqrta;
				}
				// System.out.println(maxStep + ":" + windX + ";" + windY);
			}
			veloX += windX + gravity * (xe - xs) / dist;
			veloY += windY + gravity * (ye - ys) / dist;
			double veloMag = Math.hypot(veloX, veloY);
			if (veloMag > maxStep) {
				double randomDist = maxStep / 2D + Math.random() * maxStep / 2D;
				veloX = (veloX / veloMag) * randomDist;
				veloY = (veloY / veloMag) * randomDist;
			}
			xs += veloX;
			ys += veloY;
			int mx = (int) Math.round(xs);
			int my = (int) Math.round(ys);
			if (position.x != mx || position.y != my) {
				// Scratch
				/*
				 * g.drawLine(cx,cy,mx,my); frame.repaint();
				 */
				// MouseJacking
				/*
				 * try { Robot r = new Robot(); r.mouseMove(mx,my); } catch
				 * (Exception e) { }
				 */
				moveMouse(mx, my);
			}
			double step = Math.hypot(xs - position.x, ys - position.y);
			try {
				Thread.sleep(Math.round((maxWait - minWait) * (step / maxStep)
						+ minWait));
			} catch (InterruptedException ex) {
			}
		}
		// System.out.println(Math.abs(xe - cx) + ", " + Math.abs(ye - cy));
		return new Point(position.x, position.y);
	}

}
