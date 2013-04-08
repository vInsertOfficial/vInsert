package org.vinsert.bot.script.randevent.impl;

import java.util.Random;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.tools.Navigation.NavigationPolicy;
import org.vinsert.bot.script.randevent.RandomEvent;
import org.vinsert.bot.script.randevent.RandomEvent.RandomEventPriority;
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
        return RandomEventPriority.MEDIUM;
    }

    private boolean isEntThere() {
        final Npc ent = (Npc) players.getLocalPlayer().getInteracting();
        return ent != null && ent instanceof Npc && contains(ENT_IDS, ent.getId());
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
