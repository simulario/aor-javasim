package aors.data.logger;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import aors.GeneralSimulationParameters;
import aors.GeneralSpaceModel;
import aors.ScenarioInfos;
import aors.data.java.ObjektInitEvent;
import aors.logger.model.AgentSimulatorStep;
import aors.model.envevt.ActivityEndEvent;
import aors.model.envevt.EnvironmentEvent;
import aors.model.envevt.activity.Activity;
import aors.model.envsim.AgentObject;
import aors.model.envsim.EnvironmentRule;
import aors.model.envsim.Objekt;
import aors.model.envsim.Physical;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.envsim.PhysicalObject;
import aors.statistics.AbstractStatisticsVariable;
import aors.util.JsonData;

/**
 * A basic Logger for the Web simulator. It was derived from MemoryLogger.
 * 
 * @author Christian Noack
 * @since 21/Jul/2009
 * @version 1.0
 */
public class WebLogger extends Logger {

  private static final long serialVersionUID = -1725208492516181374L;

  private long currentSimulationStep;

  private boolean debug = false;

  // step, agentSimulationResult
  // TODO: instead of java.lang.Object use simply the Double type because not
  // other is usable for the SVG
  // chart
  // a list as root, would be an better idea, because its ordered!!!
  private Map<Integer, Map<Integer, Map<String, java.lang.Object>>> environmentSteps;

  // <object id, object name>
  private Map<Integer, String> objectNames;

  // <object id, <property name, property type>>
  private Map<Integer, Map<String, String>> objectProperties;

  private String errorMessages;

  public WebLogger() {
    if (debug)
      System.out.println("WebLogger constructor");
    initialize();
  }

  @Override
  public void initialize() {
    this.currentSimulationStep = 1;

    this.environmentSteps = new HashMap<Integer, Map<Integer, Map<String, java.lang.Object>>>();
    this.objectNames = new HashMap<Integer, String>();
    this.objectProperties = new HashMap<Integer, Map<String, String>>();

    this.modelTitle = "";
    this.scenarioName = "";
    this.scenarioTitle = "";

    this.errorMessages = "";
  }

  @Override
  public void notifyStart() {
    if (debug)
      System.out.println("WebLogger: start sim");
  }

  @Override
  public void notifyEnd() {
    if (debug)
      System.out.println("WebLogger: end sim");
  }

  @Override
  public void notifyEnvEvent(EnvironmentEvent envEvent) {
    this.currentSimulationStep = envEvent.getOccurrenceTime();
    if (debug)
      System.out.println("WebLogger: current step = "
          + this.currentSimulationStep);
  }

  @Override
  public void notifyEnvEventResult(EnvironmentRule environmentRule,
      Collection<EnvironmentEvent> envEventList) {

    int indexOfCurrentEvent = (int) currentSimulationStep;
    // if (debug) System.out.println("step:" + (int) currentSimulationStep);

    // why are we testing this???
    if (indexOfCurrentEvent >= 0) {

      // state changes
      while (!dataCollector.isPropChangeListObjIsEmpty()) {

        PropertyChangeEvent propertyChangeEvent = dataCollector
            .getNextObjektChange();
        Objekt objekt = (Objekt) propertyChangeEvent.getSource();

        this.registerObjekt(objekt);
        this.registerSimulationResult(currentSimulationStep, objekt.getId(),
            propertyChangeEvent.getPropertyName(), propertyChangeEvent
                .getNewValue());

      }

      while (!dataCollector.isPropChangeListPhysObjIsEmpty()) {

        PropertyChangeEvent propertyChangeEvent = dataCollector
            .getNextPhysObjektChange();
        PhysicalObject objekt = (PhysicalObject) propertyChangeEvent
            .getSource();

        this.registerObjekt(objekt);
        this.registerSimulationResult(currentSimulationStep, objekt.getId(),
            propertyChangeEvent.getPropertyName(), propertyChangeEvent
                .getNewValue());

      }

      while (!dataCollector.isPropChangeListPhysAgentIsEmpty()) {

        PropertyChangeEvent propertyChangeEvent = dataCollector
            .getNextPhysAgentChange();
        PhysicalAgentObject objekt = (PhysicalAgentObject) propertyChangeEvent
            .getSource();

        this.registerObjekt(objekt);
        this.registerSimulationResult(currentSimulationStep, objekt.getId(),
            propertyChangeEvent.getPropertyName(), propertyChangeEvent
                .getNewValue());

      }

      while (!dataCollector.isPropChangeListAgentIsEmpty()) {

        PropertyChangeEvent propertyChangeEvent = dataCollector
            .getNextAgentChange();
        AgentObject objekt = (AgentObject) propertyChangeEvent.getSource();

        this.registerObjekt(objekt);
        this.registerSimulationResult(currentSimulationStep, objekt.getId(),
            propertyChangeEvent.getPropertyName(), propertyChangeEvent
                .getNewValue());

      }

    }

  }

