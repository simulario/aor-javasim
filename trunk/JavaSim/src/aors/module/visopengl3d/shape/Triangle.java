package aors.module.visopengl3d.shape;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.texture.TextureCoords;

import aors.module.visopengl3d.utility.Color;
import aors.module.visopengl3d.utility.VectorOperations;


/**
 * This Triangle class represents a three dimensional object with a triangle as bottom and top face
 * 
 * @author Susanne Schölzel
 * @since January 4th, 2012
 * 
 */
public class Triangle extends Shape2D {

  public Triangle() {
    type = ShapeType.Triangle;
  }
  
  @Override
  public void generateDisplayList(GL2 gl, GLU glu) {
    
    double objectHeight = getObjectHeight();
    double depth = height;
    
    // Set the alpha value of the fill color to the fill opacity
    fill.setAlpha(fillOpacity);
    
    // Get a denominator for the display list
    displayList = gl.glGenLists(1);

    // Create the display list
    gl.glNewList(displayList, GL2.GL_COMPILE);
        
    // Don't draw anything if the dimensions are too small
    if (width > 0 && objectHeight > 0 && depth > 0) {
        
      // half width of the regular triangular prism
      double halfWidth = width/2;
      
      // half height of the regular triangular prism
      double halfHeight = objectHeight/2;
      
      // half depth of the regular triangular prism
      double halfDepth = depth/2;
      
      // radius of the circumcircle of the base of the regular triangular prism
      double radius = halfDepth + (width*width)/(8*depth);
      
      // array containing the 6 vertices of the regular triangular prism
      double[][] vertices = {
        {-halfWidth, -halfHeight, depth-radius},  // Bottom front left
        {halfWidth, -halfHeight, depth-radius},   // Bottom front right
        {0, -halfHeight, -radius},                // Bottom back
        {-halfWidth, halfHeight, depth-radius},   // Top front left
        {halfWidth, halfHeight, depth-radius},    // Top front right
        {0, halfHeight, -radius}                  // Top back
       };
        
      // Check if the regular triangular prism will be rendered with a texture applied to it
      if (texture != null) {
        
        // Set the drawing color to white (because of texture)
        gl.glColor4dv(Color.WHITE.getColor(), 0);
        
        TextureCoords tc = texture.getImageTexCoords();
            
        // Enable texture support
        texture.bind();
        texture.enable();
            
        // draw the bottom and top of the regular triangular prism as triangles with texture coordinates
        gl.glBegin(GL2.GL_TRIANGLES);
          
        // Bottom Face
        gl.glNormal3d(0, -1, 0);
        gl.glTexCoord2d(tc.left() + 0.5, tc.bottom()); gl.glVertex3dv(vertices[2], 0);
        gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(vertices[1], 0);
        gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(vertices[0], 0);
        // Top Face
        gl.glNormal3d(0, 1, 0);
        gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[3], 0);
        gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[4], 0);
        gl.glTexCoord2d(tc.left() + 0.5, tc.top()); gl.glVertex3dv(vertices[5], 0);
        
        gl.glEnd();
          
        // draw the three faces of the regular triangular prism as rectangles with texture coordinates
        gl.glBegin(GL2.GL_QUADS);
        
        // Front Face
        gl.glNormal3d(0, 0, 1);
        gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[0], 0);
        gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[1], 0);
        gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(vertices[4], 0);
        gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(vertices[3], 0);
        // Right Back Face
        double[] nRight = {depth*objectHeight, 0, -halfWidth*objectHeight};
        VectorOperations.normalize(nRight);
        gl.glNormal3dv(nRight, 0);
        gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[1], 0);
        gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[2], 0);
        gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(vertices[5], 0);
        gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(vertices[4], 0);
        // Left Back Face
        double[] nLeft = {-depth*objectHeight, 0, -halfWidth*objectHeight};
        VectorOperations.normalize(nLeft);
        gl.glNormal3dv(nLeft, 0);
        gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[2], 0);
        gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[0], 0);
        gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(vertices[3], 0);
        gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(vertices[5], 0);
        
        gl.glEnd();
          
        // Disable texture support
        texture.disable();
        
      } else {
      
        // Set the drawing color
        gl.glColor4dv(fill.getColor(), 0);
        
        // draw the bottom and top of the regular triangular prism as triangles
        gl.glBegin(GL2.GL_TRIANGLES);
          
        // Bottom Face
        gl.glNormal3d(0, -1, 0);
        gl.glVertex3dv(vertices[2], 0);
        gl.glVertex3dv(vertices[1], 0);
        gl.glVertex3dv(vertices[0], 0);
        // Top Face
        gl.glNormal3d(0, 1, 0);
        gl.glVertex3dv(vertices[3], 0);
        gl.glVertex3dv(vertices[4], 0);
        gl.glVertex3dv(vertices[5], 0);
        
        gl.glEnd();
          
        // draw the three faces of the regular triangular prism as rectangles
        gl.glBegin(GL2.GL_QUADS);
        
        // Front Face
        gl.glNormal3d(0, 0, 1);
        gl.glVertex3dv(vertices[0], 0);
        gl.glVertex3dv(vertices[1], 0);
        gl.glVertex3dv(vertices[4], 0);
        gl.glVertex3dv(vertices[3], 0);
        // Right Back Face
        double[] nRight = {depth*objectHeight, 0, -halfWidth*objectHeight};
        VectorOperations.normalize(nRight);
        gl.glNormal3dv(nRight, 0);
        gl.glVertex3dv(vertices[1], 0);
        gl.glVertex3dv(vertices[2], 0);
        gl.glVertex3dv(vertices[5], 0);
        gl.glVertex3dv(vertices[4], 0);
        // Left Back Face
        double[] nLeft = {-depth*objectHeight, 0, -halfWidth*objectHeight};
        VectorOperations.normalize(nLeft);
        gl.glNormal3dv(nLeft, 0);
        gl.glVertex3dv(vertices[2], 0);
        gl.glVertex3dv(vertices[0], 0);
        gl.glVertex3dv(vertices[3], 0);
        gl.glVertex3dv(vertices[5], 0);
        
        gl.glEnd();
          
      }
    }
    
    gl.glEndList();
    
  }
}
