package aors.module.visopengl3d.shape;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl3d.utility.Color;

import com.sun.opengl.util.texture.TextureCoords;

public class RegularPolygon extends Shape2D {

  public RegularPolygon() {
    type = ShapeType.RegularPolygon;
  }
  
  @Override
  public void generateDisplayList(GL2 gl, GLU glu) {
    
    double objectHeight = getObjectHeight();
    double sideLength = width;
    // radius of the circumcircle of the regular polygon
    double radius = sideLength / (2 * Math.sin(Math.PI / numberOfPoints));    
    
    // Set the alpha value of the fill color to the fill opacity
    fill.setAlpha(fillOpacity);
    
    // Get a denominator for the display list
    displayList = gl.glGenLists(1);

    // Create the display list
    gl.glNewList(displayList, GL2.GL_COMPILE);
        
    // Don't draw anything if the dimensions are too small
    if (radius > 0 && objectHeight > 0) {
      
      // array containing the n vertices of the top face of the 3D regular polygon
      double[][] topVertices = new double[(int)numberOfPoints+1][3];
      
      // array containing the n vertices of the bottom face of the 3D regular polygon
      double[][] bottomVertices = new double[(int)numberOfPoints+1][3];
      
      for(int i = 0; i < numberOfPoints+1; i++) {
        double phi = (2 * Math.PI / numberOfPoints) * i;
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
        
        double x_min = 0;
        double x_max = 0;
        double z_min = 0;
        double z_max = 0;
        
        for (int i = 0; i < numberOfPoints; i++) {
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
        
        // Set the material to a white material (because of texture)
        //setMaterial(gl, Color.WHITE.getColorFloat());
        
        TextureCoords tc = texture.getImageTexCoords();
            
        // Enable texture support
        texture.bind();
        texture.enable();
        
        // draw the top face of the 3D regular polygon as trianglefan with texture coordinates
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        
        gl.glNormal3d(0, 1, 0);
        gl.glTexCoord2d(tc.left() + ((centerTop[0] - x_min) / x_width), tc.bottom() + ((centerTop[2] - z_min) / z_width));
        gl.glVertex3dv(centerTop, 0);
        
        for(int i = 0; i < numberOfPoints+1; i++) {
          gl.glTexCoord2d(tc.left() + ((topVertices[i][0] - x_min) / x_width), tc.bottom() + ((topVertices[i][2] - z_min) / z_width));
          gl.glVertex3dv(topVertices[i], 0);
        }
        
        gl.glEnd();
        
        
        // draw the bottom face of the 3D regular polygon as trianglefan with texture coordinates
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        
        gl.glNormal3d(0, -1, 0);
        gl.glTexCoord2d(tc.left() + ((centerBottom[0] - x_min) / x_width), tc.bottom() + ((z_max - centerBottom[2]) / z_width));
        gl.glVertex3dv(centerBottom, 0);
        
        for(int i = (int)numberOfPoints; i >= 0; i--) {
          gl.glTexCoord2d(tc.left() + ((bottomVertices[i][0] - x_min) / x_width), tc.bottom() + ((z_max - bottomVertices[i][2]) / z_width));
          gl.glVertex3dv(bottomVertices[i], 0);
        }
        
        gl.glEnd();
        
          
        // draw the n side faces of the 3D regular polygon as rectangles with texture coordinates
        gl.glBegin(GL2.GL_QUADS);
        
        //double phi = 2 * Math.PI / numberOfPoints;
        for(int i = 0; i < numberOfPoints; i++) {
          double[] normal = crossProduct(
                              subtractVectors(bottomVertices[i], topVertices[i]),
                              subtractVectors(topVertices[i+1], topVertices[i]));
          /*double[] normal =  {objectHeight * radius * (Math.sin(phi * (i+1)) - Math.sin(phi * i)),
                             0, 
                             objectHeight * radius * (Math.cos(phi * (i+1)) - Math.cos(phi * i))};*/
          normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(bottomVertices[i], 0);
          gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(bottomVertices[i+1], 0);
          gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(topVertices[i+1], 0);
          gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(topVertices[i], 0);
        }
        
        gl.glEnd();
          
        // Disable texture support
        texture.disable();
        
      } else {
      
        // Set the drawing color
        gl.glColor4dv(fill.getColor(), 0);
        
        // Set the material according to the fill color
        //setMaterial(gl, fill.getColorFloat());
          
        // draw the top face of the 3D regular polygon as trianglefan
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        
        gl.glNormal3d(0, 1, 0);
        gl.glVertex3dv(centerTop, 0);
        
        for(int i = 0; i < numberOfPoints+1; i++) {
          gl.glVertex3dv(topVertices[i], 0);
        }
        
        gl.glEnd();
        
        
        // draw the bottom face of the 3D regular polygon as trianglefan
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        
        gl.glNormal3d(0, -1, 0);
        gl.glVertex3dv(centerBottom, 0);
        
        for(int i = (int)numberOfPoints; i >= 0; i--) {
          gl.glVertex3dv(bottomVertices[i], 0);
        }
        
        gl.glEnd();
          
        // draw the n faces of the 3D regular polygon as rectangles with texture coordinates
        gl.glBegin(GL2.GL_QUADS);
        
        //double phi = 2 * Math.PI / numberOfPoints;
        for(int i = 0; i < numberOfPoints; i++) {
          double[] normal = crossProduct(
              subtractVectors(bottomVertices[i], topVertices[i]),
              subtractVectors(topVertices[i+1], topVertices[i]));
          
          /*{objectHeight * radius * (Math.sin(phi * (i+1)) - Math.sin(phi * i)),
                             0, 
                             objectHeight * radius * (Math.cos(phi * (i+1)) - Math.cos(phi * i))};*/
          normalize(normal);
          gl.glNormal3dv(normal, 0);
          gl.glVertex3dv(bottomVertices[i], 0);
          gl.glVertex3dv(bottomVertices[i+1], 0);
          gl.glVertex3dv(topVertices[i+1], 0);
          gl.glVertex3dv(topVertices[i], 0);
        }
        
        gl.glEnd();
          
      }
    }
    
    gl.glEndList();
    
  }

}
