package aors.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import aors.GeneralSimulationParameters;
import aors.GeneralSpaceModel;
import aors.ScenarioInfos;
import aors.controller.InitialState;
import aors.controller.SimulationDescription;
import aors.data.java.CollectionEvent;
import aors.data.java.CollectionEventListener;
import aors.data.java.CollectionInitEvent;
import aors.data.java.CollectionInitEventListener;
import aors.data.java.ObjektDestroyEvent;
import aors.data.java.ObjektDestroyEventListener;
import aors.data.java.ObjektInitEvent;
import aors.data.java.ObjektInitEventListener;
import aors.data.java.SimulationEvent;
import aors.data.java.SimulationEventListener;
import aors.data.java.SimulationStepEvent;
import aors.data.java.SimulationStepEventListener;
import aors.data.logger.Logger;
import aors.data.logger.MemoryLogger;
import aors.data.logger.SimObserver;
import aors.data.logger.WebLogger;
import aors.data.logger.XMLFullLogger;
import aors.logger.model.AgentSimulatorStep;
import aors.logger.model.SimulationStep;
import aors.model.envevt.ActivityEndEvent;
import aors.model.envevt.EnvironmentEvent;
import aors.model.envevt.activity.Activity;
import aors.model.envsim.EnvironmentRule;
import aors.module.evt.ModuleEvent;
import aors.module.evt.ModuleEventListener;
import aors.statistics.AbstractStatisticsVariable;
import aors.util.JsonData;

/**
 * The DataBus implements a generic data transfer channel used by all
 * components. It replaces the old logger instance used everywhere and the
 * static LoggerFactory implemented by jw. The DataBus encapsulates the logger
 * and enables the user to replace the Logger during runtime without propagating
 * the new logger manually.
 * 
 * @author Christian Noack
 * @since 04/August/2009
 * 
 */
public class DataBus implements DataBusInterface {

  /**
   * The logger used in the DataBus
   */
  private Logger logger;

  private DataStatCollector dataStatCollector = null;

  private DataCollector dataCollector = null;

  /** the list with all external created environment events extensions **/
  private List<EnvironmentEvent> envEvents = new ArrayList<EnvironmentEvent>();

  /**
   * The type of the logger
   */
  private int loggerType;

  private List<SimulationStepEventListener> simStepEventListener = new ArrayList<SimulationStepEventListener>();
  private List<SimulationEventListener> simulationEventListeners = new ArrayList<SimulationEventListener>();

  private List<CollectionEventListener> collectionListener = new ArrayList<CollectionEventListener>();
  private List<CollectionInitEventListener> collectionInitEventListener = new ArrayList<CollectionInitEventListener>();

  private List<PropertyChangeListener> propertyChangeListener = new ArrayList<PropertyChangeListener>();

  private List<ObjektInitEventListener> initListener = new ArrayList<ObjektInitEventListener>();

  private List<ObjektDestroyEventListener> objDestroyListener = new ArrayList<ObjektDestroyEventListener>();

  private List<ModuleEventListener> moduleEventListeners = new ArrayList<ModuleEventListener>();

  private boolean debug = false;

  public DataBus() {
    logLine("constructor");
    initDataCollector();
    initLogger(LoggerType.DEFAULT_LOGGER);
  }

  public DataBus(int loggerType) {
    logLine("constructor " + loggerType);
    initDataCollector();
    initLogger(loggerType);
  }

  /**
   * Initializes the DataBus. Called from AbstractSimulator.initialize()
   */
  @Override
  public void initialize() {
    if (this.logger != null) {
      this.logger.initialize();
    }
    this.dataCollector.initialize();
  }

  /**
   * Initializes the DataCollector used for XMLFullLogger..
   */
  private void initDataCollector() {
    this.dataCollector = new DataCollector();
    this.addCollectionEventListener(this.dataCollector);
    this.addCollectionInitEventListener(this.dataCollector);
    this.addDestroyObjektEventListener(this.dataCollector);
    this.addObjektInitEventListener(this.dataCollector);
    this.addPropertyChangeListener(this.dataCollector);
  }

