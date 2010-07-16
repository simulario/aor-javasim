package aors.module.visopengl.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

/**
 * Panel containing information and settings affecting the visualization.
 * 
 * @author Sebastian Mucha
 * @since March 1st, 2010
 * 
 */
public class VisualizationPanel extends JPanel {

  private static final long serialVersionUID = -8747483004958627070L;

  // Frame rate label
  private final JLabel frameRate = new JLabel("FPS: 0");

  // Simulation step label
  private final JLabel simStep = new JLabel("Step: 0");

  // Object ID label
  private final JLabel objectID = new JLabel("Object: none");

  private final JCheckBox enableVisThread = new JCheckBox(
      "Enable visualization", true);

  /**
   * Creates a panel containing information and settings affecting the
   * visualization.
   */
  public VisualizationPanel() {
    setLayout(new FlowLayout(FlowLayout.LEADING, 20, 0));
    this.setBorder(new EtchedBorder());

    // Set component dimensions
    frameRate.setPreferredSize(new Dimension(100, 25));
    simStep.setPreferredSize(new Dimension(100, 25));
    objectID.setPreferredSize(new Dimension(100, 25));

    // Don't draw a focus border around the check box if it is selected
    enableVisThread.setFocusPainted(false);

    // Add components to the panel
    add(frameRate);
    add(simStep);
    add(objectID);
    add(enableVisThread);
  }

  /**
   * Updates the text of the frame rate label.
   * 
   * @param fps
   *          Current frame rate.
   */
  public void updateFrameRateLabel(int fps) {
    frameRate.setText("FPS: " + fps);
  }

  /**
   * Updates the text of the simulation step label.
   * 
   * @param step
   *          Step that is currently displayed.
   */
  public void updateSimStepLabel(long step) {
    simStep.setText("Step: " + step);
  }

  /**
   * Updates the text of the object ID label.
   * 
   * @param id
   *          ID of a selected object.
   */
  public void updateObjectIDLabel(int id) {
    objectID.setText("Object: " + id);
  }

  public JCheckBox getEnableVisThread() {
    return enableVisThread;
  }

  public void setEnabledOnOffFeature(boolean enable) {
    this.enableVisThread.setEnabled(enable);
  }
}