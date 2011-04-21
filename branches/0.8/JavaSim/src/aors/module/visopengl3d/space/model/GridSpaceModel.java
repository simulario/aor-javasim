package aors.module.visopengl3d.space.model;

import java.lang.reflect.Field;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.GeneralSpaceModel.SpaceType;
import aors.logger.model.GridCellType;
import aors.logger.model.GridCells;
import aors.logger.model.SlotType;
import aors.model.envsim.Physical;
import aors.module.visopengl3d.space.component.Cell;
import aors.module.visopengl3d.space.component.SpaceComponent;
import aors.module.visopengl3d.space.view.GridSpaceView;
import aors.module.visopengl3d.space.view.PropertyMap;
import aors.module.visopengl3d.space.view.SpaceView;
import aors.module.visopengl3d.utility.Color;
import aors.module.visopengl3d.utility.Offset;
import aors.space.AbstractCell;
import aors.space.Space;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * Two dimensional, discrete space model.
 * 
 * @author Sebastian Mucha
 * @since March 18th, 2010
 * 
 */
public class GridSpaceModel extends SpaceModel {

  // Space view
  private GridSpaceView gridSpaceView;

  private boolean initialGridPropertiesApplied;

  /**
   * Creates a two dimensional, discrete space model.
   */
  public GridSpaceModel() {
    spaceType = SpaceType.TwoDGrid;
  }

  @Override
  public void initializeSpaceModel(GL2 gl, GLU glu) {
    if (!initialized) {
      initialize();
    } else {
      reinitialize();
    }
  }

  /**
   * Performs the first time initialization of the grid.
   */
  private void initialize() {
    // Clear space component list
    if (spaceComponents != null) {
      spaceComponents.clear();
    }

    // Dimensions of the grid
    int columns = (int) xMax;
    int rows = (int) yMax;

    // Minimal cell dimension
    final double MIN_CELL_DIM = 10;

    // Get the actual cell dimension
    double cellDim = computeCellDimension(columns, rows);

    // Make sure the cell dimension is not too small
    if (cellDim < MIN_CELL_DIM) {
      cellDim = MIN_CELL_DIM;
    }

    // Get the stroke width of grid lines
    double strokeWidth = computeStrokeWidth(cellDim);

    // Get the offset of the first cell (start at bottom left corner)
    Offset firstCellOffset = computeFirstCellOffset(columns, rows, cellDim,
        strokeWidth);

    // Offset of all other cells
    Offset cellOffset = new Offset(firstCellOffset.x1, firstCellOffset.y1,
        firstCellOffset.x2, firstCellOffset.y2);

    // Create and set up all cells
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        Cell cell = new Cell();

        // Set offsets
        cell.setOuterOffset(cellOffset);
        cell.setInnerOffset(cellOffset, strokeWidth);

        // Apply cell and border color
        cell.setColor(determineCellColor(column, row));
        cell.setBorderColor(gridSpaceView.getStroke());

        // Create cell contours
        cell.createCellContours();

        // Add the cell into the space component list
        spaceComponents.add(cell);

        // Compute the offset for the next cell in this row
        cellOffset = new Offset(cellOffset.x2 - strokeWidth, cellOffset.y1,
            cellOffset.x2 - strokeWidth + cellDim, cellOffset.y2);
      }

