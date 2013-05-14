package org.vinsert.component.debug;

import org.vinsert.bot.script.api.Player;
import org.vinsert.bot.util.Perspective;

import java.awt.*;


public class DebugPlayersInfo extends Debugger {

    @Override
    public void draw(Graphics2D graphics) {
        FontMetrics metrics = graphics.getFontMetrics();
        for (Player player : context.players.getAll()) {
            final Point point = Perspective.trans_tile_screen(context.getClient(), player.getLocation(), 0.5, 0.5, player.getModelHeight() / 2);
            String loc = player.getLocation().toString();
            String name = player.getName();
            int anim = player.getAnimation();
            graphics.setColor(Color.GREEN);
            graphics.fillOval(point.x - 2, point.y - 2, 4, 4);
            graphics.setColor(player.isInCombat() ? Color.RED : Color.WHITE);
            graphics.drawString(loc, point.x - metrics.stringWidth(loc) / 2, point.y);
            graphics.drawString(name, point.x - metrics.stringWidth(name) / 2, point.y + 15);
            graphics.drawString(String.valueOf(anim), point.x - metrics.stringWidth(String.valueOf(anim)) /2, point.y +30);
        }
    }

}
