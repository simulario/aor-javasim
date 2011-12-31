/**
 * 
 */
package aors.module.physics.collision;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import aors.GeneralSpaceModel;
import aors.GeneralSpaceModel.Geometry;
import aors.model.envsim.Physical;
import aors.model.envsim.Physical.PhysicsType;
import aors.model.envsim.PhysicalAgentObject;
import aors.module.physics.collision.IntervalBound.BoundType;
import aors.space.Space;

/**
 * An IntervalList used for 2D collision detection in grid space.
 * 
 * @author Holger Wuerke
 */
public class IntervalList2DGrid {

  GeneralSpaceModel spaceModel;

  private List<IntervalBound> xList = new ArrayList<IntervalBound>();

  private List<IntervalBound> yList = new ArrayList<IntervalBound>();

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

    IntervalBound startpoint = new IntervalBound();
    startpoint.setType(BoundType.START);
    startpoint.setPoint(x);
    startpoint.setObject(object);
    startpoint.setCollisionObjectType(CollisionObjectType.OBJECT);

    IntervalBound endpoint = new IntervalBound();
    endpoint.setType(BoundType.END);
    endpoint.setPoint(x);
    endpoint.setObject(object);
    endpoint.setCollisionObjectType(CollisionObjectType.OBJECT);

    startpoint.setOther(endpoint);
    endpoint.setOther(startpoint);

    xList.add(startpoint);
    xList.add(endpoint);

    startpoint = new IntervalBound();
    startpoint.setType(BoundType.START);
    startpoint.setPoint(y);
    startpoint.setObject(object);
    startpoint.setCollisionObjectType(CollisionObjectType.OBJECT);

    endpoint = new IntervalBound();
    endpoint.setType(BoundType.END);
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

          startpoint = new IntervalBound();
          startpoint.setType(BoundType.START);
          startpoint.setPoint(xStart);
          startpoint.setObject(object);
          startpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

          endpoint = new IntervalBound();
          endpoint.setType(BoundType.END);
          endpoint.setPoint(xEnd);
          endpoint.setObject(object);
          endpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

          startpoint.setOther(endpoint);
          endpoint.setOther(startpoint);

          xList.add(startpoint);
          xList.add(endpoint);

          startpoint = new IntervalBound();
          startpoint.setType(BoundType.START);
          startpoint.setPoint(yStart);
          startpoint.setObject(object);
          startpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

          endpoint = new IntervalBound();
          endpoint.setType(BoundType.END);
          endpoint.setPoint(yEnd);
          endpoint.setObject(object);
          endpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

          startpoint.setOther(endpoint);
          endpoint.setOther(startpoint);

          yList.add(startpoint);
          yList.add(endpoint);
          
