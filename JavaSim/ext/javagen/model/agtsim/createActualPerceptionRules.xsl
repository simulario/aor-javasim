<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation creates classes for agent rules based on a given aorsml file.

      $Rev: 4871 $
      $Date: 2010-04-23 15:36:12 +0200 (Fri, 23 Apr 2010) $

      @author:   Jens Werner (jens.werner@tu-cottbus.de)
      @license:  GNU General Public License version 2 or higher
      @last changed by $Author: jewerner $
-->

<xsl:transform version="2.0" xmlns:aorsml="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:java="http://www.sun.com/java"
  xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:schemaLocation="http://aor-simulation.org aorsml.xsd" xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">

  <!--creates class-->
  <xsl:template match="aorsml:ActualPerceptionRule" mode="createActualPerceptionRules.createActualPerceptionRule">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="agentType" as="node()" required="yes"/>

    <xsl:variable name="apr" select="." as="node()"/>
    <xsl:comment>Every WHEN must have a perceptionEventType</xsl:comment>
    <xsl:for-each select="aorsml:WHEN">
      <xsl:if test="not (fn:exists(@perceptionEventType))">
        <xsl:message terminate="yes">ERROR: no perceptionEventType defined for a WHEN in ActualPerceptionRule: <xsl:value-of
            select="$apr/@name"/>!
        </xsl:message>
      </xsl:if>
    </xsl:for-each>

    <xsl:call-template name="java:class">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="@name"/>
      <xsl:with-param name="extends" select="if (fn:exists(@superType)) then @superType else $core.class.actualPerceptionRule"/>
      <xsl:with-param name="content">

        <!-- variables (are optional in schema) -->
        <xsl:variable name="eventVariable"
          select="if (exists(aorsml:WHEN/@eventVariable)) 
                         then aorsml:WHEN/@eventVariable 
                         else jw:lowerWord(aorsml:WHEN/@perceptionEventType)"/>
        <xsl:variable name="agentTypeClassName" select="fn:concat($agentType/@name, $prefix.agentSubject)"/>

        <!-- set triggering event as classvariable -->
        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="modifier" select="'private'"/>
          <xsl:with-param name="type" select="aorsml:WHEN/@perceptionEventType"/>
          <xsl:with-param name="name" select="$eventVariable"/>
        </xsl:call-template>
        <xsl:call-template name="java:newLine"/>

        <!-- the agentsubject himself - if necessary -->
        <xsl:if test="@agentVariable">
          <xsl:call-template name="java:variable">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="modifier" select="'private'"/>
            <xsl:with-param name="type" select="$agentTypeClassName"/>
            <xsl:with-param name="name" select="@agentVariable"/>
          </xsl:call-template>
          <xsl:call-template name="java:newLine"/>
        </xsl:if>

        <!--constructor -->
        <xsl:apply-templates select="." mode="createActualPerceptionRules.constructor">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVarName" select="$eventVariable"/>
          <xsl:with-param name="agentClassName" select="$agentTypeClassName"/>
        </xsl:apply-templates>

        <!-- condition() -->
        <xsl:apply-templates select="." mode="createActualPerceptionRules.method.conditions">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <!-- stateEffects() -->
        <xsl:apply-templates select="." mode="createActualPerceptionRules.method.stateEffects">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="agentClassName" select="$agentTypeClassName"/>
          <xsl:with-param name="agentVariable" select="@agentVariable"/>
        </xsl:apply-templates>

        <!-- create setter and getter for triggering event -->
        <xsl:apply-templates select="aorsml:WHEN" mode="createActualPerceptionRules.method.setGetTriggeringEvent">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVariable"/>
        </xsl:apply-templates>

        <!-- resultingInternalEvent() -->
        <xsl:apply-templates select="." mode="createActualPerceptionRules.method.resultingInternalEvent">
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
  <xsl:template match="aorsml:ActualPerceptionRule" mode="createActualPerceptionRules.constructor">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="eventVarName" required="yes" as="xs:string"/>
    <xsl:param name="agentClassName" required="yes" as="xs:string"/>

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
          <xsl:with-param name="class" select="aorsml:WHEN/@perceptionEventType"/>
          <xsl:with-param name="varName">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="'this'"/>
              <xsl:with-param name="varName" select="$eventVarName"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="isVariable" select="true()"/>
        </xsl:call-template>

        <xsl:if test="@agentVariable">
          <xsl:call-template name="java:variable">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="castType" select="$agentClassName"/>
            <xsl:with-param name="name">
              <xsl:call-template name="java:varByDotNotation">
                <xsl:with-param name="name" select="'this'"/>
                <xsl:with-param name="varName" select="@agentVariable"/>
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
        </xsl:if>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!--**************-->
  <!--   methods    -->
  <!--**************-->
  <!-- conditions() -->
  <xsl:template match="aorsml:ActualPerceptionRule" mode="createActualPerceptionRules.method.conditions">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'boolean'"/>
      <xsl:with-param name="name" select="'condition'"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">
            <xsl:choose>
              <xsl:when test="fn:exists(aorsml:IF[@language = $output.language]) and fn:normalize-space(aorsml:IF[@language = $output.language]) != ''">
                <xsl:value-of select="aorsml:IF[@language = $output.language]"/>
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

  <!-- stateEffects() -->
  <xsl:template match="aorsml:ActualPerceptionRule" mode="createActualPerceptionRules.method.stateEffects">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="agentClassName" required="yes" as="xs:string"/>
    <xsl:param name="agentVariable"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="'stateEffects'"/>
      <xsl:with-param name="content">
        <!-- is empty, because it is a mapping rule without statechanges -->
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- resultingInternalEvent() -->
  <xsl:template match="aorsml:ActualPerceptionRule" mode="createActualPerceptionRules.method.resultingInternalEvent">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="eventVar" required="yes" as="xs:string"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="$core.class.internalEvent"/>
      <xsl:with-param name="name" select="'resultingInternalEvent'"/>
      <xsl:with-param name="content">
        
        <xsl:call-template name="java:if">
          <xsl:with-param name="indent" select="$indent +1"/>
          <xsl:with-param name="condition">
            <xsl:call-template name="java:boolExpr">
              <xsl:with-param name="not" select="true()"/>
              <xsl:with-param name="value1">
                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="method" select="'condition'"/>
                </xsl:call-template>   
              </xsl:with-param>
            </xsl:call-template>       
          </xsl:with-param>
          <xsl:with-param name="thenContent">
            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent + 2"/>
              <xsl:with-param name="value" select="'null'"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>

        <!-- ActualPerceptionEvent -->
        <xsl:apply-templates select="aorsml:CREATE-EVT" mode="createActualPerceptionRules.helper.method.resultingInternalEvent">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVar"/>
        </xsl:apply-templates>

        <!-- or -->

        <!-- ActualInMessageEvent -->
        <xsl:apply-templates select="aorsml:ResultingActualInMsgEvtExpr" mode="createActualPerceptionRules.helper.method.resultingInternalEvent">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="eventVar" select="$eventVar"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- getter and setter for triggering event -->
  <!-- its possible to share it, but the returntype of the getmethod is differnt with envRules -->
  <xsl:template match="aorsml:WHEN" mode="createActualPerceptionRules.method.setGetTriggeringEvent">
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
          <xsl:with-param name="value" select="jw:quote(@eventType)"/>
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
          <xsl:with-param name="castType" select="@perceptionEventType"/>
          <xsl:with-param name="name">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="'this'"/>
              <xsl:with-param name="varName" select="$eventVar"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="value" select="jw:lowerWord($core.class.event)"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>


  <!--***************-->
  <!--     helpers      -->
  <!--***************-->
  <xsl:template match="aorsml:CREATE-EVT" mode="createActualPerceptionRules.helper.method.resultingInternalEvent">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="eventVar" required="yes" as="xs:string"/>

    <xsl:variable name="apVarName" select="jw:lowerWord(@actualPercEvtType)"/>
    <xsl:call-template name="java:newObject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="class" select="@actualPercEvtType"/>
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

    <xsl:for-each select="aorsml:Slot">
      <xsl:call-template name="java:callSetterMethod">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="objInstance" select="$apVarName"/>
        <xsl:with-param name="instVariable" select="@property"/>
        <xsl:with-param name="value">
          <xsl:apply-templates select="." mode="assistents.getSlotValue"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:for-each>

    <xsl:call-template name="java:return">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="value" select="$apVarName"/>
    </xsl:call-template>
  </xsl:template>


  <xsl:template match="aorsml:ResultingActualInMsgEvtExpr" mode="createActualPerceptionRules.helper.method.resultingInternalEvents">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
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
        <xsl:choose>
          <xsl:when test="fn:exists(@sender)">
            <xsl:value-of select="@sender"/>
          </xsl:when>
          <xsl:otherwise>
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
          </xsl:otherwise>
        </xsl:choose>
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

</xsl:transform>
