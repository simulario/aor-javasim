<!--
    This transformation creates environment Rules based on a given aorsml file.
    
    $Rev$
    $Date$
    
    @author:   Jens Werner (jens.werner@tu-cottbus.de)
    @license:  GNU General Public License version 2 or higher
    @last changed by $Author$
-->

<xsl:transform version="2.0" xmlns:aorsl="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:java="http://www.sun.com/java"
  xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:schemaLocation="http://aor-simulation.org aorsml.xsd">

  <xsl:import href="envsim/createEnvRules.xsl"/>

  <xsl:template name="createEnvironmentRules">
    <xsl:apply-templates select="aorsl:SimulationScenario/aorsl:SimulationModel/aorsl:EnvironmentRules/aorsl:EnvironmentRule"
      mode="createEnvironmentRules.createEnvRule">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

</xsl:transform>
