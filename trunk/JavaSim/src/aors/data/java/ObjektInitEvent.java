/**
 * 
 */
package aors.data.java;

import java.util.EventObject;

/**
 * PhysObjInitEvent
 * 
 * It is a container for initial events that are created be a creation of a new
 * physical object
 * 
 * @author Jens Werner
 * @since June 23, 2008
 * @version 0.1
 * 
 */
public class ObjektInitEvent extends EventObject {

  /**
   * 
   */
  private static final long serialVersionUID = 2702789578826244014L;

  public ObjektInitEvent(Object source) {
    super(source);
  }

}
