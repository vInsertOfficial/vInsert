package org.vinsert.bot.script.api.generic;

/**
 * Represents an interactable game entity
 * @author tommo
 *
 */
public interface Interactable {
	
	/**
	 * Interacts with the implementing entity
	 * <p>
	 * The implementation should automatically left click an interactable if the
	 * first menu option meets the demands
	 * @param action The action to interact with
	 * @return <i>true</i> if the we interacted, <i>false</i> if not
	 */
	public boolean interact(String action);

}