  @SuppressWarnings("unused")
  private void resetListeners() {
    this.collectionListener.clear();
    this.collectionInitEventListener.clear();
    this.objDestroyListener.clear();
    this.initListener.clear();
    this.propertyChangeListener.clear();
  }

  // //////////////////////
  // Logger

  /**
   * Initializes the logger used in the DataBus instance. This function is not
   * available in the DataBusInterface, only in DataBus itself.
   * 
   * @param loggerType
   *          the type of the Logger
   */
  public void initLogger(int loggerType) {
    int oldType = this.loggerType;
    this.loggerType = loggerType;
    try {
      this.logger = createLogger(loggerType);
      logLine("replaced old logger (" + oldType + ") with new type "
          + loggerType + " " + logger.getClass().getSimpleName());
    } catch (Exception e) {
      try {
        this.logger = createLogger(LoggerType.DEFAULT_LOGGER);
        logLine("replaced old logger (" + oldType + ") with new type "
            + loggerType + " " + logger.getClass().getSimpleName());
      } catch (Exception e2) {
        System.err.println("Unknown LoggerType!");
        this.logger = null;
      }
    }
  }

  /**
   * Creates a new logger instance of the given type
   * 
   * @param loggerType
   * @return a new Logger instance
   * @throws Exception
   *           if an invalid type was specified
   */
  private Logger createLogger(int loggerType) throws Exception {
    Logger logger;
    switch (loggerType) {
    case LoggerType.FULL_XML_LOGGER:
      logger = new XMLFullLogger();
      break;
    case LoggerType.MEMORY_LOGGER:
      logger = new MemoryLogger();
      break;
    case LoggerType.OBSERVER_LOGGER:
      logger = new SimObserver();
      break;
    case LoggerType.WEB_LOGGER:
      logger = new WebLogger();
      break;
    default:
      throw new Exception("Unknown LoggerType!");
    }
    logger.setDataCollector(this.dataCollector);
    return logger;
  }

  public void replaceLogger(int loggerType) {
    this.initLogger(loggerType);
  }

  /**
   * Logger is only available for direct instances of DataBus. This method is
   * not published in DataBusInterface.
   * 
   * @return
   */
  public Logger getLogger() {
    return logger;
  }

  @Override
  public int getLoggerType() {
    return this.loggerType;
  }

  private void logLine(String line) {
    if (debug)
      System.out.println("DataBus: " + line);
  }

  /**
   * Enable the DataStatCollector module. This is used to record the development
   * of agent properties over time.
   */
  public void enableDataStatCollector() {
    if (this.dataStatCollector == null) {
      this.dataStatCollector = new DataStatCollector();
      this.addPropertyChangeListener(this.dataStatCollector);
      this.addObjektInitEventListener(this.dataStatCollector);
      this.addDestroyObjektEventListener(this.dataStatCollector);
      logLine("Enabled DataStatCollector.");
    }
  }

  /**
   * Disable the DataStatCollector and remove it as listener.
   */
  public void disableDataStatCollector() {
    if (this.dataStatCollector != null) {
      this.removePropertyChangeListener(this.dataStatCollector);
      this.removeObjektInitEventListener(this.dataStatCollector);
      this.removeDestroyObjektEventListener(this.dataStatCollector);
      logLine("Disabled DataStatCollector.");
    }
    this.dataStatCollector = null;
  }

  /**
   * This method will add an external created events to the databus collector.
   * These are used by the environment simulator. For performance reasons this
   * method has to be used only if is really necessary. Instead, use
   * <code>addEvents(List<EnvironmentEvent> envEvents)</code> when possible.
   * 
   * @param events
   *          the list with events
   **/
  public void addEvent(EnvironmentEvent envEvent) {
    this.envEvents.add(envEvent);
  }

  /**
   * This method will add a List of external created events to the databus
   * collector. These are used by the environment simulator. FOr performance
   * reasons this method is preferred in place of
   * <code>addEvent(EnvironmentEvent)</code>.
   * 
   * @param events
   *          the list with events
   **/
  public void addEvents(List<EnvironmentEvent> envEvents) {
    this.envEvents.addAll(envEvents);
  }

