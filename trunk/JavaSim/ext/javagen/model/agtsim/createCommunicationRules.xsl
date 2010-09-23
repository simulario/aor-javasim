<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation creates classes for communication rules based on a given aorsml file.

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

  <!--creates class-->
  <xsl:template match="aorsl:CommunicationRule" mode="createCommunicationRules.createCommunicationRule">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="agentType" as="node()" required="yes"/>

    <xsl:apply-templates select="aorsl:WHEN" mode="createCommunicationRules.checkAttributes"/>

    <xsl:call-template name="java:class">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="@name"/>
      <xsl:with-param name="extends" select="if (fn:exists(@superType)) then @superType else $core.class.communicationRule"/>
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
        <xsl:apply-templates select="." mode="createCommunicationRules.constructor">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVarName" select="$eventVariable"/>
          <xsl:with-param name="agentClassName" select="$agentTypeClassName"/>
          <xsl:with-param name="agtVarName" select="$agtVarName"/>
        </xsl:apply-templates>

        <!-- condition() -->
        <xsl:apply-templates select="." mode="createCommunicationRules.method.conditions">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <!-- resultingInternalEvents() -->
        <xsl:apply-templates select="." mode="createCommunicationRules.method.resultingInternalEvents">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVariable"/>
          <xsl:with-param name="mode" select="aorsl:DO"/>
          <xsl:with-param name="prefix" select="'do'"/>
        </xsl:apply-templates>
        <xsl:apply-templates select="." mode="createCommunicationRules.method.resultingInternalEvents">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVariable"/>
          <xsl:with-param name="mode" select="aorsl:THEN"/>
          <xsl:with-param name="prefix" select="'then'"/>
        </xsl:apply-templates>
        <xsl:apply-templates select="." mode="createCommunicationRules.method.resultingInternalEvents">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVariable"/>
          <xsl:with-param name="mode" select="aorsl:ELSE"/>
          <xsl:with-param name="prefix" select="'else'"/>
        </xsl:apply-templates>

        <!-- stateEffects() -->
        <xsl:apply-templates select="." mode="createCommunicationRules.method.stateEffects">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="agentClassName" select="$agentTypeClassName"/>
          <xsl:with-param name="agentVariable" select="$agtVarName"/>
          <xsl:with-param name="mode" select="aorsl:DO"/>
          <xsl:with-param name="prefix" select="'do'"/>
        </xsl:apply-templates>
        <xsl:apply-templates select="." mode="createCommunicationRules.method.stateEffects">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="agentClassName" select="$agentTypeClassName"/>
          <xsl:with-param name="agentVariable" select="$agtVarName"/>
          <xsl:with-param name="mode" select="aorsl:THEN"/>
          <xsl:with-param name="prefix" select="'then'"/>
        </xsl:apply-templates>
        <xsl:apply-templates select="." mode="createCommunicationRules.method.stateEffects">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="agentClassName" select="$agentTypeClassName"/>
          <xsl:with-param name="agentVariable" select="$agtVarName"/>
          <xsl:with-param name="mode" select="aorsl:ELSE"/>
          <xsl:with-param name="prefix" select="'else'"/>
        </xsl:apply-templates>

        <!-- resultingActionEvents() -->
        <xsl:apply-templates select="." mode="createCommunicationRules.method.resultingOutMessageEvents">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVariable"/>
          <xsl:with-param name="agentVariable" select="$agtVarName"/>
          <xsl:with-param name="mode" select="aorsl:DO"/>
          <xsl:with-param name="prefix" select="'do'"/>
        </xsl:apply-templates>
        <xsl:apply-templates select="." mode="createCommunicationRules.method.resultingOutMessageEvents">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVariable"/>
          <xsl:with-param name="agentVariable" select="$agtVarName"/>
          <xsl:with-param name="agentType" select="local-name($agentType)"/>
          <xsl:with-param name="mode" select="aorsl:THEN"/>
          <xsl:with-param name="prefix" select="'then'"/>
        </xsl:apply-templates>
        <xsl:apply-templates select="." mode="createCommunicationRules.method.resultingOutMessageEvents">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVariable"/>
          <xsl:with-param name="agentVariable" select="$agtVarName"/>
          <xsl:with-param name="agentType" select="local-name($agentType)"/>
          <xsl:with-param name="mode" select="aorsl:ELSE"/>
          <xsl:with-param name="prefix" select="'else'"/>
        </xsl:apply-templates>

        <!-- execute() -->
        <xsl:apply-templates select="." mode="createCommunicationRules.method.execute">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVariable"/>
          <xsl:with-param name="agentClassName" select="$agentTypeClassName"/>
        </xsl:apply-templates>

        <!-- create setter and getter for triggering event -->
        <xsl:apply-templates select="aorsl:WHEN | aorsl:ON-EACH-SIMULATION-STEP" mode="createCommunicationRules.method.setGetTriggeringEvent">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVariable"/>
        </xsl:apply-templates>

        <!-- getMessageType() -->
        <xsl:apply-templates select="." mode="shared.method.getMessageType">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!--create constructor-->
  <xsl:template match="aorsl:CommunicationRule" mode="createCommunicationRules.constructor">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="eventVarName" required="yes" as="xs:string"/>
    <xsl:param name="agentClassName" required="yes" as="xs:string"/>
    <xsl:param name="agtVarName" required="yes" as="xs:string"/>

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


        <xsl:call-template name="java:newObject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="class">
            <xsl:choose>
              <xsl:when test="aorsl:WHEN/@eventType">
                <xsl:value-of select="aorsl:WHEN/@eventType"/>
              </xsl:when>
              <xsl:when test="aorsl:ON-EACH-SIMULATION-STEP">
                <xsl:value-of select="$core.package.onEveryStepIntEvent"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:message>
                  <xsl:text>[ERROR] - neither triggering event nor on-each-simulation-step defined for </xsl:text>
                  <xsl:value-of select="concat(local-name(), ' [', @name, ']!]')"/>
                </xsl:message>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
          <xsl:with-param name="varName">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="'this'"/>
              <xsl:with-param name="varName" select="$eventVarName"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="isVariable" select="true()"/>
        </xsl:call-template>

          <xsl:call-template name="java:variable">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="castType" select="$agentClassName"/>
            <xsl:with-param name="name">
              <xsl:call-template name="java:varByDotNotation">
                <xsl:with-param name="varName" select="$agtVarName"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="value">
              <xsl:call-template name="java:callGetterMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="objInstance" select="'this'"/>
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
  <xsl:template match="aorsl:CommunicationRule" mode="createCommunicationRules.method.conditions">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="annotation" select="'@SuppressWarnings(&quot;unchecked&quot;)'"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'boolean'"/>
      <xsl:with-param name="name" select="'condition'"/>
      <xsl:with-param name="content">

        <!-- the conditional IF part -->
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

  <!-- resultingOutMessageEvents -->
  <xsl:template match="aorsl:CommunicationRule" mode="createCommunicationRules.method.resultingOutMessageEvents">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="eventVar" required="yes" as="xs:string"/>
    <xsl:param name="agentVariable"/>
    <xsl:param name="mode" as="element()?"/>
    <xsl:param name="prefix" as="xs:string" required="yes"/>

    <xsl:variable name="methodName" select="fn:concat($prefix,jw:upperWord('resultingActionEvents'))"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'ArrayList'"/>
      <xsl:with-param name="genericType" select="$core.class.outMessageEvent"/>
      <xsl:with-param name="name" select="$methodName"/>
      <xsl:with-param name="content">

        <xsl:variable name="resultVar" select="'outMessageEvents'"/>
        <xsl:call-template name="java:newArrayListObject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="generic" select="$core.class.outMessageEvent"/>
          <xsl:with-param name="name" select="$resultVar"/>
        </xsl:call-template>

        <!-- OutMessageEvents -->
        <!-- use the template from createAgentRule, because it is the same -->
        <xsl:apply-templates select="$mode/aorsl:SCHEDULE-EVT/aorsl:OutMessageEventExpr" mode="createAgentRules.helper.method.resultingActionEvents">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVar"/>
          <xsl:with-param name="agentVariable" select="$agentVariable"/>
          <xsl:with-param name="eventList" select="$resultVar"/>
        </xsl:apply-templates>

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value" select="$resultVar"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- execute() -->
  <xsl:template match="aorsl:CommunicationRule" mode="createCommunicationRules.method.execute">
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
                    <xsl:with-param name="objInstance">
                      <xsl:call-template name="java:varByDotNotation">
                        <xsl:with-param name="varName" select="$eventVar"/>
                      </xsl:call-template>
                    </xsl:with-param>
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

            <!-- create the DO part -->
            <xsl:call-template name="ruleExecuteDoIfThenElse">
              <xsl:with-param name="indent" select="$newIndent"/>
              <xsl:with-param name="methodsPrefix" select="'do'"/>
              <xsl:with-param name="scheduleInternaleEvents"
                select="fn:exists(aorsl:DO/aorsl:SCHEDULE-EVT/aorsl:ActionEventExpr) or 
                fn:exists(aorsl:DO/aorsl:SCHEDULE-EVT/aorsl:ActualInMessageEventExpr) or
                fn:exists(aorsl:DO/aorsl:SCHEDULE-EVT/aorsl:ReminderEventExpr)"/>
              <xsl:with-param name="scheduleActionEvents" select="fn:exists(aorsl:DO/aorsl:SCHEDULE-EVT/aorsl:OutMessageEventExpr)"/>
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
                    select="fn:exists(aorsl:THEN/aorsl:SCHEDULE-EVT/aorsl:ActionEventExpr) or 
                    fn:exists(aorsl:THEN/aorsl:SCHEDULE-EVT/aorsl:ActualInMessageEventExpr) or
                    fn:exists(aorsl:THEN/aorsl:SCHEDULE-EVT/aorsl:ReminderEventExpr)"/>
                  <xsl:with-param name="scheduleActionEvents" select="fn:exists(aorsl:THEN/aorsl:SCHEDULE-EVT/aorsl:OutMessageEventExpr)"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="elseContent">
                <xsl:call-template name="ruleExecuteDoIfThenElse">
                  <xsl:with-param name="indent" select="$newIndent + 1"/>
                  <xsl:with-param name="methodsPrefix" select="'else'"/>
                  <xsl:with-param name="scheduleInternaleEvents"
                    select="fn:exists(aorsl:ELSE/aorsl:SCHEDULE-EVT/aorsl:ActionEventExpr) or 
                    fn:exists(aorsl:ELSE/aorsl:SCHEDULE-EVT/aorsl:ActualInMessageEventExpr) or
                    fn:exists(aorsl:ELSE/aorsl:SCHEDULE-EVT/aorsl:ReminderEventExpr)"/>
                  <xsl:with-param name="scheduleActionEvents" select="fn:exists(aorsl:ELSE/aorsl:SCHEDULE-EVT/aorsl:OutMessageEventExpr)"/>

                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>

          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>


  <!-- getter and setter for triggering event -->
  <!-- its possible to share it, but the returntype of the getmethod is differnt with envRules -->
  <xsl:template match="aorsl:WHEN | aorsl:ON-EACH-SIMULATION-STEP" mode="createCommunicationRules.method.setGetTriggeringEvent">
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
          <xsl:with-param name="value" select="jw:quote(if (@eventType) then @eventType else $core.class.onEveryStepIntEvent)"/>
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
          <xsl:with-param name="castType" select="if (@eventType) then @eventType else $core.package.onEveryStepIntEvent"/>
          <xsl:with-param name="name">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="'this'"/>
              <xsl:with-param name="varName" select="$eventVar"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="value" select="jw:lowerWord($core.class.event)"/>
        </xsl:call-template>

        <!-- @messageVariable -->
        <xsl:if test="@messageVariable and @messageType">
          <xsl:call-template name="java:variable">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="name">
              <xsl:call-template name="java:varByDotNotation">
                <xsl:with-param name="varName" select="@messageVariable"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="value">
              <xsl:call-template name="java:callGetterMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="castType" select="@messageType"/>
                <xsl:with-param name="objInstance" select="$eventVar"/>
                <xsl:with-param name="instVariable" select="'message'"/>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:if>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- empty methods (from AgentRules) -->
  <!-- stateEffects() -->
  <xsl:template match="aorsl:CommunicationRule" mode="createCommunicationRules.method.stateEffects">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="agentVariable"/>
    <xsl:param name="agentClassName" required="yes" as="xs:string"/>
    <xsl:param name="mode" as="element()?"/>
    <xsl:param name="prefix" as="xs:string" select="''"/>

    <xsl:variable name="methodName">
      <xsl:choose>
        <xsl:when test="$prefix != ''">
          <xsl:value-of select="fn:concat($prefix,jw:upperWord('stateEffects'))"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'stateEffects'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>


    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="$methodName"/>
      <xsl:with-param name="content">

        <xsl:if test="fn:exists($mode)">

          <!-- instantiate the create description variable -->
          <xsl:variable name="theAgtSubjVar" select="fn:concat('((',$agentClassName,')this.getAgentSubject())')"/>
          <xsl:for-each select="$mode/aorsl:SCHEDULE-EVT/aorsl:CreateDescription">
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
                            <xsl:with-param name="objInstance" select="$theAgtSubjVar"/>
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
          </xsl:for-each>

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

          <xsl:comment>if we have defined an @agentVariable then we can use the created classVariable (with the assoziated AgentSubjectClass) from
            AgentRule, otherwise we use the AgentSubjectClass localy </xsl:comment>
          <xsl:if test="fn:exists($mode/aorsl:UPDATE-AGT/aorsl:SelfBeliefSlot) and not ($agentVariable)">
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
                  <xsl:with-param name="objInstance" select="'this'"/>
                  <xsl:with-param name="instVariable" select="'AgentSubject'"/>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:if>

          <xsl:apply-templates select="$mode/aorsl:UPDATE-AGT/aorsl:UpdateComplexDataPropertyValue"
            mode="createCommunicationRules.method.stateEffects.updateComplexDataPropertyValue">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="agentVariable" select="$agentVarName"/>
          </xsl:apply-templates>

          <!--sets state effects-->
          <xsl:for-each select="$mode/aorsl:UPDATE-AGT/aorsl:SelfBeliefSlot">

            <xsl:call-template name="java:callSetterMethod">
              <xsl:with-param name="indent" select="$indent +  1"/>
              <xsl:with-param name="objInstance">
                <xsl:choose>
                  <xsl:when test="$agentVariable">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="name" select="'this'"/>
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
                      <xsl:when test="fn:exists(@value) and @value!=''">
                        <xsl:value-of select="@value"/>
                      </xsl:when>
                      <xsl:when
                        test="fn:exists(aorsl:ValueExpr[@language = $output.language]) 
                        and fn:normalize-space(aorsl:ValueExpr[@language = $output.language]) != ''">
                        <xsl:value-of select="aorsl:ValueExpr[@language = $output.language]"/>
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
                <xsl:when test="fn:exists(@beliefEntityType) and @beliefEntityType!=''">
                  <xsl:call-template name="java:callMethod">
                    <xsl:with-param name="method" select="fn:concat('create',@beliefEntityType)"/>
                    <xsl:with-param name="inLine" select="true()"/>
                    <xsl:with-param name="objInstance">
                      <xsl:value-of select="$subjectVar"/>
                    </xsl:with-param>
                    <xsl:with-param name="args" select="$beliefIdVar"/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:when
                  test="fn:exists(aorsl:BeliefEntityType[@language = $output.language]) 
                and fn:normalize-space(aorsl:BeliefEntityType[@language = $output.language])!=''">
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

        </xsl:if>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- resultingInternalEvents() -->
  <xsl:template match="aorsl:CommunicationRule" mode="createCommunicationRules.method.resultingInternalEvents">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="eventVar" required="yes" as="xs:string"/>
    <xsl:param name="mode" as="element()?"/>
    <xsl:param name="prefix" as="xs:string" required="yes"/>

    <xsl:variable name="methodName" select="fn:concat($prefix,jw:upperWord('resultingInternalEvents'))"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'ArrayList'"/>
      <xsl:with-param name="genericType" select="$core.class.reminderEvent"/>
      <xsl:with-param name="name" select="$methodName"/>
      <xsl:with-param name="content">

        <xsl:variable name="resultVar" select="'internalEvents'"/>
        <xsl:call-template name="java:newArrayListObject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="generic" select="$core.class.reminderEvent"/>
          <xsl:with-param name="name" select="$resultVar"/>
        </xsl:call-template>

        <xsl:if test="fn:exists($mode)">

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

  <!-- UpdateComplexDataPropertyValue -->
  <xsl:template match="aorsl:UpdateComplexDataPropertyValue" mode="createCommunicationRules.method.stateEffects.updateComplexDataPropertyValue">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="agentVariable" as="xs:string" required="yes"/>

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


  <!--***************-->
  <!--     helpers      -->
  <!--***************-->
  <!-- check the permitted attribut combinations -->
  <xsl:template match="aorsl:WHEN" mode="createAgentRules.checkAttributes">

    <!-- if the eventType is an InMessageEvent it's a messageType required -->
    <xsl:if test="@eventType = $core.class.inMessageEvent and not (fn:exists(@messageType))">
      <xsl:message terminate="yes">
        <xsl:text>No messageType for eventType </xsl:text>
        <xsl:value-of select="$core.class.inMessageEvent"/>
        <xsl:text> defined. </xsl:text>
        <xsl:text>AgentRule: </xsl:text>
        <xsl:value-of select="../../aorsl:ReactionRule/@name"/>
      </xsl:message>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
