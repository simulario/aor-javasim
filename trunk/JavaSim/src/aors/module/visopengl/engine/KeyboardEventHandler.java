package aors.module.visopengl.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.awt.GLCanvas;

/**
 * Event handler for keyboard events.
 * 
 * @author Sebastian Mucha
 * @since March 16th, 2010
 * 
 */
public class KeyboardEventHandler implements KeyListener {

  private Camera2D camera;

  // Canvas
  private GLCanvas canvas;

  @Override
  public void keyPressed(KeyEvent e) {
    if (camera != null) {
      // Scroll left when the left arrow key was pressed
      if (e.getKeyCode() == KeyEvent.VK_LEFT) {
        camera.setX(camera.getX() + camera.getSCROLL_SPEED());
        canvas.display();
      }

      // Scroll right when the right arrow key was pressed
      if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
        camera.setX(camera.getX() - camera.getSCROLL_SPEED());
        canvas.display();
      }

      // Scroll up when the up arrow key was pressed
      if (e.getKeyCode() == KeyEvent.VK_UP) {
        camera.setY(camera.getY() - camera.getSCROLL_SPEED());
        canvas.display();
      }

      // Scroll down when the down arrow key was pressed
      if (e.getKeyCode() == KeyEvent.VK_DOWN) {
        camera.setY(camera.getY() + camera.getSCROLL_SPEED());
        canvas.display();
      }

      // Reset the camera if the space key is pressed
      if (e.getKeyCode() == KeyEvent.VK_SPACE) {
        camera.reset();
        canvas.display();
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  public Camera2D getCamera() {
    return camera;
  }

  public void setCamera(Camera2D camera) {
    this.camera = camera;
  }

  public GLCanvas getCanvas() {
    return canvas;
  }

  public void setCanvas(GLCanvas canvas) {
    this.canvas = canvas;
  }

}
