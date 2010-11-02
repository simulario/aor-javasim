package aors.controller;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
//import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import aors.codegen.XSLT2Processor;
import aors.data.DataBus;
import aors.data.DataBusInterface;
import aors.exceptions.SimulatorException;
import aors.module.Constants;
import aors.module.GUIModule;
import aors.module.Module;
import aors.util.jar.JarUtil;

/**
 * This class represents the manager of the simulation.
 * 
 * SimulationManager
 * 
 * @author Marco Pehla, Mircea Diaconescu, Jens Werner
 * @since 31.07.2008
 * @version $Revision$
 * 
 *          last change by $Author$ at $Date: 2010-05-03 21:29:08
 *          +0200 (Mo, 03 Mai 2010) $
 * 
 */
public class SimulationManager {

  private XSLT2Processor xslt2Processor;

  private HashMap<String, String> xsltParameter;

  private SimulationDescription simulationDescription;

  // the current project assigned to the simulation manager
  private static Project project;

  // the list with all current loaded modules - non grouped
  private List<Module> modules;

  // the list with all current loaded modules - grouped ones
  private List<Module> groupModules;

  // the user directory
  private final String APP_ROOT_DIRECTORY = System.getProperty("user.dir");

  public final static String PROJECT_DIRECTORY = "projects";

  private String AORSLDirectory;
  private String AORSLSchemaName;

  private String codeGenXsltDirectory;
  private String codeGenXsltName;

  // the current (used as default) schema name
  private final String CURRENT_AORSL_SCHEMA_NAME = "AORSL_0-8-4.xsd";
  private final String DEFAULT_CODEGEN_XSLT_FILE = "aorsl2java.xsl";

  // the project properties object
  private Properties properties;

  // the project property file
  private final String PROPERTY_FILE_NAME = "properties.xml";

  // property keys
  private final String propertyLoggerFileName = "logger.file.name";
  private final String propertyLoggerPath = "logger.path";
  private final String propertyAutoMultithreading = "auto.multithreading";
  private final String propertyXMLSchemaFileName = "XML-Schema.file.name";
  private final String propertyXMLSchemaFilePath = "XML-Schema.file.path";
  private final String propertyXSLTFileName = "CodeGen.XSLT.file.name";
  private final String propertyXSLTFilePath = "CodeGen.XSLT.file.path";

  public static final String propertyLogger = "logger";

  // used by the AOR-WebSim Controller component - need to be public and static,
  public static final String PROPERTY_XML_SCHEMA_FILE_NAME = "XML Schema file name";

  // property values (default values)
  private String loggerFileName = "output.xml";
  private String loggerPath = ".";
  private boolean autoMultithreading = false;
  private DataBus dataBus;

  // the project directory
  private File projectDirectory;

  /**
   * Create a new simulation manager.
   */
  public SimulationManager() {
    // create the data bus that will be the data bus of all components/listeners
    this.dataBus = new DataBus();

    this.simulationDescription = new SimulationDescription();

    // load properties from simulation project property file
    this.loadProperties();
    this.setProperties();

    // initialize the logger
    this.initLogger();

    // initialize the modules/plugins
    this.initModules();

    // create the XSLT procesor ibject
    this.xslt2Processor = new XSLT2Processor();

    // the mapt with XSLT parameters
    this.xsltParameter = new HashMap<String, String>();

    // set the project directory
    this.projectDirectory = new File(APP_ROOT_DIRECTORY + File.separator
        + PROJECT_DIRECTORY);

    // create the project directory in the current path if not exists already
    if (!this.projectDirectory.exists()) {
      this.projectDirectory.mkdir();
    }
  }

