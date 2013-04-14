package org.vinsert.bot.script.randevent.impl;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.randevent.RandomEvent;

@ScriptManifest(name = "TalkingRandom", authors = {"tholomew"}, description = "Talks to any random that will try to kill you or tele you if you ignore them.", version = 1.0)
public class TalkingRandom extends RandomEvent {

    private static final int[] NPC_ID = {2539, 407, 409, 410, 408, 956, 2540, 2458, 1056,
            4375, 2476, 3912, 3913, 2478, 2469, 2470, 3118, 2790};
    Npc npc;

    @Override
    public boolean init() {
        for (Npc n : npcs.getNpcs(Filters.npcId(NPC_ID))) {
            if (n != null) {
                try {
                    if (n.getSpeech().contains(players.getLocalPlayer().getName())) {
                        sleep(3000, 5000);      //sleeps incase it's the old man teleporting you
                        if (npcs.getNearest(n.getId()) != null) {
                            npc = n;
                            npc.interact("Talk-to");
                            sleep(1500, 2222);
                            return true;
                        }
                    }
                } catch (NullPointerException npe) {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public int pulse() {
        if (npc != null) {
            if (npc.isInteracting() && players.getLocalPlayer().isInteracting()) {
                if (widgets.canContinue()) {
                    widgets.clickContinue();
                    sleep(1700, 2000);
                }
            } else {
                npc.interact("Talk-to");
                sleep(3000, 5000);
            }
        } else if (npcs.getNearest(npc.getId()) == null) {
            requestExit();
            sleep(1000, 1500);
        }
        return random(50, 75);
    }

    @Override
    public void close() {
        if (npc != null) {
            log("Done talking with " + npc.getName() + ".");
        }
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.MEDIUM;
    }
}
