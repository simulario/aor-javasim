/**
 * 
 */
package aors.space;

import java.util.ArrayList;

import aors.util.Random;

/**
 * @author Jens Werner
 * 
 */
public class ThreeDimensionalGrid extends DiscreteSpace {

  private int xSize = 0;
  private int ySize = 0;
  private int zSize = 0;

  public ThreeDimensionalGrid(int xSize, int ySize, int zSize, int maxOccupancy) {

    this.xSize = xSize;
    this.ySize = ySize;
    this.zSize = zSize;
    this.maxOccupancy = maxOccupancy;

    this.positionList = new ArrayList<DiscretePositionData>();

    if (this.xSize * (long) this.ySize * this.zSize > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("Illegal Capacity: " + this.xSize
          * this.ySize * this.ySize);
    } else if (this.xSize * this.ySize * this.zSize == 0) {
      throw new IllegalArgumentException("Illegal Size: x: " + this.xSize
          + " or y: " + this.ySize + " or z: " + this.zSize);
    } else if (maxOccupancy > 0) {

      int cellCount = this.xSize * this.ySize * this.ySize;
      for (int i = Space.ORDINATEBASE; i < cellCount + Space.ORDINATEBASE; i++) {
        this.positionList.add(new ThreeDimDiscretePositionData(i,
            this.maxOccupancy, this.xSize, this.ySize));
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.space.Space#computePlanePosition(int, int, int)
   */
  @Override
  protected int computePlanePosition(int x, int y, int z) {

    int result = (x + (y - Space.ORDINATEBASE) * this.xSize + (this.xSize * this.ySize)
        * (z - Space.ORDINATEBASE));

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.space.Space#getRandomPosition()
   */
  @Override
  public ThreeDimDiscretePositionData getRandomPosition(
      boolean ignorePositionConstraint) {

    if (ignorePositionConstraint) {
      return new ThreeDimDiscretePositionData(Random.uniformInt(this.xSize
          * this.ySize * this.zSize + Space.ORDINATEBASE - 1), -1, this.xSize,
          this.ySize);
    }

    if (this.positionList.isEmpty())
      return null;

    int position = Random.uniformInt(this.positionList.size() - 1);

    // here we don't need to search in the list
    if (this.maxOccupancy == -1) {
      return new ThreeDimDiscretePositionData(position, -1, this.xSize,
          this.ySize);
    }

    ThreeDimDiscretePositionData threeDimPositionData = (ThreeDimDiscretePositionData) this.positionList
        .get(position);

    return threeDimPositionData;
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.space.Space#updatePositionList(int, int, int)
   */
  @Override
  public boolean updatePositionList(int x, int y, int z) {

    // if x=0 or y=0 or z=0 then true, without check in the positionList
    // as freedom for the simulation-author to "park" an objekt
    if (Space.ORDINATEBASE == 1 && (x == 0 || y == 0 || z == 0))
      return true;

    if (x > this.xSize + (Space.ORDINATEBASE - 1) || x < 0
        || y > this.ySize + (Space.ORDINATEBASE - 1) || y < 0
        || z > this.ySize + (Space.ORDINATEBASE - 1) || z < 0)
      return false;

    return this.updatePosList(x, y, z);
  }

  public class ThreeDimDiscretePositionData extends DiscretePositionData {

    private int xSize;
    private int ySize;

    public ThreeDimDiscretePositionData(int position, int contentCount,
        int xSize, int ySize) {
      super(position, contentCount);
      this.xSize = xSize;
      this.ySize = ySize;
    }

    @Override
    public int getX() {
      return (((this.planePosition - 1) % (this.xSize * this.ySize))
          % this.xSize + Space.ORDINATEBASE);
    }

    @Override
    public int getY() {
      return (((this.planePosition - 1) % (this.xSize * this.ySize))
          / this.xSize + Space.ORDINATEBASE);
    }

    @Override
    public int getZ() {
      return (((this.planePosition - 1) / (this.xSize * this.ySize)) + 1);
    }
  }

}