  /**
   * Initialize the modules found in the modules directory as jar files.
   */
  private void initModules() {
    // create reference file to modules directory
    File modulesDir = new File(Constants.modulesDirectory);

    // get all files from the modules directory
    File[] moduleJars = modulesDir.listFiles();

    // access the general modules property file
    Properties modulesGeneralProperties = new Properties();
    InputStream input;
    try {
      input = new FileInputStream(new File(Constants.modulesDirectory
          + File.separator + "properties.xml"));
      modulesGeneralProperties.loadFromXML(input);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (InvalidPropertiesFormatException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // directory not there, then is no module, so just ignore
    if (moduleJars == null) {
      // nothing to do further...
      return;
    }

    System.out.println("***** Simulation Manager: processing modules *****");

    // create the empty modules list(s)
    this.modules = new ArrayList<Module>();
    this.groupModules = new ArrayList<Module>();

    // perform module selection (drop non usable ones)
    System.out.println("   ->Perform modules selection: ");
    ArrayList<File> filteredModuleJars = this
        .filterAndOrderModulesByIndexPosition(moduleJars,
            modulesGeneralProperties);

    // try to load one by one all found modules in the modules directory
    System.out.println("   ->Load modules: ");
    for (File moduleFile : filteredModuleJars) {
      // load the module - may fail if something goes wrong...
      try {
        String moduleFileName = moduleFile.getName();

        System.out.println("      Module: " + moduleFileName);

        // load the module
        loadModule(moduleFile, modulesGeneralProperties);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    System.out
        .println("***** Simulation Manager: finished modules loading *****");
  }

  /**
   * The user can set index position for modules. This will affect the tab order
   * apparition. This method order the modules by their possible given position
   * index. The method does also a filtering of the module files, so only real
   * module files will be kept finally;
   * 
   * NOTE: for any module that is implied in a group (many modules in the same
   * tab) the index position is ignored
   * 
   * @param moduleJars
   *          the list with all and unordered modules
   * @param modulesGeneralProperties
   *          the properties file where general modules properties are described
   */
  private ArrayList<File> filterAndOrderModulesByIndexPosition(
      File[] moduleJars, Properties modulesGeneralProperties) {

    // this will contain only selected modules
    ArrayList<File> resultModuleJars = new ArrayList<File>();

    // temporarily used Map for modules ordering
    HashMap<Integer, File> tmpModules = new HashMap<Integer, File>();

    for (File moduleFile : moduleJars) {
      String moduleFileName = moduleFile.getName();

      // files without jar extension are ignored
      if (!moduleFileName.endsWith(".jar")) {
        continue;
      }

      // ignore non OS compatible modules
      if (!this.isCompatibleWithCurrentPlatform(moduleFile)) {
        continue;
      }

      try {
        if (modulesGeneralProperties.getProperty(moduleFileName) != null) {
          int pos = Integer.parseInt(modulesGeneralProperties
              .getProperty(moduleFileName));

          // using 0 or negative for positioning will have as effect that the
          // module will not be loaded
          if (pos > 0) {
            // add module in the right position
            tmpModules.put(pos, moduleFile);
          }
        }
        // this module does not use any order...
        else {
          resultModuleJars.add(moduleFile);
        }
      } catch (NumberFormatException nfex) {
        // just a backup in case that you provide by mistake a "non-number"
        // position in the properties list for this module, so in this case it
        // will be a "non-ordered" one as when the position is not given at all
        resultModuleJars.add(moduleFile);
      }
    }

    // add the ordered modules now
    List<Integer> sortList = new ArrayList<Integer>(tmpModules.keySet());
    Collections.sort(sortList);
    int len = sortList.size();
    for (int i = len - 1; i >= 0; i--) {
      resultModuleJars.add(0, tmpModules.get(sortList.get(i)));
    }

    // return filtered and ordered modules files list
    return resultModuleJars;
  }

  /**
   * Load a single module
   * 
   * @param moduleFile
   *          the module filename
   * @param moduleGeneralProperties
   *          the general modules properties object
   * @throws IOException
   */
  private void loadModule(File moduleFile, Properties moduleGeneralProperties)
      throws IOException {

    // new jar file...so, new module
    JarFile jarFile = new JarFile(moduleFile);

    // access the properties file from inside this jar
    JarEntry jarEntry = jarFile.getJarEntry("properties.xml");

    // no property file...then we can't continue loading this module.
    if (jarEntry == null) {
      System.out
          .println("The module: "
              + moduleFile.getName()
              + " does not contains a property.xml file! This module can't be loaded! \n");

      // go to the next jar file (new module) if there is one
      return;
    }

    // access the properties file of the module
    Properties properties = new Properties();
    InputStream inputStream = jarFile.getInputStream(jarEntry);
    properties.loadFromXML(inputStream);

    System.out.println("         name: "
        + properties.getProperty(Module.PROP_NAME));

    // check if we have a GUI component for this module
    System.out.print("         has GUI: ");
    if (properties.getProperty(GUIModule.PROP_GUI_MODULE_CLASS) != null) {
      System.out.print("YES");
    } else {
      System.out.print("NO");
    }

    System.out.print("\n");

    // check if we have the base component for this module
    System.out.print("         has BASE: ");
    if (properties.getProperty(Module.PROP_BASE_MODULE_CLASS) != null) {
      System.out.print("YES");
    } else {
      System.out.print("NO... module can't be loaded!.");

      // go to the next module if there is any
      return;
    }

    System.out.print("\n");

    // define this Jar file as usable - See JarUtil for more details...
    JarUtil.loadJar(new URL("file:///" + moduleFile.getPath()));

    try {
      // get the modules list for group
      String modulesGroup = moduleGeneralProperties
          .getProperty(Module.PROP_MODULES_GROUP);

      Module moduleInstance = instantiateModule(
          properties.getProperty(Module.PROP_BASE_MODULE_CLASS),
          properties.getProperty(GUIModule.PROP_GUI_TITLE),
          properties.getProperty(GUIModule.PROP_GUI_TITLE));

      // add module to the right modules list
      if (modulesGroup == null
          || modulesGroup.indexOf(moduleFile.getName()) == -1) {
        this.modules.add(moduleInstance);
      } else {
        this.groupModules.add(moduleInstance);
      }
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
      return;
    } catch (IllegalAccessException ex) {
      ex.printStackTrace();
      return;
    } catch (InstantiationException ex) {
      ex.printStackTrace();
      return;
    }
  }

  /**
   * Check compatibility with the current OS
   * 
   * @param moduleFile
   *          the module JAR file from where properties will be read
   * @return true if module is OS and Bits Version compatible with the one from
   *         the current PC, false otherwise
   * @throws IOException
   */
  private boolean isCompatibleWithCurrentPlatform(File moduleFile) {
    // access the properties file of the module
    Properties properties = new Properties();

    // access the properties file from inside this jar
    JarFile moduleJarFile;
    JarEntry jarEntry;
    try {
      moduleJarFile = new JarFile(moduleFile);
      jarEntry = moduleJarFile.getJarEntry("properties.xml");
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    // load the properties file
    InputStream inputStream;
    try {
      inputStream = moduleJarFile.getInputStream(jarEntry);
      properties.loadFromXML(inputStream);
    } catch (IOException e1) {
      e1.printStackTrace();
    }

    // check if the OS version is specified, otherwise is loaded by default
    System.out.print("      Module:  " + moduleFile.getName()
        + "   ->   OS check: ");
    if (properties.getProperty(Module.PROP_MODULE_OS_VERSION) != null) {
      String propVal = properties.getProperty(Module.PROP_MODULE_OS_VERSION)
          .trim().toLowerCase();

      // the OS specified in prop file match the PC OS
      if ((JarUtil.isWindows() && propVal
          .equals(Module.PROP_MODULE_OS_VERSION_VALUE_WINDOWS))
          || (JarUtil.isUnix() && propVal
              .equals(Module.PROP_MODULE_OS_VERSION_VALUE_LINUX))
          || (JarUtil.isMac() && propVal
              .equals(Module.PROP_MODULE_OS_VERSION_VALUE_MAC))) {
        System.out.print("OK, ");
      }
      // wrong OS...skip loading
      else {
        System.out.println("failed! The module will not be loaded!");
        return false;
      }
    }
    // properties file does not say nothing about OS, so that means
    // "good for all Operating Systems"
    else {
      System.out.print("OK, ");
    }

    // check if the OS bits version is specified
    System.out.print("OS bits version check: ");
    if (properties.getProperty(Module.PROP_MODULE_OS_BITS_VERSION) != null) {
      String propValue = properties.getProperty(
          Module.PROP_MODULE_OS_BITS_VERSION).trim();
      boolean is64BitsOs = System.getProperty("os.arch").indexOf("64") != -1;

      // the OS Bits version match the version from property file
      if ((propValue.equals("32") && !is64BitsOs)
          || (propValue.equals("64") && is64BitsOs)) {
        System.out.print("OK");
      }
      // OS Bits version does not match the version from property file.
      else {
        System.out.println("failed! The module will not be loaded! \n");
        return false;
      }
    } else {
      System.out.print("OK");
    }

    System.out.print("\n");

    // if we are here, the module is OS/Bits compatible with the current system
    return true;
  }

  /**
   * In this point is assumed that the module is fine and ready to be
   * instantiated
   * 
   * @param baseClassName
   *          the module main class name (including package)
   * @param guiClassName
   *          the module GUI class name (including package)
   * @param tabTitle
   *          the title that will appear on the module tab. This will have no
   *          meaning if the module is part of a GROUP (many modules shown in
   *          the same tab)
   * 
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  private Module instantiateModule(String baseClassName, String guiClassName,
      String tabTitle) throws ClassNotFoundException, IllegalAccessException,
      InstantiationException {

    // load the base logic module
    Class<? extends Object> moduleClass = Class.forName(baseClassName);

    // get the tab instance
    Module module = ((Module) moduleClass.newInstance());

    // module is a listener for simulation step events
    this.dataBus.addSimulationStepEventListener(module);

    // module is a listener for simulation events
    this.dataBus.addSimulationEventListener(module);

    // the module is a listener for destroy object/agent event
    this.dataBus.addDestroyObjektEventListener(module);

    // the module is a listener for object/agent creation events
    this.dataBus.addObjektInitEventListener(module);

    // get the module tab title from the property file
    if (module.getGUIComponent() != null && guiClassName != null) {
      ((JScrollPane) module.getGUIComponent()).setName(tabTitle);
    }

    return module;
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
   * Initialize the logger
   */
  private void initLogger() {

    String value;
    int loggerType = DataBus.LoggerType.DEFAULT_LOGGER;
    value = this.getProperties().getProperty(propertyLogger);
    if (value != null) {
      try {
        loggerType = Integer.valueOf(value);
      } catch (NumberFormatException nfe) {
        this.getProperties().put(propertyLogger, String.valueOf(loggerType));
      }
    } else {
      this.getProperties().put(propertyLogger, String.valueOf(loggerType));
    }

    try {
      dataBus.initLogger(loggerType);
    } catch (Exception e) {
      // Fall-Back to EmptyLogger
      e.printStackTrace();
      System.err.println("Use DefaultLogger");
      try {
        dataBus.initLogger(DataBus.LoggerType.DEFAULT_LOGGER);
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }
    dataBus.getLogger().setFileName(this.loggerFileName);
    dataBus.getLogger().setPath(this.loggerPath);

    value = this.properties.getProperty(propertyLoggerFileName);
    if (value != null) {
      if (!value.equals("")) {
        this.loggerFileName = value;
        dataBus.getLogger().setFileName(this.loggerFileName);

      }
    }

    value = this.properties.getProperty(propertyLoggerPath);
    if (value != null) {
      if (!value.equals("")) {
        this.loggerPath = value;
        dataBus.getLogger().setPath(this.loggerPath);
      }

    }
  }

  /**
   * Load the properties from the project property file
   */
  private void loadProperties() {
    this.properties = new Properties();

    try {
      this.properties.loadFromXML(new FileInputStream(this.PROPERTY_FILE_NAME));
    } catch (IOException ioe) {
      System.err.println("Can not load the file " + this.PROPERTY_FILE_NAME
          + ". Using the default values.");
      this.setDefaultProperties();
    }

  }

  /*
   * this values are used when no property file exists
   */
  private void setDefaultProperties() {
    this.AORSLDirectory = "ext" + File.separator + "aorsl";
    this.AORSLSchemaName = CURRENT_AORSL_SCHEMA_NAME;

    this.codeGenXsltDirectory = APP_ROOT_DIRECTORY + File.separator + "ext"
        + File.separator + "javagen";
    this.codeGenXsltName = DEFAULT_CODEGEN_XSLT_FILE;
  }

  private void setProperties() {

    String value = this.properties.getProperty(propertyAutoMultithreading);
    if (value != null && value.equals("true")) {
      this.autoMultithreading = true;
    }

    value = this.properties.getProperty(propertyXMLSchemaFileName);
    if (value != null) {
      this.AORSLSchemaName = value;
    } else {
      this.AORSLSchemaName = CURRENT_AORSL_SCHEMA_NAME;
    }

    value = this.properties.getProperty(propertyXMLSchemaFilePath);
    if (value != null) {
      this.AORSLDirectory = value;
    } else {
      this.AORSLDirectory = "ext" + File.separator + "aorsl";
    }

    value = this.properties.getProperty(propertyXSLTFileName);
    if (value != null) {
      this.codeGenXsltName = value;
    } else {
      this.codeGenXsltName = DEFAULT_CODEGEN_XSLT_FILE;
    }

    value = this.properties.getProperty(propertyXSLTFilePath);
    if (value != null) {
      this.codeGenXsltDirectory = value;
    } else {
      this.codeGenXsltDirectory = APP_ROOT_DIRECTORY + File.separator + "ext"
          + File.separator + "javagen";
    }

  }

  /**
   * Save the properties to the project property file
   */
  public void storeProperties() {

    this.setPropertiesFromSystem();
    this.saveProperties();

  }

  private void setPropertiesFromSystem() {
    // put on the simulator's properties
    // notice that an GUI may added already different properties as well!
    this.properties.put(this.propertyLoggerFileName, this.loggerFileName);
    this.properties.put(this.propertyLoggerPath, this.loggerPath);
    this.properties.put(this.propertyAutoMultithreading,
        String.valueOf(autoMultithreading));
    this.properties.put(this.propertyXMLSchemaFileName, this.AORSLSchemaName);
    this.properties.put(this.propertyXMLSchemaFilePath, this.AORSLDirectory);
    this.properties.put(this.propertyXSLTFileName, this.codeGenXsltName);
    this.properties.put(this.propertyXSLTFilePath, this.codeGenXsltDirectory);
  }

  private void saveProperties() {
    // store properties in the properies XML file
    try {
      this.properties.storeToXML(new FileOutputStream(new File(
          this.PROPERTY_FILE_NAME)),
          "This is the property file for the AOR simulator.", "UTF-8");
    } catch (IOException ioe) {
      ioe.printStackTrace();
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
   * Create a new project based on a given simulation description expressed as
   * string (XML in a string format)
   * 
   * @param simulationDescription
   *          the string representation of the simulation description
   */
  public void newProject() {
    // clear old project
    project = null;

    project = new Project();
    project.setDataBus(dataBus);
  }

  /**
   * Load the project from a given path
   * 
   * @param projectPath
   *          the path of the project to load
   * @return true if successful false otherwise
   */
  public boolean loadProject(String projectPath) {
    boolean status = false;

    // new project instance
    project = new Project();
    project.setDataBus(dataBus);

    // load the project for the given path
    status = project.load(projectPath);

    // return the projects load status
    return status;
  }

  /**
   * Gets the actual project object
   * 
   * @return the current project object
   */
  public Project getProject() {
    return project;
  }

  /**
   * Clear the actual project and call garbage collector for clearing
   * unnecessarily occupied memory.
   */
  public void clearProject() {
    SimulationManager.project = null;

    // force garbage collector to free any remaining garbages...
    System.gc();
  }

  /**
   * Load the content of a given XML file
   * 
   * @param file
   *          the XML file (normally a XML simulation description file)
   * @return the content, as string, of the given file
   */
  public String readXMLFile(File file) {
    try {

      InputStreamReader streamReader = new InputStreamReader(
          new FileInputStream(file), "UTF8");

      BufferedReader fileReader = new BufferedReader(streamReader);

      String line = "";
      String result = "";

      while ((line = fileReader.readLine()) != null) {
        result += line + "\n";
      }

      fileReader.close();

      // return the content of the string writer
      return result;

    } catch (IOException e) {
      e.printStackTrace();
    }

    // necessary, since return values are always required
    return "";
  }

  public String readTextFileFromJar(String filePath, String fileName) {
    ClassLoader cl = this.getClass().getClassLoader();
    String result = "";
    if (cl != null) {
      InputStream inputStream = cl.getResourceAsStream(filePath + '/'
          + fileName);
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

      URL url = cl.getResource("com/sun/tools/javac/Main.class");
      if (url != null)
        result = url.getPath();
      return System.getProperty("java.class.path");
    }
    return result;
  }

  /**
   * Validate the simulation description XML file against the AORS XML schema
   * 
   * @return true if valid, false otherwise
   */
  public boolean validateSimulation() {
    boolean result = false;

    String xml = this.getProject().getSimulationDescription();

    File schemaFile = this.getXMLSchema();

    SchemaFactory factory;

    try {
      factory = SchemaFactory
          .newInstance("http://www.w3.org/XML/XMLSchema/v1.1");
      System.out.println("Xerces with XML-Schema 1.1 Support is loaded.");
    } catch (IllegalArgumentException iae) {
      // lookup a factory for the W3C XML Schema language
      factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      System.out.println("Loaded schema validation provider "
          + factory.getClass().getName());
    }

    try {
      // System.out.println("Is 1.1: " +
      // factory.isSchemaLanguageSupported("http://www.w3.org/XML/XMLSchema/v1.1"));

      // create a new Schema instance
      Schema schema = factory.newSchema(schemaFile);

      // get a validator from the schema.
      Validator validator = schema.newValidator();

      // parse the document you want to check.
      Source source = new StreamSource(new StringReader(xml));

      // validate it
      validator.validate(source);

      // indicate when valid
      result = true;

    } catch (SAXException e) {
      // indicate when invalid
      System.err.println("The simulation is not valid because:");
      System.err.println(e.getMessage());

      result = false;
    } catch (IOException e) {
      e.printStackTrace();
    }

    return result;
  }

  private File getXMLSchema() {

    File schemaLocation = new File(System.getProperty("user.dir")
        + File.separator + this.AORSLDirectory + File.separator
        + this.AORSLSchemaName);

    if (!schemaLocation.exists()) {
      System.err.println("The XML-Schema [" + this.AORSLSchemaName
          + "] was not found. Try with [" + this.CURRENT_AORSL_SCHEMA_NAME
          + "].");
      this.AORSLSchemaName = this.CURRENT_AORSL_SCHEMA_NAME;
      schemaLocation = new File(System.getProperty("user.dir") + File.separator
          + this.AORSLDirectory + File.separator + this.AORSLSchemaName);
    }

    return schemaLocation;
  }

  /**
   * Generate the source code starting from XML description file and using XSLT
   * transformation.
   * 
   * @return true if generation was performed with success, false otherwise
   * 
   */
  public boolean generate() {
    boolean result = false;

    File xsltFile = new File(this.codeGenXsltDirectory + File.separator
        + this.codeGenXsltName);
    if (!xsltFile.exists()) {
      System.err.println("No transformation file found!");
      return false;
    }

    // out = switch generation to memory on, java = switch write to disk on
    this.xsltParameter.put("output.fileExtension", "java");

    this.xsltParameter.put(
        "sim.package.root",
        "file:///" + project.getDirectory() + File.separator
            + project.getName() + File.separator + Project.SRC_FOLDER_NAME);

    // set the package name, we use ALWAYS the same
    this.xsltParameter.put("sim.package", "");

    if (!this.getProject().isGenerated()) {

      // transform the simulation description -> generate Java code
      this.xslt2Processor.transformFromURL(this.getProject()
          .getSimulationDescription(), xsltFile.toURI(), this.xsltParameter);

      // test if the Java code generation succeed
      if (this.xslt2Processor.getMessages().isEmpty()) {
        result = true;
        this.getProject().setGenerated(true);
      } else {
        for (String message : this.xslt2Processor.getMessages()) {
          System.err.println(message);
        }
        result = false;
      }
    } else {
      // project is already generated
      result = true;
    }

    return result;
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
   * Change the logger to another type
   * 
   * @param loggerType
   *          the new logger type.
   */
  public void setLoggerByDialogSelection(int loggerType) {

    // save the new logger type in the project property file
    this.properties.put(propertyLogger, String.valueOf(loggerType));

    // initialize the logger
    this.initLogger();
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
    } else {
      return (project.isGenerated() && project.isCompiled());
    }
  }

  /**
   * Gets the URLClassLoader object of the project. Required by modules to load
   * different classes when needed.
   * 
   * @return the project URLCLassLoader object
   */
  // public static URLClassLoader getProjectUrlClassLoader() {
  // /**
  // * The URL class loader requires that the project is instantiated, this
  // * meaning that the simulation needs to be instantiated too. The
  // * instantiation does not happens if it is already done this being checked
  // * inside the instanciateSimulation() method.
  // */
  // if (isProjectReady()) {
  // try {
  // project.instantiateSimulation();
  // } catch (SimulatorException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // }
  //
  // // get the class loader of this project
  // return project.getUrlClassLoader();
  // }

  /**
   * Gets the existing modules - non-grouped ones only.
   * 
   * @return the existing non-grouped modules
   */
  public List<Module> getModules() {
    return this.modules;
  }

  /**
   * Gets the existing modules - grouped ones only
   * 
   * @return the existing grouped modules
   */
  public List<Module> getGroupModules() {
    return this.groupModules;
  }

  /**
   * This method is called whenever the DOM is changed and need
   * re-initialization of initial state object.
   * 
   * @param simulationScenarioXML
   *          the XML simulation description as string representation
   */
  public void initializeDom(String simulationScenarioXML) {
    // no data bus, this has to be a mistake...
    if (this.dataBus == null) {
      System.out.println("Warning: DataBus is null!");
      return;
    }

    if (simulationScenarioXML == null || simulationScenarioXML.equals("")) {
      System.out
          .println("Warning: try to set an empty simulation description!");
      return;
    }

    this.simulationDescription.setDom(simulationScenarioXML);
    this.dataBus.notifyDomOnlyInitialization(simulationDescription);

    // no abstract simulator yet...
    if (this.getProject().getSimulation() == null) {
      return;
    }

    InitialState initialState = this.getProject().getSimulation()
        .getInitialState();
    // set the simulation description in initial state object
    initialState.setSimulationDescription(simulationDescription);
  }

  /**
   * Used with test purposes. Please don't delete this method.
   * 
   * @param args
   */
  public static void main(String[] args) {
    Console console = System.console();

    String messageNoConsole = "You are starting the console version of the AOR Simulator \n"
        + "without an associated console. Please re-run the application \n"
        + "from your console with:\n"
        + "\n"
        + "     java -jar AOR-Simulator_console.jar [options]\n" + "\n";

    // if the application is running without console, e.g. double click on the
    // JAR file
    if (console == null) {
      JOptionPane.showMessageDialog(null, messageNoConsole, "Error",
          JOptionPane.ERROR_MESSAGE);
    } else {

    }

    // test for the number of arguments
    if (args.length < 1) {
      System.out.println();
      System.out.println("Usage: ");
      System.out
          .println("  java -jar AOR_Simulator_console.jar [simulation description]");
      System.out.println();
      System.out.println("Example:");
      System.out.println("  java -jar AOR_Simulator_noGUI.jar mysim.xml");
    } else {
      System.out.println("more than zero arguments ");

    }
  }

  public void instantiateCurrentSimulation() throws SimulatorException {
    try {
      this.getProject().instantiateSimulation();
    } catch (SimulatorException e) {
      this.getProject().setGenerated(false);
      throw e;
    }
  }

  public void runSimulation() {
    SimulationManager.project.runSimulation(this.autoMultithreading);
  }
}
