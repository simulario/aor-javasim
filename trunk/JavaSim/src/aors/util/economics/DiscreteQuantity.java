package aors.util.economics;

import aors.model.envsim.Objekt;
import java.util.ArrayList;


/**
 * The datatype class DiscreteQuantity allows to define discrete quantites 
 * (such as 1 tractor or 3 cars) 
 * 
 * Author: Gerd Wagner (10-Sept-2010)
 *
 * ISSUES
 * - should this class implement Cloneable?
 */
public class DiscreteQuantity {

  private String objectType;  
  private int quantity;  // optional (default is 1)
  private ArrayList<Objekt> objects;  // optional (default is null)
    
  /**
   * Construct a discrete default quantity without object references
   */
  public DiscreteQuantity( String t) {
    this.objectType = t;
    this.quantity = 1;
  }    
  /**
   * Construct a discrete quantity without object references
   */
  public DiscreteQuantity( String t, int q) {
    this.objectType = t;
    this.quantity = q;
  }    
  /**
   * Construct a discrete quantity with object references
   */
  public DiscreteQuantity( String t, int q, ArrayList<Objekt> o) {
    this.objectType = t;
    this.quantity = q;
    this.objects = o;
  }    

  public String getObjectType() {
    return objectType;
  }
  // Implements abstract supertype method.
  public double getQuantity() {
    return quantity;
  }
  public ArrayList<Objekt> getObjects() {
    return objects;
  }
  
  /**
   * Implements abstract supertype method.
   */
  public final boolean isDiscrete() { 
    return true;
  }
  /**
   * Returns the type as a string. Implements abstract supertype method.
   */
  public String getType() {
    return this.getObjectType();
  }
  /**
   * Add a certain quantity of this type of object. A RuntimeException will be 
   * thrown if quantity is negative.
   */
  public void deposit( double q) {
    if (q < 0) throw new RuntimeException("Quantity to be deposited must be greater than zero");
    quantity += q;
  }
  /**
   * Remove a certain quantity of this type of object. If quantity to be withdrawn 
   * is smaller than quantity, quantity is set to 0. Returns the quantity successfully 
   * withdrawn. A RuntimeException will be thrown if quantity is negative. 
   */
  public double withdraw( double q) {
    if (q < 0) throw new RuntimeException("Quantity to be withdrawn must be greater than zero");
    if (q <= quantity) {
      quantity -= q;
      return q;
    }
    else {
      int oldQuantity = quantity;
      quantity = 0;
      return oldQuantity;
    }
  }
}
