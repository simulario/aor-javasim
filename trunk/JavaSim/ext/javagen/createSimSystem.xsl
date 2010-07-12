<?xml version="1.0" encoding="UTF-8"?>
<!--
        This transformation creates the main class for simulation based on a given aorsml file.

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

  <xsl:include href="model/envsim/createInitializationRules.xsl"/>

  <!--*****-->
  <!--class-->
  <!--*****-->
  <xsl:template name="createSimSystem">
    <xsl:apply-templates select="aorsml:SimulationScenario" mode="createSimSystem.SimSystem">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

  <!--creates class-->
  <xsl:template match="aorsml:SimulationScenario" mode="createSimSystem.SimSystem">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="aorsml:classFile">
      <xsl:with-param name="path" select="$sim.path.controller"/>
      <xsl:with-param name="name" select="$sim.class.simulatorMain"/>

      <xsl:with-param name="content">

        <xsl:call-template name="java:imports">
          <xsl:with-param name="importList" as="xs:string*">
            <xsl:value-of select="$core.package.spaceModel"/>
            <xsl:value-of select="$core.package.simulationEngine"/>
            <xsl:value-of select="fn:concat($core.package.model.envSim, '.*')"/>
            <xsl:if
              test="fn:exists(aorsml:SimulationModel/aorsml:EntityTypes/aorsml:CausedEventType) or 
                        fn:exists(aorsml:SimulationModel/aorsml:EntityTypes/aorsml:PerceptionEventType) or 
                        fn:exists(aorsml:SimulationModel/aorsml:EntityTypes/aorsml:ExogenousEventType) or 
                        fn:exists(aorsml:SimulationModel/aorsml:EntityTypes/aorsml:ActionEventType)">
              <xsl:value-of select="fn:concat($sim.package.model.envevent, '.*')"/>
            </xsl:if>
            <xsl:if test="fn:exists(//aorsml:Collections/aorsml:Collection)">
              <xsl:value-of select="$collection.package.aORCollection"/>
            </xsl:if>

            <xsl:if
              test="fn:exists(aorsml:SimulationModel/aorsml:EntityTypes/aorsml:PhysicalAgentType) or 
                    fn:exists(aorsml:SimulationModel/aorsml:EntityTypes/aorsml:AgentType)">
              <xsl:value-of select="fn:concat($sim.package.model.agentsimulator, '.*')"/>
            </xsl:if>

            <!-- <xsl:value-of select="fn:concat($sim.package.model.internalevent, '.*')"/> -->

            <xsl:value-of select="$core.package.generalStatistics"/>
            <xsl:if test="fn:exists(aorsml:SimulationModel/aorsml:Statistics/aorsml:Variable)">
              <!-- <xsl:value-of select="fn:concat($core.package.statistics, '.*')"/> -->
              <xsl:value-of select="$core.package.statVarDataTypeEnumLit"/>
              <xsl:value-of select="$core.package.statVarDataSourceEnumLit"/>
              <xsl:if test="fn:exists(aorsml:SimulationModel/aorsml:Statistics/aorsml:Variable/aorsml:Source/@aggregationFunction)">
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
            <xsl:if test="fn:exists(aorsml:SimulationModel/aorsml:SpaceModel)">
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
            <xsl:apply-templates select="aorsml:SimulationParameters" mode="createSimSystem.method.getSimulationParameters">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- create SimulationModel -->
            <xsl:apply-templates select="aorsml:SimulationModel" mode="createSimSystem.method.createSimModel">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- createSpaceModel -->
            <xsl:apply-templates select="aorsml:SimulationModel" mode="createSimSystem.method.createSpaceModel">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- it is used to desid which getRandomPosition() - version is called in shared.helper.initAORObjects 
            <xsl:variable name="discreteSpace" as="xs:boolean">
              <xsl:choose>
                <xsl:when test="ends-with(local-name(aorsml:SimulationModel/aorsml:SpaceModel/aorsml:*), 'Grid')">
                  <xsl:value-of select="true()"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="false()"/>
                </xsl:otherwise>
              </xsl:choose>
              </xsl:variable> -->
            
            <xsl:apply-templates select="aorsml:SimulationModel" mode="createSimSystem.method.initGlobals">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- createEnvironmentSimulator -->
            <xsl:apply-templates select="aorsml:SimulationModel" mode="createSimSystem.method.createEnvironment">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <!--xsl:with-param name="discreteSpace" select="$discreteSpace" tunnel="yes"/-->
            </xsl:apply-templates>

            <!-- create AgentSubjects -->
            <xsl:apply-templates select="aorsml:SimulationModel" mode="createSimSystem.method.createAgentSubjects">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- create AgentSubjectFacets
            <xsl:apply-templates select="aorsml:SimulationModel/aorsml:EntityTypes" mode="createSimSystem.method.createAgentSubjectFacets">
              <xsl:with-param name="indent" select="$indent + 1"/>
              </xsl:apply-templates>  -->

            <!-- create createInitialEvents -->
            <xsl:apply-templates select="aorsml:SimulationModel" mode="createSimSystem.method.createInitialEvents">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="onEveryStep" as="xs:boolean"
                select="exists(aorsml:SimulationModel/aorsml:EnvironmentRules/aorsml:EnvironmentRule/aorsml:ON-EACH-SIMULATION-STEP)"/>
            </xsl:apply-templates>

            <!-- create Statistics -->
            <xsl:apply-templates select="aorsml:SimulationModel" mode="createSimSystem.method.createStatistic">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <xsl:if test="$with.mainMethod">
              <!-- mainMethod -->
              <xsl:apply-templates select="." mode="createSimSystem.method.main">
                <xsl:with-param name="indent" select="$indent + 1"/>
              </xsl:apply-templates>
            </xsl:if>

            <!-- executeInitializeRules()  -->
            <xsl:apply-templates select="//aorsml:InitialState" mode="createSimSystem.method.executeInitializeRules">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- setActivityFactory -->
            <xsl:apply-templates select="aorsml:SimulationModel" mode="createSimSystem.method.setActivityFactory">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- create InitializationRule  (inner classes)-->
            <xsl:apply-templates select="//aorsml:InitialState/aorsml:InitializationRule" mode="createInitializationRule.createInitializationRules">
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

  <xsl:template match="aorsml:SimulationScenario" mode="createSimSystem.constructor">
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
  <xsl:template match="aorsml:SimulationScenario" mode="createSimSystem.method.setScenarioInformations">
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
  <xsl:template match="aorsml:SimulationParameters" mode="createSimSystem.method.getSimulationParameters">
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
  <xsl:template match="aorsml:SimulationModel" mode="createSimSystem.method.createSimModel">
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
  <xsl:template match="aorsml:SimulationModel" mode="createSimSystem.method.createSpaceModel">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="type" select="$core.class.spaceModel"/>
      <xsl:with-param name="name" select="'createSpaceModel'"/>
      <xsl:with-param name="content">

        <!-- init the physim -->
        <xsl:apply-templates select="." mode="createSimSystem.helper.createEnvironment.initPhysim">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <xsl:choose>
          <xsl:when test="fn:exists(aorsml:SpaceModel)">

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
                          <xsl:when test="aorsml:SpaceModel/aorsml:OneDimensional/@multiplicity = '2'">
                            <xsl:value-of select="'OnePlus1'"/>
                          </xsl:when>
                          <xsl:when test="aorsml:SpaceModel/aorsml:OneDimensional/@multiplicity = '3'">
                            <xsl:value-of select="'OnePlus1Plus1'"/>
                          </xsl:when>
                          <xsl:when test="aorsml:SpaceModel/aorsml:OneDimensional/@multiplicity = '4'">
                            <xsl:value-of select="'OnePlus1Plus1Plus1'"/>
                          </xsl:when>
                          <xsl:when
                            test="exists(aorsml:SpaceModel/aorsml:OneDimensional) or 
                                exists(aorsml:SpaceModel/aorsml:OneDimensionalGrid)">
                            <xsl:value-of select="'one'"/>
                          </xsl:when>
                          <xsl:when
                            test="exists(aorsml:SpaceModel/aorsml:TwoDimensional) or 
                                exists(aorsml:SpaceModel/aorsml:TwoDimensional_LateralView) or 
                                exists(aorsml:SpaceModel/aorsml:TwoDimensionalGrid)">
                            <xsl:value-of select="'two'"/>
                          </xsl:when>
                          <xsl:when
                            test="exists(aorsml:SpaceModel/aorsml:ThreeDimensional) or 
                                exists(aorsml:SpaceModel/aorsml:ThreeDimensionalGrid)">
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

            <xsl:apply-templates select="aorsml:SpaceModel/aorsml:*" mode="createSimSystem.method.createSpaceModel.properties">
              <xsl:with-param name="indent" select="$indent"/>
              <xsl:with-param name="spaceModelVar" select="$spaceModelVar"/>
            </xsl:apply-templates>

            <xsl:if test="ends-with(local-name(aorsml:SpaceModel/aorsml:*) , 'Grid') and fn:exists(aorsml:SpaceModel/aorsml:*/@gridCellMaxOccupancy)">

              <!-- gridCellMaxOccupancy  (optional, default = unbounded; mapped to -1)-->
              <xsl:call-template name="java:callSetterMethod">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="objInstance" select="$spaceModelVar"/>
                <xsl:with-param name="instVariable" select="'gridCellMaxOccupancy'"/>
                <xsl:with-param name="value">
                  <xsl:choose>
                    <xsl:when test="aorsml:SpaceModel/aorsml:*/@gridCellMaxOccupancy eq 'unbounded'">
                      <xsl:value-of select="'-1'"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="aorsml:SpaceModel/aorsml:*/@gridCellMaxOccupancy"/>
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


            <!-- this.physim.setSpaceModel(generalSpaceModel); -->
            <xsl:call-template name="java:if">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="condition">
                <xsl:call-template name="java:boolExpr">
                  <xsl:with-param name="value1">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="'physim'"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="value2" select="'null'"/>
                  <xsl:with-param name="operator" select="'!='"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="thenContent">
                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="indent" select="$indent + 2"/>
                  <xsl:with-param name="objInstance">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="name" select="'this'"/>
                      <xsl:with-param name="varName" select="'physim'"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="method" select="'setSpaceModel'"/>
                  <xsl:with-param name="args" select="$spaceModelVar"/>
                </xsl:call-template>
              </xsl:with-param>
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

  <xsl:template match="aorsml:OneDimensional | aorsml:TwoDimensional | aorsml:TwoDimensional_LateralView | aorsml:ThreeDimensional"
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

  <xsl:template match="aorsml:OneDimensionalGrid | aorsml:TwoDimensionalGrid | aorsml:ThreeDimensionalGrid"
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

  <xsl:template match="aorsml:OneDimensionalGrid" mode="createSimSystem.method.createSpaceModel.properties.extra">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="spaceModelVar" as="xs:string" required="yes"/>
    <!-- not yet implemented -->
  </xsl:template>

  <xsl:template match="aorsml:TwoDimensionalGrid" mode="createSimSystem.method.createSpaceModel.properties.extra">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="spaceModelVar" as="xs:string" required="yes"/>

    <!-- set the gridcells -->
    <xsl:if test="exists(aorsml:GridCellProperty)">

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

      <xsl:apply-templates select="//aorsml:InitialState/aorsml:GridCells" mode="createSimSystem.helper.createGridCells">
        <xsl:with-param name="indent" select="$indent + 1"/>
        <xsl:with-param name="spaceModelVar" select="$spaceModelVar" tunnel="yes"/>
        <xsl:with-param name="gridCellVariable" select="$gridCellVariable" tunnel="yes"/>
        <xsl:with-param name="observeGridCells" select="if (true()) then true() else false()" tunnel="yes" as="xs:boolean"/>
      </xsl:apply-templates>
    </xsl:if>

  </xsl:template>

  <xsl:template match="aorsml:ThreeDimensionalGrid" mode="createSimSystem.method.createSpaceModel.properties.extra">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="spaceModelVar" as="xs:string" required="yes"/>
    <!-- not yet implemented -->
  </xsl:template>

  <xsl:template match="aorsml:OneDimensional" mode="createSimSystem.method.createSpaceModel.properties.extra">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="spaceModelVar" as="xs:string" required="yes"/>
    <!-- not yet implemented -->

  </xsl:template>

  <xsl:template match="aorsml:TwoDimensional" mode="createSimSystem.method.createSpaceModel.properties.extra">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="spaceModelVar" as="xs:string" required="yes"/>
    <!-- not yet implemented -->
  </xsl:template>

  <xsl:template match="aorsml:TwoDimensional_LateralView" mode="createSimSystem.method.createSpaceModel.properties.extra">
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

  <xsl:template match="aorsml:ThreeDimensional" mode="createSimSystem.method.createSpaceModel.properties.extra">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="spaceModelVar" as="xs:string" required="yes"/>
    <!-- not yet implemented -->
  </xsl:template>

  <xsl:template
    match="aorsml:OneDimensionalGrid | aorsml:TwoDimensionalGrid | aorsml:ThreeDimensionalGrid |
           aorsml:OneDimensional | aorsml:TwoDimensional | aorsml:TwoDimensional_LateralView | aorsml:ThreeDimensional"
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

  <xsl:template match="aorsml:SimulationModel" mode="createSimSystem.method.initGlobals">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'initGlobalVariables'"/>
      <xsl:with-param name="content">

        <xsl:apply-templates select="//aorsml:InitialState/aorsml:GlobalVariable" mode="shared.updateGlobalVariable">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- createEnvironmentSimulator  -->
  <xsl:template match="aorsml:SimulationModel" mode="createSimSystem.method.createEnvironment">
    <xsl:param name="indent" required="yes"/>

    <xsl:variable name="spaceReservationSystem" as="xs:boolean">
      <xsl:choose>
        <xsl:when
          test="ends-with(local-name(aorsml:SpaceModel/aorsml:*), 'Grid') and 
            fn:exists(aorsml:SpaceModel/aorsml:*/@gridCellMaxOccupancy) and 
            not(aorsml:SpaceModel/aorsml:*/@gridCellMaxOccupancy eq 'unbounded')">
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

        <!--xsl:apply-templates select="//aorsml:InitialState/aorsml:GlobalVariable" mode="shared.updateGlobalVariable">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>
        <xsl:if test="fn:exists(//aorsml:InitialState/aorsml:GlobalVariable)">
          <xsl:call-template name="java:newLine"/>
        </xsl:if-->

        <xsl:variable name="envSimVarName" select="'envSim'"/>

        <xsl:if test="fn:exists(//aorsml:InitialState/*[@hasRandomPosition = true()])">
          <xsl:call-template name="java:newObject">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="class">
              <xsl:choose>
                <xsl:when test="ends-with(local-name(aorsml:SpaceModel/aorsml:*), 'Grid')">
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

        <xsl:apply-templates select="aorsml:Collections/aorsml:Collection" mode="createSimSystem.helper.initCollections">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
        </xsl:apply-templates>

        <xsl:apply-templates
          select="aorsml:EntityTypes/aorsml:PhysicalAgentType | 
                         aorsml:EntityTypes/aorsml:PhysicalObjectType | 
                         aorsml:EntityTypes/aorsml:ObjectType |
                         aorsml:EntityTypes/aorsml:AgentType"
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

        <xsl:apply-templates select="aorsml:EnvironmentRules/aorsml:EnvironmentRule" mode="createSimSystem.helper.initEnvRule">
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

  <xsl:template match="aorsml:SimulationModel" mode="createSimSystem.helper.createEnvironment.initPhysim">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:call-template name="java:newLine"/>
    <xsl:call-template name="java:inlineComment">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="content" select="'parameters are: autoKinematics, autoCollision, autoGravitation, autoImpulse, autoPerception'"/>
    </xsl:call-template>
    <xsl:call-template name="java:if">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="condition">
        <xsl:call-template name="java:boolExpr">
          <xsl:with-param name="value1">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="varName" select="'physim'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="value2" select="'null'"/>
          <xsl:with-param name="operator" select="'!='"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="thenContent">

        <!-- Set physics attributes (autoKinematics, autoCollision, autoGravitation and autoImpulse) for PhySim -->
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="varName" select="'physim'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="method" select="'setPhysicsAttributes'"/>
          <xsl:with-param name="args" as="xs:string*">
            <xsl:choose>
              <xsl:when test="fn:exists(@autoKinematics)">
                <xsl:value-of select="@autoKinematics"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="'false'"/>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
              <xsl:when test="fn:exists(@autoCollision)">
                <xsl:value-of select="@autoCollision"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="'false'"/>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
              <xsl:when test="fn:exists(@autoGravitation)">
                <xsl:value-of select="@autoGravitation"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="'false'"/>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
              <xsl:when test="fn:exists(@autoImpulse)">
                <xsl:value-of select="@autoImpulse"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="'false'"/>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
              <xsl:when test="fn:exists(aorsml:EntityTypes/aorsml:PhysicalAgentType[@autoPerception])">
                <xsl:value-of select="'true'"/>
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

  <!-- create AgentSubjects -->
  <xsl:template match="aorsml:SimulationModel" mode="createSimSystem.method.createAgentSubjects">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'createAgentSubjects'"/>
      <xsl:with-param name="content">

        <xsl:apply-templates select="aorsml:EntityTypes/aorsml:PhysicalAgentType" mode="createSimSystem.helper.initAgentSubject">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <xsl:apply-templates select="aorsml:EntityTypes/aorsml:AgentType" mode="createSimSystem.helper.initAgentSubject">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- create AgentSubjectFacets -->
  <xsl:template match="aorsml:EntityTypes" mode="createSimSystem.method.createAgentSubjectFacets">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="'createAgentSubjectFacets'"/>
      <xsl:with-param name="content"> </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- create Statistics -->
  <xsl:template match="aorsml:SimulationModel" mode="createSimSystem.method.createStatistic">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'createStatistic'"/>
      <xsl:with-param name="type" select="$core.class.generalStatistics"/>
      <xsl:with-param name="content">


        <xsl:choose>
          <xsl:when test="fn:exists(aorsml:Statistics)">

            <xsl:variable name="statisticVarName" select="jw:lowerWord($sim.class.simStatistics)"/>
            <xsl:call-template name="java:newObject">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="class" select="$sim.class.simStatistics"/>
              <xsl:with-param name="varName" select="$statisticVarName"/>
            </xsl:call-template>

            <xsl:apply-templates select="aorsml:Statistics/aorsml:Variable" mode="createSimSystem.helper.createStatistic.variable">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="statisticVarName" select="$statisticVarName"/>
            </xsl:apply-templates>
            <xsl:call-template name="java:newLine"/>

            <xsl:apply-templates select="aorsml:Statistics/aorsml:Variable" mode="createSimSystem.helper.createStatistic.parameter">
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

  <xsl:template match="aorsml:SimulationModel" mode="createSimSystem.method.createInitialEvents">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="onEveryStep" as="xs:boolean" select="false()"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'createInitialEvents'"/>
      <xsl:with-param name="content">

        <xsl:apply-templates select="aorsml:EntityTypes/aorsml:ExogenousEventType | aorsml:EntityTypes/aorsml:CausedEventType"
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
  <xsl:template match="aorsml:InitialState" mode="createSimSystem.method.executeInitializeRules">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'executeInitializeRules'"/>
      <xsl:with-param name="content">

        <xsl:for-each select="aorsml:InitializationRule">

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
  <xsl:template match="aorsml:SimulationModel" mode="createSimSystem.method.setActivityFactory">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'setActivityFactory'"/>
      <xsl:with-param name="content">

        <xsl:if test="fn:exists(aorsml:EntityTypes/aorsml:ActivityType)">

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

  <xsl:template match="aorsml:SimulationScenario" mode="createSimSystem.method.main">
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

        <xsl:variable name="simVarName" select="jw:lowerWord(aorsml:SimulationModel/@modelName)"/>

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
  <xsl:template match="aorsml:PhysicalAgentType" mode="createSimSystem.helper.initObjectTypes">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="envSimVarName" required="yes" as="xs:string"/>

    <xsl:variable name="varName" select="jw:lowerWord(@name)"/>
    <xsl:variable name="className" select="jw:upperWord(@name)"/>

    <xsl:if
      test="fn:exists(//aorsml:InitialState/aorsml:PhysicalAgent[@type = current()/@name and not(@objectVariable)]) or 
                fn:exists(//aorsml:InitialState/aorsml:PhysicalAgents[@type = current()/@name and not(@objectVariable)])">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$className"/>
        <xsl:with-param name="varName" select="$varName"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:for-each select="//aorsml:InitialState/aorsml:PhysicalAgent[@type = current()/@name and @objectVariable]">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$className"/>
        <xsl:with-param name="varName" select="@objectVariable"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:for-each>

    <xsl:apply-templates
      select="//aorsml:InitialState/aorsml:PhysicalAgent[@type = current()/@name] | //aorsml:InitialState/aorsml:PhysicalAgents[@type = current()/@name]"
      mode="shared.helper.initAORObjects.manager">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
      <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
    </xsl:apply-templates>
    <xsl:call-template name="java:newLine"/>
  </xsl:template>

  <!-- set the initial PhysObjekts -->
  <xsl:template match="aorsml:PhysicalObjectType" mode="createSimSystem.helper.initObjectTypes">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="envSimVarName" required="yes" as="xs:string"/>

    <xsl:variable name="className" select="jw:upperWord(@name)"/>
    <xsl:variable name="varName" select="jw:lowerWord($className)"/>

    <xsl:if
      test="fn:exists(//aorsml:InitialState/aorsml:PhysicalObject[@type = current()/@name and not(@objectVariable)]) or
            fn:exists(//aorsml:InitialState/aorsml:PhysicalObjects[@type = current()/@name and not(@objectVariable)])">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$className"/>
        <xsl:with-param name="varName" select="$varName"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:for-each select="//aorsml:InitialState/aorsml:PhysicalObject[@type = current()/@name and @objectVariable]">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$className"/>
        <xsl:with-param name="varName" select="@objectVariable"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:for-each>

    <xsl:apply-templates
      select="//aorsml:InitialState/aorsml:PhysicalObject[@type = current()/@name] | 
              //aorsml:InitialState/aorsml:PhysicalObjects[@type = current()/@name]"
      mode="shared.helper.initAORObjects.manager">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
      <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
    </xsl:apply-templates>

    <xsl:call-template name="java:newLine"/>
  </xsl:template>

  <!-- set the initial Objekts -->
  <xsl:template match="aorsml:ObjectType" mode="createSimSystem.helper.initObjectTypes">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="envSimVarName" required="yes" as="xs:string"/>
    <xsl:if test="fn:exists(//aorsml:InitialState/aorsml:Object[@type = current()/@name])">

      <xsl:variable name="className" select="jw:upperWord(@name)"/>
      <xsl:variable name="varName" select="jw:lowerWord($className)"/>

      <xsl:if
        test="fn:exists(//aorsml:InitialState/aorsml:Object[@type = current()/@name and not (@objectVariable)]) or 
                            fn:exists(//aorsml:InitialState/aorsml:Objects[@type = current()/@name and not(@objectVariable)])">
        <xsl:call-template name="java:newObject">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="class" select="$className"/>
          <xsl:with-param name="varName" select="$varName"/>
          <xsl:with-param name="withDeclaration" select="false()"/>
        </xsl:call-template>
      </xsl:if>
      <xsl:for-each select="//aorsml:InitialState/aorsml:Object[@type = current()/@name and @objectVariable]">
        <xsl:call-template name="java:newObject">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="class" select="$className"/>
          <xsl:with-param name="varName" select="@objectVariable"/>
          <xsl:with-param name="withDeclaration" select="false()"/>
        </xsl:call-template>
      </xsl:for-each>
      <!-- TODO: implement Objects -->
      <xsl:apply-templates select="//aorsml:InitialState/aorsml:Object[@type = current()/@name]" mode="shared.helper.initAORObjects.manager">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="className" select="$className"/>
        <xsl:with-param name="varName" select="$varName"/>
        <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
      </xsl:apply-templates>
      <!--
    <xsl:apply-templates select="//aorsml:InitialState/aorsml:ObjectSet[@type = current()/@name]" mode="shared.helper.initAORObjectsSet">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
      <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
    </xsl:apply-templates>
    -->
      <xsl:call-template name="java:newLine"/>
    </xsl:if>
  </xsl:template>

  <xsl:template match="aorsml:AgentType" mode="createSimSystem.helper.initObjectTypes">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="envSimVarName" required="yes" as="xs:string"/>

    <xsl:variable name="varName" select="jw:lowerWord(@name)"/>
    <xsl:variable name="className" select="jw:upperWord(@name)"/>

    <xsl:if
      test="fn:exists(//aorsml:InitialState/aorsml:Agent[@type = current()/@name and not(@objectVariable)]) or 
                fn:exists(//aorsml:InitialState/aorsml:Agents[@type = current()/@name and not(@objectVariable)])">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$className"/>
        <xsl:with-param name="varName" select="$varName"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:for-each select="//aorsml:InitialState/aorsml:Agent[@type = current()/@name and @objectVariable]">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$className"/>
        <xsl:with-param name="varName" select="@objectVariable"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:for-each>

    <xsl:apply-templates
      select="//aorsml:InitialState/aorsml:Agent[@type = current()/@name] | //aorsml:InitialState/aorsml:Agents[@type = current()/@name]"
      mode="shared.helper.initAORObjects.manager">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
      <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
    </xsl:apply-templates>
    <xsl:call-template name="java:newLine"/>

  </xsl:template>

  <!-- set the EnvironmentRules -->
  <xsl:template match="aorsml:EnvironmentRule" mode="createSimSystem.helper.initEnvRule">
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
  <xsl:template match="aorsml:PhysicalAgentType" mode="createSimSystem.helper.initAgentSubject">
    <xsl:param name="indent" required="yes"/>

    <xsl:variable name="className" select="fn:concat(jw:upperWord(@name), $prefix.agentSubject)"/>
    <xsl:variable name="varName" select="fn:concat($createdVariablesNamePrefix, jw:lowerWord($className))"/>

    <xsl:if
      test="fn:exists(//aorsml:InitialState/aorsml:PhysicalAgent[@type = current()/@name]) or 
            fn:exists(//aorsml:InitialState/aorsml:PhysicalAgents[@type = current()/@name])">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$className"/>
        <xsl:with-param name="varName" select="$varName"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:call-template name="java:newLine"/>

    <xsl:apply-templates select="//aorsml:InitialState/aorsml:PhysicalAgent[@type = current()/@name]" mode="shared.helper.initAgentSubject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
    </xsl:apply-templates>

    <xsl:apply-templates select="//aorsml:InitialState/aorsml:PhysicalAgents[@type = current()/@name]" mode="shared.helper.initAgentSubjectSet">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- set the initial agentSubjects -->
  <xsl:template match="aorsml:AgentType" mode="createSimSystem.helper.initAgentSubject">
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
    <xsl:apply-templates select="//aorsml:InitialState/aorsml:Agent[@type = current()/@name]" mode="shared.helper.initAgentSubject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
    </xsl:apply-templates>

    <xsl:apply-templates select="//aorsml:InitialState/aorsml:Agents[@type = current()/@name]" mode="shared.helper.initAgentSubjectSet">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- set initial ExogenousEvent  -->
  <xsl:template match="aorsml:ExogenousEventType | aorsml:CausedEventType" mode="createSimSystem.helper.initEvents">
    <xsl:param name="indent" required="yes"/>

    <xsl:variable name="className" select="jw:upperWord(@name)"/>
    <xsl:variable name="varName" select="fn:concat($createdVariablesNamePrefix, jw:lowerWord($className))"/>

    <xsl:if
      test="(local-name() eq 'ExogenousEventType' and //aorsml:InitialState/aorsml:ExogenousEvent[@type = current()/@name]) or 
             local-name() eq 'CausedEventType' and //aorsml:InitialState/aorsml:CausedEvent[@type = current()/@name]">
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
        <xsl:apply-templates select="//aorsml:InitialState/aorsml:ExogenousEvent[@type = current()/@name]"
          mode="createSimSystem.helper.initEvents.init">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="className" select="$className"/>
          <xsl:with-param name="varName" select="$varName"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:when test="local-name() eq 'CausedEventType'">
        <xsl:apply-templates select="//aorsml:InitialState/aorsml:CausedEvent[@type = current()/@name]" mode="createSimSystem.helper.initEvents.init">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="className" select="$className"/>
          <xsl:with-param name="varName" select="$varName"/>
        </xsl:apply-templates>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="aorsml:CausedEvent | aorsml:ExogenousEvent" mode="createSimSystem.helper.initEvents.init">
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

    <xsl:for-each select="aorsml:Slot[@property != 'occurrenceTime']">
      <xsl:call-template name="java:callSetterMethod">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="objInstance" select="$varName"/>
        <xsl:with-param name="instVariable" select="@property"/>
        <xsl:with-param name="value">
          <xsl:apply-templates select="." mode="assistents.getSlotValue"/>
        </xsl:with-param>
        <xsl:with-param name="valueType">
          <xsl:value-of select="//aorsml:EntityTypes/aorsml:*[@name = $eventType]/aorsml:*[@name = current()/@property]/@type"/>
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
  <xsl:template match="aorsml:Variable" mode="createSimSystem.helper.createStatistic.variable">
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
                    test="aorsml:Source/aorsml:ObjectProperty or 
                    aorsml:Source/aorsml:ObjectTypeExtensionSize or 
                    aorsml:Source/aorsml:ResourceUtilization">

                    <xsl:variable name="node" as="node()" select="aorsml:Source/aorsml:*"/>

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

  <xsl:template match="aorsml:Variable" mode="createSimSystem.helper.createStatistic.parameter">
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
              <xsl:when test="exists(aorsml:Source)">
                <xsl:value-of select="local-name(aorsml:Source/aorsml:*[1])"/>
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
    <xsl:apply-templates select="aorsml:Source/aorsml:*" mode="createSimSystem.helper.createStatistic.source">
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
    <xsl:if test="exists(aorsml:Source/@aggregationFunction)">
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
            <xsl:with-param name="varName" select="aorsml:Source/@aggregationFunction"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>
    <xsl:call-template name="java:newLine"/>

  </xsl:template>

  <xsl:template match="aorsml:GlobalVariable" mode="createSimSystem.helper.createStatistic.source">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="statVarName" as="xs:string" required="yes"/>

  </xsl:template>

  <xsl:template match="aorsml:StatisticsVariable" mode="createSimSystem.helper.createStatistic.source">
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

  <xsl:template match="aorsml:ObjectProperty" mode="createSimSystem.helper.createStatistic.source">
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

  <xsl:template match="aorsml:ObjectTypeExtensionSize" mode="createSimSystem.helper.createStatistic.source">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="statVarName" as="xs:string" required="yes"/>
  </xsl:template>

  <xsl:template match="aorsml:ResourceUtilization" mode="createSimSystem.helper.createStatistic.source">
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

  <xsl:template match="aorsml:ValueExpr" mode="createSimSystem.helper.createStatistic.source">
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
  <xsl:template match="aorsml:GridCells" mode="createSimSystem.helper.createGridCells">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="spaceModelVar" required="yes" as="xs:string" tunnel="yes"/>
    <xsl:param name="observeGridCells" select="false()" tunnel="yes" as="xs:boolean"/>

    <xsl:if test="fn:exists(aorsml:Slot)">
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

    <xsl:apply-templates select="aorsml:GridCell | aorsml:GridCellSet" mode="createSimSystem.helper.createGridCells">
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
  <xsl:template match="aorsml:GridCell" mode="createSimSystem.helper.createGridCells">
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

        <xsl:apply-templates select="aorsml:Slot" mode="createSimSystem.helper.createGridCells.setSlot">
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
  <xsl:template match="aorsml:GridCellSet" mode="createSimSystem.helper.createGridCells">
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

                <xsl:apply-templates select="./aorsml:Slot" mode="createSimSystem.helper.createGridCells.setSlot">
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

  <xsl:template match="aorsml:Slot" mode="createSimSystem.helper.createGridCells.setSlot">
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
  <xsl:template match="aorsml:Collection" mode="createSimSystem.helper.initCollections">
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
  <xsl:template match="aorsml:GlobalVariable" mode="createSimSystem.initGlobals">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:apply-templates select="." mode="shared.updateGlobalVariable">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates>

  </xsl:template>

</xsl:stylesheet>
