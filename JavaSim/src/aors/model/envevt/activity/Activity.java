package aors.model.envevt.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import aors.logger.model.AgtSimInputEventType.ResultingEvents.OutMessageEvent;
import aors.model.Event;
import aors.model.envevt.ActionEvent;
import aors.model.envevt.ActivityEndEvent;
import aors.model.envevt.ActivityStartEvent;
import aors.model.envevt.EnvironmentEvent;
import aors.model.envsim.AgentObject;
import aors.model.envsim.EnvironmentSimulator;
import aors.model.envsim.Objekt;

public abstract class Activity extends Event {

  private AgentObject actor;

  // is used for a correlation between start and endevent
  private Object correlationValue = null;

  private String endEventCorrelationProperty = "";

  private String startEventCorrelationProperty = "";

  private List<Objekt> resourceList;

  private EnvironmentSimulator environmentSimulator;

  /**
   * constructor
   */
  public Activity(EnvironmentSimulator environmentSimulator) {
    super();
    this.environmentSimulator = environmentSimulator;
    this.resourceList = new ArrayList<Objekt>();
  }

  /**
   * 
   * Create a new {@code AbstractActivity}.
   * 
   * @param name
   * @param environmentSimulator
   */
  public Activity(String name, EnvironmentSimulator environmentSimulator) {
    super();
    this.environmentSimulator = environmentSimulator;
    this.resourceList = new ArrayList<Objekt>();
    this.setName(name);
  }

  /**
   * @return the startEvent-simpleName
   */
  public abstract String getActivityStartEventSimpleName();

  /**
   * @return the endEvent-simpleNames
   */
  public abstract List<String> getActivityEndEventSimpleNameList();

  /**
   * 
   * Usage: execute all start effects
   * 
   * 
   * Comments:
   * 
   * 
   * 
   */
  public abstract void executeStartEffects();

  /**
   * 
   * Usage: execute all end effects
   * 
   * 
   * Comments:
   * 
   * 
   * 
   */
  public abstract void executeEndEffects();

  protected void setActor(AgentObject agentObject, EnvironmentEvent event) {
    if (event != null)
      this.setActor(agentObject, event.getOccurrenceTime());
  }

  /**
   * Set an actor
   * 
   * @param agentObject
   */
  protected void setActor(AgentObject agentObject, long occurrenceTime) {
    if (agentObject != null) {
      agentObject.setBusyWithActivity(this);
      agentObject.setResourceAllocationTimeByActivity(this, occurrenceTime);
      this.actor = agentObject;
    }
  }

  public AgentObject getActor() {
    return this.actor;
  }

  /**
   * Set the actor from an ActionEvent
   * 
   * @param environmentEvent
   */
  protected void addActorByActionEvent(EnvironmentEvent environmentEvent) {
    // in the moment there are no difference
    if (OutMessageEvent.class.isInstance(environmentEvent)) {
      this.setActor(((ActionEvent) environmentEvent).getActor(),
          environmentEvent.getOccurrenceTime());
    } else if (ActionEvent.class.isInstance(environmentEvent)) {
      this.setActor(((ActionEvent) environmentEvent).getActor(),
          environmentEvent.getOccurrenceTime());
    }
  }

  /**
   * 
   * @param environmentEvent
   *          - is the startevent for this activity
   */
  public void setCurrentStartEvent(EnvironmentEvent environmentEvent) {

    // try to set the actor from event - in case it is an actionEvent
    this.addActorByActionEvent(environmentEvent);

    // try to set the actor from ActivityStartEvent
    if (ActivityStartEvent.class.isInstance(environmentEvent)) {
      this.setActor(((ActivityStartEvent) environmentEvent).getActivityActor(),
          environmentEvent.getOccurrenceTime());
    } else {

      // only if there is a defined StartEventType (except ActivityStartEvent)
      this.setStartEvent(environmentEvent);
    }

    // try to set the corelationValue
    this.setCorrelation(environmentEvent);
  }

  public abstract void setCurrentEndEvent(EnvironmentEvent environmentEvent);

  protected abstract void setStartEvent(EnvironmentEvent environmentEvent);

