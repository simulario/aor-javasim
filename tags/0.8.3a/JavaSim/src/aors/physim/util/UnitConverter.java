package aors.physim.util;

import aors.GeneralSpaceModel;

/**
 * Class for converting final (const) values (like gravity) to the unit chosen
 * by user. The converter converts from meter and second to the user defined
 * units.
 * 
 * @author Stefan Boecker
 * 
 */
public class UnitConverter {

  /**
   * Factor for time unit.
   */
  private double factorTime;

  /**
   * Factor for distance unit.
   */
  private double factorDistance;

  /**
   * Creates a new UnitConverter. This UnitConverter will convert from seconds
   * to given timeUnit and from meters to given distanceUnit.
   * 
   * @param timeUnit
   *          unit of time
   * @param distanceUnit
   *          unit of distance
   */
  public UnitConverter(String timeUnit,
      GeneralSpaceModel.SpatialDistanceUnit distanceUnit) {
    determineFactorTime(timeUnit);
    determineFactorDistance(distanceUnit);
  }

  /**
   * Convert given value from seconds (s) to timeUnit
   * 
   * @param value
   *          Value to convert
   * @return converted value
   */
  public double convertTimeUnit(double value) {
    return value * factorTime;
  }

  public double convertTimeUnitToSeconds(double value) {
    return value / factorTime;
  }

  /**
   * Convert given value from meters (m) to distanceUnit
   * 
   * @param value
   *          Value to convert
   * @return converted value
   */
  public double convertDistanceUnit(double value) {
    return value * factorDistance;
  }

  public double convertDistanceUnitToMeter(double value) {
    return value / factorDistance;
  }

  /**
   * Convert given value to timeUnit/distanceUnit
   * 
   * @param value
   *          Value to convert
   * @return converted value
   */
  public double convertDistancePerTime(double value) {
    return value * factorDistance / factorTime;
  }

  /**
   * Determine factor for given distanceUnit
   * 
   * @param distanceUnit
   */
  private void determineFactorDistance(
      GeneralSpaceModel.SpatialDistanceUnit distanceUnit) {

    if (distanceUnit == GeneralSpaceModel.SpatialDistanceUnit.mm) {
      factorDistance = 1000;
      return;
    }

    if (distanceUnit == GeneralSpaceModel.SpatialDistanceUnit.cm) {
      factorDistance = 100;
      return;
    }

    if (distanceUnit == GeneralSpaceModel.SpatialDistanceUnit.m) {
      factorDistance = 1;
      return;
    }

    if (distanceUnit == GeneralSpaceModel.SpatialDistanceUnit.km) {
      factorDistance = (double) 1 / (double) 1000;
      return;
    }
  }

  /**
   * Determine factor for given timeUnit
   * 
   * @param timeUnit
   */
  private void determineFactorTime(String timeUnit) {
    if (timeUnit.equalsIgnoreCase("ms")) {
      factorTime = 1000;
      return;
    }

    if (timeUnit.equalsIgnoreCase("s")) {
      factorTime = 1;
      return;
    }

    if (timeUnit.equalsIgnoreCase("min")) {
      factorTime = (double) 1 / (double) 60;
      return;
    }

    if (timeUnit.equalsIgnoreCase("h")) {
      factorTime = (double) 1 / (double) (60 * 60);
      return;
    }

    if (timeUnit.equalsIgnoreCase("D")) {
      factorTime = (double) 1 / (double) (60 * 60 * 24);
      return;
    }

    if (timeUnit.equalsIgnoreCase("W")) {
      factorTime = (double) 1 / (double) (60 * 60 * 24 * 7);
      return;
    }

    if (timeUnit.equalsIgnoreCase("M")) {
      factorTime = (double) 1 / (double) (60 * 60 * 24 * 30);
      return;
    }

    if (timeUnit.equalsIgnoreCase("Y")) {
      factorTime = (double) 1 / (double) (60 * 60 * 24 * 365);
      return;
    }
  }
}
