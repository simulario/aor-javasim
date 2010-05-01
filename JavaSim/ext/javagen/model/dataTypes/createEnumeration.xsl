<?xml version="1.0" encoding="UTF-8"?>
<!--
  This transformation creates classes for ComplexDataType - Enumeration based on a given aorsml file.
  
  $Rev$
  $Date$
  
  @author:   Jens Werner (jens.werner@tu-cottbus.de)
  @license:  GNU General Public License version 2 or higher
  @last changed by $Author$
-->

<xsl:stylesheet version="2.0" xmlns:aorsml="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsi:schemaLocation="http://aor-simulation.org aorsml.xsd"
  xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">

  <!--creates enumeration-class-->
  <xsl:template match="aorsml:Enumeration " mode="createEnumerations.createEnumeration">
    <xsl:param name="indent" select="0" as="xs:integer"/>

    <xsl:call-template name="aorsml:classFile">
      <xsl:with-param name="path" select="$sim.path.model.dataTypes"/>
      <xsl:with-param name="name" select="@name"/>

      <xsl:with-param name="content">

<!--        <xsl:call-template name="java:imports">
          <xsl:with-param name="importList" as="xs:string*"> </xsl:with-param>
        </xsl:call-template> -->

        <xsl:call-template name="java:enum">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="name" select="@name"/>
          <xsl:with-param name="enumerationLiteral" as="xs:string*" select="aorsml:EnumerationLiteral"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

</xsl:stylesheet>
