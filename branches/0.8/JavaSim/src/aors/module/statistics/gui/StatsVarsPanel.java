package aors.module.statistics.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.JFreeChart;

import aors.module.statistics.StatisticVar;
import aors.module.statistics.StatisticsCore;

/**
 * StatsVarsPanel
 * 
 * The StatsVarsPanel is the top panel of the tabStatistics and includes all
 * information and user-input
 * 
 * @author Daniel Draeger
 * @since 02.11.2009
 */
public class StatsVarsPanel extends JScrollPane implements ActionListener,
    ChangeListener {

  private static final long serialVersionUID = 2949097232146740075L;
  private StatsChartPanel statsChartPanel;
  protected final StatisticsCore module;
  private List<JLabel> labelList, nameList;
  protected List<StatisticVar> statsVars;
  private int shownVars;
  protected List<JCheckBox> checkBoxList;
  private JCheckBox selAll;
  protected JPanel statsVarPanel, globalP;
  protected GridBagConstraints gbc;
  private final PanelSingleSim singlePanel;
  private final PanelMultiSim multiPanel;
  private final JTabbedPane tabPane;
  private Map<TextAttribute, Number> headlineFont;
  private int currentTab = 0;

  // language keys
  private final String singleP = "singleP";
  private final String multiP = "multiP";
  private final String tabPaneTP = "tabPaneTP";
  private final String select = "selectL";
  private final String variableName = "VariableNameL";
  private final String variableValue = "VariableValueL";
  private final String selectAll = "selectAllCB";

  /**
   * Create a new {@code StatsVarsPanel}.
   * 
   * @param module
   *          StatisticsCore
   */
  public StatsVarsPanel(StatisticsCore module) {
    this.module = module;
    // Layout
    this.tabPane = new JTabbedPane();
    this.singlePanel = new PanelSingleSim(this);
    ComponentTranslator.setGuiComponent(singlePanel, singleP);
    this.multiPanel = new PanelMultiSim(this);
    ComponentTranslator.setGuiComponent(multiPanel, multiP);
    tabPane.addTab(singlePanel.getName(), singlePanel);
    tabPane.addTab(multiPanel.getName(), multiPanel);
    ComponentTranslator.setGuiComponent(tabPane, tabPaneTP);
    tabPane.addChangeListener(this);
    this.setViewportView(tabPane);
  }

  /**
   * Usage: creates a map with font definitions for the headlines
   * 
   * @param lab
   * @return Map
   */
  @SuppressWarnings("unchecked")
  public Map<TextAttribute, Number> createHeadlineMap(JLabel lab) {
    headlineFont = (Map<TextAttribute, Number>) lab.getFont().getAttributes();
    headlineFont.put(TextAttribute.SIZE, new Float(14.0));
    headlineFont.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
    headlineFont.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
    return headlineFont;
  }

  /**
   * Usage: initialize the value-panel
   * 
   * @return JPanel
   */
  protected JPanel initValuePanel() {
    statsVarPanel = new JPanel();
    statsVarPanel.setLayout(new BorderLayout());
    statsVarPanel.setBorder(BorderFactory.createEtchedBorder());
    return statsVarPanel;
  }

  /**
   * Usage: return the tabbed pane
   * 
   * @return JTabbedPane
   */
  public JTabbedPane getTabbedPane() {
    return tabPane;
  }

  /**
   * Usage: return the number of statistic variables with a displayname
   * 
   * @return int
   */
  private int getNumberOfShownVars() {
    shownVars = 0;
    for (int i = 0; i < statsVars.size(); i++) {
      if (statsVars.get(i).getStatsVarUIMap().get(StatisticVar.DISPLAYNAME) != null) {
        shownVars++;
      }
    }
    return shownVars;
  }

  /**
   * Usage: fills the Panel with the variable names and initialValues
   * 
   * @param vars
   */
  public void initialization(List<StatisticVar> vars) {
    this.statsVars = vars;
    shownVars = getNumberOfShownVars();
    globalP = new JPanel(new BorderLayout());
    gbc = new GridBagConstraints();

    JPanel eastP = new JPanel(new GridLayout(shownVars + 2, 1));
    eastP.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
    JPanel centerP = new JPanel(new GridLayout(shownVars + 2, 1));
    centerP.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
    JPanel westP = new JPanel(new GridLayout(shownVars + 2, 1));
    addLabel(select, westP, true);
    addLabel(variableName, centerP, true);
    addLabel(variableValue, eastP, true);

    selAll = new JCheckBox(selectAll);
    ComponentTranslator.setGuiComponent(selAll, selectAll);
    JLabel help1 = new JLabel(" ");
    JLabel help2 = new JLabel(" ");
    westP.add(selAll);
    centerP.add(help1);
    eastP.add(help2);
    createLabels(westP, centerP, eastP);
    singlePanel.setSelectAllChb(selAll, singlePanel);
    singlePanel.fillChartOptions(shownVars);
    multiPanel.fillChartOptions(shownVars);
    globalP.add(BorderLayout.WEST, westP);
    globalP.add(BorderLayout.CENTER, centerP);
    globalP.add(BorderLayout.EAST, eastP);
    singlePanel.setStatsInfoPanel(globalP);
  }

  /**
   * Usage: Creates a JLabel, adds it to the Components map and to the
   * destination JPanel
   * 
   * @param labelName
   *          language specific label name
   * @param destination
   *          JPanel where this JLabel has to be add
   * @param isHeadline
   *          true: if JLabel has to be formatted as headline false: else
   */
  public void addLabel(String labelName, JPanel destination, boolean isHeadline) {
    JLabel lab = new JLabel();
    ComponentTranslator.setGuiComponent(lab, labelName);
    if (isHeadline) {
      lab.setFont(new Font(createHeadlineMap(lab)));
    }
    destination.add(lab);
  }

  /**
   * Usage: for each statistic variable the components get created
   * 
   * @param chbP
   *          - JCheckBox
   * @param nameP
   *          - JLabel for name
   * @param valP
   *          - JLabel for value
   */
  private void createLabels(JPanel chbP, JPanel nameP, JPanel valP) {
    nameList = new ArrayList<JLabel>();
    labelList = new ArrayList<JLabel>();
    checkBoxList = new ArrayList<JCheckBox>();
    for (int i = 0; i < statsVars.size(); i++) {
      JCheckBox chb = new JCheckBox();
      JLabel name = new JLabel();
      name.setText(statsVars.get(i).getStatsVarUIMap().get(
          StatisticVar.DISPLAYNAME));
      name.setToolTipText(statsVars.get(i).getStatsVarUIMap().get(
          StatisticVar.TOOLTIP));
      JLabel val = new JLabel();
      try {
        val.setText(statsVars.get(i).getLastValue().toString());
      } catch (Exception e) {
        val.setText("0");
      }
      checkBoxList.add(chb);
      nameList.add(name);
      labelList.add(val);
      formatStatsVar(statsVars.get(i), i);
      chbP.add(chb);
      nameP.add(name);
      valP.add(val);
      name.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(170,
          170, 170)));
      val.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(170,
          170, 170)));
    }
    ((GridLayout) nameP.getLayout()).setVgap(-5);
    ((GridLayout) chbP.getLayout()).setVgap(-5);
    ((GridLayout) valP.getLayout()).setVgap(-5);
    singlePanel.setCheckBoxes(checkBoxList);
  }

  /**
   * Usage: formats the value output of the statistics variable
   * 
   * @param var
   * @param i
   */
  public void formatStatsVar(StatisticVar var, int i) {
    // output with DecimalFormat if defined, but keep real value for
    // calculations
    String deciP = var.getStatsVarUIMap().get(StatisticVar.DECIMALPLACES);
    String formatS = var.getStatsVarUIMap().get(StatisticVar.FORMAT);
    if (formatS == null) {
      formatS = "";
    } else {
      formatS = " " + formatS;
    }
    if ((deciP != null)) {
      String pattern;
      if (Integer.parseInt(deciP) == 0) {
        pattern = "#0";
      } else {
        pattern = "#0.";
        for (int d = 0; d < Integer.parseInt(deciP); d++) {
          pattern = pattern + "0";
        }
      }
      DecimalFormat df = new DecimalFormat(pattern);
      updateVarLabel(i, df.format(var.getLastValue()) + formatS);
    } else {
      updateVarLabel(i, var.getLastValue().toString() + formatS);
    }
  }

  /**
   * Usage: return the panel for single simulation
   * 
   * @return PanelSingleSim
   */
  public PanelSingleSim getPanelSingleSim() {
    return singlePanel;
  }

  /**
   * Usage: return the panel for multi simulation
   * 
   * @return PanelMultiSim
   */
  public PanelMultiSim getPanelMultiSim() {
    return multiPanel;
  }

  /**
   * Usage: update the label i with the value
   * 
   * @param i
   * @param value
   */
  public void updateVarLabel(int i, String value) {
    if (value != null) {
      labelList.get(i).setText(value);
    } else {
      labelList.get(i).setText("isNULL");
    }
    this.validate();
  }

  /**
   * Usage: updates the language of the StatisticVar labels & tooltips
   * 
   * @param var
   */
  public void updateLanguage(List<StatisticVar> var) {
    for (int i = 0; i < nameList.size(); i++) {
      nameList.get(i).setText(
          var.get(i).getStatsVarUIMap().get(StatisticVar.DISPLAYNAME));
      nameList.get(i).setToolTipText(
          var.get(i).getStatsVarUIMap().get(StatisticVar.TOOLTIP));
    }
  }

  /**
   * Usage: pass the data for creation of single charts to StatsChartPanel
   */
  public void setSingleCharts() {
    statsChartPanel = ((StatisticsGui) module.getGUIComponent())
        .getTabStatistics().getStatsChartPanel();
    statsChartPanel.getChartOverview().removeAll();
    statsChartPanel.setSingleCharts(statsVars, singlePanel.getCheckBoxes());
  }

  /**
   * Usage: enable all checkboxes
   */
  private void enableCheckBoxes() {
    for (int i = 0; i < checkBoxList.size(); i++) {
      checkBoxList.get(i).setEnabled(true);
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    // TODO Auto-generated method stub
    Object src = e.getSource();
    if (src == tabPane) {
      if (tabPane.getSelectedComponent() == singlePanel) {
        if (currentTab == 1) {
          statsChartPanel.clearCharts();
          singlePanel.setStatsInfoPanel((JPanel) multiPanel.getStatsInfoPanel()
              .getComponent(0));
          singlePanel.setCheckBoxes(multiPanel.getCheckBoxes());
          singlePanel
              .setSelectAllChb(multiPanel.getSelectAllChb(), singlePanel);
        }
        singlePanel.getGroupsComboBox().setSelectedItem(singlePanel.groupKV);
        currentTab = 0;
        this.validate();
      } else if (tabPane.getSelectedComponent() == multiPanel) {
        if (currentTab == 0) {
          multiPanel.setStatsInfoPanel((JPanel) singlePanel.getStatsInfoPanel()
              .getComponent(0));
          multiPanel.setCheckBoxes(singlePanel.getCheckBoxes());
          multiPanel.setSelectAllChb(singlePanel.getSelectAllChb(), multiPanel);
        }
        currentTab = 1;
        statsChartPanel = ((StatisticsGui) module.getGUIComponent())
            .getTabStatistics().getStatsChartPanel();
        statsChartPanel.chartList = new ArrayList<JFreeChart>();
        statsChartPanel.buttons = new ArrayList<JButton>();
        enableCheckBoxes();
        this.validate();
      }
    }
    ((StatisticsGui) module.getGUIComponent()).getTabStatistics()
        .setStatsChartPanel();
  }
}
