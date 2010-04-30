<?xml version="1.0" encoding="UTF-8"?>
<!--
        This transformation creates java code for the aorsml simulator v2

        $Rev: 4634 $
        $Date: 2010-03-22 11:21:58 +0100 (Mon, 22 Mar 2010) $

        @author:   Jens Werner (jens.werner@tu-cottbus.de)
        @license:  GNU General Public License version 2 or higher
        @last changed by $Author: jewerner $
-->

<xsl:stylesheet version="2.0" xmlns:aorsml="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:schemaLocation="http://aor-simulation.org aorsml.xsd">

  <!-- default imports -->
  <xsl:import href="custom.xsl"/>
  <xsl:import href="PLManager.xsl"/>
  <xsl:import href="assistents.xsl"/>
  <xsl:import href="shared.xsl"/>

  <!-- controller -->
  <xsl:import href="createSimSystem.xsl"/>
  
  <!-- simparams -->
  <xsl:import href="createSimParameters.xsl"/>
  
  <!-- simSpacemodel -->
  <xsl:import href="createSimSpacemodel.xsl"/>
  
  <!-- simStatistics -->
  <xsl:import href="createSimStatistic.xsl"/>
  
  <!-- simModel -->
  <xsl:import href="createSimModel.xsl"/>
  
  <!-- simActivityFactory -->
  <xsl:import href="createSimActivityFactory.xsl"/>
  
  <!-- simGlobal -->
  <xsl:import href="createSimGlobal.xsl"/>

  <!-- model/envevt -->
  <xsl:import href=" model/createEnvironmentEvents.xsl"/>

  <!-- model/envsim -->
  <xsl:import href="model/createEnvironmentRules.xsl"/>
  <xsl:import href="model/createObjects.xsl"/>
  <xsl:import href="model/createMessages.xsl"/>
  <xsl:import href="model/createActivities.xsl"/>
  
  <!-- model/agtsim -->
  <xsl:import href="model/createAgentSubjects.xsl"/>
  
  <!-- model/dataTypes -->
  <xsl:import href="model/createDataTypes.xsl"/>
  
  <xsl:output method="text"/>

  <xsl:template match="*"/>

  <xsl:template match="/">
    
    <!-- it is used to desid which getRandomPosition() - version is called in shared.helper.initAORObjects  -->
    <xsl:variable name="discreteSpace" as="xs:boolean">
      <xsl:choose>
        <xsl:when test="ends-with(local-name(//aorsml:SimulationModel/aorsml:SpaceModel/aorsml:*), 'Grid')">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="false()"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!--creates simulation system class  -->
    <xsl:call-template name="createSimSystem">
      <xsl:with-param name="discreteSpace" select="$discreteSpace" tunnel="yes"/>
    </xsl:call-template>
    
    <!-- creates the simparams -->
    <xsl:call-template name="createSimulationParameter"/>
    
    <!-- creates the simspacemodel -->
    <xsl:call-template name="createSimulationSpacemodel"/>
    
    <!-- creates the simStatistics -->
    <xsl:call-template name="createStatistic"/>
    
    <!-- creates the simModel -->
    <xsl:call-template name="createSimulationModel"/>
    
    <!-- creates the simActivityFactory -->
    <xsl:call-template name="createSimActivityFactory"/>
    
    <!-- create the simGlobals -->
    <xsl:call-template name="createSimulationGlobal"/>

    <xsl:call-template name="createEnvironmentEvents"/>
    
    <xsl:call-template name="createActivities"/>

    <xsl:call-template name="createEnvironmentRules">
      <xsl:with-param name="discreteSpace" select="$discreteSpace" tunnel="yes"/>
    </xsl:call-template> 
    <xsl:call-template name="createPhysicalAgents"/>
    <xsl:call-template name="createAgents"/>
    <xsl:call-template name="createPhysicalObjects"/>
    <xsl:call-template name="createObjects"/>
    <xsl:call-template name="createMessages"/>
    <xsl:call-template name="createDataTypes"/>
    
    <xsl:call-template name="createPhysicalAgentSubjects"/>
    
    <!-- not uptodate -->
    <xsl:call-template name="createAgentSubjects"/>
    
  </xsl:template>

</xsl:stylesheet>
