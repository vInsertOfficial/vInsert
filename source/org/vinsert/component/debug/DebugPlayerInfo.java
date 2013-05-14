package org.vinsert.component.debug;

import org.vinsert.bot.script.api.Tile;

import java.awt.*;


public class DebugPlayerInfo extends IncrementalDebugger {

    @Override
    public void draw(Graphics2D graphics, Point point) {
        graphics.setColor(context.players.getLocalPlayer().isInCombat() ? Color.RED : Color.WHITE);
        StringBuilder text = new StringBuilder();
        text.append("Player: ");
        int anim = context.players.getLocalPlayer().getAnimation();
        text.append("anim=").append(anim);
        Tile loc = context.players.getLocalPlayer().getLocation();
        text.append(", location=").append(loc.toString());
        String speech = context.players.getLocalPlayer().getSpeech();
        text.append(", speech=").append(speech);
        boolean moving = context.players.getLocalPlayer().isMoving();
        text.append(", moving=").append(moving);
        graphics.drawString(text.toString(), point.x, point.y);
    }

}
