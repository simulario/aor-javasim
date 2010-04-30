/**
 * 
 */
package aors.data.java;

import java.util.EventListener;

/**
 * @author Jens Werner
 * 
 */
public interface CollectionInitEventListener extends EventListener {

  /**
   * This method gets called when a collection was created.
   * 
   * @param collectionInitEvent
   */
  void collectionInitEvent(CollectionInitEvent collectionInitEvent);
}
