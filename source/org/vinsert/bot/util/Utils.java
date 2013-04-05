package org.vinsert.bot.util;

import java.util.Random;
import java.awt.geom.Point2D;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Generic utilities
 * @author tommo
 *
 */
public class Utils {
	
	private static final Random random;

	static {
		random = getMachineSeededRandom();
	}

	/**
	 * Function to calculate the center of mass for a given polygon, according
	 * ot the algorithm defined at
	 * http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/
	 * 
	 * @param polyPoints
	 *            array of points in the polygon
	 * @return point that is the center of mass
	 */
	public static Point2D centerOfMass(Point2D[] polyPoints) {
		double cx = 0, cy = 0;
		double area = area(polyPoints);
		// could change this to Point2D.Float if you want to use less memory
		Point2D res = new Point2D.Double();
		int i, j, n = polyPoints.length;

		double factor = 0;
		for (i = 0; i < n; i++) {
			j = (i + 1) % n;
			factor = (polyPoints[i].getX() * polyPoints[j].getY()
					- polyPoints[j].getX() * polyPoints[i].getY());
			cx += (polyPoints[i].getX() + polyPoints[j].getX()) * factor;
			cy += (polyPoints[i].getY() + polyPoints[j].getY()) * factor;
		}
		area *= 6.0f;
		factor = 1 / area;
		cx *= factor;
		cy *= factor;
		res.setLocation(cx, cy);
		return res;
	}
	
	/**
	 * Function to calculate the area of a polygon, according to the algorithm
	 * defined at http://local.wasp.uwa.edu.au/~pbourke/geometry/polyarea/
	 * 
	 * @param polyPoints
	 *            array of points in the polygon
	 * @return area of the polygon defined by pgPoints
	 */
	public static double area(Point2D[] polyPoints) {
		int i, j, n = polyPoints.length;
		double area = 0;

		for (i = 0; i < n; i++) {
			j = (i + 1) % n;
			area += polyPoints[i].getX() * polyPoints[j].getY();
			area -= polyPoints[j].getX() * polyPoints[i].getY();
		}
		area /= 2.0;
		return (area);
	}

	private static final java.util.Random getMachineSeededRandom() {
		return new java.util.Random();
	}



	/**
	 * Generates a random number between a min and max.
	 * 
	 * where 0 > max - min
	 * 
	 * @param min The minimum.
	 * @param max The maximum.
	 * @return The random value between the min and max values.
	 */
	public static int random(int min, int max) {
		return random.nextInt(Math.abs(max - min)) + min;
	}

	public static double randomD() {
		return random.nextDouble();
	}
	
	/**
	 * Returns a random element from the given array
	 * @param array
	 * @return
	 */
	public <T> T random(T[] array) {
		return array[random(0, array.length)];
	}
	
	/**
	 * Simulates sleeping for a given time
	 * 
	 * @param time
	 *            The time to sleep for in milliseconds
	 */
	public static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}
	
	/**
	 * Simulates sleeping for a given time
	 * 
	 * @param time
	 *            The time to sleep for in milliseconds
	 */
	public static void sleep(int min, int max) {
		try {
			Thread.sleep(random(min, max));
		} catch (InterruptedException e) {
		}
	}

}
