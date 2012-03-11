package aors.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import aors.data.DataBus;
import aors.exceptions.SimulatorException;
import aors.gui.helper.MD5Generator;

/**
 * Project
 * 
 * This class describes/defines an simulator project.
 * 
 * @author Marco Pehla, Mircea Diaconescu
 * @since 12.08.2008
 * @version $Revision$
 */
public class Project implements ProjectInterface {

  // the project name
  private String projectName;

  // the project directory
  private String directory;

  // the simulation description content as String
  private String simulationDescription;

  private File simulationDescriptionFile;

  private String simulationDescriptionMD5 = "";

  private String simulationDescriptionFilePath = "";

  // project was created on disk ?
  private boolean createdOnDisk;

  // project was saved ?
  private boolean saved;

  // project was already generated ?
  private boolean generated;

  // project was already compile
  private boolean compiled;

  // the simulation class - this is the one with main() method. It is used for
  // create instances of the AbstractSimulator
  private Class<? extends Object> simulationClass;

  // the abstract simulator - used to run the simulation
  private AbstractSimulator simulation = null;

  // the class loader used for dynamic class loadings
  private URLClassLoader urlClassLoader;

  // the compiler used to create byte code for generated Java classes
  private JavaCompiler compiler;

  // used to diagnose the compilation process
  private DiagnosticCollector<JavaFileObject> diagnostics;

  // the source folder (generated Java classes are stored inside)
  public final static String SRC_FOLDER_NAME = "src";

  public final static String MAIN_PACKAGE_NAME = "sim";

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
  private final String simPackageAndClassName = (!MAIN_PACKAGE_NAME.equals("")) ? MAIN_PACKAGE_NAME
      + ".controller.Simulator"
      : "controller.Simulator";

  // the path and file name of the java source containing the main class
  private final String simPathAndFileName = (!MAIN_PACKAGE_NAME.equals("")) ? MAIN_PACKAGE_NAME
      + File.separator + "controller" + File.separator + "Simulator.java"
      : "controller" + File.separator + "Simulator.java";

  // the destination folder where the simulation is build on request
  private String destinationFolder;

  /** property names used in the XML file **/
  private final String propertyName = "name";
  private final String propertyGenerated = "sources generated";
  private final String propertyCompiled = "sources compiled";
  private final String propertyLogFileName = "log file";
  private final String propertySimulationDescriptionFileName = "simDescFileName";
  private final String propertySimulationDescriptionFileMD5 = "simDescMD5";
  private final String propertySimulationDescriptionFilePath = "simDescFilePath";

  // the projects directory
  private final String PROJECTS_DIR = "projects";

  // user directory
  private final String USER_DIR = System.getProperty("user.dir");

  /**
   * Create a new project.
   */
  public Project() {
    this.projectName = "";
    this.directory = USER_DIR + File.separator + PROJECTS_DIR;

    this.simulationDescription = "";

    this.saved = false;
    this.generated = false;
    this.compiled = false;

    // instantiate the Java compiler
    this.compiler = ToolProvider.getSystemJavaCompiler();
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
    if (!this.projectName.equals(name)) {
      this.saved = false;
    }
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
    // if the project state has been changed
    if (!this.directory.equals(directory)) {
      this.saved = false;
    }
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
    // if the project state has been changed
    if (!this.simulationDescription.equals(simulationDescription)) {
      this.saved = false;
    }
    this.simulationDescription = simulationDescription;
  }

  /**
   * Get the name of the XML simulation description file
   * 
   * @return the XML simulation description filename.
   */
  public String getSimDescXmlFileName() {
    return simDescXmlFileName;
  }

  /**
   * Returns true if the project exists on disk
   * 
   * @return true if project exists on disk, false otherwise
   */
  public boolean isCreatedOnDisk() {
    return createdOnDisk;
  }

  /**
   * Returns true if the project was saved, false otherwise
   * 
   * @return true if project was saved, false otherwise
   */
  public boolean isSaved() {
    return this.saved;
  }

  /**
   * Set a new saved state.
   * 
   * @param saved
   *          the new saved state
   */
  public void setSaved(boolean saved) {
    this.saved = saved;
  }

  /**
   * Returns true if the project was already generated (now, or on another
   * session). Generated does not implies necessarily to be also compiled.
   * 
   * @return true if project was generated, false otherwise
   */
  public boolean isGenerated() {
    return generated;
  }

