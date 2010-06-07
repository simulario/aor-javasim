package aors.module.statistics;

import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYStepAreaRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import aors.module.statistics.gui.ChartCreator;
import aors.module.statistics.gui.ComponentTranslator;

/**
 * FrequencyDistributionChart
 * 
 * This class creates a frequency distribution chart
 * 
 * @author Daniel Draeger
 * @since 01.12.2009
 */

public class FrequencyDistributionChart {

  private String objectType;
  private String property;
  private String name;
  private ChartType chartType;
  private int minValue;
  private int maxValue;
  private int intervalSize;
  private Map<Integer, List<Number>> values;
  private boolean isSingleObject;

  private static final String HISTOGRAM_FORMAT = "{2}";
  // language strings
  private static final String percent = "percentL";
  private static final String frequencyDistributionChart = "frequencyDistributionChartL";

  /**
   * 
   * Create a new {@code FrequencyDistributionChart}.
   * 
   */
  public FrequencyDistributionChart() {
  }

  /**
   * ChartType inner Enum Class
   */
  public enum ChartType {
    BAR, PIE, PIE3D, AREA, LINE, STEP, STEP_AREA;
  }

  /**
   * Usage: set the object type
   * 
   * @param objectType
   */
  public void setObjectType(String objectType) {
    this.objectType = objectType;
  }

  /**
   * Usage: return the object type
   * 
   * @return String
   */
  public String getObjectType() {
    return objectType;
  }

  /**
   * Usage: set the name of the property
   * 
   * @param property
   */
  public void setProperty(String property) {
    this.property = property;
  }

  /**
   * Usage: return the name of the property
   * 
   * @return String
   */
  public String getProperty() {
    return property;
  }

  /**
   * Usage: add the value to the value list of the object with the given id
   * 
   * @param id
   *          object id
   * @param val
   *          value
   */
  public void addValue(Integer id, Number val) {
    this.values.get(id).add(val);
  }

  /**
   * Usage: initialize the hashmap with the value list of all objects
   * 
   * @param l
   *          value list
   */
  public void setValues(Map<Integer, List<Number>> l) {
    this.values = l;
  }

  /**
   * Usage: return the hashmap with the value list of all objects
   * 
   * @return Map
   */
  public Map<Integer, List<Number>> getValues() {
    return values;
  }

  /**
   * Usage: set the chart type
   * 
   * @param chartType
   */
  public void setChartType(String chartType) {
    for (int i = 0; i < ChartType.values().length; i++) {
      if (chartType.equalsIgnoreCase(ChartType.values()[i].name())) {
        this.chartType = ChartType.values()[i];
      }
    }
  }

  /**
   * Usage: return the chart type
   * 
   * @return ChartType
   */
  public ChartType getChartType() {
    return chartType;
  }

  /**
   * Usage: check if the interval boundaries are set
   * 
   * @return boolean
   */
  public boolean hasIntervalBoundaries() {
    if (this.getIntervalSize() != 0) {
      return true;
    }
    return false;
  }

