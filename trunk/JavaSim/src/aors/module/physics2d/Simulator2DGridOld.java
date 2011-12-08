/**
 * 
 */
package aors.module.physics2d;

import java.lang.reflect.Field;
import java.util.List;

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
import aors.space.Space;

/**
 * A physics simulator for 2D grid space.
 * 
 * @author Holger Wuerke
 */
public class Simulator2DGridOld extends PhysicsSimulator {

  /**
   * fields used in computation of the distance of the current perceiver and
   * perceived physicals
   */
  double distanceX = -1;
  double distanceY = -1;

  /**
   * @param simParams
   *          the simulation parameters object
   * @param spaceModel
   *          the space model object
   * @param simModel
   *          the simulation model object
   * @param databus
   *          the databus reference
   * @param objects
   *          the physical objects list
   * @param agents
   *          the physical agents list
   */
  public Simulator2DGridOld(SimulationParameters simParams,
      GeneralSpaceModel spaceModel, boolean autoKinematics,
      boolean autoCollisionDetection, boolean autoCollisionHandling,
      double gravitation, DataBus databus, List<PhysicalObject> objects,
      List<PhysicalAgentObject> agents) {

    // call super initialization
    super(simParams, spaceModel, autoKinematics, autoCollisionDetection,
        autoCollisionHandling, gravitation, databus, objects, agents);
  }

  @Override
  public void simulationStarted() {
    // nothing to do
  }

  @Override
  public void simulationStepEnd(SimulationStepEvent simulationStepEvent) {
    // nothing to do
  }

  @Override
  public void simulationStepStart(long stepNumber) {
    this.stepNumber = stepNumber;

    // auto-kinematics is activated
    if (autoKinematics) {
      updateVelocityAndPositions();
    }

    // determine and add to the list the perceptions fort this step;
    processStepPerceptions();

    // send events to the simulator core
    sendEvents(stepNumber);
  }

  /**
   * Determines the new velocities of all objects and agents based on their
   * acceleration.
   */
  private void updateVelocityAndPositions() {

    Geometry spaceGeometry = spaceModel.getGeometry();
    long xMin = Space.ORDINATEBASE;
    long realXMax = spaceModel.getXMax();
    long xMax = realXMax - (1 - Space.ORDINATEBASE);

    long yMin = Space.ORDINATEBASE;
    long realYMax = spaceModel.getYMax();
    long yMax = realYMax - (1 - Space.ORDINATEBASE);

    for (Physical object : getPhysicals()) {
      double vx = object.getVx() + object.getAx() * stepDuration;
      double vy = object.getVy() + object.getAy() * stepDuration;
      object.setVx(vx);
      object.setVy(vy);

      double v = object.getV().getLength();
      long x = Math.round(object.getX() + v * stepDuration);
      long y = Math.round(object.getY() + v * stepDuration);

      if (spaceGeometry.equals(Geometry.Toroidal)) {

        // adjust X so it cycle in the space
        x = (x > xMax ? x % realXMax + xMin : x);
        x = (x < xMin ? x % realXMax + xMax : x);

        // adjust Y so it cycle in the space
        y = (y > yMax ? y % realYMax + yMin : y);
        y = (y < yMin ? y % realYMax + yMax : y);

      } else if (spaceGeometry.equals(Geometry.Euclidean)) {

        // keep X in space limits
        x = (x < xMin ? xMin : x);
        x = (x > xMax ? xMax : x);

        // keep Y in space limits
        x = (y < yMin ? yMin : y);
        y = (y > yMax ? yMax : y);

      }

      // set x and y values of the object.
      object.setX(x);
      object.setY(y);
    }
  }

  /**
   * Determine perception of agents for the current step and add them to the the
   * events list.
   */
  private void processStepPerceptions() {

    // determine all perception for agents
    int agtNumber = physicalAgentObjects.size();
    int objNumber = physicalObjects.size();

    PhysicalAgentObject perceiver = null;
    Physical physical = null;
    for (int i = 0; i < agtNumber; i++) {
      perceiver = physicalAgentObjects.get(i);

      // detect perceptions for the rest of the agents list
      for (int j = i + 1; j < agtNumber; j++) {
        physical = physicalAgentObjects.get(j);

        // first agent perceive the second agent
        detectAndProcessPossiblePerception(perceiver, physical);

        // second agent perceive the first agent
        detectAndProcessPossiblePerception((PhysicalAgentObject) physical,
            perceiver);
      }

      // detect perceptions for objects list
      for (int j = 0; j < objNumber; j++) {
        physical = physicalObjects.get(j);

        // physical agent perceive physical object
        detectAndProcessPossiblePerception(perceiver, physical);
      }
    }

  }

