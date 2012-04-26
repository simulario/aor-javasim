package aors.module.visopengl3d.space.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl3d.engine.TessellatedPolygon;
import aors.module.visopengl3d.space.view.SpaceView;
import aors.module.visopengl3d.utility.Color;
import aors.module.visopengl3d.utility.Offset;
import aors.module.visopengl3d.utility.VectorOperations;

import com.sun.opengl.util.texture.Texture;

/**
 * Cell of a two dimensional grid.
 * 
 * @author Sebastian Mucha, Susanne Schölzel
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
  private ArrayList<double[]> outerContourTop;
  private ArrayList<double[]> innerContourTop;
  private ArrayList<double[]> outerContourBottom;
  private ArrayList<double[]> innerContourBottom;

  // Number of objects inside of the cell
  private int objCount;

  // Radius of the cells inner circle
  private double inRadius;

  // Scale factor for objects inside of the cell
  private double scale = 1;

  // Map of object positions
  private Map<Integer, double[]> positionMap = new HashMap<Integer, double[]>();
  
  private double cellHeight;
  
  private Texture texture;
  private ArrayList<double[]> textureCoords; 

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

        /*pos[0] = center[0] + Math.cos(halfAngle * (Math.PI / 180))
            * (inRadius / 2);
        pos[1] = center[1] + Math.sin(halfAngle * (Math.PI / 180))
            * (inRadius / 2);
        pos[2] = 0;*/
        
        pos[0] = center[0] + Math.cos(halfAngle * (Math.PI / 180))
            * (inRadius / 2);
        pos[1] = SpaceView.getObjectHeight() / 2;
        pos[2] = center[2] - Math.sin(halfAngle * (Math.PI / 180))
            * (inRadius / 2);

        // Move to next segment
        halfAngle += segmentAngle;

        // Add the position into the map
        positionMap.put(i, pos);
      }

    } else if (objCount == 1) {
      // One object will be positioned at the cell's center
      double pos[] = new double[3];
      
      pos[0] = center[0];
      pos[1] = SpaceView.getObjectHeight() / 2;
      pos[2] = center[2];
      
      positionMap.put(1, pos);
    } else {
      return;
    }
  }

  /**
   * Creates the contours of the cell.
   */
  public void createCellContours() {
    if (outerOffset != null && innerOffset != null) {
      // Set up outer contour top (4 points in counterclockwise order)
      double outerVertexTop0[] = new double[12];
      double outerVertexTop1[] = new double[12];
      double outerVertexTop2[] = new double[12];
      double outerVertexTop3[] = new double[12];
      
      // Bottom left corner
      outerVertexTop0[0] = outerOffset.x1;
      outerVertexTop0[2] = -outerOffset.y1;

      // Bottom right corner
      outerVertexTop1[0] = outerOffset.x2;
      outerVertexTop1[2] = -outerOffset.y1;

      // Top right corner
      outerVertexTop2[0] = outerOffset.x2;
      outerVertexTop2[2] = -outerOffset.y2;

      // Top left corner
      outerVertexTop3[0] = outerOffset.x1;
      outerVertexTop3[2] = -outerOffset.y2;

      // Add vertices to outer contour top list
      outerContourTop = new ArrayList<double[]>();
      outerContourTop.add(outerVertexTop0);
      outerContourTop.add(outerVertexTop1);
      outerContourTop.add(outerVertexTop2);
      outerContourTop.add(outerVertexTop3);
      
      
      // Set up outer contour bottom (4 points in clockwise order)
      double outerVertexBottom0[] = new double[12];
      double outerVertexBottom1[] = new double[12];
      double outerVertexBottom2[] = new double[12];
      double outerVertexBottom3[] = new double[12];
      
      // Top left corner
      outerVertexBottom0[0] = outerOffset.x1;
      outerVertexBottom0[1] = -cellHeight;
      outerVertexBottom0[2] = -outerOffset.y2;
      
      // Top right corner
      outerVertexBottom1[0] = outerOffset.x2;
      outerVertexBottom1[1] = -cellHeight;
      outerVertexBottom1[2] = -outerOffset.y2;
      
      // Bottom right corner
      outerVertexBottom2[0] = outerOffset.x2;
      outerVertexBottom2[1] = -cellHeight;
      outerVertexBottom2[2] = -outerOffset.y1;
      
      // Bottom left corner
      outerVertexBottom3[0] = outerOffset.x1;
      outerVertexBottom3[1] = -cellHeight;
      outerVertexBottom3[2] = -outerOffset.y1;
      
      // Add vertices to outer contour bottom list in clockwise order
      outerContourBottom = new ArrayList<double[]>();
      outerContourBottom.add(outerVertexBottom0);
      outerContourBottom.add(outerVertexBottom1);
      outerContourBottom.add(outerVertexBottom2);
      outerContourBottom.add(outerVertexBottom3);

      
      // Set up inner contour top (4 points in counterclockwise order)
      double innerVertexTop0[] = new double[12];
      double innerVertexTop1[] = new double[12];
      double innerVertexTop2[] = new double[12];
      double innerVertexTop3[] = new double[12];
      
      // Bottom left corner
      innerVertexTop0[0] = innerOffset.x1;
      innerVertexTop0[2] = -innerOffset.y1;

      // Bottom right corner
      innerVertexTop1[0] = innerOffset.x2;
      innerVertexTop1[2] = -innerOffset.y1;

      // Top right corner
      innerVertexTop2[0] = innerOffset.x2;
      innerVertexTop2[2] = -innerOffset.y2;

      // Top left corner
      innerVertexTop3[0] = innerOffset.x1;
      innerVertexTop3[2] = -innerOffset.y2;

      // Add vertices to inner contour top list
      innerContourTop = new ArrayList<double[]>();
      innerContourTop.add(innerVertexTop0);
      innerContourTop.add(innerVertexTop1);
      innerContourTop.add(innerVertexTop2);
      innerContourTop.add(innerVertexTop3);
      
      
      // Set up inner contour bottom (4 points in clockwise order)
      double innerVertexBottom0[] = new double[12];
      double innerVertexBottom1[] = new double[12];
      double innerVertexBottom2[] = new double[12];
      double innerVertexBottom3[] = new double[12];
      
      // Top left corner
      innerVertexBottom0[0] = innerOffset.x1;
      innerVertexBottom0[1] = -cellHeight;
      innerVertexBottom0[2] = -innerOffset.y2;
      
      // Top right corner
      innerVertexBottom1[0] = innerOffset.x2;
      innerVertexBottom1[1] = -cellHeight;
      innerVertexBottom1[2] = -innerOffset.y2;
      
      // Bottom right corner
      innerVertexBottom2[0] = innerOffset.x2;
      innerVertexBottom2[1] = -cellHeight;
      innerVertexBottom2[2] = -innerOffset.y1;
      
      // Bottom left corner
      innerVertexBottom3[0] = innerOffset.x1;
      innerVertexBottom3[1] = -cellHeight;
      innerVertexBottom3[2] = -innerOffset.y1;

      // Add vertices to inner contour bottom list
      innerContourBottom = new ArrayList<double[]>();
      innerContourBottom.add(innerVertexBottom0);
      innerContourBottom.add(innerVertexBottom1);
      innerContourBottom.add(innerVertexBottom2);
      innerContourBottom.add(innerVertexBottom3);
    }
  }

  /**
   * Applies a color to a contour.
   * 
   * @param contour
   * @param color
   */
  private void applyContourColor(ArrayList<double[]> contour, Color color, boolean top) {
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
        
        if(top) {
          vertex[9] = 0;
          vertex[10] = 1;
          vertex[11] = 0;
        } else {
          vertex[9] = 0;
          vertex[10] = -1;
          vertex[11] = 0;
        }
      }
    }
  }
  
  /**
   * Applies a color to a contour.
   * 
   * @param contour
   * @param color
   */
  private void applyContourTexture(ArrayList<double[]> contour, Texture texture, ArrayList<double[]> textureCoordinates, boolean top) {
    if (contour != null) {
      for (int i=0; i<contour.size(); i++) {
        double[] vertex = contour.get(i);
        
        vertex[3] = 1;
        vertex[4] = 1;
        vertex[5] = 1;
        vertex[6] = 1;
        
        if(top) {
          // texture
          vertex[7] = textureCoordinates.get(i)[0];
          vertex[8] = textureCoordinates.get(i)[1];
        } else {
          vertex[7] = textureCoordinates.get(contour.size()-1 - i)[0];
          vertex[8] = textureCoordinates.get(contour.size()-1 - i)[1];
        }
        
        if(top) {
          vertex[9] = 0;
          vertex[10] = 1;
          vertex[11] = 0;
        } else {
          vertex[9] = 0;
          vertex[10] = -1;
          vertex[11] = 0;
        }
      }
    }
  }

  @Override
  public void display(GL2 gl, GLU glu) {
    TessellatedPolygon cellTop = new TessellatedPolygon();
    cellTop.init(gl, glu);
    
    if(borderWidth != 0) {
      // Apply the border color
      applyContourColor(outerContourTop, borderColor, true);
      applyContourColor(innerContourTop, borderColor, true);
  
      // Draw the border
      cellTop.beginPolygon();
      cellTop.setWindingRule(GLU.GLU_TESS_WINDING_ODD);
      cellTop.beginContour();
      cellTop.renderContour(outerContourTop);
      cellTop.endContour();
      cellTop.beginContour();
      cellTop.renderContour(innerContourTop);
      cellTop.endContour();
      cellTop.endPolygon();
    }
    
    if(texture != null && color.getAlpha() == 0) {
      //cellTop.end();
      
      // Apply the cell texture
      applyContourTexture(innerContourTop, texture, textureCoords, true);
      
      //gl.glColor4dv(Color.WHITE.getColor(), 0);
      
      texture.bind();
      texture.enable();
      
      // Draw the interior
      cellTop.beginPolygon();
      cellTop.beginContour();
      cellTop.renderContour(innerContourTop);
      cellTop.endContour();
      cellTop.endPolygon();
      cellTop.end();
      
      /*gl.glBegin(GL2.GL_QUADS);
      
      gl.glNormal3d(innerContourTop.get(0)[9], innerContourTop.get(0)[10], innerContourTop.get(0)[11]);
      gl.glTexCoord2d(textureCoords.get(0)[0], textureCoords.get(0)[1]); gl.glVertex3d(innerContourTop.get(0)[0], innerContourTop.get(0)[1], innerContourTop.get(0)[2]);
      gl.glTexCoord2d(textureCoords.get(1)[0], textureCoords.get(1)[1]); gl.glVertex3d(innerContourTop.get(1)[0], innerContourTop.get(1)[1], innerContourTop.get(1)[2]);
      gl.glTexCoord2d(textureCoords.get(2)[0], textureCoords.get(2)[1]); gl.glVertex3d(innerContourTop.get(2)[0], innerContourTop.get(2)[1], innerContourTop.get(2)[2]);
      gl.glTexCoord2d(textureCoords.get(3)[0], textureCoords.get(3)[1]); gl.glVertex3d(innerContourTop.get(3)[0], innerContourTop.get(3)[1], innerContourTop.get(3)[2]);
      
      gl.glEnd();*/
      
      texture.disable();
    } else {
      // Apply the cell color
      applyContourColor(innerContourTop, color, true);
      
      // Draw the interior
      cellTop.beginPolygon();
      cellTop.beginContour();
      cellTop.renderContour(innerContourTop);
      cellTop.endContour();
      cellTop.endPolygon();
      cellTop.end();
    }
    
    
    
    
    TessellatedPolygon cellBottom = new TessellatedPolygon();
    cellBottom.init(gl, glu);

    if(borderWidth != 0) {
      // Apply the border color
      applyContourColor(outerContourBottom, borderColor, false);
      applyContourColor(innerContourBottom, borderColor, false);
  
      // Draw the border
      cellBottom.beginPolygon();
      cellBottom.setWindingRule(GLU.GLU_TESS_WINDING_ODD);
      cellBottom.beginContour();
      cellBottom.renderContour(outerContourBottom);
      cellBottom.endContour();
      cellBottom.beginContour();
      cellBottom.renderContour(innerContourBottom);
      cellBottom.endContour();
      cellBottom.endPolygon();
    }

    if(texture != null && color.getAlpha() == 0) {
      // Apply the cell color
      applyContourTexture(innerContourBottom, texture, textureCoords, false);
      
      texture.bind();
      texture.enable();
      
      // Draw the interior
      cellBottom.beginPolygon();
      cellBottom.beginContour();
      cellBottom.renderContour(innerContourBottom);
      cellBottom.endContour();
      cellBottom.endPolygon();
      cellBottom.end();
      
      texture.disable();
    } else {
      // Apply the cell color
      applyContourColor(innerContourBottom, color, false);
      
      // Draw the interior
      cellBottom.beginPolygon();
      cellBottom.beginContour();
      cellBottom.renderContour(innerContourBottom);
      cellBottom.endContour();
      cellBottom.endPolygon();
      cellBottom.end();
    }
    
    
    // draw the side faces of the cell
    gl.glBegin(GL2.GL_QUADS);
    
    // Set the drawing color
    if(borderWidth != 0) {
      gl.glColor4dv(borderColor.getColor(), 0);
    } else {
      Color sideColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 1);
      gl.glColor4dv(sideColor.getColor(), 0);
    }
    
    int lastIndex = outerContourTop.size()-1;
    
    for(int i = 0; i < lastIndex; i++) {
      double[] normal = VectorOperations.crossProduct(
                          VectorOperations.subtractVectors(outerContourBottom.get(lastIndex-i), outerContourTop.get(i)),
                          VectorOperations.subtractVectors(outerContourTop.get(i+1), outerContourTop.get(i)));
      
      VectorOperations.normalize(normal);
      gl.glNormal3dv(normal, 0);
      gl.glVertex3dv(outerContourBottom.get(lastIndex-i), 0);
      gl.glVertex3dv(outerContourBottom.get(lastIndex-(i+1)), 0);
      gl.glVertex3dv(outerContourTop.get(i+1), 0);
      gl.glVertex3dv(outerContourTop.get(i), 0);
    }
    
    double[] normal = VectorOperations.crossProduct(
                        VectorOperations.subtractVectors(outerContourBottom.get(lastIndex-lastIndex), outerContourTop.get(lastIndex)),
                        VectorOperations.subtractVectors(outerContourTop.get(0), outerContourTop.get(lastIndex)));

    VectorOperations.normalize(normal);
    gl.glNormal3dv(normal, 0);
    gl.glVertex3dv(outerContourBottom.get(lastIndex-lastIndex), 0);
    gl.glVertex3dv(outerContourBottom.get(lastIndex-0), 0);
    gl.glVertex3dv(outerContourTop.get(0), 0);
    gl.glVertex3dv(outerContourTop.get(lastIndex), 0);
    
    gl.glEnd();
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
      //center[0] = (innerOffset.getWidth() / 2) + innerOffset.x1;
      //center[1] = (innerOffset.getHeight() / 2) + innerOffset.y1;
      //center[2] = 0;
      
      center[0] = (innerOffset.getWidth() / 2) + innerOffset.x1;
      center[1] = 0;
      center[2] = -((innerOffset.getHeight() / 2) + innerOffset.y1);
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
    //center[0] = (innerOffset.getWidth() / 2) + innerOffset.x1;
    //center[1] = (innerOffset.getHeight() / 2) + innerOffset.y1;
    //center[2] = 0;
    
    center[0] = (innerOffset.getWidth() / 2) + innerOffset.x1;
    center[1] = 0;
    center[2] = -((innerOffset.getHeight() / 2) + innerOffset.y1);
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
  
  public double getCellHeight() {
    return cellHeight;
  }

  public void setCellHeight(double cellHeight) {
    this.cellHeight = cellHeight;
  }
  
  public Texture getTexture() {
    return texture;
  }

  public void setTexture(Texture texture) {
    this.texture = texture;
  }
  
  public ArrayList<double[]> getTextureCoords() {
    return textureCoords;
  }

  public void setTextureCoords(ArrayList<double[]> textureCoords) {
    this.textureCoords = textureCoords;
  }
}
