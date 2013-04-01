package org.vinsert.bot.util;

/**
 * Simple 3D vector implementation.
 * All respective methods return the vec3 instance for chaining.
 * The class is mutable, so beware and use copy() when applicable
 * @author tommo
 *
 */
public class Vec3 {
	
	// TODO really should implement some /actual/ vector operations lol
	
	public float x;
	public float y;
	public float z;
	
	public Vec3(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vec3 set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public Vec3 add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
	
	public Vec3 add(Vec3 other) {
		add(other.x, other.y, other.z);
		return this;
	}
	
	public Vec3 subtract(float x, float y, float z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}
	
	public Vec3 subtract(Vec3 other) {
		subtract(other.x, other.y, other.z);
		return this;
	}
	
	public Vec3 multiply(float x, float y, float z) {
		this.x *= x;
		this.y *= y;
		this.z *= z;
		return this;
	}
	
	public Vec3 multiply(Vec3 other) {
		multiply(other.x, other.y, other.z);
		return this;
	}
	
	public Vec3 multiply(float scalar) {
		this.x *= scalar;
		this.y *= scalar;
		this.z *= scalar;
		return this;
	}
	
	/**
	 * Calculates the euclidean distance between the two vectors
	 * @param other The vector to calculate to
	 * @return The euclidean length
	 */
	public double distance(Vec3 other) {
		return Math.sqrt((other.x - x) * (other.x - x)) + ((other.y - y) * (other.y - y)) + ((other.z - z) * (other.z - z));
	}
	
	public Vec3 copy() {
		return new Vec3(x, y, z);
	}
	
	@Override
	public String toString() {
		return "[" + x + ", " + y + ", " + z + "]";
	}

}
