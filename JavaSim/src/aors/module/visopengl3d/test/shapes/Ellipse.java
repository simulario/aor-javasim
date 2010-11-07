package aors.module.visopengl3d.test.shapes;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl3d.test.utility.Color;
import aors.module.visopengl3d.test.utility.TessellatedPolygon;

import com.sun.opengl.util.texture.Texture;

/**
 * The Ellipse class is representing a simple geometric ellipse with a border.
 * 
 * @author Sebastian Mucha
 * @since February 19th, 2010
 */
public class Ellipse extends Shape2D {

  // Big radius
  private double bigRadius;

  // Small radius
  private double smallRadius;

  // Width of the ellipses border
  private double borderWidth;

  // Texture of the ellipse
  Texture texture;

  // Color of the ellipses border
  private Color borderColor = new Color(Color.WHITE);

  /*
   * The fill opacity refers only to the interior of the shape (i.e. without the
   * border). The object opacity (opacity) has always a higher priority as the
   * fill opacity.
   */
  private double fillOpacity = 1;

  /**
   * Create a new Ellipse instance and initialize its dimensions and border
   * width.
   * 
   * @param bigRadius
   *          the big radius of the ellipse
   * @param smallRadius
   *          the small radius of the ellipse
   * @param borderWidth
   *          the width of the border
   */
  public Ellipse(double bigRadius, double smallRadius, double borderWidth) {
    this.bigRadius = bigRadius;
    this.smallRadius = smallRadius;
    this.borderWidth = borderWidth;
  }

  @Override
  protected void calculateContour(ArrayList<double[]> contour,
      ArrayList<double[]> border) {

    if (border == null) {
      // Point lying on the ellipses edge
      double u, v;

      for (int i = 0; i < 360; i++) {
        u = Math.cos(i / (180 / Math.PI)) * bigRadius;
        v = Math.sin(i / (180 / Math.PI)) * smallRadius;

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
   * Calculate the coordinates of the ellipses border.
   * 
   * @param contour
   *          a reference to the list storing the ellipses contour
   * @param border
   *          a reference to the list storing the ellipses border
   */
  private void calculateBorder(ArrayList<double[]> contour,
      ArrayList<double[]> border) {

    // Reduce the radii by the border width
    double scaledBigRadius = bigRadius - borderWidth;
    double scaledSmallRadius = smallRadius - borderWidth;

    if (scaledBigRadius < 0)
      scaledBigRadius = bigRadius;

    if (scaledSmallRadius < 0)
      scaledSmallRadius = smallRadius;

    // Point lying on the ellipses edge
    double u, v;

    for (int i = 0; i < 360; i++) {
      u = Math.cos(i / (180 / Math.PI)) * scaledBigRadius;
      v = Math.sin(i / (180 / Math.PI)) * scaledSmallRadius;

      // Create one vertex
      double[] borderVertex = new double[9];
      borderVertex[0] = u;
      borderVertex[1] = v;

      // Add the vertex to the border list
      border.add(borderVertex);
    }
  }

  @Override
  public void generateDisplayList(GL2 gl, GLU glu) {
    /*
     * List of all vertices describing the contour of the ellipse, as well as
     * the vertices color and texture coordinates.
     */
    ArrayList<double[]> ellipse = null;

    /*
     * List of all vertices describing the contour of the border, as well as the
     * vertices color and texture coordinates.
     */
    ArrayList<double[]> borderEllipse = null;

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
    if (bigRadius > 0 && smallRadius > 0) {

      // Create a list storing the ellipses vertices
      ellipse = new ArrayList<double[]>();

      // Check if the ellipse will be rendered with a texture applied on it
      if (texture != null) {

        // Calculate the ellipses contour
        calculateContour(ellipse, null);

        // Set the ellipses color to white (because of texture)
        applyColor(ellipse, new Color(Color.WHITE));

        // Map the texture to the ellipse
        applyTexture(ellipse, texture.getImageTexCoords());

        // Enable texture support
        texture.enable();

        // Draw the textured ellipse
        TessellatedPolygon poly = new TessellatedPolygon();
        poly.init(gl, glu);
        poly.beginPolygon();
        poly.beginContour();
        poly.renderContour(ellipse);
        poly.endContour();
        poly.endPolygon();
        poly.end();
        poly = null;

        // Disable texture support
        texture.disable();
      } else {

        // Check if the ellipse will be rendered with a border
        if (borderWidth > 0) {

          // Create a list storing the borders vertices
          borderEllipse = new ArrayList<double[]>(1);

          // Calculate the ellipses contour and the contour of the border
          calculateContour(ellipse, null);
          calculateContour(ellipse, borderEllipse);

          // Apply the border color
          applyColor(ellipse, borderColor);
          applyColor(borderEllipse, borderColor);

          // Don't apply any texture
          applyTexture(ellipse, null);
          applyTexture(borderEllipse, null);

          // Draw the border
          TessellatedPolygon poly = new TessellatedPolygon();
          poly.init(gl, glu);
          poly.setWindingRule(GLU.GLU_TESS_WINDING_ODD);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(ellipse);
          poly.endContour();
          poly.beginContour();
          poly.renderContour(borderEllipse);
          poly.endContour();
          poly.endPolygon();
          poly.end();

          // Apply the fill color
          applyColor(borderEllipse, fillColor);

          // Draw the inner ellipse
          poly.init(gl, glu);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(borderEllipse);
          poly.endContour();
          poly.endPolygon();
          poly.end();
          poly = null;
        } else {

          // Calculate the contour of the ellipse
          calculateContour(ellipse, null);

          // Apply the fill color to the ellipse
          applyColor(ellipse, fillColor);

          // Don't apply any texture
          applyTexture(ellipse, null);

          // Draw the ellipse without border
          TessellatedPolygon poly = new TessellatedPolygon();
          poly.init(gl, glu);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(ellipse);
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

  public double getBigRadius() {
    return bigRadius;
  }

  public void setBigRadius(double bigRadius) {
    this.bigRadius = bigRadius;
  }

  public double getSmallRadius() {
    return smallRadius;
  }

  public void setSmallRadius(double smallRadius) {
    this.smallRadius = smallRadius;
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
