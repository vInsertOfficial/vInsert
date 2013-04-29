package org.vinsert.bot.script.api.tools;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.util.Utils;
import org.vinsert.insertion.IClient;

/**
 * Standard game utilities
 *
 * @author tommo
 * @author Discardedx2
 */
public class Game {

    private ScriptContext ctx;
    private IClient client;

    public Game(ScriptContext ctx) {
        this.ctx = ctx;
        this.client = ctx.getClient();
    }

    public Tabs getCurrentTab() {
        for (Tabs tab : Tabs.values()) {
            if(ctx.widgets.get(tab.getParent(), tab.getChild()).getTextureId() != -1) {
                return tab;
            }
        }
        return null;
    }

    /**
     * Opens a specified tab.
     *
     * @param tab the tab to open.
     * @return {@code true} if the tab was successfully opened.
     */
    public boolean openTab(Tabs tab) {
        tab.clickTab(ctx.widgets);
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
        COMBAT("Combat Options", 47),
        STATS("Stats", 48),
        QUESTS("Quest List", 49),
        INVENTORY("Inventory", 50),
        EQUIPMENT("Worn Equipment", 51),
        PRAYER("Prayer", 52),
        MAGIC("Magic", 53),
        CLAN_CHAT("Clan Chat", 30),
        FRIENDS_LIST("Friends List", 31),
        IGNORE_LIST("Ignore List", 32),
        LOGOUT("Logout", 33),
        OPTIONS("Options", 34),
        EMOTES("Emotes", 35),
        MUSIC("Music Player", 36);

        final int PARENT = 548;
        String name;
        int child;

        Tabs(String name, int child) {
            this.name = name;
            this.child = child;
        }

        public int getParent() {
            return PARENT;
        }

        public int getChild() {
            return child;
        }

        public String getName() {
            return name;
        }

        public void clickTab(Widgets widgets) {
            Widget w = widgets.get(PARENT, child);
            w.click();
        }
    }

}