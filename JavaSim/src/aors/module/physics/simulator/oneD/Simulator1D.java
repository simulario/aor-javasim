/**
 * 
 */
package aors.module.physics.simulator.oneD;

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
import aors.data.evt.sim.ObjektDestroyEvent;
import aors.data.evt.sim.ObjektInitEvent;
import aors.data.evt.sim.SimulationStepEvent;
import aors.logger.model.SimulationParameters;
import aors.model.envevt.CollisionEvent;
import aors.model.envevt.PhysicalObjectPerceptionEvent;
import aors.model.envsim.Physical;
import aors.model.envsim.Physical.PhysicsType;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.envsim.PhysicalObject;
import aors.module.physics.PhysicsSimulator;
import aors.module.physics.collision.Collision1D;
import aors.module.physics.collision.IntervalList;
import aors.module.physics.collision.Perception1D;
import aors.module.physics.util.MaterialConstants;

/**
 * A physics simulator for 1D simulation.
 * 
 * @author Holger Wuerke
 * 
 */
public class Simulator1D extends PhysicsSimulator {

  /**
   * Used for collision detection.
   */
  private IntervalList intervalList;

  /**
   * This map stores the calculated new positions of all objects. This is needed
   * because positions might be changed multiple times within a step due to
   * collisions that may occur.
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

    newPositions = new HashMap<Physical, Double>();
    intervalList = new IntervalList(getPhysicals(), spaceModel);

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

    // calculate new positions
    if (autoKinematics) {
      determineNewVelocities();
      determineNewPositions();
    } else {
      setPositions();
    }

    // collision detection and handling
    Set<Perception1D> perceptions = new HashSet<Perception1D>();
    Set<Physical> borderReached = new HashSet<Physical>();

    int i = 1;

    do {
      List<Collision1D> collisions = new ArrayList<Collision1D>();

      intervalList.update(newPositions);
      intervalList.detectCollisions(collisions, perceptions, borderReached);

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
   * Set the {@code newPositions} with values of the objects (used when
   * autoKinematics are turned off).
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
  
//       System.out.println(entry.getKey().getId() + ") " +
//       entry.getKey().getX()
//       + " " + entry.getKey().getY());
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
      double pos = object.getX()
          + unitConverter.distanceToUser(object.getVx() * stepDuration);

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

      // skip objects on different lanes
      if (object1.getY() != object2.getY()) {
        continue;
      }

      // System.out.println(stepNumber + ": " + collision);

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

        double restitution = Math.min(
            MaterialConstants.restitution(object1.getMaterialType()),
            MaterialConstants.restitution(object2.getMaterialType()));

        // calculate new velocities and new positions
        double newV1 = v1;
        double newV2 = v2;
        double newPos1 = object1.getX();
        double newPos2 = object2.getX();
        if (object1.getPhysicsType().equals(PhysicsType.INFINITE_MASS)) {
          newV2 = - v2 * restitution;
          newPos2 = collision.getPosition()
          + object2.getWidth() / 2
          + unitConverter.distanceToUser(newV2
              * (stepDuration - collision.getTime()));          
        } 

        if (object2.getPhysicsType().equals(PhysicsType.INFINITE_MASS)) {
          newV1 = -v1 * restitution;
          newPos1 = collision.getPosition()
          - object1.getWidth() / 2
          + unitConverter.distanceToUser(newV1
              * (stepDuration - collision.getTime()));
        }

        if ((!object1.getPhysicsType().equals(PhysicsType.INFINITE_MASS)) && 
            (!object2.getPhysicsType().equals(PhysicsType.INFINITE_MASS))) {
          newV1 = (m1 * v1 + m2 * v2 - m2 * (v1 - v2) * restitution)
              / (m1 + m2);
          newV2 = (m1 * v1 + m2 * v2 - m1 * (v2 - v1) * restitution)
              / (m1 + m2);
  
          newPos1 = collision.getPosition()
              - object1.getWidth() / 2
              + unitConverter.distanceToUser(newV1
                  * (stepDuration - collision.getTime()));
          newPos2 = collision.getPosition()
              + object2.getWidth() / 2
              + unitConverter.distanceToUser(newV2
                  * (stepDuration - collision.getTime()));
        }

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

      // if objects are on different lanes, there is no collision
      if (object1.getY() != object2.getY()) {
        it.remove();
        continue;
      }

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
      double time = (vRel == 0) ? 0 : stepDuration
          + (unitConverter.distanceToMeters(distance) / vRel);

      // calculate position of impact
      double position = newPositions.get(object1) + object1.getWidth() / 2;
      position = position
          - unitConverter.distanceToUser(object1.getVx()
              * (stepDuration - time));

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
  private void processPerceptions(Set<Perception1D> perceptions) {
    for (Perception1D perception : perceptions) {
      double distance = perception.getDistance();
      double angle = perception.getAngleInDegrees();

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

//       System.out.println(stepNumber + ": " + perception);
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

  @Override
  public void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent) {
    if (objektDestroyEvent.getSource() instanceof Physical) {
      Physical object = (Physical) (objektDestroyEvent.getSource());
      intervalList.remove(object);
    }
  }

  @Override
  public void objektInitEvent(ObjektInitEvent objInitEvent) {
    if (objInitEvent.getSource() instanceof Physical) {
      Physical object = (Physical) (objInitEvent.getSource());
      intervalList.add(object);
    }
  }

}
