/**
 * 
 */
package aors.data.evt.sim;

import java.util.EventObject;

/**
 * @author Jens Werner
 * 
 */
public class ObjektDestroyEvent extends EventObject {

  private long destroyOccurenceTime;

  /**
   * 
   */
  private static final long serialVersionUID = 7501301344965934664L;

  public ObjektDestroyEvent(Object source, long destroyOccurenceTime) {
    super(source);
    this.destroyOccurenceTime = destroyOccurenceTime;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code destroyOccurenceTime}.
   * 
   * 
   * 
   * @return the {@code destroyOccurenceTime}.
   */
  public long getDestroyOccurenceTime() {
    return destroyOccurenceTime;
  }

}
