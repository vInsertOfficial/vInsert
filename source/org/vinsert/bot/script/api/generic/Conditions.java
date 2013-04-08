package org.vinsert.bot.script.api.generic;

import org.vinsert.bot.util.Utils;

/**
 * Conditions for exact timing
 * No more hard coding sleeps
 * @author Blink
 */
public class Conditions {

	/**
	 * Condition interface
	 */
	public static interface Condition {
		
		/**
		 * Verifies if the condition has been met
		 * @return true if the condition was met, otherwise false
		 */
		public boolean isMet();
	}
	
	/**
	 * Awaits the condition until it is either met or timed out after the given time
	 * @param condition The condition to get verified
	 * @param timeout The time (in milliseconds) to timeout after
	 * @return true if the condition was met, otherwise false
	 */
	public static boolean waitFor(final Condition condition, final int timeout) {
		final long start = System.currentTimeMillis();
		while (System.currentTimeMillis() <= (start + timeout)) {
			if (condition.isMet()) {
				return true;
			}
			Utils.sleep(30, 40);
		}
		return false;
	}

	/**
	 * Awaits the condition until it is either met or timed out after the a generated time between the minimum and maximum
	 * @param condition The condition to get verified
	 * @param minimumTimeout The minimum time (in milliseconds) to timeout after
	 * @param maximumTimeout The maximum time (in milliseconds) to timeout after
	 * @return true if the condition was met, otherwise false
	 */
	public static boolean waitFor(final Condition condition, final int minimumTimeout, final int maximumTimeout) {
		return waitFor(condition, Utils.random(minimumTimeout, maximumTimeout));
	}
}