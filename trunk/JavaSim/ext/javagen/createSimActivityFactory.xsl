<?xml version="1.0" encoding="UTF-8"?>
<!--
  This transformation create a class SimActivityFactory based on a given aorsml file.
  
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
  <xsl:template name="createSimActivityFactory">
    <xsl:apply-templates select="aorsl:SimulationScenario/aorsl:SimulationModel/aorsl:EntityTypes"
      mode="createSimActivityFactory.createSimActivityFactory">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="aorsl:EntityTypes" mode="createSimActivityFactory.createSimActivityFactory">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:if test="fn:exists(aorsl:ActivityType)">

      <xsl:call-template name="aorsl:classFile">
        <xsl:with-param name="path" select="$sim.path.controller"/>
        <xsl:with-param name="name" select="$sim.class.simActivityFactory"/>


        <xsl:with-param name="content">

          <xsl:call-template name="java:imports">
            <xsl:with-param name="importList" as="xs:string*">
              <xsl:value-of select="fn:concat($core.package.model.envEvent.activity, '.*')"/>
              <xsl:value-of select="$activity.package.abstractActivityFactory"/>
              <xsl:value-of select="fn:concat($sim.package.model.envsimulator, '.*')"/>
              <xsl:value-of select="$core.package.environmentSimulator"/>
              <xsl:value-of select="'java.util.ArrayList'"/>
            </xsl:with-param>
          </xsl:call-template>

          <xsl:call-template name="java:class">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="modifier" select="'public'"/>
            <xsl:with-param name="name" select="$sim.class.simActivityFactory"/>
            <xsl:with-param name="extends" select="$activity.class.abstractActivityFactory"/>
            <xsl:with-param name="content">

              <!-- getActivities() -->
              <xsl:apply-templates select="." mode="createSimActivityFactory.method.getActivities">
                <xsl:with-param name="indent" select="$indent + 1"/>
              </xsl:apply-templates>

              <!-- getActivityByTypw() -->
              <xsl:apply-templates select="." mode="createSimActivityFactory.method.getActivityByType">
                <xsl:with-param name="indent" select="$indent + 1"/>
              </xsl:apply-templates>

            </xsl:with-param>
          </xsl:call-template>

        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>

  </xsl:template>

  <!-- getActivities() -->
  <xsl:template match="aorsl:EntityTypes" mode="createSimActivityFactory.method.getActivities">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:variable name="envEventNameVarName" select="'envEventSimpleName'"/>
    <xsl:variable name="envSimName" select="jw:lowerWord($core.class.environmentSimulator)"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'ArrayList'"/>
      <xsl:with-param name="genericType" select="$activity.class.abstractActivity"/>
      <xsl:with-param name="name" select="'getActivities'"/>
      <xsl:with-param name="parameterList" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'String'"/>
          <xsl:with-param name="name" select="$envEventNameVarName"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="$core.class.environmentSimulator"/>
          <xsl:with-param name="name" select="$envSimName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:variable name="listVarName" select="'result'"/>
        <xsl:call-template name="java:newArrayListObject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="generic" select="$activity.class.abstractActivity"/>
          <xsl:with-param name="name" select="$listVarName"/>
        </xsl:call-template>

        <xsl:for-each-group select="aorsl:ActivityType[@startEventType]" group-by="@startEventType">

          <xsl:call-template name="java:if">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="condition">
              <xsl:call-template name="java:callMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="objInstance" select="$envEventNameVarName"/>
                <xsl:with-param name="method" select="'equals'"/>
                <xsl:with-param name="args" select="jw:quote(current-grouping-key())"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="thenContent">
              <xsl:for-each select="current-group()">
                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="indent" select="$indent + 2"/>
                  <xsl:with-param name="objInstance" select="$listVarName"/>
                  <xsl:with-param name="method" select="'add'"/>
                  <xsl:with-param name="args">
                    <xsl:call-template name="java:newObject">
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="class" select="jw:upperWord(@name)"/>
                      <xsl:with-param name="args" select="$envSimName"/>
                      <xsl:with-param name="isVariable" select="true()"/>
                    </xsl:call-template>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:for-each>
            </xsl:with-param>
          </xsl:call-template>
          <xsl:if test="position() &lt; last()">
            <xsl:call-template name="java:codeLine">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="semicolon" select="false()"/>
              <xsl:with-param name="content" select="'else'"/>
            </xsl:call-template>
          </xsl:if>

        </xsl:for-each-group>

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value" select="$listVarName"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- getActivityByType -->
  <xsl:template match="aorsl:EntityTypes" mode="createSimActivityFactory.method.getActivityByType">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:variable name="activityType" select="'activityTypeName'"/>
    <xsl:variable name="envSimName" select="jw:lowerWord($core.class.environmentSimulator)"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="$activity.class.abstractActivity"/>
      <xsl:with-param name="name" select="'getActivityByType'"/>
      <xsl:with-param name="parameterList" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'String'"/>
          <xsl:with-param name="name" select="$activityType"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="$core.class.environmentSimulator"/>
          <xsl:with-param name="name" select="$envSimName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:for-each select="aorsl:ActivityType">

          <xsl:call-template name="java:if">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="condition">
              <xsl:call-template name="java:callMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="objInstance" select="$activityType"/>
                <xsl:with-param name="method" select="'equals'"/>
                <xsl:with-param name="args" select="jw:quote(@name)"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="thenContent">
              <xsl:call-template name="java:return">
                <xsl:with-param name="indent" select="$indent + 2"/>
                <xsl:with-param name="value">

                  <xsl:call-template name="java:newObject">
                    <xsl:with-param name="inLine" select="true()"/>
                    <xsl:with-param name="class" select="jw:upperWord(@name)"/>
                    <xsl:with-param name="args" select="$envSimName"/>
                    <xsl:with-param name="isVariable" select="true()"/>
                  </xsl:call-template>

                </xsl:with-param>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>

          <xsl:if test="position() &lt; last()">
            <xsl:call-template name="java:codeLine">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="semicolon" select="false()"/>
              <xsl:with-param name="content" select="'else'"/>
            </xsl:call-template>
          </xsl:if>

        </xsl:for-each>

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value" select="'null'"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

</xsl:stylesheet>
