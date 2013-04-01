package org.vinsert.bot;

import java.awt.Canvas;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;

import org.vinsert.bot.util.MouseUtils;


/**
 * Controls a bot's mouse
 * 
 * @author tommo
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
	 * The random instance
	 */
	private Random random = new Random();

	/**
	 * The current drag length
	 */
	@SuppressWarnings("unused")
	private byte dragLength = 0;

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
	 * Moves the mouse from a position to another position with randomness
	 * applied.
	 * 
	 * @param speed
	 *            the speed to move the mouse. Anything under
	 *            {@link #DEFAULT_MOUSE_SPEED} is faster than normal.
	 * @param x1
	 *            from x
	 * @param y1
	 *            from y
	 * @param x2
	 *            to x
	 * @param y2
	 *            to y
	 * @param randX
	 *            randomness in the x direction
	 * @param randY
	 *            randomness in the y direction
	 */
	public void moveMouse(final int speed, final int x1, final int y1,
			final int x2, final int y2, int randX, int randY) {
		if ((x2 == -1) && (y2 == -1))
			return;
		if (randX <= 0) {
			randX = 1;
		}
		if (randY <= 0) {
			randY = 1;
		}
		try {
			if ((x2 == x1) && (y2 == y1))
				return;
			final Point[] controls = MouseUtils.generateControls(x1, y1, x2
					+ random.nextInt(randX), y2 + random.nextInt(randY), 50,
					120);
			final Point[] spline = MouseUtils.generateSpline(controls);
			final long timeToMove = MouseUtils.fittsLaw(
					Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)), 10);
			final Point[] path = MouseUtils.applyDynamism(spline,
					(int) timeToMove, MouseUtils.DEFAULT_MOUSE_SPEED);
			for (final Point aPath : path) {
				moveMouse(aPath.x, aPath.y);
				try {
					Thread.sleep(Math.max(0, speed - 2 + random.nextInt(4)));
				} catch (final InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		} catch (final Exception e) {
			// MouseHandler.log.info("Error moving mouse: " + e);
			// MouseHandler.log.info("Source: " + x1 + "," + y1);
			// MouseHandler.log.info("Dest:   " + x2 + "," + y2);
			// MouseHandler.log.info("Randx/Randy: " + randX + "/" + randY);
			// e.printStackTrace();
		}
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

}
