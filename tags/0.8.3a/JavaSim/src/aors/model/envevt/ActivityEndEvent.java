/**
 * 
 */
package aors.model.envevt;

import aors.model.envevt.activity.Activity;

/**
 * ActivityFinalizeEvent
 * 
 * @author Jens Werner
 * @since 06 April 2009
 * @version $Revision$
 */
public class ActivityEndEvent extends CausedEvent {

  private Activity activity;

  private String activityType;

  private Object __correlationValue;

  // this constructor is only used to predefine a possible triggering event in
  // rules
  public ActivityEndEvent() {
    super();
  }

  public ActivityEndEvent(Activity activity, long occurrenceTime) {
    super(occurrenceTime);
    this.activity = activity;
    this.activityType = activity.getType();
  }

  public ActivityEndEvent(String activityType, long occurenceTime) {
    super(occurenceTime);
    this.activityType = activityType;
  }

  public Activity getActivity() {
    return this.activity;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code activityType}.
   * 
   * 
   * 
   * @return the {@code activityType}.
   */
  public String getActivityType() {
    return activityType;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code correlationValue}.
   * 
   * 
   * 
   * @return the {@code correlationValue}.
   */
  public Object getCorrelationValue() {
    return __correlationValue;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code correlationValue}.
   * 
   * 
   * 
   * @param correlationValue
   *          The {@code correlationValue} to set.
   */
  public void setCorrelationValue(Object correlationValue) {
    this.__correlationValue = correlationValue;
  }

}
