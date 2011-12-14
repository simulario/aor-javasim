/**
 * 
 */
package aors.module.physics.util;

import aors.GeneralSpaceModel.SpatialDistanceUnit;

/**
 * A class for unit conversion.
 * 
 * @author Holger Wuerke
 */
public class UnitConverter {

  /**
   * The ratio for time conversion.
   */
  private double timeRatio = 1;

  /**
   * The ratio for distance conversion.
   */
  private double distanceRatio = 1;

  /**
   * Create a new unit converter.
   * 
   * @param timeUnit
   * @param distanceUnit
   */
  public UnitConverter(String timeUnit, SpatialDistanceUnit distanceUnit) {
    if (timeUnit.equals("ms")) {
      timeRatio = (double) 1 / 1000;
    }

    if (timeUnit.equals("s")) {
      timeRatio = 1;
    }

    if (timeUnit.equals("min")) {
      timeRatio = 60;
    }

    if (timeUnit.equals("h")) {
      timeRatio = 3600;
    }

    if (timeUnit.equals("D")) {
      timeRatio = 3600 * 24;
    }

    if (timeUnit.equals("W")) {
      timeRatio = 3600 * 24 * 7;
    }

    if (timeUnit.equals("M")) {
      timeRatio = 3600 * 24 * 30;
    }

    if (timeUnit.equals("Y")) {
      timeRatio = 3600 * 24 * 365;
    }

    switch (distanceUnit) {
    case mm:
      distanceRatio = (double) 1 / 1000;
      break;
    case cm:
      distanceRatio = (double) 1 / 100;
      break;
    case m:
      distanceRatio = 1;
      break;
    case km:
      distanceRatio = 1000;
      break;
    default:
      distanceRatio = 1;
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
    return value / timeRatio;
  }

  /**
   * Converts a time value from the user defined unit to seconds.
   * 
   * @param value
   * @return the converted value
   */
  public double timeToSeconds(double value) {
    return value * timeRatio;
  }

  /**
   * Converts a distance value from meters to the user defined unit.
   * 
   * @param value
   * @return the converted value
   */
  public double distanceToUser(double value) {
    return value / distanceRatio;
  }

  /**
   * Converts a distance value from the user defined unit to meters.
   * 
   * @param value
   * @return the converted value
   */
  public double distanceToMeters(double value) {
    return value * distanceRatio;
  }

  /**
   * Converts a velocity value from meters/seconds to the user defined unit.
   * 
   * @param value
   * @return the converted value
   */
  public double velocityToUser(double value) {
    return value * timeRatio / distanceRatio;
  }

  /**
   * Converts a velocity value from the user defined unit to meters/seconds.
   * 
   * @param value
   * @return the converted value
   */
  public double velocityToMetersPerSeconds(double value) {
    return value * distanceRatio / timeRatio;
  }

  /**
   * Converts an acceleration value from meters/seconds² to the user defined
   * unit.
   * 
   * @param value
   * @return the converted value
   */
  public double accelerationToUser(double value) {
    return value * timeRatio * timeRatio / distanceRatio;
  }

  /**
   * Converts an acceleration value from the user defined unit to
   * meters/seconds².
   * 
   * @param value
   * @return the converted value
   */
  public double accelerationToMetersPerSecondsSquared(double value) {
    return value * distanceRatio / (timeRatio * timeRatio);
  }

  /**
   * Converts an angular velocity value from radians/seconds to the user defined
   * unit.
   * 
   * @param value
   * @return the converted value
   */
  public double angularVelocityToUser(double value) {
    return value * timeRatio;
  }

  /**
   * Converts an angular velocity value from the user defined unit to
   * radians/seconds.
   * 
   * @param value
   * @return the converted value
   */
  public double angularVelocityToRadiansPerSeconds(double value) {
    return value / timeRatio;
  }

  /**
   * Converts an angular acceleration value from radians/seconds² to the user
   * defined unit.
   * 
   * @param value
   * @return the converted value
   */
  public double angularAccelerationToUser(double value) {
    return value * timeRatio * timeRatio;
  }

  /**
   * Converts an angular acceleration value from the user defined unit to
   * radians/seconds².
   * 
   * @param value
   * @return the converted value
   */
  public double angularAccelerationToRadiansPerSecondsSquared(double value) {
    return value / (timeRatio * timeRatio);
  }

}
