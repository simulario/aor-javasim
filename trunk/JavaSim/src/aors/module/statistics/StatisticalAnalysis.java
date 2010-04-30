package aors.module.statistics;

import java.text.DecimalFormat;
import java.util.List;

import aors.statistics.AbstractStatisticsVariable.StatVarDataTypeEnumLit;

/**
 * StatisticalAnalysis
 * 
 * This class calculates all aggregation functions
 * 
 * @author Daniel Draeger
 * @since 01.12.2009
 */
public class StatisticalAnalysis {

  private List<StatisticVar> vars;
  private long step;
  private DecimalFormat df = new DecimalFormat("0.00");

  /**
   * Create a new {@code StatisticalAnalysis}.
   * 
   */
  public StatisticalAnalysis() {

  }

  /**
   * Create a new {@code StatisticalAnalysis}.
   * 
   * @param vars
   *          list of statistic variables
   * @param step
   *          current simulation step
   */
  public StatisticalAnalysis(List<StatisticVar> vars, long step) {
    this.vars = vars;
    this.step = step;

  }

  /**
   * Usage: return the maximum value of the i-th statistic variable
   * 
   * @param variable
   * @return Number
   */
  public Number getMaxValue(int variable) {
    Number max;
    if (!(vars.get(variable).isObjPropChartVar())) {
      max = vars.get(variable).getLastValue();
    } else {
      max = vars.get(variable).getMaximumOfList(
          vars.get(variable).getObjValues());
    }
    double currMax = Double.parseDouble(vars.get(variable).getStatsVarUIMap()
        .get(StatisticVar.MAX));
    if (max.doubleValue() < currMax) {
      max = currMax;
    }
    if (vars.get(variable).getDataType().equals(StatVarDataTypeEnumLit.Integer)) {
      return max.intValue();
    }
    return max;
  }

  /**
   * Usage: return the minimum value of the i-th statistic variable
   * 
   * @param variable
   * @return Number
   */
  public Number getMinValue(int variable) {
    Number min;
    if (!(vars.get(variable).isObjPropChartVar())) {
      min = vars.get(variable).getLastValue();
    } else {
      min = vars.get(variable).getMinimumOfList(
          vars.get(variable).getObjValues());
    }
    double currMin = Double.parseDouble(vars.get(variable).getStatsVarUIMap()
        .get(StatisticVar.MIN));
    if (min.doubleValue() > currMin) {
      min = currMin;
    }
    if (vars.get(variable).getDataType().equals(StatVarDataTypeEnumLit.Integer)) {
      return min.intValue();
    }
    return min;
  }

  /**
   * Usage: return the average value of the i-th statistic variable
   * 
   * @param variable
   * @return Number
   */
  public String getAvgValue(int variable) {
    Double avg = 0.0;
    if (step == 1) {
      if (vars.get(variable).getDataType().equals(
          StatVarDataTypeEnumLit.Integer)) {
        String avgInt = new Integer(vars.get(variable).getLastValue()
            .intValue()).toString();
        return avgInt;
      }
      return df.format(vars.get(variable).getLastValue());

    } else {
      try {
        avg = Double.parseDouble(vars.get(variable).getStatsVarUIMap().get(
            StatisticVar.AVG));
        avg = ((step - 1) * avg + vars.get(variable).getLastValue()
            .doubleValue())
            / step;

      } catch (Exception e) {
        return "0";
      }
    }
    return df.format(avg);
  }

  /**
   * Usage: return the sum of the i-th statistic variable
   * 
   * @param variable
   * @return Number
   */
  public Number getSumValue(int variable) {
    Double sum;
    if (step == 1) {
      return vars.get(variable).getLastValue();
    } else {
      sum = Double.parseDouble(vars.get(variable).getStatsVarUIMap().get(
          StatisticVar.SUM))
          + vars.get(variable).getLastValue().doubleValue();
    }
    return sum;
  }
}
