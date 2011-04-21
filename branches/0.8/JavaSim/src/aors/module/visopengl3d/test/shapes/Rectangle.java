package aors.module.visopengl3d.test.shapes;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl3d.test.utility.Color;
import aors.module.visopengl3d.test.utility.TessellatedPolygon;

import com.sun.opengl.util.texture.Texture;

/**
 * The Rectangle class is representing a simple geometric rectangle with a
 * border.
 * 
 * @author Sebastian Mucha
 * @since February 17th, 2010
 */
public class Rectangle extends Shape2D {

  // Dimensions of the rectangle
  private double width, height;

  // Width of the rectangles border
  private double borderWidth;

  // Texture of the rectangle
  Texture texture;

  // Color of the rectangles border
  private Color borderColor = new Color(Color.WHITE);

  /*
   * The fill opacity refers only to the interior of the shape (i.e. without the
   * border). The object opacity (opacity) has always a higher priority as the
   * fill opacity.
   */
  private double fillOpacity = 1;

  /**
   * Create a new Rectangle instance and initialize its dimensions and border
   * width.
   * 
   * @param width
   *          the width of the rectangle
   * @param height
   *          the height of the rectangle
   * @param borderWidth
   *          the width of the border
   */
  public Rectangle(double width, double height, double borderWidth) {
    this.width = width;
    this.height = height;
    this.borderWidth = borderWidth;
  }

  @Override
  public void generateDisplayList(GL2 gl, GLU glu) {
    /*
     * List of all vertices describing the contour of the rectangle, as well as
     * the vertices color and texture coordinates.
     */
    ArrayList<double[]> rectangle = null;

    /*
     * List of all vertices describing the contour of the border, as well as the
     * vertices color and texture coordinates.
     */
    ArrayList<double[]> borderRectangle = null;

    // Get a denominator for the display list
    displayList = gl.glGenLists(1);

    // Create the display list
    gl.glNewList(displayList, GL2.GL_COMPILE);
    // Store the current OpenGL attribute states
    gl.glPushAttrib(GL2.GL_ALL_ATTRIB_BITS);

    /*
     * Set the alpha value of the colors. The opacity of the whole object
     * (opacity) always has a higher priority.
     */
    if (opacity < 1) {
      fillColor.setAlpha(opacity);
      borderColor.setAlpha(opacity);
    } else {
      fillColor.setAlpha(fillOpacity);
      borderColor.setAlpha(1);
    }

    // Don't draw if the dimensions are too small
    if (width > 0 && height > 0) {

      // Create a list storing the rectangles vertices
      rectangle = new ArrayList<double[]>();

      // Check if the rectangle will be rendered with a texture applied on it
      if (texture != null) {

        // Calculate the rectangles contour
        calculateContour(rectangle, null);

        // Set the rectangles color to white (because of texture)
        applyColor(rectangle, new Color(Color.WHITE));

        // Map the texture to the rectangle
        applyTexture(rectangle, texture.getImageTexCoords());

        // Enable texture support
        texture.enable();

        // Draw the textured rectangle
        TessellatedPolygon poly = new TessellatedPolygon();
        poly.init(gl, glu);
        poly.beginPolygon();
        poly.beginContour();
        poly.renderContour(rectangle);
        poly.endContour();
        poly.endPolygon();
        poly.end();
        poly = null;

        // Disable texture support
        texture.disable();
      } else {

        // Check if the rectangle will be rendered with a border
        if (borderWidth > 0) {

          // Create a list storing the borders vertices
          borderRectangle = new ArrayList<double[]>(1);

          // Calculate the rectangles contour and the contour of the border
          calculateContour(rectangle, null);
          calculateContour(rectangle, borderRectangle);

          // Apply the border color
          applyColor(rectangle, borderColor);
          applyColor(borderRectangle, borderColor);

          // Don't apply any texture
          applyTexture(rectangle, null);
          applyTexture(borderRectangle, null);

          // Draw the border
          TessellatedPolygon poly = new TessellatedPolygon();
          poly.init(gl, glu);
          poly.setWindingRule(GLU.GLU_TESS_WINDING_ODD);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(rectangle);
          poly.endContour();
          poly.beginContour();
          poly.renderContour(borderRectangle);
          poly.endContour();
          poly.endPolygon();
          poly.end();

          // Apply the fill color
          applyColor(borderRectangle, fillColor);

          // Draw the inner rectangle
          poly.init(gl, glu);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(borderRectangle);
          poly.endContour();
          poly.endPolygon();
          poly.end();
          poly = null;
        } else {

          // Calculate the contour of the rectangle
          calculateContour(rectangle, null);

          // Apply the fill color to the rectangle
          applyColor(rectangle, fillColor);

          // Don't apply any texture
          applyTexture(rectangle, null);

          // Draw the rectangle without border
          TessellatedPolygon poly = new TessellatedPolygon();
          poly.init(gl, glu);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(rectangle);
          poly.endContour();
          poly.endPolygon();
          poly.end();
          poly = null;
        }
      }
    }

    // Restore the OpenGL attribute states
    gl.glPopAttrib();
    gl.glEndList();
  }

