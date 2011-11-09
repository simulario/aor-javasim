package aors.controller.webstart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.swing.JScrollPane;

import aors.controller.InitialState;
import aors.controller.SimulationDescription;
import aors.data.DataBus;
import aors.data.DataBusInterface;
import aors.module.GUIModule;
import aors.module.Module;

/**
 * This class represents the manager of the simulation.
 * 
 * SimulationManager
 * 
 * @author Jens Werner
 * @since 08.02.2010
 * @version $Revision$
 */
public class WebStartSimulationManager {

  // Simulation
  private final String SIMULATION = "controller.Simulator";

  private String scenarioName = "";

  // the current project assigned to the simulation manager
  private static WebStartProject project;

  // the list with all current loaded modules
  private ArrayList<Module> modules;

  // the projects directory
  public final static String PROJECT_DIRECTORY = "projects";

  // the project properties object
  private Properties properties;

  // the property that defines the logger file name
  private final String propertyAutoMultithreading = "auto multithreading";
  private final String propertySimulation = "simulation";

  // property values (default values)
  private boolean autoMultithreading = false;
  private DataBus dataBus;

  /**
   * Create a new simulation manager.
   */
  public WebStartSimulationManager() {
    // create the data bus that will be the data bus of all components/listeners
    this.dataBus = new DataBus();

    this.initProperties();
    this.setSimulatorProperties();

    // initialize the modules/plugins
    this.modules = new ArrayList<Module>();
    this.initModules();

    System.out.println("Try to load SimulationDescription");
    if (this.loadProject()) {

      System.out.println("Try to instanciate the simulation");
      project.instantiateSimulation(this.properties
          .getProperty(propertySimulation));

      System.out.println("Try to init dom");
      this.initializeDom();

      System.out.println("Try to prepare the simulation");
      project.prepareSimulation();
    } else {
      System.err.println("Cant load a project!");
    }
  }

