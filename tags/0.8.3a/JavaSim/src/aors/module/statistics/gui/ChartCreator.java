package aors.module.statistics.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.NumberFormat;

import javax.swing.UIManager;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGSyntax;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.IntervalCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.IntervalBarRenderer;
import org.jfree.data.category.DefaultIntervalCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import aors.module.statistics.MultiSimAnalyser;
import aors.module.statistics.StatisticVar;

/**
 * ChartCreator
 * 
 * This class creates the charts for the simulation output
 * 
 * @author Daniel Draeger
 * @since 01.12.2009
 */
public class ChartCreator {

  // background color of LookAndFeel
  public static final Paint DEFAULT_BACKGROUND_PAINT = UIManager
      .getColor("Panel.background");

  private static final Color AVG_COLOR = Color.RED;
  private static final Color MIN_COLOR = Color.GRAY;
  private static final Color MAX_COLOR = Color.LIGHT_GRAY;
  private static final Color CIL_COLOR = new Color(204, 255, 102);
  private static final Color CIU_COLOR = Color.PINK;
  private static final Color RANGE_COLOR = new Color(102, 153, 255);
  private static final String MAX_ONLY_FORMAT = "({0},{1}) = {4}";
  private static final String MIN_ONLY_FORMAT = "({0},{1}) = {3}";

  // screen dimension
  private static final Dimension screenDimension = Toolkit.getDefaultToolkit()
      .getScreenSize().getSize();

  private XYSeries xySeries = null;

  private final ChartPanel pan;

  // language keys
  private String val = "";
  private final String step = ComponentTranslator.getResourceBundle()
      .getString("stepL");
  private final String created = ComponentTranslator.getResourceBundle()
      .getString("SVGCreationSuccessMsg");
  private final String valueL = ComponentTranslator.getResourceBundle()
      .getString("valueL");
  private final String runsL = ComponentTranslator.getResourceBundle()
      .getString("runsL");

  /**
   * Create a new {@code ChartCreator}.
   * 
   */
  public ChartCreator() {
    pan = new ChartPanel(null);
  }

  public ChartCreator(JFreeChart c) {
    pan = new ChartPanel(c);
  }

  /**
   * 
   * Create a new {@code ChartCreator}.
   * 
   * @param var
   *          statistic variable
   * @param d
   *          dimension
   */
  public ChartCreator(StatisticVar var, Dimension d) {
    JFreeChart chart = null;
    xySeries = new XYSeries(var.getStatsVarUIMap()
        .get(StatisticVar.DISPLAYNAME));
    if (var.getStatsVarUIMap().get(StatisticVar.FORMAT) != null) {
      val = var.getStatsVarUIMap().get(StatisticVar.FORMAT);
    } else {
      val = valueL;
    }
    final XYSeriesCollection xySeriesColl = new XYSeriesCollection(
        this.xySeries);
    chart = xySeriesChart(var.getStatsVarUIMap().get(StatisticVar.DISPLAYNAME),
        xySeriesColl);
    pan = new ChartPanel(chart);
    pan.setPreferredSize(d);
    pan.validate();
  }

  /**
   * Create a new {@code ChartCreator}.
   * 
   * @param name
   *          name of the chart
   * @param dataset
   */
  public ChartCreator(String name, final XYDataset dataset) {
    JFreeChart chart = xySeriesChart(name, dataset);
    pan = new ChartPanel(chart);
    pan.validate();
  }

  /**
   * Usage: creates a lineChart for single simulation comparisonGroup
   * 
   * @param name
   * @param tDataset
   * @return chart
   */
  public JFreeChart xySeriesChart(String name, final XYDataset dataset) {
    final JFreeChart chart = ChartFactory.createXYLineChart(name, step, val,
        dataset, PlotOrientation.VERTICAL, true, true, false);
    chart.setBackgroundPaint(DEFAULT_BACKGROUND_PAINT);
    chart.getPlot().setBackgroundPaint(DEFAULT_BACKGROUND_PAINT);
    chart.getXYPlot().setDomainGridlinePaint(Color.DARK_GRAY);
    chart.getXYPlot().setRangeGridlinePaint(Color.DARK_GRAY);
    return chart;
  }

