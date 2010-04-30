package aors.module.visopengl.shape;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl.test.utility.TessellatedPolygon;
import aors.module.visopengl.utility.Color;

/**
 * The Ellipse class is representing a simple geometric ellipse with a border.
 * 
 * @author Sebastian Mucha
 * @since January 19th, 2010
 * 
 */
public class Ellipse extends Shape2D {

  public Ellipse() {
    type = ShapeType.Ellipse;
  }

  @Override
  protected void calculateContour(ArrayList<double[]> outContour,
      ArrayList<double[]> inContour) {
    if (inContour == null) {
      // Point lying on the ellipses edge
      double u, v;

      // Radii
      double bigRadius = width / 2;
      double smallRadius = height / 2;

      for (int i = 0; i < 360; i++) {
        u = Math.cos(i / (180 / Math.PI)) * bigRadius;
        v = Math.sin(i / (180 / Math.PI)) * smallRadius;

        // Create one vertex
        double[] vertex = new double[9];
        vertex[0] = u;
        vertex[1] = v;

        // Add the vertex to the contour list
        outContour.add(vertex);
      }
    }

    // Calculate the contour of the border polygon
    else {
      calculateInnerContour(outContour, inContour);
    }
  }

  /**
   * Calculate the vertices of a smaller ellipse (border).
   * 
   * @param outContour
   * @param inContour
   */
  private void calculateInnerContour(ArrayList<double[]> outContour,
      ArrayList<double[]> inContour) {
    // Radii
    double bigRadius = width / 2;
    double smallRadius = height / 2;

    // Reduce the radii by the border width
    double scaledBigRadius = bigRadius - strokeWidth;
    double scaledSmallRadius = smallRadius - strokeWidth;

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
      inContour.add(borderVertex);
    }
  }

  @Override
  public void generateDisplayList(GL2 gl, GLU glu) {
    // Radii
    double bigRadius = width / 2;
    double smallRadius = height / 2;

    /*
     * List of all vertices describing the outer contour of the ellipse, as well
     * as the vertices color and texture coordinates.
     */
    ArrayList<double[]> outContour = new ArrayList<double[]>();

    /*
     * List of all vertices describing the inner contour of the ellipse, as well
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

    // Don't draw if the dimensions are too small
    if (bigRadius > 0 && smallRadius > 0) {
      // Check if the ellipse will be rendered with a texture applied on it
      if (texture != null) {

        // Calculate the ellipses contour
        calculateContour(outContour, null);

        // Set the ellipses color to white (because of texture)
        applyColor(outContour, Color.WHITE);

        // Map the texture to the ellipse
        applyTexture(outContour, texture.getImageTexCoords());

        // Enable texture support
        texture.enable();

        // Draw the textured ellipse
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

        // Check if the ellipse will be rendered with a border
        if (strokeWidth > 0) {
          // Calculate the ellipses contour and the contour of the border
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

          // Draw the inner ellipse
          poly.init(gl, glu);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(inContour);
          poly.endContour();
          poly.endPolygon();
          poly.end();
        } else {
          // Calculate the contour of the ellipse
          calculateContour(outContour, null);

          // Apply the fill color to the ellipse
          applyColor(outContour, fill);

          // Don't apply any texture
          applyTexture(outContour, null);

          // Draw the ellipse without border
          TessellatedPolygon poly = new TessellatedPolygon();
          poly.init(gl, glu);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(outContour);
          poly.endContour();
          poly.endPolygon();
          poly.end();
          poly = null;
        }
      }
    }

    gl.glEndList();
  }
}
