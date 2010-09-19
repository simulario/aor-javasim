package aors.util.economics;

public abstract class Quantity {

  /**
   * A common supertype of ContinuousQuantity and DiscreteQuantity.
   * 
   */
  public abstract boolean isDiscrete();

  public abstract double getQuantity();

  public abstract String getType();

  /**
   * Add a certain quantity of this type. A RuntimeException will be 
   * thrown if quantity is negative.
   */
  public abstract void deposit( double q);
  /**
   * Remove a certain quantity of this type. If quantity to be withdrawn 
   * is smaller than quantity, quantity is set to 0. Returns the quantity successfully 
   * withdrawn. A RuntimeException will be thrown if quantity is negative. 
   */
  public abstract double withdraw( double q);
}
