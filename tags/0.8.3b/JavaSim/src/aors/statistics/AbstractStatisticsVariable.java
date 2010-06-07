/**
 * 
 */
package aors.statistics;

import java.util.List;

import aors.model.envsim.Objekt;

/**
 * @author Jens Werner
 * 
 */
public abstract class AbstractStatisticsVariable {

  private String name;

  private String displayName;

  protected double value;

  private boolean computeOnlyAtEnd = false;

  private String sourceObjectType;
  private String sourceObjectProperty;
  private long sourceObjectIdRef;

  protected Objekt objekt = null;
  protected List<Objekt> objektList;

  private AbstractStatisticsVariable sourceVariable;

  private StatVarDataTypeEnumLit dataType;
  private AggregFunEnumLit aggregFun;
  private StatVarDataSourceEnumLit dataSource = StatVarDataSourceEnumLit.Default;

  /*
   * default user statistic variables
   */
  public AbstractStatisticsVariable(String name, StatVarDataTypeEnumLit type) {

    this.name = name;
    this.dataType = type;
  }

  /*
   * ObjectProperty, ResourceUtilization with one object
   */
  public AbstractStatisticsVariable(String name, StatVarDataTypeEnumLit type,
      Objekt objekt) {

    this.name = name;
    this.dataType = type;
    this.objekt = objekt;
  }

  /*
   * ObjectProperty, ResourceUtilization, ObjectTypeExtensionSize with a list of
   * objects
   */
  public AbstractStatisticsVariable(String name, StatVarDataTypeEnumLit type,
      List<Objekt> objektList) {

    this.name = name;
    this.dataType = type;
    this.objektList = objektList;
    // this.objektIterator = this.objektList.iterator();
  }

  /**
   * @param value
   *          the value to set
   */
  public void setValue(double value) {
    this.value = value;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @return the displayName
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * @param displayName
   *          the displayName to set
   */
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  /**
   * @return the sourceObjectType
   */
  public String getSourceObjectType() {
    return sourceObjectType;
  }

  /**
   * @param sourceObjectType
   *          the sourceObjectType to set
   */
  public void setSourceObjectType(String sourceObjectType) {
    this.sourceObjectType = sourceObjectType;
  }

  /**
   * @return the sourceObjectProperty
   */
  public String getSourceObjectProperty() {
    return sourceObjectProperty;
  }

  /**
   * @param sourceObjectProperty
   *          the sourceObjectProperty to set
   */
  public void setSourceObjectProperty(String sourceObjectProperty) {
    this.sourceObjectProperty = sourceObjectProperty;
  }

  /**
   * @return the sourceObjectRef
   */
  public long getSourceObjectIdRef() {
    return sourceObjectIdRef;
  }

  /**
   * @param sourceObjectRef
   *          the sourceObjectRef to set
   */
  public void setSourceObjectIdRef(long sourceObjectRef) {
    this.sourceObjectIdRef = sourceObjectRef;
  }

  /**
   * @return the computeOnlyAtEnd
   */
  public boolean isComputeOnlyAtEnd() {
    return computeOnlyAtEnd;
  }

  /**
   * @param computeOnlyAtEnd
   *          the computeOnlyAtEnd to set
   */
  public void setComputeOnlyAtEnd(boolean computeOnlyAtEnd) {
    this.computeOnlyAtEnd = computeOnlyAtEnd;
  }

  /**
   * @return the dataType
   */
  public StatVarDataTypeEnumLit getDataType() {
    return this.dataType;
  }

  /**
   * @return the dataSource
   */
  public StatVarDataSourceEnumLit getDataSource() {
    return this.dataSource;
  }

  public void setStatVarDataSource(
      StatVarDataSourceEnumLit statVarDataSourceEnumLit) {
    this.dataSource = statVarDataSourceEnumLit;
  }

  /**
   * @param aggregFun
   *          the aggregFun to set
   */
  public void setAggregFun(AggregFunEnumLit aggregFun) {
    this.aggregFun = aggregFun;
  }

  /**
   * @return the aggregFun
   */
  public AggregFunEnumLit getAggregFun() {
    return aggregFun;
  }

  public enum StatVarDataTypeEnumLit {
    Float, Integer
  }

  public enum StatVarDataSourceEnumLit {
    Default, GlobalVariable, StatisticsVariable, ObjectProperty, ObjectTypeExtensionSize, ResourceUtilization, ValueExpr
  }

  public enum AggregFunEnumLit {
    max, min, avg, sum
  }

  public abstract Number getValue();

  public abstract void computeVar();

  /**
   * @return the sourceVariable
   */
  public AbstractStatisticsVariable getSourceVariable() {
    return sourceVariable;
  }

  /**
   * @param sourceVariable
   *          the sourceVariable to set
   */
  public void setSourceVariable(AbstractStatisticsVariable sourceVariable) {
    this.sourceVariable = sourceVariable;
  }

}
