<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation creates classes for  time events based on a given aorsml file.

        $Rev: 2642 $
        $Date: 2009-04-22 17:52:13 +0200 (Wed, 22 Apr 2009) $

        @author:   Jens Werner (jens.werner@tu-cottbus.de)
        @license:  GNU General Public License version 2 or higher
        @last changed by $Author: jewerner $
-->

<xsl:stylesheet version="2.0" xmlns:aorsml="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:java="http://www.sun.com/java"
  xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:schemaLocation="http://aor-simulation.org aorsml.xsd" xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">

  <xsl:template match="aorsml:TimeEventType" mode="createTimeEvents.createTimeEvent">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:class">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="jw:upperWord(@name)"/>
      <xsl:with-param name="extends" select="if (fn:exists(@superType)) then @superType else $core.class.timeEvent"/>
      <xsl:with-param name="content">

        <xsl:apply-templates select="aorsml:Attribute" mode="assistents.classVariable">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>
        <xsl:call-template name="java:newLine"/>

        <!-- create constructor -->
        <xsl:apply-templates select="." mode="createTimeEvents.constructor">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>
        
        <!-- setter -->
        <xsl:apply-templates select="aorsml:Attribute" mode="assistents.setVariableMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>
        
        <!-- getter -->
        <xsl:apply-templates select="aorsml:Attribute" mode="assistents.getVariableMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>
        
        <!-- functions -->
        <xsl:apply-templates select="aorsml:Function" mode="shared.createFunction">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- constructors -->
  <!-- creates constructor -->
  <xsl:template match="aorsml:TimeEventType" mode="createTimeEvents.constructor">
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
      <xsl:with-param name="name" select="@name"/>
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

</xsl:stylesheet>
