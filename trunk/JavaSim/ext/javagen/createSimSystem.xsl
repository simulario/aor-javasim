<?xml version="1.0" encoding="UTF-8"?>
<!--
        This transformation creates the main class for simulation based on a given aorsml file.

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

  <xsl:include href="model/envsim/createInitializationRules.xsl"/>

  <!--*****-->
  <!--class-->
  <!--*****-->
  <xsl:template name="createSimSystem">
    <xsl:apply-templates select="aorsl:SimulationScenario" mode="createSimSystem.SimSystem">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

  <!--creates class-->
  <xsl:template match="aorsl:SimulationScenario" mode="createSimSystem.SimSystem">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="aorsl:classFile">
      <xsl:with-param name="path" select="$sim.path.controller"/>
      <xsl:with-param name="name" select="$sim.class.simulatorMain"/>

      <xsl:with-param name="content">

        <xsl:call-template name="java:imports">
          <xsl:with-param name="importList" as="xs:string*">
            <xsl:value-of select="$core.package.spaceModel"/>
            <xsl:value-of select="$core.package.simulationEngine"/>
            <xsl:value-of select="fn:concat($core.package.model.envSim, '.*')"/>
            <xsl:if
              test="fn:exists(aorsl:SimulationModel/aorsl:EntityTypes/aorsl:CausedEventType) or 
                        fn:exists(aorsl:SimulationModel/aorsl:EntityTypes/aorsl:PerceptionEventType) or 
                        fn:exists(aorsl:SimulationModel/aorsl:EntityTypes/aorsl:ExogenousEventType) or 
                        fn:exists(aorsl:SimulationModel/aorsl:EntityTypes/aorsl:ActionEventType)">
              <xsl:value-of select="fn:concat($sim.package.model.envevent, '.*')"/>
            </xsl:if>
            <xsl:if test="fn:exists(//aorsl:Collections/aorsl:Collection)">
              <xsl:value-of select="$collection.package.aORCollection"/>
            </xsl:if>

            <xsl:if
              test="fn:exists(aorsl:SimulationModel/aorsl:EntityTypes/aorsl:PhysicalAgentType) or 
                    fn:exists(aorsl:SimulationModel/aorsl:EntityTypes/aorsl:AgentType)">
              <xsl:value-of select="fn:concat($sim.package.model.agentsimulator, '.*')"/>
            </xsl:if>

            <!-- <xsl:value-of select="fn:concat($sim.package.model.internalevent, '.*')"/> -->

            <xsl:value-of select="$core.package.generalStatistics"/>
            <xsl:if test="fn:exists(aorsl:SimulationModel/aorsl:Statistics/aorsl:Variable)">
              <!-- <xsl:value-of select="fn:concat($core.package.statistics, '.*')"/> -->
              <xsl:value-of select="$core.package.statVarDataTypeEnumLit"/>
              <xsl:value-of select="$core.package.statVarDataSourceEnumLit"/>
              <xsl:if test="fn:exists(aorsl:SimulationModel/aorsl:Statistics/aorsl:Variable/aorsl:Source/@aggregationFunction)">
                <xsl:value-of select="$core.package.aggregFunEnumLit"/>
              </xsl:if>
            </xsl:if>
            <xsl:call-template name="setDefaultJavaImports"/>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="java:class">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="name" select="$sim.class.simulatorMain"/>
          <xsl:with-param name="extends" select="$core.class.simulationEngine"/>
          <xsl:with-param name="content">

            <!-- spacemodel as classvariable -->
            <xsl:if test="fn:exists(aorsl:SimulationModel/aorsl:SpaceModel)">
              <xsl:call-template name="java:variable">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="static" select="true()"/>
                <xsl:with-param name="modifier" select="'public'"/>
                <xsl:with-param name="type" select="$sim.class.simSpaceModel"/>
                <xsl:with-param name="name" select="'spaceModel'"/>
              </xsl:call-template>
            </xsl:if>
            <xsl:call-template name="java:newLine"/>

            <!-- constructor -->
            <xsl:apply-templates select="." mode="createSimSystem.constructor">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="name" select="$sim.class.simulatorMain"/>
            </xsl:apply-templates>

            <!-- setScenarioInformations -->
            <xsl:apply-templates select="." mode="createSimSystem.method.setScenarioInformations">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- createSimulationParameters -->
            <xsl:apply-templates select="aorsl:SimulationParameters" mode="createSimSystem.method.getSimulationParameters">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- create SimulationModel -->
            <xsl:apply-templates select="aorsl:SimulationModel" mode="createSimSystem.method.createSimModel">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- createSpaceModel -->
            <xsl:apply-templates select="aorsl:SimulationModel" mode="createSimSystem.method.createSpaceModel">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- it is used to desid which getRandomPosition() - version is called in shared.helper.initAORObjects 
            <xsl:variable name="discreteSpace" as="xs:boolean">
              <xsl:choose>
                <xsl:when test="ends-with(local-name(aorsl:SimulationModel/aorsl:SpaceModel/aorsl:*), 'Grid')">
                  <xsl:value-of select="true()"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="false()"/>
                </xsl:otherwise>
              </xsl:choose>
              </xsl:variable> -->
            
            <xsl:apply-templates select="aorsl:SimulationModel" mode="createSimSystem.method.initGlobals">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- createEnvironmentSimulator -->
            <xsl:apply-templates select="aorsl:SimulationModel" mode="createSimSystem.method.createEnvironment">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <!--xsl:with-param name="discreteSpace" select="$discreteSpace" tunnel="yes"/-->
            </xsl:apply-templates>

            <!-- create AgentSubjects -->
            <xsl:apply-templates select="aorsl:SimulationModel" mode="createSimSystem.method.createAgentSubjects">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- create AgentSubjectFacets
            <xsl:apply-templates select="aorsl:SimulationModel/aorsl:EntityTypes" mode="createSimSystem.method.createAgentSubjectFacets">
              <xsl:with-param name="indent" select="$indent + 1"/>
              </xsl:apply-templates>  -->

            <!-- create createInitialEvents -->
            <xsl:apply-templates select="aorsl:SimulationModel" mode="createSimSystem.method.createInitialEvents">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="onEveryStep" as="xs:boolean"
                select="exists(aorsl:SimulationModel/aorsl:EnvironmentRules/aorsl:EnvironmentRule/aorsl:ON-EACH-SIMULATION-STEP)"/>
            </xsl:apply-templates>

            <!-- create Statistics -->
            <xsl:apply-templates select="aorsl:SimulationModel" mode="createSimSystem.method.createStatistic">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <xsl:if test="$with.mainMethod">
              <!-- mainMethod -->
              <xsl:apply-templates select="." mode="createSimSystem.method.main">
                <xsl:with-param name="indent" select="$indent + 1"/>
              </xsl:apply-templates>
            </xsl:if>

            <!-- executeInitializeRules()  -->
            <xsl:apply-templates select="//aorsl:InitialState" mode="createSimSystem.method.executeInitializeRules">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- setActivityFactory -->
            <xsl:apply-templates select="aorsl:SimulationModel" mode="createSimSystem.method.setActivityFactory">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- create InitializationRule  (inner classes)-->
            <xsl:apply-templates select="//aorsl:InitialState/aorsl:InitializationRule" mode="createInitializationRule.createInitializationRules">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>

    </xsl:call-template>
  </xsl:template>


  <!-- *****************************************************************  -->
  <!--                                     Constructor(s)                                                -->
  <!-- *****************************************************************  -->

  <xsl:template match="aorsl:SimulationScenario" mode="createSimSystem.constructor">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="name" required="yes"/>

    <xsl:call-template name="java:newLine"/>
    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="$name"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="$sim.class.simParams"/>
              <xsl:with-param name="varName" select="$final.simulationSteps"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- *****************************************************************  -->
  <!--                                           Method(s)                                                  -->
  <!-- *****************************************************************  -->
  <!--  setScenarioInformations -->
  <xsl:template match="aorsl:SimulationScenario" mode="createSimSystem.method.setScenarioInformations">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'setInformations'"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:callSetterMethod">
          <xsl:with-param name="indent" select="$indent  + 1"/>
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="'this'"/>
              <xsl:with-param name="varName" select="'scenarioInfos'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="instVariable" select="'version'"/>
          <xsl:with-param name="value" select="@version"/>
          <xsl:with-param name="valueType" select="'String'"/>
        </xsl:call-template>

        <xsl:call-template name="java:callSetterMethod">
          <xsl:with-param name="indent" select="$indent  + 1"/>
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="'this'"/>
              <xsl:with-param name="varName" select="'scenarioInfos'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="instVariable" select="'scenarioName'"/>
          <xsl:with-param name="value" select="@scenarioName"/>
          <xsl:with-param name="valueType" select="'String'"/>
        </xsl:call-template>

        <xsl:if test="fn:exists(@scenarioTitle)">
          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent  + 1"/>
            <xsl:with-param name="objInstance">
              <xsl:call-template name="java:varByDotNotation">
                <xsl:with-param name="name" select="'this'"/>
                <xsl:with-param name="varName" select="'scenarioInfos'"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="instVariable" select="'scenarioTitle'"/>
            <xsl:with-param name="value" select="@scenarioTitle"/>
            <xsl:with-param name="valueType" select="'String'"/>
          </xsl:call-template>
        </xsl:if>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- createSimulationParameters -->
  <xsl:template match="aorsl:SimulationParameters" mode="createSimSystem.method.getSimulationParameters">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="type" select="$sim.class.simParams"/>
      <xsl:with-param name="name" select="'getSimParams'"/>
      <xsl:with-param name="content">

        <xsl:if test="fn:exists(@stepTimeDelay)">
          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="objInstance" select="'this'"/>
            <xsl:with-param name="instVariable" select="'stepTimeDelay'"/>
            <xsl:with-param name="value" select="@stepTimeDelay"/>
          </xsl:call-template>
        </xsl:if>

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent +  1"/>
          <xsl:with-param name="value">
            <xsl:call-template name="java:newObject">
              <xsl:with-param name="class" select="$sim.class.simParams"/>
              <xsl:with-param name="isVariable" select="true()"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>


      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- createSimulationParameters -->
  <xsl:template match="aorsl:SimulationModel" mode="createSimSystem.method.createSimModel">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'createSimModel'"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="name">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="varName" select="'simModel'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="value">
            <xsl:call-template name="java:newObject">
              <xsl:with-param name="class" select="$sim.class.simModel"/>
              <xsl:with-param name="isVariable" select="true()"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- createSpaceModel -->
  <xsl:template match="aorsl:SimulationModel" mode="createSimSystem.method.createSpaceModel">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="type" select="$core.class.spaceModel"/>
      <xsl:with-param name="name" select="'createSpaceModel'"/>
      <xsl:with-param name="content">
        <xsl:choose>
          <xsl:when test="fn:exists(aorsl:SpaceModel)">

            <xsl:variable name="spaceModelVar" select="jw:lowerWord($core.class.spaceModel)"/>

            <xsl:call-template name="java:newObject">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="class" select="$sim.class.simSpaceModel"/>
              <xsl:with-param name="varName" select="$spaceModelVar"/>
              <xsl:with-param name="args">
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="name" select="$core.class.spaceModel"/>
                  <xsl:with-param name="varName">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="name" select="'Dimensions'"/>
                      <xsl:with-param name="varName">
                        <xsl:choose>
                          <xsl:when test="aorsl:SpaceModel/aorsl:OneDimensional/@multiplicity = '2'">
                            <xsl:value-of select="'OnePlus1'"/>
                          </xsl:when>
                          <xsl:when test="aorsl:SpaceModel/aorsl:OneDimensional/@multiplicity = '3'">
                            <xsl:value-of select="'OnePlus1Plus1'"/>
                          </xsl:when>
                          <xsl:when test="aorsl:SpaceModel/aorsl:OneDimensional/@multiplicity = '4'">
                            <xsl:value-of select="'OnePlus1Plus1Plus1'"/>
                          </xsl:when>
                          <xsl:when
                            test="exists(aorsl:SpaceModel/aorsl:OneDimensional) or 
                                exists(aorsl:SpaceModel/aorsl:OneDimensionalGrid)">
                            <xsl:value-of select="'one'"/>
                          </xsl:when>
                          <xsl:when
                            test="exists(aorsl:SpaceModel/aorsl:TwoDimensional) or 
                                exists(aorsl:SpaceModel/aorsl:TwoDimensional_LateralView) or 
                                exists(aorsl:SpaceModel/aorsl:TwoDimensionalGrid)">
                            <xsl:value-of select="'two'"/>
                          </xsl:when>
                          <xsl:when
                            test="exists(aorsl:SpaceModel/aorsl:ThreeDimensional) or 
                                exists(aorsl:SpaceModel/aorsl:ThreeDimensionalGrid)">
                            <xsl:value-of select="'three'"/>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:message terminate="yes">ERROR: wrong value for spacemodel.dimension</xsl:message>
                          </xsl:otherwise>
                        </xsl:choose>
                      </xsl:with-param>
                    </xsl:call-template>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>

            <xsl:apply-templates select="aorsl:SpaceModel/aorsl:*" mode="createSimSystem.method.createSpaceModel.properties">
              <xsl:with-param name="indent" select="$indent"/>
              <xsl:with-param name="spaceModelVar" select="$spaceModelVar"/>
            </xsl:apply-templates>

            <xsl:if test="ends-with(local-name(aorsl:SpaceModel/aorsl:*) , 'Grid') and fn:exists(aorsl:SpaceModel/aorsl:*/@gridCellMaxOccupancy)">

              <!-- gridCellMaxOccupancy  (optional, default = unbounded; mapped to -1)-->
              <xsl:call-template name="java:callSetterMethod">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="objInstance" select="$spaceModelVar"/>
                <xsl:with-param name="instVariable" select="'gridCellMaxOccupancy'"/>
                <xsl:with-param name="value">
                  <xsl:choose>
                    <xsl:when test="aorsl:SpaceModel/aorsl:*/@gridCellMaxOccupancy eq 'unbounded'">
                      <xsl:value-of select="'-1'"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="aorsl:SpaceModel/aorsl:*/@gridCellMaxOccupancy"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:with-param>
              </xsl:call-template>

            </xsl:if>

            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="name">
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="name" select="$sim.class.simulatorMain"/>
                  <xsl:with-param name="varName" select="'spaceModel'"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="value" select="$spaceModelVar"/>
            </xsl:call-template>

            <!-- call the logger -->
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="varName" select="$databus.class.dataBus"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="method" select="'notifySpaceModel'"/>
              <xsl:with-param name="args" select="$spaceModelVar"/>
            </xsl:call-template>
            
            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="value" select="$spaceModelVar"/>
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

  <xsl:template match="aorsl:OneDimensional | aorsl:TwoDimensional | aorsl:TwoDimensional_LateralView | aorsl:ThreeDimensional"
    mode="createSimSystem.method.createSpaceModel.properties">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="spaceModelVar" as="xs:string" required="yes"/>

    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="objInstance" select="$spaceModelVar"/>
      <xsl:with-param name="instVariable" select="'discrete'"/>
      <xsl:with-param name="value" select="'false'"/>
    </xsl:call-template>

    <xsl:apply-templates select="." mode="createSimSystem.method.createSpaceModel.commonProperties">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="spaceModelVar" select="$spaceModelVar"/>
    </xsl:apply-templates>

    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="objInstance" select="$spaceModelVar"/>
      <xsl:with-param name="method" select="'initSpace'"/>
    </xsl:call-template>

    <xsl:apply-templates select="." mode="createSimSystem.method.createSpaceModel.properties.extra">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="spaceModelVar" select="$spaceModelVar"/>
    </xsl:apply-templates>

  </xsl:template>

  <xsl:template match="aorsl:OneDimensionalGrid | aorsl:TwoDimensionalGrid | aorsl:ThreeDimensionalGrid"
    mode="createSimSystem.method.createSpaceModel.properties">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="spaceModelVar" as="xs:string" required="yes"/>

    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="objInstance" select="$spaceModelVar"/>
      <xsl:with-param name="instVariable" select="'discrete'"/>
      <xsl:with-param name="value" select="'true'"/>
    </xsl:call-template>

    <!-- gridCellMaxOccupancy  (optional, default = unbounded; mapped to -1)-->
    <xsl:if test="@gridCellMaxOccupancy">
      <xsl:call-template name="java:callSetterMethod">
        <xsl:with-param name="indent" select="$indent + 1"/>
        <xsl:with-param name="objInstance" select="$spaceModelVar"/>
        <xsl:with-param name="instVariable" select="'gridCellMaxOccupancy'"/>
        <xsl:with-param name="value">
          <xsl:choose>
            <xsl:when test="@gridCellMaxOccupancy eq 'unbounded'">
              <xsl:value-of select="'-1'"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="@gridCellMaxOccupancy"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>

    <xsl:apply-templates select="." mode="createSimSystem.method.createSpaceModel.commonProperties">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="spaceModelVar" select="$spaceModelVar"/>
    </xsl:apply-templates>

    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="objInstance" select="$spaceModelVar"/>
      <xsl:with-param name="method" select="'initSpace'"/>
    </xsl:call-template>

    <xsl:apply-templates select="." mode="createSimSystem.method.createSpaceModel.properties.extra">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="spaceModelVar" select="$spaceModelVar"/>
    </xsl:apply-templates>

  </xsl:template>

  <xsl:template match="aorsl:OneDimensionalGrid" mode="createSimSystem.method.createSpaceModel.properties.extra">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="spaceModelVar" as="xs:string" required="yes"/>
    <!-- not yet implemented -->
  </xsl:template>

  <xsl:template match="aorsl:TwoDimensionalGrid" mode="createSimSystem.method.createSpaceModel.properties.extra">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="spaceModelVar" as="xs:string" required="yes"/>

    <!-- set the gridcells -->
    <xsl:if test="exists(aorsl:GridCellProperty)">

      <xsl:call-template name="java:newLine"/>
      <!-- initGrid() -->
      <xsl:call-template name="java:callMethod">
        <xsl:with-param name="indent" select="$indent + 1"/>
        <xsl:with-param name="objInstance" select="$spaceModelVar"/>
        <xsl:with-param name="method" select="'initGrid'"/>
        <xsl:with-param name="args" as="xs:string*">
          <xsl:value-of select="@xMax"/>
          <xsl:value-of select="@yMax"/>
          <xsl:call-template name="java:varByDotNotation">
            <xsl:with-param name="varName" select="$databus.class.dataBus"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>

      <xsl:variable name="gridCellVariable" select="jw:lowerWord($sim.class.simGridCell)"/>
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent + 1"/>
        <xsl:with-param name="class" select="fn:concat($sim.class.simSpaceModel, '.', $sim.class.simGridCell)"/>
        <xsl:with-param name="varName" select="$gridCellVariable"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>

      <xsl:apply-templates select="//aorsl:InitialState/aorsl:GridCells" mode="createSimSystem.helper.createGridCells">
        <xsl:with-param name="indent" select="$indent + 1"/>
        <xsl:with-param name="spaceModelVar" select="$spaceModelVar" tunnel="yes"/>
        <xsl:with-param name="gridCellVariable" select="$gridCellVariable" tunnel="yes"/>
        <xsl:with-param name="observeGridCells" select="if (true()) then true() else false()" tunnel="yes" as="xs:boolean"/>
      </xsl:apply-templates>
    </xsl:if>

  </xsl:template>

  <xsl:template match="aorsl:ThreeDimensionalGrid" mode="createSimSystem.method.createSpaceModel.properties.extra">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="spaceModelVar" as="xs:string" required="yes"/>
    <!-- not yet implemented -->
  </xsl:template>

  <xsl:template match="aorsl:OneDimensional" mode="createSimSystem.method.createSpaceModel.properties.extra">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="spaceModelVar" as="xs:string" required="yes"/>
    <!-- not yet implemented -->

  </xsl:template>

  <xsl:template match="aorsl:TwoDimensional" mode="createSimSystem.method.createSpaceModel.properties.extra">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="spaceModelVar" as="xs:string" required="yes"/>
    <!-- not yet implemented -->
  </xsl:template>

  <xsl:template match="aorsl:TwoDimensional_LateralView" mode="createSimSystem.method.createSpaceModel.properties.extra">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="spaceModelVar" as="xs:string" required="yes"/>

    <!-- Gravitation (optional) default: 9.81 -->
    <xsl:if test="@gravitation">

      <xsl:call-template name="java:callSetterMethod">
        <xsl:with-param name="indent" select="$indent + 1"/>
        <xsl:with-param name="objInstance">
          <xsl:call-template name="java:callGetterMethod">
            <xsl:with-param name="inLine" select="true()"/>
            <xsl:with-param name="objInstance" select="$spaceModelVar"/>
            <xsl:with-param name="instVariable" select="'space'"/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="instVariable" select="'gravitation'"/>
        <xsl:with-param name="value" select="@gravitation"/>
      </xsl:call-template>

    </xsl:if>
  </xsl:template>

  <xsl:template match="aorsl:ThreeDimensional" mode="createSimSystem.method.createSpaceModel.properties.extra">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="spaceModelVar" as="xs:string" required="yes"/>
    <!-- not yet implemented -->
  </xsl:template>

  <xsl:template
    match="aorsl:OneDimensionalGrid | aorsl:TwoDimensionalGrid | aorsl:ThreeDimensionalGrid |
           aorsl:OneDimensional | aorsl:TwoDimensional | aorsl:TwoDimensional_LateralView | aorsl:ThreeDimensional"
    mode="createSimSystem.method.createSpaceModel.commonProperties">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="spaceModelVar" as="xs:string" required="yes"/>

    <!-- SpaceModel.attributes -->
    <!-- Geometry (optional) default: Euclidean -->
    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="objInstance" select="$spaceModelVar"/>
      <xsl:with-param name="instVariable" select="'geometry'"/>
      <xsl:with-param name="value">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="name" select="$core.class.spaceModel"/>
          <xsl:with-param name="varName">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="'Geometry'"/>
              <xsl:with-param name="varName">
                <xsl:choose>
                  <xsl:when test="../@geometry">
                    <xsl:value-of select="../@geometry"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="'Euclidean'"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

    <!-- SpatialDistanceUnit (optional) default: m-->
    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="objInstance" select="$spaceModelVar"/>
      <xsl:with-param name="instVariable" select="'spatialDistanceUnit'"/>
      <xsl:with-param name="value">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="name" select="$core.class.spaceModel"/>
          <xsl:with-param name="varName">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="'SpatialDistanceUnit'"/>
              <xsl:with-param name="varName">
                <xsl:choose>
                  <xsl:when test="../@spatialDistanceUnit">
                    <xsl:value-of select="../@spatialDistanceUnit"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="'m'"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

    <!-- set multiplicity for all spaces types (now make sense only for 1D but later may be for 2D and 3D too) -->
    <xsl:if test="@multiplicity">
      <xsl:call-template name="java:callSetterMethod">
        <xsl:with-param name="indent" select="$indent + 1"/>
        <xsl:with-param name="objInstance" select="$spaceModelVar"/>
        <xsl:with-param name="instVariable" select="'multiplicity'"/>
        <xsl:with-param name="value" select="@multiplicity"/>
      </xsl:call-template>
    </xsl:if>

    <!-- xMax for all dimensions -->
    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="objInstance" select="$spaceModelVar"/>
      <xsl:with-param name="instVariable" select="'xMax'"/>
      <xsl:with-param name="value" select="@xMax"/>
    </xsl:call-template>

    <!-- yMax for 2- and 3-Dimensions -->
    <xsl:if test="starts-with(local-name(), 'Two') or starts-with(local-name(), 'Three')">
      <xsl:call-template name="java:callSetterMethod">
        <xsl:with-param name="indent" select="$indent + 1"/>
        <xsl:with-param name="objInstance" select="$spaceModelVar"/>
        <xsl:with-param name="instVariable" select="'yMax'"/>
        <xsl:with-param name="value" select="@yMax"/>
      </xsl:call-template>
    </xsl:if>

    <!-- zMax 3-Dimensions -->
    <xsl:if test="starts-with(local-name(), 'Three')">
      <xsl:call-template name="java:callSetterMethod">
        <xsl:with-param name="indent" select="$indent + 1"/>
        <xsl:with-param name="objInstance" select="$spaceModelVar"/>
        <xsl:with-param name="instVariable" select="'zMax'"/>
        <xsl:with-param name="value" select="@zMax"/>
      </xsl:call-template>
    </xsl:if>

  </xsl:template>

  <xsl:template match="aorsl:SimulationModel" mode="createSimSystem.method.initGlobals">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'initGlobalVariables'"/>
      <xsl:with-param name="content">

        <xsl:apply-templates select="//aorsl:InitialState/aorsl:GlobalVariable" mode="shared.updateGlobalVariable">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- createEnvironmentSimulator  -->
  <xsl:template match="aorsl:SimulationModel" mode="createSimSystem.method.createEnvironment">
    <xsl:param name="indent" required="yes"/>

    <xsl:variable name="spaceReservationSystem" as="xs:boolean">
      <xsl:choose>
        <xsl:when
          test="ends-with(local-name(aorsl:SpaceModel/aorsl:*), 'Grid') and 
            fn:exists(aorsl:SpaceModel/aorsl:*/@gridCellMaxOccupancy) and 
            not(aorsl:SpaceModel/aorsl:*/@gridCellMaxOccupancy eq 'unbounded')">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="false()"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'createEnvironment'"/>
      <xsl:with-param name="content">

        <!--xsl:apply-templates select="//aorsl:InitialState/aorsl:GlobalVariable" mode="shared.updateGlobalVariable">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>
        <xsl:if test="fn:exists(//aorsl:InitialState/aorsl:GlobalVariable)">
          <xsl:call-template name="java:newLine"/>
        </xsl:if-->

        <xsl:variable name="envSimVarName" select="'envSim'"/>

        <xsl:if test="fn:exists(//aorsl:InitialState/*[@hasRandomPosition = true()])">
          <xsl:call-template name="java:newObject">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="class">
              <xsl:choose>
                <xsl:when test="ends-with(local-name(aorsl:SpaceModel/aorsl:*), 'Grid')">
                  <xsl:value-of select="$space.package.positionData"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="$space.package.nonDiscretePositionData"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="varName" select="'positionData'"/>
            <xsl:with-param name="withDeclaration" select="false()"/>
          </xsl:call-template>
        </xsl:if>

        <xsl:apply-templates select="aorsl:Collections/aorsl:Collection" mode="createSimSystem.helper.initCollections">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
        </xsl:apply-templates>

        <xsl:apply-templates
          select="aorsl:EntityTypes/aorsl:PhysicalAgentType | 
                         aorsl:EntityTypes/aorsl:PhysicalObjectType | 
                         aorsl:EntityTypes/aorsl:ObjectType |
                         aorsl:EntityTypes/aorsl:AgentType"
          mode="createSimSystem.helper.initObjectTypes">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
          <xsl:with-param name="spaceReservationSystem" select="$spaceReservationSystem" tunnel="yes"/>
        </xsl:apply-templates>

        <!-- initialize environment rules -->
        <xsl:variable name="rulesVarName" select="fn:concat(jw:lowerWord($core.class.environmentRule), 's')"/>
        <xsl:call-template name="java:newArrayListObject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="generic" select="$core.class.environmentRule"/>
          <xsl:with-param name="name" select="$rulesVarName"/>
        </xsl:call-template>

        <xsl:apply-templates select="aorsl:EnvironmentRules/aorsl:EnvironmentRule" mode="createSimSystem.helper.initEnvRule">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="rulesListVarName" select="$rulesVarName"/>
        </xsl:apply-templates>

        <!-- add the genearted objektList to the environment -->
        <xsl:call-template name="java:callSetterMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="varName" select="'envSim'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="instVariable" select="'rules'"/>
          <xsl:with-param name="value" select="$rulesVarName"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- create AgentSubjects -->
  <xsl:template match="aorsl:SimulationModel" mode="createSimSystem.method.createAgentSubjects">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'createAgentSubjects'"/>
      <xsl:with-param name="content">

        <xsl:apply-templates select="aorsl:EntityTypes/aorsl:PhysicalAgentType" mode="createSimSystem.helper.initAgentSubject">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <xsl:apply-templates select="aorsl:EntityTypes/aorsl:AgentType" mode="createSimSystem.helper.initAgentSubject">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- create AgentSubjectFacets -->
  <xsl:template match="aorsl:EntityTypes" mode="createSimSystem.method.createAgentSubjectFacets">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="'createAgentSubjectFacets'"/>
      <xsl:with-param name="content"> </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- create Statistics -->
  <xsl:template match="aorsl:SimulationModel" mode="createSimSystem.method.createStatistic">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'createStatistic'"/>
      <xsl:with-param name="type" select="$core.class.generalStatistics"/>
      <xsl:with-param name="content">


        <xsl:choose>
          <xsl:when test="fn:exists(aorsl:Statistics)">

            <xsl:variable name="statisticVarName" select="jw:lowerWord($sim.class.simStatistics)"/>
            <xsl:call-template name="java:newObject">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="class" select="$sim.class.simStatistics"/>
              <xsl:with-param name="varName" select="$statisticVarName"/>
            </xsl:call-template>

            <xsl:apply-templates select="aorsl:Statistics/aorsl:Variable" mode="createSimSystem.helper.createStatistic.variable">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="statisticVarName" select="$statisticVarName"/>
            </xsl:apply-templates>
            <xsl:call-template name="java:newLine"/>

            <xsl:apply-templates select="aorsl:Statistics/aorsl:Variable" mode="createSimSystem.helper.createStatistic.parameter">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="statisticVarName" select="$statisticVarName"/>
            </xsl:apply-templates>

            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent +  1"/>
              <xsl:with-param name="value" select="$statisticVarName"/>
            </xsl:call-template>

          </xsl:when>

          <xsl:otherwise>
            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent +  1"/>
              <xsl:with-param name="value" select="'null'"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsl:SimulationModel" mode="createSimSystem.method.createInitialEvents">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="onEveryStep" as="xs:boolean" select="false()"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'createInitialEvents'"/>
      <xsl:with-param name="content">

        <xsl:apply-templates select="aorsl:EntityTypes/aorsl:ExogenousEventType | aorsl:EntityTypes/aorsl:CausedEventType"
          mode="createSimSystem.helper.initEvents">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <!-- init the build in OnEveryStepEnvEvent -->
        <xsl:if test="$onEveryStep">
          <xsl:variable name="varName" select="fn:concat($createdVariablesNamePrefix, jw:lowerWord($core.class.onEveryStepEnvEvent))"/>

          <xsl:call-template name="java:newObject">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="class" select="$core.package.onEveryStepEnvEvent"/>
            <xsl:with-param name="varName" select="$varName"/>
            <xsl:with-param name="args" as="xs:string" select="'1'"/>
          </xsl:call-template>

          <xsl:call-template name="java:callMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="objInstance">
              <xsl:call-template name="java:varByDotNotation">
                <xsl:with-param name="varName" select="'environmentEvents'"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="method" select="'add'"/>
            <xsl:with-param name="args" as="xs:string" select="$varName"/>
          </xsl:call-template>
        </xsl:if>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- executeInitializeRules() -->
  <xsl:template match="aorsl:InitialState" mode="createSimSystem.method.executeInitializeRules">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'executeInitializeRules'"/>
      <xsl:with-param name="content">

        <xsl:for-each select="aorsl:InitializationRule">

          <xsl:variable name="varName" select="fn:concat(jw:lowerWord(@name), '_', position())"/>

          <xsl:call-template name="java:newObject">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="class" select="jw:upperWord(@name)"/>
            <xsl:with-param name="varName" select="$varName"/>
            <xsl:with-param name="args" as="xs:string" select="jw:quote(@name)"/>
          </xsl:call-template>

          <xsl:call-template name="java:if">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="condition">
              <xsl:call-template name="java:boolExpr">
                <xsl:with-param name="value1" select="$varName"/>
                <xsl:with-param name="value2" select="'null'"/>
                <xsl:with-param name="operator" select="'!='"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="thenContent">
              <xsl:call-template name="java:callMethod">
                <xsl:with-param name="indent" select="$indent + 2"/>
                <xsl:with-param name="objInstance" select="$varName"/>
                <xsl:with-param name="method" select="'execute'"/>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>

        </xsl:for-each>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- setActivityFactory() -->
  <xsl:template match="aorsl:SimulationModel" mode="createSimSystem.method.setActivityFactory">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'setActivityFactory'"/>
      <xsl:with-param name="content">

        <xsl:if test="fn:exists(aorsl:EntityTypes/aorsl:ActivityType)">

          <!-- this.envSim.getActivityManager().setActivityFactory(new SimActivityFactory()); -->
          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="objInstance">
              <xsl:call-template name="java:callGetterMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="objInstance">
                  <xsl:call-template name="java:varByDotNotation">
                    <xsl:with-param name="varName" select="'envSim'"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="instVariable" select="'ActivityManager'"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="instVariable" select="'ActivityFactory'"/>
            <xsl:with-param name="value">
              <xsl:call-template name="java:newObject">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="class" select="$sim.class.simActivityFactory"/>
                <xsl:with-param name="isVariable" select="true()"/>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>

        </xsl:if>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="aorsl:SimulationScenario" mode="createSimSystem.method.main">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="static" select="true()"/>
      <xsl:with-param name="name" select="'main'"/>
      <xsl:with-param name="parameterList" as="xs:string*">
        <xsl:value-of select="'String[] args'"/>
      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:variable name="simVarName" select="jw:lowerWord(aorsl:SimulationModel/@modelName)"/>

        <xsl:call-template name="java:newObject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="class" select="$sim.class.simulatorMain"/>
          <xsl:with-param name="varName" select="$simVarName"/>
        </xsl:call-template>

        <xsl:call-template name="java:callSetterMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="objInstance" select="$simVarName"/>
          <xsl:with-param name="instVariable" select="'dataBus'"/>
          <xsl:with-param name="value">
            <xsl:call-template name="java:newObject">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="isVariable" select="true()"/>
              <xsl:with-param name="class" select="$data.package.dataBus"/>
              <xsl:with-param name="args">
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="name">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="name" select="$data.package.dataBus"/>
                      <xsl:with-param name="varName" select="'LoggerType'"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="varName" select="'FULL_XML_LOGGER'"/>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="objInstance" select="$simVarName"/>
          <xsl:with-param name="method" select="'initialize'"/>
        </xsl:call-template>

        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="objInstance" select="$simVarName"/>
          <xsl:with-param name="method" select="'runSimulation'"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>


  <!-- ****************************************************************** -->
  <!--                                             Helpers                                                    -->
  <!-- ****************************************************************** -->
  <!-- set the initial PhysicalAgentObjects -->
  <xsl:template match="aorsl:PhysicalAgentType" mode="createSimSystem.helper.initObjectTypes">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="envSimVarName" required="yes" as="xs:string"/>

    <xsl:variable name="varName" select="jw:lowerWord(@name)"/>
    <xsl:variable name="className" select="jw:upperWord(@name)"/>

    <xsl:if
      test="fn:exists(//aorsl:InitialState/aorsl:PhysicalAgent[@type = current()/@name and not(@objectVariable)]) or 
                fn:exists(//aorsl:InitialState/aorsl:PhysicalAgents[@type = current()/@name and not(@objectVariable)])">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$className"/>
        <xsl:with-param name="varName" select="$varName"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:for-each select="//aorsl:InitialState/aorsl:PhysicalAgent[@type = current()/@name and @objectVariable]">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$className"/>
        <xsl:with-param name="varName" select="@objectVariable"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:for-each>

    <xsl:apply-templates
      select="//aorsl:InitialState/aorsl:PhysicalAgent[@type = current()/@name] | //aorsl:InitialState/aorsl:PhysicalAgents[@type = current()/@name]"
      mode="shared.helper.initAORObjects.manager">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
      <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
    </xsl:apply-templates>
    <xsl:call-template name="java:newLine"/>
  </xsl:template>

  <!-- set the initial PhysObjekts -->
  <xsl:template match="aorsl:PhysicalObjectType" mode="createSimSystem.helper.initObjectTypes">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="envSimVarName" required="yes" as="xs:string"/>

    <xsl:variable name="className" select="jw:upperWord(@name)"/>
    <xsl:variable name="varName" select="jw:lowerWord($className)"/>

    <xsl:if
      test="fn:exists(//aorsl:InitialState/aorsl:PhysicalObject[@type = current()/@name and not(@objectVariable)]) or
            fn:exists(//aorsl:InitialState/aorsl:PhysicalObjects[@type = current()/@name and not(@objectVariable)])">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$className"/>
        <xsl:with-param name="varName" select="$varName"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:for-each select="//aorsl:InitialState/aorsl:PhysicalObject[@type = current()/@name and @objectVariable]">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$className"/>
        <xsl:with-param name="varName" select="@objectVariable"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:for-each>

    <xsl:apply-templates
      select="//aorsl:InitialState/aorsl:PhysicalObject[@type = current()/@name] | 
              //aorsl:InitialState/aorsl:PhysicalObjects[@type = current()/@name]"
      mode="shared.helper.initAORObjects.manager">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
      <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
    </xsl:apply-templates>

    <xsl:call-template name="java:newLine"/>
  </xsl:template>

  <!-- set the initial Objekts -->
  <xsl:template match="aorsl:ObjectType" mode="createSimSystem.helper.initObjectTypes">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="envSimVarName" required="yes" as="xs:string"/>
    <xsl:if test="fn:exists(//aorsl:InitialState/aorsl:Object[@type = current()/@name])">

      <xsl:variable name="className" select="jw:upperWord(@name)"/>
      <xsl:variable name="varName" select="jw:lowerWord($className)"/>

      <xsl:if
        test="fn:exists(//aorsl:InitialState/aorsl:Object[@type = current()/@name and not (@objectVariable)]) or 
                            fn:exists(//aorsl:InitialState/aorsl:Objects[@type = current()/@name and not(@objectVariable)])">
        <xsl:call-template name="java:newObject">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="class" select="$className"/>
          <xsl:with-param name="varName" select="$varName"/>
          <xsl:with-param name="withDeclaration" select="false()"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:for-each select="//aorsl:InitialState/aorsl:Object[@type = current()/@name and @objectVariable]">
        <xsl:call-template name="java:newObject">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="class" select="$className"/>
          <xsl:with-param name="varName" select="@objectVariable"/>
          <xsl:with-param name="withDeclaration" select="false()"/>
        </xsl:call-template>
      </xsl:for-each>
      <!-- TODO: implement Objects -->
      <xsl:apply-templates select="//aorsl:InitialState/aorsl:Object[@type = current()/@name]" mode="shared.helper.initAORObjects.manager">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="className" select="$className"/>
        <xsl:with-param name="varName" select="$varName"/>
        <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
      </xsl:apply-templates>
      <!--
    <xsl:apply-templates select="//aorsl:InitialState/aorsl:ObjectSet[@type = current()/@name]" mode="shared.helper.initAORObjectsSet">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
      <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
    </xsl:apply-templates>
    -->
      <xsl:call-template name="java:newLine"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="aorsl:AgentType" mode="createSimSystem.helper.initObjectTypes">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="envSimVarName" required="yes" as="xs:string"/>

    <xsl:variable name="varName" select="jw:lowerWord(@name)"/>
    <xsl:variable name="className" select="jw:upperWord(@name)"/>

    <xsl:if
      test="fn:exists(//aorsl:InitialState/aorsl:Agent[@type = current()/@name and not(@objectVariable)]) or 
                fn:exists(//aorsl:InitialState/aorsl:Agents[@type = current()/@name and not(@objectVariable)])">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$className"/>
        <xsl:with-param name="varName" select="$varName"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:for-each select="//aorsl:InitialState/aorsl:Agent[@type = current()/@name and @objectVariable]">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$className"/>
        <xsl:with-param name="varName" select="@objectVariable"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:for-each>

    <xsl:apply-templates
      select="//aorsl:InitialState/aorsl:Agent[@type = current()/@name] | //aorsl:InitialState/aorsl:Agents[@type = current()/@name]"
      mode="shared.helper.initAORObjects.manager">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
      <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
    </xsl:apply-templates>
    <xsl:call-template name="java:newLine"/>

  </xsl:template>

  <!-- set the EnvironmentRules -->
  <xsl:template match="aorsl:EnvironmentRule" mode="createSimSystem.helper.initEnvRule">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="rulesListVarName" required="yes"/>

    <xsl:variable name="ruleVarName" select="jw:lowerWord(@name)"/>
    <xsl:call-template name="java:newObject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="class" select="@name"/>
      <xsl:with-param name="varName" select="$ruleVarName"/>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:value-of select="jw:quote(@name)"/>
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="name" select="'this'"/>
          <xsl:with-param name="varName" select="'envSim'"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance" select="$rulesListVarName"/>
      <xsl:with-param name="method" select="'add'"/>
      <xsl:with-param name="args" as="xs:string*" select="$ruleVarName"/>
    </xsl:call-template>
    <xsl:call-template name="java:newLine"/>

  </xsl:template>

  <!-- set the initial physAgentSubjects -->
  <xsl:template match="aorsl:PhysicalAgentType" mode="createSimSystem.helper.initAgentSubject">
    <xsl:param name="indent" required="yes"/>

    <xsl:variable name="className" select="fn:concat(jw:upperWord(@name), $prefix.agentSubject)"/>
    <xsl:variable name="varName" select="fn:concat($createdVariablesNamePrefix, jw:lowerWord($className))"/>

    <xsl:if
      test="fn:exists(//aorsl:InitialState/aorsl:PhysicalAgent[@type = current()/@name]) or 
            fn:exists(//aorsl:InitialState/aorsl:PhysicalAgents[@type = current()/@name])">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$className"/>
        <xsl:with-param name="varName" select="$varName"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:call-template name="java:newLine"/>

    <xsl:apply-templates select="//aorsl:InitialState/aorsl:PhysicalAgent[@type = current()/@name]" mode="shared.helper.initAgentSubject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
    </xsl:apply-templates>

    <xsl:apply-templates select="//aorsl:InitialState/aorsl:PhysicalAgents[@type = current()/@name]" mode="shared.helper.initAgentSubjectSet">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- set the initial agentSubjects -->
  <xsl:template match="aorsl:AgentType" mode="createSimSystem.helper.initAgentSubject">
    <xsl:param name="indent" required="yes"/>

    <xsl:variable name="className" select="fn:concat(jw:upperWord(@name), $prefix.agentSubject)"/>
    <xsl:variable name="varName" select="jw:lowerWord($className)"/>

    <xsl:call-template name="java:newObject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="class" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
      <xsl:with-param name="withDeclaration" select="false()"/>
    </xsl:call-template>

    <xsl:call-template name="java:newLine"/>
    <xsl:apply-templates select="//aorsl:InitialState/aorsl:Agent[@type = current()/@name]" mode="shared.helper.initAgentSubject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
    </xsl:apply-templates>

    <xsl:apply-templates select="//aorsl:InitialState/aorsl:Agents[@type = current()/@name]" mode="shared.helper.initAgentSubjectSet">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- set initial ExogenousEvent  -->
  <xsl:template match="aorsl:ExogenousEventType | aorsl:CausedEventType" mode="createSimSystem.helper.initEvents">
    <xsl:param name="indent" required="yes"/>

    <xsl:variable name="className" select="jw:upperWord(@name)"/>
    <xsl:variable name="varName" select="fn:concat($createdVariablesNamePrefix, jw:lowerWord($className))"/>

    <xsl:if
      test="(local-name() eq 'ExogenousEventType' and //aorsl:InitialState/aorsl:ExogenousEvent[@type = current()/@name]) or 
             local-name() eq 'CausedEventType' and //aorsl:InitialState/aorsl:CausedEvent[@type = current()/@name]">
      <xsl:call-template name="java:newLine"/>
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$className"/>
        <xsl:with-param name="varName" select="$varName"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:if>

    <xsl:choose>
      <xsl:when test="local-name() eq 'ExogenousEventType'">
        <xsl:apply-templates select="//aorsl:InitialState/aorsl:ExogenousEvent[@type = current()/@name]"
          mode="createSimSystem.helper.initEvents.init">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="className" select="$className"/>
          <xsl:with-param name="varName" select="$varName"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:when test="local-name() eq 'CausedEventType'">
        <xsl:apply-templates select="//aorsl:InitialState/aorsl:CausedEvent[@type = current()/@name]" mode="createSimSystem.helper.initEvents.init">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="className" select="$className"/>
          <xsl:with-param name="varName" select="$varName"/>
        </xsl:apply-templates>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="aorsl:CausedEvent | aorsl:ExogenousEvent" mode="createSimSystem.helper.initEvents.init">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="className" as="xs:string" required="yes"/>
    <xsl:param name="varName" as="xs:string" required="yes"/>

    <xsl:call-template name="java:newLine"/>
    <xsl:call-template name="java:newObject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="class" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
      <xsl:with-param name="isVariable" select="true()"/>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:if test="@name">
          <xsl:value-of select="jw:quote(@name)"/>
        </xsl:if>
        <xsl:value-of select="@occurrenceTime"/>
      </xsl:with-param>
    </xsl:call-template>

    <xsl:variable name="eventType" select="@type"/>

    <xsl:for-each select="aorsl:Slot[@property != 'occurrenceTime']">
      <xsl:call-template name="java:callSetterMethod">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="objInstance" select="$varName"/>
        <xsl:with-param name="instVariable" select="@property"/>
        <xsl:with-param name="value">
          <xsl:apply-templates select="." mode="assistents.getSlotValue"/>
        </xsl:with-param>
        <xsl:with-param name="valueType">
          <xsl:value-of select="//aorsl:EntityTypes/aorsl:*[@name = $eventType]/aorsl:*[@name = current()/@property]/@type"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:for-each>

    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="varName" select="'environmentEvents'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="method" select="'add'"/>
      <xsl:with-param name="args" as="xs:string*" select="$varName"/>
    </xsl:call-template>

  </xsl:template>

  <!-- statisticvariables -->
  <!-- it will be 2 times in the SimStatistc; first as a public variable, is for using in Simulation (simpler to write the description);
  and second, in a list, is to analyze without reflection-->
  <xsl:template match="aorsl:Variable" mode="createSimSystem.helper.createStatistic.variable">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="statisticVarName" required="yes" as="xs:string"/>

    <!-- the variables are defined as static for a simpler access in the description expressions -->
    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="name" select="$sim.class.simStatistics"/>
          <xsl:with-param name="varName" select="@name"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="value">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="name">
            <xsl:call-template name="java:newObject">
              <xsl:with-param name="class" select="$sim.class.simStatistics"/>
              <xsl:with-param name="isVariable" select="true()"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="varName">
            <xsl:call-template name="java:newObject">
              <xsl:with-param name="class" select="fn:concat($sim.class.simStatistics.Variable, jw:upperWord(@name))"/>
              <xsl:with-param name="args" as="xs:string*">

                <!-- name -->
                <xsl:value-of select="jw:quote(@name)"/>

                <!-- type -->
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="name" select="$core.class.statVarDataTypeEnumLit"/>
                  <xsl:with-param name="varName" select="@dataType"/>
                </xsl:call-template>

                <xsl:choose>
                  <xsl:when
                    test="aorsl:Source/aorsl:ObjectProperty or 
                    aorsl:Source/aorsl:ObjectTypeExtensionSize or 
                    aorsl:Source/aorsl:ResourceUtilization">

                    <xsl:variable name="node" as="node()" select="aorsl:Source/aorsl:*"/>

                    <!-- this.getEnvironmentSimulator().getObjectById([@objectIdRef])  or -->
                    <!-- this.getEnvironmentSimulator().getObjectsByType([@objectType].class) -->
                    <xsl:call-template name="java:callMethod">
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="objInstance">
                        <xsl:call-template name="java:callGetterMethod">
                          <xsl:with-param name="inLine" select="true()"/>
                          <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
                        </xsl:call-template>
                      </xsl:with-param>
                      <xsl:with-param name="method">
                        <xsl:choose>
                          <xsl:when test="$node/@objectIdRef or $node/@resourceObjectIdRef">
                            <xsl:value-of select="'getObjectById'"/>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:value-of select="'getListReferenceObjectsByType'"/>
                          </xsl:otherwise>
                        </xsl:choose>
                      </xsl:with-param>
                      <xsl:with-param name="args" as="xs:string">
                        <xsl:choose>
                          <xsl:when test="$node/@objectIdRef or $node/@resourceObjectIdRef">
                            <xsl:value-of select="$node/@objectIdRef | $node/@resourceObjectIdRef"/>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:call-template name="java:varByDotNotation">
                              <xsl:with-param name="name" select="$node/@objectType | $node/@resourceObjectType"/>
                              <xsl:with-param name="varName" select="'class'"/>
                            </xsl:call-template>
                          </xsl:otherwise>
                        </xsl:choose>
                      </xsl:with-param>
                    </xsl:call-template>

                  </xsl:when>
                </xsl:choose>

              </xsl:with-param>
              <xsl:with-param name="isVariable" select="true()"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>

          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance" select="$statisticVarName"/>
      <xsl:with-param name="method" select="'addStatisticVariable'"/>
      <xsl:with-param name="args">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="name" select="$sim.class.simStatistics"/>
          <xsl:with-param name="varName" select="@name"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsl:Variable" mode="createSimSystem.helper.createStatistic.parameter">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="statisticVarName" required="yes" as="xs:string"/>

    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="name" select="$sim.class.simStatistics"/>
          <xsl:with-param name="varName" select="@name"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="instVariable" select="'statVarDataSource'"/>
      <xsl:with-param name="value">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="name" select="$core.enum.statVarDataSourceEnumLit"/>
          <xsl:with-param name="varName">
            <xsl:choose>
              <xsl:when test="exists(aorsl:Source)">
                <xsl:value-of select="local-name(aorsl:Source/aorsl:*[1])"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="'Default'"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

    <!-- set specific statistic variable properties -->
    <xsl:apply-templates select="aorsl:Source/aorsl:*" mode="createSimSystem.helper.createStatistic.source">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="statVarName" select="@name"/>
      <xsl:with-param name="statisticVarName" select="$statisticVarName"/>
    </xsl:apply-templates>

    <!-- set initValue -->
    <xsl:if test="fn:exists(@initialValue)">
      <xsl:call-template name="java:callSetterMethod">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="objInstance">
          <xsl:call-template name="java:varByDotNotation">
            <xsl:with-param name="name" select="$sim.class.simStatistics"/>
            <xsl:with-param name="varName" select="@name"/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="instVariable" select="'value'"/>
        <xsl:with-param name="value" select="@initialValue"/>
      </xsl:call-template>
    </xsl:if>

    <!-- set aggregat function -->
    <xsl:if test="exists(aorsl:Source/@aggregationFunction)">
      <xsl:call-template name="java:callSetterMethod">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="objInstance">
          <xsl:call-template name="java:varByDotNotation">
            <xsl:with-param name="name" select="$sim.class.simStatistics"/>
            <xsl:with-param name="varName" select="@name"/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="instVariable" select="'aggregFun'"/>
        <xsl:with-param name="value">
          <xsl:call-template name="java:varByDotNotation">
            <xsl:with-param name="name" select="$core.enum.aggregFunEnumLit"/>
            <xsl:with-param name="varName" select="aorsl:Source/@aggregationFunction"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>
    <xsl:call-template name="java:newLine"/>

  </xsl:template>

  <xsl:template match="aorsl:GlobalVariable" mode="createSimSystem.helper.createStatistic.source">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="statVarName" as="xs:string" required="yes"/>

  </xsl:template>

  <xsl:template match="aorsl:StatisticsVariable" mode="createSimSystem.helper.createStatistic.source">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="statVarName" as="xs:string" required="yes"/>

    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="name" select="$sim.class.simStatistics"/>
          <xsl:with-param name="varName" select="$statVarName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="instVariable" select="'statisticsVariable'"/>
      <xsl:with-param name="value">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="name" select="$sim.class.simStatistics"/>
          <xsl:with-param name="varName" select="@name"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsl:ObjectProperty" mode="createSimSystem.helper.createStatistic.source">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="statVarName" as="xs:string" required="yes"/>

    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="name" select="$sim.class.simStatistics"/>
          <xsl:with-param name="varName" select="$statVarName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="instVariable" select="'sourceObjectProperty'"/>
      <xsl:with-param name="value" select="jw:quote(@property)"/>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsl:ObjectTypeExtensionSize" mode="createSimSystem.helper.createStatistic.source">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="statVarName" as="xs:string" required="yes"/>
  </xsl:template>

  <xsl:template match="aorsl:ResourceUtilization" mode="createSimSystem.helper.createStatistic.source">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="statVarName" as="xs:string" required="yes"/>

    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="name" select="$sim.class.simStatistics"/>
          <xsl:with-param name="varName" select="$statVarName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="instVariable" select="'activityType'"/>
      <xsl:with-param name="value" select="jw:quote(@activityType)"/>
    </xsl:call-template>

    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="name" select="$sim.class.simStatistics"/>
          <xsl:with-param name="varName" select="$statVarName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="instVariable" select="'sourceObjectType'"/>
      <xsl:with-param name="value" select="jw:quote(@resourceObjectType)"/>
    </xsl:call-template>


    <xsl:if test="not(@objectIdRef)">

      <xsl:call-template name="java:callMethod">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="objInstance">
          <xsl:call-template name="java:varByDotNotation">
            <xsl:with-param name="varName" select="'dataBus'"/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="method" select="'addDestroyObjektEventListener'"/>
        <xsl:with-param name="args">
          <xsl:call-template name="java:varByDotNotation">
            <xsl:with-param name="name" select="$sim.class.simStatistics"/>
            <xsl:with-param name="varName" select="$statVarName"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>

    </xsl:if>

  </xsl:template>

  <xsl:template match="aorsl:ValueExpr" mode="createSimSystem.helper.createStatistic.source">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="statisticVarName" as="xs:string" required="yes"/>
    <xsl:param name="statVarName" as="xs:string" required="yes"/>

    <xsl:if test="@language eq $output.language">

      <xsl:variable name="computeOnlyAtEnd" as="xs:boolean">
        <xsl:choose>
          <xsl:when test="../@computeOnlyAtEnd eq 'true'">
            <xsl:value-of select="true()"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="false()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <xsl:if test="boolean($computeOnlyAtEnd)">

        <xsl:call-template name="java:callSetterMethod">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="$sim.class.simStatistics"/>
              <xsl:with-param name="varName" select="$statVarName"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="instVariable" select="'computeOnlyAtEnd'"/>
          <xsl:with-param name="value" select="'true'"/>
        </xsl:call-template>

      </xsl:if>

    </xsl:if>

  </xsl:template>

  <!-- create GridCells -->
  <xsl:template match="aorsl:GridCells" mode="createSimSystem.helper.createGridCells">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="spaceModelVar" required="yes" as="xs:string" tunnel="yes"/>
    <xsl:param name="observeGridCells" select="false()" tunnel="yes" as="xs:boolean"/>

    <xsl:if test="fn:exists(aorsl:Slot)">
      <xsl:call-template name="java:newLine"/>
      <xsl:call-template name="createGridLoop">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="startX" select="string($space.ORDINATEBASE)"/>
        <xsl:with-param name="endX">
          <xsl:value-of>
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="objInstance" select="$spaceModelVar"/>
                  <xsl:with-param name="instVariable" select="'space'"/>
                  <xsl:with-param name="inLine" select="true()"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="instVariable" select="'xSize'"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
            <xsl:text> + </xsl:text>
            <xsl:value-of select="$space.ORDINATEBASE"/>
            <xsl:text> - 1</xsl:text>
          </xsl:value-of>
        </xsl:with-param>
        <xsl:with-param name="startY" select="string($space.ORDINATEBASE)"/>
        <xsl:with-param name="endY">
          <xsl:value-of>
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="objInstance" select="$spaceModelVar"/>
                  <xsl:with-param name="instVariable" select="'space'"/>
                  <xsl:with-param name="inLine" select="true()"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="instVariable" select="'ySize'"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
            <xsl:value-of select="fn:concat(' + ', $space.ORDINATEBASE, ' - 1')"/>
          </xsl:value-of>

        </xsl:with-param>
        <xsl:with-param name="spaceModelVar" select="$spaceModelVar"/>
      </xsl:call-template>
    </xsl:if>

    <xsl:apply-templates select="aorsl:GridCell | aorsl:GridCellSet" mode="createSimSystem.helper.createGridCells">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates>

    <!-- generalSpaceModel.fireInitEvent(generalSpaceModel.getSpace().getSpaceCells()); -->
    <xsl:if test="$observeGridCells">
      <xsl:call-template name="java:callMethod">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="objInstance" select="$spaceModelVar"/>
        <xsl:with-param name="method" select="'fireInitEvent'"/>
        <xsl:with-param name="args">
          <xsl:call-template name="java:callGetterMethod">
            <xsl:with-param name="inLine" select="true()"/>
            <xsl:with-param name="objInstance">
              <xsl:call-template name="java:callGetterMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="objInstance" select="$spaceModelVar"/>
                <xsl:with-param name="instVariable" select="'space'"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="instVariable" select="'spaceCells'"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="java:callMethod">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="objInstance" select="$spaceModelVar"/>
        <xsl:with-param name="method" select="'initPropertyChangeListener'"/>
        <xsl:with-param name="args">
          <xsl:call-template name="java:varByDotNotation">
            <xsl:with-param name="varName" select="$databus.class.dataBus"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>

  </xsl:template>

  <!-- one special GridCell -->
  <xsl:template match="aorsl:GridCell" mode="createSimSystem.helper.createGridCells">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="spaceModelVar" required="yes" tunnel="yes"/>
    <xsl:param name="gridCellVariable" as="xs:string" required="yes" tunnel="yes"/>
    <xsl:param name="observeGridCells" as="xs:boolean" select="false()" tunnel="yes"/>

    <xsl:call-template name="java:newLine"/>
    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="$gridCellVariable"/>
      <xsl:with-param name="value">
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="inLine" select="true()"/>
          <xsl:with-param name="objInstance" select="$spaceModelVar"/>
          <xsl:with-param name="method" select="'getGridCell'"/>
          <xsl:with-param name="args" as="xs:string*" select="(@x, @y)"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>


    <xsl:call-template name="java:if">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="condition">
        <xsl:call-template name="java:boolExpr">
          <xsl:with-param name="value1" select="$gridCellVariable"/>
          <xsl:with-param name="value2" select="'null'"/>
          <xsl:with-param name="operator" select="'!='"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="thenContent">

        <xsl:apply-templates select="aorsl:Slot" mode="createSimSystem.helper.createGridCells.setSlot">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="spaceModelVar" select="$spaceModelVar"/>
        </xsl:apply-templates>

      </xsl:with-param>
      <xsl:with-param name="elseContent">
        <xsl:call-template name="java:systemPrintln">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="err" select="true()"/>
          <xsl:with-param name="value" select="'The cellrequest is out of grid-space!'"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- a GridCellSet -->
  <xsl:template match="aorsl:GridCellSet" mode="createSimSystem.helper.createGridCells">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="spaceModelVar" required="yes" tunnel="yes"/>

    <!-- x-range -->
    <xsl:variable name="startX">
      <xsl:choose>
        <xsl:when test="fn:exists(@startX)">
          <xsl:value-of select="@startX"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="string($space.ORDINATEBASE)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="endX">
      <xsl:choose>
        <xsl:when test="fn:exists(@endX)">
          <xsl:value-of select="@endX"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:variable name="val">
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="objInstance" select="$spaceModelVar"/>
                  <xsl:with-param name="instVariable" select="'space'"/>
                  <xsl:with-param name="inLine" select="true()"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="instVariable" select="'xSize'"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:variable>
          <xsl:value-of select="fn:concat($val, ' - 1')"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- y-range -->
    <xsl:variable name="startY">
      <xsl:choose>
        <xsl:when test="fn:exists(@startY)">
          <xsl:value-of select="@startY"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="string($space.ORDINATEBASE)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="endY">
      <xsl:choose>
        <xsl:when test="fn:exists(@endY)">
          <xsl:value-of select="@endY"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:variable name="val">
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="objInstance" select="$spaceModelVar"/>
                  <xsl:with-param name="instVariable" select="'space'"/>
                  <xsl:with-param name="inLine" select="true()"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="instVariable" select="'ySize'"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:variable>
          <xsl:value-of select="fn:concat($val, ' - 1')"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:call-template name="java:newLine"/>
    <xsl:call-template name="createGridLoop">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="startX" select="$startX"/>
      <xsl:with-param name="endX" select="$endX"/>
      <xsl:with-param name="startY" select="$startY"/>
      <xsl:with-param name="endY" select="$endY"/>
      <xsl:with-param name="spaceModelVar" select="$spaceModelVar"/>
    </xsl:call-template>

  </xsl:template>


  <!-- creates a 2-dim-loop for initialize grids -->
  <xsl:template name="createGridLoop">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="spaceModelVar" required="yes" as="xs:string"/>
    <xsl:param name="startX" required="yes" as="xs:string"/>
    <xsl:param name="endX" required="yes" as="xs:string"/>
    <xsl:param name="startY" required="yes" as="xs:string"/>
    <xsl:param name="endY" required="yes" as="xs:string"/>
    <xsl:param name="gridCellVariable" tunnel="yes" as="xs:string" required="yes"/>
    <xsl:param name="observeGridCells" as="xs:boolean" select="false()" tunnel="yes"/>

    <xsl:variable name="columnVarName" select="'column'"/>
    <xsl:variable name="rowVarName" select="'row'"/>
    <xsl:call-template name="java:for-loop">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="loopVariable" select="$columnVarName"/>
      <xsl:with-param name="start" select="$startX"/>
      <xsl:with-param name="condition">
        <xsl:call-template name="java:boolExpr">
          <xsl:with-param name="value1" select="$columnVarName"/>
          <xsl:with-param name="value2" select="$endX"/>
          <xsl:with-param name="operator" select="'&lt;='"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="increment" select="1"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:for-loop">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="loopVariable" select="$rowVarName"/>
          <xsl:with-param name="start" select="$startY"/>
          <xsl:with-param name="condition">
            <xsl:call-template name="java:boolExpr">
              <xsl:with-param name="value1" select="$rowVarName"/>
              <xsl:with-param name="value2" select="$endY"/>
              <xsl:with-param name="operator" select="'&lt;='"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="increment" select="1"/>
          <xsl:with-param name="content">

            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent + 2"/>
              <xsl:with-param name="name" select="$gridCellVariable"/>
              <xsl:with-param name="value">
                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="objInstance" select="$spaceModelVar"/>
                  <xsl:with-param name="method" select="'getGridCell'"/>
                  <xsl:with-param name="args" as="xs:string*" select="($columnVarName, $rowVarName)"/>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>

            <xsl:call-template name="java:if">
              <xsl:with-param name="indent" select="$indent + 2"/>
              <xsl:with-param name="condition">
                <xsl:call-template name="java:boolExpr">
                  <xsl:with-param name="value1" select="$gridCellVariable"/>
                  <xsl:with-param name="value2" select="'null'"/>
                  <xsl:with-param name="operator" select="'!='"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="thenContent">

                <xsl:apply-templates select="./aorsl:Slot" mode="createSimSystem.helper.createGridCells.setSlot">
                  <xsl:with-param name="indent" select="$indent + 2"/>
                  <xsl:with-param name="spaceModelVar" select="$spaceModelVar"/>
                </xsl:apply-templates>

              </xsl:with-param>
              <xsl:with-param name="elseContent">
                <xsl:call-template name="java:systemPrintln">
                  <xsl:with-param name="indent" select="$indent + 3"/>
                  <xsl:with-param name="err" select="true()"/>
                  <xsl:with-param name="value" select="'The cellrequest is out of grid-space!'"/>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>

          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="aorsl:Slot" mode="createSimSystem.helper.createGridCells.setSlot">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="spaceModelVar" required="yes" as="xs:string"/>
    <xsl:param name="gridCellVariable" tunnel="yes" as="xs:string" required="yes"/>

    <!-- init the property -->
    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="objInstance" select="$gridCellVariable"/>
      <xsl:with-param name="instVariable" select="@property"/>
      <xsl:with-param name="value">
        <xsl:apply-templates select="." mode="assistents.getSlotValue"/>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- Collections -->
  <xsl:template match="aorsl:Collection" mode="createSimSystem.helper.initCollections">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="envSimVarName" required="yes" as="xs:string"/>

    <xsl:call-template name="java:newLine"/>
    <!-- TODO: check if @id exists -->

    <xsl:choose>
      <xsl:when test="fn:exists(@id)">
        <xsl:variable name="collVarName">
          <xsl:value-of select="jw:createCollectionVarname(.)"/>
        </xsl:variable>
        <xsl:variable name="collType">
          <xsl:choose>
            <xsl:when test="@type = 'FIFO_QUEUE' or @type = 'LIFO_QUEUE' or @type ='SET' ">
              <xsl:value-of select="@type"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:message>
                <xsl:text>[ERROR] Unknown collection-type [</xsl:text>
                <xsl:value-of select="@type"/>
                <xsl:text>]!</xsl:text>
              </xsl:message>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>

        <xsl:call-template name="java:newObject">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="class" select="$collection.class.aORCollection"/>
          <xsl:with-param name="generic" select="@itemType"/>
          <xsl:with-param name="varName" select="$collVarName"/>
          <xsl:with-param name="args" as="xs:string*">
            <xsl:value-of select="@id"/>
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name">
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="name" select="$collection.class.aORCollection"/>
                  <xsl:with-param name="varName" select="'CollectionType'"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="varName" select="$collType"/>
            </xsl:call-template>
            <xsl:value-of select="jw:quote(@itemType)"/>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:if test="fn:exists(@name)">
          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="objInstance" select="$collVarName"/>
            <xsl:with-param name="instVariable" select="'name'"/>
            <xsl:with-param name="value" select="@name"/>
            <xsl:with-param name="valueType" select="'String'"/>
          </xsl:call-template>
        </xsl:if>

        <!-- add to the map -->
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="varName" select="$envSimVarName"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="method" select="'addCollection'"/>
          <xsl:with-param name="args" select="$collVarName"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message>No ID for collection found.</xsl:message>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!-- init the global variables -->
  <xsl:template match="aorsl:GlobalVariable" mode="createSimSystem.initGlobals">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:apply-templates select="." mode="shared.updateGlobalVariable">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates>

  </xsl:template>

</xsl:stylesheet>
