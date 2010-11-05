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
public class OneDimensionalGrid extends DiscreteSpace {

  private int xSize = 0;

  public OneDimensionalGrid(int xSize, int maxOccupancy, boolean autoKinematics,
      boolean autoCollisionHandling, boolean autoCollisionDetection,
      boolean gravitation) {
    super(autoKinematics, autoCollisionHandling, autoCollisionDetection,
        gravitation);
    this.xSize = xSize;
    this.maxOccupancy = maxOccupancy;

    this.positionList = new ArrayList<DiscretePositionData>();

    if (xSize < 1)
      throw new IllegalArgumentException("Illegal Size: x: " + this.xSize);

    if (maxOccupancy > 0) {

      for (int i = Space.ORDINATEBASE; i < xSize + Space.ORDINATEBASE; i++) {
        this.positionList.add(new OneDimDiscretePositionData(i,
            this.maxOccupancy));
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
    return x;
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.space.Space#getRandomPosition()
   */
  @Override
  public OneDimDiscretePositionData getRandomPosition(
      boolean ignorePositionConstraint) {

    if (ignorePositionConstraint) {
      return new OneDimDiscretePositionData(Random.uniformInt(this.xSize
          + Space.ORDINATEBASE - 1), -1);
    }

    if (this.positionList.isEmpty())
      return null;

    int position = Random.uniformInt(this.positionList.size() - 1);

    // here we don't need to search in the list
    if (this.maxOccupancy == -1) {
      return new OneDimDiscretePositionData(position, -1);
    }

    OneDimDiscretePositionData oneDimPositionData = (OneDimDiscretePositionData) this.positionList
        .get(position);

    return oneDimPositionData;
  }

  @Override
  public boolean updatePositionList(int x, int y, int z) {

    // if x=0 then true, without check in the positionList
    // as freedom for the simulation-author to "park" an objekt
    if (Space.ORDINATEBASE == 1 && x == 0)
      return true;

    // outside from space
    if (x > this.xSize + (Space.ORDINATEBASE - 1) || x < 0)
      return false;

    return this.updatePosList(x, 0, 0);
  }

  /**
   * 
   * @author Jens Werner
   * 
   */
  public class OneDimDiscretePositionData extends DiscretePositionData {

    /**
     * 
     * @param position
     *          - planePosition in a space
     * @param contentCount
     *          - count of possible objekts in one position
     */
    public OneDimDiscretePositionData(int position, int contentCount) {
      super(position, contentCount);
    }

    @Override
    public int getX() {
      return this.planePosition;
    }

    @Override
    public int getY() {
      return 0;
    }

    @Override
    public int getZ() {
      return 0;
    }
  }

}
