package aors.module.statistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aors.controller.InitialState;
import aors.data.java.SimulationStepEvent;
import aors.module.statistics.gui.ComponentTranslator;
import aors.statistics.AbstractStatisticsVariable.AggregFunEnumLit;
import aors.statistics.AbstractStatisticsVariable.StatVarDataSourceEnumLit;
import aors.statistics.AbstractStatisticsVariable.StatVarDataTypeEnumLit;

/**
 * StatisticVar
 * 
 * This class represents a statistic variable
 * 
 * @author Daniel Draeger
 * @since 02.11.2009
 */
public class StatisticVar {

  private String name, sourceObjType, sourceObjProperty;
  private StatVarDataSourceEnumLit sourceDataSource;
  private StatVarDataTypeEnumLit dataType;
  private AggregFunEnumLit aggrFn;
  private boolean computeOnlyAtEnd = false;
  private boolean isObjPropChartVar;
  private long sourceObjIdRef;
  private Number initialValue;
  private Number lastValue;
  private long currentStep;
  private List<Number> objectTypeValues;
  private Map<String, String> statsVarUIMap;
  private boolean hasFreqDistChart;

  public final static long DEFAULTIDREF = -1;

  // StatsVarUIMap keys
  public final static String DISPLAYNAME = "DisplayName";
  public final static String TOOLTIP = "ToolTip";
  public final static String MIN = "min";
  public final static String MAX = "max";
  public final static String AVG = "avg";
  public final static String SUM = "sum";
  public final static String STDDEVIATION = "stdDev";
  public final static String CONFIDENCELOWBOUND = "lowBConf";
  public final static String CONFIDENCEUPBOUND = "upBConf";
  public final static String HALFWIDTH = "halfWidth";
  public final static String RUNSSOLVED = "runsSolved";
  public final static String SKEWNESS = "skewness";
  public final static String FORMAT = "Format";
  public final static String DECIMALPLACES = "decimalPlaces";
  public final static String COMPARISONGROUP = "comparisonGroup";
  public final static String SHOWCHART = "showChart";
  public final static String ID = "id";

  // language keys
  private final String simstepError = "SimulationStepError";
  private final String undefDataTypeError = "undefinedDataTypeError";

  /**
   * Create a new {@code StatisticVar}. used for single simulation
   * 
   * @param isOpChartVar
   */
  public StatisticVar(boolean isOpChartVar) {
    this.setObjPropChartVar(isOpChartVar);
    if (isOpChartVar) {
      this.objectTypeValues = new ArrayList<Number>();// last values
    }
    this.statsVarUIMap = new HashMap<String, String>();
    this.sourceObjIdRef = DEFAULTIDREF;
    this.lastValue = 0;
    this.currentStep = 0;
  }

  /**
   * Create a new {@code StatisticVar}. used for multi simulation
   * 
   * @param name
   * @param lastValue
   */
  public StatisticVar(String name, Number lastValue) {
    this.name = name;
    this.lastValue = lastValue;
    this.statsVarUIMap = new HashMap<String, String>();
  }

