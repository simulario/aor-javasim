package aors.module.visopengl.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

/**
 * Panel containing all GUI elements.
 * 
 * @author Sebastian Mucha
 * @since March 1st, 2010
 * 
 */
public class ContentPanel extends JPanel {

  private static final long serialVersionUID = -1613942230985799337L;

  // Panel containing the OpenGL canvas
  private DrawingPanel drawingPanel = new DrawingPanel();

  // Panel containing visualization information and settings
  private VisualizationPanel visPanel = new VisualizationPanel();

  // Panel containing information about the space model
  private SpaceModelPanel spacePanel = new SpaceModelPanel();

  // the panel containing simulation description
  private DescriptionPanel descriptionPanel = new DescriptionPanel();

  /*
   * Creates the GUI's panel that contains all other GUI elements.
   */
  public ContentPanel() {
    setLayout(new BorderLayout());
    setPreferredSize(new Dimension(500, 500));

    // Add GUI elements
    add(BorderLayout.WEST, descriptionPanel);
    add(BorderLayout.NORTH, visPanel);
    add(BorderLayout.CENTER, drawingPanel);
    add(BorderLayout.SOUTH, spacePanel);
  }

  public DrawingPanel getDrawingPanel() {
    return drawingPanel;
  }

  public VisualizationPanel getVisualizationPanel() {
    return visPanel;
  }

  public SpaceModelPanel getSpaceModelPanel() {
    return spacePanel;
  }

  /**
   * Set the description panel content. This may be simple text or HTML. Note
   * that the HTML will be interpreted by the Swing library, therefore it may be
   * the case that not all HTML elements are interpreted or correct displayed.
   * 
   * @param description
   *          the text/html to be displayed
   */
  public void setDescriptionData(String description) {
    if (description == null || description.trim().length() < 1) {
      this.remove(descriptionPanel);
      this.repaint();
      this.updateUI();
      return;
    }
    add(BorderLayout.WEST, descriptionPanel);
    this.descriptionPanel.setDescriptionData(description);
  }

  /**
   * This method refresh the GUI. That implies updating all language dependent
   * messages/labels used.
   */
  public void refreshGUI() {
    this.spacePanel.refreshGUI();
    this.visPanel.refreshGUI();
  }
}
