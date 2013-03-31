package org.vinsert.bot.util;

import java.util.Random;

/**
 * Generic utilities
 * @author tommo
 *
 */
public class Utils {
	
	private static Random random = new Random();

	/**
	 * Generates a random number between a min and max.
	 * 
	 * where 0 > max - min
	 * 
	 * @param min The minimum.
	 * @param max The maximum.
	 * @return The random value between the min and max values.
	 */
	public static int random(int min, int max) {
		return random.nextInt(max - min) + min;
	}
	
	/**
	 * Returns a random element from the given array
	 * @param array
	 * @return
	 */
	public <T> T random(T[] array) {
		return array[random(0, array.length)];
	}
	
	/**
	 * Simulates sleeping for a given time
	 * 
	 * @param time
	 *            The time to sleep for in milliseconds
	 */
	public static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}
	
	/**
	 * Simulates sleeping for a given time
	 * 
	 * @param time
	 *            The time to sleep for in milliseconds
	 */
	public static void sleep(int min, int max) {
		try {
			Thread.sleep(random(min, max));
		} catch (InterruptedException e) {
		}
	}

}
