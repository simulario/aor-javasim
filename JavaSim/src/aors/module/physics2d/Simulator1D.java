/**
 * 
 */
package aors.module.physics2d;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import aors.GeneralSpaceModel;
import aors.GeneralSpaceModel.Geometry;
import aors.data.DataBus;
import aors.data.java.ObjektDestroyEvent;
import aors.data.java.ObjektInitEvent;
import aors.data.java.SimulationStepEvent;
import aors.logger.model.SimulationParameters;
import aors.model.envevt.CollisionEvent;
import aors.model.envevt.PhysicalObjectPerceptionEvent;
import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.envsim.PhysicalObject;
import aors.module.physics2d.util.Collision1D;
import aors.module.physics2d.util.IntervalList;
import aors.module.physics2d.util.MaterialConstants;
import aors.module.physics2d.util.Perception;
import aors.module.physics2d.util.UnitConverter;
import aors.space.Space;

/**
 * A physics simulator for 1D simulation.
 * 
 * @author Holger Wuerke
 * @since 12.12.2009
 * 
 */
public class Simulator1D extends PhysicsSimulator {
  
  /**
   * A unit converter.
   */
  private UnitConverter unitConverter;

  /**
   * Used for collision detection. Every lane gets its own list.
   */
  private List<IntervalList> intervalLists;

  /**
   * Stores the current lane of each object.
   */
  private Map<Physical, Double> currentLanes;

  /**
   * This map stores the calculated new positions of all objects. This is needed
   * because positions might be changed multiple times due to collision that may
   * occur.
   */
  private Map<Physical, Double> newPositions;

  /**
   * The maximum number of iterations that the collision detection algorithm
   * will use.
   */
  private int maxIterations = 10;

  /**
   * A threshold for certain values in the calculation of collisions. Needed to
   * avoid bad behavior due to calculation errors.
   */
  private final double calculationThreshold = 1e-10;

