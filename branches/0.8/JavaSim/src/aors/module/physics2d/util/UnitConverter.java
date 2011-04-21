/**
 * 
 */
package aors.module.physics2d.util;

import aors.GeneralSpaceModel.SpatialDistanceUnit;

/**
 * A class for unit conversion.
 * 
 * @author Holger Wuerke
 * @since 12.04.2010
 */
public class UnitConverter {

  /**
   * The factor for time conversion.
   */
  private double timeFactor = 1;

  /**
   * The factor for distance conversion.
   */
  private double distanceFactor = 1;

  /**
   * Create a new unit converter.
   * 
   * @param timeUnit
   * @param distanceUnit
   */
  public UnitConverter(String timeUnit, SpatialDistanceUnit distanceUnit) {
    if (timeUnit.equals("ms")) {
      timeFactor = (double) 1 / 1000;
    }

    if (timeUnit.equals("s")) {
      timeFactor = 1;
    }

    if (timeUnit.equals("min")) {
      timeFactor = 60;
    }

    if (timeUnit.equals("h")) {
      timeFactor = 3600;
    }

    if (timeUnit.equals("D")) {
      timeFactor = 3600 * 24;
    }

    if (timeUnit.equals("W")) {
      timeFactor = 3600 * 24 * 7;
    }

    if (timeUnit.equals("M")) {
      timeFactor = 3600 * 24 * 30;
    }

    if (timeUnit.equals("Y")) {
      timeFactor = 3600 * 24 * 365;
    }

    switch (distanceUnit) {
    case mm:
      distanceFactor = (double) 1 / 1000;
      break;
    case cm:
      distanceFactor = (double) 1 / 100;
      break;
    case m:
      distanceFactor = 1;
      break;
    case km:
      distanceFactor = 1000;
      break;
    default:
      distanceFactor = 1;
    }

    // System.out.println("time: " + timeFactor + " distance: " +
    // distanceFactor);
  }

  /**
   * Converts a time value from seconds to the user defined unit.
   * 
   * @param value
   * @return the converted value
   */
  public double timeToUser(double value) {
    return value / timeFactor;
  }

  /**
   * Converts a time value from the user defined unit to seconds.
   * 
   * @param value
   * @return the converted value
   */
  public double timeToSeconds(double value) {
    return value * timeFactor;
  }

  /**
   * Converts a distance value from meters to the user defined unit.
   * 
   * @param value
   * @return the converted value
   */
  public double distanceToUser(double value) {
    return value / distanceFactor;
  }

  /**
   * Converts a distance value from the user defined unit to meters.
   * 
   * @param value
   * @return the converted value
   */
  public double distanceToMeters(double value) {
    return value * distanceFactor;
  }

  /**
   * Converts a velocity value from meters/seconds to the user defined unit.
   * 
   * @param value
   * @return the converted value
   */
  public double velocityToUser(double value) {
    return value * timeFactor / distanceFactor;
  }

  /**
   * Converts a velocity value from the user defined unit to meters/seconds.
   * 
   * @param value
   * @return the converted value
   */
  public double velocityToMetersPerSeconds(double value) {
    return value * distanceFactor / timeFactor;
  }

  /**
   * Converts an acceleration value from meters/seconds² to the user defined
   * unit.
   * 
   * @param value
   * @return the converted value
   */
  public double accelerationToUser(double value) {
    return value * timeFactor * timeFactor / distanceFactor;
  }

  /**
   * Converts an acceleration value from the user defined unit to
   * meters/seconds².
   * 
   * @param value
   * @return the converted value
   */
  public double accelerationToMetersPerSecondsSquared(double value) {
    return value * distanceFactor / (timeFactor * timeFactor);
  }

  /**
   * Converts an angular velocity value from radians/seconds to the user defined
   * unit.
   * 
   * @param value
   * @return the converted value
   */
  public double angularVelocityToUser(double value) {
    return value * timeFactor;
  }

  /**
   * Converts an angular velocity value from the user defined unit to
   * radians/seconds.
   * 
   * @param value
   * @return the converted value
   */
  public double angularVelocityToRadiansPerSeconds(double value) {
    return value / timeFactor;
  }

  /**
   * Converts an angular acceleration value from radians/seconds² to the user
   * defined unit.
   * 
   * @param value
   * @return the converted value
   */
  public double angularAccelerationToUser(double value) {
    return value * timeFactor * timeFactor;
  }

  /**
   * Converts an angular acceleration value from the user defined unit to
   * radians/seconds².
   * 
   * @param value
   * @return the converted value
   */
  public double angularAccelerationToRadiansPerSecondsSquared(double value) {
    return value / (timeFactor * timeFactor);
  }

}
