package org.vinsert.bot.script.callback;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.vinsert.bot.util.Perspective;
import org.vinsert.insertion.IRenderable;


/**
 * Model instances are hooked and designated to their renderable instances in
 * update methods in the client, and since our api wrappers are immutable, we
 * must lookup models in this global table
 * 
 * @author tommo
 * 
 */
public class PersistentModelCache {

	/**
	 * Global mappings from renderable instances to their models
	 */
	public static Map<IRenderable, MutableCachedModel> table = new ConcurrentHashMap<IRenderable, MutableCachedModel>();

	private PersistentModelCache() {

	}

	/**
	 * Cached model vertices, vertices will be updated accordingly
	 * 
	 * @author tommo
	 * 
	 */
	public static class MutableCachedModel {

		private int[] verticesX;
		private int[] verticesY;
		private int[] verticesZ;
		private int[] trianglesA;
		private int[] trianglesB;
		private int[] trianglesC;
		private int orientation;

		private int[] x_base;
		private int[] z_base;

		public MutableCachedModel(int[] verticesX, int[] verticesY,
				int[] verticesZ, int[] trianglesA, int[] trianglesB,
				int[] trianglesC, int orientation) {
			set(verticesX, verticesY, verticesZ, trianglesA, trianglesB,
					trianglesC, orientation);
		}

		public void set(int[] verticesX, int[] verticesY, int[] verticesZ,
				int[] trianglesA, int[] trianglesB, int[] trianglesC,
				int orientation) {
			this.verticesX = new int[verticesX.length];
			this.verticesY = new int[verticesY.length];
			this.verticesZ = new int[verticesZ.length];
			this.trianglesA = new int[trianglesA.length];
			this.trianglesB = new int[trianglesB.length];
			this.trianglesC = new int[trianglesC.length];
			
			this.verticesX = Arrays.copyOfRange(verticesX, 0, verticesX.length);
			this.verticesY = Arrays.copyOfRange(verticesY, 0, verticesY.length);
			this.verticesZ = Arrays.copyOfRange(verticesZ, 0, verticesZ.length);
			this.trianglesA = Arrays.copyOfRange(trianglesA, 0, trianglesA.length);
			this.trianglesB = Arrays.copyOfRange(trianglesB, 0, trianglesB.length);
			this.trianglesC = Arrays.copyOfRange(trianglesC, 0, trianglesC.length);
			
			this.orientation = orientation;

			if (orientation != 0) {
				x_base = new int[verticesX.length];
				z_base = new int[verticesZ.length];
				x_base = Arrays.copyOfRange(verticesX, 0, verticesX.length);
				z_base = Arrays.copyOfRange(verticesZ, 0, verticesZ.length);
				verticesX = new int[x_base.length];
				verticesZ = new int[z_base.length];
				rotate();
			}
		}

		/**
		 * Performs a y rotation camera transform, where the character's
		 * orientation is the rotation around the y axis in fixed point radians.
		 * [cos(t), 0, sin(t) 0, 1, 0 -sin(t), 0, cos(t)]
		 */
		public void rotate() {
			int theta = orientation & 0x3fff;
			int sin = Perspective.SINE[theta];
			int cos = Perspective.COSINE[theta];
			for (int i = 0; i < x_base.length; ++i) {
				// Note that the second row of the matrix would result
				// in no change, as the y coordinates are always unchanged
				// by rotation about the y axis.
				verticesX[i] = (x_base[i] * cos + z_base[i] * sin >> 15) >> 1;
				verticesZ[i] = (z_base[i] * cos - x_base[i] * sin >> 15) >> 1;
			}
		}

		public int[] getVerticesX() {
			return verticesX;
		}

		public void setVerticesX(int[] verticesX) {
			this.verticesX = verticesX;
		}

		public int[] getVerticesY() {
			return verticesY;
		}

		public void setVerticesY(int[] verticesY) {
			this.verticesY = verticesY;
		}

		public int[] getVerticesZ() {
			return verticesZ;
		}

		public void setVerticesZ(int[] verticesZ) {
			this.verticesZ = verticesZ;
		}

		public int[] getTrianglesA() {
			return trianglesA;
		}

		public void setTrianglesA(int[] trianglesA) {
			this.trianglesA = trianglesA;
		}

		public int[] getTrianglesB() {
			return trianglesB;
		}

		public void setTrianglesB(int[] trianglesB) {
			this.trianglesB = trianglesB;
		}

		public int[] getTrianglesC() {
			return trianglesC;
		}

		public void setTrianglesC(int[] trianglesC) {
			this.trianglesC = trianglesC;
		}
		
		public int getOrientation() {
			return orientation;
		}

	}

}
