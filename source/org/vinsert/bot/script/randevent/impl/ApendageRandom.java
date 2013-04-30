package org.vinsert.bot.script.randevent.impl;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.script.randevent.RandomEvent;
import org.vinsert.bot.util.Filter;
import org.vinsert.bot.util.Timer;

/**
 * @author iJava
 */
@ScriptManifest(name = "Appendage Random Solver", authors = {"iJava"}, description = "Solves the appendage random")
public class ApendageRandom extends RandomEvent {

    private int[] levers = {12724, 12723, 12722, 12725};

    private boolean inArray(int[] array, int id) {
        for (int i : array) {
            if (i == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean init() {
        if (game.isLoggedIn()) {
            GameObject lever = objects.getNearest(new Filter<GameObject>() {
                @Override
                public boolean accept(GameObject element) {
                    return inArray(levers, element.getId());
                }
            });
            if (lever != null && lever.isOnScreen()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int pulse() {
        GameObject lever = objects.getNearest(new Filter<GameObject>() {
            @Override
            public boolean accept(GameObject element) {
                return element.getId() == levers[0];
            }
        });
            if (lever != null) {
                Tile t = localPlayer.getLocation();
                lever.click();
                Timer timer = new Timer(5000);
                while (timer.isRunning()) {
                    if (t.distanceTo(localPlayer.getLocation()) > 2) {
                        return 200;
                    }
                    if(widgets.canContinue()) {
                        widgets.clickContinue();
                    }
                    sleep(50);
                }
            }
        return 300;
    }

    @Override
    public void close() {
        log("AppendageRandom Finished By iJava");
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.HIGH;
    }
}
