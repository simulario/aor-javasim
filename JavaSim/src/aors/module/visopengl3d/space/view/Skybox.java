package aors.module.visopengl3d.space.view;



import aors.module.visopengl3d.utility.Color;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

import java.util.EnumMap;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * Skybox of a three dimensional space view.
 * 
 * @author Susanne Schölzel
 * @since November 26th, 2010
 * 
 */
public class Skybox {

	// String constant for the "Skybox" node
	public static final String SKYBOX = "Skybox";

	// String constants for skybox attributes
	public static final String TOP = "top";
	public static final String BOTTOM = "bottom";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";
	public static final String FRONT = "front";
	public static final String BACK = "back";
	
	// EnumMap for the texture filenames of the skybox
	protected EnumMap<Face, String> textureFilenames = new EnumMap<Face, String>(Face.class);
	
	// EnumMap for the textures of the skybox
	protected EnumMap<Face, Texture> textures = new EnumMap<Face, Texture>(Face.class);
	
	// Display list
	protected int displayList = -1;
	
	// Position of the skybox
	protected double[] position = new double[3];
	
	public void generateDisplayList(GL2 gl, GLU glu) {

		// Get a denominator for the display list
	    displayList = gl.glGenLists(1);

	    // Create the display list
	    gl.glNewList(displayList, GL2.GL_COMPILE);
		  
	    double halfWidth = 100;
	    
		// array containing the 8 vertices of the skybox
	    double[][] vertices = {
	    		{-halfWidth, -halfWidth, halfWidth},   	// Bottom front left
			  	{halfWidth, -halfWidth, halfWidth},		// Bottom front right
			  	{halfWidth, -halfWidth, -halfWidth},	// Bottom back right
			  	{-halfWidth, -halfWidth, -halfWidth},	// Bottom back left
			  	{-halfWidth, halfWidth, halfWidth},		// Top front left
			  	{halfWidth, halfWidth, halfWidth},		// Top front right
			  	{halfWidth, halfWidth, -halfWidth},		// Top back right
			  	{-halfWidth, halfWidth, -halfWidth}		// Top back left
		};
	    
	  double[][] normals = {
	      {1.0, 1.0, -1.0},
	      {-1.0, 1.0, -1.0},
	      {-1.0, 1.0, 1.0},
	      {1.0, 1.0, 1.0},
	      {1.0, -1.0, -1.0},
	      {-1.0, -1.0, -1.0},
	      {-1.0, -1.0, 1.0},
	      {1.0, -1.0, 1.0}
	  };
		  
		Class<?> faceClass = Face.class;
		// Draw all faces of the skybox as rectangles with or without texture
		for (Face face : (Face[])faceClass.getEnumConstants())  {
			// Get the texture fpr the current face
			Texture texture = getTexture(face);
			  
			// Check if the current face of the skybox will be rendered with a texture applied to it
		    if (texture != null) {
		    	  
		    	// Set the drawing color to white (because of texture)
		  	    gl.glColor4dv(Color.WHITE.getColor(), 0);
		  	    
			  	// Get texture coordinates
	            TextureCoords tc = texture.getImageTexCoords();
	            
	            // Enable texture support
		        texture.bind();
		        texture.enable();
	            
	            // draw the current face of the skybox as a rectangle with texture coordinates
		        gl.glBegin(GL2.GL_QUADS);
		        
		        switch(face) {
		        	case front:
		        		// Front Face
				        gl.glNormal3dv(normals[1], 0);
    						gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[1], 0);
    						gl.glNormal3dv(normals[0], 0);
    						gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[0], 0);
    						gl.glNormal3dv(normals[4], 0);
    						gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(vertices[4], 0);
    						gl.glNormal3dv(normals[5], 0);
    						gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(vertices[5], 0);
		        		break;
		        	case back:
		        		// Back Face
		        	  gl.glNormal3dv(normals[3], 0);
  		    			gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[3], 0);
  		    			gl.glNormal3dv(normals[2], 0);
  		    			gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[2], 0);
  		    			gl.glNormal3dv(normals[6], 0);
  		    			gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(vertices[6], 0);
  		    			gl.glNormal3dv(normals[7], 0);
  		    			gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(vertices[7], 0);
		        		break;
		        	case top:
		        		// Top Face
		        	  gl.glNormal3dv(normals[7], 0);
  		    			gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[7], 0);
  		    			gl.glNormal3dv(normals[6], 0);
  		    			gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[6], 0);
  		    			gl.glNormal3dv(normals[5], 0);
  		    			gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(vertices[5], 0);
  		    			gl.glNormal3dv(normals[4], 0);
  		    			gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(vertices[4], 0);
		        		break;
		        	case bottom:
		        		// Bottom Face
		        	  gl.glNormal3dv(normals[0], 0);
  		    			gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[0], 0);
  		    			gl.glNormal3dv(normals[1], 0);
  		    			gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[1], 0);
  		    			gl.glNormal3dv(normals[2], 0);
  		    			gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(vertices[2], 0);
  		    			gl.glNormal3dv(normals[3], 0);
  		    			gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(vertices[3], 0);
		        		break;
		        	case left:
		        		// Left Face
		        	  gl.glNormal3dv(normals[0], 0);
  		    			gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[0], 0);
  		    			gl.glNormal3dv(normals[3], 0);
  		    			gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[3], 0);
  		    			gl.glNormal3dv(normals[7], 0);
  		    			gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(vertices[7], 0);
  		    			gl.glNormal3dv(normals[4], 0);
  		    			gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(vertices[4], 0);
		        		break;
		        	case right:
		        		// Right Face
		        	  gl.glNormal3dv(normals[2], 0);
  		    			gl.glTexCoord2d(tc.left(), tc.bottom()); gl.glVertex3dv(vertices[2], 0);
  		    			gl.glNormal3dv(normals[1], 0);
  		    			gl.glTexCoord2d(tc.right(), tc.bottom()); gl.glVertex3dv(vertices[1], 0);
  		    			gl.glNormal3dv(normals[5], 0);
  		    			gl.glTexCoord2d(tc.right(), tc.top()); gl.glVertex3dv(vertices[5], 0);
  		    			gl.glNormal3dv(normals[6], 0);
  		    			gl.glTexCoord2d(tc.left(), tc.top()); gl.glVertex3dv(vertices[6], 0);
		        		break;
		        }
		        
		        gl.glEnd();

			  	// Disable texture support
		        texture.disable();
		        
		    } /*else {
		    	
		    	// Set the drawing color to white
		    	gl.glColor4dv(Color.LIGHTBLUE.getColor(), 0);
		        
		        // draw the current face of the skybox as a rectangle
			    gl.glBegin(GL2.GL_QUADS);
			        
			    switch(face) {
			      	case front:
			      		// Front Face
			      		gl.glNormal3d(0, 0, -1);
						gl.glVertex3dv(vertices[1], 0);
						gl.glVertex3dv(vertices[0], 0);
						gl.glVertex3dv(vertices[4], 0);
						gl.glVertex3dv(vertices[5], 0);
			        	break;
			        case back:
			        	// Back Face
			    		gl.glNormal3d(0, 0, 1);
			    		gl.glVertex3dv(vertices[3], 0);
			    		gl.glVertex3dv(vertices[2], 0);
			    		gl.glVertex3dv(vertices[6], 0);
			    		gl.glVertex3dv(vertices[7], 0);
			        	break;
			        case top:
			        	// Top Face
			    		gl.glNormal3d(0, -1, 0);
			    		gl.glVertex3dv(vertices[7], 0);
			    		gl.glVertex3dv(vertices[6], 0);
			    		gl.glVertex3dv(vertices[5], 0);
			    		gl.glVertex3dv(vertices[4], 0);
			        	break;
			        case bottom:
			        	// Bottom Face
			        	gl.glNormal3d(0, 1, 0);
			    		gl.glVertex3dv(vertices[0], 0);
			    		gl.glVertex3dv(vertices[1], 0);
			    		gl.glVertex3dv(vertices[2], 0);
			    		gl.glVertex3dv(vertices[3], 0);
			        	break;
			        case left:
			        	// Left Face
			    		gl.glNormal3d(1, 0, 0);
			    		gl.glVertex3dv(vertices[0], 0);
			    		gl.glVertex3dv(vertices[3], 0);
			    		gl.glVertex3dv(vertices[7], 0);
			    		gl.glVertex3dv(vertices[4], 0);
			        	break;
			        case right:
			        	// Right Face
			    		gl.glNormal3d(-1, 0, 0);
			    		gl.glVertex3dv(vertices[2], 0);
			    		gl.glVertex3dv(vertices[1], 0);
			    		gl.glVertex3dv(vertices[5], 0);
			    		gl.glVertex3dv(vertices[6], 0);
			        	break;
			    }
			        
			    gl.glEnd();
		    }*/
	    }
	    
	    gl.glEndList();
	}
	
	
	/**
	 * Displays the skybox.
	 * 
	 * @param gl
	 *          OpenGL pipeline object.
	 * @param glu
	 *          OpenGL utility library object.
	 */
	public void display(GL2 gl, GLU glu) {
		if (displayList != -1) {
			gl.glDisable(GL2.GL_DEPTH_TEST);
			gl.glDisable(GL2.GL_LIGHTING);
			gl.glDisable(GL2.GL_LIGHT0);
			gl.glPushMatrix();
		    gl.glLoadIdentity();
			gl.glTranslated(position[0], position[1], position[2]);
			gl.glCallList(displayList);
			gl.glPopMatrix();
			gl.glEnable(GL2.GL_LIGHT0);
			gl.glEnable(GL2.GL_LIGHTING);
			gl.glEnable(GL2.GL_DEPTH_TEST);
		}
	}
	
	public String getTextureFilename(Face face) {
		return textureFilenames.get(face);
	}

	public void setTextureFilename(Face face, String textureFilename) {
		this.textureFilenames.put(face, textureFilename);
	}
	
	public Texture getTexture(Face face) {
		return textures.get(face);
	}

	public void setTexture(Face face, Texture texture) {
		this.textures.put(face, texture);
	}
	
	public double[] getPosition() {
		return position;
	}
	
	public void setPosition(double[] position) {
		this.position = position;
	}
	
}
