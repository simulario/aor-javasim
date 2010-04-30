package aors.controller.webstart;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Date;

import aors.controller.AbstractSimulator;
import aors.controller.ProjectInterface;
import aors.data.DataBus;

/**
 * Project
 * 
 * This class describes/defines an simulator project.
 * 
 * @author Jens Werner
 * @since 08.02.2010
 * @version $Revision: 1.0 $
 */
public class WebStartProject implements ProjectInterface {

  // the project name
  private String projectName;

  // the project directory
  private String directory;

  // the simulation description content as String
  private String simulationDescription;

  // auto multi-threading is activated ?
  private boolean autoMultithreading;

  // the abstract simulator - used to run the simulation
  private AbstractSimulator simulation = null;

  // the class loader used for dynamic class loadings
  private URLClassLoader urlClassLoader;

  // the source folder (generated Java classes are stored inside)
  public final static String SRC_FOLDER_NAME = "src";

  // the binary folder (compiled classes are stored inside)
  public final static String BIN_FOLDER_NAME = "bin";

  // the log folder (log files are stored inside)
  public final static String LOG_FOLDER_NAME = "log";

  // the media folder name
  public final static String MEDIA_FOLDER_NAME = "media";

  // the images sub-folder name from media folder
  public final static String MEDIA_IMAGES_FOLDER_NAME = "images";

  // the sounds sub-folder name from media folder
  public final static String MEDIA_SOUNDS_FOLDER_NAME = "sounds";

  // the html sub-folder name for the media folder
  public final static String MEDIA_HTML_FOLDER_NAME = "html";

  // project property file
  public final static String PROJECT_FILE_NAME = "project.xs";

  // the prefix used for the log file
  private final String logFileNamePrefix = "log_";

  // the log file name (a prefix, see above is added to this name)
  private String logFileName = "";

  // the XML simulation description file name
  private String simDescXmlFileName = "scenario.xml";

  // the data bus that is assigned to this project
  private DataBus dataBus;

  // the main class that has to be executed for this simulation
  private final String simPackageAndClassName = "controller.Simulator";

  // the destination folder where the simulation is build on request
  private String destinationFolder;

  // the projects directory
  private final String PROJECTS_DIR = "projects";

  // user directory
  private final String USER_DIR = System.getProperty("user.dir");

  /**
   * Create a new project.
   */
  public WebStartProject() {
    this.projectName = "";
    this.directory = USER_DIR + File.separator + PROJECTS_DIR;

    this.simulationDescription = "";
    this.autoMultithreading = false;
  }

  /**
   * Gets the name of the project.
   * 
   * @return the project name
   */
  public String getName() {
    return projectName;
  }

  /**
   * Sets a new name for this project.
   * 
   * @param name
   *          the new project name
   */
  public void setName(String name) {
    // if the project state has been changed
    this.projectName = name;
  }

  /**
   * Set a data bus for this project
   * 
   * @param dataBus
   *          the data bus to set
   */
  public void setDataBus(DataBus dataBus) {
    this.dataBus = dataBus;
  }

  /**
   * Gets the project directory
   * 
   * @return the project directory
   */
  public String getDirectory() {
    return directory;
  }

  /**
   * Set project directory
   * 
   * @param directory
   *          the directory to set
   */
  public void setDirectory(String directory) {
    this.directory = directory;
  }

  /**
   * Gets the String representation of the XML simulation description
   * 
   * @return the string representation of the XML simulation description
   */
  @Override
  public String getSimulationDescription() {
    return simulationDescription;
  }

  /**
   * Set a new simulation description as String representation
   * 
   * @param simulationDescription
   *          new simulation description string
   */
  public void setSimulationDescription(String simulationDescription) {
    this.simulationDescription = simulationDescription;
  }

  /**
   * Set the step delay for the simulation associated with this project.
   * 
   * @param milliseconds
   *          the step delay in milliseconds
   */
  public void setStepTimeDelay(long milliseconds) {
    if (this.simulation != null) {
      this.simulation.setStepTimeDelay(milliseconds);
    }
  }

  /**
   * Gets the step delay number in milliseconds
   * 
   * @return the simulation step delay in miliseconds
   */
  public long getStepTimeDelay() {
    if (this.simulation != null) {
      return this.simulation.getStepTimeDelay();
    } else {
      return 0;
    }
  }

  /**
   * Set a new number of simulation steps.
   * 
   * @param steps
   *          the new number of simulation steps
   */
  public void setTotalSimulationSteps(long steps) {
    if (this.simulation != null) {
      this.simulation.setTotalSimulationSteps(steps);
    }
  }

  /**
   * Gets the number of total simulation steps.
   * 
   * @return the total number of simulation steps.
   */
  public long getTotalSimulationSteps() {
    if (this.simulation != null) {
      return this.simulation.getTotalSimulationSteps();
    } else {
      return 0;
    }
  }

