package aors.module.statistics.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultIntervalCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import aors.module.statistics.FrequencyDistributionChart;
import aors.module.statistics.StatisticVar;
import aors.module.statistics.StatisticsCore;

/**
 * StatsChartPanel
 * 
 * The StatsChartPanel is the bottom panel of tabStatistics and shows the
 * available charts
 * 
 * @author Daniel Draeger
 * @since 02.11.2009
 */
public class StatsChartPanel extends JScrollPane implements ActionListener {

  private static final long serialVersionUID = 4069051431028349305L;

  // background color of LookAndFeel
  public static final Paint DEFAULT_BACKGROUND_PAINT = UIManager
      .getColor("Panel.background");

  private StatisticsCore module;
  private List<StatisticVar> vars;
  private Integer[] varIDs;
  private boolean idsCreated = false;
  private String imagePath;
  private JPanel panel, chartBig, chartOverview;
  protected List<JFreeChart> chartList;
  private List<XYSeries> timeSeriesList;
  protected List<JButton> buttons;
  private Dimension preferredChartSize = null;
  protected int nrOfComparisons = 0;
  private int nrOfAllComparisons = 0;
  private String compString = null;

  private GridBagLayout gbl = new GridBagLayout();
  private GridBagConstraints gbc = new GridBagConstraints();

  // language keys
  private final String groupL = "groupL";
  private final String comparisonL = "comparisonL";
  private final String svgCreationError = "SVGCreationError";

  /**
   * Create a new {@code StatsChartPanel}.
   * 
   * @param path
   */
  public StatsChartPanel(StatisticsCore module) {
    this
        .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    this
        .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    setImagePath(module.getProjectPath());

    this.module = module;
    buttons = new ArrayList<JButton>();
    chartList = new ArrayList<JFreeChart>();
    panel = new JPanel(new BorderLayout());
    chartBig = new JPanel();
    chartOverview = new JPanel(gbl);
    gbc.insets = new Insets(5, 0, 0, 5);
    gbc.anchor = GridBagConstraints.LINE_START;
    panel.add(BorderLayout.WEST, chartOverview);
    panel.add(BorderLayout.CENTER, chartBig);
    this.setViewportView(panel);
  }

  /**
   * Usage: set the image path
   * 
   * @param path
   */
  public void setImagePath(String path) {
    this.imagePath = path;
    // deleteImageFolder();
  }

  /**
   * Usage: return the image path
   * 
   * @return
   */
  public String getImagePath() {
    return imagePath;
  }

  /**
   * Usage: return chart overview
   * 
   * @return JPanel
   */
  public JPanel getChartOverview() {
    return chartOverview;
  }

  /**
   * Usage: updates the charts for single simulation
   * 
   * @param vars
   */
  public void updateCharts(List<StatisticVar> vars) {
    for (int i = 0; i < vars.size(); i++) {
      if (timeSeriesList.get(i) != null) {
        timeSeriesList.get(i).add(vars.get(i).getCurrentStep(),
            vars.get(i).getLastValue());
      }
    }
  }

  /**
   * Usage: delete the content of each shown time series
   */
  public void clearCharts() {
    if (timeSeriesList != null) {
      for (int i = 0; i < timeSeriesList.size(); i++) {
        if (timeSeriesList.get(i) != null) {
          timeSeriesList.get(i).clear();
        }
      }
    }
  }

  private void saveSingleChart(int i) {
    JFreeChart chart = chartList.get(i);
    int id = chart.hashCode();
    ChartCreator chc = new ChartCreator(chart);
    if (compString != null) {
      List<Integer> varIds = new ArrayList<Integer>();
      for (int j = 0; j < vars.size(); j++) {
        if (compString.equalsIgnoreCase(vars.get(j).getStatsVarUIMap().get(
            StatisticVar.COMPARISONGROUP))) {
          int vid = vars.get(j).hashCode();
          varIds.add(vid);
          module.getHtmlSvgDataConnector().put(vid,
              vars.get(j).getAnalyseValuesForHtmlOutput(true));
        }
      }
      module.getCompData().put(id, varIds.toArray(new Integer[varIds.size()]));
    } else {
      for (int j = 0; j < vars.size(); j++) {
        if (chartList.get(i).getTitle().getText().equalsIgnoreCase(
            vars.get(j).getStatsVarUIMap().get(StatisticVar.DISPLAYNAME))) {
          module.getHtmlSvgDataConnector().put(id,
              vars.get(j).getAnalyseValuesForHtmlOutput(true));
        }
      }
    }
    try {
      chc.convertToSVG(chart, id, imagePath + File.separator
          + chart.getTitle().getText() + "SSim.svg");
    } catch (IOException ex) {
      System.out.println(ComponentTranslator.getResourceBundle().getString(
          svgCreationError));
    }
  }