  /**
   * Creates a new simulator.
   * 
   * @param simParams
   * @param spaceModel
   * @param autoKinematics
   * @param autoCollisionDetection
   * @param autoCollisionHandling
   * @param gravitation
   * @param databus
   * @param objects
   * @param agents
   */
  public Simulator1D(SimulationParameters simParams,
      GeneralSpaceModel spaceModel, boolean autoKinematics,
      boolean autoCollisionDetection, boolean autoCollisionHandling,
      double gravitation, DataBus databus, List<PhysicalObject> objects,
      List<PhysicalAgentObject> agents) {

    super(simParams, spaceModel, autoKinematics, autoCollisionDetection,
        autoCollisionHandling, gravitation, databus, objects, agents);

    unitConverter = new UnitConverter(simParams.getTimeUnit(), spaceModel.getSpatialDistanceUnit());
    stepDuration = unitConverter.timeToSeconds(simParams.getStepDuration());
    
    newPositions = new HashMap<Physical, Double>();
    currentLanes = new HashMap<Physical, Double>();

    intervalLists = new ArrayList<IntervalList>();

    // put all objects in separate interval lists for each lane, then initialize
    // each interval list
    if (spaceModel.getMultiplicity() == 1) {
      for (Physical object : getPhysicals()) {
        currentLanes.put(object, object.getY());
      }
      intervalLists.add(new IntervalList(getPhysicals(), spaceModel));
    } else {
      ArrayList<ArrayList<Physical>> objLists = new ArrayList<ArrayList<Physical>>();
      for (int i = 0; i < spaceModel.getMultiplicity(); i++) {
        objLists.add(new ArrayList<Physical>());
      }

      for (Physical object : getPhysicals()) {
        currentLanes.put(object, object.getY());
        objLists.get((int) object.getY() - Space.ORDINATEBASE).add(object);
      }

      for (int i = 0; i < spaceModel.getMultiplicity(); i++) {
        intervalLists.add(new IntervalList(objLists.get(i), spaceModel));
      }
    }

    // multiple iterations only needed for autoCollisionHandling
    if (!autoCollisionHandling) {
      maxIterations = 1;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.module.physics2d.PhysicsSimulator#simulationStarted()
   */
  @Override
  public void simulationStarted() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.module.physics2d.PhysicsSimulator#simulationStepEnd()
   */
  @Override
  public void simulationStepEnd(SimulationStepEvent simulationStepEvent) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.module.physics2d.PhysicsSimulator#simulationStepStart(long)
   */
  @Override
  public void simulationStepStart(long stepNumber) {
    this.stepNumber = stepNumber;

    // check for lane changes
    checkLanes();

    // calculate new positions
    if (autoKinematics) {
      determineNewVelocities();
      determineNewPositions();
    } else {
      setPositions();
    }

    // collision detection and handling
    Set<Perception> perceptions = new HashSet<Perception>();
    Set<Physical> borderReached = new HashSet<Physical>();

    int i = 1;

    do {
      List<Collision1D> collisions = new ArrayList<Collision1D>();

      for (IntervalList intervalList : intervalLists) {
        intervalList.update(newPositions);
        intervalList.detectCollisions(collisions, perceptions, borderReached);
      }

      if (spaceModel.getGeometry().equals(Geometry.Euclidean)) {
        handleBorderContact(borderReached);
      }

      if (autoCollisionHandling) {
        preprocessCollisions(collisions);
      }

      if (collisions.size() == 0) {
        break;
      }

      if (i < maxIterations) {
        List<Collision1D> cList = new ArrayList<Collision1D>();
        cList.add(collisions.get(0));
        processCollisions(cList);
      } else {
        processCollisions(collisions);
        break;
      }

      i++;
    } while (true);

    // update positions
    updatePositions();
    newPositions.clear();

    processPerceptions(perceptions);
    sendEvents(stepNumber);
  }

  /**
   * Set the {@code newPositions} with values of the objects (used in
   * autoKinematics off mode).
   */
  private void setPositions() {
    for (Physical object : getPhysicals()) {
      newPositions.put(object, object.getX());
    }
  }

  /**
   * Updates the positions of all objects and agents.
   */
  private void updatePositions() {
    for (Entry<Physical, Double> entry : newPositions.entrySet()) {
      double newPos = entry.getValue();

      // check if objects are out of bounds
      if (spaceModel.getGeometry().equals(Geometry.Euclidean)) {
        // euclidean
        if (newPos < 0) {
          newPos = 0;
          entry.getKey().setVx(0);
          entry.getKey().setAx(0);
        }

        if (newPos > spaceModel.getXMax()) {
          newPos = spaceModel.getXMax();
          entry.getKey().setVx(0);
          entry.getKey().setAx(0);
        }
      } else {
        // toroidal
        if (newPos < 0) {
          newPos += spaceModel.getXMax();
        }

        if (newPos > spaceModel.getXMax()) {
          newPos %= spaceModel.getXMax();
        }
      }

      entry.getKey().setX(newPos);
      // System.out.println(entry.getKey().getId() + ") " +
      // entry.getKey().getX()
      // + " " + entry.getKey().getVx());
    }
  }

  /**
   * Determines the new velocities of all objects and agents based on their
   * acceleration.
   */
  private void determineNewVelocities() {
    for (Physical object : getPhysicals()) {
      double v = object.getVx() + object.getAx() * stepDuration;
      object.setVx(v);
    }
  }

  /**
   * Determines the new position of all objects and agents based on their
   * velocity.
   */
  private void determineNewPositions() {

    for (Physical object : getPhysicals()) {
      double pos = object.getX() + unitConverter.distanceToUser(object.getVx() * stepDuration);

      // toroidal: adjust values if out of bounds
      if (spaceModel.getGeometry().equals(Geometry.Toroidal)) {
        if (pos < 0) {
          pos += spaceModel.getXMax();
        }

        if (pos >= spaceModel.getXMax()) {
          pos %= spaceModel.getXMax();
        }
      }

      newPositions.put(object, pos);
    }
  }

  /**
   * Calculates new velocities and positions of involving objects if
   * autoCollisionHandling is on. Creates collision events if
   * autoCollisionDetection is on.
   * 
   * @param collisions
   *          list of collisions to process
   */
  private void processCollisions(List<Collision1D> collisions) {
    for (Collision1D collision : collisions) {
      Physical object1 = collision.getObject1();
      Physical object2 = collision.getObject2();

      if (autoCollisionDetection) {
        // create event
        CollisionEvent event = new CollisionEvent(stepNumber);
        event.setPhysicalObject1(object1);
        event.setPhysicalObject2(object2);
        events.add(event);
      }

      if (autoCollisionHandling) {
        double m1 = object1.getM();
        double m2 = object2.getM();
        double v1 = object1.getVx();
        double v2 = object2.getVx();

        double restitution = Math.min(MaterialConstants.restitution(object1
            .getMaterialType()), MaterialConstants.restitution(object2
            .getMaterialType()));

        // calculate new velocities and new positions
        double newV1 = (m1 * v1 + m2 * v2 - m2 * (v1 - v2) * restitution)
            / (m1 + m2);
        double newV2 = (m1 * v1 + m2 * v2 - m1 * (v2 - v1) * restitution)
            / (m1 + m2);

        double newPos1 = collision.getPosition() - object1.getWidth() / 2
            + unitConverter.distanceToUser(newV1 * (stepDuration - collision.getTime()));
        double newPos2 = collision.getPosition() + object2.getWidth() / 2
            + unitConverter.distanceToUser(newV2 * (stepDuration - collision.getTime()));

        // System.out.println(stepNumber + ": Time: " + collision.getTime()
        // + " Position: " + collision.getPosition());
        // System.out.println("Old Velocities: " + object1.getVx() + " | "
        // + object2.getVx());
        // System.out.println("Old Positions: " + newPositions.get(object1)
        // + " | " + newPositions.get(object2));
        // System.out.println("New Velocities: " + newV1 + " | " + newV2);
        // System.out.println("New Positions: " + newPos1 + " | " + newPos2);

        // set new velocities and new positions
        object1.setVx(newV1);
        object2.setVx(newV2);

        newPositions.put(object1, newPos1);
        newPositions.put(object2, newPos2);

      }
    }
  }

  /**
   * Calculates the exact position and time of all collisions and sorts the list
   * hereafter by the time the collisions occur.
   * 
   * @param collisions
   */
  private void preprocessCollisions(List<Collision1D> collisions) {
    for (Iterator<Collision1D> it = collisions.iterator(); it.hasNext();) {
      Collision1D collision = it.next();
      Physical object1 = collision.getObject1();
      Physical object2 = collision.getObject2();

      // relative (enclosing) velocity
      double vRel = Math.abs(object1.getVx() - object2.getVx());

      // if velocity below threshold, do nothing
      if (vRel < calculationThreshold) {
        it.remove();
        continue;
      }

      // calculate (negative) distance after collision
      double distance = (newPositions.get(object2) - object2.getWidth() / 2)
          - (newPositions.get(object1) + object1.getWidth() / 2);

      // special case for toroidal space
      if (-distance > (object1.getWidth() + object2.getWidth())) {
        distance += spaceModel.getXMax();
      }

      // if distance below threshold, do nothing
      if (-distance < calculationThreshold) {
        it.remove();
        continue;
      }

      // calculate time of impact
      double time = (vRel == 0) ? 0 : stepDuration + (unitConverter.distanceToMeters(distance) / vRel);

      // calculate position of impact
      double position = newPositions.get(object1) + object1.getWidth() / 2;
      position = position - unitConverter.distanceToUser(object1.getVx() * (stepDuration - time));

      // adjust position if out of bounds
      if (spaceModel.getGeometry().equals(Geometry.Toroidal)) {
        if (position < 0) {
          position += spaceModel.getXMax();
        }

        if (position >= spaceModel.getXMax()) {
          position %= spaceModel.getXMax();
        }
      }

      collision.setPosition(position);
      collision.setTime(time);
    }

    Collections.sort(collisions);
  }

  /**
   * Creates perception events.
   * 
   * @param perceptions
   *          set of perceptions to process
   */
  private void processPerceptions(Set<Perception> perceptions) {
    for (Perception perception : perceptions) {
      double distance = perception.getDistance1D();
      double angle = perception.getAngle1D();

      PhysicalObjectPerceptionEvent event = new PhysicalObjectPerceptionEvent(
          stepNumber, perception.getPerceiver().getId(), perception
              .getPerceived().getClass().getSimpleName(), distance);
      event.setPerceivedPhysicalObject(perception.getPerceived());
      event.setPerceptionAngle(angle);

      try {
        Field idPerceivableField = perception.getPerceived().getClass()
            .getDeclaredField("ID_PERCEIVABLE");

        if (idPerceivableField.getBoolean(perception.getPerceived())) {
          event.setPerceivedPhysicalObjectIdRef(perception.getPerceived()
              .getId());
        }
      } catch (Exception e) {
      }

      events.add(event);

      // System.out.println(stepNumber + ": " + perception);
    }
  }

  /**
   * Take actions for objects that have reached the space border. Used in
   * euclidean space to stop these objects.
   * 
   * @param borderReached
   */
  private void handleBorderContact(Set<Physical> borderReached) {
    for (Physical object : borderReached) {
      object.setVx(0);
      object.setAx(0);
    }
  }

  /**
   * Checks if objects have changed their lane.
   */
  private void checkLanes() {
    for (Physical object : getPhysicals()) {
      if (object.getY() != currentLanes.get(object)) {
        intervalLists
            .get((int) (currentLanes.get(object) - Space.ORDINATEBASE)).remove(
                object);
        intervalLists.get((int) (object.getY() - Space.ORDINATEBASE)).add(
            object);
        currentLanes.put(object, object.getY());
      }
    }
  }

  @Override
  public void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent) {
    if (objektDestroyEvent.getSource() instanceof Physical) {
      Physical object = (Physical) (objektDestroyEvent.getSource());

      if (spaceModel.getMultiplicity() == 1) {
        intervalLists.get(0).remove(object);
      } else {
        intervalLists.get((int) (object.getY() - Space.ORDINATEBASE)).remove(
            object);
      }
    }
  }

  @Override
  public void objektInitEvent(ObjektInitEvent objInitEvent) {
    if (objInitEvent.getSource() instanceof Physical) {
      Physical object = (Physical) (objInitEvent.getSource());

      if (spaceModel.getMultiplicity() == 1) {
        intervalLists.get(0).add(object);
      } else {
        intervalLists.get((int) (object.getY() - Space.ORDINATEBASE)).add(
            object);
      }
    }
  }

}
