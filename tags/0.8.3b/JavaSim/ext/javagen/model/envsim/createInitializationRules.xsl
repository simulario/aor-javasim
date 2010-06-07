<?xml version="1.0" encoding="UTF-8"?>

<!--
  This transformation creates classes for initialization rules based on a given aorsml file.
  
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

  <xsl:template match="aorsml:InitializationRule" mode="createInitializationRule.createInitializationRules">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:class">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="jw:upperWord(@name)"/>
      <xsl:with-param name="extends" select="$core.class.initializationRule"/>

      <xsl:with-param name="content">

        <!-- set involved entitys as classvariable -->
        <xsl:for-each select="aorsml:FOR[@objectVariable]">

          <xsl:variable name="objectType">
            <xsl:apply-templates select="." mode="assistents.getVariableType"/>
          </xsl:variable>
          <xsl:if test="$objectType != ''">
            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="modifier" select="'private'"/>
              <xsl:with-param name="type" select="$objectType"/>
              <xsl:with-param name="name" select="@objectVariable"/>
            </xsl:call-template>
          </xsl:if>
        </xsl:for-each>

        <!-- set the DataVariableDeclaration as classvaraibles -->
        <xsl:apply-templates select="aorsml:FOR[@dataVariable]" mode="assistents.setDataVariableDeclarationClassVariables">
          <xsl:with-param name="indent" select="$indent"/>
        </xsl:apply-templates>
        <xsl:call-template name="java:newLine"/>

        <!--constructor -->
        <xsl:apply-templates select="." mode="createInitializationRule.constructor">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <!-- conditions -->
        <xsl:apply-templates select="." mode="createInitializationRule.method.conditions">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <!-- execute -->
        <xsl:apply-templates select="." mode="createInitializationRule.method.execute">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <!-- stateEffects -->
        <xsl:apply-templates select="." mode="createInitializationRule.method.stateEffects">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>


      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>


  <!-- constructor -->
  <xsl:template match="aorsml:InitializationRule" mode="createInitializationRule.constructor">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="@name"/>
      <xsl:with-param name="parameters" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'String'"/>
          <xsl:with-param name="name" select="'name'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string" select="'name'"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>


  </xsl:template>

  <!-- conditions() -->
  <xsl:template match="aorsml:InitializationRule" mode="createInitializationRule.method.conditions">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="type" select="'boolean'"/>
      <xsl:with-param name="name" select="'condition'"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">
            <!-- check if DataVariableDeclarations with refDataTypes isn't null -->
            <xsl:if test="fn:exists(aorsml:FOR[@dataVariable][@refDataType])">
              <xsl:value-of select="'('"/>
              <xsl:apply-templates select="aorsml:FOR[@dataVariable][@refDataType]" mode="assistents.dataVariableDeclarationcheckNull"/>
              <xsl:value-of select="') &amp;&amp; '"/>
            </xsl:if>
            <xsl:choose>
              <xsl:when
                test="fn:exists(aorsml:IF[@language = $output.language]) and fn:normalize-space(aorsml:IF[@language = $output.language]) != ''">
                <xsl:value-of select="fn:normalize-space(aorsml:IF[@language = $output.language])"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="'true'"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- execute() -->
  <xsl:template match="aorsml:InitializationRule" mode="createInitializationRule.method.execute">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="annotation">
        <xsl:if test="$suppressWarnings and aorsml:FOR[@objectVariable]/@objectType = 'Collection'">
          <xsl:call-template name="getAnnotationSuppressWarnings.unchecked"/>
        </xsl:if>
      </xsl:with-param>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="'execute'"/>
      <xsl:with-param name="content">

        <xsl:apply-templates select="." mode="assistents.createRule.createEnvInitRuleExec">
          <xsl:with-param name="indent" select="$indent"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- stateEffects() -->
  <xsl:template match="aorsml:InitializationRule" mode="createInitializationRule.method.stateEffects">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'stateEffects'"/>
      <xsl:with-param name="content">

        <!-- achieve the order -->
        <xsl:apply-templates select="aorsml:UpdateObject | aorsml:UpdateGridCell | aorsml:UpdateGridCells"
          mode="createEnvironmentRules.method.stateEffects.content">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="spaceReservationSystem" select="true()" tunnel="yes"/>
        </xsl:apply-templates>

        <!--sets state effects-->
        <xsl:apply-templates select="aorsml:UpdateObjects" mode="createEnvironmentRules.helper.method.stateEffects">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

</xsl:stylesheet>
