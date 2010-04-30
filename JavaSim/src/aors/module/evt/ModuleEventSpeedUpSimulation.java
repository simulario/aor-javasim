package aors.module.evt;

/**
 * Defines a built-in module event type that will "recommend" to the listeners
 * to speed up the simulation if possible. Notice that the listener will do the
 * speed up action only if is his problem to do this and it can do it.
 * 
 * @author Mircea Diaconescu
 * @since 25 April 2010
 * 
 */

public class ModuleEventSpeedUpSimulation extends ModuleEvent {

  private static final long serialVersionUID = -4636496836685016948L;

  /**
   * Create a new ModuleEventSpeedUpSimulation object.
   * 
   * @param source
   *          the source of the event
   */
  public ModuleEventSpeedUpSimulation(Object source) {
    super(source);
  }
}
