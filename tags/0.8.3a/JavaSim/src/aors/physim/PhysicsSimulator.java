/*
 *	PhySim - physics engine extension for AOR-JSim. PhySim realizes different features 
 *  and implements the following simulators: KinematicsSimulator, DynamicsSimulator, CollisionSimulator,
 *  PerceptionSimulator and AgentMemorySimulator.
 *  To activate the functions of PhySim for your simulation, you can use the following attributes in your
 *  AORSL-script:
 *  
 *  autoKinematics="true" or "false"		Calculates position, velocity and acceleration for physical objects
 *  autoCollision="true" or "false"			Detects collisions of physical objects and generates CollisionEvent
 *  autoPerception="true" or "false"		Detects perceptions of agents and generates PhysicalObjectPerceptionEvent
 *  autoImpulse="true" or "false"			Calculates impulse and new velocity in case of collision
 *  autoGravitation="true" or "false"		Calculates force of gravity on physical objects
 *  memorySize=Physical.PROP_X							Activates memory for agents with capacity of x
 *  
 *  For more information about this project, please have a look at the diploma thesis 
 *  "Entwurf und Implementierung einer Erweiterung zur Simulation physikalischer Gesetzmäßigkeiten im 
 *  Agent-Object-Relationship-Simulator AOR-JSim"
 *  
 *	Stefan Boecker, 2008
 *	mail@stefan-boecker.de
 *
 */

package aors.physim;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import aors.GeneralSimulationParameters;
import aors.GeneralSpaceModel;
import aors.data.DataBusInterface;
import aors.model.envevt.EnvironmentEvent;
import aors.model.envsim.EnvironmentRule;
import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.envsim.PhysicalObject;
import aors.physim.baseclasses.ExternalSimulator;
import aors.physim.baseclasses.PhysicsData;
import aors.physim.extsim.CollisionSimulator;
import aors.physim.extsim.DynamicsSimulator;
import aors.physim.extsim.PerceptionSimulator;
import aors.physim.extsim.TranslationSimulator;

/**
 * The main class for the PhysicalSimulator for all kinds of physical
 * simulations
 * 
 * To initialize PhySim, you have to set different attributes of this class (see
 * below).
 * 
 * @author Stefan Boecker
 */
public final class PhysicsSimulator {

  private PhysicsData physicsData;

  /**
   * Names of all attributes to turn off/on features of PhySim in AORSL
   */
  public static final String AUTO_PERCEPTION = "AUTO_PERCEPTION";
  // public static final String AUTO_KINEMATICS = "AUTO_KINEMATICS";
  // public static final String AUTO_COLLISION = "AUTO_COLLISION";
  public static final String MEMORY_SIZE = "MEMORY_SIZE";
  // public static final String AUTO_GRAVITATION = "AUTO_GRAVITATION";
  // public static final String AUTO_IMPULSE = "AUTO_IMPULSE";
  public static final String ID_PERCEIVABLE = "ID_PERCEIVABLE";

  /**
   * The DataBus used for sending messages and notifications
   */
  private DataBusInterface dataBus;

  /**
   * List of all external simulators.
   */

  /**
   * for the simulations at the beginning of a step (before the envSim runs)
   */
  private List<ExternalSimulator> extsims = new ArrayList<ExternalSimulator>();

  /**
   * for the simulations at the end of a step (after the envSim runs)
   */
  private List<ExternalSimulator> postExtSims = new ArrayList<ExternalSimulator>();

  private boolean init = false;
  private boolean autoKinematics = false;
  private boolean autoCollision = false;
  private boolean autoGravitation = false;
  private boolean autoImpulse = false;
  private boolean autoPerception = false;

  /**
   * Empty constructor
   */
  public PhysicsSimulator() {
    reset();
  }

  public void setDataBus(DataBusInterface dataBus) {
    this.dataBus = dataBus;
  }

  public void reset() {
    this.extsims.clear();
    this.postExtSims.clear();
    this.physicsData = new PhysicsData();
    this.init = false;
  }

