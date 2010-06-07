/**
 * 
 */
package aors.module.physics2d.util;

import aors.GeneralSpaceModel;
import aors.GeneralSpaceModel.SpaceType;
import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalAgentObject;

/**
 * Represents a perception.
 * 
 * @author Holger Wuerke
 * @since 04.03.2010
 */
public class Perception {

  /**
   * The perceiver.
   */
  private PhysicalAgentObject perceiver;

  /**
   * The perceived object.
   */
  private Physical perceived;

  /**
   * The spaceModel.
   */
  private GeneralSpaceModel spaceModel;

  /**
   * The distance in x direction.
   */
  private Double distanceX;

  /**
   * The distance in y direction
   */
  private Double distanceY;

  /**
   * Create a new perception.
   * 
   * @param perceiver
   * @param perceived
   */
  public Perception(PhysicalAgentObject perceiver, Physical perceived,
      GeneralSpaceModel spaceModel) {
    this.perceiver = perceiver;
    this.perceived = perceived;
    this.spaceModel = spaceModel;
  }

  /**
   * Calculates the perception distance for the 1D space.
   * 
   * @return the distance
   */
  public double getDistance1D() {
    double distance = perceived.getX() - perceiver.getX();
    distance += (distance > 0) ? -perceived.getWidth() / 2 : perceived
        .getWidth() / 2;

    // special cases if perception radius goes beyond space border (for toroidal
    // space)
    if (distance > perceiver.getPerceptionRadius()) {
      distance = perceived.getX() - spaceModel.getXMax() - perceiver.getX();
      distance += (distance > 0) ? -perceived.getWidth() / 2 : perceived
          .getWidth() / 2;
    }

    if (distance < -perceiver.getPerceptionRadius()) {
      distance = perceived.getX() + spaceModel.getXMax() - perceiver.getX();
      distance += (distance > 0) ? -perceived.getWidth() / 2 : perceived
          .getWidth() / 2;
    }

    return Math.abs(distance);
  }

  /**
   * Calculates the perception distance for the 2D grid space. We use the
   * Chebyshev distance (chessboard distance) in grid space.
   * 
   * @return the distance
   */
  public double getDistance2DGrid() {
    if (distanceX == null || distanceY == null) {
      calculateDistance();
    }

    return Math.max(Math.abs(distanceX), Math.abs(distanceY));
  }

  /**
   * Calculates the perception angle for the 1D space.
   * 
   * @return the angle
   */
  public double getAngle1D() {
    double distance = perceived.getX() - perceiver.getX();
    distance += (distance > 0) ? -perceived.getWidth() / 2 : perceived
        .getWidth() / 2;

    if (distance > 0) {
      return (distance <= perceiver.getPerceptionRadius()) ? 0 : Math.PI;
    } else {
      return (-distance <= perceiver.getPerceptionRadius()) ? Math.PI : 0;
    }
  }

  /**
   * Calculates the perception angle for the 2D space.
   * 
   * @return the angle
   */
  public double getAngle2D() {
    if (distanceX == null || distanceY == null) {
      calculateDistance();
    }

    double orientation = perceiver.getRotationAngleZ();

    double angle = Math.atan2(distanceY, distanceX);
    angle = (angle < 0) ? angle + 2 * Math.PI : angle;
    angle = (angle < orientation) ? (2 * Math.PI - (orientation - angle))
        : angle - orientation;

    return angle;
  }

  /**
   * @return the perceiver
   */
  public PhysicalAgentObject getPerceiver() {
    return perceiver;
  }

  /**
   * @return the perceived
   */
  public Physical getPerceived() {
    return perceived;
  }

  private void calculateDistance() {
    distanceX = perceived.getX() - perceiver.getX();
    distanceY = perceived.getY() - perceiver.getY();

    // special cases if perception radius goes beyond space border (for toroidal
    // space)
    if (distanceX > perceiver.getPerceptionRadius()) {
      distanceX = perceived.getX() - spaceModel.getXMax() - perceiver.getX();
    }

    if (distanceX < -perceiver.getPerceptionRadius()) {
      distanceX = perceived.getX() + spaceModel.getXMax() - perceiver.getX();
    }

    if (distanceY > perceiver.getPerceptionRadius()) {
      distanceY = perceived.getY() - spaceModel.getYMax() - perceiver.getY();
    }

    if (distanceY < -perceiver.getPerceptionRadius()) {
      distanceY = perceived.getY() + spaceModel.getYMax() - perceiver.getY();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((perceived == null) ? 0 : perceived.hashCode());
    result = prime * result + ((perceiver == null) ? 0 : perceiver.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Perception other = (Perception) obj;
    if (perceived == null) {
      if (other.perceived != null)
        return false;
    } else if (!perceived.equals(other.perceived))
      return false;
    if (perceiver == null) {
      if (other.perceiver != null)
        return false;
    } else if (!perceiver.equals(other.perceiver))
      return false;
    return true;
  }

  public String toString() {
    if (spaceModel.getSpaceType().equals(SpaceType.OneD)) {
      return "Perception: " + perceiver + " -> " + perceived + "\nDist: "
          + getDistance1D() + " Angle: " + (getAngle1D() * 180 / Math.PI)
          + "°";
    }

    return "Perception: " + perceiver + " -> " + perceived + "\nDist: "
        + getDistance2DGrid() + " Angle2D: " + (getAngle2D() * 180 / Math.PI)
        + "°";
  }
}
