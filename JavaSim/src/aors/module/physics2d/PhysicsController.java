/**
 * 
 */
package aors.module.physics2d;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import aors.GeneralSpaceModel;
import aors.GeneralSpaceModel.SpaceType;
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
import aors.util.XMLLoader;
import aors.util.jar.JarUtil;

/**
 * The PhysicsController creates a specific simulator (depending on the space
 * model) that is used for physics simulation.
 * 
 * @author Holger Wuerke
 * @since 01.12.2009
 * 
 */
public class PhysicsController implements Module {

  /**
   * The specific physics simulator used in the simulation.
   */
  private PhysicsSimulator simulator;

  /**
   * The gravitation value (only used in 2D LateralView).
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
      simulator.simulationStepStart(stepNumber);
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
   * @see
   * aors.data.java.SimulationEventListener#simulationInfosEvent(aors.data.java
   * .SimulationEvent)
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
    // get needed parameters
    GeneralSpaceModel spaceModel = initialState.getSpaceModel();
    SimulationParameters simParams = getSimulationParameters(initialState);
    getSpaceTypeAttributes(initialState);

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

    // no space model -> no simulator
    if (spaceModel == null) {
      return;
    }

    // create specific simulator depending on SpaceType
    if (spaceModel.getSpaceType().equals(SpaceType.TwoD)
        || spaceModel.getSpaceType().equals(SpaceType.TwoDLateralView)) {
      simulator = new Box2DSimulator(simParams, spaceModel, autoKinematics,
          autoCollisionDetection, autoCollisionHandling, gravitation,
          initialState.getDatabus(), objects, agents);
    }

    if (spaceModel.getSpaceType().equals(SpaceType.TwoDGrid)) {
      simulator = new Simulator2DGrid(simParams, spaceModel, autoKinematics,
          autoCollisionDetection, autoCollisionHandling, gravitation,
          initialState.getDatabus(), objects, agents);
    }

    if (spaceModel.getSpaceType().equals(SpaceType.OneD)) {
      simulator = new Simulator1D(simParams, spaceModel, autoKinematics,
          autoCollisionDetection, autoCollisionHandling, gravitation,
          initialState.getDatabus(), objects, agents);
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
  }

  /*
   * (non-Javadoc)
   * 
   * @see aors.data.java.SimulationEventListener#simulationStarted()
   */
  @Override
  public void simulationStarted() {
    if (simulator != null) {
      simulator.simulationStarted();
    }
  }

  /**
   * Returns the simulation model of the simulation after retrieving it from the
   * DOM. This method should be moved into initialState!
   * 
   * @param initialState
   * @return the simulation model of the simulation
   */
  private void getSpaceTypeAttributes(InitialState initialState) {
    if (initialState.getSpaceModel() == null) {
      return;
    }

    XMLLoader.loadXML(initialState.getSimulationDescription().getDom());

    // Retrieve the specific SpaceType element from the XML description
    String spaceType = null;
    switch (initialState.getSpaceModel().getSpaceType()) {
    case OneD:
      spaceType = "OneDimensional";
      gravitation = 0;
      break;
    case TwoD:
      spaceType = "TwoDimensional";
      gravitation = 0;
      break;
    case TwoDGrid:
      spaceType = "TwoDimensionalGrid";
      gravitation = 0;
      break;
    case TwoDLateralView:
      spaceType = "TwoDimensional_LateralView";
      break;
    }

    NodeList spaceTypeList = XMLLoader.getNodeList(spaceType);

    if (spaceTypeList != null) {
      for (int j = 0; j < spaceTypeList.getLength(); j++) {
        // Retrieve all attributes of the SpaceType element
        NamedNodeMap attributes = spaceTypeList.item(j).getAttributes();

        // Retrieve the attribute values for each specified attribute
        for (int i = 0; i < attributes.getLength(); i++) {
          String attributeName = attributes.item(i).getNodeName();
          String attributeValue = attributes.item(i).getNodeValue();

          // Set the auto switches
          if (attributeName.equals("autoKinematics")) {
            autoKinematics = Boolean.valueOf(attributeValue);
          } else if (attributeName.equals("autoCollisionDetection")) {
            autoCollisionDetection = Boolean.valueOf(attributeValue);
          } else if (attributeName.equals("autoCollisionHandling")) {
            autoCollisionHandling = Boolean.valueOf(attributeValue);
          } else if (attributeName.equals("gravitation")) {
            gravitation = Double.valueOf(attributeValue);
          }
        }
      }
    }
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

    // extract the jar files for sound module
    try {
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib",
          "jbox2d-2.0.1-library-only.jar");
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    // add this path in the library path...
    JarUtil.setLibraryPath(localTmpPath);

    // load jars from that temporarily directory (physics required jars)
    JarUtil.loadJar(localTmpPath, "jbox2d-2.0.1-library-only.jar");
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
