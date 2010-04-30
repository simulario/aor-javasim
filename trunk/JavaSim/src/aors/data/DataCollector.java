/**
 * 
 */
package aors.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

import aors.data.java.CollectionEvent;
import aors.data.java.CollectionEventListener;
import aors.data.java.CollectionInitEvent;
import aors.data.java.CollectionInitEventListener;
import aors.data.java.ObjektDestroyEvent;
import aors.data.java.ObjektDestroyEventListener;
import aors.data.java.ObjektInitEvent;
import aors.data.java.ObjektInitEventListener;
import aors.model.agtsim.AgentSubject;
import aors.model.envsim.AgentObject;
import aors.model.envsim.Objekt;
import aors.model.envsim.PhysicalAgentObject;
import aors.model.envsim.PhysicalObject;
import aors.space.AbstractCell;

/**
 * The ex ObjectLogger is now called DataCollector.
 * 
 * @author Jens Werner
 * @author Christian Noack
 * 
 */
public class DataCollector implements PropertyChangeListener,
    ObjektInitEventListener, CollectionEventListener,
    CollectionInitEventListener, ObjektDestroyEventListener, Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -2598773262423L;

  // ++++++++++ PropertyChangeEvents +++++++++++++++++++

  /**
   * contains the PropertyChangeEvents for PhysicalAgentObjects use as a queue
   */
  private Queue<PropertyChangeEvent> propChangeListphysA;

  /**
   * contains the PropertyChangeEvents for PhysicalObjekts use as a queue
   */
  private Queue<PropertyChangeEvent> propChangeListphysO;

  /**
   * contains the PropertyChangeEvents for Agents use as a queue
   */
  private Queue<PropertyChangeEvent> propChangeListAgt;

  /**
   * contains the PropertyChangeEvents for Objekts use as a queue
   */
  private Queue<PropertyChangeEvent> propChangeListO;

  /**
   * contains the PropertyChangeEvents for GridCellProperties use as a queue
   */
  private Queue<PropertyChangeEvent> propChangeListGridCell;

  // ++++++++++ InitEvents +++++++++++++++++++

  /**
   * contains the PhysicalObjectInitEvents, use as a queue
   */
  private Queue<ObjektInitEvent> physObjInitEvents;

  /**
   * contains the PhysicalAgentInitEvents, use as a queue
   */
  private Queue<ObjektInitEvent> physAgentInitEvents;

  /**
   * contains the ObjektInitEvents, use as a queue
   */
  private Queue<ObjektInitEvent> agentInitEvents;

  /**
   * contains the ObjektInitEvents, use as a queue
   */
  private Queue<ObjektInitEvent> objektInitEvents;

  /**
   * contains the GridCellInitEvents, use as a queue
   */
  private AbstractCell[][] gridCellInit;

  // ++++++++++ DestroyEvents +++++++++++++++++++

  /**
   * contains the ObjektDestroyEvent, use as a queue
   */
  private Queue<ObjektDestroyEvent> objDestroyEvents;

  // ++++++++++ CollectionEvents +++++++++++++++++++

  /**
   * contains the CollectionInitEvents, use as a queue
   */
  private Queue<CollectionInitEvent> collectionInitEvents;

  /**
   * contains the CollectionEvent, use as a queue
   */
  private Queue<CollectionEvent> collectionEvents;

  // ++++++++++ Listener +++++++++++++++++++

  public DataCollector() {
    initialize();
  }

  public void initialize() {
    this.propChangeListphysA = new LinkedList<PropertyChangeEvent>();
    this.propChangeListphysO = new LinkedList<PropertyChangeEvent>();
    this.propChangeListAgt = new LinkedList<PropertyChangeEvent>();
    this.propChangeListO = new LinkedList<PropertyChangeEvent>();
    this.propChangeListGridCell = new LinkedList<PropertyChangeEvent>();

    this.physObjInitEvents = new LinkedList<ObjektInitEvent>();
    this.physAgentInitEvents = new LinkedList<ObjektInitEvent>();
    this.agentInitEvents = new LinkedList<ObjektInitEvent>();
    this.objektInitEvents = new LinkedList<ObjektInitEvent>();

    this.objDestroyEvents = new LinkedList<ObjektDestroyEvent>();

    this.collectionInitEvents = new LinkedList<CollectionInitEvent>();
    this.collectionEvents = new LinkedList<CollectionEvent>();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent
   * )
   */
  @Override
  public synchronized void propertyChange(PropertyChangeEvent evt) {

    if (evt.getSource() instanceof PhysicalAgentObject) {

      if (!propChangeListphysA.offer(evt)) {
        System.err
            .println("Impossible to add an PropertyChangeEvent to the physicalAgentQueue!");
      }

    } else if (evt.getSource() instanceof PhysicalObject) {

      if (!propChangeListphysO.offer(evt)) {
        System.err
            .println("Impossible to add an PropertyChangeEvent to the physicalObjektqueue!");
      }
    } else if (evt.getSource() instanceof AgentObject) {

      if (!propChangeListAgt.offer(evt)) {
        System.err
            .println("Impossible to add an PropertyChangeEvent to the agentQueue!");
      }

    } else if (evt.getSource() instanceof Objekt) {

      if (!propChangeListO.offer(evt)) {
        System.err
            .println("Impossible to add an PropertyChangeEvent to the objektqueue!");
      }

    } else if (evt.getSource() instanceof AbstractCell) {

      // this.notifyPropertyChangeEvent(evt);

      if (!propChangeListGridCell.offer(evt)) {
        System.err
            .println("Impossible to add an PropertyChangeEvent to the gridCellqueue!");
      }

    } else if (evt.getSource() instanceof AgentSubject) {

      // this.notifyPropertyChangeEvent(evt);

    } else {
      System.err.println("Unknown Class in Propertychangeevent");
    }

  }

  public boolean isPropChangeListPhysObjIsEmpty() {
    return propChangeListphysO.isEmpty();
  }

  public boolean isPropChangeListPhysAgentIsEmpty() {
    return propChangeListphysA.isEmpty();
  }

  public boolean isPropChangeListAgentIsEmpty() {
    return propChangeListAgt.isEmpty();
  }

  public boolean isPropChangeListObjIsEmpty() {
    return propChangeListO.isEmpty();
  }

  public boolean isPropChangeListGridCellIsEmpty() {
    return propChangeListGridCell.isEmpty();
  }

  public boolean isPhysAgentInitListIsEmpty() {
    return physAgentInitEvents.isEmpty();
  }

  public boolean isAgentInitListIsEmpty() {
    return agentInitEvents.isEmpty();
  }

  public boolean isPhysObjInitListIsEmpty() {
    return physObjInitEvents.isEmpty();
  }

  public boolean isObjInitListIsEmpty() {
    return objektInitEvents.isEmpty();
  }

  public boolean isObjDestroyListIsEmpty() {
    return objDestroyEvents.isEmpty();
  }

  public boolean isCollectionInitListIsEmpty() {
    return collectionInitEvents.isEmpty();
  }

  public boolean isCollectionEventListIsEmpty() {
    return collectionEvents.isEmpty();
  }

  public PropertyChangeEvent getNextPhysObjektChange() {
    return propChangeListphysO.poll();
  }

  public PropertyChangeEvent getNextPhysAgentChange() {
    return propChangeListphysA.poll();
  }

  public PropertyChangeEvent getNextAgentChange() {
    return propChangeListAgt.poll();
  }

  public PropertyChangeEvent getNextObjektChange() {
    return propChangeListO.poll();
  }

  public PropertyChangeEvent getNextGridCellChange() {
    return propChangeListGridCell.poll();
  }

  public AbstractCell[][] getGridCellInitState() {
    return this.gridCellInit;
  }

  public ObjektInitEvent getNextPhysAgentInitEvent() {
    return physAgentInitEvents.poll();
  }

  public ObjektInitEvent getNextAgentInitEvent() {
    return agentInitEvents.poll();
  }

  public ObjektInitEvent getNextPhysObjInitEvent() {
    return physObjInitEvents.poll();
  }

  public ObjektInitEvent getNextObjektInitEvent() {
    return objektInitEvents.poll();
  }

  public ObjektDestroyEvent getNextObjektDestroyEvent() {
    return objDestroyEvents.poll();
  }

  public CollectionInitEvent getNextCollectionInitEvent() {
    return collectionInitEvents.poll();
  }

  public CollectionEvent getNextCollectionEvent() {
    return collectionEvents.poll();
  }

  public void deleteAllBuffer() {
    propChangeListphysA.clear();
    propChangeListphysO.clear();
    propChangeListAgt.clear();
    propChangeListO.clear();
    propChangeListGridCell.clear();
    objektInitEvents.clear();
    physObjInitEvents.clear();
    agentInitEvents.clear();
    physAgentInitEvents.clear();
    gridCellInit = null;
    collectionEvents.clear();
    objDestroyEvents.clear();
  }

  /*
   * (non-Javadoc)
   */
  @Override
  public void objektInitEvent(ObjektInitEvent objInitEvent) {

    if (objInitEvent.getSource() instanceof PhysicalAgentObject) {

      if (!physAgentInitEvents.offer(objInitEvent)) {
        System.err
            .println("Impossible to add an PhysicalAgentInitEvent to eventqueue");
      }

    } else if (objInitEvent.getSource() instanceof PhysicalObject) {

      if (!physObjInitEvents.offer(objInitEvent)) {
        System.err
            .println("Impossible to add a PhysicalObjectInitEvent to eventqueue");
      }
    } else if (objInitEvent.getSource() instanceof AgentObject) {

      if (!agentInitEvents.offer(objInitEvent)) {
        System.err.println("Impossible to add a AgentInitEvent to eventqueue");
      }
    } else if (objInitEvent.getSource() instanceof Objekt) {

      if (!objektInitEvents.offer(objInitEvent)) {
        System.err.println("Impossible to add a ObjektInitEvent to eventqueue");
      }

    } else if (objInitEvent.getSource() instanceof AgentSubject) {
      // do nothing here, AgentSubject is not physical

    } else if (objInitEvent.getSource() instanceof AbstractCell[][]) {

      this.gridCellInit = (AbstractCell[][]) objInitEvent.getSource();

    } else {
      System.err.println("Unknown Class in PhysObjInitEvent");
    }
  }

  @Override
  public synchronized void collectionEvent(CollectionEvent collectionEvent) {
    collectionEvents.add(collectionEvent);
  }

  @Override
  public synchronized void collectionInitEvent(
      CollectionInitEvent collectionInitEvent) {
    collectionInitEvents.add(collectionInitEvent);
  }

  @Override
  public void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent) {
    this.objDestroyEvents.add(objektDestroyEvent);
  }

  /**
   * 
   * Usage: Check if a property of an objekt is changed or something is
   * destroyed or some gridcell-state is changed or an objekt is created
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @return true if something is changed in the environment-state
   */
  public boolean isStateChange() {

    if (!(this.isPropChangeListPhysObjIsEmpty()
        && this.isPropChangeListPhysAgentIsEmpty()
        && this.isPropChangeListAgentIsEmpty()
        && this.isPropChangeListObjIsEmpty()
        && this.isCollectionEventListIsEmpty()
        && this.isObjDestroyListIsEmpty()
        && this.isPropChangeListGridCellIsEmpty()
        && this.isObjInitListIsEmpty() && this.isAgentInitListIsEmpty()
        && this.isPhysAgentInitListIsEmpty() && this.isPhysObjInitListIsEmpty())) {
      return true;
    }

    return false;
  }

}
