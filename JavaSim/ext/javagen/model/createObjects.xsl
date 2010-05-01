<!--
    This transformation creates PhysicalObjects  based on a given aorsml file.
    
    $Rev$
    $Date$
    
    @author:   Jens Werner (jens.werner@tu-cottbus.de)
    @license:  GNU General Public License version 2 or higher
    @last changed by $Author$
-->

<xsl:transform version="2.0" xmlns:aorsml="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsi:schemaLocation="http://aor-simulation.org aorsml.xsd">

  <xsl:import href="envsim/createPhysicalAgents.xsl"/>
  <xsl:import href="envsim/createPhysicalObjects.xsl"/>
  <xsl:import href="envsim/createAgents.xsl"/>
  <xsl:import href="envsim/createObjects.xsl"/>

  <xsl:template name="createPhysicalAgents">
    <xsl:apply-templates select="aorsml:SimulationScenario/aorsml:SimulationModel/aorsml:EntityTypes/aorsml:PhysicalAgentType"
      mode="createAgents.createAgent">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template name="createPhysicalObjects">
    <xsl:apply-templates select="aorsml:SimulationScenario/aorsml:SimulationModel/aorsml:EntityTypes/aorsml:PhysicalObjectType"
      mode="createPhysicalObjects.createPhysicalObject">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>
  
  <xsl:template name="createAgents">
    <xsl:apply-templates select="aorsml:SimulationScenario/aorsml:SimulationModel/aorsml:EntityTypes/aorsml:AgentType"
      mode="createAgents.createAgent">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template name="createObjects">
    <xsl:apply-templates select="aorsml:SimulationScenario/aorsml:SimulationModel/aorsml:EntityTypes/aorsml:ObjectType"
      mode="createObjects.createObject">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

</xsl:transform>
