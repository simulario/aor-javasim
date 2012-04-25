package aors.module.visopengl3d.shape;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.texture.TextureCoords;

import aors.module.visopengl3d.engine.TessellatedPolygon;
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

  protected void calculateTopContour(ArrayList<double[]> outContourTop, boolean clockwise) {
    if(clockwise) {
      for (int i=pointList.size()-1; i>=0; i--) {
        double[] point = pointList.get(i);
        
        double[] vertex = new double[12];

        vertex[0] = point[0];
        vertex[1] = getObjectHeight()/2;
        vertex[2] = -point[1];

        outContourTop.add(vertex);
      }
    } else {
      for (double[] point : pointList) {
        double[] vertex = new double[12];

        vertex[0] = point[0];
        vertex[1] = getObjectHeight()/2;
        vertex[2] = -point[1];

        outContourTop.add(vertex);
      }
    }
  }
  
  protected void calculateBottomContour(ArrayList<double[]> outContourBottom,
      boolean clockwise) {
    if(clockwise) {
      for (double[] point : pointList) {
        double[] vertex = new double[12];

        vertex[0] = point[0];
        vertex[1] = -getObjectHeight()/2;
        vertex[2] = -point[1];

        outContourBottom.add(vertex);
      }
    } else {
      for (int i=pointList.size()-1; i>=0; i--) {
        double[] point = pointList.get(i);
        
        double[] vertex = new double[12];

        vertex[0] = point[0];
        vertex[1] = -getObjectHeight()/2;
        vertex[2] = -point[1];

        outContourBottom.add(vertex);
      }
    }
  }

  @Override
  public void generateDisplayList(GL2 gl, GLU glu) {
    /*
     * Lists of all vertices describing the top and bottom face of the polygon, as well
     * as the vertices color and texture coordinates.
     */
    ArrayList<double[]> outContourTop = new ArrayList<double[]>();
    ArrayList<double[]> outContourBottom = new ArrayList<double[]>();
    
    double[] normalTop = {0, 1, 0};
    double[] normalBottom = {0, -1, 0};

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
        
        double[] point0 = {pointList.get(0)[0], 0, -pointList.get(0)[1]};
        double[] point1 = {pointList.get(1)[0], 0, -pointList.get(1)[1]};
        double[] point2 = {pointList.get(2)[0], 0, -pointList.get(2)[1]};
        
        double[] directionOfRotation = crossProduct(
            subtractVectors(point0, point1),
            subtractVectors(point2, point1));
        
        boolean clockwise = directionOfRotation[1] > 0 ? true : false;
        
        // Check if the polygon will be rendered with a texture applied on it
        if (texture != null) {

          /*TextureCoords tc = texture.getImageTexCoords();
          
          // Calculate the contour of the top and bottom polygon
          calculateTopContour(outContourTop, null);
          calculateBottomContour(outContourBottom, null);

          // Set the polygons color to white (because of texture)
          applyColor(outContourTop, Color.WHITE);
          applyColor(outContourBottom, Color.WHITE);

          // Map the texture to the polygon
          applyTexture(outContourTop, tc, false);
          applyTexture(outContourBottom, tc, true);
          
          applyNormal(outContourTop, normalTop);
          applyNormal(outContourBottom, normalBottom);
          
          // Enable texture support
          texture.bind();
          texture.enable();

          // Draw the textured polygons
          TessellatedPolygon poly = new TessellatedPolygon();
          poly.init(gl, glu);
          
          // Draw top face of the polygon
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(outContourTop);
          poly.endContour();
          poly.endPolygon();
          // Draw bottom face of the polygon
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(outContourBottom);
          poly.endContour();
          poly.endPolygon();
          
          poly.end();
          
          // Set the drawing color to white (because of texture)
          gl.glColor4dv(Color.WHITE.getColor(), 0);
          
          // draw the side faces of the 3D polygon as rectangles with texture coordinates
          gl.glBegin(GL2.GL_QUADS);
          
          int numPoints = outContourTop.size();
          
          for(int i=0; i<numPoints-1; i++) {
            double[] normal = crossProduct(
                subtractVectors(outContourBottom.get(i), outContourTop.get(i)),
                subtractVectors(outContourTop.get(i+1), outContourTop.get(i)));
            normalize(normal);
            gl.glNormal3dv(normal, 0);
            gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(outContourBottom.get(i), 0);
            gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(outContourBottom.get(i+1), 0);            
            gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(outContourTop.get(i+1), 0);
            gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(outContourTop.get(i), 0);
          }
          
          double[] normal = crossProduct(
              subtractVectors(outContourBottom.get(numPoints-1), outContourTop.get(numPoints-1)),
              subtractVectors(outContourTop.get(0), outContourTop.get(numPoints-1)));
          normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(outContourBottom.get(numPoints-1), 0);
          gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(outContourBottom.get(0), 0);            
          gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(outContourTop.get(0), 0);
          gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(outContourTop.get(numPoints-1), 0);
          
          gl.glEnd();

          // Disable texture support
          texture.disable();*/
        } else {
          // Calculate the contour of the top and bottom polygons
          calculateTopContour(outContourTop, clockwise);
          calculateBottomContour(outContourBottom, clockwise);
          
          // Apply the fill color to the polygons
          applyColor(outContourTop, fill);
          applyColor(outContourBottom, fill);
          
          // Don't apply any texture
          applyTexture(outContourTop, null, false);
          applyTexture(outContourBottom, null, true);
          
          // Apply the normals to the polygons
          applyNormal(outContourTop, normalTop);
          applyNormal(outContourBottom, normalBottom);

          // Draw the polygons without texture
          TessellatedPolygon poly = new TessellatedPolygon();
          poly.init(gl, glu);
          poly.setWindingRule(GLU.GLU_TESS_WINDING_ODD);
          
          // Draw top polygon
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(outContourTop);
          poly.endContour();
          poly.endPolygon();
          
          // Draw bottom polygon
          poly.beginPolygon();
          poly.beginContour();
          poly.renderContour(outContourBottom);
          poly.endContour();
          poly.endPolygon();
          
          poly.end();
          //poly = null;
          
          // Set the drawing color
          gl.glColor4dv(fill.getColor(), 0);
          
          // draw the side faces of the 3D polygon as rectangles
          gl.glBegin(GL2.GL_QUADS);
          
          int numPoints = outContourTop.size();
          
          for(int i=0; i<numPoints-1; i++) {
            double[] normal = crossProduct(
                subtractVectors(outContourBottom.get(numPoints-1-i), outContourTop.get(i)),
                subtractVectors(outContourTop.get(i+1), outContourTop.get(i)));
            normalize(normal);
            gl.glNormal3dv(normal, 0);
            gl.glVertex3dv(outContourBottom.get(numPoints-1-i), 0);
            gl.glVertex3dv(outContourBottom.get(numPoints-1-(i+1)), 0);            
            gl.glVertex3dv(outContourTop.get(i+1), 0);
            gl.glVertex3dv(outContourTop.get(i), 0);
          }
          
          double[] normal = crossProduct(
              subtractVectors(outContourBottom.get(0), outContourTop.get(numPoints-1)), // outContourBottom.get(numPoints-1-(numPoints-1))
              subtractVectors(outContourTop.get(0), outContourTop.get(numPoints-1)));
          normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glVertex3dv(outContourBottom.get(0), 0); // outContourBottom.get(numPoints-1-(numPoints-1))
          gl.glVertex3dv(outContourBottom.get(numPoints-1), 0);   // outContourBottom.get(numPoints-1-0)          
          gl.glVertex3dv(outContourTop.get(0), 0);
          gl.glVertex3dv(outContourTop.get(numPoints-1), 0);
          
          gl.glEnd();
        }
      }
    }

    gl.glEndList();
  }

}
