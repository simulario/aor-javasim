package aors.module.visopengl3d.shape;


import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import aors.module.visopengl3d.utility.Color;

import com.sun.opengl.util.texture.TextureCoords;

/**
 * The Cuboid class is representing a simple geometric cuboid.
 * 
 * @author Susanne Sch�lzel
 * @since November 25th, 2010
 * 
 */
public class Cuboid extends Shape3D {

	/**
	 * Creates a new Cuboid instance and initializes its members.
	 */
	public Cuboid() {
		type = ShapeType.Cuboid;
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
	    	
	      // half width of the cuboid
		  double halfWidth = width/2;
		  
		  // half height of the cuboid
		  double halfHeight = height/2;
		  
		  // half depth of the cuboid
		  double halfDepth = depth/2;
		  
		  // array containing the 8 vertices of the cuboid
		  double[][] vertices = {
		  	{-halfWidth, -halfHeight, halfDepth},   // Bottom front left
		  	{halfWidth, -halfHeight, halfDepth},	// Bottom front right
		  	{halfWidth, -halfHeight, -halfDepth},	// Bottom back right
		  	{-halfWidth, -halfHeight, -halfDepth},	// Bottom back left
		  	{-halfWidth, halfHeight, halfDepth},  	// Top front left
		  	{halfWidth, halfHeight, halfDepth},		// Top front right
		  	{halfWidth, halfHeight, -halfDepth},	// Top back right
		  	{-halfWidth, halfHeight, -halfDepth}	// Top back left
		  };
	    	
	      // Check if the cuboid will be rendered with a texture applied to it
	      if (texture != null) {
	    	  
	    	// Set the drawing color to white (because of texture)
	  	    gl.glColor4dv(Color.WHITE.getColor(), 0);
	  	    
	  	    // Set the material to a white material (because of texture)
		  	//setMaterial(gl, Color.WHITE.getColorFloat());
		  	
            TextureCoords tc = texture.getImageTexCoords();
            
            // Enable texture support
	        texture.bind();
	        texture.enable();
            
            // draw the six faces of the cuboid as rectangles with texture coordinates
	        gl.glBegin(GL2.GL_QUADS);
	        
	        // Front Face
	        gl.glNormal3d(0, 0, 1);
			gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[0], 0);
			gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[1], 0);
			gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(vertices[5], 0);
			gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(vertices[4], 0);
			// Back Face
			gl.glNormal3d(0, 0, -1);
			gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[2], 0);
			gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[3], 0);
			gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(vertices[7], 0);
			gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(vertices[6], 0);
			// Top Face
			gl.glNormal3d(0, 1, 0);
			gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[4], 0);
			gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[5], 0);
			gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(vertices[6], 0);
			gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(vertices[7], 0);
			// Bottom Face
			/*gl.glNormal3d(0, -1, 0);
			gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[1], 0);
			gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[0], 0);
			gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(vertices[3], 0);
			gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(vertices[2], 0);*/
			
			gl.glNormal3d(0, -1, 0);
			gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[3], 0);
			gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[2], 0);
			gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(vertices[1], 0);
			gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(vertices[0], 0);
			// Left Face
			gl.glNormal3d(-1, 0, 0);
			gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[3], 0);
			gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[0], 0);
			gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(vertices[4], 0);
			gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(vertices[7], 0);
			// Right Face
			gl.glNormal3d(1, 0, 0);
			gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[1], 0);
			gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[2], 0);
			gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(vertices[6], 0);
			gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(vertices[5], 0);
			
			gl.glEnd();

		  	// Disable texture support
	        texture.disable();
		  	
	      } else {
			
	    	// Set the drawing color
	  	    gl.glColor4dv(fill.getColor(), 0);
	    	
	    	// Set the material according to the fill color
		  	//setMaterial(gl, fill.getColorFloat());
	    	  
	  	    // draw the six faces of the cuboid as rectangles
	        gl.glBegin(GL2.GL_QUADS);
	        
	        // Front Face
	        gl.glNormal3d(0, 0, 1);
			gl.glVertex3dv(vertices[0], 0);
			gl.glVertex3dv(vertices[1], 0);
			gl.glVertex3dv(vertices[5], 0);
			gl.glVertex3dv(vertices[4], 0);
			// Back Face
			gl.glNormal3d(0, 0, -1);
			gl.glVertex3dv(vertices[2], 0);
			gl.glVertex3dv(vertices[3], 0);
			gl.glVertex3dv(vertices[7], 0);
			gl.glVertex3dv(vertices[6], 0);
			// Top Face
			gl.glNormal3d(0, 1, 0);
			gl.glVertex3dv(vertices[4], 0);
			gl.glVertex3dv(vertices[5], 0);
			gl.glVertex3dv(vertices[6], 0);
			gl.glVertex3dv(vertices[7], 0);
			// Bottom Face
			gl.glNormal3d(0, -1, 0);
			gl.glVertex3dv(vertices[3], 0);
			gl.glVertex3dv(vertices[2], 0);
			gl.glVertex3dv(vertices[1], 0);
			gl.glVertex3dv(vertices[0], 0);
			// Left Face
			gl.glNormal3d(-1, 0, 0);
			gl.glVertex3dv(vertices[3], 0);
			gl.glVertex3dv(vertices[0], 0);
			gl.glVertex3dv(vertices[4], 0);
			gl.glVertex3dv(vertices[7], 0);
			// Right Face
			gl.glNormal3d(1, 0, 0);
			gl.glVertex3dv(vertices[1], 0);
			gl.glVertex3dv(vertices[2], 0);
			gl.glVertex3dv(vertices[6], 0);
			gl.glVertex3dv(vertices[5], 0);
			
			gl.glEnd();
	    	  
	      }
	    }
		
		gl.glEndList();
				
	}
}