package org.vinsert.bot.loader;

/**
 * The possible languages to load.
 * @author `Discardedx2 (Discardedx2)
 */
public enum Language {
    ENGLISH(0),
    GERMAN(1),
    FRENCH(2),
    PORTUGUESE(3);
    
    /**
     * The language id.
     */
    final int id;
    
    Language(int id) {
    	this.id = id;
    }
    
    /**
     * Gets the language id.
     * @return the id.
     */
    public int getId() {
    	return id;
    }

    @Override
    public String toString() {
        return (id >= 1 ? "l=" + id + "/" : "");
    }
}
