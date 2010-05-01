<?xml version="1.0" encoding="UTF-8"?>

<!--
  This transformation creates classes for activities based on a given aorsml file.
  
  $Rev$
  $Date$
  
  @author:   Jens Werner (jens.werner@tu-cottbus.de)
  @license:
  @last changed by $Author$
-->

<xsl:stylesheet version="2.0" xmlns:aorsml="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsi:schemaLocation="http://aor-simulation.org aorsml.xsd"
  xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">

  <xsl:template match="aorsml:ActivityType" mode="createActivties.createActivity">
    <xsl:param name="indent" select="0" as="xs:integer"/>

    <xsl:variable name="className" select="jw:upperWord(@name)"/>

    <xsl:call-template name="aorsml:classFile">
      <xsl:with-param name="path" select="$sim.path.model.envsimulator"/>
      <xsl:with-param name="name" select="$className"/>

      <xsl:with-param name="content">

        <xsl:call-template name="java:imports">
          <xsl:with-param name="importList" as="xs:string*">
            <xsl:value-of select="fn:concat($core.package.model.envEvent, '.*')"/>
            <xsl:value-of select="fn:concat($core.package.model.envEvent.activity, '.*')"/>
            <xsl:if
              test="(@startEventType or @endEventType and
                                (@startEventType != $core.class.activityStartEvent) and 
                                (@endEventType != $core.class.activityEndEvent)) or 
                                fn:exists(aorsml:SuccessorActivityType[@activityName != $core.class.activityStartEvent])">
              <xsl:value-of select="fn:concat($sim.package.model.envevent, '.*')"/>
            </xsl:if>
            <xsl:value-of select="fn:concat($core.package.model.envSim, '.*')"/>
            <xsl:call-template name="setDefaultJavaImports"/>

            <xsl:if
              test="fn:exists(aorsml:FOR[@objectVariable][@objectType = 'Collection']) or 
                    fn:exists(aorsml:ActivityStartEffect/aorsml:AddObjectToCollection) or
                    fn:exists(aorsml:ActivityEndEffect/aorsml:AddObjectToCollection)">
              <xsl:value-of select="$collection.package.aORCollection"/>
            </xsl:if>

          </xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="aorsml:class">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="name" select="$className"/>
          <xsl:with-param name="extends" select="$activity.class.abstractActivity"/>
          <xsl:with-param name="content">

            <xsl:variable name="startEventVarName" select="if (@startEventVariable) then @startEventVariable else '__startEvent'"/>
            <xsl:variable name="endEventVariable" select="if (@endEventVariable) then @endEventVariable else '__endEvent'"/>

            <!-- set startEvent as classvariable -->
            <xsl:if test="@startEventType">
              <xsl:call-template name="java:variable">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="modifier" select="'private'"/>
                <xsl:with-param name="type" select="jw:upperWord(@startEventType)"/>
                <xsl:with-param name="name" select="$startEventVarName"/>
              </xsl:call-template>
              <xsl:call-template name="java:newLine"/>
            </xsl:if>

            <!-- set endEvent as classVariable (NOTICE: the type is not clear because there are a list of possible EndEventTypes)-->
            <xsl:if test="@endEventType">
              <xsl:call-template name="java:variable">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="modifier" select="'private'"/>
                <xsl:with-param name="type" select="if  (@endEventType) then $core.class.environmentEvent else $core.class.activityEndEvent"/>
                <xsl:with-param name="name" select="$endEventVariable"/>
              </xsl:call-template>
              <xsl:call-template name="java:newLine"/>
            </xsl:if>

            <!-- set involved entitys as classvariable -->
            <xsl:for-each select="aorsml:FOR[@objectVariable]">

              <xsl:variable name="objectType">
                <xsl:apply-templates select="." mode="assistents.getVariableType"/>
              </xsl:variable>
              <xsl:if test="$objectType != ''">
                <xsl:call-template name="java:variable">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="modifier" select="'private'"/>
                  <xsl:with-param name="type" select="$objectType"/>
                  <xsl:with-param name="name" select="@objectVariable"/>
                </xsl:call-template>
              </xsl:if>
            </xsl:for-each>

            <!-- set the DataVariableDeclaration as classvaraibles -->
            <xsl:apply-templates select="aorsml:FOR[@dataVariable]" mode="assistents.setDataVariableDeclarationClassVariables">
              <xsl:with-param name="indent" select="$indent"/>
            </xsl:apply-templates>

            <!-- set the ressourcereferences as classVariable -->
            <xsl:for-each select="aorsml:ResourceIdRef[@resourceVariable] | aorsml:ResourceRef[@resourceVariable]">
              <xsl:if test="@type">
                <xsl:call-template name="java:variable">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="modifier" select="'private'"/>
                  <xsl:with-param name="type" select="@type"/>
                  <xsl:with-param name="name" select="@resourceVariable"/>
                </xsl:call-template>
              </xsl:if>
            </xsl:for-each>

            <!-- set the actor as a classvariable -->
            <xsl:if test="@actorVariable and @actorType">
              <xsl:call-template name="java:variable">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="modifier" select="'private'"/>
                <xsl:with-param name="type" select="@actorType"/>
                <xsl:with-param name="name" select="@actorVariable"/>
              </xsl:call-template>
            </xsl:if>
            <xsl:call-template name="java:newLine"/>

            <!-- constructor -->
            <xsl:apply-templates select="." mode="createActivties.constructor">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="className" select="$className"/>
            </xsl:apply-templates>

            <!-- setters -->
            <xsl:for-each select="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty | aorsml:EnumerationProperty">
              <xsl:apply-templates select="." mode="assistents.setVariableMethod">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="changeCheck" select="true()"/>
              </xsl:apply-templates>
            </xsl:for-each>

            <!-- getters -->
            <xsl:apply-templates select="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty | aorsml:EnumerationProperty"
              mode="assistents.getVariableMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- getActivityStartEventSimpleName() -->
            <xsl:apply-templates select="." mode="createActivties.method.getActivityStartEventSimpleName">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- getActivityEndEventSimpleNameList() -->
            <xsl:apply-templates select="." mode="createActivties.method.getActivityEndEventSimpleNameList">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- executeStartEffects -->
            <xsl:apply-templates select="." mode="createActivties.method.executeStartEffects">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- executeEndEffects -->
            <xsl:apply-templates select="." mode="createActivties.method.executeEndEffects">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="endEventVarName" select="$endEventVariable"/>
            </xsl:apply-templates>

            <!-- getActivityEndEvent() -->
            <xsl:apply-templates select="." mode="createActivties.method.getActivityEndEvent">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- getSuccessorActivityStartEvents() -->
            <xsl:apply-templates select="." mode="createActivties.method.getSuccessorActivityStartEvents">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- setCurrentEndEvent() -->
            <xsl:apply-templates select="." mode="createActivties.method.setCurrentEndEvent">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="endEventVarName" select="$endEventVariable"/>
            </xsl:apply-templates>

            <!-- setStartEvent() -->
            <xsl:apply-templates select="." mode="createActivties.method.setStartEvent">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="startEventVarName" select="$startEventVarName"/>
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

  <!-- constructor -->
  <xsl:template match="aorsml:ActivityType" mode="createActivties.constructor">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="className" required="yes" as="xs:string"/>

    <xsl:variable name="envSimName" select="jw:lowerWord($core.class.environmentSimulator)"/>
    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="annotation">
        <xsl:if
          test="$suppressWarnings and 
                    (fn:exists(aorsml:FOR[@objectVariable][@objectType = 'Collection']) or 
                    fn:exists(aorsml:ActivityStartEffect/aorsml:AddObjectToCollection) or
                    fn:exists(aorsml:ActivityEndEffect/aorsml:AddObjectToCollection))">
          <xsl:call-template name="getAnnotationSuppressWarnings.unchecked"/>
        </xsl:if>
      </xsl:with-param>
      <xsl:with-param name="name" select="$className"/>
      <xsl:with-param name="parameters">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="$core.class.environmentSimulator"/>
          <xsl:with-param name="name" select="$envSimName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string*" select="(jw:quote(@name), $envSimName)"/>
        </xsl:call-template>

        <!-- set the correlation property names -->
        <xsl:if test="@startEventCorrelationProperty">
          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="instVariable" select="'startEventCorrelationProperty'"/>
            <xsl:with-param name="value" select="jw:quote(@startEventCorrelationProperty)"/>
          </xsl:call-template>
        </xsl:if>
        <xsl:if test="@endEventCorrelationProperty">
          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="instVariable" select="'endEventCorrelationProperty'"/>
            <xsl:with-param name="value" select="jw:quote(@endEventCorrelationProperty)"/>
          </xsl:call-template>
        </xsl:if>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- getActivityStartEventSimpleName() -->
  <xsl:template match="aorsml:ActivityType" mode="createActivties.method.getActivityStartEventSimpleName">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'String'"/>
      <xsl:with-param name="name" select="'getActivityStartEventSimpleName'"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">
            <xsl:choose>
              <xsl:when test="@startEventType">
                <xsl:value-of select="jw:quote(@startEventType)"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="jw:quote($core.class.activityStartEvent)"/>
              </xsl:otherwise>
            </xsl:choose>

          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>


  <!-- getActivityEndEventSimpleNameList() -->
  <xsl:template match="aorsml:ActivityType" mode="createActivties.method.getActivityEndEventSimpleNameList">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'List'"/>
      <xsl:with-param name="genericType" select="'String'"/>
      <xsl:with-param name="name" select="'getActivityEndEventSimpleNameList'"/>
      <xsl:with-param name="content">

        <xsl:variable name="resultListVarName" select="'result'"/>
        <xsl:call-template name="java:newArrayListObject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="generic" select="'String'"/>
          <xsl:with-param name="name" select="$resultListVarName"/>
        </xsl:call-template>

        <xsl:for-each select="fn:tokenize(@endEventType, ' ')">
          <xsl:if test=". != ''">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="objInstance" select="$resultListVarName"/>
              <xsl:with-param name="method" select="'add'"/>
              <xsl:with-param name="args" select="jw:quote(.)"/>
            </xsl:call-template>
          </xsl:if>
        </xsl:for-each>

        <xsl:if test="fn:exists(aorsml:Duration) or @duration">
          <xsl:call-template name="java:callMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="objInstance" select="$resultListVarName"/>
            <xsl:with-param name="method" select="'add'"/>
            <xsl:with-param name="args" select="jw:quote($core.class.activityEndEvent)"/>
          </xsl:call-template>
        </xsl:if>

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value" select="$resultListVarName"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>


  <!-- executeStartEffects() -->
  <xsl:template match="aorsml:ActivityType" mode="createActivties.method.executeStartEffects">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="'executeStartEffects'"/>
      <xsl:with-param name="content">

        <!-- set the actor if there is  -->
        <xsl:if test="fn:exists(@actorIdRef) or fn:exists(aorsml:ActorIdRef) or fn:exists(aorsml:ActorRef)">
          <xsl:call-template name="java:callMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="method" select="'setActor'"/>
            <xsl:with-param name="args" as="xs:string*">
              <xsl:choose>
                <!-- this.setActor(aorsml:ActorRef); -->
                <xsl:when test="fn:exists(aorsml:ActorRef[@language eq $output.language])">
                  <xsl:value-of select="aorsml:ActorRef[@language eq $output.language][1]"/>
                </xsl:when>
                <xsl:otherwise>
                  <!-- this.setActor(this.getEnvironmentSimulator().getActivityActorById(ActorIdRef or @actorIdRef)); -->
                  <xsl:call-template name="java:callMethod">
                    <xsl:with-param name="inLine" select="true()"/>
                    <xsl:with-param name="objInstance">
                      <xsl:call-template name="java:callGetterMethod">
                        <xsl:with-param name="inLine" select="true()"/>
                        <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
                      </xsl:call-template>
                    </xsl:with-param>
                    <xsl:with-param name="method" select="'getActivityActorById'"/>
                    <xsl:with-param name="args">
                      <xsl:choose>
                        <xsl:when test="fn:exists(aorsml:ActorIdRef[@language eq $output.language])">
                          <xsl:value-of select="aorsml:ActorIdRef[@language eq $output.language]"/>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:value-of select="@actorIdRef"/>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:with-param>
                  </xsl:call-template>
                </xsl:otherwise>
              </xsl:choose>
              <xsl:call-template name="java:callGetterMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="instVariable" select="'startTime'"/>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:if>

        <!-- set the actorVariable if exist -->
        <xsl:if test="@actorVariable and @actorType">

          <xsl:call-template name="java:if">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="condition">
              <xsl:call-template name="java:boolExpr">
                <xsl:with-param name="value1">
                  <xsl:call-template name="java:boolExpr">
                    <xsl:with-param name="value1">
                      <xsl:call-template name="java:callGetterMethod">
                        <xsl:with-param name="inLine" select="true()"/>
                        <xsl:with-param name="instVariable" select="'actor'"/>
                      </xsl:call-template>
                    </xsl:with-param>
                    <xsl:with-param name="value2" select="'null'"/>
                    <xsl:with-param name="operator" select="'!='"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="value2">
                  <xsl:call-template name="java:callMethod">
                    <xsl:with-param name="inLine" select="true()"/>
                    <xsl:with-param name="objInstance">
                      <xsl:call-template name="java:varByDotNotation">
                        <xsl:with-param name="name" select="@actorType"/>
                        <xsl:with-param name="varName" select="'class'"/>
                      </xsl:call-template>
                    </xsl:with-param>
                    <xsl:with-param name="method" select="'isInstance'"/>
                    <xsl:with-param name="args">
                      <xsl:call-template name="java:callGetterMethod">
                        <xsl:with-param name="inLine" select="true()"/>
                        <xsl:with-param name="instVariable" select="'actor'"/>
                      </xsl:call-template>
                    </xsl:with-param>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="operator" select="'&amp;&amp;'"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="thenContent">

              <xsl:call-template name="java:variable">
                <xsl:with-param name="indent" select="$indent + 2"/>
                <xsl:with-param name="castType" select="@actorType"/>
                <xsl:with-param name="name">
                  <xsl:call-template name="java:varByDotNotation">
                    <xsl:with-param name="varName" select="@actorVariable"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="value">
                  <xsl:call-template name="java:callGetterMethod">
                    <xsl:with-param name="inLine" select="true()"/>
                    <xsl:with-param name="instVariable" select="'actor'"/>
                  </xsl:call-template>
                </xsl:with-param>
              </xsl:call-template>

            </xsl:with-param>
          </xsl:call-template>
        </xsl:if>

        <xsl:apply-templates select="." mode="createActivties.setVariables">
          <xsl:with-param name="indent" select="$indent"/>
        </xsl:apply-templates>

        <!-- *********** Resources **************** -->
        <!-- set the resources -->
        <xsl:for-each select="aorsml:ResourceIdRef[@language eq $output.language]">

          <xsl:variable name="resourceType" as="xs:string">
            <xsl:choose>
              <xsl:when test="@resourceVariable and @type">
                <xsl:value-of select="@type"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="$core.class.object"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>

          <xsl:variable name="resourceVarName" as="xs:string">
            <xsl:choose>
              <xsl:when test="@resourceVariable and @type">
                <xsl:value-of select="@resourceVariable"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="fn:concat(jw:lowerWord($core.class.object), '_', position())"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>

          <!-- if @resourceVariable and @type, then we have this objekt as a classvariable-->
          <xsl:if test="not (@resourceVariable and @type)">

            <xsl:call-template name="java:newObject">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="class" select="$resourceType"/>
              <xsl:with-param name="varName" select="$resourceVarName"/>
              <xsl:with-param name="withDeclaration" select="false()"/>
            </xsl:call-template>

          </xsl:if>

          <xsl:call-template name="java:variable">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="castType">
              <xsl:if test="@resourceVariable and @type">
                <xsl:value-of select="@type"/>
              </xsl:if>
            </xsl:with-param>
            <xsl:with-param name="name">
              <xsl:choose>
                <xsl:when test="@resourceVariable and @type">
                  <xsl:call-template name="java:varByDotNotation">
                    <xsl:with-param name="varName" select="$resourceVarName"/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="$resourceVarName"/>
                </xsl:otherwise>
              </xsl:choose>

            </xsl:with-param>
            <xsl:with-param name="value">

              <xsl:call-template name="java:callMethod">
                <xsl:with-param name="indent" select="$indent"/>
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="objInstance">

                  <xsl:call-template name="java:callGetterMethod">
                    <xsl:with-param name="indent" select="$indent"/>
                    <xsl:with-param name="inLine" select="true()"/>
                    <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
                  </xsl:call-template>

                </xsl:with-param>
                <xsl:with-param name="method" select="'getObjectById'"/>
                <xsl:with-param name="args">
                  <xsl:value-of select="."/>
                </xsl:with-param>
              </xsl:call-template>

            </xsl:with-param>
          </xsl:call-template>

          <xsl:call-template name="java:callMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="method" select="'addResource'"/>
            <xsl:with-param name="args" as="xs:string*">
              <xsl:value-of select="$resourceVarName"/>
              <xsl:call-template name="java:callGetterMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="instVariable" select="'startTime'"/>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>
          <xsl:call-template name="java:newLine"/>

        </xsl:for-each>

        <xsl:for-each select="aorsml:ResourceRef[@language eq $output.language]">

          <xsl:if test="@resourceVariable and @type">

            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="name">
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="varName" select="@resourceVariable"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="value">
                <xsl:value-of select="."/>
              </xsl:with-param>
            </xsl:call-template>

          </xsl:if>

          <xsl:call-template name="java:callMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="method" select="'addResource'"/>
            <xsl:with-param name="args" as="xs:string*">

              <xsl:choose>
                <xsl:when test="@resourceVariable and @type">
                  <xsl:value-of select="@resourceVariable"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="."/>
                </xsl:otherwise>
              </xsl:choose>
              <xsl:call-template name="java:callGetterMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="instVariable" select="'startTime'"/>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>

        </xsl:for-each>
        <!-- ~~~~~~~~~~~ resourceS ~~~~~~~~~~~~~~~ -->

        <xsl:apply-templates
          select="aorsml:ActivityStartEffect/aorsml:UpdateObject | 
                  aorsml:ActivityStartEffect/aorsml:UpdateStatisticsVariable |
                  aorsml:ActivityStartEffect/aorsml:UpdateGridCell | 
                  aorsml:ActivityStartEffect/aorsml:UpdateGridCells | 
                  aorsml:ActivityStartEffect/aorsml:AddObjectToCollection | 
                  aorsml:ActivityStartEffect/aorsml:UpdateActor |
                  aorsml:UPDATE-ENV/aorsml:IncrementGlobalVariable |
                  aorsml:UPDATE-ENV/aorsml:UpdateGlobalVariable"
          mode="createActivties.method.executeEffects">
          <xsl:with-param name="indent" select="$indent"/>
          <!-- spaceReservationSystem works only in the initial phase, but we have to set the 'spaceReservationSystem'
            here 
            TODO: activate spaceReservationSystem dynamicly-->
          <xsl:with-param name="spaceReservationSystem" select="false()" tunnel="yes"/>
        </xsl:apply-templates>

        <xsl:apply-templates select="aorsml:ActivityStartEffect/aorsml:UpdateObjects" mode="createEnvironmentRules.helper.method.stateEffects">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- executeEndEffects() -->
  <xsl:template match="aorsml:ActivityType" mode="createActivties.method.executeEndEffects">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="endEventVarName" required="yes" as="xs:string"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="'executeEndEffects'"/>
      <xsl:with-param name="content">

        <xsl:apply-templates select="." mode="createActivties.setVariables">
          <xsl:with-param name="indent" select="$indent"/>
        </xsl:apply-templates>

        <xsl:apply-templates
          select="aorsml:ActivityEndEffect/aorsml:UpdateObject | 
          aorsml:ActivityEndEffect/aorsml:UpdateStatisticsVariable |
          aorsml:ActivityEndEffect/aorsml:UpdateGridCell | 
          aorsml:ActivityEndEffect/aorsml:UpdateGridCells | 
          aorsml:ActivityEndEffect/aorsml:AddObjectToCollection | 
          aorsml:ActivityEndEffect/aorsml:UpdateActor"
          mode="createActivties.method.executeEffects">
          <xsl:with-param name="indent" select="$indent"/>
          <!-- spaceReservationSystem works only in the initial phase, but we have to set the 'spaceReservationSystem'
            here 
            TODO: activate spaceReservationSystem dynamicly-->
          <xsl:with-param name="spaceReservationSystem" select="false()" tunnel="yes"/>
        </xsl:apply-templates>

        <xsl:apply-templates select="aorsml:ActivityEndEffect/aorsml:UpdateObjects" mode="createEnvironmentRules.helper.method.stateEffects">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="method" select="'deallocateAllRessources'"/>
          <xsl:with-param name="args">
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="instVariable" select="'occurrenceTime'"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- is it used for the additional (activity-)element UpdateActor-->
  <xsl:template
    match="aorsml:UpdateObject | aorsml:UpdateStatisticsVariable | aorsml:UpdateGridCell | aorsml:UpdateGridCells |aorsml:AddObjectToCollection | 
                   aorsml:UpdateActor | aorsml:UpdateStatisticsVariable | aorsml:IncrementGlobalVariable"
    mode="createActivties.method.executeEffects">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:choose>
      <xsl:when test="local-name() eq 'UpdateObject'">
        <xsl:call-template name="java:if">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="condition">
            <xsl:call-template name="java:boolExpr">
              <xsl:with-param name="value1" select="@objectVariable"/>
              <xsl:with-param name="value2" select="'null'"/>
              <xsl:with-param name="operator" select="'!='"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="thenContent">
            <xsl:apply-templates select="." mode="createEnvironmentRules.method.stateEffects.content">
              <xsl:with-param name="indent" select="$indent + 2"/>
              <!-- spaceReservationSystem works only in the initial phase, but we have to set the 'spaceReservationSystem'
                here 
                TODO: activate spaceReservationSystem dynamicly-->
              <xsl:with-param name="spaceReservationSystem" select="false()" tunnel="yes"/>
            </xsl:apply-templates>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="local-name() eq 'UpdateActor'">
        <xsl:apply-templates select="." mode="createActivties.helper.method.executeEffects.UpdateActorObject">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="." mode="createEnvironmentRules.method.stateEffects.content">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <!-- spaceReservationSystem works only in the initial phase, but we have to set the 'spaceReservationSystem'
            here 
            TODO: activate spaceReservationSystem dynamicly-->
          <xsl:with-param name="spaceReservationSystem" select="false()" tunnel="yes"/>
        </xsl:apply-templates>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!-- getActivityEndEvent() -->
  <!-- create an activityEndEvent with delay = duration (if duration is exists)-->
  <xsl:template match="aorsml:ActivityType" mode="createActivties.method.getActivityEndEvent">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:variable name="occurenceTimeVarName" select="'occurenceTime'"/>
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="$core.class.activityEndEvent"/>
      <xsl:with-param name="name" select="'getActivityEndEvent'"/>
      <xsl:with-param name="parameterList">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'long'"/>
          <xsl:with-param name="name" select="$occurenceTimeVarName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:apply-templates select="." mode="createActivties.method.getActivityEndEvent.duration">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <xsl:choose>

          <xsl:when test="fn:exists(aorsml:Duration) or @duration">

            <xsl:variable name="actEndEvtVarName" select="fn:concat('__', jw:lowerWord($core.class.activityEndEvent))"/>
            <xsl:call-template name="java:newObject">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="class" select="$core.class.activityEndEvent"/>
              <xsl:with-param name="varName" select="$actEndEvtVarName"/>
              <xsl:with-param name="args" as="xs:string*">
                <xsl:variable name="duration">
                  <xsl:call-template name="java:callGetterMethod">
                    <xsl:with-param name="inLine" select="true()"/>
                    <xsl:with-param name="instVariable" select="'duration'"/>
                  </xsl:call-template>
                </xsl:variable>

                <!-- param1 'this' -->
                <xsl:value-of select="'this'"/>

                <!-- param2 currentStepTime + duration -->
                <xsl:value-of select="fn:concat($occurenceTimeVarName, ' + ', $duration)"/>

              </xsl:with-param>
            </xsl:call-template>

            <xsl:call-template name="java:callSetterMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="objInstance" select="$actEndEvtVarName"/>
              <xsl:with-param name="instVariable" select="'correlationValue'"/>
              <xsl:with-param name="value">
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="instVariable" select="'correlationValue'"/>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>

            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="value" select="$actEndEvtVarName"/>
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

  <xsl:template match="aorsml:ActivityType" mode="createActivties.method.getActivityEndEvent.duration">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:if test="fn:exists(aorsml:Duration) or fn:exists(@duration)">
      <!-- set duration -->
      <xsl:call-template name="java:callSetterMethod">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="instVariable" select="'duration'"/>
        <xsl:with-param name="value">
          <xsl:choose>

            <xsl:when test="fn:exists(@duration)">
              <xsl:value-of select="@duration"/>
            </xsl:when>

            <xsl:when test="fn:exists(aorsml:Duration/aorsml:ValueExpr[@language eq $output.language])">
              <xsl:value-of select="aorsml:Duration/aorsml:ValueExpr[@language eq $output.language][1]"/>
            </xsl:when>

            <xsl:when test="exists(aorsml:Duration/aorsml:DiscreteRandomVariable)">
              <xsl:apply-templates select="aorsml:Duration/aorsml:DiscreteRandomVariable/aorsml:*" mode="assistents.distribution"/>
            </xsl:when>

          </xsl:choose>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>

  </xsl:template>


  <!-- getSuccessorActivityStartEvents() -->
  <xsl:template match="aorsml:ActivityType" mode="createActivties.method.getSuccessorActivityStartEvents">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:variable name="occurenceTimeVarName" select="'occurenceTime'"/>
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'ArrayList'"/>
      <xsl:with-param name="genericType" select="$core.class.environmentEvent"/>
      <xsl:with-param name="name" select="'getSuccessorActivityStartEvents'"/>
      <xsl:with-param name="parameterList">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'long'"/>
          <xsl:with-param name="name" select="$occurenceTimeVarName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:variable name="resultListVarName" select="'result'"/>
        <xsl:call-template name="java:newArrayListObject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="generic" select="$core.class.environmentEvent"/>
          <xsl:with-param name="name" select="$resultListVarName"/>
        </xsl:call-template>
        <xsl:call-template name="java:newLine"/>

        <xsl:apply-templates select="aorsml:SuccessorActivityType" mode="createActivties.helper.method.getSuccessorActivityStartEvents.nextActivity">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="resultListVarName" select="$resultListVarName"/>
          <xsl:with-param name="occurenceTimeVarName" select="$occurenceTimeVarName"/>
        </xsl:apply-templates>

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value" select="$resultListVarName"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsml:SuccessorActivityType" mode="createActivties.helper.method.getSuccessorActivityStartEvents.nextActivity">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="resultListVarName" required="yes" as="xs:string"/>
    <xsl:param name="occurenceTimeVarName" required="yes" as="xs:string"/>

    <xsl:variable name="nextActivity" select="//aorsml:EntityTypes/aorsml:ActivityType[@name eq current()/@activityName][1]"/>

    <xsl:choose>
      <xsl:when test="fn:exists($nextActivity)">

        <xsl:variable name="eventVarName" select="fn:concat('nextActivityStartEvent_', position())"/>

        <xsl:choose>
          <xsl:when
            test="not($nextActivity/@startEventType) or ($nextActivity/@startEventType and //aorsml:EntityTypes/aorsml:ActionEventType[@name eq $nextActivity/@startEventType])">

            <xsl:call-template name="java:newObject">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="class" select="$core.class.activityStartEvent"/>
              <xsl:with-param name="varName" select="$eventVarName"/>
              <xsl:with-param name="args" as="xs:string*">
                <xsl:value-of select="jw:quote(@activityName)"/>
                <xsl:variable name="duration">
                  <xsl:choose>
                    <xsl:when test="fn:exists(aorsml:Delay[@language eq $output.language])">
                      <xsl:value-of select="aorsml:Delay[@language eq $output.language][1]"/>
                    </xsl:when>
                    <xsl:when test="fn:exists(@delay)">
                      <xsl:value-of select="@delay"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="'1'"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>
                <xsl:value-of select="fn:concat('(', $duration, ') + ', $occurenceTimeVarName)"/>

              </xsl:with-param>
            </xsl:call-template>

            <xsl:call-template name="java:callSetterMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="objInstance" select="$eventVarName"/>
              <xsl:with-param name="instVariable" select="'correlationValue'"/>
              <xsl:with-param name="value">
                <xsl:choose>
                  <xsl:when test="fn:exists(aorsml:StartEventCorrelationProperty[@language eq $output.language])">
                    <xsl:value-of select="aorsml:StartEventCorrelationProperty[@language eq $output.language][1]"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:call-template name="java:callGetterMethod">
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="instVariable" select="'correlationValue'"/>
                    </xsl:call-template>
                  </xsl:otherwise>
                </xsl:choose>

              </xsl:with-param>
            </xsl:call-template>

          </xsl:when>
          <xsl:otherwise>

            <xsl:call-template name="java:newObject">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="class" select="jw:upperWord($nextActivity/@startEventType)"/>
              <xsl:with-param name="varName" select="$eventVarName"/>
              <xsl:with-param name="args">
                <xsl:variable name="duration">
                  <xsl:choose>
                    <xsl:when test="fn:exists(aorsml:Delay[@language eq $output.language])">
                      <xsl:value-of select="aorsml:Delay[@language eq $output.language][1]"/>
                    </xsl:when>
                    <xsl:when test="fn:exists(@delay)">
                      <xsl:value-of select="@delay"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="'1'"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:variable>
                <xsl:value-of select="fn:concat('(', $duration, ') + ', $occurenceTimeVarName)"/>

              </xsl:with-param>
            </xsl:call-template>

            <xsl:for-each select="aorsml:Slot">
              <xsl:call-template name="java:callSetterMethod">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="objInstance" select="$eventVarName"/>
                <xsl:with-param name="instVariable" select="@property"/>
                <xsl:with-param name="value">
                  <xsl:apply-templates select="." mode="assistents.getSlotValue"/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:for-each>

          </xsl:otherwise>
        </xsl:choose>

        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="objInstance" select="$resultListVarName"/>
          <xsl:with-param name="method" select="'add'"/>
          <xsl:with-param name="args" as="xs:string*" select="$eventVarName"/>
        </xsl:call-template>
        <xsl:call-template name="java:newLine"/>

      </xsl:when>
      <xsl:otherwise>
        <xsl:message>
          <xsl:text>[ERROR] No NextActivity </xsl:text>
          <xsl:value-of select="@activityName"/>
          <xsl:text> found!</xsl:text>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>


  <!-- setCurrentEndEvent() -->
  <xsl:template match="aorsml:ActivityType" mode="createActivties.method.setCurrentEndEvent">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="endEventVarName" required="yes" as="xs:string"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="name" select="'setCurrentEndEvent'"/>
      <xsl:with-param name="parameterList">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="$core.class.environmentEvent"/>
          <xsl:with-param name="name" select="$endEventVarName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <!-- set the endEvent -->
        <xsl:if test="@endEventType">
          <xsl:call-template name="java:variable">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="name">
              <xsl:call-template name="java:varByDotNotation">
                <xsl:with-param name="varName" select="$endEventVarName"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="value" select="$endEventVarName"/>
          </xsl:call-template>
        </xsl:if>

        <!-- set the correlation-value -->
        <!--      <xsl:call-template name="java:callSetterMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="instVariable" select="'correlation'"/>
          <xsl:with-param name="value">
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance" select="$endEventVarName"/>
              <xsl:with-param name="instVariable" select="'endEventCorrelationProperty'"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template> -->
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- setStartEvent() -->
  <xsl:template match="aorsml:ActivityType" mode="createActivties.method.setStartEvent">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="startEventVarName" required="yes" as="xs:string"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'setStartEvent'"/>
      <xsl:with-param name="parameterList">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="$core.class.environmentEvent"/>
          <xsl:with-param name="name" select="$startEventVarName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <!-- set the startEvent -->
        <xsl:if test="@startEventType">
          <xsl:call-template name="java:variable">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="name">
              <xsl:call-template name="java:varByDotNotation">
                <xsl:with-param name="varName" select="$startEventVarName"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="castType">
              <xsl:choose>
                <xsl:when test="@startEventType and @startEventType != $core.class.activityStartEvent">
                  <xsl:value-of select="jw:upperWord(@startEventType)"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="$core.class.activityStartEvent"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="value" select="$startEventVarName"/>
          </xsl:call-template>
        </xsl:if>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsml:UpdateActor" mode="createActivties.helper.method.executeEffects.UpdateActorObject">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <!-- UpdateActor -->
    <xsl:call-template name="java:if">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="condition">
        <xsl:call-template name="java:boolExpr">
          <xsl:with-param name="value1">
            <xsl:call-template name="java:boolExpr">
              <xsl:with-param name="value1">
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="instVariable" select="'actor'"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="value2" select="'null'"/>
              <xsl:with-param name="operator" select="'!='"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="value2">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="name" select="@actorType"/>
                  <xsl:with-param name="varName" select="'class'"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="method" select="'isInstance'"/>
              <xsl:with-param name="args">
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="instVariable" select="'actor'"/>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="operator" select="'&amp;&amp;'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="thenContent">

        <xsl:variable name="varName" select="if (@actorVariable) then @actorVariable else jw:lowerWord(@actorType)"/>
        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="type" select="@actorType"/>
          <xsl:with-param name="castType" select="@actorType"/>
          <xsl:with-param name="name" select="$varName"/>
          <xsl:with-param name="value">
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="instVariable" select="'actor'"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:for-each select="aorsml:Slot">
          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="objInstance" select="$varName"/>
            <xsl:with-param name="instVariable" select="@property"/>
            <xsl:with-param name="value">
              <xsl:apply-templates select="." mode="assistents.getSlotValue"/>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:for-each>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsml:ActivityType" mode="createActivties.setVariables">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <!-- set the variables -->
    <!-- fixed Obj by id-->
    <xsl:apply-templates
      select="aorsml:FOR[@objectVariable][(fn:exists(@objectIdRef) or fn:exists(aorsml:ObjectIdRef)) and not(@objectType ='Collection')]"
      mode="createRules.helper.method.execute.fixedById">
      <xsl:with-param name="indent" select="$indent + 1"/>
    </xsl:apply-templates>

    <!-- fixed Obj by ref-->
    <xsl:apply-templates
      select="aorsml:FOR[@objectVariable][fn:exists(aorsml:ObjectRef) and not (fn:exists(@objectIdRef) or fn:exists(aorsml:ObjectIdRef) ) and not(@objectType ='Collection')]"
      mode="createRules.helper.method.execute.fixedByRef">
      <xsl:with-param name="indent" select="$indent + 1"/>
    </xsl:apply-templates>

    <xsl:apply-templates select="aorsml:FOR[@dataVariable]" mode="assistents.setDataVariableDeclaration">
      <xsl:with-param name="indent" select="$indent + 1"/>
    </xsl:apply-templates>

    <xsl:apply-templates select="aorsml:FOR[@objectVariable][@objectType = 'Collection']" mode="createRules.helper.method.execute.fixedCollection">
      <xsl:with-param name="indent" select="$indent + 1"/>
    </xsl:apply-templates>

  </xsl:template>

</xsl:stylesheet>
