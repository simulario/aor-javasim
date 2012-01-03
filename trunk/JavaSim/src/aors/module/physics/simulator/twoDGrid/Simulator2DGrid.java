/**
 * 
 */
package aors.module.physics.simulator.twoDGrid;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import aors.GeneralSpaceModel;
import aors.GeneralSpaceModel.Geometry;
import aors.data.DataBus;
import aors.data.evt.sim.ObjektDestroyEvent;
import aors.data.evt.sim.ObjektInitEvent;
import aors.data.evt.sim.SimulationStepEvent;
import aors.logger.model.SimulationParameters;
import aors.model.envevt.PhysicalObjectPerceptionEvent;
import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.envsim.PhysicalObject;
import aors.module.physics.simulator.PhysicsSimulator;
import aors.module.physics.collision.IntervalList2DGrid;
import aors.module.physics.collision.Perception2DGrid;
import aors.space.Space;

/**
 * A physics simulator for 2D grid space.
 * 
 * @author Holger Wuerke
 */
public class Simulator2DGrid extends PhysicsSimulator {

  private IntervalList2DGrid intervalList;

  private Map<Physical, Double> xPositions = new HashMap<Physical, Double>();
  private Map<Physical, Double> yPositions = new HashMap<Physical, Double>();

  /**
   * @param simParams
   * @param spaceModel
   * @param simModel
   * @param databus
   * @param objects
   * @param agents
   */
  public Simulator2DGrid(SimulationParameters simParams,
      GeneralSpaceModel spaceModel, boolean autoKinematics,
      boolean autoCollisionDetection, boolean autoCollisionHandling,
      double gravitation, DataBus databus, List<PhysicalObject> objects,
      List<PhysicalAgentObject> agents) {

    super(simParams, spaceModel, autoKinematics, autoCollisionDetection,
        autoCollisionHandling, gravitation, databus, objects, agents);

//    if (simParams.getStepDuration() != null) {
//      stepDuration = simParams.getStepDuration();
//    } else {
      stepDuration = 1;  
//    }
    

    intervalList = new IntervalList2DGrid(getPhysicals(), spaceModel);

    for (Physical object : getPhysicals()) {
      xPositions.put(object, object.getX());
      yPositions.put(object, object.getY());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * aors.module.physics2d.PhysicsSimulator#simulationStepEnd(aors.data.java
   * .SimulationStepEvent)
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

    if (autoKinematics) {
      determineNewVelocities();
      determineNewPositions();
      updatePositions();
    }
    
    intervalList.update();
    
    Set<Perception2DGrid> perceptions = new HashSet<Perception2DGrid>();
    
    intervalList.detectPerceptions(perceptions);
    processPerceptions(perceptions);

    sendEvents(stepNumber);
  }

