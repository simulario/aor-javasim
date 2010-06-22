/**
 * 
 */
package aors.statistics;

import java.util.ArrayList;
import java.util.List;

import aors.model.envsim.EnvironmentSimulator;

/**
 * @author Jens Werner
 * 
 */
public abstract class GeneralStatistics {

  protected EnvironmentSimulator environmentSimulator;

  /**
   * a list with variables with expressions and computed in every step
   * computeOnlyAtEnd = false
   */
  private List<AbstractStatisticsVariable> exprVarStep = new ArrayList<AbstractStatisticsVariable>();

  /**
   * a list with variables with expressions and computed at the end of the
   * simulation computeOnlyAtEnd = true
   */
  private List<AbstractStatisticsVariable> exprVarSim = new ArrayList<AbstractStatisticsVariable>();

  public void addExprVarStep(AbstractStatisticsVariable v) {
    this.exprVarStep.add(v);
  }

  public void addExprVarSim(AbstractStatisticsVariable v) {
    this.exprVarSim.add(v);
  }

  /**
   * @return the exprVarStep
   */
  public List<AbstractStatisticsVariable> getExprVarStep() {
    return exprVarStep;
  }

  /**
   * @return the exprVarSim
   */
  public List<AbstractStatisticsVariable> getExprVarSim() {
    return exprVarSim;
  }

  /**
   * @param environmentSimulator
   *          the environmentSimulator to set
   */
  public void setEnvironmentSimulator(EnvironmentSimulator environmentSimulator) {
    this.environmentSimulator = environmentSimulator;
  }

}
