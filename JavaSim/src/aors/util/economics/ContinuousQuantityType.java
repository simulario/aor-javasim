package aors.util.economics;

/**
 * The datatype class ContinuousQuantityType allows to define continuous quantity 
 * types (such as water, energy or labor).
 * 
 * Author: Gerd Wagner (9-Sept-2010)
 *
 */
public class ContinuousQuantityType {

  private String name;  
  private String defaultUnit;   
  private String furtherUnits;  // optional 
    
  /**
   * Construct a continuous quantity type with a default unit
   */
  public ContinuousQuantityType( String n, String dU) {
    this.name = n;
    this.defaultUnit = dU;
  }    
  /**
   * Construct a continuous quantity type with further units
   */
  public ContinuousQuantityType( String n, String dU, String fU) {
    this.name = n;
    this.defaultUnit = dU;
    this.furtherUnits = fU;
  }    

  public String getName() {
    return name;
  }
  public String getDefaultUnit() {
    return defaultUnit;
  }
  public String getFurtherUnits() {
    return furtherUnits;
  }
}
