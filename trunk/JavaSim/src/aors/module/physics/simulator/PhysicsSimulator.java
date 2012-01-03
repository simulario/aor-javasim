/**
 * 
 */
package aors.module.physics.simulator;

import java.util.ArrayList;
import java.util.List;

import aors.GeneralSpaceModel;
import aors.data.DataBus;
import aors.data.evt.sim.ObjektDestroyEvent;
import aors.data.evt.sim.ObjektInitEvent;
import aors.data.evt.sim.SimulationStepEvent;
import aors.logger.model.SimulationParameters;
import aors.model.envevt.EnvironmentEvent;
import aors.model.envsim.EnvironmentRule;
import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.envsim.PhysicalObject;
import aors.module.physics.util.UnitConverter;
import aors.physim.PhySimEnvironmentEvent;
import aors.physim.PhySimKinematicsRule;

/**
 * This class describes a general physics simulator. It must be implemented by
 * every specific simulator.
 * 
 * @author Holger Wuerke
 * 
 */
public abstract class PhysicsSimulator {

  /**
   * The simulation parameters.
   */
  protected SimulationParameters simulationParameters;

  /**
   * The space model.
   */
  protected GeneralSpaceModel spaceModel;

  /**
   * A list of all physical objects.
   */
  protected List<PhysicalObject> physicalObjects;

  /**
   * A list of all physical agent objects
   */
  protected List<PhysicalAgentObject> physicalAgentObjects;

  /**
   * A unit converter.
   */
  protected UnitConverter unitConverter;

  /**
   * The databus, used to send events.
   */
  protected DataBus databus;

  /**
   * The current step number;
   */
  protected long stepNumber;

  /**
   * The gravitation.
   */
  protected double gravitation;

  /**
   * If true, the movements (translation and rotation) are calculated by the simulator.
   */
  protected boolean autoKinematics;

  /**
   * If true, the collision detection is performed by the simulator.
   */
  protected boolean autoCollisionDetection;

  /**
   * If true, the collision handling is performed by the simulator.
   */
  protected boolean autoCollisionHandling;

  /**
   * A list of events. Used to store events from one step to send them all
   * together at the end of the step.
   */
  protected List<EnvironmentEvent> events = new ArrayList<EnvironmentEvent>();

  /**
   * The duration of a simulation step.
   */
  protected double stepDuration;

  /**
   * Creates a new simulator. Should be called by every constructor of
   * subclasses for initialization purposes.
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
  public PhysicsSimulator(SimulationParameters simParams,
      GeneralSpaceModel spaceModel, boolean autoKinematics,
      boolean autoCollisionDetection, boolean autoCollisionHandling,
      double gravitation, DataBus databus, List<PhysicalObject> objects,
      List<PhysicalAgentObject> agents) {

    this.simulationParameters = simParams;
    this.spaceModel = spaceModel;

    this.autoKinematics = autoKinematics;
    this.autoCollisionDetection = autoCollisionDetection;
    this.autoCollisionHandling = autoCollisionHandling;

    this.gravitation = gravitation;

    this.databus = databus;

    this.physicalObjects = objects;
    this.physicalAgentObjects = agents;

    // convert step duration to seconds
    this.unitConverter = new UnitConverter(simParams.getTimeUnit(), spaceModel
        .getSpatialDistanceUnit());
    this.stepDuration = unitConverter.timeToSeconds(simParams.getStepDuration());
    
    // set up unit converter for step duration in seconds
    this.unitConverter = new UnitConverter("s", spaceModel.getSpatialDistanceUnit());
}

  /**
   * Returns a list with all objects and agents.
   * 
   * @return a list with all objects and agents
   */
  protected List<Physical> getPhysicals() {
    List<Physical> list = new ArrayList<Physical>();
    list.addAll(physicalObjects);
    list.addAll(physicalAgentObjects);
    return list;
  }

  /**
   * This method is called after every start of a simulation step.
   * 
   * @param the
   *          step number that will start
   */
  public abstract void simulationStepStart(long stepNumber);

  /**
   * This method is called after every stop of a simulation step.
   */
  public abstract void simulationStepEnd(SimulationStepEvent simulationStepEvent);

  /**
   * This method is called when an objekt is created.
   * 
   * @param objInitEvent
   */
  public abstract void objektInitEvent(ObjektInitEvent objInitEvent);

  /**
   * This method is called when an objekt is destroyed.
   * 
   * @param objektDestroyEvent
   */
  public abstract void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent);

  /**
   * Notifies the system about all events created in the physics module. Usually
   * called at the end of each step. The list is cleared after sending.
   */
  protected void sendEvents(long stepNumber) {
    // collision and perception events
    if (events.size() > 0) {
      databus.addEvents(events);
      events.clear();
    }

    /**
     * this part is used to inform the simulator system that the physics
     * simulator has done changes and finished for this step
     */
    EnvironmentRule environmentRule = new PhySimKinematicsRule(
        "PhySimKinematicsRule", null);
    EnvironmentEvent environmentEvent = new PhySimEnvironmentEvent(
        "PhySimEnvironmentEvent", stepNumber);

    databus.notifyEnvEvent(environmentEvent);
    databus.notifyEnvEventResult(environmentRule, null);
  }

}
