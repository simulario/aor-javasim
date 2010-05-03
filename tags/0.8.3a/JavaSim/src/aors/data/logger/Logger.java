/**
 * 
 */
package aors.data.logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import aors.GeneralSimulationModel;
import aors.ScenarioInfos;
import aors.data.DataCollector;
import aors.logger.model.AgentSimulatorStep;
import aors.logger.model.SimulationStep;
import aors.model.envevt.ActivityEndEvent;
import aors.model.envevt.EnvironmentEvent;
import aors.model.envevt.activity.Activity;
import aors.model.envsim.EnvironmentRule;
import aors.statistics.AbstractStatisticsVariable;
import aors.util.JsonData;

/**
 * @author Jens Werner
 * 
 */
public abstract class Logger implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 2442543662680146164L;

  /*
   * Contains a list with state changes in objects
   */
  protected DataCollector dataCollector = null;

  protected String modelName;
  protected String modelTitle;
  protected String scenarioName;
  protected String scenarioTitle;

  /**
   * The last constructed SimulationStep that is used for building
   * SimulationStepEvents outside the Logger.
   */
  protected SimulationStep lastSimulationStep = null;

  /*
   * Contains a list with statistic informations
   */
  protected List<AbstractStatisticsVariable> statisticVariables = new ArrayList<AbstractStatisticsVariable>();

  /*
   * Write the output stream to a file
   */
  // protected BufferedWriter writer;
  protected String fileName = "SimulationLog.xml";
  protected String cellInitFileName = "CellInit.xml";
  protected String path = ".";

  public void setDataCollector(DataCollector dc) {
    this.dataCollector = dc;
  }

  /**
   * Comment: use this method to reset the current object(-implementation)
   */
  public abstract void initialize();

  /**
   * Write the opening root element-tag for logger
   */
  public abstract void notifyStart();

  /**
   * The Simulation was started
   */
  public abstract void notifySimulationStart(long startTime, long steps);

  /**
   * Finds out modelName and modelTitle as well as scenario name/title.
   * 
   * @param scenarioInfos
   * @param aorSimParameters
   * @param aorSimModel
   */
  public void notifySimulationScenario(ScenarioInfos scenarioInfos,
      aors.GeneralSimulationParameters aorSimParameters,
      Map<String, String> modelParamMap) {

    String modelName = modelParamMap
        .get(GeneralSimulationModel.ModelParameter.MODEL_NAME.toString());
    if (modelName != null)
      this.modelName = modelName;

    String modelTitle = modelParamMap
        .get(GeneralSimulationModel.ModelParameter.MODEL_TITLE.toString());
    if (modelTitle != null)
      this.modelTitle = modelTitle;

    this.scenarioName = scenarioInfos.getScenarioName();
    this.scenarioTitle = scenarioInfos.getScenarioTitle();
  }

  public abstract void notifySpaceModel(aors.GeneralSpaceModel aorSpaceModel);

  /**
   * 
   * This method logs all initialized PhysObjects It is necessary to call this,
   * AFTER finishing the PhysObjectCreation
   * 
   * 
   */
  public abstract void notifyInitialisation();

  /**
   * start a simulation step; creates a new SimulationStepJaxbObject
   * 
   * @param aorStepTime
   */
  public abstract void notifySimStepStart(Long aorStepTime);

  /**
   * 
   */
  public abstract void notifyEnvEvent(EnvironmentEvent envEvent);

  /**
   * 
   */
  public abstract void notifyActivityStart(Activity activity,
      Collection<EnvironmentEvent> envEventList,
      ActivityEndEvent activityFinalizeEvent);

  /**
   * 
   */
  public abstract void notifyActivityStop(Activity activity,
      Collection<EnvironmentEvent> envEventList);

  /**
   * 
   */
  public abstract void notifyEnvEventResult(EnvironmentRule environmentRule,
      Collection<EnvironmentEvent> envEventList);

  /**
   * log the obj-statechanges triggered by the AgtSims (only PI-Agents)
   */
  public abstract void notifyAgentSimulatorsResultingStateChanges();

  /**
   * finished a simulation step; write the SimulationStepJaxbObject
   */
  public abstract void notifySimStepEnd();

  /**
   * Gets the last simulation step object.
   * 
   * @return the last simulation step known.
   */
  public SimulationStep getLastSimulationStep() {
    SimulationStep ss = this.lastSimulationStep;

    this.lastSimulationStep = null;

    return ss;
  }

  /**
   * Logs a single agentSimulatorStep
   * 
   * @param agentSubject
   *          which has a custom log
   */
  public abstract void notifyAgentSimulatorStep(JsonData agentStepLog);

  public abstract void notifyAgentSimulatorStep(
      AgentSimulatorStep agentSimulatorStep);

  /**
   * don't forget to close the writer in this method
   */
  public abstract void notifyEnd();

  public void notifyStatistics(List<AbstractStatisticsVariable> statisticList) {
    this.statisticVariables = statisticList;
  }

  /**
   * @return the statisticVariables
   */
  public List<AbstractStatisticsVariable> getStatisticVariables() {
    return statisticVariables;
  }

  public String getModelName() {
    return modelName;
  }

  public String getModelTitle() {
    return modelTitle;
  }

  public String getScenarioName() {
    return scenarioName;
  }

  public String getScenarioTitle() {
    return scenarioTitle;
  }

  /**
   * @return the fileName
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * @param fileName
   *          the fileName to set
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
    this.cellInitFileName = fileName.substring(0, fileName.lastIndexOf(".xml"))
        + "_GridInit.xml";
  }

  /**
   * @return the path
   */
  public String getPath() {
    return path;
  }

  /**
   * @param path
   *          the path to set
   */
  public void setPath(String path) {
    this.path = path;
  }

}
