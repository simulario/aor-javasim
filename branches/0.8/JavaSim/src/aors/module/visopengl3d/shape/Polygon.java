package aors.module.visopengl3d.shape;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl3d.test.utility.TessellatedPolygon;
import aors.module.visopengl3d.utility.Color;

/**
 * The Polygon class is representing a complex geometric polygon.
 * 
 * @author Sebastian Mucha
 * @since January 19th, 2010
 * 
 */
public class Polygon extends Shape2D {

  public Polygon() {
    type = ShapeType.Polygon;
  }

  @Override
  protected void calculateContour(ArrayList<double[]> outContour,
      ArrayList<double[]> inContour) {
    if (inContour == null) {
      for (double[] point : pointList) {
        double[] vertex = new double[9];

        vertex[0] = point[0];
        vertex[1] = point[1];
        vertex[2] = point[2];

        outContour.add(vertex);
      }
    }
  }

  @Override
  public void generateDisplayList(GL2 gl, GLU glu) {
    /*
     * List of all vertices describing the outer contour of the circle, as well
     * as the vertices color and texture coordinates.
     */
    ArrayList<double[]> outContour = new ArrayList<double[]>();

    /*
     * Set the alpha value of the colors. The opacity of the whole shape
     * (strokeOpacity) always has a higher priority.
     */
    fill.setAlpha(fillOpacity);

    // Get a denominator for the display list
    displayList = gl.glGenLists(1);

    // Create the display list
    gl.glNewList(displayList, GL2.GL_COMPILE);

    if (pointList != null) {
      // Don't draw if there are not enough points for a polygon
      if (pointList.size() > 2) {
        // Check if the polygon will be rendered with a texture applied on it
        if (texture != null) {

          // Calculate the polygons contour
          calculateContour(outContour, null);

          // Set the polygons color to white (because of texture)
          applyColor(outContour, Color.WHITE);

          // Map the texture to the polygon
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
          // Calculate the contour of the polygon
          calculateContour(outContour, null);

          // Apply the fill color to the polygon
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
