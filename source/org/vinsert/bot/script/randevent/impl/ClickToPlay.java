package org.vinsert.bot.script.randevent.impl;

import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.randevent.RandomEvent;

/**
 *
 * @author tholomew
 */
@ScriptManifest(name = "Click To Play", authors = {"tholomew"}, description = "Click to play, made for the sole purpose of disconnects", version = 1.0)
public class ClickToPlay extends RandomEvent {


    @Override
    public boolean init() {
        for (Widget w : widgets.getValidated()) {
            if (w.getText().equalsIgnoreCase("click here to play")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int pulse() {
        if (widgets.get(378, 17) != null) {         //super safe
            if (widgets.get(378, 17).isValid()) {
                widgets.get(378, 17).click();
                sleep(300, 500);
                requestExit();
            }
        }
        return 500;
    }

    @Override
    public void close() {
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.HIGH;        //can't do anything if you're not logged in
    }
}
