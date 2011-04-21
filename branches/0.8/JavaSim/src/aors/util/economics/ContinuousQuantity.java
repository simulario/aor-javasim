package aors.util.economics;

/**
 * The datatype class ContinuousQuantity allows to define continuous quantites
 * (such as 2.3 liters of water, 2 kJ of energy or 7.5 hours of labor) by
 * creating a suitable continuous quantity type (such as Water, Energy or Labor)
 * as an instance of the datatype class ContinuousQuantityType and then creating
 * an instance of ContinuousQuantity while setting its type property to that
 * type.
 * 
 * Author: Gerd Wagner (3-Sept-2010)
 * 
 * ISSUES - should this class implement Cloneable?
 */
public class ContinuousQuantity extends Quantity {

  private double quantity;
  private ContinuousQuantityType continuousQuantityType;
  // thr property "unit" is only used if the quantity is not expressed in terms of 
  // the default unit as defined by the continuousQuantityType
  private String unit; // optional / ISSUE: better define as a suitable enum?

  /**
   * Construct a continuous quantity with a default unit
   */
  public ContinuousQuantity(double q, ContinuousQuantityType t) {
    this.quantity = q;
    this.continuousQuantityType = t;
  }

  /**
   * Construct a continuous quantity with a non-default unit
   */
  public ContinuousQuantity(double q, ContinuousQuantityType t, String u) {
    this.quantity = q;
    this.continuousQuantityType = t;
    this.unit = u;
  }

  // Implements abstract supertype method.
  @Override
  public double getQuantity() {
    return quantity;
  }

  public String getUnit() {
    return unit;
  }

  public ContinuousQuantityType getContinuousQuantityType() {
    return continuousQuantityType;
  }

  /**
   * Returns false, since a continuous quantity is a non-discrete commodity.
   */
  @Override
  public final boolean isDiscrete() {
    return false;
  }

  /**
   * Returns the type as a string.
   */
  @Override
  public String getType() {
    return this.continuousQuantityType.getName();
  }

  /**
   * Add an amount of this type of continuous quantity. A RuntimeException will
   * be thrown if amount is negative.
   */
  @Override
  public void deposit(double amount) {
    if (amount < 0)
      throw new RuntimeException("Amount must be >= 0");
    quantity += amount;
  }

  /**
   * Remove an amount of this type of continuous quantity. If quantity is
   * smaller than amount, quantity is set to 0. Returns the amount successfully
   * withdrawn. A RuntimeException will be thrown if amount is negative.
   */
  @Override
  public double withdraw(double amount) {
    if (amount < 0)
      throw new RuntimeException("Amount must be >= 0");
    if (amount <= quantity) {
      quantity -= amount;
      return amount;
    } else {
      double oldQuantity = quantity;
      quantity = 0;
      return oldQuantity;
    }
  }
}
