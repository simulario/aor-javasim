package aors.module.physics.collision;

import aors.model.envsim.Physical;

/**
 * An IntervalBound represents a point that is either the start or the end
 * of an interval.
 * 
 * @author Holger Wuerke
 * 
 */
class IntervalBound implements Comparable<IntervalBound> {

  public enum BoundType {
    START, END
  };

  /**
   * Defines if it is the start or end point of the interval.
   */
  private BoundType type;

  /**
   * The point coordinate.
   */
  private Double point;

  /**
   * The object which belongs to the bound (null if collisionObjectType is
   * BORDER).
   */
  private Physical object;

  /**
   * The type of the collision object.
   */
  private CollisionObjectType collisionObjectType;

  /**
   * The corresponding bound (for a start point -> the corresponding end
   * point and vice versa).
   */
  private IntervalBound other;

  /**
   * @return the type
   */
  public BoundType getType() {
    return type;
  }

  /**
   * @param type
   *          the type to set
   */
  public void setType(BoundType type) {
    this.type = type;
  }

  /**
   * @return the point
   */
  public double getPoint() {
    return point;
  }

  /**
   * @param point
   *          the point to set
   */
  public void setPoint(double point) {
    this.point = point;
  }

  /**
   * @param object
   *          the object to set
   */
  public void setObject(Physical object) {
    this.object = object;
  }

  /**
   * @return the object
   */
  public Physical getObject() {
    return object;
  }

  /**
   * @param collisionObjectType
   *          the collisionObjectType to set
   */
  public void setCollisionObjectType(CollisionObjectType collisionObjectType) {
    this.collisionObjectType = collisionObjectType;
  }

  /**
   * @return the collisionObjectType
   */
  public CollisionObjectType getCollisionObjectType() {
    return collisionObjectType;
  }

  /**
   * @param other
   *          the other to set
   */
  public void setOther(IntervalBound other) {
    this.other = other;
  }

  /**
   * @return the other
   */
  public IntervalBound getOther() {
    return other;
  }

  @Override
  public int compareTo(IntervalBound ie) {
    return point.compareTo(ie.getPoint());
  }

  @Override
  public String toString() {
    String str = (type.equals(BoundType.START)) ? "S: " + point : "E: "
        + point;

    if (collisionObjectType.equals(CollisionObjectType.BORDER)) {
      str += "B";
    }

    if (collisionObjectType.equals(CollisionObjectType.PERCEPTION)) {
      str += "P";
    }

    return str;
  }
}