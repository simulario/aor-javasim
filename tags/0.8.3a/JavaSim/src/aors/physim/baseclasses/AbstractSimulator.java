package aors.physim.baseclasses;

import java.util.List;

import aors.model.AtomicEvent;

/**
 * AbstractSimulator is the baseclass for every simulator of PhySim.
 * 
 * @author Stefan Boecker
 * 
 */
public abstract class AbstractSimulator {

  /**
   * Every simulator in PhySim has to implement this method. It is called in
   * every simulation step. All work of a simulator for one step of the
   * simulation must be done in this method.
   * 
   * @param events
   *          List of current EnvironmentEvents
   * @param currentStep
   *          Current simulation step.
   */
  public abstract void simulate(List<AtomicEvent> events, long currentStep);

}
