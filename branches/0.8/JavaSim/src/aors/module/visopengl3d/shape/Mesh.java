package aors.module.visopengl3d.shape;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * The Mesh class is representing a complex geometric mesh.
 * 
 * @author Susanne Schölzel
 * @since November 25th, 2010
 * 
 */
public class Mesh extends Shape3D {

	/**
	 * Creates a new Mesh instance and initializes its members.
	 */
	public Mesh() {
		type = ShapeType.Mesh;
	}
	
	@Override
	public void generateDisplayList(GL2 gl, GLU glu) {
		
	}
}