  /**
   * External modules provides Environment Events that are collected by the data
   * bus. This method is getting all collected events until the moment of the
   * call of this method. Please note that is called in the middle of a step,
   * then not all possible events may be here while some of them may be created
   * later in the current step.
   * 
   * @return external created Environment Events list
   */
  public List<EnvironmentEvent> getEvtEvents() {
    return this.envEvents;
  }

  /**
   * Clear the events collected by the databus collector. This is done so on the
   * next step these events are "forgotten".
   */
  public void clearEvtEvents() {
    this.envEvents = new ArrayList<EnvironmentEvent>();
  }

  /**
   * If DataStatCollector enabled, this method returns statistics of the given
   * step in JSON format as a string.
   * 
   * @param step
   * @return statistics in JSON format
   */
  public String getStatistics(long step) {
    if (this.dataStatCollector != null)
      return dataStatCollector.getStatistics(step);
    return null;
  }

  @Override
  public void addModuleEventListener(ModuleEventListener moduleEventListener) {
    this.moduleEventListeners.add(moduleEventListener);
  }

  @Override
  public void removeModuleEventListener(ModuleEventListener moduleEventListener) {
    this.moduleEventListeners.remove(moduleEventListener);
  }

  /**
   * Add a new simulation event listener
   * 
   * @param simulationEventListener
   *          the simulation event listener object to add
   */
  public void addSimulationEventListener(
      SimulationEventListener simulationEventListener) {
    simulationEventListeners.add(simulationEventListener);
  }

  /**
   * Remove a simulation event listener
   * 
   * @param simulationEventListener
   *          the simulation event listener object to remove
   */
  public void removeSimulationEventListener(
      SimulationEventListener simulationEventListener) {
    simulationEventListeners.remove(simulationEventListener);
  }

  public void addCollectionEventListener(CollectionEventListener cel) {
    collectionListener.add(cel);
  }

  public void removeCollectionEventListener(CollectionEventListener cel) {
    collectionListener.remove(cel);
  }

  public void collectionEvent(CollectionEvent collectionEvent) {
    for (CollectionEventListener cel : collectionListener) {
      cel.collectionEvent(collectionEvent);
    }
  }

  // ++++++++++ eventforwarding (e.g. gui) +++++++++++++++++++++++++
  public synchronized void addPropertyChangeListener(PropertyChangeListener pcl) {
    propertyChangeListener.add(pcl);
  }

  public synchronized void removePropertyChangeListener(
      PropertyChangeListener pcl) {
    propertyChangeListener.remove(pcl);
  }

  public synchronized void propertyChange(PropertyChangeEvent pce) {
    for (PropertyChangeListener pcl : propertyChangeListener) {
      pcl.propertyChange(pce);
    }
  }

  public void notifyInitialEvent(ObjektInitEvent physObjEvent) {
    for (ObjektInitEventListener pcl : initListener) {
      pcl.objektInitEvent(physObjEvent);
    }
  }

  public void addDestroyObjektEventListener(ObjektDestroyEventListener odel) {
    objDestroyListener.add(odel);
  }

  public void removeDestroyObjektEventListener(ObjektDestroyEventListener odel) {
    objDestroyListener.remove(odel);
  }

