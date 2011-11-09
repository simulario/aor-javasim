/**
 * 
 */
package aors.module.physics2d.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import aors.GeneralSpaceModel;
import aors.GeneralSpaceModel.Geometry;
import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalAgentObject;
import aors.space.Space;

/**
 * An IntervalList used for 2D collision detection.
 * 
 * @author Holger Wuerke
 * @since 07.03.2010
 */
public class IntervalList2DGrid {

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

  GeneralSpaceModel spaceModel;

  private List<IntervalEndpoint> xList = new ArrayList<IntervalEndpoint>();

  private List<IntervalEndpoint> yList = new ArrayList<IntervalEndpoint>();

  /**
   * Creates a new IntervalList for 2D grid space.
   * 
   * @param objList
   * @param spaceModel
   */
  public IntervalList2DGrid(List<Physical> objList, GeneralSpaceModel spaceModel) {
    this.spaceModel = spaceModel;

    for (Physical object : objList) {
      add(object);
    }

    // sort lists
    Collections.sort(xList);
    Collections.sort(yList);
  }

  /**
   * Add an object to this list. This will add two endpoints for the object and
   * in case of an agent two endpoints for its perception radius to both
   * coordinate lists.
   * 
   * @param object
   */
  public void add(Physical object) {
    double x = object.getX();
    double y = object.getY();

    double xMax = spaceModel.getXMax();
    double yMax = spaceModel.getYMax();

    IntervalEndpoint startpoint = new IntervalEndpoint();
    startpoint.setType(EndpointType.START);
    startpoint.setPoint(x);
    startpoint.setObject(object);
    startpoint.setCollisionObjectType(CollisionObjectType.OBJECT);

    IntervalEndpoint endpoint = new IntervalEndpoint();
    endpoint.setType(EndpointType.END);
    endpoint.setPoint(x);
    endpoint.setObject(object);
    endpoint.setCollisionObjectType(CollisionObjectType.OBJECT);

    startpoint.setOther(endpoint);
    endpoint.setOther(startpoint);

    xList.add(startpoint);
    xList.add(endpoint);

    startpoint = new IntervalEndpoint();
    startpoint.setType(EndpointType.START);
    startpoint.setPoint(y);
    startpoint.setObject(object);
    startpoint.setCollisionObjectType(CollisionObjectType.OBJECT);

    endpoint = new IntervalEndpoint();
    endpoint.setType(EndpointType.END);
    endpoint.setPoint(y);
    endpoint.setObject(object);
    endpoint.setCollisionObjectType(CollisionObjectType.OBJECT);

    startpoint.setOther(endpoint);
    endpoint.setOther(startpoint);

    yList.add(startpoint);
    yList.add(endpoint);

    // if agent with autoPerception, also add endpoints of its perception
    // radius
    if (object instanceof PhysicalAgentObject) {
      PhysicalAgentObject agent = (PhysicalAgentObject) object;

      try {
        Field autoPerceptionField = agent.getClass().getDeclaredField(
            "AUTO_PERCEPTION");

        if (autoPerceptionField.getBoolean(agent)) {

          double xStart = x - agent.getPerceptionRadius();
          double xEnd = x + agent.getPerceptionRadius();
          double yStart = y - agent.getPerceptionRadius();
          double yEnd = y + agent.getPerceptionRadius();

          if (spaceModel.getGeometry().equals(Geometry.Toroidal)) {
            if (xStart < Space.ORDINATEBASE) {
              xStart += xMax;
            }

            if (xStart > (xMax - 1 + Space.ORDINATEBASE)) {
              xStart %= xMax;
            }

            if (xEnd < Space.ORDINATEBASE) {
              xEnd += xMax;
            }

            if (xEnd > (xMax - 1 + Space.ORDINATEBASE)) {
              xEnd %= xMax;
            }

            if (yStart < Space.ORDINATEBASE) {
              yStart += yMax;
            }

            if (yStart > (yMax - 1 + Space.ORDINATEBASE)) {
              yStart %= yMax;
            }

            if (yEnd < Space.ORDINATEBASE) {
              yEnd += yMax;
            }

            if (yEnd > (yMax - 1 + Space.ORDINATEBASE)) {
              yEnd %= yMax;
            }
          }

          startpoint = new IntervalEndpoint();
          startpoint.setType(EndpointType.START);
          startpoint.setPoint(xStart);
          startpoint.setObject(object);
          startpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

          endpoint = new IntervalEndpoint();
          endpoint.setType(EndpointType.END);
          endpoint.setPoint(xEnd);
          endpoint.setObject(object);
          endpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

          startpoint.setOther(endpoint);
          endpoint.setOther(startpoint);

          xList.add(startpoint);
          xList.add(endpoint);

          startpoint = new IntervalEndpoint();
          startpoint.setType(EndpointType.START);
          startpoint.setPoint(yStart);
          startpoint.setObject(object);
          startpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

          endpoint = new IntervalEndpoint();
          endpoint.setType(EndpointType.END);
          endpoint.setPoint(yEnd);
          endpoint.setObject(object);
          endpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

          startpoint.setOther(endpoint);
          endpoint.setOther(startpoint);

          yList.add(startpoint);
          yList.add(endpoint);
        }
      } catch (Exception e) {
      }
    }
  }

