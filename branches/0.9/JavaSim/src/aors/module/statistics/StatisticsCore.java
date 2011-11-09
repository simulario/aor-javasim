package aors.module.statistics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import aors.controller.InitialState;
import aors.controller.Project;
import aors.controller.SimulationDescription;
import aors.data.evt.ControllerEvent;
import aors.data.evt.sim.ObjektDestroyEvent;
import aors.data.evt.sim.ObjektInitEvent;
import aors.data.evt.sim.SimulationEvent;
import aors.data.evt.sim.SimulationStepEvent;
import aors.model.envevt.EnvironmentEvent;
import aors.module.Module;
import aors.module.statistics.gui.ComponentTranslator;
import aors.module.statistics.gui.StatisticsGui;
import aors.statistics.AbstractStatisticsVariable.StatVarDataSourceEnumLit;
import aors.util.jar.JarUtil;

/**
 * StatisticsCore
 * 
 * This is the implementation of the logic component
 * 
 * @author Daniel Draeger
 * @since 03.09.2009
 */
public class StatisticsCore implements Module {

  private boolean isModuleEnabled = true;
  private final StatisticsGui guiTab;
  private InitialState initialState;
  private SimulationDescription simDes;
  private boolean isInit, simulationLoaded;
  private boolean showCharts = false;
  private int simulationRuns = 0;
  private List<String> comparisons;
  private List<List<StatisticVar>> simulations = new ArrayList<List<StatisticVar>>();
  private List<StatisticVar> statisticVars;
  private List<FrequencyDistributionChart> freqDistChartVars;
  private String projectPath = "";
  private Map<String, StatisticVar> statsVarMap;
  private Map<Integer, String[]> htmlSvgDataConnector = new HashMap<Integer, String[]>();
  private Map<Integer, Integer[]> compData = new HashMap<Integer, Integer[]>();

  private SimulationStepEvent stepEvent;
  private final static String saveLocation = "saveLocationMsg";

  // local path in the temporarily directory for this module
  private static String localTmpPath = "statisticsModule";

  public static final String pathToHtmlFiles = JarUtil.TMP_DIR + File.separator
      + localTmpPath + File.separator;

  /**
   * Create a new {@code StatisticsCore}.
   * 
   */
  public StatisticsCore() {
    // initialize the module libraries
    initModuleLibraries();
    this.guiTab = new StatisticsGui(this);
    statisticVars = new ArrayList<StatisticVar>();
    freqDistChartVars = new ArrayList<FrequencyDistributionChart>();
    statsVarMap = new HashMap<String, StatisticVar>();
  }

  /**
   * Usage: check if the module is enabled
   * 
   * @return boolean
   */
  public boolean isModuleEnabled() {
    return isModuleEnabled;
  }

  /**
   * Usage: enable or disable module
   * 
   * @param b
   */
  public void setModuleEnabled(boolean b) {
    isModuleEnabled = b;
  }

