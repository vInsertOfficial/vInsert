package org.vinsert.bot.loader.arch;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Represents an Archive.
 * @author `Discardedx2 (Discardedx2)
 */
public interface Archive<T> extends Iterable<T> {

	int TMP_LEN = 1024;
	/**
	 * Gets the bytes of an entry.
	 * @param name The name of the entry.
	 * @return The entry file bytes.
	 * @throws ClassNotFoundException 
	 */
	byte[] getEntry(String name) throws ClassNotFoundException;
	
	/**
	 * Loads all files into a map.
	 * @return A loaded map.
	 * @throws IOException
	 */
	Map<String, T> load() throws IOException;
	
	/**
	 * Writes all loaded files to a specified file.
	 * @param file The specified file.
	 * @throws IOException
	 */
	void write(File file) throws IOException;

	T get(String string);
	
}
