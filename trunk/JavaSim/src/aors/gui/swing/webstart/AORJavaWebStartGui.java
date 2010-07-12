package aors.gui.swing.webstart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintStream;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import aors.controller.InitialState;
import aors.controller.SimulationDescription;
import aors.controller.webstart.WebStartSimulationManager;
import aors.data.DataBus;
import aors.data.evt.sim.ObjektInitEvent;
import aors.data.evt.sim.ObjektInitEventListener;
import aors.data.evt.sim.SimulationEvent;
import aors.data.evt.sim.SimulationEventListener;
import aors.data.evt.sim.SimulationStepEvent;
import aors.data.evt.sim.SimulationStepEventListener;
import aors.data.java.helper.AbstractSimState;
import aors.gui.swing.DialogHtml;
import aors.gui.swing.ImageLoader;
import aors.gui.swing.OutputStreamTextPane;
import aors.gui.swing.TabSimDescription;
import aors.gui.swing.ToolBarSimulation;
import aors.model.envevt.EnvironmentEvent;
import aors.module.Module;
import aors.module.evt.ModuleEvent;
import aors.module.evt.ModuleEventListener;
import aors.module.evt.ModuleEventSlowDownSimulation;
import aors.module.evt.ModuleEventSpeedUpSimulation;

/**
 * 
 * Main
 * 
 * @author Jens Werner
 * @since 08.02.2010
 * @version $Revision$
 */
