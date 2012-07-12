package aors.module.visopengl3d.shape;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import aors.module.visopengl3d.utility.Color;

/**
 * The Sphere class is representing a simple geometric sphere.
 * 
 * @author Susanne Schölzel
 * @since November 25th, 2010
 * 
 */
public class Sphere extends Shape3D {

	/**
	 * Creates a new Sphere instance and initializes its members.
	 */
	public Sphere() {
		type = ShapeType.Sphere;
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
    
    // rotate the sphere, so that the top points in direction of the positive y-axis
    gl.glRotated(-90, 1, 0, 0);
    
    // radius of the sphere
    double radius = width / 2;
    
    // Don't draw anything if the dimensions are too small
    if (radius > 0) {
	      
      // GLUquadric object for the sphere
      GLUquadric sphere = glu.gluNewQuadric();
    	
      // Check if the sphere will be rendered with a texture applied to it
      if (texture != null) {
	    	  
	    	// Set the drawing color to white (because of texture)
	  	  gl.glColor4dv(Color.WHITE.getColor(), 0);
            
        // Enable texture support
        texture.bind();
        texture.enable();
          
        gl.glMatrixMode(GL2.GL_TEXTURE);
        gl.glPushMatrix();
        gl.glTranslated(0.5, 0.5, 0);
        gl.glScaled(1, -1, 1);
        gl.glTranslated(-0.5, -0.5, 0);
        
        // draw the sphere with texture coordinates
        glu.gluQuadricTexture(sphere, true);
        glu.gluQuadricNormals(sphere, GLU.GLU_SMOOTH);
        glu.gluSphere(sphere, radius, 36, 36);
        glu.gluDeleteQuadric(sphere);
        
        gl.glPopMatrix();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
	        
		  	// Disable texture support
	      texture.disable();
		  	
	    } else {
			
	    	// Set the drawing color
	  	  gl.glColor4dv(fill.getColor(), 0);
	    	  
  	    // draw the sphere
  	    glu.gluQuadricNormals(sphere, GLU.GLU_SMOOTH);
        glu.gluSphere(sphere, radius, 36, 36);
        glu.gluDeleteQuadric(sphere);
    	  
      }
    }
	    
	  gl.glPopMatrix();
		
		gl.glEndList();
		
	}
}
