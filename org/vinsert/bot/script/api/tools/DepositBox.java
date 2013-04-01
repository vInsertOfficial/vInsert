package org.vinsert.bot.script.api.tools;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.GameObject;
import org.vinsert.bot.script.api.Widget;
import org.vinsert.bot.script.api.enums.DepositBoxSlot;
import org.vinsert.bot.util.Filter;
import org.vinsert.bot.util.Timer;
import org.vinsert.bot.util.Utils;

import java.awt.*;

/**
 * @author iJava
 */
public class DepositBox {

    public static final int[] DEPOSIT_BOX_IDS = {9398};
    private static final int WIDGET_ID = 11;
    private static final int ITEM_COMP_ID = 61;
    private static final int CLOE_COMP_ID = 62;

    /**
     * The script context.
     */
    private ScriptContext ctx;

    public DepositBox(ScriptContext ctx) {
        this.ctx = ctx;
    }

    public Point getCenter(Rectangle bounds) {
        return new Point((int) bounds.getWidth() / 2, (int) bounds.getHeight() / 2);
    }

    public boolean close() {
        if (!isOpen()) {
            return true;
        }
        Widget close = ctx.widgets.getValidated(new Filter<Widget>() {
            @Override
            public boolean accept(Widget element) {
                return element.getParentId() == WIDGET_ID && element.getId() == CLOE_COMP_ID;
            }
        }).get(0);
        if (close != null) {
            Point p = getCenter(close.getBounds());
            ctx.mouse.click(p.x, p.y);
            Timer t = new Timer(3000);
            while (isOpen() && t.isRunning()) {
                Utils.sleep(Utils.random(100, 200));
            }
        }
        return !isOpen();
    }

    public boolean deposit(int itemId, int amt) {
        int[] ids = ctx.widgets.getValidated(new Filter<Widget>() {
            @Override
            public boolean accept(Widget element) {
                return element.getParentId() == WIDGET_ID && element.getId() == ITEM_COMP_ID;
            }
        }).get(0).getSlotContents();
        int count = 0;
        boolean stack = false;
        if (ctx.inventory.getItem(itemId).getAmount() > 1) {
            count = ctx.inventory.getCount(true, itemId);
            stack = true;
        } else {
            count = ctx.inventory.getCount(false, itemId);
        }
        int index = -1;
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] == itemId) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return false;
        }
        DepositBoxSlot[] slots = DepositBoxSlot.values();
        DepositBoxSlot slot = null;
        for (DepositBoxSlot slot1 : slots) {
            if (slot1.getInvIndex() == index) {
                slot = slot1;
                break;
            }
        }
        if (slot == null) {
            return false;
        }
        switch (amt) {
            case 1:
            case 5:
            case 10:
                if (ctx.menu.getBounds() == null) {
                    ctx.mouse.move(slot.getRandomPoint().x, slot.getRandomPoint().y);
                    Utils.sleep(Utils.random(100, 250));
                    Timer t = new Timer(3000);
                    while (ctx.menu.getBounds() == null && t.isRunning()) {
                        Utils.sleep(100);
                    }
                }
                if (ctx.menu.getBounds() != null) {
                    Point p = ctx.menu.getClickPoint(ctx.menu.getIndex("Deposit " + amt));
                    ctx.mouse.click(p.x, p.y);
                    Utils.sleep(Utils.random(100, 250));
                }
                break;
            default:
                if (ctx.menu.getBounds() == null) {
                    ctx.mouse.move(slot.getRandomPoint().x, slot.getRandomPoint().y);
                    Utils.sleep(Utils.random(100, 250));
                    Timer t = new Timer(3000);
                    while (ctx.menu.getBounds() == null && t.isRunning()) {
                        Utils.sleep(100);
                    }
                }
                if (amt >= ctx.inventory.getItem(itemId).getAmount() || amt == 0) {
                    if (ctx.menu.getBounds() != null) {
                        Point p = ctx.menu.getClickPoint(ctx.menu.getIndex("Deposit " + amt));
                        ctx.mouse.click(p.x, p.y);
                        Utils.sleep(Utils.random(100, 250));
                    }
                } else {
                    if (ctx.menu.getBounds() != null) {
                        Point p = ctx.menu.getClickPoint(ctx.menu.getIndex("Deposit-X"));
                        ctx.mouse.click(p.x, p.y);
                        Utils.sleep(Utils.random(100, 250));
                    }
                }
                break;
        }
        if (stack) {
            return ctx.inventory.getItem(itemId).getAmount() == count - amt;
        }
        return ctx.inventory.getCount(false, itemId) == count - amt;
    }

    public boolean isOpen() {
        if (ctx.widgets.getValidated(new Filter<Widget>() {
            @Override
            public boolean accept(Widget element) {
                return element.getParentId() == WIDGET_ID && element.getId() == 60;
            }
        }).get(0) == null) {
            return false;
        }
        return ctx.widgets.getValidated(new Filter<Widget>() {
                    @Override
                    public boolean accept(Widget element) {
                        return element.getParentId() == WIDGET_ID && element.getId() == 60;
                    }
                }).get(0).getTooltip().contains("Bank");
    }

    public boolean open() {
        if (isOpen()) {
            return true;
        }
        GameObject box = ctx.objects.getNearest(new Filter<GameObject>() {
            @Override
            public boolean accept(GameObject element) {
                for(int id :DEPOSIT_BOX_IDS) {
                if(id == element.getId()) {
                    return true;
                }
                }
                return false;
            }
        });
        if (box != null) {
            box.interact("Deposit");
            Timer t = new Timer(3000);
            while (!isOpen() && t.isRunning()) {
                Utils.sleep(150);
            }
        }
        return isOpen();
    }

}
