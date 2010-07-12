package aors.data.evt;

/**
 * This class is a generic Controller Event type.
 * 
 * @author Mircea Diaconescu
 * @since 12 July 2010
 */

public class ControllerEvent extends Event {

  private static final long serialVersionUID = 4919090129160409145L;

  /** hold the event type, while the object is a generic event type */
  private EventTypeEnum eventType = null;

  /**
   * Create a new event instance
   * 
   * @param source
   *          the source of the event
   */
  public ControllerEvent(EventTypeEnum eventType, Object source) {
    super(source);
    this.eventType = eventType;
  }

  /**
   * Return the event type (that decides what kind of event we have since this
   * is a generic event type)
   * 
   * @return the eventType field value
   */
  public EventTypeEnum getEventType() {
    return this.eventType;
  }

}
