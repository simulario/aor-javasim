package aors.module.evt;

import java.util.EventObject;

/**
 * Defines the super type that must be implemented by a module event. Modules
 * may send back evens to the listeners. Each module events must inherit this
 * class.
 * 
 * @author Mircea Diaconesc
 * @since 25 April 2010
 * 
 */

public class ModuleEvent extends EventObject {

  private static final long serialVersionUID = -3528190005679590754L;

  private Object value;

  /**
   * Create a new ModuleEvent object.
   * 
   * @param source
   *          the source of the event
   */
  public ModuleEvent(Object source) {
    super(source);
  }

  /**
   * create new ModuleEvent object
   * 
   * @param source
   *          the source of the event
   * @param value
   *          the event value that has to be send
   */
  public ModuleEvent(Object source, Object value) {
    super(source);
    this.value = value;
  }

  /**
   * @return the value
   */
  public Object getValue() {
    return value;
  }

}
