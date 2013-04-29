package org.vinsert.bot.script.randevent.impl;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Item;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.randevent.RandomEvent;
import org.vinsert.bot.util.Timer;

/**
 * @author iJava
 */
@ScriptManifest(name = "StrangeBox Solver", description = "Solves strange boxes", authors = {"iJava"})
public class StrangeBox extends RandomEvent {

    private static final int STRANGE_BOX_ID = 3063;
    private String lastChoice = "none";
    private static final int SETTINGS_ID = 312;
    private static final int WIDGET_PARENT_ID = 190;

    @Override
    public boolean init() {
        return game.isLoggedIn() && inventory.getCount(true, STRANGE_BOX_ID) > 0;
    }

    @Override
    public int pulse() {
        Widget boxWidget = widgets.get(WIDGET_PARENT_ID, 10);
        if (boxWidget == null) {
            Item box = inventory.getItem(STRANGE_BOX_ID);
            if (box != null) {
                if (inventory.interact(inventory.indexOf(box), "Open")) {
                    sleep(700);
                    Timer t = new Timer(3000);
                    while (t.isRunning()) {
                        if (widgets.get(192, 10) != null) {
                            break;
                        }
                        sleep(200);
                    }
                }
            }
            return 100;
        }
        int answer = getWidgetIndex();
        String choice = widgets.get(WIDGET_PARENT_ID, 10).getText();
        if (choice.equals(lastChoice)) {
            Timer t = new Timer(3000);
            while (t.isRunning()) {
                if (!widgets.get(WIDGET_PARENT_ID, 10).getText().equals(lastChoice)) {
                    break;
                }
                sleep(200);
            }
            return 200;
        }
        Widget option = widgets.get(WIDGET_PARENT_ID, answer);
        if (option != null) {
            option.click();
            sleep(700);
            Timer t = new Timer(3000);
            while (t.isRunning()) {
                if (getWidgetIndex() != answer) {
                    lastChoice = choice;
                    break;
                }
                sleep(100);
            }
        }
        return 200;
    }

    @Override
    public void close() {
        log("Strange Box Solver by iJava Finished");
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.LOW;
    }

    public int getWidgetIndex() {
        switch (settings.get(SETTINGS_ID) >> 24) {
            case 0:
                return 10;
            case 1:
                return 11;
            case 2:
                return 12;
        }
        return 10;
    }
}
