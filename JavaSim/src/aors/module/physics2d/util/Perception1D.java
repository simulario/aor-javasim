/**
 * 
 */
package aors.module.physics2d.util;

import aors.GeneralSpaceModel;
import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalAgentObject;

/**
 * A perception in 1D space.
 * 
 * @author Holger Wuerke
 *
 */
public class Perception1D extends Perception {

  /**
   * @param perceiver
   * @param perceived
   * @param spaceModel
   */
  public Perception1D(PhysicalAgentObject perceiver, Physical perceived,
      GeneralSpaceModel spaceModel) {
    super(perceiver, perceived, spaceModel);
  }

  
  @Override
  protected void calculateDistance() {
    distance = perceived.getX() - perceiver.getX();
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

    distance = Math.abs(distance);
  }
  
  @Override
  protected void calculateAngle() {
    // different lanes
    if (perceiver.getY() > perceived.getY()) {
      angle = Math.PI / 2;
    }

    if (perceiver.getY() < perceived.getY()) {
      angle = 3 * Math.PI / 2;
    }

    // same lane
    double distance = perceived.getX() - perceiver.getX();
    distance += (distance > 0) ? -perceived.getWidth() / 2 : perceived
        .getWidth() / 2;

    if (distance > 0) {
      angle = (distance <= perceiver.getPerceptionRadius()) ? 0 : Math.PI;
    } else {
      angle = (-distance <= perceiver.getPerceptionRadius()) ? Math.PI : 0;
    }    
  }
  
}
