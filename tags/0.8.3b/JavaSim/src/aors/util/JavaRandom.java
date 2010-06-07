/*
 * @(#)Random.java	07/03/02
 *
 * Copyright (C)  2006 - '07  Gerd Wagner, Wolf-Ulrich Raffel, Andreas Post
 *
 * This is a free software program; you can use, redistribute and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have a copy of the GNU General Public License along with R2ML
 * schema; if not go to http://www.gnu.org/copyleft/gpl.html
 * or write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */

package aors.util;

/**
 * A class for enabling some random number functions.
 * <p>
 * All methods are available for all simulation entities and can be used in
 * AORSML.
 * <p>
 * JavaRandom wraps up the Math.random() method that comes with Java
 * 
 * @author Wolf-Ulrich Raffel
 * @author Andreas Post
 */
public class JavaRandom {
  /** a random object */
  private static java.util.Random random = new java.util.Random();

  /**
   * Returns the next value of an exponentially distributed random variable with
   * the given parameter.
   * 
   * @param val
   *          the parameter to be used for creating the next exponentially
   *          distributed random value
   * @return the next exponentially distributed random value with the given
   *         parameter
   */
  public static double exponential(double val) {
    return -val * Math.log(random.nextDouble());
  }

  /**
   * Returns the next value of an uniformally distributed random variable
   * between 0 and 1.
   * <p>
   * Using this method is equal to use <CODE>uniform(0, 1)</CODE>.
   * 
   * @return the next value of an uniformally distributed random variable
   *         between 0 and 1
   */
  public static double uniform() {
    return uniform(0, 1);
  }

  /**
   * Returns the next value of an uniformally distributed random variable
   * between a and b.
   * 
   * @param a
   *          the lowest possible value
   * @param b
   *          the highest possible value
   * @return the next value of an uniformally distributed random variable
   *         between a and b
   */
  public static double uniform(double a, double b) {
    return a + (b - a) * (random.nextDouble());
  }

  /**
   * Returns the next int value of an uniformally distributed random integer
   * between 0(inclusive) and n(inclusive).
   * <p>
   * Using this method is equal to use <CODE>uniformInt(0, val)</CODE>.
   * 
   * @param val
   *          the bound on the random number to be returned. Must be positive.
   * @return the next int value of an uniformally distributed random integer
   *         between 0 and n
   * @see java.util.Random#nextInt(int n)
   */
  public static int uniformInt(int val) {
    if (val + 1 > Integer.MAX_VALUE)
      throw new IllegalArgumentException("n have to be Integer.MAX_VALUE - 1");
    return random.nextInt(val + 1);
  }

  /**
   * Returns the next int value of an uniformally distributed random integer
   * between a(inclusive) and b(inclusive).
   * 
   * @param a
   *          the lowest possible value
   * @param b
   *          the highest possible value
   * @return the next int value of an uniformally distributed random integer
   *         between a and b
   */
  public static int uniformInt(int a, int b) {
    if (b + 1 > Integer.MAX_VALUE)
      throw new IllegalArgumentException("b have to be Integer.MAX_VALUE - 1");
    return a + random.nextInt((b - a + 1));
  }

  /**
   * Returns the next uniformally distributed random boolean value.
   * 
   * @return the next uniformally distributed random boolean value
   * @see java.util.Random#nextBoolean()
   */
  public static boolean uniformBoolean() {
    return random.nextBoolean();
  }

  /**
   * @see java.util.Random#setSeed(long)
   * 
   * @param seed
   */
  public static void setSeed(long seed) {
    JavaRandom.random.setSeed(seed);
  }
}
