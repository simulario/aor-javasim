package aors.module.visopengl3d.utility;

import javax.media.opengl.GL2;

/**
 * The Camera2D class is implementing a simple two dimensional camera concept.
 * It allows to scroll through a scene in horizontal and vertical direction,
 * rotate the scene and zoom in and out of the scene.
 * 
 * @author Sebastian Mucha
 * @since February 17th, 2010
 */
public class Camera2D {

  // Camera coordinates
  private double x, y;

  // Camera rotation angle
  private double zRot;

  // Camera scale (zoom) factor
  private double scale = 1;

  /*
   * Flags indicating if the camera is supposed to scroll in the specified
   * direction
   */
  private boolean scrollLeft, scrollRight;
  private boolean scrollUp, scrollDown;

  /*
   * Flags indicating if the camera is supposed to rotate in the specified
   * direction
   */
  private boolean rotateClockwise, rotateCounterclockwise;

  // Scroll speed in pixels per second
  private final double SCROLL_SPEED = 125;

  // Rotation speed in degrees per second
  private final double ROTATION_SPEED = 25;

  /*
   * Camera timer used to make the camera move independently from the frame rate
   * at a constant speed. It measures the elapsed time between two frames.
   */
  private final Timer deltaTime = new Timer();

  // Elapsed time between two frames
  private long elapsedTime;

  /**
   * Create a new Camera2D instance and initialize the camera timer for frame
   * rate independent movement. This should be called during the rendering
   * initialization.
   */
  public Camera2D() {
    deltaTime.start();
  }

  /**
   * Measures the elapsed time between the rendering of two frames. Make sure to
   * call this method before applying any camera operation.
   */
  public void getElapsedTime() {
    elapsedTime = deltaTime.getTime();

    // Restart the camera timer
    deltaTime.start();
  }

  /**
   * Reset the camera to its default position.
   */
  public void reset() {

    // Reset camera coordinates
    x = y = 0;

    // Reset rotation angle
    zRot = 0;

    // Reset scale factor
    scale = 1;
  }

  /**
   * Scroll the camera either horizontally or vertically.
   * 
   * @param gl
   *          the OpenGL pipeline object
   */
  public void scroll(GL2 gl) {
    // Scroll right
    if (scrollRight == true)
      x -= SCROLL_SPEED * (elapsedTime / 1000.0);

    // Scroll left
    if (scrollLeft == true)
      x += SCROLL_SPEED * (elapsedTime / 1000.0);

    // Scroll up
    if (scrollUp == true)
      y -= SCROLL_SPEED * (elapsedTime / 1000.0);

    // Scroll down
    if (scrollDown == true)
      y += SCROLL_SPEED * (elapsedTime / 1000.0);

    // Apply the camera translation
    gl.glTranslated(x, y, 0);
  }

  /**
   * Rotates the camera either clockwise or counterclockwise. Rotating the
   * camera is the same as rotating the whole scene.
   * 
   * @param gl
   *          the OpenGL pipeline object
   */
  public void rotate(GL2 gl) {
    // Rotate clockwise
    if (rotateClockwise == true)
      zRot -= ROTATION_SPEED * (elapsedTime / 1000.0);

    // Rotate counterclockwise
    if (rotateCounterclockwise == true)
      zRot += ROTATION_SPEED * (elapsedTime / 1000.0);

    // Apply the camera rotation
    gl.glRotated(zRot, 0, 0, 1);
  }

  /**
   * Zooms the camera either into the scene or away. Zooming in a 2D scene means
   * to scale each element of the scene.
   * 
   * @param gl
   *          the OpenGL pipeline object
   */
  public void zoom(GL2 gl) {
    // Apply the camera zoom
    gl.glScaled(scale, scale, 0);
  }

  /**
   * Increases the scale factor for camera zooming.
   */
  public void zoomIn() {
    scale = scale * 1.25;
  }

  /**
   * Decreases the scale factor for camera zooming.
   */
  public void zoomOut() {
    scale = scale / 1.25;
  }

  // Setter & Getter -----------------------------------------------------------

  public void scrollLeft(boolean status) {
    scrollLeft = status;
  }

  public void scrollRight(boolean status) {
    scrollRight = status;
  }

  public void scrollUp(boolean status) {
    scrollUp = status;
  }

  public void scrollDown(boolean status) {
    scrollDown = status;
  }

  public void rotateClockwise(boolean status) {
    rotateClockwise = status;
  }

  public void rotateCounterclockwise(boolean status) {
    rotateCounterclockwise = status;
  }

}
