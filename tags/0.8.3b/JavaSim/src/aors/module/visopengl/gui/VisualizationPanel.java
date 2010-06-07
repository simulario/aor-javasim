package aors.module.visopengl.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

  // Visualization thread
  private VisualizationThread visThread;

  // Frame rate label
  private final JLabel frameRate = new JLabel("FPS: 0");

  // Simulation step label
  private final JLabel simStep = new JLabel("Step: 0");

  // Buffer size label
  private final JLabel bufferSize = new JLabel("Buffer: 0");

  // Object ID label
  private final JLabel objectID = new JLabel("Object: none");

  // Check box enabling the drawing of all simulation steps
  private final JCheckBox drawAllSteps = new JCheckBox("Draw each step", true);

  private final JCheckBox enableVisThread = new JCheckBox(
      "Enable visualization", true);

  // keep the intelli buffer optimization state
  private boolean intellBufferOptimizationActive = false;
  private final JCheckBox intelliBufferOptimization = new JCheckBox(
      "Intelli Buffer Optimization", intellBufferOptimizationActive);

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
    bufferSize.setPreferredSize(new Dimension(100, 25));
    objectID.setPreferredSize(new Dimension(100, 25));
    drawAllSteps.setPreferredSize(new Dimension(125, 25));

    // Don't draw a focus border around the check box if it is selected
    drawAllSteps.setFocusPainted(false);
    enableVisThread.setFocusPainted(false);

    this.intelliBufferOptimization
        .setToolTipText("Try to keep the buffer low to optimize memory loadings! "
            + "Keep this box enabled if you are not sure what to do with it!");
    this.intelliBufferOptimization.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        intellBufferOptimizationActive = !intellBufferOptimizationActive;
      }
    });

    // Add components to the panel
    add(frameRate);
    add(simStep);
    add(bufferSize);
    add(objectID);
    add(drawAllSteps);
    add(enableVisThread);
    add(intelliBufferOptimization);
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
   * Updates the text of the buffer size label.
   * 
   * @param size
   *          Size of the step buffer.
   */
  public void updateBufferSizeLabel(int size) {
    bufferSize.setText("Buffer: " + size);
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

  public Boolean drawAllSteps() {
    return drawAllSteps.isSelected();
  }

  public JCheckBox getEnableVisThread() {
    return enableVisThread;
  }

  public void setEnabledOnOffFeature(boolean enable) {
    this.enableVisThread.setEnabled(enable);
  }

  public VisualizationThread getVisThread() {
    return visThread;
  }

  public void setVisThread(VisualizationThread visThread) {
    this.visThread = visThread;
  }

  /**
   * Gets the intelli buffer optimization option state.
   * 
   * @return true if the option is active, false otherwise
   */
  public boolean isIntelliBufferOptionActiveState() {
    return this.intellBufferOptimizationActive;
  }

}
