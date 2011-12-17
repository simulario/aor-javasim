/**
 * 
 */
package aors.module.physics.collision;

import aors.module.physics.util.UtilFunctions;
import aors.GeneralSpaceModel;
import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalAgentObject;

/**
 * Represents a general perception.
 * 
 * @author Holger Wuerke
 */
public abstract class Perception {

  /**
   * The perceiver.
   */
  protected PhysicalAgentObject perceiver;

  /**
   * The perceived object.
   */
  protected Physical perceived;

  /**
   * The spaceModel.
   */
  protected GeneralSpaceModel spaceModel;
  
  /**
   * The perception distance.
   */
  protected Double distance;

  /**
   * The perception angle.
   */
  protected Double angle;

  
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


  /**
   * @return the distance
   */
  public Double getDistance() {
    if (distance == null) {
      calculateDistance();
    }
    
    return distance;
  }

  /**
   * @param distance the distance to set
   */
  public void setDistance(Double distance) {
    this.distance = distance;
  }

  /**
   * @return the angle
   */
  public Double getAngle() {
    if (angle == null) {
      calculateAngle();
    }
    
    return angle;
  }

  /**
   * @param angle the angle to set
   */
  public void setAngle(Double angle) {
    this.angle = angle;
  }

  /**
   * Calculates the perception distance.
   */
  protected abstract void calculateDistance();

  /**
   * Calculates the perception angle.
   */
  protected abstract void calculateAngle();
  
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
    return "Perception: " + perceiver + " -> " + perceived + "\n\tDistance: "
        + String.format("%.2f", getDistance()) + " Angle: " + String.format("%.2f", UtilFunctions.radianToDegree(getAngle()))
        + "°";
  }
}
