package aors.module.visopengl3d.shape;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

public class PolyLine extends Shape2D  {

  public PolyLine() {
    type = ShapeType.Polyline;
  }
  
  @Override
  public void generateDisplayList(GL2 gl, GLU glu) {
    //System.out.println(pointList);
    //System.out.println(stroke);
    if (pointList != null) {
      // Don't draw if there are not enough points for a polyline
      if (pointList.size() > 1) {
        // Set opacity
        stroke.setAlpha(strokeOpacity);

        // Get a denominator for the display list
        displayList = gl.glGenLists(1);

        // Create the display list
        gl.glNewList(displayList, GL2.GL_COMPILE);

        // Set drawing color
        gl.glColor4dv(stroke.getColor(), 0);

        int numPoints = pointList.size();
        
        ArrayList<double[]> pointList3d = new ArrayList<double[]>();
        double objectHeight = getObjectHeight();
        
        for(int i=0; i<numPoints; i++) {
          double[] point2d = pointList.get(i);
          double[] point3d = {point2d[0],
                              objectHeight/2,
                              -point2d[1]
                             };
          pointList3d.add(i, point3d);
        }
        
        double[][] surfaceNormals1 = new double[numPoints-1][3];
        double[][] surfaceNormals2 = new double[numPoints-1][3];
        double[] downVector = {0, -1, 0};
        
        for(int i=0; i<numPoints-1; i++) {
          surfaceNormals1[i] = normalizedVector(
                                 crossProduct(
                                   subtractVectors(pointList3d.get(i+1), pointList3d.get(i)),
                                   downVector
                                 )
                               );
          
          surfaceNormals2[i][0] = -surfaceNormals1[i][0];
          surfaceNormals2[i][1] = -surfaceNormals1[i][1];
          surfaceNormals2[i][2] = -surfaceNormals1[i][2];
        }
        
        double[][] pointNormals1 = new double[numPoints][3];
        double[][] pointNormals2 = new double[numPoints][3];
        
        pointNormals1[0] = surfaceNormals1[0];
        pointNormals2[0] = surfaceNormals2[0];
        pointNormals1[numPoints-1] = surfaceNormals1[numPoints-2];
        pointNormals2[numPoints-1] = surfaceNormals2[numPoints-2];
        
        for(int i=1; i<numPoints-1; i++) {
          pointNormals1[i] = normalizedVector(
                               addVectors(
                                 surfaceNormals1[i-1],
                                 surfaceNormals1[i]
                               )
                             );
          
          pointNormals2[i][0] = -pointNormals1[i][0];
          pointNormals2[i][1] = -pointNormals1[i][1];
          pointNormals2[i][2] = -pointNormals1[i][2];
        }
        
        // array containing the vertices of the top face of the 3D polyline
        double[][] topVertices1 = new double[numPoints][3];
        double[][] topVertices2 = new double[numPoints][3];
        
        // array containing the vertices of the bottom face of the 3D polyline
        double[][] bottomVertices1 = new double[numPoints][3];
        double[][] bottomVertices2 = new double[numPoints][3];
        
        topVertices1[0] = addVectors(pointList3d.get(0), multScalarWithVector(strokeWidth/2, pointNormals1[0]));
        topVertices2[0] = addVectors(pointList3d.get(0), multScalarWithVector(strokeWidth/2, pointNormals2[0]));
        topVertices1[numPoints-1] = addVectors(pointList3d.get(numPoints-1), multScalarWithVector(strokeWidth/2, pointNormals1[numPoints-1]));
        topVertices2[numPoints-1] = addVectors(pointList3d.get(numPoints-1), multScalarWithVector(strokeWidth/2, pointNormals2[numPoints-1]));
        
        for(int i=1; i<numPoints-1; i++) {
          double cosAlpha = cosAngleBetweenVectors(pointNormals2[i], surfaceNormals2[i]);
          //System.out.println(Math.acos(cosAlpha) * (180/Math.PI));
          //double pointStrokeWidth = cosAlpha * (strokeWidth/2);
          double pointStrokeWidth = (strokeWidth/2) / cosAlpha;
          topVertices1[i] = addVectors(
                              pointList3d.get(i),
                              multScalarWithVector(pointStrokeWidth, pointNormals1[i])
                            );
          
          topVertices2[i] = addVectors(
                              pointList3d.get(i),
                              multScalarWithVector(pointStrokeWidth, pointNormals2[i])
                            );
        }
        
        for(int i = 0; i < numPoints; i++) {
          bottomVertices1[i][0] = topVertices1[i][0];
          bottomVertices1[i][1] = -topVertices1[i][1];
          bottomVertices1[i][2] = topVertices1[i][2];
          
          bottomVertices2[i][0] = topVertices2[i][0];
          bottomVertices2[i][1] = -topVertices2[i][1];
          bottomVertices2[i][2] = topVertices2[i][2];
        }
        
        /*System.out.println("topVertices1");
        for(int i=0; i<numPoints; i++) {
          System.out.println("i = " + i);
          System.out.println(topVertices1[i][0]);
          System.out.println(topVertices1[i][1]);
          System.out.println(topVertices1[i][2]);
        }
        
        System.out.println("topVertices2");
        for(int i=0; i<numPoints; i++) {
          System.out.println("i = " + i);
          System.out.println(topVertices2[i][0]);
          System.out.println(topVertices2[i][1]);
          System.out.println(topVertices2[i][2]);
        }
        
        System.out.println("bottomVertices1");
        for(int i=0; i<numPoints; i++) {
          System.out.println("i = " + i);
          System.out.println(bottomVertices1[i][0]);
          System.out.println(bottomVertices1[i][1]);
          System.out.println(bottomVertices1[i][2]);
        }
        
        System.out.println("bottomVertices2");
        for(int i=0; i<numPoints; i++) {
          System.out.println("i = " + i);
          System.out.println(bottomVertices2[i][0]);
          System.out.println(bottomVertices2[i][1]);
          System.out.println(bottomVertices2[i][2]);
        }*/
        

        
        // Draw top face of the 3D polyline
        gl.glBegin(GL2.GL_TRIANGLE_STRIP);
        
        gl.glNormal3d(0, 1, 0);
        
        for (int i=0; i<numPoints; i++) {
          gl.glVertex3dv(topVertices1[i], 0);
          gl.glVertex3dv(topVertices2[i], 0);
        }
        
        gl.glEnd();
        
        // Draw bottom face of the 3D polyline
        gl.glBegin(GL2.GL_TRIANGLE_STRIP);
        
        gl.glNormal3d(0, -1, 0);
        
        for (int i=0; i<numPoints; i++) {
          gl.glVertex3dv(topVertices2[i], 0);
          gl.glVertex3dv(topVertices1[i], 0);
        }
        
        gl.glEnd();
        
        
        gl.glBegin(GL2.GL_QUADS);
        
        // Draw side faces of the 3D polyline
        for(int i=0; i<numPoints-1; i++) {
          gl.glNormal3dv(surfaceNormals1[i], 0);
          
          gl.glVertex3dv(bottomVertices1[i+1], 0);
          gl.glVertex3dv(bottomVertices1[i], 0);
          gl.glVertex3dv(topVertices1[i], 0);
          gl.glVertex3dv(topVertices1[i+1], 0);
          
          gl.glNormal3dv(surfaceNormals2[i], 0);
          
          gl.glVertex3dv(bottomVertices2[i], 0);
          gl.glVertex3dv(bottomVertices2[i+1], 0);
          gl.glVertex3dv(topVertices2[i+1], 0);
          gl.glVertex3dv(topVertices2[i], 0);
        }
        
        // Draw the start and end side face
        double[] normalStart1 = normalizedVector(subtractVectors(pointList3d.get(0), pointList3d.get(1)));
        //System.out.println(normalStart1[0] + "," + normalStart1[1] + "," + normalStart1[2]);
        
        double[] normalStart = normalizedVector(crossProduct(subtractVectors(topVertices1[0], topVertices2[0]),
                                                        subtractVectors(bottomVertices2[0], topVertices2[0])));
        //System.out.println(normalStart[0] + "," + normalStart[1] + "," + normalStart[2]);
        gl.glNormal3dv(normalStart, 0);
        
        gl.glVertex3dv(bottomVertices1[0], 0);
        gl.glVertex3dv(bottomVertices2[0], 0);
        gl.glVertex3dv(topVertices2[0], 0);
        gl.glVertex3dv(topVertices1[0], 0);
        
        //double[] normalEnd = normalizedVector(subtractVectors(pointList3d.get(numPoints-1), pointList3d.get(numPoints-2)));
        double[] normalEnd = normalizedVector(crossProduct(subtractVectors(topVertices2[numPoints-1], topVertices1[numPoints-1]),
            subtractVectors(bottomVertices1[numPoints-1], topVertices1[numPoints-1])));
        gl.glNormal3dv(normalEnd, 0);
        
        gl.glVertex3dv(bottomVertices2[numPoints-1], 0);
        gl.glVertex3dv(bottomVertices1[numPoints-1], 0);
        gl.glVertex3dv(topVertices1[numPoints-1], 0);
        gl.glVertex3dv(topVertices2[numPoints-1], 0);
        
        gl.glEnd();

        gl.glEndList();
      }
    }
  }

}
