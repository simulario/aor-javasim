/**
 * 
 */
package aors.statistics;

import java.util.HashMap;
import java.util.Map;

import aors.model.envsim.EnvironmentSimulator;

/**
 * @author Jens Werner
 * 
 */
public abstract class GeneralStatistics {

  private Map<String, AbstractStatisticsVariable> statisticVariables = new HashMap<String, AbstractStatisticsVariable>();

  protected EnvironmentSimulator environmentSimulator;

  /**
   * @param environmentSimulator
   *          the environmentSimulator to set
   */
  public void setEnvironmentSimulator(EnvironmentSimulator environmentSimulator) {
    this.environmentSimulator = environmentSimulator;
  }

  public void addStatisticVariable(
      AbstractStatisticsVariable abstractStatisticsVariable) {
    this.statisticVariables.put(abstractStatisticsVariable.getName(),
        abstractStatisticsVariable);
  }

  public Map<String, AbstractStatisticsVariable> getStatisticVariables() {
    return this.statisticVariables;
  }

}
