package aors.module.visopengl.shape;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl.utility.Color;
import aors.module.visopengl.utility.TessellatedPolygon;

/**
 * The Circle class is representing a simple geometric circle with a border.
 * 
 * @author Sebastian Mucha
 * @since January 19th, 2010
 * 
 */
public class Circle extends Shape2D {

  /**
   * Creates a new rectangle instance and initializes its members.
   */
  public Circle() {
    type = ShapeType.Circle;
  }

  @Override
  protected void calculateContour(ArrayList<double[]> outContour,
      ArrayList<double[]> inContour) {
    if (inContour == null) {
      // Radius of the circle
      double radius = width / 2;

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
        outContour.add(vertex);
      }
    }

    // Calculate the contour of the border polygon
    else {
      calculateInnerContour(outContour, inContour);
    }
  }

  /**
   * Calculate the vertices of a smaller circle (border).
   * 
   * @param outContour
   * @param inContour
   */
  private void calculateInnerContour(ArrayList<double[]> outContour,
      ArrayList<double[]> inContour) {
    // Radius of the circle
    double radius = width / 2;

    // Reduce the radius by the border width
    double scaledRadius = radius - strokeWidth;

    // Make sure the radius never drops below zero
    if (scaledRadius < 0)
      scaledRadius = 0;

    // Scaling factor
    double scale = scaledRadius / radius;

    // Scale each vertex of the contour of the whole circle
    for (double[] vertex : outContour) {
      double[] borderVertex = new double[9];
      borderVertex[0] = vertex[0] * scale;
      borderVertex[1] = vertex[1] * scale;
      inContour.add(borderVertex);
    }
  }

  @Override
  public void generateDisplayList(GL2 gl, GLU glu) {
    double radius = width / 2;

    /*
     * List of all vertices describing the outer contour of the circle, as well
     * as the vertices color and texture coordinates.
     */
    ArrayList<double[]> outContour = new ArrayList<double[]>();

    /*
     * List of all vertices describing the inner contour of the circle, as well
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
    if (radius > 0) {
      // Check if the circle will be rendered with a texture applied on it
      if (texture != null) {

        // Calculate the circles contour
        calculateContour(outContour, null);

        // Set the circles color to white (because of texture)
        applyColor(outContour, Color.WHITE);

        // Map the texture to the circle
        applyTexture(outContour, texture.getImageTexCoords());

        // Enable texture support
        texture.bind();
        texture.enable();

        // Draw the textured circle
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

        // Check if the circle will be rendered with a border
        if (strokeWidth > 0) {
          // Calculate the circles contour and the contour of the border
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

          // Draw the inner circle
          poly.init(gl, glu);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(inContour);
          poly.endContour();
          poly.endPolygon();
          poly.end();
        } else {
          // Calculate the contour of the circle
          calculateContour(outContour, null);

          // Apply the fill color to the circle
          applyColor(outContour, fill);

          // Don't apply any texture
          applyTexture(outContour, null);

          // Draw the circle without border
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
