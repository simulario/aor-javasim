package aors.module.visopengl3d.engine;

import aors.module.visopengl3d.engine.Camera2D;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.awt.GLCanvas;

/**
 * Event handler for keyboard events.
 * 
 * @author Sebastian Mucha, Susanne Schölzel
 * @since March 16th, 2010
 * 
 */
public class KeyboardEventHandler implements KeyListener {

  private Camera2D camera;

  // Canvas
  private GLCanvas canvas;

  @Override
  public void keyPressed(KeyEvent e) {
    /*if (camera != null) {
      // Scroll left when the left arrow key was pressed
      if (e.getKeyCode() == KeyEvent.VK_LEFT) {
    	camera.scrollLeft(true);
        canvas.display();
      }

      // Scroll right when the right arrow key was pressed
      if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
    	camera.scrollRight(true);
        canvas.display();
      }

      // Scroll up when the up arrow key was pressed
      if (e.getKeyCode() == KeyEvent.VK_UP) {
        camera.scrollUp(true);
    	canvas.display();
      }

      // Scroll down when the down arrow key was pressed
      if (e.getKeyCode() == KeyEvent.VK_DOWN) {
        camera.scrollDown(true);
    	canvas.display();
      }

      // Reset the camera if the space key is pressed
      if (e.getKeyCode() == KeyEvent.VK_SPACE) {
        camera.reset();
        canvas.display();
      }
      
      // Rotate clockwise when the colon key was pressed
      if (e.getKeyCode() == KeyEvent.VK_PERIOD) {
        camera.rotateClockwise(true);
    	canvas.display();
      }
      
      // Rotate counterclockwise when the comma key was pressed
      if (e.getKeyCode() == KeyEvent.VK_COMMA) {
        camera.rotateCounterclockwise(true);
    	canvas.display();
      }
      
      // Zoom in when the plus key was pressed
      if (e.getKeyCode() == KeyEvent.VK_PLUS) {
        camera.zoomIn();
    	canvas.display();
      }
      
      // Zoom out when the minus key was pressed
      if (e.getKeyCode() == KeyEvent.VK_MINUS) {
        camera.zoomOut();
    	canvas.display();
      }
    }*/
  }

  @Override
  public void keyReleased(KeyEvent e) {
	  // Scroll left when the left arrow key was pressed
      /*if (e.getKeyCode() == KeyEvent.VK_LEFT) {
    	camera.scrollLeft(false);
        canvas.display();
      }

      // Scroll right when the right arrow key was pressed
      if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
    	camera.scrollRight(false);
        canvas.display();
      }

      // Scroll up when the up arrow key was pressed
      if (e.getKeyCode() == KeyEvent.VK_UP) {
        camera.scrollUp(false);
    	canvas.display();
      }

      // Scroll down when the down arrow key was pressed
      if (e.getKeyCode() == KeyEvent.VK_DOWN) {
        camera.scrollDown(false);
    	canvas.display();
      }
      
      // Rotate clockwise when the colon key was pressed
      if (e.getKeyCode() == KeyEvent.VK_PERIOD) {
        camera.rotateClockwise(false);
    	canvas.display();
      }
      
      // Rotate counterclockwise when the comma key was pressed
      if (e.getKeyCode() == KeyEvent.VK_COMMA) {
        camera.rotateCounterclockwise(false);
    	canvas.display();
      }*/
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