      // Compute the offset for the first cell in a new row
      cellOffset = new Offset(firstCellOffset.x1, firstCellOffset.y1
          + ((cellDim - strokeWidth) * (row + 1)), firstCellOffset.x2,
          firstCellOffset.y2 + ((cellDim - strokeWidth) * (row + 1)));
    }
  }

  /**
   * Performs a reinitialization of the grid.
   */
  private void reinitialize() {
    // Dimensions of the grid
    int columns = (int) xMax;
    int rows = (int) yMax;

    // Minimal cell dimension
    final double MIN_CELL_DIM = 10;

    // Get the actual cell dimension
    double cellDim = computeCellDimension(columns, rows);

    // Make sure the cell dimension is not too small
    if (cellDim < MIN_CELL_DIM) {
      cellDim = MIN_CELL_DIM;
    }

    // Get the stroke width of grid lines
    double strokeWidth = computeStrokeWidth(cellDim);

    // Get the offset of the first cell (start at bottom left corner)
    Offset firstCellOffset = computeFirstCellOffset(columns, rows, cellDim,
        strokeWidth);

    // Offset of all other cells
    Offset cellOffset = new Offset(firstCellOffset.x1, firstCellOffset.y1,
        firstCellOffset.x2, firstCellOffset.y2);

    // Create and set up all cells
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        // Get the corresponding cell from the list
        int index = row * (int) xMax + column;
        Cell cell = (Cell) spaceComponents.get(index);

        // Set offsets
        cell.setOuterOffset(cellOffset);
        cell.setInnerOffset(cellOffset, strokeWidth);

        // Create cell contours
        cell.createCellContours();

        // Compute the offset for the next cell in this row
        cellOffset = new Offset(cellOffset.x2 - strokeWidth, cellOffset.y1,
            cellOffset.x2 - strokeWidth + cellDim, cellOffset.y2);
      }

      // Compute the offset for the first cell in a new row
      cellOffset = new Offset(firstCellOffset.x1, firstCellOffset.y1
          + ((cellDim - strokeWidth) * (row + 1)), firstCellOffset.x2,
          firstCellOffset.y2 + ((cellDim - strokeWidth) * (row + 1)));
    }
  }

  /**
   * Computes and returns the dimension of a single cell.
   * 
   * @param columns
   * @param rows
   */
  private double computeCellDimension(int columns, int rows) {
    double cellDim = 0;

    if (drawingArea.getWidth() >= drawingArea.getHeight()) {
      if (rows >= columns) {
        if (rows != 0) {
          cellDim = drawingArea.getWidth() / rows;
        }
      } else {
        if (columns != 0) {
          cellDim = drawingArea.getWidth() / columns;
        }
      }
    } else {
      if (rows >= columns) {
        if (rows != 0) {
          cellDim = drawingArea.getHeight() / rows;
        }
      } else {
        if (columns != 0) {
          cellDim = drawingArea.getHeight() / columns;
        }
      }
    }

    return cellDim;
  }

  /**
   * Computes and returns the width of a grid line.
   * 
   * @param cellDim
   */
  private double computeStrokeWidth(double cellDim) {
    double strokeWidth = 1;

    if (gridSpaceView.getAbsoluteStrokeWidth() != 0
        && gridSpaceView.getRelativeStrokeWidth() != 0) {
      strokeWidth = gridSpaceView.getAbsoluteStrokeWidth();
    } else if (gridSpaceView.getAbsoluteStrokeWidth() != 0) {
      strokeWidth = gridSpaceView.getAbsoluteStrokeWidth();
    } else if (gridSpaceView.getRelativeStrokeWidth() != 0) {
      strokeWidth = Math.ceil((gridSpaceView.getRelativeStrokeWidth() / 100)
          * cellDim);
    }

    // Make sure the stroke width is not bigger than the cell dimension
    if (strokeWidth > cellDim / 2) {
      strokeWidth = cellDim / 2;
    }

    return strokeWidth;
  }

  /**
   * Computes and returns the offset of the first cell.
   * 
   * @param columns
   * @param rows
   * @param cellDim
   * @param strokeWidth
   */
  private Offset computeFirstCellOffset(int columns, int rows, double cellDim,
      double strokeWidth) {
    // Offsets
    double x1 = 0, y1 = 0;
    double x2 = 0, y2 = 0;

    // Calculate y1 of the first cell with respect to the number of cells
    if (rows % 2 != 0)
      y1 = -((cellDim - strokeWidth) * (rows / 2)) - (cellDim / 2);
    else
      y1 = -((cellDim - strokeWidth) * (rows / 2)) - (strokeWidth / 2);

    // Calculate x1 of the first cell with respect to the number of cells
    if (columns % 2 != 0)
      x1 = -((cellDim - strokeWidth) * (columns / 2)) - (cellDim / 2);
    else
      x1 = -((cellDim - strokeWidth) * (columns / 2)) - (strokeWidth / 2);

    // Calculate x2 and y2
    x2 = x1 + cellDim;
    y2 = y1 + cellDim;

    return new Offset(x1, y1, x2, y2);
  }

  /**
   * Determines and returns the color of a cell.
   * 
   * @param column
   * @param row
   */
  private Color determineCellColor(int column, int row) {
    // Check if a background texture is used
    if (gridSpaceView.getBackgroundImg() != null) {
      gridSpaceView.getBackgroundColor().setAlpha(0);
    }

    if (row % 2 == 0) {
      if (column % 2 == 0) {
        if (gridSpaceView.getFill1() != null) {
          return gridSpaceView.getFill1();
        } else {
          return gridSpaceView.getBackgroundColor();
        }
      } else {
        if (gridSpaceView.getFill2() != null) {
          return gridSpaceView.getFill2();
        } else {
          return gridSpaceView.getBackgroundColor();
        }
      }
    } else {
      if (column % 2 == 0) {
        if (gridSpaceView.getFill2() != null) {
          return gridSpaceView.getFill2();
        } else {
          return gridSpaceView.getBackgroundColor();
        }
      } else {
        if (gridSpaceView.getFill1() != null) {
          return gridSpaceView.getFill1();
        } else {
          return gridSpaceView.getBackgroundColor();
        }
      }
    }
  }

  /**
   * Applies state changes of grid properties to the grid, for the initial
   * state.
   */
  public void applyPropertyMaps() {
    if (generalSpaceModel != null) {
      if (aors.space.TwoDimensionalGrid.class.isInstance(generalSpaceModel
          .getSpace())) {
        aors.space.TwoDimensionalGrid grid = (aors.space.TwoDimensionalGrid) generalSpaceModel
            .getSpace();

        // Dimensions of the grid
        int columns = (int) xMax;
        int rows = (int) yMax;

        // Get initial properties for each cell
        for (int row = 0; row < rows; row++) {
          for (int column = 0; column < columns; column++) {
            AbstractCell abstractCell = grid.getSpaceCells()[column][row];

            if (abstractCell != null) {
              for (PropertyMap propertyMap : gridSpaceView.getPropertyMaps()) {
                try {
                  // Get the property value through reflection
                  Field property = abstractCell.getClass().getDeclaredField(
                      propertyMap.getPropertyName());
                  property.setAccessible(true);

                  // Apply the property map
                  updateVisualProperty(propertyMap.getVisualPropertyName(),
                      propertyMap.performMapping(property.get(abstractCell)),
                      column, row);

                } catch (SecurityException e) {
                  e.printStackTrace();
                } catch (NoSuchFieldException e) {
                  continue;
                } catch (IllegalArgumentException e) {
                  e.printStackTrace();
                } catch (IllegalAccessException e) {
                  e.printStackTrace();
                }
              }
            }
          }
        }
      }
    }

    initialGridPropertiesApplied = true;
  }

  /**
   * Applies state changes of grid properties to the grid, during runtime.
   * 
   * @param gridCells
   */
  public void applyPropertyMaps(GridCells gridCells) {
    if (initialGridPropertiesApplied) {
      if (gridSpaceView.getPropertyMaps() != null) {
        for (GridCellType cell : gridCells.getGridCell()) {
          for (PropertyMap propertyMap : gridSpaceView.getPropertyMaps()) {
            if (cell.getSlot() != null) {
              for (SlotType slot : cell.getSlot()) {
                if (slot.getProperty().equals(propertyMap.getPropertyName())) {
                  updateVisualProperty(propertyMap.getVisualPropertyName(),
                      propertyMap.performMapping(slot.getValue()), (int) (cell
                          .getX() - Space.ORDINATEBASE),
                      (int) (cell.getY() - Space.ORDINATEBASE));

                }
              }
            }
          }
        }
      }
    }
  }

  /**
   * Updates a cells visual property.
   * 
   * @param visualPropertyName
   * @param value
   * @param column
   * @param row
   */
  private void updateVisualProperty(String visualPropertyName, String value,
      int column, int row) {
    if (value != null) {
      try {
        // Determine the cells index
        int index = row * (int) xMax + column;

        // Get the cell
        Cell cell = (Cell) spaceComponents.get(index);

        if (visualPropertyName.equals("fill")
            || visualPropertyName.equals("fillRGB")) {
          Color tmp = new Color(value);

          // Only update if the color has really changed
          for (int i = 0; i < tmp.getColor().length; i++) {
            if (tmp.getColor()[i] != cell.getColor().getColor()[i]) {
              // Assign the new value
              cell.setColor(tmp);
              recompile = true;
              break;
            }
          }
        }
      } catch (Exception e) {
        System.out
            .println("Visualization Error: Wrong component index! Check startCountingWithZero ...");
        return;
      }
    }
  }

  /**
   * Compiles the space model's display list.
   * 
   * @param gl
   * @param glu
   */
  public void compileDisplayList(GL2 gl, GLU glu) {
    // Get a denominator for the display list
    displayList = gl.glGenLists(1);

    gl.glNewList(displayList, GL2.GL_COMPILE);
    // Save attribute states
    gl.glPushAttrib(GL2.GL_COLOR_BUFFER_BIT);

    // Apply the background texture
    if (gridSpaceView.getBackgroundImg() != null) {
      applyBackgroundImg(gridSpaceView.getBackgroundImg(), gl);
    }

    for (SpaceComponent comp : spaceComponents) {
      comp.display(gl, glu);
    }

    // Restore attribute states
    gl.glPopAttrib();
    gl.glEndList();
  }

  /**
   * Applies a background image to the grid.
   * 
   * @param backgroundImg
   */
  private void applyBackgroundImg(Texture backgroundImg, GL2 gl) {
    // Get texture coordinates
    TextureCoords texCoords = backgroundImg.getImageTexCoords();

    // Get offsets of the bottom left and top right corner of the grid
    double x1 = ((Cell) spaceComponents.get(0)).getOuterOffset().x1;
    double y1 = ((Cell) spaceComponents.get(0)).getOuterOffset().y1;
    double x2 = ((Cell) spaceComponents.get(spaceComponents.size() - 1))
        .getOuterOffset().x2;
    double y2 = ((Cell) spaceComponents.get(spaceComponents.size() - 1))
        .getOuterOffset().y2;

    // Enable the texture
    backgroundImg.enable();

    // Save attribute states
    gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);

    // White drawing color
    gl.glColor3d(1, 1, 1);

    // Display the texture
    gl.glBegin(GL2.GL_QUADS);
    gl.glTexCoord2d(texCoords.left(), texCoords.bottom());
    gl.glVertex2d(x1, y1);
    gl.glTexCoord2d(texCoords.right(), texCoords.bottom());
    gl.glVertex2d(x2, y1);
    gl.glTexCoord2d(texCoords.right(), texCoords.top());
    gl.glVertex2d(x2, y2);
    gl.glTexCoord2d(texCoords.left(), texCoords.top());
    gl.glVertex2d(x1, y2);
    gl.glEnd();

    // Restore attribute states
    gl.glPopAttrib();

    // Disable the texture
    backgroundImg.disable();
  }

  /**
   * Increases or decreases a cells object count by one.
   * 
   * @param phy
   * @param mode
   */
  public void updateObjectsPerCell(Physical phy, int mode) {
    // Determine the cells index
    int index = ((int) phy.getY() - Space.ORDINATEBASE) * ((int) xMax)
        + ((int) phy.getX() - Space.ORDINATEBASE);

    if (mode == 1) {
      ((Cell) spaceComponents.get(index)).increaseObjCount();
    } else if (mode == 0) {
      ((Cell) spaceComponents.get(index)).decreaseObjCount();
    }
  }

  /**
   * Calculates the position of objects in a cell.
   */
  public void calculateCellObjectPositions() {
    for (SpaceComponent comp : spaceComponents) {
      ((Cell) comp).calculateObjectPositions();
    }
  }

  /**
   * Resets a cells object count.
   */
  public void resetObjectsPerCell() {
    for (SpaceComponent comp : spaceComponents) {
      ((Cell) comp).setObjCount(0);
      ((Cell) comp).setScale(1);
    }
  }

  @Override
  public SpaceView getSpaceView() {
    return gridSpaceView;
  }

  @Override
  public void setSpaceView(SpaceView spaceView) {
    gridSpaceView = (GridSpaceView) spaceView;
  }

  public boolean isInitialGridPropertiesApplied() {
    return initialGridPropertiesApplied;
  }

  public void setInitialGridPropertiesApplied(
      boolean initialGridPropertiesApplied) {
    this.initialGridPropertiesApplied = initialGridPropertiesApplied;
  }
}
