package org.vinsert.bot.script.api.tools;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.util.Utils;
import org.vinsert.insertion.IClient;

import java.awt.*;

/**
 * Standard game utilities
 *
 * @author tommo
 * @author Discardedx2
 */
public class Game {

    private ScriptContext ctx;
    private IClient client;
    private Tabs currentTab = Tabs.INVENTORY;

    public Game(ScriptContext ctx) {
        this.ctx = ctx;
        this.client = ctx.getClient();
    }

    /**
     * Opens a specified tab.
     *
     * @param tab the tab to open.
     * @return {@code true} if the tab was successfully opened.
     */
    public boolean openTab(Tabs tab) {
        Point p = tab.getPoint(ctx.widgets);
        ctx.mouse.click(p.x, p.y);
        return true;
    }

    /**
     * Logs the player out
     *
     * @credits Kenneh
     */
    public void logout() {
        if (getGameState() != GameState.LOGIN) {
            if (ctx.players.getLocalPlayer().isInCombat()) {
                long curr = System.currentTimeMillis();
                while (System.currentTimeMillis() - curr < 5000 && ctx.players.getLocalPlayer().isInCombat()) {
                    Utils.sleep(20); // wait up to 5 seconds for combat to end
                }
            } else {
                if (getCurrentTab() != Tabs.LOGOUT) {
                    openTab(Tabs.LOGOUT);
                } else {
                    ctx.mouse.click(644, 379);
                }
            }
        }
    }

    /**
     * Checks to see if this client is logged into the game.
     *
     * @return {@code true} if the client is in a logged in state.
     */
    public boolean isLoggedIn() {
        return client.getGameState() == GameState.INGAME.id;
    }

    /**
     * Gets the client's current plane.
     *
     * @return The plane.
     */
    public int getPlane() {
        return client.getPlane();
    }

    /**
     * @return The client's {@link GameState}
     */
    public GameState getGameState() {
        for (GameState state : GameState.values()) {
            if (state.id() == client.getGameState()) {
                return state;
            }
        }
        return GameState.UNKNOWN;
    }

    /**
     * Gets the current tab.
     *
     * @return the tab we are currently on.
     */
    public Tabs getCurrentTab() {
        return currentTab;
    }

    /**
     * @return The bot index in the tab list
     */
    public int getBotIndex() {
        return ctx.getBot().getBotIndex();
    }

    /**
     * Represents the client's active game state
     *
     * @author tommo
     */
    public static enum GameState {
        LOGIN(10), CONNECTING(20), INITIATION(25), INGAME(30), UNKNOWN(-1);

        private int id;

        GameState(int id) {
            this.id = id;
        }

        public int id() {
            return id;
        }
    }

    /**
     * Represents a tab on the game screen
     *
     * @author Discardedx2
     */
    public static enum Tabs {
        COMBAT("Combat Options"),
        STATS("Stats"),
        QUESTS("Quest List"),
        INVENTORY("Inventory"),
        EQUIPMENT("Worn Equipment"),
        PRAYER("Prayer"),
        MAGIC("Magic"),
        CLAN_CHAT("Clan Chat"),
        FRIENDS_LIST("Friends List"),
        IGNORE_LIST("Ignore List"),
        LOGOUT("Logout"),
        OPTIONS("Options"),
        EMOTES("Emotes"),
        MUSIC("Music Player");

        String name;

        Tabs(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Point getPoint(Widgets widgets) {
            for (Widget w : widgets.get(548)) {
                if (w.getActions() != null) {
                    for (String s : w.getActions()) {
                        if (getName().equals(s)) {
                            Widget par = w.getRoot();
                            return new Point(par.getRelativeX() + w.getRelativeX() + (w.getWidth() / 2), par.getRelativeY() + w.getRelativeY() + (w.getHeight() / 2));
                        }
                    }
                }
            }
            return new Point(-1, -1);
        }
    }

}