  /**
   * Usage: Set the actual value of each statistic variable
   * 
   * @param initialState
   *          InitialState
   */
  public void setSimulationstepValue(InitialState initialState,
      SimulationStepEvent stepEvent) {
    long step = this.getCurrentStep();
    try {
      Number newValue = 0;
      // ResourceUtil
      if (this.sourceDataSource
          .equals(StatVarDataSourceEnumLit.ResourceUtilization)) {
        newValue = initialState.getResourceUtilization(this.getName(),
            stepEvent);
      } else if ((this.sourceDataSource
          .equals(StatVarDataSourceEnumLit.ObjectProperty))
          && (this.sourceObjIdRef == -1)) {
        this.objectTypeValues = new ArrayList<Number>();
        List<Double> valueList = initialState.getObjectPropertyIteration(this
            .getName());
        List<Number> valuesCurrStep = new ArrayList<Number>();
        for (int i = 0; i < valueList.size(); i++) {
          valuesCurrStep.add(valueList.get(i));
        }
        if (!this.isComputeOnlyAtEnd()) {
          setMinValue(getMinimumOfList(valuesCurrStep));
          setMaxValue(getMaximumOfList(valuesCurrStep));
        }
        this.setLastValue(getAverageOfList(valuesCurrStep));
        return;
      } else if (this.sourceDataSource
          .equals(StatVarDataSourceEnumLit.ObjectTypeExtensionSize)) {
        newValue = initialState.getInstancesNumberForType(this
            .getSourceObjType());
      } else if (this.sourceDataSource
          .equals(StatVarDataSourceEnumLit.ValueExpr)) {

        if (this.getDataType().equals(StatVarDataTypeEnumLit.Float)) {
          newValue = initialState.getStatisticVariableComputedValueFloat(this
              .getName());
        } else if (this.getDataType().equals(StatVarDataTypeEnumLit.Integer)) {
          newValue = initialState.getStatisticVariableComputedValueLong(this
              .getName());
        }

      } else {
        if (this.getDataType().equals(StatVarDataTypeEnumLit.Float)) {
          newValue = initialState
              .getStatisticVariableValueFloat(this.getName());
        } else if (this.getDataType().equals(StatVarDataTypeEnumLit.Integer)) {
          newValue = initialState.getStatisticVariableValueLong(this.getName());
        } else {
          System.out.println(ComponentTranslator.getResourceBundle().getString(
              undefDataTypeError));
        }
      }
      if (!this.isComputeOnlyAtEnd()) {
        setMinValue(newValue);
        setMaxValue(newValue);
        setAvgValue(newValue);
      }
      if ((this.getAggrFn() != null) && (step > 1)) {

        newValue = calcMinValue(newValue);
        newValue = calcMaxValue(newValue);
        newValue = calculateAggregation(newValue, step);
      }
      this.setLastValue(newValue);
    } catch (Exception e) {
      System.out.println(ComponentTranslator.getResourceBundle().getString(
          simstepError)
          + e.getMessage());
    }
  }

  /**
   * Usage: return values for html output
   * 
   * @return String[]
   */
  public String[] getAnalyseValuesForHtmlOutput(Boolean isSingle) {
    String[] values = { this.getStatsVarUIMap().get(DISPLAYNAME),
        this.getStatsVarUIMap().get(MIN), this.getStatsVarUIMap().get(MAX),
        this.getStatsVarUIMap().get(AVG),
        this.getStatsVarUIMap().get(STDDEVIATION),
        this.getStatsVarUIMap().get(CONFIDENCELOWBOUND),
        this.getStatsVarUIMap().get(CONFIDENCEUPBOUND), isSingle.toString() };
    return values;
  }

  /**
   * Usage: return minimum of a list
   * 
   * @param list
   * @return Number
   */
  public Number getMinimumOfList(List<Number> list) {
    /*
     * Double[] arr = list.toArray(new Double[list.size()]); Arrays.sort(arr);
     * return arr[0];
     */
    Number min = list.get(0);
    for (int i = 1; i < list.size(); i++) {
      if (list.get(i).doubleValue() < min.doubleValue()) {
        min = list.get(i);
      }
    }
    return min;
  }

  /**
   * Usage: return maximum of a list
   * 
   * @param list
   * @return Number
   */
  public Number getMaximumOfList(List<Number> list) {
    /*
     * Double[] arr = list.toArray(new Double[list.size()]); Arrays.sort(arr);
     * return arr[arr.length-1];
     */

    Number max = list.get(0);
    for (int i = 1; i < list.size(); i++) {
      if (list.get(i).doubleValue() > max.doubleValue()) {
        max = list.get(i);
      }
    }
    return max;
  }

  /**
   * Usage: return average of a list
   * 
   * @param list
   * @return Number
   */
  public Number getAverageOfList(List<Number> list) {
    double avg = list.get(0).doubleValue();
    for (int i = 1; i < list.size(); i++) {
      avg = avg + list.get(i).doubleValue();
    }
    return avg / list.size();
  }

  /**
   * Usage: set the new minimum value
   * 
   * @param value
   */
  private void setMinValue(Number value) {
    if (this.getCurrentStep() == 1) {
      this.getStatsVarUIMap().put(MIN, value.toString());
    } else {
      Double old = Double.parseDouble(this.getStatsVarUIMap().get(MIN));
      if (old > value.doubleValue()) {
        this.getStatsVarUIMap().put(MIN, value.toString());
      }
    }
  }