  /**
   * Usage: save all shown charts in single simulation
   * 
   */
  public void saveSingleSimulationCharts() {
    if (chartList.size() == 1) {
      saveSingleChart(0);
    } else {
      for (int i = 0; i < timeSeriesList.size(); i++) {
        if (timeSeriesList.get(i) != null) {
          saveSingleChart(i);
        }
      }
    }
  }

  /**
   * Usage: This function controls that max. 3 charts exist abreast
   * 
   * @param list
   * @param pan
   * @return
   */
  private void calcPreferredChartSize(List<JCheckBox> list, JPanel pan) {
    Dimension d = new Dimension();
    int amount = 0;
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).isSelected()) {
        amount++;
      }
    }
    GridLayout grid = new GridLayout();
    if (amount % 3 == 0) {
      if (preferredChartSize == null) {
        d.width = new Double(this.getSize().getWidth() / 3 - 15).intValue();
      } else {
        d.width = new Double(preferredChartSize.getWidth() / 3 - 15).intValue();
      }
      grid.setColumns(3);
      grid.setRows(amount / 3);

    } else if (amount % 2 == 0) {
      if (preferredChartSize == null) {
        d.width = new Double(this.getSize().getWidth() / 2 - 20).intValue();
      } else {
        d.width = new Double(preferredChartSize.getWidth() / 2 - 20).intValue();
      }
      grid.setColumns(2);
      grid.setRows(amount / 2);
    } else {
      grid.setColumns(3);
      grid.setRows((new Double(amount / 3)).intValue() + 1);
    }
    if (preferredChartSize == null) {
      d.height = new Double(this.getSize().getHeight() - 35).intValue();
      preferredChartSize = d;
    } else {
      d.height = new Double(preferredChartSize.getHeight() - 35).intValue();
    }
    pan.setLayout(grid);
  }

  /**
   * Usage: initialize the charts for single simulation
   * 
   * @param vars
   * @param chbList
   */
  public void setSingleCharts(List<StatisticVar> vars, List<JCheckBox> chbList) {
    this.vars = vars;
    chartBig.removeAll();
    chartList.clear();
    timeSeriesList = new ArrayList<XYSeries>();
    this.compString = null;
    calcPreferredChartSize(chbList, chartBig);
    for (int i = 0; i < vars.size(); i++) {
      if (chbList.get(i).isSelected()) {
        ChartCreator chc = new ChartCreator(vars.get(i), preferredChartSize);
        chartList.add(chc.getChart());
        timeSeriesList.add(chc.getXYSeries());
        chartBig.add(chc.getChartPanel());
      } else {
        chartList.add(null);
        timeSeriesList.add(null);
      }
    }
    this.validate();
  }

  /**
   * Usage: creates a timeseries chart for comparison (groups)
   * 
   * @param vars
   * @param group
   */
  public void setSingleSimComparison(List<StatisticVar> vars, String group) {
    this.vars = vars;
    chartBig.removeAll();
    chartOverview.removeAll();
    chartList.clear();
    timeSeriesList = new ArrayList<XYSeries>();
    this.compString = group;
    XYSeriesCollection xyColl = new XYSeriesCollection();
    for (int i = 0; i < vars.size(); i++) {
      String comGroup = vars.get(i).getStatsVarUIMap().get(
          StatisticVar.COMPARISONGROUP);
      if (group.equals(comGroup)) {
        timeSeriesList.add(i, new XYSeries(vars.get(i).getStatsVarUIMap().get(
            StatisticVar.DISPLAYNAME)));
        xyColl.addSeries(timeSeriesList.get(i));
      } else {
        timeSeriesList.add(i, null);
      }
    }
    String chartName = ComponentTranslator.getResourceBundle()
        .getString(groupL)
        + "(" + group + ")";
    ChartCreator chc = new ChartCreator(chartName, xyColl);
    JFreeChart chart = chc.getChart();
    ChartPanel pan = chc.getChartPanel();
    pan.setPreferredSize(new Dimension(new Double(
        this.getSize().getWidth() * 0.9).intValue(), new Double(this.getSize()
        .getHeight() * 0.9).intValue()));
    chartList.add(chart);
    chartBig.add(pan);
    validate();
  }

  /**
   * Usage: add time series value for each shown variable
   * 
   * @param vars
   */
  public void updateSingleSimComparison(List<StatisticVar> vars) {
    for (int i = 0; i < vars.size(); i++) {
      if (timeSeriesList.get(i) != null) {
        timeSeriesList.get(i).add(vars.get(i).getCurrentStep(),
            vars.get(i).getLastValue());
      }
    }
  }

  /**
   * Usage: set GridBagConstraints for GridBagLayout
   * 
   * @param x
   * @param y
   * @param comp
   */
  private void setGridBagConstraints(int x, int y, JComponent comp) {
    gbc.gridx = x;
    gbc.gridy = y;
    gbl.setConstraints(comp, gbc);
  }

  /**
   * Usage: set chart for comparision in multi simulation
   * 
   * @param interval
   * @param catNames
   * @param varsFromAllSim
   * @param chbList
   * @param amount
   */
  public void setMultiChartsComparison(
      DefaultIntervalCategoryDataset[] interval, String[] catNames,
      List<List<StatisticVar>> varsFromAllSim, List<JCheckBox> chbList,
      int amount) {
    if (nrOfComparisons == 0) {
      chartOverview.removeAll();
      chartList.clear();
      buttons.clear();
    }
    String name = ComponentTranslator.getResourceBundle()
        .getString(comparisonL);
    String runs = varsFromAllSim.size() + " ";
    ChartCreator chc = new ChartCreator(name, catNames, runs, interval);
    JFreeChart chart = chc.getChart();
    chart.setBackgroundPaint(ChartCreator.DEFAULT_BACKGROUND_PAINT);
    chartList.add(chart);
    JButton b = new JButton(">>");
    JLabel lab = new JLabel();
    lab.setText(name + nrOfComparisons);
    setGridBagConstraints(1, nrOfComparisons, b);
    setGridBagConstraints(0, nrOfComparisons, lab);
    chartOverview.add(lab);
    chartOverview.add(b);
    buttons.add(b);
    b.addActionListener(this);
    int id = chc.hashCode();
    setChartBig(chartList.size() - 1);
    comparisonData(id, varsFromAllSim, chbList, amount, module
        .getHtmlSvgDataConnector());
    try {
      chc.convertToSVG(chart, id, imagePath + File.separator + groupL
          + nrOfAllComparisons + "MCom.svg");
    } catch (IOException ex) {
      System.out.println(ComponentTranslator.getResourceBundle().getString(
          svgCreationError));
    }
    nrOfComparisons++;
    nrOfAllComparisons++;
    this.validate();
  }

  /**
   * Usage: collect the comparision data
   * 
   * @param id
   * @param varsFromAllSim
   * @param cb
   * @param amount
   * @param map
   */
  private void comparisonData(int id, List<List<StatisticVar>> varsFromAllSim,
      List<JCheckBox> cb, int amount, Map<Integer, String[]> map) {
    Integer[] array = new Integer[amount];
    int count = 0;
    for (int i = 0; i < cb.size(); i++) {
      if (cb.get(i).isSelected()) {
        String name = varsFromAllSim.get(0).get(i).getStatsVarUIMap().get(
            StatisticVar.DISPLAYNAME);
        Set<Map.Entry<Integer, String[]>> entrySet = map.entrySet();
        Iterator<Map.Entry<Integer, String[]>> it = entrySet.iterator();

        while (it.hasNext()) {
          Map.Entry<Integer, String[]> entry = it.next();
          if (name.equalsIgnoreCase((entry.getValue())[0])) {
            if (entry.getValue()[entry.getValue().length - 1]
                .equalsIgnoreCase("false")) {
              array[count] = entry.getKey();
              count++;
            }
          }
        }
      }
    }
    module.getCompData().put(id, array);
  }

  /**
   * Usage: generate ID for the chart of each statistic variable
   * 
   * @param vars
   */
  private void generateVarChartIDs(List<StatisticVar> vars) {
    this.varIDs = new Integer[vars.size()];
    for (int i = 0; i < vars.size(); i++) {
      varIDs[i] = vars.get(i).hashCode();
    }
  }

  /**
   * Usage: set the multi simulation chart for each statistic variable
   * 
   * @param intervals
   * @param varsFromAllSim
   */
  public void setMultiCharts(List<DefaultIntervalCategoryDataset[]> intervals,
      List<List<StatisticVar>> varsFromAllSim) {
    chartOverview.removeAll();
    chartList.clear();
    buttons.clear();
    if (!idsCreated) {
      generateVarChartIDs(varsFromAllSim.get(0));
    }
    for (int i = 0; i < intervals.size(); i++) {
      String chartName = varsFromAllSim.get(0).get(i).getStatsVarUIMap().get(
          StatisticVar.DISPLAYNAME);
      String[] catName = { chartName };
      String runs = varsFromAllSim.size() + " ";
      ChartCreator chc = new ChartCreator(chartName, catName, runs, intervals
          .get(i));
      JFreeChart chart = chc.getChart();
      chart.setBackgroundPaint(ChartCreator.DEFAULT_BACKGROUND_PAINT);
      chartList.add(chart);
      JButton b = new JButton(">>");
      JLabel lab = new JLabel();
      lab.setText(varsFromAllSim.get(0).get(i).getStatsVarUIMap().get(
          StatisticVar.DISPLAYNAME));
      setGridBagConstraints(1, i, b);
      setGridBagConstraints(0, i, lab);
      chartOverview.add(lab);
      chartOverview.add(b);
      buttons.add(b);
      b.addActionListener(this);
      setChartBig(chartList.size() - 1);
      module.getHtmlSvgDataConnector().put(varIDs[i],
          varsFromAllSim.get(0).get(i).getAnalyseValuesForHtmlOutput(false));
      try {
        chc.convertToSVG(chart, varIDs[i], imagePath + File.separator
            + chartName + "MSim.svg");
      } catch (IOException ex) {
        System.out.println(ComponentTranslator.getResourceBundle().getString(
            svgCreationError));
      }
    }
    validate();
  }

  /**
   * Usage: set the frequency distribution charts
   * 
   * @param charts
   */
  public void setFrequencyDistributionCharts(
      List<FrequencyDistributionChart> charts) {
    chartOverview.removeAll();
    chartList.clear();
    buttons.clear();
    for (int i = 0; i < charts.size(); i++) {
      // if(vars.get(i).getFrequencyDistributionChart()!=null){
      JFreeChart chart = charts.get(i).createChart(charts.get(i).getValues());
      ChartCreator chc = new ChartCreator();
      chartList.add(chart);
      JButton b = new JButton(">>");
      JLabel lab = new JLabel();
      lab.setText(charts.get(i).getName());
      setGridBagConstraints(1, i, b);
      setGridBagConstraints(0, i, lab);
      chartOverview.add(lab);
      chartOverview.add(b);
      buttons.add(b);
      b.addActionListener(this);
      int id = chc.hashCode();
      setChartBig(chartList.size() - 1);
      try {
        chc.convertToSVG(chart, id, imagePath + File.separator
            + charts.get(i).getName() + "Objp.svg");
      } catch (IOException ex) {
        System.out.println(ComponentTranslator.getResourceBundle().getString(
            svgCreationError));
      }
      // }
    }
    validate();
  }

  /**
   * Usage: delete all SVG-Files from the image folder
   */
  public void deleteImageFolder() {
    File imageFolder = new File(imagePath);
    if (imageFolder.listFiles() != null) {
      for (int m = 0; m < imageFolder.listFiles().length; m++) {
        try {
          File f = new File(imageFolder.listFiles()[m].getCanonicalPath());
          if ((f.exists())
              && ((f.getName().endsWith(".svg")) || (f.getName()
                  .equals("Statistics.html")))) {
            f.delete();
            m = m - 1;
          }
        } catch (IOException ioe) {
          System.out.println(ioe.getMessage());
        }
      }
    }
  }

  /**
   * Usage: return the panel which includes the ChartPanel
   * 
   * @return JPanel
   */
  public JPanel getChartBig() {
    return chartBig;
  }

  /**
   * Usage: load chart i from chartList as visible chart
   * 
   * @param i
   */
  public void setChartBig(int i) {
    chartBig.removeAll();
    if (i == -1) {
      return;
    }
    chartList.get(i).setBorderVisible(true);
    ChartPanel chartP = new ChartPanel(null);
    int width = new Double(chartBig.getSize().width * 0.9).intValue();
    int height = new Double(chartBig.getSize().height * 0.9).intValue();
    chartP.setPreferredSize(new Dimension(width, height));
    chartP.setChart(chartList.get(i));
    chartP.validate();
    chartBig.add(chartP, 0);
    this.validate();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    for (int i = 0; i < buttons.size(); i++) {
      if (src == buttons.get(i)) {
        this.setChartBig(i);
        this.validate();
      }
    }
  }
}
