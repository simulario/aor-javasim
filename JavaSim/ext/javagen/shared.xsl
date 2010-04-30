<?xml version="1.0" encoding="UTF-8"?>

<!--
    
        This transformation contain some usefull shared templates.
        
        $Rev: 4871 $
        $Date: 2010-04-23 15:36:12 +0200 (Fri, 23 Apr 2010) $

        @author:   Jens Werner (jens.werner@tu-cottbus.de)
        @last changed by $Author: jewerner $
-->

<xsl:stylesheet version="2.0" xmlns:aorsl="http://aor-simulation.org" xmlns:aorsml="http://aor-simulation.org"
  xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:schemaLocation="http://aor-simulation.org aorsml.xsd" xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/"
  xmlns:generator="http://aor-simulation.org/code-generator" xmlns:allex="http://aor-simulation.org/expression-language/ALLEX"
  xmlns:cgel="http://aor-simulation.org/code-generator/expression-language/CGEL">

  <!-- it can be used for physObjectcreation in createSimSystem.xsl and for dynamic physObject creation in EnvironmentRules -->

  <!-- this template is used to achieve the order of inits -->
  <!-- first is the order of entitytypes, second the order of inits -->
  <xsl:template
    match="aorsml:PhysicalAgent | aorsml:PhysicalAgents | aorsml:PhysicalObject | aorsml:PhysicalObjects | aorsml:Object | aorsml:Agent | aorsml:Agents"
    mode="shared.helper.initAORObjects.manager">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="className" required="yes"/>
    <xsl:param name="varName" required="yes"/>
    <xsl:param name="envSimVarName"/>
    <xsl:param name="id"/>

    <xsl:choose>
      <xsl:when test="local-name() = 'PhysicalAgent' or local-name() = 'PhysicalObject' or local-name() = 'Agent' or local-name() = 'Object' ">
        <xsl:apply-templates select="." mode="shared.helper.initAORObjects">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="className" select="$className"/>
          <xsl:with-param name="varName" select="if (@objectVariable) then @objectVariable else $varName"/>
          <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="." mode="shared.helper.initAORObjectsSet">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="className" select="$className"/>
          <xsl:with-param name="varName" select="$varName"/>
          <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
        </xsl:apply-templates>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!-- set the initial objects (used by initAgents and initObjects) -->
  <xsl:template
    match="aorsml:PhysicalAgent | aorsml:PhysicalAgents | aorsml:PhysicalObject | aorsml:PhysicalObjects | aorsml:Object | aorsml:Agent | aorsml:Agents"
    mode="shared.helper.initAORObjects">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="className" required="yes"/>
    <xsl:param name="varName" required="yes"/>
    <xsl:param name="envSimVarName"/>
    <xsl:param name="id"/>
    <xsl:param name="spaceReservationSystem" as="xs:boolean" required="yes" tunnel="yes"/>
    <xsl:param name="discreteSpace" as="xs:boolean" select="false()" tunnel="yes"/>

    <!-- create a new intance -->
    <xsl:call-template name="java:newObject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="class" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:when test="$id != ''">
            <xsl:value-of select="$id"/>
          </xsl:when>
        </xsl:choose>

        <xsl:value-of select="if (@name) then jw:quote(@name) else jw:quote($empty.string.quotation.symbol)"/>

        <xsl:variable name="objNode" select="current()"/>
        <xsl:variable name="dataTypesNode" select="//aorsml:DataTypes"/>
        <xsl:choose>
          <xsl:when test="local-name() = 'PhysicalAgent' or local-name() = 'PhysicalAgents'">

            <xsl:variable name="physAgentNode" select="//aorsml:PhysicalAgentType[@name = current()/@type][1]"/>

            <!-- create the constructorlist for PhysicalAgents -->
            <!-- use all attribute from PhysicalAgentTypeInstanzes and SuperTypes, without default attributes-->
            <xsl:variable name="attributes" as="node()*">
              <xsl:apply-templates select="$physAgentNode" mode="assistents.list.allAttributes"/>
            </xsl:variable>

            <xsl:for-each select="$attributes">

              <xsl:choose>
                <xsl:when test="local-name() eq 'EnumerationProperty'">

                  <xsl:variable name="enumeration" select="$dataTypesNode/aorsml:Enumeration[@name = current()/@type][1]"/>
                  <xsl:if test="fn:exists($enumeration)">
                    <xsl:call-template name="setEnumInConstructor">
                      <xsl:with-param name="objNode" select="$objNode"/>
                      <xsl:with-param name="enumeration" select="$enumeration"/>
                      <xsl:with-param name="objTypeNode" select="$physAgentNode"/>
                    </xsl:call-template>
                  </xsl:if>
                </xsl:when>
                <xsl:otherwise>

                  <xsl:choose>
                    <xsl:when test="@upperMultiplicity eq 'unbounded'">
                      <xsl:choose>
                        <!-- depricated -->
                        <xsl:when
                          test="fn:exists($objNode/aorsml:Slot[resolve-QName(@xsi:type, .) eq QName('http://aor-simulation.org', 'OpaqueExprSlot')][@property eq current()/@name]/aorsml:ValueExpr[@language eq $output.language])">
                          <xsl:value-of
                            select="$objNode/aorsml:Slot[resolve-QName(@xsi:type, .) eq QName('http://aor-simulation.org', 'OpaqueExprSlot')][@property eq current()/@name]/aorsml:ValueExpr[@language eq $output.language][1]"
                          />
                        </xsl:when>
                        <!-- new -->
                        <xsl:when test="fn:exists($objNode/aorsml:Slot[@property eq current()/@name]/aorsml:ValueExpr[@language eq $output.language])">
                          <xsl:value-of select="$objNode/aorsml:Slot[@property eq current()/@name]/aorsml:ValueExpr[@language eq $output.language][1]"
                          />
                        </xsl:when>
                        <!-- depricated -->
                        <xsl:when
                          test="fn:exists($objNode/aorsml:Slot[resolve-QName(@xsi:type, .) eq QName('http://aor-simulation.org', 'SimpleSlot')][@property eq current()/@name])">
                          <xsl:message>unbounded upperMultiplicity instanced by simpleSlot is not yet implemented</xsl:message>
                        </xsl:when>
                        <!-- new -->
                        <xsl:when test="fn:exists($objNode/aorsml:Slot[@property eq current()/@name]/@value)">
                          <xsl:message>unbounded upperMultiplicity instanced by simpleSlot is not yet implemented</xsl:message>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:value-of select="'null'"/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>

                      <xsl:apply-templates select="." mode="shared.helper.initAORObjects.ComplexDataProperty">
                        <xsl:with-param name="objNode" select="$objNode"/>
                      </xsl:apply-templates>

                      <xsl:choose>
                        <xsl:when test="fn:exists($objNode/aorsml:Slot[@property = current()/@name])">
                          <xsl:apply-templates select="$objNode/aorsml:Slot[@property = current()/@name]" mode="assistents.getSlotValue">
                            <xsl:with-param name="type" select="@type"/>
                          </xsl:apply-templates>
                        </xsl:when>
                        <xsl:when test="fn:exists($physAgentNode/aorsml:InitialAttributeValue[@attribute eq current()/@name])">
                          <xsl:apply-templates select="$physAgentNode/aorsml:InitialAttributeValue[@attribute eq current()/@name][1]"
                            mode="assistents.setInitialAttributeValue"/>
                        </xsl:when>
                        <xsl:when test="@initialValue">
                          <xsl:apply-templates select="@initialValue" mode="assistents.getValue">
                            <xsl:with-param name="type" select="@type"/>
                          </xsl:apply-templates>
                        </xsl:when>
                        <xsl:when test="local-name() eq 'ComplexDataProperty'">
                          <!-- do nothing -->
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:call-template name="java:setDefaultValue">
                            <xsl:with-param name="type" select="@type"/>
                          </xsl:call-template>
                        </xsl:otherwise>
                      </xsl:choose>

                    </xsl:otherwise>
                  </xsl:choose>

                </xsl:otherwise>
              </xsl:choose>

            </xsl:for-each>

          </xsl:when>
          <xsl:when test="local-name() = 'Agent' or local-name() = 'Agents'">

            <xsl:variable name="agentNode" select="//aorsml:AgentType[@name = current()/@type][1]"/>

            <!-- create the constructorlist for Agents -->
            <!-- use all attribute from AgentTypeInstanzes and SuperTypes, without default attributes-->
            <xsl:variable name="attributes" as="node()*">
              <xsl:apply-templates select="$agentNode" mode="assistents.list.allAttributes"/>
            </xsl:variable>

            <xsl:for-each select="$attributes">

              <xsl:choose>
                <xsl:when test="local-name() eq 'EnumerationProperty'">

                  <xsl:variable name="enumeration" select="$dataTypesNode/aorsml:Enumeration[@name = current()/@type][1]"/>
                  <xsl:if test="fn:exists($enumeration)">
                    <xsl:call-template name="setEnumInConstructor">
                      <xsl:with-param name="objNode" select="$objNode"/>
                      <xsl:with-param name="enumeration" select="$enumeration"/>
                      <xsl:with-param name="objTypeNode" select="$agentNode"/>
                    </xsl:call-template>
                  </xsl:if>
                </xsl:when>
                <xsl:otherwise>

                  <xsl:choose>
                    <xsl:when test="@upperMultiplicity eq 'unbounded'">
                      <xsl:choose>
                        <!-- depricated -->
                        <xsl:when
                          test="fn:exists($objNode/aorsml:Slot[resolve-QName(@xsi:type, .) eq QName('http://aor-simulation.org', 'OpaqueExprSlot')][@property eq current()/@name]/aorsml:ValueExpr[@language eq $output.language])">
                          <xsl:value-of
                            select="$objNode/aorsml:Slot[resolve-QName(@xsi:type, .) eq QName('http://aor-simulation.org', 'OpaqueExprSlot')][@property eq current()/@name]/aorsml:ValueExpr[@language eq $output.language][1]"
                          />
                        </xsl:when>
                        <!-- new -->
                        <xsl:when test="fn:exists($objNode/aorsml:Slot[@property eq current()/@name]/aorsml:ValueExpr[@language eq $output.language])">
                          <xsl:value-of select="$objNode/aorsml:Slot[@property eq current()/@name]/aorsml:ValueExpr[@language eq $output.language][1]"
                          />
                        </xsl:when>
                        <!-- depricated -->
                        <xsl:when
                          test="fn:exists($objNode/aorsml:Slot[resolve-QName(@xsi:type, .) eq QName('http://aor-simulation.org', 'SimpleSlot')][@property eq current()/@name])">
                          <xsl:message>unbounded upperMultiplicity instanced by simpleSlot is not yet implemented</xsl:message>
                        </xsl:when>
                        <!-- new -->
                        <xsl:when test="fn:exists($objNode/aorsml:Slot[@property eq current()/@name]/@value)">
                          <xsl:message>unbounded upperMultiplicity instanced by simpleSlot is not yet implemented</xsl:message>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:value-of select="'null'"/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>

                      <xsl:apply-templates select="." mode="shared.helper.initAORObjects.ComplexDataProperty">
                        <xsl:with-param name="objNode" select="$objNode"/>
                      </xsl:apply-templates>

                      <xsl:choose>
                        <xsl:when test="fn:exists($objNode/aorsml:Slot[@property = current()/@name])">
                          <xsl:apply-templates select="$objNode/aorsml:Slot[@property = current()/@name]" mode="assistents.getSlotValue">
                            <xsl:with-param name="type" select="@type"/>
                          </xsl:apply-templates>
                        </xsl:when>
                        <xsl:when test="fn:exists($agentNode/aorsml:InitialAttributeValue[@attribute eq current()/@name])">
                          <xsl:apply-templates select="$agentNode/aorsml:InitialAttributeValue[@attribute eq current()/@name][1]"
                            mode="assistents.setInitialAttributeValue"/>
                        </xsl:when>
                        <xsl:when test="@initialValue">
                          <xsl:apply-templates select="@initialValue" mode="assistents.getValue">
                            <xsl:with-param name="type" select="@type"/>
                          </xsl:apply-templates>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:if
                            test="not(root($objNode)//aorsml:DataTypes/aorsml:ComplexDataType[@name eq current()/@type][1]/aorsml:ClassDef[@language eq $output.language])">
                            <xsl:call-template name="java:setDefaultValue">
                              <xsl:with-param name="type" select="@type"/>
                            </xsl:call-template>
                          </xsl:if>
                        </xsl:otherwise>
                      </xsl:choose>

                    </xsl:otherwise>
                  </xsl:choose>

                </xsl:otherwise>
              </xsl:choose>

            </xsl:for-each>

          </xsl:when>
          <xsl:when test="local-name() = 'PhysicalObject' or local-name() = 'PhysicalObjects'">

            <xsl:variable name="physObjNode" as="node()" select="//aorsml:PhysicalObjectType[@name = current()/@type][1]"/>

            <!-- create the constructorlist for PhysicalObjekts -->
            <xsl:variable name="attributes" as="node()*">
              <xsl:apply-templates select="$physObjNode" mode="assistents.list.allAttributes"/>
            </xsl:variable>
            <xsl:for-each select="$attributes">

              <xsl:choose>
                <xsl:when test="local-name() eq 'EnumerationProperty'">

                  <xsl:variable name="enumeration" select="$dataTypesNode/aorsml:Enumeration[@name = current()/@type][1]"/>
                  <xsl:if test="fn:exists($enumeration)">
                    <xsl:call-template name="setEnumInConstructor">
                      <xsl:with-param name="objNode" select="$objNode"/>
                      <xsl:with-param name="enumeration" select="$enumeration"/>
                      <xsl:with-param name="objTypeNode" select="$physObjNode"/>
                    </xsl:call-template>
                  </xsl:if>

                </xsl:when>
                <xsl:otherwise>

                  <xsl:choose>
                    <xsl:when test="@upperMultiplicity eq 'unbounded'">
                      <xsl:choose>
                        <!-- depricated -->
                        <xsl:when
                          test="fn:exists($objNode/aorsml:Slot[resolve-QName(@xsi:type, .) eq QName('http://aor-simulation.org', 'OpaqueExprSlot')][@property eq current()/@name]/aorsml:ValueExpr[@language eq $output.language])">
                          <xsl:value-of
                            select="$objNode/aorsml:Slot[resolve-QName(@xsi:type, .) eq QName('http://aor-simulation.org', 'OpaqueExprSlot')][@property eq current()/@name]/aorsml:ValueExpr[@language eq $output.language][1]"
                          />
                        </xsl:when>
                        <!-- new -->
                        <xsl:when test="fn:exists($objNode/aorsml:Slot[@property eq current()/@name]/aorsml:ValueExpr[@language eq $output.language])">
                          <xsl:value-of select="$objNode/aorsml:Slot[@property eq current()/@name]/aorsml:ValueExpr[@language eq $output.language][1]"
                          />
                        </xsl:when>
                        <!-- depricated -->
                        <xsl:when
                          test="fn:exists($objNode/aorsml:Slot[resolve-QName(@xsi:type, .) eq QName('http://aor-simulation.org', 'SimpleSlot')][@property eq current()/@name])">
                          <xsl:message>unbounded upperMultiplicity instanced by simpleSlot is not yet implemented</xsl:message>
                        </xsl:when>
                        <!-- new -->
                        <xsl:when test="fn:exists($objNode/aorsml:Slot[@property eq current()/@name]/@value)">
                          <xsl:message>unbounded upperMultiplicity instanced by simpleSlot is not yet implemented</xsl:message>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:value-of select="'null'"/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>

                      <xsl:apply-templates select="." mode="shared.helper.initAORObjects.ComplexDataProperty">
                        <xsl:with-param name="objNode" select="$objNode"/>
                      </xsl:apply-templates>

                      <xsl:choose>
                        <xsl:when test="fn:exists($objNode/aorsml:Slot[@property = current()/@name])">
                          <xsl:apply-templates select="$objNode/aorsml:Slot[@property = current()/@name]" mode="assistents.getSlotValue">
                            <xsl:with-param name="type" select="@type"/>
                          </xsl:apply-templates>
                        </xsl:when>
                        <xsl:when test="fn:exists($physObjNode/aorsml:InitialAttributeValue[@attribute eq current()/@name])">
                          <xsl:apply-templates select="$physObjNode/aorsml:InitialAttributeValue[@attribute eq current()/@name][1]"
                            mode="assistents.setInitialAttributeValue"/>
                        </xsl:when>
                        <xsl:when test="@initialValue">
                          <xsl:apply-templates select="@initialValue" mode="assistents.getValue">
                            <xsl:with-param name="type" select="@type"/>
                          </xsl:apply-templates>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:if test="not(aorsml:ClassDef[@language eq $output.language])">
                            <xsl:call-template name="java:setDefaultValue">
                              <xsl:with-param name="type" select="@type"/>
                            </xsl:call-template>
                          </xsl:if>
                        </xsl:otherwise>
                      </xsl:choose>

                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:otherwise>
              </xsl:choose>

            </xsl:for-each>

          </xsl:when>

          <xsl:when test="local-name() = 'Object'">

            <xsl:variable name="objNodeType" as="node()" select="//aorsml:ObjectType[@name = current()/@type][1]"/>

            <!-- create the constructorlist for Objekts -->
            <xsl:variable name="attributes" as="node()*">
              <xsl:apply-templates select="$objNodeType" mode="assistents.list.allAttributes"/>
            </xsl:variable>
            <xsl:for-each select="$attributes">

              <xsl:choose>
                <xsl:when test="local-name() eq 'EnumerationProperty'">

                  <xsl:variable name="enumeration" select="$dataTypesNode/aorsml:Enumeration[@name = current()/@type][1]"/>
                  <xsl:if test="fn:exists($enumeration)">
                    <xsl:call-template name="setEnumInConstructor">
                      <xsl:with-param name="objNode" select="$objNode"/>
                      <xsl:with-param name="enumeration" select="$enumeration"/>
                      <xsl:with-param name="objTypeNode" select="$objNodeType"/>
                    </xsl:call-template>
                  </xsl:if>

                </xsl:when>
                <xsl:otherwise>

                  <xsl:choose>
                    <xsl:when test="@upperMultiplicity eq 'unbounded'">
                      <xsl:choose>
                        <!-- depricated -->
                        <xsl:when
                          test="fn:exists($objNode/aorsml:Slot[resolve-QName(@xsi:type, .) eq QName('http://aor-simulation.org', 'OpaqueExprSlot')][@property eq current()/@name]/aorsml:ValueExpr[@language eq $output.language])">
                          <xsl:value-of
                            select="$objNode/aorsml:Slot[resolve-QName(@xsi:type, .) eq QName('http://aor-simulation.org', 'OpaqueExprSlot')][@property eq current()/@name]/aorsml:ValueExpr[@language eq $output.language][1]"
                          />
                        </xsl:when>
                        <!-- new -->
                        <xsl:when test="fn:exists($objNode/aorsml:Slot[@property eq current()/@name]/aorsml:ValueExpr[@language eq $output.language])">
                          <xsl:value-of select="$objNode/aorsml:Slot[@property eq current()/@name]/aorsml:ValueExpr[@language eq $output.language][1]"
                          />
                        </xsl:when>
                        <!-- depricated -->
                        <xsl:when
                          test="fn:exists($objNode/aorsml:Slot[resolve-QName(@xsi:type, .) eq QName('http://aor-simulation.org', 'SimpleSlot')][@property eq current()/@name])">
                          <xsl:message>unbounded upperMultiplicity instanced by simpleSlot is not yet implemented</xsl:message>
                        </xsl:when>
                        <!-- new -->
                        <xsl:when test="fn:exists($objNode/aorsml:Slot[@property eq current()/@name]/@value)">
                          <xsl:message>unbounded upperMultiplicity instanced by simpleSlot is not yet implemented</xsl:message>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:value-of select="'null'"/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>

                      <xsl:apply-templates select="." mode="shared.helper.initAORObjects.ComplexDataProperty">
                        <xsl:with-param name="objNode" select="$objNode"/>
                      </xsl:apply-templates>

                      <xsl:choose>
                        <xsl:when test="fn:exists($objNode/aorsml:Slot[@property = current()/@name])">
                          <xsl:apply-templates select="$objNode/aorsml:Slot[@property = current()/@name]" mode="assistents.getSlotValue">
                            <xsl:with-param name="type" select="@type"/>
                          </xsl:apply-templates>
                        </xsl:when>
                        <xsl:when test="fn:exists($objNodeType/aorsml:InitialAttributeValue[@attribute eq current()/@name])">
                          <xsl:apply-templates select="$objNodeType/aorsml:InitialAttributeValue[@attribute eq current()/@name][1]"
                            mode="assistents.setInitialAttributeValue"/>
                        </xsl:when>
                        <xsl:when test="@initialValue">
                          <xsl:apply-templates select="@initialValue" mode="assistents.getValue">
                            <xsl:with-param name="type" select="@type"/>
                          </xsl:apply-templates>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:if test="not(aorsml:ClassDef[@language eq $output.language])">
                            <xsl:call-template name="java:setDefaultValue">
                              <xsl:with-param name="type" select="@type"/>
                            </xsl:call-template>
                          </xsl:if>
                        </xsl:otherwise>
                      </xsl:choose>

                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:otherwise>
              </xsl:choose>

            </xsl:for-each>

          </xsl:when>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="isVariable" select="true()"/>
    </xsl:call-template>

    <!-- set the instancevariables -->
    <!-- from attributes
      <xsl:choose>
      <xsl:when test="local-name() != 'PhysicalAgents' and local-name() != 'PhysicalObjects'"> -->
    <xsl:for-each
      select="@*[local-name() != 'id' and local-name() != 'type' and local-name() != 'variable' and 
      local-name() != 'addToCollection' and local-name() != 'hasRandomPosition'  and 
      local-name() != 'ignorePositionConstraint' and 
      local-name() != 'rangeStartID' and local-name() != 'rangeEndID' and 
      local-name() != 'creationLoopVar' and local-name() != 'objectVariable']">
      <xsl:apply-templates select="." mode="assistents.setPhysicalObjectProperty">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="physObjName" select="$varName"/>
        <xsl:with-param name="type">
          <xsl:if test="local-name() = 'name'">
            <xsl:value-of select="'String'"/>
          </xsl:if>
        </xsl:with-param>
      </xsl:apply-templates>
    </xsl:for-each>
    <!--     </xsl:when>
      </xsl:choose> -->

    <!-- for default physObjAttributes, which set with InitialAttributeValue  -->
    <xsl:apply-templates select="." mode="shared.helper.setInitialAttributes">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="varName" select="$varName" tunnel="yes"/>
    </xsl:apply-templates>

    <xsl:for-each select="aorsml:Slot">
      <xsl:variable name="slot" select="."/>
      <xsl:for-each select="$physObjAttrList">
        <xsl:if test=". = $slot/@property">
          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="objInstance" select="$varName"/>
            <xsl:with-param name="instVariable" select="."/>
            <xsl:with-param name="value">
              <xsl:apply-templates select="$slot" mode="assistents.getSlotValue">
                <xsl:with-param name="type">
                  <xsl:choose>
                    <xsl:when test=". eq 'materialType'">
                      <xsl:value-of select="$core.enum.materialType"/>
                    </xsl:when>
                    <xsl:when test=". eq 'shape2D'">
                      <xsl:value-of select="$core.enum.shape2D"/>
                    </xsl:when>
                    <xsl:when test=". eq 'shape3D'">
                      <xsl:value-of select="$core.enum.shape3D"/>
                    </xsl:when>
                  </xsl:choose>
                </xsl:with-param>
              </xsl:apply-templates>
            </xsl:with-param>
            <xsl:with-param name="valueType">
              <xsl:choose>
                <xsl:when test=". eq 'materialType'">
                  <xsl:value-of select="$core.enum.materialType"/>
                </xsl:when>
                <xsl:when test=". eq 'shape2D'">
                  <xsl:value-of select="$core.enum.shape2D"/>
                </xsl:when>
                <xsl:when test=". eq 'shape3D'">
                  <xsl:value-of select="$core.enum.shape3D"/>
                </xsl:when>
                <xsl:when test=". eq 'points'">
                  <xsl:value-of select="'String'"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="'double'"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:if>
      </xsl:for-each>
      <!-- for PhysicalAgents -->
      <xsl:if test="local-name(current()/..) = 'PhysicalAgents'">
        <xsl:for-each select="$physAgentObjAttrList">
          <xsl:if test=". = $slot/@property">
            <xsl:message select="."/>
            <xsl:call-template name="java:callSetterMethod">
              <xsl:with-param name="indent" select="$indent"/>
              <xsl:with-param name="objInstance" select="$varName"/>
              <xsl:with-param name="instVariable" select="."/>
              <xsl:with-param name="value">
                <xsl:apply-templates select="$slot" mode="assistents.getSlotValue"/>
              </xsl:with-param>
              <xsl:with-param name="valueType" select="'double'"/>
            </xsl:call-template>
          </xsl:if>
        </xsl:for-each>
      </xsl:if>
    </xsl:for-each>

    <!-- distributionhandling -->
    <xsl:call-template name="distributionHandling">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="discreteSpace" select="$discreteSpace"/>
      <xsl:with-param name="varName" select="$varName"/>
      <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
      <xsl:with-param name="spaceReservationSystem" select="$spaceReservationSystem"/>
      <xsl:with-param name="aorObject" select="."/>
    </xsl:call-template>

    <xsl:apply-templates select="@addToCollection" mode="shared.helper.initAORObjects.addToCollection">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="type" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
    </xsl:apply-templates>

  </xsl:template>

  <xsl:template match="aorsml:ComplexDataProperty" mode="shared.helper.initAORObjects.ComplexDataProperty">
    <xsl:param name="objNode" required="yes"/>

    <xsl:variable name="complexDataType" select="root($objNode)//aorsml:DataTypes/aorsml:ComplexDataType[@name eq current()/@type][1]"/>
    <xsl:choose>
      <xsl:when test="fn:exists($complexDataType)">

        <xsl:choose>
          <xsl:when test="fn:exists($complexDataType/aorsml:ClassDef[@language eq $output.language])">
            <xsl:call-template name="java:newObject">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="isVariable" select="true()"/>
              <xsl:with-param name="class" select="$complexDataType/@name"/>
            </xsl:call-template>
          </xsl:when>
        </xsl:choose>

      </xsl:when>
    </xsl:choose>

  </xsl:template>

  <!-- get the value that have to use for init -->
  <xsl:template match="aorsml:Object | aorsml:PhysicalObject | aorsml:PhysicalAgent" mode="shared.helper.initObj.initData">
    <xsl:param name="property" required="yes"/>
    <xsl:choose>
      <xsl:when test="fn:exists(aorsml:Slot[@property = current()/@name])">
        <xsl:apply-templates select="aorsml:Slot[@property = current()/@name]" mode="assistents.getSlotValue">
          <xsl:with-param name="type" select="@type"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:when test="fn:exists($property/parent::node()/aorsml:InitialAttributeValue[@attribute eq current()/@name])">
        <xsl:apply-templates select="$property/parent::node()/aorsml:InitialAttributeValue[@attribute eq current()/@name][1]"
          mode="assistents.setInitialAttributeValue"/>
      </xsl:when>
      <xsl:when test="$property/@initialValue">
        <xsl:apply-templates select="$property/@initialValue" mode="assistents.getValue">
          <xsl:with-param name="type" select="@type"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="java:setDefaultValue">
          <xsl:with-param name="type" select="@type"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- create a value for an constructor-argument if it is an enumtype -->
  <xsl:template name="setEnumInConstructor">
    <xsl:param name="objNode"/>
    <xsl:param name="enumeration"/>
    <xsl:param name="objTypeNode"/>
    <xsl:choose>
      <xsl:when test="fn:exists($objNode/aorsml:Slot[@property = current()/@name])">
        <xsl:apply-templates select="$objNode/aorsml:Slot[@property = current()/@name][1]" mode="assistents.getEnumSlotValue">
          <xsl:with-param name="enumeration" select="$enumeration"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:when test="fn:exists($objTypeNode/aorsml:InitialAttributeValue[@attribute eq current()/@name])">
        <xsl:apply-templates select="$objTypeNode/aorsml:InitialAttributeValue[@attribute eq current()/@name][1]"
          mode="assistents.setInitialEnumValue">
          <xsl:with-param name="enumeration" select="$enumeration"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:when test="@initialValue">
        <xsl:apply-templates select="@initialValue" mode="assistents.getEnumValue">
          <xsl:with-param name="enumeration" select="$enumeration"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="java:setDefaultValue">
          <xsl:with-param name="type" select="@type"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="distributionHandling">
    <xsl:param name="indent"/>
    <xsl:param name="discreteSpace"/>
    <xsl:param name="varName"/>
    <xsl:param name="envSimVarName"/>
    <xsl:param name="spaceReservationSystem"/>
    <xsl:param name="aorObject" as="node()" required="yes"/>
    
    <xsl:variable name="positionVarName" select="'positionData'"/>
    <xsl:if test="@hasRandomPosition = true()">

      <!-- positionData = Simulation.spaceModel.getSpace().getRandomPosition(); -->
      <xsl:call-template name="java:variable">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="name" select="$positionVarName"/>
        <xsl:with-param name="value">
          <xsl:call-template name="java:callMethod">
            <xsl:with-param name="inLine" select="true()"/>
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="objInstance">
              <xsl:call-template name="java:callGetterMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="objInstance">
                  <xsl:call-template name="java:varByDotNotation">
                    <xsl:with-param name="name" select="$sim.class.simulatorMain"/>
                    <xsl:with-param name="varName" select="'spaceModel'"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="instVariable" select="'space'"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="method" select="'getRandomPosition'"/>
            <xsl:with-param name="args" as="xs:string*">
              <xsl:if test="$discreteSpace">
                <xsl:choose>
                  <xsl:when test="fn:exists($aorObject/ancestor::aorsml:EnvironmentRule)">
                    <xsl:value-of select="'true'"/>
                  </xsl:when>
                  <xsl:when test="not(fn:exists($aorObject/@ignorePositionConstraint)) or $aorObject/@ignorePositionConstraint = false()">
                    <xsl:value-of select="'false'"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="'true'"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:if>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>

      <!-- if the position == null; then is not enought space for objekts -->
      <xsl:call-template name="java:if">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="condition">
          <xsl:call-template name="java:boolExpr">
            <xsl:with-param name="value1" select="$positionVarName"/>
            <xsl:with-param name="value2" select="'null'"/>
            <xsl:with-param name="operator" select="'!='"/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="thenContent">

          <!-- setX -->
          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="objInstance" select="$varName"/>
            <xsl:with-param name="instVariable" select="'x'"/>
            <xsl:with-param name="value">
              <xsl:call-template name="java:callGetterMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="objInstance" select="$positionVarName"/>
                <xsl:with-param name="instVariable" select="'x'"/>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>

          <!-- setY -->
          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="objInstance" select="$varName"/>
            <xsl:with-param name="instVariable" select="'y'"/>
            <xsl:with-param name="value">
              <xsl:call-template name="java:callGetterMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="objInstance" select="$positionVarName"/>
                <xsl:with-param name="instVariable" select="'y'"/>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>

          <!-- setZ -->
          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="objInstance" select="$varName"/>
            <xsl:with-param name="instVariable" select="'z'"/>
            <xsl:with-param name="value">
              <xsl:call-template name="java:callGetterMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="objInstance" select="$positionVarName"/>
                <xsl:with-param name="instVariable" select="'z'"/>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>

    <xsl:if test="$envSimVarName">
      <xsl:choose>
        <xsl:when
          test="$spaceReservationSystem and not(@ignorePositionConstraint = true()) and starts-with(local-name($aorObject), 'Physical')">

          <xsl:apply-templates select="." mode="shared.helper.initAORObjects.WithOccupanceCheck">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="positionVarName" select="$positionVarName"/>
            <xsl:with-param name="varName" select="$varName"/>
            <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
          </xsl:apply-templates>

        </xsl:when>
        <xsl:otherwise>

          <xsl:call-template name="addObject">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
            <xsl:with-param name="varName" select="$varName"/>
          </xsl:call-template>

        </xsl:otherwise>
      </xsl:choose>

    </xsl:if>
  </xsl:template>

  <!-- add to environment with occupancy-check -->
  <xsl:template match="aorsml:PhysicalAgent | aorsml:PhysicalAgents | aorsml:PhysicalObject | aorsml:PhysicalObjects | aorsml:Object"
    mode="shared.helper.initAORObjects.WithOccupanceCheck">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="positionVarName" required="yes" as="xs:string"/>
    <xsl:param name="varName" required="yes" as="xs:string"/>
    <xsl:param name="envSimVarName" required="yes" as="xs:string"/>
    <xsl:param name="spaceReservationSystem" as="xs:boolean" tunnel="yes"/>

    <xsl:call-template name="java:if">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="condition">
        <xsl:choose>
          <xsl:when test="fn:exists(@hasRandomPosition) and (@hasRandomPosition = true())">

            <xsl:call-template name="java:boolExpr">
              <xsl:with-param name="value1">
                <xsl:call-template name="java:boolExpr">
                  <xsl:with-param name="value1" select="$positionVarName"/>
                  <xsl:with-param name="value2" select="'null'"/>
                  <xsl:with-param name="operator" select="'!='"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="value2">
                <xsl:call-template name="updatePositionListWithPositionDataObject">
                  <xsl:with-param name="positionDataVarname" select="$positionVarName"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="operator" select="'&amp;&amp;'"/>
            </xsl:call-template>

          </xsl:when>
          <xsl:otherwise>

            <xsl:call-template name="updatePositionListWithPhysicalObject">
              <xsl:with-param name="physicalObjVarname" select="$varName"/>
            </xsl:call-template>

          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="thenContent">

        <xsl:call-template name="addObject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
          <xsl:with-param name="varName" select="$varName"/>
        </xsl:call-template>

      </xsl:with-param>
      <xsl:with-param name="elseContent">
        <xsl:if test="$spaceReservationSystem = true()">
          <xsl:choose>

            <xsl:when test="fn:exists(@hasRandomPosition) and (@hasRandomPosition = true())">
              <xsl:call-template name="java:systemPrintln">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="value" select="'Not enought place in the space!'"/>
              </xsl:call-template>

            </xsl:when>
            <xsl:otherwise>

              <xsl:call-template name="java:systemPrintln">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="value" select="'Not enought place or outside from space!'"/>
              </xsl:call-template>

            </xsl:otherwise>
          </xsl:choose>
        </xsl:if>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- with java-loop for sets -->
  <xsl:template match="aorsml:PhysicalAgents | aorsml:PhysicalObjects | aorsml:Agents" mode="shared.helper.initAORObjectsSet">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="className" required="yes"/>
    <xsl:param name="varName" required="yes"/>
    <xsl:param name="envSimVarName"/>
    <xsl:param name="spaceReservationSystem" required="yes" tunnel="yes"/>
    <xsl:param name="discreteSpace" as="xs:boolean" select="false()" tunnel="yes"/>
    <xsl:param name="id"/>

    <!-- create here a new objectvariable if necessary -->
    <xsl:if test="fn:exists(@objectVariable)">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$className"/>
        <xsl:with-param name="varName" select="@objectVariable"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:if>

    <xsl:variable name="creationLoopVar" select="if (fn:exists(@creationLoopVar)) then @creationLoopVar else 'i'"/>
    <xsl:call-template name="java:for-loop">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="loopVariable" select="$creationLoopVar"/>
      <xsl:with-param name="loopVarType" select="'long'"/>
      <xsl:with-param name="start">
        <xsl:apply-templates select="." mode="assistents.getStartID"/>
      </xsl:with-param>
      <xsl:with-param name="condition">
        <xsl:call-template name="java:boolExpr">
          <xsl:with-param name="value1" select="$creationLoopVar"/>
          <xsl:with-param name="value2">
            <xsl:apply-templates select="." mode="assistents.getEndID"/>
          </xsl:with-param>
          <xsl:with-param name="operator" select="'&lt;='"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="increment" select="1"/>
      <xsl:with-param name="content">

        <xsl:apply-templates select="." mode="shared.helper.initAORObjects">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="className" select="$className"/>
          <xsl:with-param name="varName" select="if (fn:exists(@objectVariable)) then @objectVariable else $varName"/>
          <xsl:with-param name="envSimVarName" select="$envSimVarName"/>
          <xsl:with-param name="id" select="$creationLoopVar"/>
        </xsl:apply-templates>

        <!-- independ if the x,y, and z values are set -->
        <xsl:if test="false() and @hasRandomPosition = true()">

          <!-- PositionData positionData = Simulation.spaceModel.getSpace().getRandomPosition(); -->
          <xsl:variable name="positionVarName" select="'positionData'"/>
          <xsl:call-template name="java:variable">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="name" select="$positionVarName"/>
            <xsl:with-param name="value">
              <xsl:call-template name="java:callMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="indent" select="$indent"/>
                <xsl:with-param name="objInstance">
                  <xsl:call-template name="java:callGetterMethod">
                    <xsl:with-param name="inLine" select="true()"/>
                    <xsl:with-param name="objInstance">
                      <xsl:call-template name="java:varByDotNotation">
                        <xsl:with-param name="name" select="$sim.class.simulatorMain"/>
                        <xsl:with-param name="varName" select="'spaceModel'"/>
                      </xsl:call-template>
                    </xsl:with-param>
                    <xsl:with-param name="instVariable" select="'space'"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="method" select="'getRandomPosition'"/>
                <xsl:with-param name="args" as="xs:string*">
                  <xsl:if test="$discreteSpace">
                    <xsl:choose>
                      <xsl:when test="not(fn:exists(@ignorePositionConstraint)) or @ignorePositionConstraint = false()">
                        <xsl:value-of select="'false'"/>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:value-of select="'true'"/>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:if>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>

          <!-- if the position == null; the is not enought space for objekts -->
          <xsl:call-template name="java:if">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="condition">
              <xsl:call-template name="java:boolExpr">
                <xsl:with-param name="value1" select="$positionVarName"/>
                <xsl:with-param name="value2" select="'null'"/>
                <xsl:with-param name="operator" select="'!='"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="thenContent">
              <!-- setX -->
              <xsl:call-template name="java:callSetterMethod">
                <xsl:with-param name="indent" select="$indent + 2"/>
                <xsl:with-param name="objInstance" select="$varName"/>
                <xsl:with-param name="instVariable" select="'x'"/>
                <xsl:with-param name="value">
                  <xsl:call-template name="java:callGetterMethod">
                    <xsl:with-param name="inLine" select="true()"/>
                    <xsl:with-param name="objInstance" select="$positionVarName"/>
                    <xsl:with-param name="instVariable" select="'x'"/>
                  </xsl:call-template>
                </xsl:with-param>
              </xsl:call-template>

              <!-- setY -->
              <xsl:call-template name="java:callSetterMethod">
                <xsl:with-param name="indent" select="$indent + 2"/>
                <xsl:with-param name="objInstance" select="$varName"/>
                <xsl:with-param name="instVariable" select="'y'"/>
                <xsl:with-param name="value">
                  <xsl:call-template name="java:callGetterMethod">
                    <xsl:with-param name="inLine" select="true()"/>
                    <xsl:with-param name="objInstance" select="$positionVarName"/>
                    <xsl:with-param name="instVariable" select="'y'"/>
                  </xsl:call-template>
                </xsl:with-param>
              </xsl:call-template>

              <!-- setZ -->
              <xsl:call-template name="java:callSetterMethod">
                <xsl:with-param name="indent" select="$indent + 2"/>
                <xsl:with-param name="objInstance" select="$varName"/>
                <xsl:with-param name="instVariable" select="'z'"/>
                <xsl:with-param name="value">
                  <xsl:call-template name="java:callGetterMethod">
                    <xsl:with-param name="inLine" select="true()"/>
                    <xsl:with-param name="objInstance" select="$positionVarName"/>
                    <xsl:with-param name="instVariable" select="'z'"/>
                  </xsl:call-template>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:if>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="@addToCollection" mode="shared.helper.initAORObjects.addToCollection">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="varName" as="xs:string" required="yes"/>
    <xsl:param name="type" as="xs:string" required="yes"/>

    <xsl:variable name="collectionNode" select="//aorsml:Collections/aorsml:Collection[@itemType = $type and @name = current()][1]"/>

    <xsl:choose>
      <xsl:when test="fn:exists($collectionNode)">

        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="objInstance">
            <xsl:value-of select="jw:createCollectionVarname($collectionNode)"/>
          </xsl:with-param>
          <xsl:with-param name="method" select="'addObjekt'"/>
          <xsl:with-param name="args" as="xs:string*">
            <xsl:value-of select="$varName"/>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:when>
      <xsl:otherwise>
        <xsl:message>
          <xsl:text>ERROR. No itemType [</xsl:text>
          <xsl:value-of select="$type"/>
          <xsl:text>] found for addToCollection in </xsl:text>
          <xsl:text> InitialState.</xsl:text>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!-- create an PhysicalAgentSubject with all selbeliefattributes in the constructor -->
  <xsl:template match="aorsml:PhysicalAgent | aorsml:PhysicalAgents | aorsml:Agent | aorsml:Agents" mode="shared.helper.initAgentSubject">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="className" required="yes"/>
    <xsl:param name="varName" required="yes"/>
    <xsl:param name="id"/>
    <xsl:param name="dynamic" as="xs:boolean" select="false()"/>

    <!-- create a new intance -->
    <xsl:call-template name="java:newObject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="class" select="$className"/>
      <xsl:with-param name="varName" select="$varName"/>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:when test="$id != ''">
            <xsl:value-of select="$id"/>
          </xsl:when>
        </xsl:choose>

        <xsl:choose>
          <xsl:when test="@name">
            <xsl:value-of select="jw:quote(@name)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="jw:quote('')"/>
          </xsl:otherwise>
        </xsl:choose>

        <xsl:variable name="objNode" select="current()" as="node()"/>

        <xsl:choose>
          <xsl:when test="local-name() = 'PhysicalAgent' or local-name() = 'PhysicalAgents'">

            <xsl:variable name="physAgentNode" as="node()" select="//aorsml:PhysicalAgentType[@name = current()/@type][1]"/>

            <!-- create the constructorlist for PhysicalAgents -->
            <!-- use all selfBeliefAttributes from PhysicalAgentType and SuperType -->
            <xsl:variable name="attributes" as="node()*">
              <xsl:apply-templates select="$physAgentNode" mode="assistents.list.allSelfBeliefAttributes"/>
            </xsl:variable>
            <xsl:for-each select="$attributes">
              <xsl:choose>
                <xsl:when test="fn:exists($objNode/aorsml:SelfBeliefSlot[@property = current()/@name])">
                  <xsl:apply-templates select="$objNode/aorsml:SelfBeliefSlot[@property = current()/@name]" mode="assistents.getSlotValue">
                    <xsl:with-param name="type" select="@type"/>
                  </xsl:apply-templates>
                </xsl:when>
                <xsl:when test="fn:exists($physAgentNode/aorsml:InitialAttributeValue[@attribute eq current()/@name])">
                  <xsl:apply-templates select="$physAgentNode/aorsml:InitialAttributeValue[@attribute eq current()/@name][1]"
                    mode="assistents.setInitialAttributeValue"/>
                </xsl:when>
                <xsl:when test="@initialValue">
                  <xsl:apply-templates select="@initialValue" mode="assistents.getValue">
                    <xsl:with-param name="type" select="@type"/>
                  </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:call-template name="java:setDefaultValue">
                    <xsl:with-param name="type" select="@type"/>
                  </xsl:call-template>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:for-each>

          </xsl:when>

          <xsl:when test="local-name() = 'Agent' or local-name() = 'Agents'">

            <!-- create the constructorlist for Agents -->
            <!-- use all selfBeliefAttributes from AgentType and SuperType -->
            <xsl:variable name="agentNode" as="node()" select="//aorsml:AgentType[@name = current()/@type][1]"/>

            <!-- create the constructorlist for PhysicalAgents -->
            <!-- use all attribute from PhysicalAgentType and SuperTypes-->
            <xsl:variable name="attributes" as="node()*">
              <xsl:apply-templates select="$agentNode" mode="assistents.list.allSelfBeliefAttributes"/>
            </xsl:variable>

            <xsl:for-each select="$attributes">
              <xsl:choose>
                <xsl:when test="fn:exists($objNode/aorsml:SelfBeliefSlot[@property = current()/@name])">
                  <xsl:apply-templates select="$objNode/aorsml:SelfBeliefSlot[@property = current()/@name]" mode="assistents.getSlotValue">
                    <xsl:with-param name="type" select="@type"/>
                  </xsl:apply-templates>
                </xsl:when>
                <xsl:when test="fn:exists($agentNode/aorsml:InitialAttributeValue[@attribute eq current()/@name])">
                  <xsl:apply-templates select="$agentNode/aorsml:InitialAttributeValue[@attribute eq current()/@name][1]"
                    mode="assistents.setInitialAttributeValue"/>
                </xsl:when>
                <xsl:when test="@initialValue">
                  <xsl:apply-templates select="@initialValue" mode="assistents.getValue">
                    <xsl:with-param name="type" select="@type"/>
                  </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:call-template name="java:setDefaultValue">
                    <xsl:with-param name="type" select="@type"/>
                  </xsl:call-template>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:for-each>

          </xsl:when>
        </xsl:choose>

      </xsl:with-param>
      <xsl:with-param name="isVariable" select="true()"/>
    </xsl:call-template>

    <!-- set the beliefEntities -->
    <xsl:apply-templates select="aorsml:BeliefEntity" mode="shared.helper.initAgentSubject.initBeliefEntitiy">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="varName" select="$varName"/>
      <xsl:with-param name="agendType" select="@type"/>
    </xsl:apply-templates>

    <!-- set the periodicTimeEvents -->
    <xsl:variable name="objNode" select="."/>
    <xsl:variable name="objPosition" select="position()"/>
    <xsl:for-each select="aorsml:PeriodicTimeEvent">
      <xsl:variable name="perTimEvtVarName" select="fn:concat(jw:lowerWord(@type), jw:upperWord(../@type), '_', $objPosition)"/>
      <xsl:call-template name="java:variable">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="name" select="$perTimEvtVarName"/>
        <xsl:with-param name="type" select="fn:concat($objNode/@type, $prefix.agentSubject, '.', current()/@type)"/>
        <xsl:with-param name="value">
          <xsl:call-template name="java:varByDotNotation">
            <xsl:with-param name="name" select="$varName"/>
            <xsl:with-param name="varName">
              <xsl:call-template name="java:newObject">
                <xsl:with-param name="class" select="@type"/>
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="isVariable" select="true()"/>
                <xsl:with-param name="args">
                  <xsl:if test="local-name($objNode/..) eq 'Create'">
                    <xsl:call-template name="java:callGetterMethod">
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="instVariable" select="'triggeredTime'"/>
                    </xsl:call-template>
                    <xsl:text> + </xsl:text>
                  </xsl:if>
                  <xsl:value-of select="@occurrenceTime"/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>

      <!-- add it to InternalEventList -->
      <xsl:call-template name="java:callMethod">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="objInstance" select="$varName"/>
        <xsl:with-param name="method" select="'addInternalEvent'"/>
        <xsl:with-param name="args" select="$perTimEvtVarName"/>
      </xsl:call-template>
    </xsl:for-each>

    <!-- set a OnEveryStepIntEvent -->
    <xsl:apply-templates select="//aorsml:PhysicalAgentType[@name = current()/@type][1]"
      mode="shared.helper.initAgentSubject.addON-EACH-SIMULATION-STEP">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="varName" select="$varName"/>
    </xsl:apply-templates>

    <xsl:choose>
      <xsl:when test="$dynamic"> </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="method" select="'addAgentSubject'"/>
          <xsl:with-param name="args" as="xs:string*" select="$varName"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:call-template name="java:newLine"/>

  </xsl:template>

  <!-- BeliefEntities -->
  <xsl:template match="aorsml:BeliefEntity" mode="shared.helper.initAgentSubject.initBeliefEntitiy">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="varName" as="xs:string" required="yes"/>
    <xsl:param name="agendType" as="xs:string" required="yes"/>

    <xsl:variable name="entityTypeNode" as="node()">
      <xsl:apply-templates select="//aorsml:EntityTypes/*[@name eq $agendType]"
        mode="shared.helper.initAgentSubject.initBeliefEntitiy.getBeliefEntityType">
        <xsl:with-param name="entityType" select="@type"/>
      </xsl:apply-templates>
    </xsl:variable>

    <xsl:if test="fn:exists($entityTypeNode)">

      <xsl:call-template name="java:callMethod">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="objInstance" select="$varName"/>
        <xsl:with-param name="method" select="'addBeliefEntity'"/>
        <xsl:with-param name="args">
          <xsl:call-template name="java:callMethod">
            <xsl:with-param name="inLine" select="true()"/>
            <xsl:with-param name="objInstance" select="$varName"/>
            <xsl:with-param name="method" select="fn:concat('create', $entityTypeNode/@name)"/>
            <xsl:with-param name="args" as="xs:string*">
              <!-- id -->
              <xsl:choose>
                <xsl:when test="@idRef">
                  <xsl:value-of select="@idRef"/>
                </xsl:when>
                <xsl:when test="fn:exists(aorsml:IdRef[@language eq $output.language])">
                  <xsl:value-of select="aorsml:IdRef[@language eq $output.language]"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:message>
                    <xsl:text>[ERROR] - No idRef for BeliefEntity </xsl:text>
                    <xsl:value-of select="@name"/>
                    <xsl:text>!</xsl:text>
                  </xsl:message>
                </xsl:otherwise>
              </xsl:choose>

              <!-- name -->
              <xsl:choose>
                <!-- depricated -->
                <xsl:when
                  test="fn:exists(aorsml:BeliefSlot[resolve-QName(@xsi:type, .) eq QName('http://aor-simulation.org', 'OpaqueExprSlot')]
                  [@property eq 'name' and fn:exists(aorsml:ValueExpr[@language eq $output.language])])">
                  <xsl:value-of
                    select="aorsml:BeliefSlot[@property eq 'name' and @xsi:type eq 'aors:OpaqueExprSlot']/aorsml:ValueExpr[@language eq $output.language]"
                  />
                </xsl:when>
                <!-- new -->
                <xsl:when test="fn:exists(aorsml:BeliefSlot[@property eq 'name' and fn:exists(aorsml:ValueExpr[@language eq $output.language])])">
                  <xsl:value-of select="aorsml:BeliefSlot/aorsml:ValueExpr[@language eq $output.language][1]"/>
                </xsl:when>
                <!-- depricated -->
                <xsl:when
                  test="fn:exists(aorsml:BeliefSlot[resolve-QName(@xsi:type, .) eq QName('http://aor-simulation.org', 'SimpleSlot')][@property eq 'name'])">
                  <xsl:value-of select="jw:quote(aorsml:BeliefSlot[@property eq 'name' and @xsi:type = 'aors:SimpleSlot'][1]/@value)"/>
                </xsl:when>
                <!-- new -->
                <xsl:when test="fn:exists(aorsml:BeliefSlot[@property eq 'name']/@value)">
                  <xsl:value-of select="jw:quote(aorsml:BeliefSlot[@property eq 'name'][1]/@value)"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="'&quot;&quot;'"/>
                </xsl:otherwise>
              </xsl:choose>

              <xsl:variable name="currentBeliefNode" as="node()" select="."/>

              <!-- create the constructorlist for BeliefEntities -->
              <xsl:for-each select="$entityTypeNode/aorsml:BeliefAttribute | $entityTypeNode/aorsml:BeliefReferenceProperty">
                <xsl:choose>
                  <xsl:when test="fn:exists($currentBeliefNode/aorsml:BeliefSlot[@property = current()/@name])">
                    <xsl:apply-templates select="$currentBeliefNode/aorsml:BeliefSlot[@property = current()/@name]" mode="assistents.getSlotValue">
                      <xsl:with-param name="type" select="@type"/>
                    </xsl:apply-templates>
                  </xsl:when>
                  <xsl:when test="@initialValue">
                    <xsl:apply-templates select="@initialValue" mode="assistents.getValue">
                      <xsl:with-param name="type" select="@type"/>
                    </xsl:apply-templates>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:call-template name="java:setDefaultValue">
                      <xsl:with-param name="type" select="@type"/>
                    </xsl:call-template>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:for-each>

            </xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>

    </xsl:if>

  </xsl:template>

  <xsl:template match="aorsml:AgentType | aorsml:PhysicalAgentType" mode="shared.helper.initAgentSubject.initBeliefEntitiy.getBeliefEntityType">
    <xsl:param name="entityType" as="xs:string" required="yes"/>

    <xsl:choose>
      <xsl:when test="fn:exists(aorsml:BeliefEntityType[@name eq $entityType])">
        <xsl:copy-of select="aorsml:BeliefEntityType[@name eq $entityType]"/>
      </xsl:when>
      <xsl:when test="fn:exists(@superType)">
        <xsl:apply-templates select="//aorsml:EntityTypes/aorsml:*[@name eq current()/@superType]"
          mode="shared.helper.initAgentSubject.initBeliefEntitiy.getBeliefEntityType">
          <xsl:with-param name="entityType" select="$entityType"/>
        </xsl:apply-templates>
      </xsl:when>
    </xsl:choose>

  </xsl:template>


  <!-- if one of the internal rules is triggered by an ON-EACH-SIMULATION-STEP, add this event to the internal eventlist-->
  <xsl:template match="aorsml:AgentType | aorsml:PhysicalAgentType" mode="shared.helper.initAgentSubject.addON-EACH-SIMULATION-STEP">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="varName" as="xs:string" required="yes"/>
    <xsl:param name="getTriggeredTimeForDynamicCreatedAgentsWithOnEachStepEvents" as="xs:string" select="''" tunnel="yes"/>

    <xsl:choose>
      <xsl:when test="fn:exists(aorsml:*/aorsml:ON-EACH-SIMULATION-STEP)">
        <!-- add it to InternalEventList -->
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="objInstance" select="$varName"/>
          <xsl:with-param name="method" select="'addInternalEvent'"/>
          <xsl:with-param name="args">
            <xsl:call-template name="java:newObject">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="isVariable" select="true()"/>
              <xsl:with-param name="class" select="$core.package.onEveryStepIntEvent"/>
              <xsl:with-param name="args" as="xs:string">
                <xsl:choose>
                  <xsl:when test="$getTriggeredTimeForDynamicCreatedAgentsWithOnEachStepEvents eq ''">
                    <xsl:value-of select="'1'"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="$getTriggeredTimeForDynamicCreatedAgentsWithOnEachStepEvents"/>
                  </xsl:otherwise>
                </xsl:choose>

              </xsl:with-param>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="fn:exists(@superType)">

        <xsl:apply-templates select="//aorsml:EntityTypes/aorsml:*[@name eq current()/@superType]"
          mode="shared.helper.initAgentSubject.addON-EACH-SIMULATION-STEP">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="varName" select="$varName"/>
        </xsl:apply-templates>

      </xsl:when>
    </xsl:choose>

  </xsl:template>

  <!-- create an PhysicalAgentSubject with all selbeliefattributes in the constructor -->
  <xsl:template match="aorsml:PhysicalAgents | aorsml:Agents" mode="shared.helper.initAgentSubjectSet">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="className" required="yes"/>
    <xsl:param name="varName" required="yes"/>
    <xsl:param name="id"/>

    <xsl:variable name="creationLoopVar" select="if (fn:exists(@creationLoopVar)) then @creationLoopVar else 'i'"/>
    <xsl:call-template name="java:for-loop">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="loopVariable" select="$creationLoopVar"/>
      <xsl:with-param name="loopVarType" select="'long'"/>
      <xsl:with-param name="start">
        <xsl:apply-templates select="." mode="assistents.getStartID"/>
      </xsl:with-param>
      <xsl:with-param name="condition">
        <xsl:call-template name="java:boolExpr">
          <xsl:with-param name="value1" select="$creationLoopVar"/>
          <xsl:with-param name="value2">
            <xsl:apply-templates select="." mode="assistents.getEndID"/>
          </xsl:with-param>
          <xsl:with-param name="operator" select="'&lt;='"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="increment" select="1"/>
      <xsl:with-param name="content">

        <xsl:apply-templates select="." mode="shared.helper.initAgentSubject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="className" select="$className"/>
          <xsl:with-param name="varName" select="$varName"/>
          <xsl:with-param name="id" select="$creationLoopVar"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- NOTICE: the methodparameter will be not mapped (to simple datatypes) -->
  <xsl:template match="aorsml:Function | aorsml:SubjectiveFunction | aorsml:GlobalFunction | aorsml:GridCellFunction" mode="shared.createFunction">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="static" select="false()"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="static" select="$static"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="@resultType"/>
      <xsl:with-param name="name" select="@name"/>
      <xsl:with-param name="parameterList" as="xs:string*">
        <xsl:for-each select="aorsml:Parameter">
          <xsl:call-template name="java:createParam">
            <xsl:with-param name="type" select="@type"/>
            <xsl:with-param name="name" select="@name"/>
            <xsl:with-param name="typeMapping" select="true()"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:with-param>
      <xsl:with-param name="content">
        <xsl:call-template name="java:indent">
          <xsl:with-param name="size" select="$indent + 1"/>
        </xsl:call-template>
        <xsl:choose>
          <xsl:when test="fn:exists(aorsml:Body[@language = $output.language])">
            <xsl:value-of select="aorsml:Body[@language = $output.language]"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message>No Body for created function available.</xsl:message>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:call-template name="java:newLine"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- set the description -->
  <xsl:template match="aorsml:documentation" mode="shared.setDocumentation">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <!-- TODO: implement this -->

  </xsl:template>

  <!-- create the method getMessageType() for rules -->
  <xsl:template match="aorsml:ReactionRule | aorsml:CommunicationRule | aorsml:ActualPerceptionRule"
    mode="shared.method.getMessageType">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'String'"/>
      <xsl:with-param name="name" select="'getMessageType'"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">
            <xsl:choose>
              <xsl:when test="fn:exists(aorsml:WHEN/@messageType)">
                <xsl:value-of select="jw:quote(aorsml:WHEN/@messageType)"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="jw:quote($empty.string.quotation.symbol)"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>
  
  <xsl:template match="aorsml:EnvironmentRule"
    mode="shared.method.getMessageType">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'String'"/>
      <xsl:with-param name="name" select="'getMessageType'"/>
      <xsl:with-param name="content">
        
        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">
            <xsl:choose>
              <xsl:when test="fn:exists(aorsml:WHEN/@messageType)">
                <xsl:value-of select="jw:quote(aorsml:WHEN/@messageType)"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="jw:quote($empty.string.quotation.symbol)"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
        </xsl:call-template>
        
      </xsl:with-param>
    </xsl:call-template>
    
  </xsl:template>

  <xsl:template match="aorsml:UpdateStatisticsVariable" mode="shared.helper.updateStatistics">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="name" select="$sim.class.simStatistics"/>
          <xsl:with-param name="varName" select="@variable"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="instVariable" select="'value'"/>
      <xsl:with-param name="value" select="fn:normalize-space(aorsml:ValueExpr[@language = $output.language])"/>
    </xsl:call-template>

  </xsl:template>

  <!-- set here the initialValues from aorsml:InitialValue there be a default value for objects/agents -->
  <xsl:template
    match="aorsml:PhysicalAgent | aorsml:PhysicalObject | aorsml:PhysicalAgents | aorsml:PhysicalObjects | aorsml:Object | aorsml:Agent | aorsml:Agents"
    mode="shared.helper.setInitialAttributes">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:variable name="contextNode" select="."/>
    <xsl:variable name="typeNode">
      <xsl:choose>
        <xsl:when test="fn:exists(//aorsml:PhysicalObjectType[@name = current()/@type])">
          <xsl:copy-of select="//aorsml:PhysicalObjectType[@name = current()/@type]"/>
        </xsl:when>
        <xsl:when test="fn:exists(//aorsml:PhysicalAgentType[@name = current()/@type])">
          <xsl:copy-of select="//aorsml:PhysicalAgentType[@name = current()/@type]"/>
        </xsl:when>
        <!-- without effect, only for the check of existenz -->
        <xsl:when test="fn:exists(//aorsml:AgentType[@name = current()/@type])">
          <xsl:copy-of select="//aorsml:AgentType[@name = current()/@type]"/>
        </xsl:when>
        <xsl:when test="fn:exists(//aorsml:ObjectType[@name = current()/@type])">
          <xsl:copy-of select="//aorsml:ObjectType[@name = current()/@type]"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:message>
            <xsl:text>ERROR: No assiciated EntityType found for </xsl:text>
            <xsl:value-of select="local-name()"/>
            <xsl:text> type: </xsl:text>
            <xsl:value-of select="@type"/>
            <xsl:text>!</xsl:text>
          </xsl:message>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:if test="fn:exists($typeNode)">
      <xsl:if test="local-name() = 'PhysicalAgent' or local-name() = 'PhysicalAgents'">
        <xsl:for-each select="$physAgentObjAttrList">
          <xsl:if test="not ($contextNode/@*[name() = current()]) and not (fn:exists($contextNode/aorsml:Slot[@property = .]))">
            <xsl:apply-templates select="$typeNode" mode="assistents.setInitialAttributeValue">
              <xsl:with-param name="indent" select="$indent"/>
              <xsl:with-param name="attrName" select="."/>
            </xsl:apply-templates>
          </xsl:if>
        </xsl:for-each>
      </xsl:if>

      <xsl:if
        test="local-name() = 'PhysicalAgent' or local-name() = 'PhysicalAgents' or
        local-name() = 'PhysicalObject' or local-name() = 'PhysicalObjects'">
        <xsl:for-each select="$physObjAttrList">
          <xsl:if test="not ($contextNode/@*[name() = current()]) and not (fn:exists($contextNode/aorsml:Slot[@property = .]))">
            <xsl:apply-templates select="$typeNode" mode="assistents.setInitialAttributeValue">
              <xsl:with-param name="indent" select="$indent"/>
              <xsl:with-param name="attrName" select="."/>
            </xsl:apply-templates>
          </xsl:if>
        </xsl:for-each>
      </xsl:if>

    </xsl:if>

  </xsl:template>

  <xsl:template match="aorsml:BeliefEntityType" mode="shared.methods.ceateBeliefTypes">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <!-- create belief method with parameter only the Id of the belief -->
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="@name"/>
      <xsl:with-param name="name" select="fn:concat('create', @name)"/>
      <xsl:with-param name="parameterList" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'long'"/>
          <xsl:with-param name="name" select="'id'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">
        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">
            <xsl:call-template name="java:newObject">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="class" select="@name"/>
              <xsl:with-param name="onlyInitialization" select="true()"/>
              <xsl:with-param name="args" as="xs:string" select="'id'"> </xsl:with-param>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

    <!-- create belief method with parameters all belief properties -->
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="@name"/>
      <xsl:with-param name="name" select="fn:concat('create', @name)"/>
      <xsl:with-param name="parameterList" as="xs:string*">
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
        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">
            <xsl:call-template name="java:newObject">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="class" select="@name"/>
              <xsl:with-param name="onlyInitialization" select="true()"/>
              <xsl:with-param name="args" as="xs:string*" select="('id', 'name', aorsml:BeliefAttribute/@name | aorsml:BeliefReferenceProperty/@name)"
              > </xsl:with-param>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="updatePositionListWithPositionDataObject">
    <xsl:param name="positionDataVarname" as="xs:string" required="yes"/>

    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="inLine" select="true()"/>
      <xsl:with-param name="objInstance">
        <xsl:call-template name="java:callGetterMethod">
          <xsl:with-param name="inLine" select="true()"/>
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="$sim.class.simulatorMain"/>
              <xsl:with-param name="varName" select="'spaceModel'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="instVariable" select="'space'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="method" select="'updatePositionList'"/>
      <xsl:with-param name="args" as="xs:string*" select="$positionDataVarname"/>
    </xsl:call-template>

  </xsl:template>

  <xsl:template name="updatePositionListWithPhysicalObject">
    <xsl:param name="physicalObjVarname"/>

    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="inLine" select="true()"/>
      <xsl:with-param name="objInstance">
        <xsl:call-template name="java:callGetterMethod">
          <xsl:with-param name="inLine" select="true()"/>
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="$sim.class.simulatorMain"/>
              <xsl:with-param name="varName" select="'spaceModel'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="instVariable" select="'space'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="method" select="'updatePositionList'"/>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:call-template name="java:callGetterMethod">
          <xsl:with-param name="inLine" select="true()"/>
          <xsl:with-param name="objInstance" select="$physicalObjVarname"/>
          <xsl:with-param name="instVariable" select="'x'"/>
        </xsl:call-template>
        <xsl:call-template name="java:callGetterMethod">
          <xsl:with-param name="inLine" select="true()"/>
          <xsl:with-param name="objInstance" select="$physicalObjVarname"/>
          <xsl:with-param name="instVariable" select="'y'"/>
        </xsl:call-template>
        <xsl:call-template name="java:callGetterMethod">
          <xsl:with-param name="inLine" select="true()"/>
          <xsl:with-param name="objInstance" select="$physicalObjVarname"/>
          <xsl:with-param name="instVariable" select="'z'"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="addObject">
    <xsl:param name="indent"/>
    <xsl:param name="envSimVarName"/>
    <xsl:param name="varName"/>
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="varName" select="$envSimVarName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="method">
        <xsl:choose>
          <xsl:when test="local-name() = 'PhysicalAgent' or local-name() = 'PhysicalAgents'">
            <xsl:value-of select="'addPhysicalAgent'"/>
          </xsl:when>
          <xsl:when test="local-name() = 'Agent' or local-name() = 'Agents'">
            <xsl:value-of select="'addAgent'"/>
          </xsl:when>
          <xsl:when test="local-name() = 'Object'">
            <xsl:value-of select="'addObjekt'"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'addPhysicalObjekt'"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="args" as="xs:string*" select="$varName"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="setDefaultJavaImports">
    <xsl:for-each select="$defaultImports">
      <xsl:value-of select="."/>
    </xsl:for-each>
  </xsl:template>

  <!-- ***************************************** -->
  <!--      templates for global variables          -->
  <!-- ***************************************** -->
  <xsl:template match="aorsml:IncrementGlobalVariable" mode="shared.incrementGlobalVariable">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance" select="$sim.class.simGlobal"/>
      <xsl:with-param name="instVariable" select="@name"/>
      <xsl:with-param name="value">
        <xsl:call-template name="java:callGetterMethod">
          <xsl:with-param name="objInstance" select="$sim.class.simGlobal"/>
          <xsl:with-param name="instVariable" select="@name"/>
          <xsl:with-param name="inLine" select="true()"/>
        </xsl:call-template>
        <xsl:value-of select="fn:concat(' + ', @value)"/>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsml:UpdateGlobalVariable | aorsml:GlobalVariable" mode="shared.updateGlobalVariable">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:if test="@value or fn:exists(aorsml:ValueExpr[@language eq $output.language])">
      <xsl:call-template name="java:callSetterMethod">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="objInstance" select="$sim.class.simGlobal"/>
        <xsl:with-param name="instVariable" select="@name"/>
        <xsl:with-param name="value">
          <xsl:choose>
            <xsl:when test="fn:exists(aorsml:ValueExpr[@language eq $output.language])">
              <xsl:value-of select="aorsml:ValueExpr[@language eq $output.language][1]"/>
            </xsl:when>
            <xsl:when test="@value">
              <xsl:choose>
                <xsl:when test="//aorsml:Globals/aorsml:GlobalVariable[@name eq current()/@name]/@dataType eq 'String'">
                  <xsl:value-of select="jw:quote(@value)"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="@value"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:when>
          </xsl:choose>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>

  </xsl:template>


  <xsl:template match="aorsml:FOR-ListItemVariable" mode="shared.FOR-ListItemVariable.classVariable">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'private'"/>
      <xsl:with-param name="type" select="@listItemType"/>
      <xsl:with-param name="name" select="@variable"/>
    </xsl:call-template>

  </xsl:template>

</xsl:stylesheet>
