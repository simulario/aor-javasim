/**
 * 
 */
package aors.module.physics2d.util;

import aors.GeneralSpaceModel;
import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalAgentObject;

/**
 * A perception in 2D grid space.
 * 
 * @author Holger Wuerke
 *
 */
public class Perception2DGrid extends Perception {

  /**
   * The distance in x direction.
   */
  private Double distanceX;

  /**
   * The distance in y direction
   */
  private Double distanceY;
  
  /**
   * @param perceiver
   * @param perceived
   * @param spaceModel
   */
  public Perception2DGrid(PhysicalAgentObject perceiver, Physical perceived,
      GeneralSpaceModel spaceModel) {
    super(perceiver, perceived, spaceModel);
  }

  /**
   * Calculates the perception distance for the 2D grid space. We use the
   * Chebyshev distance (chessboard distance) in grid space.
   */
  @Override
  protected void calculateDistance() {
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
    
    distance = Math.max(Math.abs(distanceX), Math.abs(distanceY));    
  }
  
  @Override
  protected void calculateAngle() {
    if (distanceX == null || distanceY == null) {
      calculateDistance();
    }

    double orientation = perceiver.getRotZ();

    angle = Math.atan2(distanceY, distanceX);
    angle = (angle < 0) ? angle + 2 * Math.PI : angle;
    angle = (angle < orientation) ? (2 * Math.PI - (orientation - angle))
        : angle - orientation;
  }
  
}
