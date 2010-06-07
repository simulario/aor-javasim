/**
 * 
 */
package examples.movingcars.controller;

import aors.GeneralSimulationParameters;

/**
 * @author Jens Werner
 * 
 */
@SuppressWarnings("serial")
public class SimulationParameters extends GeneralSimulationParameters {

  /**
   * 
   */
  public static final long STEP_DURATION = 1;

  /**
   * 
   */
  public static final long SIMULATION_STEPS = 3600;

  /**
   * 
   */
  public static final TimeUnit TIME_UNIT = TimeUnit.s;

  /**
   * 
   */
  public static final long RANDOM_SEED = 100;

}
