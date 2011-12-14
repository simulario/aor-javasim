/**
 * 
 */
package aors.module.physics.collision;

import aors.model.envsim.Physical;

/**
 * Represents a collision between two 1D objects. Object1 is the object that is
 * on the left side of the collision (usually the one with the smaller
 * coordinate, except for some cases in toroidal space). The class implements
 * the Comparable interface in order to sort a number of collisions by the time
 * they occur.
 * 
 * @author Holger Wuerke
 * 
 */
public class Collision1D implements Comparable<Collision1D> {

  private Physical object1;
  private Physical object2;

  /**
   * The time of the impact, in seconds, measured from the start of the current
   * step.
   */
  private Double time;

  /**
   * The position of the impact, in the user defined unit.
   */
  private double position;

  /**
   * Create a new collision.
   * 
   * @param object1
   * @param object2
   * @param spaceModel
   */
  public Collision1D(Physical object1, Physical object2) {
    // collision between resting objects
    if (object1.getVx() == 0 && object2.getVx() == 0) {
      // determine left object -> object1
      if (object1.getX() < object2.getX()) {
        this.object1 = object1;
        this.object2 = object2;
      } else {
        this.object1 = object2;
        this.object2 = object1;
      }

      return;
    }

    // moving objects, determine left object -> object1
    if ((object1.getVx() - object2.getVx()) > 0) {
      this.object1 = object1;
      this.object2 = object2;
    } else {
      this.object1 = object2;
      this.object2 = object1;
    }
  }

  /**
   * @return the object1
   */
  public Physical getObject1() {
    return object1;
  }

  /**
   * @return the objekt2
   */
  public Physical getObject2() {
    return object2;
  }

  /**
   * @return the time
   */
  public double getTime() {
    return time;
  }

  /**
   * @param time
   */
  public void setTime(double time) {
    this.time = time;
  }

  /**
   * @return the position
   */
  public double getPosition() {
    return position;
  }

  /**
   * @param position
   */
  public void setPosition(double position) {
    this.position = position;
  }

  public boolean equals(Object other) {
    if (other instanceof Collision1D) {
      Collision1D col = (Collision1D) other;

      if (object1 == null && object2 == null) {
        return (col.getObject1() == null && col.getObject2() == null);
      }

      if (object1 == null) {
        return (object2.equals(col.getObject1()) && col.getObject2() == null)
            || (object2.equals(col.getObject2()) && col.getObject1() == null);
      }

      if (object2 == null) {
        return (object1.equals(col.getObject1()) && col.getObject2() == null)
            || (object1.equals(col.getObject2()) && col.getObject1() == null);
      }

      return (object1.equals(col.getObject1()) && object2.equals(col
          .getObject2()))
          || (object1.equals(col.getObject2()) && object2.equals(col
              .getObject1()));
    }

    return false;
  }

  public String toString() {
    return "Collision: " + object1 + " | " + object2;
  }

  @Override
  public int compareTo(Collision1D other) {
    return time.compareTo(other.time);
  }

}
