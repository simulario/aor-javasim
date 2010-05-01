<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation create a class with Parameters based on a given aorsml file.

        $Rev$
        $Date$

        @author:   Jens Werner (jens.werner@tu-cottbus.de)
        @license:  GNU General Public License version 2 or higher
        @last changed by $Author$
-->

<xsl:stylesheet version="2.0" xmlns:aorsml="http://aor-simulation.org"
  xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:java="http://www.sun.com/java"
  xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:schemaLocation="http://aor-simulation.org aorsml.xsd"
  xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">

  <!--*****-->
  <!--class-->
  <!--*****-->
  <xsl:template name="createSimulationParameter">
    <xsl:apply-templates select="aorsml:SimulationScenario"
      mode="createSimulationParameter.createSimulationParameter">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="aorsml:SimulationScenario"
    mode="createSimulationParameter.createSimulationParameter">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="aorsml:classFile">
      <xsl:with-param name="path" select="$sim.path.controller"/>
      <xsl:with-param name="name" select="$sim.class.simParams"/>

      <xsl:with-param name="content">

        <xsl:call-template name="java:imports">
          <xsl:with-param name="importList" as="xs:string*">
            <xsl:value-of select="$core.package.simulationParameters"/>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="java:class">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="annotation">
            <xsl:call-template name="getAnnotationSuppressWarnings.serial"/>
          </xsl:with-param>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="name" select="$sim.class.simParams"/>
          <xsl:with-param name="extends" select="$core.class.simulationParameters"/>
          <xsl:with-param name="content">

            <xsl:apply-templates select="aorsml:SimulationParameters"
              mode="createSimulationParameter.setParameter">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <xsl:apply-templates select="aorsml:SimulationParameters/aorsml:SimulationParameter"
              mode="createSimulationParameter.setParameter">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- default parameter -->
  <xsl:template match="aorsml:SimulationParameters" mode="createSimulationParameter.setParameter">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <!-- simulationSteps(required) -->
    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="static" select="true()"/>
      <xsl:with-param name="final" select="true()"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'long'"/>
      <xsl:with-param name="name" select="$final.simulationSteps"/>
      <xsl:with-param name="value" select="@simulationSteps"/>
    </xsl:call-template>
    <xsl:call-template name="java:newLine"/>

    <!--  stepDuration(optional; default 1) -->
    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="static" select="true()"/>
      <xsl:with-param name="final" select="true()"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'long'"/>
      <xsl:with-param name="name" select="$final.stepDuration"/>
      <xsl:with-param name="value">
        <xsl:choose>
          <xsl:when test="fn:exists(@stepDuration)">
            <xsl:value-of select="@stepDuration"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'1'"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="java:newLine"/>

    <!--  timeUnit(optional; default s) -->
    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="static" select="true()"/>
      <xsl:with-param name="final" select="true()"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'TimeUnit'"/>
      <xsl:with-param name="name" select="$final.timeUnit"/>
      <xsl:with-param name="value">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="name" select="'TimeUnit'"/>
          <xsl:with-param name="varName">
            <xsl:choose>
              <xsl:when test="fn:exists(@timeUnit)">
                <xsl:value-of select="@timeUnit"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="'s'"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

    <!--  stepTimeDelay(optional) -->
    <xsl:if test="fn:exists(@stepTimeDelay)">
      <xsl:call-template name="java:newLine"/>
      <xsl:call-template name="java:variable">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="static" select="true()"/>
        <xsl:with-param name="final" select="true()"/>
        <xsl:with-param name="modifier" select="'public'"/>
        <xsl:with-param name="type" select="'long'"/>
        <xsl:with-param name="name" select="$final.stepTimeDelay"/>
        <xsl:with-param name="value" select="@stepTimeDelay"/>
      </xsl:call-template>
    </xsl:if>

    <!--  randomSeed(optional) -->
    <xsl:if test="fn:exists(@randomSeed)">
      <xsl:call-template name="java:newLine"/>
      <xsl:call-template name="java:variable">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="static" select="true()"/>
        <xsl:with-param name="final" select="true()"/>
        <xsl:with-param name="modifier" select="'public'"/>
        <xsl:with-param name="type" select="'int'"/>
        <xsl:with-param name="name" select="$final.pseudoRandomSeed"/>
        <xsl:with-param name="value" select="@randomSeed"/>
      </xsl:call-template>
    </xsl:if>

    <!--  randomOrderAgentSimulation(optional); default true -->
    <xsl:call-template name="java:newLine"/>
    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="static" select="true()"/>
      <xsl:with-param name="final" select="true()"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'boolean'"/>
      <xsl:with-param name="name" select="$final.randomOrderAgentSimulation"/>
      <xsl:with-param name="value">
        <xsl:choose>
          <xsl:when test="fn:exists(@randomOrderAgentSimulation)">
            <xsl:value-of select="@randomOrderAgentSimulation"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'true'"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="aorsml:SimPar" mode="createSimulationParameter.setParameter">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:newLine"/>

    <xsl:variable name="type"
      select="//aorsml:SimulationModel/aorsml:SimulationParameterDeclaration[@name = current()/@parameter]/@type"/>
    <xsl:if test="$type != ''">
      <xsl:call-template name="java:variable">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="static" select="true()"/>
        <xsl:with-param name="final" select="true()"/>
        <xsl:with-param name="modifier" select="'public'"/>
        <xsl:with-param name="type" select="jw:mappeDataType($type)"/>
        <xsl:with-param name="name" select="@parameter"/>
        <xsl:with-param name="value" select="@value"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
