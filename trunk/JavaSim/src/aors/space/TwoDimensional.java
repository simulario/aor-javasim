/**
 * 
 */
package aors.space;

import aors.util.Random;

/**
 * @author Jens Werner
 * 
 */
public class TwoDimensional extends NonDiscreteSpace {

  private double xSize = 0;
  private double ySize = 0;

  public TwoDimensional(double xSize, double ySize, boolean autoKinematics,
      boolean autoCollisionHandling, boolean autoCollisionDetection,
      boolean gravitation) {
    super(autoKinematics, autoCollisionHandling, autoCollisionDetection,
        gravitation);
    this.xSize = xSize;
    this.ySize = ySize;
  }

  @Override
  public NonDiscretePositionData getRandomPosition() {
    return new TwoDimNonDiscretePositionData(Random.uniform(0, xSize),
        Random.uniform(0, ySize));
  }

  /**
   * 
   * @author Jens Werner
   * 
   */
  public class TwoDimNonDiscretePositionData extends NonDiscretePositionData {

    /**
     * 
     */
    public TwoDimNonDiscretePositionData(double x, double y) {
      super(x, y, 0);
    }

    @Override
    public double getX() {
      return this.x;
    }

    @Override
    public double getY() {
      return this.y;
    }

    @Override
    public double getZ() {
      return 0;
    }
  }

}
