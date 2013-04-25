package org.vinsert.bot.script.randevent.impl;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Player;
import org.vinsert.bot.script.api.tools.Navigation.NavigationPolicy;
import org.vinsert.bot.script.randevent.RandomEvent;
import org.vinsert.bot.util.Utils;

@ScriptManifest(name = "Ent", description = "Stops chopping ents!", authors = {"tholomew"})
public class Ent extends RandomEvent {

    private static final int[] ENT_IDS = new int[]{1740, 1731, 1735, 1736, 1739, 1737, 1734}; //these are the NPC ids, not object

    @Override
    public boolean init() {
        return isEntThere();
    }

    @Override
    public int pulse() {
        if (isEntThere()) {
            navigation.navigate(localPlayer.getLocation().deviate(localPlayer.getLocation(), 1, 1), NavigationPolicy.MINIMAP);
            Utils.sleep(1000, 1500);
        } else {
            requestExit();
            Utils.sleep(1000, 1500);
        }
        return random(150, 250);
    }

    @Override
    public void close() {
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.HIGH;
    }

    private boolean isEntThere() {
        if (players.getLocalPlayer() == null || players.getLocalPlayer().getInteracting() == null) {
            return false;
        }
        final Actor actor = players.getLocalPlayer().getInteracting();
        if (actor instanceof Npc) {
            final Npc ent = (Npc) players.getLocalPlayer().getInteracting();
	        return ent != null && contains(ENT_IDS, ent.getId());
        } else
        	return false;
    }

    public boolean contains(final int[] y, final int i) {
        for (int x : y) {
            if (x == i) {
                return true;
            }
        }
        return false;
    }
}
