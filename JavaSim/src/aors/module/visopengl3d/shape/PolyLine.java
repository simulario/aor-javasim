package aors.module.visopengl3d.shape;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import aors.module.visopengl3d.utility.VectorOperations;

/**
 * This PolyLine class represents a three dimensional object with a polyline as bottom and top face
 * 
 * @author Susanne Schölzel
 * @since January 4th, 2012
 * 
 */
public class PolyLine extends Shape2D  {

  public PolyLine() {
    type = ShapeType.Polyline;
  }
  
  @Override
  public void generateDisplayList(GL2 gl, GLU glu) {
    //System.out.println(pointList);
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
          surfaceNormals1[i] = VectorOperations.normalizedVector(
                                 VectorOperations.crossProduct(
                                   VectorOperations.subtractVectors(pointList3d.get(i+1), pointList3d.get(i)),
                                   downVector
                                 )
                               );
          
          surfaceNormals2[i][0] = -surfaceNormals1[i][0];
          surfaceNormals2[i][1] = -surfaceNormals1[i][1];
          surfaceNormals2[i][2] = -surfaceNormals1[i][2];
        }
        
        // draw a cuboid for each connection line between two consecutive points of the polyline
        gl.glBegin(GL2.GL_QUADS);
        
        for(int i=0; i<numPoints-1; i++) {
          
          double[][] topVertices = new double[4][3];
          topVertices[0] = VectorOperations.addVectors(pointList3d.get(i), VectorOperations.multScalarWithVector(strokeWidth/2, surfaceNormals1[i]));
          topVertices[1] = VectorOperations.addVectors(pointList3d.get(i), VectorOperations.multScalarWithVector(strokeWidth/2, surfaceNormals2[i]));
          topVertices[2] = VectorOperations.addVectors(pointList3d.get(i+1), VectorOperations.multScalarWithVector(strokeWidth/2, surfaceNormals2[i]));
          topVertices[3] = VectorOperations.addVectors(pointList3d.get(i+1), VectorOperations.multScalarWithVector(strokeWidth/2, surfaceNormals1[i]));
          
          double[][] bottomVertices = new double[4][3];
          for(int j=0; j<4; j++) {
            bottomVertices[j][0] = topVertices[j][0];
            bottomVertices[j][1] = -topVertices[j][1];
            bottomVertices[j][2] = topVertices[j][2];
          }
          
          // top face
          double[] normal = VectorOperations.crossProduct(VectorOperations.subtractVectors(topVertices[2], topVertices[1]), 
              VectorOperations.subtractVectors(topVertices[0], topVertices[1]));
          VectorOperations.normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glVertex3dv(topVertices[0], 0);
          gl.glVertex3dv(topVertices[1], 0);
          gl.glVertex3dv(topVertices[2], 0);
          gl.glVertex3dv(topVertices[3], 0);
          
          normal = VectorOperations.crossProduct(VectorOperations.subtractVectors(bottomVertices[1], bottomVertices[2]), 
              VectorOperations.subtractVectors(bottomVertices[3], bottomVertices[2]));
          VectorOperations.normalize(normal);
          gl.glNormal3dv(normal, 0);
          // bottom face
          gl.glVertex3dv(bottomVertices[3], 0);
          gl.glVertex3dv(bottomVertices[2], 0);
          gl.glVertex3dv(bottomVertices[1], 0);
          gl.glVertex3dv(bottomVertices[0], 0);
          
          // front face
          normal = VectorOperations.crossProduct(VectorOperations.subtractVectors(topVertices[1], bottomVertices[1]), 
              VectorOperations.subtractVectors(bottomVertices[0], bottomVertices[1]));
          VectorOperations.normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glVertex3dv(bottomVertices[0], 0);
          gl.glVertex3dv(bottomVertices[1], 0);
          gl.glVertex3dv(topVertices[1], 0);
          gl.glVertex3dv(topVertices[0], 0);
          
          // back face
          normal = VectorOperations.crossProduct(VectorOperations.subtractVectors(topVertices[3], bottomVertices[3]), 
              VectorOperations.subtractVectors(bottomVertices[2], bottomVertices[3]));
          VectorOperations.normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glVertex3dv(bottomVertices[2], 0);
          gl.glVertex3dv(bottomVertices[3], 0);
          gl.glVertex3dv(topVertices[3], 0);
          gl.glVertex3dv(topVertices[2], 0);
          
          // left face
          normal = VectorOperations.crossProduct(VectorOperations.subtractVectors(topVertices[0], bottomVertices[0]), 
              VectorOperations.subtractVectors(bottomVertices[3], bottomVertices[0]));
          VectorOperations.normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glVertex3dv(bottomVertices[3], 0);
          gl.glVertex3dv(bottomVertices[0], 0);
          gl.glVertex3dv(topVertices[0], 0);
          gl.glVertex3dv(topVertices[3], 0);
          
          // right face
          normal = VectorOperations.crossProduct(VectorOperations.subtractVectors(topVertices[2], bottomVertices[2]), 
              VectorOperations.subtractVectors(bottomVertices[1], bottomVertices[2]));
          VectorOperations.normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glVertex3dv(bottomVertices[1], 0);
          gl.glVertex3dv(bottomVertices[2], 0);
          gl.glVertex3dv(topVertices[2], 0);
          gl.glVertex3dv(topVertices[1], 0);
        }
        
        gl.glEnd();
        
        // draw a cylinder with radius strokeWidth/2 at each point of the polyline to connect the ends of the single segments
        for(int i=0; i<numPoints; i++) {
          double[] point = pointList3d.get(i);
          
          gl.glPushMatrix();
          
          // rotate and translate the cylinder, so that the top points in direction of the positive y-axis
          // and one half of the cylinder is on the positive y-axis and the other half is on the negative y-axis
          gl.glTranslated(point[0], -objectHeight/2, point[2]);
          gl.glRotated(-90, 1, 0, 0);
          
          // radius of the cylinder
          double radius = strokeWidth / 2;
          
          // Don't draw anything if the dimensions are too small
          if (objectHeight > 0 && radius > 0) {
            
            // GLUquadric objects for the side face, top face and bottom face of the cylinder
            GLUquadric side = glu.gluNewQuadric();
            GLUquadric top = glu.gluNewQuadric();
            GLUquadric bottom = glu.gluNewQuadric();
            
            // draw the side face of the cylinder
            glu.gluQuadricNormals(side, GLU.GLU_SMOOTH);
            glu.gluCylinder(side, radius, radius, objectHeight, 36, 6);
            glu.gluDeleteQuadric(side);

            // draw the top face of the cylinder
            gl.glPushMatrix();
            
            gl.glTranslated(0, 0, objectHeight);
            
            glu.gluQuadricNormals(top, GLU.GLU_SMOOTH);
            glu.gluDisk(top, 0, radius, 36, 6);
            glu.gluDeleteQuadric(top);
            
            gl.glPopMatrix();
            
            // draw the bottom face of the cylinder
            glu.gluQuadricOrientation(bottom, GLU.GLU_INSIDE);
            glu.gluQuadricNormals(bottom, GLU.GLU_SMOOTH);
            glu.gluDisk(bottom, 0, radius, 36, 6);
            glu.gluDeleteQuadric(bottom);
          }
          
          gl.glPopMatrix();
        }

        gl.glEndList();
      }
    }
  }

}
