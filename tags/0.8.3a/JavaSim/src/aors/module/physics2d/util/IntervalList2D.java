/**
 * 
 */
package aors.module.physics2d.util;

import java.util.ArrayList;
import java.util.List;

import aors.GeneralSpaceModel;
import aors.model.envsim.Physical;
import aors.module.physics2d.util.IntervalList.Axis;

/**
 * An IntervalList used for 2D collision detection.
 * 
 * @author Holger Wuerke
 * @since 07.03.2010
 */
public class IntervalList2D {

  private IntervalList xList;

  private IntervalList yList;

  /**
   * Creates a new IntervalList for 2D space.
   * 
   * @param objList
   * @param spaceModel
   */
  public IntervalList2D(List<Physical> objList, GeneralSpaceModel spaceModel) {
    xList = new IntervalList(objList, spaceModel, Axis.X);
    yList = new IntervalList(objList, spaceModel, Axis.Y);
  }

  /**
   * Updates the list. Calls the updateGrid method for both axes.
   */
  public void updateGrid() {
    xList.updateGrid(Axis.X);
    yList.updateGrid(Axis.Y);
  }

  /**
   * Adds an object to the list. Calls the addGrid method for both axes.
   * 
   * @param object
   */
  public void addGrid(Physical object) {
    xList.addGrid(object, Axis.X);
    yList.addGrid(object, Axis.Y);
  }

  /**
   * Removes an object from the list. Calls the remove method for both axes.
   * 
   * @param object
   */
  public void remove(Physical object) {
    xList.remove(object);
    yList.remove(object);
  }

  /**
   * Checks if any collisions or perceptions occur.
   * 
   * @param collisions
   *          - list where all detected collisions are saved
   * @param perceptions
   *          - list where all detected perceptions are saved
   */
  public void detectCollisionsGrid(List<Collision1D> collisions,
      List<Perception> perceptions) {
    List<Collision1D> xCollisions = new ArrayList<Collision1D>();
    List<Collision1D> yCollisions = new ArrayList<Collision1D>();
    List<Perception> xPerceptions = new ArrayList<Perception>();
    List<Perception> yPerceptions = new ArrayList<Perception>();

    xList.detectCollisionsGrid(xCollisions, xPerceptions);
    yList.detectCollisionsGrid(yCollisions, yPerceptions);

    // search for collisions and perceptions that occur on both axes
    for (Collision1D collision : xCollisions) {
      if (yCollisions.contains(collision)) {
        collisions.add(collision);
      }
    }

    for (Perception perception : xPerceptions) {
      if (yPerceptions.contains(perception)) {
        perceptions.add(perception);
      }
    }

  }

}
