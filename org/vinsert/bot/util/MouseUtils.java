package org.vinsert.bot.util;

import java.awt.Point;

/**
 * Mouse utilities
 * @author BenLand100
 *
 */
public class MouseUtils {
	
	/**
	   * The default mouse speed. This is the delay in ms between actual mouse
	   * moves. Lower is faster.
	   */
	  public static final int DEFAULT_MOUSE_SPEED = 10;

	  /**
	   * The maximum distance (in pixels) to move the mouse after clicks in either
	   * direction on both axes.
	   */
	  public static final int DEFAULT_MAX_MOVE_AFTER = 0;

	  /**
	   * The amount of time (in ms) it takes per bit of difficulty (look up Fitts
	   * Law) to move the mouse. This appears to partially control the speed of
	   * mouse movement.
	   */
	  public static final int msPerBit = 105;

	  /**
	   * The amount of time (in ms) it takes an average mouse user to realise the
	   * mouse needs to be moved
	   */
	  public static final int reactionTime = 0;

	  private static java.util.Random staticRandom = new java.util.Random();
	  
	 /**
	   * Applies a midpoint algorithm to the Vector of points to ensure pixel to
	   * pixel movement
	   *
	   * @param points The vector of points to be manipulated
	   */
	  private static void adaptiveMidpoints(final java.util.Vector<Point> points) {
	    int i = 0;
	    while (i < points.size() - 1) {
	      final Point a = points.get(i++);
	      final Point b = points.get(i);
	      if ((Math.abs(a.x - b.x) > 1) || (Math.abs(a.y - b.y) > 1)) {
	        if (Math.abs(a.x - b.x) != 0) {
	          final double slope = (double) (a.y - b.y) / (double) (a.x - b.x);
	          final double incpt = a.y - slope * a.x;
	          for (int c = a.x < b.x ? a.x + 1 : b.x - 1; a.x < b.x ? c < b.x : c > a.x; c += a.x < b.x ? 1 : -1) {
	            points.add(i++, new Point(c, (int) Math.round(incpt + slope * c)));
	          }
	        } else {
	          for (int c = a.y < b.y ? a.y + 1 : b.y - 1; a.y < b.y ? c < b.y : c > a.y; c += a.y < b.y ? 1 : -1) {
	            points.add(i++, new Point(a.x, c));
	          }
	        }
	      }
	    }
	  }

	  /**
	   * Omits points along the spline in order to move in steps rather then pixel
	   * by pixel
	   *
	   * @param spline  The pixel by pixel spline
	   * @param msForMove The ammount of time taken to traverse the spline. should be a
	   *                  value from {@link #fittsLaw}
	   * @param msPerMove The ammount of time per each move
	   * @return The stepped spline
	   */
	  public static Point[] applyDynamism(final Point[] spline, final int msForMove, final int msPerMove) {
	    final int numPoints = spline.length;
	    final double msPerPoint = (double) msForMove / (double) numPoints;
	    final double undistStep = msPerMove / msPerPoint;
	    final int steps = (int) Math.floor(numPoints / undistStep);
	    final Point[] result = new Point[steps];
	    final double[] gaussValues = gaussTable(result.length);
	    double currentPercent = 0;
	    for (int i = 0; i < steps; i++) {
	      currentPercent += gaussValues[i];
	      final int nextIndex = (int) Math.floor(numPoints * currentPercent);
	      if (nextIndex < numPoints) {
	        result[i] = spline[nextIndex];
	      } else {
	        result[i] = spline[numPoints - 1];
	      }
	    }
	    if (currentPercent < 1D) {
	      result[steps - 1] = spline[numPoints - 1];
	    }
	    return result;
	  }

	  /**
	   * Binomial Coefficient.
	   *
	   * @param n The superset element count.
	   * @param k The subset size.
	   * @return <code>n</code> choose <code>k</code>.
	   */
	  private static double nCk(final int n, final int k) {
	    return fact(n) / (fact(k) * fact(n - k));
	  }

	  /**
	   * Factorial ("n!").
	   *
	   * @param n The integer.
	   * @return The factorial.
	   */
	  private static double fact(final int n) {
	    double result = 1;
	    for (int i = 1; i <= n; i++) {
	      result *= i;
	    }
	    return result;
	  }

	  /**
	   * Calculates the ammount of time a movement should Mousetake based on
	   * Fitts' Law TIP: Do not add/subtract random values from this result,
	   * rather varry the targetSize value or do not move the same distance each
	   * time ;)
	   *
	   * @param targetDist The distance from the current position to the center of the
	   *                   target
	   * @param targetSize The maximum distence from the center of the target within
	   *                   which the end point could be
	   * @return the ammount of time (in ms) the movement should take
	   */
	  public static long fittsLaw(final double targetDist, final double targetSize) {
	    return (long) (reactionTime + msPerBit * Math.log10(targetDist / targetSize + 1) / Math.log10(2));
	  }

