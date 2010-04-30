/**
 * 
 */
package aors.space;

import java.util.Iterator;
import java.util.List;

/**
 * @author Jens Werner
 * 
 */
public abstract class DiscreteSpace extends Space {

  protected int maxOccupancy;

  protected List<DiscretePositionData> positionList;

  /**
   * 
   * @param ignorePositionConstraint
   *          - if its true, then give a position from the hole space
   * @return a random position in space
   */
  public abstract DiscretePositionData getRandomPosition(
      boolean ignorePositionConstraint);

  protected abstract int computePlanePosition(int x, int y, int z);

  /**
   * 
   * @param positionData
   * @return
   */
  public boolean updatePositionList(DiscretePositionData positionData) {
    if (positionData == null)
      return false;

    if (positionData.getContentCount() != -1) {
      if (positionData.getContentCount() == 1) {
        this.positionList.remove(positionData);
      } else {
        positionData.incrementContentCount();
      }
    }
    return true;
  }

  /**
   * A simple solution for mapping double in int, but we assume here that the
   * values comes from a discrete space
   * 
   * @param x
   * @param y
   * @param z
   * @return
   */
  public boolean updatePositionList(double x, double y, double z) {
    return this.updatePositionList((int) x, (int) y, (int) z);
  }

  public abstract boolean updatePositionList(int x, int y, int z);

  /**
   * This method check the position whether is free for one object or not
   * 
   * @param x
   * @param y
   * @param z
   * @return if the position x, y, z have space for one object
   */
  protected boolean updatePosList(int x, int y, int z) {
    int position = this.computePlanePosition(x, y, z);

    // at the border
    if (position < Space.ORDINATEBASE)
      return true;

    for (Iterator<DiscretePositionData> iterator = this.positionList.iterator(); iterator
        .hasNext();) {

      DiscretePositionData positionData = iterator.next();
      if (positionData.getContentCount() != -1
          && positionData.getPlanePosition() == position) {

        if (positionData.getContentCount() == 1) {
          iterator.remove();
        } else {
          positionData.incrementContentCount();
        }

        return true;
      }
    }

    return false;
  }

  public abstract class DiscretePositionData {

    protected int planePosition;
    private int contentCount;

    public DiscretePositionData(int position, int contentCount) {
      this.planePosition = position;
      this.contentCount = contentCount;
    }

    /**
     * @return the position
     */
    public int getPlanePosition() {
      return planePosition;
    }

    /**
     * @return the contentCount
     */
    public int getContentCount() {
      return contentCount;
    }

    public void incrementContentCount() {
      this.contentCount--;
    }

    public abstract int getX();

    public abstract int getY();

    public abstract int getZ();
  }

}