  /**
   * Establish if a given perceiver detected the a given physical (checking also
   * if the physical is perceivable). In case of perception, create the
   * perception event and set its properties.
   * 
   * @param perceiver
   *          the physical agent for which the perception is tested
   * @param physical
   *          the physical that is tested to see if is perceived
   */
  private void detectAndProcessPossiblePerception(
      PhysicalAgentObject perceiver, Physical physical) {

    double xPerceiver = perceiver.getX();
    double yPerceiver = perceiver.getY();
    double xPerceived = physical.getX();
    double yPerceived = physical.getY();
    double perceptionRadius = perceiver.getPerceptionRadius();

    // TODO: here the reflection must be replaced by generating isPerceivable()
    // method in place of the static field ID_PERCEIVABLE on the code
    // generation. The same for the static field AUTO_PERCEPTION
    try {
      Field idPerceivableField = physical.getClass().getDeclaredField(
          "ID_PERCEIVABLE");

      Field autoPerceptionField = perceiver.getClass().getDeclaredField(
          "AUTO_PERCEPTION");

      if (!idPerceivableField.getBoolean(physical)
          || !autoPerceptionField.getBoolean(perceiver)) {
        return;
      }
    } catch (Exception e) {
      System.out
          .println("Missing field 'ID_PERCEIVABLE' or 'AUTO_PERCEPTION'  for object: "
              + physical);
      return;
    }

    // determine the distance (Euclidean and Toroidal spaces)
    Geometry spaceGeometry = spaceModel.getGeometry();
    long xMin = Space.ORDINATEBASE;
    long realXMax = spaceModel.getXMax();
    long xMax = realXMax - (1 - Space.ORDINATEBASE);

    long yMin = Space.ORDINATEBASE;
    long realYMax = spaceModel.getYMax();
    long yMax = realYMax - (1 - Space.ORDINATEBASE);

    // default distance computation
    this.distanceX = Math.abs(xPerceived - xPerceiver);
    this.distanceY = Math.abs(yPerceived - yPerceiver);

    // Cyclic space special cases - adjust distance computation
    if (spaceGeometry.equals(Geometry.Toroidal)) {
      // perception radius is over right border, perceived object is in the left
      // side of perceiver and perception radius smaller than the left distance
      // between perceiver and perceived - compute X distance between objects
      if (xPerceiver + perceptionRadius > xMax && xPerceived < xPerceiver
          && distanceX > perceptionRadius) {

        distanceX = xMax - xPerceiver + xPerceived;
      }

      // perception radius is over left border, perceived object is in the right
      // side of perceiver and perception radius smaller than the right distance
      // between perceiver and perceived - compute X distance between objects
      if (xPerceiver - perceptionRadius < xMin && xPerceived > xPerceiver) {
        distanceX = xPerceiver + xMax - xPerceived;
      }

      // perception radius is over top border, perceived object is in the bottom
      // side of perceiver and perception radius smaller than the down distance
      // between perceiver and perceived - compute y distance between objects
      if (yPerceiver + perceptionRadius > yMax && yPerceived < yPerceiver
          && distanceY > perceptionRadius) {

        distanceY = yMax - yPerceiver + yPerceived;
      }

      // perception radius is over bottom border, perceived object is in the top
      // side of perceiver and perception radius smaller than the top distance
      // between perceiver and perceived - compute y distance between objects
      if (yPerceiver + perceptionRadius < yMin && yPerceived > yPerceiver
          && distanceY > perceptionRadius) {

        distanceY = yPerceiver + yMax - yPerceived;
      }
    }

    // perception occurred, create event
    if (this.distanceX <= perceptionRadius
        && this.distanceY <= perceptionRadius) {

      double distance = this.distanceX + this.distanceY;
      double angle = computePerceiveAngle(perceiver, physical);

      // create the perception event
      PhysicalObjectPerceptionEvent perceptionEvent = new PhysicalObjectPerceptionEvent(
          stepNumber, perceiver.getId(), physical.getClass().getSimpleName(),
          distance);

      // set perceived agent/object
      perceptionEvent.setPerceivedPhysicalObject(physical);

      // set perception angle
      perceptionEvent.setPerceptionAngle(angle);

      // add event to the list
      this.events.add(perceptionEvent);
    }
  }

  /**
   * Helper method to compute the perception angle.
   * 
   * @param perceiver
   *          the physical agent that do the perception
   * @param physical
   *          the physical that is perceived
   * @return the perception angle between perceiver and perceived physicals,
   *         measured in degrees
   */
  private double computePerceiveAngle(Physical perceiver, Physical physical) {

    double orientation = perceiver.getRotZ();

    double angle = Math.atan2(distanceY, distanceX);
    angle = (angle < 0) ? angle + 2 * Math.PI : angle;
    angle = (angle < orientation) ? (2 * Math.PI - (orientation - angle))
        : angle - orientation;

    return angle;
  }

  @Override
  public void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent) {
    Object destroyed = objektDestroyEvent.getSource();
    if (destroyed instanceof PhysicalAgentObject) {
      this.physicalAgentObjects.remove(destroyed);
    } else if (destroyed instanceof PhysicalObject) {
      this.physicalObjects.remove(destroyed);
    }
  }

  @Override
  public void objektInitEvent(ObjektInitEvent objInitEvent) {
    Object created = objInitEvent.getSource();
    if (created instanceof PhysicalAgentObject) {
      this.physicalAgentObjects.add((PhysicalAgentObject) created);
    } else if (created instanceof PhysicalObject) {
      this.physicalObjects.add((PhysicalObject) created);
    }
  }
}
