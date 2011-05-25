package aors.module.visopengl.utility;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;

public class TessellatedPolygon {

  // Tessellation callbacks
  TesselationCallback tessCallback;

  // Tesselation object
  GLUtessellator tobj;

  /**
   * Initialize the tessellation process by registering the required
   * tessellation callbacks to the tessellation object.
   * 
   * @param gl
   *          the GL pipeline object
   * @param glu
   *          the GL utility library object
   */
  public void init(GL2 gl, GLU glu) {

    tessCallback = new TesselationCallback(gl, glu);
    tobj = GLU.gluNewTess();

    // Register tessellation callbacks
    GLU.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);
    GLU.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);
    GLU.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);
    GLU.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);
  }

  /**
   * Sets the winding rule used for the tessellation process. By using this
   * method it is possible to draw polygons with holes inside.
   * 
   * @param windingRule
   *          the winding rule that should be used
   */
  public void setWindingRule(double windingRule) {

    GLU.gluTessProperty(tobj, GLU.GLU_TESS_WINDING_RULE, windingRule);
  }

  /**
   * Starts the tessellation process and will draw the tessellated polygon to
   * the screen.
   * 
   * @param objData
   */
  public void renderContour(ArrayList<double[]> vertexList) {
    for (double[] vertex : vertexList) {
      GLU.gluTessVertex(tobj, vertex, 0, vertex);
    }
  }

  /**
   * Delimits the definition of a convex, concave or self intersecting polygon.
   */
  public void beginPolygon() {

    GLU.gluTessBeginPolygon(tobj, null);
  }

  /**
   * Delimits the definition of a convex, concave or self intersecting polygon.
   */
  public void endPolygon() {

    GLU.gluTessEndPolygon(tobj);
  }

  /**
   * Delimits the definition of a polygon contour.
   */
  public void beginContour() {

    GLU.gluTessBeginContour(tobj);
  }

  /**
   * Delimits the definition of a polygon contour.
   */
  public void endContour() {

    GLU.gluTessEndContour(tobj);
  }

  /**
   * Destroys the tessellation object.
   */
  public void end() {
    GLU.gluDeleteTess(tobj);
    tessCallback = null;
  }

}
