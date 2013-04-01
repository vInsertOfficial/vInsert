package org.vinsert.component.debug;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.vinsert.bot.Bot;
import org.vinsert.bot.script.ScriptContext;
import org.vinsert.component.ProjectionListener;


public abstract class Debugger implements ProjectionListener {
	
	protected ScriptContext context;
	
	public Debugger() {
		
	}
	
	@Override
	public void render(Graphics2D g) {
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHints(rh);
        draw(g);
	}
	
	public abstract void draw(Graphics2D g);
	
	public void install(Bot bot) {
		this.context = new ScriptContext(bot, bot.getLoader().getClient(), null);
	}
	
	public void uninstall() {
		
	}
	
	public ScriptContext getContext() {
		return context;
	}
	
}
