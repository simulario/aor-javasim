package aors.module.visopengl3d.shape;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.texture.TextureCoords;
//import javax.media.opengl.glu.GLUquadric;

import aors.module.visopengl3d.utility.Color;
import aors.module.visopengl3d.utility.VectorOperations;

/**
 * This Ellipse class represents a three dimensional object with an Ellipse as bottom and top face
 * 
 * @author Susanne Schölzel
 * @since January 4th, 2012
 * 
 */
public class Ellipse extends Shape2D {

  public Ellipse() {
    type = ShapeType.Ellipse;
  }
  
  @Override
  public void generateDisplayList(GL2 gl, GLU glu) {
    double objectHeight = getObjectHeight();
    
    // Set the alpha value of the fill color to the fill opacity
    fill.setAlpha(fillOpacity);
    
    // Get a denominator for the display list
    displayList = gl.glGenLists(1);

    // Create the display list
    gl.glNewList(displayList, GL2.GL_COMPILE);
    
    double radiusX = width/2;
    double radiusY = height/2;
    
    int slices = 180;
    double sliceAngle = 2.0;
    
    // array containing the vertices of the top face of the 3D ellipse
    double[][] topVertices = new double[slices+1][3];
    
    // array containing the vertices of the bottom face of the 3D ellipse
    double[][] bottomVertices = new double[slices+1][3];
    
    for(int i = 0; i < slices+1; i++) {
      double phi = sliceAngle * i * (Math.PI / 180);
      topVertices[i][0] = radiusX * Math.cos(phi);
      topVertices[i][1] = objectHeight / 2;
      topVertices[i][2] = -radiusY * Math.sin(phi);
      
      bottomVertices[i][0] = topVertices[i][0];
      bottomVertices[i][1] = -topVertices[i][1];
      bottomVertices[i][2] = topVertices[i][2];
    }
    
    double[] centerTop = {0.0, objectHeight/2, 0.0};
    double[] centerBottom = {0.0, -objectHeight/2, 0.0};
    
    double[][] normals = new double[slices+1][3];
    
    for(int i = 0; i < slices+1; i++) {
      double[] normal = VectorOperations.subtractVectors(topVertices[i], centerTop);
      VectorOperations.normalize(normal);
      normals[i] = normal;
    }
    
    // Don't draw anything if the dimensions are too small
    if (objectHeight > 0 && radiusX > 0 && radiusY > 0) {
      
      
      // Check if the cylinder will be rendered with a texture applied to it
      if (texture != null) {
        
        double x_min = 0;
        double x_max = 0;
        double z_min = 0;
        double z_max = 0;
        
        for (int i = 0; i < slices+1; i++) {
          if(topVertices[i][0] < x_min) {
            x_min = topVertices[i][0];
          } else if(topVertices[i][0] > x_max) {
            x_max = topVertices[i][0];
          }
          
          if(topVertices[i][2] < z_min) {
            z_min = topVertices[i][2];
          } else if(topVertices[i][2] > z_max) {
            z_max = topVertices[i][2];
          }
        }
        
        double x_width = x_max - x_min;
        double z_width = z_max - z_min;
        
        // Set the drawing color to white (because of texture)
        gl.glColor4dv(Color.WHITE.getColor(), 0);
        
        TextureCoords tc = texture.getImageTexCoords();
        
        // Enable texture support
        texture.bind();
        texture.enable();
        
        // draw the top face of the 3D ellipse with texture coordinates
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        
        gl.glNormal3d(0, 1, 0);
        gl.glTexCoord2d(tc.left() + ((centerTop[0] - x_min) / x_width), tc.bottom() + ((centerTop[2] - z_min) / z_width));
        gl.glVertex3dv(centerTop, 0);
        
        for(int i = 0; i < slices+1; i++) {
          gl.glTexCoord2d(tc.left() + ((topVertices[i][0] - x_min) / x_width), tc.bottom() + ((topVertices[i][2] - z_min) / z_width));
          gl.glVertex3dv(topVertices[i], 0);
        }
        
        gl.glEnd();
        
        // draw the bottom face of the 3D ellipse with texture coordinates
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        
        gl.glNormal3d(0, -1, 0);
        gl.glTexCoord2d(tc.left() + ((centerBottom[0] - x_min) / x_width), tc.bottom() + ((z_max - centerBottom[2]) / z_width));
        gl.glVertex3dv(centerBottom, 0);
        
        for(int i = slices; i >= 0; i--) {
          gl.glTexCoord2d(tc.left() + ((bottomVertices[i][0] - x_min) / x_width), tc.bottom() + ((z_max - bottomVertices[i][2]) / z_width));
          gl.glVertex3dv(bottomVertices[i], 0);
        }
        
        gl.glEnd();
        
        // draw the side face of the 3D ellipse with texture coordinates
        gl.glBegin(GL2.GL_QUADS);
        
        for(int i = 0; i < slices; i++) {
          gl.glNormal3dv(normals[i], 0);
          gl.glTexCoord2d(tc.left() + (i * sliceAngle) / 360 - 0.25, tc.bottom());
          gl.glVertex3dv(bottomVertices[i], 0);
          
          gl.glNormal3dv(normals[i+1], 0);
          gl.glTexCoord2d(tc.left() + ((i+1) * sliceAngle) / 360 - 0.25, tc.bottom());
          gl.glVertex3dv(bottomVertices[i+1], 0);
          
          gl.glTexCoord2d(tc.left() + ((i+1) * sliceAngle) / 360 - 0.25, tc.top());
          gl.glVertex3dv(topVertices[i+1], 0);
         
          gl.glNormal3dv(normals[i], 0);
          gl.glTexCoord2d(tc.left() + (i * sliceAngle) / 360 - 0.25, tc.top());
          gl.glVertex3dv(topVertices[i], 0);
        }
        
        gl.glEnd();
        
        // Disable texture support
        texture.disable();
        
      } else {
    
        // Set the drawing color
        gl.glColor4dv(fill.getColor(), 0);
          
        // draw the top face of the 3D ellipse
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        
        gl.glNormal3d(0, 1, 0);
        gl.glVertex3dv(centerTop, 0);
        
        for(int i = 0; i < slices+1; i++) {
          gl.glVertex3dv(topVertices[i], 0);
        }
        
        gl.glEnd();
        
        // draw the bottom face of the 3D ellipse
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        
        gl.glNormal3d(0, -1, 0);
        gl.glVertex3dv(centerBottom, 0);
        
        for(int i = slices; i >= 0; i--) {
          gl.glVertex3dv(bottomVertices[i], 0);
        }
        
        gl.glEnd();
        
        // draw the side face of the 3D ellipse
        gl.glBegin(GL2.GL_QUADS);
        
        for(int i = 0; i < slices; i++) {
          gl.glNormal3dv(normals[i], 0);
          gl.glVertex3dv(bottomVertices[i], 0);
          gl.glNormal3dv(normals[i+1], 0);
          gl.glVertex3dv(bottomVertices[i+1], 0);
          gl.glVertex3dv(topVertices[i+1], 0);
          gl.glNormal3dv(normals[i], 0);
          gl.glVertex3dv(topVertices[i], 0);
        }
        
        gl.glEnd();
          
      }
    }
    
    gl.glEndList();
    
  }
}
