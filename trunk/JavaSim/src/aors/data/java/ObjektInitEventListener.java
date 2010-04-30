/**
 * 
 */
package aors.data.java;

import java.util.EventListener;

/**
 * @author Jens Werner
 * @since June 23, 2008
 * @version 0.1
 * 
 */

/**
 * A "PhysObjInit" event gets fired whenever a physObj was created.
 */
public interface ObjektInitEventListener extends EventListener {

  /**
   * This method gets called when a physObj was created.
   * 
   * @param objInitEvent
   *          A PhysObjInitEvent object describing the event source, a HashMap
   *          with properties and values (<String>,<String>)
   */
  void objektInitEvent(ObjektInitEvent objInitEvent);

}
