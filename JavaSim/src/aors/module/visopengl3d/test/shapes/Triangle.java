package aors.module.visopengl3d.test.shapes;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl3d.test.utility.Color;
import aors.module.visopengl3d.test.utility.TessellatedPolygon;

import com.sun.opengl.util.texture.Texture;

/**
 * The Triangle class is representing a simple geometric, equal sided triangle
 * with a border.
 * 
 * @author Sebastian Mucha
 * @since February 19th, 2010
 */
public class Triangle extends Shape2D {

  // Side length
  private double sideLength;

  // Width of the triangles border
  private double borderWidth;

  // Texture of the triangle
  Texture texture;

  // Color of the triangles border
  private Color borderColor = new Color(Color.WHITE);

  /*
   * The fill opacity refers only to the interior of the shape (i.e. without the
   * border). The object opacity (opacity) has always a higher priority as the
   * fill opacity.
   */
  private double fillOpacity = 1;

  /**
   * Create a new Triangle instance and initialize its dimensions and border
   * width.
   * 
   * @param sideLength
   *          the length of one side of the triangle
   * @param borderWidth
   *          the width of the border
   */
  public Triangle(double sideLength, double borderWidth) {
    this.sideLength = sideLength;
    this.borderWidth = borderWidth;
  }

  @Override
  protected void calculateContour(ArrayList<double[]> contour,
      ArrayList<double[]> border) {

    if (border == null) {
      /*
       * A triangles contour consists of 3 points, starting at the bottom left
       * corner in counterclockwise order.
       */
      double[] p0 = new double[9];
      double[] p1 = new double[9];
      double[] p2 = new double[9];

      // Radii of the triangle
      double circumradius = (sideLength * Math.sqrt(3)) / 3;
      double inradius = 0.5 * circumradius;

      // Bottom left corner
      p0[0] = -sideLength / 2;
      p0[1] = -inradius;

      // Bottom right corner
      p1[0] = sideLength / 2;
      p1[1] = -inradius;

      // Top corner
      p2[0] = 0;
      p2[1] = circumradius;

      // Add the points to the contour list
      contour.add(p0);
      contour.add(p1);
      contour.add(p2);
    }

    // Calculate the contour of the border polygon
    else {
      calculateBorder(contour, border);
    }
  }

  /**
   * Calculate the coordinates of the triangles border.
   * 
   * @param contour
   *          a reference to the list storing the triangles contour
   * @param border
   *          a reference to the list storing the triangles border
   */
  private void calculateBorder(ArrayList<double[]> contour,
      ArrayList<double[]> border) {

    // Circumradius
    double circumradius = (sideLength * Math.sqrt(3)) / 3;

    // Reduce the circumradius by the border width
    double scaledCircumRadius = circumradius - borderWidth;

    // Make sure the radius never drops below zero
    if (scaledCircumRadius < 0)
      scaledCircumRadius = 0;

    // Scaling factor
    double scale = scaledCircumRadius / circumradius;

    // Scale each vertex of the contour of the whole polygon
    for (double[] vertex : contour) {
      double[] borderVertex = new double[9];
      borderVertex[0] = vertex[0] * scale;
      borderVertex[1] = vertex[1] * scale;
      border.add(borderVertex);
    }
  }

  @Override
  public void generateDisplayList(GL2 gl, GLU glu) {
    /*
     * List of all vertices describing the contour of the triangle, as well as
     * the vertices color and texture coordinates.
     */
    ArrayList<double[]> triangle = null;

    /*
     * List of all vertices describing the contour of the border, as well as the
     * vertices color and texture coordinates.
     */
    ArrayList<double[]> borderTriangle = null;

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
    if (sideLength > 0) {

      // Create a list storing the triangles vertices
      triangle = new ArrayList<double[]>();

      // Check if the triangle will be rendered with a texture applied on it
      if (texture != null) {

        // Calculate the triangles contour
        calculateContour(triangle, null);

        // Set the triangles color to white (because of texture)
        applyColor(triangle, new Color(Color.WHITE));

        // Map the texture to the triangle
        applyTexture(triangle, texture.getImageTexCoords());

        // Enable texture support
        texture.enable();

        // Draw the textured triangle
        TessellatedPolygon poly = new TessellatedPolygon();
        poly.init(gl, glu);
        poly.beginPolygon();
        poly.beginContour();
        poly.renderContour(triangle);
        poly.endContour();
        poly.endPolygon();
        poly.end();
        poly = null;

        // Disable texture support
        texture.disable();
      } else {

        // Check if the triangle will be rendered with a border
        if (borderWidth > 0) {

          // Create a list storing the borders vertices
          borderTriangle = new ArrayList<double[]>(1);

          // Calculate the triangles contour and the contour of the border
          calculateContour(triangle, null);
          calculateContour(triangle, borderTriangle);

          // Apply the border color
          applyColor(triangle, borderColor);
          applyColor(borderTriangle, borderColor);

          // Don't apply any texture
          applyTexture(triangle, null);
          applyTexture(borderTriangle, null);

          // Draw the border
          TessellatedPolygon poly = new TessellatedPolygon();
          poly.init(gl, glu);
          poly.setWindingRule(GLU.GLU_TESS_WINDING_ODD);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(triangle);
          poly.endContour();
          poly.beginContour();
          poly.renderContour(borderTriangle);
          poly.endContour();
          poly.endPolygon();
          poly.end();

          // Apply the fill color
          applyColor(borderTriangle, fillColor);

          // Draw the inner triangle
          poly.init(gl, glu);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(borderTriangle);
          poly.endContour();
          poly.endPolygon();
          poly.end();
          poly = null;
        } else {

          // Calculate the contour of the triangle
          calculateContour(triangle, null);

          // Apply the fill color to the triangle
          applyColor(triangle, fillColor);

          // Don't apply any texture
          applyTexture(triangle, null);

          // Draw the triangle without border
          TessellatedPolygon poly = new TessellatedPolygon();
          poly.init(gl, glu);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(triangle);
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

  // Setter & Getter -----------------------------------------------------------

  public double getSideLength() {
    return sideLength;
  }

  public void setSideLength(double sideLength) {
    this.sideLength = sideLength;
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
