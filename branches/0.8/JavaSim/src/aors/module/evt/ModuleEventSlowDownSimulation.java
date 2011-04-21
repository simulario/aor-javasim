package aors.module.evt;

/**
 * Defines a built-in module event type that will "recommend" to the listeners
 * to slow down the simulation if possible. Notice that the listener will do the
 * slow down action only if is his problem to do this and it can do it.
 * 
 * @author Mircea Diaconescu
 * @since 25 April 2010
 * 
 */
public class ModuleEventSlowDownSimulation extends ModuleEvent {
  
  private static final long serialVersionUID = 6282672236358899190L;

  /**
   * Create a new ModuleEventSlowDownSimulation object.
   * 
   * @param source
   *          the source of the event
   */
  public ModuleEventSlowDownSimulation(Object source) {
    super(source);
  }
}