  private void registerSimulationResult(long simulationStep, long objectId,
      String propertyName, Object propertyValue) {
    /*
     * if (debug) System.out.println("SimStep:" + simulationStep +
     * ",Object ID: " + objectId + "Property Name:" + propertyName +
     * "Property Value:" + propertyValue );
     */
    // create a new map for the objects simulation results in this very
    // simulation step
    // <AgentID, Property<Name, Value>>
    Map<Integer, Map<String, java.lang.Object>> environmentStep;

    // property map to keep the current simulation results
    Map<String, java.lang.Object> propertyResult;

    // get the map for the current simulation step
    environmentStep = this.environmentSteps.get((int) simulationStep);
    // when the current simulation step is not yet existent
    if (environmentStep == null) {
      // create a new simulation step
      environmentStep = new HashMap<Integer, Map<String, java.lang.Object>>();
      // add it
      this.environmentSteps.put((int) simulationStep, environmentStep);
    }

    // get the property results of the object with the ID
    propertyResult = environmentStep.get((int) objectId);
    // if not instance yet, create one
    if (propertyResult == null) {
      // Property<Name, Value>
      propertyResult = new HashMap<String, java.lang.Object>();
      // put the reference of the property result map to the current environment
      // step
      environmentStep.put((int) objectId, propertyResult);
    }
    // add the property changes to the result map
    propertyResult.put(propertyName, propertyValue);
  }

  private void registerObjekt(Objekt objekt) {

    // put it on the map of <object id, object name>
    this.objectNames.put((int) objekt.getId(), objekt.getName());

    Map<String, String> properties = this.objectProperties.get((int) objekt
        .getId());
    if (properties == null) {

      properties = buildDefaultProperties(objekt);
      // put the on the map of all objects properties
      this.objectProperties.put((int) objekt.getId(), properties);
    }
  }

  private void initialiseObjectsDefaultProperties(Objekt objekt) {

    Map<Integer, Map<String, java.lang.Object>> initialisedObjects;

    // if there is a map holding objects and their initialised properties
    if (this.environmentSteps.get(0) == null) {
      initialisedObjects = new HashMap<Integer, Map<String, java.lang.Object>>();
      // register the reference
      this.environmentSteps.put(0, initialisedObjects);
    } else {
      initialisedObjects = this.environmentSteps.get(0);
    }

    Map<String, java.lang.Object> initialisedProperties;
    if (initialisedObjects.get((int) objekt.getId()) == null) {
      initialisedProperties = new HashMap<String, java.lang.Object>();
      // register the reference
      initialisedObjects.put((int) objekt.getId(), initialisedProperties);
    } else {
      initialisedProperties = initialisedObjects.get((int) objekt.getId());
    }

    // initialise the default properties
    if (objekt instanceof PhysicalAgentObject) {

      initialisedProperties.put(Physical.PROP_PERCEPTION_RADIUS,
          ((PhysicalAgentObject) objekt).getPerceptionRadius());
    }

    if (objekt instanceof PhysicalObject) {

      Physical physicalObject = (Physical) objekt;

      initialisedProperties.put(Physical.PROP_X, physicalObject.getX());
      initialisedProperties.put(Physical.PROP_Y, physicalObject.getY());
      initialisedProperties.put(Physical.PROP_Z, physicalObject.getZ());
      initialisedProperties.put(Physical.PROP_ROTATION_ANGLE_X, physicalObject
          .getRotationAngleX());
      initialisedProperties.put(Physical.PROP_ROTATION_ANGLE_Y, physicalObject
          .getRotationAngleY());
      initialisedProperties.put(Physical.PROP_ROTATION_ANGLE_Z, physicalObject
          .getRotationAngleZ());
      initialisedProperties.put(Physical.PROP_VX, physicalObject.getVx());
      initialisedProperties.put(Physical.PROP_VY, physicalObject.getVy());
      initialisedProperties.put(Physical.PROP_VZ, physicalObject.getVz());
      initialisedProperties.put(Physical.PROP_OMEGA_X, physicalObject
          .getOmegaX());
      initialisedProperties.put(Physical.PROP_OMEGA_Y, physicalObject
          .getOmegaY());
      initialisedProperties.put(Physical.PROP_OMEGA_Z, physicalObject
          .getOmegaZ());
      initialisedProperties.put(Physical.PROP_AX, physicalObject.getAx());
      initialisedProperties.put(Physical.PROP_AY, physicalObject.getAy());
      initialisedProperties.put(Physical.PROP_AZ, physicalObject.getAz());
      initialisedProperties.put(Physical.PROP_M, physicalObject.getM());
      initialisedProperties.put(Physical.PROP_WIDTH, physicalObject.getWidth());
      initialisedProperties.put(Physical.PROP_HEIGHT, physicalObject
          .getHeight());
      initialisedProperties.put(Physical.PROP_DEPTH, physicalObject.getDepth());
    }

    if (objekt instanceof Objekt) {
      // nothing to do here until now
    }

  }

