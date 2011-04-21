package aors.module.visopengl3d.space.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl3d.engine.TessellatedPolygon;
import aors.module.visopengl3d.utility.Color;
import aors.module.visopengl3d.utility.Offset;

/**
 * Cell of a two dimensional grid.
 * 
 * @author Sebastian Mucha
 * @since March 18th, 2010
 * 
 */
public class Cell implements SpaceComponent {

  // Cell color
  private Color color;

  // Border color
  private Color borderColor;

  // Border width
  private double borderWidth;

  // Cell center
  private double center[] = new double[3];

  // Offsets
  private Offset outerOffset;
  private Offset innerOffset;

  // Contours
  private ArrayList<double[]> outerContour;
  private ArrayList<double[]> innerContour;

  // Number of objects inside of the cell
  private int objCount;

  // Radius of the cells inner circle
  private double inRadius;

  // Scale factor for objects inside of the cell
  private double scale = 1;

  // Map of object positions
  private Map<Integer, double[]> positionMap = new HashMap<Integer, double[]>();

  /**
   * Increases the number of objects inside of the cell by one.
   */
  public void increaseObjCount() {
    objCount++;
  }

  /**
   * Decreases the number of objects inside of the cell by one.
   */
  public void decreaseObjCount() {
    objCount--;

    if (objCount < 0) {
      objCount = 0;
    }
  }

  /**
   * Calculates the position of objects that will be placed into the cell.
   */
  public void calculateObjectPositions() {
    positionMap.clear();
    inRadius = (innerOffset.getWidth() - 2) / 2;

    if (objCount > 1) {
      scale = objCount;

      // Angle of a circle segment an object will be placed into
      double segmentAngle = 360.0 / objCount;

      // Half of the segment angle
      double halfAngle = segmentAngle / 2;

      for (int i = 1; i <= objCount; i++) {
        // Object position
        double pos[] = new double[3];

        pos[0] = center[0] + Math.cos(halfAngle * (Math.PI / 180))
            * (inRadius / 2);
        pos[1] = center[1] + Math.sin(halfAngle * (Math.PI / 180))
            * (inRadius / 2);
        pos[2] = 0;

        // Move to next segment
        halfAngle += segmentAngle;

        // Add the position into the map
        positionMap.put(i, pos);
      }

    } else if (objCount == 1) {
      // One object will be positioned at the cell's center
      positionMap.put(1, center);
    } else {
      return;
    }
  }

  /**
   * Creates the contours of the cell.
   */
  public void createCellContours() {
    if (outerOffset != null && innerOffset != null) {
      // Set up outer contour (4 points in counterclockwise order)
      double outerVertex0[] = new double[9];
      double outerVertex1[] = new double[9];
      double outerVertex2[] = new double[9];
      double outerVertex3[] = new double[9];

      // Bottom left corner
      outerVertex0[0] = outerOffset.x1;
      outerVertex0[1] = outerOffset.y1;

      // Bottom right corner
      outerVertex1[0] = outerOffset.x2;
      outerVertex1[1] = outerOffset.y1;

      // Top right corner
      outerVertex2[0] = outerOffset.x2;
      outerVertex2[1] = outerOffset.y2;

      // Top left corner
      outerVertex3[0] = outerOffset.x1;
      outerVertex3[1] = outerOffset.y2;

      // Add vertices to outer contour list
      outerContour = new ArrayList<double[]>();
      outerContour.add(outerVertex0);
      outerContour.add(outerVertex1);
      outerContour.add(outerVertex2);
      outerContour.add(outerVertex3);

      // Set up inner contour (4 points in counterclockwise order)
      double innerVertex0[] = new double[9];
      double innerVertex1[] = new double[9];
      double innerVertex2[] = new double[9];
      double innerVertex3[] = new double[9];

      // Bottom left corner
      innerVertex0[0] = innerOffset.x1;
      innerVertex0[1] = innerOffset.y1;

      // Bottom right corner
      innerVertex1[0] = innerOffset.x2;
      innerVertex1[1] = innerOffset.y1;

      // Top right corner
      innerVertex2[0] = innerOffset.x2;
      innerVertex2[1] = innerOffset.y2;

      // Top left corner
      innerVertex3[0] = innerOffset.x1;
      innerVertex3[1] = innerOffset.y2;

      // Add vertices to outer contour list
      innerContour = new ArrayList<double[]>();
      innerContour.add(innerVertex0);
      innerContour.add(innerVertex1);
      innerContour.add(innerVertex2);
      innerContour.add(innerVertex3);
    }
  }

