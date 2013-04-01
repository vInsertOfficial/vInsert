package org.vinsert.bot;

import java.applet.Applet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.vinsert.bot.accounts.Account;
import org.vinsert.bot.loader.HijackLoader;
import org.vinsert.bot.loader.Language;
import org.vinsert.bot.script.Script;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.randevent.RandomEventPool;
import org.vinsert.bot.ui.BotLogger;
import org.vinsert.bot.util.Callback;
import org.vinsert.bot.util.Utils;
import org.vinsert.bot.util.VBLogin;
import org.vinsert.component.HijackCanvas;


/**
 * Represents a bot instance
 * @author tommo
 *
 */
public class Bot implements Runnable {

	/**
	 * The client loader
	 */
	private HijackLoader loader;

	/**
	 * The bot logger
	 */
	private BotLogger logger;

	/**
	 * The active script stack
	 */
	public Stack<Script> scriptStack = new Stack<Script>();

	/**
	 * The callback the UI supplies to call when the bot applet is loaded and initialized
	 */
	private Callback callback;

	/**
	 * The bot's input handler
	 */
	private InputHandler inputHandler;

	/**
	 * The bot instance, since scripts are created on a separate thread
	 * we must store the local bot instance for reference
	 */
	private Bot bot;
	
	/**
	 * The canvas
	 */
	private HijackCanvas canvas;
	
	/**
	 * The bot's index in the tab list
	 */
	private int botIndex;
	
	/**
	 * The next script execution timestamp
	 */
	private long nextExecutionTime = -1;
	
	/**
	 * The last account used to execute a script
	 */
	private Account lastAccount;
	
	/**
	 * This bot's random event pool
	 */
	private RandomEventPool randomEventPool;
	
	/**
	 * The thread the bot is running in
	 */
	public Thread thread;
	
	/**
	 * Shall the bot exit
	 */
	private boolean exit = false;

	/**
	 * The VB Login instance
	 */
	private VBLogin login;

    private boolean initialized;

    public Bot() {
		this.bot = this;
	}

