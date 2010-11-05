/**
 * 
 */
package aors.space;

import aors.util.Random;

/**
 * @author Jens Werner
 * 
 */
public class OneDimensional extends NonDiscreteSpace {

  private double xSize = 0;

  public OneDimensional(double xSize, boolean autoKinematics,
      boolean autoCollisionHandling, boolean autoCollisionDetection,
      boolean gravitation) {
    super(autoKinematics, autoCollisionHandling, autoCollisionDetection,
        gravitation);
    this.xSize = xSize;
  }

  @Override
  public NonDiscretePositionData getRandomPosition() {
    return new OneDimNonDiscretePositionData(Random.uniform(0, xSize));
  }

  /**
   * 
   * @author Jens Werner
   * 
   */
  public class OneDimNonDiscretePositionData extends NonDiscretePositionData {

    /**
     * 
     */
    public OneDimNonDiscretePositionData(double x) {
      super(x, 0, 0);
    }

    @Override
    public double getX() {
      return this.x;
    }

    @Override
    public double getY() {
      return 0;
    }

    @Override
    public double getZ() {
      return 0;
    }
  }

}
