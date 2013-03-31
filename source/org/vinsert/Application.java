package org.vinsert;

import org.vinsert.bot.ui.BotWindow;
import org.vinsert.bot.util.VSecruityManager;

public class Application {
	
	private static BotWindow window;

	public static void main(String[] args) throws Exception {
		window = new BotWindow();
        System.setSecurityManager(new VSecruityManager());
		Configuration.checkVersion();
        Configuration.mkdirs();
		window.init();
	}

    public static BotWindow getBotWindow() {
        return window;
    }

}