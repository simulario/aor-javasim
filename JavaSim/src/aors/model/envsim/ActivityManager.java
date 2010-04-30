/**
 * 
 */
package aors.model.envsim;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import aors.data.DataBusInterface;
import aors.model.envevt.ActionEvent;
import aors.model.envevt.ActivityEndEvent;
import aors.model.envevt.ActivityStartEvent;
import aors.model.envevt.EnvironmentEvent;
import aors.model.envevt.activity.Activity;

/**
 * @author Jens Werner
 * 
 */
public class ActivityManager {

  private DataBusInterface dataBus;

  /**
   * holds a list with active activities
   */
  private List<Activity> activeActivities = new ArrayList<Activity>();

  private AbstractActivityFactory abstractActivityFactory;

  private EnvironmentSimulator environmentSimulator;

  public ActivityManager(EnvironmentSimulator environmentSimulator,
      DataBusInterface dataBus) {
    this.environmentSimulator = environmentSimulator;
    this.dataBus = dataBus;
  }

  // public boolean removeActiveActivity(AbstractActivity activity) {
  // return this.activeActivities.remove(activity);
  // }

  public ArrayList<EnvironmentEvent> processEvent(
      EnvironmentEvent environmentEvent) {

    ArrayList<EnvironmentEvent> resultingEvents = this
        .startActivitiesByEvent(environmentEvent);
    resultingEvents.addAll(this.terminateActivitiesByEvent(environmentEvent));

    return resultingEvents;
  }

  /**
   * Start activities by an environment event and execute the start-task if they
   * are no activities in the system (abstractActivityFactory = null), do
   * nothing
   * 
   * @param environmentEvent
   * 
   * @return resultingEvents - a List of ActivityFinalizeEvent's
   */
  private ArrayList<EnvironmentEvent> startActivitiesByEvent(
      EnvironmentEvent environmentEvent) {
    ArrayList<EnvironmentEvent> resultingEvents = new ArrayList<EnvironmentEvent>();

    if (this.abstractActivityFactory != null) {

      // with ActivityStartEvent
      if (ActivityStartEvent.class.isInstance(environmentEvent)) {

        ActivityStartEvent activityStartEvent = (ActivityStartEvent) environmentEvent;
        Activity activity = this.abstractActivityFactory.getActivityByType(
            activityStartEvent.getActivityType(), environmentSimulator);

        // set the correlationValue from the activityStartEvent
        if (activity != null
            && activityStartEvent.getCorrelationValue() != null) {
          activity.setStartEventCorrelationProperty("__correlationValue");
          activity
              .setCorrelationValue(activityStartEvent.getCorrelationValue());
        }

        activity.setStartTime(environmentEvent.getOccurrenceTime());
        activity.setCurrentStartEvent(environmentEvent);

        activity.executeStartEffects();

        // get the ActivityEndEvent if is defined (for activities with
        // defined Duration)
        ActivityEndEvent activityEndEvent = activity
            .getActivityEndEvent(environmentEvent.getOccurrenceTime());
        if (activityEndEvent != null)
          resultingEvents.add(activityEndEvent);

        this.activeActivities.add(activity);
        this.dataBus.notifyActivityStart(activity, resultingEvents,
            activityEndEvent);

      } else {

        for (Activity activity : this.abstractActivityFactory.getActivities(
            environmentEvent.getClass().getSimpleName(),
            this.environmentSimulator)) {

          activity.setStartTime(environmentEvent.getOccurrenceTime());
          activity.setCurrentStartEvent(environmentEvent);

          activity.executeStartEffects();

          // get the ActivityEndEvent if is defined (for activities with
          // defined Duration)
          ActivityEndEvent activityEndEvent = activity
              .getActivityEndEvent(environmentEvent.getOccurrenceTime());
          if (activityEndEvent != null)
            resultingEvents.add(activityEndEvent);

          this.activeActivities.add(activity);
          this.dataBus.notifyActivityStart(activity, resultingEvents,
              activityEndEvent);
        }
      }
    }
    return resultingEvents;
  }

  /**
   * This method returns a list with activities, which are finalized by an
   * environmentEvent
   * 
   * @param environmentEvent
   * @return
   */
  private ArrayList<Activity> getActiveActivitiesEndsByEvent(
      EnvironmentEvent environmentEvent) {
    ArrayList<Activity> activityList = new ArrayList<Activity>();

    String currentEventSimpleName = environmentEvent.getClass().getSimpleName();

    for (Activity activity : this.activeActivities) {

      for (String activityEndEvent : activity
          .getActivityEndEventSimpleNameList()) {

        if (currentEventSimpleName.equals(activityEndEvent)) {
          activityList.add(activity);
          break;
        }
      }
    }

    return activityList;
  }

