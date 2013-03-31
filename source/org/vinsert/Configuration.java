package org.vinsert;


import java.io.File;

import org.vinsert.bot.loader.arch.ArchiveClassLoader;
import org.vinsert.bot.ui.BotWindow;

/**
 * Configuration variables
 * @author tommo
 *
 */
//TODO: this shit needs some serious refactoring

public class Configuration {
	
	public static final String BOT_NAME = "vInsert";
	public static final int BOT_VERSION_MAJOR = 2;
	public static final int BOT_VERSION_MINOR = 15;
	public static final String BOT_DESC = "Written by Discardedx2 & Tommo";
	
	private static int remote_major;
	private static int remote_minor;

	/* 		
	 * 		vinsert.org
	 */
	private static final String RES_BODY = "dmluc2VydC5vcmc=";
	private static final byte[] RES_HEAD, RES_TAIL;
	public static final String jsonfile = "insertions.json.gz";
	public static final String vesrionfile = "version.txt";	

	
	public static final int BOT_APPLET_WIDTH = 765;
	public static final int BOT_APPLET_HEIGHT = 503;
	public static final int BOT_LOGGER_WIDTH = BOT_APPLET_WIDTH;
	public static final int BOT_LOGGER_HEIGHT = 120;
	public static final int BOT_TOOLBAR_WIDTH = BOT_APPLET_WIDTH;
	public static final int BOT_TOOLBAR_HEIGHT = 24;
	public static final int BOT_UI_WIDTH = BOT_APPLET_WIDTH;
	public static final int BOT_UI_HEIGHT = BOT_APPLET_HEIGHT + BOT_TOOLBAR_HEIGHT;

    public static final String STORAGE_DIR = System.getProperty("user.home") + File.separator + BOT_NAME;
    public static final String SCRIPTS_DIR = STORAGE_DIR + File.separator + "Scripts";
    public static final String ACCOUNTS_DIR = STORAGE_DIR + File.separator + "Accounts";
    public static final String SCREENSHOTS_DIR = STORAGE_DIR + File.separator + "Screenshots";
    public static final String SOURCE_DIR = SCRIPTS_DIR + File.separator + "sources";
    public static final String COMPILED_DIR = SCRIPTS_DIR + File.separator + "compiled";

    public static final String SCRIPT_PATH_FILE = SCRIPTS_DIR + File.separator + "path.txt";
    
	private static final char[] ALPHA;
	private static final int[] ALPHATABLE = new int[128];
	static {
		// http://
		RES_HEAD = new byte[] { 104, 116, 116, 112, 58, 47, 47 };
		// /bot/
		RES_TAIL = new byte[] { 47, 98, 111, 116, 47};
		ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
		for(int i = 0; i < ALPHA.length; ++i) {
			ALPHATABLE[ALPHA[i]] = i;
		}
	}
	
	private static String decode(byte[] b) {
		return new String(b);
	}
	
	private static String decode(String s) {
		int delta = s.endsWith("==") ? 2 : s.endsWith("=") ? 1 : 0;
		byte[] buffer = new byte[s.length() * 3 / 4 - delta];
		int mask = 0xFF;
		int index = 0;
		for(int i = 0; i < s.length(); i += 4) {
			int c = ALPHATABLE[s.charAt(i)], c_ = ALPHATABLE[s.charAt(i + 1)];
			buffer[index++] = (byte) (((c << 2) | (c_ >> 4)) & mask);
			if (index >= buffer.length) {
				return new String(buffer);
			}
			int c2 = ALPHATABLE[s.charAt(i + 2)];
			buffer[index++] = (byte) (((c_ << 4) | (c2 >> 2)) & mask);
			if (index >= buffer.length) {
				return new String(buffer);
			}
			int c3 = ALPHATABLE[s.charAt(i + 3)];
			buffer[index++] = (byte) (((c2 << 6) | c3) & mask);
		}
		return new String(buffer);
	}
	
	public static String composeres() {
		return decode(RES_HEAD) + decode(RES_BODY) + decode(RES_TAIL);
	}
    
    public static void checkVersion() {
    	try {
			String str = ArchiveClassLoader.getText(composeres() + vesrionfile);
			String[] strargs = str.split(",");
			remote_major = Integer.parseInt(strargs[0]);
			remote_minor = Integer.parseInt(strargs[1]);
            System.out.println("Local version: " + BOT_VERSION_MAJOR + "." + BOT_VERSION_MINOR);
			System.out.println("Remote version: " + remote_major + "." + remote_minor);
			if (remote_major > BOT_VERSION_MAJOR || remote_minor > BOT_VERSION_MINOR) {
				BotWindow.error("Out of date!", "Vinsert has been updated, re-download the new version at http://vinsert.org/");
				System.exit(0);
			}
    	} catch (Exception e) {
    		BotWindow.warn("Connection error", "Could not verify bot version from server, entering offline mode.");
    		//ignore
    	}
    }

    public static void mkdirs() {
        final String[] dirs = {STORAGE_DIR, SCRIPTS_DIR, ACCOUNTS_DIR, SCREENSHOTS_DIR, SOURCE_DIR, COMPILED_DIR};
        for(String dir :dirs) {
            File file = new File(dir);
            if(!file.exists()) {
                file.mkdir();
            }
        }
    }

	public static int getMinor() {
		return remote_minor;
	}

	public static final boolean JAR = false;

}
