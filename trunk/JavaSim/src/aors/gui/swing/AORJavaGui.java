package aors.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import aors.controller.AbstractSimulator;
import aors.controller.InitialState;
import aors.controller.Project;
import aors.controller.SimulationDescription;
import aors.controller.SimulationManager;
import aors.data.DataBus;
import aors.data.evt.sim.ObjektInitEvent;
import aors.data.evt.sim.ObjektInitEventListener;
import aors.data.evt.sim.SimulationEvent;
import aors.data.evt.sim.SimulationEventListener;
import aors.data.evt.sim.SimulationStepEvent;
import aors.data.evt.sim.SimulationStepEventListener;
import aors.data.java.helper.AbstractSimState;
import aors.gui.helper.FileListener;
import aors.gui.helper.FileMonitor;
import aors.model.envevt.EnvironmentEvent;
import aors.model.envsim.AgentObject;
import aors.model.envsim.Objekt;
import aors.module.Module;
import aors.module.evt.ModuleEvent;
import aors.module.evt.ModuleEventListener;
import aors.module.evt.ModuleEventSlowDownSimulation;
import aors.module.evt.ModuleEventSpeedUpSimulation;

/**
 * 
 * Main
 * 
 * @author Marco Pehla, Jens Werner
 * @since 18.07.2008
 * @version $Revision$
 */
