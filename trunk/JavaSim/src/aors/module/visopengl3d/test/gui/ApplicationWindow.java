package aors.module.visopengl3d.test.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;

/**
 * The ApplicationWindow class is representing the main application window. It
 * contains all other GUI components.
 * 
 * @author Sebastian Mucha
 * @since February 16th, 2010
 */
public class ApplicationWindow extends JFrame {

  private static final long serialVersionUID = 486727950581579019L;

  // Panel containing the OpenGL canvas
  private final DrawingPanel drawingPanel = new DrawingPanel();

  // Status panel displaying information about the application
  private final StatusPanel statusPanel = new StatusPanel();

  /**
   * Creates and sets up the main application window. All other GUI components
   * are added to the window here.
   * 
   * @param title
   *          the title of the window that appears in the title bar
   */
  public ApplicationWindow(String title) {

    // Set window properties
    setTitle(title);
    setSize(800, 600);
    setLayout(new BorderLayout());
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Add components to the window
    add(BorderLayout.CENTER, drawingPanel);
    add(BorderLayout.SOUTH, statusPanel);

    // Hand over a reference of the status panel to the drawing panel
    drawingPanel.setStatusPanel(statusPanel);

    // Display the window
    setVisible(true);
  }

  // Setter & Getter -----------------------------------------------------------

  public DrawingPanel getDrawingPanel() {
    return drawingPanel;
  }

}
