package aors.module.visopengl3d.test.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This is a JPanel that is displaying some useful information about the
 * application.
 * 
 * @author Sebastian Mucha
 * @since February 17th, 2010
 */
public class StatusPanel extends JPanel {

  private static final long serialVersionUID = -7751546713998407856L;

  // Label displaying the frame rate
  private JLabel fpsLabel = new JLabel("FPS: 0");

  // Label displaying the position of the mouse cursor
  private JLabel mouseLabel = new JLabel("Mouse: (0,0)");

  // Label displaying the ID of a selected object
  private JLabel objectLabel = new JLabel();

  /**
   * Creates and sets up the status panel and its components.
   */
  public StatusPanel() {

    // Set status panel layout
    setLayout(new FlowLayout(FlowLayout.LEADING, 2, 0));

    // Set label dimensions
    fpsLabel.setPreferredSize(new Dimension(60, 20));
    mouseLabel.setPreferredSize(new Dimension(120, 20));

    // Add the labels to the status panel
    add(fpsLabel);
    add(mouseLabel);
    add(objectLabel);
  }

  /**
   * Updates the frame rate label in the status panel.
   * 
   * @param fps
   *          the number of frames that where rendered in 1 second
   */
  public void updateFrameRate(int fps) {

    fpsLabel.setText("FPS: " + fps);
  }

  /**
   * Updates the mouse label in the status panel.
   * 
   * @param x
   *          the x position of the mouse cursor
   * @param y
   *          the y position of the mouse cursor
   */
  public void updateMousePosition(int x, int y) {

    mouseLabel.setText("Mouse: (" + x + "," + y + ")");
  }

  /**
   * Updates the object label in the status panel.
   * 
   * @param id
   *          the ID of the selected object
   */
  public void updateObjectLabel(int id) {

    objectLabel.setText("Object: " + id);
  }

}
