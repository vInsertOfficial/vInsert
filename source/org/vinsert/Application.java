package org.vinsert;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel;
import org.vinsert.bot.ui.BotVersionChecker;
import org.vinsert.bot.ui.BotWindow;
import org.vinsert.bot.util.VSecruityManager;

public class Application {
	
	public static BotWindow window;

	public static void main(String[] args) {
		if(args.length == 1 && args[0].equals("-dev"))
			Configuration.OFFLINE = true;

		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(new SubstanceGraphiteGlassLookAndFeel());
					JFrame.setDefaultLookAndFeelDecorated(true);
					JPopupMenu.setDefaultLightWeightPopupEnabled(false);
			       		System.setSecurityManager(new VSecruityManager());
					
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