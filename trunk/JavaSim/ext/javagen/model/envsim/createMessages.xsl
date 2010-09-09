<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation creates classes for messages based on a given aorsml file.
	
	$Rev$
	$Date$
	
	@author:   Jens Werner (jens.werner@tu-cottbus.de)
	@license:  GNU General Public License version 2 or higher
	@last changed by $Author$
-->

<xsl:stylesheet version="2.0" xmlns:aorsl="http://aor-simulation.org"
  xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:java="http://www.sun.com/java"
  xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:schemaLocation="http://aor-simulation.org aorsml.xsd"
  xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">

  <!--creates class-->
  <xsl:template match="aorsl:MessageType" mode="createMessages.createMessage">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="aorsl:classFile">
      <xsl:with-param name="path" select="$sim.path.model.envsimulator"/>
      <xsl:with-param name="name" select="jw:upperWord(@name)"/>

      <xsl:with-param name="content">

        <xsl:call-template name="java:imports">
          <xsl:with-param name="importList" as="xs:string*">
            <xsl:value-of select="$core.package.message"/>

            <xsl:value-of select="fn:concat($core.package.model.envSim.msg,'.*')"/>
            <xsl:if test="fn:exists(aorsl:Function)">
              <xsl:value-of select="$util.package.refTypes"/>
            </xsl:if>

            <xsl:value-of select="fn:concat($sim.package.controller, '.', $sim.class.simulatorMain)"/>

            <xsl:call-template name="setDefaultJavaImports"/>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="java:class">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="name" select="jw:upperWord(@name)"/>
          <xsl:with-param name="extends"
            select="if (fn:exists(@superType)) then @superType else $core.class.message"/>
          <xsl:with-param name="content">

            <!-- classvariables -->
            <xsl:apply-templates
              select="aorsl:Attribute |  aorsl:ReferenceProperty | 
              aorsl:EnumerationProperty | aorsl:ComplexDataProperty  | 
              aorsl:GlobalVariable"
              mode="assistents.classVariable">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>
            <xsl:call-template name="java:newLine"/>

            <!-- constructors -->
            <xsl:apply-templates select="." mode="createMessages.constructors">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- setters -->
            <xsl:apply-templates
              select="aorsl:Attribute |  aorsl:ReferenceProperty | 
              aorsl:EnumerationProperty | aorsl:ComplexDataProperty  | 
              aorsl:GlobalVariable"
              mode="assistents.setVariableMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- getters -->
            <xsl:apply-templates
              select="aorsl:Attribute |  aorsl:ReferenceProperty | 
              aorsl:EnumerationProperty | aorsl:ComplexDataProperty  | 
              aorsl:GlobalVariable"
              mode="assistents.getVariableMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- functions -->
            <xsl:apply-templates select="aorsl:Function" mode="shared.createFunction">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- constructors -->
  <xsl:template match="aorsl:MessageType" mode="createMessages.constructors">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <!-- empty constructor -->
    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="jw:upperWord(@name)"/>
      <xsl:with-param name="content">
        <!--super-->
        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:call-template>
        <xsl:apply-templates select="aorsl:Attribute[fn:exists(@initialValue)]"
          mode="assistents.setInitialAttributeValue">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="asClassVariable" select="true()"/>
        </xsl:apply-templates>
      </xsl:with-param>
    </xsl:call-template>

    <!-- id constructor -->
    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="jw:upperWord(@name)"/>
      <xsl:with-param name="parameters">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'long'"/>
          <xsl:with-param name="name" select="'idRef'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">
        <!--super-->
        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" select="'idRef'"/>
        </xsl:call-template>
        <xsl:apply-templates select="aorsl:Attribute[fn:exists(@initialValue)]"
          mode="assistents.setInitialAttributeValue">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="asClassVariable" select="true()"/>
        </xsl:apply-templates>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>
</xsl:stylesheet>
