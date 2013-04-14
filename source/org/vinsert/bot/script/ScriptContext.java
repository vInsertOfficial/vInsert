package org.vinsert.bot.script;

import org.vinsert.bot.Bot;
import org.vinsert.bot.accounts.Account;
import org.vinsert.bot.script.api.tools.*;
import org.vinsert.bot.script.randevent.RandomEventPool;
import org.vinsert.insertion.IClient;


/**
 * The context in which a script executes.
 *
 * @author tommo
 */
public class ScriptContext {

    /**
     * The client instance linked to this script context
     */
    private IClient client;

    /**
     * The bot instance controlling this script
     */
    private Bot bot;

    /**
     * The account which is running this script
     */
    private Account account;

    /**
     * This script context's utility instances
     */
    public Game game;
    public Mouse mouse;
    public Npcs npcs;
    public Players players;
    public Keyboard keyboard;
    public Camera camera;
    public Menu menu;
    public Inventory inventory;
    public Objects objects;
    public Bank bank;
    public Widgets widgets;
    public Navigation navigation;
    public GroundItems groundItems;
    public Skills skills;
    public Settings settings;
    public Equipment equipment;
    public RandomEventPool randomEvents;

    public ScriptContext(Bot bot, IClient client, Account account) {
        this.bot = bot;
        this.client = client;
        this.account = account;
        this.game = new Game(this);
        this.mouse = new Mouse(this);
        this.npcs = new Npcs(this);
        this.players = new Players(this);
        this.keyboard = new Keyboard(this);
        this.camera = new Camera(this);
        this.menu = new Menu(this);
        this.inventory = new Inventory(this);
        this.objects = new Objects(this);
        this.bank = new Bank(this);
        this.widgets = new Widgets(this);
        this.navigation = new Navigation(this);
        this.groundItems = new GroundItems(this);
        this.skills = new Skills(this);
        this.settings = new Settings(this);
        this.equipment = new Equipment(this);
    }

    /**
     * @return The client instance belonging to this context
     */
    public IClient getClient() {
        return client;
    }

    /**
     * @return The bot instance
     */
    public Bot getBot() {
        return bot;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

}
