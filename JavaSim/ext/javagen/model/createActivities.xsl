<!--
  This transformation creates Activities based on a given aorsml file.
  
  $Rev$
  $Date$
  
  @author:   Jens Werner (jens.werner@tu-cottbus.de)
  @license:  GNU General Public License version 2 or higher
  @last changed by $Author$
-->

<xsl:transform version="2.0" xmlns:aorsml="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsi:schemaLocation="http://aor-simulation.org aorsml.xsd">

  <xsl:import href="envsim/createActivities.xsl"/>

  <xsl:template name="createActivities">
   <!--  <xsl:message>Please stand by, activities are under re-construction now!</xsl:message> -->
    
    <xsl:apply-templates select="aorsml:SimulationScenario/aorsml:SimulationModel/aorsml:EntityTypes/aorsml:ActivityType"
      mode="createActivties.createActivity">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

</xsl:transform>
