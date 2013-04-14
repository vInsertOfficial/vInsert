package org.vinsert.bot.util;

/**
 * Generic filter interface
 *
 * @param <T> The element filtration type
 * @author tommo
 */
public interface Filter<T> {

    public boolean accept(T element);

}
