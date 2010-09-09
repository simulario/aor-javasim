<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation creates classes for periodic time events based on a given aorsml file.

        $Rev$
        $Date$

        @author:   Jens Werner (jens.werner@tu-cottbus.de)
        @last changed by $Author$
-->

<xsl:transform version="2.0" xmlns:aorsl="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsi:schemaLocation="http://aor-simulation.org aorsml.xsd"
  xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">

  <xsl:template match="aorsl:PeriodicTimeEventType" mode="createPeriodicTimeEvents.createPeriodicTimeEvent">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:class">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="jw:upperWord(@name)"/>
      <xsl:with-param name="extends" select="if (fn:exists(@superType)) then @superType else $core.class.periodicTimeEvent"/>
      <xsl:with-param name="content">

        <xsl:apply-templates select="aorsl:Attribute" mode="assistents.classVariable">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>
        <xsl:call-template name="java:newLine"/>

        <!-- create constructor -->
        <xsl:apply-templates select="." mode="createPeriodicTimeEvents.constructor">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <!-- Periodicity -->
        <xsl:apply-templates select="." mode="createPeriodicTimeEvents.method.periodicity">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <!-- StopCondition -->
        <xsl:apply-templates select="." mode="createPeriodicTimeEvents.method.stopCondition">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <!-- setter -->
        <xsl:apply-templates select="aorsl:Attribute" mode="assistents.setVariableMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <!-- getter -->
        <xsl:apply-templates select="aorsl:Attribute" mode="assistents.getVariableMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <!-- functions -->
        <xsl:apply-templates select="aorsl:Function" mode="shared.createFunction">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- constructors -->
  <!-- creates constructor -->
  <xsl:template match="aorsl:PeriodicTimeEventType" mode="createPeriodicTimeEvents.constructor">
    <xsl:param name="indent" required="yes"/>

    <!-- empty constructor -->
    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="jw:upperWord(@name)"/>
      <xsl:with-param name="content">
        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

    <!--  occurrenceTime -->
    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="jw:upperWord(@name)"/>
      <xsl:with-param name="parameters" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'long'"/>
          <xsl:with-param name="name" select="'occurrenceTime'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">
        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string*">
            <xsl:value-of select="'occurrenceTime'"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!--**************-->
  <!--   methods    -->
  <!--**************-->

  <!-- periodicity() -->
  <xsl:template match="aorsl:PeriodicTimeEventType" mode="createPeriodicTimeEvents.method.periodicity">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'long'"/>
      <xsl:with-param name="name" select="'periodicity'"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">
            <xsl:apply-templates select="." mode="assistents.periodicity"/>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- stopCondition() -->
  <xsl:template match="aorsl:PeriodicTimeEventType" mode="createPeriodicTimeEvents.method.stopCondition">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'boolean'"/>
      <xsl:with-param name="name" select="'stopCondition'"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">
            <xsl:choose>
              <xsl:when test="fn:exists(aorsl:StopCondition[@language = $output.language])">
                <xsl:value-of select="aorsl:StopCondition[@language = $output.language][1]"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="'false'"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

</xsl:transform>
