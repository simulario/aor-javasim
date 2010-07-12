/*************************************************************************************************************
 * AgentObject-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
 *
 * AOR-JSim v.2
 *
 * Copyright (C) 2008 AOR Team: Daniel Draeger, Adrian Giurca, Emilian Pascalau, Andreas Post, Marco Pehla,
 * Gerd Wagner, Jens Werner
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * File: SpaceModel.java
 * 
 * Package: info.aors
 *
 **************************************************************************************************************/
package aors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import aors.data.evt.sim.ObjektInitEvent;
import aors.data.evt.sim.ObjektInitEventListener;
import aors.model.envsim.Physical;
import aors.space.AbstractCell;
import aors.space.Space;

/**
 * SpaceModel This class is instantiated only by the XSLTGenerator.
 * 
 * @author Emilian Pascalau, Adrian Giurca
 * @since May 25, 2008
 * @version $Revision$
 */
public abstract class GeneralSpaceModel {

  protected ArrayList<ObjektInitEventListener> initGridListener;

  // Perhaps is better to define this 3 methods in the derifed,
  // but then we have to create it with code-creation
  public void addObjektInitListener(ObjektInitEventListener listener) {
    if (this.initGridListener != null)
      this.initGridListener.add(listener);
  }

  public boolean removeObjektInitListener(ObjektInitEventListener listener) {
    if (this.initGridListener != null) {
      return this.initGridListener.remove(listener);
    }
    return false;
  }

  public void fireInitEvent(AbstractCell[][] abstractCells) {
    if (abstractCells != null) {
      for (ObjektInitEventListener oIEL : this.initGridListener) {
        oIEL.objektInitEvent(new ObjektInitEvent(abstractCells));
      }
    }
  }

  /**
   * The space dimensions. See <code>Dimensions</code> enumeration values.
   */
  private Dimensions dimensions;

  /**
   * The space multiplicity. How many times the space is multiplied. This mainly
   * make sense for 1D spaces. By default this is 1, meaning that the space is
   * not multiplied (is just one space).
   */
  private int multiplicity = 1;

  /**
   * Comments: required
   */
  private SpaceType spaceType;

  /**
   * default: Euclidean
   */
  private Geometry geometry = Geometry.Euclidean;

  /**
   * default: false
   */
  private boolean discrete = false;

  /**
   * 
   */
  private SpatialDistanceUnit spatialDistanceUnit = SpatialDistanceUnit.m;

  /**
   * 
   */
  private int xMax = 0;

  /**
   * 
   */
  private int yMax = 0;

  /**
   * 
   */
  private int zMax = 0;

  /**
   * the value -1 represent the default 'unbounded'
   */
  private int gridCellMaxOccupancy = -1;

  /**
   * 
   * Create a new {@code SpaceModel}. All other properties are added using
   * setter
   * 
   * @param dimensions
   *          this property is required
   */
  public GeneralSpaceModel(Dimensions dimensions) {
    this.dimensions = dimensions;
  }

  public GeneralSpaceModel(Dimensions dimensions, SpaceType spaceType) {
    this.dimensions = dimensions;
    this.spaceType = spaceType;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code dimensions}.
   * 
   * 
   * 
   * @return the {@code dimensions}.
   */
  public Dimensions getDimensions() {
    return dimensions;
  }

  /**
   * Get the multiplicity of the space.
   * 
   * @return the value of <code>multiplicity</code> property.
   */
  public int getMultiplicity() {
    return this.multiplicity;
  }

  /**
   * Set new value for the space multiplicity.
   * 
   * @param multiplicity
   *          the new multiplicity value to set
   * @throws IllegalArgumentException
   *           if the value to be set is not an integer bigger than 1.
   */
  public void setMultiplicity(int multiplicity) throws IllegalArgumentException {
    if (multiplicity < 1 || multiplicity > Integer.MAX_VALUE)
      throw new IllegalArgumentException(
          "GeneralSpaceModel.multiplicity can have as value only integers bigger or equal with 1!");
    else {
      this.multiplicity = multiplicity;
    }
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code geometry}.
   * 
   * 
   * 
   * @return the {@code geometry}.
   */
  public Geometry getGeometry() {
    return geometry;
  }

