package aors.module.visopengl.shape;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl.test.utility.TessellatedPolygon;
import aors.module.visopengl.utility.Color;

/**
 * The Rectangle class is representing a simple geometric rectangle with a
 * border.
 * 
 * @author Sebastian Mucha
 * @since January 19th, 2010
 * 
 */
public class Rectangle extends Shape2D {

  /**
   * Creates a new rectangle instance and initializes its members.
   */
  public Rectangle() {
    type = ShapeType.Rectangle;
  }

  @Override
  protected void calculateContour(ArrayList<double[]> outContour,
      ArrayList<double[]> inContour) {
    if (inContour == null) {
      /*
       * A rectangles contour consists of 4 points, starting at the bottom left
       * corner in counterclockwise order.
       */
      double[] p0 = new double[9];
      double[] p1 = new double[9];
      double[] p2 = new double[9];
      double[] p3 = new double[9];

      if (positioning.equals(Positioning.CenterCenter)) {
        // Bottom left corner
        p0[0] = -width / 2;
        p0[1] = -height / 2;

        // Bottom right corner
        p1[0] = width / 2;

        // Top right corner
        p2[1] = height / 2;
      }

      else if (positioning.equals(Positioning.CenterBottom)) {
        // Bottom left corner
        p0[0] = -width / 2;
        p0[1] = 0;

        // Bottom right corner
        p1[0] = width / 2;

        // Top right corner
        p2[1] = height;
      }

      else if (positioning.equals(Positioning.CenterTop)) {
        // Bottom left corner
        p0[0] = -width / 2;
        p0[1] = -height;

        // Bottom right corner
        p1[0] = width / 2;

        // Top right corner
        p2[1] = 0;
      }

      else if (positioning.equals(Positioning.LeftCenter)) {
        // Bottom left corner
        p0[0] = 0;
        p0[1] = -height / 2;

        // Bottom right corner
        p1[0] = width;

        // Top right corner
        p2[1] = height / 2;
      }

      else if (positioning.equals(Positioning.LeftBottom)) {
        // Bottom left corner
        p0[0] = 0;
        p0[1] = 0;

        // Bottom right corner
        p1[0] = width;

        // Top right corner
        p2[1] = height;
      }

      else if (positioning.equals(Positioning.LeftTop)) {
        // Bottom left corner
        p0[0] = 0;
        p0[1] = 0;

        // Bottom right corner
        p1[0] = width;

        // Top right corner
        p2[1] = -height;
      }

      else if (positioning.equals(Positioning.RightCenter)) {
        // Bottom left corner
        p0[0] = -width;
        p0[1] = -height / 2;

        // Bottom right corner
        p1[0] = 0;

        // Top right corner
        p2[1] = height / 2;
      }

      else if (positioning.equals(Positioning.RightBottom)) {
        // Bottom left corner
        p0[0] = -width;
        p0[1] = 0;

        // Bottom right corner
        p1[0] = 0;

        // Top right corner
        p2[1] = height;
      }

      else if (positioning.equals(Positioning.RightTop)) {
        // Bottom left corner
        p0[0] = -width;
        p0[1] = -height;

        // Bottom right corner
        p1[0] = 0;

        // Top right corner
        p2[1] = 0;
      }

      // Bottom right corner
      p1[1] = p0[1];

      // Top right corner
      p2[0] = p1[0];

      // Top left corner
      p3[0] = p0[0];
      p3[1] = p2[1];

      // Add the points to the contour list
      outContour.add(p0);
      outContour.add(p1);
      outContour.add(p2);
      outContour.add(p3);
    } else {
      // Calculate the contour of the inner rectangle (border)
      calculateInnerContour(outContour, inContour);
    }
  }

  /**
   * Calculate the vertices of a smaller rectangle (border).
   * 
   * @param outContour
   * @param inContour
   */
  private void calculateInnerContour(ArrayList<double[]> outContour,
      ArrayList<double[]> inContour) {
    // Make sure the border width is valid
    if (width - (2 * strokeWidth) < 0)
      strokeWidth = width / 2;

    if (height - (2 * strokeWidth) < 0)
      strokeWidth = height / 2;

    /*
     * A rectangles border consists of 4 points, starting at the bottom left
     * corner in counterclockwise order.
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
     * List of all vertices describing the outer contour of the rectangle, as
     * well as the vertices color and texture coordinates.
     */
    ArrayList<double[]> outContour = new ArrayList<double[]>();

    /*
     * List of all vertices describing the inner contour of the rectangle, as
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

    // Don't draw anything if the dimensions are too small
    if (width > 0 && height > 0) {
      // Check if the rectangle will be rendered with a texture applied on it
      if (texture != null) {
        // Calculate the rectangles contour
        calculateContour(outContour, null);

        // Set the rectangles color to white (because of texture)
        applyColor(outContour, Color.WHITE);

        // Map the texture to the rectangle
        applyTexture(outContour, texture.getImageTexCoords());

        // Enable texture support
        texture.bind();
        texture.enable();

        // Draw the textured rectangle
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
        // Check if the rectangle will be rendered with a border
        if (strokeWidth > 0) {
          // Calculate the rectangles inner and outer contour
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
          // Calculate the contour of the rectangle
          calculateContour(outContour, null);

          // Apply the fill color to the rectangle
          applyColor(outContour, fill);

          // Don't apply any texture
          applyTexture(outContour, null);

          // Draw the rectangle without border
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
