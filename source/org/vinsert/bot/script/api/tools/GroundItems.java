package org.vinsert.bot.script.api.tools;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Area;
import org.vinsert.bot.script.api.GroundItem;
import org.vinsert.bot.script.api.NodeDeque;
import org.vinsert.bot.script.api.Tile;
import org.vinsert.bot.util.Filter;
import org.vinsert.bot.util.Perspective;
import org.vinsert.insertion.IClient;
import org.vinsert.insertion.IItem;
import org.vinsert.insertion.INode;
import org.vinsert.insertion.INodeDeque;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for all ground items.
 *
 * @author `Discardedx2
 */
public class GroundItems {

    /**
     * The script context.
     */
    private ScriptContext ctx;

    public GroundItems(ScriptContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Gets all ground items in an {@link Area}, by applying a filter first.
     *
     * @param filter The filter to apply.
     * @return The ground items.
     */
    public List<GroundItem> getAll(Filter<GroundItem> filter) {
        List<GroundItem> items = new ArrayList();

        IClient client = ctx.getClient();
        INodeDeque[][] deques = client.getGroundItems()[client.getPlane()];
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                INodeDeque deq = deques[x][y];

                if (deq == null) {
                    continue;
                }

                NodeDeque<INode> deque = new NodeDeque<INode>(deq);

                for (INode node = deque.front(); node != null; node = deque.next()) {
                    if(!(node.prev() instanceof IItem)) {
                        continue;
                    }
                    IItem i = (IItem) node.prev();

                    GroundItem item = new GroundItem(ctx, i.getId(), i.getAmount(), new Tile(client.getOriginX() + x, client.getOriginY() + y, x, y));

                    if (filter == null || filter.accept(item)) {
                        items.add(item);
                    }
                }
            }
        }
        return items;
    }

    /**
     * Gets the nearest ground item to a tile.
     *
     * @param filter The filter to apply.
     * @return The nearest ground item.
     */
    public GroundItem getNearest(Tile tile, Filter<GroundItem> filter) {
        List<GroundItem> items = new ArrayList<GroundItem>();
        IClient client = ctx.getClient();

        INodeDeque[][] deques = client.getGroundItems()[client.getPlane()];

        GroundItem item = null;
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                INodeDeque deq = deques[x][y];

                if (deq == null) {
                    continue;
                }

                NodeDeque<INode> deque = new NodeDeque<INode>(deq);

                for (INode node = deque.front(); node != null; node = deque.next()) {
                    IItem i = (IItem) node.prev();

                    GroundItem ii = new GroundItem(ctx, i.getId(), i.getAmount(), new Tile(client.getOriginX() + x, client.getOriginY() + y, x, y));

                    if (item == null) {
                        item = ii;
                    } else if (Perspective.edist_tile(tile, ii.getLocation()) < Perspective.edist_tile(tile, ii.getLocation())) {
                        item = ii;
                    }

                    if (filter == null || filter.accept(item)) {
                        items.add(item);
                    }
                }
            }
        }
        return item;
    }

    /**
     * Gets all ground items in an {@link Area}, without applying a filter.
     *
     * @return The ground items.
     */
    public List<GroundItem> getAll() {
        return getAll(null);
    }


}
