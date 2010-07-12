package aors.data.evt;

/**
 * Defines the interface of listeners for ControllerEvent(s)
 * 
 * @author Mircea Diaconescu
 * @since 12 July 2010
 * 
 */
public interface ControllerEventListener {
  /**
   * This method is called whenever a listener of ControllerEvent(s) needs to be
   * notified about such event occurence
   * 
   * @param event the ControllerEvent that occurs
   */
  public void notifyEvent(ControllerEvent event);

}
