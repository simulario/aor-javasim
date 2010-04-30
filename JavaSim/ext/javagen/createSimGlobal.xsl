<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation creates a class with global (static) variables and functions.

        $Rev: 4870 $
        $Date: 2010-04-23 14:27:26 +0200 (Fri, 23 Apr 2010) $

        @author:   Jens Werner (jens.werner@tu-cottbus.de)
        @license:  GNU General Public License version 2 or higher
        @last changed by $Author: jewerner $
-->

<xsl:stylesheet version="2.0" xmlns:aorsml="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsi:schemaLocation="http://aor-simulation.org aorsml.xsd"
  xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">

  <!--*****-->
  <!--class-->
  <!--*****-->
  <xsl:template name="createSimulationGlobal">
    <xsl:apply-templates select="aorsml:SimulationScenario/aorsml:SimulationModel/aorsml:Globals" mode="createSimulationGlobal.createSimulationGlobal">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="aorsml:Globals" mode="createSimulationGlobal.createSimulationGlobal">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="aorsml:classFile">
      <xsl:with-param name="path" select="$sim.path.controller"/>
      <xsl:with-param name="name" select="$sim.class.simGlobal"/>

      <xsl:with-param name="content">

        <xsl:call-template name="java:imports">
          <xsl:with-param name="importList" as="xs:string*">

            <xsl:value-of select="fn:concat($core.package.util, '.*')"/>
            <xsl:value-of select="$util.package.refTypes"/>
            <xsl:if test="fn:exists(//aorsml:Enumeration) or fn:exists(//aorsml:ComplexDataProperty)">
              <xsl:value-of select="fn:concat($sim.package.model.dataTypes, '.*')"/>
            </xsl:if>

          </xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="java:class">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="name" select="$sim.class.simGlobal"/>
          <xsl:with-param name="content">

            <!-- set GlobalVariables as public static variables -->
            <xsl:apply-templates select="aorsml:GlobalVariable" mode="createSimulationGlobal.classVariable">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>
            <xsl:call-template name="java:newLine"/>

            <!-- static setter -->
            <xsl:apply-templates select="aorsml:GlobalVariable" mode="assistents.setVariableMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="static" select="true()"/>
              <xsl:with-param name="staticClassName" select="$sim.class.simGlobal"/>
            </xsl:apply-templates>

            <!-- static getter -->
            <xsl:apply-templates select="aorsml:GlobalVariable" mode="assistents.getVariableMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="static" select="true()"/>
              <xsl:with-param name="staticClassName" select="$sim.class.simGlobal"/>
            </xsl:apply-templates>

            <!-- create GlobalFunctions -->
            <xsl:apply-templates select="aorsml:GlobalFunction" mode="shared.createFunction">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="static" select="true()"/>
            </xsl:apply-templates>

          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>

    </xsl:call-template>
  </xsl:template>


  <!--creates class variables-->
  <xsl:template match="aorsml:GlobalVariable" mode="createSimulationGlobal.classVariable">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:choose>
      <xsl:when test="@upperMultiplicity eq 'unbounded'">
        <!-- currently unused -->
      </xsl:when>
      <xsl:otherwise>

        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="static" select="true()"/>
          <xsl:with-param name="modifier" select="'private'"/>
          <xsl:with-param name="type">
            <xsl:choose>
              <xsl:when test="@dataType">
                <xsl:value-of select="jw:mappeDataType(@dataType)"/>
              </xsl:when>
              <xsl:when test="@refDataType">
                <xsl:value-of select="@refDataType"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:message>
                  <xsl:text>[ERROR] No type in GlobalVariable </xsl:text>
                  <xsl:value-of select="@name"/>
                  <xsl:text> defined.</xsl:text>
                </xsl:message>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
          <xsl:with-param name="name" select="@name"/>
        </xsl:call-template>

      </xsl:otherwise>

    </xsl:choose>

  </xsl:template>

</xsl:stylesheet>
