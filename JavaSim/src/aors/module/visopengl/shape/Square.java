package aors.module.visopengl.shape;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl.utility.Color;
import aors.module.visopengl.utility.TessellatedPolygon;

/**
 * The Square class is representing a simple geometric square with a border.
 * 
 * @author Sebastian Mucha
 * @since January 19th, 2010
 * 
 */
public class Square extends Shape2D {

  /**
   * Creates a new rectangle instance and initializes its members.
   */
  public Square() {
    type = ShapeType.Square;
  }

  @Override
  protected void calculateContour(ArrayList<double[]> outContour,
      ArrayList<double[]> inContour) {
    if (inContour == null) {
      /*
       * A squares contour consists of 4 points, starting at the bottom left
       * corner in counterclockwise order.
       */
      double[] p0 = new double[9];
      double[] p1 = new double[9];
      double[] p2 = new double[9];
      double[] p3 = new double[9];

      // Bottom left corner
      p0[0] = -width / 2;
      p0[1] = -width / 2;

      // Bottom right corner
      p1[0] = width / 2;
      p1[1] = p0[1];

      // Top right corner
      p2[0] = p1[0];
      p2[1] = width / 2;

      // Top left corner
      p3[0] = p0[0];
      p3[1] = p2[1];

      // Add the points to the contour list
      outContour.add(p0);
      outContour.add(p1);
      outContour.add(p2);
      outContour.add(p3);
    } else {
      // Calculate the contour of the inner square (border)
      calculateInnerContour(outContour, inContour);
    }
  }

  /**
   * Calculate the vertices of a smaller square (border).
   * 
   * @param outContour
   *          List storing vertices of the shapes contour.
   * @param inContour
   *          List storing vertices of the shapes border.
   */
  private void calculateInnerContour(ArrayList<double[]> outContour,
      ArrayList<double[]> inContour) {
    // Make sure the border width is valid
    if (width - (2 * strokeWidth) < 0)
      strokeWidth = width / 2;

    /*
     * A squares border consists of 4 points, starting at the bottom left corner
     * in counterclockwise order.
     */
    double[] p0 = new double[9];
    double[] p1 = new double[9];
    double[] p2 = new double[9];
    double[] p3 = new double[9];

    // Bottom left corner
    p0[0] = outContour.get(0)[0] + strokeWidth;
    p0[1] = outContour.get(0)[1] + strokeWidth;

    // Bottom right corner
    p1[0] = outContour.get(1)[0] - strokeWidth;
    p1[1] = p0[1];

    // Top right corner
    p2[0] = p1[0];
    p2[1] = outContour.get(2)[1] - strokeWidth;

    // Top left corner
    p3[0] = p0[0];
    p3[1] = p2[1];

    // Add the points to the border list
    inContour.add(p0);
    inContour.add(p1);
    inContour.add(p2);
    inContour.add(p3);
  }

  @Override
  public void generateDisplayList(GL2 gl, GLU glu) {
    /*
     * List of all vertices describing the outer contour of the square, as well
     * as the vertices color and texture coordinates.
     */
    ArrayList<double[]> outContour = new ArrayList<double[]>();

    /*
     * List of all vertices describing the inner contour of the square, as well
     * as the vertices color and texture coordinates.
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

    // Don't draw anything if the dimensions are too small
    if (width > 0) {
      // Check if the square will be rendered with a texture applied on it
      if (texture != null) {
        // Calculate the squares contour
        calculateContour(outContour, null);

        // Set the squares color to white (because of texture)
        applyColor(outContour, Color.WHITE);

        // Map the texture to the square
        applyTexture(outContour, texture.getImageTexCoords());

        // Enable texture support
        texture.bind();
        texture.enable();

        // Draw the textured square
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
        // Check if the square will be rendered with a border
        if (strokeWidth > 0) {
          // Calculate the squares inner and outer contour
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

          // Draw the interior
          poly.init(gl, glu);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(inContour);
          poly.endContour();
          poly.endPolygon();
          poly.end();
        } else {
          // Calculate the contour of the square
          calculateContour(outContour, null);

          // Apply the fill color to the square
          applyColor(outContour, fill);

          // Don't apply any texture
          applyTexture(outContour, null);

          // Draw the square without border
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
