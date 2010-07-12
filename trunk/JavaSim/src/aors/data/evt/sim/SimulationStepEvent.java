/**
 * 
 */
package aors.data.evt.sim;

import java.util.EventObject;

import aors.logger.model.SimulationStep;

/**
 * @author Jens Werner
 * 
 */
public class SimulationStepEvent extends EventObject {

  public SimulationStepEvent(Object source) {
    super(source);
  }

  public SimulationStep getSimulationStep() {
    return (SimulationStep) this.getSource();
  }

  /**
   * 
   */
  private static final long serialVersionUID = 2099688371274455946L;

}
