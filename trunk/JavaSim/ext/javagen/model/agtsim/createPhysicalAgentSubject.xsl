<?xml version="1.0" encoding="UTF-8"?>
<!--
  This transformation creates classes for agentsubjects based on a given aorsml file.
  
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

  <!--creates class-->
  <xsl:template match="aorsml:PhysicalAgentType" mode="createPhysicalAgentSubjects.createPhysicalAgentSubject">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:variable name="className" select="jw:upperWord(fn:concat(@name, $prefix.agentSubject))"/>

    <!-- if there is no  SelfBeliefAttribute then it is an PerfectInformationAgentType -->
    <xsl:variable name="isPIAgent" as="xs:boolean">
      <xsl:apply-templates select="." mode="createAgentSubjects.checkPIAgent"/>
    </xsl:variable>

    <xsl:call-template name="aorsml:classFile">
      <xsl:with-param name="path" select="$sim.path.model.agentsimulator"/>
      <xsl:with-param name="name" select="$className"/>

      <xsl:with-param name="content">

        <xsl:call-template name="java:imports">
          <xsl:with-param name="importList" as="xs:string*">
            <xsl:value-of select="fn:concat($core.package.model, '.*')"/>
            <xsl:value-of select="fn:concat($core.package.model.agentSim, '.*')"/>
            <xsl:value-of select="fn:concat($core.package.model.agentSim.agt, '.*')"/>
            <xsl:value-of select="fn:concat($core.package.model.envEvent, '.*')"/>
            <xsl:value-of select="fn:concat($core.package.model.intEvent, '.*')"/>
            <xsl:value-of select="fn:concat($core.package.model.envSim, '.*')"/>

            <xsl:if test="fn:exists(aorsml:ReactionRule/aorsml:SCHEDULE-EVT/aorsml:ActionEventExpr)">
              <xsl:value-of select="$core.package.physAgentObject"/>
            </xsl:if>
            <xsl:if
              test="fn:exists(//aorsml:SimulationModel/aorsml:EntityTypes/aorsml:CausedEventType) or 
              fn:exists(//aorsml:SimulationModel/aorsml:EntityTypes/aorsml:PerceptionEventType) or 
              fn:exists(//aorsml:SimulationModel/aorsml:EntityTypes/aorsml:ExogenousEventType) or 
              fn:exists(//aorsml:SimulationModel/aorsml:EntityTypes/aorsml:ActionEventType)">
              <xsl:value-of select="fn:concat($sim.package.model.envevent, '.*')"/>
            </xsl:if>
            <xsl:value-of select="fn:concat($sim.package.model.envsimulator, '.*')"/>
            <xsl:if test="$isPIAgent and fn:exists(aorsml:Attribute)">
              <xsl:value-of select="fn:concat($sim.package.model.envsimulator, '.' , jw:upperWord(@name))"/>
            </xsl:if>

            <xsl:call-template name="setDefaultJavaImports"/>

            <xsl:value-of select="'java.util.ArrayList'"/>
            <xsl:value-of select="'java.util.HashMap'"/>
            <xsl:value-of select="'java.beans.PropertyChangeEvent'"/>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="java:class">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="name" select="$className"/>
          <xsl:with-param name="extends"
            select="if (fn:exists(@superType)) then fn:concat(@superType, $prefix.agentSubject) else $core.class.agentSubject"/>
          <xsl:with-param name="content">

            <!-- set SelfbeliefAttributes as classvariables -->
            <xsl:apply-templates select="aorsml:SelfBeliefAttribute | aorsml:SelfBeliefReferenceProperty" mode="assistents.classVariable">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>
            <xsl:call-template name="java:newLine"/>

            <!-- set memorySize (MEMORY_SIZE) -->
            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="modifier" select="'public'"/>
              <xsl:with-param name="static" select="true()"/>
              <xsl:with-param name="final" select="true()"/>
              <xsl:with-param name="type" select="'int'"/>
              <xsl:with-param name="name" select="'MEMORY_SIZE'"/>
              <xsl:with-param name="value">
                <xsl:apply-templates select="." mode="createPhysicalAgentSubjects.setMomerySize"/>
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="java:newLine"/>

            <!-- create constructor without agentSubjectListener -->
            <xsl:apply-templates select="." mode="createPhysicalAgentSubjects.constructor">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="className" select="$className"/>
              <xsl:with-param name="agentSubjectListener" select="false()"/>
            </xsl:apply-templates>

            <!-- setter -->
            <xsl:for-each select="aorsml:SelfBeliefAttribute | aorsml:SelfBeliefReferenceProperty">
              <xsl:apply-templates select="." mode="assistents.setVariableMethod">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="changeCheck" select="true()"/>
                <xsl:with-param name="extraContent">

                  <xsl:call-template name="java:callMethod">
                    <xsl:with-param name="indent" select="$indent + 3"/>
                    <xsl:with-param name="objInstance">
                      <xsl:call-template name="java:varByDotNotation">
                        <xsl:with-param name="name" select="'this'"/>
                        <xsl:with-param name="varName" select="'propertyChangeSupport'"/>
                      </xsl:call-template>
                    </xsl:with-param>
                    <xsl:with-param name="method" select="'firePropertyChange'"/>
                    <xsl:with-param name="args" as="xs:string*">
                      <xsl:call-template name="java:newObject">
                        <xsl:with-param name="class" select="'PropertyChangeEvent'"/>
                        <xsl:with-param name="args" as="xs:string*">
                          <xsl:value-of select="'this'"/>
                          <xsl:value-of select="jw:quote(@name)"/>
                          <xsl:value-of select="'null'"/>
                          <xsl:call-template name="java:varByDotNotation">
                            <xsl:with-param name="name" select="'this'"/>
                            <xsl:with-param name="varName" select="@name"/>
                          </xsl:call-template>
                        </xsl:with-param>
                        <xsl:with-param name="isVariable" select="true()"/>
                        <xsl:with-param name="inLine" select="true()"/>
                      </xsl:call-template>
                    </xsl:with-param>
                  </xsl:call-template>

                </xsl:with-param>
              </xsl:apply-templates>
            </xsl:for-each>

            <!-- getter -->
            <xsl:apply-templates select="aorsml:SelfBeliefAttribute | aorsml:SelfBeliefReferenceProperty" mode="assistents.getVariableMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- for PI-Agents -->
            <xsl:if test="$isPIAgent">
              <xsl:apply-templates select="." mode="createPhysicalAgentSubjects.pi-agents.getters">
                <xsl:with-param name="indent" select="$indent + 1"/>
              </xsl:apply-templates>
              <xsl:apply-templates select="." mode="createAgentSubjects.pi-agents.getters">
                <xsl:with-param name="indent" select="$indent + 1"/>
              </xsl:apply-templates>
              <xsl:apply-templates select="." mode="createPhysicalAgentSubjects.pi-agents.setters">
                <xsl:with-param name="indent" select="$indent + 1"/>
              </xsl:apply-templates>
              <xsl:apply-templates select="." mode="createAgentSubjects.pi-agents.setters">
                <xsl:with-param name="indent" select="$indent + 1"/>
              </xsl:apply-templates>
            </xsl:if>

            <!-- creates for BeliefTypes -->
            <xsl:apply-templates select="aorsml:BeliefEntityType" mode="shared.methods.ceateBeliefTypes">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- functions -->
            <!-- as a simplification, we assume, if we have only <Functions>, then we use all of them in Objects and Subjects -->
            <xsl:choose>
              <xsl:when test="fn:exists(aorsml:SubjectiveFunction)">
                <xsl:apply-templates select="aorsml:SubjectiveFunction" mode="shared.createFunction">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                </xsl:apply-templates>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="aorsml:Function" mode="shared.createFunction">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                </xsl:apply-templates>
              </xsl:otherwise>
            </xsl:choose>

            <!-- AgentRules -->
            <xsl:apply-templates select="aorsml:ReactionRule" mode="createAgentRules.createAgentRule">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="agentType" select="."/>
              <xsl:with-param name="isPIAgent" select="$isPIAgent"/>
            </xsl:apply-templates>

            <!-- ActualPerceptionRules -->
            <xsl:apply-templates select="aorsml:ActualPerceptionRule" mode="createActualPerceptionRules.createActualPerceptionRule">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="agentType" select="."/>
            </xsl:apply-templates>

            <!-- ActualPerceptionEvent -->
            <xsl:apply-templates select="aorsml:ActualPerceptionEventType" mode="createActualPerceptionEvents.createActualPerceptionEvent">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- PeriodicTimeEvent -->
            <xsl:apply-templates select="aorsml:PeriodicTimeEventType" mode="createPeriodicTimeEvents.createPeriodicTimeEvent">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- TimeEventType -->
            <xsl:apply-templates select="aorsml:TimeEventType" mode="createTimeEventTypes.createTimeEventType">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- ActualInMessageEventType -->
            <xsl:apply-templates select="aorsml:ActualInMessageEventType" mode="createActualInMessageEventTypes.createActualInMessageEventType">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- BeliefEntityType -->
            <xsl:apply-templates select="aorsml:BeliefEntityType" mode="createBeliefEntityTypes.createBeliefEntityType">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- creates constructor -->
  <xsl:template match="aorsml:PhysicalAgentType" mode="createPhysicalAgentSubjects.constructor">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="className" required="yes" as="xs:string"/>
    <xsl:param name="agentSubjectListener" as="xs:boolean" select="true()"/>

    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="$className"/>
      <xsl:with-param name="parameters" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'long'"/>
          <xsl:with-param name="name" select="'id'"/>
        </xsl:call-template>
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'String'"/>
          <xsl:with-param name="name" select="'name'"/>
        </xsl:call-template>
        <xsl:if test="$agentSubjectListener">
          <xsl:call-template name="java:createParam">
            <xsl:with-param name="type" select="$core.class.agentSubjectListener"/>
            <xsl:with-param name="name" select="'agtSimListener'"/>
          </xsl:call-template>
        </xsl:if>

        <xsl:apply-templates select="." mode="assistents.constructor.allSelfBeliefAttributes"/>

      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string*">
            <xsl:value-of select="'id'"/>
            <xsl:value-of select="'name'"/>
            <xsl:if test="$agentSubjectListener">
              <xsl:value-of select="'agtSimListener'"/>
            </xsl:if>
            <xsl:apply-templates select="." mode="assistents.constructor.allSuperSelfBeliefAttributes"/>
          </xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="java:newLine"/>

        <!-- instanciate reference self beliefs from this class -->
        <xsl:for-each select="aorsml:SelfBeliefReferenceProperty">
          <xsl:call-template name="java:newObject">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="class" select="@type"/>
            <xsl:with-param name="varName" select="@name"/>
            <xsl:with-param name="isVariable" select="true()"/>
          </xsl:call-template>
        </xsl:for-each>

        <!-- set all attributvalues from this class -->
        <xsl:for-each select="aorsml:SelfBeliefAttribute">
          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="objInstance" select="'this'"/>
            <xsl:with-param name="instVariable" select="@name"/>
            <xsl:with-param name="value" select="@name"/>
          </xsl:call-template>
        </xsl:for-each>
        <xsl:call-template name="java:newLine"/>

        <!-- instantiate a new ArrayList for AgentRules if there is not @superType -->
        <xsl:variable name="rulesListVarName" select="'reactionRules'"/>
        <xsl:if test="not (fn:exists(@superType))">
          <xsl:call-template name="java:newArrayListObject">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="generic" select="$core.class.reactionRule"/>
            <xsl:with-param name="name">
              <xsl:call-template name="java:varByDotNotation">
                <xsl:with-param name="name" select="'this'"/>
                <xsl:with-param name="varName" select="$rulesListVarName"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="isVariable" select="true()"/>
          </xsl:call-template>
        </xsl:if>

        <xsl:variable name="actPercrulesListVarName" select="'actualPercRules'"/>
        <xsl:if test="fn:exists(aorsml:ActualPerceptionRule) and not (fn:exists(@superType))">
          <!-- instantiate a new ArrayList for actualPerceptionRules -->
          <xsl:call-template name="java:newArrayListObject">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="generic" select="$core.class.actualPerceptionRule"/>
            <xsl:with-param name="name">
              <xsl:call-template name="java:varByDotNotation">
                <xsl:with-param name="name" select="'this'"/>
                <xsl:with-param name="varName" select="$actPercrulesListVarName"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="isVariable" select="true()"/>
          </xsl:call-template>
        </xsl:if>

        <xsl:for-each select="aorsml:ReactionRule, aorsml:ActualPerceptionRule">

          <xsl:call-template name="java:newLine"/>
          <xsl:call-template name="java:newObject">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="class" select="@name"/>
            <xsl:with-param name="varName" select="jw:lowerWord(@name)"/>
            <xsl:with-param name="args" as="xs:string*">
              <xsl:value-of select="jw:quote(@name)"/>
              <xsl:value-of select="'this'"/>
            </xsl:with-param>
          </xsl:call-template>
          <xsl:call-template name="java:callMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="objInstance">
              <xsl:call-template name="java:varByDotNotation">
                <xsl:with-param name="name" select="'this'"/>
                <xsl:with-param name="varName">
                  <xsl:choose>
                    <xsl:when test="local-name() = 'ReactionRule'">
                      <xsl:value-of select="$rulesListVarName"/>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:value-of select="$actPercrulesListVarName"/>
                    </xsl:otherwise>
                  </xsl:choose>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="method" select="'add'"/>
            <xsl:with-param name="args" as="xs:string*">
              <xsl:value-of select="jw:lowerWord(@name)"/>
            </xsl:with-param>
          </xsl:call-template>

        </xsl:for-each>

        <!-- create periodictimeevents
        <xsl:apply-templates select="aorsml:PeriodicTimeEventType" mode="createAgentSubjects.constructor.helper.createPeriodicTimeEventType">
          <xsl:with-param name="indent" select="$indent + 1"/>
          </xsl:apply-templates>  -->

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!--xsl:template match="aorsml:PeriodicTimeEventType" mode="createPhysicalAgentSubjects.constructor.helper.createPeriodicTimeEventType">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:newLine"/>
    <xsl:call-template name="java:newObject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="class" select="@name"/>
      <xsl:with-param name="varName" select="jw:lowerWord(@name)"/>
    </xsl:call-template>
    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance" select="jw:lowerWord(@name)"/>
      <xsl:with-param name="instVariable" select="'OccurrenceTime'"/>
      <xsl:with-param name="value">
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="inLine" select="true()"/>
          <xsl:with-param name="objInstance" select="jw:lowerWord(@name)"/>
          <xsl:with-param name="method" select="'periodicity'"/>
        </xsl:call-template>
        <xsl:value-of select="' + 1'"/>
      </xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="name" select="'this'"/>
          <xsl:with-param name="varName" select="'internalEvents'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="method" select="'add'"/>
      <xsl:with-param name="args" as="xs:string*">
        <xsl:value-of select="jw:lowerWord(@name)"/>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template-->

  <!-- getters and setters for pi-agent -->
  <xsl:template match="aorsml:PhysicalAgentType" mode="createPhysicalAgentSubjects.pi-agents.getters">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:for-each select="$physObjAttrList, $physAgentObjAttrList">

      <xsl:call-template name="java:method">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="modifier" select="'public'"/>
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
            <xsl:when test=". eq 'points'">
              <xsl:value-of select="'String'"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="'double'"/>
            </xsl:otherwise>
          </xsl:choose>
          
        </xsl:with-param>
        <xsl:with-param name="name" select="fn:concat('get', jw:upperWord(.))"/>
        <xsl:with-param name="content">


          <xsl:call-template name="java:return">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="value">
              <xsl:call-template name="java:callGetterMethod">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="objInstance">
                  <xsl:call-template name="java:callGetterMethod">
                    <xsl:with-param name="inLine" select="true()"/>
                    <xsl:with-param name="objInstance" select="'this'"/>
                    <xsl:with-param name="instVariable" select="'agentObject'"/>
                    <xsl:with-param name="castType" select="$core.class.physAgentObject"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="instVariable" select="."/>
                <xsl:with-param name="inLine" select="true()"/>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>

    </xsl:for-each>

  </xsl:template>

  <xsl:template match="aorsml:PhysicalAgentType" mode="createPhysicalAgentSubjects.pi-agents.setters">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:variable name="agentObjectClassName" select="jw:upperWord(@name)"/>
    <xsl:variable name="agentObjVarName" select="jw:lowerWord($agentObjectClassName)"/>

    <xsl:for-each select="$physObjAttrList, $physAgentObjAttrList">

      <xsl:call-template name="java:method">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="modifier" select="'public'"/>
        <xsl:with-param name="name" select="fn:concat('set', jw:upperWord(.))"/>
        <xsl:with-param name="parameterList">
          <xsl:call-template name="java:createParam">
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
                <xsl:when test=". eq 'points'">
                  <xsl:value-of select="'String'"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="'double'"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="name" select="."/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="content">

          <xsl:call-template name="java:variable">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="type" select="$core.class.physAgentObject"/>
            <xsl:with-param name="name" select="$agentObjVarName"/>
            <xsl:with-param name="castType" select="$core.class.physAgentObject"/>
            <xsl:with-param name="value">
              <xsl:call-template name="java:callGetterMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="instVariable" select="'agentObject'"/>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>

          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="objInstance" select="$agentObjVarName"/>
            <xsl:with-param name="instVariable" select="."/>
            <xsl:with-param name="value" select="."/>
          </xsl:call-template>

        </xsl:with-param>
      </xsl:call-template>

    </xsl:for-each>
  </xsl:template>

  <!-- set the @memorySize from PhysicalAgentType or from superType -->
  <xsl:template match="aorsml:PhysicalAgentType" mode="createPhysicalAgentSubjects.setMomerySize">
    <xsl:choose>
      <xsl:when test="fn:exists(@memorySize)">
        <xsl:value-of select="@memorySize"/>
      </xsl:when>
      <xsl:when test="fn:exists(@superType) and fn:exists(../aorsml:PhysicalAgentType[@name eq current()/@superType])">
        <xsl:apply-templates select="../aorsml:PhysicalAgentType[@name eq current()/@superType]" mode="createPhysicalAgentSubjects.setMomerySize"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="'0'"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
