<?xml version="1.0" encoding="UTF-8"?>

<!--
        This transformation creates classes for caused events based on a given aorsml file.

    
        $Rev$
        $Date$
    
        @author:   Jens Werner (jens.werner@tu-cottbus.de)
        @license:  GNU General Public License version 2 or higher
        @last changed by $Author$
-->

<xsl:stylesheet version="2.0" xmlns:aorsml="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsi:schemaLocation="http://aor-simulation.org aorsml.xsd"
  xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">

  <xsl:template match="aorsml:CausedEventType" mode="createCausedEvents.createEvents">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="aorsml:classFile">
      <xsl:with-param name="path" select="$sim.path.model.envevent"/>
      <xsl:with-param name="name" select="@name"/>

      <xsl:with-param name="content">

        <xsl:call-template name="java:imports">
          <xsl:with-param name="importList" as="xs:string*">
            <xsl:value-of select="$core.package.causedEvent"/>
            <xsl:value-of select="fn:concat($sim.package.controller, '.', $sim.class.simulatorMain)"/>

            <xsl:call-template name="setDefaultJavaImports"/>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="aorsml:class">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="name" select="jw:upperWord(@name)"/>
          <xsl:with-param name="extends" select="if (fn:exists(@superType)) then @superType else $core.class.causedEvent"/>
          <xsl:with-param name="content">

            <!-- classvariables -->
            <xsl:apply-templates select="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:EnumerationProperty | aorsml:ComplexDataProperty"
              mode="assistents.classVariable">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>
            <xsl:call-template name="java:newLine"/>

            <!-- constructors -->
            <xsl:apply-templates select="." mode="createCausedEvent.constructors">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- setters -->
            <xsl:apply-templates select="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:EnumerationProperty | aorsml:ComplexDataProperty"
              mode="assistents.setVariableMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- getters -->
            <xsl:apply-templates select="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:EnumerationProperty | aorsml:ComplexDataProperty"
              mode="assistents.getVariableMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- functions -->
            <xsl:apply-templates select="aorsml:Function" mode="shared.createFunction">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- constructors -->
  <xsl:template match="aorsml:CausedEventType" mode="createCausedEvent.constructors">
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
        <xsl:apply-templates select="aorsml:Attribute[fn:exists(@initialValue)]" mode="assistents.setInitialAttributeValue">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="asClassVariable" select="true()"/>
        </xsl:apply-templates>
      </xsl:with-param>
    </xsl:call-template>

    <!-- constructor  (id, occurrenceTime)-->
    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="jw:upperWord(@name)"/>
      <xsl:with-param name="parameters" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'long'"/>
          <xsl:with-param name="name" select="'id'"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'long'"/>
          <xsl:with-param name="name" select="'occurrenceTime'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">
        <!--super-->
        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string*">
            <xsl:value-of select="'id'"/>
            <xsl:value-of select="'occurrenceTime'"/>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:apply-templates select="aorsml:Attribute[fn:exists(@initialValue)]" mode="assistents.setInitialAttributeValue">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="asClassVariable" select="true()"/>
        </xsl:apply-templates>
      </xsl:with-param>
    </xsl:call-template>

    <!-- constructor  (occurrenceTime)-->
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
        <!--super-->
        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string*">
            <xsl:value-of select="'occurrenceTime'"/>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:apply-templates select="aorsml:Attribute[fn:exists(@initialValue)]" mode="assistents.setInitialAttributeValue">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="asClassVariable" select="true()"/>
        </xsl:apply-templates>
      </xsl:with-param>
    </xsl:call-template>

    <!-- constructor  (id, name, occurrenceTime)-->
    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="jw:upperWord(@name)"/>
      <xsl:with-param name="parameters" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'long'"/>
          <xsl:with-param name="name" select="'id'"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'String'"/>
          <xsl:with-param name="name" select="'name'"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'long'"/>
          <xsl:with-param name="name" select="'occurrenceTime'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">
        <!--super-->
        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string*">
            <xsl:value-of select="'id'"/>
            <xsl:value-of select="'name'"/>
            <xsl:value-of select="'occurrenceTime'"/>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:apply-templates select="aorsml:Attribute[fn:exists(@initialValue)]" mode="assistents.setInitialAttributeValue">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="asClassVariable" select="true()"/>
        </xsl:apply-templates>
      </xsl:with-param>
    </xsl:call-template>

    <!-- constructor  (name, occurrenceTime)-->
    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="jw:upperWord(@name)"/>
      <xsl:with-param name="parameters" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'String'"/>
          <xsl:with-param name="name" select="'name'"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'long'"/>
          <xsl:with-param name="name" select="'occurrenceTime'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">
        <!--super-->
        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string*">
            <xsl:value-of select="'name'"/>
            <xsl:value-of select="'occurrenceTime'"/>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:apply-templates select="aorsml:Attribute[fn:exists(@initialValue)]" mode="assistents.setInitialAttributeValue">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="asClassVariable" select="true()"/>
        </xsl:apply-templates>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

</xsl:stylesheet>
