package org.vinsert.bot.script.api.tools;

import java.awt.event.KeyEvent;

import org.vinsert.bot.InputHandler;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.util.Utils;


/**
 * Keyboard utilities
 * 
 * @author tommo
 * 
 */
public class Keyboard {

	private ScriptContext ctx;
	private InputHandler handler;

	public Keyboard(ScriptContext ctx) {
		this.ctx = ctx;
		this.handler = ctx.getBot().getInputHandler();
	}
	
	/**
	 * Simulates a key press
	 * <p>
	 * Note: The key is not automatically released, so this must be matched with a
	 * <code>release(keycode)</code>
	 * @param keycode
	 */
	public void press(int keycode) {
		handler.pressKey(keycode);
	}
	
	/**
	 * Simulates a key release
	 * <p>
	 * This method is generally only used after calling <code>press(keycode)</code>
	 * to manually simulate a key press.
	 * @param keycode
	 */
	public void release(int keycode) {
		handler.releaseKey(keycode);
	}

	/**
	 * Holds a key down for the given time
	 * 
	 * @param keycode
	 *            The keycode, such as KeyEvent.VK_X
	 * @param millis
	 *            The time to hold the key down for in milliseconds
	 */
	public void hold(int keycode, int millis) {
		handler.pressKey(keycode);
		Utils.sleep(millis);
		handler.releaseKey(keycode);
	}

	/**
	 * Holds a key down for a random time between min and max millis
	 * 
	 * @param keycode
	 *            The keycode, such as KeyEvent.VK_X
	 * @param minMillis
	 *            The minimum time to hold the key down for in milliseconds
	 * @param maxMillis
	 *            The maximum time to hold the key down for in milliseconds
	 */
	public void hold(int keycode, int minMillis, int maxMillis) {
		hold(keycode, Utils.random(minMillis, maxMillis));
	}
	
	/**
	 * Types a character
	 * @param c The character to type
	 */
	public void type(char c) {
		handler.typeKey(c);
	}
	
	/**
	 * Types a sequence of text
	 * @param text The text to type
	 */
	public void type(String text) {
		final char[] chars = text.toCharArray();
		for (char c : chars) {
			type(c);
			Utils.sleep(Utils.random(60, 250));
		}
	}
	
	/**
	 * Types a sequence of text
	 * @param text The text to type
	 * @param minDelay The minimum delay between each key in milliseconds
	 * @param maxDelay The maximum delay between each key in milliseconds
	 */
	public void type(String text, int minDelay, int maxDelay) {
		final char[] chars = text.toCharArray();
		for (char c : chars) {
			type(c);
			Utils.sleep(Utils.random(minDelay, maxDelay));
		}
	}
	
	/**
	 * Simulates pressing the 'enter' or 'return' key on the keyboard
	 */
	public void enter() {
		ctx.keyboard.hold(KeyEvent.VK_ENTER, Utils.random(20, 50));
	}
	
	/**
	 * Types a sequence of text
	 * @param text The text to type
	 * @param enter Press enter at the end of the text
	 */
	public void type(String text, boolean enter) {
		type(text);
		enter();
	}
	
	/**
	 * NOT RECCOMENDED FOR NORMAL USE
	 * <p>
	 * Types a sequence of text instantly
	 * @param text The text to type
	 */
	public void typeInstantly(String text) {
		final char[] chars = text.toCharArray();
		for (char c : chars) {
			type(c);
		}
	}
	
}
