/**
 * 
 */
package aors.module.physics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import aors.GeneralSpaceModel;
import aors.controller.InitialState;
import aors.controller.SimulationDescription;
import aors.data.evt.ControllerEvent;
import aors.data.evt.sim.ObjektDestroyEvent;
import aors.data.evt.sim.ObjektInitEvent;
import aors.data.evt.sim.SimulationEvent;
import aors.data.evt.sim.SimulationStepEvent;
import aors.logger.model.SimulationParameters;
import aors.model.envevt.EnvironmentEvent;
import aors.model.envsim.Objekt;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.envsim.PhysicalObject;
import aors.module.Module;
import aors.module.physics.simulator.PhysicsSimulator;
import aors.module.physics.simulator.oneD.Simulator1D;
import aors.module.physics.simulator.threeD.BulletSimulator;
import aors.module.physics.simulator.twoD.Box2DSimulator;
import aors.module.physics.simulator.twoDGrid.Simulator2DGrid;
import aors.space.Space;
import aors.util.XMLLoader;
import aors.util.jar.JarUtil;

/**
 * The PhysicsController creates a specific simulator (depending on the space
 * model) that is used for physics simulation. This is the main class of the 
 * physics module.
 * 
 * @author Holger Wuerke
 * 
 */
public class PhysicsController implements Module {

  /**
   * The specific physics simulator used in the simulation.
   */
  private PhysicsSimulator simulator;

  /**
   * The gravitation value (only used in 2D LateralView and 3D).
   */
  private double gravitation = -9.81;

  /**
   * If true, the movements are calculated by the simulator.
   */
  private boolean autoKinematics = false;

  /**
   * If true, the collision detection is performed by the simulator.
   */
  private boolean autoCollisionDetection = false;

  /**
   * If true, the collision handling is performed by the simulator.
   */
  private boolean autoCollisionHandling = false;