  /**
   * Usage: initialize the modules libraries.
   */
  private void initModuleLibraries() {

    // path to jar
    String jarPath = System.getProperty("user.dir") + File.separator
        + "modules" + File.separator + "statisticsModule.jar";

    try {

      // extracting the HTML/CSS/JS files required for statistic results
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "html", "hover.css");
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "html", "hover.js");
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "html",
          "jquery-1.3.min.js");
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "html",
          "jquery.hoverIntent.minified.js");
      JarUtil
          .extractFileFromJar(jarPath, localTmpPath, "html", "jquery.svg.js");

      // extract the lang files
      JarUtil.extractFilesListFromJar(jarPath, localTmpPath, "lang");

      // extract used jar libraries
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib",
          "batik-awt-util.jar");
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib", "batik-dom.jar");
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib", "batik-ext.jar");
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib",
          "batik-svggen.jar");
      JarUtil
          .extractFileFromJar(jarPath, localTmpPath, "lib", "batik-util.jar");
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib", "batik-xml.jar");
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib", "colt.jar");
      JarUtil
          .extractFileFromJar(jarPath, localTmpPath, "lib", "concurrent.jar");
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib",
          "jcommon-1.0.16.jar");
      JarUtil.extractFileFromJar(jarPath, localTmpPath, "lib",
          "jfreechart-1.0.13.jar");

    } catch (IOException ex) {
      ex.printStackTrace();
    }

    // add this path in the library path...
    JarUtil.setLibraryPath(localTmpPath);

    // load jars from that temporarily directory (statistics required jars)
    JarUtil.loadJar(localTmpPath, "batik-awt-util.jar");
    JarUtil.loadJar(localTmpPath, "batik-dom.jar");
    JarUtil.loadJar(localTmpPath, "batik-ext.jar");
    JarUtil.loadJar(localTmpPath, "batik-svggen.jar");
    JarUtil.loadJar(localTmpPath, "batik-util.jar");
    JarUtil.loadJar(localTmpPath, "batik-xml.jar");
    JarUtil.loadJar(localTmpPath, "colt.jar");
    JarUtil.loadJar(localTmpPath, "concurrent.jar");
    JarUtil.loadJar(localTmpPath, "jcommon-1.0.16.jar");
    JarUtil.loadJar(localTmpPath, "jfreechart-1.0.13.jar");
  }

  @Override
  public Object getGUIComponent() {
    return this.guiTab;
  }

  /**
   * Usage: return the project path
   * 
   * @return String
   */
  public String getProjectPath() {
    return projectPath;
  }

  /**
   * Usage: return the InitialState
   * 
   * @return InitialState
   */
  public InitialState getInitialState() {
    return this.initialState;
  }

  /**
   * Usage: return the simulation (list with statistic variables for each
   * simulation run)
   * 
   * @return List
   */
  public List<List<StatisticVar>> getStatisticVarsFromAllSimulations() {
    return this.simulations;
  }

  /**
   * Usage: return the simulation description
   * 
   * @return SimulationDescription
   */
  public SimulationDescription getSimDom() {
    return simDes;
  }

  @Override
  public void simulationInitialize(InitialState initialState) {
    // TODO Auto-generated method stub
    this.initialState = initialState;
  }

  @Override
  public void simulationPaused(boolean pauseState) {
    // TODO Auto-generated method stub

  }

  @Override
  public void simulationStarted() {
    // TODO Auto-generated method stub
    if (isModuleEnabled) {
      if (showCharts) {
        guiTab.getTabStatistics().getStatsChartPanel().clearCharts();
      }
      for (int i = 0; i < statisticVars.size(); i++) {
        statisticVars.get(i).setCurrentStep(0);
      }
      for (int i = 0; i < freqDistChartVars.size(); i++) {
        freqDistChartVars.get(i)
            .setValues(new HashMap<Integer, List<Number>>());
      }
      // if editor is working, changes should be loaded without reopening
      simulationLoaded = false;

      guiTab.getTabStatistics().setLangComboBox(false);
    }
  }

  @Override
  public void simulationEnded() {
    // TODO Auto-generated method stub
    if (isModuleEnabled) {
      if (simulationRuns == 0) {
        simulations = new ArrayList<List<StatisticVar>>();
      } else if (simulationRuns == 1) {
        guiTab.getTabStatistics().getStatsVarsPanel().getPanelMultiSim()
            .enableButtons();
      }
      getActualStatsVarValue(true);
      List<StatisticVar> varList = new ArrayList<StatisticVar>();
      for (int i = 0; i < statisticVars.size(); i++) {
        StatisticVar var;
        var = new StatisticVar(statisticVars.get(i).getName(), statisticVars
            .get(i).getLastValue());
        var.setStatsVarUIMap(statisticVars.get(i).getStatsVarUIMap());
        varList.add(var);
      }
      try {
        guiTab.getTabStatistics().getStatsChartPanel()
            .saveSingleSimulationCharts();
      } catch (Exception e) {
      }
      simulations.add(varList);
      simulationRuns++;
    }
    guiTab.getTabStatistics().setSimulationRuns(simulationRuns);
    guiTab.getTabStatistics().setLangComboBox(true);

  }

  @Override
  public void simulationStepEnd(SimulationStepEvent simulationStepEvent) {

    this.stepEvent = simulationStepEvent;
    if (isModuleEnabled) {
      for (int i = 0; i < statisticVars.size(); i++) {
        if (!statisticVars.get(i).isComputeOnlyAtEnd()) {
          statisticVars.get(i).incCurrentStep();
        }
      }

      getActualStatsVarValue(false);
      if (showCharts) {
        guiTab.getTabStatistics().getStatsChartPanel()
            .updateCharts(statisticVars);
      }
    }
  }

  @Override
  public void simulationStepStart(long stepNumber) {
    // TODO Auto-generated method stub

  }

  @Override
  public void simulationEnvironmentEventOccured(
      EnvironmentEvent environmentEvent) {
    // TODO Auto-generated method stub

  }

  @Override
  public void simulationInfosEvent(SimulationEvent simulationEvent) {
    // TODO Auto-generated method stub

  }

  /**
   * Usage: if project path exists the statistics files are saved in the media
   * directory of it otherwise the directory have to be chosen by the user
   * 
   * @param path
   * @return
   */
  protected static String getLocationPath(String path) {
    JOptionPane.setDefaultLocale(ComponentTranslator.getResourceBundle()
        .getLocale());
    JFileChooser fc = new JFileChooser();
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fc.setCurrentDirectory(new File(StatisticsCore.TEMP_DIR));
    // language specific DialogTitle if supported by whole simulator
    fc.setDialogTitle(ComponentTranslator.getResourceBundle().getString(
        saveLocation));
    int ret = fc.showSaveDialog(null);
    if (ret == JFileChooser.CANCEL_OPTION) {
      System.out.println("Default Statistics Directory: "
          + StatisticsCore.TEMP_DIR);
      return StatisticsCore.TEMP_DIR;
    } else {
      return fc.getSelectedFile().getPath();
    }
  }

  @Override
  public void simulationProjectDirectoryChanged(File projectDirectory) {
    setModuleEnabled(true);
    this.isInit = false;
    showCharts = false;
    simulationLoaded = false;
    simulationRuns = 0;
    if (isModuleEnabled()) {
      File statisticsFolder;
      if (projectDirectory.getPath() == null) {
        this.projectPath = getLocationPath(projectDirectory.getPath());
        statisticsFolder = new File(this.projectPath);
      } else {
        statisticsFolder = new File(projectDirectory.getPath() + File.separator
            + Project.MEDIA_FOLDER_NAME + File.separator
            + Project.MEDIA_HTML_FOLDER_NAME);
        this.projectPath = statisticsFolder.getPath();
      }
      statisticsFolder.mkdir();
    }
    this.simulations = new ArrayList<List<StatisticVar>>();
    htmlSvgDataConnector = new HashMap<Integer, String[]>();
    compData = new HashMap<Integer, Integer[]>();
  }

  @Override
  public void simulationDomOnlyInitialization(
      SimulationDescription simulationDescription) {
    // TODO Auto-generated method stub
    if (isModuleEnabled && simulationRuns == 0) {
      this.simDes = simulationDescription;
      XMLDataReader xmlReader = new XMLDataReader(simulationDescription);
      statsVarMap = xmlReader.getStatisticVars();
      if (!simulationLoaded) {
        statisticVars = new ArrayList<StatisticVar>();
        for (String key : statsVarMap.keySet()) {
          if (statsVarMap.get(key).getStatsVarUIMap()
              .get(StatisticVar.DISPLAYNAME) != null) {
            statisticVars.add(statsVarMap.get(key));
          }
        }
        Collections.sort(statisticVars, new Comparator<StatisticVar>() {
          public int compare(StatisticVar o1, StatisticVar o2) {
            String id1 = o1.getStatsVarUIMap().get(StatisticVar.ID);
            String id2 = o2.getStatsVarUIMap().get(StatisticVar.ID);
            return -id2.compareTo(id1);
          }
        });
        simulationLoaded = true;
      }
      freqDistChartVars = xmlReader.getFrequencyDistributionChartVars();
      comparisons = xmlReader.getComparisons();
      if (isInit == false) {
        guiTab.getTabStatistics().setStatsVarsPanel();
        guiTab.getTabStatistics().getStatsVarsPanel()
            .initialization(statisticVars);
        guiTab.getTabStatistics().setStatsChartPanel();
        guiTab.getTabStatistics().getStatsChartPanel().deleteImageFolder();
        isInit = true;
        simulationRuns = 0;
        guiTab.getTabStatistics().setSimulationRuns(simulationRuns);
      }
      if (statisticVars.size() == 0) {
        guiTab.getTabStatistics().setModuleVisibiliy(false);
        guiTab.getTabStatistics().setDisableButtonEnable(false);
      } else {
        guiTab.getTabStatistics().setModuleVisibiliy(true);
        guiTab.getTabStatistics().setDisableButtonEnable(true);
      }
      if (freqDistChartVars.size() > 0) {
        guiTab.getTabStatistics().getStatsVarsPanel().getPanelSingleSim()
            .enableButtons();
      }
    }
  }

  @Override
  public void objektDestroyEvent(ObjektDestroyEvent objektDestroyEvent) {
    // nothing to do
  }

  @Override
  public void objektInitEvent(ObjektInitEvent objInitEvent) {
    // nothing to do
  }

  /**
   * Usage: return the comparison groups
   * 
   * @return List
   */
  public List<String> getComparisonGroups() {
    return comparisons;
  }

  /**
   * Usage: update the values of all statistic variables
   * 
   * @param computeOnlyAtEnd
   */
  private void getActualStatsVarValue(boolean computeOnlyAtEnd) {
    for (String key : statsVarMap.keySet()) {
      if ((statsVarMap.get(key).isComputeOnlyAtEnd() == computeOnlyAtEnd)
          && (statsVarMap.get(key).hasFrequencyDistributionChart())) {
        setFreqDistValue(statsVarMap.get(key));
      }
    }
    for (int i = 0; i < statisticVars.size(); i++) {
      if (statisticVars.get(i).isComputeOnlyAtEnd() == computeOnlyAtEnd) {
        statisticVars.get(i).setSimulationstepValue(initialState, stepEvent);
        guiTab.getTabStatistics().getStatsVarsPanel()
            .formatStatsVar(statisticVars.get(i), i);
      }
    }
    guiTab.getTabStatistics().getStatsVarsPanel().getPanelSingleSim()
        .updateAnalyseValues(statisticVars);
  }

  /**
   * Usage: change the language of simulation specific values
   * 
   * @param lang
   */
  public void updateStatsVarLang(String lang) {
    XMLDataReader xmlReader = new XMLDataReader(this.simDes);
    for (int i = 0; i < statisticVars.size(); i++) {
      xmlReader.getStatisticsVarUI(statisticVars.get(i), lang);
      if (simulations.size() > 0) {
        simulations.get(0).get(i)
            .setStatsVarUIMap(statisticVars.get(i).getStatsVarUIMap());
      }
    }
    guiTab.getTabStatistics().getStatsVarsPanel().updateLanguage(statisticVars);
  }

  /**
   * Usage: updates the FrequencyDistributionChart of the StatisticVar var
   */
  public void setFreqDistValue(StatisticVar var) {
    List<FrequencyDistributionChart> fList = this.freqDistChartVars;
    InitialState is = this.initialState;
    try {
      if ((var.getSourceDataSource()
          .equals(StatVarDataSourceEnumLit.ObjectProperty))
          && (var.getSourceObjIdRef() == StatisticVar.DEFAULTIDREF)) {
        Map<Long, Double> valueList = is.getObjektIDRefPropertyIteration(var
            .getName());
        for (int f = 0; f < fList.size(); f++) {
          if (fList.get(f).getName().equalsIgnoreCase(var.getName())) {
            for (Long id : valueList.keySet()) {
              if (fList.get(f).getValues().containsKey(id.intValue())) {
                fList.get(f).addValue(id.intValue(), valueList.get(id));
              } else {
                List<Number> initList = new ArrayList<Number>();
                initList.add(valueList.get(id));
                fList.get(f).getValues().put(id.intValue(), initList);
              }
            }
            ;
          }
        }
      } else {
        double val = is.getStatisticVariableValueFloat(var.getName());
        for (int f = 0; f < fList.size(); f++) {
          if (fList.get(f).getName().equalsIgnoreCase(var.getName())) {
            if (fList.get(f).getValues().containsKey(0)) {
              fList.get(f).addValue(0, val);
            } else {
              List<Number> initList = new ArrayList<Number>();
              initList.add(val);
              fList.get(f).getValues().put(0, initList);
            }
          }
        }
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Usage: return the list of statistic variables
   * 
   * @return List
   */
  public List<StatisticVar> getStatisticVars() {
    return statisticVars;
  }

  /**
   * Usage: return the list of frequency distribution charts
   * 
   * @return List
   */
  public List<FrequencyDistributionChart> getFrequencyDistributionCharts() {
    return freqDistChartVars;
  }

  /**
   * Usage: indicate if charts are shown
   * 
   * @return boolean
   */
  public boolean isShowCharts() {
    return showCharts;
  }

  /**
   * Usage: set the showCharts to the given boolean value
   * 
   * @param b
   */
  public void setShowCharts(boolean b) {
    showCharts = b;
  }

  /**
   * Usage: return the number of simulation runs
   * 
   * @return int
   */
  public int getSimulationRuns() {
    return simulationRuns;
  }

  public void setHtmlSvgDataConnector(
      Map<Integer, String[]> htmlSvgDataConnector) {
    this.htmlSvgDataConnector = htmlSvgDataConnector;
  }

  public Map<Integer, String[]> getHtmlSvgDataConnector() {
    return htmlSvgDataConnector;
  }

  public void setCompData(Map<Integer, Integer[]> compData) {
    this.compData = compData;
  }

  public Map<Integer, Integer[]> getCompData() {
    return compData;
  }

  @Override
  public void notifyEvent(ControllerEvent event) {
    // TODO Auto-generated method stub

  }
}
