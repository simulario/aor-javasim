/**
 * 
 */
package aors.util.reftypes;

/**
 * @author Jens Werner
 * 
 *         This class is a predefined referenzetype
 * 
 */
public class Position {

  float x = 0;
  float y = 0;
  float z = 0;

  public Position() {
  }

  /**
   * 
   * @param x
   */
  public Position(float x) {
    this.x = x;
  }

  /**
   * 
   * @param x
   * @param y
   */
  public Position(float x, float y) {
    this.x = x;
    this.y = y;
  }

  /**
   * 
   * @param x
   * @param y
   * @param z
   */
  public Position(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * @return the x
   */
  public float getX() {
    return x;
  }

  /**
   * @param x
   *          the x to set
   */
  public void setX(float x) {
    this.x = x;
  }

  /**
   * @return the y
   */
  public float getY() {
    return y;
  }

  /**
   * @param y
   *          the y to set
   */
  public void setY(float y) {
    this.y = y;
  }

  /**
   * @return the z
   */
  public float getZ() {
    return z;
  }

  /**
   * @param z
   *          the z to set
   */
  public void setZ(float z) {
    this.z = z;
  }

}
