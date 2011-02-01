/**
 * 
 */
package aors.module.physics2d.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import aors.GeneralSpaceModel;
import aors.GeneralSpaceModel.Geometry;
import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalAgentObject;
import aors.space.Space;

/**
 * A list of interval endpoints used for 1D collision detection. An interval
 * represents the space a physical object covers. The beginning and the end of
 * each interval are kept in the list.
 * 
 * @author Holger Wuerke
 * @since 17.12.2009
 * 
 */
public class IntervalList {

  /**
   * An IntervalEndpoint represents a point that is either the start or the end
   * of an interval.
   * 
   * @author Holger Wuerke
   * @since 17.12.2009
   * 
   */
  private class IntervalEndpoint implements Comparable<IntervalEndpoint> {

    /**
     * Defines if it is the start or end point of the interval.
     */
    private EndpointType type;

    /**
     * The point coordinate.
     */
    private Double point;

    /**
     * The object which belongs to the point (null if collisionObjectType is
     * BORDER).
     */
    private Physical object;

    /**
     * The type of the collision object.
     */
    private CollisionObjectType collisionObjectType;

    /**
     * The corresponding endpoint (for a start point -> the corresponding end
     * point and vice versa).
     */
    private IntervalEndpoint other;

    /**
     * @return the type
     */
    public EndpointType getType() {
      return type;
    }

    /**
     * @param type
     *          the type to set
     */
    public void setType(EndpointType type) {
      this.type = type;
    }

    /**
     * @return the point
     */
    public double getPoint() {
      return point;
    }

    /**
     * @param point
     *          the point to set
     */
    public void setPoint(double point) {
      this.point = point;
    }

    /**
     * @param object
     *          the object to set
     */
    public void setObject(Physical object) {
      this.object = object;
    }

    /**
     * @return the object
     */
    public Physical getObject() {
      return object;
    }

    /**
     * @param collisionObjectType
     *          the collisionObjectType to set
     */
    public void setCollisionObjectType(CollisionObjectType collisionObjectType) {
      this.collisionObjectType = collisionObjectType;
    }

    /**
     * @return the collisionObjectType
     */
    public CollisionObjectType getCollisionObjectType() {
      return collisionObjectType;
    }

    /**
     * @param other
     *          the other to set
     */
    public void setOther(IntervalEndpoint other) {
      this.other = other;
    }

    /**
     * @return the other
     */
    public IntervalEndpoint getOther() {
      return other;
    }

    @Override
    public int compareTo(IntervalEndpoint ie) {
      return point.compareTo(ie.getPoint());
    }

    @Override
    public String toString() {
      String str = (type.equals(EndpointType.START)) ? "S: " + point : "E: "
          + point;

      if (collisionObjectType.equals(CollisionObjectType.BORDER)) {
        str += "B";
      }

      if (collisionObjectType.equals(CollisionObjectType.PERCEPTION)) {
        str += "P";
      }

      return str;
    }
  }

  private enum EndpointType {
    START, END
  };

  public enum Axis {
    X, Y, Z
  }

  private List<IntervalEndpoint> list = new ArrayList<IntervalEndpoint>();

  private GeneralSpaceModel spaceModel;

  /**
   * Creates an IntervalList from a list of Physicals. Determines the interval
   * endpoints of every object and stores it in the list. The x-coordinate of
   * the objects is used. (This is the constructor for the continuous space)
   * 
   * @param objList
   * @param spaceModel
   */
  public IntervalList(List<Physical> objList, GeneralSpaceModel spaceModel) {
    this.spaceModel = spaceModel;

    for (Physical object : objList) {
      add(object);
    }

    // add borders for euclidean space
    if (spaceModel.getGeometry().equals(Geometry.Euclidean)) {
      IntervalEndpoint startpoint = new IntervalEndpoint();
      startpoint.setType(EndpointType.START);
      startpoint.setPoint(-1);
      startpoint.setObject(null);
      startpoint.setCollisionObjectType(CollisionObjectType.BORDER);

      IntervalEndpoint endpoint = new IntervalEndpoint();
      endpoint.setType(EndpointType.END);
      endpoint.setPoint(0);
      endpoint.setObject(null);
      endpoint.setCollisionObjectType(CollisionObjectType.BORDER);

      startpoint.setOther(endpoint);
      endpoint.setOther(startpoint);

      list.add(startpoint);
      list.add(endpoint);

      startpoint = new IntervalEndpoint();
      startpoint.setType(EndpointType.START);
      startpoint.setPoint(spaceModel.getXMax());
      startpoint.setObject(null);
      startpoint.setCollisionObjectType(CollisionObjectType.BORDER);

      endpoint = new IntervalEndpoint();
      endpoint.setType(EndpointType.END);
      endpoint.setPoint(spaceModel.getXMax() + 1);
      endpoint.setObject(null);
      endpoint.setCollisionObjectType(CollisionObjectType.BORDER);

      startpoint.setOther(endpoint);
      endpoint.setOther(startpoint);

      list.add(startpoint);
      list.add(endpoint);
    }

    // sort list
    Collections.sort(list);
  }


