/**
 * 
 */
package aors.module.physics.util;

/**
 * A collection of utility functions.
 * 
 * @author Holger Wuerke
 *
 */
public class UtilFunctions {
  
  /**
   * Converts radian values to degree values.
   * 
   * @param radian the value in radians
   * @return the value in degrees
   */
  public static double radianToDegree(double radian) {
    return (radian * 180 / Math.PI);
  }

  /**
   * Converts degree values to radian values.
   * 
   * @param degree the value in degrees
   * @return the value in radians
   */
  public static double degreeToRadian(double degree) {
    return (degree * Math.PI / 180);
  }
  
}