  private void initialiseObjectsInheritedProperties(Objekt object,
      String propertyName, String propertyType) {

    // object id, object name
    this.objectNames.put((int) object.getId(), object.getName());

    // object id, property name, property type
    Map<String, String> property = this.objectProperties.get((int) object
        .getId());
    if (property == null) {
      property = buildDefaultProperties(object);
      // put the on the map of all objects properties
      this.objectProperties.put((int) object.getId(), property);

    }
    property.put(propertyName, propertyType);
    this.objectProperties.put((int) object.getId(), property);

    Map<Integer, Map<String, java.lang.Object>> initialisedObjects;

    // if there is a map holding objects and their initialisation properties
    if (this.environmentSteps.get(0) == null) {
      initialisedObjects = new HashMap<Integer, Map<String, java.lang.Object>>();
    } else {
      initialisedObjects = this.environmentSteps.get(0);
    }

    Map<String, java.lang.Object> initialisedProperties;
    if (initialisedObjects.get((int) object.getId()) == null) {
      initialisedProperties = new HashMap<String, java.lang.Object>();
    } else {
      initialisedProperties = initialisedObjects.get((int) object.getId());
    }

    if (propertyType.equals("Double")) {
      initialisedProperties.put(propertyName, object.getInheritedProperty()
          .getDoubleProperties().get(propertyName));
    } else if (propertyType.equals("Long")) {

    } else if (propertyType.equals("String")) {

    } else if (propertyType.equals("Boolean")) {

    }
  }

  private HashMap<String, String> buildDefaultProperties(Objekt objekt) {
    HashMap<String, String> properties = new HashMap<String, String>();

    // create the default, not the inherited properties
    if (objekt instanceof PhysicalAgentObject) {

      properties.put(Physical.PROP_PERCEPTION_RADIUS, "Double");

    }

    if (objekt instanceof PhysicalObject) {

      properties.put(Physical.PROP_X, "Double");
      properties.put(Physical.PROP_Y, "Double");
      properties.put(Physical.PROP_Z, "Double");
      properties.put(Physical.PROP_ROTATION_ANGLE_X, "Double");
      properties.put(Physical.PROP_ROTATION_ANGLE_Y, "Double");
      properties.put(Physical.PROP_ROTATION_ANGLE_Z, "Double");
      properties.put(Physical.PROP_VX, "Double");
      properties.put(Physical.PROP_VY, "Double");
      properties.put(Physical.PROP_VZ, "Double");
      properties.put(Physical.PROP_OMEGA_X, "Double");
      properties.put(Physical.PROP_OMEGA_Y, "Double");
      properties.put(Physical.PROP_OMEGA_Z, "Double");
      properties.put(Physical.PROP_AX, "Double");
      properties.put(Physical.PROP_AY, "Double");
      properties.put(Physical.PROP_AZ, "Double");
      properties.put("a", "Double");
      properties.put("v", "Double");
      properties.put(Physical.PROP_WIDTH, "Double");
      properties.put(Physical.PROP_HEIGHT, "Double");
      properties.put(Physical.PROP_DEPTH, "Double");
      properties.put(Physical.PROP_M, "Double");

    }

    /*
     * if (objekt instanceof Object) { //nothing to do here until now }
     */

    return properties;
  }

