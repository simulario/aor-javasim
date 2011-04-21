package aors.module.statistics.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import aors.module.statistics.StatisticVar;

public abstract class AbstractStatisticsInfoPanel extends JPanel implements
    ActionListener, ItemListener {

  /**
   * Abstract Class for the information- and interaction panels
   * 
   * @author Daniel Draeger
   * @since 19.11.2009
   */
  private static final long serialVersionUID = 8234794819253557084L;
  protected final StatsVarsPanel svp;
  protected final TabStatistics tabStatistics;
  private final JPanel centerPanel, statsInfoPanel;
  protected List<List<StatisticVar>> varsFromAllSim = null;
  protected JCheckBox selAll;
  protected JPanel chartOptionPanel, centerP, westP;
  protected JPanel actionPanel;
  protected List<JCheckBox> checkBoxList;
  protected JComboBox groups;

  // language keys
  private final String ownGroup = "ownGroupL";
  protected final String defaultL = "Default";
  protected final String infoOP = "infoL";
  protected final String information = "infoMsg";
  protected final String minL = "minL";
  protected final String maxL = "maxL";
  protected final String avgL = "avgL";

  protected KeyValuePair groupKV = new KeyValuePair(ownGroup);

  /**
   * Create a new {@code AbstractStatisticsInfoPanel}.
   * 
   * @param s
   *          StatsVarsPanel
   */
  public AbstractStatisticsInfoPanel(StatsVarsPanel s) {
    this.svp = s;
    this.tabStatistics = ((StatisticsGui) s.module.getGUIComponent())
        .getTabStatistics();
    this.setLayout(new BorderLayout());
    this.centerPanel = new JPanel(new GridLayout(1, 2));
    this.statsInfoPanel = svp.initValuePanel();
    centerPanel.add(statsInfoPanel);
    chartOptionPanel = new JPanel(new BorderLayout());
    centerPanel.add(initChartOptionPanel());
    this.add(BorderLayout.CENTER, centerPanel);
    this.add(BorderLayout.SOUTH, initActionPanel());
  }

  /**
   * Usage: initialize the chart option panel
   * 
   * @return
   */
  protected JPanel initChartOptionPanel() {
    chartOptionPanel = new JPanel(new BorderLayout());
    chartOptionPanel.setBorder(BorderFactory.createEtchedBorder());
    westP = new JPanel();
    chartOptionPanel.add(BorderLayout.WEST, westP);
    centerP = new JPanel();
    chartOptionPanel.add(BorderLayout.CENTER, centerP);
    return chartOptionPanel;
  }

  /**
   * Usage: return checkBoxList
   * 
   * @return List
   */
  public List<JCheckBox> getCheckBoxes() {
    return checkBoxList;
  }

  /**
   * Usage: set checkBoxLsit
   * 
   * @param chbList
   */
  public void setCheckBoxes(List<JCheckBox> chbList) {
    this.checkBoxList = chbList;
  }

  /**
   * Usage: return checkbox to select all
   * 
   * @return
   */
  public JCheckBox getSelectAllChb() {
    return selAll;
  }

  /**
   * Usage: set the checkbox to select all
   * 
   * @param chb
   *          JCheckBox
   * @param al
   *          ActionListener
   */
  public void setSelectAllChb(JCheckBox chb, ActionListener al) {
    selAll = chb;
    selAll.addActionListener(al);
  }

  /**
   * Usage: returns the amount of selected checkboxes for comparison
   * 
   */
  protected int nrOfSelectedBoxes(List<JCheckBox> chb) {
    int length = 0;
    for (int i = 0; i < chb.size(); i++) {
      if (chb.get(i).isSelected()) {
        length++;
      }
    }
    return length;
  }

  /**
   * Usage: return the info panel
   * 
   * @return JPanel
   */
  public JPanel getStatsInfoPanel() {
    return statsInfoPanel;
  }

  /**
   * Usage: set the info panel
   * 
   * @param p
   */
  public void setStatsInfoPanel(JPanel p) {
    this.statsInfoPanel.removeAll();
    this.statsInfoPanel.add(BorderLayout.CENTER, p);
  }

  /**
   * Usage: set the combobox for group selection
   * 
   * @param insertDefault
   */
  protected void setGroupsComboBox(boolean insertDefault) {
    groups = new JComboBox(svp.module.getComparisonGroups().toArray());
    if (insertDefault) {
      groups.addItem(defaultL);
    }
    groups.addItem(groupKV);
    groups.setSelectedIndex(groups.getItemCount() - 1);
    groups.addItemListener(this);
    groups.setEnabled(false);
    groups.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.GRAY));
  }

  /**
   * Usage: return the combobox for the group selection
   * 
   * @return JComboBox
   */
  public JComboBox getGroupsComboBox() {
    return groups;
  }

  /**
   * Usage: disable all checkboxes
   * 
   * @param chbList
   */
  protected void clearCheckboxes(List<JCheckBox> chbList) {
    for (int i = 0; i < chbList.size(); i++) {
      chbList.get(i).setSelected(false);
    }
    selAll.setSelected(false);
  }

  /**
   * Usage: enable buttons
   */
  public abstract void enableButtons();

  /**
   * Usage: set action panel
   * 
   * @return
   */
  protected abstract JPanel initActionPanel();

  /**
   * Usage: fill chartOptionPanel
   * 
   * @param amount
   *          - no of variables
   */
  public abstract void fillChartOptions(int amount);

  /**
   * Usage: reset tooltips when changing language
   */
  public abstract void updateToolTips();

}
