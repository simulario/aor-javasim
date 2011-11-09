package aors.data;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import java.util.Map;

import aors.GeneralSimulationParameters;
import aors.GeneralSpaceModel;
import aors.ScenarioInfos;
import aors.controller.InitialState;
import aors.controller.SimulationDescription;
import aors.data.evt.ControllerEventListener;
import aors.data.evt.sim.CollectionEventListener;
import aors.data.evt.sim.CollectionInitEventListener;
import aors.data.evt.sim.ObjektDestroyEventListener;
import aors.data.evt.sim.ObjektInitEventListener;
import aors.data.evt.sim.SimulationEvent;
import aors.data.evt.sim.SimulationEventListener;
import aors.data.evt.sim.SimulationStepEventListener;
import aors.logger.model.AgentSimulatorStep;
import aors.model.envevt.ActivityEndEvent;
import aors.model.envevt.EnvironmentEvent;
import aors.model.envevt.activity.Activity;
import aors.model.envsim.EnvironmentRule;
import aors.module.evt.ModuleEvent;
import aors.module.evt.ModuleEventListener;
import aors.statistics.AbstractStatisticsVariable;
import aors.util.JsonData;

/**
 * The DataBusInterface offers a reduced set of functions to the world. It will
 * be used all over the simulator.
 * 
 * @author Christian Noack
 * @since 04/August/2009
 * 
 */

public interface DataBusInterface extends CollectionEventListener,
    PropertyChangeListener, ObjektInitEventListener,
    CollectionInitEventListener, ObjektDestroyEventListener,
    ControllerEventListener {

  public int getLoggerType();

  // //////////////////////
  // Notifications

  public void initialize();

  /**
   * This is called when the hole thing starts. This does not means the
   * simulation start.
   */
  public void notifyStart();

  /**
   * This method is called whenever the simulation starts
   * 
   * @param startTime
   *          the start time
   * @param steps
   *          the number of steps
   */
  public void notifySimulationStart(long startTime, long steps);

  /**
   * This is called whenever the simulation is paused
   * 
   * @param paused
   *          the pause state: pause or continue
   */
  public void notifySimulationPaused(boolean paused);

  /**
   * This method is called whenever the simulation ends
   */
  public void notifySimulationEnd();

  /**
   * this method is called whenever the simulation is initialized
   * 
   * @param initialState
   *          the initialState object
   */
  public void notifyInitialisation(InitialState initialState);

  /**
   * This method is called whenever the DOM is initialized or reinitialized.
   * 
   * @param dom
   *          the current dom
   */
  public void notifyDomOnlyInitialization(
      SimulationDescription simulationDescription);

  /**
   * This method is called whenever a simulation step starts
   * 
   * @param newSimulationStep
   *          the step number that will start
   */
  public void notifySimStepStart(Long newSimulationStep);

  /**
   * This method is called whenever the a simulation step ends
   */
  public void notifySimStepEnd();

  /**
   * This method is called whenever the project directory is changed
   */
  public void notifyProjectDirectoryChange(File projectDirectory);

  /**
   * This method is called whenever some infos have to be sent to listeners
   * 
   * @param simulationEvent
   *          the event to be sent. This contains also the infos
   */
  public void notifySimulationInfo(SimulationEvent simulationEvent);

  public void notifySimulationScenario(ScenarioInfos scenarioInfos,
      GeneralSimulationParameters aorSimParameters,
      Map<String, String> modelParamMap);

  public void notifySpaceModel(GeneralSpaceModel aorSpaceModel);

  public void notifyAgentSimulatorStep(JsonData agentStepLog);

  public void notifyAgentSimulatorStep(AgentSimulatorStep agentSimulatorStep);

  public void notifyAgentSimulatorsResultingStateChanges();

  public void notifyEnvEvent(EnvironmentEvent envEvent);

  public void notifyEnvEventResult(EnvironmentRule environmentRule,
      List<EnvironmentEvent> envEventList);

  public void notifyActivityStart(Activity activity,
      List<EnvironmentEvent> envEventList,
      ActivityEndEvent activityFinalizeEvent);

  public void notifyActivityStop(Activity activity,
      List<EnvironmentEvent> envEventList);

  public void notifyStatistics(List<AbstractStatisticsVariable> statisticList);

  public void addObjektInitEventListener(ObjektInitEventListener pel);

  public void addSimulationEventListener(SimulationEventListener sel);

  public void addSimulationStepEventListener(SimulationStepEventListener ssel);

  public void addDestroyObjektEventListener(ObjektDestroyEventListener odel);

  /**
   * Notify a module events listener about an incoming module event.
   * 
   * @param moduleEvent
   *          the incoming module event
   */
  public void notifyModuleEvent(ModuleEvent moduleEvent);

  /**
   * Add a new module event listener
   * 
   * @param moduleEventListener
   *          the module event listener object to add
   */
  public void addModuleEventListener(ModuleEventListener moduleEventListener);

  /**
   * Remove a module event listener
   * 
   * @param moduleEventListener
   *          the module event listener object to remove
   */
  public void removeModuleEventListener(ModuleEventListener moduleEventListener);

  /**
   * Add a new listener for ControllerEvent(s)
   * 
   * @param controllerEventListener the listener to add
   */
  public void addControllerEventListener(
      ControllerEventListener controllerEventListener);

  /**
   * Remove an listener for ControllerEvent(s)
   * 
   * @param controllerEventListener the listener to remove
   */
  public void removeControllerEventListener(
      ControllerEventListener controllerEventListener);
}
