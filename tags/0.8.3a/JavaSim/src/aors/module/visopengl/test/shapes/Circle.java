package aors.module.visopengl.test.shapes;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl.test.utility.Color;
import aors.module.visopengl.test.utility.TessellatedPolygon;

import com.sun.opengl.util.texture.Texture;

/**
 * The Circle class is representing a simple geometric circle with a border.
 * 
 * @author Sebastian Mucha
 * @since February 19th, 2010
 */
public class Circle extends Shape2D {

  // Radius
  private double radius;

  // Width of the circles border
  private double borderWidth;

  // Texture of the circle
  Texture texture;

  // Color of the circles border
  private Color borderColor = new Color(Color.WHITE);

  /*
   * The fill opacity refers only to the interior of the shape (i.e. without the
   * border). The object opacity (opacity) has always a higher priority as the
   * fill opacity.
   */
  private double fillOpacity = 1;

  /**
   * Create a new Circle instance and initialize its dimensions and border
   * width.
   * 
   * @param radius
   *          the radius of the circle
   * @param borderWidth
   *          the width of the border
   */
  public Circle(double radius, double borderWidth) {
    this.radius = radius;
    this.borderWidth = borderWidth;
  }

  @Override
  protected void calculateContour(ArrayList<double[]> contour,
      ArrayList<double[]> border) {

    if (border == null) {
      // Point lying on the circles edge
      double u, v;

      // Loop over a circles angles
      for (int i = 0; i < 360; i++) {
        u = Math.cos(i / (180 / Math.PI)) * radius;
        v = Math.sin(i / (180 / Math.PI)) * radius;

        // Create one vertex
        double[] vertex = new double[9];
        vertex[0] = u;
        vertex[1] = v;

        // Add the vertex to the contour list
        contour.add(vertex);
      }
    }

    // Calculate the contour of the border polygon
    else {
      calculateBorder(contour, border);
    }
  }

  /**
   * Calculate the coordinates of the circles border.
   * 
   * @param contour
   *          a reference to the list storing the circles contour
   * @param border
   *          a reference to the list storing the circles border
   */
  private void calculateBorder(ArrayList<double[]> contour,
      ArrayList<double[]> border) {

    // Reduce the radius by the border width
    double scaledRadius = radius - borderWidth;

    // Make sure the radius never drops below zero
    if (scaledRadius < 0)
      scaledRadius = 0;

    // Scaling factor
    double scale = scaledRadius / radius;

    // Scale each vertex of the contour of the whole circle
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
     * List of all vertices describing the contour of the circle, as well as the
     * vertices color and texture coordinates.
     */
    ArrayList<double[]> circle = null;

    /*
     * List of all vertices describing the contour of the border, as well as the
     * vertices color and texture coordinates.
     */
    ArrayList<double[]> borderCircle = null;

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
    if (radius > 0) {

      // Create a list storing the circles vertices
      circle = new ArrayList<double[]>();

      // Check if the circle will be rendered with a texture applied on it
      if (texture != null) {

        // Calculate the circles contour
        calculateContour(circle, null);

        // Set the circles color to white (because of texture)
        applyColor(circle, new Color(Color.WHITE));

        // Map the texture to the circle
        applyTexture(circle, texture.getImageTexCoords());

        // Enable texture support
        texture.enable();

        // Draw the textured circle
        TessellatedPolygon poly = new TessellatedPolygon();
        poly.init(gl, glu);
        poly.beginPolygon();
        poly.beginContour();
        poly.renderContour(circle);
        poly.endContour();
        poly.endPolygon();
        poly.end();
        poly = null;

        // Disable texture support
        texture.disable();
      } else {

        // Check if the circle will be rendered with a border
        if (borderWidth > 0) {

          // Create a list storing the borders vertices
          borderCircle = new ArrayList<double[]>(1);

          // Calculate the circles contour and the contour of the border
          calculateContour(circle, null);
          calculateContour(circle, borderCircle);

          // Apply the border color
          applyColor(circle, borderColor);
          applyColor(borderCircle, borderColor);

          // Don't apply any texture
          applyTexture(circle, null);
          applyTexture(borderCircle, null);

          // Draw the border
          TessellatedPolygon poly = new TessellatedPolygon();
          poly.init(gl, glu);
          poly.setWindingRule(GLU.GLU_TESS_WINDING_ODD);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(circle);
          poly.endContour();
          poly.beginContour();
          poly.renderContour(borderCircle);
          poly.endContour();
          poly.endPolygon();
          poly.end();

          // Apply the fill color
          applyColor(borderCircle, fillColor);

          // Draw the inner circle
          poly.init(gl, glu);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(borderCircle);
          poly.endContour();
          poly.endPolygon();
          poly.end();
          poly = null;
        } else {

          // Calculate the contour of the circle
          calculateContour(circle, null);

          // Apply the fill color to the circle
          applyColor(circle, fillColor);

          // Don't apply any texture
          applyTexture(circle, null);

          // Draw the circle without border
          TessellatedPolygon poly = new TessellatedPolygon();
          poly.init(gl, glu);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(circle);
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

  public double getRadius() {
    return radius;
  }

  public void setRadius(double radius) {
    this.radius = radius;
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
