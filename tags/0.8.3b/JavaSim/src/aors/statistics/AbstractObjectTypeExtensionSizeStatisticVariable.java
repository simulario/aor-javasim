package aors.statistics;

import java.util.List;

import aors.model.envsim.Objekt;

public abstract class AbstractObjectTypeExtensionSizeStatisticVariable extends
    AbstractStatisticsVariable {

  /*
   * ObjectProperty, ResourceUtilization, ObjectTypeExtensionSize with a list of
   * objects
   */
  public AbstractObjectTypeExtensionSizeStatisticVariable(String name,
      StatVarDataTypeEnumLit type, List<Objekt> objektList) {
    super(name, type, objektList);
  }

  @Override
  public Number getValue() {
    if (this.objektList != null)
      return this.objektList.size();
    return 0;
  }

}
