package aors.module.visopengl3d.test.gui;

import java.awt.BorderLayout;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.JPanel;

import aors.module.visopengl3d.test.engine.RenderEngine2D;
import aors.module.visopengl3d.test.utility.Timer;

/**
 * This is a JPanel that contains an OpenGL canvas. It implements the Runnable
 * interface in order to redraw the canvas frequently.
 * 
 * @author Sebastian Mucha
 * @since February 16th, 2010
 */
class DrawingPanel extends JPanel implements Runnable {

  private static final long serialVersionUID = -9151652278828755010L;

  // OpenGL canvas
  private final GLCanvas canvas = new GLCanvas();

  // Rendering engine that is associated to the canvas
  private final RenderEngine2D engine = new RenderEngine2D();

  // Reference to the applications status panel
  private StatusPanel statusPanel;

  // Frame timer
  private final Timer frameTimer = new Timer();

  // Update timer (used to control when the frame rate will be updated)
  private final Timer updateTimer = new Timer();

  // Maximal frame rate in milliseconds
  private final int MAX_FRAME_RATE = 1000 / 100;

  /**
   * Creates an instance of the DrawingPanel class. Adds the OpenGL canvas to
   * the panel and set up some system properties.
   */
  public DrawingPanel() {

    // Add the canvas to the panel
    setLayout(new BorderLayout());
    add(BorderLayout.CENTER, canvas);

    // Prevent flickering when the application window is resized
    System.setProperty("sun.awt.noerasebackground", "true");
  }

  @Override
  public void run() {

    // Associate the rendering engine with the OpenGL canvas
    canvas.addGLEventListener(engine);

    // Add mouse and keyboard listeners to the canvas
    canvas.addMouseMotionListener(engine);
    canvas.addMouseListener(engine);
    canvas.addKeyListener(engine);

    // Request the input focus
    canvas.requestFocus();

    // Frame counter
    int fps = 0;

    // Start the update timer
    updateTimer.start();

    while (true) {
      // Start the frame timer
      frameTimer.start();

      // One frame will be displayed, increase the frame counter
      fps++;

      // Redraw the canvas
      canvas.display();

      // Update the frame rate label
      if (updateTimer.getTime() > 1000) {
        statusPanel.updateFrameRate(fps);

        fps = 0;
        updateTimer.start();
      }

      // Cap the frame rate
      long elapsedTime = frameTimer.getTime();

      if (elapsedTime < MAX_FRAME_RATE) {
        try {
          Thread.sleep(MAX_FRAME_RATE - elapsedTime);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  // Setter & Getter -----------------------------------------------------------

  public void setStatusPanel(StatusPanel statusPanel) {
    this.statusPanel = statusPanel;

    // Hand over a reference of the status panel to the rendering engine
    engine.setStatusPanel(statusPanel);
  }

}
