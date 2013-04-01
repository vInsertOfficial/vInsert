package org.vinsert.bot.util;

/**
 * Generic filter interface
 * @author tommo
 *
 * @param <T> The element filtration type
 */
public interface Filter<T> {
	
	public boolean accept(T element);

}
