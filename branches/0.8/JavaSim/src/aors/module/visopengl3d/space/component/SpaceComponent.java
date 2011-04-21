package aors.module.visopengl3d.space.component;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * Each space component (Track, Cell, Margin) has to implement this interface.
 * 
 * @author Sebastian Mucha
 * @since March 17th, 2010
 * 
 */
public interface SpaceComponent {

  /**
   * Displays the space component.
   * 
   * @param gl
   * @param glu
   */
  void display(GL2 gl, GLU glu);

  /**
   * Returns an objects coordinates in world coordinate system. The methods
   * parameters are in space coordinate system.
   * 
   * @param x
   * @param y
   */
  double[] getWorldCoordinates(double x, double y);

  /**
   * Returns the rotation of an object related to the space model. The methods
   * parameters are in space coordinate system.
   * 
   * @param x
   * @param y
   */
  double getRotation(double x, double y);

  /**
   * Returns the width of an object in world coordinate system. The methods
   * parameter is in space coordinate system.
   * 
   * @param width
   */
  double getObjectWidth(double width);

  /**
   * Returns the height of an object in world coordinate system. The methods
   * parameter is in space coordinate system.
   * 
   * @param height
   */
  double getObjectHeight(double height);
}