  /**
   * Usage: creates the chart of the given values
   * 
   * @param values
   * @return JFreeChart
   */
  public JFreeChart createChart(Map<Integer, List<Number>> values) {
    HistogramDataset dataset = new HistogramDataset();
    dataset.setType(HistogramType.RELATIVE_FREQUENCY);

    DefaultPieDataset pieset = new DefaultPieDataset();
    String chartName = ComponentTranslator.getResourceBundle().getString(
        frequencyDistributionChart)
        + "(" + this.getProperty() + "/" + this.getObjectType() + ")";
    double[] setDouble;
    // PieDataset
    int piesetCount = 0;
    int setCount = 0;
    if (isSingleObject) {
      setDouble = new double[values.get(0).size()];
      for (int l = 0; l < values.get(0).size(); l++) {
        setDouble[setCount] = values.get(0).get(l).doubleValue();
        if (pieset.getKeys().contains(values.get(0).get(l).toString())) {
          pieset.setValue(values.get(0).get(l).toString(), pieset.getValue(
              values.get(0).get(l).toString()).intValue() + 1);
        } else {
          pieset.insertValue(piesetCount, values.get(0).get(l).toString(), 1);
          piesetCount++;
        }
        setCount++;
      }
    } else {
      setDouble = new double[values.keySet().size()];
      for (Integer key : values.keySet()) {
        int last = values.get(key).size() - 1;
        setDouble[setCount] = values.get(key).get(last).doubleValue();
        if (pieset.getKeys().contains(values.get(key).get(last).toString())) {
          pieset.setValue(values.get(key).get(last).toString(), pieset
              .getValue(values.get(key).get(last).toString()).intValue() + 1);
        } else {
          pieset.insertValue(piesetCount, values.get(key).get(last).toString(),
              1);
          piesetCount++;
        }
        setCount++;
      }
    }
    // HistogramDataset

    if (!hasIntervalBoundaries()) {
      dataset.addSeries(this.getProperty(), setDouble, values.size());
    } else {
      int bounds = (this.maxValue / this.intervalSize)
          - (this.minValue / this.intervalSize);
      dataset.addSeries(this.getProperty(), setDouble, bounds, this.minValue,
          this.maxValue);
    }
    JFreeChart chart;
    // PieChart
    if (this.chartType.equals(ChartType.PIE)) {
      chart = ChartFactory.createPieChart(chartName, pieset, true, true, false);
      chart.setBackgroundPaint(ChartCreator.DEFAULT_BACKGROUND_PAINT);
      PiePlot plot = (PiePlot) chart.getPlot();
      plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{2}"));
      plot.setBackgroundPaint(ChartCreator.DEFAULT_BACKGROUND_PAINT);

      // PieChart 3D
    } else if (this.chartType.equals(ChartType.PIE3D)) {
      chart = ChartFactory.createPieChart3D(chartName, pieset, true, true,
          false);
      PiePlot3D plot = (PiePlot3D) chart.getPlot();
      plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{2}"));

      // AreaChart
    } else if (this.chartType.equals(ChartType.AREA)) {
      chart = ChartFactory.createXYAreaChart(chartName, this.property, percent,
          dataset, PlotOrientation.VERTICAL, true, true, false);
      XYAreaRenderer renderer = (XYAreaRenderer) chart.getXYPlot()
          .getRenderer();
      renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
      chart.getXYPlot().setRenderer(renderer);

      // LineChart
    } else if (this.chartType.equals(ChartType.LINE)) {
      chart = ChartFactory.createXYLineChart(chartName, this.property,
          ComponentTranslator.getResourceBundle().getString(percent), dataset,
          PlotOrientation.VERTICAL, true, true, false);
      XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart
          .getXYPlot().getRenderer();
      renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
      chart.getXYPlot().setRenderer(renderer);

      // StepChart
    } else if (this.chartType.equals(ChartType.STEP)) {
      chart = ChartFactory.createXYStepChart(chartName, this.property,
          ComponentTranslator.getResourceBundle().getString(percent), dataset,
          PlotOrientation.VERTICAL, true, true, false);
      XYStepRenderer renderer = (XYStepRenderer) chart.getXYPlot()
          .getRenderer();
      renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
      chart.getXYPlot().setRenderer(renderer);

      // StepAreaChart
    } else if (this.chartType.equals(ChartType.STEP_AREA)) {
      chart = ChartFactory.createXYStepAreaChart(chartName, this.property,
          ComponentTranslator.getResourceBundle().getString(percent), dataset,
          PlotOrientation.VERTICAL, true, true, false);
      XYStepAreaRenderer renderer = (XYStepAreaRenderer) chart.getXYPlot()
          .getRenderer();
      renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
      chart.getXYPlot().setRenderer(renderer);

      // Histogram (barChart)
    } else {
      chart = ChartFactory.createHistogram(chartName, this.property,
          ComponentTranslator.getResourceBundle().getString(percent), dataset,
          PlotOrientation.VERTICAL, true, true, false);
      XYBarRenderer renderer = (XYBarRenderer) (((XYPlot) chart.getPlot())
          .getRenderer());
      renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator(
          HISTOGRAM_FORMAT, NumberFormat.getInstance(), NumberFormat
              .getInstance()));
      renderer.setMargin(0.1);
    }
    chart.setBackgroundPaint(ChartCreator.DEFAULT_BACKGROUND_PAINT);
    return chart;
  }

  /**
   * Usage: set the minimum value
   * 
   * @param minValue
   */
  public void setMinValue(int minValue) {
    this.minValue = minValue;
  }

  /**
   * Usage: get the minimum value
   * 
   * @return int
   */
  public int getMinValue() {
    return minValue;
  }

  /**
   * Usage: set the maximum value
   * 
   * @param maxValue
   */
  public void setMaxValue(int maxValue) {
    this.maxValue = maxValue;
  }

  /**
   * Usage: get the maximum value
   * 
   * @return int
   */
  public int getMaxValue() {
    return maxValue;
  }

  /**
   * Usage: set the interval size
   * 
   * @param intervalSize
   */
  public void setIntervalSize(int intervalSize) {
    this.intervalSize = intervalSize;
  }

  /**
   * Usage: get the interval size
   * 
   * @return int
   */
  public int getIntervalSize() {
    return intervalSize;
  }

  /**
   * Usage: set the name of the statistics variable the chart belongs to
   * 
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Usage: get the name of the statistics variable
   * 
   * @return String
   */
  public String getName() {
    return name;
  }

  /**
   * Usage: note if the underlying object type has one or more instances
   * 
   * @param isSingleObject
   */
  public void setSingleObject(boolean isSingleObject) {
    this.isSingleObject = isSingleObject;
  }

  /**
   * Usage: return if the underlying object type has one or more instances
   * 
   * @return boolean
   */
  public boolean isSingleObject() {
    return isSingleObject;
  }

}