  /**
   * Checks if any collisions occur (this includes perceptions as well). The
   * "sweep and prune" algorithm is used here to determine overlapping
   * intervals. (used in continuous space)
   * 
   * @param collisionList
   *          - list where all detected collisions are saved
   * @param perceptions
   *          - set where all detected perceptions are saved
   * @param borderReached
   *          - list where all objects, that have reached the space border, are
   *          saved
   */
  public void detectCollisions(List<Collision1D> collisionList,
      Set<Perception> perceptions, Set<Physical> borderReached) {
    // If a start point is found, save it in the activeList. Every other object
    // belonging to a point on the activeList is colliding with the object
    // belonging to the start point. If an end point is found, the corresponding
    // object is removed from the activeList.

    List<IntervalEndpoint> activeList = new ArrayList<IntervalEndpoint>();

    for (IntervalEndpoint ie : list) {
      if (ie.getType().equals(EndpointType.START)) {
        for (IntervalEndpoint activeIE : activeList) {
          // filter out collisions between:
          // - an object and its own perception radius
          // - two perception radiuses
          // - a perception radius and the space border
          if (activeIE.getObject() != null
              && activeIE.getObject().equals(ie.getObject())
              || (activeIE.getCollisionObjectType().equals(
                  CollisionObjectType.PERCEPTION) && ie
                  .getCollisionObjectType().equals(
                      CollisionObjectType.PERCEPTION))
              || ((activeIE.getCollisionObjectType()
                  .equals(CollisionObjectType.BORDER)) && ie
                  .getCollisionObjectType().equals(
                      CollisionObjectType.PERCEPTION))
              || (activeIE.getCollisionObjectType().equals(
                  CollisionObjectType.PERCEPTION) && (ie
                  .getCollisionObjectType().equals(CollisionObjectType.BORDER)))
              || (activeIE.getCollisionObjectType().equals(
                  CollisionObjectType.BORDER) && (ie.getCollisionObjectType()
                  .equals(CollisionObjectType.BORDER)))) {
            continue;
          }

          // perception
          if (activeIE.getCollisionObjectType().equals(
              CollisionObjectType.PERCEPTION)) {
            Perception perception = new Perception(
                (PhysicalAgentObject) activeIE.getObject(), ie.getObject(),
                spaceModel);
            perceptions.add(perception);
            continue;
          }

          if (ie.getCollisionObjectType()
              .equals(CollisionObjectType.PERCEPTION)) {
            Perception perception = new Perception((PhysicalAgentObject) ie
                .getObject(), activeIE.getObject(), spaceModel);
            perceptions.add(perception);
            continue;
          }

          // space border reached
          if (activeIE.getCollisionObjectType().equals(
              CollisionObjectType.BORDER)) {
            borderReached.add(ie.getObject());
            continue;
          }

          if (ie.getCollisionObjectType().equals(CollisionObjectType.BORDER)) {
            borderReached.add(activeIE.getObject());
            continue;
          }

          // collision
          Collision1D collision = new Collision1D(ie.getObject(), activeIE
              .getObject());
          collisionList.add(collision);
        }

        activeList.add(ie);
      } else {
        activeList.remove(ie.getOther());
      }
    }
  }