  /**
   * Creates a new PhysicsController.
   */
  public PhysicsController() {
    initModuleLibraries();
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.module.Module#getGUIComponent()
   */
  @Override
  public Object getGUIComponent() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * aors.data.java.SimulationStepEventListener#simulationStepEnd(aors.data.
   * java.SimulationStepEvent)
   */
  @Override
  public void simulationStepEnd(SimulationStepEvent simulationStepEvent) {
    if (simulator != null) {
      simulator.simulationStepEnd(simulationStepEvent);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.data.java.SimulationStepEventListener#simulationStepStart(long)
   */
  @Override
  public void simulationStepStart(long stepNumber) {
    if (simulator != null) {
      //long start = System.nanoTime();
      simulator.simulationStepStart(stepNumber);
      //System.err.println(System.nanoTime() - start);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.data.java.SimulationEventListener#simulationEnded()
   */
  @Override
  public void simulationEnded() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * aors.data.java.SimulationEventListener#simulationEnvironmentEventOccured
   * (aors.model.envevt.EnvironmentEvent)
   */
  @Override
  public void simulationEnvironmentEventOccured(
      EnvironmentEvent environmentEvent) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.data.java.SimulationEventListener#simulationInfosEvent(aors.data
   * .java .SimulationEvent)
   */
  @Override
  public void simulationInfosEvent(SimulationEvent simulationEvent) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * aors.data.java.SimulationEventListener#simulationInitialize(aors.module
   * .InitialState)
   */
  @Override
  public void simulationInitialize(InitialState initialState) {
    // get space model and simulation parameters
    GeneralSpaceModel spaceModel = initialState.getSpaceModel();
    SimulationParameters simParams = getSimulationParameters(initialState);

    // no space model -> no simulator
    if (spaceModel == null) {
      return;
    }

    // initialize properties from space model
    Space space = spaceModel.getSpace();    
    this.autoKinematics = space.isAutoKinematics();
    this.autoCollisionDetection = space.isAutoCollisionDetection();
    this.autoCollisionHandling = space.isAutoCollisionHandling();
    this.gravitation = -space.getGravitation();

    // get objects and agents
    List<Objekt> objectList = initialState
        .getObjectsByType(PhysicalObject.class);
    List<Objekt> agentList = initialState
        .getObjectsByType(PhysicalAgentObject.class);

    List<PhysicalObject> objects = new ArrayList<PhysicalObject>();
    for (Objekt o : objectList) {
      objects.add((PhysicalObject) o);
    }

    List<PhysicalAgentObject> agents = new ArrayList<PhysicalAgentObject>();
    for (Objekt a : agentList) {
      agents.add((PhysicalAgentObject) a);
    }
    
    switch (spaceModel.getSpaceType()) {
    case ThreeD:
      simulator = new BulletSimulator(simParams, spaceModel, autoKinematics,
          autoCollisionDetection, autoCollisionHandling, gravitation,
          initialState.getDatabus(), objects, agents);
      break;
      
    case TwoDLateralView:
      simulator = new Box2DSimulator(simParams, spaceModel, autoKinematics,
          autoCollisionDetection, autoCollisionHandling, gravitation,
          initialState.getDatabus(), objects, agents);
      break;
      
    case TwoD:
      simulator = new Box2DSimulator(simParams, spaceModel, autoKinematics,
          autoCollisionDetection, autoCollisionHandling, 0,
          initialState.getDatabus(), objects, agents);
      break;
      
    case TwoDGrid:
      simulator = new Simulator2DGrid(simParams, spaceModel, autoKinematics,
          false, false, 0, initialState.getDatabus(), objects, agents);
      break;
      
    case OneD:
      simulator = new Simulator1D(simParams, spaceModel, autoKinematics,
          autoCollisionDetection, autoCollisionHandling, 0,
          initialState.getDatabus(), objects, agents);
      break;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.data.java.SimulationEventListener#simulationPaused(boolean)
   */
  @Override
  public void simulationPaused(boolean pauseState) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * aors.data.java.SimulationEventListener#simulationProjectDirectoryChanged
   * (java.io.File)
   */
  @Override
  public void simulationProjectDirectoryChanged(File projectDirectory) {
    // in this point the simulator does not exist while a new scenario is
    // opened, and the simulator must be created later when the simulation
    // initialize!
    this.simulator = null;

    // reset parameters
    this.gravitation = -9.81;
    this.autoKinematics = false;
    this.autoCollisionDetection = false;
    this.autoCollisionHandling = false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.data.java.SimulationEventListener#simulationStarted()
   */
  @Override
  public void simulationStarted() {
  }

  /**
   * Returns the simulation parameters of the simulation after retrieving them
   * from the DOM. This method should be moved into initialState!
   * 
   * @param initialState
   * @return the simulation parameters of the simulation
   */
  private SimulationParameters getSimulationParameters(InitialState initialState) {
    SimulationParameters simParams = new SimulationParameters();

    XMLLoader.loadXML(initialState.getSimulationDescription().getDom());

    // Retrieve the "SimulationParameters" element from the XML description
    NodeList simParamsList = XMLLoader.getNodeList("SimulationParameters");

    if (simParamsList != null) {
      for (int j = 0; j < simParamsList.getLength(); j++) {
        // Retrieve all attributes of the "SimulationParameters" element
        NamedNodeMap attributes = simParamsList.item(j).getAttributes();

        // Retrieve the attribute values for each specified attribute
        for (int i = 0; i < attributes.getLength(); i++) {
          String attributeName = attributes.item(i).getNodeName();
          String attributeValue = attributes.item(i).getNodeValue();

          // Set the attributes
          if (attributeName.equals("simulationSteps")) {
            simParams.setSimulationSteps(Long.valueOf(attributeValue));
          } else if (attributeName.equals("stepDuration")) {
            simParams.setStepDuration(Double.valueOf(attributeValue));
          } else if (attributeName.equals("timeUnit")) {
            simParams.setTimeUnit(attributeValue);
          } else if (attributeName.equals("stepTimeDelay")) {
            simParams.setStepTimeDelay(Double.valueOf(attributeValue));
          }
        }
      }
    } else {

      return null;
    }

    return simParams;
  }

  /**
   * Initialize the module libraries by unpacking the Jars, loading and setting
   * class paths.
   */
  private void initModuleLibraries() {
    // local path in the temporarily directory for this module
    String localTmpPath = "physicsModule";

    // path to jar
    String jarPath = System.getProperty("user.dir") + File.separator
        + "modules" + File.separator + "physicsModule.jar";

    // extract the jar files for physics module
    try {
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib",
          "jbox2d-library-2.1.2.jar");
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib",
      "j3dcore.jar");
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib",
      "j3dutils.jar");
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib",
      "vecmath.jar");
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib",
      "jbullet.jar");
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    // add this path in the library path...
    JarUtil.setLibraryPath(localTmpPath);

    // load jars from that temporarily directory (physics required jars)
    JarUtil.loadJar(localTmpPath, "jbox2d-library-2.1.2.jar");
    JarUtil.loadJar(localTmpPath, "j3dcore.jar");
    JarUtil.loadJar(localTmpPath, "j3dutils.jar");
    JarUtil.loadJar(localTmpPath, "vecmath.jar");
    JarUtil.loadJar(localTmpPath, "jbullet.jar");
  }

  @Override
  public void simulationDomOnlyInitialization(
      SimulationDescription simulationDescription) {
  }

  @Override
  public void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent) {
    if (simulator != null) {
      simulator.objektDestroyEvent(objektDestroyEvent);
    }
  }

  @Override
  public void objektInitEvent(ObjektInitEvent objInitEvent) {
    if (simulator != null) {
      simulator.objektInitEvent(objInitEvent);
    }
  }

  @Override
  public void notifyEvent(ControllerEvent event) {
    // TODO Auto-generated method stub
  }
}
