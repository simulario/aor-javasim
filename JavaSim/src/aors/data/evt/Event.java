package aors.data.evt;

import java.util.EventObject;

/**
 * This class is an abstract event type that is used to be spread from the
 * simulator system to modules and/or any registered listener
 * 
 * @author Mircea Diaconescu
 * @since 12 July 2010
 */

public abstract class Event extends EventObject {

  private static final long serialVersionUID = 2102862903858093807L;

  private Object value;

  /**
   * Create a new Event object.
   * 
   * @param source
   *          the source of the event
   */
  public Event(Object source) {
    super(source);
  }

  /**
   * create new Event object
   * 
   * @param source
   *          the source of the event
   * @param value
   *          the event value that has to be send
   */
  public Event(Object source, Object value) {
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