  /**
   * Applies a color to a contour.
   * 
   * @param contour
   * @param color
   */
  private void applyContourColor(ArrayList<double[]> contour, Color color) {
    if (contour != null) {
      for (double[] vertex : contour) {
        if (color != null) {
          vertex[3] = color.getRed();
          vertex[4] = color.getGreen();
          vertex[5] = color.getBlue();
          vertex[6] = color.getAlpha();
        } else {
          vertex[3] = 0;
          vertex[4] = 0;
          vertex[5] = 0;
          vertex[6] = 1;
        }

        // No texture
        vertex[7] = 0;
        vertex[8] = 0;
      }
    }
  }

  @Override
  public void display(GL2 gl, GLU glu) {
    TessellatedPolygon cell = new TessellatedPolygon();
    cell.init(gl, glu);

    // Apply the border color
    applyContourColor(outerContour, borderColor);
    applyContourColor(innerContour, borderColor);

    // Draw the border
    cell.beginPolygon();
    cell.setWindingRule(GLU.GLU_TESS_WINDING_ODD);
    cell.beginContour();
    cell.renderContour(outerContour);
    cell.endContour();
    cell.beginContour();
    cell.renderContour(innerContour);
    cell.endContour();
    cell.endPolygon();

    // Apply the cell color
    applyContourColor(innerContour, color);

    // Draw the interior
    cell.beginPolygon();
    cell.beginContour();
    cell.renderContour(innerContour);
    cell.endContour();
    cell.endPolygon();
    cell.end();
  }

  @Override
  public double getObjectHeight(double height) {
    return (1 / scale) * (inRadius * 2) - 2;
  }

  @Override
  public double getObjectWidth(double width) {
    return (1 / scale) * (inRadius * 2) - 2;
  }

  @Override
  public double getRotation(double x, double y) {
    return 0;
  }

  @Override
  public double[] getWorldCoordinates(double x, double y) {
    return positionMap.get(objCount);
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public Color getBorderColor() {
    return borderColor;
  }

  public void setBorderColor(Color borderColor) {
    this.borderColor = borderColor;
  }

  public double getBorderWidth() {
    return borderWidth;
  }

  public void setBorderWidth(double borderWidth) {
    this.borderWidth = borderWidth;

    // Adjust the inner offset
    if (outerOffset != null) {
      innerOffset = new Offset(outerOffset.x1 + borderWidth, outerOffset.y1
          + borderWidth, outerOffset.x2 - borderWidth, outerOffset.y2
          - borderWidth);

      // Calculate the cells center
      center[0] = (innerOffset.getWidth() / 2) + innerOffset.x1;
      center[1] = (innerOffset.getHeight() / 2) + innerOffset.y1;
      center[2] = 0;
    }
  }

  public Offset getOuterOffset() {
    return outerOffset;
  }

  public void setOuterOffset(Offset outerOffset) {
    this.outerOffset = outerOffset;
  }

  public Offset getInnerOffset() {
    return innerOffset;
  }

  public void setInnerOffset(Offset outerOffset, double strokeWidth) {
    this.borderWidth = strokeWidth;
    this.innerOffset = new Offset(outerOffset.x1 + strokeWidth, outerOffset.y1
        + strokeWidth, outerOffset.x2 - strokeWidth, outerOffset.y2
        - strokeWidth);

    // Calculate the cells center
    center[0] = (innerOffset.getWidth() / 2) + innerOffset.x1;
    center[1] = (innerOffset.getHeight() / 2) + innerOffset.y1;
    center[2] = 0;
  }

  public int getObjCount() {
    return objCount;
  }

  public void setObjCount(int objCount) {
    this.objCount = objCount;
  }

  public double getScale() {
    return scale;
  }

  public void setScale(double scale) {
    this.scale = scale;
  }
}
