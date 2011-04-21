package aors.module.statistics;

import hep.aida.bin.DynamicBin1D;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.jfree.data.category.DefaultIntervalCategoryDataset;

import aors.module.statistics.gui.ComponentTranslator;
import cern.jet.stat.Probability;

/**
 * MultiSimAnalyser
 * 
 * This class analysis the output data of the simulation. It calculates the
 * confidence intervals and all aggregated values and creates the datasets for
 * the multisimulation charts
 * 
 * @author Daniel Draeger
 * @since 01.12.2010
 */
public class MultiSimAnalyser {

  private final int percentOfElementsToCutOf = 10;
  private String[] catName;
  private double[] minA, maxA, avgL, avgU, lowA, uppA, nulA, minTrim, maxTrim,
      minB, maxB, avgLB, avgUB, lowB, uppB, nulB, miTr, maTr;
  private int currentItem;
  private final List<List<StatisticVar>> sims;
  private DefaultIntervalCategoryDataset[] comparisonDataset;
  private List<DefaultIntervalCategoryDataset[]> singleSets;

  public static final int RANGE = 0;
  public static final int MAX = 1;
  public static final int MIN = 2;
  public static final int CIL = 3;
  public static final int AVG = 4;
  public static final int CIU = 5;

  // Language Strings
  private final String solvedL = ComponentTranslator.getResourceBundle()
      .getString("solvedL");
  private final String upperBound = ComponentTranslator.getResourceBundle()
      .getString("upperBoundL");
  private final String lowerBound = ComponentTranslator.getResourceBundle()
      .getString("lowerBoundL");
  private final String maximum = ComponentTranslator.getResourceBundle()
      .getString("maximumL");
  private final String minimum = ComponentTranslator.getResourceBundle()
      .getString("minimumL");
  private final String average = ComponentTranslator.getResourceBundle()
      .getString("averageL");
  private final String allValues = ComponentTranslator.getResourceBundle()
      .getString("allValuesL");

  private final String percentOfValues = (100 - percentOfElementsToCutOf)
      + "% " + allValues;

  /**
   * 
   * Create a new {@code MultiSimAnalyser}.
   * 
   * @param sims
   */
  public MultiSimAnalyser(List<List<StatisticVar>> sims) {
    this.sims = sims;
    this.minA = new double[sims.get(0).size()];
    this.maxA = new double[sims.get(0).size()];
    this.avgL = new double[sims.get(0).size()];
    this.avgU = new double[sims.get(0).size()];
    this.lowA = new double[sims.get(0).size()];
    this.uppA = new double[sims.get(0).size()];
    this.nulA = new double[sims.get(0).size()];
    this.minTrim = new double[sims.get(0).size()];
    this.maxTrim = new double[sims.get(0).size()];
  }

  /**
   * Usage: calculates all facts of interest
   * 
   * @param alpha
   * @param epsilon
   */
  public void calculate(double alpha, double epsilon) {
    DecimalFormat df = new DecimalFormat("###0.00");
    DynamicBin1D[] X = new DynamicBin1D[sims.get(0).size()];
    singleSets = new ArrayList<DefaultIntervalCategoryDataset[]>();

    for (int i = 0; i < sims.get(0).size(); i++) {
      DynamicBin1D temp = new DynamicBin1D();
      for (int j = 0; j < sims.size(); j++) {
        temp.add(sims.get(j).get(i).getLastValue().doubleValue());
        X[i] = temp;
      }
    }
    double tQuantile = Probability.studentTInverse((1 - alpha), sims.size());
    for (int k = 0; k < X.length; k++) {
      double mean = X[k].mean();
      double stdDev = X[k].standardDeviation();
      double min = X[k].min();
      double max = X[k].max();
      double halfWidth = tQuantile * stdDev / Math.sqrt(mean);
      double lowB = mean - halfWidth;
      double uppB = mean + halfWidth;
      double eps = mean * epsilon;
      double skew = X[k].skew();

      int minRuns = new Double(Math.ceil(Math.pow(tQuantile * stdDev / eps, 2)))
          .intValue();
      if (minRuns <= sims.size()) {
        sims.get(0).get(k).getStatsVarUIMap().put(StatisticVar.RUNSSOLVED,
            solvedL);
      } else {
        sims.get(0).get(k).getStatsVarUIMap().put(StatisticVar.RUNSSOLVED,
            minRuns + "");
      }
      sims.get(0).get(k).getStatsVarUIMap().put(StatisticVar.MIN,
          df.format(min));
      sims.get(0).get(k).getStatsVarUIMap().put(StatisticVar.MAX,
          df.format(max));
      sims.get(0).get(k).getStatsVarUIMap().put(StatisticVar.AVG,
          df.format(X[k].mean()));
      sims.get(0).get(k).getStatsVarUIMap().put(
          StatisticVar.CONFIDENCELOWBOUND, df.format(lowB));
      sims.get(0).get(k).getStatsVarUIMap().put(StatisticVar.CONFIDENCEUPBOUND,
          df.format(uppB));
      sims.get(0).get(k).getStatsVarUIMap().put(StatisticVar.STDDEVIATION,
          df.format(stdDev));
      sims.get(0).get(k).getStatsVarUIMap().put(StatisticVar.HALFWIDTH,
          df.format(halfWidth));
      sims.get(0).get(k).getStatsVarUIMap().put(StatisticVar.SKEWNESS,
          df.format(skew));

      minA[k] = min;
      maxA[k] = max;
      avgL[k] = mean - (mean * 0.01);
      avgU[k] = mean;
      lowA[k] = lowB;
      uppA[k] = uppB;
      nulA[k] = 0.0;

      double percentage = (double) (percentOfElementsToCutOf) / 100;
      int halfAmountToTrim = (int) Math.round(sims.size() * percentage / 2);
      X[k].trim(halfAmountToTrim, halfAmountToTrim);
      minTrim[k] = X[k].min();
      maxTrim[k] = X[k].max();
    }
  }