	@Override
	public void run() {
		try {
			loader = HijackLoader.create(Language.ENGLISH, true);
			callback.call();
		} catch (Exception e) {
			e.printStackTrace();
			log(Bot.class, Level.SEVERE, "Error loading bot!");
		}
        initialized = true;
		log(Bot.class, "Bot now active.");
		
		//just loop until canvas is initialized
		HijackCanvas c;
		while((c = getCanvas()) == null) {
            Utils.sleep(1);
		}
		
		/*
		 * Initialize
		 */
		canvas = c;
		inputHandler = new InputHandler(this, canvas);
		randomEventPool = new RandomEventPool(this);
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				synchronized (scriptStack) {
					String tag = scriptStack.isEmpty() ? "Exception" : scriptStack.peek().getClass().getSimpleName();
					log(tag, Level.SEVERE, e.getClass().getSimpleName() + " caught in thread " + t.getName() + ": " + e.getMessage());
					for (StackTraceElement ste : e.getStackTrace()) {
						log(tag, Level.SEVERE, ste.toString());
					}
				}
			}
		});

		/*
		 * Start the main bot cycle
		 */
		while (!exit) {
			synchronized (scriptStack) {
                randomEventPool.check();
                if (!scriptStack.isEmpty() && scriptStack.peek() != null) {
                    Script active = scriptStack.peek();
                    if (active.isExitRequested()) {
                        popScript();
                    }
                    if (!active.isPaused()) {
                        long time = System.currentTimeMillis();
                        if (time >= nextExecutionTime) {
                            try {
                                int delay = active.pulse();
                                if (delay < 0) popScript();
                                nextExecutionTime = time + delay;
                            } catch (final Throwable t) {
                                //prevent the bot thread from stopping if the script throws an exception (shit scripter failsafe)
                                log(active, t);
                                Utils.sleep(1000);
                            }
                        }
                    } else {
                        Utils.sleep(1000);
                    }
                }
			}
			
			try {
				if (nextExecutionTime > 0) {
					long sleep = nextExecutionTime - System.currentTimeMillis();
					if (sleep > 0)
						Thread.sleep(nextExecutionTime - System.currentTimeMillis());
				} else {
					Thread.sleep(200);
				}
			} catch (InterruptedException e1) {
				//ignore the exception, because interrupting is normal
			}
		}
		
		loader.getApplet().stop();
		loader.getApplet().destroy();
	}

	/**
	 * Executes the script, and cancels the currently running script if applicable
	 * @param script The script to run
	 * @param stack Should the script be stacked upon previous scripts, or wipe the script stack
	 * (temporary scripts such as randoms generally stack, whilst others don't)
	 */
	public synchronized void pushScript(Script script, boolean stack, Account account) {
		synchronized (scriptStack) {
			if (!stack) {
				popScript();
			} else if (!initialized) {
                return;
            }
			this.lastAccount = account;
			script.create(new ScriptContext(bot, loader.getClient(), account));
			log(Bot.class, Level.FINE, "Starting script: " + script.getManifest().name());

            boolean init;
            try {
                init = script.init();
            } catch (final Throwable t) {
                log(script, t);
                init = false;
            }
            if (init) {
				scriptStack.push(script);
				getCanvas().getListeners().add(script);
				inputHandler.setHumanInput(false);
			} else {
				log(Bot.class, Level.WARNING, "Script " + script.getManifest().name() + " refused to start.");
			}
		}
	}

	/**
	 * Pops a running script off the stack, if applicable
	 * @return The script which was closed, null if none
	 */
	public synchronized Script popScript() {
		synchronized (scriptStack) {
			if (!scriptStack.isEmpty() && scriptStack.peek() != null) {
				Script script = scriptStack.pop();
				log(Bot.class, Level.FINE, "Stopping script " + script.getManifest().name() + "...");
                try {
                    script.close();
                } catch (final Throwable t) {
                    log(script, t);
                }
				getCanvas().getListeners().remove(script);
				if (scriptStack.isEmpty()) {
					inputHandler.setHumanInput(true);
				}
				return script;
			}
		}
		return null;
	}
	
	/**
	 * Causes the bot to exit
	 */
	public void exit() {
		exit = true;
	}

	public synchronized void log(Class<?> source, String msg) {
		LogRecord record = new LogRecord(Level.INFO, msg);
		record.setSourceClassName(source.getSimpleName());
		getLogger().log(record);
	}

	public synchronized void log(Class<?> source, Level level, String msg) {
		LogRecord record = new LogRecord(level, msg);
		record.setSourceClassName(source.getSimpleName());
		getLogger().log(record);
	}

	public synchronized void log(String tag, String msg) {
		LogRecord record = new LogRecord(Level.INFO, msg);
		record.setSourceClassName(tag);
		getLogger().log(record);
	}

	public synchronized void log(String tag, Level level, String msg) {
		LogRecord record = new LogRecord(level, msg);
		record.setSourceClassName(tag);
		getLogger().log(record);
	}

    public synchronized void log(final Script s, final Throwable t) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        t.printStackTrace(printWriter);
        log(s.getClass(), Level.SEVERE, result.toString());
    }


    public synchronized void setLogger(BotLogger logger) {
		this.logger = logger;
	}

	public synchronized BotLogger getLogger() {
		return logger;
	}

	public synchronized Applet getApplet() {
		return loader.getApplet();
	}

	public synchronized HijackLoader getLoader() {
		return loader;
	}

	public synchronized HijackCanvas getCanvas() {
		if (canvas != null) {
			return canvas;
		}
		if (!(loader.getApplet().getComponentAt(100, 100) instanceof HijackCanvas)) {
			return null;
		}
		return (HijackCanvas) loader.getApplet().getComponentAt(100, 100);
	}

	public synchronized InputHandler getInputHandler() {
		return inputHandler;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

	public int getBotIndex() {
		return botIndex;
	}

	public void setBotIndex(int botIndex) {
		this.botIndex = botIndex;
	}

	public Account getLastAccount() {
		return lastAccount;
	}

	public Script peekScript() {
		synchronized (scriptStack) {
			if (scriptStack.isEmpty()) return null;
			
			return scriptStack.peek();
		}
	}
	
	public boolean isScriptStackEmpty() {
		synchronized (scriptStack) {
			return scriptStack.isEmpty();
		}
	}

	public VBLogin getLogin() {
		return login;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public Thread getThread() {
		return thread;
	}

	public RandomEventPool getRandomEvents() {
		return randomEventPool;
	}

}