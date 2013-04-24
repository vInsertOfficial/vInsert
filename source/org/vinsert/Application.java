package org.vinsert;

import org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel;
import org.vinsert.bot.ui.BotErrorDialog;
import org.vinsert.bot.ui.BotVersionChecker;
import org.vinsert.bot.ui.BotWindow;
import org.vinsert.bot.util.VSecruityManager;

import javax.swing.*;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

public class Application {

    public static BotWindow window;

    public static void main(final String... args) throws IOException {
        if (args.length == 1 && args[0].equals("-dev"))
            Configuration.OFFLINE_MODE = true;

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    /*
					 * Must disable the strict EDT checking for substance
					 * (almost must be set before the LAF is changed)
					 */
                    System.setProperty("insubstantial.checkEDT", "false");
                    System.setProperty("insubstantial.logEDT", "false");
                    UIManager.setLookAndFeel(new SubstanceGraphiteGlassLookAndFeel());
                    JFrame.setDefaultLookAndFeelDecorated(true);
                    JPopupMenu.setDefaultLightWeightPopupEnabled(false);
                    System.setSecurityManager(new VSecruityManager());

                    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                        @Override
                        public void uncaughtException(final Thread t, final Throwable e) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    BotErrorDialog.show("Error in thread: " + t.getName(), e);
                                }
                            });
                        }
                    });

                    Configuration.mkdirs();
                    if (Configuration.OFFLINE_MODE) {
                        window = new BotWindow();
                        window.init();
                    } else new BotVersionChecker();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}