  /**
   * 
   * Usage: An @see ActivityEndEvent can 'create' by a duration
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param occurenceTime
   * @return
   */
  public abstract ActivityEndEvent getActivityEndEvent(long occurenceTime);

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param occurenceTime
   * @return
   */
  public abstract ArrayList<EnvironmentEvent> getSuccessorActivityStartEvents(
      long occurenceTime);

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code environmentSimulator}.
   * 
   * 
   * 
   * @return the {@code environmentSimulator}.
   */
  public EnvironmentSimulator getEnvironmentSimulator() {
    return environmentSimulator;
  }

  @Deprecated
  protected boolean addResourceByIdRef(long id, long occurenceTime) {
    Objekt aorObjekt = this.environmentSimulator.getObjectById(id);
    if (aorObjekt != null) {
      aorObjekt.setResourceAllocationTimeByActivity(this, occurenceTime);
      return this.resourceList.add(aorObjekt);
    }
    return false;
  }

  protected boolean addResource(Objekt aorObjekt, long occurenceTime) {
    if (aorObjekt != null) {
      aorObjekt.setResourceAllocationTimeByActivity(this, occurenceTime);
      return this.resourceList.add(aorObjekt);
    }
    return false;
  }

  protected void deallocateAllRessources(long occurenceTime) {
    Iterator<Objekt> i = this.resourceList.iterator();
    while (i.hasNext()) {
      Objekt o = i.next();
      o.computeResourceUtilizationTimeByActivity(this, occurenceTime);
      i.remove();
    }
  }

  /**
   * 
   * Usage: this methods set the correlationValue as an java object
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param event
   */
  private void setCorrelation(EnvironmentEvent event) {

    if (!this.startEventCorrelationProperty.equals("")) {

      if (this.startEventCorrelationProperty.equals("actor")) {

        this.correlationValue = this.getActor();
      } else {

        try {
          Field field = event.getClass().getDeclaredField(
              this.startEventCorrelationProperty);
          field.setAccessible(true);
          this.correlationValue = field.get(event);
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
  }

  // public boolean checkCorrelation(EnvironmentEvent event) {
  //
  // if (!this.startEventCorrelationProperty.equals("")
  // && !this.endEventCorrelationProperty.equals("")
  // && this.correlationValue != null) {
  //
  // try {
  // Field field = event.getClass().getDeclaredField(
  // this.endEventCorrelationProperty);
  // field.setAccessible(true);
  // return (this.correlationValue.equals(field.get(event)));
  // } catch (SecurityException e) {
  // e.printStackTrace();
  // } catch (NoSuchFieldException e) {
  // // do nothing
  // } catch (IllegalArgumentException e) {
  // // do nothing
  // } catch (IllegalAccessException e) {
  // // do nothing
  // }
  //
  // }
  //
  // return false;
  // }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code endEventCorrelationProperty}.
   * 
   * 
   * 
   * @param endEventCorrelationProperty
   *          The {@code endEventCorrelationProperty} to set.
   */
  public void setEndEventCorrelationProperty(String endEventCorrelationProperty) {
    this.endEventCorrelationProperty = endEventCorrelationProperty;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code startEventCorrelationProperty}.
   * 
   * 
   * 
   * @param startEventCorrelationProperty
   *          The {@code startEventCorrelationProperty} to set.
   */
  public void setStartEventCorrelationProperty(
      String startEventCorrelationProperty) {
    this.startEventCorrelationProperty = startEventCorrelationProperty;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code endEventCorrelationProperty}.
   * 
   * 
   * 
   * @return the {@code endEventCorrelationProperty}.
   */
  public String getEndEventCorrelationProperty() {
    return endEventCorrelationProperty;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code startEventCorrelationProperty}.
   * 
   * 
   * 
   * @return the {@code startEventCorrelationProperty}.
   */
  public String getStartEventCorrelationProperty() {
    return startEventCorrelationProperty;
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
    return correlationValue;
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
    this.correlationValue = correlationValue;
  }

  /**
   * 
   * Usage: is the duration lower the 1 the the duration is set to 1
   * 
   * 
   * Comments: Overrides method {@code setDuration} from super class
   * 
   * 
   * 
   * @param duration
   */
  @Override
  public void setDuration(long duration) {
    if (duration < 1)
      duration = 1;
    super.setDuration(duration);
  }

}