  @Deprecated
  public String toString() {
    String result = "";
    boolean init = true;
    if (!init) {
      // for all simulation steps, ordered because set's are not ordered
      for (int step = 1; step <= this.environmentSteps.keySet().size(); step++) {
        // for(int step : this.environmentSteps.keySet()) {
        if (debug)
          System.out.println("Simulation Step: " + step);
        // for all object id's
        for (int objectId : this.environmentSteps.get(step).keySet()) {
          if (debug)
            System.out.println("  Object Id: " + objectId + " Name: "
                + this.objectNames.get(objectId));
          for (String propertyName : this.environmentSteps.get(step).get(
              objectId).keySet()) {
            Object propertyValue = this.environmentSteps.get(step)
                .get(objectId).get(propertyName);

            // if(!propertyName.equals(Physical.PROP_X)) {
            // if (debug) System.out.println("    " + propertyName + "=" +
            // propertyValue);
            // }
            if (debug)
              System.out.println("    " + propertyName + "=" + propertyValue);
          }
        }
      }
    } else {
      if (debug)
        System.out.println("\nInitialized Objects:");
      for (Integer objectId : this.objectNames.keySet()) {
        if (debug)
          System.out.println("\tID=" + objectId + ", Name="
              + this.objectNames.get(objectId));
        for (String propertyName : this.objectProperties.get(objectId).keySet()) {
          if (debug)
            System.out.println("\t\t+" + propertyName + " : "
                + this.objectProperties.get(objectId).get(propertyName));
        }
      }

      if (debug)
        System.out.println("\nInitialized Objects Properties:");
      for (Integer objectId : this.environmentSteps.get(0).keySet()) {
        if (debug)
          System.out.println("Object " + objectId);
        for (String propertyName : this.environmentSteps.get(0).get(objectId)
            .keySet()) {
          if (debug)
            System.out.println("\t\t" + propertyName + "="
                + this.environmentSteps.get(0).get(objectId).get(propertyName));
        }
      }

    }

    return result;
  }

