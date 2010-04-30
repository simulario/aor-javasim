<?xml version="1.0" encoding="UTF-8"?>

<!--
  This transformation creates classes for agentobjects based on a given aorsml file.
  
  $Rev: 3695 $
  $Date: 2009-11-09 18:11:54 +0100 (Mon, 09 Nov 2009) $
  
  @author:   Jens Werner (jens.werner@tu-cottbus.de)
  @license:  GNU General Public License version 2 or higher
  @last changed by $Author: jewerner $
-->

<xsl:stylesheet version="2.0" xmlns:aorsml="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xsi:schemaLocation="http://aor-simulation.org aorsml.xsd"
  xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">
  
  <!--creates class-->
  <xsl:template match="aorsml:AgentType" mode="createAgents.createAgent">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    
    <xsl:variable name="className" select="jw:upperWord(@name)"/>
    
    <xsl:call-template name="aorsml:classFile">
      <xsl:with-param name="path" select="$sim.path.model.envsimulator"/>
      <xsl:with-param name="name" select="$className"/>
      
      <xsl:with-param name="content">
        
        <xsl:call-template name="java:imports">
          <xsl:with-param name="importList" as="xs:string*">
            <xsl:value-of select="$core.package.agentObject"/>
            
            <xsl:if test="fn:exists(aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty | aorsml:EnumerationProperty)">
              <xsl:value-of select="'java.beans.PropertyChangeEvent'"/>
            </xsl:if>
            
            <xsl:value-of select="fn:concat($sim.package.controller, '.*')"/>
            <xsl:value-of select="fn:concat($core.package.model.envSim.agt, '.*')"/>
            
            <xsl:call-template name="setDefaultJavaImports"/>
          </xsl:with-param>
        </xsl:call-template>
        
        <xsl:call-template name="java:class">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="name" select="$className"/>
          <xsl:with-param name="extends" select="if (fn:exists(@superType)) then @superType else $core.class.agentObject"/>
          <xsl:with-param name="content">
            
            <!-- classVariables (from aorsml:Attribute and aorsml:ReferenceProperty) -->
            <xsl:apply-templates select="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty | aorsml:EnumerationProperty"
              mode="assistents.classVariable">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>
            <xsl:call-template name="java:newLine"/>
            
            <!-- constructor with all classVariables -->
            <xsl:apply-templates select="." mode="createAgents.constructor">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="className" select="$className"/>
            </xsl:apply-templates>
            
            <!-- setters -->
            <xsl:for-each select="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty | aorsml:EnumerationProperty">
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
            
            <!-- getters -->
            <xsl:apply-templates select="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty | aorsml:EnumerationProperty"
              mode="assistents.getVariableMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>
            
            <!-- get(int index), remove(int index), remove(Object o), add(Object o) for Properties with @upperMultiplicity eq 'unbounded' -->
            <xsl:apply-templates
              select="aorsml:Attribute[@upperMultiplicity eq 'unbounded'] | 
              aorsml:ReferenceProperty[@upperMultiplicity eq 'unbounded'] | 
              aorsml:ComplexDataProperty[@upperMultiplicity eq 'unbounded']" mode="assistents.listMethods">
              <xsl:with-param name="indent" select="$indent + 1"/>
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
  
  <!-- creates constructor -->
  <xsl:template match="aorsml:AgentType" mode="createAgents.constructor">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="className" required="yes" as="xs:string"/>
    
    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="$className"/>
      <xsl:with-param name="parameters" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'long'"/>
          <xsl:with-param name="name" select="'id'"/>
        </xsl:call-template>
        <xsl:apply-templates select="." mode="assistents.constructor.allAttributes"/>
      </xsl:with-param>
      <xsl:with-param name="content">
        
        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string*">
            <xsl:value-of select="'id'"/>
            <xsl:apply-templates select="." mode="assistents.constructor.allSuperAttributes"/>
          </xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="java:newLine"/>
        
        <!-- set all attributvalues from constructor -->
        <xsl:for-each select="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty | aorsml:EnumerationProperty">
          
          <!-- init the lists for upperMultiplicity -->
          <xsl:if test="@upperMultiplicity eq 'unbounded'">
            <xsl:apply-templates select="." mode="assistent.initList">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>
          </xsl:if>
          
          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="objInstance" select="'this'"/>
            <xsl:with-param name="instVariable" select="@name"/>
            <xsl:with-param name="value" select="@name"/>
          </xsl:call-template>
          
          <xsl:apply-templates select="." mode="assistents.setInheritedProperty">
            <xsl:with-param name="indent" select="$indent + 1"/>
          </xsl:apply-templates>
          
        </xsl:for-each>
        
      </xsl:with-param>
    </xsl:call-template>
    
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
        <xsl:apply-templates select="." mode="assistents.constructor.allAttributes"/>
      </xsl:with-param>
      <xsl:with-param name="content">
        
        <xsl:call-template name="java:callSuper">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="paramList" as="xs:string*">
            <xsl:value-of select="'id'"/>
            <xsl:value-of select="'name'"/>
            <xsl:apply-templates select="." mode="assistents.constructor.allSuperAttributes"/>
          </xsl:with-param>
        </xsl:call-template>
        <xsl:call-template name="java:newLine"/>
        
        <!-- set all attributvalues from constructor -->
        <xsl:for-each select="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty | aorsml:EnumerationProperty">
          
          <!-- init the lists for upperMultiplicity -->
          <xsl:if test="@upperMultiplicity eq 'unbounded'">
            <xsl:apply-templates select="." mode="assistent.initList">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>
          </xsl:if>
          
          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="objInstance" select="'this'"/>
            <xsl:with-param name="instVariable" select="@name"/>
            <xsl:with-param name="value" select="@name"/>
          </xsl:call-template>
          
          <xsl:apply-templates select="." mode="assistents.setInheritedProperty">
            <xsl:with-param name="indent" select="$indent + 1"/>
          </xsl:apply-templates>
          
        </xsl:for-each>
        
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  
</xsl:stylesheet>
