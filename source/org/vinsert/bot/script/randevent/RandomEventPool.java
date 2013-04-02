package org.vinsert.bot.script.randevent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vinsert.bot.Bot;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.randevent.impl.AutoLogin;

/**
 * Checks for random event
 * @author tommo
 *
 */
public class RandomEventPool {
	
	/**
	 * The bot instance
	 */
	private Bot bot;
	
	/**
	 * High priority handlers
	 */
	private List<RandomEvent> handlers = new ArrayList<RandomEvent>();
	
	public RandomEventPool(Bot bot) {
		this.bot = bot;
		register(new AutoLogin());
	}
	
	/**
	 * Registers a random event solver to the event pool
	 * @param e The random event solver
	 */
	public void register(RandomEvent e) {
		e.create(new ScriptContext(bot, bot.getLoader().getClient(), bot.getLastAccount()));
		handlers.add(e);
	}
	
	/**
	 * Checks all random event handlers if they need to run
	 */
	public void check() {
		for (RandomEvent rand : getHandlers()) {
			rand.getContext().setAccount(bot.getLastAccount());
			if (!bot.isScriptStackEmpty() && bot.peekScript() != rand && rand.init()) {
				bot.pushScript(rand, true, bot.getLastAccount());
				return;
			}
		}
	}
	
	/**
	 * Returns an immutable, sorted priority list in accordance to the
	 * event's natural ordering
	 * @return
	 */
	public List<RandomEvent> getHandlers() {
		Collections.sort(handlers);
		return Collections.unmodifiableList(handlers);
	}

}
