package org.vinsert.bot.script.randevent.impl;

import java.awt.Rectangle;

import org.vinsert.bot.accounts.Account;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.api.tools.Game.GameState;
import org.vinsert.bot.script.randevent.RandomEvent;

/**
 * Auto login, written very fast, doesn't use widgets
 *
 * @author tommo
 *
 */
@ScriptManifest(name = "Auto Login", authors = {"tommo", "tholomew"}, description = "Automatic login", version = 1.0)
public class AutoLogin extends RandomEvent {

    private Rectangle userRect = new Rectangle(398, 278, 129, 12);
    private Rectangle playRect = new Rectangle(280, 300, 212, 75);
    private boolean clicked = false;

    @Override
    public boolean init() {
        if (getContext().getAccount() == null || getContext().getAccount().getUsername().equals("null")
                || getContext().getAccount().getPassword().equals("null")) {
            log("You must have an account saved to use Auto Login. Make sure you press the save button. You can also try editing the Accounts.txt in your vInsert folder.");
            return false;
        } else if (game.getGameState() == GameState.LOGIN) {
            return true;
        }
        return false;
    }

    @Override
    public int pulse() {
        if (game.getGameState() == GameState.LOGIN) {
            if (!clicked) {
                mouse.click(userRect.x, userRect.y, userRect.x + userRect.width, userRect.y + userRect.height);
                clicked = true;
                sleep(1000, 1500);
            } else {
                log("Logging in...");
                Account account = getContext().getAccount();
                keyboard.type(account.getUsername(), true);
                sleep(500, 1200);
                keyboard.type(account.getPassword(), true);
                sleep(4000, 8000);
                Widget w = getClickToLogin();
                if (w != null) {
                    w.click();
                }
            }
        } else if (game.getGameState() == GameState.INGAME && widgets.get(328, 12) != null && widgets.get(328, 12).isValid()) {
            mouse.click(playRect.x, playRect.y, playRect.x + playRect.width, playRect.y + playRect.height);
            requestExit();
        }
        return 500;
    }

    @Override
    public void close() {
    }

    @Override
    public RandomEventPriority priority() {
        return RandomEventPriority.HIGH;
    }

    public Widget getClickToLogin() {
        for (Widget w : widgets.getValidated()) {
            if (w.getText().equalsIgnoreCase("click here to play")) {
                System.out.println(w.getParentId() + " , " + w.getId());
                return w;
            }
        }
        return null;
    }
}
