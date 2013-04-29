package org.vinsert.bot.script.randevent.impl;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Item;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.randevent.RandomEvent;
import org.vinsert.bot.util.Utils;

/**
 * @author iJava
 */
@ScriptManifest(name="Lamp Solver", description = "Solves the lamp random", authors = {"iJava"})
public class Lamp extends RandomEvent {

    private static final int LAMP_ID = 2529;
    private static final int CONFIRM_ID = 26;
    private static final int PARENT_ID = 134;
    private static final int SETTING = 261;

    @Override
    public boolean init() {
        return game.isLoggedIn() && inventory.getCount(true, LAMP_ID) > 0;
    }

    @Override
    public int pulse() {
        if (widgets.get(PARENT_ID, CONFIRM_ID) == null) {
            Item lamp = inventory.getItem(LAMP_ID);
            if (lamp != null) {
                if (inventory.interact(inventory.indexOf(lamp), "Rub")) {
                    return 800;
                }
            }
            return 300;
        }
        int widgetIndex = Choice.getWidgetIndex(getContext().getAccount().getReward());
        Widget choice = widgets.get(PARENT_ID, widgetIndex);
        if (choice != null) {
            if (settings.get(SETTING) == (widgetIndex - 2)) {
                Widget confirm = widgets.get(PARENT_ID, CONFIRM_ID);
                if (confirm != null) {
                    confirm.click();
                    return 1000;
                }
                return 500;
            } else {
            choice.click();
            return 1000;
            }
        }
        return 500;
    }

    @Override
    public void close() {
        log("Lamp Solver Random by iJava Finished");
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.LOW;
    }

    private static enum Choice {
        ATTACK("Attack", 3), STRENGTH("Strength", 4), RANGED("Ranged", 5), MAGIC("Magic", 6),
        DEFENCE("Defence", 7), HITPOINTS("Constitution", 8), PRAYER("Prayer", 9), AGILITY("Agility", 10),
        HERBLORE("Herblore", 11), THIEVING("Thieving", 12), CRAFTING("Crafting", 13), RUNECRAFT("Runecrafting", 14),
        MINING("Mining", 15), SMITHING("Smithing", 16), FISHING("Fishing", 17), COOKING("Cooking", 18), FIREMAKING("Firemaking", 19),
        WOODCUTTING("Woodcutting", 20), FLETCHING("Fletching", 21), SLAYER("Slayer", 22), FARMING("Farming", 23),
        CONSTRUCTION("Construction", 24), HUNTER("Hunter", 25);
        private String name;
        private int widgetIndex;

        private Choice(String name, int widgetIndex) {
            this.name = name;
            this.widgetIndex = widgetIndex;
        }

        public static int getWidgetIndex(String skill) {
            for (Choice choice : Choice.values()) {
                if (choice.getName().equals(skill)) {
                    return choice.getWidgetIndex();
                }
            }
            return Utils.random(12, 23);
        }

        public String getName() {
            return name;
        }

        public int getWidgetIndex() {
            return widgetIndex;
        }
    }
}
