package aors.codegen;

public enum InitializerNodeNames {

  /*
   * 
   */
  SimulationScenario, SimulationModel, EntityTypes,
  /*
   * EntityTypes
   */
  ObjectType, AgentType, PhysicalObjectType, PhysicalAgentType,

  InitialAttributeValue,

  /*
   * DataTypes
   */
  DataTypes, ComplexDataType, Enumeration, EnumerationLiteral,
  /*
   * PropertyElements
   */
  Attribute, ComplexDataProperty, EnumerationProperty, ReferenceProperty,

  /*
   * PropertyAttributes
   */
  type,

  /*
   * Initialstate
   */
  InitialState, Object, Agent, PhysicalObject, PhysicalAgent,

  Slot
}
