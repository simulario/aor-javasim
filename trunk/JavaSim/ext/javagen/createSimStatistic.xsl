<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation creates a class for statistic time events based on a given aorsml file.

        $Rev$
        $Date$

        @author:   Jens Werner (jens.werner@tu-cottbus.de)
        @license:
        @last changed by $Author$
-->

<xsl:stylesheet version="2.0" xmlns:aorsl="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsi:schemaLocation="http://aor-simulation.org aorsml.xsd"
  xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">

  <!--*****-->
  <!--class-->
  <!--*****-->
  <xsl:template name="createStatistic">
    <xsl:apply-templates select="aorsl:SimulationScenario/aorsl:SimulationModel/aorsl:Statistics" mode="createStatistics.createStatistics">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="aorsl:Statistics" mode="createStatistics.createStatistics">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="aorsl:classFile">
      <xsl:with-param name="path" select="$sim.path.controller"/>
      <xsl:with-param name="name" select="$sim.class.simStatistics"/>

      <xsl:with-param name="content">

        <xsl:call-template name="java:imports">
          <xsl:with-param name="importList" as="xs:string*">
            <xsl:value-of select="fn:concat($core.package.statistics, '.*')"/>
            <xsl:value-of select="$core.package.object"/>
            <xsl:value-of select="'java.util.List'"/>
            <xsl:if
              test="aorsl:Variable/aorsl:Source/aorsl:*/@objectType | 
                    aorsl:Variable/aorsl:Source/aorsl:*/@resourceObjectType">
              <xsl:value-of select="fn:concat($sim.package.model.envsimulator, '.*')"/>
            </xsl:if>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="java:class">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="name" select="$sim.class.simStatistics"/>
          <xsl:with-param name="extends" select="$core.class.generalStatistics"/>
          <xsl:with-param name="content">

            <xsl:apply-templates select="aorsl:Variable" mode="createStatistics.setPublicVariables">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- statistic variable classes -->
            <xsl:apply-templates select="aorsl:Variable" mode="createStatistics.createVariableClasses">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="aorsl:Variable" mode="createStatistics.setPublicVariables">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="static" select="true()"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="fn:concat($sim.class.simStatistics.Variable, jw:upperWord(@name))"/>
      <xsl:with-param name="name" select="@name"/>
    </xsl:call-template>

  </xsl:template>

  <!-- create the classes -->
  <!-- we have to create one class for every variable -->
  <xsl:template match="aorsl:Variable" mode="createStatistics.createVariableClasses">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:variable name="className" select="fn:concat($sim.class.simStatistics.Variable, jw:upperWord(@name))"/>

    <xsl:call-template name="java:class">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="$className"/>
      <xsl:with-param name="extends">
        <xsl:choose>
          <xsl:when test="aorsl:Source/aorsl:ResourceUtilization">
            <xsl:value-of select="$core.class.abstractResourceUtilizationStatisticVariable"/>
          </xsl:when>
          <xsl:when test="aorsl:Source/aorsl:ObjectTypeExtensionSize">
            <xsl:value-of select="$core.class.abstractObjectTypeExtensionSizeStatisticVariable"/>
          </xsl:when>
          <xsl:when test="aorsl:Source/aorsl:ObjectProperty">
            <xsl:value-of select="$core.class.abstractObjectPropertyStatisticVariable"/>
          </xsl:when>
          <xsl:when test="aorsl:Source/aorsl:StatisticsVariable">
            <xsl:value-of select="$core.class.abstractStatisticsVariableStatisticsVariable"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$core.class.abstractStatisticsVariable"/>
          </xsl:otherwise>
        </xsl:choose>

      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:apply-templates select="." mode="createStatistics.createVariableClasses.constructor">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="className" select="$className"/>
        </xsl:apply-templates>

        <xsl:apply-templates select="." mode="createStatistics.createVariableClasses.method.getter">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <xsl:apply-templates select="." mode="createStatistics.createVariableClasses.method.expression">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- constructor -->
  <xsl:template match="aorsl:Variable" mode="createStatistics.createVariableClasses.constructor">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="className" required="yes" as="xs:string"/>

    <xsl:choose>
      <xsl:when test="aorsl:Source[not(aorsl:ValueExpr)]">
        <xsl:apply-templates select="aorsl:Source" mode="createStatistics.createVariableClasses.constructor.source">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="className" select="$className" tunnel="yes"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>

        <!-- for default statistic vars (incl. Source/ValueExpr) -->
        <xsl:call-template name="java:constructor">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="name" select="$className"/>
          <xsl:with-param name="parameters" as="xs:string*">
            <xsl:call-template name="java:createParam">
              <xsl:with-param name="type" select="'String'"/>
              <xsl:with-param name="name" select="'name'"/>
            </xsl:call-template>
            <xsl:call-template name="java:createParam">
              <xsl:with-param name="type" select="$core.class.statVarDataTypeEnumLit"/>
              <xsl:with-param name="name" select="'type'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="content">
            <xsl:call-template name="java:callSuper">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="paramList" as="xs:string*" select="('name', 'type')"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <xsl:template match="aorsl:Source" mode="createStatistics.createVariableClasses.constructor.source">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:apply-templates
      select="aorsl:ObjectProperty[not(@objectIdRef)] | aorsl:ObjectTypeExtensionSize | 
      aorsl:ResourceUtilization[not(@resourceObjectIdRef)]"
      mode="createStatistics.createVariableClasses.constructor.source.objList">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates>

    <xsl:apply-templates select="aorsl:ObjectProperty[@objectIdRef] | 
      aorsl:ResourceUtilization[@resourceObjectIdRef]"
      mode="createStatistics.createVariableClasses.constructor.source.objRef">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates>

    <xsl:apply-templates select="aorsl:GlobalVariable | aorsl:StatisticsVariable" mode="createStatistics.createVariableClasses.constructor.source">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates>

  </xsl:template>

  <xsl:template match="aorsl:GlobalVariable | aorsl:StatisticsVariable" mode="createStatistics.createVariableClasses.constructor.source">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="className" as="xs:string" required="yes" tunnel="yes"/>

    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="$className"/>
      <xsl:with-param name="parameters" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'String'"/>
          <xsl:with-param name="name" select="'name'"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="$core.class.statVarDataTypeEnumLit"/>
          <xsl:with-param name="name" select="'type'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">
        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string*" select="('name', 'type')"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- constructor for a single object reference -->
  <xsl:template match="aorsl:ObjectProperty | aorsl:ResourceUtilization" mode="createStatistics.createVariableClasses.constructor.source.objRef">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="className" as="xs:string" required="yes" tunnel="yes"/>

    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="$className"/>
      <xsl:with-param name="parameters" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'String'"/>
          <xsl:with-param name="name" select="'name'"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="$core.class.statVarDataTypeEnumLit"/>
          <xsl:with-param name="name" select="'type'"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="$core.class.object"/>
          <xsl:with-param name="name" select="'objekt'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">
        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string*" select="('name', 'type', 'objekt')"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- constructor for an object list -->
  <xsl:template match="aorsl:ObjectProperty | aorsl:ObjectTypeExtensionSize | 
    aorsl:ResourceUtilization"
    mode="createStatistics.createVariableClasses.constructor.source.objList">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="className" as="xs:string" required="yes" tunnel="yes"/>

    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="$className"/>
      <xsl:with-param name="parameters" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'String'"/>
          <xsl:with-param name="name" select="'name'"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="$core.class.statVarDataTypeEnumLit"/>
          <xsl:with-param name="name" select="'type'"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="concat('List&lt;', $core.class.object ,'>')"/>
          <xsl:with-param name="name" select="'objektList'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">
        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string*" select="('name', 'type', 'objektList')"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- create the gettermethod; returnvalue is depends the current valueType; -->
  <xsl:template match="aorsl:Variable" mode="createStatistics.createVariableClasses.method.getter">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:choose>
      <xsl:when test="aorsl:Source">
        <xsl:apply-templates select="aorsl:Source" mode="createStatistics.createVariableClasses.method.getter">
          <xsl:with-param name="indent" select="$indent"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="java:method">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="type" select="jw:upperWord(jw:mappeDataType(@dataType))"/>
          <xsl:with-param name="name" select="'getValue'"/>
          <xsl:with-param name="content">

            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="value">
                <xsl:choose>
                  <xsl:when test="aorsl:Source/aorsl:ObjectProperty[not(@objectIdRef)]">
                    <xsl:value-of select="'null'"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="'value'"/>
                    </xsl:call-template>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
              <xsl:with-param name="returnType">
                <xsl:value-of select="jw:mappeDataType(@dataType)"/>
              </xsl:with-param>
            </xsl:call-template>

          </xsl:with-param>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <xsl:template match="aorsl:Source" mode="createStatistics.createVariableClasses.method.getter">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:apply-templates select="aorsl:*" mode="createStatistics.createVariableClasses.method.getter.value">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates>

  </xsl:template>

  <xsl:template match="aorsl:GlobalVariable" mode="createStatistics.createVariableClasses.method.getter.value">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="jw:upperWord(jw:mappeDataType(../../@dataType))"/>
      <xsl:with-param name="name" select="'getValue'"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">

            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance" select="$sim.class.simGlobal"/>
              <xsl:with-param name="instVariable" select="@name"/>
            </xsl:call-template>

          </xsl:with-param>
        </xsl:call-template>


      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsl:StatisticsVariable" mode="createStatistics.createVariableClasses.method.getter.value">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="jw:upperWord(jw:mappeDataType(../../@dataType))"/>
      <xsl:with-param name="name" select="'getValue'"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">

            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="varName" select="'statisticsVariable'"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="instVariable" select="'value'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="returnType">
            <xsl:value-of select="jw:upperWord(jw:mappeDataType(../../@dataType))"/>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>


  <xsl:template match="aorsl:ObjectProperty" mode="createStatistics.createVariableClasses.method.getter.value">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="jw:upperWord(jw:mappeDataType(../../@dataType))"/>
      <xsl:with-param name="name" select="'getValue'"/>
      <xsl:with-param name="content">

        <!-- single object instance -->
        <xsl:if test="@objectIdRef">

          <xsl:call-template name="java:if">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="condition">
              <xsl:call-template name="java:boolExpr">
                <xsl:with-param name="value1">
                  <xsl:call-template name="java:varByDotNotation">
                    <xsl:with-param name="varName" select="'objekt'"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="value2" select="'null'"/>
                <xsl:with-param name="operator" select="'!='"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="thenContent">
              <xsl:variable name="varName" select="concat('__', jw:lowerWord(@objectType))"/>

              <!-- [@objectType] __[@objectType] = this.objekt -->
              <xsl:call-template name="java:variable">
                <xsl:with-param name="indent" select="$indent + 2"/>
                <xsl:with-param name="type" select="@objectType"/>
                <xsl:with-param name="castType" select="@objectType"/>
                <xsl:with-param name="name" select="$varName"/>
                <xsl:with-param name="value">
                  <xsl:call-template name="java:varByDotNotation">
                    <xsl:with-param name="varName" select="'objekt'"/>
                  </xsl:call-template>
                </xsl:with-param>
              </xsl:call-template>

              <xsl:call-template name="java:return">
                <xsl:with-param name="indent" select="$indent + 2"/>
                <xsl:with-param name="value">
                  <xsl:call-template name="java:callGetterMethod">
                    <xsl:with-param name="inLine" select="true()"/>
                    <xsl:with-param name="objInstance" select="$varName"/>
                    <xsl:with-param name="instVariable" select="@property"/>
                  </xsl:call-template>
                </xsl:with-param>
              </xsl:call-template>

            </xsl:with-param>
          </xsl:call-template>

        </xsl:if>

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value" select="'null'"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>


    <xsl:if test="not(@objectIdRef)">

      <xsl:call-template name="java:method">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="modifier" select="'public'"/>
        <xsl:with-param name="type" select="$sim.class.simStatistics.Variable.PropertyIterator"/>
        <xsl:with-param name="name" select="'getPropertyIterator'"/>
        <xsl:with-param name="content">

          <xsl:call-template name="java:return">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="value">

              <xsl:call-template name="java:newObject">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="class" select="$sim.class.simStatistics.Variable.PropertyIterator"/>
                <xsl:with-param name="isVariable" select="true()"/>
              </xsl:call-template>

            </xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>

      <xsl:call-template name="java:method">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="modifier" select="'public'"/>
        <xsl:with-param name="type" select="$sim.class.simStatistics.Variable.PropertyWithObjektIDRefIterator"/>
        <xsl:with-param name="name" select="'getObjektIDRefPropertyIterator'"/>
        <xsl:with-param name="content">

          <xsl:call-template name="java:return">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="value">

              <xsl:call-template name="java:newObject">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="class" select="$sim.class.simStatistics.Variable.PropertyWithObjektIDRefIterator"/>
                <xsl:with-param name="isVariable" select="true()"/>
              </xsl:call-template>

            </xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>

      <xsl:apply-templates select="." mode="createStatistics.createVariableClasses.createPropertyIterator">
        <xsl:with-param name="indent" select="$indent"/>
      </xsl:apply-templates>

      <xsl:apply-templates select="." mode="createStatistics.createVariableClasses.createPropertyWithObjektIDRefIterator">
        <xsl:with-param name="indent" select="$indent"/>
      </xsl:apply-templates>

    </xsl:if>

  </xsl:template>

  <xsl:template match="aorsl:ObjectTypeExtensionSize" mode="createStatistics.createVariableClasses.method.getter.value">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <!-- empty -->
    <!-- getValue() is implemented in super-class -->

  </xsl:template>

  <xsl:template match="aorsl:ResourceUtilization" mode="createStatistics.createVariableClasses.method.getter.value">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:variable name="currentStepVar" select="'currentStep'"/>
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="jw:upperWord(jw:mappeDataType(../../@dataType))"/>
      <xsl:with-param name="name" select="'getValue'"/>
      <xsl:with-param name="parameterList">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'long'"/>
          <xsl:with-param name="name" select="$currentStepVar"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:choose>
          <!-- single object instance -->
          <xsl:when test="@resourceObjectIdRef">

            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="value">

                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="objInstance">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="'objekt'"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="method" select="'getResourceUtilizationTimeByActivity'"/>
                  <xsl:with-param name="args" as="xs:string*">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="'activityType'"/>
                    </xsl:call-template>
                    <xsl:value-of select="$currentStepVar"/>
                  </xsl:with-param>
                </xsl:call-template>

              </xsl:with-param>
              <xsl:with-param name="returnType">
                <xsl:value-of select="jw:mappeDataType(../../@dataType)"/>
              </xsl:with-param>
            </xsl:call-template>

          </xsl:when>
          <xsl:otherwise>

            <xsl:variable name="cumulativeVarName" select="'ressourceUtilization'"/>
            <xsl:variable name="objektVarName" select="'objekt'"/>
            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="type" select="'long'"/>
              <xsl:with-param name="name" select="$cumulativeVarName"/>
              <xsl:with-param name="value" select="'0'"/>
            </xsl:call-template>

            <xsl:call-template name="java:for-each-loop">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="elementType" select="$core.class.object"/>
              <xsl:with-param name="elementVarName" select="$objektVarName"/>
              <xsl:with-param name="listVarName">
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="varName" select="'objektList'"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="content">
                <xsl:call-template name="java:variable">
                  <xsl:with-param name="indent" select="$indent + 2"/>
                  <xsl:with-param name="name" select="$cumulativeVarName"/>
                  <xsl:with-param name="value">
                    <xsl:call-template name="java:plus">
                      <xsl:with-param name="value1" select="$cumulativeVarName"/>
                      <xsl:with-param name="value2">
                        <xsl:call-template name="java:callMethod">
                          <xsl:with-param name="inLine" select="true()"/>
                          <xsl:with-param name="objInstance" select="$objektVarName"/>
                          <xsl:with-param name="method" select="'getResourceUtilizationTimeByActivity'"/>
                          <xsl:with-param name="args" as="xs:string*">
                            <xsl:call-template name="java:varByDotNotation">
                              <xsl:with-param name="varName" select="'activityType'"/>
                            </xsl:call-template>
                            <xsl:value-of select="$currentStepVar"/>
                          </xsl:with-param>
                        </xsl:call-template>
                      </xsl:with-param>
                    </xsl:call-template>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>

            <!-- list of object instances -->
            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="value">

                <xsl:call-template name="java:plus">
                  <xsl:with-param name="value1" select="$cumulativeVarName"/>
                  <xsl:with-param name="value2">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="'destroyedObjectResourceUtilization'"/>
                    </xsl:call-template>
                  </xsl:with-param>
                </xsl:call-template>

              </xsl:with-param>
              <xsl:with-param name="returnType">
                <xsl:value-of select="jw:mappeDataType(../../@dataType)"/>
              </xsl:with-param>
            </xsl:call-template>

          </xsl:otherwise>

        </xsl:choose>
      </xsl:with-param>
    </xsl:call-template>

    <!-- empty -->
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="jw:upperWord(jw:mappeDataType(../../@dataType))"/>
      <xsl:with-param name="name" select="'getValue'"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value" select="'null'"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsl:ValueExpr" mode="createStatistics.createVariableClasses.method.getter.value">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:if test="not(preceding-sibling::aorsl:ValueExpr)">

      <xsl:call-template name="java:method">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="modifier" select="'public'"/>
        <xsl:with-param name="type" select="jw:upperWord(jw:mappeDataType(../../@dataType))"/>
        <xsl:with-param name="name" select="'getValue'"/>
        <xsl:with-param name="content">

          <xsl:call-template name="java:return">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="value">
              <xsl:call-template name="java:varByDotNotation">
                <xsl:with-param name="varName" select="'value'"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="returnType">
              <xsl:value-of select="jw:mappeDataType(../../@dataType)"/>
            </xsl:with-param>
          </xsl:call-template>

        </xsl:with-param>
      </xsl:call-template>

    </xsl:if>

  </xsl:template>

  <!-- for expression -->
  <xsl:template match="aorsl:Variable" mode="createStatistics.createVariableClasses.method.expression">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="'computeVar'"/>
      <xsl:with-param name="content">

        <xsl:if test="fn:exists(aorsl:Source/aorsl:ValueExpr[matches(@language, $output.lang.RegExpr)])">
          <xsl:call-template name="java:variable">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="name">
              <xsl:call-template name="java:varByDotNotation">
                <xsl:with-param name="varName" select="'value'"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="value">
              <xsl:value-of select="fn:normalize-space(aorsl:Source/aorsl:ValueExpr[matches(@language, $output.lang.RegExpr)][1])"/>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:if>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>


  <!-- ++++++++++++++++++++++++++++++++++++++++++++++ -->
  <!--              PropertyIterator                  -->
  <!-- ++++++++++++++++++++++++++++++++++++++++++++++ -->
  <xsl:template match="aorsl:ObjectProperty" mode="createStatistics.createVariableClasses.createPropertyIterator">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:class">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="$sim.class.simStatistics.Variable.PropertyIterator"/>
      <xsl:with-param name="extends" select="$core.class.abstractPropertyIterator"/>
      <xsl:with-param name="content">

        <!-- constructor -->
        <xsl:apply-templates select="." mode="createStatistics.createVariableClasses.createPropertyIterator.constructor">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <!-- hasNext() -->
        <xsl:apply-templates select="." mode="createStatistics.createVariableClasses.createPropertyIterator.method.hasNext">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <!-- next() -->
        <xsl:apply-templates select="." mode="createStatistics.createVariableClasses.createPropertyIterator.method.next">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="java:newLine"/>
  </xsl:template>

  <xsl:template match="aorsl:ObjectProperty" mode="createStatistics.createVariableClasses.createPropertyIterator.constructor">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="$sim.class.simStatistics.Variable.PropertyIterator"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string">

            <xsl:choose>
              <xsl:when test="not(@objectIdRef)">

                <!-- super(StatisticsVariable[@name].this.objektList.iterator()) -->
                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="objInstance">

                    <xsl:call-template name="java:callGetterMethodOuterClass">
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="outerClass" select="concat($sim.class.simStatistics.Variable, jw:upperWord(../../@name))"/>
                      <xsl:with-param name="instVariable" select="'objektList'"/>
                    </xsl:call-template>

                  </xsl:with-param>
                  <xsl:with-param name="method" select="'iterator'"/>
                </xsl:call-template>

              </xsl:when>

              <xsl:otherwise>
                <xsl:value-of select="'null'"/>
              </xsl:otherwise>
            </xsl:choose>

          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsl:ObjectProperty" mode="createStatistics.createVariableClasses.createPropertyIterator.method.hasNext">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="annotation" select="'@Override'"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'boolean'"/>
      <xsl:with-param name="name" select="'hasNext'"/>
      <xsl:with-param name="content">

        <!-- return this.iterator.hasNext(); -->
        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">

            <xsl:choose>
              <xsl:when test="not(@objectIdRef)">

                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="objInstance">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="'iterator'"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="method" select="'hasNext'"/>
                </xsl:call-template>

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

  <xsl:template match="aorsl:ObjectProperty" mode="createStatistics.createVariableClasses.createPropertyIterator.method.next">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="annotation" select="'@Override'"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'Double'"/>
      <xsl:with-param name="name" select="'next'"/>
      <xsl:with-param name="content">

        <xsl:choose>

          <xsl:when test="not(@objectIdRef)">

            <xsl:variable name="varName" select="concat('__', jw:lowerWord(@objectType))"/>
            <xsl:call-template name="java:newObject">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="class" select="@objectType"/>
              <xsl:with-param name="varName" select="$varName"/>
              <xsl:with-param name="withDeclaration" select="false()"/>
            </xsl:call-template>

            <xsl:call-template name="java:tryCatch">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="tryContent">

                <xsl:call-template name="java:variable">
                  <xsl:with-param name="indent" select="$indent + 2"/>
                  <xsl:with-param name="name" select="$varName"/>
                  <xsl:with-param name="castType" select="@objectType"/>
                  <xsl:with-param name="value">

                    <xsl:call-template name="java:callMethod">
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="objInstance">
                        <xsl:call-template name="java:varByDotNotation">
                          <xsl:with-param name="varName" select="'iterator'"/>
                        </xsl:call-template>
                      </xsl:with-param>
                      <xsl:with-param name="method" select="'next'"/>
                    </xsl:call-template>

                  </xsl:with-param>
                </xsl:call-template>

              </xsl:with-param>
              <xsl:with-param name="catchContent">

                <xsl:call-template name="java:return">
                  <xsl:with-param name="indent" select="$indent + 2"/>
                  <xsl:with-param name="value" select="'null'"/>
                </xsl:call-template>

              </xsl:with-param>
              <xsl:with-param name="exceptionType" select="'java.util.NoSuchElementException'"/>
              <xsl:with-param name="exceptionVariable" select="'nsee'"/>
            </xsl:call-template>

            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="value">
                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="objInstance" select="'Double'"/>
                  <xsl:with-param name="method" select="'valueOf'"/>
                  <xsl:with-param name="args">
                    <xsl:call-template name="java:callGetterMethod">
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="objInstance" select="$varName"/>
                      <xsl:with-param name="instVariable" select="@property"/>
                    </xsl:call-template>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>

          </xsl:when>
          <xsl:otherwise>

            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="value" select="'null'"/>
            </xsl:call-template>

          </xsl:otherwise>
        </xsl:choose>

      </xsl:with-param>

    </xsl:call-template>

  </xsl:template>


  <!-- ++++++++++++++++++++++++++++++++++++++++++++++ -->
  <!--       PropertyWithObjektIDRefIterator          -->
  <!-- ++++++++++++++++++++++++++++++++++++++++++++++ -->
  <xsl:template match="aorsl:ObjectProperty" mode="createStatistics.createVariableClasses.createPropertyWithObjektIDRefIterator">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:class">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="$sim.class.simStatistics.Variable.PropertyWithObjektIDRefIterator"/>
      <xsl:with-param name="extends" select="$core.class.abstractObjektIDRefPropertyIterator"/>
      <xsl:with-param name="content">

        <!-- constructor -->
        <xsl:apply-templates select="." mode="createStatistics.createVariableClasses.createPropertyWithObjektIDRefIterator.constructor">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <!-- hasNext() -->
        <xsl:apply-templates select="." mode="createStatistics.createVariableClasses.createPropertyWithObjektIDRefIterator.method.hasNext">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <!-- next() -->
        <xsl:apply-templates select="." mode="createStatistics.createVariableClasses.createPropertyWithObjektIDRefIterator.method.next">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="java:newLine"/>
  </xsl:template>

  <xsl:template match="aorsl:ObjectProperty" mode="createStatistics.createVariableClasses.createPropertyWithObjektIDRefIterator.constructor">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="$sim.class.simStatistics.Variable.PropertyWithObjektIDRefIterator"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string">

            <xsl:choose>
              <xsl:when test="not(@objectIdRef)">

                <!-- super(StatisticsVariable[@name].this.objektList.iterator()) -->
                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="objInstance">

                    <xsl:call-template name="java:callGetterMethodOuterClass">
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="outerClass" select="concat($sim.class.simStatistics.Variable, jw:upperWord(../../@name))"/>
                      <xsl:with-param name="instVariable" select="'objektList'"/>
                    </xsl:call-template>

                  </xsl:with-param>
                  <xsl:with-param name="method" select="'iterator'"/>
                </xsl:call-template>

              </xsl:when>

              <xsl:otherwise>
                <xsl:value-of select="'null'"/>
              </xsl:otherwise>
            </xsl:choose>

          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- same as createStatistics.createVariableClasses.createPropertyIterator.method.hasNext -->
  <xsl:template match="aorsl:ObjectProperty" mode="createStatistics.createVariableClasses.createPropertyWithObjektIDRefIterator.method.hasNext">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="annotation" select="'@Override'"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'boolean'"/>
      <xsl:with-param name="name" select="'hasNext'"/>
      <xsl:with-param name="content">

        <!-- return this.iterator.hasNext(); -->
        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">

            <xsl:choose>
              <xsl:when test="not(@objectIdRef)">

                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="objInstance">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="'iterator'"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="method" select="'hasNext'"/>
                </xsl:call-template>

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

  <xsl:template match="aorsl:ObjectProperty" mode="createStatistics.createVariableClasses.createPropertyWithObjektIDRefIterator.method.next">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="annotation" select="'@Override'"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="$core.class.objektIdPropertyData"/>
      <xsl:with-param name="name" select="'next'"/>
      <xsl:with-param name="content">

        <xsl:choose>

          <xsl:when test="not(@objectIdRef)">

            <xsl:variable name="varName" select="concat('___', jw:lowerWord(@objectType))"/>
            <xsl:call-template name="java:newObject">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="class" select="@objectType"/>
              <xsl:with-param name="varName" select="$varName"/>
              <xsl:with-param name="withDeclaration" select="false()"/>
            </xsl:call-template>

            <xsl:call-template name="java:tryCatch">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="tryContent">

                <xsl:call-template name="java:variable">
                  <xsl:with-param name="indent" select="$indent + 2"/>
                  <xsl:with-param name="name" select="$varName"/>
                  <xsl:with-param name="castType" select="@objectType"/>
                  <xsl:with-param name="value">

                    <xsl:call-template name="java:callMethod">
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="objInstance">
                        <xsl:call-template name="java:varByDotNotation">
                          <xsl:with-param name="varName" select="'iterator'"/>
                        </xsl:call-template>
                      </xsl:with-param>
                      <xsl:with-param name="method" select="'next'"/>
                    </xsl:call-template>

                  </xsl:with-param>
                </xsl:call-template>

              </xsl:with-param>
              <xsl:with-param name="catchContent">

                <xsl:call-template name="java:return">
                  <xsl:with-param name="indent" select="$indent + 2"/>
                  <xsl:with-param name="value" select="'null'"/>
                </xsl:call-template>

              </xsl:with-param>
              <xsl:with-param name="exceptionType" select="'java.util.NoSuchElementException'"/>
              <xsl:with-param name="exceptionVariable" select="'nsee'"/>
            </xsl:call-template>

            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="value">
                <xsl:call-template name="java:newObject">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="class" select="$core.class.objektIdPropertyData"/>
                  <xsl:with-param name="isVariable" select="true()"/>
                  <xsl:with-param name="args" as="xs:string*">
                    <xsl:call-template name="java:callGetterMethod">
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="objInstance" select="$varName"/>
                      <xsl:with-param name="instVariable" select="'id'"/>
                    </xsl:call-template>
                    <xsl:call-template name="java:callMethod">
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="objInstance" select="'Double'"/>
                      <xsl:with-param name="method" select="'valueOf'"/>
                      <xsl:with-param name="args">
                        <xsl:call-template name="java:callGetterMethod">
                          <xsl:with-param name="inLine" select="true()"/>
                          <xsl:with-param name="objInstance" select="$varName"/>
                          <xsl:with-param name="instVariable" select="@property"/>
                        </xsl:call-template>
                      </xsl:with-param>
                    </xsl:call-template>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>

          </xsl:when>
          <xsl:otherwise>

            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="value" select="'null'"/>
            </xsl:call-template>

          </xsl:otherwise>
        </xsl:choose>

      </xsl:with-param>

    </xsl:call-template>

  </xsl:template>

</xsl:stylesheet>
