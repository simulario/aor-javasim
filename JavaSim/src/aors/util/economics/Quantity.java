package aors.model.util.economics;

public abstract class Quantity {

  /**
   * A common supertype of ContinuousQuantity and DiscreteQuantity.
   * 
   */
  public abstract boolean isDiscrete();

  public abstract double getQuantity();

  public abstract String getType();

}