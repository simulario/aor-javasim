<?xml version="1.0" encoding="UTF-8"?>

<!--
        This transformation creates classes for environment rules based on a given aorsml file.

        $Rev$
        $Date$

        @author:   Jens Werner (jens.werner@tu-cottbus.de)
        @last changed by $Author$
-->

<xsl:stylesheet version="2.0" xmlns:aorsl="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsi:schemaLocation="http://aor-simulation.org aorsml.xsd"
  xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">

  <!--xsl:import href="../../java.xsl"/-->

  <!--creates class-->
  <xsl:template match="aorsl:EnvironmentRule" mode="createEnvironmentRules.createEnvRule">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <!-- set values for optional attributes -->
    <xsl:variable name="eventVariable">
      <xsl:choose>
        <xsl:when test="fn:exists(aorsl:WHEN/@eventVariable)">
          <xsl:value-of select="aorsl:WHEN/@eventVariable"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'triggeringEvent'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:apply-templates select="aorsl:WHEN" mode="createEnvironmentRules.checkAttributes"/>

    <xsl:call-template name="aorsl:classFile">
      <xsl:with-param name="path" select="$sim.path.model.envsimulator"/>
      <xsl:with-param name="name" select="@name"/>

      <xsl:with-param name="content">

        <xsl:call-template name="java:imports">
          <xsl:with-param name="importList" as="xs:string*">
            <xsl:value-of select="fn:concat($core.package.model, '.*')"/>
            <xsl:value-of select="fn:concat($core.package.model.envEvent, '.*')"/>
            <xsl:value-of select="fn:concat($core.package.model.envSim, '.*')"/>

            <xsl:if
              test="fn:exists(//aorsl:SimulationModel/aorsl:EntityTypes/aorsl:CausedEventType) or 
              fn:exists(//aorsl:SimulationModel/aorsl:EntityTypes/aorsl:PerceptionEventType) or 
              fn:exists(//aorsl:SimulationModel/aorsl:EntityTypes/aorsl:ExogenousEventType) or 
              fn:exists(//aorsl:SimulationModel/aorsl:EntityTypes/aorsl:ActionEventType)">
              <xsl:value-of select="fn:concat($sim.package.model.envevent, '.*')"/>
            </xsl:if>

            <xsl:if
              test="fn:exists(aorsl:UPDATE-ENV/aorsl:Create/aorsl:PhysicalAgent) or 
              fn:exists(aorsl:UPDATE-ENV/aorsl:Create/aorsl:Agent) or 
              fn:exists(aorsl:*/aorsl:UPDATE-ENV/aorsl:Create/aorsl:PhysicalAgent) or 
              fn:exists(aorsl:*/aorsl:UPDATE-ENV/aorsl:Create/aorsl:Agent)">
              <xsl:value-of select="fn:concat($sim.package.model.agentsimulator, '.*')"/>
            </xsl:if>

            <xsl:if
              test="fn:exists(aorsl:FOR[@objectVariable][@objectType = 'Collection']) or 
                        fn:exists(aorsl:UPDATE-ENV/aorsl:RemoveObjectFromCollection) or 
                        fn:exists(aorsl:UPDATE-ENV/aorsl:Create/*/@addToCollection) or 
                        fn:exists(aorsl:UPDATE-ENV/aorsl:AddObjectToCollection) or
                        fn:exists(aorsl:*/aorsl:UPDATE-ENV/aorsl:RemoveObjectFromCollection) or 
                        fn:exists(aorsl:*/aorsl:UPDATE-ENV/aorsl:Create/*/@addToCollection) or 
                        fn:exists(aorsl:*/aorsl:UPDATE-ENV/aorsl:AddObjectToCollection)">
              <xsl:value-of select="$collection.package.aORCollection"/>
            </xsl:if>

            <xsl:call-template name="setDefaultJavaImports"/>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="java:class">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="name" select="@name"/>
          <xsl:with-param name="extends" select="$core.class.environmentRule"/>
          <xsl:with-param name="content">

            <!-- set triggering event as classvariable -->
            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="modifier" select="'private'"/>
              <xsl:with-param name="type">
                <xsl:choose>
                  <xsl:when test="aorsl:WHEN/@eventType">
                    <xsl:value-of select="aorsl:WHEN/@eventType"/>
                  </xsl:when>
                  <xsl:when test="aorsl:ON-EACH-SIMULATION-STEP">
                    <xsl:value-of select="$core.package.onEveryStepEnvEvent"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:message>
                      <xsl:text>[ERROR] - neither triggering event nor on-each-simulation-step defined for </xsl:text>
                      <xsl:value-of select="concat(local-name(), ' [', @name, ']!]')"/>
                    </xsl:message>
                  </xsl:otherwise>
                </xsl:choose>

              </xsl:with-param>
              <xsl:with-param name="name" select="$eventVariable"/>
            </xsl:call-template>
            <xsl:call-template name="java:newLine"/>

            <!-- set involved entitys as classvariable -->
            <xsl:for-each select="aorsl:FOR[@objectVariable]">
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

            <!-- set the message as classvariable -->
            <xsl:if
              test="aorsl:WHEN/@eventType eq $core.class.outMessageEvent and 
              fn:exists(aorsl:WHEN/@messageType) and 
              fn:exists(aorsl:WHEN/@messageVariable)">
              <xsl:call-template name="java:variable">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="modifier" select="'private'"/>
                <xsl:with-param name="type" select="aorsl:WHEN/@messageType"/>
                <xsl:with-param name="name" select="aorsl:WHEN/@messageVariable"/>
              </xsl:call-template>
            </xsl:if>

            <!-- set the DataVariableDeclaration as classvaraibles -->
            <xsl:apply-templates select="aorsl:FOR[@dataVariable]" mode="assistents.setDataVariableDeclarationClassVariables">
              <xsl:with-param name="indent" select="$indent"/>
            </xsl:apply-templates>

            <!-- set used collectionitems as classvariable if itemObjectVariable exists -->
            <xsl:apply-templates
              select="aorsl:UPDATE-ENV/aorsl:RemoveObjectFromCollection[fn:exists(@itemObjectVariable) or fn:exists(@destroyObject)] | 
                      aorsl:*/aorsl:UPDATE-ENV/aorsl:RemoveObjectFromCollection[fn:exists(@itemObjectVariable) or fn:exists(@destroyObject)]"
              mode="createEnvironmentRules.setGlobalsByCollectionItems">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>
            <xsl:call-template name="java:newLine"/>

            <!-- set to create aor-obj with @objectVariable as classvariable-->
            <xsl:for-each select="aorsl:UPDATE-ENV/aorsl:Create/*[@objectVariable] | aorsl:*/aorsl:UPDATE-ENV/aorsl:Create/*[@objectVariable]">
              <xsl:call-template name="java:variable">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="modifier" select="'private'"/>
                <xsl:with-param name="type" select="@type"/>
                <xsl:with-param name="name" select="@objectVariable"/>
              </xsl:call-template>
            </xsl:for-each>
            <xsl:call-template name="java:newLine"/>

            <!-- constructors -->
            <xsl:apply-templates select="." mode="createEnvironmentRules.constructors">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="eventVariable" select="$eventVariable"/>
            </xsl:apply-templates>

            <!-- execute -->
            <xsl:apply-templates select="." mode="createEnvironmentRules.method.execute">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="eventVariable" select="$eventVariable" tunnel="yes"/>
            </xsl:apply-templates>

            <!-- resultingEvents -->
            <xsl:apply-templates select="." mode="createEnvironmentRules.method.resultingEvents">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="triggeringEventVar" select="$eventVariable" tunnel="yes"/>
            </xsl:apply-templates>

            <!-- stateEffects -->
            <xsl:apply-templates select="." mode="createEnvironmentRules.method.stateEffects">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- destroyObjects -->
            <xsl:apply-templates select="." mode="createEnvironmentRules.method.destroyObject">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- conditions -->
            <xsl:apply-templates select="." mode="createEnvironmentRules.method.conditions">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- create setter and getter for triggering event -->
            <xsl:apply-templates select="." mode="createEnvironmentRules.method.setGetTriggeringEvent">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="eventVar" select="$eventVariable"/>
            </xsl:apply-templates>

            <!-- getMessageType() -->
            <xsl:apply-templates select="." mode="shared.method.getMessageType">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- getActivityType()
            <xsl:apply-templates select="." mode="createEnvironmentRules.method.getActivityType">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="eventVariable" select="$eventVariable"/>
              </xsl:apply-templates>  -->

          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- constructor -->
  <xsl:template match="aorsl:EnvironmentRule" mode="createEnvironmentRules.constructors">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="eventVariable" required="yes" as="xs:string"/>

    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="@name"/>
      <xsl:with-param name="parameters" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'String'"/>
          <xsl:with-param name="name" select="'name'"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="$core.class.environmentAccessFacet"/>
          <xsl:with-param name="name" select="'envSim'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">
        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string*">
            <xsl:value-of select="'name'"/>
            <xsl:value-of select="'envSim'"/>
          </xsl:with-param>
        </xsl:call-template>

        <!-- set a default triggering event objekt 
        <xsl:call-template name="java:newObject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="class">
            <xsl:choose>
              <xsl:when test="aorsl:WHEN/@eventType">
                <xsl:value-of select="aorsl:WHEN/@eventType"/>
              </xsl:when>
              <xsl:when test="aorsl:ON-EACH-SIMULATION-STEP">
                <xsl:value-of select="$core.package.onEveryStepEnvEvent"/>
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
              <xsl:with-param name="varName" select="$eventVariable"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="isVariable" select="true()"/>
          </xsl:call-template>-->

        <xsl:apply-templates select="aorsl:documentation" mode="shared.setDocumentation">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!--**************-->
  <!--   methods    -->
  <!--**************-->

  <!-- ********************************************************************* -->
  <!-- destroyObjekt() -->
  <!-- ********************************************************************* -->
  <!-- this method check if an element aorsl:DestroyEntity exists (if true the return true otherwise false)-->
  <!-- if the element  aorsl:DestroyEntity have the attribute @entity then use the value as the id otherwise
    use the child aorsl:Entity. If they have an appropriate expression then use the expression as id-value.
    if there are non of them, then it was non methodcall destroyEntity() created, but the returnvalue is 
    nevertheless false-->
  <xsl:template match="aorsl:EnvironmentRule" mode="createEnvironmentRules.method.destroyObject">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="else" select="false()"/>

    <!-- DO destroyObjekt  -->
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="annotation">
        <xsl:if
          test="$suppressWarnings and 
          fn:exists(aorsl:DO/aorsl:UPDATE-ENV/aorsl:DestroyObject[@deferred eq 'true' and 
                        (not (fn:exists(@objectIdRef) or fn:exists(@objectVariable) or fn:exists(aorsl:ObjectRef))) and 
                        fn:exists(@removeFromCollection)])">
          <xsl:call-template name="getAnnotationSuppressWarnings.unchecked"/>
        </xsl:if>
      </xsl:with-param>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="type" select="'boolean'"/>
      <xsl:with-param name="name" select="'doDestroyObjekt'"/>
      <xsl:with-param name="content">

        <xsl:if test="fn:not(aorsl:IF)">
          <xsl:apply-templates select="aorsl:UPDATE-ENV" mode="createEnvironmentRules.method.destroyObject.DoThenElse">
            <xsl:with-param name="indent" select="$indent"/>
          </xsl:apply-templates>
        </xsl:if>

        <xsl:apply-templates select="aorsl:DO/aorsl:UPDATE-ENV" mode="createEnvironmentRules.method.destroyObject.DoThenElse">
          <xsl:with-param name="indent" select="$indent"/>
        </xsl:apply-templates>

        <xsl:choose>
          <xsl:when
            test="fn:exists(aorsl:UPDATE-ENV/aorsl:DestroyObject[@deferred eq 'true']) or
                  fn:exists(aorsl:UPDATE-ENV/aorsl:DestroyObjects[@deferred eq 'true']) or
                  fn:exists(aorsl:UPDATE-ENV/aorsl:RemoveObjectFromCollection[@destroyObject = true()]) or 
                  fn:exists(aorsl:DO/aorsl:UPDATE-ENV/aorsl:DestroyObject[@deferred eq 'true']) or
                  fn:exists(aorsl:DO/aorsl:UPDATE-ENV/aorsl:DestroyObjects[@deferred eq 'true']) or
                  fn:exists(aorsl:DO/aorsl:UPDATE-ENV/aorsl:RemoveObjectFromCollection[@destroyObject = true()])">

            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="value" select="'true'"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="value" select="'false'"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
    </xsl:call-template>

    <!-- THEN destroyObjekt -->
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="annotation">
        <xsl:if
          test="$suppressWarnings and 
          (fn:exists(aorsl:UPDATE-ENV/aorsl:DestroyObject[@deferred eq 'true' and 
          (not (fn:exists(@objectIdRef) or fn:exists(@objectVariable) or fn:exists(aorsl:ObjectRef))) and 
          fn:exists(@removeFromCollection)])) or 
          (fn:exists(aorsl:THEN/aorsl:UPDATE-ENV/aorsl:DestroyObject[@deferred eq 'true' and 
          (not (fn:exists(@objectIdRef) or fn:exists(@objectVariable) or fn:exists(aorsl:ObjectRef))) and 
          fn:exists(@removeFromCollection)]))">
          <xsl:call-template name="getAnnotationSuppressWarnings.unchecked"/>
        </xsl:if>
      </xsl:with-param>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="type" select="'boolean'"/>
      <xsl:with-param name="name" select="'thenDestroyObjekt'"/>
      <xsl:with-param name="content">

        <xsl:if test="fn:exists(aorsl:IF)">
          <xsl:apply-templates select="aorsl:UPDATE-ENV" mode="createEnvironmentRules.method.destroyObject.DoThenElse">
            <xsl:with-param name="indent" select="$indent"/>
          </xsl:apply-templates>
        </xsl:if>

        <xsl:apply-templates select="aorsl:THEN/aorsl:UPDATE-ENV" mode="createEnvironmentRules.method.destroyObject.DoThenElse">
          <xsl:with-param name="indent" select="$indent"/>
        </xsl:apply-templates>

        <xsl:choose>
          <xsl:when
            test="fn:exists(aorsl:UPDATE-ENV/aorsl:DestroyObject[@deferred eq 'true']) or
                  fn:exists(aorsl:UPDATE-ENV/aorsl:DestroyObjects[@deferred eq 'true']) or
                  fn:exists(aorsl:UPDATE-ENV/aorsl:RemoveObjectFromCollection[@destroyObject = true()])">

            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="value" select="'true'"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="value" select="'false'"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
    </xsl:call-template>

    <!-- ELSE destroyObjekt -->
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="annotation">
        <xsl:if
          test="$suppressWarnings and 
          fn:exists(aorsl:ELSE/aorsl:UPDATE-ENV/aorsl:DestroyObject[@deferred eq 'true' and 
          (not (fn:exists(@objectIdRef) or fn:exists(@objectVariable) or fn:exists(aorsl:ObjectRef))) and 
          fn:exists(@removeFromCollection)])">
          <xsl:call-template name="getAnnotationSuppressWarnings.unchecked"/>
        </xsl:if>
      </xsl:with-param>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="type" select="'boolean'"/>
      <xsl:with-param name="name" select="'elseDestroyObjekt'"/>
      <xsl:with-param name="content">

        <xsl:apply-templates select="aorsl:ELSE/aorsl:UPDATE-ENV" mode="createEnvironmentRules.method.destroyObject.DoThenElse">
          <xsl:with-param name="indent" select="$indent"/>
        </xsl:apply-templates>

        <xsl:choose>
          <xsl:when
            test="fn:exists(aorsl:ELSE/aorsl:UPDATE-ENV/aorsl:DestroyObject[@deferred eq 'true']) or
                  fn:exists(aorsl:ELSE/aorsl:UPDATE-ENV/aorsl:DestroyObjects[@deferred eq 'true']) or
                  fn:exists(aorsl:ELSE/aorsl:UPDATE-ENV/aorsl:RemoveObjectFromCollection[@destroyObject = true()])">

            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="value" select="'true'"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="value" select="'false'"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsl:UPDATE-ENV" mode="createEnvironmentRules.method.destroyObject.DoThenElse">
    <xsl:param name="indent" required="yes"/>

    <!-- only for errormessage -->
    <xsl:variable name="ruleName" select="@name"/>

    <xsl:variable name="objectCollElemVarName" select="fn:concat(jw:lowerWord($core.class.object), 'CollectionElement')"/>

    <!-- set the collections for DestroyObject with @removeFromCollection (without other attributes) - DISTINCT -->
    <xsl:for-each
      select="aorsl:DestroyObject[@deferred eq 'true' and fn:exists(@removeFromCollection) and 
      not(@removeFromCollection = preceding-sibling::aorsl:DestroyObject[@deferred eq 'true']/@removeFromCollection)]">
      <!-- we need the collection only if @removeFromCollection without other attributes or childelements -->
      <xsl:if test="(not (fn:exists(@objectIdRef) or fn:exists(@objectVariable) or fn:exists(aorsl:ObjectRef)))">
        <xsl:choose>

          <!-- check if Collection exists -->
          <xsl:when test="fn:exists(//aorsl:Collections/aorsl:Collection[@name = current()/@removeFromCollection])">
            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="type">
                <xsl:call-template name="java:classWithGenericType">
                  <xsl:with-param name="genericType">
                    <xsl:value-of select="//aorsl:Collections/aorsl:Collection[@name = current()/@removeFromCollection]/@itemType"/>
                  </xsl:with-param>
                  <xsl:with-param name="class" select="$collection.class.aORCollection"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="name" select="jw:lowerWord(./@removeFromCollection)"/>
              <xsl:with-param name="castType">
                <xsl:call-template name="java:classWithGenericType">
                  <xsl:with-param name="genericType">
                    <xsl:value-of select="//aorsl:Collections/aorsl:Collection[@name = current()/@removeFromCollection]/@itemType"/>
                  </xsl:with-param>
                  <xsl:with-param name="class" select="$collection.class.aORCollection"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="value">
                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="indent" select="$indent"/>
                  <xsl:with-param name="objInstance">
                    <xsl:call-template name="java:callGetterMethod">
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="objInstance" select="'this'"/>
                      <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="method" select="'getCollectionByName'"/>
                  <xsl:with-param name="args" select="jw:quote(./@removeFromCollection)"/>
                  <xsl:with-param name="inLine" select="true()"/>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message>
              <xsl:text>[ERROR] Strange collectiontype: </xsl:text>
              <xsl:value-of select="./@removeFromCollection"/>
              <xsl:text> in EnvironmentRule/DestroyObject: </xsl:text>
              <xsl:value-of select="$ruleName"/>
            </xsl:message>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:if>
    </xsl:for-each>

    <!-- it's created if @removeFromCollection (single), also if there is no correspond Collection -->
    <xsl:if
      test="fn:exists(aorsl:DestroyObject[@deferred eq 'true'][not (fn:exists(@objectIdRef) or fn:exists(@objectVariable) or fn:exists(aorsl:ObjectRef)) 
      and fn:exists(@removeFromCollection)])">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent + 1"/>
        <xsl:with-param name="class" select="$core.class.object"/>
        <xsl:with-param name="varName" select="$objectCollElemVarName"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:if>

    <!-- DestroyEntity -->
    <xsl:apply-templates select="aorsl:DestroyObject[@deferred eq 'true']" mode="createEnvironmentRules.helper.method.destroyObject">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="objectCollElemVarName" select="$objectCollElemVarName"/>
    </xsl:apply-templates>

    <!-- DestroyEntities -->
    <xsl:apply-templates select="aorsl:DestroyObjects[@deferred eq 'true']" mode="createEnvironmentRules.helper.method.destroyObjects">
      <xsl:with-param name="indent" select="$indent + 1"/>
    </xsl:apply-templates>

    <!-- RemoveObjectFromCollection with @destroyObject=true -->
    <xsl:apply-templates select="aorsl:RemoveObjectFromCollection[@destroyObject = true()]"
      mode="createEnvironmentRules.helper.method.destroyRemoveObjectFromCollection">
      <xsl:with-param name="indent" select="$indent + 1"/>
    </xsl:apply-templates>

  </xsl:template>
  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
  <!-- destroyObjekt() -->
  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->

  <!-- execute() -->
  <xsl:template match="aorsl:EnvironmentRule" mode="createEnvironmentRules.method.execute">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="annotation">
        <xsl:if test="$suppressWarnings and aorsl:FOR[@objectVariable]/@objectType = 'Collection'">
          <xsl:call-template name="getAnnotationSuppressWarnings.unchecked"/>
        </xsl:if>
      </xsl:with-param>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'ArrayList'"/>
      <xsl:with-param name="genericType" select="$core.class.environmentEvent"/>
      <xsl:with-param name="name" select="'execute'"/>
      <xsl:with-param name="content">

        <!-- resultListVar is used in this method as a return -->
        <xsl:variable name="resultListVar" select="'result'"/>

        <xsl:call-template name="java:newArrayListObject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="generic" select="$core.class.environmentEvent"/>
          <xsl:with-param name="name" select="$resultListVar"/>
        </xsl:call-template>
        <xsl:call-template name="java:newLine"/>

        <xsl:apply-templates select="aorsl:WHEN" mode="createEnvironmentRules.helper.method.execute.checkActivityType">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="resultListVar" select="$resultListVar"/>
        </xsl:apply-templates>

        <xsl:apply-templates select="." mode="assistents.createRule.createEnvInitRuleExec">
          <xsl:with-param name="indent" select="$indent"/>
        </xsl:apply-templates>

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value" select="$resultListVar"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- ********************************************************************* -->
  <!-- resultingEvents() -->
  <!-- ********************************************************************* -->
  <xsl:template match="aorsl:EnvironmentRule" mode="createEnvironmentRules.method.resultingEvents">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <!-- Do ResultingEvents   -->
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="type" select="'ArrayList'"/>
      <xsl:with-param name="genericType" select="$core.class.environmentEvent"/>
      <xsl:with-param name="name" select="'doResultingEvents'"/>
      <xsl:with-param name="content">

        <xsl:variable name="resultVar" select="'result'"/>
        <xsl:call-template name="java:newArrayListObject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="generic" select="$core.class.environmentEvent"/>
          <xsl:with-param name="name" select="$resultVar"/>
        </xsl:call-template>

        <xsl:if test="fn:not(aorsl:IF)">
          <xsl:apply-templates select="aorsl:SCHEDULE-EVT" mode="createEnvironmentRules.method.resultingEvents.DoThenElse">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="resultVar" select="$resultVar"/>
          </xsl:apply-templates>
        </xsl:if>

        <xsl:apply-templates select="aorsl:DO/aorsl:SCHEDULE-EVT" mode="createEnvironmentRules.method.resultingEvents.DoThenElse">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="resultVar" select="$resultVar"/>
        </xsl:apply-templates>

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value" select="$resultVar"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

    <!-- Then ResultingEvents -->
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="type" select="'ArrayList'"/>
      <xsl:with-param name="genericType" select="$core.class.environmentEvent"/>
      <xsl:with-param name="name" select="'thenResultingEvents'"/>
      <xsl:with-param name="content">

        <xsl:variable name="resultVar" select="'result'"/>
        <xsl:call-template name="java:newArrayListObject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="generic" select="$core.class.environmentEvent"/>
          <xsl:with-param name="name" select="$resultVar"/>
        </xsl:call-template>

        <xsl:if test="fn:exists(aorsl:IF)">
          <xsl:apply-templates select="aorsl:SCHEDULE-EVT" mode="createEnvironmentRules.method.resultingEvents.DoThenElse">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="resultVar" select="$resultVar"/>
          </xsl:apply-templates>
        </xsl:if>

        <xsl:apply-templates select="aorsl:THEN/aorsl:SCHEDULE-EVT" mode="createEnvironmentRules.method.resultingEvents.DoThenElse">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="resultVar" select="$resultVar"/>
        </xsl:apply-templates>

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value" select="$resultVar"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

    <!-- Else ResultingEvents -->
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="type" select="'ArrayList'"/>
      <xsl:with-param name="genericType" select="$core.class.environmentEvent"/>
      <xsl:with-param name="name" select="'elseResultingEvents'"/>
      <xsl:with-param name="content">

        <xsl:variable name="resultVar" select="'result'"/>
        <xsl:call-template name="java:newArrayListObject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="generic" select="$core.class.environmentEvent"/>
          <xsl:with-param name="name" select="$resultVar"/>
        </xsl:call-template>

        <xsl:apply-templates select="aorsl:ELSE/aorsl:SCHEDULE-EVT" mode="createEnvironmentRules.method.resultingEvents.DoThenElse">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="resultVar" select="$resultVar"/>
        </xsl:apply-templates>

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value" select="$resultVar"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsl:SCHEDULE-EVT" mode="createEnvironmentRules.method.resultingEvents.DoThenElse">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="resultVar" required="yes" as="xs:string"/>

    <xsl:apply-templates select="aorsl:CausedEventExpr" mode="createEnvironmentRules.helper.method.resultingEvents">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="resultList" select="$resultVar"/>
    </xsl:apply-templates>

    <xsl:apply-templates select="aorsl:PerceptionEventExpr" mode="createEnvironmentRules.helper.method.resultingEvents">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="resultList" select="$resultVar"/>
    </xsl:apply-templates>

    <xsl:apply-templates select="aorsl:InMessageEventExpr" mode="createEnvironmentRules.helper.method.resultingEvents">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="resultList" select="$resultVar"/>
    </xsl:apply-templates>

    <xsl:apply-templates select="aorsl:ActivityStartEventExpr" mode="createEnvironmentRules.helper.method.resultingEvents">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="resultList" select="$resultVar"/>
    </xsl:apply-templates>

    <xsl:apply-templates select="aorsl:ActivityEndEventExpr" mode="createEnvironmentRules.helper.method.resultingEvents">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="resultList" select="$resultVar"/>
    </xsl:apply-templates>

  </xsl:template>
  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
  <!-- resultingEvents() -->
  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->


  <!-- ********************************************************************* -->
  <!-- stateEffects() -->
  <!-- ********************************************************************* -->
  <xsl:template match="aorsl:EnvironmentRule" mode="createEnvironmentRules.method.stateEffects">
    <xsl:param name="indent" required="yes"/>

    <!-- doStateEffects  -->
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="annotation">
        <xsl:if
          test="$suppressWarnings and 
          ((fn:exists(aorsl:UPDATE-ENV/aorsl:RemoveObjectFromCollection) or
          fn:exists(aorsl:UPDATE-ENV/aorsl:Create/*/@addToCollection)) or
          fn:exists(aorsl:UPDATE-ENV/aorsl:AddObjectToCollection)) and not(aorsl:IF) or
          (fn:exists(aorsl:DO/aorsl:UPDATE-ENV/aorsl:RemoveObjectFromCollection) or
          fn:exists(aorsl:DO/aorsl:UPDATE-ENV/aorsl:Create/*/@addToCollection)) or
          fn:exists(aorsl:DO/aorsl:UPDATE-ENV/aorsl:AddObjectToCollection)">
          <xsl:call-template name="getAnnotationSuppressWarnings.unchecked"/>
        </xsl:if>
      </xsl:with-param>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'doStateEffects'"/>
      <xsl:with-param name="content">

        <xsl:if test="fn:exists(aorsl:DO/aorsl:UPDATE-ENV/aorsl:Create/aorsl:*[@hasRandomPosition = true()])">
          <xsl:call-template name="java:newObject">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="class">
              <xsl:choose>
                <xsl:when test="ends-with(local-name(//aorsl:SimulationModel/aorsl:SpaceModel/aorsl:*), 'Grid')">
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

        <xsl:apply-templates select="aorsl:DO/aorsl:UPDATE-ENV" mode="createEnvironmentRules.method.stateEffects.DoThenElse">
          <xsl:with-param name="indent" select="$indent"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>

    <!-- (then)stateEffects -->
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="annotation">
        <xsl:if
          test="$suppressWarnings and 
          (fn:exists(aorsl:UPDATE-ENV/aorsl:RemoveObjectFromCollection) or
          fn:exists(aorsl:UPDATE-ENV/aorsl:Create/*/@addToCollection)) or
          fn:exists(aorsl:UPDATE-ENV/aorsl:AddObjectToCollection) or
          (fn:exists(aorsl:THEN/aorsl:UPDATE-ENV/aorsl:RemoveObjectFromCollection) or
          fn:exists(aorsl:THEN/aorsl:UPDATE-ENV/aorsl:Create/*/@addToCollection)) or
          fn:exists(aorsl:THEN/aorsl:UPDATE-ENV/aorsl:AddObjectToCollection)">
          <xsl:call-template name="getAnnotationSuppressWarnings.unchecked"/>
        </xsl:if>
      </xsl:with-param>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'thenStateEffects'"/>
      <xsl:with-param name="content">

        <xsl:if test="fn:exists(aorsl:THEN/aorsl:UPDATE-ENV/aorsl:Create/aorsl:*[@hasRandomPosition = true()])">
          <xsl:call-template name="java:newObject">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="class">
              <xsl:choose>
                <xsl:when test="ends-with(local-name(//aorsl:SimulationModel/aorsl:SpaceModel/aorsl:*), 'Grid')">
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

        <!-- deprecated for backwards-compatibility -->
        <xsl:if test="fn:exists(aorsl:IF)">
          <xsl:apply-templates select="aorsl:UPDATE-ENV" mode="createEnvironmentRules.method.stateEffects.DoThenElse">
            <xsl:with-param name="indent" select="$indent"/>
          </xsl:apply-templates>
        </xsl:if>

        <xsl:apply-templates select="aorsl:THEN/aorsl:UPDATE-ENV" mode="createEnvironmentRules.method.stateEffects.DoThenElse">
          <xsl:with-param name="indent" select="$indent"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>

    <!-- elseStateEffects -->
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="annotation">
        <xsl:if
          test="$suppressWarnings and 
          (fn:exists(aorsl:ELSE/aorsl:UPDATE-ENV/aorsl:RemoveObjectFromCollection) or
          fn:exists(aorsl:ELSE/aorsl:UPDATE-ENV/aorsl:Create/*/@addToCollection)) or
          fn:exists(aorsl:ELSE/aorsl:UPDATE-ENV/aorsl:AddObjectToCollection)">
          <xsl:call-template name="getAnnotationSuppressWarnings.unchecked"/>
        </xsl:if>
      </xsl:with-param>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="name" select="'elseStateEffects'"/>
      <xsl:with-param name="content">

        <xsl:if test="fn:exists(aorsl:ELSE/aorsl:UPDATE-ENV/aorsl:Create/aorsl:*[@hasRandomPosition = true()])">
          <xsl:call-template name="java:newObject">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="class">
              <xsl:choose>
                <xsl:when test="ends-with(local-name(//aorsl:SimulationModel/aorsl:SpaceModel/aorsl:*), 'Grid')">
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

        <xsl:apply-templates select="aorsl:ELSE/aorsl:UPDATE-ENV" mode="createEnvironmentRules.method.stateEffects.DoThenElse">
          <xsl:with-param name="indent" select="$indent"/>
        </xsl:apply-templates>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsl:UPDATE-ENV" mode="createEnvironmentRules.method.stateEffects.DoThenElse">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <!-- create all neccessary collections (excluding if its exists @collectionObjectVariable, because these was defined global from FOR[@objectVariable])-->
    <!--xsl:apply-templates
      select="aorsl:RemoveObjectFromCollection[(not (fn:exists(@collectionObjectVariable))) and 
      not(@collectionName = preceding-sibling::aorsl:UPDATE-ENV/aorsl:RemoveObjectFromCollection/@collectionName)]/@collectionName"
      mode="createEnvironmentRules.helper.method.stateEffects.setCollections">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates-->

    <xsl:variable name="root" select="root()"/>
    <xsl:variable name="distinctAddToCollection" as="xs:string*">
      <xsl:if test="aorsl:Create/aorsl:*/@addToCollection">
        <xsl:copy-of select="distinct-values(aorsl:Create/aorsl:*/@addToCollection)"/>
      </xsl:if>
    </xsl:variable>
    <xsl:variable name="distinctCollectionName" as="xs:string*">
      <xsl:if test="aorsl:RemoveObjectFromCollection[(not(fn:exists(@collectionObjectVariable)))]">
        <xsl:copy-of select="distinct-values(aorsl:RemoveObjectFromCollection[(not(fn:exists(@collectionObjectVariable)))]/@collectionName)"/>
      </xsl:if>
    </xsl:variable>
    <xsl:for-each select="($distinctAddToCollection, $distinctCollectionName)">

      <xsl:apply-templates select="$root//aorsl:Collections/aorsl:Collection[@name = current()]"
        mode="createEnvironmentRules.helper.method.stateEffects.setCollections">
        <xsl:with-param name="indent" select="$indent"/>
      </xsl:apply-templates>

    </xsl:for-each>


    <!-- achieve the order -->
    <xsl:apply-templates
      select="aorsl:RemoveObjectFromCollection |
              aorsl:UpdateObject | 
              aorsl:UpdateStatisticsVariable |
              aorsl:Create | 
              aorsl:DestroyObject[not(@deferred) or @deferred eq 'false'] |
              aorsl:UpdateGridCell | 
              aorsl:ForEachGridCell | 
              aorsl:AddObjectToCollection |
              aorsl:IncrementGlobalVariable |
              aorsl:UpdateGlobalVariable | 
              aorsl:Call"
      mode="createEnvironmentRules.method.stateEffects.content">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <!-- spaceReservationSystem works only in the initial phase, but we have to set the 'spaceReservationSystem'
        here 
        TODO: activate spaceReservationSystem dynamicly-->
      <xsl:with-param name="spaceReservationSystem" select="false()" tunnel="yes"/>
    </xsl:apply-templates>

    <!--sets state effects-->
    <xsl:apply-templates select="aorsl:UpdateObjects" mode="createEnvironmentRules.helper.method.stateEffects">
      <xsl:with-param name="indent" select="$indent + 1"/>
    </xsl:apply-templates>

    <xsl:apply-templates select="aorsl:DestroyObjects[not(@deferred) or @deferred eq 'false']"
      mode="createEnvironmentRules.helper.method.destroyObjects">
      <xsl:with-param name="indent" select="$indent + 1"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
  <!-- stateEffects() -->
  <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
  <xsl:template match="aorsl:Collection" mode="createEnvironmentRules.helper.method.stateEffects.setCollections">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="type">
        <xsl:call-template name="java:classWithGenericType">
          <xsl:with-param name="genericType">
            <xsl:value-of select="@itemType"/>
          </xsl:with-param>
          <xsl:with-param name="class" select="$collection.class.aORCollection"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="name">
        <xsl:value-of select="jw:createCollectionVarname(.)"/>
      </xsl:with-param>
      <xsl:with-param name="castType">
        <xsl:call-template name="java:classWithGenericType">
          <xsl:with-param name="genericType">
            <xsl:value-of select="@itemType"/>
          </xsl:with-param>
          <xsl:with-param name="class" select="$collection.class.aORCollection"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="value">
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance" select="'this'"/>
              <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="method" select="'getCollectionByName'"/>
          <xsl:with-param name="args" select="jw:quote(@name)"/>
          <xsl:with-param name="inLine" select="true()"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- this forwarding is necessary, because we have toachieve the order of elements in UpdateObjectiveStateExpr (see: createEnvironmentRules.method.stateEffects) -->
  <!-- this template is additional used by createActivities.xsl-->
  <xsl:template match="aorsl:UpdateStatisticsVariable" mode="createEnvironmentRules.method.stateEffects.content">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:apply-templates select="." mode="shared.helper.updateStatistics">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- this forwarding is necessary, because we have toachieve the order of elements in UpdateObjectiveStateExpr (see: createEnvironmentRules.method.stateEffects) -->
  <xsl:template match="aorsl:IncrementGlobalVariable" mode="createEnvironmentRules.method.stateEffects.content">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:apply-templates select="." mode="shared.incrementGlobalVariable">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- this forwarding is necessary, because we have toachieve the order of elements in UpdateObjectiveStateExpr (see: createEnvironmentRules.method.stateEffects) -->
  <xsl:template match="aorsl:UpdateGlobalVariable" mode="createEnvironmentRules.method.stateEffects.content">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:apply-templates select="." mode="shared.updateGlobalVariable">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- this template is additional used by createActivities.xsl-->
  <xsl:template match="aorsl:AddObjectToCollection" mode="createEnvironmentRules.method.stateEffects.content">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:variable name="collectionVarName" select="fn:concat('addTocollection_', position())"/>

    <xsl:variable name="collectionTypeNode"
      select="if (@collectionID) then //aorsl:SimulationModel/aorsl:Collections/aorsl:Collection[@id eq current()/@collectionID] else 
                     if (@collectionName) then //aorsl:SimulationModel/aorsl:Collections/aorsl:Collection[@name eq current()/@collectionName] else ''"/>

    <xsl:if test="$collectionTypeNode">

      <xsl:variable name="objVarDeclaration"
        select="ancestor::aorsl:EnvironmentRule/aorsl:FOR[@objectVariable][@objectType eq 'Collection']
        [@objectName eq $collectionTypeNode/@name or @objectIdRef eq $collectionTypeNode/@id][1]"/>
      <xsl:choose>
        <xsl:when test="fn:exists($objVarDeclaration)">

          <xsl:call-template name="java:callMethod">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="objInstance">
              <xsl:call-template name="java:varByDotNotation">
                <xsl:with-param name="varName" select="$objVarDeclaration/@objectVariable"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="method" select="'addObjekt'"/>
            <xsl:with-param name="args">
              <xsl:choose>
                <xsl:when test="aorsl:ItemObjectRef[matches(@language, $output.lang.RegExpr)]">
                  <xsl:value-of select="aorsl:ItemObjectRef[matches(@language, $output.lang.RegExpr)][1]"/>
                </xsl:when>
                <xsl:when test="@itemObjectVariable">
                  <xsl:value-of select="@itemObjectVariable"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:message>
                    <xsl:text>[ERROR] No Item to add to a collection in Rule [</xsl:text>
                    <xsl:value-of select="ancestor::aorsl:EnvironmentRule/@name"/>
                    <xsl:text>]!</xsl:text>
                  </xsl:message>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
          </xsl:call-template>

        </xsl:when>
        <xsl:otherwise>
          <xsl:variable name="itemType" select="$collectionTypeNode/@itemType"/>

          <!-- get the collection -->
          <xsl:call-template name="java:newObject">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="class" select="$collection.class.aORCollection"/>
            <xsl:with-param name="generic">
              <xsl:choose>
                <xsl:when test="not($itemType eq '')">
                  <xsl:value-of select="$itemType"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:message>
                    <xsl:text>[ERROR] No ItemType found for collection in AddObjectToCollection in Rule [</xsl:text>
                    <xsl:value-of select="ancestor::aorsl:EnvironmentRule/@name"/>
                    <xsl:text>]!</xsl:text>
                  </xsl:message>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="varName" select="$collectionVarName"/>
            <xsl:with-param name="withDeclaration" select="false()"/>
          </xsl:call-template>

          <xsl:call-template name="java:variable">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="castType" select="fn:concat($collection.class.aORCollection, '&lt;', $itemType, '&gt;')"/>
            <xsl:with-param name="name" select="$collectionVarName"/>
            <xsl:with-param name="value">
              <xsl:choose>
                <xsl:when test="@collectionObjectVariable">
                  <xsl:call-template name="java:varByDotNotation">
                    <xsl:with-param name="varName" select="@collectionObjectVariable"/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:when test="@collectionID">
                  <xsl:call-template name="java:callMethod">
                    <xsl:with-param name="objInstance">
                      <xsl:call-template name="java:callGetterMethod">
                        <xsl:with-param name="inLine" select="true()"/>
                        <xsl:with-param name="objInstance" select="'this'"/>
                        <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
                      </xsl:call-template>
                    </xsl:with-param>
                    <xsl:with-param name="method" select="'getCollectionById'"/>
                    <xsl:with-param name="args" select="@collectionID"/>
                    <xsl:with-param name="inLine" select="true()"/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:when test="@collectionName">
                  <xsl:call-template name="java:callMethod">
                    <xsl:with-param name="objInstance">
                      <xsl:call-template name="java:callGetterMethod">
                        <xsl:with-param name="inLine" select="true()"/>
                        <xsl:with-param name="objInstance" select="'this'"/>
                        <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
                      </xsl:call-template>
                    </xsl:with-param>
                    <xsl:with-param name="method" select="'getCollectionByName'"/>
                    <xsl:with-param name="args" select="jw:quote(@collectionName)"/>
                    <xsl:with-param name="inLine" select="true()"/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:message>
                    <xsl:text>[ERROR] Unknown Collection in AddToCollection in Ruele [</xsl:text>
                    <xsl:value-of select="ancestor::aorsl:EnvironmentRule/@name"/>
                    <xsl:text>]}!</xsl:text>
                  </xsl:message>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
          </xsl:call-template>

          <xsl:call-template name="java:callMethod">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="objInstance" select="$collectionVarName"> </xsl:with-param>
            <xsl:with-param name="method" select="'addObjekt'"/>
            <xsl:with-param name="args">
              <xsl:choose>
                <xsl:when test="aorsl:ItemObjectRef[matches(@language, $output.lang.RegExpr)]">
                  <xsl:value-of select="aorsl:ItemObjectRef[matches(@language, $output.lang.RegExpr)][1]"/>
                </xsl:when>
                <xsl:when test="@itemObjectVariable">
                  <xsl:value-of select="@itemObjectVariable"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:message>
                    <xsl:text>[ERROR] No Item to add to a collection in Rule [</xsl:text>
                    <xsl:value-of select="ancestor::aorsl:EnvironmentRule/@name"/>
                    <xsl:text>]!</xsl:text>
                  </xsl:message>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
          </xsl:call-template>

        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>

  </xsl:template>

  <!-- updates a gridCell -->
  <!-- this template is additional used by createInitialzationRule.xsl  and createActivities.xsl-->
  <xsl:template match="aorsl:UpdateGridCell" mode="createEnvironmentRules.method.stateEffects.content">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:variable name="gridCellVarName" select="if (@gridCellVariable) then @gridCellVariable else fn:concat('simGridCell_', position())"/>
    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="type" select="fn:concat($sim.class.simSpaceModel, '.', $sim.class.simGridCell)"/>
      <xsl:with-param name="name" select="$gridCellVarName"/>
      <xsl:with-param name="value">
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="inLine" select="true()"/>
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="$sim.class.simulatorMain"/>
              <xsl:with-param name="varName" select="'spaceModel'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="method" select="'getGridCell'"/>
          <xsl:with-param name="args" as="xs:string*">
            <xsl:variable name="xCoord" as="xs:string">
              <xsl:apply-templates select="aorsl:XCoordinate[matches(@language, $output.lang.RegExpr)]" mode="assistents.getExpression"/>
            </xsl:variable>
            <xsl:variable name="yCoord" as="xs:string">
              <xsl:apply-templates select="aorsl:YCoordinate[matches(@language, $output.lang.RegExpr)]" mode="assistents.getExpression"/>
            </xsl:variable>
            <xsl:value-of select="$xCoord"/>
            <xsl:value-of select="$yCoord"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

    <xsl:apply-templates select="aorsl:Increment" mode="assistents.increment">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objectVariable" select="$gridCellVarName"/>
    </xsl:apply-templates>

    <xsl:apply-templates select="aorsl:Decrement" mode="assistents.decrement">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objectVariable" select="$gridCellVarName"/>
    </xsl:apply-templates>

    <xsl:for-each select="aorsl:Slot">
      <xsl:call-template name="java:callSetterMethod">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="objInstance" select="$gridCellVarName"/>
        <xsl:with-param name="instVariable" select="@property"/>
        <xsl:with-param name="value">
          <xsl:apply-templates select="." mode="assistents.getSlotValue"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:for-each>

    <xsl:apply-templates select="aorsl:MultiValuedSlot" mode="assistent.setMultiValuedSlot">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objectContext" select="$gridCellVarName"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- updates all gridCells -->
  <!-- this template is additional used by createInitialzationRule.xsl  and createActivities.xsl-->
  <xsl:template match="aorsl:ForEachGridCell" mode="createEnvironmentRules.method.stateEffects.content">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:variable name="gridCellVarName"
      select="if (@gridCellVariable) then @gridCellVariable else fn:concat($createdVariablesNamePrefix, 'simGridCell_', position())"/>

    <xsl:variable name="indentOffset" as="xs:integer">
      <xsl:choose>
        <xsl:when test="fn:exists(aorsl:Selection/aorsl:Condition[matches(@language, $output.lang.RegExpr)])">
          <xsl:value-of select="1"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="3"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="content">

      <xsl:for-each select="aorsl:Code[matches(@language, $output.lang.RegExpr)]">
        <xsl:copy-of select="."/>
      </xsl:for-each>

      <xsl:apply-templates select="aorsl:Increment" mode="assistents.increment">
        <xsl:with-param name="indent" select="$indent + $indentOffset"/>
        <xsl:with-param name="objectVariable" select="$gridCellVarName"/>
      </xsl:apply-templates>

      <xsl:apply-templates select="aorsl:Decrement" mode="assistents.decrement">
        <xsl:with-param name="indent" select="$indent + $indentOffset"/>
        <xsl:with-param name="objectVariable" select="$gridCellVarName"/>
      </xsl:apply-templates>

      <xsl:for-each select="aorsl:Slot">
        <xsl:call-template name="java:callSetterMethod">
          <xsl:with-param name="indent" select="$indent + $indentOffset"/>
          <xsl:with-param name="objInstance" select="$gridCellVarName"/>
          <xsl:with-param name="instVariable" select="@property"/>
          <xsl:with-param name="value">
            <xsl:apply-templates select="." mode="assistents.getSlotValue"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:for-each>

      <xsl:apply-templates select="aorsl:MultiValuedSlot" mode="assistent.setMultiValuedSlot">
        <xsl:with-param name="indent" select="$indent + $indentOffset"/>
        <xsl:with-param name="objectContext" select="$gridCellVarName"/>
      </xsl:apply-templates>

    </xsl:variable>

    <xsl:variable name="colCounter" select="'i'"/>
    <xsl:variable name="rowCounter" select="'j'"/>
    <xsl:call-template name="java:newLine"/>

    <xsl:choose>
      <xsl:when test="not(exists(preceding-sibling::aorsl:ForEachGridCell[@gridCellVariable = current()/@gridCellVariable]))">
        <xsl:call-template name="java:newObject">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="class" select="fn:concat($sim.class.simSpaceModel, '.', $sim.class.simGridCell)"/>
          <xsl:with-param name="varName" select="$gridCellVarName"/>
          <xsl:with-param name="withDeclaration" select="false()"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="name" select="$gridCellVarName"/>
          <xsl:with-param name="value" select="'null'"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>

    <xsl:choose>

      <xsl:when test="fn:exists(aorsl:Selection/aorsl:Condition[matches(@language, $output.lang.RegExpr)])">

        <xsl:variable name="selectedCellsArrayVarName" select="fn:concat($createdVariablesNamePrefix, 'selectedCells_', position())"/>

        <xsl:call-template name="java:newArrayListObject">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="generic" select="$sim.package.simGridCell"/>
          <xsl:with-param name="name" select="$selectedCellsArrayVarName"/>
          <xsl:with-param name="withDeclaration" select="false()"/>
        </xsl:call-template>

        <xsl:for-each select="aorsl:Selection/aorsl:Condition[matches(@language, $output.lang.RegExpr)]">

          <xsl:call-template name="java:newArrayListObject">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="generic" select="fn:concat($sim.class.simSpaceModel, '.', $sim.class.simGridCell)"/>
            <xsl:with-param name="isVariable" select="true()"/>
            <xsl:with-param name="name" select="$selectedCellsArrayVarName"/>
          </xsl:call-template>

          <xsl:apply-templates select="../.." mode="createEnvironmentRules.method.stateEffects.createRuleGridLoop">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="colCounter" select="$colCounter"/>
            <xsl:with-param name="rowCounter" select="$rowCounter"/>
            <xsl:with-param name="gridCellVarName" select="$gridCellVarName"/>
            <xsl:with-param name="content">

              <xsl:apply-templates select="." mode="createEnvironmentRules.method.stateEffects.updateGridCells.selectionCondition">
                <xsl:with-param name="indent" select="$indent + 2"/>
                <xsl:with-param name="gridCellVariable" select="$gridCellVarName"/>
                <xsl:with-param name="selectedCellsArrayVarName" select="$selectedCellsArrayVarName"/>
              </xsl:apply-templates>

            </xsl:with-param>
          </xsl:apply-templates>
          <xsl:call-template name="java:newLine"/>

          <!-- this means no Code, Increment or Decrement or Slot element in ForEachGridCell-->
          <xsl:if test="not(empty($content/*))">
            <xsl:variable name="cellVarName" select="fn:concat($createdVariablesNamePrefix, 'cell')"/>
            <xsl:call-template name="java:for-each-loop">
              <xsl:with-param name="indent" select="$indent"/>
              <xsl:with-param name="elementType" select="$sim.package.simGridCell"/>
              <xsl:with-param name="elementVarName" select="$cellVarName"/>
              <xsl:with-param name="listVarName" select="$selectedCellsArrayVarName"/>
              <xsl:with-param name="content">

                <xsl:call-template name="java:variable">
                  <xsl:with-param name="indent" select="$indent"/>
                  <xsl:with-param name="name" select="$gridCellVarName"/>
                  <xsl:with-param name="value" select="$cellVarName"/>
                </xsl:call-template>

                <xsl:value-of select="$content"/>

              </xsl:with-param>
            </xsl:call-template>
          </xsl:if>

          <xsl:if test="../@copyToList">
            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent"/>
              <!-- works for Globals, because is in the imports -->
              <xsl:with-param name="name" select="../@copyToList"/>
              <xsl:with-param name="value" select="$selectedCellsArrayVarName"/>
            </xsl:call-template>
          </xsl:if>

        </xsl:for-each>

      </xsl:when>
      <xsl:otherwise>

        <xsl:apply-templates select="." mode="createEnvironmentRules.method.stateEffects.createRuleGridLoop">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="colCounter" select="$colCounter"/>
          <xsl:with-param name="rowCounter" select="$rowCounter"/>
          <xsl:with-param name="gridCellVarName" select="$gridCellVarName"/>
          <xsl:with-param name="content" select="$content"/>
        </xsl:apply-templates>

      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <xsl:template match="aorsl:Condition" mode="createEnvironmentRules.method.stateEffects.updateGridCells.selectionCondition">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="selectedCellsArrayVarName" as="xs:string" required="yes"/>
    <xsl:param name="gridCellVariable" as="xs:string" required="yes"/>

    <xsl:call-template name="java:if">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="condition" select="."/>
      <xsl:with-param name="thenContent">

        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent + 2"/>
          <xsl:with-param name="objInstance" select="$selectedCellsArrayVarName"/>
          <xsl:with-param name="method" select="'add'"/>
          <xsl:with-param name="args" select="$gridCellVariable"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="aorsl:ForEachGridCell[not(@selectionList)]" mode="createEnvironmentRules.method.stateEffects.createRuleGridLoop">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="colCounter" as="xs:string" required="yes"/>
    <xsl:param name="rowCounter" as="xs:string" required="yes"/>
    <xsl:param name="gridCellVarName" as="xs:string" required="yes"/>
    <xsl:param name="content"/>

    <!-- outer loop -->
    <xsl:call-template name="java:for-loop">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="loopVariable" select="$colCounter"/>
      <xsl:with-param name="start">
        <xsl:choose>
          <xsl:when test="@startX">
            <xsl:value-of select="@startX"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="$space.package.space.space"/>
              <xsl:with-param name="varName" select="'ORDINATEBASE'"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="condition">
        <xsl:call-template name="java:boolExpr">
          <xsl:with-param name="value1" select="$colCounter"/>
          <xsl:with-param name="value2">
            <xsl:choose>
              <xsl:when test="@endX">
                <xsl:value-of select="@endX"/>
                <xsl:text> + 1</xsl:text>
              </xsl:when>
              <xsl:otherwise>
                <xsl:call-template name="java:callGetterMethod">
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
                  <xsl:with-param name="instVariable" select="'xSize'"/>
                </xsl:call-template>
                <xsl:text> + </xsl:text>
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="name" select="$space.package.space.space"/>
                  <xsl:with-param name="varName" select="'ORDINATEBASE'"/>
                </xsl:call-template>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
          <xsl:with-param name="operator" select="'&lt;'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="increment" select="1"/>
      <xsl:with-param name="content">

        <!-- inner loop -->
        <xsl:call-template name="java:for-loop">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="loopVariable" select="$rowCounter"/>
          <xsl:with-param name="start">
            <xsl:choose>
              <xsl:when test="@startY">
                <xsl:value-of select="@startY"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="name" select="$space.package.space.space"/>
                  <xsl:with-param name="varName" select="'ORDINATEBASE'"/>
                </xsl:call-template>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
          <xsl:with-param name="condition">
            <xsl:call-template name="java:boolExpr">
              <xsl:with-param name="value1" select="$rowCounter"/>
              <xsl:with-param name="value2">

                <xsl:choose>
                  <xsl:when test="@endY">
                    <xsl:value-of select="@endY"/>
                    <xsl:text> + 1</xsl:text>
                  </xsl:when>
                  <xsl:otherwise>

                    <xsl:call-template name="java:callGetterMethod">
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
                      <xsl:with-param name="instVariable" select="'ySize'"/>
                    </xsl:call-template>
                    <xsl:text> + </xsl:text>
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="name" select="$space.package.space.space"/>
                      <xsl:with-param name="varName" select="'ORDINATEBASE'"/>
                    </xsl:call-template>

                  </xsl:otherwise>
                </xsl:choose>

              </xsl:with-param>
              <xsl:with-param name="operator" select="'&lt;'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="increment" select="1"/>
          <xsl:with-param name="content">

            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent + 2"/>
              <xsl:with-param name="name" select="$gridCellVarName"/>
              <xsl:with-param name="value">
                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="objInstance">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="name" select="$sim.class.simulatorMain"/>
                      <xsl:with-param name="varName" select="'spaceModel'"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="method" select="'getGridCell'"/>
                  <xsl:with-param name="args" as="xs:string*" select="($colCounter, $rowCounter)"/>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>

            <xsl:call-template name="java:if">
              <xsl:with-param name="indent" select="$indent + 2"/>
              <xsl:with-param name="condition">
                <xsl:call-template name="java:boolExpr">
                  <xsl:with-param name="value1" select="$gridCellVarName"/>
                  <xsl:with-param name="value2" select="'null'"/>
                  <xsl:with-param name="operator" select="'!='"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="thenContent">
                <xsl:value-of select="$content"/>
              </xsl:with-param>
            </xsl:call-template>

          </xsl:with-param>

        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsl:ForEachGridCell[@selectionList]" mode="createEnvironmentRules.method.stateEffects.createRuleGridLoop">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="gridCellVarName" as="xs:string" required="yes"/>
    <xsl:param name="content"/>

    <xsl:variable name="internalGridCellVarName" select="fn:concat($createdVariablesNamePrefix, 'gridCell')"/>

    <xsl:call-template name="java:for-each-loop">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="elementType" select="$sim.package.simGridCell"/>
      <xsl:with-param name="elementVarName" select="$internalGridCellVarName"/>
      <xsl:with-param name="listVarName" select="@selectionList"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:if">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="condition">
            <xsl:call-template name="java:boolExpr">
              <xsl:with-param name="value1" select="$internalGridCellVarName"/>
              <xsl:with-param name="value2" select="'null'"/>
              <xsl:with-param name="operator" select="'!='"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="thenContent">

            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent + 2"/>
              <xsl:with-param name="name" select="$gridCellVarName"/>
              <xsl:with-param name="value" select="$internalGridCellVarName"/>
            </xsl:call-template>

            <xsl:value-of select="$content"/>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- conditions() -->
  <xsl:template match="aorsl:EnvironmentRule" mode="createEnvironmentRules.method.conditions">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'protected'"/>
      <xsl:with-param name="type" select="'boolean'"/>
      <xsl:with-param name="name" select="'condition'"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">
            <!-- check if DataVariableDeclarations with refDataTypes isn't null -->
            <xsl:if test="fn:exists(aorsl:FOR[@dataVariable][@refDataType])">
              <xsl:value-of select="'('"/>
              <xsl:apply-templates select="aorsl:FOR[@dataVariable][@refDataType]" mode="assistents.dataVariableDeclarationcheckNull"/>
              <xsl:value-of select="') &amp;&amp; '"/>
            </xsl:if>
            <xsl:choose>
              <xsl:when test="fn:exists(aorsl:IF[matches(@language, $output.lang.RegExpr)]) and fn:normalize-space(aorsl:IF[matches(@language, $output.lang.RegExpr)]) != ''">
                <xsl:value-of select="fn:normalize-space(aorsl:IF[matches(@language, $output.lang.RegExpr)][1])"/>
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

  <!-- its possible to share it, but the returntype of the getmethod is differnt with agtRules -->
  <xsl:template match="aorsl:EnvironmentRule" mode="createEnvironmentRules.method.setGetTriggeringEvent">
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
          <xsl:with-param name="value" select="jw:quote(if (aorsl:WHEN/@eventType) then aorsl:WHEN/@eventType else $core.class.onEveryStepEnvEvent)"/>
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
              <xsl:when test="aorsl:WHEN/@eventType">
                <xsl:value-of select="aorsl:WHEN/@eventType"/>
              </xsl:when>
              <xsl:when test="aorsl:ON-EACH-SIMULATION-STEP">
                <xsl:value-of select="$core.package.onEveryStepEnvEvent"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:message>
                  <xsl:text>[ERROR]</xsl:text>
                </xsl:message>
              </xsl:otherwise>
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

        <!-- set the message -->
        <xsl:if
          test="aorsl:WHEN/@eventType eq $core.class.outMessageEvent and 
          fn:exists(aorsl:WHEN/@messageType) and 
          fn:exists(aorsl:WHEN/@messageVariable)">
          <xsl:call-template name="java:variable">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="name">
              <xsl:call-template name="java:varByDotNotation">
                <xsl:with-param name="varName" select="aorsl:WHEN/@messageVariable"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="castType" select="aorsl:WHEN/@messageType"/>
            <xsl:with-param name="value">
              <xsl:call-template name="java:callGetterMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="objInstance">
                  <xsl:call-template name="java:varByDotNotation">
                    <xsl:with-param name="varName" select="$eventVar"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="instVariable" select="'message'"/>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:if>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>


  <!-- ++++++++++++++++++++++++++++++ -->
  <!--           Helper               -->
  <!-- ++++++++++++++++++++++++++++++ -->
  <!-- helper: creates an Agent -->
  <xsl:template match="aorsl:Agent" mode="createEnvironmentRules.helper.method.createAgent">
    <xsl:param name="indent" required="yes"/>

    <xsl:variable name="varNameObj">
      <xsl:apply-templates select="." mode="createEnvironmentRules.helper.method.createObjekt.varName"/>
    </xsl:variable>
    <xsl:variable name="classNameObj" select="jw:upperWord(@type)"/>

    <!-- create AgentObject -->
    <xsl:if test="not (@objectVariable)">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$classNameObj"/>
        <xsl:with-param name="varName" select="$varNameObj"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:if>

    <xsl:apply-templates select="." mode="shared.helper.initAORObjects">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$classNameObj"/>
      <xsl:with-param name="varName" select="$varNameObj"/>
      <xsl:with-param name="id">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="indent" select="$indent"/>
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="objInstance" select="'this'"/>
                  <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="method" select="'getAutoId'"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>

      </xsl:with-param>
    </xsl:apply-templates>

    <xsl:variable name="varNameSubj" select="if (@name) then fn:concat(@name, 'Subject') else fn:concat(jw:lowerWord(@type), 'Subject')"/>
    <xsl:variable name="classNameSubj" select="fn:concat(jw:upperWord(@type), $prefix.agentSubject)"/>

    <!-- create AgentSubject -->
    <xsl:call-template name="java:newObject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="class" select="$classNameSubj"/>
      <xsl:with-param name="varName" select="$varNameSubj"/>
      <xsl:with-param name="withDeclaration" select="false()"/>
    </xsl:call-template>

    <xsl:apply-templates select="." mode="shared.helper.initAgentSubject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$classNameSubj"/>
      <xsl:with-param name="varName" select="$varNameSubj"/>
      <xsl:with-param name="id">
        <xsl:call-template name="java:callGetterMethod">
          <xsl:with-param name="objInstance" select="$varNameObj"/>
          <xsl:with-param name="instVariable" select="'id'"/>
          <xsl:with-param name="inLine" select="true()"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="dynamic" select="true()"/>
    </xsl:apply-templates>

    <!-- set the created in the environment -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance">
        <xsl:call-template name="java:callGetterMethod">
          <xsl:with-param name="objInstance" select="'this'"/>
          <xsl:with-param name="instVariable" select="'EnvironmentSimulator'"/>
          <xsl:with-param name="inLine" select="true()"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="method" select="'createAgent'"/>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:value-of select="$varNameObj"/>
        <xsl:value-of select="$varNameSubj"/>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>


  <!-- helper: creates an PhysicalAgent -->
  <xsl:template match="aorsl:PhysicalAgent" mode="createEnvironmentRules.helper.method.createPhysAgent">
    <xsl:param name="indent" required="yes"/>

    <xsl:variable name="varNameObj">
      <xsl:apply-templates select="." mode="createEnvironmentRules.helper.method.createObjekt.varName"/>
    </xsl:variable>
    <xsl:variable name="classNameObj" select="jw:upperWord(@type)"/>

    <!-- create PhysicalAgentObject -->
    <xsl:if test="not (@objectVariable)">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$classNameObj"/>
        <xsl:with-param name="varName" select="$varNameObj"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:if>

    <xsl:apply-templates select="." mode="shared.helper.initAORObjects">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$classNameObj"/>
      <xsl:with-param name="varName" select="$varNameObj"/>
      <xsl:with-param name="id">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="indent" select="$indent"/>
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="objInstance" select="'this'"/>
                  <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="method" select="'getAutoId'"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>

      </xsl:with-param>
    </xsl:apply-templates>

    <xsl:variable name="varNameSubj" select="if (@name) then fn:concat(@name, 'Subject') else fn:concat(jw:lowerWord(@type), 'Subject')"/>
    <xsl:variable name="classNameSubj" select="fn:concat(jw:upperWord(@type), $prefix.agentSubject)"/>

    <!-- create PhysicalAgentSubject -->
    <xsl:call-template name="java:newObject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="class" select="$classNameSubj"/>
      <xsl:with-param name="varName" select="$varNameSubj"/>
      <xsl:with-param name="withDeclaration" select="false()"/>
    </xsl:call-template>

    <xsl:apply-templates select="." mode="shared.helper.initAgentSubject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$classNameSubj"/>
      <xsl:with-param name="varName" select="$varNameSubj"/>
      <xsl:with-param name="id">
        <xsl:call-template name="java:callGetterMethod">
          <xsl:with-param name="objInstance" select="$varNameObj"/>
          <xsl:with-param name="instVariable" select="'id'"/>
          <xsl:with-param name="inLine" select="true()"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="dynamic" select="true()"/>
    </xsl:apply-templates>

    <!-- set the created in the environment -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance">
        <xsl:call-template name="java:callGetterMethod">
          <xsl:with-param name="objInstance" select="'this'"/>
          <xsl:with-param name="instVariable" select="'EnvironmentSimulator'"/>
          <xsl:with-param name="inLine" select="true()"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="method" select="'createAgent'"/>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:value-of select="$varNameObj"/>
        <xsl:value-of select="$varNameSubj"/>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsl:PhysicalObject" mode="createEnvironmentRules.helper.method.createPhysicalObjekt">
    <xsl:param name="indent" required="yes"/>

    <xsl:variable name="varNameObj">
      <xsl:apply-templates select="." mode="createEnvironmentRules.helper.method.createObjekt.varName"/>
    </xsl:variable>
    <xsl:variable name="classNameObj" select="jw:upperWord(@type)"/>

    <!-- create PhysicalObject -->
    <xsl:if test="not (@objectVariable)">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$classNameObj"/>
        <xsl:with-param name="varName" select="$varNameObj"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:if>

    <xsl:apply-templates select="." mode="shared.helper.initAORObjects">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$classNameObj"/>
      <xsl:with-param name="varName" select="$varNameObj"/>
      <xsl:with-param name="id">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="indent" select="$indent"/>
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="objInstance" select="'this'"/>
                  <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="method" select="'getAutoId'"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>

      </xsl:with-param>
    </xsl:apply-templates>

    <!-- set the created in the environment -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance">
        <xsl:call-template name="java:callGetterMethod">
          <xsl:with-param name="objInstance" select="'this'"/>
          <xsl:with-param name="instVariable" select="'EnvironmentSimulator'"/>
          <xsl:with-param name="inLine" select="true()"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="method" select="'createPhysicalObject'"/>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:value-of select="$varNameObj"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- helper: create agents from agentset -->
  <xsl:template match="aorsl:PhysicalAgents" mode="createEnvironmentRules.helper.method.createPhysAgent">
    <xsl:param name="indent" required="yes"/>

    <xsl:variable name="varNameObj" select="jw:lowerWord(@objectVariable)"/>
    <xsl:variable name="classNameObj" select="fn:concat(jw:upperWord(@type), $core.class.physAgentObject)"/>
    <xsl:variable name="varNameSubj" select="fn:concat($varNameObj, 'Subject')"/>
    <xsl:variable name="classNameSubj" select="fn:concat(jw:upperWord(@type), $prefix.agentSubject)"/>
    <xsl:variable name="creationLoopVar"
      select="if (fn:exists(@creationLoopVar) and @creationLoopVar != '') then @creationLoopVar else 'creationLoopVar'"/>

    <xsl:variable name="endId">
      <xsl:apply-templates select="." mode="assistents.getEndID"/>
    </xsl:variable>

    <!-- create AgentObject -->
    <xsl:call-template name="java:newObject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="class" select="$classNameObj"/>
      <xsl:with-param name="varName" select="$varNameObj"/>
      <xsl:with-param name="withDeclaration" select="false()"/>
    </xsl:call-template>

    <!-- create AgentSubject -->
    <xsl:call-template name="java:newObject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="class" select="$classNameSubj"/>
      <xsl:with-param name="varName" select="$varNameSubj"/>
      <xsl:with-param name="withDeclaration" select="false()"/>
    </xsl:call-template>
    <xsl:call-template name="java:newLine"/>

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
          <xsl:with-param name="value2" select="$endId"/>
          <xsl:with-param name="operator" select="'&lt;='"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="increment" select="1"/>
      <xsl:with-param name="content">

        <xsl:apply-templates select="." mode="shared.helper.initAORObjects">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="className" select="$classNameObj"/>
          <xsl:with-param name="varName" select="$varNameObj"/>
          <xsl:with-param name="id" select="$creationLoopVar"/>
        </xsl:apply-templates>

        <xsl:apply-templates select="." mode="shared.helper.initAgentSubject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="className" select="$classNameSubj"/>
          <xsl:with-param name="varName" select="$varNameSubj"/>
          <xsl:with-param name="id" select="$creationLoopVar"/>
        </xsl:apply-templates>

        <!-- set the created in the environment -->
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="objInstance" select="'this'"/>
              <xsl:with-param name="instVariable" select="'EnvironmentSimulator'"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="method" select="'createAgent'"/>
          <xsl:with-param name="args" as="xs:string*">
            <xsl:value-of select="$varNameObj"/>
            <xsl:value-of select="$varNameSubj"/>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- helper: creates an object -->
  <xsl:template match="aorsl:Object" mode="createEnvironmentRules.helper.method.createObjekt">
    <xsl:param name="indent" required="yes"/>

    <xsl:variable name="varNameObj">
      <xsl:apply-templates select="." mode="createEnvironmentRules.helper.method.createObjekt.varName"/>
    </xsl:variable>
    <xsl:variable name="classNameObj" select="jw:upperWord(@type)"/>

    <!-- create Object -->
    <xsl:if test="not (@objectVariable)">
      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="class" select="$classNameObj"/>
        <xsl:with-param name="varName" select="$varNameObj"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
    </xsl:if>

    <!-- set id -->
    <xsl:variable name="id">
      <xsl:choose>
        <xsl:when test="fn:exists(@id)">
          <xsl:value-of select="@id"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="java:callMethod">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="objInstance">
              <xsl:call-template name="java:callGetterMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="objInstance" select="'this'"/>
                <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="method" select="'getAutoId'"/>
            <xsl:with-param name="inLine" select="true()"/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:apply-templates select="." mode="shared.helper.initAORObjects">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="className" select="$classNameObj"/>
      <xsl:with-param name="varName" select="$varNameObj"/>
      <xsl:with-param name="id" select="$id"/>
    </xsl:apply-templates>

    <!-- set the created in the environment -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance">
        <xsl:call-template name="java:callGetterMethod">
          <xsl:with-param name="objInstance" select="'this'"/>
          <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
          <xsl:with-param name="inLine" select="true()"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="method" select="'createObjekt'"/>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:value-of select="$varNameObj"/>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- helper: create objects from objectset -->
  <xsl:template match="aorsl:Objects" mode="createEnvironmentRules.helper.method.createObjekt">
    <xsl:param name="indent" required="yes"/>

    <xsl:variable name="varNameObj">
      <xsl:apply-templates select="." mode="createEnvironmentRules.helper.method.createObjekt.varName"/>
    </xsl:variable>
    <xsl:variable name="classNameObj" select="jw:upperWord(@type)"/>
    <xsl:variable name="creationLoopVar"
      select="if (fn:exists(@creationLoopVar) and @creationLoopVar != '') then @creationLoopVar else jw:createInternalVarName('creationLoopVar')"/>

    <xsl:variable name="endId">
      <xsl:apply-templates select="." mode="assistents.getEndID"/>
    </xsl:variable>

    <!-- create Object -->
    <xsl:call-template name="java:newObject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="class" select="$classNameObj"/>
      <xsl:with-param name="varName" select="$varNameObj"/>
      <xsl:with-param name="withDeclaration" select="false()"/>
    </xsl:call-template>
    <xsl:call-template name="java:newLine"/>

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
          <xsl:with-param name="value2" select="$endId"/>
          <xsl:with-param name="operator" select="'&lt;='"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="increment" select="1"/>
      <xsl:with-param name="content">

        <!-- calls direct the object creation without the detour via shared.helper.initAORObjectsSet -->
        <xsl:apply-templates select="." mode="shared.helper.initAORObjects">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="className" select="$classNameObj"/>
          <xsl:with-param name="varName" select="$varNameObj"/>
          <xsl:with-param name="id" select="$creationLoopVar"/>
        </xsl:apply-templates>

        <!-- set the created in the environment -->
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="objInstance" select="'this'"/>
              <xsl:with-param name="instVariable" select="'EnvironmentSimulator'"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="method" select="'createObjekt'"/>
          <xsl:with-param name="args" as="xs:string*">
            <xsl:value-of select="$varNameObj"/>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- helper: destroy objekt -->
  <xsl:template match="aorsl:DestroyObject" mode="createEnvironmentRules.helper.method.destroyObject">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="objectCollElemVarName" select="''" as="xs:string"/>

    <xsl:variable name="physObjId">
      <xsl:choose>
        <xsl:when test="aorsl:ObjectRef[matches(@language, $output.lang.RegExpr)]">
          <xsl:call-template name="java:callGetterMethod">
            <xsl:with-param name="inLine" select="true()"/>
            <xsl:with-param name="objInstance">
              <xsl:value-of select="aorsl:ObjectRef[matches(@language, $output.lang.RegExpr)]"/>
            </xsl:with-param>
            <xsl:with-param name="instVariable" select="'id'"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:when test="aorsl:ObjectIdRef[matches(@language, $output.lang.RegExpr)]">
          <xsl:value-of select="aorsl:ObjectIdRef[matches(@language, $output.lang.RegExpr)][1]"/>
        </xsl:when>
        <xsl:when test="fn:exists(@objectIdRef)">
          <xsl:value-of select="@objectIdRef"/>
        </xsl:when>
        <xsl:when test="fn:exists(@objectVariable)">
          <xsl:call-template name="java:callGetterMethod">
            <xsl:with-param name="inLine" select="true()"/>
            <xsl:with-param name="objInstance" select="@objectVariable"/>
            <xsl:with-param name="instVariable" select="'id'"/>
          </xsl:call-template>
        </xsl:when>
      </xsl:choose>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$physObjId != ''">
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="objInstance" select="'this'"/>
              <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="method" select="'destroyObject'"/>
          <xsl:with-param name="args" as="xs:string*">
            <xsl:value-of select="$physObjId"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <!-- if we don't have @objectIdRef, ObjectRef or @objectVariable, but  @removeFromCollection -->
      <xsl:when
        test="fn:exists(@removeFromCollection) and $objectCollElemVarName != '' and
        fn:exists(//aorsl:Collections/aorsl:Collection[@name = current()/@removeFromCollection])">
        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="name" select="$objectCollElemVarName"/>
          <xsl:with-param name="value">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="typecast" select="$core.class.object"/>
              <xsl:with-param name="objInstance" select="jw:lowerWord(./@removeFromCollection)"/>
              <xsl:with-param name="method" select="'removeObjekt'"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
        <!-- if not null -->
        <xsl:call-template name="java:if">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="condition">
            <xsl:call-template name="java:boolExpr">
              <xsl:with-param name="value1" select="$objectCollElemVarName"/>
              <xsl:with-param name="value2" select="'null'"/>
              <xsl:with-param name="operator" as="xs:string*" select="'!='"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="thenContent">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="objInstance" select="'this'"/>
                  <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
                  <xsl:with-param name="inLine" select="true()"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="method" select="'destroyObject'"/>
              <xsl:with-param name="args" as="xs:string*">
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="objInstance" select="$objectCollElemVarName"/>
                  <xsl:with-param name="instVariable" select="'id'"/>
                  <xsl:with-param name="inLine" select="true()"/>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- helper: destroy RemoveObjectFromCollection with @destroy = true-->
  <xsl:template match="aorsl:RemoveObjectFromCollection" mode="createEnvironmentRules.helper.method.destroyRemoveObjectFromCollection">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:variable name="collection" as="node()">
      <xsl:apply-templates select="." mode="createEnvironmentRules.helper.getCollectionFromRemoveObjectFromCollection"/>
    </xsl:variable>

    <xsl:variable name="classVarName">
      <xsl:choose>
        <xsl:when test="fn:exists(@itemObjectVariable)">
          <xsl:value-of select="@itemObjectVariable"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="fn:concat('removeFrom', $collection/@name, $collection/@id)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:call-template name="java:if">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="condition">
        <xsl:call-template name="java:boolExpr">
          <xsl:with-param name="value1">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="varName" select="$classVarName"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="value2" select="'null'"/>
          <xsl:with-param name="operator" as="xs:string*" select="'!='"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="thenContent">
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="objInstance" select="'this'"/>
              <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="method" select="'destroyObject'"/>
          <xsl:with-param name="args" as="xs:string*">
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance" select="$classVarName"/>
              <xsl:with-param name="instVariable" select="'id'"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="elseContent">
        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value" select="'false'"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- helper: destroy entities from a set -->
  <xsl:template match="aorsl:DestroyObjects" mode="createEnvironmentRules.helper.method.destroyObjects">
    <xsl:param name="indent" required="yes"/>

    <xsl:variable name="endId">
      <xsl:apply-templates select="." mode="assistents.getEndID"/>
    </xsl:variable>

    <xsl:call-template name="java:for-loop">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="loopVariable" select="'i'"/>
      <xsl:with-param name="loopVarType" select="'long'"/>
      <xsl:with-param name="start">
        <xsl:apply-templates select="." mode="assistents.getStartID"/>
      </xsl:with-param>
      <xsl:with-param name="condition">
        <xsl:call-template name="java:boolExpr">
          <xsl:with-param name="value1" select="'i'"/>
          <xsl:with-param name="value2" select="$endId"/>
          <xsl:with-param name="operator" select="'&lt;='"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="increment" select="1"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="objInstance" select="'this'"/>
              <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="method" select="'destroyObject'"/>
          <xsl:with-param name="args" as="xs:string*">
            <xsl:value-of select="'i'"/>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- helper -->
  <!-- this template is additional used by createInitialzationRule.xsl and createActivities.xsl -->
  <xsl:template match="aorsl:UpdateObject" mode="createEnvironmentRules.method.stateEffects.content">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:variable name="objectVariable">
      <xsl:choose>
        <xsl:when test="fn:exists(@objectVariable)">
          <xsl:value-of select="@objectVariable"/>
        </xsl:when>
        <xsl:when test="fn:exists(aorsl:ObjectRef[matches(@language, $output.lang.RegExpr)])">
          <!-- @objectType is required, but still we check it  for potential changes-->
          <xsl:choose>
            <!-- TODO: delete this part, if the changes in repo maked-->
            <xsl:when test="@objectType">
              <xsl:value-of select="fn:concat('((', @objectType, ')', aorsl:ObjectRef[matches(@language, $output.lang.RegExpr)],')')"/>
            </xsl:when>
            <!-- ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ -->
            <xsl:when test="aorsl:ObjectRef[matches(@language, $output.lang.RegExpr)]/@objectType">
              <xsl:value-of
                select="fn:concat('((', aorsl:ObjectRef[matches(@language, $output.lang.RegExpr)]/@objectType, ')', aorsl:ObjectRef[matches(@language, $output.lang.RegExpr)],')')"
              />
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="fn:concat('(', aorsl:ObjectRef[matches(@language, $output.lang.RegExpr)],')')"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
      </xsl:choose>
    </xsl:variable>

    <xsl:apply-templates select="aorsl:Call" mode="createEnvironmentRules.method.stateEffects.content.call">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objectVarName" select="$objectVariable"/>
      <xsl:with-param name="objectType" as="xs:string*">
        <xsl:choose>
          <xsl:when test="@objectVariable">
            <xsl:value-of select="ancestor::aorsl:EnvironmentRule/aorsl:FOR[@objectVariable =
              current()/@objectVariable][1]/@objectType"/>
          </xsl:when>
          <xsl:when test="exists(aorsl:ObjectRef[matches(@language, $output.lang.RegExpr)])">
            <xsl:value-of select="aorsl:ObjectRef[matches(@language, $output.lang.RegExpr)][1]/@objectType"/>
          </xsl:when>
        </xsl:choose>
      </xsl:with-param>
    </xsl:apply-templates>

    <xsl:for-each select="aorsl:Slot">
      <xsl:call-template name="java:callSetterMethod">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="objInstance" select="$objectVariable"/>
        <xsl:with-param name="instVariable" select="@property"/>
        <xsl:with-param name="value">
          <xsl:apply-templates select="." mode="assistents.getSlotValue"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:for-each>

    <xsl:apply-templates select="aorsl:MultiValuedSlot" mode="assistent.setMultiValuedSlot">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objectContext">
        <xsl:choose>
          <xsl:when test="@objectVariable">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="varName" select="@objectVariable"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="exists(aorsl:ObjectRef[matches(@language, $output.lang.RegExpr)])">
            <xsl:value-of select="jw:parenthesise(aorsl:ObjectRef[matches(@language, $output.lang.RegExpr)][1])"/>
          </xsl:when>
        </xsl:choose>

      </xsl:with-param>
    </xsl:apply-templates>

    <xsl:apply-templates select="aorsl:Increment" mode="assistents.increment">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objectVariable" select="$objectVariable"/>
    </xsl:apply-templates>

    <xsl:apply-templates select="aorsl:Decrement" mode="assistents.decrement">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objectVariable" select="$objectVariable"/>
    </xsl:apply-templates>

  </xsl:template>

  <xsl:template match="aorsl:Call" mode="createEnvironmentRules.method.stateEffects.content">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:apply-templates select="." mode="createEnvironmentRules.method.stateEffects.content.call">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objectVarName">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="varName" select="@contextObjectVariable"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="objectType">
        <xsl:value-of select="ancestor::aorsl:EnvironmentRule/aorsl:FOR[@objectVariable =
          current()/@contextObjectVariable][1]/@objectType"
        />
      </xsl:with-param>
    </xsl:apply-templates>

  </xsl:template>

  <xsl:template match="aorsl:Call" mode="createEnvironmentRules.method.stateEffects.content.call">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="objectVarName" as="xs:string" required="yes"/>
    <xsl:param name="objectType" as="xs:string" select="''"/>

    <xsl:variable name="funct" as="element()*">
      <xsl:call-template name="getObjectFunction">
        <xsl:with-param name="objectType" select="$objectType"/>
        <xsl:with-param name="functionName" select="@procedure"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="call" select="."/>

    <xsl:choose>
      <xsl:when test="exists($funct)">
        <xsl:choose>
          <xsl:when test="count(aorsl:Argument) = count($funct/aorsl:Parameter)">

            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="objInstance" select="$objectVarName"/>
              <xsl:with-param name="method" select="$funct/@name"/>
              <xsl:with-param name="args" as="item()*">

                <xsl:for-each select="$funct/aorsl:Parameter">
                  <xsl:variable name="argument" select="$call/aorsl:Argument[@parameter = current()/@name]"/>
                  <xsl:choose>
                    <xsl:when test="exists($argument)">
                      <xsl:choose>
                        <xsl:when test="$argument/@value">
                          <xsl:choose>
                            <xsl:when test="@type = 'String'">
                              <xsl:value-of select="jw:quote($argument/@value)"/>
                            </xsl:when>
                            <xsl:otherwise>
                              <xsl:value-of select="$argument/@value"/>
                            </xsl:otherwise>
                          </xsl:choose>
                        </xsl:when>
                        <xsl:when test="exists($argument/aorsl:ValueExpr[matches(@language, $output.lang.RegExpr)])">
                          <xsl:value-of select="$argument/aorsl:ValueExpr[matches(@language, $output.lang.RegExpr)][1]"/>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:message>
                            <xsl:text>[ERROR] No value for </xsl:text>
                            <xsl:value-of select="local-name($argument)"/>
                            <xsl:text> found in </xsl:text>
                            <xsl:value-of select="local-name($call)"/>
                            <xsl:text>!</xsl:text>
                          </xsl:message>
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
          <xsl:text>] found.</xsl:text>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!--sets state effects-->
  <!-- this template is additional used by createInitialzationRule.xsl and by createActivities.xsl -->
  <xsl:template match="aorsl:UpdateObjects" mode="createEnvironmentRules.helper.method.stateEffects">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:variable name="loopVariable" select="if (exists(@loopVariable)) then @loopVariable else 'i'"/>

    <xsl:call-template name="java:for-loop">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="loopVariable" select="$loopVariable"/>
      <xsl:with-param name="loopVarType" select="'long'"/>
      <xsl:with-param name="start">
        <xsl:apply-templates select="." mode="assistents.getStartID"/>
      </xsl:with-param>
      <xsl:with-param name="condition">
        <xsl:call-template name="java:boolExpr">
          <xsl:with-param name="value1" select="$loopVariable"/>
          <xsl:with-param name="value2">
            <xsl:apply-templates select="." mode="assistents.getEndID"/>
          </xsl:with-param>
          <xsl:with-param name="operator" select="'&lt;='"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="increment" select="1"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="castType" select="@objectType"/>
          <xsl:with-param name="type" select="@objectType"/>
          <xsl:with-param name="name" select="@objectVariable"/>
          <xsl:with-param name="value">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="objInstance" select="'this'"/>
                  <xsl:with-param name="instVariable" select="'EnvironmentSimulator'"/>
                  <xsl:with-param name="inLine" select="true()"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="method">
                <xsl:choose>
                  <xsl:when test="fn:exists(//aorsl:PhysicalAgentType[@name = current()/@objectType])">
                    <xsl:value-of select="'getPhysicalAgentById'"/>
                  </xsl:when>
                  <xsl:when test="fn:exists(//aorsl:PhysicalObjectType[@name = current()/@objectType])">
                    <xsl:value-of select="'getPhysicalObjectById'"/>
                  </xsl:when>
                  <xsl:when test="fn:exists(//aorsl:ObjectType[@name = current()/@objectType])">
                    <xsl:value-of select="'getPhysObjectById'"/>
                  </xsl:when>
                  <xsl:when test="fn:exists(//aorsl:AgentType[@name = current()/@objectType])">
                    <xsl:value-of select="'getAgentById'"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:message select="fn:concat('Error - strange type: ', @objectType)"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
              <xsl:with-param name="args" as="xs:string*">
                <xsl:value-of select="$loopVariable"/>
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="name" select="@objectType"/>
                  <xsl:with-param name="varName" select="'class'"/>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>

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

            <xsl:for-each select="aorsl:Slot">
              <xsl:call-template name="java:callSetterMethod">
                <xsl:with-param name="indent" select="$indent + 2"/>
                <xsl:with-param name="objInstance" select="../@objectVariable"/>
                <xsl:with-param name="instVariable" select="@property"/>
                <xsl:with-param name="value">
                  <xsl:apply-templates select="." mode="assistents.getSlotValue"/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:for-each>

            <xsl:apply-templates select="aorsl:Increment" mode="assistents.increment">
              <xsl:with-param name="indent" select="$indent"/>
              <xsl:with-param name="objectVariable" select="../@objectVariable"/>
            </xsl:apply-templates>

            <xsl:apply-templates select="aorsl:Decrement" mode="assistents.decrement">
              <xsl:with-param name="indent" select="$indent"/>
              <xsl:with-param name="objectVariable" select="../@objectVariable"/>
            </xsl:apply-templates>

            <xsl:apply-templates select="aorsl:MultiValuedSlot" mode="assistent.setMultiValuedSlot">
              <xsl:with-param name="indent" select="$indent"/>
              <xsl:with-param name="objectContext" select="../@objectVariable"/>
            </xsl:apply-templates>

          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- set CausedEvents -->
  <xsl:template match="aorsl:CausedEventExpr" mode="createEnvironmentRules.helper.method.resultingEvents">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="resultList" required="yes" as="xs:string"/>
    <xsl:param name="triggeringEventVar" required="yes" as="xs:string" tunnel="yes"/>

    <xsl:variable name="isStopSimulation" as="xs:boolean">
      <xsl:choose>
        <xsl:when test="@eventType = $core.class.stopSimulationEvent.alias">
          <xsl:value-of select="true()"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="false()"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:variable name="eventType" select="if ($isStopSimulation) then $core.class.stopSimulationEvent else @eventType"/>
    <xsl:variable name="eventVar" select="fn:concat(jw:lowerWord($eventType), '_', position())"/>

    <xsl:variable name="delay" as="xs:string">
      <xsl:apply-templates select="." mode="assistents.getDelay"/>
    </xsl:variable>

    <xsl:variable name="currentStep" as="xs:string">
      <xsl:call-template name="java:callGetterMethod">
        <xsl:with-param name="objInstance">
          <xsl:call-template name="java:varByDotNotation">
            <xsl:with-param name="name" select="'this'"/>
            <xsl:with-param name="varName" select="$triggeringEventVar"/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="instVariable" select="'occurrenceTime'"/>
        <xsl:with-param name="inLine" select="true()"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="output">
      <xsl:variable name="block-indent" select="if (fn:exists(aorsl:Condition[matches(@language, $output.lang.RegExpr)])) then $indent + 1 else $indent"/>

      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$block-indent"/>
        <xsl:with-param name="class" select="$eventType"/>
        <xsl:with-param name="varName" select="$eventVar"/>
        <xsl:with-param name="args" select="fn:concat($currentStep, ' + ', $delay)"/>
      </xsl:call-template>

      <!-- WARNING -->
      <xsl:if test="fn:exists(aorsl:Slot[@property = 'occurrenceTime'])">
        <xsl:message>The use of occurrenceTime is not allowed in CausedEventExpressionSlot!</xsl:message>
      </xsl:if>

      <xsl:for-each select="aorsl:Slot[@property != 'occurrenceTime']">
        <xsl:choose>
          <xsl:when
            test="//aorsl:CausedEventType[@name = $eventType][aorsl:Attribute/@name = current()/@property or 
                                                                                                                    aorsl:ReferenceProperty/@name = current()/@property or 
                                                                                                                    aorsl:ComplexDataProperty/@name = current()/@property or 
                                                                                                                    aorsl:EnumerationProperty/@name = current()/@property]">
            <xsl:call-template name="java:callSetterMethod">
              <xsl:with-param name="indent" select="$block-indent"/>
              <xsl:with-param name="objInstance" select="$eventVar"/>
              <xsl:with-param name="instVariable" select="@property"/>
              <xsl:with-param name="value">
                <xsl:apply-templates select="." mode="assistents.getSlotValue"/>
              </xsl:with-param>
              <xsl:with-param name="valueType">
                <xsl:value-of select="//aorsl:CausedEventType[@name = $eventType][aorsl:*/@name = current()/@property]/@type"/>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message>
              <xsl:text>[ERROR] Wrong property in Slot (property: </xsl:text>
              <xsl:value-of select="@property"/>
              <xsl:text>) in Rule </xsl:text>
              <xsl:value-of select="../../../../@name"/>
            </xsl:message>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>

      <!-- add the event -->
      <xsl:call-template name="java:callMethod">
        <xsl:with-param name="indent" select="$block-indent"/>
        <xsl:with-param name="objInstance" select="$resultList"/>
        <xsl:with-param name="method" select="'add'"/>
        <xsl:with-param name="args" as="xs:string*" select="$eventVar"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:apply-templates select="." mode="assistent.resultingEvent.output">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="output" select="$output"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- set PerceptionEvents -->
  <xsl:template match="aorsl:PerceptionEventExpr" mode="createEnvironmentRules.helper.method.resultingEvents">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="resultList" required="yes" as="xs:string"/>
    <xsl:param name="triggeringEventVar" required="yes" as="xs:string" tunnel="yes"/>

    <xsl:variable name="eventType" select="@eventType"/>
    <xsl:variable name="eventVar" select="fn:concat(jw:lowerWord($eventType), '_', position())"/>

    <!-- get the delay -->
    <xsl:variable name="delay">
      <xsl:apply-templates select="." mode="assistents.getDelay"/>
    </xsl:variable>

    <!-- define variables her because they will be used in a for-each -->
    <xsl:variable name="occurrenceTime" as="xs:string">
      <xsl:choose>
        <xsl:when test="fn:exists(aorsl:Slot[@property = 'occurrenceTime'])">
          <xsl:apply-templates select="aorsl:Slot[@property = 'occurrenceTime']" mode="assistents.getSlotValue"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:variable name="param">
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="name" select="'this'"/>
                  <xsl:with-param name="varName" select="$triggeringEventVar"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="instVariable" select="'occurrenceTime'"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:variable>
          <xsl:value-of select="fn:concat($param, ' + ', $delay)"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="slots" select="aorsl:Slot[@property != 'occurrenceTime']"/>
    <xsl:variable name="perceiverIdRefs1" as="xs:string*">
      <!-- necessary if someone use two spaces -->
      <xsl:for-each select="fn:tokenize(@perceiverIdRefs, ' ')">
        <xsl:if test=". != ''">
          <xsl:value-of select="."/>
        </xsl:if>
      </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="perceiverIdRef2" select="aorsl:PerceiverIdRef[matches(@language, $output.lang.RegExpr)]"/>

    <xsl:variable name="output">
      <xsl:variable name="block-indent" select="if (fn:exists(aorsl:Condition[matches(@language, $output.lang.RegExpr)])) then $indent + 1 else $indent"/>

      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$block-indent"/>
        <xsl:with-param name="class" select="@eventType"/>
        <xsl:with-param name="varName" select="$eventVar"/>
        <xsl:with-param name="withDeclaration" select="false()"/>
      </xsl:call-template>
      <xsl:call-template name="java:newLine"/>

      <!-- for each perceiver  -->
      <xsl:for-each select="($perceiverIdRefs1, $perceiverIdRef2)">

        <xsl:call-template name="java:newObject">
          <xsl:with-param name="indent" select="$block-indent"/>
          <xsl:with-param name="class" select="$eventType"/>
          <xsl:with-param name="varName" select="$eventVar"/>
          <xsl:with-param name="isVariable" select="true()"/>
          <xsl:with-param name="args" as="xs:string*">
            <xsl:value-of select="$occurrenceTime"/>
            <xsl:value-of select="."/>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:for-each select="$slots">
          <xsl:choose>
            <xsl:when
              test="//aorsl:PerceptionEventType[@name = $eventType][aorsl:Attribute/@name = current()/@property or 
                  aorsl:ReferenceProperty/@name = current()/@property or 
                  aorsl:ComplexDataProperty/@name = current()/@property or 
                  aorsl:EnumerationProperty/@name = current()/@property]">
              <xsl:call-template name="java:callSetterMethod">
                <xsl:with-param name="indent" select="$block-indent"/>
                <xsl:with-param name="objInstance" select="$eventVar"/>
                <xsl:with-param name="instVariable" select="@property"/>
                <xsl:with-param name="value">
                  <xsl:apply-templates select="." mode="assistents.getSlotValue"/>
                </xsl:with-param>
                <xsl:with-param name="valueType">
                  <xsl:value-of select="//aorsl:PerceptionEventType[@name = $eventType][aorsl:*/@name = current()/@property][1]/@type"/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:message>
                <xsl:text>[ERROR] Wrong property</xsl:text>
              </xsl:message>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:for-each>

        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$block-indent"/>
          <xsl:with-param name="objInstance" select="$resultList"/>
          <xsl:with-param name="method" select="'add'"/>
          <xsl:with-param name="args" as="xs:string*" select="$eventVar"/>
        </xsl:call-template>

      </xsl:for-each>

    </xsl:variable>

    <xsl:apply-templates select="." mode="assistent.resultingEvent.output">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="output" select="$output"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- set InMessageEvents -->
  <!-- it is complete untested -->
  <xsl:template match="aorsl:InMessageEventExpr" mode="createEnvironmentRules.helper.method.resultingEvents">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="resultList" required="yes" as="xs:string"/>
    <xsl:param name="triggeringEventVar" required="yes" as="xs:string" tunnel="yes"/>

    <xsl:variable name="messageNode" select="//aorsl:MessageType[@name = current()/@messageType]"/>
    <!-- get the delay -->
    <xsl:variable name="delay">
      <xsl:apply-templates select="." mode="assistents.getDelay"/>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="fn:exists($messageNode)">

        <!-- create a new message -->
        <xsl:variable name="messageVarName" select="fn:concat(jw:lowerWord($messageNode/@name), '_', position())"/>


        <xsl:variable name="output">
          <xsl:variable name="block-indent" select="if (fn:exists(aorsl:Condition[matches(@language, $output.lang.RegExpr)])) then $indent + 1 else $indent"/>

          <xsl:call-template name="java:newObject">
            <xsl:with-param name="indent" select="$block-indent"/>
            <xsl:with-param name="class" select="$messageNode/@name"/>
            <xsl:with-param name="varName" select="$messageVarName"/>
          </xsl:call-template>

          <xsl:for-each select="aorsl:Slot">

            <xsl:call-template name="java:callSetterMethod">
              <xsl:with-param name="indent" select="$block-indent"/>
              <xsl:with-param name="objInstance" select="$messageVarName"/>
              <xsl:with-param name="instVariable" select="@property"/>
              <xsl:with-param name="value">
                <xsl:apply-templates select="." mode="assistents.getSlotValue"/>
              </xsl:with-param>
              <!-- TODO: check whats happen when we have a slot with aors:SimpleSlot and a valuetype string -->
            </xsl:call-template>

          </xsl:for-each>

          <xsl:variable name="inMessageType" select="$core.class.inMessageEvent"/>
          <xsl:variable name="imVarName" select="fn:concat(jw:lowerWord($inMessageType), '_', position())"/>

          <xsl:call-template name="java:newObject">
            <xsl:with-param name="indent" select="$block-indent"/>
            <xsl:with-param name="class" select="$inMessageType"/>
            <xsl:with-param name="varName" select="$imVarName"/>
            <xsl:with-param name="withDeclaration" select="false()"/>
          </xsl:call-template>
          <xsl:call-template name="java:newLine"/>

          <!-- its a mapping from OutMessageEvent to InMessageEvent; we use the receiver/sender from OutMessage -->
          <!-- TODO: implement DO/THEN/ELSE -->
          <xsl:choose>
            <xsl:when
              test="./ancestor::*/aorsl:WHEN/@eventType = $core.class.outMessageEvent and 
              ./ancestor::*/aorsl:WHEN/@messageType = current()/@messageType 
                and (not(aorsl:ReceiverIdRef or @receiverIdRefs) and not(aorsl:SenderIdRef or @senderIdRef))">

              <!-- if the modeller don't define a delay then we use '0' as default; in this case we save one step -->
              <xsl:variable name="mapping-delay">
                <xsl:choose>
                  <xsl:when test="fn:exists(@delay)">
                    <xsl:value-of select="@delay"/>
                  </xsl:when>
                  <xsl:when test="fn:exists(aorsl:Delay/aorsl:ValueExpr[matches(@language, $output.lang.RegExpr)])">
                    <xsl:value-of select="aorsl:Delay/aorsl:ValueExpr[matches(@language, $output.lang.RegExpr)][1]"/>
                  </xsl:when>
                  <xsl:when test="fn:exists(aorsl:Delay/aorsl:DiscreteRandomVariable)">
                    <xsl:apply-templates select="aorsl:Delay/aorsl:DiscreteRandomVariable/aorsl:*" mode="assistents.distribution"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="'0'"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:variable>

              <xsl:call-template name="java:variable">
                <xsl:with-param name="indent" select="$block-indent"/>
                <xsl:with-param name="name" select="$imVarName"/>
                <xsl:with-param name="value">
                  <xsl:call-template name="java:newObject">
                    <xsl:with-param name="indent" select="$indent"/>
                    <xsl:with-param name="class" select="$inMessageType"/>
                    <xsl:with-param name="varName" select="$imVarName"/>
                    <xsl:with-param name="args" as="xs:string*">
                      <!-- occurenceTime -->
                      <xsl:variable name="param1" as="xs:string">
                        <xsl:call-template name="java:callGetterMethod">
                          <xsl:with-param name="objInstance">
                            <xsl:call-template name="java:varByDotNotation">
                              <xsl:with-param name="name" select="'this'"/>
                              <xsl:with-param name="varName" select="$triggeringEventVar"/>
                            </xsl:call-template>
                          </xsl:with-param>
                          <xsl:with-param name="instVariable" select="'occurrenceTime'"/>
                          <xsl:with-param name="inLine" select="true()"/>
                        </xsl:call-template>
                      </xsl:variable>
                      <!-- receiverIdRef -->
                      <xsl:variable name="param2" as="xs:string">
                        <xsl:call-template name="java:callGetterMethod">
                          <xsl:with-param name="objInstance" select="$triggeringEventVar"/>
                          <xsl:with-param name="instVariable" select="'receiverIdRef'"/>
                          <xsl:with-param name="inLine" select="true()"/>
                        </xsl:call-template>
                      </xsl:variable>
                      <!-- senderIdRef -->
                      <xsl:variable name="param3" as="xs:string">
                        <xsl:call-template name="java:callGetterMethod">
                          <xsl:with-param name="objInstance" select="$triggeringEventVar"/>
                          <xsl:with-param name="instVariable" select="'actorIdRef'"/>
                          <xsl:with-param name="inLine" select="true()"/>
                        </xsl:call-template>
                      </xsl:variable>

                      <!-- set the params -->
                      <!-- occurenceTime -->
                      <xsl:value-of select="fn:concat($param1, ' + ', $mapping-delay)"/>
                      <!-- receiverIdRef -->
                      <xsl:value-of select="$param2"/>
                      <!-- senderIdRef -->
                      <xsl:value-of select="$param3"/>
                      <!-- messageTypeObj -->
                      <xsl:value-of select="$messageVarName"/>
                    </xsl:with-param>
                    <xsl:with-param name="inLine" select="true()"/>
                    <xsl:with-param name="onlyInitialization" select="true()"/>
                  </xsl:call-template>

                  <xsl:call-template name="java:callMethod">
                    <xsl:with-param name="indent" select="$block-indent"/>
                    <xsl:with-param name="objInstance" select="$resultList"/>
                    <xsl:with-param name="method" select="'add'"/>
                    <xsl:with-param name="args" as="xs:string*" select="$imVarName"/>
                  </xsl:call-template>

                </xsl:with-param>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <!-- create the InMessage for each receiver-->
              <xsl:variable name="receiverIdRefs1" as="xs:string*">
                <!-- necessary if someone use two spaces -->
                <xsl:for-each select="fn:tokenize(@receiverIdRefs, ' ')">
                  <xsl:if test=". != ''">
                    <xsl:value-of select="."/>
                  </xsl:if>
                </xsl:for-each>
              </xsl:variable>
              <xsl:variable name="receiverIdRefs2" select="aorsl:ReceiverIdRef[matches(@language, $output.lang.RegExpr)]"/>
              <xsl:variable name="senderIdRef" as="xs:string">
                <xsl:choose>
                  <xsl:when test="fn:exists(@senderIdRef)">
                    <xsl:value-of select="@senderIdRef"/>
                  </xsl:when>
                  <xsl:when test="fn:exists(aorsl:SenderIdRef[matches(@language, $output.lang.RegExpr)])">
                    <xsl:value-of select="aorsl:SenderIdRef[matches(@language, $output.lang.RegExpr)]"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:message>No sender defined for <xsl:value-of select="local-name()"/></xsl:message>
                    <xsl:value-of select="'0'"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:variable>

              <xsl:for-each select="($receiverIdRefs1, $receiverIdRefs2)">

                <xsl:call-template name="java:variable">
                  <xsl:with-param name="indent" select="$block-indent"/>
                  <xsl:with-param name="name" select="$imVarName"/>
                  <xsl:with-param name="value">
                    <xsl:call-template name="java:newObject">
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="class" select="$inMessageType"/>
                      <xsl:with-param name="varName" select="$imVarName"/>
                      <xsl:with-param name="args" as="xs:string*">
                        <!-- occurenceTime -->
                        <xsl:variable name="param1" as="xs:string">
                          <xsl:call-template name="java:callGetterMethod">
                            <xsl:with-param name="objInstance">
                              <xsl:call-template name="java:varByDotNotation">
                                <xsl:with-param name="name" select="'this'"/>
                                <xsl:with-param name="varName" select="$triggeringEventVar"/>
                              </xsl:call-template>
                            </xsl:with-param>
                            <xsl:with-param name="instVariable" select="'occurrenceTime'"/>
                            <xsl:with-param name="inLine" select="true()"/>
                          </xsl:call-template>
                        </xsl:variable>

                        <!-- set the params -->
                        <!-- occurenceTime -->
                        <xsl:value-of select="fn:concat($param1, ' + ', $delay)"/>
                        <!-- receiverIdRef -->
                        <xsl:value-of select="."/>
                        <!-- senderIdRef -->
                        <xsl:value-of select="$senderIdRef"/>
                        <!-- messageTypeObj -->
                        <xsl:value-of select="$messageVarName"/>
                      </xsl:with-param>
                      <xsl:with-param name="onlyInitialization" select="true()"/>
                    </xsl:call-template>

                  </xsl:with-param>
                </xsl:call-template>

                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="indent" select="$block-indent"/>
                  <xsl:with-param name="objInstance" select="$resultList"/>
                  <xsl:with-param name="method" select="'add'"/>
                  <xsl:with-param name="args" as="xs:string*" select="$imVarName"/>
                </xsl:call-template>

              </xsl:for-each>

            </xsl:otherwise>
          </xsl:choose>

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

  </xsl:template>

  <!-- set  ActivityStartEvent -->
  <xsl:template match="aorsl:ActivityStartEventExpr" mode="createEnvironmentRules.helper.method.resultingEvents">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="resultList" required="yes" as="xs:string"/>
    <xsl:param name="triggeringEventVar" required="yes" as="xs:string" tunnel="yes"/>

    <xsl:variable name="activityType" select="@activityType"/>
    <xsl:variable name="activityStartEventVar" select="fn:concat(jw:lowerWord($activityType), $core.class.activityStartEvent, '_', position())"/>

    <xsl:variable name="currentStep" as="xs:string">
      <xsl:call-template name="java:callGetterMethod">
        <xsl:with-param name="objInstance">
          <xsl:call-template name="java:varByDotNotation">
            <xsl:with-param name="name" select="'this'"/>
            <xsl:with-param name="varName" select="$triggeringEventVar"/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="instVariable" select="'occurrenceTime'"/>
        <xsl:with-param name="inLine" select="true()"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="delay">
      <xsl:apply-templates select="." mode="assistents.getDelay"/>
    </xsl:variable>

    <xsl:variable name="output">
      <xsl:variable name="block-indent" select="if (fn:exists(aorsl:Condition[matches(@language, $output.lang.RegExpr)])) then $indent + 1 else $indent"/>

      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$block-indent"/>
        <xsl:with-param name="class" select="$core.class.activityStartEvent"/>
        <xsl:with-param name="varName" select="$activityStartEventVar"/>
        <xsl:with-param name="args" as="xs:string*">
          <xsl:value-of select="jw:quote($activityType)"/>
          <xsl:value-of select="fn:concat($currentStep, ' + ', $delay)"/>
        </xsl:with-param>
      </xsl:call-template>

      <xsl:call-template name="setStartEndEventCorrelation">
        <xsl:with-param name="indent" select="$block-indent"/>
        <xsl:with-param name="eventExpr" select="."/>
        <xsl:with-param name="envEvtVarName" select="$activityStartEventVar"/>
      </xsl:call-template>

      <xsl:if
        test="fn:exists(aorsl:ActorRef[matches(@language, $output.lang.RegExpr)]) or fn:exists(aorsl:ActorIdRef[matches(@language, $output.lang.RegExpr)]) or @actorIdRef">

        <xsl:call-template name="java:callSetterMethod">
          <xsl:with-param name="indent" select="$block-indent"/>
          <xsl:with-param name="objInstance" select="$activityStartEventVar"/>
          <xsl:with-param name="instVariable" select="'activityActor'"/>
          <xsl:with-param name="value">
            <xsl:choose>
              <xsl:when test="fn:exists(aorsl:ActorRef[matches(@language, $output.lang.RegExpr)])">
                <xsl:value-of select="aorsl:ActorRef[matches(@language, $output.lang.RegExpr)][1]"/>
              </xsl:when>
              <xsl:otherwise>
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
                      <xsl:when test="aorsl:ActorIdRef[matches(@language, $output.lang.RegExpr)]">
                        <xsl:value-of select="aorsl:ActorIdRef[matches(@language, $output.lang.RegExpr)][1]"/>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:value-of select="@actorIdRef"/>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:otherwise>
            </xsl:choose>

          </xsl:with-param>
        </xsl:call-template>

      </xsl:if>

      <xsl:call-template name="java:callMethod">
        <xsl:with-param name="indent" select="$block-indent"/>
        <xsl:with-param name="objInstance" select="$resultList"/>
        <xsl:with-param name="method" select="'add'"/>
        <xsl:with-param name="args" as="xs:string*" select="$activityStartEventVar"/>
      </xsl:call-template>

    </xsl:variable>

    <xsl:apply-templates select="." mode="assistent.resultingEvent.output">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="output" select="$output"/>
    </xsl:apply-templates>

    <xsl:call-template name="java:newLine"/>

  </xsl:template>

  <!-- set  ActivityEndEvent -->
  <xsl:template match="aorsl:ActivityEndEventExpr" mode="createEnvironmentRules.helper.method.resultingEvents">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="resultList" required="yes" as="xs:string"/>
    <xsl:param name="triggeringEventVar" required="yes" as="xs:string" tunnel="yes"/>

    <xsl:variable name="activityType" select="@activityType"/>
    <xsl:variable name="activityEndEventVar" select="fn:concat(jw:lowerWord($activityType), $core.class.activityEndEvent, '_', position())"/>

    <xsl:variable name="currentStep" as="xs:string">
      <xsl:call-template name="java:callGetterMethod">
        <xsl:with-param name="objInstance">
          <xsl:call-template name="java:varByDotNotation">
            <xsl:with-param name="name" select="'this'"/>
            <xsl:with-param name="varName" select="$triggeringEventVar"/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="instVariable" select="'occurrenceTime'"/>
        <xsl:with-param name="inLine" select="true()"/>
      </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="delay">
      <xsl:apply-templates select="." mode="assistents.getDelay"/>
    </xsl:variable>

    <xsl:variable name="output">
      <xsl:variable name="block-indent" select="if (fn:exists(aorsl:Condition[matches(@language, $output.lang.RegExpr)])) then $indent + 1 else $indent"/>

      <xsl:call-template name="java:newObject">
        <xsl:with-param name="indent" select="$block-indent"/>
        <xsl:with-param name="class" select="$core.class.activityStartEvent"/>
        <xsl:with-param name="varName" select="$activityEndEventVar"/>
        <xsl:with-param name="args" as="xs:string*">
          <xsl:value-of select="jw:quote($activityType)"/>
          <xsl:value-of select="fn:concat($currentStep, ' + ', $delay)"/>
        </xsl:with-param>
      </xsl:call-template>

      <xsl:call-template name="setStartEndEventCorrelation">
        <xsl:with-param name="indent" select="$block-indent"/>
        <xsl:with-param name="eventExpr" select="."/>
        <xsl:with-param name="envEvtVarName" select="$activityEndEventVar"/>
      </xsl:call-template>

      <xsl:call-template name="java:callMethod">
        <xsl:with-param name="indent" select="$block-indent"/>
        <xsl:with-param name="objInstance" select="$resultList"/>
        <xsl:with-param name="method" select="'add'"/>
        <xsl:with-param name="args" as="xs:string*" select="$activityEndEventVar"/>
      </xsl:call-template>

    </xsl:variable>

    <xsl:apply-templates select="." mode="assistent.resultingEvent.output">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="output" select="$output"/>
    </xsl:apply-templates>

    <xsl:call-template name="java:newLine"/>

  </xsl:template>

  <!-- check the permitted attribut combinations -->
  <xsl:template match="aorsl:WHEN" mode="createEnvironmentRules.checkAttributes">

    <!-- if the eventType is an OutMessageEvent it's a messageType required -->
    <xsl:if test="@eventType = $core.class.outMessageEvent and not (fn:exists(@messageType))">
      <xsl:message terminate="yes">
        <xsl:text>No messageType for eventType </xsl:text>
        <xsl:value-of select="$core.class.outMessageEvent"/>
        <xsl:text> defined. </xsl:text>
        <xsl:text>EnvironmentRule: </xsl:text>
        <xsl:value-of select="../../aorsl:EnvironmentRule/@name"/>
      </xsl:message>
    </xsl:if>

  </xsl:template>

  <!-- set collectionitem as classvariable -->
  <xsl:template match="aorsl:RemoveObjectFromCollection" mode="createEnvironmentRules.setGlobalsByCollectionItems">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:variable name="collection" as="node()">
      <xsl:apply-templates select="." mode="createEnvironmentRules.helper.getCollectionFromRemoveObjectFromCollection"/>
    </xsl:variable>

    <xsl:if
      test="not (fn:exists(./ancestor::aorsl:EnvironmentRule/aorsl:FOR[@objectType = $collection/@itemType and @objectVariable = current()/@itemObjectVariable]))">

      <xsl:call-template name="java:variable">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="modifier" select="'private'"/>
        <xsl:with-param name="type" select="jw:upperWord($collection/@itemType)"/>
        <xsl:with-param name="name">
          <xsl:choose>
            <xsl:when test="fn:exists(@itemObjectVariable)">
              <xsl:value-of select="@itemObjectVariable"/>
            </xsl:when>
            <xsl:when test="@destroyObject = true()">
              <xsl:value-of select="fn:concat('removeFrom', $collection/@name, $collection/@id)"/>
            </xsl:when>
          </xsl:choose>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template match="aorsl:RemoveObjectFromCollection" mode="createEnvironmentRules.helper.getCollectionFromRemoveObjectFromCollection">
    <xsl:choose>
      <xsl:when test="fn:exists(@collectionObjectVariable)">
        <xsl:variable name="variabledeclaration" as="node()"
          select="ancestor::aorsl:EnvironmentRule/aorsl:FOR[@objectVariable = current()/@collectionObjectVariable]"/>
        <xsl:choose>
          <xsl:when test="$variabledeclaration/@objectIdRef">
            <xsl:copy-of select="//aorsl:Collections/aorsl:Collection[@id = $variabledeclaration/@objectIdRef]"/>
          </xsl:when>
          <xsl:when test="$variabledeclaration/@objectName">
            <xsl:copy-of select="//aorsl:Collections/aorsl:Collection[@name = $variabledeclaration/@objectName]"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message terminate="yes">ERROR: No FOR[@objectVariable] found for removeOperation in EnvironmentRule </xsl:message>
            <xsl:value-of select="../../aorsl:EnvironmentRule/@name"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="fn:exists(@collectionID)">
        <xsl:copy-of select="//aorsl:Collections/aorsl:Collection[@id = current()/@collectionID]"/>
      </xsl:when>
      <xsl:when test="fn:exists(@collectionName)">
        <xsl:copy-of select="//aorsl:Collections/aorsl:Collection[@name = current()/@collectionName]"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="node()"/>
        <xsl:message terminate="yes">
          <xsl:text>ERROR: No Collection found for removeOperation in EnvironmentRule </xsl:text>
          <xsl:value-of select="ancestor::aorsl:EnvironmentRule/@name"/>
          <xsl:text>]!</xsl:text>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- set collectionitems classvariables from collection -->
  <xsl:template match="aorsl:RemoveObjectFromCollection" mode="createEnvironmentRules.method.stateEffects.content">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:choose>
      <xsl:when test="fn:exists(@itemObjectVariable) or @destroyObject = true()">
        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="name">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="varName">
                <xsl:choose>
                  <xsl:when test="fn:exists(@itemObjectVariable)">
                    <xsl:value-of select="@itemObjectVariable"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:variable name="collection" as="node()">
                      <xsl:apply-templates select="." mode="createEnvironmentRules.helper.getCollectionFromRemoveObjectFromCollection"/>
                    </xsl:variable>
                    <xsl:value-of select="fn:concat('removeFrom', $collection/@name, $collection/@id)"/>
                  </xsl:otherwise>
                </xsl:choose>

              </xsl:with-param>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="value">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance">
                <xsl:choose>
                  <xsl:when test="fn:exists(@collectionObjectVariable)">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="@collectionObjectVariable"/>
                    </xsl:call-template>
                  </xsl:when>
                  <xsl:when test="fn:exists(@collectionID)">
                    <xsl:message>Not yet implementet</xsl:message>
                  </xsl:when>
                  <xsl:when test="fn:exists(@collectionName)">
                    <xsl:variable name="collectionNode" select="//aorsl:Collections/aorsl:Collection[@name = current()/@collectionName][1]"/>
                    <xsl:choose>
                      <xsl:when test="fn:exists($collectionNode)">
                        <xsl:value-of select="jw:createCollectionVarname($collectionNode)"/>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:message>[ERROR] Non Collection found.</xsl:message>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:message>[ERROR] No collection specified.</xsl:message>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
              <xsl:with-param name="method" select="'removeObjekt'"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="objInstance">
            <xsl:choose>
              <xsl:when test="fn:exists(@collectionObjectVariable)">
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="varName" select="@collectionObjectVariable"/>
                </xsl:call-template>
              </xsl:when>
              <xsl:when test="@collectionName">

                <xsl:variable name="collection">
                  <xsl:call-template name="java:callMethod">
                    <xsl:with-param name="indent" select="$indent"/>
                    <xsl:with-param name="objInstance">
                      <xsl:call-template name="java:callGetterMethod">
                        <xsl:with-param name="inLine" select="true()"/>
                        <xsl:with-param name="objInstance" select="'this'"/>
                        <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
                      </xsl:call-template>
                    </xsl:with-param>
                    <xsl:with-param name="method" select="'getCollectionByName'"/>
                    <xsl:with-param name="args" select="jw:quote(@collectionName)"/>
                    <xsl:with-param name="inLine" select="true()"/>
                  </xsl:call-template>
                </xsl:variable>
                <xsl:value-of select="jw:parenthesise($collection)"/>

              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="jw:lowerWord(@collectionName)"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
          <xsl:with-param name="method" select="'removeObjekt'"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--  -->
  <xsl:template match="aorsl:Create" mode="createEnvironmentRules.method.stateEffects.content">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:variable name="getTriggeredTimeForDynamicCreatedAgentsWithOnEachStepEvents">
      <xsl:call-template name="java:callGetterMethod">
        <xsl:with-param name="inLine" select="true()"/>
        <xsl:with-param name="instVariable" select="'triggeredTime'"/>
      </xsl:call-template>
    </xsl:variable>

    <!-- create Object -->
    <xsl:apply-templates select="aorsl:Object" mode="createEnvironmentRules.helper.method.createObjekt">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates>

    <!-- create Objects -->
    <xsl:apply-templates select="aorsl:Objects" mode="createEnvironmentRules.helper.method.createObjekt">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates>

    <!-- create Agent -->
    <xsl:apply-templates select="aorsl:Agent" mode="createEnvironmentRules.helper.method.createAgent">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="getTriggeredTimeForDynamicCreatedAgentsWithOnEachStepEvents"
        select="$getTriggeredTimeForDynamicCreatedAgentsWithOnEachStepEvents" tunnel="yes"/>
    </xsl:apply-templates>

    <!-- create PhysicalAgent -->
    <xsl:apply-templates select="aorsl:PhysicalAgent" mode="createEnvironmentRules.helper.method.createPhysAgent">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="getTriggeredTimeForDynamicCreatedAgentsWithOnEachStepEvents"
        select="$getTriggeredTimeForDynamicCreatedAgentsWithOnEachStepEvents" tunnel="yes"/>
    </xsl:apply-templates>

    <!-- create PhysicalAgents -->
    <xsl:apply-templates select="aorsl:PhysicalAgents" mode="createEnvironmentRules.helper.method.createPhysAgent">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates>

    <!-- create PhysicalObjekt -->
    <xsl:apply-templates select="aorsl:PhysicalObject" mode="createEnvironmentRules.helper.method.createPhysicalObjekt">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates>

    <!-- create PhysicalObjekts -->
    <xsl:apply-templates select="aorsl:PhysicalObjects" mode="createEnvironmentRules.helper.method.createPhysicalObjekt">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates>

    <xsl:call-template name="java:newLine"/>
  </xsl:template>

  <xsl:template match="aorsl:DestroyObject" mode="createEnvironmentRules.method.stateEffects.content">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:apply-templates select="." mode="createEnvironmentRules.helper.method.destroyObject">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- set a variablename for a created AOR-Objekt; if @objectVariable is exists then use this from classvariable; otherwise use a 
    lowerCaseStarted @type -->
  <xsl:template
    match="aorsl:Object | aorsl:Objects | aorsl:Agent | aorsl:Agents | aorsl:PhysicalObject | aorsl:PhysicalObjects |
    aorsl:PhysicalAgent | aorsl:PhysicalAgents"
    mode="createEnvironmentRules.helper.method.createObjekt.varName">

    <xsl:choose>
      <xsl:when test="@objectVariable">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="varName" select="@objectVariable"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="if (@name) then @name else jw:lowerWord(@type)"/>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!-- check the @activityType if exist -->
  <xsl:template match="aorsl:WHEN" mode="createEnvironmentRules.helper.method.execute.checkActivityType">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="eventVariable" as="xs:string" required="yes" tunnel="yes"/>
    <xsl:param name="resultListVar" as="xs:string" required="yes"/>

    <xsl:if test="(@eventType eq $core.class.activityStartEvent or @eventType eq $core.class.activityEndEvent) and @activityType">

      <xsl:call-template name="java:if">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="condition">
          <xsl:value-of select="'!'"/>
          <xsl:call-template name="java:callMethod">
            <xsl:with-param name="inLine" select="true()"/>
            <xsl:with-param name="objInstance">
              <xsl:call-template name="java:callGetterMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="objInstance">
                  <xsl:call-template name="java:varByDotNotation">
                    <xsl:with-param name="varName" select="$eventVariable"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="instVariable" select="'activityType'"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="method" select="'equals'"/>
            <xsl:with-param name="args" select="jw:quote(@activityType)"/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="thenContent">

          <xsl:call-template name="java:return">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="value" select="$resultListVar"/>
          </xsl:call-template>

        </xsl:with-param>
      </xsl:call-template>
      <xsl:call-template name="java:newLine"/>

    </xsl:if>

  </xsl:template>

</xsl:stylesheet>
