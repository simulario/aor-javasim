<?xml version="1.0" encoding="UTF-8"?>

<!--
        This transformation creates environment Events based on a given aorsml file.
        
        $Rev$
        $Date$

        @author:   Jens Werner (jens.werner@tu-cottbus.de)
        @license:  GNU General Public License version 2 or higher
        @last changed by $Author$
-->

<xsl:stylesheet version="2.0" xmlns:aorsml="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:java="http://www.sun.com/java"
  xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:schemaLocation="http://aor-simulation.org aorsml.xsd">

  <xsl:import href="envevt/createCausedEvents.xsl"/>
  <xsl:import href="envevt/createPerceptionEvents.xsl"/>
  <xsl:import href="envevt/createExogenousEvents.xsl"/>
  <xsl:import href="envevt/createActionEvents.xsl"/>

  <xsl:template name="createEnvironmentEvents">
    <xsl:call-template name="createCausedEvents"/>
    <xsl:call-template name="createPerceptionEvents"/>
    <xsl:call-template name="createExogenousEvents"/>
    <xsl:call-template name="createActionEvents"/>
  </xsl:template>


  <!-- caused events -->
  <xsl:template name="createCausedEvents">
    <xsl:apply-templates select="aorsml:SimulationScenario/aorsml:SimulationModel/aorsml:EntityTypes/aorsml:CausedEventType[@name != $core.class.stopSimulationEvent.alias]"
      mode="createCausedEvents.createEvents">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

  <!-- perceptionEvents -->
  <xsl:template name="createPerceptionEvents">
    <xsl:apply-templates select="aorsml:SimulationScenario/aorsml:SimulationModel/aorsml:EntityTypes/aorsml:PerceptionEventType"
      mode="createPerceptionEvents.createEvents">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

  <!-- exogeneous events -->
  <xsl:template name="createExogenousEvents">
    <xsl:apply-templates select="aorsml:SimulationScenario/aorsml:SimulationModel/aorsml:EntityTypes/aorsml:ExogenousEventType"
      mode="createExogenousEvents.createEvents">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

  <!-- action events -->
  <xsl:template name="createActionEvents">
    <xsl:apply-templates select="aorsml:SimulationScenario/aorsml:SimulationModel/aorsml:EntityTypes/aorsml:ActionEventType"
      mode="createActionEvent.createActionEvent">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

</xsl:stylesheet>
