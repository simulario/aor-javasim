package aors.module.statistics.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import aors.module.statistics.StatisticVar;

/**
 * PanelSingleSim
 * 
 * Panel for settings belonging to single simulation
 * 
 * @author Daniel Draeger
 * @since 02.11.2009
 */
public class PanelSingleSim extends AbstractStatisticsInfoPanel {

  private static final long serialVersionUID = -3665295139218695504L;
  protected JButton showChartB, showFreqDistChartB, histogramB;
  private List<StatisticVar> vars;
  private final int noOfFacts = 3;

  // language keys
  private final String showChart = "showChartSingleB";
  private final String showFreqDistChart = "showFreqDistChartB";
  private final String selectionL = "selectionL";
  private final String AnalyseL = "AnalyseSingleL";

  public PanelSingleSim(StatsVarsPanel s) {
    super(s);
  }

  @Override
  protected JPanel initActionPanel() {
    actionPanel = new JPanel();
    showChartB = ComponentTranslator.setButton(showChart, actionPanel, this,
        true);
    tabStatistics.addDivider(actionPanel);
    ComponentTranslator.setLabel(selectionL, actionPanel, null);
    setGroupsComboBox(true);
    groups.setEnabled(true);
    actionPanel.add(groups);
    tabStatistics.addDivider(actionPanel);
    showFreqDistChartB = ComponentTranslator.setButton(showFreqDistChart,
        actionPanel, this, false);
    return actionPanel;
  }

  @Override
  public void enableButtons() {
    showFreqDistChartB.setEnabled(true);
  }

  @Override
  public void fillChartOptions(int amount) {

    JPanel northP = new JPanel();
    northP.setLayout(new GridLayout(1, 1));
    // chartOptionPanel.add(BorderLayout.NORTH, northP);
    svp.addLabel(AnalyseL, centerP, true);
    for (int i = 1; i < noOfFacts; i++) {
      centerP.add(new JLabel(""));
    }
    centerP.setLayout(new GridLayout(amount + 2, noOfFacts));
    ((GridLayout) centerP.getLayout()).setVgap(-5);
    svp.addLabel(minL, centerP, false);
    svp.addLabel(maxL, centerP, false);
    svp.addLabel(avgL, centerP, false);

    for (int i = 0; i < (amount * noOfFacts); i++) {
      JLabel l = new JLabel("-");
      l.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(170,
          170, 170)));
      centerP.add(l);
    }
  }

  /**
   * Usage: update the analysed values
   * 
   * @param vars
   */
  public void updateAnalyseValues(List<StatisticVar> vars) {
    for (int i = 0; i < vars.size(); i++) {
      int place = noOfFacts * 2;
      ((JLabel) centerP.getComponent(i * noOfFacts + place)).setText(vars
          .get(i).getStatsVarUIMap().get(StatisticVar.MIN));
      place++;
      ((JLabel) centerP.getComponent(i * noOfFacts + place)).setText(vars
          .get(i).getStatsVarUIMap().get(StatisticVar.MAX));
      place++;
      ((JLabel) centerP.getComponent(i * noOfFacts + place)).setText(vars
          .get(i).getStatsVarUIMap().get(StatisticVar.AVG));
      place++;
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // TODO Auto-generated method stub
    Object src = e.getSource();

    // select all
    if (src == selAll) {
      if (selAll.isSelected()) {
        for (int i = 0; i < checkBoxList.size(); i++) {
          if (checkBoxList.get(i).isEnabled()) {
            checkBoxList.get(i).setSelected(true);
          }
        }
      } else {
        for (int i = 0; i < checkBoxList.size(); i++) {
          checkBoxList.get(i).setSelected(false);
        }
      }
    }
    // single charts
    else if (src == showChartB) {
      int amount = nrOfSelectedBoxes(checkBoxList);
      if (amount == 0) {
        JOptionPane.showMessageDialog(this, ComponentTranslator
            .getResourceBundle().getString(information), ComponentTranslator
            .getResourceBundle().getString(infoOP),
            JOptionPane.OK_CANCEL_OPTION);

      } else {
        if ((groups.getSelectedItem().equals(groupKV))
            || (groups.getSelectedItem().equals(defaultL))) {
          svp.setSingleCharts();
          // predefined comparisonGroup in simulation description
        } else {
          StatsChartPanel scp = ((StatisticsGui) svp.module.getGUIComponent())
              .getTabStatistics().getStatsChartPanel();
          scp.setSingleSimComparison(svp.statsVars, groups.getSelectedItem()
              .toString());
        }
        svp.module.setShowCharts(true);
      }
    } else if (src == showFreqDistChartB) {
      if (svp.module.getSimulationRuns() > 0) {
        ((StatisticsGui) svp.module.getGUIComponent()).getTabStatistics()
            .getStatsChartPanel().setFrequencyDistributionCharts(
                svp.module.getFrequencyDistributionCharts());
      }
    }
  }

  @Override
  public void updateToolTips() {
    // TODO Auto-generated method stub

  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    // TODO Auto-generated method stub
    if (e.getStateChange() == ItemEvent.SELECTED) {
      if (groups.getSelectedItem().equals(groupKV)) {
        clearCheckboxes(svp.checkBoxList);
        for (int i = 0; i < svp.checkBoxList.size(); i++) {
          svp.checkBoxList.get(i).setEnabled(true);
        }
        selAll.setEnabled(true);
      } else {
        clearCheckboxes(svp.checkBoxList);
        vars = svp.module.getStatisticVars();
        for (int i = 0; i < vars.size(); i++) {
          if (groups.getSelectedItem().equals(defaultL)) {
            Boolean showC = Boolean.parseBoolean(vars.get(i).getStatsVarUIMap()
                .get(StatisticVar.SHOWCHART));
            if (showC) {
              svp.checkBoxList.get(i).setSelected(true);
            }
          } else {
            String varGroup = vars.get(i).getStatsVarUIMap().get(
                StatisticVar.COMPARISONGROUP);
            if ((groups.getSelectedItem().toString()).equals(varGroup)) {
              svp.checkBoxList.get(i).setSelected(true);
            }
          }
          svp.checkBoxList.get(i).setEnabled(false);
          selAll.setEnabled(false);
        }
      }
    }
  }
}
