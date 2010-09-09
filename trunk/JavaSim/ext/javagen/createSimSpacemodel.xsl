<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation creates a class with SpaceModelInformations.

        $Rev$
        $Date$

        @author:   Jens Werner (jens.werner@tu-cottbus.de)
        @last changed by $Author$
-->

<xsl:stylesheet version="2.0" xmlns:aorsl="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsi:schemaLocation="http://aor-simulation.org aorsml.xsd"
  xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">

  <!--*****-->
  <!--class-->
  <!--*****-->
  <xsl:template name="createSimulationSpacemodel">
    <xsl:apply-templates select="aorsl:SimulationScenario/aorsl:SimulationModel/aorsl:SpaceModel"
      mode="createSimulationSpacemodel.createSimulationSpacemodel">
      <xsl:with-param name="indent" select="0"/>
    </xsl:apply-templates>
  </xsl:template>

  <xsl:template match="aorsl:SpaceModel" mode="createSimulationSpacemodel.createSimulationSpacemodel">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <!-- flag for using 1-dim-discrete -->
    <xsl:variable name="useOneDimensionalGrid" as="xs:boolean">
      <xsl:choose>
        <!-- deprecated -->
        <xsl:when test="fn:exists(@xMax) and @dimensions = '1'  and @discrete = true()">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <!-- new version -->
        <xsl:when test="exists(aorsl:OneDimensionalGrid)">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="false()"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- flag for using 1-dim-nonDiscrete -->
    <xsl:variable name="useOneDimensional" as="xs:boolean">
      <xsl:choose>
        <!-- deprecated -->
        <xsl:when test="fn:exists(@xMax) and @dimensions = '1'  and ((not (fn:exists(@discrete))) or @discrete = false())">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <!-- new version -->
        <xsl:when test="exists(aorsl:OneDimensional)">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="false()"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- flag for using 2-dim-discrete -->
    <xsl:variable name="useTwoDimensionalGrid" as="xs:boolean">
      <xsl:choose>
        <!-- deprecated -->
        <xsl:when test="fn:exists(@xMax) and fn:exists(@yMax) and @dimensions = '2' and @discrete = true()">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <!-- new version -->
        <xsl:when test="exists(aorsl:TwoDimensionalGrid)">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="false()"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- flag for using 2-dim-nonDiscrete -->
    <xsl:variable name="useTwoDimensional" as="xs:boolean">
      <xsl:choose>
        <!-- deprecated -->
        <xsl:when test="fn:exists(@xMax) and fn:exists(@yMax) and @dimensions = '2' and ((not (fn:exists(@discrete))) or @discrete = false())">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <!-- new version -->
        <xsl:when test="exists(aorsl:TwoDimensional)">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="false()"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- flag for using 2-dim-nonDiscrete-lateral-view -->
    <xsl:variable name="useTwoDimensionalLateralView" as="xs:boolean">
      <xsl:choose>
        <!-- new version -->
        <xsl:when test="exists(aorsl:TwoDimensional_LateralView)">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="false()"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- flag for using 3-dim-discrete -->
    <xsl:variable name="useThreeDimensionalGrid" as="xs:boolean">
      <xsl:choose>
        <!-- deprecated -->
        <xsl:when test="fn:exists(@xMax) and fn:exists(@yMax) and fn:exists(@zMax) and @dimensions = '3'  and @discrete = true()">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <!-- new version -->
        <xsl:when test="exists(aorsl:ThreeDimensionalGrid)">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="false()"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- flag for using 3-dim-nonDiscrete -->
    <xsl:variable name="useThreeDimensional" as="xs:boolean">
      <xsl:choose>
        <!-- deprecated -->
        <xsl:when
          test="fn:exists(@xMax) and fn:exists(@yMax) and fn:exists(@zMax) and @dimensions = '3'  and ((not (fn:exists(@discrete))) or @discrete = false())">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <!-- new version -->
        <xsl:when test="exists(aorsl:ThreeDimensional)">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="false()"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- flag for discrete -->
    <xsl:variable name="isDiscrete" as="xs:boolean">
      <xsl:choose>
        <xsl:when test="$useOneDimensionalGrid or $useTwoDimensionalGrid or $useThreeDimensionalGrid">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="false()"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- flag for using 2-dim-grids -->
    <xsl:variable name="useTwoDimCells" as="xs:boolean">
      <xsl:choose>
        <!-- deprecated -->
        <xsl:when test="$useTwoDimensionalGrid = true() and 
                (exists(aorsl:GridCellProperty) or exists(aorsl:GridCellFunction))">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <!-- new -->
        <xsl:when
          test="$useTwoDimensionalGrid and 
                ((exists(aorsl:TwoDimensionalGrid/aorsl:GridCellProperty) or 
                 (exists(aorsl:TwoDimensionalGrid/aorsl:GridCellFunction))))">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="false()"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- flag for observing gridcells -->
    <xsl:variable name="observeGridCells" as="xs:boolean">
      <xsl:choose>
        <!-- TODO: check here the right attribute -->
        <!-- deprecated -->
        <xsl:when test="exists(aorsl:GridCellProperty)">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <!-- new -->
        <xsl:when test="exists(aorsl:*/aorsl:GridCellProperty)">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="false()"/>
        </xsl:otherwise>
      </xsl:choose>

    </xsl:variable>

    <xsl:call-template name="aorsl:classFile">
      <xsl:with-param name="path" select="$sim.path.controller"/>
      <xsl:with-param name="name" select="$sim.class.simSpaceModel"/>

      <xsl:with-param name="content">

        <xsl:call-template name="java:imports">
          <xsl:with-param name="importList" as="xs:string*">
            <xsl:value-of select="$core.package.spaceModel"/>
            <xsl:value-of select="fn:concat($space.package.space, '.*')"/>
            <xsl:if test="$observeGridCells">
              <xsl:value-of select="'java.beans.PropertyChangeEvent'"/>
              <xsl:value-of select="'java.beans.PropertyChangeSupport'"/>
              <xsl:value-of select="'java.beans.PropertyChangeListener'"/>
              <xsl:value-of select="$logger.package.objInitEventListener"/>
              <xsl:value-of select="$logger.package.objInitEvent"/>
              <xsl:value-of select="'java.util.ArrayList'"/>
            </xsl:if>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="java:class">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="name" select="$sim.class.simSpaceModel"/>
          <xsl:with-param name="extends" select="$core.class.spaceModel"/>
          <xsl:with-param name="implements">
            <xsl:choose>
              <xsl:when test="$useTwoDimCells">
                <xsl:value-of select="$space.class.gridCells"/>
              </xsl:when>
            </xsl:choose>
          </xsl:with-param>
          <xsl:with-param name="content">

            <xsl:variable name="spaceVarName" select="'space'"/>
            <xsl:choose>
              <xsl:when test="$useOneDimensionalGrid">
                <xsl:call-template name="java:newObject">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="modifier" select="'private'"/>
                  <xsl:with-param name="class" select="$space.class.oneDimDiscreteSpace"/>
                  <xsl:with-param name="varName" select="$spaceVarName"/>
                  <xsl:with-param name="withDeclaration" select="false()"/>
                </xsl:call-template>
                <xsl:call-template name="java:newLine"/>
              </xsl:when>
              <xsl:when test="$useTwoDimensionalGrid">
                <xsl:call-template name="java:newObject">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="modifier" select="'private'"/>
                  <xsl:with-param name="class" select="$space.class.twoDimDiscreteSpace"/>
                  <xsl:with-param name="varName" select="$spaceVarName"/>
                  <xsl:with-param name="withDeclaration" select="false()"/>
                </xsl:call-template>
                <xsl:call-template name="java:newLine"/>
              </xsl:when>
              <xsl:when test="$useThreeDimensionalGrid">
                <xsl:call-template name="java:newObject">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="modifier" select="'private'"/>
                  <xsl:with-param name="class" select="$space.class.threeDimDiscreteSpace"/>
                  <xsl:with-param name="varName" select="$spaceVarName"/>
                  <xsl:with-param name="withDeclaration" select="false()"/>
                </xsl:call-template>
                <xsl:call-template name="java:newLine"/>
              </xsl:when>
              <xsl:when test="$useOneDimensional">
                <xsl:call-template name="java:newObject">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="modifier" select="'private'"/>
                  <xsl:with-param name="class" select="$space.class.oneDimNonDiscreteSpace"/>
                  <xsl:with-param name="varName" select="$spaceVarName"/>
                  <xsl:with-param name="withDeclaration" select="false()"/>
                </xsl:call-template>
                <xsl:call-template name="java:newLine"/>
              </xsl:when>
              <xsl:when test="$useTwoDimensional">
                <xsl:call-template name="java:newObject">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="modifier" select="'private'"/>
                  <xsl:with-param name="class" select="$space.class.twoDimNonDiscreteSpace"/>
                  <xsl:with-param name="varName" select="$spaceVarName"/>
                  <xsl:with-param name="withDeclaration" select="false()"/>
                </xsl:call-template>
                <xsl:call-template name="java:newLine"/>
              </xsl:when>
              <xsl:when test="$useTwoDimensionalLateralView">
                <xsl:call-template name="java:newObject">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="modifier" select="'private'"/>
                  <xsl:with-param name="class" select="$space.class.twoDimNonDiscreteLateralViewSpace"/>
                  <xsl:with-param name="varName" select="$spaceVarName"/>
                  <xsl:with-param name="withDeclaration" select="false()"/>
                </xsl:call-template>
                <xsl:call-template name="java:newLine"/>
              </xsl:when>
              <xsl:when test="$useThreeDimensional">
                <xsl:call-template name="java:newObject">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="modifier" select="'private'"/>
                  <xsl:with-param name="class" select="$space.class.threeDimNonDiscreteSpace"/>
                  <xsl:with-param name="varName" select="$spaceVarName"/>
                  <xsl:with-param name="withDeclaration" select="false()"/>
                </xsl:call-template>
                <xsl:call-template name="java:newLine"/>
              </xsl:when>
              <xsl:otherwise> </xsl:otherwise>
            </xsl:choose>

            <xsl:apply-templates select="." mode="createSimulationSpacemodel.constructor">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="observeGridCells" select="$observeGridCells"/>
            </xsl:apply-templates>

            <!-- getSpace() -->
            <xsl:choose>

              <xsl:when test="$useOneDimensionalGrid">
                <xsl:call-template name="java:createGetter">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="variableType" select="$space.class.oneDimDiscreteSpace"/>
                  <xsl:with-param name="variableName" select="$spaceVarName"/>
                </xsl:call-template>
              </xsl:when>

              <xsl:when test="$useTwoDimensionalGrid">
                <xsl:call-template name="java:createGetter">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="variableType" select="$space.class.twoDimDiscreteSpace"/>
                  <xsl:with-param name="variableName" select="$spaceVarName"/>
                </xsl:call-template>
              </xsl:when>

              <xsl:when test="$useThreeDimensionalGrid">
                <xsl:call-template name="java:createGetter">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="variableType" select="$space.class.threeDimDiscreteSpace"/>
                  <xsl:with-param name="variableName" select="$spaceVarName"/>
                </xsl:call-template>
              </xsl:when>

              <xsl:when test="$useOneDimensional">
                <xsl:call-template name="java:createGetter">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="variableType" select="$space.class.oneDimNonDiscreteSpace"/>
                  <xsl:with-param name="variableName" select="$spaceVarName"/>
                </xsl:call-template>
              </xsl:when>

              <xsl:when test="$useTwoDimensional">
                <xsl:call-template name="java:createGetter">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="variableType" select="$space.class.twoDimNonDiscreteSpace"/>
                  <xsl:with-param name="variableName" select="$spaceVarName"/>
                </xsl:call-template>
              </xsl:when>

              <xsl:when test="$useTwoDimensionalLateralView">
                <xsl:call-template name="java:createGetter">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="variableType" select="$space.class.twoDimNonDiscreteLateralViewSpace"/>
                  <xsl:with-param name="variableName" select="$spaceVarName"/>
                </xsl:call-template>
              </xsl:when>

              <xsl:when test="$useThreeDimensional">
                <xsl:call-template name="java:createGetter">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="variableType" select="$space.class.threeDimNonDiscreteSpace"/>
                  <xsl:with-param name="variableName" select="$spaceVarName"/>
                </xsl:call-template>
              </xsl:when>

              <xsl:otherwise>
                <xsl:call-template name="java:method">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="modifier" select="'public'"/>
                  <xsl:with-param name="type" select="$space.package.space.space"/>
                  <xsl:with-param name="name" select="'getSpace'"/>
                  <xsl:with-param name="content">
                    <xsl:call-template name="java:return">
                      <xsl:with-param name="indent" select="$indent + 2"/>
                      <xsl:with-param name="value" select="'null'"/>
                    </xsl:call-template>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:otherwise>

            </xsl:choose>

            <!-- initSpace() -->
            <xsl:call-template name="java:method">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="modifier" select="'public'"/>
              <xsl:with-param name="name" select="'initSpace'"/>
              <!--           <xsl:with-param name="parameterList" as="xs:string*">
                <xsl:call-template name="java:createParam">
                  <xsl:with-param name="type">
                    <xsl:choose>
                      <xsl:when test="$isDiscrete">
                        <xsl:value-of select="'int'"/>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:value-of select="'double'"/>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:with-param>
                  <xsl:with-param name="name" select="'xSize'"/>
                </xsl:call-template>
                <xsl:call-template name="java:createParam">
                  <xsl:with-param name="type">
                    <xsl:choose>
                      <xsl:when test="$isDiscrete">
                        <xsl:value-of select="'int'"/>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:value-of select="'double'"/>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:with-param>
                  <xsl:with-param name="name" select="'ySize'"/>
                </xsl:call-template>
                <xsl:call-template name="java:createParam">
                  <xsl:with-param name="type">
                    <xsl:choose>
                      <xsl:when test="$isDiscrete">
                        <xsl:value-of select="'int'"/>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:value-of select="'double'"/>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:with-param>
                  <xsl:with-param name="name" select="'zSize'"/>
                </xsl:call-template>
              </xsl:with-param> -->
              <xsl:with-param name="content">

                <xsl:choose>
                  <xsl:when
                    test="$useOneDimensionalGrid or $useTwoDimensionalGrid or $useThreeDimensionalGrid or 
                    $useOneDimensional or $useTwoDimensional or $useTwoDimensionalLateralView or $useThreeDimensional">
                    <xsl:call-template name="java:variable">
                      <xsl:with-param name="indent" select="$indent + 2"/>
                      <xsl:with-param name="name">
                        <xsl:call-template name="java:varByDotNotation">
                          <xsl:with-param name="varName" select="$spaceVarName"/>
                        </xsl:call-template>
                      </xsl:with-param>
                      <xsl:with-param name="value">

                        <xsl:call-template name="java:newObject">
                          <xsl:with-param name="onlyInitialization" select="true()"/>
                          <xsl:with-param name="inLine" select="true()"/>
                          <xsl:with-param name="class">

                            <xsl:choose>
                              <xsl:when test="$useOneDimensionalGrid">
                                <xsl:value-of select="$space.class.oneDimDiscreteSpace"/>
                              </xsl:when>
                              <xsl:when test="$useTwoDimensionalGrid">
                                <xsl:value-of select="$space.class.twoDimDiscreteSpace"/>
                              </xsl:when>
                              <xsl:when test="$useThreeDimensionalGrid">
                                <xsl:value-of select="$space.class.threeDimDiscreteSpace"/>
                              </xsl:when>
                              <xsl:when test="$useOneDimensional">
                                <xsl:value-of select="$space.class.oneDimNonDiscreteSpace"/>
                              </xsl:when>
                              <xsl:when test="$useTwoDimensional">
                                <xsl:value-of select="$space.class.twoDimNonDiscreteSpace"/>
                              </xsl:when>
                              <xsl:when test="$useTwoDimensionalLateralView">
                                <xsl:value-of select="$space.class.twoDimNonDiscreteLateralViewSpace"/>
                              </xsl:when>
                              <xsl:when test="$useThreeDimensional">
                                <xsl:value-of select="$space.class.threeDimNonDiscreteSpace"/>
                              </xsl:when>
                            </xsl:choose>

                          </xsl:with-param>
                          <xsl:with-param name="args" as="xs:string*">
                            <xsl:call-template name="java:callGetterMethod">
                              <xsl:with-param name="inLine" select="true()"/>
                              <xsl:with-param name="instVariable" select="'xMax'"/>
                            </xsl:call-template>
                            <xsl:if test="$useTwoDimensionalGrid or $useThreeDimensionalGrid or $useTwoDimensional or $useTwoDimensionalLateralView or $useThreeDimensional">
                              <xsl:call-template name="java:callGetterMethod">
                                <xsl:with-param name="inLine" select="true()"/>
                                <xsl:with-param name="instVariable" select="'yMax'"/>
                              </xsl:call-template>
                            </xsl:if>
                            <xsl:if test="$useThreeDimensionalGrid or $useThreeDimensional">
                              <xsl:call-template name="java:callGetterMethod">
                                <xsl:with-param name="inLine" select="true()"/>
                                <xsl:with-param name="instVariable" select="'zMax'"/>
                              </xsl:call-template>
                            </xsl:if>
                            <xsl:if test="$useOneDimensionalGrid or $useTwoDimensionalGrid or $useThreeDimensionalGrid">
                              <xsl:call-template name="java:callGetterMethod">
                                <xsl:with-param name="instVariable" select="'gridCellMaxOccupancy'"/>
                                <xsl:with-param name="inLine" select="true()"/>
                              </xsl:call-template>
                            </xsl:if>
                          </xsl:with-param>
                        </xsl:call-template>
                      </xsl:with-param>
                    </xsl:call-template>
                  </xsl:when>
                  <xsl:otherwise>
                    <!-- <xsl:call-template name="java:systemPrintln">
                      <xsl:with-param name="indent" select="$indent + 2"/>
                      <xsl:with-param name="value" select="'More then 3-Dim Space are not yet implemented!'"/>
                    </xsl:call-template> -->
                  </xsl:otherwise>
                </xsl:choose>

              </xsl:with-param>

            </xsl:call-template>

            <xsl:choose>
              <xsl:when test="$useTwoDimCells">

                <xsl:apply-templates select="." mode="createSimulationSpacemodel.interfaceMethods.gridCells">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="gridSpaceVarName" select="$spaceVarName"/>
                  <xsl:with-param name="observeGridCells" select="$observeGridCells"/>
                </xsl:apply-templates>

                <!-- create gridCell as innerclass -->
                <xsl:choose>
                  <xsl:when test="aorsl:TwoDimensionalGrid">
                    <xsl:apply-templates select="aorsl:TwoDimensionalGrid" mode="createSimulationSpacemodel.createGridCell">
                      <xsl:with-param name="indent" select="$indent + 1"/>
                      <xsl:with-param name="observeGridCells" select="$observeGridCells"/>
                    </xsl:apply-templates>
                  </xsl:when>
                  <xsl:otherwise>
                    <!-- deprecated -->
                    <xsl:apply-templates select="." mode="createSimulationSpacemodel.createGridCell">
                      <xsl:with-param name="indent" select="$indent + 1"/>
                      <xsl:with-param name="observeGridCells" select="$observeGridCells"/>
                    </xsl:apply-templates>
                  </xsl:otherwise>
                </xsl:choose>

              </xsl:when>
            </xsl:choose>

          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>

    </xsl:call-template>
  </xsl:template>

  <xsl:template match="aorsl:SpaceModel" mode="createSimulationSpacemodel.constructor">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="observeGridCells" select="false()" as="xs:boolean"/>

    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="$sim.class.simSpaceModel"/>
      <xsl:with-param name="parameters" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'Dimensions'"/>
          <xsl:with-param name="name" select="'dimensions'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">
        <xsl:choose>

          <xsl:when test="aorsl:*[not(local-name() eq 'GridCellProperty') and not(local-name() eq 'GridCellFunction')]">

            <xsl:call-template name="java:callSuper">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="paramList" as="xs:string*">
                <xsl:value-of select="'dimensions'"/>
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="name" select="$core.enum.spaceType"/>
                  <xsl:with-param name="varName">
                    <xsl:choose>
                      <xsl:when test="aorsl:OneDimensional">
                        <xsl:value-of select="'OneD'"/>
                      </xsl:when>
                      <xsl:when test="aorsl:TwoDimensional">
                        <xsl:value-of select="'TwoD'"/>
                      </xsl:when>
                      <xsl:when test="aorsl:TwoDimensional_LateralView">
                        <xsl:value-of select="'TwoDLateralView'"/>
                      </xsl:when>
                      <xsl:when test="aorsl:ThreeDimensional">
                        <xsl:value-of select="'ThreeD'"/>
                      </xsl:when>
                      <xsl:when test="aorsl:OneDimensionalGrid">
                        <xsl:value-of select="'OneDGrid'"/>
                      </xsl:when>
                      <xsl:when test="aorsl:TwoDimensionalGrid">
                        <xsl:value-of select="'TwoDGrid'"/>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:value-of select="'ThreeDGrid'"/>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <!-- deprecated -->
            <xsl:call-template name="java:callSuper">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="paramList" select="'dimensions'"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>

        <xsl:if test="$observeGridCells">
          <xsl:call-template name="java:newArrayListObject">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="generic" select="$logger.class.objInitEventListener"/>
            <xsl:with-param name="name">
              <xsl:call-template name="java:varByDotNotation">
                <xsl:with-param name="varName" select="'initGridListener'"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="isVariable" select="true()"/>
          </xsl:call-template>
        </xsl:if>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="aorsl:SpaceModel" mode="createSimulationSpacemodel.interfaceMethods.gridCells">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="gridSpaceVarName" required="yes" as="xs:string"/>
    <xsl:param name="observeGridCells" select="false()" as="xs:boolean"/>

    <!--  getGridCell(int xPos, int yPos) -->
    <xsl:call-template name="createGetGrid">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="sim.class.simGridCell" select="$sim.class.simGridCell"/>
      <xsl:with-param name="gridSpaceVarName" select="$gridSpaceVarName"/>
    </xsl:call-template>

    <!-- initGrid(int xSize, int ySize, ObjektInitEventListener objektInitEventListener) -->
    <xsl:choose>
      <xsl:when test="aorsl:TwoDimensionalGrid">
        <xsl:apply-templates select="aorsl:TwoDimensionalGrid" mode="createSimulationSpacemodel.interfaceMethods.gridCells.initGrid">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="observeGridCells" select="$observeGridCells"/>
          <xsl:with-param name="gridSpaceVarName" select="$gridSpaceVarName"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <!-- deprecated -->
        <xsl:call-template name="createInitGrid">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="logger.class.objInitEventListener" select="$logger.class.objInitEventListener"/>
          <xsl:with-param name="observeGridCells" select="$observeGridCells"/>
          <xsl:with-param name="gridSpaceVarName" select="$gridSpaceVarName"/>
          <xsl:with-param name="sim.class.simGridCell" select="$sim.class.simGridCell"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>

    <!-- initPropertyChangeListener(PropertyChangeListener propertyChangeListener) -->
    <xsl:call-template name="initGridCellPropertyChangeListener">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="observeGridCells" select="$observeGridCells"/>
      <xsl:with-param name="gridSpaceVarName" select="$gridSpaceVarName"/>
      <xsl:with-param name="sim.class.simGridCell" select="$sim.class.simGridCell"/>
    </xsl:call-template>

  </xsl:template>

  <!-- create the methods from gridcellinterface -->
  <!-- deprecated -->
  <xsl:template name="createGetGrid">
    <xsl:param name="indent"/>
    <xsl:param name="sim.class.simGridCell"/>
    <xsl:param name="gridSpaceVarName"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="$sim.class.simGridCell"/>
      <xsl:with-param name="name" select="'getGridCell'"/>
      <xsl:with-param name="parameterList" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'int'"/>
          <xsl:with-param name="name" select="'xPos'"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'int'"/>
          <xsl:with-param name="name" select="'yPos'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">
        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="typecast" select="$sim.class.simGridCell"/>
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="name" select="'this'"/>
                  <xsl:with-param name="varName" select="$gridSpaceVarName"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="method" select="'getCell'"/>
              <xsl:with-param name="args" as="xs:string*" select="('xPos', 'yPos')"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="createInitGrid">
    <xsl:param name="indent"/>
    <xsl:param name="logger.class.objInitEventListener"/>
    <xsl:param name="observeGridCells"/>
    <xsl:param name="gridSpaceVarName"/>
    <xsl:param name="sim.class.simGridCell"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="'initGrid'"/>
      <xsl:with-param name="parameterList" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'int'"/>
          <xsl:with-param name="name" select="'xSize'"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'int'"/>
          <xsl:with-param name="name" select="'ySize'"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="$logger.class.objInitEventListener"/>
          <xsl:with-param name="name" select="jw:lowerWord($logger.class.objInitEventListener)"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <!-- this.addObjektInitListener(logger.getObjLogger()); -->
        <xsl:if test="$observeGridCells">
          <xsl:call-template name="java:newLine"/>
          <xsl:call-template name="java:callMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="method" select="'addObjektInitListener'"/>
            <xsl:with-param name="args" as="xs:string" select="jw:lowerWord($logger.class.objInitEventListener)"/>
          </xsl:call-template>
          <xsl:call-template name="java:newLine"/>
        </xsl:if>

        <!-- instanziate the gridcells -->
        <xsl:variable name="colCounter" select="'i'"/>
        <xsl:variable name="rowCounter" select="'j'"/>
        <xsl:call-template name="java:for-loop">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="loopVariable" select="$colCounter"/>
          <xsl:with-param name="start" select="'0'"/>
          <xsl:with-param name="condition">
            <xsl:call-template name="java:boolExpr">
              <xsl:with-param name="value1" select="$colCounter"/>
              <xsl:with-param name="value2" select="'xSize'"/>
              <xsl:with-param name="operator" select="'&lt;'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="increment" select="1"/>
          <xsl:with-param name="content">
            <xsl:call-template name="java:newLine"/>

            <xsl:call-template name="java:for-loop">
              <xsl:with-param name="indent" select="$indent + 2"/>
              <xsl:with-param name="loopVariable" select="$rowCounter"/>
              <xsl:with-param name="start" select="'0'"/>
              <xsl:with-param name="condition">
                <xsl:call-template name="java:boolExpr">
                  <xsl:with-param name="value1" select="$rowCounter"/>
                  <xsl:with-param name="value2" select="'ySize'"/>
                  <xsl:with-param name="operator" select="'&lt;'"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="increment" select="1"/>
              <xsl:with-param name="content">

                <!-- instanziate the cell -->
                <xsl:call-template name="java:variable">
                  <xsl:with-param name="indent" select="$indent  + 3"/>
                  <xsl:with-param name="name">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="name" select="'this'"/>
                      <xsl:with-param name="varName" select="$gridSpaceVarName"/>
                    </xsl:call-template>
                    <!-- NOTICE: be carefull, its e special java-construct -->
                    <xsl:value-of select="fn:concat('.getSpaceCells()[', $colCounter, '][', $rowCounter, ']')"/>
                  </xsl:with-param>
                  <xsl:with-param name="value">
                    <xsl:call-template name="java:newObject">
                      <xsl:with-param name="onlyInitialization" select="true()"/>
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="class" select="$sim.class.simGridCell"/>
                      <xsl:with-param name="args" as="xs:string*">
                        <xsl:choose>
                          <xsl:when test="@dimensions eq '2' or exists(aorsl:TwoDimensionalGrid)">
                            <xsl:value-of select="$colCounter"/>
                            <xsl:value-of select="$rowCounter"/>
                            <xsl:value-of select="'0'"/>
                          </xsl:when>
                          <xsl:otherwise>
                            <xsl:message>
                              <xsl:text>Non cells implementetd for dimension </xsl:text>
                              <xsl:value-of select="if (@dimension) then @dimension else local-name(aorsl:*)"/>
                            </xsl:message>
                          </xsl:otherwise>
                        </xsl:choose>
                      </xsl:with-param>
                    </xsl:call-template>
                  </xsl:with-param>
                </xsl:call-template>
                <!-- the propertyChangeListener will be add after the initializing -->

              </xsl:with-param>
            </xsl:call-template>

          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="aorsl:OneDimensionalGrid | aorsl:TwoDimensionalGrid | aorsl:ThreeDimensionalGrid"
    mode="createSimulationSpacemodel.interfaceMethods.gridCells.initGrid">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="observeGridCells" as="xs:boolean" select="false()"/>
    <xsl:param name="gridSpaceVarName" as="xs:string" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="'initGrid'"/>
      <xsl:with-param name="parameterList" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'int'"/>
          <xsl:with-param name="name" select="'xSize'"/>
        </xsl:call-template>
        <xsl:if test="local-name() eq 'TwoDimensionalGrid' or local-name() eq 'ThreeDimensionalGrid'">
          <xsl:call-template name="java:createParam">
            <xsl:with-param name="type" select="'int'"/>
            <xsl:with-param name="name" select="'ySize'"/>
          </xsl:call-template>
        </xsl:if>
        <xsl:if test="local-name() eq 'ThreeDimensionalGrid'">
          <xsl:call-template name="java:createParam">
            <xsl:with-param name="type" select="'int'"/>
            <xsl:with-param name="name" select="'zSize'"/>
          </xsl:call-template>
        </xsl:if>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="$logger.class.objInitEventListener"/>
          <xsl:with-param name="name" select="jw:lowerWord($logger.class.objInitEventListener)"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <!-- this.addObjektInitListener(logger.getObjLogger()); -->
        <xsl:if test="$observeGridCells">
          <xsl:call-template name="java:newLine"/>
          <xsl:call-template name="java:callMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="method" select="'addObjektInitListener'"/>
            <xsl:with-param name="args" as="xs:string" select="jw:lowerWord($logger.class.objInitEventListener)"/>
          </xsl:call-template>
          <xsl:call-template name="java:newLine"/>
        </xsl:if>

        <!-- instanziate the gridcells -->
        <xsl:variable name="colCounter" select="'i'"/>
        <xsl:variable name="rowCounter" select="'j'"/>
        <xsl:call-template name="java:for-loop">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="loopVariable" select="$colCounter"/>
          <xsl:with-param name="start" select="'0'"/>
          <xsl:with-param name="condition">
            <xsl:call-template name="java:boolExpr">
              <xsl:with-param name="value1" select="$colCounter"/>
              <xsl:with-param name="value2" select="'xSize'"/>
              <xsl:with-param name="operator" select="'&lt;'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="increment" select="1"/>
          <xsl:with-param name="content">

            <xsl:call-template name="java:for-loop">
              <xsl:with-param name="indent" select="$indent + 2"/>
              <xsl:with-param name="loopVariable" select="$rowCounter"/>
              <xsl:with-param name="start" select="'0'"/>
              <xsl:with-param name="condition">
                <xsl:call-template name="java:boolExpr">
                  <xsl:with-param name="value1" select="$rowCounter"/>
                  <xsl:with-param name="value2" select="'ySize'"/>
                  <xsl:with-param name="operator" select="'&lt;'"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="increment" select="1"/>
              <xsl:with-param name="content">

                <!-- instanziate the cell -->
                <xsl:call-template name="java:variable">
                  <xsl:with-param name="indent" select="$indent  + 3"/>
                  <xsl:with-param name="name">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="$gridSpaceVarName"/>
                    </xsl:call-template>
                    <!-- NOTICE: be carefull, its e special java-construct -->
                    <xsl:value-of select="fn:concat('.getSpaceCells()[', $colCounter, '][', $rowCounter, ']')"/>
                  </xsl:with-param>
                  <xsl:with-param name="value">
                    <xsl:call-template name="java:newObject">
                      <xsl:with-param name="onlyInitialization" select="true()"/>
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="class" select="$sim.class.simGridCell"/>
                      <xsl:with-param name="args" as="xs:string*">
                        <xsl:value-of select="$colCounter"/>
                        <xsl:if test="local-name() eq 'TwoDimensionalGrid' or local-name() eq 'ThreeDimensionalGrid'">
                          <xsl:value-of select="$rowCounter"/>
                        </xsl:if>
                        <xsl:if test="local-name() eq 'ThreeDimensionalGrid'">
                          <xsl:message>
                            <xsl:text>ThreeDimensionalGrid Not yet implemented</xsl:text>
                          </xsl:message>
                        </xsl:if>
                      </xsl:with-param>
                    </xsl:call-template>
                  </xsl:with-param>
                </xsl:call-template>
                <!-- the propertyChangeListener will be add after the initializing -->

              </xsl:with-param>
            </xsl:call-template>

          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="initGridCellPropertyChangeListener">
    <xsl:param name="indent"/>
    <xsl:param name="observeGridCells"/>
    <xsl:param name="gridSpaceVarName"/>
    <xsl:param name="sim.class.simGridCell"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="'initPropertyChangeListener'"/>
      <xsl:with-param name="parameterList" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'PropertyChangeListener'"/>
          <xsl:with-param name="name" select="'__pcl'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <!-- instanziate the gridcells -->
        <xsl:variable name="colCounter" select="'i'"/>
        <xsl:variable name="rowCounter" select="'j'"/>
        <xsl:call-template name="java:for-loop">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="loopVariable" select="$colCounter"/>
          <xsl:with-param name="start" select="'0'"/>
          <xsl:with-param name="condition">
            <xsl:call-template name="java:boolExpr">
              <xsl:with-param name="value1" select="$colCounter"/>
              <xsl:with-param name="value2">
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="objInstance">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="$gridSpaceVarName"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="instVariable" select="'xSize'"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="operator" select="'&lt;'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="increment" select="1"/>
          <xsl:with-param name="content">
            <xsl:call-template name="java:newLine"/>

            <xsl:call-template name="java:for-loop">
              <xsl:with-param name="indent" select="$indent + 2"/>
              <xsl:with-param name="loopVariable" select="$rowCounter"/>
              <xsl:with-param name="start" select="'0'"/>
              <xsl:with-param name="condition">
                <xsl:call-template name="java:boolExpr">
                  <xsl:with-param name="value1" select="$rowCounter"/>
                  <xsl:with-param name="value2">
                    <xsl:call-template name="java:callGetterMethod">
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="objInstance">
                        <xsl:call-template name="java:varByDotNotation">
                          <xsl:with-param name="varName" select="$gridSpaceVarName"/>
                        </xsl:call-template>
                      </xsl:with-param>
                      <xsl:with-param name="instVariable" select="'ySize'"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="operator" select="'&lt;'"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="increment" select="1"/>
              <xsl:with-param name="content">

                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="indent" select="$indent + 3"/>
                  <xsl:with-param name="objInstance">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="$gridSpaceVarName"/>
                    </xsl:call-template>
                    <!-- NOTICE: be carefull, its e special java-construct -->
                    <xsl:value-of select="fn:concat('.getSpaceCells()[', $colCounter, '][', $rowCounter, ']')"/>
                  </xsl:with-param>
                  <xsl:with-param name="method" select="'addPropertyChangeListener'"/>
                  <xsl:with-param name="args" select="'__pcl'"/>
                </xsl:call-template>

              </xsl:with-param>
            </xsl:call-template>

          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>


  <!-- create the gridCell -->
  <!-- deprecated -->
  <xsl:template match="aorsl:SpaceModel" mode="createSimulationSpacemodel.createGridCell">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="observeGridCells" as="xs:boolean" select="false()"/>

    <xsl:call-template name="java:class">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="$sim.class.simGridCell"/>
      <xsl:with-param name="extends" select="$space.class.abstractCell"/>
      <xsl:with-param name="content">

        <!-- classVariables -->
        <xsl:apply-templates select="aorsl:GridCellProperty" mode="assistents.classVariable">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>
        <xsl:call-template name="java:newLine"/>

        <xsl:call-template name="java:constructor">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="name" select="$sim.class.simGridCell"/>
          <xsl:with-param name="parameters" as="xs:string*">
            <xsl:call-template name="java:createParam">
              <xsl:with-param name="type" select="'int'"/>
              <xsl:with-param name="name" select="'xPos'"/>
            </xsl:call-template>
            <xsl:call-template name="java:createParam">
              <xsl:with-param name="type" select="'int'"/>
              <xsl:with-param name="name" select="'yPos'"/>
            </xsl:call-template>
            <xsl:call-template name="java:createParam">
              <xsl:with-param name="type" select="'int'"/>
              <xsl:with-param name="name" select="'zPos'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="content">

            <xsl:call-template name="java:callSuper">
              <xsl:with-param name="indent" select="$indent + 2"/>
              <xsl:with-param name="paramList" as="xs:string*" select="('xPos', 'yPos', 'zPos')"/>
            </xsl:call-template>

            <!-- creates PropertyChangeSupport if is necessary to observe the gridcells, to instanziate the propertyChangeSupport -->
            <xsl:if test="$observeGridCells">
              <xsl:call-template name="java:newObject">
                <xsl:with-param name="indent" select="$indent + 2"/>
                <xsl:with-param name="class" select="'PropertyChangeSupport'"/>
                <xsl:with-param name="varName">
                  <xsl:call-template name="java:varByDotNotation">
                    <xsl:with-param name="varName" select="'propertyChangeSupport'"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="args" as="xs:string" select="'this'"/>
                <xsl:with-param name="isVariable" select="true()"/>
              </xsl:call-template>
            </xsl:if>

          </xsl:with-param>
        </xsl:call-template>


        <!-- setters -->
        <xsl:for-each select="aorsl:GridCellProperty">
          <xsl:apply-templates select="." mode="assistents.setVariableMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="changeCheck" select="true()"/>
            <xsl:with-param name="extraContent">

              <xsl:if test="$observeGridCells">
                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="indent" select="$indent + 3"/>
                  <xsl:with-param name="objInstance">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="'propertyChangeSupport'"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="method" select="'firePropertyChange'"/>
                  <xsl:with-param name="args" as="xs:string*">
                    <xsl:call-template name="java:newObject">
                      <xsl:with-param name="class" select="'PropertyChangeEvent'"/>
                      <xsl:with-param name="args" as="xs:string*">
                        <xsl:value-of select="'this'"/>
                        <xsl:value-of select="jw:quote(./@name)"/>
                        <xsl:value-of select="'null'"/>
                        <xsl:call-template name="java:varByDotNotation">
                          <xsl:with-param name="varName" select="./@name"/>
                        </xsl:call-template>
                      </xsl:with-param>
                      <xsl:with-param name="isVariable" select="true()"/>
                      <xsl:with-param name="inLine" select="true()"/>
                    </xsl:call-template>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:if>

            </xsl:with-param>
          </xsl:apply-templates>
        </xsl:for-each>

        <!-- getters -->
        <xsl:apply-templates select="aorsl:GridCellProperty" mode="assistents.getVariableMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <!-- gridCellFunctions -->
        <xsl:apply-templates select="aorsl:GridCellFunction" mode="shared.createFunction">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- create the gridCell -->
  <xsl:template match="aorsl:OneDimensionalGrid | aorsl:TwoDimensionalGrid | aorsl:ThreeDimensionalGrid"
    mode="createSimulationSpacemodel.createGridCell">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="observeGridCells" as="xs:boolean" select="false()"/>

    <xsl:call-template name="java:class">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="$sim.class.simGridCell"/>
      <xsl:with-param name="extends" select="$space.class.abstractCell"/>
      <xsl:with-param name="content">

        <!-- classVariables -->
        <xsl:apply-templates select="aorsl:GridCellProperty" mode="assistents.classVariable">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>
        <xsl:call-template name="java:newLine"/>

        <xsl:call-template name="java:constructor">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="name" select="$sim.class.simGridCell"/>
          <xsl:with-param name="parameters" as="xs:string*">
            <xsl:call-template name="java:createParam">
              <xsl:with-param name="type" select="'int'"/>
              <xsl:with-param name="name" select="'xPos'"/>
            </xsl:call-template>
            <xsl:if test="local-name() eq 'TwoDimensionalGrid' or local-name() eq 'ThreeDimensionalGrid'">
              <xsl:call-template name="java:createParam">
                <xsl:with-param name="type" select="'int'"/>
                <xsl:with-param name="name" select="'yPos'"/>
              </xsl:call-template>
            </xsl:if>
            <xsl:if test="local-name() eq 'ThreeDimensionalGrid'">
              <xsl:call-template name="java:createParam">
                <xsl:with-param name="type" select="'int'"/>
                <xsl:with-param name="name" select="'zPos'"/>
              </xsl:call-template>
            </xsl:if>
          </xsl:with-param>
          <xsl:with-param name="content">

            <xsl:call-template name="java:callSuper">
              <xsl:with-param name="indent" select="$indent + 2"/>
              <xsl:with-param name="paramList" as="xs:string*">
                <xsl:value-of select="'xPos'"/>
                <xsl:if test="local-name() eq 'TwoDimensionalGrid' or local-name() eq 'ThreeDimensionalGrid'">
                  <xsl:value-of select="'yPos'"/>
                </xsl:if>
                <xsl:if test="local-name() eq 'ThreeDimensionalGrid'">
                  <xsl:value-of select="'zPos'"/>
                </xsl:if>
              </xsl:with-param>
            </xsl:call-template>

            <!-- creates PropertyChangeSupport if is necessary to observe the gridcells, to instanziate the propertyChangeSupport -->
            <xsl:if test="$observeGridCells">
              <xsl:call-template name="java:newObject">
                <xsl:with-param name="indent" select="$indent + 2"/>
                <xsl:with-param name="class" select="'PropertyChangeSupport'"/>
                <xsl:with-param name="varName">
                  <xsl:call-template name="java:varByDotNotation">
                    <xsl:with-param name="varName" select="'propertyChangeSupport'"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="args" as="xs:string" select="'this'"/>
                <xsl:with-param name="isVariable" select="true()"/>
              </xsl:call-template>
            </xsl:if>

          </xsl:with-param>
        </xsl:call-template>


        <!-- setters -->
        <xsl:for-each select="aorsl:GridCellProperty">
          <xsl:apply-templates select="." mode="assistents.setVariableMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="changeCheck" select="true()"/>
            <xsl:with-param name="extraContent">

              <xsl:if test="$observeGridCells">
                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="indent" select="$indent + 3"/>
                  <xsl:with-param name="objInstance">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="'propertyChangeSupport'"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="method" select="'firePropertyChange'"/>
                  <xsl:with-param name="args" as="xs:string*">
                    <xsl:call-template name="java:newObject">
                      <xsl:with-param name="class" select="'PropertyChangeEvent'"/>
                      <xsl:with-param name="args" as="xs:string*">
                        <xsl:value-of select="'this'"/>
                        <xsl:value-of select="jw:quote(./@name)"/>
                        <xsl:value-of select="'null'"/>
                        <xsl:call-template name="java:varByDotNotation">
                          <xsl:with-param name="varName" select="@name"/>
                        </xsl:call-template>
                      </xsl:with-param>
                      <xsl:with-param name="isVariable" select="true()"/>
                      <xsl:with-param name="inLine" select="true()"/>
                    </xsl:call-template>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:if>

            </xsl:with-param>
          </xsl:apply-templates>
        </xsl:for-each>

        <!-- getters -->
        <xsl:apply-templates select="aorsl:GridCellProperty" mode="assistents.getVariableMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <!-- gridCellFunctions -->
        <xsl:apply-templates select="aorsl:GridCellFunction" mode="shared.createFunction">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>


</xsl:stylesheet>
