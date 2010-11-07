package aors.module.visopengl3d.gui;

import java.awt.BorderLayout;

import javax.media.opengl.awt.GLCanvas;
import javax.swing.JPanel;

import aors.module.visopengl3d.engine.Engine;

/**
 * Panel containing the OpenGL canvas.
 * 
 * @author Sebastian Mucha
 * @since March 1st, 2010
 * 
 */
public class DrawingPanel extends JPanel {

  private static final long serialVersionUID = -9003948345680612054L;

  // OpenGL canvas
  private GLCanvas canvas = new GLCanvas();

  // Rendering engine
  private Engine engine = new Engine();

  /**
   * Creates a panel that contains an OpenGL canvas.
   */
  public DrawingPanel() {
    setLayout(new BorderLayout());

    /*
     * The keybord event handler needs a reference to the canvas in order to
     * make it possible to scroll before the simulation starts.
     */
    engine.getKeyboardEvtHandler().setCanvas(canvas);

    // Associate the engine with the canvas
    canvas.addGLEventListener(engine);
    canvas.addKeyListener(engine.getKeyboardEvtHandler());
    canvas.addMouseListener(engine.getMouseEvtHandler());
    canvas.addMouseMotionListener(engine.getMouseEvtHandler());

    add(BorderLayout.CENTER, canvas);

    // Prevent flickering when the application window is resized
    System.setProperty("sun.awt.noerasebackground", "true");
  }

  public GLCanvas getCanvas() {
    return canvas;
  }

  public Engine getEngine() {
    return engine;
  }

}
