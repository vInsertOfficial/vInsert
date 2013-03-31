package org.vinsert.bot.script.api.enums;

import org.vinsert.bot.util.Utils;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Owner
 * Date: 10/03/13
 * Time: 3:20 PM
 * To change this template use File | Settings | File Templates.
 */
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
}