  /**
   * Checks if any collisions occur (this includes perceptions as well). The
   * "sweep and prune" algorithm is used here to determine overlapping
   * intervals. (This method is only used in grid space, because we need
   * slightly different behavior here).
   * 
   * @param collisionList
   *          - list where all detected collisions are saved
   * @param perceptionList
   *          - list where all detected perceptions are saved
   */
  public void detectCollisionsGrid(List<Collision1D> collisionList,
      List<Perception> perceptionList) {
    // If a start point is found, save the corresponding object together with
    // the perception flag in the activeList. Every other object on the
    // activeList is colliding with the object belonging to the start point. If
    // an end point is found, the point is saved in the oldList and removed from
    // both lists once a start point with a different value is found.

    List<IntervalEndpoint> activeList = new ArrayList<IntervalEndpoint>();
    List<IntervalEndpoint> oldList = new ArrayList<IntervalEndpoint>();

    Double old = null; // value of points in old list

    for (IntervalEndpoint ie : list) {
      // if the points coordinates differ from the ones in the oldList,
      // remove all objects in the oldList from the activeList
      if (old != null && old != ie.getPoint()) {
        activeList.removeAll(oldList);
        oldList.clear();
        old = null;
      }

      if (ie.getType().equals(EndpointType.START)) {

        for (IntervalEndpoint activeIE : activeList) {
          // leave out collisions between:
          // - two perception radiuses
          // - an object and its own perception radius
          if (activeIE.getObject().equals(ie.getObject())
              || (activeIE.getCollisionObjectType().equals(
                  CollisionObjectType.PERCEPTION) && ie
                  .getCollisionObjectType().equals(
                      CollisionObjectType.PERCEPTION))) {
            continue;
          }

          // perception
          if (activeIE.getCollisionObjectType().equals(
              CollisionObjectType.PERCEPTION)) {
            Perception perception = new Perception(
                (PhysicalAgentObject) activeIE.getObject(), ie.getObject(),
                spaceModel);
            perceptionList.add(perception);
            continue;
          }

          if (ie.getCollisionObjectType()
              .equals(CollisionObjectType.PERCEPTION)) {
            Perception perception = new Perception((PhysicalAgentObject) ie
                .getObject(), activeIE.getObject(), spaceModel);
            perceptionList.add(perception);
            continue;
          }

          // collision
          Collision1D collision = new Collision1D(ie.getObject(), activeIE
              .getObject());

          collisionList.add(collision);
        }

        activeList.add(ie);
      } else {
        old = ie.getPoint();
        oldList.add(ie.getOther());
      }
    }
  }

  /**
   * Sort the list of interval endpoints. This method is used in each simulation
   * step. We can assume that the list is still almost in order as objects only
   * change their position slightly from one step to another. Therefore we use
   * an insertion sort here, which - in this case - is more efficient than other
   * more complex algorithms.
   */
  private void sort() {
    if (list.size() < 2) {
      return;
    }

    List<IntervalEndpoint> newList = new ArrayList<IntervalEndpoint>();

    newList.add(list.get(0));
    double highest = list.get(0).getPoint();

    for (int i = 1; i < list.size(); i++) {
      double current = list.get(i).getPoint();

      if (current >= highest) {
        newList.add(list.get(i));
        highest = current;
      } else {
        if (newList.size() == 1) {
          newList.add(0, list.get(i));
          continue;
        }

        int pos = newList.size() - 2;
        while (pos >= 0) {
          if (current >= newList.get(pos).getPoint()) {
            newList.add(pos + 1, list.get(i));
            break;
          }

          if (pos == 0) {
            newList.add(0, list.get(i));
          }

          pos--;
        }
      }
    }

    list = newList;
  }

