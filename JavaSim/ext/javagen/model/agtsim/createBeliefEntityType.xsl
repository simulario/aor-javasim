<?xml version="1.0" encoding="UTF-8"?>

<!--
      This transformation creates classes for BeliefEntityTypes based on a given aorsml file.

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

  <!--creates class-->
  <xsl:template match="aorsl:BeliefEntityType" mode="createBeliefEntityTypes.createBeliefEntityType">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:class">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="@name"/>
      <xsl:with-param name="extends" select="$core.class.entity"/>
      <xsl:with-param name="content">

        <!-- classVariables (from aorsl:BeliefAttribute and aorsl:BeliefReferenceProperty) -->
        <xsl:apply-templates select="aorsl:BeliefAttribute | aorsl:BeliefReferenceProperty" mode="assistents.classVariable">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>
        <xsl:call-template name="java:newLine"/>

        <!-- constructors with all classVariables and only with ID -->
        <xsl:apply-templates select="." mode="createBeliefEntityTypes.constructor">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>
        <xsl:call-template name="java:newLine"/>

        <!-- setters -->
        <xsl:for-each select="aorsl:BeliefAttribute | aorsl:BeliefReferenceProperty">
          <xsl:apply-templates select="." mode="assistents.setVariableMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="changeCheck" select="true()"/>
          </xsl:apply-templates>
        </xsl:for-each>

        <!-- getters -->
        <xsl:apply-templates select="aorsl:BeliefAttribute | aorsl:BeliefReferenceProperty" mode="assistents.getVariableMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- creates constructors -->
  <xsl:template match="aorsl:BeliefEntityType" mode="createBeliefEntityTypes.constructor">
    <xsl:param name="indent" required="yes"/>
    
    <!-- create constructor only with ID parameter -->
    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="@name"/>
      <xsl:with-param name="parameters" as="xs:string">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'long'"/>
          <xsl:with-param name="name" select="'id'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">   
        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string" select="'id'"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

    <!-- create constructor with parameters all properties of the belief -->
    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="@name"/>
      <xsl:with-param name="parameters" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'long'"/>
          <xsl:with-param name="name" select="'id'"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'String'"/>
          <xsl:with-param name="name" select="'name'"/>
        </xsl:call-template>

        <xsl:apply-templates select="." mode="assistents.constructor.allAttributes"/>

      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string*" select="('id', 'name')"/>
        </xsl:call-template>
        <xsl:call-template name="java:newLine"/>

        <!-- set all attributvalues from constructor -->
        <xsl:for-each select="aorsl:BeliefAttribute | aorsl:BeliefReferenceProperty">

          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="objInstance" select="'this'"/>
            <xsl:with-param name="instVariable" select="@name"/>
            <xsl:with-param name="value" select="@name"/>
          </xsl:call-template>

        </xsl:for-each>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

</xsl:transform>
