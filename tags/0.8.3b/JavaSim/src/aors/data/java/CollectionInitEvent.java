package aors.data.java;

import java.util.EventObject;

/**
 * CollectionInitEvent
 * 
 * It is a container for initial events that are created be a creation of a new
 * collection
 * 
 * @author Jens Werner
 * @since Nov 5, 2008
 * @version 0.1
 * 
 */
public class CollectionInitEvent extends EventObject {

  public CollectionInitEvent(Object source) {
    super(source);
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

}
