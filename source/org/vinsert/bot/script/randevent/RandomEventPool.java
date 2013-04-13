package org.vinsert.bot.script.randevent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.randevent.impl.*;

/**
 * Checks for random event
 * @author tommo
 *
 */
public class RandomEventPool {
	
	private ScriptContext ctx;
	
	/**
	 * High priority handlers
	 */
	private List<RandomEvent> handlers = new ArrayList<RandomEvent>();
	
	public RandomEventPool(ScriptContext ctx) {
		this.ctx = ctx;
		register(new AutoLogin());
                register(new BankPin());
                register(new ClickToPlay());
                register(new TalkingRandom());
                register(new Ent());
                register(new SandwichLady());
                register(new MysteryBox());
	}
	
	/**
	 * Registers a random event solver to the event pool
	 * @param e The random event solver
	 */
	public void register(RandomEvent e) {
		e.create(new ScriptContext(ctx.getBot(), ctx.getClient(), ctx.getBot().getLastAccount()));
		handlers.add(e);
	}
	
	/**
	 * Checks all random event handlers if they need to run
	 */
	public void check() {
		for (RandomEvent rand : getHandlers()) {
			rand.getContext().setAccount(ctx.getBot().getLastAccount());
			if (!ctx.getBot().isScriptStackEmpty() && ctx.getBot().peekScript() != rand && rand.init()) {
				ctx.getBot().pushScript(rand, true, ctx.getBot().getLastAccount());
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