  /**
   * finished all active activities with environmentEvent as an endEvent
   * 
   * @param environmentEvent
   * @param result
   *          in a List of EnvironmentEvents (to start next activities)
   */
  private ArrayList<EnvironmentEvent> terminateActivitiesByEvent(
      EnvironmentEvent environmentEvent) {

    ArrayList<EnvironmentEvent> result = new ArrayList<EnvironmentEvent>();
    String currentEventSimpleName = environmentEvent.getClass().getSimpleName();

    for (Activity activity : this
        .getActiveActivitiesEndsByEvent(environmentEvent)) {

      // if the environmentEvent ActivityEndEvent then we check if this is
      // for the current activity (otherwise skip the termination)
      if (ActivityEndEvent.class.isInstance(environmentEvent)) {

        ActivityEndEvent activityEndEvent = (ActivityEndEvent) environmentEvent;

        // either it is a by duration generated activityEndEvent
        if (activityEndEvent.getActivity() != null) {

          // then it is only necessary to check if this activity to finalizing
          // there can't be a correlation
          if (activity == activityEndEvent.getActivity()) {

            activity.setOccurrenceTime(environmentEvent.getOccurrenceTime());
            activity.setCurrentEndEvent(environmentEvent);
            activity.executeEndEffects();
            this.stopActivity(activity, environmentEvent.getOccurrenceTime());
            result.addAll(activity
                .getSuccessorActivityStartEvents(environmentEvent
                    .getOccurrenceTime()));
            this.dataBus.notifyActivityStop(activity, result);
            continue;

          } else {
            continue;
          }
        }

        // or it has an activityType
        if (!activityEndEvent.getActivityType().equals("")) {

          if (activity.getName().equals(activityEndEvent.getActivityType())) {

            if (activityEndEvent.getCorrelationValue() != null)
              activity.setEndEventCorrelationProperty("__correlationValue");

          } else {
            continue;
          }
        } else {
          continue;
        }
      }

      for (String activityEndEventSimpleName : activity
          .getActivityEndEventSimpleNameList()) {

        if (currentEventSimpleName.equals(activityEndEventSimpleName)) {

          // if exist an endEventCorrelationProperty, then finalize only
          // activities with matching correlation-value
          if (this.checkCorrelation(environmentEvent, activity)) {

            activity.setOccurrenceTime(environmentEvent.getOccurrenceTime());
            activity.setCurrentEndEvent(environmentEvent);
            activity.executeEndEffects();
            this.stopActivity(activity, environmentEvent.getOccurrenceTime());
            result.addAll(activity
                .getSuccessorActivityStartEvents(environmentEvent
                    .getOccurrenceTime()));
            this.dataBus.notifyActivityStop(activity, result);
          }

          // if there is no correlationValue
          // NOTE: null is an illegal correlationValue!!
          if (activity.getCorrelationValue() == null) {

            activity.setOccurrenceTime(environmentEvent.getOccurrenceTime());
            activity.setCurrentEndEvent(environmentEvent);
            activity.executeEndEffects();
            this.stopActivity(activity, environmentEvent.getOccurrenceTime());
            result.addAll(activity
                .getSuccessorActivityStartEvents(environmentEvent
                    .getOccurrenceTime()));
            this.dataBus.notifyActivityStop(activity, result);
          }
        }
      }
    }

    return result;
  }

  private void stopActivity(Activity activity, long occurrenceTime) {
    this.stopActivity(activity, true, occurrenceTime);
  }

  /**
   * 
   * removes an activity from the activeActivityList and set the actor free from
   * this activity
   * 
   * @param activity
   *          - to finish
   * @param completeStop
   *          - if true, then remove the activity from activeActivityList
   */
  private void stopActivity(Activity activity, boolean completeStop,
      long occurrenceTime) {

    Iterator<Activity> acIterator = this.activeActivities.iterator();

    while (acIterator.hasNext()) {
      Activity activeActivity = acIterator.next();
      if (activeActivity.equals(activity)) {

        // set the actor to free from this activity
        AgentObject actor = activeActivity.getActor();
        if (actor != null) {
          actor.setFinishedActivity(activeActivity);
          actor.computeResourceUtilizationTimeByActivity(activity,
              occurrenceTime);
        }

        if (completeStop)
          acIterator.remove();
      }
    }
  }

  private boolean checkCorrelation(EnvironmentEvent event, Activity activity) {

    if (!activity.getStartEventCorrelationProperty().equals("")
        && !activity.getEndEventCorrelationProperty().equals("")
        && activity.getCorrelationValue() != null) {

      if (activity.getEndEventCorrelationProperty().equals("actor")
          && ActionEvent.class.isInstance(event)) {

        return (activity.getCorrelationValue().equals(((ActionEvent) event)
            .getActor()));

      } else {

        try {
          Field field = event.getClass().getDeclaredField(
              activity.getEndEventCorrelationProperty());
          field.setAccessible(true);
          return (activity.getCorrelationValue().equals(field.get(event)));
        } catch (SecurityException e) {
          e.printStackTrace();
        } catch (NoSuchFieldException e) {
          // do nothing
        } catch (IllegalArgumentException e) {
          // do nothing
        } catch (IllegalAccessException e) {
          // do nothing
        }
      }
    }

    return false;
  }

  public void setActivityFactory(AbstractActivityFactory abstractActivityFactory) {
    this.abstractActivityFactory = abstractActivityFactory;
  }

}
