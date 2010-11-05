/**
 * 
 */
package aors.space;

/**
 * @author Jens Werner
 * 
 */
public abstract class Space {

  /**
   * set the value of the ordinate base if is [0, 0, ..., 0] then use 0 if is
   * [1, 1, ..., 1] then use 1
   * 
   * this is valid for discrete spaces
   */
  public static final int ORDINATEBASE = 1;
  
  /*
   * This following values may not used in some special space types. But it
   * is simpler to defined it so (for code creation and the structure here)
   */
  private boolean autoKinematics = false;
  private boolean gravitation = false;
  private boolean autoCollisionHandling = false;
  private boolean autoCollisionDetection = false;
  
  protected Space(boolean autoKinematics,
      boolean autoCollisionHandling, boolean autoCollisionDetection,
      boolean gravitation) {
    
    this.autoKinematics = autoKinematics;
    this.autoCollisionHandling = autoCollisionHandling;
    this.autoCollisionDetection = autoCollisionDetection;
    this.gravitation = gravitation;
  }
  
  
  public boolean isAutoKinematics() {
    return autoKinematics;
  }
  public boolean isGravitation() {
    return gravitation;
  }
  public boolean isAutoCollisionHandling() {
    return autoCollisionHandling;
  }
  public boolean isAutoCollisionDetection() {
    return autoCollisionDetection;
  }
  
}
