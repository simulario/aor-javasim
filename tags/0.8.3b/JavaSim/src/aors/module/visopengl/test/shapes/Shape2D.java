package aors.module.visopengl.test.shapes;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl.test.utility.Color;

import com.sun.opengl.util.texture.TextureCoords;

/**
 * The Shape2D class is the base class for all two dimensional shapes.
 * 
 * @author Sebastian Mucha
 * @since February 17th, 2010
 */
public abstract class Shape2D {

  // Opacity of the whole shape
  protected double opacity = 1;

  // Color of the shape
  protected Color fillColor = new Color(Color.BLACK);

  // Display list
  protected int displayList;

  /**
   * Generates the shape's display list. Each specialized shape has to implement
   * this method.
   * 
   * @param gl
   *          the OpenGL pipeline object
   * @param glu
   *          the OpenGL utility object
   */
  public abstract void generateDisplayList(GL2 gl, GLU glu);

  /**
   * Displays the shape.
   * 
   * @param gl
   *          the OpenGL pipeline object
   */
  public void display(GL2 gl) {
    gl.glCallList(displayList);
  }

  /**
   * Calculate the coordinates of either the contour of the whole shape or the
   * contour of its border. If you just want to calculate the contour of the
   * whole rectangle the second parameter will be null.
   * 
   * @param contour
   *          List storing vertices of the shapes contour
   * @param border
   *          List storing vertices of the shapes border
   */
  protected abstract void calculateContour(ArrayList<double[]> contour,
      ArrayList<double[]> border);

  /**
   * Applies a color to each vertex in a list of vertices.
   * 
   * @param contour
   *          list of vertices
   * @param color
   *          the color that will be applied to each vertex
   */
  protected void applyColor(ArrayList<double[]> contour, Color color) {

    for (double[] vertex : contour) {
      // Set the RGBA components
      vertex[3] = color.getRed();
      vertex[4] = color.getGreen();
      vertex[5] = color.getBlue();
      vertex[6] = color.getAlpha();
    }
  }

  /**
   * Performs a mapping from object coordinates into texture coordinates. If the
   * second parameter equals null texture coordinates will be set to 0.
   * 
   * @param contour
   *          a reference to a list storing vertices
   * @param tc
   *          the image coordinates of the texture image
   */
  protected void applyTexture(ArrayList<double[]> contour, TextureCoords tc) {
    /*
     * Initialize the texture coordinates to 0 if no image coordinates are
     * available.
     */
    if (tc == null) {
      for (double[] vertex : contour) {
        vertex[7] = 0;
        vertex[8] = 0;
      }
    } else {
      // Maximal and minimal extensions of the polygon
      double xMin = contour.get(0)[0];
      double xMax = contour.get(0)[0];
      double yMin = contour.get(0)[1];
      double yMax = contour.get(0)[1];

      // Loop over all vertices to find the maxima and minima
      for (double[] vertex : contour) {
        if (vertex[0] < xMin)
          xMin = vertex[0];

        if (vertex[0] > xMax)
          xMax = vertex[0];

        if (vertex[1] < yMin)
          yMin = vertex[1];

        if (vertex[1] > yMax)
          yMax = vertex[1];
      }

      // Dimensions of the polygon
      double width = xMax - xMin;
      double height = yMax - yMin;

      // Texture coordinates
      double s = 0, t = 0;

      // Loop over all vertices to find the appropriate texture coordinates
      for (double[] vertex : contour) {
        s = tc.left() + (width - (xMax - vertex[0])) / width;
        t = tc.bottom() - (height - (yMax - vertex[1])) / height;

        vertex[7] = s;
        vertex[8] = t;
      }
    }
  }

  // Setter & Getter -----------------------------------------------------------

  public double getOpacity() {
    return opacity;
  }

  public void setOpacity(double opacity) {
    this.opacity = opacity;
  }

  public Color getFillColor() {
    return fillColor;
  }

  public void setFillColor(Color fillColor) {
    this.fillColor = fillColor;
  }

}