  /**
   * Usage: set the new maximum value
   * 
   * @param value
   */
  private void setMaxValue(Number value) {
    if (this.getCurrentStep() == 1) {
      this.getStatsVarUIMap().put(MAX, value.toString());
    } else {
      Double old = Double.parseDouble(this.getStatsVarUIMap().get(MAX));
      if (old < value.doubleValue()) {
        this.getStatsVarUIMap().put(MAX, value.toString());
      }
    }
  }

  /**
   * Usage: set the new average value
   * 
   * @param value
   */
  private void setAvgValue(Number value) {
    DecimalFormat df = new DecimalFormat("0.00");
    if (Double.isNaN(value.doubleValue())) {
      value = 0;
    }
    if (this.getCurrentStep() == 1) {
      this.getStatsVarUIMap().put(AVG, df.format(value));
    } else {
      Double old = Double.parseDouble(this.getStatsVarUIMap().get(AVG));
      Double newValue = (old * (currentStep - 1) + value.doubleValue())
          / currentStep;
      this.getStatsVarUIMap().put(AVG, df.format(newValue));
    }
  }

  /**
   * Usage: return new minimum value
   * 
   * @param value
   * @return Number
   */
  private Number calcMinValue(Number value) {
    Double old = Double.parseDouble(this.getStatsVarUIMap().get(MIN));
    if (old > value.doubleValue()) {
      this.getStatsVarUIMap().put(MIN, value.toString());
      return value;
    }
    return old;
  }

  /**
   * Usage: return new minimum value
   * 
   * @param value
   * @return Number
   */
  private Number calcMaxValue(Number value) {
    Double old = Double.parseDouble(this.getStatsVarUIMap().get(MAX));
    if (old <= value.doubleValue()) {
      this.getStatsVarUIMap().put(MAX, value.toString());
      return value;
    }
    return old;
  }

  /**
   * Usage: calculate aggregation if defined
   * 
   * @return Number
   */
  private Number calculateAggregation(Number value, long step) {

    Number newValue = value;
    Number result = 0;
    Number oldValue = this.getLastValue();
    if (this.getAggrFn().equals(AggregFunEnumLit.avg)) {
      float divident = oldValue.floatValue() * (step - 1);
      divident = divident + newValue.floatValue();
      return (divident / step);
    } else if (this.getAggrFn().equals(AggregFunEnumLit.max)) {
      if (oldValue.intValue() < newValue.intValue()) {
        return newValue;
      } else {
        return oldValue;
      }
    } else if (this.getAggrFn().equals(AggregFunEnumLit.min)) {
      if (oldValue.floatValue() < newValue.floatValue()) {
        return oldValue;
      } else {
        return newValue;
      }
    } else if (this.getAggrFn().equals(AggregFunEnumLit.sum)) {
      return (oldValue.floatValue() + newValue.floatValue());
    }
    return result;
  }

  /**
   * Usage: return last value
   * 
   * @return Number
   */
  public Number getLastValue() {
    return lastValue;
  }

  /**
   * Usage: set last value
   * 
   * @param lastValue
   */
  public void setLastValue(Number lastValue) {
    this.lastValue = lastValue;
  }

  /**
   * Usage: return last values if objecttype variable has more than one instance
   * 
   * @return List
   */
  public List<Number> getObjValues() {
    return objectTypeValues;
  }

  /**
   * Usage: return name of the statistics variable
   * 
   * @return String
   */
  public String getName() {
    return name;
  }

  /**
   * Usage: set name of the statistics variable
   * 
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Usage: return datatype
   * 
   * @return StatVarDataTypeEnumLit
   */
  public StatVarDataTypeEnumLit getDataType() {
    return dataType;
  }

  /**
   * Usage: set datatype
   * 
   * @param dataType
   */
  public void setDataType(StatVarDataTypeEnumLit dataType) {
    this.dataType = dataType;
  }

  /**
   * Usage: return aggregation function
   * 
   * @return AggregFunEnumLit
   */
  public AggregFunEnumLit getAggrFn() {
    return aggrFn;
  }

