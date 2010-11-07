package aors.module.visopengl3d.test.shapes;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl3d.test.utility.Color;
import aors.module.visopengl3d.test.utility.TessellatedPolygon;

import com.sun.opengl.util.texture.Texture;

/**
 * The RegularPolygon class is representing a simple geometric regular polygon
 * with n edges and a border.
 * 
 * @author Sebastian Mucha
 * @since February 20th, 2010
 */
public class RegularPolygon extends Shape2D {

  // Side length
  private double sideLength;

  // Number of edges
  private int edges;

  // Width of the polygons border
  private double borderWidth;

  // Texture of the polygon
  Texture texture;

  // Color of the polygons border
  private Color borderColor = new Color(Color.WHITE);

  /*
   * The fill opacity refers only to the interior of the shape (i.e. without the
   * border). The object opacity (opacity) has always a higher priority as the
   * fill opacity.
   */
  private double fillOpacity = 1;

  /**
   * Create a new RegularPolygon instance and initialize its dimensions and
   * border width.
   * 
   * @param sideLength
   *          the length of one side of the polygon
   * @param edges
   *          the number of edges of the polygon
   * @param borderWidth
   *          the width of the border
   */
  public RegularPolygon(double sideLength, int edges, double borderWidth) {
    this.sideLength = sideLength;
    this.edges = edges;
    this.borderWidth = borderWidth;
  }

  @Override
  protected void calculateContour(ArrayList<double[]> contour,
      ArrayList<double[]> border) {

    if (border == null) {

      // Point lying on the polygons edge
      double u, v;

      // Circumradius
      double circumradius = sideLength
          / (2 * Math.sin((180.0 / edges) / (180 / Math.PI)));

      // Loop over a circles angles
      for (int i = 0; i < 360; i += (360.0 / edges)) {
        u = Math.cos(i / (180 / Math.PI)) * circumradius;
        v = Math.sin(i / (180 / Math.PI)) * circumradius;

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
   * Calculate the coordinates of the polygons border.
   * 
   * @param contour
   *          a reference to the list storing the polygons contour
   * @param border
   *          a reference to the list storing the polygons border
   */
  private void calculateBorder(ArrayList<double[]> contour,
      ArrayList<double[]> border) {

    // Circumradius
    double circumradius = sideLength
        / (2 * Math.sin((180.0 / edges) / (180 / Math.PI)));

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
     * List of all vertices describing the contour of the polygon, as well as
     * the vertices color and texture coordinates.
     */
    ArrayList<double[]> polygon = null;

    /*
     * List of all vertices describing the contour of the border, as well as the
     * vertices color and texture coordinates.
     */
    ArrayList<double[]> borderPolygon = null;

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
    if (sideLength > 0 && edges > 2) {

      // Create a list storing the polygons vertices
      polygon = new ArrayList<double[]>();

      // Check if the polygon will be rendered with a texture applied on it
      if (texture != null) {

        // Calculate the polygons contour
        calculateContour(polygon, null);

        // Set the polygons color to white (because of texture)
        applyColor(polygon, new Color(Color.WHITE));

        // Map the texture to the polygon
        applyTexture(polygon, texture.getImageTexCoords());

        // Enable texture support
        texture.enable();

        // Draw the textured polygon
        TessellatedPolygon poly = new TessellatedPolygon();
        poly.init(gl, glu);
        poly.beginPolygon();
        poly.beginContour();
        poly.renderContour(polygon);
        poly.endContour();
        poly.endPolygon();
        poly.end();
        poly = null;

        // Disable texture support
        texture.disable();
      } else {

        // Check if the polygon will be rendered with a border
        if (borderWidth > 0) {

          // Create a list storing the borders vertices
          borderPolygon = new ArrayList<double[]>(1);

          // Calculate the polygons contour and the contour of the border
          calculateContour(polygon, null);
          calculateContour(polygon, borderPolygon);

          // Apply the border color
          applyColor(polygon, borderColor);
          applyColor(borderPolygon, borderColor);

          // Don't apply any texture
          applyTexture(polygon, null);
          applyTexture(borderPolygon, null);

          // Draw the border
          TessellatedPolygon poly = new TessellatedPolygon();
          poly.init(gl, glu);
          poly.setWindingRule(GLU.GLU_TESS_WINDING_ODD);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(polygon);
          poly.endContour();
          poly.beginContour();
          poly.renderContour(borderPolygon);
          poly.endContour();
          poly.endPolygon();
          poly.end();

          // Apply the fill color
          applyColor(borderPolygon, fillColor);

          // Draw the inner polygon
          poly.init(gl, glu);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(borderPolygon);
          poly.endContour();
          poly.endPolygon();
          poly.end();
          poly = null;
        } else {

          // Calculate the contour of the polygon
          calculateContour(polygon, null);

          // Apply the fill color to the polygon
          applyColor(polygon, fillColor);

          // Don't apply any texture
          applyTexture(polygon, null);

          // Draw the polygon without border
          TessellatedPolygon poly = new TessellatedPolygon();
          poly.init(gl, glu);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(polygon);
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

  public int getEdges() {
    return edges;
  }

  public void setEdges(int edges) {
    this.edges = edges;
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
