<?xml version="1.0" encoding="UTF-8"?>
<!--
	This transformation creates classes for entities based on a given aorsml file.

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

  <xsl:template match="aorsl:ObjectType" mode="createObjects.createObject">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="aorsl:classFile">
      <xsl:with-param name="path" select="$sim.path.model.envsimulator"/>
      <xsl:with-param name="name" select="jw:upperWord(@name)"/>

      <xsl:with-param name="content">

        <xsl:call-template name="java:imports">
          <xsl:with-param name="importList" as="xs:string*">
            <xsl:value-of select="$core.package.object"/>

            <xsl:if
              test="fn:exists(aorsl:Attribute | aorsl:ReferenceProperty | aorsl:ComplexDataProperty | aorsl:ComplexDataProperty | aorsl:EnumerationProperty)">
              <xsl:value-of select="'java.beans.PropertyChangeEvent'"/>
            </xsl:if>

            <xsl:call-template name="setDefaultJavaImports"/>
          </xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="java:class">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'public'"/>
          <xsl:with-param name="name" select="jw:upperWord(@name)"/>
          <xsl:with-param name="extends" select="if (fn:exists(@superType)) then @superType else $core.class.object"/>
          <xsl:with-param name="content">

            <!-- classVariables (from aorsl:Attribute and aorsl:ReferenceProperty) -->
            <xsl:apply-templates select="aorsl:Attribute | aorsl:ReferenceProperty | aorsl:ComplexDataProperty | aorsl:EnumerationProperty"
              mode="assistents.classVariable">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>
            <xsl:call-template name="java:newLine"/>

            <!-- constructor with all classVariables -->
            <xsl:apply-templates select="." mode="createObjekts.constructor">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- setters -->
            <xsl:for-each select="aorsl:Attribute | aorsl:ReferenceProperty | aorsl:ComplexDataProperty | aorsl:EnumerationProperty">
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
                            <!-- here is the reason for the for-each -->
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
            <xsl:apply-templates select="aorsl:Attribute | aorsl:ReferenceProperty | aorsl:ComplexDataProperty | aorsl:EnumerationProperty"
              mode="assistents.getVariableMethod">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>
            
            <!-- get(int index), remove(int index), remove(Object o), add(Object o) for Properties with @upperMultiplicity eq 'unbounded' -->
            <xsl:apply-templates
              select="aorsl:Attribute[@upperMultiplicity eq 'unbounded'] | 
              aorsl:ReferenceProperty[@upperMultiplicity eq 'unbounded'] | 
              aorsl:ComplexDataProperty[@upperMultiplicity eq 'unbounded']" mode="assistents.listMethods">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

            <!-- functions -->
            <xsl:apply-templates select="aorsl:Function" mode="shared.createFunction">
              <xsl:with-param name="indent" select="$indent + 1"/>
            </xsl:apply-templates>

          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- creates constructor -->
  <xsl:template match="aorsl:ObjectType" mode="createObjekts.constructor">
    <xsl:param name="indent" required="yes"/>

    <xsl:call-template name="java:constructor">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="jw:upperWord(@name)"/>
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
        <xsl:for-each select="aorsl:Attribute | aorsl:ReferenceProperty | aorsl:ComplexDataProperty | aorsl:EnumerationProperty">

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
      <xsl:with-param name="name" select="jw:upperWord(@name)"/>
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
        <xsl:for-each select="aorsl:Attribute | aorsl:ReferenceProperty | aorsl:ComplexDataProperty | aorsl:EnumerationProperty">
          
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
