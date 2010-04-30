/**
 * 
 */
package aors.data.java;

import java.util.EventObject;

import aors.model.envsim.Objekt;

/**
 * @author Jens Werner
 * 
 */
public class CollectionEvent extends EventObject {

  /**
   * 
   */
  private static final long serialVersionUID = 2558513306279848388L;

  /**
   * used objekt
   */
  private Objekt objekt;

  /**
   * currentSize
   */
  private int currentSize = 0;

  /**
   * 
   * @param source
   * @param objekt
   */
  private CollectionAction collectionAction;

  public CollectionEvent(Object source, Objekt objekt,
      CollectionAction collectionAction, int currentSize) {
    super(source);
    this.objekt = objekt;
    this.collectionAction = collectionAction;
    this.currentSize = currentSize;
  }

  /**
   * @return the objekt
   */
  public Objekt getObjekt() {
    return objekt;
  }

  public enum CollectionAction {
    add, remove
  }

  /**
   * @return the collectionAction
   */
  public CollectionAction getCollectionAction() {
    return collectionAction;
  }

  /**
   * @return the currentSize
   */
  public int getCurrentSize() {
    return currentSize;
  }

}