public class AORJavaWebStartGui extends JFrame implements ActionListener,
    ChangeListener, SimulationEventListener, ObjektInitEventListener,
    SimulationStepEventListener, ModuleEventListener {// vlog

  private static final long serialVersionUID = -875856187880067114L;

  private Runnable simulationThread;

  private final String guiTitle = "AOR JavaWebStartSim";
  private final String tabAORSLName = "AORSL";
  private final int tabAORSLIndex = 0;

  private final String aboutHTMLFileName = "about.htm";

  private final WebStartSimulationManager webStartSimulationManager;
  private ExecutorService backgroundExecution;

  // used to redirect the System.out and System.err streams
  private PrintStream out;
  private PrintStream err;
  private PrintStream success;
  // private PrintStream warning;

  // property names for values regarding GUI settings
  private String propertySwingLookAndFeel = "GUI.SwingLookAndFeel";

  /**
   * PANES
   */
  private JTabbedPane tabPane;
  private TabSimDescription tabAORSL;

  // in the right bottom, holds e.g. the progress bar
  private JPanel statusPane;

  // private ToolBarFile toolBarFile;
  private WebStartToolBarSimulation toolBarSimulation;

  private JProgressBar progressBar;
  private JLabel agentsNumberLabel;
  private JLabel objectsNumberLabel;
  private JLabel eventsNumberLabel;

  // the label that states the number of current simulation runs
  private JLabel currentSimulationIterationsLabel;

  // the label displaying the total time of all iterations on a single run
  private JLabel totalIterationsTimeLabel;

  // the label that states the number of current simulation runs
  private JLabel lastIterationTimeLabel;

  private WebStartDialogPreferences preferences;

  private final String messageSimulationTime = "Simulation time";

  private final String messageSimulationStarted = "Simulation started.";
  private final String messageSimulationFinished = "Simulation finished.";

  public static final String CONTEXT_MENU_ITEM_PREFERENCES = "Preferences";

  private int tempSimSteps = -1;
  private int tempStepTimeDelay = -1;

  // the max iterations number - 1 by default
  private int maxSimulationIterationsNumber = 1;

  // the current simulation number - 0 in the beginning
  private int currentSimulationIterationNumber = 0;

  // total simulation iterations time
  private long totalSimulationIterationsTime = 0;

  // simulation start time
  private long simulationStartTime = 0;

  // simulation end time
  private long simulationEndTime = 0;

  // this is used to observe if the simulation was stopped by the user
  private boolean simulationStoppedByUser = false;

  /**
   * 
   * Create a new {@code MainSwing}.
   * 
   */
  public AORJavaWebStartGui() {
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    System.setSecurityManager(null);
    // register an window listener
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        if (showExitDialog() == JOptionPane.YES_OPTION) {
          e.getWindow().dispose();
          e.getWindow().setVisible(false);
          System.exit(0);
        }
      }
    });

    Locale.setDefault(Locale.ENGLISH);
    this.getContentPane().setLocale(Locale.ENGLISH);
    this.tabAORSL = new TabSimDescription(JSplitPane.VERTICAL_SPLIT, this);

    // this.setPrintStreams();
    // +++++++ new simulator instance ++++++++++++++++++++++++++++++++
    this.webStartSimulationManager = new WebStartSimulationManager();
    this.tabAORSL.getEditorPane().setText(
        this.webStartSimulationManager.getProject().getSimulationDescription());
    String scenarioTitle = this.webStartSimulationManager.getScenarioName();
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    this.preferences = new WebStartDialogPreferences(this);

    String value = this.webStartSimulationManager.getProperties().getProperty(
        this.propertySwingLookAndFeel);
    if (value != null) {
      this.setLookAndFeel(value);
    }

    // instantiate a cached thread pool for the execution of background thread
    // like generation of Java source codes or the compilation
    this.backgroundExecution = Executors.newCachedThreadPool();

    this.initListener();

    this.setLayout(new BorderLayout());
    this.setIconImage(ImageLoader.loadImage("logo.gif"));
    this.setTitle(guiTitle
        + "  -  "
        + ((scenarioTitle.length() < 40 ? scenarioTitle : scenarioTitle
            .substring(0, 30))));

    // arrange the GUI window to display
    double percent = 0.8;
    int windowWidth = (int) (this.getToolkit().getScreenSize().width * percent);
    int windowHeight = (int) (this.getToolkit().getScreenSize().height * percent);

    // set size to ?% of the screen size dimensions
    this.setSize(windowWidth, windowHeight);

    // center the window
    this.setLocation(
        (this.getToolkit().getScreenSize().width - windowWidth) / 2, (this
            .getToolkit().getScreenSize().height - windowHeight) / 2);

    /**
     * This is required in order to not interfere with other components in
     * display, such as the ones using OpenGL technologies.
     */
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);

    this.setJMenuBar(new WebStartMenu(this));

    this.statusPane = new JPanel(new BorderLayout());
    this.statusPane.setSize(200, 50);
    this.progressBar = new JProgressBar();
    this.progressBar.setStringPainted(true);

    JPanel leftStatus = new JPanel(new FlowLayout());
    JPanel centerStatus = new JPanel(new FlowLayout());
    JPanel rightStatus = new JPanel(new FlowLayout());
    this.agentsNumberLabel = new JLabel();
    this.agentsNumberLabel.setBorder(new EtchedBorder());
    this.agentsNumberLabel.setText("   Agents: 0   ");
    this.objectsNumberLabel = new JLabel();
    this.objectsNumberLabel.setBorder(new EtchedBorder());
    this.objectsNumberLabel.setText("   Objects: 0   ");

    this.currentSimulationIterationsLabel = new JLabel();
    this.updateCurrentSimulationIterationsLabel(0, 1);

    this.totalIterationsTimeLabel = new JLabel();
    this.updateTotalIterationsTimeLabel(0);

    this.lastIterationTimeLabel = new JLabel();
    this.updateLastIterationTimeLabel(0);

    this.eventsNumberLabel = new JLabel();
    this.eventsNumberLabel.setBorder(new EtchedBorder());
    this.eventsNumberLabel.setText("   Events: 0   ");

    leftStatus.add(agentsNumberLabel);
    leftStatus.add(objectsNumberLabel);
    leftStatus.add(eventsNumberLabel);

    rightStatus.add(this.totalIterationsTimeLabel);
    rightStatus.add(this.lastIterationTimeLabel);
    rightStatus.add(this.currentSimulationIterationsLabel);
    rightStatus.add(this.progressBar);

    this.statusPane.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
    this.statusPane.add(leftStatus, BorderLayout.WEST);
    this.statusPane.add(centerStatus, BorderLayout.CENTER);
    this.statusPane.add(rightStatus, BorderLayout.EAST);
    this.add(this.statusPane, BorderLayout.PAGE_END);

    // create a pane to hold all tool bars
    JPanel toolBarPane = new JPanel(new BorderLayout());
    this.toolBarSimulation = new WebStartToolBarSimulation(this);

    toolBarPane.add(this.toolBarSimulation, BorderLayout.CENTER);

    this.add(toolBarPane, BorderLayout.PAGE_START);

    // instantiate the tab pane
    this.tabPane = new JTabbedPane();

    // DEPRECATED
    // this.tabAORSL.getEditorPane().setEnabled(false);
    this.tabAORSL.setName(this.tabAORSLName);

    this.tabPane.add(this.tabAORSL, this.tabAORSLIndex);

    // create modules tabs
    for (Module module : this.webStartSimulationManager.getModules()) {
      // no GUI component defined for this module
      if (module.getGUIComponent() == null) {
        continue;
      }
      this.tabPane.add((JScrollPane) module.getGUIComponent());
    }

    this.getContentPane().add(this.tabPane);

    // enable all tabs
    for (int tabIndex = 0; tabIndex < this.tabPane.getTabCount(); tabIndex++) {
      this.tabPane.setEnabledAt(tabIndex, true);
    }
    this.setPrintStreams();
  }// constructor

  private void setPrintStreams() {

    // wrap an PrintStream around the text pane object (=> decorator pattern)
    this.out = new PrintStream(new OutputStreamTextPane(this.tabAORSL
        .getOutputTextPane(), new Color(0, 20, 0), new Font("SansSerif",
        Font.PLAIN, 12)));

    this.err = new PrintStream(new OutputStreamTextPane(this.tabAORSL
        .getOutputTextPane(), new Color(240, 0, 0), new Font("SansSerif",
        Font.BOLD | Font.ITALIC, 12)));

    this.success = new PrintStream(new OutputStreamTextPane(this.tabAORSL
        .getOutputTextPane(), new Color(0, 120, 0), new Font("SansSerif",
        Font.BOLD, 12)));

    // this.warning = new PrintStream(new OutputStreamTextPane(this.tabAORSL
    // .getOutputTextPane(), new Color(0, 0, 220), new Font("SansSerif",
    // Font.BOLD, 12)));

    // after all errors that may happen...
    // redirect the System.out and System.err streams to the text area in the
    // GUI
    System.setOut(this.out);
    System.setErr(this.err);

  }

  private void initListener() {
    // register the GUI as property change listener for events raised by the
    // logger
    webStartSimulationManager.getDataBus().addSimulationEventListener(this);
    webStartSimulationManager.getDataBus().addObjektInitEventListener(this);
    webStartSimulationManager.getDataBus().addSimulationStepEventListener(this);
    webStartSimulationManager.getDataBus().addModuleEventListener(this);
  }

  /**
   * This method tries to switch to the given Java Look and Feel after checking
   * if this is even available.
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @param nameToSet
   */
  private void setLookAndFeel(String nameToSet) {
    // for all look an feel names
    for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {

      if (laf.getName().equals(nameToSet)) {
        try {

          UIManager.setLookAndFeel(laf.getClassName());
          this.preferences.selectLookAndFeel(nameToSet);

          SwingUtilities.updateComponentTreeUI(this);
          if (this.preferences != null) {
            SwingUtilities.updateComponentTreeUI(this.preferences);
          }

        } catch (Exception e) {
          e.printStackTrace();
        }

        break;
      }
    }

  }

  /**
   * Usage:
   * 
   * Comments:
   * 
   * 
   * 
   */
  private void showPreferencesDialog() {

    // load all GUI preferences from the simulators property file and set them
    String value;

    value = this.webStartSimulationManager.getProperties().getProperty(
        this.propertySwingLookAndFeel);
    if (value != null) {
      this.setLookAndFeel(value);
    }

    this.preferences.setMultithreading(this.webStartSimulationManager
        .isAutoMultithreading());

    this.preferences.setVisible(true);
  }

  private int showExitDialog() {
    return JOptionPane.showConfirmDialog(this,
        "Are you sure you want to exit?", "Exit", JOptionPane.YES_OPTION,
        JOptionPane.QUESTION_MESSAGE);
  }

  private void runSimulation() {

    // try to get the simulation state
    if (this.webStartSimulationManager.getProject().getSimulation() != null) {
      // the simulation is paused ?
      boolean isPaused = this.webStartSimulationManager.getProject()
          .getSimulation().isPaused();

      // simulation was in a pause state ?
      if (isPaused) {

        // continue the simulation
        this.webStartSimulationManager.getProject().getSimulation()
            .pauseSimulation(false);

        // nothing to do next on this function
        return;

      }
    }

    // indicate on the progress bar the start of the class loading process
    this.progressBar.setIndeterminate(true);
    this.progressBar.setStringPainted(true);
    this.progressBar.setString("Loading...");

    // define a threadl
    simulationThread = new Runnable() {
      public void run() {

        try {

          webStartSimulationManager.initializeDom();

          // the max nr of steps for the progress bar
          int maxValue = Math.round(webStartSimulationManager.getProject()
              .getSimulation().getTotalSimulationSteps());
          progressBar.setMaximum(maxValue);

        } finally {

          progressBar.setIndeterminate(false);
          progressBar.setString("Loaded.");

          // get the number of simulation steps
          Integer simulationSteps = new Long(webStartSimulationManager
              .getProject().getTotalSimulationSteps()).intValue();
          if (AORJavaWebStartGui.this.tempSimSteps > -1) {
            simulationSteps = AORJavaWebStartGui.this.tempSimSteps;
          } else {
            simulationSteps = new Long(webStartSimulationManager.getProject()
                .getTotalSimulationSteps()).intValue();
          }

          // set it in the tool bar
          AORJavaWebStartGui.this.toolBarSimulation
              .setSimulationSteps(simulationSteps);
          AORJavaWebStartGui.this.progressBar.setMaximum(simulationSteps);
          AORJavaWebStartGui.this.webStartSimulationManager.getProject()
              .setTotalSimulationSteps(simulationSteps);

          // get the step time from the simulation
          Integer stepTimeDelay;
          if (AORJavaWebStartGui.this.tempStepTimeDelay > -1) {
            stepTimeDelay = AORJavaWebStartGui.this.tempStepTimeDelay;
          } else {
            stepTimeDelay = new Long(webStartSimulationManager.getProject()
                .getStepTimeDelay()).intValue();
          }
          // set it in the tool bar
          AORJavaWebStartGui.this.toolBarSimulation.setStepTime(stepTimeDelay);
          AORJavaWebStartGui.this.webStartSimulationManager.getProject()
              .setStepTimeDelay(stepTimeDelay);

          try {

            // simulate the simulation project
            webStartSimulationManager.getProject().prepareSimulation();

            if (webStartSimulationManager.getProject().getSimulation() != null) {
              // run the simulation
              webStartSimulationManager.getProject().getSimulation()
                  .runSimulation();
            }
          } finally {

            // switch on/off the external log viewer button, depending if an
            // log file name was set in the project
            ((WebStartMenu) getJMenuBar()).getMenuItemByName(
                WebStartMenu.Item.EXTERNAL_LOG_VIEWER).setEnabled(
                !webStartSimulationManager.getProject().getLogFileName()
                    .equals(""));

          }

        }
      }
    };

    // start the simulation thread in the background
    this.backgroundExecution.execute(simulationThread);
  }

  /**
   * Pause the simulation when user push the PAUSE button
   */
  private synchronized void pauseSimulation() {
    if (webStartSimulationManager.getProject().getSimulation() != null) {
      // pause simulation
      webStartSimulationManager.getProject().getSimulation().pauseSimulation(
          true);
    }
  }

  private void stopSimulation() {

    if (this.webStartSimulationManager.getProject() == null)
      return;

    // start a thread in the background
    this.backgroundExecution.execute(new Runnable() {
      public void run() {
        try {
          // when the simulation is instantiated
          if (webStartSimulationManager.getProject().isSimulationInstantiated()) {
            long previousStepTimeDelay = webStartSimulationManager.getProject()
                .getSimulation().getStepTimeDelay();

            // delay the simulation step to 2 seconds
            webStartSimulationManager.getProject().getSimulation()
                .setStepTimeDelay(1000);

            // get the current step
            long step = webStartSimulationManager.getProject().getSimulation()
                .getCurrentSimulationStep();

            webStartSimulationManager.getProject().getSimulation()
                .stopSimulation();

            // update the max. step in the progress bar
            progressBar.setMaximum(new Long(step).intValue());

            // set the simulation step to it previous setting
            webStartSimulationManager.getProject().getSimulation()
                .setStepTimeDelay(previousStepTimeDelay);

          }
        } finally {
        }
      }
    });
  }

  /**
   * Usage:
   * 
   * Comments: Overrides method {@code actionPerformed} from super class
   * 
   * @param arg0
   */
  @Override
  public void actionPerformed(ActionEvent evt) {

    // click on: the exit icon & menu entry
    if (evt.getActionCommand().equals(WebStartMenu.Item.EXIT)) {
      // !!! DO NOT FORGET TO MODIFY THE ACTION LISTENER FOR THE X BUTTON
      // IN TOP OF THIS CLASS AS WELL, WHEN YOU CHANGE SOMETHING HERE !!!
      if (this.showExitDialog() == JOptionPane.YES_OPTION) {
        this.setVisible(false);
        this.dispose();
        System.exit(0);
      }

      // click on: preferences
    } else if (evt.getActionCommand().equals(WebStartMenu.Item.PREFERENCES)
        | evt.getActionCommand().equals(
            AORJavaWebStartGui.CONTEXT_MENU_ITEM_PREFERENCES)) {
      this.showPreferencesDialog();

      // click on: run icon & menu entry
    } else if (evt.getActionCommand().equals(WebStartMenu.Item.RUN)) {
      // run the simulation
      this.runSimulation();

    } else if (evt.getActionCommand().equals(WebStartMenu.Item.PAUSE)) {
      this.pauseSimulation();

      // click on: stop icon & menu entry
    } else if (evt.getActionCommand().equals(WebStartMenu.Item.STOP)) {

      // the user has stopped the simulation using the STOP button
      this.simulationStoppedByUser = true;

      // reset current simulation iteration to 0
      this.currentSimulationIterationNumber = 0;

      // stop simulation
      this.stopSimulation();

    } else if (evt.getActionCommand().endsWith(WebStartMenu.Item.ABOUT)) {
      String htmlContent = this.webStartSimulationManager.readTextFileFromJar(
          "ext/documents", aboutHTMLFileName);
      new DialogHtml(this, htmlContent, "About");

      // changed preferences
    } else if (evt.getActionCommand().equals("comboBoxChanged")) {
      String lookAndFeelName = (String) ((JComboBox) evt.getSource())
          .getSelectedItem();

      this.setLookAndFeel(lookAndFeelName);

      // when the OK button of the preferences dialog was pressed
    } else if (evt.getActionCommand().equals(
        WebStartDialogPreferences.OK_BUTTON_ACTION_COMMAND)) {
      this.preferences.setVisible(false);

      // persist the external XML editor location of the preferences dialog in
      // the properties of the simulator the GUI doesn't have its own properties
      this.webStartSimulationManager.getProperties().put(
          this.propertySwingLookAndFeel,
          this.preferences.getSelectedLookAndFeel());

      if (this.webStartSimulationManager.getProject() != null) {

        this.tabPane.setEnabledAt(this.tabAORSLIndex, true);
        this.tabAORSL.getEditorPane().setEnabled(true);
      }
      this.tabPane.setTitleAt(this.tabAORSLIndex, this.tabAORSLName);

      this.webStartSimulationManager.setAutoMultithreading(this.preferences
          .isMultithreading());

      // when in the context menu of the AORSL editor "Reload" was clicked
    } else {
      System.err.println("Unimplemented action event occured: "
          + evt.getActionCommand());

    }

  }

  @Override
  public void stateChanged(ChangeEvent evt) {
    if (evt.getSource() instanceof JSpinner
        && this.webStartSimulationManager.getProject() != null) {
      JSpinner spinner = (JSpinner) evt.getSource();
      Integer value = (Integer) spinner.getValue();
      // this.simulator.getProject().setStepTimeDelay(value);
      if (spinner.getName().equals(ToolBarSimulation.SIMULATION_STEP_SPINNER)) {
        this.tempSimSteps = value;
        this.webStartSimulationManager.getProject().setTotalSimulationSteps(
            value);
        // update the max. step in the progress bar
        progressBar.setMaximum(value);

      } else if (spinner.getName().equals(ToolBarSimulation.STEP_TIME_SPINNER)) {
        this.tempStepTimeDelay = value;
        this.webStartSimulationManager.getProject().setStepTimeDelay(value);
      } else if (spinner.getName().equals(
          ToolBarSimulation.SIMULATION_ITERATIONS_SPINNER)) {
        this.maxSimulationIterationsNumber = value;
        this.updateCurrentSimulationIterationsLabel(
            this.currentSimulationIterationNumber,
            this.maxSimulationIterationsNumber);
      } else {
        System.err
            .println("Your JSpinner component is not yet registered in Main.stateChanged().");
      }

    }
  }

  @Override
  public void objektInitEvent(ObjektInitEvent evt) {
  }

  @Override
  public void simulationStepEnd(SimulationStepEvent simulationStepEvent) {
  }

  @Override
  public void simulationStepStart(long stepNumber) {
    this.progressBar.setString("" + stepNumber);
    this.progressBar.setValue((int) stepNumber);
  }

  /**
   * Update the label that states the current iteration runs from the total
   * defined
   * 
   * @param currentIteration
   *          the current iteration
   * @param maxIterations
   *          the max number of iterations
   */
  private void updateCurrentSimulationIterationsLabel(int currentIteration,
      int maxIterations) {
    this.currentSimulationIterationsLabel.setText(" Iterations: "
        + currentIteration + "/" + maxIterations + "     ");
  }

  /**
   * Update the label that states the total iteration run time
   * 
   * @param totalIterationsTime
   *          the total iterations time in milliseconds
   */
  private void updateTotalIterationsTimeLabel(long totalIterationsTime) {
    this.totalIterationsTimeLabel.setText(" Total Iterations Time: "
        + totalIterationsTime + "ms     ");

  }

  /**
   * Update the label that states the last iteration run time
   * 
   * @param lasIterationTime
   *          the last iterations time in milliseconds
   */
  private void updateLastIterationTimeLabel(long lastIterationsTime) {
    this.lastIterationTimeLabel.setText(" Last Iteration Time: "
        + lastIterationsTime + "ms     ");

  }

  @Override
  public void simulationEnvironmentEventOccured(
      EnvironmentEvent environmentEvent) {
    // nothing to do

  }

  @Override
  public void simulationInitialize(InitialState initialState) {
    // nothing to do

  }

  @Override
  public void simulationPaused(boolean pauseState) {

    // pause
    if (pauseState) {

      // change the pause to a run button
      toolBarSimulation.switchButton(WebStartMenu.Item.PAUSE,
          WebStartMenu.Item.RUN, WebStartMenu.Item.RUN_IMAGE);

      // enable the run button
      toolBarSimulation.enableButton(WebStartMenu.Item.RUN, true);

      // disable the pause button
      toolBarSimulation.enableButton(WebStartMenu.Item.PAUSE, false);

      // enable the stop button
      toolBarSimulation.enableButton(WebStartMenu.Item.STOP, true);

      // indicate in the GUI title with a pause state
      this.setTitle(this.guiTitle + " - "
          + this.webStartSimulationManager.getProject().getName()
          + " - Paused ");
    }
    // continue
    else {
      // change the run to a pause button
      toolBarSimulation.switchButton(WebStartMenu.Item.RUN,
          WebStartMenu.Item.PAUSE, WebStartMenu.Item.PAUSE_IMAGE);

      // disable the run button
      toolBarSimulation.enableButton(WebStartMenu.Item.RUN, false);

      // enable the pause button
      toolBarSimulation.enableButton(WebStartMenu.Item.PAUSE, true);

      // enable the stop button
      toolBarSimulation.enableButton(WebStartMenu.Item.STOP, true);

      // indicate in the GUI title with no pause state
      this.setTitle(this.guiTitle + " - "
          + this.webStartSimulationManager.getProject().getName());

    }

  }

  @Override
  public void simulationStarted() {
    // store the simulation starting time
    this.simulationStartTime = System.currentTimeMillis();

    // currentSimulationIteration is increased by 1
    currentSimulationIterationNumber++;

    // update the simulation iteration label
    updateCurrentSimulationIterationsLabel(currentSimulationIterationNumber,
        maxSimulationIterationsNumber);

    // disable the spinner for simulation iterations
    this.toolBarSimulation.enableSimulationToolBarEditableComponents(false);

    // change the run to a pause button
    this.toolBarSimulation.switchButton(WebStartMenu.Item.RUN,
        WebStartMenu.Item.PAUSE, WebStartMenu.Item.PAUSE_IMAGE);

    // disable the run button
    this.toolBarSimulation.enableButton(WebStartMenu.Item.RUN, false);

    // enable the pause button
    this.toolBarSimulation.enableButton(WebStartMenu.Item.PAUSE, true);

    // enable the stop button
    this.toolBarSimulation.enableButton(WebStartMenu.Item.STOP, true);

    // the user has not stopp the simulation...yet
    this.simulationStoppedByUser = false;

    success.println("Ok");

    System.out.println(messageSimulationStarted);
  }

  @Override
  public void simulationEnded() {

    System.out.println(messageSimulationFinished);

    // change the run to a pause button
    toolBarSimulation.switchButton(WebStartMenu.Item.PAUSE,
        WebStartMenu.Item.RUN, WebStartMenu.Item.RUN_IMAGE);

    // disable the pause button
    toolBarSimulation.enableButton(WebStartMenu.Item.PAUSE, false);

    // disable the pause button
    toolBarSimulation.enableButton(WebStartMenu.Item.RUN, true);

    // enable the stop button
    toolBarSimulation.enableButton(WebStartMenu.Item.STOP, false);

    // enable the spinner for simulation iterations
    toolBarSimulation.enableSimulationToolBarEditableComponents(true);

    // if the user has stopped the simulation, then we don iterate anymore
    if (this.simulationStoppedByUser) {
      return;
    }

    // compute simulation time
    this.simulationEndTime = System.currentTimeMillis();
    long simulationTime = this.simulationEndTime - this.simulationStartTime;

    // console messages with simulation time
    System.out.print(messageSimulationTime + ": ");
    success.print(simulationTime);
    System.out.println(" ms. \n");

    // update the label with current iteration time
    this.updateLastIterationTimeLabel(simulationTime);

    // increase the total simulation iterations time.
    totalSimulationIterationsTime += simulationTime;

    // update the total iterations time label
    updateTotalIterationsTimeLabel(this.totalSimulationIterationsTime);

    // we have more simulation runs to do ?
    if (this.currentSimulationIterationNumber < this.maxSimulationIterationsNumber) {
      // run the simulation again
      this.runSimulation();
      return;
    }

    // iterations reset
    this.currentSimulationIterationNumber = 0;

    // reset the total time
    this.totalSimulationIterationsTime = 0;
  }

  @Override
  public void simulationInfosEvent(SimulationEvent simulationEvent) {

    if (!simulationEvent.getPropertyName().equals(
        DataBus.LoggerEvent.EVENT_INFOS)) {
      return;
    }

    AbstractSimState simState = (AbstractSimState) simulationEvent.getValue();

    long agtNumber = simState.getAgentsCount();

    long objNumber = simState.getObjectsCount();

    long evtNumber = simState.getEventsCount();

    this.agentsNumberLabel.setText("   Agents: " + agtNumber + "   ");
    this.objectsNumberLabel.setText("   Objects: " + objNumber + "   ");
    this.eventsNumberLabel.setText("   Events: " + evtNumber + "   ");

  }

  @Override
  public void simulationProjectDirectoryChanged(File projectDirectory) {
    // nothing to do

  }

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code simulationDomOnlyInitialization} from
   * super class
   * 
   * 
   * 
   * @param simulationDescription
   */
  @Override
  public void simulationDomOnlyInitialization(
      SimulationDescription simulationDescription) {
    // TODO Auto-generated method stub

  }

  @Override
  public void moduleEvent(ModuleEvent moduleEvent) {
    int actualStepDelay = this.toolBarSimulation.getStepTime();
    int speedUpTimeValue = (actualStepDelay > 3 ? actualStepDelay / 3 : 1);
    int slowDownTimeValue = 1;

    if (moduleEvent instanceof ModuleEventSpeedUpSimulation
        && actualStepDelay >= speedUpTimeValue) {
      int newStepTime = actualStepDelay - speedUpTimeValue;

      // set new step time delay
      this.toolBarSimulation.setStepTime(newStepTime);
      this.webStartSimulationManager.getProject().setStepTimeDelay(
          newStepTime);
    }
    if (moduleEvent instanceof ModuleEventSlowDownSimulation) {
      int newStepTime = actualStepDelay + slowDownTimeValue;

      // set new step time delay
     this.toolBarSimulation.setStepTime(newStepTime);
     this.webStartSimulationManager.getProject().setStepTimeDelay(
          newStepTime);
    }
  }

  /**
   * This is the main method that starts the GUI for the simulator application.
   * 
   * Usage: It is called from the Java VM when the whole application is packet
   * into the Java archive file (JAR) with GUI support.
   * 
   * 
   * @param args
   *          - no arguments are used
   */
  public static void main(String args[]) {
    new AORJavaWebStartGui().setVisible(true);
  }
}