  @Override
  public void notifyInitialisation() {
    // agent id, property name, property type
    this.objectProperties = new HashMap<Integer, Map<String, String>>();

    // get the initStates from objects (as HashMap<String, String>)
    while (!dataCollector.isObjInitListIsEmpty()) {

      ObjektInitEvent objInitEvent = dataCollector.getNextObjektInitEvent();
      Objekt objekt = (Objekt) objInitEvent.getSource();

      this.initialiseObjectsDefaultProperties(objekt);

      Map<String, String> initStrProps = objekt.getInheritedProperty()
          .getStringProperties();
      // for all string properties
      for (String propertyName : initStrProps.keySet()) {
        this.initialiseObjectsInheritedProperties(objekt, propertyName,
            "String");
      }

      Map<String, Boolean> initBooleanProps = objekt.getInheritedProperty()
          .getBooleanProperties();
      for (String propertyName : initBooleanProps.keySet()) {
        /*
         * if (debug) System.out.println("=> Property Type: Boolean, Name:" +
         * propertyName + ", Value: " + initStrProps.get(propertyName));
         */
        this.initialiseObjectsInheritedProperties(objekt, propertyName,
            "Boolean");
      }

      Map<String, Long> initLongProps = objekt.getInheritedProperty()
          .getLongProperties();

      for (String propertyName : initLongProps.keySet()) {
        /*
         * if (debug) System.out.println("=> Property Type: Long, Name:" +
         * propertyName + ", Value: " + initStrProps.get(propertyName));
         */
        this.initialiseObjectsInheritedProperties(objekt, propertyName, "Long");
      }

      Map<String, Double> initDoubleProps = objekt.getInheritedProperty()
          .getDoubleProperties();
      for (String propertyName : initDoubleProps.keySet()) {
        /*
         * if (debug) System.out.println("=> Property Type: Double, Name:" +
         * propertyName + ", Value: " + initStrProps.get(propertyName));
         */
        this.initialiseObjectsInheritedProperties(objekt, propertyName,
            "Double");
      }

    }

    // get the initStates from physicalobjects (as HashMap<String, String>)
    while (!dataCollector.isPhysObjInitListIsEmpty()) {

      ObjektInitEvent physObjInitEvent = dataCollector
          .getNextPhysObjInitEvent();
      PhysicalObject objekt = (PhysicalObject) physObjInitEvent.getSource();
      /*
       * if (debug) System.out.println("Init. Physical Objekt Name: " +
       * objekt.getName() + ", ID:" + objekt.getId() + ", Type: " +
       * objekt.getType());
       */
      this.initialiseObjectsDefaultProperties(objekt);

      HashMap<String, String> initStrProps = objekt.getInheritedProperty()
          .getStringProperties();
      for (String propertyName : initStrProps.keySet()) {
        /*
         * if (debug) System.out.println("=> Property Type: String, Name:" +
         * propertyName + ", Value: " + initStrProps.get(propertyName));
         */
        this.initialiseObjectsInheritedProperties(objekt, propertyName,
            "String");
      }

      HashMap<String, Boolean> initBooleanProps = objekt.getInheritedProperty()
          .getBooleanProperties();
      for (String propertyName : initBooleanProps.keySet()) {
        /*
         * if (debug) System.out.println("=> Property Type: Boolean, Name:" +
         * propertyName + ", Value: " + initBooleanProps.get(propertyName));
         */
        this.initialiseObjectsInheritedProperties(objekt, propertyName,
            "Boolean");
      }

      HashMap<String, Long> initLongProps = objekt.getInheritedProperty()
          .getLongProperties();
      for (String propertyName : initLongProps.keySet()) {
        /*
         * if (debug) System.out.println("=> Property Type: Long, Name:" +
         * propertyName + ", Value: " + initLongProps.get(propertyName));
         */
        this.initialiseObjectsInheritedProperties(objekt, propertyName, "Long");
      }

      HashMap<String, Double> initDoubleProps = objekt.getInheritedProperty()
          .getDoubleProperties();
      for (String propertyName : initDoubleProps.keySet()) {
        /*
         * if (debug) System.out.println("=> Property Type: Double, Name:" +
         * propertyName + ", Value: " + initDoubleProps.get(propertyName));
         */
        this.initialiseObjectsInheritedProperties(objekt, propertyName,
            "Double");
      }

    }

    // get the initstates from agents (as HashMap<String, String>)
    while (!dataCollector.isPhysAgentInitListIsEmpty()) {

      ObjektInitEvent physObjInitEvent = dataCollector
          .getNextPhysAgentInitEvent();
      PhysicalAgentObject objekt = (PhysicalAgentObject) physObjInitEvent
          .getSource();
      /*
       * if (debug) System.out.println("Init. Physical Agent Name: " +
       * objekt.getName() + ", ID:" + objekt.getId() + ", Type: " +
       * objekt.getType());
       */
      this.initialiseObjectsDefaultProperties(objekt);

      HashMap<String, String> initStrProps = objekt.getInheritedProperty()
          .getStringProperties();
      for (String propertyName : initStrProps.keySet()) {
        /*
         * if (debug) System.out.println("=> Property Type: String, Name:" +
         * propertyName + ", Value: " + initStrProps.get(propertyName));
         */

        this.initialiseObjectsInheritedProperties(objekt, propertyName,
            "String");
      }

      HashMap<String, Boolean> initBooleanProps = objekt.getInheritedProperty()
          .getBooleanProperties();
      for (String propertyName : initBooleanProps.keySet()) {
        /*
         * if (debug) System.out.println("=> Property Type: Boolean, Name:" +
         * propertyName + ", Value: " + initStrProps.get(propertyName));
         */

        this.initialiseObjectsInheritedProperties(objekt, propertyName,
            "Boolean");
      }

      HashMap<String, Long> initLongProps = objekt.getInheritedProperty()
          .getLongProperties();
      for (String propertyName : initLongProps.keySet()) {
        /*
         * if (debug) System.out.println("=> Property Type: Long, Name:" +
         * propertyName + ", Value: " + initStrProps.get(propertyName));
         */
        this.initialiseObjectsInheritedProperties(objekt, propertyName, "Long");
      }

      HashMap<String, Double> initDoubleProps = objekt.getInheritedProperty()
          .getDoubleProperties();
      for (String propertyName : initDoubleProps.keySet()) {
        /*
         * if (debug) System.out.println("=> Property Type: Double, Name:" +
         * propertyName + ", Value: " + initStrProps.get(propertyName));
         */
        this.initialiseObjectsInheritedProperties(objekt, propertyName,
            "Integer");
      }
    }

    // delete the bufferqueus, because we have some
    // statechanges while the initialization
    dataCollector.deleteAllBuffer();
  }