  /**
   * Updates the list with the values of the objects saved in the interval
   * endpoints.
   * 
   */
  public void update() {
    // x-list
    for (IntervalEndpoint ie : xList) {
      Physical object = ie.getObject();

      if (object == null) {
        continue;
      }

      double point = object.getX();
      double max = spaceModel.getXMax();

      if (ie.getType().equals(EndpointType.START)) {
        // start point

        if (ie.getCollisionObjectType().equals(CollisionObjectType.PERCEPTION)) {
          point -= ((PhysicalAgentObject) object).getPerceptionRadius();
        }

        if (spaceModel.getGeometry().equals(Geometry.Toroidal)) {
          if (point < Space.ORDINATEBASE) {
            point += max;
          }

          if (point > (max - 1 + Space.ORDINATEBASE)) {
            point %= max;
          }
        }

        ie.setPoint(point);
      } else {
        // end point

        if (ie.getCollisionObjectType().equals(CollisionObjectType.PERCEPTION)) {
          point += ((PhysicalAgentObject) object).getPerceptionRadius();
        }

        if (spaceModel.getGeometry().equals(Geometry.Toroidal)) {
          if (point < Space.ORDINATEBASE) {
            point += max;
          }

          if (point > (max - 1 + Space.ORDINATEBASE)) {
            point %= max;
          }
        }

        ie.setPoint(point);
      }

    }

    // y-list
    for (IntervalEndpoint ie : yList) {
      Physical object = ie.getObject();

      if (object == null) {
        continue;
      }

      double point = object.getY();
      double max = spaceModel.getYMax();

      if (ie.getType().equals(EndpointType.START)) {
        // start point

        if (ie.getCollisionObjectType().equals(CollisionObjectType.PERCEPTION)) {
          point -= ((PhysicalAgentObject) object).getPerceptionRadius();
        }

        if (spaceModel.getGeometry().equals(Geometry.Toroidal)) {
          if (point < Space.ORDINATEBASE) {
            point += max;
          }

          if (point > (max - 1 + Space.ORDINATEBASE)) {
            point %= max;
          }
        }

        ie.setPoint(point);
      } else {
        // end point

        if (ie.getCollisionObjectType().equals(CollisionObjectType.PERCEPTION)) {
          point += ((PhysicalAgentObject) object).getPerceptionRadius();
        }

        if (spaceModel.getGeometry().equals(Geometry.Toroidal)) {
          if (point < Space.ORDINATEBASE) {
            point += max;
          }

          if (point > (max - 1 + Space.ORDINATEBASE)) {
            point %= max;
          }
        }

        ie.setPoint(point);
      }

    }

    sort();
    // System.out.println(list);
  }

  /**
   * Sort the lists of interval endpoints. This method is used in each
   * simulation step. We can assume that the list is still almost in order as
   * objects only change their position slightly from one step to another.
   * Therefore we use an insertion sort here, which - in this case - is more
   * efficient than other more complex algorithms.
   */
  private void sort() {

    if (xList.size() > 2) {

      List<IntervalEndpoint> newList = new ArrayList<IntervalEndpoint>();

      newList.add(xList.get(0));
      double highest = xList.get(0).getPoint();

      for (int i = 1; i < xList.size(); i++) {
        double current = xList.get(i).getPoint();

        if (current >= highest) {
          newList.add(xList.get(i));
          highest = current;
        } else {
          if (newList.size() == 1) {
            newList.add(0, xList.get(i));
            continue;
          }

          int pos = newList.size() - 2;
          while (pos >= 0) {
            if (current >= newList.get(pos).getPoint()) {
              newList.add(pos + 1, xList.get(i));
              break;
            }

            if (pos == 0) {
              newList.add(0, xList.get(i));
            }

            pos--;
          }
        }
      }

      xList = newList;
    }

    if (yList.size() > 2) {

      List<IntervalEndpoint> newList = new ArrayList<IntervalEndpoint>();

      newList.add(yList.get(0));
      double highest = yList.get(0).getPoint();

      for (int i = 1; i < yList.size(); i++) {
        double current = yList.get(i).getPoint();

        if (current >= highest) {
          newList.add(yList.get(i));
          highest = current;
        } else {
          if (newList.size() == 1) {
            newList.add(0, yList.get(i));
            continue;
          }

          int pos = newList.size() - 2;
          while (pos >= 0) {
            if (current >= newList.get(pos).getPoint()) {
              newList.add(pos + 1, yList.get(i));
              break;
            }

            if (pos == 0) {
              newList.add(0, yList.get(i));
            }

            pos--;
          }
        }
      }

      yList = newList;
    }
  }

