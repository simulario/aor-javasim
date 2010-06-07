/**
 * A collection of collections. 
 */
package aors.util.collection;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import aors.data.java.CollectionEvent;
import aors.data.java.CollectionEventListener;
import aors.data.java.CollectionInitEvent;
import aors.data.java.CollectionInitEventListener;
import aors.data.java.CollectionEvent.CollectionAction;
import aors.model.Entity;
import aors.model.envsim.Objekt;
import aors.util.Random;

/**
 * @author Jens Werner
 * 
 */
public class AORCollection<T extends Objekt> extends Entity {

  private CollectionType collectionType;

  private String dataType;

  private List<T> data;

  protected final ArrayList<CollectionEventListener> collectionListener = new ArrayList<CollectionEventListener>();
  protected final ArrayList<CollectionInitEventListener> initListener = new ArrayList<CollectionInitEventListener>();

  public AORCollection(long id, CollectionType collectionType, String dataType) {
    super(id);
    this.collectionType = collectionType;
    this.dataType = dataType;

    this.data = new LinkedList<T>();
  }

  public CollectionType getCollectionType() {
    return this.collectionType;
  }

  // Listoperations
  public boolean addObjekt(T obj) {

    if (obj == null) {
      System.err.println("Add a Null to a AOR-Collection is not allowed!");
      return false;
    }

    for (CollectionEventListener listener : collectionListener) {
      listener.collectionEvent(new CollectionEvent(this, obj,
          CollectionAction.add, this.size() + 1)); // assume that the add works
    }
    return this.data.add(obj);
  }

  /**
   * <p>
   * removes and returns a objekt from the collection
   * </p>
   * 
   * <p>
   * <strong>notice:</strong> per default we assume, that a remove-operation of
   * a list returns the first element of the collection (FIFO-behavior)
   * </p>
   * 
   * @return an element at the specific position, depending on the
   *         collectionType, in the collection
   */
  public T removeObjekt() {

    T result = null;

    if (!this.data.isEmpty()) {

      if (this.collectionType.equals(CollectionType.FIFO_QUEUE)
          || this.collectionType.equals(CollectionType.List)) {
        result = ((LinkedList<T>) this.data).removeFirst();
      } else if (this.collectionType.equals(CollectionType.LIFO_QUEUE)) {
        result = ((LinkedList<T>) this.data).removeLast();
      } else if (this.collectionType.equals(CollectionType.SET)) {
        if (!this.data.isEmpty())
          result = ((LinkedList<T>) this.data).remove(Random.uniformInt(0,
              this.data.size()));
      }

      for (CollectionEventListener listener : collectionListener) {
        listener.collectionEvent(new CollectionEvent(this, result,
            CollectionAction.remove, this.size()));
      }
    }

    return result;
  }

  /**
   * <p>
   * returns a objekt from the collection
   * </p>
   * <p>
   * <strong>notice:</strong>> per default we assume, that a remove-operation of
   * a list returns the first element of the collection (FIFO-behavior)
   * </p>
   * <p>
   * <strong>CollectionType.Set is here not supported</strong>
   * </p>
   * 
   * TODO: exceptionhandling for not supported collectionTypes
   * 
   * @return an element at the specific position, depending on the
   *         collectionType, in the collection
   */
  public T getItem() {

    T result = null;

    /*
     * if (this.collectionType.equals(CollectionType.SET)) { try { throw new
     * Exception("Collectiontype set do not allow getItem()-access!"); } catch
     * (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); }
     * }
     */

    if (!this.data.isEmpty()) {

      if (this.collectionType.equals(CollectionType.FIFO_QUEUE)
          || this.collectionType.equals(CollectionType.List)) {
        result = ((LinkedList<T>) this.data).getFirst();
      } else if (this.collectionType.equals(CollectionType.LIFO_QUEUE)) {
        result = ((LinkedList<T>) this.data).getLast();
      }
    }

    return result;
  }

  /**
   * <p>
   * Returns the element at the specified position in this collection
   * </p>
   * <p>
   * <strong>CollectionType.List is here only supported</strong>
   * </p>
   * 
   * TODO: exceptionhandling for not supported collectionTypes
   * 
   * @param index
   *          index of the element to return
   * @return the element of the specific position in the collection
   */
  public T getItem(int index) {

    T result = null;

    if (!this.data.isEmpty() && this.collectionType.equals(CollectionType.List)) {
      result = ((LinkedList<T>) this.data).get(index);
    }
    return result;
  }

  public int size() {
    return this.data.size();
  }

  public void addCollectionListener(CollectionEventListener listener) {
    if (collectionListener != null) {
      collectionListener.add(listener);
    }
  }

  public void removeCollectionListener(CollectionEventListener listener) {
    if (collectionListener != null) {
      collectionListener.remove(listener);
    }
  }

  public void addCollectionInitListener(CollectionInitEventListener listener) {
    if (initListener != null) {
      initListener.add(listener);
    }
  }

  public void removeCollectionInitListener(CollectionInitEventListener listener) {
    if (initListener != null) {
      initListener.remove(listener);
    }
  }

  public void notifyCollectionInitEvent() {
    if (initListener != null) {
      for (CollectionInitEventListener collectionInitEventListener : initListener) {
        collectionInitEventListener
            .collectionInitEvent(new CollectionInitEvent(this));
      }
    }
  }

  /**
   * CollectionTypes
   * 
   * @author Jens Werner
   * 
   */
  public enum CollectionType {
    FIFO_QUEUE, LIFO_QUEUE, SET, List
  }

  /**
   * @return the dataType
   */
  public String getDataType() {
    return dataType;
  }

}
