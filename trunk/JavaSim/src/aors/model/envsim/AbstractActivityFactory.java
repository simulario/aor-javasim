/**
 * 
 */
package aors.model.envsim;

import java.util.ArrayList;

import aors.model.envevt.activity.Activity;

/**
 * @author Jens Werner
 * 
 */
public abstract class AbstractActivityFactory {

  public abstract ArrayList<Activity> getActivities(String envEventSimpleName,
      EnvironmentSimulator environmentSimulator);

  public abstract Activity getActivityByType(String activityType,
      EnvironmentSimulator environmentSimulator);

}
