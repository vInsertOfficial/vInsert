package org.vinsert.bot.script.api.tools;

import java.awt.Point;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.api.Item;
import org.vinsert.bot.util.Condition;
import org.vinsert.bot.util.Filter;
import org.vinsert.bot.util.Utils;

/**
 * @author tholomew
 */
public class DepositBox {

    public enum DepositBoxSlot {

        SLOT_0(new Point(145, 92), 0), SLOT_1(new Point(185, 92), 1), SLOT_2(new Point(225, 92), 2),
        SLOT_3(new Point(265, 92), 3), SLOT_4(new Point(305, 92), 4), SLOT_5(new Point(345, 92), 5),
        SLOT_6(new Point(385, 92), 6), SLOT_7(new Point(145, 132), 7), SLOT_8(new Point(185, 132), 8),
        SLOT_9(new Point(225, 132), 9), SLOT_10(new Point(265, 132), 10), SLOT_11(new Point(305, 132), 11),
        SLOT_12(new Point(345, 132), 12), SLOT_13(new Point(385, 132), 13), SLOT_14(new Point(145, 172), 14),
        SLOT_15(new Point(185, 172), 15), SLOT_16(new Point(225, 172), 16), SLOT_17(new Point(265, 172), 17),
        SLOT_18(new Point(305, 172), 18), SLOT_19(new Point(345, 172), 19), SLOT_20(new Point(385, 172), 20),
        SLOT_21(new Point(145, 212), 21), SLOT_22(new Point(185, 212), 22), SLOT_23(new Point(225, 212), 23),
        SLOT_24(new Point(265, 212), 24), SLOT_25(new Point(305, 212), 25), SLOT_26(new Point(345, 212), 26),
        SLOT_27(new Point(385, 212), 27);
        private Point center;
        private int invIndex;

        private DepositBoxSlot(Point center, int invIndex) {
            this.center = center;
            this.invIndex = invIndex;
        }

        public Point getCentralPoint() {
            return center;
        }

        public int getInvIndex() {
            return invIndex;
        }

        public Point getRandomPoint() {
            int plus = Utils.random(0, 1);
            if (plus == 1) {
                return new Point((center.x + (Utils.random(2, 4))), center.y + (Utils.random(2, 4)));
            }
            return new Point((center.x - (Utils.random(2, 4))), center.y - (Utils.random(2, 4)));
        }

        public static DepositBoxSlot getSlot(int slot) {
            for (DepositBoxSlot dps : DepositBoxSlot.values()) {
                if (dps.getInvIndex() == slot) {
                    return dps;
                }
            }
            return null;
        }
    }
    public static final int[] DEPOSIT_BOX_IDS = {9398, 20228};
    private static final int WIDGET_ID = 11;
    private static final int CLOSE = 62;
    /**
     * The script context.
     */
    private ScriptContext ctx;

    public DepositBox(ScriptContext ctx) {
        this.ctx = ctx;
    }

    /**
     *
     * @return if deposit box interface is open
     */
    public boolean isOpen() {
        return ctx.widgets.get(WIDGET_ID, CLOSE) != null;
    }
    
    /**
     * 
     * @param filter - don't deposit
     */
    public void depositAllExcept(Filter<Item> filter) {
        if (isOpen()) {
            for (Item i : ctx.inventory.getUniqueItems()) {
                if (filter.accept(i)) {
                    deposit(i, 0);
                }
            }
        }
    }

    /**
     * deposits all items
     */
    public void depositAll() {
        if (isOpen()) {
            for (Item i : ctx.inventory.getUniqueItems()) {
                deposit(i, 0);
            }
        }
    }

    /**
     * deposits a specific item
     *
     * @param i item to deposit
     * @param amt amount of item, 0 for all
     */
    public void deposit(Item i, int amt) {
        boolean stacks = i.getAmount() > 1;
        if (isOpen()) {
            Point itemPoint = DepositBoxSlot.getSlot(ctx.inventory.getSlot(i)).getRandomPoint();
            ctx.mouse.move(itemPoint);
            Utils.sleep(100, 250);
            if (ctx.inventory.getCount(stacks, DEPOSIT_BOX_IDS) > 1) {
                ctx.mouse.click(true);
                Utils.waitFor(new Condition() {

                    @Override
                    public boolean validate() {
                        return ctx.menu.isMenuOpen();
                    }
                }, 1000);
                switch (amt) {
                    case 0: //all
                        ctx.menu.click("Deposit All");
                        break;
                    case 5: //5
                        ctx.menu.click("Deposit 5");
                        break;
                    case 10: //10
                        ctx.menu.click("Deposit 10");
                        break;
                    default:    //x amount
                        ctx.menu.click("Deposit X");
                        Utils.waitFor(new Condition() {

                            @Override
                            public boolean validate() {
                                return ctx.widgets.get(548, 93) != null;
                            }
                        }, 1000);
                        ctx.keyboard.type(String.valueOf(amt), true);
                        break;
                }
            } else {
                ctx.mouse.click(false);
            }
        }
    }
    
    /**
     * closes the deposit box interface
     */
    public void close() {
        if (isOpen()) {
            ctx.widgets.get(WIDGET_ID, CLOSE).click();
        }
    }
}
