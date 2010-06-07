package aors.module.statistics.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.data.category.DefaultIntervalCategoryDataset;

import aors.module.statistics.MultiSimAnalyser;
import aors.module.statistics.StatisticVar;

/**
 * PanelMultiSim
 * 
 * @author Daniel Draeger
 * @since 02.11.2009
 */
public class PanelMultiSim extends AbstractStatisticsInfoPanel {

  private static final long serialVersionUID = -8173135142015982291L;
  protected JButton comparisonB, analyseB, confidenceB, abortB, freqChartB;
  private final int noOfFacts = 6;
  private JComboBox alphaCombo, epsiCombo;

  private Double[] alphaValues;
  private Double[] epsiValues;

  private MultiSimAnalyser calculator;

  // language keys
  private final String AnalyseL = "AnalyseMultiL";
  private final String stdDevL = "stdDevL";
  private final String halfWidthL = "halfWidthL";
  private final String skewnessL = "skewnessL";
  private final String solvedL = "solvedL";
  private final String comparisonChart = "comparisonChartB";
  private final String analyse = "analyseB";
  private final String runs2SolveMsg = "runs2SolveMsg";
  private final String alpha = "1-alpha";
  private final String epsilon = "epsilon";

  private final String alphaToolTip = ComponentTranslator.getResourceBundle()
      .getString("alphaTT");
  private final String epsiToolTip = ComponentTranslator.getResourceBundle()
      .getString("epsiTT");
  private String halfWConfTT = ComponentTranslator.getResourceBundle()
      .getString("halfWConfTT");
  private String skewTT = ComponentTranslator.getResourceBundle().getString(
      "skewTT");
  private String stdDevTT = ComponentTranslator.getResourceBundle().getString(
      "stdDevTT");