          // if in toroidal space, the radius exceeds a border, add another two 
          // interval points on the other side 
          if (spaceModel.getGeometry().equals(Geometry.Toroidal)) {
            if (xStart < Space.ORDINATEBASE) {
              double newStart = xStart + xMax;
              double newEnd = newStart + 2 * agent.getPerceptionRadius();
              
              startpoint = new IntervalBound();
              startpoint.setType(BoundType.START);
              startpoint.setPoint(newStart);
              startpoint.setObject(object);
              startpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

              endpoint = new IntervalBound();
              endpoint.setType(BoundType.END);
              endpoint.setPoint(newEnd);
              endpoint.setObject(object);
              endpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

              startpoint.setOther(endpoint);
              endpoint.setOther(startpoint);

              xList.add(startpoint);
              xList.add(endpoint);              
            }

            if (xStart > (xMax - 1 + Space.ORDINATEBASE)) {
              double newStart = xStart % xMax;
              double newEnd = newStart + 2 * agent.getPerceptionRadius();

              startpoint = new IntervalBound();
              startpoint.setType(BoundType.START);
              startpoint.setPoint(newStart);
              startpoint.setObject(object);
              startpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

              endpoint = new IntervalBound();
              endpoint.setType(BoundType.END);
              endpoint.setPoint(newEnd);
              endpoint.setObject(object);
              endpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

              startpoint.setOther(endpoint);
              endpoint.setOther(startpoint);

              xList.add(startpoint);
              xList.add(endpoint);              
            }

            if (xEnd < Space.ORDINATEBASE) {
              double newEnd = xEnd + xMax;
              double newStart = newEnd - 2 * agent.getPerceptionRadius();

              startpoint = new IntervalBound();
              startpoint.setType(BoundType.START);
              startpoint.setPoint(newStart);
              startpoint.setObject(object);
              startpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

              endpoint = new IntervalBound();
              endpoint.setType(BoundType.END);
              endpoint.setPoint(newEnd);
              endpoint.setObject(object);
              endpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

              startpoint.setOther(endpoint);
              endpoint.setOther(startpoint);

              xList.add(startpoint);
              xList.add(endpoint);              
            }

            if (xEnd > (xMax - 1 + Space.ORDINATEBASE)) {
              double newEnd = xEnd % xMax;
              double newStart = newEnd - 2 * agent.getPerceptionRadius();

              startpoint = new IntervalBound();
              startpoint.setType(BoundType.START);
              startpoint.setPoint(newStart);
              startpoint.setObject(object);
              startpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

              endpoint = new IntervalBound();
              endpoint.setType(BoundType.END);
              endpoint.setPoint(newEnd);
              endpoint.setObject(object);
              endpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

              startpoint.setOther(endpoint);
              endpoint.setOther(startpoint);

              xList.add(startpoint);
              xList.add(endpoint);              
            }

            if (yStart < Space.ORDINATEBASE) {
              double newStart = yStart + yMax;
              double newEnd = newStart + 2 * agent.getPerceptionRadius();
              
              startpoint = new IntervalBound();
              startpoint.setType(BoundType.START);
              startpoint.setPoint(newStart);
              startpoint.setObject(object);
              startpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

              endpoint = new IntervalBound();
              endpoint.setType(BoundType.END);
              endpoint.setPoint(newEnd);
              endpoint.setObject(object);
              endpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

              startpoint.setOther(endpoint);
              endpoint.setOther(startpoint);

              yList.add(startpoint);
              yList.add(endpoint);              
            }

            if (yStart > (yMax - 1 + Space.ORDINATEBASE)) {
              double newStart = yStart % yMax;
              double newEnd = newStart + 2 * agent.getPerceptionRadius();
              
              startpoint = new IntervalBound();
              startpoint.setType(BoundType.START);
              startpoint.setPoint(newStart);
              startpoint.setObject(object);
              startpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

              endpoint = new IntervalBound();
              endpoint.setType(BoundType.END);
              endpoint.setPoint(newEnd);
              endpoint.setObject(object);
              endpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

              startpoint.setOther(endpoint);
              endpoint.setOther(startpoint);

              yList.add(startpoint);
              yList.add(endpoint);              
            }

            if (yEnd < Space.ORDINATEBASE) {
              double newEnd = yEnd + yMax;
              double newStart = newEnd - 2 * agent.getPerceptionRadius();

              startpoint = new IntervalBound();
              startpoint.setType(BoundType.START);
              startpoint.setPoint(newStart);
              startpoint.setObject(object);
              startpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

              endpoint = new IntervalBound();
              endpoint.setType(BoundType.END);
              endpoint.setPoint(newEnd);
              endpoint.setObject(object);
              endpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

              startpoint.setOther(endpoint);
              endpoint.setOther(startpoint);

              yList.add(startpoint);
              yList.add(endpoint);              
            }

            if (yEnd > (yMax - 1 + Space.ORDINATEBASE)) {
              double newEnd = yEnd % yMax;
              double newStart = newEnd - 2 * agent.getPerceptionRadius();

              startpoint = new IntervalBound();
              startpoint.setType(BoundType.START);
              startpoint.setPoint(newStart);
              startpoint.setObject(object);
              startpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

              endpoint = new IntervalBound();
              endpoint.setType(BoundType.END);
              endpoint.setPoint(newEnd);
              endpoint.setObject(object);
              endpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

              startpoint.setOther(endpoint);
              endpoint.setOther(startpoint);

              yList.add(startpoint);
              yList.add(endpoint);              
            }
          }

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
    for (IntervalBound ie : xList) {
      Physical object = ie.getObject();

      if (object == null) {
        continue;
      }

      double point = object.getX();
      double max = spaceModel.getXMax();

      if (ie.getType().equals(BoundType.START)) {
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
    for (IntervalBound ie : yList) {
      Physical object = ie.getObject();

      if (object == null) {
        continue;
      }

      double point = object.getY();
      double max = spaceModel.getYMax();

      if (ie.getType().equals(BoundType.START)) {
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
//    System.out.println("xList: " + xList);
//    System.out.println("yList: " + yList);
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

      List<IntervalBound> newList = new ArrayList<IntervalBound>();

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

      List<IntervalBound> newList = new ArrayList<IntervalBound>();

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
    for (Iterator<IntervalBound> it = xList.iterator(); it.hasNext();) {
      IntervalBound ie = it.next();
      if (ie.getObject().equals(object)) {
        it.remove();
      }
    }

    for (Iterator<IntervalBound> it = yList.iterator(); it.hasNext();) {
      IntervalBound ie = it.next();
      if (ie.getObject().equals(object)) {
        it.remove();
      }
    }
  }

  /**
   * Checks if any perceptions occur. The "sort-and-sweep" algorithm is used
   * here to determine overlapping intervals.
   * 
   * @param perceptionList
   *          - list where all detected perceptions are saved
   */
  public void detectPerceptions(Set<Perception2DGrid> perceptionList) {
    // start with x-list
    HashMap<Perception2DGrid, Integer> xPerceptions = new HashMap<Perception2DGrid, Integer>();
    
    // If a start point is found, save the corresponding object together with
    // the perception flag in the activeList. Every other object on the
    // activeList is colliding with the object belonging to the start point. If
    // an end point is found, the point is saved in the oldList and removed from
    // both lists once a start point with a different value is found.

    List<IntervalBound> activeList = new ArrayList<IntervalBound>();
    List<IntervalBound> oldList = new ArrayList<IntervalBound>();

    Double old = null; // value of points in old list

    for (IntervalBound ib : xList) {
      // if the points coordinates differ from the ones in the oldList,
      // remove all objects in the oldList from the activeList
      if (old != null && old != ib.getPoint()) {
        activeList.removeAll(oldList);
        oldList.clear();
        old = null;
      }

      if (ib.getType().equals(BoundType.START)) {

        for (IntervalBound activeIB : activeList) {
          // leave out collisions between:
          // - two objects (no collision detection)
          // - two perception radiuses
          // - an object and its own perception radius
          if (activeIB.getObject().equals(ib.getObject())
              || (activeIB.getCollisionObjectType().equals(ib
                  .getCollisionObjectType()))) {
            continue;
          }

          // perception
          if ((activeIB.getCollisionObjectType().equals(
              CollisionObjectType.PERCEPTION)) && 
              (!ib.getObject().getPhysicsType().equals(PhysicsType.PHANTOM))) {
            Perception2DGrid perception = new Perception2DGrid(
                (PhysicalAgentObject) activeIB.getObject(), ib.getObject(),
                spaceModel);
            xPerceptions.put(perception, 1);
            continue;
          }

          if ((ib.getCollisionObjectType()
              .equals(CollisionObjectType.PERCEPTION)) && 
              (!activeIB.getObject().getPhysicsType().equals(PhysicsType.PHANTOM))) {
            Perception2DGrid perception = new Perception2DGrid((PhysicalAgentObject) ib
                .getObject(), activeIB.getObject(), spaceModel);
            xPerceptions.put(perception, 1);
            continue;
          }
        }

        activeList.add(ib);
      } else {
        old = ib.getPoint();
        oldList.add(ib.getOther());
      }
    }
    
    // If a start point is found, save the corresponding object together with
    // the perception flag in the activeList. Every other object on the
    // activeList is colliding with the object belonging to the start point. If
    // an end point is found, the point is saved in the oldList and removed from
    // both lists once a start point with a different value is found.

    activeList = new ArrayList<IntervalBound>();
    oldList = new ArrayList<IntervalBound>();

    old = null; // value of points in old list

    for (IntervalBound ib : yList) {
      // if the points coordinates differ from the ones in the oldList,
      // remove all objects in the oldList from the activeList
      if (old != null && old != ib.getPoint()) {
        activeList.removeAll(oldList);
        oldList.clear();
        old = null;
      }

      if (ib.getType().equals(BoundType.START)) {

        for (IntervalBound activeIB : activeList) {
          // leave out collisions between:
          // - two objects (no collision detection)
          // - two perception radiuses
          // - an object and its own perception radius
          if (activeIB.getObject().equals(ib.getObject())
              || (activeIB.getCollisionObjectType().equals(ib
                  .getCollisionObjectType()))) {
            continue;
          }

          // perception
          if ((activeIB.getCollisionObjectType().equals(
              CollisionObjectType.PERCEPTION)) && 
              (!ib.getObject().getPhysicsType().equals(PhysicsType.PHANTOM))) {
            Perception2DGrid perception = new Perception2DGrid(
                (PhysicalAgentObject) activeIB.getObject(), ib.getObject(),
                spaceModel);

            if (xPerceptions.containsKey(perception)) {
              perceptionList.add(perception);
            }
            
            continue;
          }

          if ((ib.getCollisionObjectType()
              .equals(CollisionObjectType.PERCEPTION)) && 
              (!activeIB.getObject().getPhysicsType().equals(PhysicsType.PHANTOM))) {
            Perception2DGrid perception = new Perception2DGrid((PhysicalAgentObject) ib
                .getObject(), activeIB.getObject(), spaceModel);

            if (xPerceptions.containsKey(perception)) {
              perceptionList.add(perception);
            }

            continue;
          }
        }

        activeList.add(ib);
      } else {
        old = ib.getPoint();
        oldList.add(ib.getOther());
      }
    }

  }

}
