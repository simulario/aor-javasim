/**
 * 
 */
package aors.data.java;

import java.util.EventListener;

/**
 * @author Jens Werner, Mircea Diaconescu
 * @version $Revision: 1.1 $
 */
public interface SimulationStepEventListener extends EventListener {

  /**
   * This method is called whenever a step is starting.
   * 
   * @param stepNumber
   *          - the step number that is just starting
   */
  void simulationStepStart(long stepNumber);

  /**
   * This method is called whenever a step is ending.
   * 
   * @param simulationStepEvent
   *          the start step event
   */
  void simulationStepEnd(SimulationStepEvent simulationStepEvent);

}
