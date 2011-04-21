package aors.module.evt;

/**
 * The ModuleEventListener offers an interface for module events listeners.
 * While modules may send events to the simulator, this interface must be
 * implemented by any module event listeners. be used all over the simulator.
 * 
 * @author Mircea Diaconescu
 * @since 25 April 2010
 * 
 */

public interface ModuleEventListener {

  /**
   * 
   * Modules may send back events for informing listeners about some states.
   * This method is called whenever a such event must be trown.
   * 
   * @param moduleEvent
   *          the module event about which the listeners are notified.
   */
  public void moduleEvent(ModuleEvent moduleEvent);
}
