package aors.module.visopengl3d.shape;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import aors.module.visopengl3d.utility.Color;
import aors.module.visopengl3d.utility.VectorOperations;

import com.sun.opengl.util.texture.TextureCoords;

/**
 * This Arc class represents a three dimensional object with an arc as bottom and top face
 * 
 * @author Susanne Schölzel
 * @since January 4th, 2012
 * 
 */
public class Arc extends Shape2D {
  public Arc() {
    type = ShapeType.Arc;
  }
  
  @Override
  public void generateDisplayList(GL2 gl, GLU glu) {
    
    double objectHeight = getObjectHeight();
    double radius = width / 2;
    
    double arcAngle;
    if(useEndAngle) {
      while(endAngle < startAngle) {
        endAngle += 360;
      }
      arcAngle = endAngle - startAngle;
    } else {
      arcAngle = angle;
    }

    if(arcAngle > 360) arcAngle = 360;
    
    
    // Set the alpha value of the fill color to the fill opacity
    fill.setAlpha(fillOpacity);
    
    // Get a denominator for the display list
    displayList = gl.glGenLists(1);

    // Create the display list
    gl.glNewList(displayList, GL2.GL_COMPILE);
    
    // Don't draw anything if the dimensions are too small
    if (arcAngle > 0 && radius > 0 && objectHeight > 0) {
      
      // GLUquadric objects for the top face and bottom face of the 3D arc
      GLUquadric top = glu.gluNewQuadric();
      GLUquadric bottom = glu.gluNewQuadric();
      
      // a slice is about 2 degrees wide
      int slices = (int)(arcAngle / 2);
      if(slices == 0) {
        slices = 1;
      }
      double sliceAngle = arcAngle / slices;
      
      // array containing the vertices of the top face of the 3D arc
      double[][] topVertices = new double[slices+1][3];
      
      // array containing the vertices of the bottom face of the 3D arc
      double[][] bottomVertices = new double[slices+1][3];
      
      for(int i = 0; i < slices+1; i++) {
        double phi = (startAngle + i * sliceAngle) * (Math.PI / 180);
        topVertices[i][0] = radius * Math.cos(phi);
        topVertices[i][1] = objectHeight / 2;
        topVertices[i][2] = -radius * Math.sin(phi);
        
        bottomVertices[i][0] = topVertices[i][0];
        bottomVertices[i][1] = -topVertices[i][1];
        bottomVertices[i][2] = topVertices[i][2];
      }
      
      double[] centerTop = {0.0, objectHeight/2, 0.0};
      double[] centerBottom = {0.0, -objectHeight/2, 0.0};
      
        
      // Check if the regular triangular prism will be rendered with a texture applied to it
      if (texture != null) {
        
        // Set the drawing color to white (because of texture)
        gl.glColor4dv(Color.WHITE.getColor(), 0);
        
        TextureCoords tc = texture.getImageTexCoords();
            
        // Enable texture support
        texture.bind();
        texture.enable();
        
        // draw the top face of the 3D arc as partial disk with texture coordinates
        gl.glPushMatrix();
        
        gl.glTranslated(0, objectHeight/2, 0);
        gl.glRotated(-90, 1, 0, 0);
        
        gl.glMatrixMode(GL2.GL_TEXTURE);
        gl.glPushMatrix();
        gl.glScaled(1, -1, 1);
        
        glu.gluQuadricTexture(top, true);
        glu.gluQuadricNormals(top, GLU.GLU_SMOOTH);
        glu.gluPartialDisk(top, 0, radius, slices, 6, startAngle+90, -arcAngle);
        glu.gluDeleteQuadric(top);
        
        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        
        // draw the bottom face of the 3D arc as partial disk with texture coordinates
        gl.glTranslated(0, -objectHeight/2, 0);
        gl.glRotated(-90, 1, 0, 0);
        
        glu.gluQuadricTexture(bottom, true);
        glu.gluQuadricOrientation(bottom, GLU.GLU_INSIDE);
        glu.gluQuadricNormals(bottom, GLU.GLU_SMOOTH);
        glu.gluPartialDisk(bottom, 0, radius, slices, 6, startAngle+90, -arcAngle);
        glu.gluDeleteQuadric(bottom);
        
        gl.glPopMatrix();
        
        // draw the side face of the 3D arc as rectangles with texture coordinates
        gl.glBegin(GL2.GL_QUADS);
        
        for(int i = 0; i < slices; i++) {
          double[] normal = VectorOperations.subtractVectors(bottomVertices[i], centerBottom);
          VectorOperations.normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glTexCoord2d(tc.left() + (i * sliceAngle) / arcAngle, tc.bottom()); gl.glVertex3dv(bottomVertices[i], 0);
          
          normal = VectorOperations.subtractVectors(bottomVertices[i+1], centerBottom);
          VectorOperations.normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glTexCoord2d(tc.left() + ((i+1) * sliceAngle) / arcAngle, tc.bottom()); gl.glVertex3dv(bottomVertices[i+1], 0);
          
          normal = VectorOperations.subtractVectors(topVertices[i+1], centerTop);
          VectorOperations.normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glTexCoord2d(tc.left() + ((i+1) * sliceAngle) / arcAngle, tc.top()); gl.glVertex3dv(topVertices[i+1], 0);
          
          normal = VectorOperations.subtractVectors(topVertices[i], centerTop);
          VectorOperations.normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glTexCoord2d(tc.left() + (i * sliceAngle) / arcAngle, tc.top()); gl.glVertex3dv(topVertices[i], 0);
        }
        
        if(arcAngle < 360) {
          double[] normal = VectorOperations.crossProduct(
              VectorOperations.subtractVectors(bottomVertices[slices], topVertices[slices]),
              VectorOperations.subtractVectors(centerTop, topVertices[slices])
          );
          
          VectorOperations.normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(bottomVertices[slices], 0);
          gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(centerBottom, 0);
          gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(centerTop, 0);
          gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(topVertices[slices], 0);
          
          normal = VectorOperations.crossProduct(
              VectorOperations.subtractVectors(centerBottom, centerTop),
              VectorOperations.subtractVectors(topVertices[0], centerTop)
          );
          
          VectorOperations.normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(centerBottom, 0);
          gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(bottomVertices[0], 0);
          gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(topVertices[0], 0);
          gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(centerTop, 0);
        }
        
        gl.glEnd();
          
        // Disable texture support
        texture.disable();
        
      } else {
      
        // Set the drawing color
        gl.glColor4dv(fill.getColor(), 0);
        
        // draw the top face of the 3D arc as partial disk
        gl.glPushMatrix();
        
        gl.glTranslated(0, objectHeight/2, 0);
        gl.glRotated(-90, 1, 0, 0);
        
        glu.gluQuadricNormals(top, GLU.GLU_SMOOTH);
        glu.gluPartialDisk(top, 0, radius, slices, 6, startAngle+90, -arcAngle);
        glu.gluDeleteQuadric(top);
        
        gl.glPopMatrix();
        
        gl.glPushMatrix();
        
        // draw the bottom face of the 3D arc as partial disk
        gl.glTranslated(0, -objectHeight/2, 0);
        gl.glRotated(-90, 1, 0, 0);
        
        glu.gluQuadricOrientation(bottom, GLU.GLU_INSIDE);
        glu.gluQuadricNormals(bottom, GLU.GLU_SMOOTH);
        glu.gluPartialDisk(bottom, 0, radius, slices, 6, startAngle+90, -arcAngle);
        glu.gluDeleteQuadric(bottom);
        
        gl.glPopMatrix();
        
        // draw the side face of the 3D arc as rectangles
        gl.glBegin(GL2.GL_QUADS);
        
        for(int i = 0; i < slices; i++) {
          double[] normal = VectorOperations.subtractVectors(bottomVertices[i], centerBottom);
          VectorOperations.normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glVertex3dv(bottomVertices[i], 0);
          
          normal = VectorOperations.subtractVectors(bottomVertices[i+1], centerBottom);
          VectorOperations.normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glVertex3dv(bottomVertices[i+1], 0);
          
          normal = VectorOperations.subtractVectors(topVertices[i+1], centerTop);
          VectorOperations.normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glVertex3dv(topVertices[i+1], 0);
          
          normal = VectorOperations.subtractVectors(topVertices[i], centerTop);
          VectorOperations.normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glVertex3dv(topVertices[i], 0);
        }
        
        if(arcAngle < 360) {
          double[] normal = VectorOperations.crossProduct(
              VectorOperations.subtractVectors(bottomVertices[slices], topVertices[slices]),
              VectorOperations.subtractVectors(centerTop, topVertices[slices])
          );
          
          VectorOperations.normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glVertex3dv(bottomVertices[slices], 0);
          gl.glVertex3dv(centerBottom, 0);
          gl.glVertex3dv(centerTop, 0);
          gl.glVertex3dv(topVertices[slices], 0);
          
          normal = VectorOperations.crossProduct(
              VectorOperations.subtractVectors(centerBottom, centerTop),
              VectorOperations.subtractVectors(topVertices[0], centerTop)
          );
          
          VectorOperations.normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glVertex3dv(centerBottom, 0);
          gl.glVertex3dv(bottomVertices[0], 0);
          gl.glVertex3dv(topVertices[0], 0);
          gl.glVertex3dv(centerTop, 0);
        }
        
        gl.glEnd();
          
      }
    }
    
    gl.glEndList();
    
  }

}
