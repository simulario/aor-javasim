package aors.module.visopengl3d.shape;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl3d.utility.Color;

import com.sun.opengl.util.texture.TextureCoords;

/**
 * The Tertahedra class is representing a simple geometric tetrahedra.
 * 
 * @author Susanne Schölzel
 * @since November 25th, 2010
 * 
 */
public class Tetrahedra extends Shape3D {

	/**
	 * Creates a new Tetrahedra instance and initializes its members.
	 */
	public Tetrahedra() {
		type = ShapeType.Tetrahedra;
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
	    	
	      // half width of the tetrahedra
		  double halfWidth = width/2;
		  
		  // half height of the tetrahedra
		  double halfHeight = height/2;
		  
		  // half depth of the regular triangular prism
		  double halfDepth = depth/2;
		  
		  // radius of the circumcircle of the base of the tetrahedra
		  double radius = halfDepth + (width*width)/(8*depth);
		  
		  // array containing the 4 vertices of the tetrahedra
		  double[][] vertices = {
		  	{0, -halfHeight, radius},   				// Bottom front
		  	{halfWidth, -halfHeight, radius-depth},		// Bottom back right
		  	{-halfWidth, -halfHeight, radius-depth},	// Bottom back left
		  	{0, halfHeight, 0}							// Top
		  };
	    	
	      // Check if the tetrahedra will be rendered with a texture applied to it
	      if (texture != null) {
	    	  
	    	// Set the drawing color to white (because of texture)
	  	    gl.glColor4dv(Color.WHITE.getColor(), 0);
	    	
	    	// Set the material to a white material (because of texture)
			//setMaterial(gl, Color.WHITE.getColorFloat());
		  	
            TextureCoords tc = texture.getImageTexCoords();
            
            // Enable texture support
	        texture.bind();
	        texture.enable();
            
	        // draw the four faces of the tetrahedra as triangles with texture coordinates
	        gl.glBegin(GL2.GL_TRIANGLES);
	        
			// Bottom Face
	        gl.glNormal3d(0, -1, 0);
			gl.glTexCoord2d(tc.left() + 0.5, tc.bottom()); gl.glVertex3dv(vertices[0], 0);
			gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(vertices[2], 0);
			gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(vertices[1], 0);
			// Left Face
			double[] nLeft = {-height*depth, halfWidth*radius, height*halfWidth};
			normalize(nLeft);
			gl.glNormal3dv(nLeft, 0);
			gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[2], 0);
			gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[0], 0);
			gl.glTexCoord2d(tc.left() + 0.5, tc.top()); gl.glVertex3dv(vertices[3], 0);
			// Right Face
			double[] nRight = {height*depth, halfWidth*radius, height*halfWidth};
			normalize(nRight);
			gl.glNormal3dv(nRight, 0);
			gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[0], 0);
			gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[1], 0);
			gl.glTexCoord2d(tc.left() + 0.5, tc.top()); gl.glVertex3dv(vertices[3], 0);
			// Back Face
			double[] nBack = {0, width*(depth-radius), -height*width};
			normalize(nBack);
			gl.glNormal3dv(nBack, 0);
			gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[1], 0);
			gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[2], 0);
			gl.glTexCoord2d(tc.left() + 0.5, tc.top()); gl.glVertex3dv(vertices[3], 0);
			
			gl.glEnd();
	        
		  	// Disable texture support
	        texture.disable();
		  	
	      } else {
			
	    	// Set the drawing color
	  	    gl.glColor4dv(fill.getColor(), 0);
	    	
	    	// Set the material according to the fill color
		  	//setMaterial(gl, fill.getColorFloat());
	    	  
	        // draw the four faces of the tetrahedra as triangles
	        gl.glBegin(GL2.GL_TRIANGLES);
	        
			// Bottom Face
	        gl.glNormal3d(0, -1, 0);
			gl.glVertex3dv(vertices[0], 0);
			gl.glVertex3dv(vertices[2], 0);
			gl.glVertex3dv(vertices[1], 0);
			// Left Face
			double[] nLeft = {-height*depth, halfWidth*radius, height*halfWidth};
			normalize(nLeft);
			gl.glNormal3dv(nLeft, 0);
			gl.glVertex3dv(vertices[2], 0);
			gl.glVertex3dv(vertices[0], 0);
			gl.glVertex3dv(vertices[3], 0);
			// Right Face
			double[] nRight = {height*depth, halfWidth*radius, height*halfWidth};
			normalize(nRight);
			gl.glNormal3dv(nRight, 0);
			gl.glVertex3dv(vertices[0], 0);
			gl.glVertex3dv(vertices[1], 0);
			gl.glVertex3dv(vertices[3], 0);
			// Back Face
			double[] nBack = {0, width*(depth-radius), -height*width};
			normalize(nBack);
			gl.glNormal3dv(nBack, 0);
			gl.glVertex3dv(vertices[1], 0);
			gl.glVertex3dv(vertices[2], 0);
			gl.glVertex3dv(vertices[3], 0);
			
			gl.glEnd();
	    	  
	      }
	    }
		
		gl.glEndList();
		
	}
}
