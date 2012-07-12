package aors.module.visopengl3d.shape;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import aors.module.visopengl3d.utility.Color;


/**
 * The Cone class is representing a simple geometric cone.
 * 
 * @author Susanne Schölzel
 * @since November 25th, 2010
 * 
 */
public class Cone extends Shape3D {

	/**
	 * Creates a new Cone instance and initializes its members.
	 */
	public Cone() {
		type = ShapeType.Cone;
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
	    
	    // rotate and translate the cone, so that the top points in direction of the positive y-axis
	    // and one half of the cone is on the positive y-axis and the other half is on the negative y-axis
	    gl.glTranslated(0, -height/2, 0);
	    gl.glRotated(-90, 1, 0, 0);
	    
	    // radius of the cone
	    double radius = width / 2;
	    
	    // Don't draw anything if the dimensions are too small
	    if (height > 0 && radius > 0) {
	      
	      // GLUquadric objects for the side face and the bottom face of the cone
	      GLUquadric side = glu.gluNewQuadric();
	      GLUquadric bottom = glu.gluNewQuadric();
	    	
	      // Check if the cone will be rendered with a texture applied to it
	      if (texture != null) {
	    	  
	    	// Set the drawing color to white (because of texture)
	  	    gl.glColor4dv(Color.WHITE.getColor(), 0);
            
          // Enable texture support
	        texture.bind();
	        texture.enable();
            
	        gl.glMatrixMode(GL2.GL_TEXTURE);
	        gl.glPushMatrix();
	        gl.glTranslated(0.5, 0.5, 0);
	        gl.glScaled(-1, -1, 1);
	        gl.glTranslated(-0.5, -0.5, 0);
	        
	        // draw the side face of the cone with texture coordinates
	        glu.gluQuadricTexture(side, true);
	        glu.gluQuadricNormals(side, GLU.GLU_SMOOTH);
	        glu.gluCylinder(side, radius, 0, height, 36, 6);
	        glu.gluDeleteQuadric(side);
	        
	        gl.glPopMatrix();

	        gl.glPushMatrix();
	        gl.glTranslated(0.5, 0.5, 0);
	        gl.glScaled(-1, 1, 1);
	        gl.glTranslated(-0.5, -0.5, 0);
	        
	        // draw the bottom face of the cone with texture coordinates
	        glu.gluQuadricOrientation(bottom, GLU.GLU_INSIDE);
	        glu.gluQuadricTexture(bottom, true);
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
	  	    
	  	    // draw the side face of the cone
	  	    glu.gluQuadricNormals(side, GLU.GLU_SMOOTH);
	        glu.gluCylinder(side, radius, 0, height, 36, 6);
	        glu.gluDeleteQuadric(side);

	        // draw the bottom face of the cone
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