public class AORJavaGui extends JFrame implements ActionListener,
    ChangeListener, SimulationEventListener, ObjektInitEventListener,
    FileListener, SimulationStepEventListener, ModuleEventListener {

  static final long serialVersionUID = 122140342341234L;
  private Runnable simulationThread;

  private FileMonitor fileMonitor;

  private final String guiTitle = "AOR JavaSim";
  private final String tabAORSLName = "AORSL";
  private final String tabAORSLNameExternalEditor = "AORSL [external editor]";
  private final int tabAORSLIndex = 0;
  // private final String tabStatisticsName = "Statistics";

  private final String loggerChangeDialogMessage = "Changing the logger must stop the simulation.\n Do you still want to continue?";
  private final String loggerChangeDialogTitle = "Stop request";

  private final String aboutHTMLFileName = System.getProperty("user.dir")
      + File.separator + "ext" + File.separator + "documents" + File.separator
      + "about.htm";

  public final static String noCompilerFoundHTMLFileName = System
      .getProperty("user.dir")
      + File.separator
      + "ext"
      + File.separator
      + "documents"
      + File.separator
      + "nojavacompiler.htm";

  private final SimulationManager simulationManager;
  private ExecutorService backgroundExecution;

  // used to redirect the System.out and System.err streams
  private PrintStream out;
  private PrintStream err;
  private PrintStream success;
  private PrintStream warning;

  // property names for values regarding GUI settings
  private String propertySwingLookAndFeel = "GUI.SwingLookAndFeel";
  private String propertyUseExternalXMLEditor = "GUI.UseExternalXMLEditor";
  private String propertyUseExternalLogViewer = "GUI.UseExternalLogViewer";
  private String propertyExternalXMLEditorLocation = "GUI.ExternalXMLEditorLocation";

  // the loading message dialog
  private JDialog loadingDialog;

  /**
   * PANES
   */
  private JTabbedPane tabPane;
  private TabSimDescription tabAORSL;
  // private TabStatistics tabStatistics;

  // in the right bottom, holds e.g. the progress bar
  private JPanel statusPane;

  private ToolBarFile toolBarFile;
  private ToolBarSimulation toolBarSimulation;

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

  private JFileChooser fileChooser;
  private FileNameExtensionFilter simulationExtensionFilter = new FileNameExtensionFilter(
      "AORSL Simulations", "xml", "aor", "aors");
  private FileNameExtensionFilter projectExtensionFilter = new FileNameExtensionFilter(
      "AORSL Projects", " ");

  private DialogPreferences preferences;

  // user messages
  public final static String messageNoJavaCompiler = "No Java Compiler was found. "
      + "Make sure you are running this application at least with the Java Standard Developer Kit (SDK) 6. "
      + "The Java Runtime Environment (JRE) is not sufficient because it does not include any Java Compiler.";
  private final String messageSaveExistendProject = "Your simulation project has not been saved. Do you want to save it now?";
  private final String messageGenerateAgain = "The simulation is already generated. Should it be generated again?";
  private final String messageCompileAgain = "The simulation project is already compiled. Should it be compiled again?";
  private final String messageNotImplemented = "This function is not implemented yet.";
  private final String messageProjectAlreadyExist = "An project with this name already exist. Overwrite?";
  private final String messageNeedSavedProject = "The project need to be saved, in order to know where to save the generated simulation. \n"
      + " Do you want to choose again the project name? ";
  private final String messageSaveProjectToDisk = "The project need to be saved, before you are able to edit it with an external XML editor.";
  private final String messageNoLogAvailable = "Actually is no log file available. You need to start a simulation in order to create one.";

  private final String messageValidation = "AORSL Validation : ";
  private final String messageGeneration = "Code Generation : ";
  private final String messageCompilation = "Code Compilation : ";
  private final String messageBuilt = "Simulation Built : ";
  private final String messageLoadClasses = "Loading simulation";
  private final String messageSimulationTime = "Simulation time";

  private final String messageSimulationStarted = "Simulation started.";
  private final String messageSimulationFinished = "Simulation finished.";

  // options for user selection
  private final String optionEmptyProject = "Create an empty project";
  private final String optionOpenExistentDescription = "Open existent simulation description";

  private final String[] optionsNewProject = { optionOpenExistentDescription,
      optionEmptyProject, };

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
  public AORJavaGui() {
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

    // register an window listener
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        if (showExitDialog() == JOptionPane.YES_OPTION) {
          e.getWindow().dispose();
          e.getWindow().setVisible(false);
          // save all properties
          try {
            simulationManager.storeProperties();
          } finally {
            System.exit(0);
          }

        }
      }
    });

    Locale.setDefault(Locale.ENGLISH);
    this.getContentPane().setLocale(Locale.ENGLISH);

    // new simulator instance
    this.simulationManager = new SimulationManager();

    this.preferences = new DialogPreferences(this);

    String value = this.simulationManager.getProperties().getProperty(
        this.propertySwingLookAndFeel);
    if (value != null) {
      this.setLookAndFeel(value);
    }

    // instantiate a cached thread pool for the execution of background thread
    // like generation of Java source codes or the compilation
    this.backgroundExecution = Executors.newCachedThreadPool();

    this.initListener();
    this.startFileMonitorTask();
    this.preferences.selectLoggerRadioButton(simulationManager.getDataBus()
        .getLoggerType());

    this.setLayout(new BorderLayout());
    this.setIconImage(ImageLoader.loadImage("logo.gif"));
    this.setTitle(guiTitle);

    // arrange the GUI window to display
    double percent = 0.8;
    int windowWidth = (int) (this.getToolkit().getScreenSize().width * percent);
    int windowHeight = (int) (this.getToolkit().getScreenSize().height * percent);

    // set size to ?% of the screen size dimensions
    this.setSize(windowWidth, windowHeight);

    // centre the window
    this.setLocation(
        (this.getToolkit().getScreenSize().width - windowWidth) / 2, (this
            .getToolkit().getScreenSize().height - windowHeight) / 2);

    /**
     * This is required in order to not interfere with other components in
     * display, such as the ones using OpenGL technologies.
     */
    JPopupMenu.setDefaultLightWeightPopupEnabled(false);

    this.setJMenuBar(new Menu(this));

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
    this.toolBarFile = new ToolBarFile(this);
    this.toolBarSimulation = new ToolBarSimulation(this);

    toolBarPane.add(this.toolBarFile, BorderLayout.LINE_START);
    toolBarPane.add(this.toolBarSimulation, BorderLayout.CENTER);

    this.add(toolBarPane, BorderLayout.PAGE_START);

    // disable tool bar buttons
    this.toolBarSimulation.enableButton(Menu.Item.RUN, false);
    this.toolBarSimulation.enableButton(Menu.Item.STOP, false);

    // disable editable components of the simulation toolbar
    this.toolBarSimulation.enableSimulationToolBarEditableComponents(false);

    // instantiate the tab pane
    this.tabPane = new JTabbedPane();

    this.tabAORSL = new TabSimDescription(JSplitPane.VERTICAL_SPLIT, this);

    // DEPRECATED
    this.tabAORSL.getEditorPane().setEnabled(false);
    this.tabAORSL.setName(this.tabAORSLName);

    // this.tabStatistics = new TabStatistics();
    // this.tabStatistics.setName(this.tabStatisticsName);

    this.tabPane.add(this.tabAORSL, this.tabAORSLIndex);
    // this.tabPane.add(this.tabStatistics, this.tabStatistics);

    // create modules group tab - if is the case
    List<Module> groupModules = this.simulationManager.getGroupModules();
    if (groupModules.size() > 0) {
      JScrollPane groupModulesTab = new JScrollPane();
      groupModulesTab.setName("Group-Modules");
      JPanel groupModulesPanel = new JPanel();
      groupModulesPanel.setLayout(new BorderLayout());
      int posIndex = 0;
      for (Module module : groupModules) {
        Component moduleGUI = ((JScrollPane) module.getGUIComponent());

        switch (posIndex) {
        case 0:
          groupModulesPanel.add(BorderLayout.CENTER, moduleGUI);
          break;
        case 1:
          groupModulesPanel.add(BorderLayout.SOUTH, moduleGUI);
          break;
        case 2:
          groupModulesPanel.add(BorderLayout.EAST, moduleGUI);
          break;
        case 3:
          groupModulesPanel.add(BorderLayout.WEST, moduleGUI);
          break;
        }
        posIndex++;
      }
      groupModulesTab.setViewportView(groupModulesPanel);
      this.tabPane.add(groupModulesTab);
    }

    // create modules tabs
    for (Module module : this.simulationManager.getModules()) {
      // no GUI component defined for this module
      if (module.getGUIComponent() == null) {
        continue;
      }
      this.tabPane.add((JScrollPane) module.getGUIComponent());
    }

    // disable all tabs
    for (int tabIndex = 0; tabIndex < this.tabPane.getTabCount(); tabIndex++) {
      this.tabPane.setEnabledAt(tabIndex, false);
    }

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

    // this.statistic = new PrintStream(new OutputStreamTextPane(
    // this.tabStatistics.getOutputTextPane(), new Color(0, 0, 0), new Font(
    // "SansSerif", Font.PLAIN, 12)));

    this.warning = new PrintStream(new OutputStreamTextPane(this.tabAORSL
        .getOutputTextPane(), new Color(0, 0, 220), new Font("SansSerif",
        Font.BOLD, 12)));

    this.getContentPane().add(this.tabPane);

    // put the application window in full screen mode
    this.setExtendedState(AORJavaGui.MAXIMIZED_BOTH);

    this.fileChooser = new JFileChooser();

    this.fileChooser.setFileFilter(this.projectExtensionFilter);

    // set file viewer to render simulation related icons in open / save dialogs
    this.fileChooser.setFileView(new ProjectFileView());

    // disable all not yet usable menu items
    ((Menu) this.getJMenuBar()).switchMenuItems(Arrays.asList(Menu.Item.SAVE,
        Menu.Item.SAVE_AS, Menu.Item.CLOSE, Menu.Item.IMPORT_XML,
        Menu.Item.EXPORT_XML, Menu.Item.EXPORT_JAR, Menu.Item.BUILD_ALL,
        Menu.Item.VALIDATE_ONLY, Menu.Item.GENERATE_ONLY,
        Menu.Item.COMPILE_ONLY, Menu.Item.RUN, Menu.Item.EXTERNAL_XML_EDITOR,
        Menu.Item.EXTERNAL_LOG_VIEWER), false);

    this.toolBarFile.enableButton(Menu.Item.SAVE, false);

    this.toolBarFile.getButtonByActionCommand(Menu.Item.EXTERNAL_XML_EDITOR)
        .setEnabled(false);
    this.toolBarFile.getButtonByActionCommand(Menu.Item.EXTERNAL_LOG_VIEWER)
        .setEnabled(false);

    value = this.simulationManager.getProperties().getProperty(
        this.propertyUseExternalXMLEditor);
    Boolean externalXMLEditor = false;
    if (value != null) {
      externalXMLEditor = new Boolean(value);
    }

    this.preferences.setExternalXMLEditor(externalXMLEditor);
    if (externalXMLEditor) {
      this.tabPane.setTitleAt(this.tabAORSLIndex,
          this.tabAORSLNameExternalEditor);
    }

    value = this.simulationManager.getProperties().getProperty(
        this.propertyUseExternalLogViewer);
    Boolean externalLogViewer = false;
    if (value != null) {
      externalLogViewer = new Boolean(value);
    }

    this.preferences.setExternalLogViewer(externalLogViewer);

    value = this.simulationManager.getProperties().getProperty(
        this.propertyExternalXMLEditorLocation);
    // when the editor location was set
    if (value != null) {
      this.preferences.setExternalXMLEditorLocation(value);
    }

    // create the loading message dialog
    loadingDialog = new JDialog(this);
    loadingDialog.setSize(250, 70);
    Rectangle bounds = this.getBounds();
    Point location = this.getLocation();
    int x = location.x + (bounds.width - loadingDialog.getSize().width) / 2;
    int y = location.y + (bounds.height - loadingDialog.getSize().height) / 2;
    loadingDialog.setLocation(x, y);
    loadingDialog.setUndecorated(true);
    JLabel messageLabel = new JLabel("Loading scenario...please wait!",
        JLabel.CENTER);
    messageLabel.setBorder(new EtchedBorder());
    loadingDialog.add(messageLabel);
    loadingDialog.setVisible(false);

    // after all errors that may happen...
    // redirect the System.out and System.err streams to the text area in the
    // GUI
    System.setOut(this.out);
    System.setErr(this.err);

    System.out.println("AOR Simulator started successfully.");
    if (AbstractSimulator.showDebugFlags) {
      System.out
          .println("****************************************************");
      System.out.println("State of module-switches\n");
      System.out.println("Run activities   : "
          + AbstractSimulator.runActivities);
      System.out.println("Run logger       : " + AbstractSimulator.runLogger);
      System.out
          .println("****************************************************");
    }
  }// constructor

  private void startFileMonitorTask() {
    if (this.fileMonitor == null) {
      this.fileMonitor = new FileMonitor(1000);
      this.fileMonitor.addListener(this);
    }

    this.fileMonitor.startFileMonitorNotifier();
  }

  private void stopFileMonitorTask() {
    if (this.fileMonitor != null)
      this.fileMonitor.stopFileMonitorNotifier();
  }

  private void initListener() {
    // register the GUI as property change listener for events raised by the
    // logger
    simulationManager.getDataBus().addSimulationEventListener(this);
    simulationManager.getDataBus().addObjektInitEventListener(this);
    simulationManager.getDataBus().addSimulationStepEventListener(this);
    simulationManager.getDataBus().addModuleEventListener(this);
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

          if (this.fileChooser != null) {
            SwingUtilities.updateComponentTreeUI(this.fileChooser);
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

    value = this.simulationManager.getProperties().getProperty(
        this.propertySwingLookAndFeel);
    if (value != null) {
      this.setLookAndFeel(value);
    }

    value = this.simulationManager.getProperties().getProperty(
        this.propertyUseExternalXMLEditor);
    if (value != null) {
      this.preferences.setExternalXMLEditor(new Boolean(value));
    }

    value = this.simulationManager.getProperties().getProperty(
        this.propertyExternalXMLEditorLocation);
    if (value != null) {
      this.preferences.setExternalXMLEditorLocation(value);
    }

    this.preferences.setMultithreading(this.simulationManager
        .isAutoMultithreading());

    this.preferences.setVisible(true);
  }

  private int showExitDialog() {
    return JOptionPane.showConfirmDialog(this,
        "Are you sure you want to exit?", "Exit", JOptionPane.YES_OPTION,
        JOptionPane.QUESTION_MESSAGE);
  }

  /**
   * 
   * Usage:
   * 
   * 
   * Comments:
   * 
   * 
   * 
   */
  private void updateSimulationDescription() {
    // when an external XML editor is used
    if (this.preferences.isExternalXMLEditor()) {
      // load the XML from the disk
      this.simulationManager.getProject().loadSimulationDescription();
      // set the it in the internal editor
      this.tabAORSL.getEditorPane().setText(
          this.simulationManager.getProject().getSimulationDescription());

      // when the internal XML editor is used
    } else {
      // set the text content from the internal XMl editor as latest simulation
      // description
      this.simulationManager.getProject().setSimulationDescription(
          this.tabAORSL.getEditorPane().getText());
      // save only the simulation description of the project to the disk
      // this.simulationManager.getProject().saveSimulationDescription();
    }
  }

  /**
   * 
   * Usage: This method validates the XML simulation description.
   * 
   * 
   * Comments:
   * 
   * 
   * 
   * @return
   */
  private boolean validateXML() {
    boolean result = false;

    // update the simulation description depending on the current XML editor
    // setting
    this.updateSimulationDescription();

    // validate the current project, which means the XML content of the
    // simulation description will be load from the project an validated against
    // the XML Schema

    result = this.simulationManager.validateSimulation();

    System.out.print(this.messageValidation);
    if (result) {
      this.success.println("Ok");
      this.progressBar.setString("Valid.");

      // enable all previously may not usable menu items
      ((Menu) this.getJMenuBar()).switchMenuItems(Arrays
          .asList(Menu.Item.GENERATE_ONLY), true);
    } else {
      System.err.println("Failed!");
      this.progressBar.setString("Invalid.");

      // disable all not usable menu items
      ((Menu) this.getJMenuBar()).switchMenuItems(Arrays.asList(
          Menu.Item.GENERATE_ONLY, Menu.Item.COMPILE_ONLY), false);
    }

    return result;
  }

  private void generate() {

    // update the simulation description depending on the current XML editor
    // setting
    this.updateSimulationDescription();

    boolean saved = false;

    // as long as not saved
    while (!saved) {

      // try to save
      saved = this.saveProject();

      // if the user did cancel the save dialog
      if (saved == false) {

        // inform him why saving is necessary
        int overwriteExistentProject = JOptionPane.showConfirmDialog(this,
            messageNeedSavedProject, "", JOptionPane.YES_OPTION);
        // if saving the project is confirmed
        if (overwriteExistentProject == JOptionPane.YES_OPTION) {
          // still not saved
          saved = false;
          // if she did cancel the question again
        } else {
          // break the generation task
          break;
        }
      } // if not saved

      if (saved == true) {

        // check if the XML markup is valid against the XML Schema
        if (this.validateXML()) {

          if (this.simulationManager.getProject().isGenerated()) {
            // ask the user if to compile again?
            int status = JOptionPane.showConfirmDialog(this,
                this.messageGenerateAgain, "", JOptionPane.YES_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            // user clicked yes, set project in the not yet generated state
            if (status == JOptionPane.YES_OPTION) {
              this.simulationManager.getProject().setGenerated(false);
              toolBarSimulation.enableButton(Menu.Item.RUN, false);

            }
          }

          // disable all not usable menu items
          ((Menu) this.getJMenuBar()).switchMenuItems(Arrays.asList(
              Menu.Item.SAVE, Menu.Item.SAVE_AS, Menu.Item.EXPORT_XML,
              Menu.Item.EXPORT_JAR), false);
          this.toolBarFile.enableButton(Menu.Item.SAVE, false);

          // when an external XML editor is used
          if (this.preferences.isExternalXMLEditor()) {
            this.simulationManager.getProject().loadSimulationDescription();
            this.tabAORSL.getEditorPane().setText(
                this.simulationManager.getProject().getSimulationDescription());
            // when the internal editor is used
          } else {
            // update the simulation description in the project
            this.simulationManager.getProject().setSimulationDescription(
                this.tabAORSL.getEditorPane().getText());
            // save the project
            this.simulationManager.getProject().save();
          }

          // indicate it in the progress bar the beginning of the generation
          // process
          this.progressBar.setIndeterminate(true);
          this.progressBar.setString("Generating...");
          this.progressBar.setStringPainted(true);

          // start a thread in the background
          this.backgroundExecution.execute(new Runnable() {
            public void run() {
              try {
                // generate the simulation project
                simulationManager.generate();
              } finally {
                // when finished, indicate it in the progress bar
                GuiExecutor.instance().execute(new Runnable() {
                  public void run() {
                    progressBar.setIndeterminate(false);

                    System.out.print(messageGeneration);

                    if (simulationManager.getProject().isGenerated()) {
                      progressBar.setString("Generated.");
                      success.println("Ok");

                      // enable some disabled menu items
                      ((Menu) getJMenuBar()).switchMenuItems(Arrays.asList(
                          Menu.Item.EXPORT_JAR, Menu.Item.COMPILE_ONLY), true);

                    } else {
                      progressBar.setString("Generation failed.");
                      System.err.println("Failed!");

                      // disable menu items
                      ((Menu) getJMenuBar()).switchMenuItems(Arrays.asList(
                          Menu.Item.EXPORT_JAR, Menu.Item.COMPILE_ONLY), true);
                    }

                    // enable some disabled menu items
                    ((Menu) getJMenuBar()).switchMenuItems(Arrays
                        .asList(Menu.Item.SAVE, Menu.Item.SAVE_AS,
                            Menu.Item.EXPORT_XML), true);

                  }// execute()
                });// run()
              }// finally
            }// run()
          });// execute()
          // if not valid XML
          this.tempSimSteps = -1;
          this.tempStepTimeDelay = -1;

        } else {
          System.err
              .println("The simulation can not be generated as long as the simulation description is not valid.");
        }
      }// if saved

    }// while not saved

  }

  private void compile() {

    if (this.simulationManager.getProject().isCompiled()) {
      // ask the user if to compile again?
      int status = JOptionPane.showConfirmDialog(this,
          this.messageCompileAgain, "", JOptionPane.YES_OPTION,
          JOptionPane.QUESTION_MESSAGE);
      // user clicked yes, set project in the not yet compiled status
      if (status == JOptionPane.YES_OPTION) {
        this.simulationManager.getProject().setCompiled(false);
      }
    }

    // disable all not usable menu items
    ((Menu) this.getJMenuBar()).switchMenuItems(Arrays.asList(Menu.Item.SAVE,
        Menu.Item.SAVE_AS, Menu.Item.EXPORT_XML, Menu.Item.EXPORT_JAR), false);

    this.toolBarFile.enableButton(Menu.Item.SAVE, false);

    // indicate it in the progress bar the beginning of the compilation process
    this.progressBar.setIndeterminate(true);
    this.progressBar.setStringPainted(true);
    this.progressBar.setString("Compiling...");

    // start a thread in the background
    this.backgroundExecution.execute(new Runnable() {
      public void run() {
        System.out.print(messageCompilation);
        try {
          // compile the simulation project
          simulationManager.getProject().compile();

          // The simulation is already builded. Initialize it and
          // announce modules. This is the point when the modules are
          // asked to initialize on an build project is open.
          simulationManager.getProject().instantiateSimulation();

          // initialize data from XML for module
          simulationManager.initializeDom(simulationManager.getProject()
              .getSimulationDescription());

          simulationManager.getProject().prepareSimulationWithoutLogFile();
        } finally {
          // when finished, indicate it in the progress bar
          GuiExecutor.instance().execute(new Runnable() {
            public void run() {
              progressBar.setIndeterminate(false);

              // enable some previously disabled menu items
              ((Menu) getJMenuBar()).switchMenuItems(Arrays.asList(
                  Menu.Item.SAVE, Menu.Item.SAVE_AS, Menu.Item.EXPORT_XML),
                  true);

              if (simulationManager.getProject().isCompiled()) {
                progressBar.setString("Compiled.");
                success.println("Ok");
                // enable the run button for the simulation
                toolBarSimulation.enableButton(Menu.Item.RUN, true);
                ((Menu) getJMenuBar()).switchMenuItems(Arrays.asList(
                    Menu.Item.EXPORT_JAR, Menu.Item.RUN), true);
              } else {
                progressBar.setString("Compilation failed!");
                System.err.println("Failed!");
                toolBarSimulation.enableButton(Menu.Item.RUN, false);
                ((Menu) getJMenuBar()).switchMenuItems(Arrays.asList(
                    Menu.Item.EXPORT_JAR, Menu.Item.RUN), false);
              }

              // when the compilation started and created a diagnostic collector
              if (simulationManager.getProject().getDiagnosticCollector() != null) {
                // print the diagnostics if there are any
                // simulator.getProject().printDiagnostics();
                printDiagnostics();
              }
            }// execute()
          });// run()
        }// finally
      }// run()
    });// execute()

  }

  private void build() {

    // update the simulation description depending on the current XML editor
    // setting
    // TODO: delete if the editorpane is no longer changeable
    this.updateSimulationDescription();

    // clear the output panel
    this.tabAORSL.getOutputTextPane().setText("");

    boolean saved = false;

    // as long as not saved
    while (!saved) {
      // try to save
      saved = this.saveProject();

      // if the user did cancel the save dialog
      if (saved == false) {

        // inform him why saving is necessary
        int overwriteExistentProject = JOptionPane.showConfirmDialog(this,
            messageNeedSavedProject, "", JOptionPane.YES_OPTION);
        // if saving the project is confirmed
        if (overwriteExistentProject == JOptionPane.YES_OPTION) {
          // still not saved
          saved = false;
          // if she did cancel the question again
        } else {
          // break the generation task
          break;
        }
      } // if not saved

      // when the project was saved
      if (saved == true) {
        // if the validation of the description.xml file(!) was successful
        if (this.validateXML()) {

          // update the simulation description depending on the external editor
          // settings
          // this.updateSimulationDescription();

          // always build everything from scratch
          this.simulationManager.getProject().setGenerated(false);

          // disable all not usable menu items
          ((Menu) this.getJMenuBar()).switchMenuItems(Arrays.asList(
              Menu.Item.SAVE, Menu.Item.SAVE_AS, Menu.Item.EXPORT_XML,
              Menu.Item.EXPORT_JAR, Menu.Item.COMPILE_ONLY), false);

          this.toolBarFile.enableButton(Menu.Item.SAVE, false);
          // this.toolBarFile.enableButton(Menu.Item.RUN, false);
          // this.toolBarFile.enableButton(Menu.Item.NEW, false);
          // this.toolBarFile.enableButton(Menu.Item.OPEN, false);

          // indicate it in the progress bar the beginning of the build process
          this.progressBar.setIndeterminate(true);
          this.progressBar.setString("building...");
          this.progressBar.setStringPainted(true);

          // start one thread in the background
          this.backgroundExecution.execute(new Runnable() {
            public void run() {

              try {
                System.out.print(messageGeneration);
                // if the generation of the simulation project was successful
                if (simulationManager.generate()) {
                  success.println("Ok");
                  // compile the simulator project
                  if (simulationManager.getProject().compile()) {

                    // The simulation is already builded. Initialize it and
                    // announce modules. This is the point when the modules are
                    // asked to initialize on an build project is open.
                    simulationManager.getProject().instantiateSimulation();

                    // initialize data from XML for module
                    simulationManager.initializeDom(simulationManager
                        .getProject().getSimulationDescription());

                    simulationManager.getProject()
                        .prepareSimulationWithoutLogFile();
                  } else {
                    System.err.println("Compilation failed!");
                  }
                } else {
                  System.err.println("Failed!");

                }
              } finally { // when finished, indicate it in the progress bar
                GuiExecutor.instance().execute(new Runnable() {
                  public void run() {
                    progressBar.setIndeterminate(false);

                    System.out.print(messageBuilt);

                    // enable all previously disabled menu items
                    ((Menu) getJMenuBar()).switchMenuItems(Arrays.asList(
                        Menu.Item.SAVE, Menu.Item.SAVE_AS,
                        Menu.Item.EXPORT_XML, Menu.Item.EXPORT_JAR,
                        Menu.Item.COMPILE_ONLY), true);

                    if (simulationManager.getProject().isCompiled()) {
                      progressBar.setString("Built.");
                      success.println("Ok");

                      // enable the run button for the simulation
                      toolBarSimulation.enableButton(Menu.Item.RUN, true);
                      ((Menu) getJMenuBar()).switchMenuItems(Arrays
                          .asList(Menu.Item.RUN), true);

                      // enable editable simulation elements on tool-bar
                      toolBarSimulation
                          .enableSimulationToolBarEditableComponents(true);
                    } else {
                      progressBar.setString("Built failed!");
                      System.err.println("Failed!");
                      toolBarSimulation.enableButton(Menu.Item.RUN, false);
                      ((Menu) getJMenuBar()).switchMenuItems(Arrays
                          .asList(Menu.Item.RUN), false);
                    }

                    // when the compilation started and created a diagnostic
                    // collector
                    if (simulationManager.getProject().getDiagnosticCollector() != null) {
                      // print the diagnostics if there are any
                      // simulator.getProject().printDiagnostics();
                      printDiagnostics();
                    }
                  }
                });
              }

            }
          });// execute()

        } else {

          System.out.print(messageValidation);
          System.err.println("Failed!");
          System.out.print(messageBuilt);
          System.err.println("Failed!");
        }
      }
    }
  }

  private void runSimulation() {

    // try to get the simulation state
    if (this.simulationManager.getProject().getSimulation() != null) {
      // the simulation is paused ?
      boolean isPaused = this.simulationManager.getProject().getSimulation()
          .isPaused();

      // simulation was in a pause state ?
      if (isPaused) {

        // continue the simulation
        this.simulationManager.getProject().getSimulation().pauseSimulation(
            false);

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

          // instantiate the simulation
          simulationManager.getProject().instantiateSimulation();

          // initialize data from XML (and other) for modules
          // TODO: check whats happen if something is changed in the editor
          // pane, but not saved, created ...
          simulationManager.initializeDom(tabAORSL.getEditorPane().getText());

          // the max nr of steps for the progress bar
          int maxValue = Math.round(simulationManager.getProject()
              .getSimulation().getTotalSimulationSteps());
          progressBar.setMaximum(maxValue);

        } finally {

          progressBar.setIndeterminate(false);
          progressBar.setString("Loaded.");

          // get the number of simulation steps
          Integer simulationSteps = new Long(simulationManager.getProject()
              .getTotalSimulationSteps()).intValue();
          if (AORJavaGui.this.tempSimSteps > -1) {
            simulationSteps = AORJavaGui.this.tempSimSteps;
          } else {
            simulationSteps = new Long(simulationManager.getProject()
                .getTotalSimulationSteps()).intValue();
          }

          // set it in the tool bar
          AORJavaGui.this.toolBarSimulation.setSimulationSteps(simulationSteps);
          AORJavaGui.this.progressBar.setMaximum(simulationSteps);
          AORJavaGui.this.simulationManager.getProject()
              .setTotalSimulationSteps(simulationSteps);

          // get the step time from the simulation
          Integer stepTimeDelay;
          if (AORJavaGui.this.tempStepTimeDelay > -1) {
            stepTimeDelay = AORJavaGui.this.tempStepTimeDelay;
          } else {
            stepTimeDelay = new Long(simulationManager.getProject()
                .getStepTimeDelay()).intValue();
          }
          // set it in the tool bar
          AORJavaGui.this.toolBarSimulation.setStepTime(stepTimeDelay);
          AORJavaGui.this.simulationManager.getProject().setStepTimeDelay(
              stepTimeDelay);

          try {

            // simulate the simulation project
            simulationManager.getProject().prepareSimulation();

            if (simulationManager.getProject().existSimulation()) {
              // run the simulation
              simulationManager.runSimulation();
            }
          } finally {

            // switch on/off the external log viewer button, depending if an
            // log file name was set in the project
            ((Menu) getJMenuBar()).getMenuItemByName(
                Menu.Item.EXTERNAL_LOG_VIEWER).setEnabled(
                !simulationManager.getProject().getLogFileName().equals(""));

            toolBarFile
                .getButtonByActionCommand(Menu.Item.EXTERNAL_LOG_VIEWER)
                .setEnabled(
                    !simulationManager.getProject().getLogFileName().equals(""));

            // save the latest log file name in the project, but do not save
            // the XML simulation description
            simulationManager.getProject().setSaved(false);
            simulationManager.getProject().save();

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
    if (simulationManager.getProject().getSimulation() != null) {
      // pause simulation
      simulationManager.getProject().getSimulation().pauseSimulation(true);
    }
  }

  private void changeLogger(String loggerSel) {

    int loggerType;

    if (DialogPreferences.XML_FULL_LOGGER.equals(loggerSel)) {
      loggerType = DataBus.LoggerType.FULL_XML_LOGGER;
    } else if (DialogPreferences.MEMORY_LOGGER.equals(loggerSel)) {
      loggerType = DataBus.LoggerType.MEMORY_LOGGER;
    } else if (DialogPreferences.NO_LOGGER.equals(loggerSel)) {
      loggerType = DataBus.LoggerType.OBSERVER_LOGGER;
    } else {
      loggerType = DataBus.LoggerType.DEFAULT_LOGGER;
    }

    if (loggerType != simulationManager.getDataBus().getLoggerType()) {

      if (this.simulationManager.getProject() == null) {

        this.simulationManager.setLoggerByDialogSelection(loggerType);
        // this.init();

      } else {

        int result = JOptionPane.showConfirmDialog(this,
            this.loggerChangeDialogMessage, this.loggerChangeDialogTitle,
            JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.OK_OPTION) {

          this.stopSimulation();
          this.simulationManager.setLoggerByDialogSelection(loggerType);
          // this.init();

        } else {

          try {
            this.preferences.selectLoggerRadioButton(Integer
                .valueOf(this.simulationManager.getProperties().getProperty(
                    SimulationManager.propertyLogger)));
          } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
          }

        }

      }
    }
  }

  private void stopSimulation() {

    if (this.simulationManager.getProject() == null)
      return;

    // start a thread in the background
    this.backgroundExecution.execute(new Runnable() {
      public void run() {
        try {
          // when the simulation is instantiated
          if (simulationManager.getProject().isSimulationInstantiated()) {
            long previousStepTimeDelay = simulationManager.getProject()
                .getSimulation().getStepTimeDelay();

            // delay the simulation step to 2 seconds
            simulationManager.getProject().getSimulation().setStepTimeDelay(
                1000);

            // get the current step
            long step = simulationManager.getProject().getSimulation()
                .getCurrentSimulationStep();

            simulationManager.getProject().getSimulation().stopSimulation();

            // update the max. step in the progress bar
            progressBar.setMaximum(new Long(step).intValue());

            // set the simulation step to it previous setting
            simulationManager.getProject().getSimulation().setStepTimeDelay(
                previousStepTimeDelay);

          }
        } finally {
        }
      }
    });
  }

  private void newProject() {
    String selection = (String) JOptionPane.showInputDialog(this,
        "Please select:", "New Project", JOptionPane.QUESTION_MESSAGE, null,
        this.optionsNewProject, this.optionOpenExistentDescription);

    // if anything useful was selected, except cancel or the X
    if (selection != null) {

      boolean sucessfulSelection = false;

      while (!sucessfulSelection) {
        if (selection.equals(this.optionEmptyProject)) {

          // close the previous project
          this.closeProject();

          // create a new project
          this.simulationManager.newProject();
          this.setTitle(this.guiTitle + " - " + "New Project");
          this.tabAORSL.getEditorPane().setText("");
          this.fileChooser.setSelectedFile(null);
          this.simulationManager.getProject().setSimulationDescription("");
          this.simulationManager.getProject().setSaved(true);

          // enable the tab, when no external is used
          this.tabPane.setEnabledAt(tabAORSLIndex, !this.preferences
              .isExternalXMLEditor());

          // enable the internal editor, when no external is used
          this.tabAORSL.getEditorPane().setEnabled(
              !this.preferences.isExternalXMLEditor());

          // switch the list of menu items on
          ((Menu) this.getJMenuBar()).switchMenuItems(Arrays.asList(
              Menu.Item.SAVE, Menu.Item.SAVE_AS, Menu.Item.CLOSE,
              Menu.Item.IMPORT_XML, Menu.Item.BUILD_ALL,
              Menu.Item.VALIDATE_ONLY, Menu.Item.GENERATE_ONLY), true);

          this.toolBarFile.enableButton(Menu.Item.SAVE, true);

          sucessfulSelection = true;
          // new project from existent simulation description
        } else if (selection.equals(this.optionOpenExistentDescription)) {
          // close the previous project
          if (this.closeProject()) {
            // create a new project
            this.simulationManager.newProject();
            // if importing the XML is successful
            if (this.importXML()) {
              // enable all tabs
              for (int tabIndex = 0; tabIndex < this.tabPane.getTabCount(); tabIndex++) {
                this.tabPane.setEnabledAt(tabIndex, true);
              }

              // initialize data from XML for module
              this.simulationManager.initializeDom(this.simulationManager
                  .getProject().getSimulationDescription());

              this.setTitle(this.guiTitle + " - Not Saved");

              // when an external XML editor / log viewer is preferred
              if (preferences.isExternalXMLEditor()) {
                // change the title of the AORSL tab
                this.tabPane.setTitleAt(this.tabAORSLIndex,
                    this.tabAORSLNameExternalEditor);

                // switch on the menu items for the external functionality
                ((Menu) this.getJMenuBar()).switchMenuItems(Arrays.asList(
                    Menu.Item.EXTERNAL_XML_EDITOR,
                    Menu.Item.EXTERNAL_LOG_VIEWER), true);

                // switch on the regarding buttons
                this.toolBarFile.getButtonByActionCommand(
                    Menu.Item.EXTERNAL_XML_EDITOR).setEnabled(true);
                this.toolBarFile.getButtonByActionCommand(
                    Menu.Item.EXTERNAL_LOG_VIEWER).setEnabled(true);

                // switch on/off the external log viewer, depending if an log
                // file
                // name was set in the project
                ((Menu) this.getJMenuBar()).getMenuItemByName(
                    Menu.Item.EXTERNAL_LOG_VIEWER)
                    .setEnabled(
                        !simulationManager.getProject().getLogFileName()
                            .equals(""));

                // disable the text area for editing
                this.tabAORSL.getEditorPane().setEnabled(false);

              } else {
                // change the title of the AORSL tab
                this.tabPane.setTitleAt(this.tabAORSLIndex, this.tabAORSLName);

                // enable the internal editor
                // this.tabAORSL.getEditorPane().setEnabled(true);

                // disable external functionality
                ((Menu) this.getJMenuBar()).switchMenuItems(Arrays.asList(
                    Menu.Item.EXTERNAL_XML_EDITOR,
                    Menu.Item.EXTERNAL_LOG_VIEWER), false);
                this.toolBarFile.getButtonByActionCommand(
                    Menu.Item.EXTERNAL_XML_EDITOR).setEnabled(false);
                this.toolBarFile.getButtonByActionCommand(
                    Menu.Item.EXTERNAL_LOG_VIEWER).setEnabled(false);

                // enable the text area for reading
                this.tabAORSL.getEditorPane().setEnabled(true);

              }

            } else {
              this.closeProject();
            }

          } else {
            System.err.println("Can not close the current project.");
          }
          sucessfulSelection = true;
        }// if-else
      }// while

    }
  }

  private void openProject() {
    // first close the current project if is one open...
    if (this.closeProject()) {

      // show only directories (means projects)
      this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      // set extensions filter for projects
      this.fileChooser.setFileFilter(this.projectExtensionFilter);

      // set button text
      this.fileChooser.setApproveButtonText("Open Project");

      this.fileChooser.setCurrentDirectory(this.getCurrentUserDir());

      // show the loading message dialog
      // NOTE: this line can't go after this call :
      // fileChooser.showOpenDialog(this) while does not work anymore until
      // the loading finishes...must find why ?!
      loadingDialog.setVisible(true);

      int response = fileChooser.showOpenDialog(this);
      if (response == JFileChooser.APPROVE_OPTION) {

        // if the selection was successful
        if (fileChooser.getSelectedFile().isDirectory()) {

          // if the simulator loads the project property
          if (this.simulationManager.loadProject(fileChooser.getSelectedFile()
              .getAbsolutePath())) {

            // observe the used file
            this.fileMonitor.addFile(this.simulationManager.getProject()
                .getSimulationDescriptionFile());

            // set current user directory
            this.setCurrentUserDir(fileChooser.getSelectedFile().getParent());

            // set the projects simulation description as content for the editor
            // area
            this.tabAORSL.getEditorPane().setText(
                this.simulationManager.getProject().getSimulationDescription());

            // show file name in the window title
            this.setTitle(this.guiTitle + " - "
                + fileChooser.getSelectedFile().getName());

            // The simulation is already builded. Initialize it and announce
            // modules. This is the point when the modules are asked to
            // initialize on an build project is open.
            if (simulationManager.getProject().isCompiled()) {

              // instantiate the simulation
              simulationManager.getProject().instantiateSimulation();

              // initialize data from XML for module
              simulationManager.initializeDom(simulationManager.getProject()
                  .getSimulationDescription());

              // prepare the simulation project
              simulationManager.getProject().prepareSimulationWithoutLogFile();

            }

            // switch the list of menu items on
            ((Menu) this.getJMenuBar()).switchMenuItems(Arrays.asList(
                Menu.Item.SAVE, Menu.Item.SAVE_AS, Menu.Item.CLOSE,
                Menu.Item.IMPORT_XML, Menu.Item.EXPORT_XML,
                Menu.Item.BUILD_ALL, Menu.Item.VALIDATE_ONLY,
                Menu.Item.GENERATE_ONLY), true);

            this.toolBarFile.enableButton(Menu.Item.SAVE, true);

            // when the project is already generated, switch on the compile
            // command
            ((Menu) this.getJMenuBar()).getMenuItemByName(
                Menu.Item.COMPILE_ONLY).setEnabled(
                simulationManager.getProject().isGenerated());

            // when the project is already compiled
            if (simulationManager.getProject().isCompiled()) {
              // enable the run button for the simulation
              toolBarSimulation.enableButton(Menu.Item.RUN, true);

              // allow project export as JAR file & enable run menu item
              ((Menu) this.getJMenuBar()).switchMenuItems(Arrays.asList(
                  Menu.Item.EXPORT_JAR, Menu.Item.RUN), true);

            } else {
              toolBarSimulation.enableButton(Menu.Item.RUN, false);
              ((Menu) this.getJMenuBar()).switchMenuItems(Arrays
                  .asList(Menu.Item.RUN), false);

            }

            // enable all tabs
            for (int tabIndex = 0; tabIndex < this.tabPane.getTabCount(); tabIndex++) {
              this.tabPane.setEnabledAt(tabIndex, true);
            }

            // enable simulatio toolbar editable components
            this.toolBarSimulation
                .enableSimulationToolBarEditableComponents(true);

            // when the user preferred an external XML editor
            if (this.preferences.isExternalXMLEditor()) {
              // change the title of the AORSL tab
              this.tabPane.setTitleAt(this.tabAORSLIndex,
                  this.tabAORSLNameExternalEditor);

              // disable the internal editor, read-only now
              this.tabAORSL.getEditorPane().setEnabled(false);

              // switch on the menu items for the external functionality
              ((Menu) this.getJMenuBar()).switchMenuItems(
                  Arrays.asList(Menu.Item.EXTERNAL_XML_EDITOR,
                      Menu.Item.EXTERNAL_LOG_VIEWER), true);

              // switch on the regarding buttons
              this.toolBarFile.getButtonByActionCommand(
                  Menu.Item.EXTERNAL_XML_EDITOR).setEnabled(true);

              // switch on/off the external log viewer, depending if an log file
              // name was set in the project
              ((Menu) this.getJMenuBar()).getMenuItemByName(
                  Menu.Item.EXTERNAL_LOG_VIEWER).setEnabled(
                  !simulationManager.getProject().getLogFileName().equals(""));
              // switch on or off depending on the preferences
              this.toolBarFile.getButtonByActionCommand(
                  Menu.Item.EXTERNAL_LOG_VIEWER).setEnabled(
                  !simulationManager.getProject().getLogFileName().equals(""));

              // disable the text area for editing
              this.tabAORSL.getEditorPane().setEnabled(false);

            } else {
              // change the title of the AORSL tab
              this.tabPane.setTitleAt(this.tabAORSLIndex, this.tabAORSLName);

              // enable the internal editor
              // this.tabAORSL.getEditorPane().setEnabled(true);

              // disable external functionality
              ((Menu) this.getJMenuBar()).switchMenuItems(
                  Arrays.asList(Menu.Item.EXTERNAL_XML_EDITOR,
                      Menu.Item.EXTERNAL_LOG_VIEWER), false);
              this.toolBarFile.getButtonByActionCommand(
                  Menu.Item.EXTERNAL_XML_EDITOR).setEnabled(false);
              this.toolBarFile.getButtonByActionCommand(
                  Menu.Item.EXTERNAL_LOG_VIEWER).setEnabled(false);

              // enable the text area for editing
              this.tabAORSL.getEditorPane().setEnabled(true);
            }
          } else {
            System.out
                .println("The selected directory didn't contain a simulation project.");
          }

        }
      }
    }

    // finished loading, hide the loading message dialog
    loadingDialog.setVisible(false);
  }

  private File getCurrentUserDir() {
    File file = null;

    String lastUserDir = this.simulationManager.getProperties().getProperty(
        "lastUserDir");

    if (lastUserDir != null) {
      file = new File(lastUserDir);
      if (file.exists())
        return file;
    }
    file = new File(System.getProperty("user.dir") + File.separator
        + SimulationManager.PROJECT_DIRECTORY);
    return file;
  }

  private void setCurrentUserDir(String path) {
    this.simulationManager.getProperties().setProperty("lastUserDir", path);
  }

  private boolean saveProject() {

    // announce listeners about DOM reinitialization
    this.simulationManager.initializeDom(this.tabAORSL.getEditorPane()
        .getText());

    // show only directories (means projects)
    // this.fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    // set extensions filter for projects
    this.fileChooser.setFileFilter(this.projectExtensionFilter);

    // set button text
    this.fileChooser.setApproveButtonText("Save Project");

    this.fileChooser.setCurrentDirectory(this.getCurrentUserDir());

    String selectedName = "";
    String selectedParentPath = "";

    boolean readyToSave = false;
    // if no project name set, indicate this
    if (this.simulationManager.getProject().getName() == null
        || this.simulationManager.getProject().getName().equals("")) {

      readyToSave = false;
    } else {
      readyToSave = true;
    }

    // as long as no proper project name is set
    while (!readyToSave) {

      int dialogStatus = this.fileChooser.showSaveDialog(this);
      // if "Save Project" was clicked
      if (dialogStatus == JFileChooser.APPROVE_OPTION) {

        selectedName = this.fileChooser.getSelectedFile().getName();
        selectedParentPath = this.fileChooser.getSelectedFile().getParent();
        this.setCurrentUserDir(selectedParentPath);

        // users directory selection is the projects name
        this.simulationManager.getProject().setName(selectedName);

        // set the path
        this.simulationManager.getProject().setDirectory(selectedParentPath);

        // if there is already a project with this name
        if (this.fileChooser.getSelectedFile().exists()) {
          // ask the user to confirm overwriting
          int overwriteExistentProject = JOptionPane.showConfirmDialog(this,
              messageProjectAlreadyExist, "", JOptionPane.YES_OPTION);
          // if overwriting is confirmed
          if (overwriteExistentProject == JOptionPane.YES_OPTION) {
            // store the current simulation description in the project
            this.simulationManager.getProject().setSimulationDescription(
                this.tabAORSL.getEditorPane().getText());

            // show the project name in the GUI title
            this.setTitle(this.guiTitle);

            // ready save the project
            readyToSave = true;

          } else {

            // reset name to empty, this forces the user
            // to type in an project name again
            this.simulationManager.getProject().setName("");
          }// if
          // when there is not yet a project with this name
        } else {
          // ready save the project
          readyToSave = true;

        }
        // when "Cancel" was clicked
      } else {
        // break the while loop and return an failure
        return false;
      }
    }// while

    if (readyToSave) {
      // when no external XML editor is used
      if (!this.preferences.isExternalXMLEditor()) {
        // store the current simulation description in the project
        this.simulationManager.getProject().setSimulationDescription(
            this.tabAORSL.getEditorPane().getText());

        // announce the modules that the simulation DOM was changed
        this.simulationManager.initializeDom(this.tabAORSL.getEditorPane()
            .getText());
      }

      // set the "save the simulation description" flag, when NO external editor
      // is used
      this.simulationManager.getProject().setSaved(
          !this.preferences.isExternalXMLEditor());

      // set the may changed project name in the title
      this.setTitle(this.guiTitle + " - "
          + this.fileChooser.getSelectedFile().getName());

      // save the project, either with or without the simulation description
      this.simulationManager.getProject().save();

      return true;
    } else {
      return false;
    }
  }

  private void saveProjectAs() {
    // reset the project name to empty
    this.simulationManager.getProject().setName("");
    // force to save this not named project -> GUI ask for name
    this.saveProject();

  }

  /**
   * 
   * Usage: This method is closing an open project. If there actual project has
   * no been save, the user is being asked if he want to save it.
   * 
   * Comments:
   * 
   */

  private boolean closeProject() {
    // TODO: simplifications are necessary here

    boolean successful = false;

    this.tempSimSteps = -1;
    this.tempStepTimeDelay = -1;

    this.toolBarSimulation.enableSimulationToolBarEditableComponents(false);

    // if there is already a project
    if (this.simulationManager.getProject() != null) {
      // if the current project has not been saved yet
      if (!this.simulationManager.getProject().isSaved()) {
        // ask the user to save it
        int status = JOptionPane.showConfirmDialog(this,
            this.messageSaveExistendProject, "", JOptionPane.YES_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        // user clicked yes, to save the actual project
        if (status == JOptionPane.YES_OPTION) {
          // if the project was saved
          if (this.saveProject()) {
            successful = true;
          }
        } else {
          successful = true;
        }

      } else {
        successful = true;
      }

      // if the project is saved
      if (successful) {

        // clear the actual project
        this.simulationManager.clearProject();

        // disable the tabs
        this.tabAORSL.getEditorPane().setText("");
        this.tabAORSL.getEditorPane().setEnabled(false);
        for (int tabIndex = 0; tabIndex < this.tabPane.getTabCount(); tabIndex++) {
          this.tabPane.setEnabledAt(tabIndex, true);
        }
        this.tabPane.setSelectedIndex(tabAORSLIndex);

        // disable some menu items
        ((Menu) this.getJMenuBar()).switchMenuItems(Arrays.asList(
            Menu.Item.SAVE, Menu.Item.SAVE_AS, Menu.Item.CLOSE,
            Menu.Item.IMPORT_XML, Menu.Item.EXPORT_XML, Menu.Item.EXPORT_JAR,
            Menu.Item.BUILD_ALL, Menu.Item.VALIDATE_ONLY,
            Menu.Item.GENERATE_ONLY, Menu.Item.COMPILE_ONLY,
            Menu.Item.EXTERNAL_XML_EDITOR, Menu.Item.EXTERNAL_LOG_VIEWER),
            false);

        // disable some buttons
        this.toolBarSimulation.enableButton(Menu.Item.RUN, false);
        this.toolBarFile.enableButton(Menu.Item.SAVE, false);
        this.toolBarFile.enableButton(Menu.Item.EXTERNAL_XML_EDITOR, false);
        this.toolBarFile.enableButton(Menu.Item.EXTERNAL_LOG_VIEWER, false);

        // reset steps and delay to 0
        AORJavaGui.this.toolBarSimulation.setSimulationSteps(0);
        AORJavaGui.this.toolBarSimulation.setStepTime(0);

        // reset current iterations
        this.currentSimulationIterationNumber = 0;

        // reset max iterations
        this.maxSimulationIterationsNumber = 1;

        // update iterations spinner box
        AORJavaGui.this.toolBarSimulation
            .setSimulationIterationsNumber(this.maxSimulationIterationsNumber);

        // update iterations label
        this.updateCurrentSimulationIterationsLabel(
            this.currentSimulationIterationNumber,
            this.maxSimulationIterationsNumber);

        // clear the console
        this.tabAORSL.getOutputTextPane().setText("");

        // reset to default title
        this.setTitle(this.guiTitle);

        // remove files from file monitor
        this.fileMonitor.removeAllFiles();
      }

      return true;

    } else {
      // nothing to close
      return true;
    }
  }

  private boolean importXML() {

    // if there is already an project in use
    if (this.simulationManager.getProject() != null) {
      String importedSimDesc = this.loadTextFromFile();

      // if something was loaded
      if (importedSimDesc != null && !importedSimDesc.equals("")) {
        // set the new simulation description
        this.simulationManager.getProject().setSimulationDescription(
            importedSimDesc);

        this.tabAORSL.getEditorPane().setText(
            this.simulationManager.getProject().getSimulationDescription());

        // enable some menu items
        ((Menu) this.getJMenuBar()).switchMenuItems(Arrays.asList(
            Menu.Item.SAVE, Menu.Item.SAVE_AS, Menu.Item.CLOSE,
            Menu.Item.IMPORT_XML, Menu.Item.EXPORT_XML, Menu.Item.EXPORT_JAR,
            Menu.Item.BUILD_ALL, Menu.Item.VALIDATE_ONLY,
            Menu.Item.GENERATE_ONLY, Menu.Item.COMPILE_ONLY), true);

        this.toolBarFile.enableButton(Menu.Item.SAVE, true);

        // disable some menu items
        ((Menu) this.getJMenuBar()).switchMenuItems(Arrays
            .asList(Menu.Item.COMPILE_ONLY), false);

        return true;
        // if nothing was loaded
      } else {
        return false;
      }
      // if no project is active
    } else {
      return false;
    }
  }

  private String loadTextFromFile() {
    this.fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

    this.fileChooser.setCurrentDirectory(new File(System
        .getProperty("user.dir")));

    // set extensions filter for simulations
    this.fileChooser.setFileFilter(this.simulationExtensionFilter);

    this.fileChooser.setApproveButtonText("Open Description");

    // show the open dialog
    int dialogStatus = this.fileChooser.showOpenDialog(this);

    // when the user selected a file
    if (dialogStatus == JFileChooser.APPROVE_OPTION) {
      // if the selection is a file
      if (this.fileChooser.getSelectedFile().isFile()) {
        // load and return the XML content with the help of the simulator
        // instance
        File file = this.fileChooser.getSelectedFile();
        if (this.simulationManager.getProject() != null)
          this.simulationManager.getProject().setSimulationDescriptionFilePath(
              file.getAbsolutePath());
        this.simulationManager.getProject().setSimulationDescriptionFile(file);
        this.fileMonitor.addFile(file);
        String strFromFile = this.simulationManager.readXMLFile(file);
        if (!strFromFile.equals(""))
          this.fileMonitor.addFile(file);
        return strFromFile;
      }
    }
    // otherwise
    return "";
  }

  private void printDiagnostics() {
    DiagnosticCollector<? extends JavaFileObject> diagnostics = this.simulationManager
        .getProject().getDiagnosticCollector();
    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics
        .getDiagnostics()) {
      // display only compiler errors that prevent the compilation
      if (diagnostic.getKind().equals(Diagnostic.Kind.ERROR)) {
        System.err.println("\n" + diagnostic.getMessage(null));
      } else {
        warning.println("\n" + diagnostic.getMessage(null));
      }

    }
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
    if (evt.getActionCommand().equals(Menu.Item.EXIT)) {
      // !!! DO NOT FORGET TO MODIFY THE ACTION LISTENER FOR THE X BUTTON
      // IN TOP OF THIS CLASS AS WELL, WHEN YOU CHANGE SOMETHING HERE !!!
      if (this.showExitDialog() == JOptionPane.YES_OPTION) {
        this.setVisible(false);
        this.dispose();
        // save all properties
        try {
          simulationManager.storeProperties();
        } finally {
          System.exit(0);
        }

      }
      // click on: new icon & menu entry
    } else if (evt.getActionCommand().equals(Menu.Item.NEW)) {
      this.newProject();

      // click on: open icon & menu entry
    } else if (evt.getActionCommand().equals(Menu.Item.OPEN)) {
      this.openProject();

    } else if (evt.getActionCommand().equals(Menu.Item.SAVE)) {
      this.saveProject();

    } else if (evt.getActionCommand().equals(Menu.Item.SAVE_AS)) {
      this.saveProjectAs();

    } else if (evt.getActionCommand().equals(Menu.Item.CLOSE)) {
      this.closeProject();

    } else if (evt.getActionCommand().equals(Menu.Item.IMPORT_XML)) {
      this.importXML();

    } else if (evt.getActionCommand().equals(Menu.Item.EXPORT_JAR)) {
      JOptionPane.showMessageDialog(this, messageNotImplemented, "",
          JOptionPane.INFORMATION_MESSAGE);

    } else if (evt.getActionCommand().equals(Menu.Item.EXPORT_XML)) {
      JOptionPane.showMessageDialog(this, messageNotImplemented, "",
          JOptionPane.INFORMATION_MESSAGE);

      // click on: preferences
    } else if (evt.getActionCommand().equals(Menu.Item.PREFERENCES)
        | evt.getActionCommand().equals(
            AORJavaGui.CONTEXT_MENU_ITEM_PREFERENCES)) {
      this.showPreferencesDialog();

      // click on: build all menu entry
    } else if (evt.getActionCommand().equals(Menu.Item.BUILD_ALL)) {
      this.build();

      // click on: validate simulation menu entry
    } else if (evt.getActionCommand().equals(Menu.Item.VALIDATE_ONLY)) {
      this.validateXML();

      // click on: generate only menu entry
    } else if (evt.getActionCommand().equals(Menu.Item.GENERATE_ONLY)) {
      this.generate();

      // click on: generate only menu entry
    } else if (evt.getActionCommand().equals(Menu.Item.COMPILE_ONLY)) {
      this.compile();

      // click on: run icon & menu entry
    } else if (evt.getActionCommand().equals(Menu.Item.RUN)) {
      // run the simulation
      this.runSimulation();

    } else if (evt.getActionCommand().equals(Menu.Item.PAUSE)) {
      this.pauseSimulation();

      // click on: stop icon & menu entry
    } else if (evt.getActionCommand().equals(Menu.Item.STOP)) {

      // the user has stopped the simulation using the STOP button
      this.simulationStoppedByUser = true;

      // reset current simulation iteration to 0
      this.currentSimulationIterationNumber = 0;

      // stop simulation
      this.stopSimulation();

    } else if (evt.getActionCommand().endsWith(Menu.Item.ABOUT)) {
      String htmlContent = this.simulationManager.readXMLFile(new File(
          aboutHTMLFileName));
      new DialogHtml(this, htmlContent, "About");

    } else if (evt.getActionCommand().equals("comboBoxChanged")) {
      String lookAndFeelName = (String) ((JComboBox) evt.getSource())
          .getSelectedItem();

      this.setLookAndFeel(lookAndFeelName);

      // when the OK button of the preferences dialog was pressed
    } else if (evt.getActionCommand().equals(
        DialogPreferences.OK_BUTTON_ACTION_COMMAND)) {
      this.preferences.setVisible(false);

      // persist the external XML editor location of the preferences dialog in
      // the properties of the simulator the GUI doesn't have its own properties
      this.simulationManager.getProperties().put(this.propertySwingLookAndFeel,
          this.preferences.getSelectedLookAndFeel());

      this.simulationManager.getProperties().put(
          this.propertyUseExternalXMLEditor,
          Boolean.toString(this.preferences.isExternalXMLEditor()));

      this.simulationManager.getProperties().put(
          this.propertyUseExternalLogViewer,
          Boolean.toString(this.preferences.isExternalLogViewer()));

      // when an external editor is used
      if (this.preferences.isExternalXMLEditor()) {
        // disable the save functions, to avoid overwriting the AORSL from the
        // GUI
        ((Menu) this.getJMenuBar()).switchMenuItems(Arrays.asList(
            Menu.Item.SAVE, Menu.Item.SAVE_AS), false);

        this.tabAORSL.getEditorPane().setEnabled(false);
        this.tabPane.setTitleAt(this.tabAORSLIndex,
            this.tabAORSLNameExternalEditor);
        // when the internal editor is used
      } else {
        if (this.simulationManager.getProject() != null) {

          // enable some menu items
          ((Menu) this.getJMenuBar()).switchMenuItems(Arrays.asList(
              Menu.Item.SAVE, Menu.Item.SAVE_AS), true);
          this.tabPane.setEnabledAt(this.tabAORSLIndex, true);
          this.tabAORSL.getEditorPane().setEnabled(true);
        }
        this.tabPane.setTitleAt(this.tabAORSLIndex, this.tabAORSLName);
      }

      this.simulationManager.getProperties().put(
          this.propertyExternalXMLEditorLocation,
          this.preferences.getExternalXMLEditorLocation());

      this.simulationManager.setAutoMultithreading(this.preferences
          .isMultithreading());

      // enable the external editor button/menu items only if there is an
      // project loaded
      if (this.simulationManager.getProject() != null) {
        // if an external editor location was set
        if (!preferences.getExternalXMLEditorLocation().equals("")) {

          // enable / disable the menu items
          ((Menu) this.getJMenuBar()).getMenuItemByName(
              Menu.Item.EXTERNAL_XML_EDITOR).setEnabled(
              preferences.isExternalXMLEditor());

          // enable / disable the tool bar items
          this.toolBarFile.getButtonByActionCommand(
              Menu.Item.EXTERNAL_XML_EDITOR).setEnabled(
              preferences.isExternalXMLEditor());

          // switch on/off the external log viewer, depending if an log file
          // name was set in the project and an editor for external files is set
          boolean extLog = false;
          if (!simulationManager.getProject().getLogFileName().equals("")
              && preferences.isExternalLogViewer()) {
            extLog = true;
          }
          // switch on or off the menu item
          ((Menu) this.getJMenuBar()).getMenuItemByName(
              Menu.Item.EXTERNAL_LOG_VIEWER).setEnabled(extLog);

          // switch on or off the button
          this.toolBarFile.getButtonByActionCommand(
              Menu.Item.EXTERNAL_LOG_VIEWER).setEnabled(extLog);

        }

      }

      this.changeLogger(this.preferences.getLoggerSelection());

      // when the external editor button / menu item was clicked
    } else if (evt.getActionCommand().equals(Menu.Item.EXTERNAL_XML_EDITOR)) {

      if (this.simulationManager.getProject().isCreatedOnDisk()) {
        if (this.preferences.getExternalXMLEditorLocation() != "") {

          File externalEditor = new File(this.preferences
              .getExternalXMLEditorLocation());

          if (externalEditor.exists()) {

            this.backgroundExecution.execute(new Runnable() {
              public void run() {
                try {

                  Process process;

                  BufferedReader bufferedReader;
                  String line;
                  try {
                    // execute the external program
                    String pathToSimulationDescription = "";
                    if (simulationManager.getProject() != null) {
                      pathToSimulationDescription = simulationManager
                          .getProject().getSimulationDescriptionFile()
                          .getAbsolutePath();
                    }

                    ProcessBuilder processBuilder = new ProcessBuilder();
                    processBuilder.command(Arrays.asList(preferences
                        .getExternalXMLEditorLocation(),
                        pathToSimulationDescription));

                    process = processBuilder.start();

                    // connect the output stream of the process to the Java
                    // output
                    // stream
                    bufferedReader = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                    while ((line = bufferedReader.readLine()) != null) {
                      System.out.println(line);
                    }
                    process.waitFor();

                  } catch (IOException e) {
                    e.printStackTrace();
                  } catch (InterruptedException e) {
                    System.err.println(e);
                  }

                } finally {
                  // TODO reload the XML description
                }

              }
            });

          } else {
            System.err
                .println("The specified external XML editor does not exist.");
          }
        } else {
          System.err.println("No external XML editor specified.");
        }
        // if the current project is not saved
      } else {
        int result = JOptionPane.showConfirmDialog(this,
            this.messageSaveProjectToDisk, "Save Project",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
          this.saveProject();
        }
      }
      // when the external log viewer button / menu item was clicked
    } else if (evt.getActionCommand().equals(Menu.Item.EXTERNAL_LOG_VIEWER)) {

      // if there is already an log file set
      if (!this.simulationManager.getProject().getLogFileName().equals("")) {
        if (this.preferences.getExternalXMLEditorLocation() != "") {

          File externalEditor = new File(this.preferences
              .getExternalXMLEditorLocation());

          if (externalEditor.exists()) {

            this.backgroundExecution.execute(new Runnable() {
              public void run() {
                try {
                  Process process;

                  BufferedReader bufferedReader;
                  String line;
                  try {
                    // execute the external program
                    String pathToLatestLogFile = "";
                    if (simulationManager.getProject() != null) {
                      pathToLatestLogFile = simulationManager.getProject()
                          .getDirectory()
                          + File.separator
                          + simulationManager.getProject().getName()
                          + File.separator
                          + Project.LOG_FOLDER_NAME
                          + File.separator
                          + simulationManager.getProject().getLogFileName();

                    }

                    ProcessBuilder processBuilder = new ProcessBuilder();
                    processBuilder.command(Arrays.asList(preferences
                        .getExternalXMLEditorLocation(), pathToLatestLogFile));

                    process = processBuilder.start();

                    // connect the output stream of the process to the Java
                    // output stream
                    bufferedReader = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                    while ((line = bufferedReader.readLine()) != null) {
                      System.out.println(line);
                    }
                    process.waitFor();

                  } catch (IOException e) {
                    e.printStackTrace();
                  } catch (InterruptedException e) {
                    System.err.println(e);
                  }

                } finally {

                }
              }
            });

          } else {
            System.err
                .println("The specified external log viewer does not exist.");
          }
        } else {
          System.err.println("No external log viewer specified.");
        }

        // if the current project is not saved
      } else {
        int result = JOptionPane.showConfirmDialog(this,
            this.messageNoLogAvailable, " ", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
          this.saveProject();
        }
      }

      // when in the context menu of the AORSL editor "Reload" was clicked
    } else if (evt.getActionCommand().equals(
        TabSimDescription.CONTEXT_MENU_ITEM_RELOAD)) {

      if (this.simulationManager.getProject() != null) {
        this.simulationManager.getProject().loadSimulationDescription();
        String aorsl = this.simulationManager.getProject()
            .getSimulationDescription();
        tabAORSL.getEditorPane().setText(aorsl);
      }

    } else {
      System.err.println("Unimplemented action event occured: "
          + evt.getActionCommand());

    }
  }

  @Override
  public void stateChanged(ChangeEvent evt) {
    if (evt.getSource() instanceof JSpinner
        && this.simulationManager.getProject() != null) {
      JSpinner spinner = (JSpinner) evt.getSource();
      Integer value = (Integer) spinner.getValue();
      // this.simulator.getProject().setStepTimeDelay(value);
      if (spinner.getName().equals(ToolBarSimulation.SIMULATION_STEP_SPINNER)) {
        this.tempSimSteps = value;
        this.simulationManager.getProject().setTotalSimulationSteps(value);
        // update the max. step in the progress bar
        progressBar.setMaximum(value);

      } else if (spinner.getName().equals(ToolBarSimulation.STEP_TIME_SPINNER)) {
        this.tempStepTimeDelay = value;
        this.simulationManager.getProject().setStepTimeDelay(value);
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

    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    if (compiler == null) {
      System.err.println(messageNoJavaCompiler);

      String htmlContent = new SimulationManager().readXMLFile(new File(
          noCompilerFoundHTMLFileName));
      new DialogHtml(null, htmlContent, "Error");
    } else {
      new AORJavaGui().setVisible(true);
    }

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
   * 
   * Usage: prints the information about allocated resources in statistic stream
   * 
   * 
   * Comments: only a temporary solution
   * 
   * 
   * 
   */
  private void printRessourceAllocation() {

    if (this.simulationManager.getProject() != null
        && this.simulationManager.getProject().getSimulation()
            .getEnvironmentSimulator() != null) {

      List<Objekt> objList = this.simulationManager.getProject()
          .getSimulation().getEnvironmentSimulator().getObjectsByType(
              Objekt.class);
      objList.addAll(this.simulationManager.getProject().getSimulation()
          .getEnvironmentSimulator().getObjectsByType(AgentObject.class));
      objList.addAll(this.simulationManager.getProject().getSimulation()
          .getEnvironmentSimulator().getObjectsByType(
              aors.model.envsim.PhysicalObject.class));
      objList.addAll(this.simulationManager.getProject().getSimulation()
          .getEnvironmentSimulator().getObjectsByType(
              aors.model.envsim.PhysicalAgentObject.class));

      ArrayList<Objekt> usedObjAsRes = new ArrayList<Objekt>();
      for (Objekt o : objList) {
        Set<String> activitieNames = o.getUsedByActivities();
        if (activitieNames.size() > 0)
          usedObjAsRes.add(o);
      }
    }
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
      toolBarSimulation.switchButton(Menu.Item.PAUSE, Menu.Item.RUN,
          Menu.Item.RUN_IMAGE);

      // enable the run button
      toolBarSimulation.enableButton(Menu.Item.RUN, true);

      // disable the pause button
      toolBarSimulation.enableButton(Menu.Item.PAUSE, false);

      // enable the stop button
      toolBarSimulation.enableButton(Menu.Item.STOP, true);

      // indicate in the GUI title with a pause state
      this.setTitle(this.guiTitle + " - "
          + this.simulationManager.getProject().getName() + " - Paused ");
    }
    // continue
    else {
      // change the run to a pause button
      toolBarSimulation.switchButton(Menu.Item.RUN, Menu.Item.PAUSE,
          Menu.Item.PAUSE_IMAGE);

      // disable the run button
      toolBarSimulation.enableButton(Menu.Item.RUN, false);

      // enable the pause button
      toolBarSimulation.enableButton(Menu.Item.PAUSE, true);

      // enable the stop button
      toolBarSimulation.enableButton(Menu.Item.STOP, true);

      // indicate in the GUI title with no pause state
      this.setTitle(this.guiTitle + " - "
          + this.simulationManager.getProject().getName());

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
    this.toolBarSimulation.switchButton(Menu.Item.RUN, Menu.Item.PAUSE,
        Menu.Item.PAUSE_IMAGE);

    // disable the run button
    this.toolBarSimulation.enableButton(Menu.Item.RUN, false);

    // enable the pause button
    this.toolBarSimulation.enableButton(Menu.Item.PAUSE, true);

    // enable the stop button
    this.toolBarSimulation.enableButton(Menu.Item.STOP, true);

    // disable new and open buttons
    this.toolBarFile.enableButton(Menu.Item.NEW, false);
    this.toolBarFile.enableButton(Menu.Item.OPEN, false);
    this.toolBarFile.enableButton(Menu.Item.SAVE, false);

    // the user has not stopp the simulation...yet
    this.simulationStoppedByUser = false;

    // disable all not usable menu items
    ((Menu) this.getJMenuBar()).switchMenuItems(Arrays.asList(Menu.Item.NEW,
        Menu.Item.OPEN, Menu.Item.CLOSE, Menu.Item.IMPORT_XML,
        Menu.Item.BUILD_ALL, Menu.Item.COMPILE_ONLY), false);

    // display console messages
    System.out.print("\n" + messageLoadClasses + "  "
        + currentSimulationIterationNumber + "/"
        + maxSimulationIterationsNumber + " :  ");
    success.println("Ok");

    // stop the file monitor task
    this.stopFileMonitorTask();

    System.out.println(messageSimulationStarted);
  }

  @Override
  public void simulationEnded() {

    System.out.println(messageSimulationFinished);

    // change the run to a pause button
    toolBarSimulation.switchButton(Menu.Item.PAUSE, Menu.Item.RUN,
        Menu.Item.RUN_IMAGE);

    // disable the pause button
    toolBarSimulation.enableButton(Menu.Item.PAUSE, false);

    // disable the pause button
    toolBarSimulation.enableButton(Menu.Item.RUN, true);

    // enable the stop button
    toolBarSimulation.enableButton(Menu.Item.STOP, false);

    // enable new and open buttons
    this.toolBarFile.enableButton(Menu.Item.NEW, true);
    this.toolBarFile.enableButton(Menu.Item.OPEN, true);
    this.toolBarFile.enableButton(Menu.Item.SAVE, true);

    // enable the spinner for simulation iterations
    toolBarSimulation.enableSimulationToolBarEditableComponents(true);

    // enable all not usable menu items
    ((Menu) getJMenuBar()).switchMenuItems(Arrays.asList(Menu.Item.NEW,
        Menu.Item.OPEN, Menu.Item.CLOSE, Menu.Item.IMPORT_XML,
        Menu.Item.BUILD_ALL, Menu.Item.COMPILE_ONLY), true);

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

    // update the label with curent iteration time
    updateLastIterationTimeLabel(simulationTime);

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

    // output informations about resources in activities (only
    // temporary)
    this.printRessourceAllocation();
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

  /**
   * Usage:
   * 
   * 
   * Comments: Overrides method {@code fileChange} from super class
   * 
   * 
   * 
   * @param file
   */
  @Override
  public void fileChange(File file) {
    String messageTextChangeFile = "This file was changed externaly. \nDo you want to relaod it?";
    String messageTextRebuild = "Simulation rebuild is recommended. \nDo you want to rebuild the simulation?";
    this.stopFileMonitorTask();
    int confirmAnswer = JOptionPane.showConfirmDialog(this,
        messageTextChangeFile, "Information", JOptionPane.YES_NO_OPTION);
    if (confirmAnswer == JOptionPane.YES_OPTION) {
      String xmlScenario = this.simulationManager.readXMLFile(file);
      // System.out.println(MD5Generator.getMD5("MD5: " + xmlScenario));

      if (this.simulationManager.getProject() != null) {
        this.simulationManager.getProject().setSimulationDescription(
            xmlScenario);
        if (this.simulationManager.getProject().isGenerated()
            && !this.simulationManager.getProject().checkBuildStatus()) {
          confirmAnswer = JOptionPane.showConfirmDialog(this,
              messageTextRebuild, "Information", JOptionPane.YES_NO_OPTION);

          if (confirmAnswer == JOptionPane.YES_OPTION) {
            this.build();
          }

        }
      }
      this.tabAORSL.getEditorPane().setText(xmlScenario);
      this.simulationManager.initializeDom(xmlScenario);
      this.fileMonitor.updateFile(file);
    }
    this.startFileMonitorTask();
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
      AORJavaGui.this.toolBarSimulation.setStepTime(newStepTime);
      AORJavaGui.this.simulationManager.getProject().setStepTimeDelay(
          newStepTime);
    }
    if (moduleEvent instanceof ModuleEventSlowDownSimulation) {
      int newStepTime = actualStepDelay + slowDownTimeValue;

      // set new step time delay
      AORJavaGui.this.toolBarSimulation.setStepTime(newStepTime);
      AORJavaGui.this.simulationManager.getProject().setStepTimeDelay(
          newStepTime);
    }
  }

}