  /**
   * Set new project generated state.
   * 
   * @param status
   *          the new state for the generated state of the project
   */
  public void setGenerated(boolean status) {
    generated = status;
    compiled = false;
    // reset the simulation instance
    this.simulationClass = null;
    if (status) {
      this.simulationDescriptionMD5 = MD5Generator.getMD5(this
          .getSimulationDescription());
    }
    // this.save();
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
   * 
   * Returns the diagnostic object that hold compiler errors after unsuccessful
   * compilations.
   * 
   * @return the diagnostic object that hold compiler errors if there are any
   */
  public DiagnosticCollector<? extends JavaFileObject> getDiagnosticCollector() {
    return this.diagnostics;
  }

  /**
   * Checks if were some compile errors
   * 
   * @return true if no compile errors occured, false otherwise
   */
  private boolean isDiagnositicsOk() {
    boolean result = true;

    for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics
        .getDiagnostics()) {
      if (diagnostic.getKind().equals(Diagnostic.Kind.ERROR)) {
        // System.err.println(diagnostic.getMessage(null));
        return false;
      }
    }

    return result;
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
   * Returns the compiled state of this project
   * 
   * @return true if project was compiled, false otherwise
   */
  public boolean isCompiled() {
    return compiled;
  }

  /**
   * Set new project compiled state.
   * 
   * @param status
   *          the new state for the compiled state of the project
   */
  public void setCompiled(boolean status) {
    compiled = status;
    if (status == false) {
      this.simulationClass = null;
    }
  }

  /**
   * Helper method for loading a new project
   * 
   * @return true if load was successful, false otherwise
   */
  private boolean load() {
    boolean result = false;
    boolean projectFileFound = false;

    Properties properties = new Properties();
    File projectDirectory = new File(directory + File.separator + projectName);

    // notify listeners that the project directory was changed
    this.dataBus.notifyProjectDirectoryChange(projectDirectory);

    File projectFile = null;
    if (projectDirectory.isDirectory()) {

      for (String fileName : Arrays.asList(projectDirectory.list())) {
        // if the file name, of the XML file for the project, has been
        // found
        if (fileName.equals(PROJECT_FILE_NAME)) {
          projectFileFound = true;
          break;
        }
      }

      if (projectFileFound) {
        projectFile = new File(directory + File.separator + projectName
            + File.separator + PROJECT_FILE_NAME);
        try {
          properties.loadFromXML(new FileInputStream(projectFile));

        } catch (IOException ioe) {
          System.err.println("Can not load the project file ");
          ioe.printStackTrace();
        }
        // overwrite objects project attributes
        this.projectName = properties.getProperty(this.propertyName);

        String simDescXmlFileName = properties
            .getProperty(this.propertySimulationDescriptionFileName);
        if (simDescXmlFileName != null) {
          this.simDescXmlFileName = simDescXmlFileName;
        } else {
          // this is only for downward compatibility
          this.simDescXmlFileName = this.projectName + ".xml";
        }

        this.generated = new Boolean(
            properties.getProperty(this.propertyGenerated));
        this.compiled = new Boolean(
            properties.getProperty(this.propertyCompiled));

        this.simulationDescriptionMD5 = properties
            .getProperty(this.propertySimulationDescriptionFileMD5);
        if (this.simulationDescriptionMD5 == null)
          this.simulationDescriptionMD5 = "";

        this.simulationDescriptionFilePath = properties
            .getProperty(this.propertySimulationDescriptionFilePath);
        if (this.simulationDescriptionFilePath == null)
          this.simulationDescriptionFilePath = "";

        this.logFileName = properties.getProperty(this.propertyLogFileName);

        // the project state is the same as saved and of course as
        // "created on disk"
        this.saved = true;
        this.createdOnDisk = true;

        // destination folder for the compiled Java source (*.class)
        // files
        destinationFolder = this.directory + File.separator + this.projectName
            + File.separator + BIN_FOLDER_NAME;
      }

      this.loadSimulationDescription();

      result = true;

    }

    return result;
  }

  /**
   * Load a new project based by the project name
   * 
   * @param project
   *          - the path to the project
   * @return true if successful, false otherwise
   */
  public boolean load(String project) {
    File projectDirectory = new File(project);
    this.directory = projectDirectory.getParent();
    this.projectName = projectDirectory.getName();

    return this.load();
  }

  /**
   * Load the simulation description from an XML file
   */
  public void loadSimulationDescription() {

    String filePath = this.simulationDescriptionFilePath;
    // try it first with the absolute path
    this.simulationDescriptionFile = new File(filePath);

    // try it from the project folder (old solution)
    if (!this.simulationDescriptionFile.exists()) {
      filePath = directory + File.separator + this.projectName + File.separator
          + this.simDescXmlFileName;
      this.simulationDescriptionFile = new File(filePath);
    }

    // load the XML simulation description
    try {
      InputStreamReader streamReader = new InputStreamReader(
          new FileInputStream(this.simulationDescriptionFile), "UTF8");

      BufferedReader fileReader = new BufferedReader(streamReader);

      String line = "";
      String result = "";

      while ((line = fileReader.readLine()) != null) {
        result += line + "\n";
      }

      fileReader.close();

      // store the content of the string writer as simulation description
      this.setSimulationDescription(result);
      this.setSimulationDescriptionFilePath(this.simulationDescriptionFile
          .getAbsolutePath());

    } catch (IOException e) {
      // e.printStackTrace();
      JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Save the XML simulation description to file.
   */
  public void saveSimulationDescription() {
    try {
      FileWriter fileWriter = new FileWriter(directory + File.separator
          + projectName + File.separator + this.simDescXmlFileName);
      this.simulationDescriptionFilePath = directory + File.separator
          + projectName + File.separator + this.simDescXmlFileName;

      if (this.simulationDescription == null) {
        this.setSimulationDescription("");
      }

      fileWriter.write(this.simulationDescription);
      fileWriter.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  /**
   * Save the current project.
   * 
   * Comments: This method does NOT ask if there is already a project with the
   * same name in the directory. To check the users choice whether to overwrite
   * or not, is in the duty of an user interface.
   * 
   * @return true if successful, false otherwise
   */
  public boolean save() {
    boolean result = false;

    // if no project name has been set, use the default name
    if (this.projectName.equals("") || this.projectName == null) {
      this.projectName = "simulation_"
          + new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss").format(new Date());
    }

    File rootDirectory = new File(directory + File.separator + projectName);
    File srcDirectory = new File(directory + File.separator + projectName
        + File.separator + SRC_FOLDER_NAME);
    File binDirectory = new File(directory + File.separator + projectName
        + File.separator + BIN_FOLDER_NAME);
    File logDirectory = new File(directory + File.separator + projectName
        + File.separator + LOG_FOLDER_NAME);
    File mediaDirectory = new File(directory + File.separator + projectName
        + File.separator + MEDIA_FOLDER_NAME);
    File mediaImagesDirectory = new File(directory + File.separator
        + projectName + File.separator + MEDIA_FOLDER_NAME + File.separator
        + MEDIA_IMAGES_FOLDER_NAME);
    File mediaSoundsDirectory = new File(directory + File.separator
        + projectName + File.separator + MEDIA_FOLDER_NAME + File.separator
        + MEDIA_SOUNDS_FOLDER_NAME);

    File mediaHtmlDirectory = new File(directory + File.separator + projectName
        + File.separator + MEDIA_FOLDER_NAME + File.separator
        + MEDIA_HTML_FOLDER_NAME);

    if (!rootDirectory.exists()) {
      rootDirectory.mkdirs();
    }

    if (rootDirectory.isDirectory()) {
      if (!srcDirectory.exists()) {
        srcDirectory.mkdir();
      }
      if (!binDirectory.exists()) {
        binDirectory.mkdir();
      }
      if (!logDirectory.exists()) {
        logDirectory.mkdir();
      }
      if (!mediaDirectory.exists()) {
        mediaDirectory.mkdir();
      }
      if (!mediaImagesDirectory.exists()) {
        mediaImagesDirectory.mkdir();
      }
      if (!mediaSoundsDirectory.exists()) {
        mediaSoundsDirectory.mkdir();
      }
      if (!mediaHtmlDirectory.exists()) {
        mediaHtmlDirectory.mkdir();
      }

    } else {
      return false;
    }

    this.saveSimulationDescription();
    
    // create the projects properties
    Properties properties = new Properties();
    properties.put(this.propertyName, this.projectName);
    properties.put(this.propertyGenerated, Boolean.valueOf(generated)
        .toString());
    properties.put(this.propertyCompiled, Boolean.valueOf(compiled).toString());
    properties.put(this.propertyLogFileName, this.logFileName);
    properties.put(this.propertySimulationDescriptionFileName,
        this.simDescXmlFileName);
    properties.put(this.propertySimulationDescriptionFileMD5,
        this.simulationDescriptionMD5);
    properties.put(this.propertySimulationDescriptionFilePath,
        this.simulationDescriptionFilePath);

    // store them in an XML file
    try {
      properties.storeToXML(new FileOutputStream(new File(directory
          + File.separator + projectName + File.separator
          + Project.PROJECT_FILE_NAME)),
          "This is the project description file for the AOR simulator project named "
              + this.projectName + ".", "UTF-8");
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

    result = true;

    this.saved = true;
    this.createdOnDisk = true;

    return result;
  }

  /**
   * Delete a directory and recursively its entire content
   * 
   * @return true if successful, false otherwise
   */
  public static boolean deleteDirectory(File path) {
    if (path.exists()) {
      File[] files = path.listFiles();
      for (int i = 0; i < files.length; i++) {
        if (files[i].isDirectory()) {
          deleteDirectory(files[i]);
        } else {
          files[i].delete();
        }
      }
    }
    return (path.delete());
  }

  /**
   * Compile the project (Java classes to binary code)
   * 
   * @return true if successful, false otherwise
   */
  public boolean compile() {
    boolean result = false;
    // when the simulation source codes are generated
    if (isGenerated()) {
      // when there not yet compiled
      if (!isCompiled()) {

        // root path of all source files
        String sourcePath = this.directory + File.separator + this.projectName
            + File.separator + SRC_FOLDER_NAME;

        // the full qualified name to the simulation
        // because of the dependencies it's NOT necessary to specify
        // every Java source code
        String javaSourcePathPlusFileName = this.directory + File.separator
            + this.projectName + File.separator + SRC_FOLDER_NAME
            + File.separator + this.simPathAndFileName;

        // destination folder for the compiled Java source (*.class)
        // files
        destinationFolder = this.directory + File.separator + this.projectName
            + File.separator + BIN_FOLDER_NAME;

        diagnostics = new DiagnosticCollector<JavaFileObject>();

        if (this.compiler != null) {
          StandardJavaFileManager fileManager = compiler
              .getStandardFileManager(diagnostics, null, null);

          File simulationFile = new File(javaSourcePathPlusFileName);
          if (simulationFile.exists()) {
            // drop the content of the destination directory before
            // generation
            // new content
            File destinationDirectory = new File(destinationFolder);
            deleteDirectory(destinationDirectory);
            destinationDirectory.mkdirs();

            // compile the source file
            Iterable<? extends JavaFileObject> fileObject = fileManager
                .getJavaFileObjects(simulationFile);
            String[] options = new String[] { "-d", destinationFolder,
                "-sourcepath", sourcePath };
            compiler.getTask(null, null, diagnostics, Arrays.asList(options),
                null, fileObject).call();
            try {
              fileManager.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          } else {
            System.err.println("Unable to find: " + javaSourcePathPlusFileName
                + ". Abort compilation.");

          }

        } else {
          System.err.println("No Java compiler available!");
          return false;
        }

        // when no compiler errors appear
        if (isDiagnositicsOk()) {
          result = true;
          compiled = true;

          // notify listeners that the project directory was changed
          this.dataBus.notifyProjectDirectoryChange(new File(this.directory
              + File.separator + this.projectName));

          // save project
          this.save();

        } else {
          result = false;
        }
        // reset the current simulation, in order to force the project
        // to instantiate the new compiled simulation
        this.simulation = null;
      }// !isCompiled
    }// isGenerated
    return result;
  }

  /**
   * Create an instance of the current simulation class in order to be ready to
   * run the simulation.
   */
  public void instantiateSimulation() throws SimulatorException {

    try {
      // create URL from the class folder
      URL[] generatedClassesFolder = new URL[] { new File(destinationFolder
          + File.separator).toURI().toURL() };

      // instantiate an class loader for these classes
      this.urlClassLoader = URLClassLoader.newInstance(generatedClassesFolder,
          ClassLoader.getSystemClassLoader());

      // load the simulation class
      this.simulationClass = this.urlClassLoader
          .loadClass(simPackageAndClassName);

    } catch (MalformedURLException e) {
      // System.err.println("ERROR: " + e.getClass().getCanonicalName());
      e.printStackTrace();
      throw new SimulatorException(e.getClass().getSimpleName() + ": "
          + e.getMessage());
    } catch (ClassNotFoundException e) {
      // System.err.println("ERROR: " + e.getClass().getCanonicalName());
      // System.out.println("Message: " + e.getMessage());
      // e.printStackTrace();
      throw new SimulatorException(e.getClass().getSimpleName() + ": "
          + e.getMessage());
    }

    // try to instantiate the simulation
    try {
      // type cast the new simulation class instance, this is necessary
      // because the generated simulation has no run() method!
      if (this.simulation == null) {
        this.simulation = (AbstractSimulator) this.simulationClass
            .newInstance();
      }

    } catch (IllegalAccessException e) {
      // System.err.println("ERROR: " + e.getClass().getSimpleName() + ": " +
      // e.getMessage());
      // e.printStackTrace();
      throw new SimulatorException(e.getClass().getSimpleName() + ": "
          + e.getMessage());
    } catch (InstantiationException e) {
      // System.err.println("ERROR: " + e.getClass().getCanonicalName());
      // e.printStackTrace();
      throw new SimulatorException(e.getClass().getSimpleName() + ": "
          + e.getMessage());
    } catch (NoClassDefFoundError error) {
      throw new SimulatorException(error.getClass().getSimpleName() + ": "
          + error.getMessage());
    }

  }

  /**
   * Prepare the simulation with the current project settings.
   */
  public void prepareSimulation() {
    this.prepareSimulation(this.logFileNamePrefix
        + new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss").format(new Date())
        + ".xml", this.directory + File.separator + this.projectName
        + File.separator + LOG_FOLDER_NAME, true);
  }

  public void prepareSimulationWithoutLogFile() {
    this.prepareSimulation(null, null, false);
  }

  /**
   * Prepare the simulation with specified log path and filename.
   * 
   * @param logFileName
   *          the path and file name of the generated log
   * @param logPath
   *          the path where the log has to be created
   * @param createLogFile
   *          specify while the log file is or not created
   */
  public void prepareSimulation(String logFileName, String logPath,
      boolean createLogFile) {

    if (createLogFile) {

      // in case the log folder was deleted completely
      File logDirectory = new File(directory + File.separator + projectName
          + File.separator + LOG_FOLDER_NAME);
      if (!logDirectory.exists()) {
        logDirectory.mkdirs();
      }

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
    }

    // please maintain the order of following two calls
    this.simulation.setDataBus(this.dataBus);

    // call the simulation initialization
    this.simulation.initialize();
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Get the {@code simulationDescriptionFile}.
   * 
   * 
   * 
   * @return the {@code simulationDescriptionFile}.
   */
  public File getSimulationDescriptionFile() {
    return simulationDescriptionFile;
  }

  public boolean checkBuildStatus() {
    return this.simulationDescriptionMD5.equals(MD5Generator.getMD5(this
        .getSimulationDescription()));
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code simulationDescriptionFilePath}.
   * 
   * 
   * 
   * @param simulationDescriptionFilePath
   *          The {@code simulationDescriptionFilePath} to set.
   */
  public void setSimulationDescriptionFilePath(
      String simulationDescriptionFilePath) {
    this.simulationDescriptionFilePath = simulationDescriptionFilePath;
  }

  /**
   * Usage:
   * 
   * 
   * Comments: Set the {@code simulationDescriptionFile}.
   * 
   * 
   * 
   * @param simulationDescriptionFile
   *          The {@code simulationDescriptionFile} to set.
   */
  public void setSimulationDescriptionFile(File simulationDescriptionFile) {
    this.simulationDescriptionFile = simulationDescriptionFile;
  }

  public void runSimulation(boolean multithreading) {
    this.simulation.runSimulation(multithreading);
  }

  public boolean existSimulation() {
    return this.simulation != null;
  }
}
