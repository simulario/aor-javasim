<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation creates classes for agent rules based on a given aorsml file.

      $Rev$
      $Date$

      @author:   Jens Werner (jens.werner@tu-cottbus.de)
      @license:  GNU General Public License version 2 or higher
      @last changed by $Author$
-->

<xsl:stylesheet version="2.0" xmlns:aorsl="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsi:schemaLocation="http://aor-simulation.org aorsml.xsd"
  xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/" xmlns:md="http://www.informatik.tu-cottbus.de/~diacones/">

  <!--creates class-->
  <xsl:template match="aorsl:ReactionRule" mode="createAgentRules.createAgentRule">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="agentType" as="node()" required="yes"/>
    <xsl:param name="isPIAgent" as="xs:boolean" select="false()"/>

    <xsl:call-template name="checkAttributes"/>

    <xsl:call-template name="java:class">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="@name"/>
      <xsl:with-param name="extends" select="if (fn:exists(@superType)) then @superType else $core.class.reactionRule"/>
      <xsl:with-param name="content">

        <!-- variables (are optional in schema) -->
        <xsl:variable name="eventVariable">
          <xsl:choose>
            <xsl:when test="exists(aorsl:WHEN)">
              <xsl:value-of select="if (exists(aorsl:WHEN/@eventVariable)) then aorsl:WHEN/@eventVariable else jw:lowerWord(aorsl:WHEN/@eventType)"
              />
            </xsl:when>
            <xsl:when test="exists(aorsl:ON-EACH-SIMULATION-STEP)">
              <xsl:value-of select="concat($createdVariablesNamePrefix, jw:lowerWord($core.class.onEveryStepIntEvent))"/>
            </xsl:when>
          </xsl:choose>
        </xsl:variable>

        <xsl:variable name="agentTypeClassName" select="fn:concat($agentType/@name, $prefix.agentSubject)"/>
        <xsl:variable name="agtVarName" select="if (@agentVariable) then @agentVariable else fn:concat($createdVariablesNamePrefix, 'agt')"/>

        <!-- private classvariables -->
        <xsl:apply-templates select="." mode="createAgentRules.classVariables">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="eventVariable" select="$eventVariable"/>
          <xsl:with-param name="agentTypeClassName" select="$agentTypeClassName"/>
          <xsl:with-param name="agtVarName" select="$agtVarName"/>
        </xsl:apply-templates>

        <!--constructor -->
        <xsl:apply-templates select="." mode="createAgentRules.constructor">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVarName" select="$eventVariable"/>
          <xsl:with-param name="agentClassName" select="$agentTypeClassName"/>
          <xsl:with-param name="agentVarName" select="$agtVarName"/>
        </xsl:apply-templates>

        <!-- condition() -->
        <xsl:apply-templates select="." mode="createAgentRules.method.conditions">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <!-- resultingInternalEvents() -->
        <xsl:apply-templates select="." mode="createAgentRules.method.resultingInternalEvents">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVariable"/>
          <xsl:with-param name="mode" select="aorsl:DO"/>
          <xsl:with-param name="prefix" select="'do'"/>
        </xsl:apply-templates>
        <xsl:apply-templates select="." mode="createAgentRules.method.resultingInternalEvents">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVariable"/>
          <xsl:with-param name="mode" select="aorsl:THEN"/>
          <xsl:with-param name="prefix" select="'then'"/>
        </xsl:apply-templates>
        <xsl:apply-templates select="." mode="createAgentRules.method.resultingInternalEvents">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVariable"/>
          <xsl:with-param name="mode" select="aorsl:ELSE"/>
          <xsl:with-param name="prefix" select="'else'"/>
        </xsl:apply-templates>

        <!-- stateEffects() -->
        <xsl:apply-templates select="." mode="createAgentRules.method.stateEffects">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="agentClassName" select="$agentTypeClassName"/>
          <xsl:with-param name="agentVariable" select="$agtVarName" tunnel="yes"/>
          <xsl:with-param name="mode" select="aorsl:DO"/>
          <xsl:with-param name="prefix" select="'do'"/>
          <xsl:with-param name="isPIAgent" select="$isPIAgent"/>
        </xsl:apply-templates>
        <xsl:apply-templates select="." mode="createAgentRules.method.stateEffects">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="agentClassName" select="$agentTypeClassName"/>
          <xsl:with-param name="agentVariable" select="$agtVarName" tunnel="yes"/>
          <xsl:with-param name="mode" select="aorsl:THEN"/>
          <xsl:with-param name="prefix" select="'then'"/>
          <xsl:with-param name="isPIAgent" select="$isPIAgent"/>
        </xsl:apply-templates>
        <xsl:apply-templates select="." mode="createAgentRules.method.stateEffects">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="agentClassName" select="$agentTypeClassName"/>
          <xsl:with-param name="agentVariable" select="$agtVarName" tunnel="yes"/>
          <xsl:with-param name="mode" select="aorsl:ELSE"/>
          <xsl:with-param name="prefix" select="'else'"/>
          <xsl:with-param name="isPIAgent" select="$isPIAgent"/>
        </xsl:apply-templates>

        <!-- resultingActionEvents() -->
        <xsl:apply-templates select="." mode="createAgentRules.method.resultingActionEvents">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVariable"/>
          <xsl:with-param name="agentVariable" select="$agtVarName"/>
          <xsl:with-param name="agentType" select="local-name($agentType)"/>
          <xsl:with-param name="mode" select="aorsl:DO"/>
          <xsl:with-param name="prefix" select="'do'"/>
        </xsl:apply-templates>
        <xsl:apply-templates select="." mode="createAgentRules.method.resultingActionEvents">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVariable"/>
          <xsl:with-param name="agentVariable" select="$agtVarName"/>
          <xsl:with-param name="agentType" select="local-name($agentType)"/>
          <xsl:with-param name="mode" select="aorsl:THEN"/>
          <xsl:with-param name="prefix" select="'then'"/>
        </xsl:apply-templates>
        <xsl:apply-templates select="." mode="createAgentRules.method.resultingActionEvents">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVariable"/>
          <xsl:with-param name="agentVariable" select="$agtVarName"/>
          <xsl:with-param name="agentType" select="local-name($agentType)"/>
          <xsl:with-param name="mode" select="aorsl:ELSE"/>
          <xsl:with-param name="prefix" select="'else'"/>
        </xsl:apply-templates>

        <!-- create setter and getter for triggering event -->
        <xsl:apply-templates select="." mode="createAgentRules.method.setGetTriggeringEvent">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVariable"/>
        </xsl:apply-templates>

        <!-- execute() -->
        <xsl:apply-templates select="." mode="createAgentRules.method.execute">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVariable"/>
          <xsl:with-param name="agentClassName" select="$agentTypeClassName"/>
        </xsl:apply-templates>


        <!-- getMessageType() -->
        <xsl:apply-templates select="." mode="shared.method.getMessageType">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- create class variables -->
  <xsl:template match="aorsl:ReactionRule | aorsl:CommunicationRule" mode="createAgentRules.classVariables">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="eventVariable" as="xs:string" required="yes"/>
    <xsl:param name="agentTypeClassName" as="xs:string" required="yes"/>
    <xsl:param name="agtVarName" as="xs:string" required="yes"/>

    <!-- variables as private classvariables -->
    <xsl:apply-templates select="aorsl:FOR[@beliefEntityVariable]" mode="createAgentRules.beliefEntities.classVariables">
      <xsl:with-param name="indent" select="$indent + 1"/>
    </xsl:apply-templates>

    <!-- set the DataVariableDeclaration as classvaraibles -->
    <xsl:apply-templates select="aorsl:FOR[@dataVariable]" mode="assistents.setDataVariableDeclarationClassVariables">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates>

    <xsl:apply-templates select="aorsl:*/aorsl:SCHEDULE-EVT/aorsl:CreateDescription" mode="createAgentRules.description.classVariables">
      <xsl:with-param name="indent" select="$indent + 1"/>
    </xsl:apply-templates>

    <xsl:apply-templates select="aorsl:FOR-ListItemVariable" mode="shared.FOR-ListItemVariable.classVariable">
      <xsl:with-param name="indent" select="$indent + 1"/>
    </xsl:apply-templates>

    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="modifier" select="'private'"/>
      <xsl:with-param name="type">
        <xsl:choose>
          <xsl:when test="exists(aorsl:WHEN)">
            <xsl:value-of select="aorsl:WHEN/@eventType"/>
          </xsl:when>
          <xsl:when test="exists(aorsl:ON-EACH-SIMULATION-STEP)">
            <xsl:value-of select="$core.package.onEveryStepIntEvent"/>
          </xsl:when>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="name" select="$eventVariable"/>
    </xsl:call-template>

    <!-- agent -->
    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="modifier" select="'private'"/>
      <xsl:with-param name="type" select="$agentTypeClassName"/>
      <xsl:with-param name="name" select="$agtVarName"/>
    </xsl:call-template>
    <xsl:call-template name="java:newLine"/>

    <!-- @messageVariable -->
    <xsl:if test="fn:exists(aorsl:WHEN/@messageVariable) and fn:exists(aorsl:WHEN/@messageType)">
      <xsl:call-template name="java:variable">
        <xsl:with-param name="indent" select="$indent + 1"/>
        <xsl:with-param name="modifier" select="'private'"/>
        <xsl:with-param name="type" select="aorsl:WHEN/@messageType"/>
        <xsl:with-param name="name" select="aorsl:WHEN/@messageVariable"/>
      </xsl:call-template>
      <xsl:call-template name="java:newLine"/>
    </xsl:if>

  </xsl:template>

  <!--create constructor-->
  <xsl:template match="aorsl:ReactionRule" mode="createAgentRules.constructor">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="eventVarName" required="yes" as="xs:string"/>
    <xsl:param name="agentClassName" required="yes" as="xs:string"/>
    <xsl:param name="agentVarName" required="yes" as="xs:string"/>

    <!-- this two variablenames are only used localy -->
    <xsl:variable name="agentSubjVarName" select="jw:lowerWord($core.class.agentSubject)"/>
    <xsl:variable name="agentSubjNameVarName" select="'name'"/>
    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="@name"/>
      <xsl:with-param name="parameters" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'String'"/>
          <xsl:with-param name="name" select="$agentSubjNameVarName"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="$core.class.agentSubject"/>
          <xsl:with-param name="name" select="$agentSubjVarName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string*">
            <xsl:value-of select="$agentSubjNameVarName"/>
            <xsl:value-of select="$agentSubjVarName"/>
          </xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="java:newLine"/>

        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="castType" select="$agentClassName"/>
          <xsl:with-param name="name">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="varName" select="$agentVarName"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="value">
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="instVariable" select="'AgentSubject'"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:apply-templates select="aorsl:documentation" mode="shared.setDocumentation">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!--**************-->
  <!--   methods    -->
  <!--**************-->
  <!-- conditions() -->
  <xsl:template match="aorsl:ReactionRule" mode="createAgentRules.method.conditions">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="annotation" select="'@SuppressWarnings(&quot;unchecked&quot;)'"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="type" select="'boolean'"/>
      <xsl:with-param name="name" select="'condition'"/>
      <xsl:with-param name="content">

        <!-- The IF condition part -->
        <xsl:choose>
          <xsl:when test="fn:exists(aorsl:IF[@language = $output.language]) and fn:normalize-space(aorsl:IF[@language = $output.language]) != ''">
            <xsl:call-template name="java:tryCatch">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="exceptionType" select="'Exception'"/>
              <xsl:with-param name="exceptionVariable" select="'e'"/>
              <xsl:with-param name="tryContent">

                <xsl:call-template name="java:return">
                  <xsl:with-param name="indent" select="$indent + 2"/>
                  <xsl:with-param name="value" select="fn:normalize-space(aorsl:IF[@language = $output.language][1])"/>
                </xsl:call-template>

              </xsl:with-param>
              <xsl:with-param name="catchContent">
                <xsl:call-template name="java:return">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="value" select="'false'"/>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="value" select="'true'"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- stateEffects() -->
  <xsl:template match="aorsl:ReactionRule" mode="createAgentRules.method.stateEffects">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="agentClassName" required="yes" as="xs:string"/>
    <xsl:param name="agentVariable" tunnel="yes"/>
    <xsl:param name="isPIAgent" as="xs:boolean" select="false()"/>
    <xsl:param name="mode" as="element()?"/>
    <xsl:param name="prefix" as="xs:string" required="yes"/>

    <xsl:variable name="methodName" select="fn:concat($prefix, jw:upperWord('stateEffects'))"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="$methodName"/>
      <xsl:with-param name="content">

        <xsl:if test="fn:exists($mode)">

          <xsl:apply-templates select="$mode/aorsl:SCHEDULE-EVT/aorsl:CreateDescription"
            mode="createAgentRules.method.stateEffects.createDescription">
            <xsl:with-param name="indent" select="$indent"/>
          </xsl:apply-templates>

          <xsl:variable name="agentVarName">
            <xsl:choose>
              <xsl:when test="$agentVariable">
                <xsl:value-of select="$agentVariable"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="jw:lowerWord($agentClassName)"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>

          <!-- TODO: check if this necessary, maybe we should set a variable for the agentsubject ever -->
          <xsl:comment>if we have defined an @agentVariable then we can use the created classVariable (with the assoziated AgentSubjectClass) from
            AgentRule, otherwise we use the AgentSubjectClass localy </xsl:comment>
          <xsl:if test="(fn:exists($mode/aorsl:UPDATE-AGT/aorsl:Slot) or fn:exists($mode/aorsl:UPDATE-AGT/aorsl:Call)) and not ($agentVariable)">
            <xsl:call-template name="java:newObject">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="class" select="$agentClassName"/>
              <xsl:with-param name="varName" select="$agentVarName"/>
              <xsl:with-param name="withDeclaration" select="false()"/>
            </xsl:call-template>
            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="castType" select="$agentClassName"/>
              <xsl:with-param name="name" select="$agentVarName"/>
              <xsl:with-param name="value">
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="instVariable" select="'AgentSubject'"/>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:if>

          <xsl:apply-templates select="$mode/aorsl:UPDATE-AGT/aorsl:UpdateComplexDataPropertyValue"
            mode="createAgentRules.method.stateEffects.updateComplexDataPropertyValue">
            <xsl:with-param name="indent" select="$indent + 1"/>
          </xsl:apply-templates>
          
          <xsl:apply-templates select="$mode/aorsl:UPDATE-AGT/aorsl:Call" mode="createAgentRules.method.stateEffects.call">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="agtVarName" select="$agentVarName"/>
          </xsl:apply-templates>

          <!--sets state effects-->
          <xsl:for-each select="$mode/aorsl:UPDATE-AGT/aorsl:SelfBeliefSlot">

            <xsl:call-template name="java:callSetterMethod">
              <xsl:with-param name="indent" select="$indent +  1"/>
              <xsl:with-param name="objInstance">
                <xsl:choose>
                  <xsl:when test="$agentVariable">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="$agentVarName"/>
                    </xsl:call-template>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="$agentVarName"/>
                  </xsl:otherwise>
                </xsl:choose>

              </xsl:with-param>
              <xsl:with-param name="instVariable" select="@property"/>
              <xsl:with-param name="value">
                <xsl:apply-templates select="." mode="assistents.getSlotValue"/>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:for-each>

          <!--sets state effects (PI-Agents)-->
          <xsl:for-each select="$mode/aorsl:UPDATE-AGT/aorsl:Slot">

            <xsl:call-template name="java:callSetterMethod">
              <xsl:with-param name="indent" select="$indent +  1"/>
              <xsl:with-param name="objInstance">
                <xsl:choose>
                  <xsl:when test="$agentVariable">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="$agentVarName"/>
                    </xsl:call-template>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="$agentVarName"/>
                  </xsl:otherwise>
                </xsl:choose>

              </xsl:with-param>
              <xsl:with-param name="instVariable" select="@property"/>
              <xsl:with-param name="value">
                <xsl:apply-templates select="." mode="assistents.getSlotValue"/>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:for-each>

          <!-- update belief entities in agent rules -->
          <xsl:for-each select="$mode/aorsl:UPDATE-AGT/aorsl:UpdateBeliefEntity">
            <xsl:variable name="tmpObjInstance" select="fn:concat('((',$agentClassName,')this.getAgentSubject())')"/>
            <xsl:variable name="entityIdRefVar">
              <xsl:choose>
                <xsl:when
                  test="fn:exists(aorsl:BeliefEntityIdRef[@language = $output.language]) 
                    and fn:normalize-space(aorsl:BeliefEntityIdRef[@language = $output.language]) != ''">
                  <xsl:value-of select="aorsl:BeliefEntityIdRef[@language = $output.language]"/>
                </xsl:when>
                <xsl:when test="fn:exists(@beliefEntityIdRef) and @beliefEntityIdRef!=''">
                  <xsl:value-of select="@beliefEntityIdRef"/>
                </xsl:when>
                <xsl:when test="fn:exists(@beliefEntityVariable) and @beliefEntityVariable!=''">
                  <xsl:value-of select="fn:concat('this.', @beliefEntityVariable,'.getId()')"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:message>
                    <xsl:text>[ERROR] in variable UpdateBeliefEntity in AgentRule [</xsl:text>
                    <xsl:value-of select="../../@name"/>
                    <xsl:text>! No IdRef or variable was defined for this belief entity</xsl:text>
                    <xsl:text>]!</xsl:text>
                  </xsl:message>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <!-- update belief entities in agent rules - aorsl:BeliefSlot elements-->
            <xsl:for-each select="aorsl:BeliefSlot">
              <xsl:call-template name="java:callMethod">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="method" select="'updateBeliefEntityProperty'"/>
                <xsl:with-param name="objInstance" select="$tmpObjInstance"/>
                <xsl:with-param name="args" as="xs:string*">
                  <xsl:value-of select="$entityIdRefVar"/>
                  <xsl:value-of select="jw:quote(@property)"/>
                  <xsl:value-of>
                    <xsl:choose>
                      <xsl:when
                        test="fn:exists(aorsl:ValueExpr[@language = $output.language]) 
                                        and fn:normalize-space(aorsl:ValueExpr[@language = $output.language]) != ''">
                        <xsl:value-of select="aorsl:ValueExpr[@language = $output.language]"/>
                      </xsl:when>
                      <xsl:when test="fn:exists(@value) and @value!=''">
                        <xsl:value-of select="@value"/>
                      </xsl:when>
                    </xsl:choose>
                  </xsl:value-of>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:for-each>

            <!-- update belief entities in agent rules - aorsl:Increment elements-->
            <xsl:for-each select="aorsl:Increment">
              <xsl:call-template name="java:callMethod">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="method" select="'incrementBeliefEntityPropVal'"/>
                <xsl:with-param name="objInstance" select="$tmpObjInstance"/>
                <xsl:with-param name="args" as="xs:string*">
                  <xsl:value-of select="$entityIdRefVar"/>
                  <xsl:value-of select="jw:quote(@property)"/>
                  <xsl:value-of select="@value"/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:for-each>

            <!-- update belief entities in agent rules - aorsl:Decrement elements-->
            <xsl:for-each select="aorsl:Decrement">
              <xsl:call-template name="java:callMethod">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="method" select="'decrementBeliefEntityPropVal'"/>
                <xsl:with-param name="objInstance" select="$tmpObjInstance"/>
                <xsl:with-param name="args" as="xs:string*">
                  <xsl:value-of select="$entityIdRefVar"/>
                  <xsl:value-of select="jw:quote(@property)"/>
                  <xsl:value-of select="@value"/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:for-each>
          </xsl:for-each>

          <!-- create belief entities in agent rules -->
          <xsl:for-each select="$mode/aorsl:UPDATE-AGT/aorsl:CreateBeliefEntity">
            <!-- create the belief -->
            <xsl:variable name="beliefTmpVar" select="fn:concat('tmpBeliefEntity','_', position())"/>
            <xsl:variable name="subjectVar" select="fn:concat('((',$agentClassName,')this.getAgentSubject())')"/>
            <xsl:variable name="beliefIdVar">
              <xsl:choose>
                <xsl:when test="fn:exists(@beliefEntityId) and @beliefEntityId!=''">
                  <xsl:value-of select="@beliefEntityId"/>
                </xsl:when>
                <xsl:when
                  test="fn:exists(aorsl:BeliefEntityIdRef[@language = $output.language]) 
                    and fn:normalize-space(aorsl:BeliefEntityIdRef[@language = $output.language])!=''">
                  <xsl:value-of select="aorsl:BeliefEntityIdRef[@language = $output.language]"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="'generateUniqueBeliefId()'"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>
            <xsl:variable name="beliefTypeVar">
              <xsl:choose>
                <xsl:when test="fn:exists(@beliefEntityType)">
                  <xsl:call-template name="java:callMethod">
                    <xsl:with-param name="method" select="fn:concat('create',@beliefEntityType)"/>
                    <xsl:with-param name="inLine" select="true()"/>
                    <xsl:with-param name="objInstance">
                      <xsl:value-of select="$subjectVar"/>
                    </xsl:with-param>
                    <xsl:with-param name="args" select="$beliefIdVar"/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:when test="fn:exists(aorsl:BeliefEntityType[@language = $output.language])">
                  <xsl:call-template name="java:callMethod">
                    <xsl:with-param name="method" select="'createBeliefEntity'"/>
                    <xsl:with-param name="inLine" select="true()"/>
                    <xsl:with-param name="objInstance">
                      <xsl:value-of select="$subjectVar"/>
                    </xsl:with-param>
                    <xsl:with-param name="args" as="xs:string*">
                      <xsl:value-of select="'this.getAgentSubject()'"/>
                      <xsl:value-of select="aorsl:BeliefEntityType[@language = $output.language]"/>
                      <xsl:value-of select="$beliefIdVar"/>
                    </xsl:with-param>
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:message>
                    <xsl:text>[ERROR] in create belief entity in AgentRule [</xsl:text>
                    <xsl:value-of select="../../@name"/>
                    <xsl:text>. No type defined for this belief.]! </xsl:text>
                  </xsl:message>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent+2"/>
              <xsl:with-param name="type" select="$core.class.entity"/>
              <xsl:with-param name="name" select="$beliefTmpVar"/>
              <xsl:with-param name="value" select="$beliefTypeVar"> </xsl:with-param>
            </xsl:call-template>

            <!-- add belief to agent beliefs list -->
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="indent" select="$indent+2"/>
              <xsl:with-param name="method" select="'addBeliefEntity'"/>
              <xsl:with-param name="objInstance" select="$subjectVar"/>
              <xsl:with-param name="args" select="$beliefTmpVar"/>
            </xsl:call-template>

            <!-- Update properties of the new created belief -->
            <xsl:for-each select="aorsl:BeliefSlot">
              <xsl:choose>
                <xsl:when test="fn:exists(../@beliefEntityType) and ../@beliefEntityType!=''">
                  <xsl:call-template name="java:callSetterMethod">
                    <xsl:with-param name="indent" select="$indent+2"/>
                    <xsl:with-param name="instVariable" select="@property"/>
                    <xsl:with-param name="objInstance">
                      <xsl:text>((</xsl:text>
                      <xsl:value-of select="../@beliefEntityType"/>
                      <xsl:text>)</xsl:text>
                      <xsl:value-of select="$beliefTmpVar"/>
                      <xsl:text>)</xsl:text>
                    </xsl:with-param>
                    <xsl:with-param name="value" select="aorsl:ValueExpr[@language = $output.language]"/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:when
                  test="fn:exists(../aorsl:BeliefEntityType[@language = $output.language]) 
                    and fn:normalize-space(../aorsl:BeliefEntityType[@language = $output.language])!=''">
                  <xsl:call-template name="java:callMethod">
                    <xsl:with-param name="indent" select="$indent+2"/>
                    <xsl:with-param name="method" select="'updateBeliefEntityProperty'"/>
                    <xsl:with-param name="objInstance" select="$subjectVar"/>
                    <xsl:with-param name="args" as="xs:string*">
                      <xsl:value-of select="$beliefIdVar"/>
                      <xsl:value-of select="jw:quote(@property)"/>
                      <xsl:value-of select="aorsl:ValueExpr[@language = $output.language]"/>
                    </xsl:with-param>
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:message>
                    <xsl:text>[ERROR] in create belief entity in AgentRule [</xsl:text>
                    <xsl:value-of select="../../@name"/>
                    <xsl:text>. No type defined for this belief.]! </xsl:text>
                  </xsl:message>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:for-each>
          </xsl:for-each>

          <!-- destroy belief entity in agent rules -->
          <xsl:for-each select="$mode/aorsl:UPDATE-AGT/aorsl:DestroyBeliefEntity">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="indent" select="$indent+1"/>
              <xsl:with-param name="method" select="'removeBeliefEntityById'"/>
              <xsl:with-param name="objInstance">
                <xsl:value-of select="fn:concat('((',$agentClassName,')this.getAgentSubject())')"/>
              </xsl:with-param>
              <xsl:with-param name="args">
                <xsl:choose>
                  <xsl:when
                    test="fn:exists(aorsl:BeliefEntityIdRef[@language = $output.language]) 
                      and fn:normalize-space(aorsl:BeliefEntityIdRef[@language = $output.language]) != ''">
                    <xsl:value-of select="aorsl:BeliefEntityIdRef[@language = $output.language]"/>
                  </xsl:when>
                  <xsl:when test="fn:exists(@beliefEntityIdRef) and @beliefEntityIdRef!=''">
                    <xsl:value-of select="@beliefEntityIdRef"/>
                  </xsl:when>
                  <xsl:when test="fn:exists(@beliefEntityVariable) and @beliefEntityVariable!=''">
                    <xsl:value-of select="fn:concat('this.', @beliefEntityVariable,'.getId()')"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:message>
                      <xsl:text>[ERROR] in destroy belief entity in AgentRule [</xsl:text>
                      <xsl:value-of select="../../@name"/>
                      <xsl:text>! No IdRef or variable was defined for this belief entity</xsl:text>
                      <xsl:text>]!</xsl:text>
                    </xsl:message>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:for-each>

        </xsl:if>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="aorsl:Call" mode="createAgentRules.method.stateEffects.call">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="agtVarName" as="xs:string" required="yes"/>
    
    
    <xsl:variable name="hasSubjectivFunction" as="xs:boolean">
      <xsl:call-template name="checkForExistingSubjectiveFunctions">
        <xsl:with-param name="agentType" select="(ancestor::aorsl:AgentType | ancestor::aorsl:PhysicalAgentType)[1]/@name"/>
      </xsl:call-template>
    </xsl:variable>
    
    <xsl:variable name="funct" as="element()*">
      <xsl:choose>
        <xsl:when test="not($hasSubjectivFunction)">
          <xsl:call-template name="getAgentFunction">
            <xsl:with-param name="agentType" select="(ancestor::aorsl:AgentType | ancestor::aorsl:PhysicalAgentType)[1]/@name"/>
            <xsl:with-param name="functionName" select="@procedure"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="getAgentSubjectiveFunction">
            <xsl:with-param name="agentType" select="(ancestor::aorsl:AgentType | ancestor::aorsl:PhysicalAgentType)[1]/@name"/>
            <xsl:with-param name="functionName" select="@procedure"/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>     
    </xsl:variable>
    
    <xsl:apply-templates select="$funct" mode="assistents.call.function">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="call" select="."/>
      <xsl:with-param name="variableName">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="varName" select="$agtVarName"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:apply-templates>
    
    
    
    <!--xsl:variable name="call" select="."/>
    
    <xsl:choose>
      <xsl:when test="exists($funct)">
        <xsl:choose>
          <xsl:when test="count(aorsl:Argument) = count($funct/aorsl:Parameter)">
            
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="varName" select="$agtVarName"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="method" select="$funct/@name"/>
              <xsl:with-param name="args" as="item()*">
                <xsl:for-each select="$funct/aorsl:Parameter">
                  <xsl:variable name="argument" select="$call/aorsl:Argument[@property = current()/@name]"/>
                  <xsl:choose>
                    <xsl:when test="exists($argument)">
                      <xsl:choose>
                        <xsl:when test="@type = 'String'">
                          <xsl:value-of select="jw:quote($argument/@value)"/>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:value-of select="$argument/@value"/>
                        </xsl:otherwise>
                      </xsl:choose>               
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:message>
                        <xsl:text>[ERROR] No Argument for function parameter [</xsl:text>
                        <xsl:value-of select="@name"/>
                        <xsl:text>] found.</xsl:text>
                      </xsl:message>
                    </xsl:otherwise>
                  </xsl:choose>               
                </xsl:for-each>
              </xsl:with-param>
            </xsl:call-template>
            
          </xsl:when>
          <xsl:otherwise>
            <xsl:message>
              <xsl:text>[ERROR] Wrong numbers of Argument for call of function </xsl:text>
              <xsl:value-of select="$funct/@name"/>
              <xsl:text> found.</xsl:text>
            </xsl:message>
          </xsl:otherwise>
        </xsl:choose>    

      </xsl:when>
      <xsl:otherwise>
        <xsl:message>
          <xsl:text>[ERROR] No Function [</xsl:text>
          <xsl:value-of select="@procedure"/>
          <xsl:text>] in found.</xsl:text>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose-->
     
  </xsl:template>

  <!-- UpdateComplexDataPropertyValue -->
  <xsl:template match="aorsl:UpdateComplexDataPropertyValue" mode="createAgentRules.method.stateEffects.updateComplexDataPropertyValue">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="agentVariable" as="xs:string" required="yes" tunnel="yes"/>

    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="method" select="@procedure"/>
      <xsl:with-param name="objInstance">
        <xsl:call-template name="java:callGetterMethod">
          <xsl:with-param name="inLine" select="true()"/>
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="varName" select="$agentVariable"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="instVariable" select="@complexDataProperty"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:for-each select="aorsl:Argument">
          <xsl:if test="aorsl:ValueExpr[@language eq $output.language]">
            <xsl:value-of select="aorsl:ValueExpr[@language eq $output.language][1]"/>
          </xsl:if>
        </xsl:for-each>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- createDescription -->
  <xsl:template match="aorsl:CreateDescription" mode="createAgentRules.method.stateEffects.createDescription">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="agentVariable" as="xs:string" required="yes" tunnel="yes"/>

    <xsl:variable name="descriptionVariable" select="@descriptionVariable"/>
    <xsl:variable name="beliefEntityVariable" select="@beliefEntityVariable"/>
    <xsl:variable name="tmpDescriptionVar" select="fn:concat('tmpDescriptionVar','_', position())"/>
    <xsl:call-template name="java:if">
      <xsl:with-param name="indent" select="$indent+1"/>
      <xsl:with-param name="condition" select="fn:concat($beliefEntityVariable,' != null')"/>
      <xsl:with-param name="thenContent">
        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent+2"/>
          <xsl:with-param name="name" select="$tmpDescriptionVar"/>
          <xsl:with-param name="type" select="'HashMap&lt;String, Object&gt;'"/>
          <xsl:with-param name="value" select="'new HashMap&lt;String, Object&gt;()'"/>
        </xsl:call-template>
        <xsl:for-each select="fn:tokenize(@properties, ' ')">
          <xsl:if test=". != ''">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="indent" select="$indent+2"/>
              <xsl:with-param name="method" select="'put'"/>
              <xsl:with-param name="objInstance" select="$tmpDescriptionVar"/>
              <xsl:with-param name="args" as="xs:string*">
                <xsl:value-of select="jw:quote(.)"/>
                <xsl:value-of>
                  <xsl:call-template name="java:callMethod">
                    <xsl:with-param name="inLine" select="true()"/>
                    <xsl:with-param name="method" select="'getBeliefEntityPropertyValue'"/>
                    <xsl:with-param name="objInstance" select="$agentVariable"/>
                    <xsl:with-param name="args" as="xs:string*">
                      <xsl:value-of select="$beliefEntityVariable"/>
                      <xsl:value-of select="jw:quote(.)"/>
                    </xsl:with-param>
                  </xsl:call-template>
                </xsl:value-of>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:if>
        </xsl:for-each>
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent+2"/>
          <xsl:with-param name="method" select="'put'"/>
          <xsl:with-param name="objInstance" select="$descriptionVariable"/>
          <xsl:with-param name="args" as="xs:string*">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="method" select="'getId'"/>
              <xsl:with-param name="objInstance" select="$beliefEntityVariable"/>
            </xsl:call-template>
            <xsl:value-of select="$tmpDescriptionVar"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- resultingActionEvents -->
  <xsl:template match="aorsl:ReactionRule" mode="createAgentRules.method.resultingActionEvents">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="eventVar" required="yes" as="xs:string"/>
    <xsl:param name="agentVariable"/>
    <xsl:param name="agentType" required="yes" as="xs:string"/>
    <xsl:param name="mode"/>
    <xsl:param name="prefix" as="xs:string" select="''"/>

    <xsl:variable name="methodName" select="fn:concat($prefix,jw:upperWord('resultingActionEvents'))"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'ArrayList'"/>
      <xsl:with-param name="genericType" select="$core.class.actionEvent"/>
      <xsl:with-param name="name" select="$methodName"/>
      <xsl:with-param name="content">

        <xsl:variable name="resultVar" select="'actionEvents'"/>
        <xsl:call-template name="java:newArrayListObject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="generic" select="$core.class.actionEvent"/>
          <xsl:with-param name="name" select="$resultVar"/>
        </xsl:call-template>

        <xsl:if test="fn:exists($mode)">

          <xsl:if test="fn:exists($mode/aorsl:SCHEDULE-EVT/aorsl:ActionEventExpr)">

            <!-- create the variables agentId, occurrencetime and agentRef for action event constructor -->
            <xsl:variable name="agentIdVarName" select="'agentId'"/>
            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="type" select="'long'"/>
              <xsl:with-param name="name" select="$agentIdVarName"/>
              <xsl:with-param name="value">
                <xsl:choose>
                  <xsl:when test="$agentVariable">
                    <xsl:call-template name="java:callGetterMethod">
                      <xsl:with-param name="objInstance">
                        <xsl:call-template name="java:varByDotNotation">
                          <xsl:with-param name="varName" select="$agentVariable"/>
                        </xsl:call-template>
                      </xsl:with-param>
                      <xsl:with-param name="instVariable" select="'id'"/>
                      <xsl:with-param name="inLine" select="true()"/>
                    </xsl:call-template>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:call-template name="java:callGetterMethod">
                      <xsl:with-param name="objInstance">
                        <xsl:call-template name="java:callGetterMethod">
                          <xsl:with-param name="instVariable" select="'AgentSubject'"/>
                          <xsl:with-param name="inLine" select="true()"/>
                        </xsl:call-template>
                      </xsl:with-param>
                      <xsl:with-param name="instVariable" select="'id'"/>
                      <xsl:with-param name="inLine" select="true()"/>
                    </xsl:call-template>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
            </xsl:call-template>

            <xsl:variable name="occurrenceTimeVarName" select="'occurrenceTime'"/>
            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="type" select="'long'"/>
              <xsl:with-param name="name" select="$occurrenceTimeVarName"/>
              <xsl:with-param name="value">
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="objInstance">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="$eventVar"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="instVariable" select="'occurrenceTime'"/>
                  <xsl:with-param name="inLine" select="true()"/>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>

            <xsl:variable name="agentRefVarName" select="'agentRef'"/>
            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="type" select="$core.class.physAgentObject"/>
              <xsl:with-param name="castType">
                <xsl:if test="$agentType eq 'PhysicalAgentType'">
                  <xsl:value-of select="$core.class.physAgentObject"/>
                </xsl:if>
              </xsl:with-param>
              <xsl:with-param name="name" select="$agentRefVarName"/>
              <xsl:with-param name="value">
                <xsl:choose>
                  <xsl:when test="$agentVariable">
                    <xsl:call-template name="java:callGetterMethod">
                      <xsl:with-param name="objInstance" select="$agentVariable"/>
                      <xsl:with-param name="instVariable" select="'AgentObject'"/>
                      <xsl:with-param name="inLine" select="true()"/>
                    </xsl:call-template>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:call-template name="java:callGetterMethod">
                      <xsl:with-param name="objInstance">
                        <xsl:call-template name="java:callGetterMethod">
                          <xsl:with-param name="instVariable" select="'AgentSubject'"/>
                          <xsl:with-param name="inLine" select="true()"/>
                        </xsl:call-template>
                      </xsl:with-param>
                      <xsl:with-param name="instVariable" select="'AgentObject'"/>
                      <xsl:with-param name="inLine" select="true()"/>
                    </xsl:call-template>

                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="java:newLine"/>

            <!-- create the actionevents -->
            <xsl:for-each select="$mode/aorsl:SCHEDULE-EVT/aorsl:ActionEventExpr">

              <xsl:variable name="output">
                <xsl:variable name="block-indent"
                  select="if (fn:exists(aorsl:Condition[@language eq $output.language])) then $indent + 1 else $indent"/>

                <xsl:variable name="actionVarName" select="fn:concat(jw:lowerWord(@actionEventType), '_', position())"/>

                <xsl:variable name="delay">
                  <xsl:apply-templates select="." mode="assistents.getDelay"/>
                </xsl:variable>

                <xsl:call-template name="java:newObject">
                  <xsl:with-param name="indent" select="$block-indent + 1"/>
                  <xsl:with-param name="class" select="@actionEventType"/>
                  <xsl:with-param name="varName" select="$actionVarName"/>
                  <xsl:with-param name="args" as="xs:string*">
                    <xsl:value-of select="fn:concat($occurrenceTimeVarName, ' + ', $delay)"/>
                    <xsl:value-of select="$agentIdVarName"/>
                    <xsl:value-of select="$agentRefVarName"/>
                  </xsl:with-param>
                </xsl:call-template>

                <xsl:for-each select="aorsl:Slot">
                  <xsl:call-template name="java:callSetterMethod">
                    <xsl:with-param name="indent" select="$block-indent + 1"/>
                    <xsl:with-param name="objInstance" select="$actionVarName"/>
                    <xsl:with-param name="instVariable" select="@property"/>
                    <xsl:with-param name="value">
                      <xsl:apply-templates select="." mode="assistents.getSlotValue"/>
                    </xsl:with-param>
                  </xsl:call-template>
                </xsl:for-each>

                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="indent" select="$block-indent + 1"/>
                  <xsl:with-param name="objInstance" select="$resultVar"/>
                  <xsl:with-param name="method" select="'add'"/>
                  <xsl:with-param name="args" as="xs:string*" select="$actionVarName"/>
                </xsl:call-template>
                <xsl:call-template name="java:newLine"/>

              </xsl:variable>

              <xsl:apply-templates select="." mode="assistent.resultingEvent.output">
                <xsl:with-param name="indent" select="$indent"/>
                <xsl:with-param name="output" select="$output"/>
              </xsl:apply-templates>

            </xsl:for-each>
          </xsl:if>

          <!-- OutMessageEvents -->
          <xsl:apply-templates select="$mode/aorsl:SCHEDULE-EVT/aorsl:OutMessageEventExpr"
            mode="createAgentRules.helper.method.resultingActionEvents">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="eventVar" select="$eventVar"/>
            <xsl:with-param name="agentVariable" select="$agentVariable"/>
            <xsl:with-param name="eventList" select="$resultVar"/>
          </xsl:apply-templates>

        </xsl:if>

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value" select="$resultVar"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- resultingInternalEvents() -->
  <xsl:template match="aorsl:ReactionRule" mode="createAgentRules.method.resultingInternalEvents">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="eventVar" required="yes" as="xs:string"/>
    <xsl:param name="mode"/>
    <xsl:param name="prefix" as="xs:string" required="yes"/>

    <xsl:variable name="methodName" select="fn:concat($prefix, jw:upperWord('resultingInternalEvents'))"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'ArrayList'"/>
      <xsl:with-param name="genericType" select="$core.class.internalEvent"/>
      <xsl:with-param name="name" select="$methodName"/>
      <xsl:with-param name="content">

        <xsl:variable name="resultVar" select="'internalEvents'"/>
        <xsl:call-template name="java:newArrayListObject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="generic" select="$core.class.internalEvent"/>
          <xsl:with-param name="name" select="$resultVar"/>
        </xsl:call-template>
        <xsl:call-template name="java:newLine"/>

        <xsl:if test="fn:exists($mode)">

          <!-- ActualPerceptionEvents -->
          <xsl:apply-templates select="$mode/aorsl:SCHEDULE-EVT/aorsl:ActualPerceptionEventExpr"
            mode="createAgentRules.helper.method.resultingInternalEvents">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="eventVar" select="$eventVar"/>
            <xsl:with-param name="eventList" select="$resultVar"/>
          </xsl:apply-templates>
          <xsl:call-template name="java:newLine"/>

          <!-- ActualInMessageEvents -->
          <xsl:apply-templates select="$mode/aorsl:SCHEDULE-EVT/aorsl:ActualInMessageEventExpr"
            mode="createAgentRules.helper.method.resultingInternalEvents">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="eventVar" select="$eventVar"/>
            <xsl:with-param name="eventList" select="$resultVar"/>
          </xsl:apply-templates>

          <!-- ReminderEvents -->
          <xsl:apply-templates select="$mode/aorsl:SCHEDULE-EVT/aorsl:ReminderEventExpr"
            mode="createAgentRules.helper.method.resultingInternalEvents">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="eventVar" select="$eventVar"/>
            <xsl:with-param name="eventList" select="$resultVar"/>
          </xsl:apply-templates>

        </xsl:if>

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value" select="$resultVar"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- execute() -->
  <xsl:template match="aorsl:ReactionRule" mode="createAgentRules.method.execute">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="eventVar" required="yes" as="xs:string"/>
    <xsl:param name="agentClassName" required="yes" as="xs:string"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="annotation">
        <xsl:call-template name="getAnnotationSuppressWarnings.unchecked"/>
      </xsl:with-param>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="'execute'"/>
      <xsl:with-param name="content">

        <!-- ReminderEvent with reminder message -->
        <xsl:if test="aorsl:WHEN/@eventType eq $core.class.reminderEvent and fn:exists(aorsl:WHEN/@reminderMsg)">
          <xsl:call-template name="java:if">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="condition">
              <xsl:text>!</xsl:text>
              <xsl:call-template name="java:callMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="method" select="'equals'"/>
                <xsl:with-param name="objInstance">
                  <xsl:call-template name="java:callMethod">
                    <xsl:with-param name="inLine" select="true()"/>
                    <xsl:with-param name="method" select="'getReminderMsg'"/>
                    <xsl:with-param name="objInstance" select="fn:concat('this.',$eventVar)"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="args" as="xs:string" select="jw:quote(aorsl:WHEN/@reminderMsg)"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="thenContent">
              <xsl:call-template name="java:return">
                <xsl:with-param name="indent" select="$indent + 2"/>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:if>

        <xsl:apply-templates select="aorsl:WHEN[@eventType eq $core.class.physicalObjectPerceptionEvent]"
          mode="createAgentRules.method.execute.helper.physicalObjectPerceptionEvent">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="eventVar" select="$eventVar"/>
        </xsl:apply-templates>

        <!-- declare belief entity variable - priority of Id declaration up-to-down (attribute has lower priority than element)-->
        <xsl:apply-templates select="aorsl:FOR[@beliefEntityVariable]" mode="createAgentRules.method.execute.helper.declareBeliefEntity">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="agentClassName" select="$agentClassName"/>
        </xsl:apply-templates>

        <xsl:apply-templates select="aorsl:FOR-ListItemVariable" mode="createAgentRules.method.execute.helper.declareFOR-List">
          <xsl:with-param name="indent" select="$indent"/>
        </xsl:apply-templates>

        <xsl:variable name="for-beliefs"
          select="aorsl:FOR[@beliefEntityVariable][fn:exists(aorsl:BeliefEntityType[@language = $output.language]) or
          fn:exists(@beliefEntityType)]"/>
        <xsl:variable name="for-ListItemVariable" select="aorsl:FOR-ListItemVariable"/>

        <xsl:call-template name="nestedLoops">
          <xsl:with-param name="set" select="($for-beliefs, $for-ListItemVariable)"/>
          <xsl:with-param name="iteratorPostfix" select="'Id'"/>
          <xsl:with-param name="forEachLoopPostfix" select="'Obj'"/>
          <xsl:with-param name="listPostfix" select="'List'"/>
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="content">
            <xsl:call-template name="java:newLine"/>

            <xsl:variable name="newIndent" select="count(($for-beliefs, $for-ListItemVariable)) + 3"/>

            <!--  set the classvariables from DataVariableDeclaration -->
            <xsl:apply-templates select="aorsl:FOR[@dataVariable]" mode="assistents.setDataVariableDeclaration">
              <xsl:with-param name="indent" select="$newIndent"/>
            </xsl:apply-templates>
            <xsl:if test="fn:exists(aorsl:FOR[@dataVariable])">
              <xsl:call-template name="java:newLine"/>
            </xsl:if>

            <xsl:call-template name="ruleExecuteDoIfThenElse">
              <xsl:with-param name="indent" select="$newIndent"/>
              <xsl:with-param name="methodsPrefix" select="'do'"/>
              <xsl:with-param name="scheduleInternaleEvents"
                select="fn:exists(aorsl:DO/aorsl:SCHEDULE-EVT/aorsl:ActionEventExpr) or 
                fn:exists(aorsl:DO/aorsl:SCHEDULE-EVT/aorsl:ActualInMessageEventExpr) or
                fn:exists(aorsl:DO/aorsl:SCHEDULE-EVT/aorsl:ReminderEventExpr)"/>
              <xsl:with-param name="scheduleActionEvents"
                select="fn:exists(aorsl:DO/aorsl:SCHEDULE-EVT/aorsl:ActionEventExpr) or 
                fn:exists(aorsl:DO/aorsl:SCHEDULE-EVT/aorsl:OutMessageEventExpr)"
              />
            </xsl:call-template>

            <xsl:call-template name="java:if">
              <xsl:with-param name="indent" select="$newIndent"/>
              <xsl:with-param name="condition">
                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="method" select="'condition'"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="thenContent">
                <xsl:call-template name="ruleExecuteDoIfThenElse">
                  <xsl:with-param name="indent" select="$newIndent + 1"/>
                  <xsl:with-param name="methodsPrefix" select="'then'"/>
                  <xsl:with-param name="scheduleInternaleEvents"
                    select="fn:exists(aorsl:THEN/aorsl:SCHEDULE-EVT/aorsl:ActualPerceptionEventExpr) or 
                    fn:exists(aorsl:THEN/aorsl:SCHEDULE-EVT/aorsl:ActualInMessageEventExpr) or
                    fn:exists(aorsl:THEN/aorsl:SCHEDULE-EVT/aorsl:ReminderEventExpr)"/>
                  <xsl:with-param name="scheduleActionEvents"
                    select="fn:exists(aorsl:THEN/aorsl:SCHEDULE-EVT/aorsl:ActionEventExpr) or 
                    fn:exists(aorsl:THEN/aorsl:SCHEDULE-EVT/aorsl:OutMessageEventExpr)"
                  />
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="elseContent">
                <xsl:call-template name="ruleExecuteDoIfThenElse">
                  <xsl:with-param name="indent" select="$newIndent + 1"/>
                  <xsl:with-param name="methodsPrefix" select="'else'"/>
                  <xsl:with-param name="scheduleInternaleEvents"
                    select="fn:exists(aorsl:ELSE/aorsl:SCHEDULE-EVT/aorsl:ActualPerceptionEventExpr) or 
                    fn:exists(aorsl:ELSE/aorsl:SCHEDULE-EVT/aorsl:ActualInMessageEventExpr) or
                    fn:exists(aorsl:ELSE/aorsl:SCHEDULE-EVT/aorsl:ReminderEventExpr)"/>
                  <xsl:with-param name="scheduleActionEvents"
                    select="fn:exists(aorsl:ELSE/aorsl:SCHEDULE-EVT/aorsl:ActionEventExpr) or 
                    fn:exists(aorsl:ELSE/aorsl:SCHEDULE-EVT/aorsl:OutMessageEventExpr)"
                  />
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>



          </xsl:with-param>
        </xsl:call-template>

        <!-- generate for cycles - if neded 
        <xsl:for-each select="aorsl:FOR[@beliefEntityVariable]">
          <xsl:if test="fn:exists(aorsl:BeliefEntityType[@language = $output.language]) or
            fn:exists(@beliefEntityType)">
            <xsl:variable name="contor" select="fn:concat('_i', position())"/>
            <xsl:variable name="beliefVar" select="fn:concat($createdVariablesNamePrefix, @beliefEntityVariable)"/>
            <xsl:call-template name="java:for-loop">
              <xsl:with-param name="indent" select="$indent + position()"/>
              <xsl:with-param name="withThenBrackets" select="false()"/>
              <xsl:with-param name="loopVariable" select="$contor"/>
              <xsl:with-param name="start" select="0"/>
              <xsl:with-param name="condition" select="fn:concat($contor,'&lt; this.',$beliefVar,'.size()')"/>
              <xsl:with-param name="increment" select="1"/>
              <xsl:with-param name="content" select="''"/>
            </xsl:call-template>
          </xsl:if>
        </xsl:for-each>

        <xsl:variable name="newIndent" select="$indent + fn:count(aorsl:FOR[@beliefEntityVariable]) - 1"/>

        <xsl:if test="fn:exists(aorsl:FOR)">
          <xsl:call-template name="java:codeLine">
            <xsl:with-param name="indent" select="($newIndent)-1"/>
            <xsl:with-param name="semicolon" select="false()"/>
            <xsl:with-param name="content">
              <xsl:text>{</xsl:text>
            </xsl:with-param>
          </xsl:call-template>
          </xsl:if> -->


        <!-- instantiate variables with the corresponding value from list 
        <xsl:for-each select="aorsl:FOR[@beliefEntityVariable]">
          <xsl:if test="fn:exists(@beliefEntityType) or fn:exists(aorsl:BeliefEntityType[@language = $output.language])">
            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$newIndent + 2"/>
              <xsl:with-param name="name" select="@beliefEntityVariable"/>
              <xsl:with-param name="value" select="fn:concat('__',@beliefEntityVariable,'.get(','_i',position(),')')"/>
            </xsl:call-template>
          </xsl:if>
          </xsl:for-each> -->

        <!-- create the DO part
        <xsl:call-template name="ruleExecuteDoIfThenElse">
          <xsl:with-param name="indent" select="$newIndent + 2"/>
          <xsl:with-param name="methodsPrefix" select="'do'"/>
          <xsl:with-param name="scheduleInternaleEvents"
            select="fn:exists(aorsl:DO/aorsl:SCHEDULE-EVT/aorsl:ActionEventExpr) or 
                    fn:exists(aorsl:DO/aorsl:SCHEDULE-EVT/aorsl:ActualInMessageEventExpr) or
                    fn:exists(aorsl:DO/aorsl:SCHEDULE-EVT/aorsl:ReminderEventExpr)"/>
          <xsl:with-param name="scheduleActionEvents"
            select="fn:exists(aorsl:DO/aorsl:SCHEDULE-EVT/aorsl:ActionEventExpr) or 
            fn:exists(aorsl:DO/aorsl:SCHEDULE-EVT/aorsl:OutMessageEventExpr)"
          />
        </xsl:call-template>

        <xsl:call-template name="java:if">
          <xsl:with-param name="indent" select="$newIndent + 2"/>
          <xsl:with-param name="condition">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="method" select="'condition'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="thenContent">
            <xsl:call-template name="ruleExecuteDoIfThenElse">
              <xsl:with-param name="indent" select="$indent + 3"/>
              <xsl:with-param name="methodsPrefix" select="''"/>
              <xsl:with-param name="scheduleInternaleEvents"
                select="fn:exists(aorsl:SCHEDULE-EVT/aorsl:ActualPerceptionEventExpr) or 
                fn:exists(aorsl:SCHEDULE-EVT/aorsl:ActualInMessageEventExpr) or
                fn:exists(aorsl:SCHEDULE-EVT/aorsl:ReminderEventExpr)"/>
              <xsl:with-param name="scheduleActionEvents"
                select="fn:exists(aorsl:SCHEDULE-EVT/aorsl:ActionEventExpr) or 
                fn:exists(aorsl:SCHEDULE-EVT/aorsl:OutMessageEventExpr)"
              />
            </xsl:call-template>
            <xsl:call-template name="ruleExecuteDoIfThenElse">
              <xsl:with-param name="indent" select="$indent + 3"/>
              <xsl:with-param name="methodsPrefix" select="'then'"/>
              <xsl:with-param name="scheduleInternaleEvents"
                select="fn:exists(aorsl:THEN/aorsl:SCHEDULE-EVT/aorsl:ActualPerceptionEventExpr) or 
                fn:exists(aorsl:THEN/aorsl:SCHEDULE-EVT/aorsl:ActualInMessageEventExpr) or
                fn:exists(aorsl:THEN/aorsl:SCHEDULE-EVT/aorsl:ReminderEventExpr)"/>
              <xsl:with-param name="scheduleActionEvents"
                select="fn:exists(aorsl:THEN/aorsl:SCHEDULE-EVT/aorsl:ActionEventExpr) or 
                fn:exists(aorsl:THEN/aorsl:SCHEDULE-EVT/aorsl:OutMessageEventExpr)"
              />
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="elseContent">
            <xsl:call-template name="ruleExecuteDoIfThenElse">
              <xsl:with-param name="indent" select="$indent + 3"/>
              <xsl:with-param name="methodsPrefix" select="'else'"/>
              <xsl:with-param name="scheduleInternaleEvents"
                select="fn:exists(aorsl:ELSE/aorsl:SCHEDULE-EVT/aorsl:ActualPerceptionEventExpr) or 
                fn:exists(aorsl:ELSE/aorsl:SCHEDULE-EVT/aorsl:ActualInMessageEventExpr) or
                fn:exists(aorsl:ELSE/aorsl:SCHEDULE-EVT/aorsl:ReminderEventExpr)"/>
              <xsl:with-param name="scheduleActionEvents"
                select="fn:exists(aorsl:ELSE/aorsl:SCHEDULE-EVT/aorsl:ActionEventExpr) or 
                fn:exists(aorsl:ELSE/aorsl:SCHEDULE-EVT/aorsl:OutMessageEventExpr)"
              />
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:if test="fn:exists(aorsl:FOR)">
          <xsl:call-template name="java:codeLine">
            <xsl:with-param name="indent" select="$indent+1"/>
            <xsl:with-param name="semicolon" select="false()"/>
            <xsl:with-param name="content">
              <xsl:text>}</xsl:text>
            </xsl:with-param>
          </xsl:call-template>
          </xsl:if>   -->

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- this function call the three methods of an agent rules, and use a prefix for the do, if, then, else cases -->
  <xsl:template name="ruleExecuteDoIfThenElse">
    <xsl:param name="indent"/>
    <xsl:param name="methodsPrefix" as="xs:string"/>
    <xsl:param name="scheduleInternaleEvents" as="xs:boolean" select="true()"/>
    <xsl:param name="scheduleActionEvents" as="xs:boolean" select="true()"/>

    <xsl:variable name="resultingInternalEventsMethodName">
      <xsl:choose>
        <xsl:when test="$methodsPrefix != ''">
          <xsl:value-of select="fn:concat($methodsPrefix, jw:upperWord('resultingInternalEvents'))"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'resultingInternalEvents'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="stateEffectsMethodName">
      <xsl:choose>
        <xsl:when test="$methodsPrefix != ''">
          <xsl:value-of select="fn:concat($methodsPrefix, jw:upperWord('stateEffects'))"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'stateEffects'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="resultingActionEventsMethodName">
      <xsl:choose>
        <xsl:when test="$methodsPrefix != ''">
          <xsl:value-of select="fn:concat($methodsPrefix, jw:upperWord('resultingActionEvents'))"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'resultingActionEvents'"/>
        </xsl:otherwise>
      </xsl:choose>

    </xsl:variable>

    <!-- resulting internal events -->
    <xsl:if test="$scheduleInternaleEvents">
      <xsl:call-template name="java:callMethod">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="objInstance">
          <xsl:call-template name="java:varByDotNotation">
            <xsl:with-param name="varName" select="'resultingInternalEvents'"/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="method" select="'addAll'"/>
        <xsl:with-param name="args">
          <xsl:call-template name="java:callMethod">
            <xsl:with-param name="inLine" select="true()"/>
            <xsl:with-param name="method" select="$resultingInternalEventsMethodName"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>

    <!-- state effects -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="method" select="$stateEffectsMethodName"/>
    </xsl:call-template>

    <!-- resulting actions events -->
    <xsl:if test="$scheduleActionEvents">
      <xsl:call-template name="java:callMethod">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="objInstance">
          <xsl:call-template name="java:varByDotNotation">
            <xsl:with-param name="varName" select="'resultingActionEvents'"/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="method" select="'addAll'"/>
        <xsl:with-param name="args">
          <xsl:call-template name="java:callMethod">
            <xsl:with-param name="inLine" select="true()"/>
            <xsl:with-param name="method" select="$resultingActionEventsMethodName"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <!-- getter and setter for triggering event -->
  <!-- its possible to share it, but the returntype of the getmethod is differnt with envRules -->
  <xsl:template match="aorsl:ReactionRule" mode="createAgentRules.method.setGetTriggeringEvent">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="eventVar" required="yes" as="xs:string"/>

    <!-- getter -->
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'String'"/>
      <xsl:with-param name="name" select="'getTriggeringEventType'"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value" select="jw:quote(if (aorsl:WHEN/@eventType) then aorsl:WHEN/@eventType else $core.class.onEveryStepIntEvent)"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

    <!-- setter -->
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="'setTriggeringEvent'"/>
      <xsl:with-param name="parameterList" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="$core.class.event"/>
          <xsl:with-param name="name" select="jw:lowerWord($core.class.event)"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="castType">
            <xsl:choose>
              <xsl:when test="exists(aorsl:WHEN)">
                <xsl:value-of select="aorsl:WHEN/@eventType"/>
              </xsl:when>
              <xsl:when test="exists(aorsl:ON-EACH-SIMULATION-STEP)">
                <xsl:value-of select="$core.package.onEveryStepIntEvent"/>
              </xsl:when>
            </xsl:choose>
          </xsl:with-param>
          <xsl:with-param name="name">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="varName" select="$eventVar"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="value" select="jw:lowerWord($core.class.event)"/>
        </xsl:call-template>

        <!-- set the triggeredTime from occurenceTime -->
        <xsl:call-template name="java:callSetterMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="instVariable" select="'triggeredTime'"/>
          <xsl:with-param name="value">
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance" select="$eventVar"/>
              <xsl:with-param name="instVariable" select="'occurrenceTime'"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>

        <!-- @messageVariable -->
        <xsl:if test="aorsl:WHEN/@messageVariable and aorsl:WHEN/@messageType">
          <xsl:call-template name="java:variable">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="name">
              <xsl:call-template name="java:varByDotNotation">
                <xsl:with-param name="varName" select="aorsl:WHEN/@messageVariable"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="value">
              <xsl:call-template name="java:callGetterMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="castType" select="aorsl:WHEN/@messageType"/>
                <xsl:with-param name="objInstance" select="$eventVar"/>
                <xsl:with-param name="instVariable" select="'message'"/>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:if>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>


  <!--***************-->
  <!--     helpers      -->
  <!--***************-->
  <xsl:template match="aorsl:ActualPerceptionEventExpr" mode="createAgentRules.helper.method.resultingInternalEvents">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="eventList" required="yes" as="xs:string"/>
    <xsl:param name="eventVar" required="yes" as="xs:string"/>

    <xsl:variable name="apVarName" select="fn:concat(jw:lowerWord(@eventType), '_', position())"/>
    <xsl:call-template name="java:newObject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="class" select="@eventType"/>
      <xsl:with-param name="varName" select="$apVarName"/>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:call-template name="java:callGetterMethod">
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="'this'"/>
              <xsl:with-param name="varName" select="$eventVar"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="instVariable" select="'occurrenceTime'"/>
          <xsl:with-param name="inLine" select="true()"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

    <xsl:for-each select="aorsl:Slot">
      <xsl:call-template name="java:callSetterMethod">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="objInstance" select="$apVarName"/>
        <xsl:with-param name="instVariable" select="@property"/>
        <xsl:with-param name="value">
          <xsl:apply-templates select="." mode="assistents.getSlotValue"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:for-each>

    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance" select="$eventList"/>
      <xsl:with-param name="method" select="'add'"/>
      <xsl:with-param name="args" as="xs:string*" select="$apVarName"/>
    </xsl:call-template>
    <xsl:call-template name="java:newLine"/>
  </xsl:template>


  <xsl:template match="aorsl:ActualInMessageEventExpr" mode="createAgentRules.helper.method.resultingInternalEvents">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="eventList" required="yes" as="xs:string"/>
    <xsl:param name="eventVar" required="yes" as="xs:string"/>

    <!-- we can define this here, because its only one ActualInMessageEvent possible -->
    <!-- senderId -->
    <xsl:variable name="senderId" select="'senderId'"/>
    <!-- for mapping from InMessageEvent to  ActualInMessageEvent -->
    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="type" select="'long'"/>
      <xsl:with-param name="name" select="$senderId"/>
      <xsl:with-param name="value">
        <!-- for mapping from InMessageEvent to  ActualInMessageEvent -->
        <xsl:call-template name="java:callGetterMethod">
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="'this'"/>
              <xsl:with-param name="varName" select="$eventVar"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="instVariable" select="'senderId'"/>
          <xsl:with-param name="inLine" select="true()"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

    <xsl:variable name="aimVarName" select="jw:lowerWord(@messageType)"/>
    <xsl:call-template name="java:newObject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="class" select="@messageType"/>
      <xsl:with-param name="varName" select="$aimVarName"/>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:call-template name="java:callGetterMethod">
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="'this'"/>
              <xsl:with-param name="varName" select="$eventVar"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="instVariable" select="'occurrenceTime'"/>
          <xsl:with-param name="inLine" select="true()"/>
        </xsl:call-template>
        <xsl:value-of select="$senderId"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="aorsl:OutMessageEventExpr" mode="createAgentRules.helper.method.resultingActionEvents">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="eventVar" required="yes" as="xs:string"/>
    <xsl:param name="eventList" required="yes" as="xs:string"/>
    <xsl:param name="agentVariable"/>

    <xsl:variable name="OutMessageEventExprNode" select="."/>
    <xsl:variable name="messageNode" select="//aorsl:MessageType[@name = current()/@messageType]"/>

    <xsl:choose>
      <xsl:when test="(@receiverIdRefs and not(@receiverIdRefs eq '')) or fn:exists(aorsl:ReceiverIdRef[@language = $output.language])">

        <xsl:choose>
          <xsl:when test="fn:exists($messageNode)">

            <xsl:variable name="output">
              <xsl:variable name="block-indent" select="if (fn:exists(aorsl:Condition[@language eq $output.language])) then $indent + 1 else $indent"/>

              <!-- create a new message -->
              <xsl:variable name="messageVarName" select="fn:concat(jw:lowerWord($messageNode/@name), '_', position())"/>

              <xsl:call-template name="java:newObject">
                <xsl:with-param name="indent" select="$block-indent"/>
                <xsl:with-param name="class" select="$messageNode/@name"/>
                <xsl:with-param name="varName" select="$messageVarName"/>
              </xsl:call-template>

              <xsl:for-each select="aorsl:Slot">
                <!-- <xsl:if test="@property = $messageNode/aorsl:Attribute/@name"> -->
                <xsl:call-template name="java:callSetterMethod">
                  <xsl:with-param name="indent" select="$block-indent"/>
                  <xsl:with-param name="objInstance" select="$messageVarName"/>
                  <xsl:with-param name="instVariable" select="@property"/>
                  <xsl:with-param name="value">
                    <xsl:apply-templates select="." mode="assistents.getSlotValue">
                      <xsl:with-param name="type" select="$messageNode/aorsl:Attribute[@name = current()/@property]/@type"/>
                    </xsl:apply-templates>
                  </xsl:with-param>
                </xsl:call-template>
                <!-- </xsl:if> -->
              </xsl:for-each>

              <!-- get the delay -->
              <xsl:variable name="delay">
                <xsl:apply-templates select="." mode="assistents.getDelay"/>
              </xsl:variable>

              <!-- create the OutMessage for each receiver-->
              <xsl:variable name="receivers1" as="xs:string*">
                <!-- necessary if someone use two spaces -->
                <xsl:for-each select="fn:tokenize(@receiverIdRefs, ' ')">
                  <xsl:if test=". != ''">
                    <xsl:value-of select="."/>
                  </xsl:if>
                </xsl:for-each>
              </xsl:variable>
              <xsl:variable name="receivers2" select="aorsl:ReceiverIdRef[@language = $output.language]/text()"/>
              <xsl:variable name="omMessageType" select="$core.class.outMessageEvent"/>
              <xsl:variable name="omVarName" select="fn:concat(jw:lowerWord($omMessageType), '_', position())"/>

              <xsl:call-template name="java:newObject">
                <xsl:with-param name="indent" select="$block-indent"/>
                <xsl:with-param name="class" select="$omMessageType"/>
                <xsl:with-param name="varName" select="$omVarName"/>
                <xsl:with-param name="withDeclaration" select="false()"/>
              </xsl:call-template>

              <xsl:for-each select="($receivers1, $receivers2)">

                <xsl:call-template name="java:variable">
                  <xsl:with-param name="indent" select="$block-indent"/>
                  <xsl:with-param name="name" select="$omVarName"/>
                  <xsl:with-param name="value">
                    <xsl:call-template name="java:newObject">
                      <xsl:with-param name="indent" select="$indent"/>
                      <xsl:with-param name="class" select="$omMessageType"/>
                      <xsl:with-param name="varName" select="$omVarName"/>
                      <xsl:with-param name="args" as="xs:string*">
                        <!-- occurenceTime -->
                        <xsl:variable name="param1" as="xs:string">
                          <xsl:call-template name="java:callGetterMethod">
                            <xsl:with-param name="objInstance">
                              <xsl:call-template name="java:varByDotNotation">
                                <xsl:with-param name="name" select="'this'"/>
                                <xsl:with-param name="varName" select="$eventVar"/>
                              </xsl:call-template>
                            </xsl:with-param>
                            <xsl:with-param name="instVariable" select="'occurrenceTime'"/>
                            <xsl:with-param name="inLine" select="true()"/>
                          </xsl:call-template>
                        </xsl:variable>
                        <!-- agentSubject.id as senderId(actorId)-->
                        <xsl:variable name="param3" as="xs:string">
                          <xsl:call-template name="java:callGetterMethod">
                            <xsl:with-param name="objInstance">
                              <xsl:choose>
                                <xsl:when test="not (fn:exists($agentVariable))">
                                  <xsl:call-template name="java:callGetterMethod">
                                    <xsl:with-param name="objInstance" select="'this'"/>
                                    <xsl:with-param name="instVariable" select="'agentSubject'"/>
                                    <xsl:with-param name="inLine" select="true()"/>
                                  </xsl:call-template>
                                </xsl:when>
                                <xsl:otherwise>
                                  <xsl:value-of select="$agentVariable"/>
                                </xsl:otherwise>
                              </xsl:choose>
                            </xsl:with-param>
                            <xsl:with-param name="instVariable" select="'id'"/>
                            <xsl:with-param name="inLine" select="true()"/>
                          </xsl:call-template>
                        </xsl:variable>
                        <!-- agentSubject as senderRef(actorRef) -->
                        <xsl:variable name="param4" as="xs:string">
                          <xsl:call-template name="java:callGetterMethod">
                            <xsl:with-param name="inLine" select="true()"/>
                            <xsl:with-param name="objInstance">
                              <xsl:choose>
                                <xsl:when test="not (fn:exists($agentVariable))">
                                  <xsl:call-template name="java:callGetterMethod">
                                    <xsl:with-param name="objInstance" select="'this'"/>
                                    <xsl:with-param name="instVariable" select="'agentSubject'"/>
                                    <xsl:with-param name="inLine" select="true()"/>
                                  </xsl:call-template>
                                </xsl:when>
                                <xsl:otherwise>
                                  <xsl:value-of select="$agentVariable"/>
                                </xsl:otherwise>
                              </xsl:choose>
                            </xsl:with-param>
                            <xsl:with-param name="instVariable" select="'AgentObject'"/>
                          </xsl:call-template>

                        </xsl:variable>

                        <!-- set the params -->
                        <!-- occurenceTime -->
                        <xsl:value-of select="fn:concat($param1, ' + ', $delay)"/>
                        <!-- receiver -->
                        <xsl:value-of select="."/>
                        <!-- senderId -->
                        <xsl:value-of select="$param3"/>
                        <!-- senderRef -->
                        <xsl:value-of select="$param4"/>
                        <!-- messageTypeObj -->
                        <xsl:value-of select="$messageVarName"/>
                      </xsl:with-param>
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="onlyInitialization" select="true()"/>
                    </xsl:call-template>

                  </xsl:with-param>
                </xsl:call-template>

                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="indent" select="$block-indent"/>
                  <xsl:with-param name="objInstance" select="$eventList"/>
                  <xsl:with-param name="method" select="'add'"/>
                  <xsl:with-param name="args" as="xs:string*" select="$omVarName"/>
                </xsl:call-template>
                <xsl:call-template name="java:newLine"/>
              </xsl:for-each>
            </xsl:variable>

            <xsl:apply-templates select="." mode="assistent.resultingEvent.output">
              <xsl:with-param name="indent" select="$indent"/>
              <xsl:with-param name="output" select="$output"/>
            </xsl:apply-templates>

          </xsl:when>
          <xsl:otherwise>
            <xsl:message>[ERROR] No associated Message found for <xsl:value-of select="local-name()"/>
              <xsl:text> </xsl:text>
              <xsl:value-of select="@messageType"/>
            </xsl:message>
          </xsl:otherwise>
        </xsl:choose>

      </xsl:when>
      <xsl:otherwise>
        <xsl:message>
          <xsl:text>[ERROR] No receiver defined for </xsl:text>
          <xsl:value-of select="local-name()"/>
          <xsl:text> in Rule </xsl:text>
          <xsl:value-of select="../../@name"/>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <xsl:template match="aorsl:ReminderEventExpr" mode="createAgentRules.helper.method.resultingInternalEvents">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="eventVar" required="yes" as="xs:string"/>
    <xsl:param name="eventList" required="yes" as="xs:string"/>

    <xsl:variable name="remEvtVarName" select="jw:createInternalVarName(fn:concat(jw:lowerWord($core.class.reminderEvent), '_', position()))"/>

    <xsl:variable name="output">
      <xsl:variable name="block-indent" select="if (fn:exists(aorsl:Condition[@language eq $output.language])) then $indent + 1 else $indent"/>

      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$block-indent"/>
        <xsl:with-param name="class" select="$core.class.reminderEvent"/>
        <xsl:with-param name="varName" select="$remEvtVarName"/>
        <xsl:with-param name="args" as="xs:string*">
          <xsl:variable name="delay">
            <xsl:apply-templates select="." mode="assistents.getDelay"/>
          </xsl:variable>
          <xsl:variable name="occurenceTime" as="xs:string">
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="varName" select="$eventVar"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="instVariable" select="'occurrenceTime'"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:variable>
          <xsl:variable name="reminderMsgVar">
            <xsl:choose>
              <xsl:when test="fn:exists(aorsl:ReminderMsg[@language eq $output.language])">
                <xsl:value-of select="aorsl:ReminderMsg[@language eq $output.language][1]"/>
              </xsl:when>
              <xsl:when test="@reminderMsg">
                <xsl:value-of select="jw:quote(@reminderMsg)"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="jw:quote($empty.string.quotation.symbol)"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>

          <xsl:value-of select="fn:concat($occurenceTime, ' + ', $delay)"/>
          <xsl:value-of select="$reminderMsgVar"/>

        </xsl:with-param>
      </xsl:call-template>

      <xsl:call-template name="java:callMethod">
        <xsl:with-param name="indent" select="$block-indent"/>
        <xsl:with-param name="objInstance" select="$eventList"/>
        <xsl:with-param name="method" select="'add'"/>
        <xsl:with-param name="args" as="xs:string*" select="$remEvtVarName"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:apply-templates select="." mode="assistent.resultingEvent.output">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="output" select="$output"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- create a classvariable for every belief entity -->
  <xsl:template match="aorsl:FOR" mode="createAgentRules.beliefEntities.classVariables">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:choose>
      <xsl:when test="fn:exists(@beliefEntityIdRef) or fn:exists(aorsl:BeliefEntityIdRef[@language = $output.language])">
        <!-- create belief entity based on the ID -->
        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="modifier" select="'private'"/>
          <xsl:with-param name="type" select="$core.class.entity"/>
          <xsl:with-param name="name" select="@beliefEntityVariable"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="fn:exists(aorsl:BeliefEntityType[@language = $output.language])">
        <!-- create belief entity variable based on type -->
        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'private'"/>
          <xsl:with-param name="type" select="$core.class.entity"/>
          <xsl:with-param name="name" select="@beliefEntityVariable"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="fn:exists(@beliefEntityType)">
        <!-- create belief entity variable based on type -->
        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'private'"/>
          <xsl:with-param name="type" select="@beliefEntityType"/>
          <xsl:with-param name="name" select="@beliefEntityVariable"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message>
          <xsl:text>[ERROR] in belief variable declaration in AgentRule [</xsl:text>
          <xsl:value-of select="../@name"/>
          <xsl:text>! One of @beliefEntityIdRef or @beliefEntityType is required</xsl:text>
          <xsl:text>]!</xsl:text>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <xsl:template match="aorsl:CreateDescription" mode="createAgentRules.description.classVariables">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:call-template name="java:newHashMap">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="varName" select="@descriptionVariable"/>
      <xsl:with-param name="keyType" select="'Long'"/>
      <xsl:with-param name="valueType" select="'HashMap&lt;String, Object&gt;'"/>
      <xsl:with-param name="modifier" select="'private'"/>
      <xsl:with-param name="withDeclaration" select="true()"/>
    </xsl:call-template>

  </xsl:template>

  <!-- declare belief entity variable -->
  <xsl:template match="aorsl:FOR[@beliefEntityVariable]" mode="createAgentRules.method.execute.helper.declareBeliefEntity">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="agentClassName" as="xs:string" required="yes"/>

    <!-- cannot have both the id and the type ... -->
    <xsl:if
      test="(fn:exists(@beliefEntityIdRef) and (fn:exists(aorsl:BeliefEntityType[@language = $output.language]) or fn:exists(@beliefEntityType))) 
      or (fn:exists(@beliefEntityType) and (fn:exists(aorsl:BeliefEntityIdRef[@language = $output.language]) or fn:exists(@beliefEntityIdRef)))">
      <xsl:message>
        <xsl:text>[ERROR] in variable instantiation in AgentRule [</xsl:text>
        <xsl:value-of select="../@name"/>
        <xsl:text>! Cannot use both ID an Type for variablle declaration </xsl:text>
        <xsl:text>]!</xsl:text>
      </xsl:message>
    </xsl:if>

    <xsl:choose>
      <xsl:when test="fn:exists(aorsl:BeliefEntityIdRef[@language = $output.language]) or fn:exists(@beliefEntityIdRef)">

        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="name">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="varName" select="@beliefEntityVariable"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="value">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="name" select="jw:upperWord(fn:concat(../../@name, $prefix.agentSubject))"/>
                  <xsl:with-param name="varName" select="'this'"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="method" select="'getBeliefEntityById'"/>
              <xsl:with-param name="args">
                <xsl:choose>
                  <xsl:when test="fn:exists(aorsl:BeliefEntityIdRef[@language = $output.language])">
                    <xsl:value-of select="aorsl:BeliefEntityIdRef[@language = $output.language][1]"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="@beliefEntityIdRef"/>
                  </xsl:otherwise>
                </xsl:choose>

              </xsl:with-param>

            </xsl:call-template>

          </xsl:with-param>
        </xsl:call-template>

        <!-- check the state of the variable -->
        <xsl:apply-templates select="." mode="createAgentRules.method.execute.helper.checkStateOfVariable">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="agentClassName" select="$agentClassName"/>
        </xsl:apply-templates>

      </xsl:when>
      <xsl:when test="fn:exists(aorsl:BeliefEntityType[@language = $output.language]) or fn:exists(@beliefEntityType)">

        <xsl:call-template name="java:newArrayListObject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="useInterfaceAsTypeName" select="true()"/>
          <xsl:with-param name="generic" select="$core.class.entity"/>
          <xsl:with-param name="name" select="fn:concat($createdVariablesNamePrefix, @beliefEntityVariable, 'List')"/>
          <xsl:with-param name="withDeclaration" select="false()"/>
        </xsl:call-template>

        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="name" select="fn:concat($createdVariablesNamePrefix, @beliefEntityVariable, 'List')"/>
          <xsl:with-param name="value">

            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="name" select="jw:upperWord(fn:concat(../../@name, $prefix.agentSubject))"/>
                  <xsl:with-param name="varName" select="'this'"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="method" select="'getBeliefEntities'"/>
              <xsl:with-param name="args">

                <xsl:choose>
                  <xsl:when test="fn:exists(aorsl:BeliefEntityType[@language = $output.language])">
                    <xsl:value-of select="aorsl:BeliefEntityType[@language = $output.language][1]"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="jw:quote(@beliefEntityType)"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>

            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:when>
      <xsl:otherwise>
        <xsl:message>
          <xsl:text>[ERROR] in variable instantiation in AgentRule [</xsl:text>
          <xsl:value-of select="../@name"/>
          <xsl:text>! No @beliefEntityIdRef, BeliefEntityIdRef, @beliefEntityType or BeliefEntityType defined for this belief entity</xsl:text>
          <xsl:text>]!</xsl:text>
        </xsl:message>
      </xsl:otherwise>

    </xsl:choose>

  </xsl:template>

  <xsl:template match="aorsl:FOR-ListItemVariable" mode="createAgentRules.method.execute.helper.declareFOR-List">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:call-template name="java:newArrayListObject">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="useInterfaceAsTypeName" select="true()"/>
      <xsl:with-param name="generic" select="@listItemType"/>
      <xsl:with-param name="name" select="fn:concat($createdVariablesNamePrefix, @variable, 'sList')"/>
    </xsl:call-template>

    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="name" select="fn:concat($createdVariablesNamePrefix, @variable, 'sList')"/>
      <xsl:with-param name="value">

        <xsl:choose>
          <xsl:when test="aorsl:ListExpr[@language eq $output.language]">
            <xsl:value-of select="aorsl:ListExpr[@language eq $output.language][1]"/>
          </xsl:when>
          <xsl:when test="@listValuedAgentProperty">
            <xsl:variable name="methodPrefix" select="jw:upperWord(@listValuedAgentProperty)"/>
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="name" select="jw:upperWord(fn:concat(../../@name, $prefix.agentSubject))"/>
                  <xsl:with-param name="varName" select="'this'"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="instVariable" select="if (fn:ends-with($methodPrefix,'s')) then $methodPrefix else fn:concat($methodPrefix, 's')"
              />
            </xsl:call-template>
          </xsl:when>
        </xsl:choose>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- check if there is the expected percepted physical object -->
  <xsl:template match="aorsl:WHEN" mode="createAgentRules.method.execute.helper.physicalObjectPerceptionEvent">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="eventVar" as="xs:string" required="yes"/>

    <xsl:variable name="physicalObjectType" as="xs:string">
      <xsl:choose>
        <xsl:when test="@physicalObjectType">
          <xsl:value-of select="@physicalObjectType"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$defaultPhysObjType"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:call-template name="java:if">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="condition">
        <xsl:text>!</xsl:text>
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="inLine" select="true()"/>
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="$physicalObjectType"/>
              <xsl:with-param name="varName" select="'class'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="method" select="'isInstance'"/>
          <xsl:with-param name="args">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance" select="$eventVar"/>
              <xsl:with-param name="method" select="'getPerceivedPhysicalObject'"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="thenContent">
        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 2"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="java:newLine"/>

  </xsl:template>

  <!-- check the state of the variable -->
  <xsl:template match="aorsl:FOR[@beliefEntityVariable]" mode="createAgentRules.method.execute.helper.checkStateOfVariable">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="agentClassName" as="xs:string" required="yes"/>

    <xsl:if test="fn:exists(aorsl:BeliefEntityIdRef[@language = $output.language]) or fn:exists(@beliefEntityIdRef)">
      <xsl:call-template name="java:if">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="condition">
          <xsl:call-template name="java:boolExpr">
            <xsl:with-param name="value1" select="@beliefEntityVariable"/>
            <xsl:with-param name="value2" select="'null'"/>
            <xsl:with-param name="operator" select="'=='"/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="thenContent">
          <xsl:call-template name="java:return">
            <xsl:with-param name="indent" select="$indent + 1"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>

  </xsl:template>


  <!-- check the permitted attribut combinations -->
  <xsl:template name="checkAttributes">
    <!-- if the eventType is an InMessageEvent it's a messageType required -->
    <xsl:if test="TriggeringAtomicEventExpr/@eventType = $core.class.inMessageEvent and not (fn:exists(@messageType))">
      <xsl:message terminate="yes">
        <xsl:text>No messageType for eventType </xsl:text>
        <xsl:value-of select="$core.class.inMessageEvent"/>
        <xsl:text> defined. </xsl:text>
        <xsl:text>AgentRule: </xsl:text>
        <xsl:value-of select="@name"/>
      </xsl:message>
    </xsl:if>

  </xsl:template>

  <xsl:template name="nestedLoops">
    <xsl:param name="set"/>
    <xsl:param name="iteratorPostfix"/>
    <xsl:param name="forEachLoopPostfix"/>
    <xsl:param name="listPostfix"/>
    <xsl:param name="indent"/>
    <xsl:param name="content"/>

    <xsl:variable name="forElement" select="$set[1]"/>
    <xsl:choose>
      <xsl:when test="$forElement">

        <xsl:variable name="nextIndent" select="$indent + 1"/>

        <xsl:choose>
          <!-- FOR for belief entities  -->
          <xsl:when test="local-name($forElement) eq 'FOR' and $forElement[@beliefEntityVariable]">

            <xsl:variable name="objVar" select="fn:concat($createdVariablesNamePrefix, $forElement/@beliefEntityVariable, $listPostfix)"/>

            <xsl:call-template name="java:for-each-loop">
              <xsl:with-param name="indent" select="$indent"/>
              <xsl:with-param name="elementType" select="$core.class.entity"/>
              <xsl:with-param name="elementVarName" select="fn:concat($forElement/@beliefEntityVariable, $forEachLoopPostfix)"/>
              <xsl:with-param name="listVarName" select="$objVar"/>
              <xsl:with-param name="content">

                <xsl:call-template name="java:variable">
                  <xsl:with-param name="indent" select="$nextIndent"/>
                  <xsl:with-param name="name">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="$forElement/@beliefEntityVariable"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="castType" select="if ($forElement/@beliefEntityType) then $forElement/@beliefEntityType else ''"/>
                  <xsl:with-param name="value" select="fn:concat($forElement/@beliefEntityVariable, $forEachLoopPostfix)"/>
                </xsl:call-template>

                <xsl:call-template name="nestedLoops">
                  <xsl:with-param name="forEachLoopPostfix" select="$forEachLoopPostfix"/>
                  <xsl:with-param name="iteratorPostfix" select="$iteratorPostfix"/>
                  <xsl:with-param name="content" select="$content"/>
                  <xsl:with-param name="listPostfix" select="$listPostfix"/>
                  <xsl:with-param name="indent" select="$nextIndent"/>
                  <xsl:with-param name="set" select="$set[generate-id()!=generate-id($forElement)]"/>
                </xsl:call-template>

              </xsl:with-param>
            </xsl:call-template>

          </xsl:when>

          <xsl:when test="local-name($forElement) eq 'FOR-ListItemVariable'">

            <xsl:variable name="objVar" select="fn:concat($createdVariablesNamePrefix, $forElement/@variable, 'sList')"/>

            <xsl:call-template name="java:for-each-loop">
              <xsl:with-param name="indent" select="$indent"/>
              <xsl:with-param name="elementType" select="$forElement/@listItemType"/>
              <xsl:with-param name="elementVarName" select="fn:concat($forElement/@variable, $forEachLoopPostfix)"/>
              <xsl:with-param name="listVarName" select="$objVar"/>
              <xsl:with-param name="content">

                <xsl:call-template name="java:variable">
                  <xsl:with-param name="indent" select="$nextIndent"/>
                  <xsl:with-param name="name">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="$forElement/@variable"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="value" select="fn:concat($forElement/@variable, $forEachLoopPostfix)"/>
                </xsl:call-template>

                <xsl:call-template name="nestedLoops">
                  <xsl:with-param name="forEachLoopPostfix" select="$forEachLoopPostfix"/>
                  <xsl:with-param name="iteratorPostfix" select="$iteratorPostfix"/>
                  <xsl:with-param name="content" select="$content"/>
                  <xsl:with-param name="listPostfix" select="$listPostfix"/>
                  <xsl:with-param name="indent" select="$nextIndent"/>
                  <xsl:with-param name="set" select="$set[generate-id()!=generate-id($forElement)]"/>
                </xsl:call-template>

              </xsl:with-param>
            </xsl:call-template>

          </xsl:when>

        </xsl:choose>

      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="$content"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