  /**
   * Updates the positions of all objects and agents.
   */
  private void updatePositions() {
    for (Physical object : getPhysicals()) {
      double currentX = xPositions.get(object);
      double currentY = yPositions.get(object);

      double deltaX = Math.floor(Math.abs(currentX - object.getX()));
      double deltaY = Math.floor(Math.abs(currentY - object.getY()));

      double newX = (currentX > object.getX()) ? object.getX() + deltaX
          : object.getX() - deltaX;
      double newY = (currentY > object.getY()) ? object.getY() + deltaY
          : object.getY() - deltaY;

      // check if objects are out of bounds
      if (spaceModel.getGeometry().equals(Geometry.Euclidean)) {
        // euclidean: don't let objects move outside
        if (newX < Space.ORDINATEBASE) {
          newX = Space.ORDINATEBASE;
          xPositions.put(object, new Double(Space.ORDINATEBASE));
//          object.setVx(0);
//          object.setAx(0);
        }

        if (newX > (spaceModel.getXMax() - 1 + Space.ORDINATEBASE)) {
          newX = spaceModel.getXMax() - 1 + Space.ORDINATEBASE;
          xPositions.put(object, new Double(spaceModel.getXMax() - 1
              + Space.ORDINATEBASE));
//          object.setVx(0);
//          object.setAx(0);
        }

        if (newY < Space.ORDINATEBASE) {
          newY = Space.ORDINATEBASE;
          yPositions.put(object, new Double(Space.ORDINATEBASE));
//          object.setVy(0);
//          object.setAy(0);
        }

        if (newY > (spaceModel.getYMax() - 1 + Space.ORDINATEBASE)) {
          newY = spaceModel.getYMax() - 1 + Space.ORDINATEBASE;
          yPositions.put(object, new Double(spaceModel.getYMax() - 1
              + Space.ORDINATEBASE));
//          object.setVy(0);
//          object.setAy(0);
        }
      } else {
        // toroidal: adjust values
        if (newX < Space.ORDINATEBASE) {
          newX += spaceModel.getXMax();
          xPositions.put(object, xPositions.get(object) + spaceModel.getXMax());
        }

        if (newX > (spaceModel.getXMax() - 1 + Space.ORDINATEBASE)) {
          newX %= spaceModel.getXMax();
          xPositions.put(object, xPositions.get(object) % spaceModel.getXMax());
        }

        if (newY < Space.ORDINATEBASE) {
          newY += spaceModel.getYMax();
          yPositions.put(object, yPositions.get(object) + spaceModel.getYMax());
        }

        if (newY > (spaceModel.getYMax() - 1 + Space.ORDINATEBASE)) {
          newY %= spaceModel.getYMax();
          yPositions.put(object, yPositions.get(object) % spaceModel.getYMax());
        }
      }

      object.setX(newX);
      object.setY(newY);
      
//       System.out.println(object.getId() + ") " + object.getX() + ","
//       + object.getY());
    }
  }

  /**
   * Determines the new velocities of all objects and agents based on their
   * acceleration.
   */
  private void determineNewVelocities() {
    for (Physical object : getPhysicals()) {
      double vx = object.getVx() + object.getAx() * stepDuration;
      double vy = object.getVy() + object.getAy() * stepDuration;
      object.setVx(vx);
      object.setVy(vy);
    }
  }

  /**
   * Determines the new position of all objects and agents.
   */
  private void determineNewPositions() {
    for (Physical object : getPhysicals()) {
      double x = xPositions.get(object) + object.getVx() * stepDuration;
      double y = yPositions.get(object) + object.getVy() * stepDuration;
      xPositions.put(object, x);
      yPositions.put(object, y);
    }
  }

  /**
   * Creates collision events.
   * 
   * @param collisions
   *          a list of collisions
   */
 /* private void processCollisions(List<Collision1D> collisions) {
    for (Collision1D collision : collisions) {
      Physical object1 = collision.getObject1();
      Physical object2 = collision.getObject2();

      CollisionEvent event = new CollisionEvent(stepNumber);
      event.setPhysicalObject1(object1);
      event.setPhysicalObject2(object2);
      events.add(event);

      // System.out.println(stepNumber + ": " + collision);
    }

  }*/

  /**
   * Creates perception events.
   * 
   * @param perceptions
   *          list of perceptions to process
   */
  private void processPerceptions(Set<Perception2DGrid> perceptions) {
    for (Perception2DGrid perception : perceptions) {
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

//       System.out.println(perception);
    }
  }

  @Override
  public void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent) {
    if (objektDestroyEvent.getSource() instanceof Physical) {
      Physical object = (Physical) (objektDestroyEvent.getSource()); 
      xPositions.remove(object);
      yPositions.remove(object);
      intervalList.remove(object);
    }
  }

  @Override
  public void objektInitEvent(ObjektInitEvent objInitEvent) {
    if (objInitEvent.getSource() instanceof Physical) {
      Physical object = (Physical) (objInitEvent.getSource()); 
      xPositions.put(object, object.getX());
      yPositions.put(object, object.getY());
      intervalList.add(object);
    }
  }

}