  /**
   * 
   * Create a new {@code ChartCreator}. Used for multiple simulation
   * 
   * @param chartName
   * @param runs
   * @param dataset
   */
  public ChartCreator(String chartName, String[] catName, String runs,
      DefaultIntervalCategoryDataset[] dataset) {

    JFreeChart chart = ChartFactory.createBarChart(chartName, runs + runsL,
        valueL, dataset[0], PlotOrientation.HORIZONTAL, true, true, false);

    CategoryPlot plot = chart.getCategoryPlot();
    plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
    for (int j = 0; j < dataset.length; j++) {
      dataset[j].setCategoryKeys(catName);
      final IntervalBarRenderer renderer = new IntervalBarRenderer();
      renderer.setMaximumBarWidth(0.2);
      renderer.setShadowVisible(false);
      renderer.setBaseToolTipGenerator(new IntervalCategoryToolTipGenerator());
      if (j == MultiSimAnalyser.AVG) {
        renderer.setSeriesPaint(0, AVG_COLOR);
        renderer.setBaseToolTipGenerator(new IntervalCategoryToolTipGenerator(
            MAX_ONLY_FORMAT, NumberFormat.getInstance()));
      }
      if (j == MultiSimAnalyser.MIN) {
        renderer.setSeriesPaint(0, MIN_COLOR);
        if (dataset[j].getStartValue(0, 0).doubleValue() < 0) {
          renderer
              .setBaseToolTipGenerator(new IntervalCategoryToolTipGenerator(
                  MIN_ONLY_FORMAT, NumberFormat.getInstance()));
        } else {
          renderer
              .setBaseToolTipGenerator(new IntervalCategoryToolTipGenerator(
                  MAX_ONLY_FORMAT, NumberFormat.getInstance()));
        }
      }
      if (j == MultiSimAnalyser.MAX) {
        renderer.setSeriesPaint(0, MAX_COLOR);
        renderer.setBaseToolTipGenerator(new IntervalCategoryToolTipGenerator(
            MAX_ONLY_FORMAT, NumberFormat.getInstance()));
      }
      if (j == MultiSimAnalyser.CIL) {
        renderer.setSeriesPaint(0, CIL_COLOR);
        renderer
            .setBaseToolTipGenerator(new IntervalCategoryToolTipGenerator());
      }
      if (j == MultiSimAnalyser.CIU) {
        renderer.setSeriesPaint(0, CIU_COLOR);
        renderer
            .setBaseToolTipGenerator(new IntervalCategoryToolTipGenerator());
      }
      if (j == MultiSimAnalyser.RANGE) {
        renderer.setShadowVisible(true);
        renderer.setMaximumBarWidth(0.3);
        renderer.setSeriesPaint(0, RANGE_COLOR);
        renderer
            .setBaseToolTipGenerator(new IntervalCategoryToolTipGenerator());
      }
      plot.setDataset(j, dataset[j]);
      plot.setRenderer(j, renderer);
    }
    pan = new ChartPanel(chart);
    pan.validate();
  }

  /**
   * Usage: return the XYSeries
   * 
   * @return XYSeries
   */
  public XYSeries getXYSeries() {
    return xySeries;
  }

  /**
   * Usage: return the ChartPanel
   * 
   * @return ChartPanel
   */
  public ChartPanel getChartPanel() {
    return pan;
  }

  /**
   * Usage: return the chart
   * 
   * @return JFreeChart
   */
  public JFreeChart getChart() {
    return pan.getChart();
  }

  /**
   * Usage: converts the chart into a SVG-File
   * 
   * @param c
   *          chart
   * @param fileName
   * @throws IOException
   */
  public void convertToSVG(JFreeChart c, int id, String fileName)
      throws IOException {
    File svgFile = new File(fileName);
    // Get a DOMImplementation and create an XML document
    DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
    Document document = domImpl.createDocument(null, SVGSyntax.SVG_SVG_TAG,
        null);
    SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
    String viewBox = "0 0 " + screenDimension.getWidth() * 0.8 + " "
        + screenDimension.getHeight() * 0.6;
    Rectangle bounds = new Rectangle(new Dimension(new Double(screenDimension
        .getWidth() * 0.8).intValue(), new Double(
        screenDimension.getHeight() * 0.6).intValue()));
    c.draw(svgGenerator, bounds, null);
    Element root = svgGenerator.getRoot();
    root.setAttribute(SVGSyntax.SVG_VIEW_BOX_ATTRIBUTE, viewBox);
    root.setAttribute(SVGSyntax.SVG_ID_ATTRIBUTE, id + "");
    // Write svg file
    OutputStream outputStream = new FileOutputStream(svgFile);
    Writer out = new OutputStreamWriter(outputStream, "UTF-8");
    svgGenerator.stream(root, out, false /* use css */, false);
    outputStream.flush();
    outputStream.close();
    System.out.println(fileName + "\t-" + created);
  }

}
