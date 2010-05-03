package aors.gui.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;

public class ToolBarSimulation extends ToolBar {

  public static final long serialVersionUID = 1324342341313L;

  public static final String SIMULATION_STEP_SPINNER = "SIMULATION_STEP_SPINNER";
  public static final String STEP_TIME_SPINNER = "STEP_TIME_SPINNER";
  public static final String SIMULATION_ITERATIONS_SPINNER = "SIMULATION_ITERATIONS_SPINNER";

  private final JFrame parent;
  private SpinnerNumberModel stepTimeSpinnerNumberModel;
  private SpinnerNumberModel simulationStepsSpinnerModel;
  private SpinnerNumberModel simulationIterationsSpinnerModel;

  public ToolBarSimulation(JFrame parent) {
    super((ActionListener) parent);
    this.parent = parent;

    this.setName(Menu.SIMULATION);

    this.setLayout(new FlowLayout(FlowLayout.LEFT));
    this.addButton(Menu.Item.RUN, Menu.Item.RUN_IMAGE);
    this.addButton(Menu.Item.STOP, Menu.Item.STOP_IMAGE);

    JPanel simulationIterationsPanel = getSimulationIterationsSpinner();

    this.add(simulationIterationsPanel);
    this.add(getSimulationStepsSpinner());
    this.add(getStepTimeSpinner());

  }

  private JPanel getSimulationStepsSpinner() {
    JPanel panel = new JPanel();

    simulationStepsSpinnerModel = new SpinnerNumberModel(0, 0,
        Integer.MAX_VALUE, 1);

    // panel.setBorder(BorderFactory.createBevelBorder(1));
    JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
    separator.setPreferredSize(new Dimension(2, 20));

    JSpinner jSpinner = new JSpinner();
    jSpinner.setModel(simulationStepsSpinnerModel);
    jSpinner.setName(SIMULATION_STEP_SPINNER);
    jSpinner.setPreferredSize(new Dimension(75, 20));
    jSpinner.addChangeListener((ChangeListener) this.parent);

    panel.add(separator);
    panel.add(new JLabel("Simulation Steps:"));
    panel.add(jSpinner);

    return panel;
  }

  /**
   * Enable/disable the simulation toolbar components
   * 
   * @param enabledState
   *          true - to enable; false to disable;
   */
  public void enableSimulationToolBarEditableComponents(boolean enabledState) {
    Component[] panels = this.getComponents();
    int panelsNumber = panels.length;

    for (int i = 0; i < panelsNumber; i++) {
      if (panels[i] instanceof JPanel) {
        Component[] components = ((JPanel) panels[i]).getComponents();
        int componentsNumber = components.length;

        for (int j = 0; j < componentsNumber; j++) {
          if (components[j] instanceof JSpinner) {
            // the step timer delay is not influenced by this method
            if (!STEP_TIME_SPINNER.equals(components[j].getName())) {
              components[j].setEnabled(enabledState);
            }
          }
        }
      }
    }
  }

  /**
   * Set the number of the simulation iterations that is displayed in the
   * spinner box.
   * 
   * @param iterationsNumber
   *          the value (number) of the iterations number
   */
  protected void setSimulationIterationsNumber(int iterationsNumber) {
    this.simulationIterationsSpinnerModel.setValue(iterationsNumber);
  }

  /**
   * Create and return the small panel that allows to set the number of
   * simulation iterations.
   * 
   * @return the pannel for setting the number of simulation iterations
   */
  private JPanel getSimulationIterationsSpinner() {
    JPanel panel = new JPanel();

    // allowed range 1 - MAX_INTEGER
    this.simulationIterationsSpinnerModel = new SpinnerNumberModel(1, 1,
        Integer.MAX_VALUE, 1);

    // panel.setBorder(BorderFactory.createBevelBorder(1));
    JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
    separator.setPreferredSize(new Dimension(2, 20));

    JSpinner jSpinner = new JSpinner();
    jSpinner.setModel(this.simulationIterationsSpinnerModel);
    jSpinner.setName(SIMULATION_ITERATIONS_SPINNER);
    jSpinner.addChangeListener((ChangeListener) this.parent);
    jSpinner.setPreferredSize(new Dimension(75, 20));

    panel.add(separator);
    panel.add(new JLabel("Simulation iterations:"));
    panel.add(jSpinner);

    return panel;

  }

  public void setSimulationSteps(int value) {
    this.simulationStepsSpinnerModel.setValue(value);
  }

  private JPanel getStepTimeSpinner() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

    stepTimeSpinnerNumberModel = new SpinnerNumberModel(0, 0,
        Integer.MAX_VALUE, 1);

    JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
    separator.setPreferredSize(new Dimension(2, 20));

    // panel.setBorder(BorderFactory.createBevelBorder(1));

    JSpinner jSpinner = new JSpinner();
    jSpinner.setModel(stepTimeSpinnerNumberModel);
    jSpinner.setName(STEP_TIME_SPINNER);
    jSpinner.setPreferredSize(new Dimension(50, 20));
    jSpinner.addChangeListener((ChangeListener) this.parent);

    panel.add(separator);
    panel.add(new JLabel("Step Time Delay:"));
    panel.add(jSpinner);
    panel.add(new JLabel("ms"));

    return panel;
  }

  public void setStepTime(int value) {
    this.stepTimeSpinnerNumberModel.setValue(value);
  }

  public int getStepTime() {
    return (Integer) this.stepTimeSpinnerNumberModel.getValue();
  }

}
