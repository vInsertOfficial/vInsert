package org.vinsert.bot;

import org.vinsert.bot.accounts.Account;
import org.vinsert.bot.loader.HijackLoader;
import org.vinsert.bot.loader.Language;
import org.vinsert.bot.script.Script;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.ui.BotLogger;
import org.vinsert.bot.ui.BotWindow;
import org.vinsert.bot.util.Callback;
import org.vinsert.bot.util.Utils;
import org.vinsert.bot.util.VBLogin;
import org.vinsert.component.HijackCanvas;

import java.applet.Applet;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.EventListener;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * Represents a bot instance
 *
 * @author tommo
 */
public class Bot {

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
     * The last account used to execute a script
     */
    private Account lastAccount;

    /**
     * The thread the bot is running in
     */
    public Thread thread;

    /**
     * The VB Login instance
     */
    private VBLogin login;

    /**
     * The bot window containing this bot instance
     */
    private BotWindow window;

    private boolean initialized;

    public Bot(final BotWindow window) {
        this.window = window;
        this.bot = this;
    }

    /**
     * Starts a new thread for loading the bot client
     */
    public void load() {
        new Thread(new Runnable() {
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

                while ((canvas = getCanvas()) == null) {
                    Utils.sleep(1);
                }

                canvas.setBot(Bot.this);
                Utils.sleep(3000);
                inputHandler = new InputHandler(bot, canvas);
            }
        }).start();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                        if (!scriptStack.isEmpty()) {
                            final Script script = scriptStack.peek();
                            if (script != null) {
                                final ScriptContext context = script.getContext();
                                if (context.randomEvents != null) {
                                    context.randomEvents.check();
                                }
                                if (script.isExitRequested()) {
                                    context.getBot().popScript();
                                    break;
                                }
                                if (!script.isPaused()) {
                                    long time = System.currentTimeMillis();
                                    if (time >= script.getNextExecutionTime()) {
                                        int delay = script.pulse();
                                        if (delay < 0)
                                            context.getBot().popScript();
                                        script.setNextExecutionTime(time + delay);
                                    }
                                }

                                try {
                                    if (script.getNextExecutionTime() > 0) {
                                        long sleep = script.getNextExecutionTime() - System.currentTimeMillis();
                                        if (sleep > 0)
                                            Thread.sleep(script.getNextExecutionTime()
                                                    - System.currentTimeMillis());
                                    } else {
                                        Thread.sleep(500);
                                    }
                                } catch (InterruptedException e1) {
                                    // ignore the exception, because interrupting is normal
                                }
                            }
                        } else {
                            Utils.sleep(5000);
                        }
                    }

            }
        });
        thread.setName("Bot-" + getBotIndex());
        thread.start();
    }

    /**
     * Destroys the applet backing this bot instance
     */
    public void exit() {
        loader.getApplet().stop();
        loader.getApplet().destroy();
    }

    /**
     * Executes the script, and cancels the currently running script if applicable
     *
     * @param script The script to run
     * @param stack  Should the script be stacked upon previous scripts, or wipe the script stack
     *               (temporary scripts such as randoms generally stack, whilst others don't)
     */
    public synchronized void pushScript(Script script, boolean stack, Account account) {
        synchronized (scriptStack) {
            if (!stack) {
                popScript();
            } else if (!initialized) {
                log(Bot.class, Level.SEVERE, "Bot not initialized!");
                return;
            }
            this.lastAccount = account;
            script.create(new ScriptContext(bot, loader.getClient(), account));
            log(Bot.class, Level.FINE, "Starting script: " + script.getManifest().name());

            scriptStack.push(script);
            getCanvas().getListeners().add(script);
            inputHandler.setHumanInput(false);
            if (script instanceof EventListener) {
                inputHandler.addScriptListener((EventListener) script);
            }
            script.begin();
        }
    }

    /**
     * Pops a running script off the stack, if applicable
     *
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

                script.destroy();
                getCanvas().getListeners().remove(script);
                if (scriptStack.isEmpty()) {
                    inputHandler.setHumanInput(true);
                }
                if (script instanceof EventListener) {
                    inputHandler.removeScriptListener((EventListener) script);
                }
                return script;
            }
        }
        return null;
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
        if (loader == null || getApplet() == null || getApplet().getComponentCount() == 0 || !(getApplet().getComponent(0) instanceof HijackCanvas)) {
            return null;
        }
        return (HijackCanvas) getApplet().getComponent(0);
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
        synchronized (scriptStack)
        {
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

    public synchronized BotWindow getWindow() {
        return window;
    }

    public synchronized void setWindow(BotWindow window) {
        this.window = window;
    }
}