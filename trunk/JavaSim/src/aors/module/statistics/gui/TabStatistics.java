package aors.module.statistics.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import aors.module.statistics.StatisticsCore;
import aors.module.statistics.StatisticsHtmlOutput;

/**
 * TabStatistics
 * 
 * This class specifies the main panel of the statistics module
 * 
 * @author Daniel Draeger
 * @since 01.11.2009
 */
public class TabStatistics extends JPanel implements ActionListener {

  private static final long serialVersionUID = -8156953819500494886L;

  // gui components
  private final StatisticsCore module;
  private StatsVarsPanel statsVarsPanel;
  private StatsChartPanel statsChartPanel;
  private JPanel northPanel, southPanel;
  private JSplitPane centerSplitPane;
  private JLabel descriptionL, simulationRunsL, simulationRunsValueL;
  private JButton disableB, clearB, saveSelChartB, deleteChartsB, htmlB;
  private final List<String> languages;
  private final JComboBox langCB;
  protected static List<JComponent> compList;

  // language keys
  private final String description = "descriptionL";
  private final String clear = "clearChartB";
  private final String enable = "enableB";
  private final String disable = "disableB";
  private final String noOfSimRuns = "simulationRunsL";
  private final String saveSelChart = "saveSelectedChartB";
  private final String deleteCharts = "deleteChartsB";
  private final String createHtml = "createHtmlB";
  private final String SVGCreationNameMsg = "SVGCreationNameMsg";
  private final String noChartToSaveMsg = "noChartToSaveMsg";
  private final String chartsDeleted = "SVGDeletedMsg";
  private final String deleteSVGMsg = "deleteSVGMsg";

  /**
   * Create a new {@code TabStatistics}.
   * 
   * @param module
   */
  public TabStatistics(StatisticsCore module) {
    this.module = module;
    ComponentTranslator.getInstance();
    ComponentTranslator.compMap = new HashMap<String, JComponent>();
    languages = ComponentTranslator.languages;
    langCB = new JComboBox(languages.toArray());
    for (int i = 0; i < languages.size(); i++) {
      if ((Locale.getDefault().getDisplayLanguage()).equalsIgnoreCase(languages
          .get(i))) {
        langCB.setSelectedIndex(i);
      }
    }
    init();
    this.setLayout(new BorderLayout());
    this.add(BorderLayout.NORTH, northPanel);
    this.add(BorderLayout.CENTER, centerSplitPane);
    this.add(BorderLayout.SOUTH, southPanel);
    this.validateTree();
  }

  /**
   * Usage: initialize the statistics tab
   */
  public void init() {
    // northPanel
    northPanel = new JPanel(new BorderLayout());
    JPanel northEast = new JPanel();
    JPanel northCenter = new JPanel();
    descriptionL = new JLabel();
    ComponentTranslator.setGuiComponent(descriptionL, description);
    langCB.addActionListener(this);
    simulationRunsL = new JLabel();
    ComponentTranslator.setGuiComponent(simulationRunsL, noOfSimRuns);
    simulationRunsValueL = new JLabel("0");
    northCenter.add(simulationRunsL);
    northCenter.add(simulationRunsValueL);
    northEast.add(langCB);
    disableB = ComponentTranslator.setButton(disable, northEast, this, true);
    northPanel.add(BorderLayout.WEST, descriptionL);
    northPanel.add(BorderLayout.CENTER, northCenter);
    northPanel.add(BorderLayout.EAST, northEast);
    // centerPanel
    centerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    centerSplitPane.setOneTouchExpandable(true);
    // southPanel
    southPanel = new JPanel();
    clearB = ComponentTranslator.setButton(clear, southPanel, this, true);
    saveSelChartB = ComponentTranslator.setButton(saveSelChart, southPanel,
        this, true);
    deleteChartsB = ComponentTranslator.setButton(deleteCharts, southPanel,
        this, true);
    addDivider(southPanel);
    htmlB = ComponentTranslator.setButton(createHtml, southPanel, this, true);

  }

  /**
   * Usage: return the StatsVarsPanel
   * 
   * @return StatsVarsPanel
   */
  public StatsVarsPanel getStatsVarsPanel() {
    return statsVarsPanel;
  }

  /**
   * Usage: set StatsVarsPanel on top of centerSplitPane
   */
  public void setStatsVarsPanel() {
    statsVarsPanel = new StatsVarsPanel(module);
    centerSplitPane.setTopComponent(statsVarsPanel);
  }

  /**
   * Usage: return the StatsChartPanel
   * 
   * @return StatsChartPanel
   */
  public StatsChartPanel getStatsChartPanel() {
    return statsChartPanel;
  }

  /**
   * Usage: set StatsVarsPanel on bottom of centerSplitPane
   */
  public void setStatsChartPanel() {
    statsChartPanel = new StatsChartPanel(module);
    centerSplitPane.setBottomComponent(statsChartPanel);
    module.setShowCharts(false);
    this.validate();
  }