  /**
   * Notify PhySim about a new simulation-step. This has to be done in every
   * step of the EnvironmentSimulator.
   * 
   * @param currentEvents
   *          List of current Events in simulation
   * @param currentStep
   *          Current simulation step
   * @param preRunning
   *          if its true, then run the first part of extSimulations; otherwise
   *          the second part (Perception- and CollisionSimulator)
   */
  @SuppressWarnings("unchecked")
  public void simulationStepExternal(List<EnvironmentEvent> currentEvents,
      long currentStep, boolean preRunning) {
    // long start = System.currentTimeMillis();
    if (!this.init)
      return;

    this.currentStep = currentStep;

    if (preRunning == true) {
      // runs runs before current events processed in envSim
      // TODO: check the List cast
      for (ExternalSimulator extsim : extsims) {
        extsim.simulate((List) currentEvents, currentStep);
      }
      if (autoKinematics) {
        // notify logger, that PhySim has changed attributes of objects in
        // simulation
        EnvironmentRule environmentRule = new PhySimKinematicsRule(
            "PhySimKinematicsRule", null);
        EnvironmentEvent environmentEvent = new PhySimEnvironmentEvent(
            "PhySimEnvironmentEvent", currentStep);

        dataBus.notifyEnvEvent(environmentEvent);
        dataBus.notifyEnvEventResult(environmentRule, null);
      }

    } else {

      // runs runs after current events processed in envSim
      for (ExternalSimulator extsim : postExtSims) {
        extsim.simulate((List) currentEvents, currentStep);
      }

    }

    for (ExternalSimulator extsim : extsims)
      extsim.clearPhysicalDestroyed();
    for (ExternalSimulator extsim : postExtSims)
      extsim.clearPhysicalDestroyed();
    // System.out.println(currentStep + ": " + (System.currentTimeMillis() -
    // start));
  }

  /**
   * init the extSimulators;
   */
  private void initialize() {
    // System.out.println("PhysicsSimulator: initialize");

    extsims.clear();
    postExtSims.clear();

    if (autoKinematics) {
      extsims.add(new TranslationSimulator(physicsData));
      // System.out.println("PhysicsSimulator:   added TranslationSimulator");
    }

    if (autoPerception) {
      postExtSims.add(new PerceptionSimulator(physicsData));
      // System.out.println("PhysicsSimulator:   added PerceptionSimulator");
    }

    if (autoCollision) {
      postExtSims.add(new CollisionSimulator(physicsData, autoCollision));
      // System.out.println("PhysicsSimulator:   added CollisionSimulator");
    }

    if (autoGravitation || autoImpulse) {
      extsims.add(new DynamicsSimulator(physicsData, autoGravitation,
          autoImpulse));
      // System.out.println("PhysicsSimulator:   added DynamicsSimulator");
    }

    init = true;
  }

  public void setPhysicalAgents(List<PhysicalAgentObject> a) {
    // System.out.println("PhysicsSimulator: setPhysicalAgents");
    physicsData.setPhysAgents(a);

    physicsData
        .setCollisions(new ArrayList<CollisionSimulator.ActualCollision>());
    physicsData.setPositionChanged(new Hashtable<Long, Boolean>());

    for (int i = 0; i < a.size(); i++) {
      physicsData.getPositionChanged().put(a.get(i).getId(), true);
    }

    if (physicsData.isInited())
      initialize();
  }

  public void setPhysicalObjects(List<PhysicalObject> o) {
    // System.out.println("PhysicsSimulator: setPhysicalObjects");
    physicsData.setPhysObjects(o);
    if (physicsData.isInited())
      initialize();
  }

  public void setSimulationParameters(GeneralSimulationParameters gsp) {
    // System.out.println("PhysicsSimulator: setSimulationsParameters");
    physicsData.setParams(gsp);
    if (physicsData.isInited())
      initialize();
  }

  public void setSpaceModel(GeneralSpaceModel gsm) {
    // System.out.println("PhysicsSimulator: setSpaceModel");
    physicsData.setSpaceModel(gsm);
    if (physicsData.isInited())
      initialize();

  }

  public void initPhys() {
    if (physicsData.isInited())
      initialize();
  }

  public void setPhysicsAttributes(boolean autoKinematics,
      boolean autoCollision, boolean autoGravitation, boolean autoImpulse,
      boolean autoPerception) {
    this.autoKinematics = autoKinematics;
    this.autoCollision = autoCollision;
    this.autoGravitation = autoGravitation;
    this.autoImpulse = autoImpulse;
    this.autoPerception = autoPerception;
  }

  public void physicalDestroyed(long id) {
    physicsData.getPhysicalDestroyed().add(id);
  }

  public void physicalCreated(Physical physical) {
    physicsData.getPhysicalCreated().add(physical);
  }

  private long currentStep;

  public long getCurrentStep() {
    return currentStep;
  }

  /**
   * @return the autoKinematics
   */
  public boolean isAutoKinematics() {
    return autoKinematics;
  }

}
