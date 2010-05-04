<?xml version="1.0" encoding="UTF-8"?>
<!--
    Custom Configuration Template
    
    $Rev$
    $Date$

    @author:   Jens Werner (jens.werner@tu-cottbus.de)
    @license:  GNU General Public License version 2 or higher
    @last changed by $Author$
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns:aorsml="http://aor-simulation.org"
  xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://aor-simulation.org aorsml.xsd">

  <xsl:param name="file.separator" select="'/'"/>

  <xsl:param name="with.mainMethod" as="xs:boolean" select="true()"/>

  <!-- used language in expressions -->
  <xsl:param name="output.language" as="xs:string">Java</xsl:param>

  <!-- file extension for created files -->
  <xsl:param name="output.fileExtension" as="xs:string">java</xsl:param>

  <!-- switch @SuppressWarnings("unchecked") on/off -->
  <xsl:param name="suppressWarnings" as="xs:boolean" select="true()"/>

  <!-- ++++ SimulatorPackestruktur ++++ -->
  <xsl:param name="core.package.root" as="xs:string">aors</xsl:param>

  <xsl:param name="core.package.controller" select="fn:concat($core.package.root, '.controller')"/>
  <xsl:param name="core.package.logger" select="fn:concat($core.package.root, '.data.logger')"/>
  <xsl:param name="core.package.data" select="fn:concat($core.package.root, '.data')"/>

  <xsl:param name="core.package.model" select="fn:concat($core.package.root, '.model')"/>
  <xsl:param name="core.package.model.agentSim" select="fn:concat($core.package.model, '.agtsim')"/>
  <xsl:param name="core.package.model.agentSim.agt" select="fn:concat($core.package.model.agentSim, '.agt')"/>
  <xsl:param name="core.package.model.agentSim.java" select="fn:concat($core.package.model.agentSim, '.java')"/>
  <xsl:param name="core.package.model.envEvent" select="fn:concat($core.package.model, '.envevt')"/>
  <xsl:param name="core.package.model.envEvent.activity" select="fn:concat($core.package.model.envEvent, '.activity')"/>
  <xsl:param name="core.package.model.envSim" select="fn:concat($core.package.model, '.envsim')"/>
  <xsl:param name="core.package.model.envSim.agt" select="fn:concat($core.package.model.envSim, '.agt')"/>
  <xsl:param name="core.package.model.envSim.msg" select="fn:concat($core.package.model.envSim, '.msg')"/>
  <xsl:param name="core.package.model.envSim.java" select="fn:concat($core.package.model.envSim, '.java')"/>
  <xsl:param name="core.package.model.intEvent" select="fn:concat($core.package.model, '.intevt')"/>
  <xsl:param name="core.package.statistics" select="fn:concat($core.package.root, '.statistics')"/>
  <xsl:param name="core.package.util" select="fn:concat($core.package.root, '.util')"/>
  <xsl:param name="core.package.util.collection" select="fn:concat($core.package.util, '.collection')"/>
  <xsl:param name="core.package.util.refTypes" select="fn:concat($core.package.util, '.reftypes')"/>
  
  <!-- NEW -->
  <xsl:param name="core.package.util.wrapper" select="fn:concat($core.package.util, '.wrapper')"/>

  <xsl:param name="logger.package.logger" select="fn:concat($core.package.data, '.logger')"/>
  <xsl:param name="logger.package.logger.java" select="fn:concat($core.package.data, '.java')"/>

  <xsl:param name="space.package.space" select="fn:concat($core.package.root, '.space')"/>

  <!-- +++++ SimulatorClassNames/Packages ++++ -->
  <xsl:param name="core.class.entity" as="xs:string">Entity</xsl:param>
  <xsl:param name="core.package.entity" select="fn:concat($core.package.model, '.', $core.class.entity)"/>

  <xsl:param name="core.class.message" as="xs:string">Message</xsl:param>
  <xsl:param name="core.package.message" select="fn:concat($core.package.model, '.', $core.class.message)"/>

  <xsl:param name="core.class.simulationParameters" as="xs:string">GeneralSimulationParameters</xsl:param>
  <xsl:param name="core.package.simulationParameters" select="fn:concat($core.package.root, '.', $core.class.simulationParameters)"/>

  <xsl:param name="core.class.spaceModel" as="xs:string">GeneralSpaceModel</xsl:param>
  <xsl:param name="core.package.spaceModel" select="fn:concat($core.package.root, '.', $core.class.spaceModel)"/>

  <xsl:param name="core.enum.spaceType" as="xs:string">SpaceType</xsl:param>
  <xsl:param name="core.package.spaceModel.spaceType" select="fn:concat($core.package.spaceModel, '.', $core.enum.spaceType)"/>

  <xsl:param name="core.class.simulationEngine" as="xs:string">AbstractSimulator</xsl:param>
  <xsl:param name="core.package.simulationEngine" select="fn:concat($core.package.controller, '.', $core.class.simulationEngine)"/>

  <xsl:param name="core.class.physAgentObject" as="xs:string">PhysicalAgentObject</xsl:param>
  <xsl:param name="core.package.physAgentObject" select="fn:concat($core.package.model.envSim, '.', $core.class.physAgentObject)"/>

  <xsl:param name="core.class.initializationRule" as="xs:string">InitializationRule</xsl:param>
  <xsl:param name="core.package.initializationRule" select="fn:concat($core.package.model.envSim, '.', $core.class.initializationRule)"/>

  <xsl:param name="core.class.environmentRule" as="xs:string">EnvironmentRule</xsl:param>
  <xsl:param name="core.package.environmentRule" select="fn:concat($core.package.model.envSim, '.', $core.class.environmentRule)"/>

  <xsl:param name="core.class.environmentSimulator" as="xs:string">EnvironmentSimulator</xsl:param>
  <xsl:param name="core.package.environmentSimulator" select="fn:concat($core.package.model.envSim, '.', $core.class.environmentSimulator)"/>

  <xsl:param name="core.class.environmentAccessFacet" as="xs:string">EnvironmentAccessFacet</xsl:param>
  <xsl:param name="core.package.environmentAccessFacet" select="fn:concat($core.package.model.envSim, '.', $core.class.environmentAccessFacet)"/>

  <xsl:param name="core.class.object" as="xs:string">Objekt</xsl:param>
  <xsl:param name="core.package.object" select="fn:concat($core.package.model.envSim, '.', $core.class.object)"/>

  <xsl:param name="core.class.agentObject" as="xs:string">AgentObject</xsl:param>
  <xsl:param name="core.package.agentObject" select="fn:concat($core.package.model.envSim, '.', $core.class.agentObject)"/>

  <xsl:param name="core.class.physicalObjekt" as="xs:string">PhysicalObject</xsl:param>
  <xsl:param name="core.package.physicalObjekt" select="fn:concat($core.package.model.envSim, '.', $core.class.physicalObjekt)"/>

  <!-- new -->
  <xsl:param name="core.enum.materialType" as="xs:string">MaterialType</xsl:param>
  <xsl:param name="core.package.materialType" select="fn:concat($core.package.model.envSim, '.', $core.enum.materialType)"/>

  <!-- new -->
  <xsl:param name="core.enum.shape2D" as="xs:string">Shape2D</xsl:param>
  <xsl:param name="core.package.shape2D" select="fn:concat($core.package.model.envSim, '.', $core.enum.shape2D)"/>

  <!-- new -->
  <xsl:param name="core.enum.shape3D" as="xs:string">Shape3D</xsl:param>
  <xsl:param name="core.package.shape3D" select="fn:concat($core.package.model.envSim, '.', $core.enum.shape3D)"/>

  <xsl:param name="core.class.agentSubject" as="xs:string">AgentSubject</xsl:param>
  <xsl:param name="core.package.agentSubject" select="fn:concat($core.package.model.agentSim, '.', $core.class.agentSubject)"/>

  <xsl:param name="core.class.physAgentSubject" as="xs:string">PhysicalAgentSubject</xsl:param>
  <xsl:param name="core.package.physAgentSubject" select="fn:concat($core.package.model.agentSim, '.', $core.class.physAgentSubject)"/>

  <xsl:param name="core.class.event" as="xs:string">AtomicEvent</xsl:param>
  <xsl:param name="core.package.event" select="fn:concat($core.package.model, '.', $core.class.event)"/>

  <xsl:param name="core.class.environmentEvent" as="xs:string">EnvironmentEvent</xsl:param>
  <xsl:param name="core.package.environmentEvent" select="fn:concat($core.package.model.envEvent, '.', $core.class.environmentEvent)"/>

  <xsl:param name="core.class.exogenousEvent" as="xs:string">ExogenousEvent</xsl:param>
  <xsl:param name="core.package.exogenousEvent" select="fn:concat($core.package.model.envEvent, '.', $core.class.exogenousEvent)"/>

  <xsl:param name="core.class.onEveryStepEnvEvent" as="xs:string">EachSimulationStep</xsl:param>
  <xsl:param name="core.package.onEveryStepEnvEvent" select="fn:concat($core.package.model.envEvent, '.', $core.class.onEveryStepEnvEvent)"/>

  <xsl:param name="core.class.actionEvent" as="xs:string">ActionEvent</xsl:param>
  <xsl:param name="core.package.actionEvent" select="fn:concat($core.package.model.envEvent, '.', $core.class.actionEvent)"/>

  <xsl:param name="core.class.perceptionEvent" as="xs:string">PerceptionEvent</xsl:param>
  <xsl:param name="core.package.perceptionEvent" select="fn:concat($core.package.model.envEvent, '.', $core.class.perceptionEvent)"/>

  <xsl:param name="core.class.causedEvent" as="xs:string">CausedEvent</xsl:param>
  <xsl:param name="core.package.causedEvent" select="fn:concat($core.package.model.envEvent, '.', $core.class.causedEvent)"/>

  <xsl:param name="core.class.stopSimulationEvent" as="xs:string">StopSimulationEvent</xsl:param>
  <xsl:param name="core.class.stopSimulationEvent.alias" as="xs:string">StopSimulation</xsl:param>
  <xsl:param name="core.package.stopSimulationEvent" select="fn:concat($core.package.model.envEvent, '.', $core.class.stopSimulationEvent)"/>

  <xsl:param name="core.class.inMessageEvent" as="xs:string">InMessageEvent</xsl:param>
  <xsl:param name="core.package.inMessageEvent" select="fn:concat($core.package.model.envEvent, '.', $core.class.inMessageEvent)"/>

  <xsl:param name="core.class.outMessageEvent" as="xs:string">OutMessageEvent</xsl:param>
  <xsl:param name="core.package.outMessageEvent" select="fn:concat($core.package.model.envEvent, '.', $core.class.outMessageEvent)"/>

  <xsl:param name="core.class.physicalObjectPerceptionEvent" as="xs:string">PhysicalObjectPerceptionEvent</xsl:param>
  <xsl:param name="core.package.physicalObjectPerceptionEvent"
    select="fn:concat($core.package.model.envEvent, '.', $core.class.physicalObjectPerceptionEvent)"/>

  <xsl:param name="core.class.collisionEvent" as="xs:string">CollisionEvent</xsl:param>
  <xsl:param name="core.package.collisionEvent" select="fn:concat($core.package.model.envEvent, '.', $core.class.collisionEvent)"/>

  <xsl:param name="core.class.internalEvent" as="xs:string">InternalEvent</xsl:param>
  <xsl:param name="core.package.internalEvent" select="fn:concat($core.package.model.intEvent, '.', $core.class.internalEvent)"/>

  <xsl:param name="core.class.actualPerceptionEvent" as="xs:string">ActualPerceptionEvent</xsl:param>
  <xsl:param name="core.package.actualPerceptionEvent" select="fn:concat($core.package.model.intEvent, '.', $core.class.actualPerceptionEvent)"/>

  <xsl:param name="core.class.timeEvent" as="xs:string">TimeEvent</xsl:param>
  <xsl:param name="core.package.timeEvent" select="fn:concat($core.package.model.intEvent, '.', $core.class.timeEvent)"/>

  <xsl:param name="core.class.periodicTimeEvent" as="xs:string">PeriodicTimeEvent</xsl:param>
  <xsl:param name="core.package.periodicTimeEvent" select="fn:concat($core.package.model.intEvent, '.', $core.class.periodicTimeEvent)"/>

  <xsl:param name="core.class.onEveryStepIntEvent" as="xs:string">EachSimulationStep</xsl:param>
  <xsl:param name="core.package.onEveryStepIntEvent" select="fn:concat($core.package.model.intEvent, '.', $core.class.onEveryStepIntEvent)"/>

  <xsl:param name="core.class.reminderEvent" as="xs:string">ReminderEvent</xsl:param>
  <xsl:param name="core.package.reminderEvent" select="fn:concat($core.package.model.intEvent, '.', $core.class.reminderEvent)"/>

  <xsl:param name="core.class.agentSubjectListener" as="xs:string">AgentSubjectListener</xsl:param>
  <xsl:param name="core.package.agentSubjectListener" select="fn:concat($core.package.model.agentSim.java, '.', $core.class.agentSubjectListener)"/>

  <xsl:param name="core.class.agentSimulatorDefaultImpl" as="xs:string">AgentSimulatorDefaultImpl</xsl:param>
  <xsl:param name="core.package.agentSimulatorDefaultImpl"
    select="fn:concat($core.package.model.agentSim, '.', $core.class.agentSimulatorDefaultImpl)"/>

  <xsl:param name="core.class.communicationRule" as="xs:string">CommunicationRule</xsl:param>
  <xsl:param name="core.package.communicationRule" select="fn:concat($core.package.model.agentSim, '.', $core.class.communicationRule)"/>

  <xsl:param name="core.class.reactionRule" as="xs:string">ReactionRule</xsl:param>
  <xsl:param name="core.package.reactionRule" select="fn:concat($core.package.model.agentSim, '.', $core.class.reactionRule)"/>

  <xsl:param name="core.class.actualPerceptionRule" as="xs:string">ActualPerceptionRule</xsl:param>
  <xsl:param name="core.package.actualPerceptionRule" select="fn:concat($core.package.model.agentSim, '.', $core.class.actualPerceptionRule)"/>

  <!-- activities -->
  <xsl:param name="activity.class.abstractActivity" as="xs:string">Activity</xsl:param>
  <xsl:param name="activity.package.abstractActivity" select="fn:concat($core.package.model.envEvent.activity, '.', $activity.class.abstractActivity)"/>

  <xsl:param name="activity.class.abstractActivityFactory" as="xs:string">AbstractActivityFactory</xsl:param>
  <xsl:param name="activity.package.abstractActivityFactory"
    select="fn:concat($core.package.model.envSim, '.', $activity.class.abstractActivityFactory)"/>

  <xsl:param name="core.class.activityEndEvent" as="xs:string">ActivityEndEvent</xsl:param>
  <xsl:param name="core.package.activityEndEvent" select="fn:concat($core.package.model.envEvent, '.', $core.class.activityEndEvent)"/>

  <xsl:param name="core.class.activityStartEvent" as="xs:string">ActivityStartEvent</xsl:param>
  <xsl:param name="core.package.activityStartEvent" select="fn:concat($core.package.model.envEvent, '.', $core.class.activityStartEvent)"/>

  <!-- logger -->
  <xsl:param name="logger.class.loggerDefaultImpl" as="xs:string">Logger</xsl:param>
  <xsl:param name="logger.package.loggerDefaultImpl" select="fn:concat($logger.package.logger, '.', $logger.class.loggerDefaultImpl)"/>

  <xsl:param name="logger.class.objInitEvent" as="xs:string">ObjektInitEvent</xsl:param>
  <xsl:param name="logger.package.objInitEvent" select="fn:concat($logger.package.logger.java, '.', $logger.class.objInitEvent)"/>

  <xsl:param name="logger.class.objInitEventListener" as="xs:string">ObjektInitEventListener</xsl:param>
  <xsl:param name="logger.package.objInitEventListener" select="fn:concat($logger.package.logger.java, '.', $logger.class.objInitEventListener)"/>

  <xsl:param name="data.class.dataBus" as="xs:string">DataBus</xsl:param>
  <xsl:param name="data.package.dataBus" select="fn:concat($core.package.data, '.', $data.class.dataBus)"/>

  <xsl:param name="databus.class.dataBus" as="xs:string">dataBus</xsl:param>

  <!-- statistic -->
  <xsl:param name="core.class.generalStatistics" as="xs:string">GeneralStatistics</xsl:param>
  <xsl:param name="core.package.generalStatistics" select="fn:concat($core.package.statistics, '.', $core.class.generalStatistics)"/>

  <xsl:param name="core.class.abstractStatisticsVariable" as="xs:string">AbstractStatisticsVariable</xsl:param>
  <xsl:param name="core.package.abstractStatisticsVariable" select="fn:concat($core.package.statistics, '.', $core.class.abstractStatisticsVariable)"/>

  <xsl:param name="core.class.abstractResourceUtilizationStatisticVariable" as="xs:string">AbstractResourceUtilizationStatisticVariable</xsl:param>
  <xsl:param name="core.package.abstractResourceUtilizationStatisticVariable"
    select="fn:concat($core.package.statistics, '.', $core.class.abstractResourceUtilizationStatisticVariable)"/>

  <xsl:param name="core.class.abstractObjectTypeExtensionSizeStatisticVariable" as="xs:string"
    >AbstractObjectTypeExtensionSizeStatisticVariable</xsl:param>
  <xsl:param name="core.package.abstractObjectTypeExtensionSizeStatisticVariable"
    select="fn:concat($core.package.statistics, '.', $core.class.abstractObjectTypeExtensionSizeStatisticVariable)"/>

  <xsl:param name="core.class.abstractObjectPropertyStatisticVariable" as="xs:string">AbstractObjectPropertyStatisticVariable</xsl:param>
  <xsl:param name="core.package.abstractObjectPropertyStatisticVariable"
    select="fn:concat($core.package.statistics, '.', $core.class.abstractObjectPropertyStatisticVariable)"/>

  <xsl:param name="core.class.abstractStatisticsVariableStatisticsVariable" as="xs:string">AbstractStatisticsVariableStatisticsVariable</xsl:param>
  <xsl:param name="core.package.abstractStatisticsVariableStatisticsVariable"
    select="fn:concat($core.package.statistics, '.', $core.class.abstractStatisticsVariableStatisticsVariable)"/>

  <xsl:param name="core.class.statVarDataTypeEnumLit" as="xs:string">StatVarDataTypeEnumLit</xsl:param>
  <xsl:param name="core.package.statVarDataTypeEnumLit"
    select="fn:concat($core.package.abstractStatisticsVariable, '.', $core.class.statVarDataTypeEnumLit)"/>

  <xsl:param name="core.enum.statVarDataSourceEnumLit" as="xs:string">StatVarDataSourceEnumLit</xsl:param>
  <xsl:param name="core.package.statVarDataSourceEnumLit"
    select="fn:concat($core.package.abstractStatisticsVariable, '.', $core.enum.statVarDataSourceEnumLit)"/>

  <xsl:param name="core.enum.aggregFunEnumLit" as="xs:string">AggregFunEnumLit</xsl:param>
  <xsl:param name="core.package.aggregFunEnumLit" select="fn:concat($core.package.abstractStatisticsVariable, '.', $core.enum.aggregFunEnumLit)"/>

  <xsl:param name="core.class.abstractPropertyIterator" as="xs:string">AbstractPropertyIterator</xsl:param>
  <xsl:param name="core.package.abstractPropertyIterator"
    select="fn:concat($core.package.statistics, '.', $core.class.abstractStatisticsVariable, '.', core.class.abstractPropertyIterator)"/>
  
  <!-- new -->
  <xsl:param name="core.class.abstractObjektIDRefPropertyIterator" as="xs:string">AbstractObjektIDRefPropertyIterator</xsl:param>
  <xsl:param name="core.package.abstractObjektIDRefPropertyIterator"
    select="fn:concat($core.package.statistics, '.', $core.class.abstractStatisticsVariable, '.', core.class.abstractObjektIDRefPropertyIterator)"/>
  
  <!-- new -->
  <xsl:param name="core.class.objektIdPropertyData" as="xs:string">ObjektIdPropertyData</xsl:param>
  <xsl:param name="core.package.objektIdPropertyData"
    select="fn:concat($core.package.statistics, '.', $core.class.abstractStatisticsVariable, '.', core.class.objektIdPropertyData)"/>

  <!-- space -->
  <xsl:param name="space.class.gridCells" as="xs:string">GridCells</xsl:param>
  <xsl:param name="space.package.gridCells" select="fn:concat($space.package.space, '.', $space.class.gridCells)"/>

  <xsl:param name="space.class.abstractCell" as="xs:string">AbstractCell</xsl:param>
  <xsl:param name="space.package.abstractCell" select="fn:concat($space.package.space, '.', $space.class.abstractCell)"/>

  <xsl:param name="space.class.oneDimDiscreteSpace" as="xs:string">OneDimensionalGrid</xsl:param>
  <xsl:param name="space.package.oneDimDiscreteSpace" select="fn:concat($space.package.space, '.', $space.class.oneDimDiscreteSpace)"/>

  <xsl:param name="space.class.twoDimDiscreteSpace" as="xs:string">TwoDimensionalGrid</xsl:param>
  <xsl:param name="space.package.twoDimDiscreteSpace" select="fn:concat($space.package.space, '.', $space.class.twoDimDiscreteSpace)"/>

  <xsl:param name="space.class.threeDimDiscreteSpace" as="xs:string">ThreeDimensionalGrid</xsl:param>
  <xsl:param name="space.package.threeDimDiscreteSpace" select="fn:concat($space.package.space, '.', $space.class.threeDimDiscreteSpace)"/>

  <xsl:param name="space.class.oneDimNonDiscreteSpace" as="xs:string">OneDimensional</xsl:param>
  <xsl:param name="space.package.oneDimNonDiscreteSpace" select="fn:concat($space.package.space, '.', $space.class.oneDimNonDiscreteSpace)"/>

  <xsl:param name="space.class.twoDimNonDiscreteSpace" as="xs:string">TwoDimensional</xsl:param>
  <xsl:param name="space.package.twoDimNonDiscreteSpace" select="fn:concat($space.package.space, '.', $space.class.twoDimNonDiscreteSpace)"/>

  <!-- new -->
  <xsl:param name="space.class.twoDimNonDiscreteLateralViewSpace" as="xs:string">TwoDimensionalLateralView</xsl:param>
  <xsl:param name="space.package.twoDimNonDiscreteLateralViewSpace"
    select="fn:concat($space.package.space, '.', $space.class.twoDimNonDiscreteLateralViewSpace)"/>

  <xsl:param name="space.enum.twoDimNonDiscreteSpace.viewMode" as="xs:string">ViewMode</xsl:param>
  <xsl:param name="space.package.twoDimNonDiscreteSpace.viewMode"
    select="fn:concat($space.package.twoDimNonDiscreteSpace, '.', $space.enum.twoDimNonDiscreteSpace.viewMode)"/>

  <xsl:param name="space.class.threeDimNonDiscreteSpace" as="xs:string">ThreeDimensional</xsl:param>
  <xsl:param name="space.package.threeDimNonDiscreteSpace" select="fn:concat($space.package.space, '.', $space.class.threeDimNonDiscreteSpace)"/>

  <xsl:param name="space.class.space" as="xs:string">Space</xsl:param>
  <xsl:param name="space.package.space.space" select="fn:concat($space.package.space, '.', $space.class.space)"/>

  <xsl:param name="space.class.discreteSpace" as="xs:string">DiscreteSpace</xsl:param>
  <xsl:param name="space.package.space.discreteSpace" select="fn:concat($space.package.space, '.', $space.class.discreteSpace)"/>

  <xsl:param name="space.class.positionData" as="xs:string">DiscretePositionData</xsl:param>
  <xsl:param name="space.package.positionData" select="fn:concat($space.package.space.discreteSpace, '.', $space.class.positionData)"/>

  <xsl:param name="space.class.nonDiscreteSpace" as="xs:string">NonDiscreteSpace</xsl:param>
  <xsl:param name="space.package.space.nonDiscreteSpace" select="fn:concat($space.package.space, '.', $space.class.nonDiscreteSpace)"/>

  <xsl:param name="space.class.nonDiscretePositionData" as="xs:string">NonDiscretePositionData</xsl:param>
  <xsl:param name="space.package.nonDiscretePositionData"
    select="fn:concat($space.package.space.nonDiscreteSpace, '.', $space.class.nonDiscretePositionData)"/>

  <!-- collection -->
  <xsl:param name="collection.class.aORCollection" as="xs:string">AORCollection</xsl:param>
  <xsl:param name="collection.package.aORCollection" select="fn:concat($core.package.util.collection, '.', $collection.class.aORCollection)"/>

  <!-- model -->
  <xsl:param name="core.class.simulationModel" as="xs:string">GeneralSimulationModel</xsl:param>
  <xsl:param name="core.package.simulationModel" select="fn:concat($core.package.root, '.', $core.class.simulationModel)"/>

  <!-- predifined refTypes (path only) -->
  <xsl:param name="util.package.refTypes" as="xs:string" select="fn:concat($core.package.util.refTypes, '.*')"/>

  <!-- created classnames/packages/path -->
  <xsl:param name="sim.package" select="fn:lower-case(aorsml:SimulationScenario/@scenarioName)"/>

  <!-- insert here the package-node for the created simulation -->
  <xsl:param name="sim.package.node" select="$core.package.root"/>

  <!--  insert here the outputpath for the created package -->
  <!-- current: ../../src/ -->
  <!-- xsl:param name="sim.package.root" select="fn:concat('..', $file.separator, '..', $file.separator, 'src', $file.separator)"/-->
  <!-- current: projects/src -->
  <!-- <xsl:param name="sim.package.root" select="fn:concat('projects', $file.separator, 'src')"/> -->
  <xsl:param name="sim.package.root" select="'.'"/>


  <xsl:param name="sim.class.simulatorMain" as="xs:string">Simulator</xsl:param>
  <xsl:param name="sim.class.simParams" as="xs:string">SimParameter</xsl:param>
  <xsl:param name="sim.class.simSpaceModel" as="xs:string">SpaceModel</xsl:param>
  <xsl:param name="sim.class.simGridCell" as="xs:string">SimGridCell</xsl:param>
  <xsl:param name="sim.class.simStatistics" as="xs:string">SimStatistics</xsl:param>
  <xsl:param name="sim.class.simStatistics.Variable">StatisticsVariable</xsl:param>
  <xsl:param name="sim.class.simStatistics.Variable.PropertyIterator">PropertyIterator</xsl:param>
  
  <!-- new -->
  <xsl:param name="sim.class.simStatistics.Variable.PropertyWithObjektIDRefIterator">PropertyWithObjektIDRefIterator</xsl:param>
  
  <xsl:param name="sim.class.simModel">SimModel</xsl:param>
  <xsl:param name="sim.class.simActivityFactory">SimActivityFactory</xsl:param>
  <xsl:param name="sim.class.simGlobal">Global</xsl:param>

  <!--  .: path/package for controller :. -->
  <xsl:param name="sim.package.controller" select="fn:concat($sim.package, if ($sim.package eq '') then '' else '.','controller')"/>
  <xsl:param name="sim.path.controller" select="fn:replace($sim.package.controller, '\.', $file.separator)"/>

  <!-- .: path/package for model :.-->
  <xsl:param name="sim.package.model" select="fn:concat($sim.package,if ($sim.package eq '') then '' else '.','model')"/>
  <xsl:param name="sim.path.model" select="fn:replace($sim.package.model, '\.', $file.separator)"/>
  <!-- .: path/package for model/dataTypes-->
  <xsl:param name="sim.package.model.dataTypes" select="fn:concat($sim.package.model, '.', 'dataTypes')"/>
  <xsl:param name="sim.path.model.dataTypes" select="fn:replace($sim.package.model.dataTypes, '\.', $file.separator)"/>
  <!-- agentsimulator -->
  <xsl:param name="sim.package.model.agentsimulator" select="fn:concat($sim.package.model, '.', 'agtsim')"/>
  <xsl:param name="sim.path.model.agentsimulator" select="fn:replace($sim.package.model.agentsimulator, '\.', $file.separator)"/>
  <!-- environmentsimulator -->
  <xsl:param name="sim.package.model.envsimulator" select="fn:concat($sim.package.model, '.', 'envsim')"/>
  <xsl:param name="sim.path.model.envsimulator" select="fn:replace($sim.package.model.envsimulator, '\.', $file.separator)"/>
  <!-- environmentevent -->
  <xsl:param name="sim.package.model.envevent" select="fn:concat($sim.package.model, '.', 'envevt')"/>
  <xsl:param name="sim.path.model.envevent" select="fn:replace($sim.package.model.envevent, '\.', $file.separator)"/>
  <!-- internalevent -->
  <xsl:param name="sim.package.model.internalevent" select="fn:concat($sim.package.model, '.', 'intevt')"/>
  <xsl:param name="sim.path.model.internalevent" select="fn:replace($sim.package.model.internalevent, '\.', $file.separator)"/>


  <!-- ***  PREFIXES for classnames*** -->

  <!-- for subjectivagent -->
  <!-- NOTICE: change the Classnames of aors.model.agtsim.agt.TrustfulAndSincere$core.class.agentSubject -->
  <!-- NOTICE: prefix must correlate with aors.model.agtsim.json.JsonGenerator method notifyAgentSubject -->
  <xsl:param name="prefix.agentSubject" as="xs:string" select="$core.class.agentSubject"/>

  <!-- for agent-->
  <xsl:param name="prefix.agent" as="xs:string">PhysicalAgentObject</xsl:param>

  <!-- for objekt -->
  <xsl:param name="prefix.objekt" select="''"/>


  <!-- end of params -->

  <!-- list of default imports -->
  <xsl:variable name="defaultImports" as="xs:string*">
    <xsl:value-of select="fn:concat($core.package.util, '.*')"/>
    <xsl:value-of select="$util.package.refTypes"/>
    <xsl:if test="fn:exists(//aorsml:Enumeration) or fn:exists(//aorsml:ComplexDataProperty)">
      <xsl:value-of select="fn:concat($sim.package.model.dataTypes, '.*')"/>
    </xsl:if>
    <xsl:value-of select="fn:concat($sim.package.model.envsimulator, '.*')"/>
    <xsl:value-of select="fn:concat($sim.package.controller, '.*')"/>
    <!-- don't use  java.util.*, because there will be a problem with ambiguous Random (is in java.util and in aor.util)-->
    <xsl:value-of select="'java.util.List'"/>
    <xsl:value-of select="'java.util.ArrayList'"/>
    <xsl:value-of select="'java.util.Arrays'"/>
  </xsl:variable>

  <!-- default physicalObjectType for PhysicalObjectPerceptionEvent -->
  <xsl:param name="defaultPhysObjType">aors.model.envsim.Physical</xsl:param>

  <!-- list of default physObjAttributes -->
  <xsl:variable name="physObjAttrList" as="xs:string*">
    <xsl:value-of>x</xsl:value-of>
    <xsl:value-of>y</xsl:value-of>
    <xsl:value-of>z</xsl:value-of>
    <xsl:value-of>m</xsl:value-of>
    <xsl:value-of>width</xsl:value-of>
    <xsl:value-of>height</xsl:value-of>
    <xsl:value-of>depth</xsl:value-of>
    <xsl:value-of>vx</xsl:value-of>
    <xsl:value-of>vy</xsl:value-of>
    <xsl:value-of>vz</xsl:value-of>
    <xsl:value-of>ax</xsl:value-of>
    <xsl:value-of>ay</xsl:value-of>
    <xsl:value-of>az</xsl:value-of>
    <!-- new -->
    <xsl:value-of>rotX</xsl:value-of>
    <!-- new -->
    <xsl:value-of>rotY</xsl:value-of>
    <!-- new -->
    <xsl:value-of>rotZ</xsl:value-of>
    <!-- new -->
    <xsl:value-of>omegaX</xsl:value-of>
    <!-- new -->
    <xsl:value-of>omegaY</xsl:value-of>
    <!-- new -->
    <xsl:value-of>omegaZ</xsl:value-of>
    <!-- new -->
    <xsl:value-of>alphaX</xsl:value-of>
    <!-- new -->
    <xsl:value-of>alphaY</xsl:value-of>
    <!-- new -->
    <xsl:value-of>alphaZ</xsl:value-of>
    <!-- new -->
    <xsl:value-of>materialType</xsl:value-of>
    <!-- new -->
    <xsl:value-of>shape2D</xsl:value-of>
    <!-- new -->
    <xsl:value-of>shape3D</xsl:value-of>
    <!-- new -->
    <xsl:value-of>points</xsl:value-of>
  </xsl:variable>
  <xsl:variable name="physObjPattern" select="concat('^(', string-join($physObjAttrList, '|'), ')$')"/>

  <xsl:variable name="physAgentObjAttrList" as="xs:string*">
    <xsl:value-of>perceptionRadius</xsl:value-of>
  </xsl:variable>
  <xsl:variable name="physAgtPattern" select="concat('^(', string-join($physObjAttrList, '|'), '|', string-join($physAgentObjAttrList, '|'), ')$')"/>

  <!-- final variable names -->
  <!-- if you change here someone, please change it in the info.aors.GeneralSimulationParameters.java too -->
  <xsl:param name="final.simulationSteps">SIMULATION_STEPS</xsl:param>
  <xsl:param name="final.stepDuration">STEP_DURATION</xsl:param>
  <xsl:param name="final.timeUnit">TIME_UNIT</xsl:param>
  <xsl:param name="final.stepTimeDelay">STEP_TIME_DELAY</xsl:param>
  <xsl:param name="final.pseudoRandomSeed">RANDOM_SEED</xsl:param>
  <xsl:param name="final.randomOrderAgentSimulation">Random_Order_Agent_Simulation</xsl:param>

  <!-- if you change here someone, please change it in the info.aors.GeneralSimulationModel.java too -->
  <xsl:param name="final.modelName">MODEL_NAME</xsl:param>
  <xsl:param name="final.modelTitle">MODEL_TITLE</xsl:param>
  <xsl:param name="final.autoKinematics">AUTO_KINEMATICS</xsl:param>
  <xsl:param name="final.autoGravitation">AUTO_GRAVITATION</xsl:param>
  <xsl:param name="final.autoImpulse">AUTO_IMPULSE</xsl:param>
  <xsl:param name="final.autoCollision">AUTO_COLLISION</xsl:param>
  <xsl:param name="final.baseURI">BASE_URI</xsl:param>

  <!-- symbol for an empty string quotation -->
  <xsl:param name="empty.string.quotation.symbol">*</xsl:param>

  <xsl:param name="space.ORDINATEBASE" as="xs:integer">1</xsl:param>
  
  <!-- new -->
  <xsl:param name="createdVariablesNamePrefix" select="'__'"/>

</xsl:stylesheet>
