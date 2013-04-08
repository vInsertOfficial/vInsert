package org.vinsert.bot.script.randevent.impl;

import java.util.Random;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Npc;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.api.generic.Filters;
import org.vinsert.bot.script.randevent.RandomEvent;
import org.vinsert.bot.script.randevent.RandomEvent.RandomEventPriority;
import org.vinsert.bot.util.Condition;
import org.vinsert.bot.util.Utils;


/**
 *
 * @author tholomew
 */
@ScriptManifest(name = "Sandwich Lady", authors = {"tholomew"}, description = "Talks to this obnoxious cunt.", version = 1.0)
public class SandwichLady extends RandomEvent {

    private final int NPC_ID = 3117;
    private Npc npc;

    private enum Sandwiches {

        SQUARE(10731, "square"),
        ROLL(10727, "roll"),
        CHOCOLATE(10728, "chocolate"),
        BAGUETTE(10726, "baguette"),
        TRIANGLE(10732, "triangle"),
        KEBAB(10729, "kebab"),
        PIE(10730, "pie");
        private final int modelId;
        private final String name;

        Sandwiches(int m, String n) {
            this.modelId = m;
            this.name = n;
        }

        public int getId() {
            return modelId;
        }

        public String getMessage() {
            return name;
        }

        public static Sandwiches getObject(String nm) {
            for (Sandwiches e : Sandwiches.values()) {
                if (nm.equals(e.getMessage())) {
                    return e;
                }
            }
            return null;
        }
    }

    @Override
    public boolean init() {
        for (Npc n : npcs.getNpcs(Filters.npcId(NPC_ID))) {
            if (n != null) {
                try {
                    if (n.getSpeech().contains(players.getLocalPlayer().getName())) {
                        if (npcs.getNearest(n.getId()) != null) {
                            npc = n;
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
        if (widgets.get(297) == null && !players.getLocalPlayer().isInteracting()) {
            npc.interact("Talk-to");
            Utils.waitFor(new Condition() {

                @Override
                public boolean validate() {
                    return players.getLocalPlayer().isInteracting();
                }
            }, 1500);
        } else if (widgets.get(297) == null && players.getLocalPlayer().isInteracting()) {
            widgets.clickContinue();
            Utils.sleep(1500, 2000);
        } else {
            String whatSandwich = widgets.get(297, 8).getText();
            Widget sandwich = getSandwichComponent(Sandwiches.getObject(whatSandwich).getId());
            if (sandwich != null) {
                sandwich.click();
                Utils.sleep(1500, 2000);
            }
        }
        if (npcs.getNearest(NPC_ID) == null) {
            requestExit();
            Utils.sleep(1500, 2000);
        }
        if (widgets.canContinue()) {
            widgets.clickContinue();
            Utils.sleep(1000, 1500);
        }
        return random(150, 200);
    }

    @Override
    public void close() {
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.MEDIUM;
    }

    public Widget getSandwichComponent(int mid) {
        Widget sandwich;
        for (int i = 1; i < 8; i++) {
            sandwich = widgets.get(297, i);
            if (sandwich != null) {
                if (sandwich.getModelId() == mid) {
                    return sandwich;
                }
            }
        }
        return null;
    }
}
