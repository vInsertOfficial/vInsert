package org.vinsert.bot.script.callback;

import org.vinsert.bot.script.callback.PersistentModelCache.MutableCachedModel;
import org.vinsert.insertion.IActor;
import org.vinsert.insertion.IModel;
import org.vinsert.insertion.IRenderable;


/**
 * Model callback used to retrieve renderable's model instances
 * @author tommo
 * @author `Discardedx2
 *
 */
public class ModelCallback {

	public static void callback(IRenderable renderable, IModel model) {
		if (model != null) {
			if (!PersistentModelCache.table.containsKey(renderable)) {
				int orientation = 0;
				if (renderable instanceof IActor) {
					orientation = ((IActor) renderable).getOrientation();
				}
				MutableCachedModel m = new MutableCachedModel(model.getVerticesX(), model.getVerticesY(), model.getVerticesZ(), model.getTriViewX(), model.getTriViewY(), model.getTriViewZ(), orientation);
				PersistentModelCache.table.put(renderable, m);
			} else {
				int orientation = 0;
				if (renderable instanceof IActor) {
					orientation = ((IActor) renderable).getOrientation();
				}
				MutableCachedModel cache = PersistentModelCache.table.get(renderable);
				cache.set(model.getVerticesX(), model.getVerticesY(), model.getVerticesZ(), model.getTriViewX(), model.getTriViewY(), model.getTriViewZ(), orientation);
				PersistentModelCache.table.put(renderable, cache);
			}
		}
	}

}
