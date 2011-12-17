/**
 * 
 */
package aors.module.physics.collision;

import aors.GeneralSpaceModel;
import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalAgentObject;
import aors.module.physics.util.UtilFunctions;

/**
 * A perception in 2D space.
 * 
 * @author Holger Wuerke
 *
 */
public class Perception2D extends Perception {

  /**
   * @param perceiver
   * @param perceived
   * @param spaceModel
   */
  public Perception2D(PhysicalAgentObject perceiver, Physical perceived,
      GeneralSpaceModel spaceModel) {
    super(perceiver, perceived, spaceModel);
  }

  @Override
  protected void calculateDistance() {
    distance = Math.sqrt(Math.pow((perceived.getX() - perceiver.getX()), 2)
        + Math.pow((perceived.getY() - perceiver.getY()), 2));
  }

  @Override
  protected void calculateAngle() {
    double globalAngle = Math.atan2(perceived.getY() - perceiver.getY(), perceived.getX() - perceiver.getX());
    
    if (globalAngle < 0) {
      globalAngle += 2 * Math.PI;
    }

    double orientation = UtilFunctions.degreeToRadian(perceiver.getRotZ());
    double perceptionDirection = (Math.atan2(perceiver.getPerceptionDirection().getY(), perceiver.getPerceptionDirection().getX()) + orientation) % (2 * Math.PI);
    angle = globalAngle - perceptionDirection;
    
    if (angle > (perceiver.getPerceptionAngle() / 2)) {
      angle -= 2 * Math.PI;
    }
    
    if (angle < -(perceiver.getPerceptionAngle() / 2)) {
      angle += 2 * Math.PI;
    }
  }


  
}
