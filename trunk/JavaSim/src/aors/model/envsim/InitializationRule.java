/**
 * 
 */
package aors.model.envsim;

/**
 * @author Jens Werner
 * 
 */
public abstract class InitializationRule {

  private String name;

  protected InitializationRule(String name) {
    this.name = name;
  }

  /**
   * 
   * @return
   */
  public String getName() {
    return this.name;
  }

  /**
   * 
   */
  public abstract void execute();

  /**
   * 
   * Usage: Comments:
   * 
   * @return true if the rule condition holds; otherwise false
   */
  protected abstract boolean condition();

  /**
   * 
   * Usage:
   * 
   * Comments:
   * 
   */
  protected abstract void stateEffects();

}
