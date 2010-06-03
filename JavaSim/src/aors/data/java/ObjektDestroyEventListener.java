/**
 * 
 */
package aors.data.java;

import java.util.EventListener;

/**
 * @author Jens Werner
 * 
 */
public interface ObjektDestroyEventListener extends EventListener {

  /**
   * This method gets called when a physObj was destroyed.
   * 
   * @param objektDestroyEvent
   */
  void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent);

}
