/**
 * 
 */
package aors.model.envsim;

import java.util.ArrayList;
import java.util.List;

import aors.model.envevt.activity.Activity;

/**
 * @author Jens Werner
 * 
 */
public abstract class AgentObject extends Objekt {

  private List<Activity> busyWithActivity = null;

  /**
   * This constructor is not supposed to initialize any data but just to create
   * an instance with no value for its fields, therefore the
   * <code>busyWithActivity</code> will be null in this point.
   */
  protected AgentObject() {
    super();
  }

  protected AgentObject(long id) {
    super(id);
    this.busyWithActivity = new ArrayList<Activity>();
  }

  protected AgentObject(long id, String name) {
    super(id, name);
    this.busyWithActivity = new ArrayList<Activity>();
  }

  public boolean setBusyWithActivity(Activity activity) {
    return this.busyWithActivity.add(activity);
  }

  public boolean setFinishedActivity(Activity activity) {
    return this.busyWithActivity.remove(activity);
  }

  public boolean isBusyWithActivity(Activity activity) {
    return this.isBusyWithActivity(activity.getName());
  }

  public boolean isBusyWithActivity(String activityName) {
    boolean result = false;

    for (Activity activity : this.busyWithActivity) {
      if (activity.getName().equals(activityName)) {
        return true;
      }
    }

    return result;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code busyWithActivity}.
   * 
   * 
   * 
   * @return the {@code busyWithActivity}.
   */
  public List<Activity> getBusyWithActivity() {
    return busyWithActivity;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code busyWithActivity}.
   * 
   * 
   * 
   * @return the names of {@code busyWithActivity}.
   */
  public List<String> getBusyWithActivityNames() {
    List<String> busyWithActivities = new ArrayList<String>();

    for (Activity activity : this.busyWithActivity) {
      busyWithActivities.add(activity.getName());
    }

    return busyWithActivities;
  }

}
