<!--
    This transformation creates PhysicalObjects  based on a given aorsml file.
    
    $Rev: 4836 $
    $Date: 2010-04-21 19:40:03 +0200 (Wed, 21 Apr 2010) $
    
    @author:   Jens Werner (jens.werner@tu-cottbus.de)
    @license:  GNU General Public License version 2 or higher
    @last changed by $Author: jewerner $
-->

<xsl:transform version="2.0" xmlns:aorsml="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsi:schemaLocation="http://aor-simulation.org aorsml.xsd">

  <xsl:import href="agtsim/createPhysicalAgentSubject.xsl"/>
  <xsl:import href="agtsim/createReactionRules.xsl"/>
  <xsl:import href="agtsim/createAgentSubject.xsl"/>
  <xsl:import href="agtsim/createCommunicationRules.xsl"/>
  <xsl:import href="agtsim/createActualPerceptionRules.xsl"/>
  <xsl:import href="agtsim/createBeliefEntityType.xsl"/>

  <xsl:import href="intevt/createActualPerceptionEvents.xsl"/>
  <xsl:import href="intevt/createPeriodicTimeEvents.xsl"/>

  <xsl:template name="createPhysicalAgentSubjects">
    <xsl:apply-templates select="aorsml:SimulationScenario/aorsml:SimulationModel/aorsml:EntityTypes/aorsml:PhysicalAgentType"
      mode="createPhysicalAgentSubjects.createPhysicalAgentSubject">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template name="createAgentSubjects">
    <xsl:apply-templates select="aorsml:SimulationScenario/aorsml:SimulationModel/aorsml:EntityTypes/aorsml:AgentType"
      mode="createAgentSubjects.createAgentSubject">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

</xsl:transform>