  @Override
  public void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent) {
    for (ObjektDestroyEventListener odel : objDestroyListener) {
      odel.objektDestroyEvent(objektDestroyEvent);
    }
  }

  public void addSimulationStepEventListener(SimulationStepEventListener ssel) {
    this.simStepEventListener.add(ssel);
  }

  public void removeSimulationStepEventListener(SimulationStepEventListener ssel) {
    this.simStepEventListener.remove(ssel);
  }

  public void addCollectionInitEventListener(CollectionInitEventListener ciel) {
    collectionInitEventListener.add(ciel);
  }

  public void removeCollectionInitEventListener(CollectionInitEventListener ciel) {
    collectionInitEventListener.remove(ciel);
  }

  @Override
  public void collectionInitEvent(CollectionInitEvent collectionInitEvent) {
    for (CollectionInitEventListener ciel : collectionInitEventListener) {
      ciel.collectionInitEvent(collectionInitEvent);
    }
  }

  public void addObjektInitEventListener(ObjektInitEventListener oiel) {
    initListener.add(oiel);
  }

  public void removeObjektInitEventListener(ObjektInitEventListener oiel) {
    initListener.remove(oiel);
  }

  @Override
  public void objektInitEvent(ObjektInitEvent objInitEvent) {
    for (ObjektInitEventListener oiel : initListener) {
      oiel.objektInitEvent(objInitEvent);
    }
  }

  /***********************
   **** Notifications ****
   ***********************/

  // notifications comming from modules

  @Override
  public void notifyModuleEvent(ModuleEvent moduleEvent) {
    for (ModuleEventListener moduleEventListener : this.moduleEventListeners) {
      moduleEventListener.moduleEvent(moduleEvent);
    }
  }

  /*** SIMULATION: init, start, pause, stop ***/
  @Override
  public void notifyStart() {
    logLine("notifyStart");
    if (logger != null)
      logger.notifyStart();
  }

  @Override
  public void notifySimulationStart(long startTime, long steps) {
    logLine("notifySimulationStart");

    if (logger != null)
      logger.notifySimulationStart(startTime, steps);

    // notify all listeners that the simulation was started
    for (SimulationEventListener simulationEventListener : this.simulationEventListeners) {
      simulationEventListener.simulationStarted();
    }
  }

  @Override
  public void notifySimulationPaused(boolean paused) {
    // notify all listeners that the simulation was paused/continued
    for (SimulationEventListener simulationEventListener : this.simulationEventListeners) {
      simulationEventListener.simulationPaused(paused);
    }
  }

  @Override
  public void notifySimulationEnd() {
    logLine("notifyEnd");
    if (logger != null)
      logger.notifyEnd();

    // announce all listeners that the simulation was stopped/finished
    for (SimulationEventListener simulationEventListener : this.simulationEventListeners) {
      simulationEventListener.simulationEnded();
    }
  }

  @Override
  public void notifyInitialisation(InitialState initialState) {
    logLine("notifyInitialisation");
    if (logger != null)
      logger.notifyInitialisation();

    // announce all listeners that the simulation was initialized
    for (SimulationEventListener simulationEventListener : this.simulationEventListeners) {
      simulationEventListener.simulationInitialize(initialState);
    }
  }

  @Override
  public void notifySimulationInfo(SimulationEvent simulationEvent) {
    // announce all listeners that the simulation was stopped/finished
    for (SimulationEventListener simulationEventListener : this.simulationEventListeners) {
      simulationEventListener.simulationInfosEvent(simulationEvent);
    }
  }

  /*** SIMULATION STEP: start, end ***/
  @Override
  public void notifySimStepStart(Long newSimulationStep) {
    logLine("notifySimStepStart");
    if (dataStatCollector != null)
      dataStatCollector.notifyStepRunning(true);
    if (logger != null)
      logger.notifySimStepStart(newSimulationStep);
    if (dataStatCollector != null)
      dataStatCollector.setCurrentSimulatorStep(newSimulationStep);

    // announce all listeners about the fact that a new step starts
    for (SimulationStepEventListener simulationStepEventListener : this.simStepEventListener) {
      simulationStepEventListener.simulationStepStart(newSimulationStep);
    }
  }

  @Override
  public void notifySimStepEnd() {
    logLine("notifySimStepEnd");
    if (dataStatCollector != null)
      dataStatCollector.notifyStepRunning(false);
    if (logger != null) {
      logger.notifySimStepEnd();
      SimulationStep lastSimulationStep = logger.getLastSimulationStep();

      // announce all listeners about the fact that the step is ending
      if (lastSimulationStep != null) {
        SimulationStepEvent thisStep = new SimulationStepEvent(
            lastSimulationStep);
        for (SimulationStepEventListener simulationStepEventListener : this.simStepEventListener) {
          simulationStepEventListener.simulationStepEnd(thisStep);
        }
      }
    }
  }

  /*** ACTIVITIES ***/

  @Override
  public void notifyActivityStart(Activity activity,
      List<EnvironmentEvent> envEventList,
      ActivityEndEvent activityFinalizeEvent) {
    if (logger != null)
      logger.notifyActivityStart(activity, envEventList, activityFinalizeEvent);
  }

  @Override
  public void notifyActivityStop(Activity activity,
      List<EnvironmentEvent> envEventList) {
    if (logger != null)
      logger.notifyActivityStop(activity, envEventList);
  }

  /*** AGENT SIMULATOR ***/
  @Override
  public void notifyAgentSimulatorsResultingStateChanges() {
    if (logger != null)
      logger.notifyAgentSimulatorsResultingStateChanges();
  }

  @Override
  public void notifyAgentSimulatorStep(JsonData agentStepLog) {
    if (logger != null)
      logger.notifyAgentSimulatorStep(agentStepLog);
  }

  @Override
  public void notifyAgentSimulatorStep(AgentSimulatorStep agentSimulatorStep) {
    if (this.logger != null)
      this.logger.notifyAgentSimulatorStep(agentSimulatorStep);
  }

  /*** ENVIRONMENT EVENTS ***/
  @Override
  public void notifyEnvEvent(EnvironmentEvent envEvent) {
    logLine("notifyEnvEvent");
    if (logger != null)
      logger.notifyEnvEvent(envEvent);

    // notify all listeners about this environment event
    for (SimulationEventListener simulationJavaEventListener : this.simulationEventListeners) {
      simulationJavaEventListener.simulationEnvironmentEventOccured(envEvent);
    }
  }

  @Override
  public void notifyEnvEventResult(EnvironmentRule environmentRule,
      List<EnvironmentEvent> envEventList) {
    logLine("notifyEnvEventResult");
    if (logger != null)
      logger.notifyEnvEventResult(environmentRule, envEventList);
  }

  /*** SIMULATION SCENARIO ***/
  @Override
  public void notifySimulationScenario(ScenarioInfos scenarioInfos,
      GeneralSimulationParameters aorSimParameters,
      Map<String, String> modelParamMap) {
    logLine("notifySimulationScenario");
    if (logger != null)
      logger.notifySimulationScenario(scenarioInfos, aorSimParameters,
          modelParamMap);
  }

  @Override
  public void notifySpaceModel(GeneralSpaceModel aorSpaceModel) {
    logLine("notifySpaceModel");
    if (logger != null)
      logger.notifySpaceModel(aorSpaceModel);
  }

  /*** STATISTICS ***/
  @Override
  public void notifyStatistics(List<AbstractStatisticsVariable> statisticList) {
    logLine("notifyStatistics");
    if (logger != null)
      logger.notifyStatistics(statisticList);
  }

  /*** LOGGER STRUCTURE & STUFF ***/
  public class LoggerType {
    public static final int NO_LOGGER = -1;
    public static final int FULL_XML_LOGGER = 0;
    public static final int MEMORY_LOGGER = 1;
    public static final int OBSERVER_LOGGER = 2;
    public static final int WEB_LOGGER = 3;

    // is used to set the fallbacks for loggers
    public static final int DEFAULT_LOGGER = OBSERVER_LOGGER;
  }

  public class LoggerEvent {
    public static final String EVENT_INFOS = "event_infos";
  }

  @Override
  public void notifyProjectDirectoryChange(File projectDirectory) {
    logLine("notifyProjectDirectoryChange");
    // announce all listeners that the simulation was initialized
    for (SimulationEventListener simulationEventListener : this.simulationEventListeners) {
      simulationEventListener
          .simulationProjectDirectoryChanged(projectDirectory);
    }
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code notifyDomOnlyInitialization} from super
   * class
   * 
   * 
   * 
   * @param simulationDescription
   */
  @Override
  public void notifyDomOnlyInitialization(
      SimulationDescription simulationDescription) {
    logLine("notifyDomOnlyInitialisation");
    // announce all listeners that the simulation was initialized
    for (SimulationEventListener simulationEventListener : this.simulationEventListeners) {
      simulationEventListener
          .simulationDomOnlyInitialization(simulationDescription);
    }

  }
}
