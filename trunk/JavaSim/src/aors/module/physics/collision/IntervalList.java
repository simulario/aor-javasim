/**
 * 
 */
package aors.module.physics.collision;

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
import aors.model.envsim.Physical.PhysicsType;
import aors.module.physics.collision.IntervalBound.BoundType;


/**
 * A list of interval bounds used for 1D collision detection. An interval
 * represents the space a physical object covers. The start and end point of
 * each interval are kept in the list.
 * 
 * @author Holger Wuerke
 * 
 */
public class IntervalList {

  private List<IntervalBound> list = new ArrayList<IntervalBound>();

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
      IntervalBound startpoint = new IntervalBound();
      startpoint.setType(BoundType.START);
      startpoint.setPoint(-1);
      startpoint.setObject(null);
      startpoint.setCollisionObjectType(CollisionObjectType.BORDER);

      IntervalBound endpoint = new IntervalBound();
      endpoint.setType(BoundType.END);
      endpoint.setPoint(0);
      endpoint.setObject(null);
      endpoint.setCollisionObjectType(CollisionObjectType.BORDER);

      startpoint.setOther(endpoint);
      endpoint.setOther(startpoint);

      list.add(startpoint);
      list.add(endpoint);

      startpoint = new IntervalBound();
      startpoint.setType(BoundType.START);
      startpoint.setPoint(spaceModel.getXMax());
      startpoint.setObject(null);
      startpoint.setCollisionObjectType(CollisionObjectType.BORDER);

      endpoint = new IntervalBound();
      endpoint.setType(BoundType.END);
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
   * "sort-and-sweep" algorithm is used here to determine overlapping
   * intervals.
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
      Set<Perception1D> perceptions, Set<Physical> borderReached) {
    // If a start point is found, save it in the activeList. Every other object
    // belonging to a point on the activeList is colliding with the object
    // belonging to the start point. If an end point is found, the corresponding
    // object is removed from the activeList.

    List<IntervalBound> activeList = new ArrayList<IntervalBound>();

    for (IntervalBound ib : list) {
      if (ib.getType().equals(BoundType.START)) {
        for (IntervalBound activeIB : activeList) {
          // filter out collisions between:
          // - an object and its own perception radius
          // - two perception radiuses
          // - a perception radius and the space border
          if (activeIB.getObject() != null
              && activeIB.getObject().equals(ib.getObject())
              || (activeIB.getCollisionObjectType().equals(
                  CollisionObjectType.PERCEPTION) && ib
                  .getCollisionObjectType().equals(
                      CollisionObjectType.PERCEPTION))
              || ((activeIB.getCollisionObjectType()
                  .equals(CollisionObjectType.BORDER)) && ib
                  .getCollisionObjectType().equals(
                      CollisionObjectType.PERCEPTION))
              || (activeIB.getCollisionObjectType().equals(
                  CollisionObjectType.PERCEPTION) && (ib
                  .getCollisionObjectType().equals(CollisionObjectType.BORDER)))
              || (activeIB.getCollisionObjectType().equals(
                  CollisionObjectType.BORDER) && (ib.getCollisionObjectType()
                  .equals(CollisionObjectType.BORDER)))) {
            continue;
          }

          // perception
          if ((activeIB.getCollisionObjectType().equals(
              CollisionObjectType.PERCEPTION)) && 
              (!ib.getObject().getPhysicsType().equals(PhysicsType.PHANTOM))) {
            Perception1D perception = new Perception1D(
                (PhysicalAgentObject) activeIB.getObject(), ib.getObject(),
                spaceModel);
            perceptions.add(perception);
            continue;
          }

          if ((ib.getCollisionObjectType()
              .equals(CollisionObjectType.PERCEPTION)) && 
              (!activeIB.getObject().getPhysicsType().equals(PhysicsType.PHANTOM))) {
            Perception1D perception = new Perception1D((PhysicalAgentObject) ib
                .getObject(), activeIB.getObject(), spaceModel);
            perceptions.add(perception);
            continue;
          }

          // space border reached
          if (activeIB.getCollisionObjectType().equals(
              CollisionObjectType.BORDER)) {
            borderReached.add(ib.getObject());
            continue;
          }

          if (ib.getCollisionObjectType().equals(CollisionObjectType.BORDER)) {
            borderReached.add(activeIB.getObject());
            continue;
          }

          // collision
          // if one is IMMATERIAL or PHANTOM, no collision
          if ((ib.getObject().getPhysicsType().equals(PhysicsType.IMMATERIAL)) ||
            (activeIB.getObject().getPhysicsType().equals(PhysicsType.IMMATERIAL)) ||
            (ib.getObject().getPhysicsType().equals(PhysicsType.PHANTOM)) ||
            (activeIB.getObject().getPhysicsType().equals(PhysicsType.PHANTOM))) {
            continue;
          }
          
          // collision
          Collision1D collision = new Collision1D(ib.getObject(), activeIB
              .getObject());
          collisionList.add(collision);
        }

        activeList.add(ib);
      } else {
        activeList.remove(ib.getOther());
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

    List<IntervalBound> newList = new ArrayList<IntervalBound>();

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

    IntervalBound startpoint = new IntervalBound();
    startpoint.setType(BoundType.START);
    startpoint.setPoint(start);
    startpoint.setObject(object);
    startpoint.setCollisionObjectType(CollisionObjectType.OBJECT);

    IntervalBound endpoint = new IntervalBound();
    endpoint.setType(BoundType.END);
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

          startpoint = new IntervalBound();
          startpoint.setType(BoundType.START);
          startpoint.setPoint(start);
          startpoint.setObject(object);
          startpoint.setCollisionObjectType(CollisionObjectType.PERCEPTION);

          endpoint = new IntervalBound();
          endpoint.setType(BoundType.END);
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
    for (Iterator<IntervalBound> it = list.iterator(); it.hasNext();) {
      IntervalBound ie = it.next();
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
    for (IntervalBound ie : list) {
      Physical object = ie.getObject();

      if (object == null) {
        continue;
      }

      double width = object.getWidth();

      if (ie.getType().equals(BoundType.START)) {
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
