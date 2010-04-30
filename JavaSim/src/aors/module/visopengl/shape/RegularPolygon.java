package aors.module.visopengl.shape;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl.test.utility.TessellatedPolygon;
import aors.module.visopengl.utility.Color;

/**
 * The RegularPolygon class is representing a simple geometric regular polygon
 * with a border.
 * 
 * @author Sebastian Mucha
 * @since January 19th, 2010
 * 
 */
public class RegularPolygon extends Shape2D {

  public RegularPolygon() {
    type = ShapeType.RegularPolygon;
  }

  @Override
  protected void calculateContour(ArrayList<double[]> outContour,
      ArrayList<double[]> inContour) {
    if (inContour == null) {
      // Point lying on the polygons edge
      double u, v;

      // Side length
      double sideLength = width;

      if (numberOfPoints < 3) {
        System.out
            .println("Visualization Warning: A regular polygon has at least 3 points!");
        numberOfPoints = 3;
      }

      // Circumradius
      double circumradius = sideLength
          / (2 * Math.sin((180.0 / numberOfPoints) / (180 / Math.PI)));

      // Loop over a circles angles
      for (int i = 0; i < 360; i += (360.0 / numberOfPoints)) {
        u = Math.cos(i / (180 / Math.PI)) * circumradius;
        v = Math.sin(i / (180 / Math.PI)) * circumradius;

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
    // Side length
    double sideLength = width;

    // Circumradius
    double circumradius = sideLength
        / (2 * Math.sin((180.0 / numberOfPoints) / (180 / Math.PI)));

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
    // Side length
    double sideLength = width;

    /*
     * List of all vertices describing the outer contour of the polygon, as well
     * as the vertices color and texture coordinates.
     */
    ArrayList<double[]> outContour = new ArrayList<double[]>();

    /*
     * List of all vertices describing the inner contour of the polygon, as well
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
    if (sideLength > 0 && numberOfPoints >= 3) {
      // Check if the circle will be rendered with a texture applied on it
      if (texture != null) {

        // Calculate the polygons contour
        calculateContour(outContour, null);

        // Set the polygons color to white (because of texture)
        applyColor(outContour, Color.WHITE);

        // Map the texture to the polygon
        applyTexture(outContour, texture.getImageTexCoords());

        // Enable texture support
        texture.enable();

        // Draw the textured polygon
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

        // Check if the polygon will be rendered with a border
        if (strokeWidth > 0) {
          // Calculate the polygons contour and the contour of the border
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

          // Draw the inner polygon
          poly.init(gl, glu);
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(inContour);
          poly.endContour();
          poly.endPolygon();
          poly.end();
        } else {
          // Calculate the contour of the polygon
          calculateContour(outContour, null);

          // Apply the fill color to the polygon
          applyColor(outContour, fill);

          // Don't apply any texture
          applyTexture(outContour, null);

          // Draw the polygon without border
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
