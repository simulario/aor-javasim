package aors.module.visopengl3d.shape;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import aors.module.visopengl3d.utility.Color;

/**
 * The Cylinder class is representing a simple geometric cylinder.
 * 
 * @author Susanne Sch�lzel
 * @since November 25th, 2010
 * 
 */
public class Cylinder extends Shape3D {

	/**
	 * Creates a new Cylinder instance and initializes its members.
	 */
	public Cylinder() {
		type = ShapeType.Cylinder;
	}
	
	@Override
	public void generateDisplayList(GL2 gl, GLU glu) {
		
		// Set the alpha value of the fill color to the fill opacity
		fill.setAlpha(fillOpacity);
		
		// Get a denominator for the display list
	    displayList = gl.glGenLists(1);

	    // Create the display list
	    gl.glNewList(displayList, GL2.GL_COMPILE);
	    
	    gl.glPushMatrix();
	    
	    // rotate and translate the cylinder, so that the top points in direction of the positive y-axis
	    // and one half of the cylinder is on the positive y-axis and the other half is on the negative y-axis
	    gl.glTranslated(0, -height/2, 0);
	    gl.glRotated(-90, 1, 0, 0);
	    
	    // radius of the cylinder
	    double radius = width / 2;
	    
	    // Don't draw anything if the dimensions are too small
	    if (height > 0 && radius > 0) {
	      
	      // GLUquadric objects for the side face, top face and bottom face of the cylinder
	      GLUquadric side = glu.gluNewQuadric();
	      GLUquadric top = glu.gluNewQuadric();
	      GLUquadric bottom = glu.gluNewQuadric();
	    	
	      // Check if the cylinder will be rendered with a texture applied to it
	      if (texture != null) {
	    	  
	    	// Set the drawing color to white (because of texture)
	  	    gl.glColor4dv(Color.WHITE.getColor(), 0);
	  	    
	  	    // Set the material to a white material (because of texture)
		  	//setMaterial(gl, Color.WHITE.getColorFloat());
            
            // Enable texture support
	        texture.bind();
	        texture.enable();
	        
	        
	        // draw the side face of the cylinder with texture coordinates
	        gl.glMatrixMode(GL2.GL_TEXTURE);
	        gl.glPushMatrix();
	        gl.glTranslated(0.5, 0.5, 0);
	        gl.glScaled(-1, -1, 1);
	        gl.glTranslated(-0.5, -0.5, 0);
	        
	        glu.gluQuadricTexture(side, true);
	        glu.gluQuadricNormals(side, GLU.GLU_SMOOTH);
	        glu.gluCylinder(side, radius, radius, height, 36, 6);
	        glu.gluDeleteQuadric(side);
	        
	        gl.glPopMatrix();
	        gl.glMatrixMode(GL2.GL_MODELVIEW);
	        
	        // draw the top face of the cylinder with texture coordinates
	        gl.glPushMatrix();
	        gl.glTranslated(0, 0, height);
	        
	        gl.glMatrixMode(GL2.GL_TEXTURE);
	        gl.glPushMatrix();
	        gl.glTranslated(0.5, 0.5, 0);
	        gl.glScaled(1, -1, 1);
	        gl.glTranslated(-0.5, -0.5, 0);
	        
	        glu.gluQuadricTexture(top, true);
	        glu.gluQuadricNormals(top, GLU.GLU_SMOOTH);
	        glu.gluDisk(top, 0, radius, 36, 6);
	        glu.gluDeleteQuadric(top);
	        
	        gl.glPopMatrix();
	        gl.glMatrixMode(GL2.GL_MODELVIEW);
	        
	        gl.glPopMatrix();
	        
	        // draw the bottom face of the cylinder with texture coordinates
	        
	        gl.glMatrixMode(GL2.GL_TEXTURE);
	        gl.glPushMatrix();
	        gl.glTranslated(0.5, 0.5, 0);
	        gl.glScaled(-1, 1, 1);
	        gl.glTranslated(-0.5, -0.5, 0);
	        
	        glu.gluQuadricTexture(bottom, true);
	        glu.gluQuadricOrientation(bottom, GLU.GLU_INSIDE);
	        glu.gluQuadricNormals(bottom, GLU.GLU_SMOOTH);
	        glu.gluDisk(bottom, 0, radius, 36, 6);
	        glu.gluDeleteQuadric(bottom);
	        
	        gl.glPopMatrix();
	        gl.glMatrixMode(GL2.GL_MODELVIEW);
	        
		  	// Disable texture support
	        texture.disable();
		  	
	      } else {
			
	    	// Set the drawing color
	  	    gl.glColor4dv(fill.getColor(), 0);
	  	    
	  	    // Set the material according to the fill color
	  	    //setMaterial(gl, fill.getColorFloat());
	  	    
	  	    // draw the side face of the cylinder
	  	    glu.gluQuadricNormals(side, GLU.GLU_SMOOTH);
	        glu.gluCylinder(side, radius, radius, height, 36, 6);
	        glu.gluDeleteQuadric(side);

	        // draw the top face of the cylinder
	        gl.glPushMatrix();
	        
	        gl.glTranslated(0, 0, height);
	        
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
	    }
	    
	    gl.glPopMatrix();
		
		gl.glEndList();
		
	}
}