  /**
   * Usage: creates dataset for single variable
   * 
   * @param k
   */
  public void setVariableDataset(int k) {
    DefaultIntervalCategoryDataset[] datasets = new DefaultIntervalCategoryDataset[6];
    final double[] minB = { minA[k] };
    final double[] maxB = { maxA[k] };
    final double[] avgLB = { avgL[k] };
    final double[] avgUB = { avgU[k] };
    final double[] lowB = { lowA[k] };
    final double[] uppB = { uppA[k] };
    final double[] maTr = { maxTrim[k] };
    final double[] miTr = { minTrim[k] };
    final double[] nulB = { nulA[k] };

    if (minA[k] < 0.0) {
      datasets[1] = setCategoryDataset(minB, maxB, maximum);
      datasets[2] = setCategoryDataset(minB, nulB, minimum);
    } else {
      datasets[1] = setCategoryDataset(nulB, maxB, maximum);
      datasets[2] = setCategoryDataset(nulB, minB, minimum);
    }
    datasets[0] = setCategoryDataset(miTr, maTr, percentOfValues);
    datasets[3] = setCategoryDataset(lowB, avgUB, lowerBound);
    datasets[4] = setCategoryDataset(avgLB, avgUB, average);
    datasets[5] = setCategoryDataset(avgUB, uppB, upperBound);

    singleSets.add(datasets);
  }

  /**
   * Usage: return datasets for all set single variables
   * 
   * @return List<DefaultIntervalCategoryDataset[]>
   */
  public List<DefaultIntervalCategoryDataset[]> getVariableDataset() {
    return singleSets;
  }

  /**
   * Usage: initialize comparison dataset
   * 
   * @param length
   */
  public void initComparisonDataset(int length) {
    catName = new String[length];
    minB = new double[length];
    maxB = new double[length];
    avgLB = new double[length];
    avgUB = new double[length];
    lowB = new double[length];
    uppB = new double[length];
    maTr = new double[length];
    miTr = new double[length];
    nulB = new double[length];
    currentItem = 0;
  }

  /**
   * Usage: fill comparison dataset
   * 
   * @param i
   *          - index of single variable
   */
  public void addToComparisonDataset(int i) {
    minB[currentItem] = minA[i];
    maxB[currentItem] = maxA[i];
    avgLB[currentItem] = avgL[i];
    avgUB[currentItem] = avgU[i];
    lowB[currentItem] = lowA[i];
    uppB[currentItem] = uppA[i];
    maTr[currentItem] = maxTrim[i];
    miTr[currentItem] = minTrim[i];
    nulB[currentItem] = 0.0;

    catName[currentItem] = sims.get(0).get(i).getStatsVarUIMap().get(
        StatisticVar.DISPLAYNAME);
    currentItem++;
  }

  /**
   * Usage: return all set names for categorization
   * 
   * @return String[]
   */
  public String[] getCatNames() {
    return catName;
  }

  /**
   * Usage: complete comparison dataset for return
   */
  public void setComparisonDataset() {
    comparisonDataset = new DefaultIntervalCategoryDataset[6];
    boolean hasNegative = false;
    for (int i = 0; i < minB.length; i++) {
      if (minB[i] < 0) {
        hasNegative = true;
      }
    }
    if (hasNegative) {
      comparisonDataset[MAX] = setCategoryDataset(minB, maxB, maximum);
      comparisonDataset[MIN] = setCategoryDataset(minB, nulB, minimum);
    } else {
      comparisonDataset[MAX] = setCategoryDataset(nulB, maxB, maximum);
      comparisonDataset[MIN] = setCategoryDataset(nulB, minB, minimum);
    }
    comparisonDataset[RANGE] = setCategoryDataset(miTr, maTr, percentOfValues);
    comparisonDataset[CIL] = setCategoryDataset(lowB, avgUB, lowerBound);
    comparisonDataset[AVG] = setCategoryDataset(avgLB, avgUB, average);
    comparisonDataset[CIU] = setCategoryDataset(avgUB, uppB, upperBound);

  }

  /**
   * Usage: return comparison dataset
   * 
   * @return DefaultIntervalCategoryDataset[]
   */
  public DefaultIntervalCategoryDataset[] getComparisonDataset() {
    return comparisonDataset;
  }

  /**
   * Usage: creates a DefaultIntervalCategoryDataset
   * 
   * @param min
   *          - Array of minimums
   * @param max
   *          - Array of maximum
   * @param seriesKey
   *          - name of series
   * @return
   */
  private DefaultIntervalCategoryDataset setCategoryDataset(double[] min,
      double[] max, String seriesKey) {
    final double[][] lows = { min };
    final double[][] highs = { max };
    DefaultIntervalCategoryDataset dset = new DefaultIntervalCategoryDataset(
        lows, highs);
    dset.setSeriesKeys(new String[] { seriesKey });
    return dset;
  }

}
