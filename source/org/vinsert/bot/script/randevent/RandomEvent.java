package org.vinsert.bot.script.randevent;

import java.awt.Graphics2D;

import org.vinsert.bot.script.Script;

/**
 * An abstract random event
 * @author tommo
 *
 */
public abstract class RandomEvent extends Script implements Comparable<RandomEvent> {

	@Override
	public abstract boolean init();

	@Override
	public abstract int pulse();

	@Override
	public abstract void close();
	
	/**
	 * Used to determine a random event's priority, useful
	 * when multiple events occur
	 * @return
	 */
	public abstract RandomEventPriority priority();
	
	@Override
	public int compareTo(RandomEvent other) {
		if (priority().priority() > other.priority().priority()) {
			return 1;
		} else if (priority().priority() < other.priority().priority()) {
			return -1;
		}
		
		return 0;
	}
	
	@Override
	public void render(Graphics2D graphics) {
		
	}
	
	public static enum RandomEventPriority {
		LOW(1), MEDIUM(2), HIGH(3);
		
		private int priority;
		
		RandomEventPriority(int priority) {
			this.priority = priority;
		}
		
		public int priority() {
			return priority;
		}
	}

}