  @Override
  public void notifySimStepEnd() {
    // this.printOutput(this.simulationStep);
    // this.simulationStep = null;
  }

  @Override
  public void notifySimStepStart(Long aorStepTime) {

    // create an new entry even if the simulation step contains no results!
    Map<Integer, Map<String, java.lang.Object>> environmentStep = new HashMap<Integer, Map<String, java.lang.Object>>();
    this.environmentSteps.put(aorStepTime.intValue(), environmentStep);
  }

  @Override
  public void notifySimulationScenario(ScenarioInfos scenarioInfos,
      GeneralSimulationParameters aorSimParameters,
      Map<String, String> modelParamMap) {
    super.notifySimulationScenario(scenarioInfos, aorSimParameters,
        modelParamMap);
    if (debug) {
      System.out.println("WebLogger: simulation scenario");
      for (String key : modelParamMap.keySet()) {
        System.out.println("       " + key + " : " + modelParamMap.get(key));
      }
    }

    super.notifySimulationScenario(scenarioInfos, aorSimParameters,
        modelParamMap);
  }

  @Override
  public void notifySimulationStart(long startTime, long steps) {

    if (debug)
      if (debug)
        System.out.println("WebLogger: simulation start");
  }

  @Override
  public void notifySpaceModel(GeneralSpaceModel aorSpaceModel) {
    if (debug)
      if (debug)
        System.out.println("WebLogger: spaceModel");
  }

  public String getAgentCustomName(int id) {
    String result = "";

    // TODO finish

    return result;
  }

  public Map<Integer, Map<Integer, Map<String, java.lang.Object>>> getEnvironmentSteps() {
    return this.environmentSteps;
  }

  public Map<Integer, String> getObjectNames() {
    return this.objectNames;
  }

  public Map<Integer, Map<String, String>> getObjectProperties() {
    return this.objectProperties;
  }

  public String getErrorMessages() {
    return errorMessages;
  }

  public void setErrorMessages(String errorMessages) {
    this.errorMessages = errorMessages;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the statistics
   * 
   * 
   * 
   * @return the statistics
   */
  public Map<String, String> getStatistics() {
    Map<String, String> statistics = new HashMap<String, String>();

    for (AbstractStatisticsVariable variable : this.statisticVariables) {
      statistics.put(variable.getDisplayName(), variable.getValue().toString());

    }

    return statistics;
  }

  @Override
  public void notifyAgentSimulatorStep(JsonData agentStepLog) {
    // TODO Auto-generated method stub
    if (debug)
      System.out.println("WebLogger: agentSimulator step");
  }

  @Override
  public void notifyAgentSimulatorStep(AgentSimulatorStep agentSimulatorStep) {
    // TODO Auto-generated method stub
    if (debug)
      System.out.println("WebLogger: agentSimulator step");
  }

  @Override
  public void notifyActivityStart(Activity activity,
      Collection<EnvironmentEvent> envEventList,
      ActivityEndEvent activityFinalizeEvent) {
    // TODO Auto-generated method stub
    if (debug)
      System.out.println("WebLogger: activity start");

  }

  @Override
  public void notifyActivityStop(Activity activity,
      Collection<EnvironmentEvent> envEventList) {
    // TODO Auto-generated method stub
    if (debug)
      System.out.println("WebLogger: activity stop");
  }

  @Override
  // ToDo: check if this is the right implementation _jw_
  public void notifyAgentSimulatorsResultingStateChanges() {

    while (!dataCollector.isPropChangeListPhysAgentIsEmpty()) {

      PropertyChangeEvent propertyChangeEvent = dataCollector
          .getNextPhysAgentChange();
      PhysicalAgentObject objekt = (PhysicalAgentObject) propertyChangeEvent
          .getSource();

      this.registerObjekt(objekt);
      this.registerSimulationResult(currentSimulationStep, objekt.getId(),
          propertyChangeEvent.getPropertyName(), propertyChangeEvent
              .getNewValue());

    }

    while (!dataCollector.isPropChangeListAgentIsEmpty()) {

      PropertyChangeEvent propertyChangeEvent = dataCollector
          .getNextAgentChange();
      AgentObject objekt = (AgentObject) propertyChangeEvent.getSource();

      this.registerObjekt(objekt);
      this.registerSimulationResult(currentSimulationStep, objekt.getId(),
          propertyChangeEvent.getPropertyName(), propertyChangeEvent
              .getNewValue());
    }
  }

}