  /**
   * Usage: Set the {@code geometry}.
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param geometry
   *          The {@code geometry} to set.
   */
  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code discrete}.
   * 
   * 
   * 
   * @return the {@code discrete}.
   */
  public boolean isDiscrete() {
    return discrete;
  }

  /**
   * Usage: Set the {@code discrete}.
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param discrete
   *          The {@code discrete} to set.
   */
  public void setDiscrete(boolean discrete) {
    this.discrete = discrete;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code spatialDistanceUnit}.
   * 
   * 
   * 
   * @return the {@code spatialDistanceUnit}.
   */
  public SpatialDistanceUnit getSpatialDistanceUnit() {
    return spatialDistanceUnit;
  }

  /**
   * Usage: Set the {@code spatialDistanceUnit}.
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param spatialDistanceUnit
   *          The {@code spatialDistanceUnit} to set.
   */
  public void setSpatialDistanceUnit(SpatialDistanceUnit spatialDistanceUnit) {
    this.spatialDistanceUnit = spatialDistanceUnit;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code xMax}.
   * 
   * 
   * 
   * @return the {@code xMax}.
   */
  public int getXMax() {
    return xMax;
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param xMax
   * @throws IllegalArgumentException
   *           when the value of xMax is negative
   */
  public void setXMax(long xMax) throws IllegalArgumentException {
    if (xMax < 0 || xMax > Integer.MAX_VALUE)
      throw new IllegalArgumentException("Insert positive integer!");
    else {
      this.xMax = (int) xMax;
    }
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code yMax}.
   * 
   * 
   * 
   * @return the {@code yMax}.
   */
  public int getYMax() {
    return yMax;
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param yMax
   * @throws IllegalArgumentException
   *           when the value of yMax is negative
   */
  public void setYMax(long yMax) throws IllegalArgumentException {
    if (yMax < 0 || yMax > Integer.MAX_VALUE)
      throw new IllegalArgumentException("Insert positive value!");
    else {
      this.yMax = (int) yMax;
    }
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code zMax}.
   * 
   * 
   * 
   * @return the {@code zMax}.
   */
  public long getZMax() {
    return zMax;
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param zMax
   * @throws IllegalArgumentException
   *           when the value of zMax is negative
   */
  public void setZMax(long zMax) throws IllegalArgumentException {
    if (zMax < 0 || zMax > Integer.MAX_VALUE)
      throw new IllegalArgumentException("Insert positive value!");
    else {
      this.zMax = (int) zMax;
    }
  }

  /**
   * 
   * Comments: this methods compute a {@link Physical} depends of the
   * space-model-geometry (Notice: if its Spheric then its toroidal otherwise
   * non-toroidal)
   * 
   * @param physicalObject
   *          - the {@link Physical} that have to be moved
   * @param moveX
   *          - the distance
   * @return the new position (or null if the maxValue is 0)
   * 
   */
  public double newX(Physical physicalObject, double moveX) {

    double targetX = 0;
    if (this.xMax > 0) {
      double currentX = physicalObject.getX();

      if (this.geometry == Geometry.Toroidal) {
        targetX = this.computeSphericPosition(currentX, moveX, this.xMax);
      } else {
        targetX = this.computeEuclideanPosition(currentX, moveX, this.xMax,
            physicalObject.getWidth());
      }
    }
    return targetX;
  }

  /**
   * 
   * Comments: this methods compute a {@link Physical} depends of the
   * space-model-geometry (Notice: if its Spheric then its toroidal otherwise
   * non-toroidal)
   * 
   * @param physicalObject
   *          - the {@link Physical} that have to be moved
   * @param moveY
   *          - the distance
   * @return the new position (or null if the maxValue is 0)
   * 
   */
  public double newY(Physical physicalObject, double moveY) {

    double targetY = 0;
    if (this.yMax > 0) {
      double currentY = physicalObject.getY();

      if (this.geometry == Geometry.Toroidal) {
        targetY = this.computeSphericPosition(currentY, moveY, this.yMax);
      } else {
        targetY = this.computeEuclideanPosition(currentY, moveY, this.yMax,
            physicalObject.getHeight());
      }
    }
    return targetY;
  }

  /**
   * 
   * Comments: this methods compute a {@link Physical} depends of the
   * space-model-geometry (Notice: if its Spheric then its toroidal otherwise
   * non-toroidal)
   * 
   * @param physicalObject
   *          - the {@link Physical} that have to be moved
   * @param moveZ
   *          - the distance
   * @return the new position (or null if the maxValue is 0)
   * 
   */
  public double newZ(Physical physicalObject, double moveZ) {

    double targetZ = 0;
    if (this.zMax > 0) {
      double currentZ = physicalObject.getZ();

      if (this.geometry == Geometry.Toroidal) {
        targetZ = this.computeSphericPosition(currentZ, moveZ, this.zMax);
      } else {
        targetZ = this.computeEuclideanPosition(currentZ, moveZ, this.zMax,
            physicalObject.getDepth());
      }
    }
    return targetZ;
  }

  /**
   * 
   * 
   * @param current
   *          - the current position
   * @param move
   *          - the points to move
   * @param max
   *          - the maximum
   * @return the new position in a spheric space
   */
  private double computeSphericPosition(double current, double move, long max) {

    // special case
    if (Space.ORDINATEBASE == 1 && current == 0 && move == 0)
      return 0;

    double target;
    target = current + move;
    if (this.isDiscrete())
      target = target - Space.ORDINATEBASE;
    /*
     * if (target >= 0 && target > max) { target = target % max; } else if
     * (target < 0) { target = max + (target % max); } its possible too, but its
     * slower
     */

    if (target >= 0) {
      target = target % (max);
    } else {
      target = max + (target % max);
    }
    if (this.isDiscrete())
      target = target + Space.ORDINATEBASE;

    return target;
  }

  /**
   * Notice: if the movement greater then the border, then is the new position
   * on the border
   * 
   * @param current
   *          - the current position
   * @param move
   *          - the points to move
   * @param max
   *          - the maximum
   * @param size
   *          - the size of the object in the appropriate dimension (width,
   *          height or depth)
   * 
   * @return the new position in a euclidian space
   */
  private double computeEuclideanPosition(double current, double move,
      long max, double size) {
    double target = current + move;
    if (target > max - size / 2) {
      if (this.isDiscrete()) {
        target = (int) (max - size / 2) + Space.ORDINATEBASE;
      } else {
        target = max - size / 2;
      }
    } else if (this.isDiscrete() && target < size / 2 + Space.ORDINATEBASE) {
      target = (int) (size / 2 + Space.ORDINATEBASE);
    } else if (!this.isDiscrete() && target < size / 2) {
      target = size / 2;
    }
    return target;
  }

  public enum Geometry {

    /**
     * 
     */

    Toroidal,

    /**
     * 
     */

    Euclidean;

    // define a new HashMap
    static final Map<String, Geometry> geometryMap = new HashMap<String, Geometry>();

    static { // put as key the String and as value the object Geometry itself
      for (Geometry g : Geometry.values())
        geometryMap.put(g.toString(), g);
    }
  }

  public enum Dimensions {
    /**
     * 
     */
    one,
    /**
     * 
     */
    two,
    /**
     * 
     */
    three,
    /**
     *
     */
    OnePlus1, OnePlus1Plus1, OnePlus1Plus1Plus1;

    public static final Map<String, Integer> dimensionsMap = new HashMap<String, Integer>();

    static {
      int i = 1;
      for (Dimensions dimensions : Dimensions.values())
        dimensionsMap.put(dimensions.toString(), i++);
    }
  }

  public enum SpaceType {
    OneD, OneDGrid, TwoD, TwoDLateralView, TwoDGrid, ThreeD, ThreeDGrid;

    public static final Map<String, Integer> spaceTypeMap = new HashMap<String, Integer>();

    static {
      int i = 1;
      for (SpaceType spaceType : SpaceType.values())
        spaceTypeMap.put(spaceType.toString(), i++);
    }
  }

  public enum SpatialDistanceUnit {

    /**
     * 
     */
    mm,
    /**
     * 
     */
    cm,
    /**
     * 
     */
    m,
    /**
     * 
     */
    km;

    static final Map<String, SpatialDistanceUnit> spatialDistanceUnitMap = new HashMap<String, SpatialDistanceUnit>();

    static {
      for (SpatialDistanceUnit sdu : SpatialDistanceUnit.values())
        spatialDistanceUnitMap.put(sdu.toString(), sdu);
    }
  }

  /**
   * @return the gridCellMaxOccupancy
   */
  public int getGridCellMaxOccupancy() {
    return gridCellMaxOccupancy;
  }

  /**
   * @param gridCellMaxOccupancy
   *          the gridCellMaxOccupancy to set
   */
  public void setGridCellMaxOccupancy(int gridCellMaxOccupancy) {
    this.gridCellMaxOccupancy = gridCellMaxOccupancy;
  }

  public abstract Space getSpace();

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code spaceType}.
   * 
   * 
   * 
   * @return the {@code spaceType}.
   */
  public SpaceType getSpaceType() {
    return spaceType;
  }

}