  /**
   * Removes an object from the list.
   * 
   * @param object
   */
  public void remove(Physical object) {
    for (Iterator<IntervalEndpoint> it = xList.iterator(); it.hasNext();) {
      IntervalEndpoint ie = it.next();
      if (ie.getObject().equals(object)) {
        it.remove();
      }
    }

    for (Iterator<IntervalEndpoint> it = yList.iterator(); it.hasNext();) {
      IntervalEndpoint ie = it.next();
      if (ie.getObject().equals(object)) {
        it.remove();
      }
    }
  }

  /**
   * Checks if any perceptions occur. The "sweep and prune" algorithm is used
   * here to determine overlapping intervals.
   * 
   * @param perceptionList
   *          - list where all detected perceptions are saved
   */
  public void detectPerceptions(List<Perception> perceptionList) {
    // start with x-list
    HashMap<Perception, Integer> xPerceptions = new HashMap<Perception, Integer>();
    
    // If a start point is found, save the corresponding object together with
    // the perception flag in the activeList. Every other object on the
    // activeList is colliding with the object belonging to the start point. If
    // an end point is found, the point is saved in the oldList and removed from
    // both lists once a start point with a different value is found.

    List<IntervalEndpoint> activeList = new ArrayList<IntervalEndpoint>();
    List<IntervalEndpoint> oldList = new ArrayList<IntervalEndpoint>();

    Double old = null; // value of points in old list

    for (IntervalEndpoint ie : xList) {
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
          // - two objects (no collision detection)
          // - two perception radiuses
          // - an object and its own perception radius
          if (activeIE.getObject().equals(ie.getObject())
              || (activeIE.getCollisionObjectType().equals(ie
                  .getCollisionObjectType()))) {
            continue;
          }

          // perception
          if (activeIE.getCollisionObjectType().equals(
              CollisionObjectType.PERCEPTION)) {
            Perception perception = new Perception(
                (PhysicalAgentObject) activeIE.getObject(), ie.getObject(),
                spaceModel);
            xPerceptions.put(perception, 1);
            continue;
          }

          if (ie.getCollisionObjectType()
              .equals(CollisionObjectType.PERCEPTION)) {
            Perception perception = new Perception((PhysicalAgentObject) ie
                .getObject(), activeIE.getObject(), spaceModel);
            xPerceptions.put(perception, 1);
            continue;
          }
        }

        activeList.add(ie);
      } else {
        old = ie.getPoint();
        oldList.add(ie.getOther());
      }
    }
    
    
    // If a start point is found, save the corresponding object together with
    // the perception flag in the activeList. Every other object on the
    // activeList is colliding with the object belonging to the start point. If
    // an end point is found, the point is saved in the oldList and removed from
    // both lists once a start point with a different value is found.

    activeList = new ArrayList<IntervalEndpoint>();
    oldList = new ArrayList<IntervalEndpoint>();

    old = null; // value of points in old list

    for (IntervalEndpoint ie : yList) {
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
          // - two objects (no collision detection)
          // - two perception radiuses
          // - an object and its own perception radius
          if (activeIE.getObject().equals(ie.getObject())
              || (activeIE.getCollisionObjectType().equals(ie
                  .getCollisionObjectType()))) {
            continue;
          }

          // perception
          if (activeIE.getCollisionObjectType().equals(
              CollisionObjectType.PERCEPTION)) {
            Perception perception = new Perception(
                (PhysicalAgentObject) activeIE.getObject(), ie.getObject(),
                spaceModel);

            if (xPerceptions.containsKey(perception)) {
              perceptionList.add(perception);
            }
            
            continue;
          }

          if (ie.getCollisionObjectType()
              .equals(CollisionObjectType.PERCEPTION)) {
            Perception perception = new Perception((PhysicalAgentObject) ie
                .getObject(), activeIE.getObject(), spaceModel);

            if (xPerceptions.containsKey(perception)) {
              perceptionList.add(perception);
            }

            continue;
          }
        }

        activeList.add(ie);
      } else {
        old = ie.getPoint();
        oldList.add(ie.getOther());
      }
    }

  }

}
