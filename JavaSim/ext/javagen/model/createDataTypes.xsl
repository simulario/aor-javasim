<?xml version="1.0" encoding="UTF-8"?>
<!--
  This transformation creates DataTypes based on a given aorsml file.
  
  $Rev$
  $Date$
  
  @author:   Jens Werner (jens.werner@tu-cottbus.de)
  @license:  GNU General Public License version 2 or higher
  @last changed by $Author$
-->

<xsl:stylesheet version="2.0" xmlns:aorsl="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsi:schemaLocation="http://aor-simulation.org aorsml.xsd">

  <xsl:import href="dataTypes/createEnumeration.xsl"/>
  <xsl:import href="dataTypes/createComplexDataType.xsl"/>

  <xsl:template name="createDataTypes">
    <xsl:apply-templates select="aorsl:SimulationScenario/aorsl:SimulationModel/aorsl:DataTypes/aorsl:Enumeration"
      mode="createEnumerations.createEnumeration"/>
    <xsl:apply-templates select="aorsl:SimulationScenario/aorsl:SimulationModel/aorsl:DataTypes/aorsl:ComplexDataType"
      mode="createComplexDataTypes.createComplexDataType"/>
  </xsl:template>

</xsl:stylesheet>
