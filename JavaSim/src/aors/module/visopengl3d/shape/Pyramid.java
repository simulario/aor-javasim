package aors.module.visopengl3d.shape;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl3d.utility.Color;

import com.sun.opengl.util.texture.TextureCoords;

/**
 * The Pyramid class is representing a simple geometric pyramid.
 * 
 * @author Susanne Schölzel
 * @since November 25th, 2010
 * 
 */
public class Pyramid extends Shape3D {

	/**
	 * Creates a new Pyramid instance and initializes its members.
	 */
	public Pyramid() {
		type = ShapeType.Pyramid;
	}
	
	@Override
	public void generateDisplayList(GL2 gl, GLU glu) {
		
		// Set the alpha value of the fill color to the fill opacity
		fill.setAlpha(fillOpacity);
		
		// Get a denominator for the display list
    displayList = gl.glGenLists(1);

    // Create the display list
    gl.glNewList(displayList, GL2.GL_COMPILE);
        
    // Don't draw anything if the dimensions are too small
    if (width > 0 && height > 0 && depth > 0) {
	    	
	    // half width of the pyramid
		  double halfWidth = width/2;
		  
		  // half height of the pyramid
		  double halfHeight = height/2;
		  
		  // half depth of the pyramid
		  double halfDepth = depth/2;
		  
		  // array containing the 5 vertices of the pyramid
		  double[][] vertices = {
		  	{-halfWidth, -halfHeight, halfDepth},   // Front left
		  	{halfWidth, -halfHeight, halfDepth},	// Front right
		  	{halfWidth, -halfHeight, -halfDepth},	// Back right
		  	{-halfWidth, -halfHeight, -halfDepth},	// Back left
		  	{0, halfHeight, 0}  					// Top
		  };
	    	
      // Check if the pyramid will be rendered with a texture applied to it
      if (texture != null) {
	    	  
	    	// Set the drawing color to white (because of texture)
  	    gl.glColor4dv(Color.WHITE.getColor(), 0);
		  	
        TextureCoords tc = texture.getImageTexCoords();
            
        // Enable texture support
        texture.bind();
        texture.enable();
            
        // draw the base of the pyramid as a rectangle with texture coordinates
        gl.glBegin(GL2.GL_QUADS);
	        
        // Base
        gl.glNormal3d(0, -1, 0);
  			gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[3], 0);
  			gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[2], 0);
  			gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(vertices[1], 0);
  			gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(vertices[0], 0);
  			
  			gl.glEnd();
	        
        // draw the four faces of the pyramid as triangles with texture coordinates
        gl.glBegin(GL2.GL_TRIANGLES);
	        
        // Front Face
        double[] nFront = {0, depth*halfWidth, height*width};
        normalize(nFront);
        gl.glNormal3dv(nFront, 0);
  			gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[0], 0);
  			gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[1], 0);
  			gl.glTexCoord2d(tc.left() + 0.5, tc.top()); gl.glVertex3dv(vertices[4], 0);
  			// Back Face
  			double[] nBack = {0, depth*halfWidth, -height*width};
        normalize(nBack);
        gl.glNormal3dv(nBack, 0);
    		gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[2], 0);
    		gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[3], 0);
    		gl.glTexCoord2d(tc.left() + 0.5, tc.top()); gl.glVertex3dv(vertices[4], 0);
    		// Left Face
    		double[] nLeft = {-height*depth, depth*halfWidth, 0};
        normalize(nLeft);
        gl.glNormal3dv(nLeft, 0);
  			gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[3], 0);
  			gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[0], 0);
  			gl.glTexCoord2d(tc.left() + 0.5, tc.top()); gl.glVertex3dv(vertices[4], 0);
  			// Right Face
  			double[] nRight = {height*depth, depth*halfWidth, 0};
        normalize(nRight);
        gl.glNormal3dv(nRight, 0);
  			gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[1], 0);
  			gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[2], 0);
  			gl.glTexCoord2d(tc.left() + 0.5, tc.top()); gl.glVertex3dv(vertices[4], 0);
  			
  			gl.glEnd();

		  	// Disable texture support
        texture.disable();
		  	
      } else {
			
	    	// Set the drawing color
  	    gl.glColor4dv(fill.getColor(), 0);
	    	  
  	    // draw the base of the pyramid as a rectangle
        gl.glBegin(GL2.GL_QUADS);
        
        // Base
        gl.glNormal3d(0, -1, 0);
  			gl.glVertex3dv(vertices[3], 0);
  			gl.glVertex3dv(vertices[2], 0);
  			gl.glVertex3dv(vertices[1], 0);
  			gl.glVertex3dv(vertices[0], 0);
  			
  			gl.glEnd();
			
        // draw the four faces of the pyramid as triangles
        gl.glBegin(GL2.GL_TRIANGLES);
	        
        // Front Face
        double[] nFront = {0, depth*halfWidth, height*width};
        normalize(nFront);
        gl.glNormal3dv(nFront, 0);
  			gl.glVertex3dv(vertices[0], 0);
  			gl.glVertex3dv(vertices[1], 0);
  			gl.glVertex3dv(vertices[4], 0);
  			
  			// Back Face
  			double[] nBack = {0, depth*halfWidth, -height*width};
        normalize(nBack);
        gl.glNormal3dv(nBack, 0);
  			gl.glVertex3dv(vertices[2], 0);
  			gl.glVertex3dv(vertices[3], 0);
  			gl.glVertex3dv(vertices[4], 0);
			
  			// Left Face
  			double[] nLeft = {-height*depth, depth*halfWidth, 0};
        normalize(nLeft);
        gl.glNormal3dv(nLeft, 0);
  			gl.glVertex3dv(vertices[3], 0);
  			gl.glVertex3dv(vertices[0], 0);
  			gl.glVertex3dv(vertices[4], 0);
			
  			// Right Face
  			double[] nRight = {height*depth, depth*halfWidth, 0};
        normalize(nRight);
        gl.glNormal3dv(nRight, 0);
  			gl.glVertex3dv(vertices[1], 0);
  			gl.glVertex3dv(vertices[2], 0);
  			gl.glVertex3dv(vertices[4], 0);
			
  			gl.glEnd();
	    	  
      }
    }
		
		gl.glEndList();
		
	}
}
