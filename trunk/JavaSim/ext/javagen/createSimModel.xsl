<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation creates a class with ModelInformations.

        $Rev$
        $Date$

        @author:   Jens Werner (jens.werner@tu-cottbus.de)
        @license:  GNU General Public License version 2 or higher
        @last changed by $Author$
-->

<xsl:transform version="2.0" xmlns:aorsl="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsi:schemaLocation="http://aor-simulation.org aorsml.xsd"
  xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">

  <!--*****-->
  <!--class-->
  <!--*****-->
  <xsl:template name="createSimulationModel">
    <xsl:apply-templates select="aorsl:SimulationScenario/aorsl:SimulationModel" mode="createSimulationModel.createSimulationModel">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="aorsl:SimulationModel" mode="createSimulationModel.createSimulationModel">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="aorsl:classFile">
      <xsl:with-param name="path" select="$sim.path.controller"/>
      <xsl:with-param name="name" select="$sim.class.simModel"/>

      <xsl:with-param name="content">

        <xsl:call-template name="java:imports">
          <xsl:with-param name="importList" as="xs:string*">
            <xsl:value-of select="$core.package.simulationModel"/>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="java:class">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="name" select="$sim.class.simModel"/>
          <xsl:with-param name="extends" select="$core.class.simulationModel"/>
          <xsl:with-param name="content">

            <xsl:apply-templates select="." mode="createSimulationModel.setParameter">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>

    </xsl:call-template>
  </xsl:template>


  <xsl:template match="aorsl:SimulationModel" mode="createSimulationModel.setParameter">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <!-- modelName(required) -->
    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="static" select="true()"/>
      <xsl:with-param name="final" select="true()"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'String'"/>
      <xsl:with-param name="name" select="$final.modelName"/>
      <xsl:with-param name="value" select="@modelName"/>
    </xsl:call-template>
    <xsl:call-template name="java:newLine"/>

    <!-- modelTitle(optional) -->
    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="static" select="true()"/>
      <xsl:with-param name="final" select="true()"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'String'"/>
      <xsl:with-param name="name" select="$final.modelTitle"/>
      <xsl:with-param name="value" select="if (fn:exists(@modelTitle)) then @modelTitle else $empty.string.quotation.symbol"/>
    </xsl:call-template>
    <xsl:call-template name="java:newLine"/>

    <!--  baseURI(optional) -->
    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="static" select="true()"/>
      <xsl:with-param name="final" select="true()"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'String'"/>
      <xsl:with-param name="name" select="$final.baseURI"/>
      <xsl:with-param name="value" select="if (fn:exists(@baseURI)) then @baseURI else $empty.string.quotation.symbol"/>
    </xsl:call-template>

  </xsl:template>

  <!-- set the modelparameter in the createSimModel() in Simulation.java see createSimSystem.xsl -->
  <xsl:template match="aorsl:SimulationModel" mode="createSimulationModel.helper.method.createSimModel">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:variable name="simModelVarName" select="$sim.class.simModel"/>

    <!-- modelName(required) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance" select="$simModelVarName"/>
      <xsl:with-param name="method" select="'setModelParameter'"/>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:value-of select="jw:quote($final.modelName)"/>
        <xsl:value-of select="jw:quote(@modelName)"/>
      </xsl:with-param>
    </xsl:call-template>

    <!-- modelTitle(optional) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance" select="$simModelVarName"/>
      <xsl:with-param name="method" select="'setModelParameter'"/>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:value-of select="jw:quote($final.modelTitle)"/>
        <xsl:value-of select="jw:quote(if (fn:exists(@modelTitle)) then @modelTitle else $empty.string.quotation.symbol)"/>
      </xsl:with-param>
    </xsl:call-template>


    <!--  autoKinematics(optional; default false) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance" select="$simModelVarName"/>
      <xsl:with-param name="method" select="'setModelParameter'"/>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:value-of select="jw:quote($final.autoKinematics)"/>
        <xsl:choose>
          <xsl:when test="fn:exists(@autoKinematics)">
            <xsl:value-of select="jw:quote(@autoKinematics)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="jw:quote('false')"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
    </xsl:call-template>

    <!--  autoGravitation(optional; default false) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance" select="$simModelVarName"/>
      <xsl:with-param name="method" select="'setModelParameter'"/>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:value-of select="jw:quote($final.autoGravitation)"/>
        <xsl:choose>
          <xsl:when test="fn:exists(@autoGravitation)">
            <xsl:value-of select="jw:quote(@autoGravitation)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="jw:quote('false')"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
    </xsl:call-template>

    <!--  autoImpulse(optional; default false) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance" select="$simModelVarName"/>
      <xsl:with-param name="method" select="'setModelParameter'"/>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:value-of select="jw:quote($final.autoImpulse)"/>
        <xsl:choose>
          <xsl:when test="fn:exists(@autoImpulse)">
            <xsl:value-of select="jw:quote(@autoImpulse)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="jw:quote('false')"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
    </xsl:call-template>

    <!--  autoCollision(optional; default false) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance" select="$simModelVarName"/>
      <xsl:with-param name="method" select="'setModelParameter'"/>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:value-of select="jw:quote($final.autoCollision)"/>
        <xsl:choose>
          <xsl:when test="fn:exists(@autoCollision)">
            <xsl:value-of select="jw:quote(@autoCollision)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="jw:quote('false')"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
    </xsl:call-template>

    <!--  baseURI(optional) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance" select="$simModelVarName"/>
      <xsl:with-param name="method" select="'setModelParameter'"/>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:value-of select="jw:quote($final.baseURI)"/>
        <xsl:value-of select="jw:quote(if (fn:exists(@baseURI)) then @baseURI else $empty.string.quotation.symbol)"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

</xsl:transform>