  /**
   * Usage: set aggregation function
   * 
   * @param aggregFunEnumLit
   */
  public void setAggrFn(AggregFunEnumLit aggregFunEnumLit) {
    this.aggrFn = aggregFunEnumLit;
  }

  /**
   * Usage: return source object type
   * 
   * @return String
   */
  public String getSourceObjType() {
    return sourceObjType;
  }

  /**
   * Usage: set source object type
   * 
   * @param sourceObjType
   */
  public void setSourceObjType(String sourceObjType) {
    this.sourceObjType = sourceObjType;
  }

  /**
   * Usage: return source object id
   * 
   * @return long
   */
  public long getSourceObjIdRef() {
    return sourceObjIdRef;
  }

  /**
   * Usage: set source object id
   * 
   * @param sourceObjIdRef
   */
  public void setSourceObjIdRef(long sourceObjIdRef) {
    this.sourceObjIdRef = sourceObjIdRef;
  }

  /**
   * Usage: return if the the variable should be calculated at the end of a
   * simulation or at each step
   * 
   * @return boolean
   */
  public boolean isComputeOnlyAtEnd() {
    return computeOnlyAtEnd;
  }

  /**
   * Usage: set if the the variable should be calculated at the end of a
   * simulation or at each step
   * 
   * @param computeOnlyAtEnd
   */
  public void setComputeOnlyAtEnd(boolean computeOnlyAtEnd) {
    this.computeOnlyAtEnd = computeOnlyAtEnd;
  }

  /**
   * Usage: return the initial value
   * 
   * @return Number
   */
  public Number getInitialValue() {
    return initialValue;
  }

  /**
   * Usage: set the initial value
   * 
   * @param initialValue
   */
  public void setInitialValue(Number initialValue) {
    this.initialValue = initialValue;
  }

  /**
   * Usage: return the statisticsVariableUI
   * 
   * @return Map
   */
  public Map<String, String> getStatsVarUIMap() {
    return statsVarUIMap;
  }

  /**
   * Usage: set the statisticsVariableUI
   * 
   * @param map
   */
  public void setStatsVarUIMap(Map<String, String> map) {
    statsVarUIMap = map;
  }

  /**
   * Usage: set data source
   * 
   * @param sourceDataSource
   */
  public void setSourceDataSource(StatVarDataSourceEnumLit sourceDataSource) {
    this.sourceDataSource = sourceDataSource;
  }

  /**
   * Usage: return data source
   * 
   * @return StatVarDataSourceEnumLit
   */
  public StatVarDataSourceEnumLit getSourceDataSource() {
    return sourceDataSource;
  }

  /**
   * Usage: set object property
   * 
   * @param sourceObjProperty
   */
  public void setSourceObjProperty(String sourceObjProperty) {
    this.sourceObjProperty = sourceObjProperty;
  }

  /**
   * Usage: return object property
   * 
   * @return String
   */
  public String getSourceObjProperty() {
    return sourceObjProperty;
  }

  /**
   * Usage: set true if the statistics variable has a frequency distribution
   * chart
   * 
   * @param freqDistChart
   */
  public void setFrequencyDistributionChart(boolean hasFreqDistChart) {
    this.hasFreqDistChart = hasFreqDistChart;
  }

  /**
   * Usage: return true if the statistics variable has a frequency distribution
   * chart
   * 
   * @return boolean
   */
  public boolean hasFrequencyDistributionChart() {
    return hasFreqDistChart;
  }

  /**
   * Usage: set true if the statistics variable has an object as source
   * 
   * @param isObjPropChartVar
   */
  public void setObjPropChartVar(boolean isObjPropChartVar) {
    this.isObjPropChartVar = isObjPropChartVar;
  }

  /**
   * Usage: return true if the statistics variable has an object as source
   * 
   * @return boolean
   */
  public boolean isObjPropChartVar() {
    return isObjPropChartVar;
  }

  /**
   * Usage: set the current simulation step
   * 
   * @param currentStep
   */
  public void setCurrentStep(long currentStep) {
    this.currentStep = currentStep;
  }

  /**
   * Usage: return the current simulation step
   * 
   * @return long
   */
  public long getCurrentStep() {
    return currentStep;
  }

  /**
   * Usage: increment the simulation step
   * 
   */
  public void incCurrentStep() {
    this.currentStep++;
  }
}
