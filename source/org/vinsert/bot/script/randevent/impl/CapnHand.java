package org.vinsert.bot.script.randevent.impl;

import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.randevent.RandomEvent;

public class CapnHand extends RandomEvent {
	
	private static final int NPC_ID = 2539;

	@Override
	public boolean init() {
		for (Npc n : npcs.getNpcs(Filters.npcId(NPC_ID))) {
			if (n.isInteracting() && n.getInteracting() == localPlayer) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int pulse() {
		Npc npc = npcs.getNearest(NPC_ID);
		if (!localPlayer.isInteracting() || !(localPlayer.getInteracting() instanceof Npc)) {
			if (npc != null && npc.isInteracting() && npc.getInteracting() == localPlayer) {
				npc.interact("Talk-to");
			}
		} else if (localPlayer.isInteracting() && localPlayer.getInteracting() instanceof Npc) {
			Npc interacting = (Npc) localPlayer.getInteracting();
			if (interacting.getId() == NPC_ID) {
				//alot of safety checks
				//game.clickToContinue();
				requestExit();
			}
		}
		return 500;
	}

	@Override
	public void close() {
		log("Solved CapnHand successfully.");
	}

	@Override
	public RandomEventPriority priority() {
		return RandomEventPriority.MEDIUM;
	}

}
