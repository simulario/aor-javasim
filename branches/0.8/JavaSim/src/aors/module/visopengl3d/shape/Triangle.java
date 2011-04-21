package aors.module.visopengl3d.shape;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl3d.test.utility.TessellatedPolygon;
import aors.module.visopengl3d.utility.Color;

/**
 * The Triangle class is representing a simple geometric triangle with a border.
 * 
 * @author Sebastian Mucha
 * @since January 19th, 2010
 * 
 */
public class Triangle extends Shape2D {

  /**
   * Creates a new Triangle instance and initializes its members.
   */
  public Triangle() {
    type = ShapeType.Triangle;
  }

  @Override
  protected void calculateContour(ArrayList<double[]> outContour,
      ArrayList<double[]> inContour) {

    if (inContour == null) {
      /*
       * A triangles contour consists of 3 points, starting at the bottom left
       * corner in counterclockwise order.
       */
      double[] p0 = new double[9];
      double[] p1 = new double[9];
      double[] p2 = new double[9];

      // Side length of a and b
      double sidelength = Math.sqrt(Math.pow(width / 2, 2)
          + Math.pow(height, 2));

      // Area
      double area = 0.5 * width * height;

      // Radii of the triangle
      double inradius = (2 * area) / (sidelength + sidelength + width);

      // Bottom left corner
      p0[0] = -width / 2;
      p0[1] = -inradius;

      // Bottom right corner
      p1[0] = width / 2;
      p1[1] = -inradius;

      // Top corner
      p2[0] = 0;
      p2[1] = height - inradius;

      // Add the points to the contour list
      outContour.add(p0);
      outContour.add(p1);
      outContour.add(p2);
    }

    // Calculate the contour of the border polygon
    else {
      calculateInnerContour(outContour, inContour);
    }
  }

  /**
   * Calculate the vertices of a smaller triangle (border).
   * 
   * @param outContour
   *          List storing vertices of the shapes contour.
   * @param inContour
   *          List storing vertices of the shapes border.
   */
  private void calculateInnerContour(ArrayList<double[]> outContour,
      ArrayList<double[]> inContour) {
    // Side length of a and b
    double sidelength = Math.sqrt(Math.pow(width / 2, 2) + Math.pow(height, 2));

    // Area
    double area = 0.5 * width * height;

    // Radii of the triangle
    double circumradius = (sidelength * sidelength * width) / (4 * area);

    // Reduce the circumradius by the border width
    double scaledCircumRadius = circumradius - strokeWidth;

    // Make sure the radius never drops below zero
    if (scaledCircumRadius < 0)
      scaledCircumRadius = 0;

    // Scaling factor
    double scale = scaledCircumRadius / circumradius;

    // Scale each vertex of the contour of the whole polygon
    for (double[] vertex : outContour) {
      double[] borderVertex = new double[9];
      borderVertex[0] = vertex[0] * scale;
      borderVertex[1] = vertex[1] * scale;
      inContour.add(borderVertex);
    }
  }

  @Override
  public void generateDisplayList(GL2 gl, GLU glu) {
    /*
     * List of all vertices describing the outer contour of the triangle, as
     * well as the vertices color and texture coordinates.
     */
    ArrayList<double[]> outContour = new ArrayList<double[]>();

    /*
     * List of all vertices describing the inner contour of the triangle, as
     * well as the vertices color and texture coordinates.
     */
    ArrayList<double[]> inContour = new ArrayList<double[]>();

    /*
     * Set the alpha value of the colors. The opacity of the whole shape
     * (strokeOpacity) always has a higher priority.
     */
    if (strokeOpacity < 1) {
      fill.setAlpha(strokeOpacity);
      stroke.setAlpha(strokeOpacity);
    } else {
      fill.setAlpha(fillOpacity);
      stroke.setAlpha(1);
    }

    // Get a denominator for the display list
    displayList = gl.glGenLists(1);

    // Create the display list
    gl.glNewList(displayList, GL2.GL_COMPILE);
    // Don't draw if the dimensions are too small
    if (width > 0) {
      // Check if the triangle will be rendered with a texture applied on it
      if (texture != null) {
        // Calculate the triangles contour
        calculateContour(outContour, null);

        // Set the triangles color to white (because of texture)
        applyColor(outContour, Color.WHITE);

        // Map the texture to the triangle
        applyTexture(outContour, texture.getImageTexCoords());

        // Enable texture support
        texture.bind();
        texture.enable();

        // Draw the textured triangle
        TessellatedPolygon poly = new TessellatedPolygon();
        poly.init(gl, glu);
        poly.beginPolygon();
        poly.beginContour();
        poly.renderContour(outContour);
        poly.endContour();
        poly.endPolygon();
        poly.end();

        // Disable texture support
        texture.disable();
      } else {

        // Check if the triangle will be rendered with a border
        if (strokeWidth > 0) {
          // Calculate the triangles contour and the contour of the border
          calculateContour(outContour, null);
          calculateContour(outContour, inContour);

          // Apply the border color
          applyColor(outContour, stroke);
          applyColor(inContour, stroke);

          // Don't apply any texture
          applyTexture(outContour, null);
          applyTexture(inContour, null);

          // Draw the border
          TessellatedPolygon poly = new TessellatedPolygon();
          poly.init(gl, glu);
          poly.setWindingRule(GLU.GLU_TESS_WINDING_ODD);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(outContour);
          poly.endContour();
          poly.beginContour();
          poly.renderContour(inContour);
          poly.endContour();
          poly.endPolygon();
          poly.end();

          // Apply the fill color
          applyColor(inContour, fill);

          // Draw the inner triangle
          poly.init(gl, glu);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(inContour);
          poly.endContour();
          poly.endPolygon();
          poly.end();
        } else {

          // Calculate the contour of the triangle
          calculateContour(outContour, null);

          // Apply the fill color to the triangle
          applyColor(outContour, fill);

          // Don't apply any texture
          applyTexture(outContour, null);

          // Draw the triangle without border
          TessellatedPolygon poly = new TessellatedPolygon();
          poly.init(gl, glu);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(outContour);
          poly.endContour();
          poly.endPolygon();
          poly.end();
        }
      }
    }

    gl.glEndList();
  }
}
