<?xml version="1.0" encoding="UTF-8"?>
<!--
  This transformation creates classes for ComplexDataType - Enumeration based on a given aorsml file.
  
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

  <!--creates referenceDataType-class-->
  <xsl:template match="aorsl:ComplexDataType" mode="createComplexDataTypes.createComplexDataType">
    <xsl:param name="indent" select="0" as="xs:integer"/>

    <xsl:variable name="className" select="jw:upperWord(@name)"/>

    <xsl:call-template name="aorsl:classFile">
      <xsl:with-param name="path" select="$sim.path.model.dataTypes"/>
      <xsl:with-param name="name" select="$className"/>

      <xsl:with-param name="content">

        <xsl:call-template name="java:imports">
          <xsl:with-param name="importList" as="xs:string*">
            <xsl:value-of select="$util.package.refTypes"/>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="java:class">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="name" select="$className"/>
          <xsl:with-param name="extends" select="if (fn:exists(@superType)) then @superType else ''"/>
          <xsl:with-param name="content">

            <xsl:choose>
              
              <!-- for class definitions -->
              <xsl:when test="fn:exists(aorsl:ClassDef)">
                <xsl:value-of select="aorsl:ClassDef[@language eq $output.language]"/>
              </xsl:when>
              <xsl:otherwise>

                <!-- set SelfbeliefAttributes as classvariables -->
                <xsl:apply-templates select="aorsl:Attribute | aorsl:ComplexDataProperty | aorsl:ReferenceProperty | aorsl:EnumerationProperty"
                  mode="assistents.classVariable">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                </xsl:apply-templates>
                <xsl:call-template name="java:newLine"/>

                <!-- constructors -->
                <xsl:apply-templates select="." mode="createComplexDataTypes.createComplexDataType.constructors">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="className" select="$className"/>
                </xsl:apply-templates>

                <!-- getter -->
                <xsl:apply-templates select="aorsl:Attribute | aorsl:ComplexDataProperty | aorsl:ReferenceProperty | aorsl:EnumerationProperty"
                  mode="assistents.getVariableMethod">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                </xsl:apply-templates>

                <!-- getter -->
                <xsl:apply-templates select="aorsl:Attribute | aorsl:ComplexDataProperty | aorsl:ReferenceProperty | aorsl:EnumerationProperty"
                  mode="assistents.setVariableMethod">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                </xsl:apply-templates>

                <!-- functions -->
                <xsl:apply-templates select="aorsl:Function" mode="shared.createFunction">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                </xsl:apply-templates>
              </xsl:otherwise>
            </xsl:choose>

          </xsl:with-param>
        </xsl:call-template>


      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="aorsl:ComplexDataType" mode="createComplexDataTypes.createComplexDataType.constructors">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="className" required="yes" as="xs:string"/>

    <!-- default -->
    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="$className"/>
      <xsl:with-param name="parameters" as="xs:string*">

       <!--<xsl:apply-templates select="." mode="createComplexDataTypes.createComplexDataType.constructors.allAttributes"/>-->
      </xsl:with-param>
      <xsl:with-param name="content">
        
        <xsl:value-of select="aorsl:DefaultConstructor/aorsl:Def[@language eq $output.language]"/>

       <!-- <xsl:for-each select="aorsl:Attribute | aorsl:ComplexDataProperty | aorsl:ReferenceProperty | aorsl:EnumerationProperty">
          <xsl:call-template name="java:variable">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="name">
              <xsl:call-template name="java:varByDotNotation">
                <xsl:with-param name="varName" select="@name"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="value" select="jw:lowerWord(@name)"/>
          </xsl:call-template>
        </xsl:for-each> -->

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!--<xsl:template match="aorsl:ComplexDataType" mode="createComplexDataTypes.createComplexDataType.constructors.allAttributes">
    <xsl:choose>
      <xsl:when test="fn:exists(@superType)">
        <xsl:for-each
          select="aorsl:Attribute | aorsl:ReferenceProperty | aorsl:ComplexDataProperty | aorsl:EnumerationProperty | aorsl:BeliefAttribute">

          <xsl:choose>
            <xsl:when test="@upperMultiplicity eq 'unbounded'">
              <xsl:message>unbounded upperMultiplicity is not yet implemented</xsl:message>
            </xsl:when>
            <xsl:otherwise>

              <xsl:call-template name="java:createParam">
                <xsl:with-param name="type" select="jw:mappeDataType(@type)"/>
                <xsl:with-param name="name" select="jw:lowerWord(@name)"/>
              </xsl:call-template>
            </xsl:otherwise>

          </xsl:choose>
        </xsl:for-each>
        <xsl:apply-templates select="//aorsl:DataTypes/aorsl:ComplexDataType[@name = current()/@superType]"
          mode="createComplexDataTypes.createComplexDataType.constructors.allAttributes"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:for-each
          select="aorsl:Attribute | aorsl:ReferenceProperty | aorsl:ComplexDataProperty | aorsl:EnumerationProperty | aorsl:BeliefAttribute">

          <xsl:choose>
            <xsl:when test="@upperMultiplicity eq 'unbounded'">
              <xsl:call-template name="java:createParam">
                <xsl:with-param name="type" select="fn:concat('List&lt;', jw:upperWord(@type), '&gt;')"/>
                <xsl:with-param name="name" select="@name"/>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>

              <xsl:call-template name="java:createParam">
                <xsl:with-param name="type" select="jw:mappeDataType(@type)"/>
                <xsl:with-param name="name" select="jw:lowerWord(@name)"/>
              </xsl:call-template>
            </xsl:otherwise>
          </xsl:choose>

        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template> -->

</xsl:stylesheet>
