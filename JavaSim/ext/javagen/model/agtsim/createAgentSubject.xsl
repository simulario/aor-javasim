<?xml version="1.0" encoding="UTF-8"?>
<!--
  This transformation creates classes for agentsubjects based on a given aorsml file.
  
  $Rev$
  $Date$
  
  @author:   Jens Werner (jens.werner@tu-cottbus.de)
  @license:  GNU General Public License version 2 or higher
  @last changed by $Author$
-->

<xsl:stylesheet version="2.0" xmlns:aorsl="http://aor-simulation.org"
  xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:java="http://www.sun.com/java"
  xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:schemaLocation="http://aor-simulation.org aorsml.xsd"
  xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">

  <!--creates class-->
  <xsl:template match="aorsl:AgentType" mode="createAgentSubjects.createAgentSubject">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:variable name="className" select="jw:upperWord(fn:concat(@name, $prefix.agentSubject))"/>

    <!-- if there is no  SelfBeliefAttribute then it is an PerfectInformationAgentType -->
    <xsl:variable name="isPIAgent" as="xs:boolean">
      <xsl:apply-templates select="." mode="createAgentSubjects.checkPIAgent"/>
    </xsl:variable>

    <xsl:call-template name="aorsl:classFile">
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

            <xsl:if
              test="fn:exists(//aorsl:SimulationModel/aorsl:EntityTypes/aorsl:CausedEventType) or 
              fn:exists(//aorsl:SimulationModel/aorsl:EntityTypes/aorsl:PerceptionEventType) or 
              fn:exists(//aorsl:SimulationModel/aorsl:EntityTypes/aorsl:ExogenousEventType) or 
              fn:exists(//aorsl:SimulationModel/aorsl:EntityTypes/aorsl:ActionEventType)">
              <xsl:value-of select="fn:concat($sim.package.model.envevent, '.*')"/>
            </xsl:if>
            <xsl:value-of select="fn:concat($sim.package.model.envsimulator, '.*')"/>

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

            <!-- set SelfbeliefAttributes and SelfBeliefReferenceProperty as classvariables -->
            <xsl:apply-templates
              select="aorsl:SelfBeliefAttribute | aorsl:SelfBeliefReferenceProperty"
              mode="assistents.classVariable">
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
                <xsl:apply-templates select="." mode="createAgentSubjects.setMomerySize"/>
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="java:newLine"/>

            <!-- create constructor without agentSubjectListener -->
            <xsl:apply-templates select="." mode="createAgentSubjects.constructor">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="className" select="$className"/>
              <xsl:with-param name="agentSubjectListener" select="false()"/>
            </xsl:apply-templates>

            <!-- setter -->
            <xsl:for-each select="aorsl:SelfBeliefAttribute | aorsl:SelfBeliefReferenceProperty">
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
            <xsl:apply-templates select="aorsl:SelfBeliefAttribute | aorsl:SelfBeliefReferenceProperty"
              mode="assistents.getVariableMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- for PI-Agents -->
            <xsl:if test="$isPIAgent">
              <xsl:apply-templates select="." mode="createAgentSubjects.pi-agents.getters">
                <xsl:with-param name="indent" select="$indent + 1"/>
              </xsl:apply-templates>
              <!--      <xsl:apply-templates select="." mode="createPhysicalAgentSubjects.pi-agents.setters">
                <xsl:with-param name="indent" select="$indent + 1"/>
                </xsl:apply-templates> -->
              <xsl:apply-templates select="." mode="createAgentSubjects.pi-agents.setters">
                <xsl:with-param name="indent" select="$indent + 1"/>
              </xsl:apply-templates>
            </xsl:if>

            <!-- creates for BeliefTypes -->
            <xsl:apply-templates select="aorsl:BeliefEntityType"
              mode="shared.methods.ceateBeliefTypes">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- functions -->
            <xsl:choose>
              <xsl:when test="fn:exists(aorsl:SubjectiveFunction)">
                <xsl:apply-templates select="aorsl:SubjectiveFunction" mode="shared.createFunction">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                </xsl:apply-templates>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="aorsl:Function" mode="shared.createFunction">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                </xsl:apply-templates>
              </xsl:otherwise>
            </xsl:choose>

            <!-- CommunicationRules -->
            <xsl:apply-templates select="aorsl:CommunicationRule"
              mode="createCommunicationRules.createCommunicationRule">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="agentType" select="."/>
            </xsl:apply-templates>

            <!-- PeriodicTimeEvent -->
            <xsl:apply-templates select="aorsl:PeriodicTimeEventType"
              mode="createPeriodicTimeEvents.createPeriodicTimeEvent">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- TimeEventType -->
            <xsl:apply-templates select="aorsl:TimeEventType"
              mode="createTimeEventTypes.createTimeEventType">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- ActualInMessageEventType -->
            <xsl:apply-templates select="aorsl:ActualInMessageEventType"
              mode="createActualInMessageEventTypes.createActualInMessageEventType">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- BeliefEntityType -->
            <xsl:apply-templates select="aorsl:BeliefEntityType"
              mode="createBeliefEntityTypes.createBeliefEntityType">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- creates constructor -->
  <xsl:template match="aorsl:AgentType" mode="createAgentSubjects.constructor">
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
            <xsl:apply-templates select="."
              mode="assistents.constructor.allSuperSelfBeliefAttributes"/>
          </xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="java:newLine"/>
        
        <!-- instanciate reference self beliefs from this class -->
        <xsl:for-each select="aorsl:SelfBeliefReferenceProperty">
          <xsl:call-template name="java:newObject">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="class" select="@type"/>
            <xsl:with-param name="varName" select="@name"/>
            <xsl:with-param name="isVariable" select="true()"/>
          </xsl:call-template>
        </xsl:for-each>

        <!-- set all attributvalues from this class -->
        <xsl:for-each select="aorsl:SelfBeliefAttribute">
          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="objInstance" select="'this'"/>
            <xsl:with-param name="instVariable" select="@name"/>
            <xsl:with-param name="value" select="@name"/>
          </xsl:call-template>
        </xsl:for-each>
        <xsl:call-template name="java:newLine"/>

        <!-- instantiate a new ArrayList for AgentRules -->
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

        <xsl:for-each select="aorsl:CommunicationRule">

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
                  <xsl:value-of select="$rulesListVarName"/>
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
        <xsl:apply-templates select="aorsl:PeriodicTimeEventType" mode="createAgentSubjects.constructor.helper.createPeriodicTimeEventType">
          <xsl:with-param name="indent" select="$indent + 1"/>
          </xsl:apply-templates> -->

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsl:PeriodicTimeEventType"
    mode="createAgentSubjects.constructor.helper.createPeriodicTimeEventType">
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

  </xsl:template>

  <xsl:template match="aorsl:AgentType | aorsl:PhysicalAgentType"
    mode="createAgentSubjects.pi-agents.getters">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:variable name="agentObjectClassName" select="jw:upperWord(@name)"/>
    <xsl:variable name="agentObjVarName" select="jw:lowerWord($agentObjectClassName)"/>

    <xsl:apply-templates
      select="aorsl:Attribute[@upperMultiplicity eq 'unbounded'] | 
      aorsl:ReferenceProperty[@upperMultiplicity eq 'unbounded'] | 
      aorsl:ComplexDataProperty[@upperMultiplicity eq 'unbounded'] | 
      aorsl:EnumerationProperty[@upperMultiplicity eq 'unbounded']"
      mode="createAgentSubjects.pi-agents.getters.lists">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="agentObjectClassName" select="$agentObjectClassName"/>
    </xsl:apply-templates>

    <xsl:for-each
      select="aorsl:Attribute[not(matches(lower-case(@name), $physObjPattern))] | aorsl:ReferenceProperty | aorsl:ComplexDataProperty | aorsl:EnumerationProperty">

      <xsl:if test="not (@upperMultiplicity eq 'unbounded')">

        <xsl:call-template name="java:method">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="type" select="jw:mappeDataType(@type)"/>
          <xsl:with-param name="name">
            <xsl:variable name="method_prefix" as="xs:string">
              <xsl:choose>
                <xsl:when test="@type = 'Boolean'">
                  <xsl:value-of select="'is'"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="'get'"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:variable>
            <xsl:value-of select="fn:concat($method_prefix, jw:upperWord(@name))"/>
          </xsl:with-param>
          <xsl:with-param name="content">

            <xsl:call-template name="createObjekt">
              <xsl:with-param name="indent" select="$indent"/>
              <xsl:with-param name="agentObjectClassName" select="$agentObjectClassName"/>
              <xsl:with-param name="agentObjVarName" select="$agentObjVarName"/>
            </xsl:call-template>

            <xsl:call-template name="java:return">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="value">
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="indent" select="$indent + 1"/>
                  <xsl:with-param name="objInstance" select="$agentObjVarName"/>
                  <xsl:with-param name="instVariable" select="@name"/>
                  <xsl:with-param name="type" select="@type"/>
                  <xsl:with-param name="inLine" select="true()"/>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:if>

    </xsl:for-each>

  </xsl:template>

  <!-- get the corresponded agentObjekt -->
  <xsl:template name="createObjekt">
    <xsl:param name="indent"/>
    <xsl:param name="agentObjectClassName"/>
    <xsl:param name="agentObjVarName"/>
    <xsl:call-template name="java:newObject">
      <xsl:with-param name="indent" select="$indent + 1"/>
      <xsl:with-param name="class" select="$agentObjectClassName"/>
      <xsl:with-param name="varName" select="$agentObjVarName"/>
      <xsl:with-param name="withDeclaration" select="false()"/>
    </xsl:call-template>
    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent+ 1"/>
      <xsl:with-param name="name" select="$agentObjVarName"/>
      <xsl:with-param name="castType" select="$agentObjectClassName"/>
      <xsl:with-param name="value">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="varName" select="'agentObject'"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template
    match="aorsl:Attribute | aorsl:ReferenceProperty | aorsl:ComplexDataProperty | aorsl:EnumerationProperty"
    mode="createAgentSubjects.pi-agents.getters.lists">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="agentObjectClassName" as="xs:string" required="yes"/>

    <xsl:variable name="agentObjVarName" select="jw:lowerWord($agentObjectClassName)"/>
    <xsl:variable name="methodPrefix" select="jw:upperWord(@name)"/>
    <xsl:variable name="indexVarName" select="'index'"/>

    <!-- get(int index) -->
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="jw:upperWord(@type)"/>
      <xsl:with-param name="name" select="fn:concat('get', $methodPrefix)"/>
      <xsl:with-param name="parameterList" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'int'"/>
          <xsl:with-param name="name" select="$indexVarName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:call-template name="createObjekt">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="agentObjectClassName" select="$agentObjectClassName"/>
          <xsl:with-param name="agentObjVarName" select="$agentObjVarName"/>
        </xsl:call-template>

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance" select="$agentObjVarName"/>
              <xsl:with-param name="method" select="fn:concat('get', $methodPrefix)"/>
              <xsl:with-param name="args" select="$indexVarName"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

    <!-- get#prefix#[s]() -->
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'ArrayList'"/>
      <xsl:with-param name="genericType" select="jw:upperWord(jw:mappeDataType(@type))"/>
      <xsl:with-param name="name"
        select="fn:concat('get', if (fn:ends-with($methodPrefix,'s')) then $methodPrefix else fn:concat($methodPrefix, 's'))"/>
      <xsl:with-param name="content">

        <xsl:call-template name="createObjekt">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="agentObjectClassName" select="$agentObjectClassName"/>
          <xsl:with-param name="agentObjVarName" select="$agentObjVarName"/>
        </xsl:call-template>

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance" select="$agentObjVarName"/>
              <xsl:with-param name="method"
                select="fn:concat('get', if (fn:ends-with($methodPrefix,'s')) then $methodPrefix else fn:concat($methodPrefix, 's'))"
              />
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="returnType"
            select="fn:concat('ArrayList&lt;', jw:upperWord(jw:mappeDataType(@type)), '&gt;')"
          />
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- all non-phys-attr for PI-agents -->
  <xsl:template match="aorsl:AgentType | aorsl:PhysicalAgentType"
    mode="createAgentSubjects.pi-agents.setters">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:variable name="agentObjectClassName" select="jw:upperWord(@name)"/>
    <xsl:variable name="agentObjVarName" select="jw:lowerWord($agentObjectClassName)"/>

    <xsl:apply-templates
      select="aorsl:Attribute[@upperMultiplicity eq 'unbounded'] | 
      aorsl:ReferenceProperty[@upperMultiplicity eq 'unbounded'] | 
      aorsl:ComplexDataProperty[@upperMultiplicity eq 'unbounded'] | 
      aorsl:EnumerationProperty[@upperMultiplicity eq 'unbounded']"
      mode="createAgentSubjects.pi-agents.setters.lists">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="agentObjectClassName" select="$agentObjectClassName"/>
    </xsl:apply-templates>

    <xsl:for-each
      select="aorsl:Attribute[not(matches(lower-case(@name), $physObjPattern))] | aorsl:ReferenceProperty | aorsl:ComplexDataProperty | aorsl:EnumerationProperty">

      <xsl:if test="not (@upperMultiplicity eq 'unbounded')">

        <xsl:call-template name="java:method">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="name" select="fn:concat('set', jw:upperWord(@name))"/>
          <xsl:with-param name="parameterList">
            <xsl:call-template name="java:createParam">
              <xsl:with-param name="type">
                <xsl:choose>
                  <xsl:when test="@upperMultiplicity eq 'unbounded'">
                    <xsl:value-of
                      select="fn:concat('List&lt;', jw:upperWord(jw:mappeDataType(@type)), '&gt;')"
                    />
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:value-of select="jw:mappeDataType(@type)"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
              <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="content">

            <xsl:call-template name="createObjekt">
              <xsl:with-param name="indent" select="$indent"/>
              <xsl:with-param name="agentObjectClassName" select="$agentObjectClassName"/>
              <xsl:with-param name="agentObjVarName" select="$agentObjVarName"/>
            </xsl:call-template>

            <xsl:call-template name="java:callSetterMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="objInstance" select="$agentObjVarName"/>
              <xsl:with-param name="instVariable" select="@name"/>
              <xsl:with-param name="value" select="@name"/>
            </xsl:call-template>

          </xsl:with-param>
        </xsl:call-template>

      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <xsl:template
    match="aorsl:Attribute | aorsl:ReferenceProperty | aorsl:ComplexDataProperty | aorsl:EnumerationProperty"
    mode="createAgentSubjects.pi-agents.setters.lists">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="agentObjectClassName" as="xs:string" required="yes"/>

    <xsl:variable name="agentObjVarName" select="jw:lowerWord($agentObjectClassName)"/>
    <xsl:variable name="methodPrefix" select="jw:upperWord(@name)"/>
    <xsl:variable name="indexVarName" select="'index'"/>

    <!-- remove(int index) -->
    <xsl:variable name="indexVarName" select="'index'"/>
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="jw:upperWord(@type)"/>
      <xsl:with-param name="name" select="fn:concat('remove', $methodPrefix)"/>
      <xsl:with-param name="parameterList" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'int'"/>
          <xsl:with-param name="name" select="$indexVarName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:call-template name="createObjekt">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="agentObjectClassName" select="$agentObjectClassName"/>
          <xsl:with-param name="agentObjVarName" select="$agentObjVarName"/>
        </xsl:call-template>

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance" select="$agentObjVarName"/>
              <xsl:with-param name="method" select="fn:concat('remove', $methodPrefix)"/>
              <xsl:with-param name="args" select="$indexVarName"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

    <!-- remove(Object o) -->
    <xsl:variable name="objVarName" select="fn:concat(@name, 'Obj')"/>
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'boolean'"/>
      <xsl:with-param name="name" select="fn:concat('remove', $methodPrefix)"/>
      <xsl:with-param name="parameterList" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="jw:upperWord(@type)"/>
          <xsl:with-param name="name" select="$objVarName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:call-template name="createObjekt">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="agentObjectClassName" select="$agentObjectClassName"/>
          <xsl:with-param name="agentObjVarName" select="$agentObjVarName"/>
        </xsl:call-template>

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">

            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance" select="$agentObjVarName"/>
              <xsl:with-param name="method" select="fn:concat('remove', $methodPrefix)"/>
              <xsl:with-param name="args" select="$objVarName"/>
            </xsl:call-template>

          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

    <!-- add(Object o) -->
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'boolean'"/>
      <xsl:with-param name="name" select="fn:concat('add', $methodPrefix)"/>
      <xsl:with-param name="parameterList" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="jw:upperWord(@type)"/>
          <xsl:with-param name="name" select="$objVarName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:call-template name="createObjekt">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="agentObjectClassName" select="$agentObjectClassName"/>
          <xsl:with-param name="agentObjVarName" select="$agentObjVarName"/>
        </xsl:call-template>

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">

            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance" select="$agentObjVarName"/>
              <xsl:with-param name="method" select="fn:concat('add', $methodPrefix)"/>
              <xsl:with-param name="args" select="$objVarName"/>
            </xsl:call-template>

          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- set the @memorySize from AgentType or from superType -->
  <xsl:template match="aorsl:AgentType" mode="createAgentSubjects.setMomerySize">
    <xsl:choose>
      <xsl:when test="fn:exists(@memorySize)">
        <xsl:value-of select="@memorySize"/>
      </xsl:when>
      <xsl:when test="fn:exists(@superType)">
        <xsl:apply-templates select="../aorsl:AgentType[@name eq current()/@superType]"
          mode="createAgentSubjects.setMomerySize"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="'0'"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- check for PI-Agent -->
  <xsl:template match="aorsl:PhysicalAgentType | aorsl:AgentType"
    mode="createAgentSubjects.checkPIAgent">
    <xsl:choose>
      <!-- TODO: check if  -->
      <xsl:when test="fn:exists(aorsl:SelfBeliefAttribute) or fn:exists(aorsl:BeliefEntityType)">
        <xsl:value-of select="false()"/>
      </xsl:when>
      <xsl:when test="fn:exists(@superType)">
        <xsl:apply-templates
          select="../aorsl:PhysicalAgentType[@name eq current()/@superType] | ../aorsl:AgentType[@name eq current()/@superType] "
          mode="createAgentSubjects.checkPIAgent"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="true()"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>
