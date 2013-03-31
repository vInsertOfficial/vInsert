package org.vinsert.bot.script.api;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.insertion.INpc;


/**
 * Wraps the {@link INpc} class in the client.
 * @author tommo
 *
 */
public class Npc extends Actor {
	
	private INpc npc;

	public Npc(ScriptContext ctx, INpc npc) {
		super(ctx, npc);
		this.npc = npc;
	}
	
	/**
	 * @return The npc's name, or null if not applicable
	 */
	public String getName() {
		if (npc == null || npc.getComposite() == null) return null;
		return npc.getComposite().getName();
	}
	
	/**
	 * @return The npc id, or -1 if not applicable
	 */
	public int getId() {
		if (npc == null || npc.getComposite() == null) return -1;
		return npc.getComposite().getId();
	}
	
	/**
	 * Searches through the npc's actions for the given phrase
	 * @param phrase The phrase to search for
	 * @return <b>true</b> if an action matches the phrase, <b>false</b> if not
	 */
	public boolean containsAction(String phrase) {
		if (npc.getComposite() == null || npc.getComposite().getActions() == null || npc.getComposite().getActions().length == 0) {
			return false;
		}
		
		for (int i = 0; i < npc.getComposite().getActions().length; i++) {
			if (npc.getComposite().getActions()[i] != null && npc.getComposite().getActions()[i].contains(phrase)) {
				return true;
			}
		}
		return false;
	}

}
