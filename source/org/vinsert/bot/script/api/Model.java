package org.vinsert.bot.script.api;

import java.awt.Polygon;

import org.vinsert.bot.script.callback.PersistentModelCache.MutableCachedModel;
import org.vinsert.bot.util.Vec3;
import org.vinsert.insertion.IModel;


/**
 * Wraps the {@link IModel} class in the client.
 * @author tommo
 * @author `Discardedx2
 *
 */
public class Model {

	private MutableCachedModel model;

	public Model(MutableCachedModel model) {
		this.model = model;
	}

	/**
	 * @return The vertex count for this model
	 */
	public int getVertices() {
		return model.getVerticesX().length;
	}

	/**
	 * @return The X vertices
	 */
	public int[] getVerticesX() {
		return model.getVerticesX();
	}

	/**
	 * @return The Y vertices
	 */
	public int[] getVerticesY() {
		return model.getVerticesY();
	}

	/**
	 * @return The Z vertices
	 */
	public int[] getVerticesZ() {
		return model.getVerticesZ();
	}
	
	/**
	 * Returns an array of all polygons in this model which have been
	 * translated to screen space
	 * @return The polygons
	 */
	public Polygon[] getPolygons() {
		return null;
	}

	/**
	 * Convenience method to return an array of {@link Vec3}'s denoting vertices
	 * @return The list of vertices
	 */
	public Vec3[][] getVectors() {
		Vec3[][] vectors = new Vec3[model.getTrianglesA().length][3];
		
		int[] amap = getTrianglesA();
		int[] bmap = getTrianglesB();
		int[] cmap = getTrianglesB();
		
		int[] vx = getVerticesX();
		int[] vy = getVerticesY();
		int[] vz = getVerticesZ();
		
		for (int i = 0; i < vectors.length; i++) {
			vectors[i][0] = new Vec3(vx[amap[i]], vx[bmap[i]], vx[cmap[i]]);
			vectors[i][1] = new Vec3(vy[amap[i]], vy[bmap[i]], vy[cmap[i]]);
			vectors[i][2] = new Vec3(vz[amap[i]], vz[bmap[i]], vz[cmap[i]]);
		}
		return vectors;
	}

	/**
	 * @return The amount of triangles in the model
	 */
	public int getTriangles() {
		return model.getTrianglesA().length;
	}

	/**
	 * @return The A triangles
	 */
	public int[] getTrianglesA() {
		return model.getTrianglesA();
	}

	/**
	 * @return The B triangles
	 */
	public int[] getTrianglesB() {
		return model.getTrianglesB();
	}

	/**
	 * @return The C triangles
	 */
	public int[] getTrianglesC() {
		return model.getTrianglesC();
	}

	public boolean isValid() {
		return model != null && model.getVerticesX().length > 0 && model.getVerticesY().length > 0 && model.getVerticesZ().length > 0;
	}

	public int getOrientation() {
		return model.getOrientation();
	}
	
}