  /**
   * Create a new {@code PanelMultiSim}.
   * 
   * @param s
   *          StatsVarsPanel
   */
  public PanelMultiSim(StatsVarsPanel s) {
    super(s);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void enableButtons() {
    // TODO Auto-generated method stub
    analyseB.setEnabled(true);
  }

  @Override
  public void fillChartOptions(int amount) {
    // TODO Auto-generated method stub
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
    svp.addLabel(stdDevL, centerP, false);
    svp.addLabel(halfWidthL, centerP, false);
    svp.addLabel(skewnessL, centerP, false);
    ((JLabel) centerP.getComponent(noOfFacts + 3)).setToolTipText(stdDevTT);
    ((JLabel) centerP.getComponent(noOfFacts + 4)).setToolTipText(halfWConfTT);
    ((JLabel) centerP.getComponent(noOfFacts + 5)).setToolTipText(skewTT);

    for (int i = 0; i < (amount * noOfFacts); i++) {
      JLabel l = new JLabel("0");
      l.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(170,
          170, 170)));
      centerP.add(l);
    }

  }

  @Override
  public void updateToolTips() {
    halfWConfTT = ComponentTranslator.getResourceBundle().getString(
        "halfWConfTT");
    skewTT = ComponentTranslator.getResourceBundle().getString("skewTT");
    stdDevTT = ComponentTranslator.getResourceBundle().getString("stdDevTT");
    ((JLabel) centerP.getComponent(noOfFacts + 3)).setToolTipText(stdDevTT);
    ((JLabel) centerP.getComponent(noOfFacts + 4)).setToolTipText(halfWConfTT);
    ((JLabel) centerP.getComponent(noOfFacts + 5)).setToolTipText(skewTT);
  }

  @Override
  protected JPanel initActionPanel() {
    // TODO Auto-generated method stub
    actionPanel = new JPanel();
    comparisonB = ComponentTranslator.setButton(comparisonChart, actionPanel,
        this, false);
    setGroupsComboBox(false);

    actionPanel.add(groups);
    tabStatistics.addDivider(actionPanel);
    analyseB = ComponentTranslator.setButton(analyse, actionPanel, this, false);

    JLabel alphaL = new JLabel(alpha);
    alphaL.setToolTipText(alphaToolTip);
    alphaValues = new Double[] { 0.9, 0.95, 0.98, 0.99 };
    this.alphaCombo = new JComboBox(alphaValues);
    actionPanel.add(alphaL);
    actionPanel.add(alphaCombo);

    JLabel epsiL = new JLabel(epsilon);
    epsiL.setToolTipText(epsiToolTip);
    epsiValues = new Double[] { 0.1, 0.05, 0.02, 0.01 };
    this.epsiCombo = new JComboBox(epsiValues.clone());
    actionPanel.add(epsiL);
    actionPanel.add(epsiCombo);

    return actionPanel;
  }

  /**
   * Usage: updates the analysed values
   * 
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
      ((JLabel) centerP.getComponent(i * noOfFacts + place)).setText(vars
          .get(i).getStatsVarUIMap().get(StatisticVar.STDDEVIATION));
      place++;
      ((JLabel) centerP.getComponent(i * noOfFacts + place)).setText(vars
          .get(i).getStatsVarUIMap().get(StatisticVar.HALFWIDTH));
      place++;
      ((JLabel) centerP.getComponent(i * noOfFacts + place)).setText(vars
          .get(i).getStatsVarUIMap().get(StatisticVar.SKEWNESS));
      if (vars.get(i).getStatsVarUIMap().get(StatisticVar.RUNSSOLVED)
          .equalsIgnoreCase(
              ComponentTranslator.getResourceBundle().getString(solvedL))) {
        ((JLabel) centerP.getComponent(i * noOfFacts + place - 1))
            .setForeground(Color.GREEN);
        ((JLabel) centerP.getComponent(i * noOfFacts + place - 1))
            .setToolTipText(null);
      } else {
        ((JLabel) centerP.getComponent(i * noOfFacts + place - 1))
            .setForeground(Color.RED);
        ((JLabel) centerP.getComponent(i * noOfFacts + place - 1))
            .setToolTipText(vars.get(i).getStatsVarUIMap().get(
                StatisticVar.RUNSSOLVED)
                + " "
                + ComponentTranslator.getResourceBundle().getString(
                    runs2SolveMsg));
      }
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    // select all
    StatsChartPanel scp = ((StatisticsGui) svp.module.getGUIComponent())
        .getTabStatistics().getStatsChartPanel();

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
    // show selected chart
    else if (src == comparisonB) {
      int amount = nrOfSelectedBoxes(checkBoxList);
      if (amount == 0) {
        JOptionPane.showMessageDialog(this, ComponentTranslator
            .getResourceBundle().getString(information), ComponentTranslator
            .getResourceBundle().getString(infoOP),
            JOptionPane.OK_CANCEL_OPTION);

      } else {
        calculator.initComparisonDataset(amount);
        for (int i = 0; i < checkBoxList.size(); i++) {
          if (checkBoxList.get(i).isSelected()) {
            calculator.addToComparisonDataset(i);
          }
        }
        calculator.setComparisonDataset();
        DefaultIntervalCategoryDataset[] intervals = calculator
            .getComparisonDataset();
        scp.setMultiChartsComparison(intervals, calculator.getCatNames(),
            varsFromAllSim, checkBoxList, amount);
      }
    } else if (src == analyseB) {
      varsFromAllSim = svp.module.getStatisticVarsFromAllSimulations();
      scp.chartList.clear();
      calculator = new MultiSimAnalyser(varsFromAllSim);
      calculator.calculate((Double) (alphaCombo.getSelectedItem()),
          (Double) (epsiCombo.getSelectedItem()));
      updateAnalyseValues(varsFromAllSim.get(0));
      for (int i = 0; i < varsFromAllSim.get(0).size(); i++) {
        calculator.setVariableDataset(i);
      }
      List<DefaultIntervalCategoryDataset[]> intervals = calculator
          .getVariableDataset();
      scp.setMultiCharts(intervals, varsFromAllSim);
      scp.nrOfComparisons = 0;
      comparisonB.setEnabled(true);
      groups.setEnabled(true);
    }
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    // TODO Auto-generated method stub
    if (e.getStateChange() == ItemEvent.SELECTED) {
      if (groups.getSelectedItem().equals(groupKV)) {
        clearCheckboxes(checkBoxList);
        for (int i = 0; i < checkBoxList.size(); i++) {
          checkBoxList.get(i).setEnabled(true);
        }
        selAll.setEnabled(true);
      } else if (!(groups.getSelectedItem().equals(groupKV))) {
        clearCheckboxes(checkBoxList);
        varsFromAllSim = svp.module.getStatisticVarsFromAllSimulations();
        for (int i = 0; i < varsFromAllSim.get(0).size(); i++) {
          String varGroup = varsFromAllSim.get(0).get(i).getStatsVarUIMap()
              .get(StatisticVar.COMPARISONGROUP);
          if ((groups.getSelectedItem().toString()).equals(varGroup)) {
            checkBoxList.get(i).setSelected(true);
          }
          checkBoxList.get(i).setEnabled(false);
        }
        selAll.setEnabled(false);
      }
    }
  }
}
