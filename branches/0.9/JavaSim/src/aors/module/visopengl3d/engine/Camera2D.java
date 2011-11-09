package aors.module.visopengl3d.engine;

import javax.media.opengl.GL2;

/**
 * This class is providing a simple two dimensional camera model. It offers the
 * possibility to scroll through a OpenGL rendered scene.
 * 
 * @author Sebastian Mucha, Susanne Schölzel
 * @since March 1st, 2010
 * 
 */
public class Camera2D {
	
  public static final String GLOBAL_CAMERA = "GlobalCamera";
	
  public static final String POSITION = "position";
  public static final String VIEW_VECTOR = "viewVector";
  public static final String UP_VECTOR = "upVector";

  private double[] position = new double[3];
  private double[] viewVector = new double[3];
  private double[] upVector = new double[3];
  
  // Camera coordinates
  private double x, y, z;

  // Scroll speed in pixels
  private final double SCROLL_SPEED = 30;

  /**
   * Reset the camera to its default position.
   */
  public void reset() {

    // Reset camera coordinates
    x = y = 0;
  }

  /**
   * Scroll the camera either horizontally or vertically.
   * 
   * @param gl
   *          OpenGL pipeline object.
   */
  public void scroll(GL2 gl) {
    // Apply the camera translation
    gl.glTranslated(x, y, 0);
  }

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }

  public double getSCROLL_SPEED() {
    return SCROLL_SPEED;
  }
  
  public double getZ() {
	  return 400;
  }
  
  public void setZ(double z) {
	    this.z = z;
  }
  
  public double[] getPosition() {
	  return position;
  }
  
  public void setPosition(double[] position) {
	  this.position = position;
  }
  
  public double[] getViewVector() {
	  return viewVector;
  }
  
  public void setViewVector(double[] viewVector) {
	  this.viewVector = viewVector;
  }
  
  public double[] getUpVector() {
	  return upVector;
  }
  
  public void setUpVector(double[] upVector) {
	  this.upVector = upVector;
  }
  
}
