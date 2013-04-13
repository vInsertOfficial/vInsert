package org.vinsert.bot.script.randevent.impl;

import java.awt.Rectangle;

import org.vinsert.bot.accounts.Account;
import org.vinsert.bot.script.ScriptManifest;
import org.vinsert.bot.script.api.tools.Game.GameState;
import org.vinsert.bot.script.randevent.RandomEvent;

/**
 * Auto login, written very fast, doesn't use widgets
 * now it does, ;)
 *
 * @author tommo
 *
 */
@ScriptManifest(name = "Auto Login", authors = {"tommo", "tholomew"}, description = "Automatic login", version = 1.0)
public class AutoLogin extends RandomEvent {

    private Rectangle userRect = new Rectangle(398, 278, 129, 12);
    private boolean clicked = false;

    @Override
    public boolean init() {
        if (game.getGameState() == GameState.LOGIN) {
        	if (getContext().getAccount() == null || getContext().getAccount().getUsername().equals("null")
                    || getContext().getAccount().getPassword().equals("null")) {
                log("You must have an account saved to use Auto Login. Make sure you press the save button. You can also try editing the Accounts.txt in your vInsert folder.");
                return false;
        	} else {
        		return true;
        	}
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
                clicked = false;
            }
        } else if (game.getGameState() == GameState.INGAME) {
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
}