  /**
   * Returns true if the simulation was instantiated, false otherwise
   * 
   * @return true if simulation is instantiated, false otherwise
   */
  public boolean isSimulationInstantiated() {
    if (this.simulation == null) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Gets the url class loader of this project. This is used externally for
   * modules which may need to load generated and compiled classes
   * 
   * @return the urlCLassLoader object, or null if not yet created.
   */
  public URLClassLoader getUrlClassLoader() {
    // create URL from the class folder
    URL[] generatedClassesFolder;
    try {
      generatedClassesFolder = new URL[] { new File(destinationFolder
          + File.separator).toURI().toURL() };

      // instantiate an class loader for these classes
      if (this.urlClassLoader == null) {
        this.urlClassLoader = URLClassLoader.newInstance(
            generatedClassesFolder, ClassLoader.getSystemClassLoader());
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }

    return urlClassLoader;
  }

  /**
   * Gets the log file name
   * 
   * @return the log file name.
   */
  public String getLogFileName() {
    return logFileName;
  }

  /**
   * Gets the simulation instance associated with this project
   * 
   * 
   * @return the simulation instance if there is one, null otherwise.
   */
  public AbstractSimulator getSimulation() {
    return simulation;
  }

  /**
   * The state of auto-multi-threading
   * 
   * @return true if auto-multi-threading is activated, false otherwise
   */
  public boolean isAutoMultithreading() {
    return autoMultithreading;
  }

  /**
   * Set new value for auto-multi-threading
   * 
   * @param status
   *          new status to set
   */
  public void setAutoMultithreading(boolean status) {
    this.autoMultithreading = status;
  }

  /**
   * Load the simulation description from an XML file
   */
  public boolean loadSimulationDescription() {

    ClassLoader cl = this.getClass().getClassLoader();

    String p = "";
    String r = this.simDescXmlFileName;
    StringWriter stringWriter = new StringWriter();

    if (cl != null) {
      InputStream inputStream = cl.getResourceAsStream(p + r);
      if (inputStream == null) {
        System.out.println("No Resource: " + p + r);
        return false;
      }
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
          inputStream));
      try {
        int c;
        // read until the end of the file (EOF)
        while ((c = bufferedReader.read()) != -1) {
          stringWriter.write(c);
        }

      } catch (IOException e) {
        e.printStackTrace();
        return false;
      } finally {
        try {
          if (inputStream != null)
            inputStream.close();
          if (bufferedReader != null)
            bufferedReader.close();
          stringWriter.close();
        } catch (IOException ioe) {
          ioe.printStackTrace();
        }
      }
    }

    if (stringWriter.getBuffer().length() > 0) {
      this.setSimulationDescription(stringWriter.toString());
      return true;
    }
    this.setSimulationDescription("");
    return false;
  }

  /**
   * Create an instance of the current simulation class in order to be ready to
   * run the simulation.
   */
  public void instantiateSimulation(String simulation) {

    /*
     * ClassLoader cl = this.getClass().getClassLoader();
     * 
     * try { Class c =this.getClass().getClassLoader().loadClass(
     * "drivethrurestaurant_physicalagentbasedversion_withactivities.controller.Simulator"
     * ); this.simulation = (AbstractSimulator) c.newInstance(); } catch
     * (ClassNotFoundException e) { // TODO Auto-generated catch block
     * e.printStackTrace(); } catch (InstantiationException e) { // TODO
     * Auto-generated catch block e.printStackTrace(); } catch
     * (IllegalAccessException e) { // TODO Auto-generated catch block
     * e.printStackTrace(); }
     */

    try {

      this.simulation = (AbstractSimulator) this.getClass().getClassLoader()
          .loadClass(simulation).newInstance();
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * Prepare the simulation with the current project settings.
   */
  public void prepareSimulation() {
    this.prepareSimulation(this.logFileNamePrefix
        + new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss").format(new Date())
        + ".xml", this.directory + File.separator + this.projectName
        + File.separator + LOG_FOLDER_NAME);
  }

  /**
   * Prepare the simulation with specified log path and filename.
   * 
   * @param logPathAndFileName
   *          the path and file name of the generated log
   */
  private void prepareSimulation(String logFileName, String logPath) {

    // in case the log folder was deleted completely
    // File logDirectory = new File(directory + File.separator + projectName
    // + File.separator + LOG_FOLDER_NAME);
    // if (!logDirectory.exists()) {
    // logDirectory.mkdirs();
    // }

    // set the log filename
    this.logFileName = logFileName;

    // redirect the logger file to the project folder
    if (logPath == null) {

      dataBus.getLogger().setPath(
          this.directory + File.separator + this.projectName + File.separator
              + LOG_FOLDER_NAME);
    } else {
      dataBus.getLogger().setPath(logPath);
    }

    dataBus.getLogger().setFileName(this.logFileName);

    // please maintain the order of following two calls
    this.simulation.setDataBus(this.dataBus);

    // turn on/off multi-threading
    this.simulation.setAutoMultithreading(this.autoMultithreading);

    // call the simulation initialization
    this.simulation.initialize();
  }
}
