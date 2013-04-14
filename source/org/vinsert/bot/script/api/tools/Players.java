package org.vinsert.bot.script.api.tools;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Player;
import org.vinsert.bot.util.Filter;
import org.vinsert.insertion.IPlayer;

import java.util.ArrayList;
import java.util.List;


/**
 * Player methods
 *
 * @author tommo
 */
public class Players {

    private ScriptContext ctx;

    public Players(ScriptContext ctx) {
        this.ctx = ctx;
    }

    /**
     * @return The list of nearby local players
     */
    public List<Player> getAll() {
        List<Player> players = new ArrayList<Player>();
        for (int i = 0; i < ctx.getClient().getPlayers().length; i++) {
            if (ctx.getClient().getPlayers()[i] != null) {
                players.add(new Player(ctx, ctx.getClient().getPlayers()[i]));
            }
        }
        return players;
    }

    /**
     * Gets a filtered list of players.
     *
     * @param filter The filter.
     * @return The players.
     */
    public List<Player> getPlayers(Filter<Player> filter) {
        List<Player> players = new ArrayList<Player>();
        for (int i = 0; i < ctx.getClient().getPlayers().length; i++) {
            IPlayer player = ctx.getClient().getPlayers()[i];

            if (player != null) {
                Player rsplayer = new Player(ctx, ctx.getClient().getPlayers()[i]);

                if (filter.accept(rsplayer)) {
                    players.add(rsplayer);
                }
            }
        }
        return players;
    }

    /**
     * @return The local player instance
     */
    public Player getLocalPlayer() {
        final IPlayer localPlayer = ctx.getClient().getLocalPlayer();
        return localPlayer == null ? null : new Player(ctx, localPlayer);
    }

}