	  /**
	   * Satisfies Integral[gaussian(t),t,0,1] == 1D Therefore can distribute a
	   * value as a bell curve over the intervel 0 to 1
	   *
	   * @param t = A value, 0 to 1, representing a percent along the curve
	   * @return The value of the gaussian curve at this position
	   */
	  private static double gaussian(double t) {
	    t = 10D * t - 5D;
	    return 1D / (Math.sqrt(5D) * Math.sqrt(2D * Math.PI)) * Math.exp(-t * t / 20D);
	  }

	  /**
	   * Returns an array of gaussian values that add up to 1 for the number of
	   * steps Solves the problem of having using an intergral to distribute
	   * values
	   *
	   * @param steps Number of steps in the distribution
	   * @return An array of values that contains the percents of the distribution
	   */
	  private static double[] gaussTable(final int steps) {
	    final double[] table = new double[steps];
	    final double step = 1D / steps;
	    double sum = 0;
	    for (int i = 0; i < steps; i++) {
	      sum += gaussian(i * step);
	    }
	    for (int i = 0; i < steps; i++) {
	      table[i] = gaussian(i * step) / sum;
	    }
	    return table;
	  }

	  /**
	   * Creates random control points for a spline. Written by Benland100
	   *
	   * @param sx       Begining X position
	   * @param sy       Begining Y position
	   * @param ex       Begining X position
	   * @param ey       Begining Y position
	   * @param ctrlSpacing  Distance between control origins
	   * @param ctrlVariance Max X or Y variance of each control point from its origin
	   * @return An array of Points that represents the control points of the
	   *         spline
	   */
	  public static Point[] generateControls(final int sx, final int sy, final int ex, final int ey, int ctrlSpacing, int ctrlVariance) {
	    final double dist = Math.sqrt((sx - ex) * (sx - ex) + (sy - ey) * (sy - ey));
	    final double angle = Math.atan2(ey - sy, ex - sx);
	    int ctrlPoints = (int) Math.floor(dist / ctrlSpacing);
	    ctrlPoints = ctrlPoints * ctrlSpacing == dist ? ctrlPoints - 1 : ctrlPoints;
	    if (ctrlPoints <= 1) {
	      ctrlPoints = 2;
	      ctrlSpacing = (int) dist / 3;
	      ctrlVariance = (int) dist / 2;
	    }
	    final Point[] result = new Point[ctrlPoints + 2];
	    result[0] = new Point(sx, sy);
	    for (int i = 1; i < ctrlPoints + 1; i++) {
	      final double radius = ctrlSpacing * i;
	      final Point cur = new Point((int) (sx + radius * Math.cos(angle)), (int) (sy + radius * Math.sin(angle)));
	      double percent = 1D - (double) (i - 1) / (double) ctrlPoints;
	      percent = percent > 0.5 ? percent - 0.5 : percent;
	      percent += 0.25;
	      final int curVariance = (int) (ctrlVariance * percent);
	      /**
	       * Hopefully {@link java.util.Random} is thread safe. (it is in Sun
	       * JVM 1.5+)
	       */
	      cur.x = (int) (cur.x + curVariance * 2 * staticRandom.nextDouble() - curVariance);
	      cur.y = (int) (cur.y + curVariance * 2 * staticRandom.nextDouble() - curVariance);
	      result[i] = cur;
	    }
	    result[ctrlPoints + 1] = new Point(ex, ey);
	    return result;
	  }

	  /**
	   * Generates a spline that moves no more then one pixel at a time TIP: For
	   * most movements, this spline is not good, use <code>applyDynamism</code>
	   *
	   * @param controls An array of control points
	   * @return An array of Points that represents the spline
	   */
	  public static Point[] generateSpline(final Point[] controls) {
	    final double degree = controls.length - 1;
	    final java.util.Vector<Point> spline = new java.util.Vector<Point>();
	    boolean lastFlag = false;
	    for (double theta = 0; theta <= 1; theta += 0.01) {
	      double x = 0;
	      double y = 0;
	      for (double index = 0; index <= degree; index++) {
	        final double probPoly = nCk((int) degree, (int) index) * Math.pow(theta, index) * Math.pow(1D - theta, degree - index);
	        x += probPoly * controls[(int) index].x;
	        y += probPoly * controls[(int) index].y;
	      }
	      final Point temp = new Point((int) x, (int) y);
	      try {
	        if (!temp.equals(spline.lastElement())) {
	          spline.add(temp);
	        }
	      } catch (final Exception e) {
	        spline.add(temp);
	      }
	      lastFlag = theta != 1.0;
	    }
	    if (lastFlag) {
	      spline.add(new Point(controls[(int) degree].x, controls[(int) degree].y));
	    }
	    adaptiveMidpoints(spline);
	    return spline.toArray(new Point[spline.size()]);
	  }

}
