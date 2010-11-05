/**
 * 
 */
package aors.space;

import aors.util.Random;

/**
 * @author Jens Werner
 * 
 */
public class ThreeDimensional extends NonDiscreteSpace {

  private double xSize = 0;
  private double ySize = 0;
  private double zSize = 0;

  public ThreeDimensional(double xSize, double ySize, double zSize,
      boolean autoKinematics, boolean autoCollisionHandling,
      boolean autoCollisionDetection, double gravitation) {
    super(autoKinematics, autoCollisionHandling, autoCollisionDetection,
        gravitation);

    this.xSize = xSize;
    this.ySize = ySize;
    this.zSize = zSize;

  }

  @Override
  public NonDiscretePositionData getRandomPosition() {
    return new ThreeDimNonDiscretePositionData(Random.uniform(0, xSize),
        Random.uniform(0, ySize), Random.uniform(0, zSize));
  }

  /**
   * 
   * @author Jens Werner
   * 
   */
  public class ThreeDimNonDiscretePositionData extends NonDiscretePositionData {

    /**
     * 
     */
    public ThreeDimNonDiscretePositionData(double x, double y, double z) {
      super(x, y, z);
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
      return this.z;
    }
  }

}
