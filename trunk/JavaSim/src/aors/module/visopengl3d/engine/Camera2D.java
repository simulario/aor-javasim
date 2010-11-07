package aors.module.visopengl3d.engine;

import javax.media.opengl.GL2;

/**
 * This class is providing a simple two dimensional camera model. It offers the
 * possibility to scroll through a OpenGL rendered scene.
 * 
 * @author Sebastian Mucha
 * @since March 1st, 2010
 * 
 */
public class Camera2D {

  // Camera coordinates
  private double x, y;

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
}