  /**
   * Initialize the modules found in the modules directory as jar files.
   */
  private void initModules() {

    // URL url2 =
    // this.getClass().getClassLoader().getResource("lib/batik-awt-util.jar");

    // try {
    // InputStream input =
    // this.getClass().getClassLoader().getResourceAsStream("aors/module/statistics/gui/lang/GuiComponent.properties");
    // File fout = new File("Test.jar");
    //      
    // BufferedOutputStream out = new BufferedOutputStream(
    // new FileOutputStream(fout));
    // byte buffer[] = new byte[1024];
    // int len;
    // for (int sum = 0; (len = input.read(buffer)) > 0; sum += len) {
    // out.write(buffer, 0, len);
    // }
    // input.close();
    // out.close();
    //
    // } catch (FileNotFoundException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // } catch (IOException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }

    Enumeration<URL> urlEnum = this.getModulePropertiesURLs();
    Properties modulProperties = new Properties();

    if (urlEnum != null && urlEnum.hasMoreElements()) {

      while (urlEnum.hasMoreElements()) {

        URL url = urlEnum.nextElement();
        modulProperties.clear();

        try {
          modulProperties.loadFromXML(url.openStream());

          String moduleClass = modulProperties
              .getProperty(Module.PROP_BASE_MODULE_CLASS);
          String tabTitle = modulProperties
              .getProperty(GUIModule.PROP_GUI_TITLE);

          this.loadModule(moduleClass, tabTitle);

        } catch (InvalidPropertiesFormatException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

      // this.loadNativeLibraries();

    } else {

      // workaround for eclipse (here are the jars not in the classpath)
      this.loadModule("aors.module.sound.SoundController", "Sound");
      this.loadModule("aors.module.statistics.StatisticsCore", null);
      this
          .loadModule("aors.module.visopengl.Visualization", "Visualization GL");

    }
  }

  private void loadModule(String moduleClass, String tabTitle) {

    if (moduleClass != null) {

      try {
        System.out.println("Try to load module: " + moduleClass);
        Module module = (Module) this.getClass().getClassLoader().loadClass(
            moduleClass.trim()).newInstance();

        this.modules.add(module);

        // module is a listener for simulation step events
        this.dataBus.addSimulationStepEventListener(module);

        // module is a listener for simulation events
        this.dataBus.addSimulationEventListener(module);

        // get the module tab title from the property file
        if (module.getGUIComponent() != null && tabTitle != null) {
          ((JScrollPane) module.getGUIComponent()).setName(tabTitle);
        }

      } catch (InstantiationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  /*
   * this call is necessary to load the right libraries for the web start
   * version
   */
  @SuppressWarnings("unused")
  private void loadNativeLibraries() {

    // try to load native libraries
    String os = System.getProperty("os.name");
    System.out.println("Loading " + os + " native libraries ..");

    // load the native libraries for windows
    if (os.startsWith("Windowss")) {

      try {
        // check if the OpenGL Visualization was loaded (this works only outside
        // from eclipse!!!)
        this.getClass().getClassLoader().loadClass(
            "aors.module.visopengl.Visualization");
        String[] libsWin = { "gluegen-rt.dll", "jogl_gl2.dll",
            "nativewindow_awt.dll", "nativewindow_jvm.dll", "newt.dll",
            "jogl_cg.dll" };
        for (String nativeLib : libsWin) {

          try {
            System.out.println("  " + nativeLib);
            System.loadLibrary(nativeLib.substring(0, nativeLib
                .lastIndexOf(".")));
          } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
          }
        }

      } catch (ClassNotFoundException e1) {
        System.out.println(e1.getLocalizedMessage() + " is not exist!");
      }
    } else if (os.equals("Linux")) {

      try {
        // check if the OpenGL Visualization was loaded (this works only outside
        // from eclipse)
        this.getClass().getClassLoader().loadClass(
            "aors.module.visopengl.Visualization");
        // Libraries for UNIX
        String[] libsUnix = { "libgluegen-rt.so", "libjogl_cg.so",
            "libjogl_gl2.so", "libnativewindow_awt.so",
            "libnativewindow_jvm.so", "libnativewindow_x11.so", "libnewt.so" };
        for (String nativeLib : libsUnix) {

          try {
            System.out.println("  " + nativeLib);
            System.loadLibrary(nativeLib.substring(0, nativeLib
                .lastIndexOf(".")));
          } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
          }
        }

      } catch (ClassNotFoundException e1) {
        System.out.println(e1.getLocalizedMessage() + " is not exist!");
      }

    }
  }

  /**
   * Gets the "global" data bus object
   * 
   * @return the "global" data bus object
   */
  public DataBusInterface getDataBus() {
    return this.dataBus;
  }

  /**
   * initialize the properties for this simulation
   * 
   */
  private void initProperties() {
    if (this.properties == null) {
      this.properties = new Properties();
      this.properties.put(propertyAutoMultithreading, autoMultithreading);
      this.properties.put(propertySimulation, this.SIMULATION);
    }
  }

  private Enumeration<URL> getModulePropertiesURLs() {
    try {
      return this.getClass().getClassLoader().getResources("properties.xml");
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private void setSimulatorProperties() {
    if (this.properties != null) {
      String value = this.properties.getProperty(propertyAutoMultithreading);
      this.setAutoMultithreading(Boolean.valueOf(value));
    }

  }

  /**
   * Gets the properties of the project
   * 
   * @return the project properties object.
   */
  public Properties getProperties() {
    return properties;
  }

  /**
   * Load the project from a given path
   * 
   * @param projectPath
   *          the path of the project to load
   * @return true if successful false otherwise
   */
  public boolean loadProject() {

    // new project instance
    project = new WebStartProject();
    project.setDataBus(dataBus);

    return project.loadSimulationDescription();
  }

  /**
   * Gets the actual project object
   * 
   * @return the current project object
   */
  public WebStartProject getProject() {
    return project;
  }

  /**
   * Clear the actual project and call garbage collector for clearing
   * unnecessarily occupied memory.
   */
  public void clearProject() {
    WebStartSimulationManager.project = null;

    // force garbage collector to free any remaining garbages...
    System.gc();
  }

  /**
   * 
   * Gets the state of automatic multi-threading state
   * 
   * @return true if auto-multi-threading is active, false otherwise
   */
  public boolean isAutoMultithreading() {
    return autoMultithreading;
  }

  /**
   * Activate/disable the automatic multi-threading
   * 
   * @param autoMultithreading
   *          the multi-threading state to set.
   */
  public void setAutoMultithreading(boolean autoMultithreading) {
    this.autoMultithreading = autoMultithreading;
  }

  /**
   * Return the state of the project, meaning that there is a project that was
   * already generated and compiled
   * 
   * @return true if project is ready, false otherwise
   */
  public static boolean isProjectReady() {
    if (project == null) {
      return false;
    }
    return true;
  }

  /**
   * Gets the existing modules.
   * 
   * @return the existing modules
   */
  public ArrayList<Module> getModules() {
    return modules;
  }

  /**
   * This method is called whenever the DOM is changed and need
   * re-initialization of initial state object.
   * 
   * @param simulationScenarioXML
   *          the XML simulation description as string representation
   */
  public void initializeDom() {
    // no data bus, this has to be a mistake...
    if (this.dataBus == null) {
      System.out.println("Warning: DataBus is null!");
      return;
    }
    String simulationScenarioXML = project.getSimulationDescription();

    if (simulationScenarioXML == null || simulationScenarioXML.equals(""))
      return;

    SimulationDescription simulationDescription = new SimulationDescription();
    simulationDescription.setDom(simulationScenarioXML);
    this.scenarioName = simulationDescription.getScenarioName();
    this.dataBus.notifyDomOnlyInitialization(simulationDescription);

    // no abstract simulator yet...
    if (this.getProject().getSimulation() == null) {
      return;
    }

    InitialState initialState = this.getProject().getSimulation()
        .getInitialState();
    // set the dom in initial state object
    // initialState.setDom(simulationScenarioXML);
    initialState.setSimulationDescription(simulationDescription);

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
   * @param filePath
   * @param fileName
   * @return
   */
  public String readTextFileFromJar(String filePath, String fileName) {
    ClassLoader cl = this.getClass().getClassLoader();
    String result = "";
    if (cl != null) {
      InputStream inputStream = cl.getResourceAsStream(filePath + '/'
          + fileName);
      System.out.println();
      if (inputStream == null)
        return "No Resource: " + filePath + '/' + fileName;
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
          inputStream));
      String line = "";
      try {
        while (null != (line = bufferedReader.readLine())) {
          result += line;
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          if (inputStream != null)
            inputStream.close();
          if (bufferedReader != null)
            bufferedReader.close();
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
      }

    }
    return result;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code scenarioName}.
   * 
   * 
   * 
   * @return the {@code scenarioName}.
   */
  public String getScenarioName() {
    return scenarioName;
  }
}
