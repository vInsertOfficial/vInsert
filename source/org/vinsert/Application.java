package org.vinsert;

import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel;
import org.vinsert.bot.ui.BotErrorDialog;
import org.vinsert.bot.ui.BotVersionChecker;
import org.vinsert.bot.ui.BotWindow;
import org.vinsert.bot.util.Settings;
import org.vinsert.bot.util.VSecruityManager;

public class Application {
	
	public static BotWindow window;

	public static void main(String[] args) {
		if(args.length == 1 && args[0].equals("-dev"))
			Configuration.OFFLINE = true;

		Settings.read();
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
			       	switch(Settings.get("theme")) {
			       	case "System":
			       		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			       		break;
			       	case "Graphite":
			       		UIManager.setLookAndFeel(new SubstanceGraphiteGlassLookAndFeel());
			       		break;
			       	}
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
					
					if(Configuration.OFFLINE) {
						window = new BotWindow();
						window.init(false);
					} else new BotVersionChecker();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}