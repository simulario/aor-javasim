/**
 * This class represent the ObjectType from the metamaodel. They are only
 * objects of observation(and logging).
 */
package aors.model.envsim;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import aors.InheritedProperty;
import aors.controller.AbstractSimulator;
import aors.data.evt.sim.ObjektInitEventListener;
import aors.model.Entity;
import aors.model.envevt.activity.Activity;

/**
 * @author Jens Werner
 * 
 */
public class Objekt extends Entity {

  /**
   * propertyChangeSupport - this can't be null or initialized while is used in
   * setter methods for inherited classes.
   */
  protected final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
      this);;

  protected final ArrayList<ObjektInitEventListener> initListener;

  protected InheritedProperty inheritedProperty;

  // contains the total time of activity which allocated this object
  private HashMap<String, Long> resourceUtilizationTime = null;

  // contains the start time of resource-allocation of an activity
  private HashMap<Activity, Long> resourceAllocationStartTime = null;

  protected Objekt(long id) {
    super(id);
    this.initListener = new ArrayList<ObjektInitEventListener>();
    init();
  }

  protected Objekt(long id, String name) {
    super(id, name);
    this.initListener = new ArrayList<ObjektInitEventListener>();
    init();
  }

  /**
   * This constructor is not supposed to initialize any data but just to create
   * an instance with no value for its fields, therefore the fields of the
   * objects will be null in this point.
   */
  protected Objekt() {
    super();
    this.initListener = null;
  }

  /**
   * Make some initialization of the data. Please notice that this does not
   * works for <code>final</code> fields, and these has to be called directly in
   * the constructor.
   */
  private void init() {

    this.inheritedProperty = new InheritedProperty();

    // contains the total time of activity which allocated this object
    this.resourceUtilizationTime = new HashMap<String, Long>();

    // contains the start time of resource-allocation of an activity
    this.resourceAllocationStartTime = new HashMap<Activity, Long>();
  }

  /**
   * 
   * Usage: returns the total time of resource allocation by an activity
   * 
   * 
   * Comments: if the object is currently allocated by this activity, then the
   * current allocated time is considered
   * 
   * 
   * 
   * @param activityType
   *          - the name of the activity
   * @param occurenceTime
   *          - the
   * @return the total allocated time by an activity
   */
  public long getResourceUtilizationTimeByActivity(String activityType,
      long occurenceTime) {

    if (this.resourceUtilizationTime.containsKey(activityType)) {

      long currentAllocation = 0;
      for (Activity activity : this.resourceAllocationStartTime.keySet()) {
        if (activity.getName().equals(activityType) && occurenceTime > 0) {
          long allocationStart = this.resourceAllocationStartTime.get(activity);
          if (allocationStart > occurenceTime) {
            throw new IllegalArgumentException("Wrong value for occurenceTime");
          }
          currentAllocation += occurenceTime - allocationStart;
        }
      }

      return this.resourceUtilizationTime.get(activityType) + currentAllocation;
    }
    return 0;
  }

  /**
   * 
   * Usage: notify that this objekt is allocated by an activity and save the
   * allocationtime
   * 
   * 
   * Comments: it is impossible that the same objekt is active with the same
   * activity by twice simultaneously
   * 
   * 
   * 
   * @param activity
   *          - allocating activity
   * @param allocationTime
   *          - when the objekt is allocated
   */
  public void setResourceAllocationTimeByActivity(Activity activity,
      long allocationTime) {

    if (this.resourceAllocationStartTime.containsKey(activity)) {
      System.err.println("Objekt " + this.getId()
          + " is currently active with the activity " + activity.getName());
    } else {

      this.resourceAllocationStartTime.put(activity, allocationTime);
      if (!this.resourceUtilizationTime.containsKey(activity.getName())) {
        this.resourceUtilizationTime.put(activity.getName(), new Long(0));
      }
    }

  }

  /**
   * 
   * Usage: notify that this objekt is deallocated by an activity and save the
   * deallocationtime
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param activity
   *          - deallocating activity
   * @param deallocationTime
   *          - when the objekt is deallocated
   */
  public void computeResourceUtilizationTimeByActivity(Activity activity,
      long deallocationTime) {

    if (this.resourceAllocationStartTime.containsKey(activity)) {

      this.resourceUtilizationTime.put(activity.getName(),
          this.resourceUtilizationTime.get(activity.getName())
              + deallocationTime
              - this.resourceAllocationStartTime.get(activity));
      this.resourceAllocationStartTime.remove(activity);

    } else {
      throw new IllegalStateException("Object " + this.getId()
          + " is not allocated by activity " + activity.getName() + "!");
    }
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @return
   */
  public Set<String> getUsedByActivities() {
    return this.resourceUtilizationTime.keySet();
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {

    if (AbstractSimulator.runLogger) {
      if (propertyChangeSupport != null) {
        propertyChangeSupport.addPropertyChangeListener(listener);
      }
    }
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    if (propertyChangeSupport != null) {
      propertyChangeSupport.removePropertyChangeListener(listener);
    }
  }

  /**
   * 
   * 
   * @param logger
   *          the loggerinstance that contain the listener
   */
  public void addObjektInitListener(ObjektInitEventListener listener) {
    if (AbstractSimulator.runLogger) {
      this.initListener.add(listener);
    }
  }

  public boolean removeObjektInitListener(ObjektInitEventListener listener) {
    return this.initListener.remove(listener);
  }

  public InheritedProperty getInheritedProperty() {
    return inheritedProperty;
  }
  
  // is implemented as a hook and should be overloaded if necessary
  protected void initInheritedAttr() {};

}
