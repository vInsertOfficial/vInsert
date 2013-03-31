package org.vinsert.bot.script;

import java.util.logging.Level;

import org.vinsert.bot.script.api.Player;
import org.vinsert.bot.script.api.tools.Bank;
import org.vinsert.bot.script.api.tools.Camera;
import org.vinsert.bot.script.api.tools.Equipment;
import org.vinsert.bot.script.api.tools.Game;
import org.vinsert.bot.script.api.tools.GroundItems;
import org.vinsert.bot.script.api.tools.Inventory;
import org.vinsert.bot.script.api.tools.Keyboard;
import org.vinsert.bot.script.api.tools.Menu;
import org.vinsert.bot.script.api.tools.Mouse;
import org.vinsert.bot.script.api.tools.Navigation;
import org.vinsert.bot.script.api.tools.Npcs;
import org.vinsert.bot.script.api.tools.Objects;
import org.vinsert.bot.script.api.tools.Players;
import org.vinsert.bot.script.api.tools.Settings;
import org.vinsert.bot.script.api.tools.Skills;
import org.vinsert.bot.script.api.tools.Widgets;
import org.vinsert.bot.util.Utils;
import org.vinsert.bot.util.VBLogin;
import org.vinsert.component.ProjectionListener;


/**
 * Base script class
 * @author tommo
 * @author `Discardedx2
 *
 */
public abstract class Script implements ProjectionListener {

	/**
	 * This script's manifest
	 */
	private ScriptManifest manifest;

	/**
	 * This script's client context
	 */
	private ScriptContext context;

	/**
	 * Has the script requested an exit
	 */
	private boolean exitRequested = false;
	
	/**
	 * Is the script currently paused
	 */
	private boolean paused = false;
	
	/**
	 * The context's script classes
	 */
	public Bank bank;
	public Camera camera;
	public Inventory inventory;
	public Keyboard keyboard;
	public Menu menu;
	public Mouse mouse;
	public Navigation navigation;
	public Npcs npcs;
	public Objects objects;
	public Players players;
	public Widgets widgets;
	public Player localPlayer;
	public Game game;
	public Skills skills;
	public Settings settings;
	public GroundItems groundItems;
	public Equipment equipment;
	
	public Script() {

	}

    private boolean canUse() {
        if(!VBLogin.self.isLoggedIn()) {
           return false;
        }
        
        if (VBLogin.self.getUsergroupId() == 8) {
        	return false;
        }
        
        int[] vip = {10, 11, 6, 9};
        if(manifest.type() == ScriptType.FREE) {
            return true;
        }
        if(manifest.type() == ScriptType.VIP) {
            for(int id : vip) {
                if(id == VBLogin.self.getUsergroupId()) {
                    return true;
                }
            }
        }
        return false;
    }

	/**
	 * FOR INTERNAL USE. Called by the bot to create the script.
	 * @param context
	 */
	public void create(ScriptContext context) throws RuntimeException {
		this.context = context;
		if (this.getClass().isAnnotationPresent(ScriptManifest.class)) {
			this.manifest = this.getClass().getAnnotation(ScriptManifest.class);
		} else {
			throw new RuntimeException("No ScriptManifest defined!");
		}
        if(!canUse()) {
            throw new RuntimeException("Cannot use this script");
        }
		this.game = context.game;
		this.bank = context.bank;
		this.camera = context.camera;
		this.inventory = context.inventory;
		this.keyboard = context.keyboard;
		this.menu = context.menu;
		this.mouse = context.mouse;
		this.navigation = context.navigation;
		this.npcs = context.npcs;
		this.objects = context.objects;
		this.players = context.players;
		this.widgets = context.widgets;
		this.skills = context.skills;
		this.settings = context.settings;
		this.groundItems = context.groundItems;
		this.localPlayer = context.players.getLocalPlayer();
		this.equipment = context.equipment;
	}

	/**
	 * Called when the script is executed.
	 */
	public abstract boolean init();

	/**
	 * The main loop method. Next execution time will be the value returned by this method.
	 * @return The next execution time.
	 */
	public abstract int pulse();

	/**
	 * Called when the script is exited, any final logic happens here.
	 */
	public abstract void close();

	/**
	 * Logs the message to the bot logger
	 * @param string The string to log
	 */
	public void log(String string) {
		context.getBot().log(manifest.name(), string);
	}

	/**
	 * Logs the message to the bot logger with given severity
	 * @param level The severity level
	 * @param string The string to log
	 */
	public void log(Level level, String string) {
		context.getBot().log(manifest.name(), level, string);
	}

	/**
	 * @return Has the script requested an exit
	 */
	public boolean isExitRequested() {
		return exitRequested;
	}

	/**
	 * Requests the bot to cancel this running script.
	 */
	public void requestExit() {
		exitRequested = true;
	}
	
	/**
	 * @return Is the script currently paused
	 */
	public boolean isPaused() {
		return paused;
	}
	
	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	/**
	 * @return The script context the script is running in
	 */
	public ScriptContext getContext() {
		return context;
	}

	/**
	 * @return The script manifest
	 */
	public ScriptManifest getManifest() {
		return manifest;
	}

	///////////////////////////////////
	//EASE OF USE METHODS
	//////////////////////////////////
	
	/**
	 * Returns a random number between 0 and max
	 * @param max
	 * @return
	 */
	public int random(int max) {
		return Utils.random(0, max);
	}
	
	/**
	 * Returns a random number between min and max
	 * @param min
	 * @param max
	 * @return
	 */
	public int random(int min, int max) {
		return Utils.random(min, max);
	}

	/**
	 * Sleeps for the given time in millis
	 * @param millis
	 */
	public void sleep(int millis) {
		Utils.sleep(millis);
	}

	/**
	 * Sleeps for a random time between min and max milliseconds
	 * @param minMillis
	 * @param maxMillis
	 */
	public void sleep(int minMillis, int maxMillis) {
		Utils.sleep(Utils.random(minMillis, maxMillis));
	}
	
	/**
	 * Returns a random element from the given array
	 * @param array
	 * @return
	 */
	public <T> T random(T[] array) {
		return array[random(0, array.length)];
	}
	
}
