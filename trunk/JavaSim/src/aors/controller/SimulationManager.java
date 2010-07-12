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
import java.net.URLClassLoader;
import java.util.ArrayList;
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
import aors.module.Constants;
import aors.module.GUIModule;
import aors.module.Module;
import aors.util.jar.JarUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

  // the XSLT processor object
  private XSLT2Processor xslt2Processor;

  // the map with XSLT parameters
  private HashMap<String, String> xsltParameter;

  // Simulationdescription
  private SimulationDescription simulationDescription;

  // the current project assigned to the simulation manager
  private static Project project;

  // the list with all current loaded modules
  private List<Module> modules;

  // the user directory
  private final String APP_ROOT_DIRECTORY = System.getProperty("user.dir");

  // the projects directory
  public final static String PROJECT_DIRECTORY = "projects";

  private String AORSLDirectory;
  private String AORSLSchemaName;

  private String codeGenXsltDirectory;
  private String codeGenXsltName;

  // the current (used as defautl) schema name
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
		Properties generalproperties = new Properties();
		InputStream input;
		try {
			input = new FileInputStream(new File(Constants.modulesDirectory + File.separator + "properties.xml"));
			generalproperties.loadFromXML(input);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(InvalidPropertiesFormatException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		}

		// directory not there, then is no module, so just ignore
		if(moduleJars == null) {
			// nothing to do further...
			return;
		}

		List<ValuePair<JarFile, Integer>> modulesToLoad = new ArrayList<ValuePair<JarFile, Integer>>();

		System.out.println("********** Manager: Load modules **********");

		// load GUI component of the module
		for(File moduleJar : moduleJars) {

			// only jar files are considered, ignore all others possible files
			if(!moduleJar.getName().endsWith(".jar")) {
				continue;
			}

			try {
				// new jar file...so, new module
				JarFile jarFile = new JarFile(moduleJar);

				// access the properties file from inside this jar
				JarEntry jarEntry = jarFile.getJarEntry("properties.xml");

				// no property file...then we can't continue loading this module.
				if(jarEntry == null) {
					System.out.println("The module: " + moduleJar.getName() + " does not " +
						"contains a property.xml file! This module can't be loaded! \n");

					// go to the next jar file (new module) if there is one
					continue;
				}

				// initially the position is undefined
				int pos = -1;

				// try to get the tab position for this module, if there is defined a
				// position in the general modules property file...
				if(generalproperties.getProperty(moduleJar.getName()) != null) {
					pos = Integer.parseInt(generalproperties.getProperty(moduleJar.getName()));
				}

				modulesToLoad.add(new ValuePair<JarFile, Integer>(jarFile, pos));

			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		this.modules = loadModules(modulesToLoad);

		System.out.println("*******************************************");
	}

	private List<Module> loadModules(
		List<ValuePair<JarFile, Integer>> modulesToLoad) {
		Map<String, ValuePair<Module, Integer>> loadedModules =
			new HashMap<String, ValuePair<Module, Integer>>();
		Map<String, Set<ValuePair<JarFile, Integer>>> dependingModules =
			new HashMap<String, Set<ValuePair<JarFile, Integer>>>();

		for(ValuePair<JarFile, Integer> moduleToLoad : modulesToLoad) {
			try {
				tryToLoadModule(moduleToLoad, loadedModules, dependingModules);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

		List<Module> returnValue = new ArrayList<Module>();
		for(ValuePair<Module, Integer> loadedModule : loadedModules.values()) {
			Module module = loadedModule.value1;
			int pos = loadedModule.value2;

			//TODO: FIXME
			// add the new module to list according to its position
			if(pos >= 0 && returnValue.size() >= pos) {
				returnValue.add(pos - 1, module);
			} // no order found for this module, so it is added at end
			else {
				returnValue.add(module);
			}
		}
		return returnValue;
	}

	private void tryToLoadModule(ValuePair<JarFile, Integer> moduleToLoad,
		Map<String, ValuePair<Module, Integer>> loadedModules,
		Map<String, Set<ValuePair<JarFile, Integer>>> dependingModules)
		throws IOException {

		JarFile jarFile = moduleToLoad.value1;
		int pos = moduleToLoad.value2;

		// access the properties file of the module
		JarEntry jarEntry = jarFile.getJarEntry("properties.xml");
		Properties moduleProperties = new Properties();
		InputStream inputStream = jarFile.getInputStream(jarEntry);
		moduleProperties.loadFromXML(inputStream);

		String[] dependencies = moduleProperties.getProperty(
			Module.PROP_DEPENDS_ON_MODULE_CLASSES, " ").split("\\s+");

		// check if alle dependencies are available
		boolean isDependingOnAMissingModule = false;
		for(String dependency : dependencies) {
			if(!loadedModules.containsKey(dependency)) {
				if(!dependingModules.containsKey(dependency)) {
					dependingModules.put(dependency,
						new HashSet<ValuePair<JarFile, Integer>>());
				}
				dependingModules.get(dependency).add(
					new ValuePair<JarFile, Integer>(jarFile, moduleToLoad.value2));
				isDependingOnAMissingModule = true;
			}
		}
		if(isDependingOnAMissingModule) {
			return;
		}

		// start loading
		System.out.println(" - NAME: " + moduleProperties.getProperty(Module.PROP_NAME));

		// check if the OS version is specified, otherwise is loaded by default
		if(moduleProperties.getProperty(Module.PROP_MODULE_OS_VERSION) != null) {
			String propVal = moduleProperties.getProperty(Module.PROP_MODULE_OS_VERSION).
				trim().toLowerCase();

			// wrong OS...skip loading (the OS specified in prop file does not match
			// the PC OS
			if(!((JarUtil.isWindows() && propVal.equals(Module.PROP_MODULE_OS_VERSION_VALUE_WINDOWS)) ||
				(JarUtil.isUnix() && propVal.equals(Module.PROP_MODULE_OS_VERSION_VALUE_LINUX)) ||
				(JarUtil.isMac() && propVal.equals(Module.PROP_MODULE_OS_VERSION_VALUE_MAC)))) {
				System.out.println(" - OS Version: not match! Skip loading of this " +
					"module! \n");
				return;
			}

			System.out.println(" - OS Version: " + propVal);
		} else {
			System.out.println(" - OS Version: ALL");
		}

		// check if the OS bits version is specified
		if(moduleProperties.getProperty(Module.PROP_MODULE_OS_BITS_VERSION) != null) {
			String propValue = moduleProperties.getProperty(Module.PROP_MODULE_OS_BITS_VERSION).trim();
			boolean is64BitsOs = System.getProperty("os.arch").indexOf("64") != -1;

			// OS Bits version does not match the version from property file.
			if(!((propValue.equals("32") && !is64BitsOs) || (propValue.equals("64") && is64BitsOs))) {
				System.out.println(" - OS Bits Version: not match! Skip loading of " +
					"this module! \n");
				return;
			}

			System.out.println(" - OS Bits Version: " + moduleProperties.getProperty(Module.PROP_MODULE_OS_BITS_VERSION));
		} else {
			System.out.println(" - OS Bits Version: ALL");
		}

		// check if we have the base component for this module
		if(moduleProperties.getProperty(Module.PROP_BASE_MODULE_CLASS) == null) {
			System.out.println(" - HAS BASE: NO... Failed to load this module. \n");
			return;
		}
		System.out.println(" - HAS BASE: YES");

		// check if we have a GUI component for this module
		if(moduleProperties.getProperty(GUIModule.PROP_GUI_MODULE_CLASS) != null) {
			System.out.println(" - HAS GUI: YES");
		} else {
			System.out.println(" - HAS GUI: NO");
		}

		// return the priority
		if(pos < 0) {
			System.out.println(" - PRIORITY: NONE");
		} else {
			System.out.println(" - PRIORITY: " + pos);
		}

		Module module = null;
		try {
			module = loadModule(moduleToLoad, loadedModules);
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InstantiationException e) {
			e.printStackTrace();
		} catch(MalformedURLException e) {
			e.printStackTrace();
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} catch(NoSuchMethodException e) {
			e.printStackTrace();
		} catch(InvocationTargetException e) {
			e.printStackTrace();
		}

		if(module != null) {
			// module is a listener for simulation step events
			this.dataBus.addSimulationStepEventListener(module);

			// module is a listener for simulation events
			this.dataBus.addSimulationEventListener(module);

			// the module is a listener for destroy object/agent event
			this.dataBus.addDestroyObjektEventListener(module);

			// the module is a listener for object/agent creation events
			this.dataBus.addObjektInitEventListener(module);
			
		// the module is a listener for ControllerEvents
      this.dataBus.addControllerEventListener(module);

			// get the module tab title from the property file
			if(module.getGUIComponent() != null && moduleProperties.getProperty(GUIModule.PROP_GUI_TITLE) != null) {
				((JScrollPane)module.getGUIComponent()).setName(moduleProperties.getProperty(GUIModule.PROP_GUI_TITLE));
			}
		}
		System.out.println();

		String moduleClassName =
						moduleProperties.getProperty(Module.PROP_BASE_MODULE_CLASS);

		if(module != null && moduleClassName != null &&
			dependingModules.containsKey(moduleClassName)) {
			for(ValuePair<JarFile, Integer> dependingModule : dependingModules.
				remove(moduleClassName)) {
				tryToLoadModule(dependingModule, loadedModules, dependingModules);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private Module loadModule(ValuePair<JarFile, Integer> moduleToLoad,
		Map<String, ValuePair<Module, Integer>> loadedModules) throws
		IllegalAccessException, InstantiationException, MalformedURLException,
		ClassNotFoundException, IOException, NoSuchMethodException,
		InvocationTargetException {

		JarFile jarFile = moduleToLoad.value1;

		// access the properties file of the module
		JarEntry jarEntry = jarFile.getJarEntry("properties.xml");
		Properties moduleProperties = new Properties();
		InputStream inputStream = jarFile.getInputStream(jarEntry);
		moduleProperties.loadFromXML(inputStream);

		// define this Jar file as usable - See JarUtil for more details...
		JarUtil.loadJar(new URL("file:///" + jarFile.getName()));

		String className = moduleProperties.getProperty(
			Module.PROP_BASE_MODULE_CLASS);
		String[] dependencies = moduleProperties.getProperty(
			Module.PROP_DEPENDS_ON_MODULE_CLASSES, " ").split("\\s+");

		// load the base logic module
		Class<? extends Object> moduleClass = Class.forName(className);

		List<Class<? extends Module>> parameterClasses
			= new ArrayList<Class<? extends Module>>();
		List<Module> parameterObjects = new ArrayList<Module>();

		for(String dependency : dependencies) {
			parameterClasses.add(Module.class);
			parameterObjects.add(loadedModules.get(dependency).value1);
		}

		Class[] parameters = new Class[parameterClasses.size()];
		parameters = parameterClasses.toArray(parameters);
		Constructor moduleConstructor =
			moduleClass.getConstructor(parameterClasses.toArray(parameters));
		Module module = ((Module)moduleConstructor.newInstance(parameterObjects.toArray()));
		loadedModules.put(className, new ValuePair<Module, Integer>(module,
			moduleToLoad.value2));
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
      this.setProperties();

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
    this.properties.put(this.propertyAutoMultithreading, String
        .valueOf(autoMultithreading));
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
    project.setAutoMultithreading(this.autoMultithreading);
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

    // set whether to use MT on Multi-Core CPU's
    project.setAutoMultithreading(this.autoMultithreading);
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

    try {
      // lookup a factory for the W3C XML Schema language
      SchemaFactory factory = SchemaFactory
          .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

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

    this.xsltParameter.put("sim.package.root", "file:///"
        + project.getDirectory() + File.separator + project.getName()
        + File.separator + Project.SRC_FOLDER_NAME);

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
    if (project != null) {
      project.setAutoMultithreading(this.autoMultithreading);
    }
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
  public static URLClassLoader getProjectUrlClassLoader() {
    /**
     * The URL class loader requires that the project is instantiated, this
     * meaning that the simulation needs to be instantiated too. The
     * instantiation does not happens if it is already done this being checked
     * inside the instanciateSimulation() method.
     */
    if (isProjectReady()) {
      project.instantiateSimulation();
    }

    // get the class loader of this project
    return project.getUrlClassLoader();
  }

  /**
   * Gets the existing modules.
   * 
   * @return the existing modules
   */
  public List<Module> getModules() {
    return this.modules;
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

	private class ValuePair<T1, T2> {

		public T1 value1;
		public T2 value2;

		public ValuePair(T1 value1, T2 value2) {
			this.value1 = value1;
			this.value2 = value2;
		}

		@Override
		public String toString() {
			return "(" + value1.toString() + ", " + value2.toString() + ")";
		}
	}
}