  /**
   * Usage: enable / disable JComboBox for language choice
   * 
   * @param enable
   */
  public void setLangComboBox(boolean enable) {
    this.langCB.setEnabled(enable);
  }

  /**
   * Usage: enable / disable button
   * 
   * @param enable
   */
  public void setDisableButtonEnable(boolean enable) {
    disableB.setEnabled(enable);
  }

  /**
   * Usage: create a divider for the GUI
   * 
   * @param destination
   */
  public void addDivider(JPanel destination) {
    destination.add(new JLabel(" | "));
  }

  /**
   * Usage: enable / disable module
   * 
   * @param visible
   */
  public void setModuleVisibiliy(boolean visible) {
    module.setModuleEnabled(visible);
    ComponentTranslator.compMap.remove(disable);
    if (visible) {
      ComponentTranslator.setGuiComponent(disableB, disable);
    } else {
      ComponentTranslator.setGuiComponent(disableB, enable);
    }
    this.statsChartPanel.setVisible(visible);
    this.statsVarsPanel.setVisible(visible);
    this.southPanel.setVisible(visible);
  }

  /**
   * Usage: set text for the simulation runs
   * 
   * @param simRuns
   */
  public void setSimulationRuns(int simRuns) {
    simulationRunsValueL.setText(simRuns + "");
  }

  public void setSplitDevider() {
    centerSplitPane.setDividerLocation(statsVarsPanel.getHeight());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub
    Object src = e.getSource();
    if (src == disableB) {
      if (module.isModuleEnabled()) {
        setModuleVisibiliy(false);
      } else {
        setModuleVisibiliy(true);
      }
    } else if (src == langCB) {
      // change language
      ComponentTranslator
          .setResourceBundle(ComponentTranslator.supportedLocales.get(langCB
              .getSelectedIndex()));
      // update simulation components
      module.updateStatsVarLang(ComponentTranslator.supportedLocales.get(
          langCB.getSelectedIndex()).getLanguage());
      // update gui components
      ComponentTranslator.translateGuiComponents();
      this.statsVarsPanel.getPanelMultiSim().updateToolTips();
      this.validate();

    } else if (src == clearB) {
      setStatsChartPanel();
    } else if (src == saveSelChartB) {

      JOptionPane.setDefaultLocale(ComponentTranslator.getResourceBundle()
          .getLocale());
      if (this.statsChartPanel.getChartBig().getComponentCount() == 0) {
        JOptionPane.showMessageDialog(this, ComponentTranslator
            .getResourceBundle().getString(noChartToSaveMsg));
      } else {
        String name = JOptionPane.showInputDialog(ComponentTranslator
            .getResourceBundle().getString(SVGCreationNameMsg), "");
        if (name != null) {
          for (int i = 0; i < statsChartPanel.getChartBig().getComponentCount(); i++) {
            JFreeChart chart = ((ChartPanel) (this.statsChartPanel
                .getChartBig().getComponent(i))).getChart();
            String fileName;
            ;
            if (statsChartPanel.getChartBig().getComponentCount() == 1) {
              fileName = statsChartPanel.getImagePath() + File.separator + name
                  + "_own.svg";
            } else {
              fileName = statsChartPanel.getImagePath() + File.separator + name
                  + i + "_own.svg";
            }
            int id = fileName.hashCode();
            try {
              new ChartCreator().convertToSVG(chart, id, fileName);
            } catch (IOException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
          }
        }
      }
      // can be deleted when whole Simulator supports internationalization
      JOptionPane.setDefaultLocale(new Locale("en"));
    }
    // delete image folder
    else if (src == deleteChartsB) {
      JOptionPane.setDefaultLocale(ComponentTranslator.getResourceBundle()
          .getLocale());
      int ret = JOptionPane.showConfirmDialog(this, ComponentTranslator
          .getResourceBundle().getString(deleteSVGMsg));
      if (ret == JOptionPane.YES_OPTION) {
        statsChartPanel.deleteImageFolder();
        System.out.println(ComponentTranslator.getResourceBundle().getString(
            chartsDeleted));
      }
      // can be deleted when whole Simulator supports internationalization
      JOptionPane.setDefaultLocale(new Locale("en"));

    }
    // html output
    else if (src == htmlB) {
      // if project path not exists choose directory
      String htmlPath = statsChartPanel.getImagePath();
      StatisticsHtmlOutput htmlOutput = new StatisticsHtmlOutput(htmlPath,
          module.getHtmlSvgDataConnector(), module.getCompData());
      // open in standard browser
      Desktop desktop = Desktop.getDesktop();
      URL url;
      try {
        url = new URL("file:///" + htmlOutput.getHtmlPath());
        desktop.browse(url.toURI());
      } catch (Exception urie) {
        System.out.println(urie.getMessage());
      }
      // can be deleted when whole Simulator supports internationalization
      JOptionPane.setDefaultLocale(new Locale("en"));
    }
  }

}