  /**
   * Add an object to this list. This will add two endpoints for the object and
   * in case of an agent two endpoints for its perception radius to the list.
   * 
   * @param object
   */
  public void add(Physical object) {
    double width = object.getWidth();
    double start = object.getX() - width / 2;
    double end = object.getX() + width / 2;

    if (spaceModel.getGeometry().equals(Geometry.Toroidal)) {
      if (start < 0) {
        start += spaceModel.getXMax();
      }

      if (start >= spaceModel.getXMax()) {
        start %= spaceModel.getXMax();
      }

      if (end < 0) {
        end += spaceModel.getXMax();
      }

      if (end >= spaceModel.getXMax()) {
        end %= spaceModel.getXMax();
      }
    }

    IntervalEndpoint startpoint = new IntervalEndpoint();
    startpoint.setType(EndpointType.START);
    startpoint.setPoint(start);
    startpoint.setObject(object);
    startpoint.setCollisionObjectType(CollisionObjectType.OBJECT);

    IntervalEndpoint endpoint = new IntervalEndpoint();
    endpoint.setType(EndpointType.END);
    endpoint.setPoint(end);
    endpoint.setObject(object);
    endpoint.setCollisionObjectType(CollisionObjectType.OBJECT);

    startpoint.setOther(endpoint);
    endpoint.setOther(startpoint);

    list.add(startpoint);
    list.add(endpoint);

    // if agent with autoPerception, also add endpoints of its perception
    // radius
    if (object instanceof PhysicalAgentObject) {
      PhysicalAgentObject agent = (PhysicalAgentObject) object;

      try {
        Field autoPerceptionField = agent.getClass().getDeclaredField(
            "AUTO_PERCEPTION");

        if (autoPerceptionField.getBoolean(agent)
            && (agent.getPerceptionRadius() > (agent.getWidth() / 2))) {
          start = agent.getX() - agent.getPerceptionRadius();
          end = agent.getX() + agent.getPerceptionRadius();

          if (spaceModel.getGeometry().equals(Geometry.Toroidal)) {
            if (start < 0) {
              start += spaceModel.getXMax();
            }

            if (start >= spaceModel.getXMax()) {
              start %= spaceModel.getXMax();
            }

            if (end < 0) {
              end += spaceModel.getXMax();
            }

            if (end >= spaceModel.getXMax()) {
              end %= spaceModel.getXMax();
            }
          }

          startpoint = new IntervalEndpoint();
          startpoint.setType(EndpointType.START);
          startpoint.setPoint(start);
          startpoint.setObject(object);
          startpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

          endpoint = new IntervalEndpoint();
          endpoint.setType(EndpointType.END);
          endpoint.setPoint(end);
          endpoint.setObject(object);
          endpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

          startpoint.setOther(endpoint);
          endpoint.setOther(startpoint);

          list.add(startpoint);
          list.add(endpoint);
        }
      } catch (Exception e) {
      }
    }

  }

  /**
   * Removes the endpoints belonging to the given object.
   * 
   * @param object
   */
  public void remove(Physical object) {
    for (Iterator<IntervalEndpoint> it = list.iterator(); it.hasNext();) {
      IntervalEndpoint ie = it.next();
      if (ie.getObject().equals(object)) {
        it.remove();
      }
    }
  }

  /**
   * Updates the List with the values of the map. (used in continuous space)
   * 
   * @param newPositions
   */
  public void update(Map<Physical, Double> newPositions) {
    for (IntervalEndpoint ie : list) {
      Physical object = ie.getObject();

      if (object == null) {
        continue;
      }

      double width = object.getWidth();

      if (ie.getType().equals(EndpointType.START)) {
        // start point
        double start = (ie.getCollisionObjectType()
            .equals(CollisionObjectType.PERCEPTION)) ? newPositions.get(object)
            - ((PhysicalAgentObject) object).getPerceptionRadius()
            : newPositions.get(object) - width / 2;

        if (spaceModel.getGeometry().equals(Geometry.Toroidal)) {
          if (start < 0) {
            start += spaceModel.getXMax();
          }

          if (start >= spaceModel.getXMax()) {
            start %= spaceModel.getXMax();
          }
        }

        ie.setPoint(start);
      } else {
        // end point
        double end = (ie.getCollisionObjectType()
            .equals(CollisionObjectType.PERCEPTION)) ? newPositions.get(object)
            + ((PhysicalAgentObject) object).getPerceptionRadius()
            : newPositions.get(object) + width / 2;

        if (spaceModel.getGeometry().equals(Geometry.Toroidal)) {
          if (end < 0) {
            end += spaceModel.getXMax();
          }

          if (end >= spaceModel.getXMax()) {
            end %= spaceModel.getXMax();
          }
        }

        ie.setPoint(end);
      }

    }

    sort();
//     System.out.println(list);
  }

}
