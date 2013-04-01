package org.vinsert.bot.util;

import org.vinsert.Application;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author iJava
 */
public class Screenshot {

    public static BufferedImage getGameImage() {
        return Application.getBotWindow().getActiveBot().getCanvas().getGameBuffer();
    }

    public static void takeScreenshot(String path) {
        try {
            File file = new File(path);
            file.createNewFile();
            ImageIO.write(getGameImage(), "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
