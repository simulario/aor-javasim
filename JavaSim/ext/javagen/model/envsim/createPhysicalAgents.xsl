<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation creates classes for physicalagentobjects based on a given aorsml file.

        $Rev: 4386 $
        $Date: 2010-03-03 12:38:53 +0100 (Wed, 03 Mar 2010) $

        @author:  Jens Werner (jens.werner@tu-cottbus.de)
        @last changed by $Author: jewerner $
-->

<xsl:stylesheet version="2.0" xmlns:aorsml="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsi:schemaLocation="http://aor-simulation.org aorsml.xsd"
  xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">

  <!--creates class-->
  <xsl:template match="aorsml:PhysicalAgentType" mode="createAgents.createAgent">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:variable name="className" select="jw:upperWord(@name)"/>
    <xsl:variable name="isInSimpleTwoDimensionalGrid" as="xs:boolean">
      <xsl:choose>
        <xsl:when test="(//aorsml:SpaceModel/@dimensions eq '2') and (//aorsml:SpaceModel/@discrete eq 'true')">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <xsl:when test="fn:exists(//aorsml:SpaceModel/aorsml:TwoDimensionalGrid)">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="false()"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:call-template name="aorsml:classFile">
      <xsl:with-param name="path" select="$sim.path.model.envsimulator"/>
      <xsl:with-param name="name" select="$className"/>

      <xsl:with-param name="content">

        <xsl:call-template name="java:imports">
          <xsl:with-param name="importList" as="xs:string*">
            <xsl:value-of select="$core.package.physAgentObject"/>

            <xsl:if test="fn:exists(aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty | aorsml:EnumerationProperty)">
              <xsl:value-of select="'java.beans.PropertyChangeEvent'"/>
            </xsl:if>

            <xsl:value-of select="fn:concat($core.package.model.envSim.agt, '.*')"/>

            <xsl:call-template name="setDefaultJavaImports"/>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="java:class">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="name" select="$className"/>
          <xsl:with-param name="extends" select="if (fn:exists(@superType)) then @superType else $core.class.physAgentObject"/>
          <xsl:with-param name="content">

            <!-- classVariables -->
            <xsl:apply-templates
              select="aorsml:Attribute[not(matches(lower-case(@name), $physObjPattern))] | aorsml:ReferenceProperty | aorsml:ComplexDataProperty | aorsml:EnumerationProperty"
              mode="assistents.classVariable">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>
            <xsl:call-template name="java:newLine"/>

            <!-- set autoPerception (AUTO_PERCEPTION) -->
            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="modifier" select="'public'"/>
              <xsl:with-param name="static" select="true()"/>
              <xsl:with-param name="final" select="true()"/>
              <xsl:with-param name="type" select="'boolean'"/>
              <xsl:with-param name="name" select="'AUTO_PERCEPTION'"/>
              <xsl:with-param name="value">
                <xsl:apply-templates select="." mode="createAgents.isAutoPerception"/>
              </xsl:with-param>
            </xsl:call-template>

            <!-- set idPerceivable (ID_PERCEIVABLE) -->
            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="modifier" select="'public'"/>
              <xsl:with-param name="static" select="true()"/>
              <xsl:with-param name="final" select="true()"/>
              <xsl:with-param name="type" select="'boolean'"/>
              <xsl:with-param name="name" select="'ID_PERCEIVABLE'"/>
              <xsl:with-param name="value">
                <xsl:apply-templates select="." mode="createAgents.isIdPerceivable"/>
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="java:newLine"/>

            <!-- constructor with all classVariables -->
            <xsl:apply-templates select="." mode="createAgents.constructor">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="className" select="$className"/>
              <xsl:with-param name="isInSimpleTwoDimensionalGrid" select="$isInSimpleTwoDimensionalGrid"/>
            </xsl:apply-templates>

            <!-- setters -->
            <xsl:for-each
              select="aorsml:Attribute[not(matches(lower-case(@name), $physObjPattern))] | aorsml:ReferenceProperty | 
                                                   aorsml:ComplexDataProperty | aorsml:EnumerationProperty">
              <xsl:apply-templates select="." mode="assistents.setVariableMethod">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="changeCheck" select="true()"/>
                <xsl:with-param name="extraContent">

                  <xsl:call-template name="java:callMethod">
                    <xsl:with-param name="indent" select="$indent + 3"/>
                    <xsl:with-param name="objInstance">
                      <xsl:call-template name="java:varByDotNotation">
                        <xsl:with-param name="name" select="'this'"/>
                        <xsl:with-param name="varName" select="'propertyChangeSupport'"/>
                      </xsl:call-template>
                    </xsl:with-param>
                    <xsl:with-param name="method" select="'firePropertyChange'"/>
                    <xsl:with-param name="args" as="xs:string*">
                      <xsl:call-template name="java:newObject">
                        <xsl:with-param name="class" select="'PropertyChangeEvent'"/>
                        <xsl:with-param name="args" as="xs:string*">
                          <xsl:value-of select="'this'"/>
                          <xsl:value-of select="jw:quote(@name)"/>
                          <xsl:value-of select="'null'"/>
                          <xsl:call-template name="java:varByDotNotation">
                            <xsl:with-param name="name" select="'this'"/>
                            <xsl:with-param name="varName" select="@name"/>
                          </xsl:call-template>
                        </xsl:with-param>
                        <xsl:with-param name="isVariable" select="true()"/>
                        <xsl:with-param name="inLine" select="true()"/>
                      </xsl:call-template>
                    </xsl:with-param>
                  </xsl:call-template>

                </xsl:with-param>
              </xsl:apply-templates>
            </xsl:for-each>

            <!-- setters for predifined properties with constrain-attributes-->
            <xsl:for-each select="aorsml:Attribute[matches(lower-case(@name), $physObjPattern)][@minValue or @maxValue]">
              <xsl:apply-templates select="." mode="assistents.setVariableMethod.predefinedProps">
                <xsl:with-param name="indent" select="$indent + 1"/>
              </xsl:apply-templates>
            </xsl:for-each>

            <!-- if isInSimpleTwoDimensionalGrid then we set width an height to 1-->
            <xsl:if test="$isInSimpleTwoDimensionalGrid">
              <xsl:call-template name="createDefaultSettersForWidthAndHeight">
                <xsl:with-param name="indent" select="$indent + 1"/>
              </xsl:call-template>
            </xsl:if>

            <!-- getters -->
            <xsl:apply-templates
              select="aorsml:Attribute[not(matches(lower-case(@name), $physObjPattern))] | aorsml:ReferenceProperty | 
                                                                 aorsml:ComplexDataProperty | aorsml:EnumerationProperty"
              mode="assistents.getVariableMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- get(int index), remove(int index), remove(Object o), add(Object o) for Properties with @upperMultiplicity eq 'unbounded' -->
            <xsl:apply-templates
              select="aorsml:Attribute[@upperMultiplicity eq 'unbounded'] | 
                             aorsml:ReferenceProperty[@upperMultiplicity eq 'unbounded'] | 
                             aorsml:ComplexDataProperty[@upperMultiplicity eq 'unbounded']"
              mode="assistents.listMethods">
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

  <!-- creates constructor -->
  <xsl:template match="aorsml:PhysicalAgentType" mode="createAgents.constructor">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="className" required="yes" as="xs:string"/>
    <xsl:param name="isInSimpleTwoDimensionalGrid" as="xs:boolean" select="false()"/>

    <!-- create a private constructor with no parameter that is used by different modules to get default instances -->
    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="$className"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="content">
        <xsl:if test="$isInSimpleTwoDimensionalGrid">
          <xsl:call-template name="setWidthAndHeightTo1">
            <xsl:with-param name="indent" select="$indent + 1"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:with-param>
    </xsl:call-template>

    <!-- TODO: delete following if is not necessary to have a constructor without the name-param -->
    <!-- creates the constructor with all parameters
    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="$className"/>
      <xsl:with-param name="parameters" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'long'"/>
          <xsl:with-param name="name" select="'id'"/>
        </xsl:call-template>
        <xsl:apply-templates select="." mode="assistents.constructor.allAttributes"/>
      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string*">
            <xsl:value-of select="'id'"/>
            <xsl:apply-templates select="." mode="assistents.constructor.allSuperAttributes"/>
          </xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="java:newLine"/>

        <xsl:for-each select="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty | aorsml:EnumerationProperty">

          <xsl:if test="@upperMultiplicity eq 'unbounded'">
            <xsl:apply-templates select="." mode="assistent.initList">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>
          </xsl:if>

          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="objInstance" select="'this'"/>
            <xsl:with-param name="instVariable" select="@name"/>
            <xsl:with-param name="value" select="@name"/>
          </xsl:call-template>

          <xsl:apply-templates select="." mode="assistents.setInheritedProperty">
            <xsl:with-param name="indent" select="$indent + 1"/>
          </xsl:apply-templates>

        </xsl:for-each>

      </xsl:with-param>
    </xsl:call-template> -->

    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="$className"/>
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
          <xsl:with-param name="paramList" as="xs:string*">
            <xsl:value-of select="'id'"/>
            <xsl:value-of select="'name'"/>
            <xsl:apply-templates select="." mode="assistents.constructor.allSuperAttributes"/>
          </xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="java:newLine"/>

        <!-- set all attributvalues from constructor -->
        <xsl:for-each select="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty | aorsml:EnumerationProperty">

          <!-- init the lists for upperMultiplicity -->
          <xsl:if test="@upperMultiplicity eq 'unbounded'">
            <xsl:apply-templates select="." mode="assistent.initList">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>
          </xsl:if>

          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="objInstance" select="'this'"/>
            <xsl:with-param name="instVariable" select="@name"/>
            <xsl:with-param name="value" select="@name"/>
          </xsl:call-template>

          <xsl:apply-templates select="." mode="assistents.setInheritedProperty">
            <xsl:with-param name="indent" select="$indent + 1"/>
          </xsl:apply-templates>

        </xsl:for-each>
        
        <xsl:if test="$isInSimpleTwoDimensionalGrid">
          <xsl:call-template name="java:newLine"/>
          <xsl:call-template name="setWidthAndHeightTo1">
            <xsl:with-param name="indent" select="$indent + 1"/>
          </xsl:call-template>
        </xsl:if>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- set the @autoPerception from PhysicalAgentType or from superType -->
  <xsl:template match="aorsml:PhysicalAgentType" mode="createAgents.isAutoPerception">
    <xsl:choose>
      <xsl:when test="fn:exists(@autoPerception)">
        <xsl:value-of select="@autoPerception"/>
      </xsl:when>
      <xsl:when test="fn:exists(@superType)">
        <xsl:apply-templates select="../aorsml:PhysicalAgentType[@name eq current()/@superType]" mode="createAgents.isAutoPerception"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="'false'"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <!-- set the @idPerceivable from PhysicalAgentType or from superType -->
  <xsl:template match="aorsml:PhysicalAgentType" mode="createAgents.isIdPerceivable">
    <xsl:choose>
      <xsl:when test="fn:exists(@idPerceivable)">
        <xsl:value-of select="@idPerceivable"/>
      </xsl:when>
      <xsl:when test="fn:exists(@superType)">
        <xsl:apply-templates select="../aorsml:PhysicalAgentType[@name eq current()/@superType]" mode="createAgents.isIdPerceivable"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="'false'"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
