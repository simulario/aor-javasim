package aors.module.visopengl3d.shape;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * The PolyLine class is representing a complex geometric polyline.
 * 
 * @author Sebastian Mucha
 * @since January 19th, 2010
 * 
 */
public class PolyLine extends Shape2D {

  public PolyLine() {
    type = ShapeType.Polyline;
  }

  @Override
  protected void calculateContour(ArrayList<double[]> outContour,
      ArrayList<double[]> inContour) {
    // TODO Auto-generated method stub

  }

  @Override
  public void generateDisplayList(GL2 gl, GLU glu) {
    if (pointList != null) {
      // Don't draw if there are not enough points for a polygon
      if (pointList.size() > 1) {
        // Set opacity
        stroke.setAlpha(strokeOpacity);

        // Get a denominator for the display list
        displayList = gl.glGenLists(1);

        // Create the display list
        gl.glNewList(displayList, GL2.GL_COMPILE);

        // Set drawing color
        gl.glColor4dv(stroke.getColor(), 0);

        gl.glPushAttrib(GL2.GL_LINE_BIT);

        // Set the line width
        gl.glLineWidth((float) strokeWidth);

        gl.glBegin(GL2.GL_LINE_STRIP);
        for (double[] point : pointList) {
          gl.glVertex3d(point[0], point[1], point[2]);
        }
        gl.glEnd();

        gl.glPopAttrib();

        gl.glEndList();
      }
    }
  }
}
