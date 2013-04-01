package org.vinsert.bot.script.api;

import org.vinsert.bot.script.ScriptContext;
import org.vinsert.bot.script.callback.PersistentModelCache;
import org.vinsert.insertion.IRenderable;


/**
 * Wraps the {@link IRenderable} class in the client
 * @author tommo
 *
 */
public class Renderable {

	@SuppressWarnings("unused")
	private ScriptContext ctx;
	private IRenderable renderable;

	public Renderable(ScriptContext ctx, IRenderable renderable) {
		this.ctx = ctx;
		this.renderable = renderable;
	}

	/**
	 * @return The renderable entity's model height.
	 */
	public int getModelHeight() {
		return renderable.getModelHeight();
	}
	
	/**
	 * @return The renderable's model
	 */
	public Model getModel() {
		return new Model(PersistentModelCache.table.get(renderable));
	}
	
}
