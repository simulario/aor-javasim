/**
 * 
 */
package aors.space;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Jens Werner
 * 
 */
public abstract class AbstractCell {

  private int posX = 0;
  private int posY = 0;
  private int posZ = 0;

  protected PropertyChangeSupport propertyChangeSupport;

  /*
   * OneDimensionalGrid
   */
  protected AbstractCell(int x) {
    this.posX = x + Space.ORDINATEBASE;
    this.posY = Space.ORDINATEBASE;
    this.posZ = Space.ORDINATEBASE;
  }

  /*
   * TwoDimensionalGrid
   */
  protected AbstractCell(int x, int y) {
    this.posX = x + Space.ORDINATEBASE;
    this.posY = y + Space.ORDINATEBASE;
    this.posZ = Space.ORDINATEBASE;
  }

  /*
   * ThreeDimensionalGrid
   */
  protected AbstractCell(int x, int y, int z) {
    this.posX = x + Space.ORDINATEBASE;
    this.posY = y + Space.ORDINATEBASE;
    this.posZ = z + Space.ORDINATEBASE;
  }

  /**
   * @return the posX
   */
  public int getPosX() {
    return posX;
  }

  /**
   * @return the posY
   */
  public int getPosY() {
    return posY;
  }

  /**
   * @return the posZ
   */
  public int getPosZ() {
    return posZ;
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    if (propertyChangeSupport != null) {
      propertyChangeSupport.addPropertyChangeListener(listener);
    }
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    if (propertyChangeSupport != null) {
      propertyChangeSupport.removePropertyChangeListener(listener);
    }
  }

}
