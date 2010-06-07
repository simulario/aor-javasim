<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation creates classes for outMessages based on a given aorsml file.
	
	$Rev$
	$Date$
	
	@author:   Jens Werner (jens.werner@tu-cottbus.de)
	@license:  GNU General Public License version 2 or higher
	@last changed by $Author$
-->

<xsl:stylesheet version="2.0" xmlns:aorsml="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:java="http://www.sun.com/java"
  xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:schemaLocation="http://aor-simulation.org aorsml.xsd">

  <!--creates class-->
  <xsl:template match="aorsml:OutMessageEventType" mode="createOutMessages.createOutMessage">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="aorsml:classFile">
      <xsl:with-param name="path" select="$sim.path.model.envevent"/>
      <xsl:with-param name="name" select="@name"/>

      <xsl:with-param name="content">

        <xsl:call-template name="java:imports">
          <xsl:with-param name="importList" as="xs:string*">
            <xsl:value-of select="$core.package.outMessageEvent"/>
            <xsl:value-of select="$core.package.message"/>
            <xsl:value-of select="fn:concat($sim.package.controller, '.', $sim.class.simulatorMain)"/>
            
            <xsl:call-template name="setDefaultJavaImports"/>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="java:class">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="name" select="@name"/>
          <xsl:with-param name="extends" select="if (fn:exists(@superType)) then @superType else $core.class.outMessageEvent"/>
          <xsl:with-param name="content">

            <!-- constructors -->
            <xsl:apply-templates select="." mode="createOutMessages.constructors">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- constructors -->
  <xsl:template match="aorsml:OutMessageEventType" mode="createOutMessages.constructors">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <!-- empty constructor -->
    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="@name"/>
      <xsl:with-param name="content">
        <!--super-->
        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

    <!-- occurrenceTime, receiver, senderId, message -->
    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="@name"/>
      <xsl:with-param name="parameters" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'long'"/>
          <xsl:with-param name="name" select="'occurrenceTime'"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'long'"/>
          <xsl:with-param name="name" select="'receiver'"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'long'"/>
          <xsl:with-param name="name" select="'senderId'"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="$core.class.message"/>
          <xsl:with-param name="name" select="'message'"/>
        </xsl:call-template>

      </xsl:with-param>
      <xsl:with-param name="content">
        <!--super-->
        <!--super-->
        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string*">
            <xsl:value-of select="'occurrenceTime'"/>
            <xsl:value-of select="'receiver'"/>
            <xsl:value-of select="'senderId'"/>
            <xsl:value-of select="'message'"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>
</xsl:stylesheet>
