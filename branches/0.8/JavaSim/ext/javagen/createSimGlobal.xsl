<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation creates a class with global (static) variables and functions.

        $Rev$
        $Date$

        @author:   Jens Werner (jens.werner@tu-cottbus.de)
        @license:  GNU General Public License version 2 or higher
        @last changed by $Author$
-->

<xsl:stylesheet version="2.0" xmlns:aorsl="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsi:schemaLocation="http://aor-simulation.org aorsml.xsd"
  xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">

  <!--*****-->
  <!--class-->
  <!--*****-->
  <xsl:template name="createSimulationGlobal">
    <xsl:apply-templates select="aorsl:SimulationScenario/aorsl:SimulationModel/aorsl:Globals" mode="createSimulationGlobal.createSimulationGlobal">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="aorsl:Globals" mode="createSimulationGlobal.createSimulationGlobal">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="aorsl:classFile">
      <xsl:with-param name="path" select="$sim.path.controller"/>
      <xsl:with-param name="name" select="$sim.class.simGlobal"/>

      <xsl:with-param name="content">

        <xsl:call-template name="java:imports">
          <xsl:with-param name="importList" as="xs:string*">

            <xsl:call-template name="setDefaultJavaImports"/>

          </xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="java:class">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="name" select="$sim.class.simGlobal"/>
          <xsl:with-param name="content">

            <!-- set GlobalVariables as public static variables -->
            <xsl:apply-templates select="aorsl:GlobalVariable" mode="createSimulationGlobal.classVariable">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>
            <xsl:call-template name="java:newLine"/>

            <!-- static setter -->
            <xsl:apply-templates select="aorsl:GlobalVariable[not(@upperMultiplicity = 'unbounded')]" mode="assistents.setVariableMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="static" select="true()"/>
              <xsl:with-param name="staticClassName" select="$sim.class.simGlobal"/>
            </xsl:apply-templates>

            <!-- static getter -->
            <xsl:apply-templates select="aorsl:GlobalVariable" mode="assistents.getVariableMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="static" select="true()"/>
              <xsl:with-param name="staticClassName" select="$sim.class.simGlobal"/>
            </xsl:apply-templates>

            <!-- create GlobalFunctions -->
            <xsl:apply-templates select="aorsl:GlobalFunction" mode="shared.createFunction">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="static" select="true()"/>
            </xsl:apply-templates>

          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>

    </xsl:call-template>
  </xsl:template>


  <!--creates class variables-->
  <xsl:template match="aorsl:GlobalVariable" mode="createSimulationGlobal.classVariable">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:choose>
      <xsl:when test="@upperMultiplicity eq 'unbounded'">

        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="static" select="true()"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="type" select="fn:concat('java.util.List&lt;', jw:mappeDataType(@dataType | @refDataType), '&gt;')"/>
          <xsl:with-param name="name" select="@name"/>
          <xsl:with-param name="value">
            
            <xsl:call-template name="java:newObject">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="class" select="'java.util.ArrayList'"/>
              <xsl:with-param name="generic" select="jw:mappeDataType(@dataType | @refDataType)"/>
              <xsl:with-param name="onlyInitialization" select="true()"/>
            </xsl:call-template>
            
          </xsl:with-param>
        </xsl:call-template>   
        
      </xsl:when>
      <xsl:otherwise>

        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="static" select="true()"/>
          <xsl:with-param name="modifier" select="'public'"/>
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