  @Override
  protected void calculateContour(ArrayList<double[]> contour,
      ArrayList<double[]> border) {

    if (border == null) {
      /*
       * A rectangles contour consists of 4 points, starting at the bottom left
       * corner in counterclockwise order.
       */
      double[] p0 = new double[9];
      double[] p1 = new double[9];
      double[] p2 = new double[9];
      double[] p3 = new double[9];

      // Bottom left corner
      p0[0] = -width / 2;
      p0[1] = -height / 2;

      // Bottom right corner
      p1[0] = width / 2;
      p1[1] = p0[1];

      // Top right corner
      p2[0] = p1[0];
      p2[1] = height / 2;

      // Top left corner
      p3[0] = p0[0];
      p3[1] = p2[1];

      // Add the points to the contour list
      contour.add(p0);
      contour.add(p1);
      contour.add(p2);
      contour.add(p3);
    }

    // Calculate the contour of the border polygon
    else {
      calculateBorder(contour, border);
    }
  }

  /**
   * Calculate the coordinates of the rectangles border.
   * 
   * @param contour
   *          a reference to the list storing the rectangles contour
   * @param border
   *          a reference to the list storing the rectangles border
   */
  private void calculateBorder(ArrayList<double[]> contour,
      ArrayList<double[]> border) {

    // Make sure the border width is valid
    if (width - (2 * borderWidth) < 0)
      borderWidth = width / 2;

    if (height - (2 * borderWidth) < 0)
      borderWidth = height / 2;

    /*
     * A rectangles border consists of 4 points, starting at the bottom left
     * corner in counterclockwise order.
     */
    double[] p0 = new double[9];
    double[] p1 = new double[9];
    double[] p2 = new double[9];
    double[] p3 = new double[9];

    // Bottom left corner
    p0[0] = contour.get(0)[0] + borderWidth;
    p0[1] = contour.get(0)[1] + borderWidth;

    // Bottom right corner
    p1[0] = contour.get(1)[0] - borderWidth;
    p1[1] = p0[1];

    // Top right corner
    p2[0] = p1[0];
    p2[1] = contour.get(2)[1] - borderWidth;

    // Top left corner
    p3[0] = p0[0];
    p3[1] = p2[1];

    // Add the points to the border list
    border.add(p0);
    border.add(p1);
    border.add(p2);
    border.add(p3);
  }

  // Setter & Getter -----------------------------------------------------------

  public double getWidth() {
    return width;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  public double getHeight() {
    return height;
  }

  public void setHeight(double height) {
    this.height = height;
  }

  public double getBorderWidth() {
    return borderWidth;
  }

  public void setBorderWidth(double borderWidth) {
    this.borderWidth = borderWidth;
  }

  public Texture getTexture() {
    return texture;
  }

  public void setTexture(Texture texture) {
    this.texture = texture;
  }

  public Color getBorderColor() {
    return borderColor;
  }

  public void setBorderColor(Color borderColor) {
    this.borderColor = borderColor;
  }

  public double getFillOpacity() {
    return fillOpacity;
  }

  public void setFillOpacity(double fillOpacity) {
    this.fillOpacity = fillOpacity;
  }

}
