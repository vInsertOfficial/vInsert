package org.vinsert.bot.script;

import java.awt.Graphics2D;

/**
 * Represents an extension of the generic script class to incorporate state based mechanics
 * @author tommo
 *
 */
@SuppressWarnings("rawtypes")
public abstract class StatefulScript<T extends Enum> extends Script {
	
	/**
	 * Used to determine the current state of this script
	 * @return The enumeration representing the script state
	 */
	public abstract T determine();
	
	/**
	 * Used to handle the current state of this script
	 * @param state The enumeration representing the script state
	 */
	public abstract int handle(T state);

	@Override
	public abstract void render(Graphics2D graphics);

	@Override
	public abstract boolean init();

	@Override
	public int pulse() {
		T state = determine();
		return handle(state);
	}

	@Override
	public abstract void close();

}
