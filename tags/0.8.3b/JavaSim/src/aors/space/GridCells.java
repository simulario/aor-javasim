/**
 * 
 */
package aors.space;

import java.beans.PropertyChangeListener;

import aors.data.java.ObjektInitEventListener;

/**
 * An interface for a 2-dimensional gridCell
 * 
 * @author Jens Werner
 * 
 */
public interface GridCells {

  public void initGrid(int xSize, int ySize,
      ObjektInitEventListener objektInitEventListener);

  // it is necessary set the prop listener after the cells are initialized and
  // the cell properties are initialized (for the logger)
  public void initPropertyChangeListener(
      PropertyChangeListener propertyChangeListener);

  public AbstractCell getGridCell(int xPos, int yPos);

  public void addObjektInitListener(ObjektInitEventListener listener);

  public boolean removeObjektInitListener(ObjektInitEventListener listener);

  public void fireInitEvent(AbstractCell[][] abstractCells);

}
