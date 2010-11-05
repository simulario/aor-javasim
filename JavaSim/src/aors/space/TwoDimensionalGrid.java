/**
 * 
 */
package aors.space;

import java.util.ArrayList;

import aors.util.Random;

/**
 * Class for a 2-dimensional Space
 * 
 * @author Jens Werner
 * 
 */
public class TwoDimensionalGrid extends DiscreteSpace {

  private int xSize = 0;
  private int ySize = 0;

  private AbstractCell spaceCell[][];

  /**
   * 
   * @param xSize
   * @param ySize
   */
  public TwoDimensionalGrid(int xSize, int ySize, int maxOccupancy,
      boolean autoKinematics, boolean autoCollisionHandling,
      boolean autoCollisionDetection, boolean gravitation) {
    super(autoKinematics, autoCollisionHandling, autoCollisionDetection,
        gravitation);

    this.spaceCell = new AbstractCell[xSize][ySize];
    this.xSize = xSize;
    this.ySize = ySize;
    this.maxOccupancy = maxOccupancy;

    this.positionList = new ArrayList<DiscretePositionData>();

    if (this.xSize * (long) this.ySize > Integer.MAX_VALUE) {
      throw new IllegalArgumentException("Illegal Capacity: " + this.xSize
          * this.ySize);
    } else if (this.xSize * (long) this.ySize == 0) {
      throw new IllegalArgumentException("Illegal Size: x: " + this.xSize
          + " or y: " + this.ySize);
    } else if (maxOccupancy > 0) {

      int cellCount = this.xSize * this.ySize;
      for (int i = Space.ORDINATEBASE; i < cellCount + Space.ORDINATEBASE; i++) {
        this.positionList.add(new TwoDimDiscretePositionData(i,
            this.maxOccupancy, this.xSize));
      }
    }
  }

  /**
   * 
   * This method returns a random {@link TwoDimDiscretePositionData}.
   * 
   * @param ignorePositionConstraint
   *          - if its true, then give a position from the hole space
   * @return {@link TwoDimDiscretePositionData} if is available; otherwise null
   */
  @Override
  public TwoDimDiscretePositionData getRandomPosition(
      boolean ignorePositionConstraint) {

    if (ignorePositionConstraint) {
      return new TwoDimDiscretePositionData(Random.uniformInt(this.xSize
          * this.ySize + Space.ORDINATEBASE - 1), -1, this.xSize);
    }

    if (this.positionList.isEmpty())
      return null;

    int position = Random.uniformInt(this.positionList.size() - 1);

    // here we don't need to search in the list
    if (this.maxOccupancy == -1) {
      return new TwoDimDiscretePositionData(position, -1, this.xSize);
    }

    TwoDimDiscretePositionData twoDimPositionData = (TwoDimDiscretePositionData) this.positionList
        .get(position);

    return twoDimPositionData;
  }

  @Override
  public boolean updatePositionList(int x, int y, int z) {

    // if x=0 or y=0 then true, without check in the positionList
    // as freedom for the simulation-author to "park" an objekt
    if (Space.ORDINATEBASE == 1 && (x == 0 || y == 0))
      return true;

    // outside from space
    if (x > this.xSize + (Space.ORDINATEBASE - 1) || x < 0
        || y > this.ySize + (Space.ORDINATEBASE - 1) || y < 0)
      return false;

    return this.updatePosList(x, y, 0);
  }

  @Override
  protected int computePlanePosition(int x, int y, int z) {

    int result = (x + (y - Space.ORDINATEBASE) * this.xSize);

    return result;
  }

  /**
   * @return the xSize
   */
  public int getXSize() {
    return xSize;
  }

  /**
   * @return the ySize
   */
  public int getYSize() {
    return ySize;
  }

  public AbstractCell getCell(int xPos, int yPos) {

    if (xPos - Space.ORDINATEBASE < 0 || yPos - Space.ORDINATEBASE < 0)
      return null;

    if (xPos <= this.xSize && yPos <= this.ySize) {
      return this.spaceCell[xPos - Space.ORDINATEBASE][yPos
          - Space.ORDINATEBASE];
    }
    return null;
  }

  /**
   * @return the spaceCell
   */
  public AbstractCell[][] getSpaceCells() {
    return spaceCell;
  }

  /**
   * 
   * @author Jens Werner
   * 
   */
  public class TwoDimDiscretePositionData extends DiscretePositionData {

    private int xSize;

    /**
     * Notice: the planPosition means the sequence of all cells in a grid begins
     * with the [1,1] position
     * 
     * example for 3x4 grid: -- -- -- |10|11|12| -- -- -- | 7| 8| 9| -- -- -- |
     * 4| 5| 6| -- -- -- | 1| 2| 3| -- -- --
     * 
     * 
     * @param position
     *          - planePosition in a grid
     * @param contentCount
     *          - count of possible objekts in one cell
     * @param xSize
     *          - xSize of the grid
     */
    public TwoDimDiscretePositionData(int position, int contentCount, int xSize) {
      super(position, contentCount);
      this.xSize = xSize;
    }

    @Override
    public int getX() {
      return ((this.planePosition - 1) % this.xSize)
          + TwoDimensionalGrid.ORDINATEBASE;
    }

    @Override
    public int getY() {
      if (this.xSize > 0)
        return ((this.planePosition - 1) / this.xSize)
            + TwoDimensionalGrid.ORDINATEBASE;
      return 0;
    }

    @Override
    public int getZ() {
      return 0;
    }
  }

}
