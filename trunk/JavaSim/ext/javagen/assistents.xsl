<?xml version="1.0" encoding="UTF-8"?>

<!--
        This transformation contain some usefull templates.
        
        $Rev$
        $Date$

        @author:   Jens Werner (jens.werner@tu-cottbus.de)
        @last changed by $Author$
-->

<xsl:stylesheet version="2.0" xmlns:aorsl="http://aor-simulation.org" xmlns:aorsml="http://aor-simulation.org"
  xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:schemaLocation="http://aor-simulation.org aorsml.xsd" xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">

  <!-- return the UWORD with a lower startletter -->
  <xsl:function name="jw:lowerWord">
    <xsl:param name="uWord" as="xs:string"/>
    <xsl:variable name="firstLetter">
      <xsl:value-of select="substring($uWord, 1, 1)"/>
    </xsl:variable>
    <xsl:variable name="letters">
      <xsl:value-of select="substring($uWord, 2, string-length($uWord))"/>
    </xsl:variable>
    <xsl:value-of select="fn:concat(fn:lower-case($firstLetter), $letters)"/>
  </xsl:function>

  <!-- return the UWORD with a upper startletter -->
  <xsl:function name="jw:upperWord">
    <xsl:param name="uWord" as="xs:string"/>
    <xsl:variable name="firstLetter">
      <xsl:value-of select="substring($uWord, 1, 1)"/>
    </xsl:variable>
    <xsl:variable name="letters">
      <xsl:value-of select="substring($uWord, 2, string-length($uWord))"/>
    </xsl:variable>
    <xsl:value-of select="fn:concat(fn:upper-case($firstLetter), $letters)"/>
  </xsl:function>

  <!-- return the VALUE with quotes -->
  <!-- if value is '*' then creates an empty string [""] -->
  <xsl:function name="jw:quote">
    <xsl:param name="value" as="xs:string"/>
    <xsl:choose>
      <xsl:when test="$value = $empty.string.quotation.symbol">
        <xsl:value-of select="'&quot;&quot;'"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="fn:concat('&quot;', $value, '&quot;')"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <!-- NEW -->
  <xsl:function name="jw:parenthesise">
    <xsl:param name="content" as="xs:string"/>
    <xsl:value-of select="fn:concat('(', $content, ')')"/>
  </xsl:function>

  <xsl:function name="jw:createCollectionVarname">
    <xsl:param name="collectionNode" as="node()"/>
    <xsl:value-of
      select="fn:concat(jw:lowerWord(jw:lowerWord($collectionNode/@itemType)), $collection.class.aORCollection, $collectionNode/@name, $collectionNode/@id)"
    />
  </xsl:function>

  <xsl:function name="jw:createInternalVarName" as="xs:string">
    <xsl:param name="varName"/>
    <xsl:value-of select="fn:concat($createdVariablesNamePrefix, $varName)"/>
  </xsl:function>

  <!-- all attributes as param of a class incl. superclassattributes -->
  <xsl:template match="aorsml:PhysicalObjectType | aorsml:PhysicalAgentType | aorsml:AgentType | aorsml:ObjectType | aorsml:BeliefEntityType"
    mode="assistents.constructor.allAttributes">
    <xsl:choose>
      <xsl:when test="fn:exists(@superType)">
        <xsl:for-each
          select="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty | aorsml:EnumerationProperty | aorsml:BeliefAttribute">

          <xsl:choose>
            <xsl:when test="@upperMultiplicity eq 'unbounded'">
              <!--<xsl:message>unbounded upperMultiplicity is not yet implemented</xsl:message>-->
              <xsl:call-template name="java:createParam">
                <xsl:with-param name="type" select="fn:concat('List&lt;', jw:upperWord(jw:mappeDataType(@type)), '&gt;')"/>
                <xsl:with-param name="name" select="@name"/>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>

              <xsl:call-template name="java:createParam">
                <xsl:with-param name="type" select="@type"/>
                <xsl:with-param name="name" select="@name"/>
              </xsl:call-template>
            </xsl:otherwise>

          </xsl:choose>
        </xsl:for-each>
        <xsl:apply-templates select="//aorsml:EntityTypes/*[@name = current()/@superType]" mode="assistents.constructor.allAttributes"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:for-each
          select="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty | aorsml:EnumerationProperty | aorsml:BeliefAttribute">

          <xsl:choose>
            <xsl:when test="@upperMultiplicity eq 'unbounded'">
              <xsl:call-template name="java:createParam">
                <xsl:with-param name="type" select="fn:concat('List&lt;', jw:upperWord(jw:mappeDataType(@type)), '&gt;')"/>
                <xsl:with-param name="name" select="@name"/>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>

              <xsl:call-template name="java:createParam">
                <xsl:with-param name="type" select="@type"/>
                <xsl:with-param name="name" select="@name"/>
              </xsl:call-template>

            </xsl:otherwise>
          </xsl:choose>

        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- all superclassattributes as variablenames -->
  <xsl:template match="aorsml:PhysicalObjectType | aorsml:PhysicalAgentType | aorsml:AgentType | aorsml:ObjectType | aorsml:BeliefEntityType"
    mode="assistents.constructor.allSuperAttributes">
    <xsl:param name="current" select="false()" as="xs:boolean"/>
    <xsl:if test="$current">
      <xsl:for-each
        select="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty | aorsml:EnumerationProperty | aorsml:BeliefAttribute">
        <xsl:value-of select="@name"/>
      </xsl:for-each>
    </xsl:if>
    <xsl:choose>
      <xsl:when test="fn:exists(@superType)">
        <xsl:apply-templates select="//aorsml:EntityTypes/*[@name = current()/@superType]" mode="assistents.constructor.allSuperAttributes">
          <xsl:with-param name="current" select="true()"/>
        </xsl:apply-templates>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- all selfBeliefAttributes of a class incl. superclassattributes -->
  <xsl:template match="aorsml:PhysicalAgentType | aorsml:AgentType" mode="assistents.constructor.allSelfBeliefAttributes">
    <xsl:choose>
      <xsl:when test="fn:exists(@superType)">
        <xsl:for-each select="aorsml:SelfBeliefAttribute">
          <xsl:call-template name="java:createParam">
            <xsl:with-param name="type" select="@type"/>
            <xsl:with-param name="name" select="@name"/>
          </xsl:call-template>
        </xsl:for-each>
        <xsl:apply-templates select="//aorsml:EntityTypes/*[@name = current()/@superType]" mode="assistents.constructor.allSelfBeliefAttributes"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:for-each select="aorsml:SelfBeliefAttribute">
          <xsl:call-template name="java:createParam">
            <xsl:with-param name="type" select="@type"/>
            <xsl:with-param name="name" select="@name"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- all superclassselfBeliefAttributes as variablenames -->
  <xsl:template match="aorsml:PhysicalAgentType | aorsml:AgentType" mode="assistents.constructor.allSuperSelfBeliefAttributes">
    <xsl:param name="current" select="false()" as="xs:boolean"/>
    <xsl:if test="$current">
      <xsl:for-each select="aorsml:SelfBeliefAttribute">
        <xsl:value-of select="@name"/>
      </xsl:for-each>
    </xsl:if>
    <xsl:choose>
      <xsl:when test="fn:exists(@superType)">
        <xsl:apply-templates select="//aorsml:EntityTypes/*[@name = current()/@superType]" mode="assistents.constructor.allSuperSelfBeliefAttributes">
          <xsl:with-param name="current" select="true()"/>
        </xsl:apply-templates>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- all attributes of a class incl. superclassattributes, but without default attributes like x, perceptionRadius, ... -->
  <xsl:template match="aorsml:PhysicalObjectType | aorsml:PhysicalAgentType | aorsml:AgentType | aorsml:ObjectType"
    mode="assistents.list.allAttributes">
    <xsl:copy-of select="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty | aorsml:EnumerationProperty"/>
    <xsl:choose>
      <xsl:when test="fn:exists(@superType)">
        <xsl:apply-templates select="//aorsml:EntityTypes/*[@name = current()/@superType]" mode="assistents.list.allAttributes"/>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- all selfBeliefattributes of a class incl. superclassattributes -->
  <xsl:template match="aorsml:PhysicalAgentType | aorsml:AgentType" mode="assistents.list.allSelfBeliefAttributes">
    <xsl:copy-of select="aorsml:SelfBeliefAttribute"/>
    <xsl:choose>
      <xsl:when test="fn:exists(@superType)">
        <xsl:apply-templates select="//aorsml:EntityTypes/*[@name = current()/@superType]" mode="assistents.list.allSelfBeliefAttributes"/>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- creates getClassVariable methods -->
  <xsl:template
    match="aorsml:SelfBeliefAttribute | aorsml:Attribute | aorsml:GridCellProperty |  aorsml:ReferenceProperty | 
     aorsml:EnumerationProperty | aorsml:ComplexDataProperty  | aorsml:BeliefAttribute | 
     aorsml:BeliefReferenceProperty | aorsml:SelfBeliefReferenceProperty"
    mode="assistents.getVariableMethod">
    <xsl:param name="indent" select="0"/>
    <xsl:param name="static" as="xs:boolean" select="false()"/>
    <xsl:param name="extraContent"/>

    <xsl:if test="not(@upperMultiplicity eq 'unbounded')">
      <xsl:call-template name="java:createGetter">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="static" select="$static"/>
        <xsl:with-param name="variableType" select="@type | @dataType | @refDataType"/>
        <xsl:with-param name="variableName" select="@name"/>
        <xsl:with-param name="extraContent" select="$extraContent"/>
      </xsl:call-template>
    </xsl:if>

  </xsl:template>

  <xsl:template match="aorsml:GlobalVariable" mode="assistents.getVariableMethod">
    <xsl:param name="indent" select="0"/>
    <xsl:param name="static" as="xs:boolean" select="false()"/>
    <xsl:param name="staticClassName" as="xs:string" select="''"/>
    <xsl:param name="extraContent"/>

    <xsl:choose>
      <xsl:when test="@upperMultiplicity eq 'unbounded'">
        <xsl:call-template name="java:createGetter">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="static" select="$static"/>
          <xsl:with-param name="staticClassName" select="$staticClassName"/>
          <xsl:with-param name="variableType" select="fn:concat('java.util.List&lt;', jw:mappeDataType(@dataType | @refDataType), '&gt;')"/>
          <xsl:with-param name="variableName" select="@name"/>
          <xsl:with-param name="extraContent" select="$extraContent"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="java:createGetter">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="static" select="$static"/>
          <xsl:with-param name="variableType" select="jw:mappeDataType(@dataType | @refDataType)"/>
          <xsl:with-param name="variableName" select="@name"/>
          <xsl:with-param name="extraContent" select="$extraContent"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!--creates setClassVariable methods -->
  <xsl:template
    match="aorsml:GridCellProperty |  aorsml:ReferenceProperty | 
    aorsml:EnumerationProperty | aorsml:ComplexDataProperty  | aorsml:BeliefAttribute | 
    aorsml:BeliefReferenceProperty | aorsml:SelfBeliefReferenceProperty"
    mode="assistents.setVariableMethod">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="static" select="false()"/>
    <xsl:param name="staticClassName" select="''"/>
    <xsl:param name="changeCheck" select="false()" as="xs:boolean"/>
    <xsl:param name="extraContent"/>

    <xsl:call-template name="java:createSetter">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="variableType">
        <xsl:choose>
          <xsl:when test="@upperMultiplicity eq 'unbounded'">
            <xsl:value-of select="fn:concat('List&lt;', jw:upperWord(jw:mappeDataType(@type)), '&gt;')"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@type | @dataType | @refDataType"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="variableName" select="@name"/>
      <xsl:with-param name="modifier">
        <xsl:choose>
          <xsl:when test="@upperMultiplicity eq 'unbounded'">
            <xsl:value-of select="'private'"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'public'"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="static" select="$static"/>
      <xsl:with-param name="staticClassName" select="$staticClassName"/>
      <xsl:with-param name="changeCheck" select="$changeCheck"/>
      <xsl:with-param name="extraContent" select="$extraContent"/>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsml:SelfBeliefAttribute | aorsml:Attribute | aorsml:GlobalVariable" mode="assistents.setVariableMethod">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="static" select="false()"/>
    <xsl:param name="staticClassName" select="''"/>
    <xsl:param name="changeCheck" select="false()" as="xs:boolean"/>
    <xsl:param name="extraContent"/>

    <xsl:variable name="constrainContent">
      <xsl:if test="@minValue or @maxValue">
        <xsl:choose>
          <xsl:when test="(@type|@dataType) eq 'Integer' or (@type|@dataType) eq 'Float'">

            <xsl:if test="@minValue">

              <xsl:call-template name="java:if">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="condition">
                  <xsl:choose>
                    <xsl:when test="(@type|@dataType) eq 'Integer' or (@type|@dataType) eq 'Float'">

                      <xsl:call-template name="java:boolExpr">
                        <xsl:with-param name="value1" select="@name"/>
                        <xsl:with-param name="value2" select="@minValue"/>
                        <xsl:with-param name="operator" select="'&lt;'"/>
                      </xsl:call-template>

                    </xsl:when>

                  </xsl:choose>

                </xsl:with-param>
                <xsl:with-param name="thenContent">

                  <xsl:call-template name="java:systemPrintln">
                    <xsl:with-param name="indent" select="$indent + 2"/>
                    <xsl:with-param name="err" select="true()"/>
                    <xsl:with-param name="value">
                      <xsl:text>[ERROR] minValue constraint violation in </xsl:text>
                      <xsl:value-of select="concat(../@name, '.', @name)"/>
                    </xsl:with-param>
                  </xsl:call-template>
                  <xsl:call-template name="java:systemPrintln">
                    <xsl:with-param name="indent" select="$indent + 2"/>
                    <xsl:with-param name="err" select="true()"/>
                    <xsl:with-param name="value">
                      <xsl:text>use the minValue: </xsl:text>
                      <xsl:value-of select="@minValue"/>
                    </xsl:with-param>
                  </xsl:call-template>

                  <xsl:call-template name="java:variable">
                    <xsl:with-param name="indent" select="$indent + 2"/>
                    <xsl:with-param name="name" select="@name"/>
                    <xsl:with-param name="value" select="@minValue"/>
                  </xsl:call-template>

                </xsl:with-param>
              </xsl:call-template>

            </xsl:if>

            <xsl:if test="@maxValue">

              <xsl:call-template name="java:if">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="condition">
                  <xsl:choose>
                    <xsl:when test="(@type|@dataType) eq 'Integer' or (@type|@dataType) eq 'Float'">

                      <xsl:call-template name="java:boolExpr">
                        <xsl:with-param name="value1" select="@name"/>
                        <xsl:with-param name="value2" select="@maxValue"/>
                        <xsl:with-param name="operator" select="'&gt;'"/>
                      </xsl:call-template>

                    </xsl:when>

                  </xsl:choose>

                </xsl:with-param>
                <xsl:with-param name="thenContent">

                  <xsl:call-template name="java:systemPrintln">
                    <xsl:with-param name="indent" select="$indent + 2"/>
                    <xsl:with-param name="err" select="true()"/>
                    <xsl:with-param name="value">
                      <xsl:text>[ERROR] maxValue constraint violation in </xsl:text>
                      <xsl:value-of select="concat(../@name, '.', @name)"/>
                    </xsl:with-param>
                  </xsl:call-template>
                  <xsl:call-template name="java:systemPrintln">
                    <xsl:with-param name="indent" select="$indent + 2"/>
                    <xsl:with-param name="err" select="true()"/>
                    <xsl:with-param name="value">
                      <xsl:text>use the maxValue: </xsl:text>
                      <xsl:value-of select="@maxValue"/>
                    </xsl:with-param>
                  </xsl:call-template>

                  <xsl:call-template name="java:variable">
                    <xsl:with-param name="indent" select="$indent + 2"/>
                    <xsl:with-param name="name" select="@name"/>
                    <xsl:with-param name="value" select="@maxValue"/>
                  </xsl:call-template>

                </xsl:with-param>
              </xsl:call-template>

            </xsl:if>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message>
              <xsl:text>[ERROR] Non supported constrain-type </xsl:text>
              <xsl:value-of select="@type"/>
              <xsl:text> in </xsl:text>
              <xsl:value-of select="concat(local-name(..), ' [', ../@name, '] ')"/>
              <xsl:value-of select="concat(local-name(), ' [', @name, ']')"/>
            </xsl:message>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:if>

    </xsl:variable>

    <xsl:call-template name="java:createSetter">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="variableType">
        <xsl:choose>
          <xsl:when test="@upperMultiplicity eq 'unbounded'">
            <xsl:value-of select="fn:concat('java.util.List&lt;', jw:mappeDataType(@type | @dataType | @refDataType), '&gt;')"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@type | @dataType | @refDataType"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="variableName" select="@name"/>
      <xsl:with-param name="modifier">
        <xsl:choose>
          <xsl:when test="@upperMultiplicity eq 'unbounded'">
            <xsl:value-of select="'private'"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'public'"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="static" select="$static"/>
      <xsl:with-param name="staticClassName" select="$staticClassName"/>
      <xsl:with-param name="changeCheck" select="$changeCheck"/>
      <xsl:with-param name="constrainContent" select="$constrainContent"/>
      <xsl:with-param name="extraContent" select="$extraContent"/>
    </xsl:call-template>

    <xsl:apply-templates select=".[@minValue or @maxValue]" mode="assistents.createGettersForMinMaxInformations">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates>

  </xsl:template>

  <xsl:template match="aorsml:SelfBeliefAttribute | aorsml:Attribute | aorsml:GlobalVariable" mode="assistents.createGettersForMinMaxInformations">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:if test="@minValue">

      <xsl:call-template name="java:method">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="modifier" select="'public'"/>
        <xsl:with-param name="static" select="true()"/>
        <xsl:with-param name="type" select="@type | @dataType "/>
        <xsl:with-param name="name" select="fn:concat('get__', jw:upperWord(@name), 'Min')"/>
        <xsl:with-param name="content">
          <xsl:call-template name="java:return">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="value" select="@minValue"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>

    </xsl:if>

    <xsl:if test="@maxValue">

      <xsl:call-template name="java:method">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="modifier" select="'public'"/>
        <xsl:with-param name="static" select="true()"/>
        <xsl:with-param name="type" select="@type | @dataType "/>
        <xsl:with-param name="name" select="fn:concat('get__', jw:upperWord(@name), 'Max')"/>
        <xsl:with-param name="content">
          <xsl:call-template name="java:return">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="value" select="@maxValue"/>
          </xsl:call-template>
        </xsl:with-param>
      </xsl:call-template>

    </xsl:if>

  </xsl:template>

  <!-- create a special setter method for predefined properties (mapping to the super.setter)-->
  <xsl:template match="aorsml:Attribute" mode="assistents.setVariableMethod.predefinedProps">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="static" select="false()"/>
    <xsl:param name="staticClassName" select="''"/>


    <xsl:if test="@minValue or @maxValue">

      <xsl:variable name="constrainContent">

        <xsl:choose>
          <xsl:when test="@type eq 'Integer' or @type eq 'Float'">

            <xsl:if test="@minValue">

              <xsl:call-template name="java:if">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="condition">
                  <xsl:choose>
                    <xsl:when test="@type eq 'Integer' or @type eq 'Float'">

                      <xsl:call-template name="java:boolExpr">
                        <xsl:with-param name="value1" select="@name"/>
                        <xsl:with-param name="value2" select="@minValue"/>
                        <xsl:with-param name="operator" select="'&lt;'"/>
                      </xsl:call-template>

                    </xsl:when>

                  </xsl:choose>

                </xsl:with-param>
                <xsl:with-param name="thenContent">

                  <xsl:call-template name="java:systemPrintln">
                    <xsl:with-param name="indent" select="$indent + 2"/>
                    <xsl:with-param name="err" select="true()"/>
                    <xsl:with-param name="value">
                      <xsl:text>[ERROR] minValue constraint violation in </xsl:text>
                      <xsl:value-of select="concat(../@name, '.', @name)"/>
                    </xsl:with-param>
                  </xsl:call-template>
                  <xsl:call-template name="java:systemPrintln">
                    <xsl:with-param name="indent" select="$indent + 2"/>
                    <xsl:with-param name="err" select="true()"/>
                    <xsl:with-param name="value">
                      <xsl:text>use the minValue: </xsl:text>
                      <xsl:value-of select="@minValue"/>
                    </xsl:with-param>
                  </xsl:call-template>

                  <xsl:call-template name="java:variable">
                    <xsl:with-param name="indent" select="$indent + 2"/>
                    <xsl:with-param name="name" select="@name"/>
                    <xsl:with-param name="value" select="@minValue"/>
                  </xsl:call-template>

                </xsl:with-param>
              </xsl:call-template>

            </xsl:if>

            <xsl:if test="@maxValue">

              <xsl:call-template name="java:if">
                <xsl:with-param name="indent" select="$indent + 1"/>
                <xsl:with-param name="condition">
                  <xsl:choose>
                    <xsl:when test="@type eq 'Integer' or @type eq 'Float'">

                      <xsl:call-template name="java:boolExpr">
                        <xsl:with-param name="value1" select="@name"/>
                        <xsl:with-param name="value2" select="@maxValue"/>
                        <xsl:with-param name="operator" select="'&gt;'"/>
                      </xsl:call-template>

                    </xsl:when>

                  </xsl:choose>

                </xsl:with-param>
                <xsl:with-param name="thenContent">

                  <xsl:call-template name="java:systemPrintln">
                    <xsl:with-param name="indent" select="$indent + 2"/>
                    <xsl:with-param name="err" select="true()"/>
                    <xsl:with-param name="value">
                      <xsl:text>[ERROR] maxValue constraint violation in </xsl:text>
                      <xsl:value-of select="concat(../@name, '.', @name)"/>
                    </xsl:with-param>
                  </xsl:call-template>
                  <xsl:call-template name="java:systemPrintln">
                    <xsl:with-param name="indent" select="$indent + 2"/>
                    <xsl:with-param name="err" select="true()"/>
                    <xsl:with-param name="value">
                      <xsl:text>use the maxValue: </xsl:text>
                      <xsl:value-of select="@maxValue"/>
                    </xsl:with-param>
                  </xsl:call-template>

                  <xsl:call-template name="java:variable">
                    <xsl:with-param name="indent" select="$indent + 2"/>
                    <xsl:with-param name="name" select="@name"/>
                    <xsl:with-param name="value" select="@maxValue"/>
                  </xsl:call-template>

                </xsl:with-param>
              </xsl:call-template>

            </xsl:if>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message>
              <xsl:text>[ERROR] Non supported constrain-type </xsl:text>
              <xsl:value-of select="@type"/>
              <xsl:text> in </xsl:text>
              <xsl:value-of select="concat(local-name(..), ' [', ../@name, '] ')"/>
              <xsl:value-of select="concat(local-name(), ' [', @name, ']')"/>
            </xsl:message>
          </xsl:otherwise>
        </xsl:choose>

      </xsl:variable>

      <xsl:call-template name="java:method">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="modifier" select="'public'"/>
        <xsl:with-param name="name" select="concat('set', jw:upperWord(@name))"/>
        <xsl:with-param name="parameterList">
          <xsl:call-template name="java:createParam">
            <xsl:with-param name="type" select="@type"/>
            <xsl:with-param name="name" select="@name"/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="content">

          <xsl:value-of select="$constrainContent"/>

          <xsl:call-template name="java:callSetterMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="objInstance" select="'super'"/>
            <xsl:with-param name="instVariable" select="@name"/>
            <xsl:with-param name="value" select="@name"/>
          </xsl:call-template>

        </xsl:with-param>
      </xsl:call-template>

    </xsl:if>

  </xsl:template>

  <!-- creates class variables -->
  <xsl:template
    match="aorsml:SelfBeliefAttribute | aorsml:Attribute | aorsml:GridCellProperty | aorsml:ReferenceProperty | 
    aorsml:EnumerationProperty | aorsml:ComplexDataProperty | aorsml:BeliefAttribute | aorsml:SelfBeliefReferenceProperty"
    mode="assistents.classVariable">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:choose>
      <xsl:when test="@upperMultiplicity eq 'unbounded'">

        <xsl:call-template name="java:newObject">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'private'"/>
          <xsl:with-param name="class" select="'List'"/>
          <xsl:with-param name="generic" select="jw:upperWord(jw:mappeDataType(@type))"/>
          <xsl:with-param name="varName" select="@name"/>
          <xsl:with-param name="withDeclaration" select="false()"/>
        </xsl:call-template>

      </xsl:when>
      <xsl:otherwise>

        <xsl:call-template name="java:newObject">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="'private'"/>
          <xsl:with-param name="class" select="jw:mappeDataType(@type)"/>
          <xsl:with-param name="varName" select="@name"/>
          <xsl:with-param name="withDeclaration" select="false()"/>
        </xsl:call-template>

      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <xsl:template match="@*" mode="assistents.classVariable">
    <xsl:param name="indent" as="xs:integer?"/>
    <xsl:param name="type" required="yes"/>

    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'private'"/>
      <xsl:with-param name="type" select="jw:mappeDataType($type)"/>
      <xsl:with-param name="name" select="name()"/>
    </xsl:call-template>

  </xsl:template>

  <!-- set initialValue of an attribute -->
  <!-- if it exists and not empty -->
  <xsl:template match="aorsml:Attribute" mode="assistents.setInitialAttributeValue">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="asClassVariable" select="false()"/>
    <xsl:if test="@initialValue and @initialValue != ''">
      <xsl:call-template name="java:variable">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="name">
          <xsl:choose>
            <xsl:when test="$asClassVariable">
              <xsl:call-template name="java:varByDotNotation">
                <xsl:with-param name="name" select="'this'"/>
                <xsl:with-param name="varName" select="@name"/>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="@name"/>
            </xsl:otherwise>
          </xsl:choose>

        </xsl:with-param>
        <xsl:with-param name="value">
          <xsl:choose>
            <xsl:when test="@type = 'String'">
              <xsl:value-of select="jw:quote(@initialValue)"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="@initialValue"/>
            </xsl:otherwise>
          </xsl:choose>

        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <xsl:template match="aorsml:Slot | aorsml:SelfBeliefSlot | aorsml:BeliefSlot" mode="assistents.getSlotValue">
    <xsl:param name="type"/>
    <xsl:choose>
      <!-- the using of xsi:type is depricated -->
      <!-- its only used for downwards compatible -->
      <xsl:when test="(resolve-QName(@xsi:type, .) eq QName('http://aor-simulation.org', 'SimpleSlot')) or @value">
        <xsl:choose>
          <xsl:when test="fn:starts-with(@value, '{')">
            <!-- DO NOTHING: it is an JSON-String -->

            <!-- for tests -->
            <xsl:value-of select="'null'"/>
          </xsl:when>
          <xsl:when test="$type = 'String'">
            <xsl:value-of select="jw:quote(@value)"/>
          </xsl:when>
          <!-- NOTICE: be sure that value is from Enumeration -->
          <xsl:when test="$type eq $core.enum.materialType">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="$core.enum.materialType"/>
              <xsl:with-param name="varName" select="@value"/>
            </xsl:call-template>
          </xsl:when>
          <!-- NOTICE: be sure that value is from Enumeration -->
          <xsl:when test="$type eq $core.enum.shape2D">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="$core.enum.shape2D"/>
              <xsl:with-param name="varName" select="@value"/>
            </xsl:call-template>
          </xsl:when>
          <!-- NOTICE: be sure that value is from Enumeration -->
          <xsl:when test="$type eq $core.enum.shape3D">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="$core.enum.shape3D"/>
              <xsl:with-param name="varName" select="@value"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="@value"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <!-- the using of xsi:type is depricated -->
      <!-- its only used for downwards compatible -->
      <xsl:when
        test="((resolve-QName(@xsi:type, .) eq QName('http://aor-simulation.org', 'OpaqueExprSlot')) and 
        fn:exists(aorsml:ValueExpr[@language = $output.language])) or
        fn:exists(aorsml:ValueExpr[@language = $output.language])">
        <xsl:value-of select="fn:normalize-space(aorsml:ValueExpr[@language = $output.language][1])"/>
      </xsl:when>
      <xsl:when test="fn:exists(aorsml:ValueExpr[@language = $output.language])">
        <xsl:value-of select="fn:normalize-space(aorsml:ValueExpr[@language = $output.language][1])"/>
      </xsl:when>
      <xsl:when test="fn:exists(aorsml:RandomVariable)">
        <xsl:apply-templates select="aorsml:RandomVariable/aorsml:*" mode="assistents.distribution"/>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- get the enum-value in the form: EnumName.Literal -->
  <xsl:template match="aorsml:Slot" mode="assistents.getEnumSlotValue">
    <xsl:param name="enumeration" required="yes"/>

    <xsl:variable name="slotValue">
      <xsl:apply-templates select="." mode="assistents.getSlotValue"/>
    </xsl:variable>

    <xsl:choose>
      <!-- NOTICE: this is also true if e.g. WORD.WORD.LETTER* -->
      <xsl:when test="fn:matches($slotValue, '\w*\.\w*')">
        <xsl:variable name="enumLiteral" select="$enumeration/aorsml:EnumerationLiteral[text() eq fn:tokenize($slotValue, '\.')[2]][1]"/>
        <xsl:choose>
          <xsl:when test="$enumLiteral">
            <xsl:value-of select="fn:concat($enumeration/@name, '.', $enumLiteral)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message>
              <xsl:text>[ERROR] unknown EnumLiteral in Slot in TypeInit for [</xsl:text>
              <xsl:value-of select="../@type"/>
              <xsl:text>]!</xsl:text>
            </xsl:message>
          </xsl:otherwise>
        </xsl:choose>

      </xsl:when>
      <xsl:otherwise>
        <xsl:message>
          <xsl:text>[ERROR] Wrong Enum-Value-Format in Slot! </xsl:text>
          <xsl:text>Value: </xsl:text>
          <xsl:value-of select="$slotValue"/>
          <xsl:text> Use [Enumeration/@name].[EnumerationLiteral/text()]</xsl:text>
        </xsl:message>

      </xsl:otherwise>
    </xsl:choose>


  </xsl:template>

  <xsl:template match="@initialValue" mode="assistents.getValue">
    <xsl:param name="type"/>
    <xsl:choose>
      <xsl:when test="$type = 'String'">
        <xsl:value-of select="jw:quote(.)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="."/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- set the value for enums -->
  <xsl:template match="@initialValue" mode="assistents.getEnumValue">
    <xsl:param name="enumeration" required="yes"/>

    <xsl:variable name="enumLiteral" select="$enumeration/aorsml:EnumerationLiteral[text() eq fn:normalize-space(current())][1]"/>
    <xsl:choose>
      <xsl:when test="$enumLiteral">
        <xsl:value-of select="fn:concat($enumeration/@name, '.', $enumLiteral)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message>
          <xsl:text>[ERROR] unknown EnumLiteral in @initialValue in [</xsl:text>
          <xsl:value-of select="../../@name"/>
          <xsl:text>]!</xsl:text>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!-- for all expressionvalues -->
  <xsl:template match="aorsml:XCoordinate | aorsml:YCoordinate" mode="assistents.getExpression">
    <xsl:value-of select=".[@language eq $output.language]"/>
  </xsl:template>

  <!-- calls by objectVariablesDeclaration -->
  <xsl:template match="aorsml:FOR" mode="assistents.getEntityRef">
    <xsl:choose>
      <xsl:when test="fn:exists(aorsml:ObjectIdRef[@language eq $output.language])">
        <xsl:value-of select="aorsml:ObjectIdRef[@language eq $output.language]"/>
      </xsl:when>
      <xsl:when test="fn:exists(@objectIdRef)">
        <xsl:value-of select="@objectIdRef"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="aorsml:ObjectRef[@language = $output.language]"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="aorsml:UpdateObjects | aorsml:PhysicalObjects | aorsml:PhysicalAgents | aorsml:Agents | aorsml:Objects | aorsml:DestroyObjects"
    mode="assistents.getStartID">
    <xsl:choose>
      <xsl:when test="fn:exists(@rangeStartID)">
        <xsl:value-of select="@rangeStartID"/>
      </xsl:when>
      <xsl:when test="fn:exists(aorsml:RangeStartID[@language = $output.language])">
        <xsl:value-of select="aorsml:RangeStartID[@language = $output.language]"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message>
          <xsl:text>ERROR: No StartId defined in: </xsl:text>
          <xsl:value-of select="local-name()"/>
          <xsl:text> in </xsl:text>
          <xsl:value-of select="local-name(..)"/>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="aorsml:UpdateObjects | aorsml:PhysicalObjects | aorsml:PhysicalAgents | aorsml:Agents | aorsml:Objects | aorsml:DestroyObjects"
    mode="assistents.getEndID">
    <xsl:choose>
      <xsl:when test="fn:exists(@rangeEndID)">
        <xsl:value-of select="@rangeEndID"/>
      </xsl:when>
      <xsl:when test="fn:exists(aorsml:RangeEndID[@language = $output.language])">
        <xsl:value-of select="aorsml:RangeEndID[@language = $output.language]"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message>
          <xsl:text>ERROR: No EndId defined in: </xsl:text>
          <xsl:value-of select="local-name()"/>
          <xsl:text> in </xsl:text>
          <xsl:value-of select="local-name(..)"/>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="@*" mode="assistents.setPhysicalObjectProperty">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="physObjName" required="yes"/>

    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance" select="$physObjName"/>
      <xsl:with-param name="instVariable" select="name()"/>
      <xsl:with-param name="value">
        <xsl:choose>
          <xsl:when test="local-name() eq 'materialType'">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="$core.enum.materialType"/>
              <xsl:with-param name="varName" select="."/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="local-name() eq 'shape2D'">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="$core.enum.shape2D"/>
              <xsl:with-param name="varName" select="."/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="local-name() eq 'shape3D'">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="$core.enum.shape3D"/>
              <xsl:with-param name="varName" select="."/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="local-name() eq 'points'">
            <xsl:value-of select="jw:quote(.)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="."/>
          </xsl:otherwise>
        </xsl:choose>

      </xsl:with-param>
      <xsl:with-param name="valueType" select="if (name()='name') then 'String' else ''"/>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsml:Slot" mode="assistents.setPhysicalObjectProperty">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="physObjName" required="yes"/>

    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance" select="$physObjName"/>
      <xsl:with-param name="instVariable" select="@property"/>
      <xsl:with-param name="value" select="@value"/>
      <xsl:with-param name="valueType">
        <xsl:choose>
          <xsl:when test="local-name(current()/..) = 'PhysicalAgent' or local-name(current()/..) = 'PhysicalAgentSet'">
            <xsl:value-of select="//aorsml:PhysicalAgentType[@name=current()/../@type]/aorsml:Attribute[@name=current()/@property]/@type"/>
          </xsl:when>
          <xsl:when test="local-name(current()/..) = 'PhysicalObject' or local-name(current()/..) = 'PhysicalObjectSet'">
            <xsl:value-of select="//aorsml:PhysicalObjectType[@name=current()/../@type]/aorsml:Attribute[@name=current()/@property]/@type"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message select="'Problem in assistents.setPhysicalObjectProperty'"/>
          </xsl:otherwise>
        </xsl:choose>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsml:SelfBeliefSlot" mode="assistents.setSelfBeliefProperty">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="agentName" required="yes"/>

    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance" select="$agentName"/>
      <xsl:with-param name="instVariable" select="@property"/>
      <xsl:with-param name="value">
        <xsl:choose>
          <xsl:when test="fn:exists(@value)">
            <xsl:value-of select="@value"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:choose>
              <xsl:when test="fn:exists(aorsml:ValueExpr[@language eq $output.language])">
                <xsl:value-of select="fn:normalize-space(aorsml:ValueExpr[@language eq $output.language])"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:message>No value for SelfBeliefSlot</xsl:message>
              </xsl:otherwise>
            </xsl:choose>

          </xsl:otherwise>
        </xsl:choose>

      </xsl:with-param>
      <xsl:with-param name="valueType">
        <xsl:value-of select="//aorsml:PhysicalAgentType[@name=current()/../@type]/aorsml:SelfBeliefAttribute[@name=current()/@property]/@type"/>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsml:Attribute" mode="assistents.setInheritedProperty">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:choose>
      <xsl:when test="@upperMultiplicity eq 'unbounded'">
        <xsl:message>unbounded upperMultiplicity is not yet implemented</xsl:message>
      </xsl:when>
      <xsl:otherwise>

        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="objInstance" select="'this'"/>
              <xsl:with-param name="instVariable" select="'InheritedProperty'"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="method" select="fn:concat('set', jw:upperWord(jw:mappeDataType(@type)), 'Property')"/>
          <xsl:with-param name="args" as="xs:string*">
            <xsl:value-of select="jw:quote(@name)"/>
            <xsl:value-of select="@name"/>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <xsl:template match="aorsml:ReferenceProperty" mode="assistents.setInheritedProperty">
    <xsl:param name="indent" as="xs:integer" required="yes"/>

    <xsl:choose>
      <xsl:when test="@upperMultiplicity eq 'unbounded'">
        <xsl:message>unbounded upperMultiplicity is not yet implemented</xsl:message>
      </xsl:when>
      <xsl:otherwise>

        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="objInstance">
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="objInstance" select="'this'"/>
              <xsl:with-param name="instVariable" select="'InheritedProperty'"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="method" select="'setObjektProperty'"/>
          <xsl:with-param name="args" as="xs:string*">
            <xsl:value-of select="jw:quote(@name)"/>
            <xsl:value-of select="@name"/>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <xsl:template match="aorsml:Increment" mode="assistents.increment">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="objectVariable" required="yes" as="xs:string"/>
    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance" select="$objectVariable"/>
      <xsl:with-param name="instVariable" select="@property"/>
      <xsl:with-param name="value">
        <xsl:call-template name="java:callGetterMethod">
          <xsl:with-param name="objInstance" select="$objectVariable"/>
          <xsl:with-param name="instVariable" select="@property"/>
          <xsl:with-param name="inLine" select="true()"/>
        </xsl:call-template>
        <xsl:value-of select="fn:concat(' + ', @value)"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="aorsml:Decrement" mode="assistents.decrement">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="objectVariable" required="yes" as="xs:string"/>
    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance" select="$objectVariable"/>
      <xsl:with-param name="instVariable" select="@property"/>
      <xsl:with-param name="value">
        <xsl:call-template name="java:callGetterMethod">
          <xsl:with-param name="objInstance" select="$objectVariable"/>
          <xsl:with-param name="instVariable" select="@property"/>
          <xsl:with-param name="inLine" select="true()"/>
        </xsl:call-template>
        <xsl:value-of select="fn:concat(' - ', @value)"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- get the delay -->
  <xsl:template
    match="aorsml:OutMessageEventExpr | aorsml:CausedEventExpr | aorsml:PerceptionEventExpr |
    aorsml:InMessageEventExpr | aorsml:ActionEventExpr | aorsml:ReminderEventExpr | 
    aorsml:ActivityStartEventExpr | aorsml:ActivityEndEventExpr"
    mode="assistents.getDelay">
    <xsl:choose>
      <xsl:when test="fn:exists(aorsml:Delay/aorsml:ValueExpr[@language eq $output.language])">
        <xsl:value-of select="aorsml:Delay/aorsml:ValueExpr[@language eq $output.language][1]"/>
      </xsl:when>
      <xsl:when test="fn:exists(aorsml:Delay/aorsml:DiscreteRandomVariable)">
        <xsl:apply-templates select="aorsml:Delay/aorsml:DiscreteRandomVariable/aorsml:*" mode="assistents.distribution"/>
      </xsl:when>
      <xsl:when test="fn:exists(@delay)">
        <xsl:value-of select="@delay"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="'1'"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- get type of variable-->
  <!-- calls by objectVariablesDeclaration -->
  <xsl:template match="aorsml:FOR" mode="assistents.getVariableType">

    <xsl:variable name="objektType" as="xs:string">
      <xsl:choose>
        <xsl:when test="fn:exists(@objectType)">
          <xsl:choose>
            <xsl:when test="@objectType = 'Collection'">
              <xsl:variable name="genericType">
                <xsl:choose>
                  <xsl:when test="@objectIdRef">
                    <xsl:value-of select="//aorsml:Collections/aorsml:Collection[@id = current()/@objectIdRef]/@itemType"/>
                  </xsl:when>
                  <xsl:when test="@objectName">
                    <xsl:value-of select="//aorsml:Collections/aorsml:Collection[@name = current()/@objectName]/@itemType"/>
                  </xsl:when>
                </xsl:choose>
              </xsl:variable>
              <xsl:choose>
                <xsl:when test="$genericType and ($genericType != '')">
                  <xsl:call-template name="java:classWithGenericType">
                    <xsl:with-param name="class" select="$collection.class.aORCollection"/>
                    <xsl:with-param name="genericType" select="$genericType"/>
                  </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="''"/>
                  <xsl:message>
                    <xsl:text>[ERROR] Unknown CollectionType [</xsl:text>
                    <xsl:value-of select="@objectName"/>
                    <xsl:text>] in Rule: </xsl:text>
                    <xsl:value-of select="../@name"/>
                    <xsl:text> !</xsl:text>
                  </xsl:message>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:when>
            <xsl:when test="@objectType eq 'PhysicalAgent' or @objectType eq 'Agent'">
              <xsl:value-of select="fn:concat(@objectType, 'Object')"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="@objectType"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$core.class.object"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- check if this kind of entityType exists -->
    <xsl:choose>
      <xsl:when
        test="@objectType = 'Collection' or 
              @objectType eq 'PhysicalAgent' or 
              @objectType eq 'Agent' or 
              @objectType eq 'Objekt' or 
              @objectType eq 'PhysicalObject' or 
              not(@objectType)">
        <!-- do nothing -->
      </xsl:when>
      <xsl:when test="$objektType != ''">
        <xsl:if test="not (fn:exists(//aorsml:EntityTypes/*[@name = $objektType]))">
          <xsl:message>
            <xsl:text>[ERROR] No associated EntityType found in </xsl:text>
            <xsl:value-of select="fn:concat(local-name(.), ' objectType: [', $objektType, '] in ',local-name(..), ': [', ../@name, ']!')"/>
          </xsl:message>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message>
          <xsl:text>No objectType found in </xsl:text>
          <xsl:value-of select="fn:concat(local-name(.), ' in ',local-name(..), ': [', ../@name, ']!')"/>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>

    <!-- return -->
    <xsl:value-of select="$objektType"/>

  </xsl:template>

  <!-- set the  InitialAttributeValue for Attributes -->
  <xsl:template match="aorsml:InitialAttributeValue" mode="assistents.setInitialAttributeValue">

    <xsl:variable name="type">
      <xsl:apply-templates select=".." mode="assistents.getAttributeType">
        <xsl:with-param name="attributName" select="@attribute"/>
      </xsl:apply-templates>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$type eq 'String'">
        <xsl:value-of select="jw:quote(@value)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="@value"/>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!-- set the  InitialAttributeValue for Enumerations -->
  <xsl:template match="aorsml:InitialAttributeValue" mode="assistents.setInitialEnumValue">
    <xsl:param name="enumeration" required="yes"/>

    <xsl:variable name="type">
      <xsl:apply-templates select=".." mode="assistents.getAttributeType">
        <xsl:with-param name="attributName" select="@attribute"/>
      </xsl:apply-templates>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$type eq $enumeration/@name">
        <xsl:variable name="enumLiteral" select="$enumeration/aorsml:EnumerationLiteral[text() eq fn:normalize-space(current()/@value)][1]"/>

        <xsl:choose>
          <xsl:when test="$enumLiteral">
            <xsl:value-of select="fn:concat($enumeration/@name, '.', $enumLiteral)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:message>
              <xsl:text>[ERROR] unknown EnumLiteral in InitialAttributeValue in [</xsl:text>
              <xsl:value-of select="../@name"/>
              <xsl:text>]!</xsl:text>
            </xsl:message>
          </xsl:otherwise>
        </xsl:choose>

      </xsl:when>

      <xsl:otherwise>
        <xsl:message>
          <xsl:text>[ERROR] Unknown Enumeration in InitialAttributeValue in [</xsl:text>
          <xsl:value-of select="../@name"/>
          <xsl:text>]!</xsl:text>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!-- set the  InitialAttributeValue for SelfBeliefAttributes -->
  <xsl:template match="aorsml:InitialAttributeValue" mode="assistents.setInitialSelfBeliefAttributeValue">

    <xsl:variable name="type">
      <xsl:apply-templates select=".." mode="assistents.getSelfBeliefAttributeType">
        <xsl:with-param name="attributName" select="@attribute"/>
      </xsl:apply-templates>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$type eq 'String'">
        <xsl:value-of select="jw:quote(@value)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="@value"/>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!-- set defaultAttributes from InitialAttributValue if exists -->
  <xsl:template match="aorsml:PhysicalObjectType | aorsml:PhysicalAgentType | aorsml:Agent | aorsml:Object" mode="assistents.setInitialAttributeValue">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="attrName" as="xs:string" required="yes"/>
    <xsl:param name="varName" as="xs:string" required="yes" tunnel="yes"/>

    <xsl:choose>
      <xsl:when test="fn:exists(aorsml:InitialAttributeValue[@attribute eq $attrName])">
        <xsl:call-template name="java:callSetterMethod">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="objInstance" select="$varName"/>
          <xsl:with-param name="instVariable" select="$attrName"/>
          <xsl:with-param name="value">
            <xsl:variable name="initAttrValue" select="aorsml:InitialAttributeValue[@attribute eq $attrName][1]/@value"/>
            <xsl:choose>
              <xsl:when test="$attrName eq 'materialType'">
                <xsl:value-of select="concat($core.enum.materialType, '.', $initAttrValue)"/>
              </xsl:when>
              <xsl:when test="$attrName eq 'shape2D'">
                <xsl:value-of select="concat($core.enum.shape2D, '.', $initAttrValue)"/>
              </xsl:when>
              <xsl:when test="$attrName eq 'shape3D'">
                <xsl:value-of select="concat($core.enum.shape3D, '.', $initAttrValue)"/>
              </xsl:when>
              <xsl:when test="$attrName eq 'points'">
                <xsl:value-of select="jw:quote($initAttrValue)"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="$initAttrValue"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>

      <xsl:when test="@superType">
        <xsl:apply-templates select="//aorsml:EntityTypes/*[@name = current()/@superType]" mode="assistents.setInitialAttributeValue">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="attrName" select="$attrName"/>
        </xsl:apply-templates>
      </xsl:when>

    </xsl:choose>

  </xsl:template>

  <!-- get the type of an InitialAttributeValue from EntityType or from the SuperType -->
  <xsl:template match="aorsml:PhysicalObjectType | aorsml:PhysicalAgentType | aorsml:ObjectType | aorsml:AgentType" mode="assistents.getAttributeType">
    <xsl:param name="attributName" as="xs:string" required="yes"/>

    <xsl:choose>
      <xsl:when test="fn:exists(aorsml:Attribute[@name eq $attributName])">
        <xsl:value-of select="aorsml:Attribute[@name eq $attributName]/@type"/>
      </xsl:when>
      <xsl:when test="fn:exists(aorsml:ReferenceProperty[@name eq $attributName])">
        <xsl:value-of select="aorsml:ReferenceProperty[@name eq $attributName]/@type"/>
      </xsl:when>
      <xsl:when test="fn:exists(aorsml:ComplexDataProperty[@name eq $attributName])">
        <xsl:value-of select="aorsml:ComplexDataProperty[@name eq $attributName]/@type"/>
      </xsl:when>
      <xsl:when test="fn:exists(aorsml:EnumerationProperty[@name eq $attributName])">
        <xsl:value-of select="aorsml:EnumerationProperty[@name eq $attributName]/@type"/>
      </xsl:when>
      <xsl:when test="fn:exists(@superType)">
        <xsl:apply-templates select="//aorsml:EntityTypes/*[@name = current()/@superType]" mode="assistents.getAttributeType">
          <xsl:with-param name="attributName" select="$attributName"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message>
          <xsl:text>[ERROR] No type found for </xsl:text>
          <xsl:value-of select="$attributName"/>
          <xsl:text>!</xsl:text>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!-- get the type of an InitialAttributeValue from EntityType or from the SuperType -->
  <xsl:template match="aorsml:PhysicalAgentType | aorsml:AgentType" mode="assistents.getSelfBeliefAttributeType">
    <xsl:param name="attributName" as="xs:string" required="yes"/>

    <xsl:choose>
      <xsl:when test="fn:exists(aorsml:SelfBeliefAttribute[@name eq $attributName])">
        <xsl:value-of select="aorsml:SelfBeliefAttribute[@name eq $attributName]/@type"/>
      </xsl:when>
      <xsl:when test="fn:exists(@superType)">
        <xsl:apply-templates select="//aorsml:EntityTypes/*[@name = current()/@superType]" mode="assistents.getSelfBeliefAttributeType">
          <xsl:with-param name="attributName" select="$attributName"/>
        </xsl:apply-templates>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message>
          <xsl:text>[ERROR] No type found for </xsl:text>
          <xsl:value-of select="$attributName"/>
          <xsl:text>!</xsl:text>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!-- calls by dataVariableDeclaration -->
  <xsl:template match="aorsml:FOR" mode="assistents.setDataVariableDeclarationClassVariables">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:choose>
      <xsl:when test="@refDataType">
        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="modifier" select="'private'"/>
          <xsl:with-param name="type" select="@refDataType"/>
          <xsl:with-param name="name" select="@dataVariable"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="@dataType">
        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="modifier" select="'private'"/>
          <xsl:with-param name="type" select="jw:mappeDataType(@dataType)"/>
          <xsl:with-param name="name" select="@dataVariable"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message>
          <xsl:text>[ERROR] no type defined for FOR-DataVariableDeclaration [</xsl:text>
          <xsl:value-of select="@dataVariable"/>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!-- calls by dataVariableDeclaration -->
  <xsl:template match="aorsml:FOR" mode="assistents.setDataVariableDeclaration">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:if test="fn:exists(aorsml:ValueExpr[@language eq $output.language])">
      <xsl:call-template name="java:variable">
        <xsl:with-param name="indent" select="$indent"/>
        <xsl:with-param name="name">
          <xsl:call-template name="java:varByDotNotation">
            <xsl:with-param name="varName" select="@dataVariable"/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="value">
          <xsl:value-of select="aorsml:ValueExpr[@language eq $output.language]"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <!-- calls by dataVariableDeclaration -->
  <xsl:template match="aorsml:FOR" mode="assistents.dataVariableDeclarationcheckNull">
    <xsl:if test="@refDataType">
      <xsl:call-template name="java:boolExpr">
        <xsl:with-param name="value1">
          <xsl:call-template name="java:varByDotNotation">
            <xsl:with-param name="varName" select="@dataVariable"/>
          </xsl:call-template>
        </xsl:with-param>
        <xsl:with-param name="value2" select="'null'"/>
        <xsl:with-param name="operator" select="'!='"/>
      </xsl:call-template>
      <xsl:if test="position() != last()">
        <xsl:value-of select="' &amp;&amp; '"/>
      </xsl:if>
    </xsl:if>
  </xsl:template>

  <!-- some annotations -->
  <!-- @SuppressWarnings(value = "unchecked") -->
  <xsl:template name="getAnnotationSuppressWarnings.unchecked">
    <xsl:call-template name="java:createAnnotation">
      <xsl:with-param name="annotationName" select="'SuppressWarnings'"/>
      <xsl:with-param name="annotationValues" as="xs:string*" select="'unchecked'"/>
    </xsl:call-template>
  </xsl:template>
  <!-- @SuppressWarnings(value = "serial") -->
  <xsl:template name="getAnnotationSuppressWarnings.serial">
    <xsl:call-template name="java:createAnnotation">
      <xsl:with-param name="annotationName" select="'SuppressWarnings'"/>
      <xsl:with-param name="annotationValues" as="xs:string*" select="'serial'"/>
    </xsl:call-template>
  </xsl:template>

  <!-- get the next continuous DataVariableDeclarations -->
  <!-- calls by dataVariableDeclaration -->
  <xsl:template match="aorsml:FOR" mode="assistents.getNextDataVariableDeclarations">
    <xsl:param name="following-direction" select="true()"/>
    <xsl:copy-of select="."/>
    <xsl:choose>
      <xsl:when test="$following-direction">
        <xsl:if test="local-name(following-sibling::*[1]) eq 'DataVariableDeclaration'">
          <xsl:apply-templates select="following-sibling::*[1]" mode="assistents.getNextDataVariableDeclarations"/>
        </xsl:if>
      </xsl:when>
    </xsl:choose>
  </xsl:template>


  <!-- ********************************************************************************************************************************* -->
  <!-- ***********************                               A C T I V I T I E S                                  ***************************** -->
  <!-- ********************************************************************************************************************************* -->

  <!-- set the  StartEventCorrelationProperty -->
  <xsl:template name="setStartEndEventCorrelation">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="eventExpr" required="yes" as="node()"/>
    <xsl:param name="envEvtVarName" required="yes" as="xs:string"/>

    <xsl:choose>
      <xsl:when test="$eventExpr/aorsml:CorrelationValue[@language eq $output.language]">
        <xsl:call-template name="java:callSetterMethod">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="objInstance" select="$envEvtVarName"/>
          <xsl:with-param name="instVariable" select="'correlationValue'"/>
          <xsl:with-param name="value" select="$eventExpr/aorsml:CorrelationValue[@language eq $output.language][1]"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="$eventExpr/@correlationValue">
        <xsl:call-template name="java:callSetterMethod">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="objInstance" select="$envEvtVarName"/>
          <xsl:with-param name="instVariable" select="'correlationValue'"/>
          <xsl:with-param name="value" select="jw:quote($eventExpr/@correlationValue)"/>
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- set the  EndEventCorrelationProperty -->
  <xsl:template name="setEndEventCorrelation">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="eventExpr" required="yes"/>
    <xsl:param name="envEvtVarName" required="yes" as="xs:string"/>

    <xsl:choose>
      <xsl:when test="$eventExpr/aorsml:EndEventCorrelation[@language eq $output.language][1]">
        <xsl:call-template name="java:callSetterMethod">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="objInstance" select="$envEvtVarName"/>
          <xsl:with-param name="instVariable" select="'correlationValue'"/>
          <xsl:with-param name="value" select="$eventExpr/aorsml:EndEventCorrelation[@language eq $output.language][1]"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:when test="$eventExpr/@endEventCorrelation">
        <xsl:call-template name="java:callSetterMethod">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="objInstance" select="$envEvtVarName"/>
          <xsl:with-param name="instVariable" select="'correlationValue'"/>
          <xsl:with-param name="value" select="jw:quote($eventExpr/@endEventCorrelation)"/>
        </xsl:call-template>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- ********************************************************************************************************************************* -->
  <!-- ***********************                               R U L E S                                     ***************************** -->
  <!-- ********************************************************************************************************************************* -->

  <!-- this section contains template for EnvironmentRules and InitializationRules -->

  <!-- **********************                        EXECUTION                 ************************ -->
  <xsl:template match="aorsml:EnvironmentRule | aorsml:InitializationRule" mode="assistents.createRule.createEnvInitRuleExec">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:apply-templates select="aorsml:FOR[@objectVariable]" mode="createRules.helper.method.execute.fixedById.setObjVarDeclarationsReceiveOrder">
      <xsl:with-param name="indent" select="$indent"/>
    </xsl:apply-templates>

    <xsl:variable name="listPostfix" select="'List'"/>

    <!-- complete entity-Sets -->
    <xsl:variable name="completeObjectSets"
      select="aorsml:FOR[@objectVariable]
        [not(fn:exists(@objectIdRef) or  fn:exists(aorsml:ObjectRef) or fn:exists(aorsml:ObjectIdRef) or fn:exists(@objectName)) and 
          not(fn:exists(@rangeStartID) and fn:exists(@rangeEndID)) and 
          not(@objectType ='Collection')]"/>

    <xsl:if test="fn:count($completeObjectSets) > 0">
      <xsl:apply-templates select="$completeObjectSets" mode="createRules.helper.method.execute">
        <xsl:with-param name="indent" select="$indent + 1"/>
        <xsl:with-param name="listPostfix" select="$listPostfix"/>
      </xsl:apply-templates>
    </xsl:if>

    <!-- objSubSet -->
    <xsl:variable name="objectSetRange" select="aorsml:FOR[@objectVariable][fn:exists(@rangeStartID) or fn:exists(@rangeEndID)]"/>
    <xsl:variable name="entitieSeqCount" select="fn:count($objectSetRange) + fn:count($completeObjectSets)"/>

    <xsl:choose>
      <xsl:when test="fn:exists(($objectSetRange, $completeObjectSets))">
        <xsl:call-template name="java:newLine"/>

        <!-- fixed ObjectVariableDeclarations (if there are ObjectVariableDeclarations before the first ) -->
        <xsl:apply-templates select="($objectSetRange, $completeObjectSets)[1]/preceding-sibling::aorsml:FOR[@dataVariable]"
          mode="assistents.setDataVariableDeclaration">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>
        <xsl:variable name="iteratorPostfix" select="'Id'"/>
        <xsl:variable name="forEachLoopPostfix" select="'Obj'"/>

        <xsl:call-template name="assistents.createRule.createEnvInitRuleExec.nestedLoops">
          <xsl:with-param name="set"
            select="aorsml:FOR[@objectVariable][not(fn:exists(@objectIdRef) or fn:exists(aorsml:ObjectRef) or fn:exists(aorsml:ObjectIdRef) or 
            fn:exists(@objectName)) and 
            not(fn:exists(@rangeStartID) and fn:exists(@rangeEndID)) and not(@objectType ='Collection')]|aorsml:FOR[@objectVariable][fn:exists(@rangeStartID) or fn:exists(@rangeEndID)]"/>
          <xsl:with-param name="iteratorPostfix" select="$iteratorPostfix"/>
          <xsl:with-param name="forEachLoopPostfix" select="$forEachLoopPostfix"/>
          <xsl:with-param name="listPostfix" select="$listPostfix"/>
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="content">

            <!-- DataVariableDeclarations -->
            <xsl:apply-templates select="aorsml:FOR[@dataVariable]" mode="assistents.setDataVariableDeclaration">
              <xsl:with-param name="indent" select="$indent + $entitieSeqCount + 1"/>
            </xsl:apply-templates>

            <xsl:call-template name="java:newLine"/>
            <xsl:apply-templates select="." mode="createRules.helper.method.execute.setProcessMethods">
              <xsl:with-param name="indent" select="$indent + $entitieSeqCount + 1"/>
            </xsl:apply-templates>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:when>
      <xsl:otherwise>

        <!-- DataVariableDeclarations -->
        <xsl:apply-templates select="aorsml:FOR[@dataVariable]" mode="assistents.setDataVariableDeclaration">
          <xsl:with-param name="indent" select="$indent + 1"/>
        </xsl:apply-templates>

        <xsl:apply-templates select="." mode="createRules.helper.method.execute.setProcessMethods">
          <xsl:with-param name="indent" select="$indent+$entitieSeqCount + 1"/>
        </xsl:apply-templates>

      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <xsl:template name="assistents.createRule.createEnvInitRuleExec.nestedLoops">
    <xsl:param name="set"/>
    <xsl:param name="iteratorPostfix"/>
    <xsl:param name="forEachLoopPostfix"/>
    <xsl:param name="listPostfix"/>
    <xsl:param name="indent"/>
    <xsl:param name="content"/>

    <xsl:variable name="forElement" select="$set[1]"/>
    <xsl:choose>
      <xsl:when test="$forElement">

        <xsl:variable name="objVar" select="fn:concat($forElement/@objectVariable, $listPostfix)"/>
        <xsl:variable name="nextIndent" select="$indent + 1"/>

        <xsl:variable name="loopContent">
          <xsl:apply-templates select="$forElement" mode="createRules.helper.method.execute.setLoopObjectVariable">
            <xsl:with-param name="indent" select="$nextIndent"/>
            <xsl:with-param name="loopPostfix">
              <xsl:choose>
                <xsl:when test="fn:exists($forElement/@rangeStartID) or fn:exists($forElement/@rangeEndID)">
                  <xsl:value-of select="$iteratorPostfix"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="$forEachLoopPostfix"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
            <xsl:with-param name="subSet">
              <xsl:choose>
                <xsl:when test="fn:exists($forElement/@rangeStartID) or fn:exists($forElement/@rangeEndID)">
                  <xsl:value-of select="true()"/>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:value-of select="false()"/>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:with-param>
          </xsl:apply-templates>

          <xsl:variable name="followingDataVariables">
            <xsl:apply-templates
              select="$forElement/following-sibling::aorsml:FOR[@dataVariable][generate-id(preceding-sibling::aorsml:FOR[@objectVariable][1]) = generate-id($forElement)]"
              mode="assistents.getNextDataVariableDeclarations"/>
          </xsl:variable>

          <xsl:for-each select="$followingDataVariables">
            <xsl:apply-templates select="." mode="assistents.setDataVariableDeclaration">
              <xsl:with-param name="indent" select="$nextIndent"/>
            </xsl:apply-templates>
          </xsl:for-each>

          <xsl:if test="$forElement/aorsml:SelectionCondition[@language eq $output.language]">
            <xsl:call-template name="java:if">
              <xsl:with-param name="indent" select="$nextIndent"/>
              <xsl:with-param name="condition" select="fn:concat('!(', $forElement/aorsml:SelectionCondition[@language eq $output.language][1], ')')"/>
              <xsl:with-param name="thenContent">
                <xsl:call-template name="java:codeLine">
                  <xsl:with-param name="indent" select="$nextIndent + 1"/>
                  <xsl:with-param name="content" select="'continue'"/>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:if>

          <xsl:call-template name="assistents.createRule.createEnvInitRuleExec.nestedLoops">
            <xsl:with-param name="forEachLoopPostfix" select="$forEachLoopPostfix"/>
            <xsl:with-param name="iteratorPostfix" select="$iteratorPostfix"/>
            <xsl:with-param name="content" select="$content"/>
            <xsl:with-param name="listPostfix" select="$listPostfix"/>
            <xsl:with-param name="indent" select="$nextIndent"/>
            <xsl:with-param name="set" select="$set[generate-id()!=generate-id($forElement)]"/>
          </xsl:call-template>
        </xsl:variable>

        <xsl:choose>
          <!-- use java for-each -->
          <xsl:when
            test="$forElement[not(fn:exists(@objectIdRef) or fn:exists(aorsml:ObjectRef)) and not(fn:exists(@rangeStartID) and fn:exists(@rangeEndID))]">

            <xsl:call-template name="java:for-each-loop">
              <xsl:with-param name="indent" select="$indent"/>
              <xsl:with-param name="elementType" select="$core.class.object">
                <!--                <xsl:choose>
                  <xsl:when test="fn:exists(//aorsml:PhysicalAgentType[@name = $e1/@objectType])">
                    <xsl:value-of select="$core.class.physAgentObject"/>
                  </xsl:when>
                  <xsl:when test="fn:exists(//aorsml:PhysicalObjectType[@name = $e1/@objectType])">
                    <xsl:value-of select="$core.class.physicalObjekt"/>
                  </xsl:when>
                  <xsl:when test="fn:exists(//aorsml:ObjectType[@name = $e1/@objectType])">
                    <xsl:value-of select="$core.class.object"/>
                  </xsl:when>
                  <xsl:when test="fn:exists(//aorsml:AgentType[@name = $e1/@objectType])">
                    <xsl:value-of select="$core.class.agentObject"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:message>
                      <xsl:text>[ERROR] No associated ObjectType found in </xsl:text>
                      <xsl:value-of
                        select="fn:concat(local-name($e1), ' objectType: [', $e1/@objectType, '] in ',local-name($e1/..), ': [', $e1/../@name, ']!')"
                      />
                    </xsl:message>
                  </xsl:otherwise>
                </xsl:choose>-->
              </xsl:with-param>
              <xsl:with-param name="elementVarName" select="concat($forElement/@objectVariable, $forEachLoopPostfix)"/>
              <xsl:with-param name="listVarName" select="$objVar"/>
              <xsl:with-param name="content" select="$loopContent"/>
            </xsl:call-template>
          </xsl:when>

          <!-- use java-for-loop -->
          <xsl:otherwise>
            <xsl:variable name="iteratorVar" select="fn:concat($forElement/@objectVariable, $iteratorPostfix)"/>

            <xsl:call-template name="java:for-loop">
              <xsl:with-param name="indent" select="$indent"/>
              <xsl:with-param name="loopVariable" select="$iteratorVar"/>
              <xsl:with-param name="loopVarType" select="'long'"/>
              <xsl:with-param name="start" select="$forElement/@rangeStartID"/>
              <xsl:with-param name="condition">
                <xsl:call-template name="java:boolExpr">
                  <xsl:with-param name="value1" select="$iteratorVar"/>
                  <xsl:with-param name="value2" select="$forElement/@rangeEndID"/>
                  <xsl:with-param name="operator" select="'&lt;='"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="increment" select="1"/>
              <xsl:with-param name="content" select="$loopContent"/>
            </xsl:call-template>
          </xsl:otherwise>
        </xsl:choose>

      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="$content"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- this template is used to receive the order of  FOR-ObjectVariableDeclaration's-->
  <!-- calls by objectVariablesDeclaration -->
  <xsl:template match="aorsml:FOR" mode="createRules.helper.method.execute.fixedById.setObjVarDeclarationsReceiveOrder">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <!-- fixed physObj by id-->
    <xsl:apply-templates select=".[(fn:exists(@objectIdRef) or fn:exists(aorsml:ObjectIdRef)) and not(@objectType ='Collection')]"
      mode="createRules.helper.method.execute.fixedById">
      <xsl:with-param name="indent" select="$indent + 1"/>
    </xsl:apply-templates>

    <!-- fixed physObj by ref-->
    <xsl:apply-templates
      select=".[fn:exists(aorsml:ObjectRef) and not (fn:exists(@objectIdRef) or fn:exists(aorsml:ObjectIdRef)) and not(@objectType ='Collection')]"
      mode="createRules.helper.method.execute.fixedByRef">
      <xsl:with-param name="indent" select="$indent + 1"/>
    </xsl:apply-templates>

    <!-- fixed physObj by name-->
    <xsl:apply-templates
      select=".[not(fn:exists(@objectIdRef) or fn:exists(aorsml:ObjectRef) or fn:exists(aorsml:ObjectIdRef)) and 
          fn:exists(@objectName) and not(@objectType ='Collection')]"
      mode="createRules.helper.method.execute.fixedByName">
      <xsl:with-param name="indent" select="$indent + 1"/>
    </xsl:apply-templates>

    <!-- collections -->
    <xsl:apply-templates select=".[@objectType = 'Collection']" mode="createRules.helper.method.execute.fixedCollection">
      <xsl:with-param name="indent" select="$indent + 1"/>
    </xsl:apply-templates>
  </xsl:template>


  <!--sets class variables (ObjectSet)-->
  <!-- calls by objectVariablesDeclaration -->
  <xsl:template match="aorsml:FOR" mode="createRules.helper.method.execute">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="listPostfix" required="yes" as="xs:string"/>

    <xsl:variable name="listVar" select="fn:concat(@objectVariable, $listPostfix)"/>

    <xsl:call-template name="java:newArrayListObject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="useInterfaceAsTypeName" select="true()"/>
      <xsl:with-param name="generic" select="$core.class.object"/>
      <xsl:with-param name="name" select="$listVar"/>
      <xsl:with-param name="withDeclaration" select="false()"/>
    </xsl:call-template>

    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name" select="$listVar"/>
      <xsl:with-param name="value">
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="inLine" select="true()"/>
          <xsl:with-param name="objInstance">
            <!-- be shure that AbstractSimulator and environmentrule have the method getEnvironmentSimulator -->
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="objInstance">
                <xsl:choose>
                  <!-- for environmentrules -->
                  <xsl:when test="local-name(..) eq 'EnvironmentRule'">
                    <xsl:value-of select="'this'"/>
                  </xsl:when>
                  <!-- for initialisationrules -->
                  <xsl:otherwise>
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="name" select="$sim.class.simulatorMain"/>
                      <xsl:with-param name="varName" select="'this'"/>
                    </xsl:call-template>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
              <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="method" select="'getObjectsByType'"/>
          <xsl:with-param name="args" as="xs:string*">
            <xsl:value-of select="fn:concat(@objectType, '.class')"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!--sets class variables (if only one specific entity its involved)-->
  <!-- calls by objectVariablesDeclaration -->
  <xsl:template match="aorsml:FOR" mode="createRules.helper.method.execute.fixedById">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:variable name="objType" as="xs:string">
      <xsl:choose>
        <xsl:when test="fn:exists(@objectType)">
          <xsl:apply-templates select="." mode="assistents.getVariableType"/>
        </xsl:when>
        <xsl:when test="fn:exists(@objectIdRef)">
          <xsl:value-of select="$core.class.object"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="''"/>
        </xsl:otherwise>

      </xsl:choose>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$objType != ''">
        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="castType" select="jw:upperWord($objType)"/>
          <xsl:with-param name="name">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="varName" select="@objectVariable"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="value">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance">
                <!-- be shure that AbstractSimulator and environmentrule have the method getEnvironmentSimulator -->
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="objInstance">
                    <xsl:choose>
                      <!-- for environmentrules and activities-->
                      <xsl:when test="local-name(..) eq 'EnvironmentRule' or local-name(..) eq 'ActivityType'">
                        <xsl:value-of select="'this'"/>
                      </xsl:when>
                      <!-- for initialisationrules -->
                      <xsl:otherwise>
                        <xsl:call-template name="java:varByDotNotation">
                          <xsl:with-param name="name" select="$sim.class.simulatorMain"/>
                          <xsl:with-param name="varName" select="'this'"/>
                        </xsl:call-template>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:with-param>
                  <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
                  <xsl:with-param name="inLine" select="true()"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="method" select="'getObjectById'"/>
              <xsl:with-param name="args" as="xs:string*">
                <xsl:apply-templates select="." mode="assistents.getEntityRef"/>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message>
          <xsl:text>No objekttype for </xsl:text>
          <xsl:value-of select="local-name()"/>
          <xsl:text> (id: </xsl:text>
          <xsl:value-of select="@objectIdRef"/>
          <xsl:text>) found. [</xsl:text>
          <xsl:value-of select="../@name"/>
          <xsl:text>]</xsl:text>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!--sets class variables (if only one specific entity its involved)-->
  <!-- calls by objectVariablesDeclaration -->
  <xsl:template match="aorsml:FOR" mode="createRules.helper.method.execute.fixedByName">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:variable name="objType" as="xs:string">
      <xsl:choose>
        <xsl:when test="fn:exists(@objectType)">
          <xsl:apply-templates select="." mode="assistents.getVariableType"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$core.class.object"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$objType != ''">
        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="castType" select="jw:upperWord($objType)"/>
          <xsl:with-param name="name">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="varName" select="@objectVariable"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="value">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance">

                <!-- be shure that AbstractSimulator and environmentrule have the method getEnvironmentSimulator -->
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="objInstance">
                    <xsl:choose>
                      <!-- for environmentrules -->
                      <xsl:when test="local-name(..) eq 'EnvironmentRule'">
                        <xsl:value-of select="'this'"/>
                      </xsl:when>
                      <!-- for initialisationrules -->
                      <xsl:otherwise>
                        <xsl:call-template name="java:varByDotNotation">
                          <xsl:with-param name="name" select="$sim.class.simulatorMain"/>
                          <xsl:with-param name="varName" select="'this'"/>
                        </xsl:call-template>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:with-param>
                  <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
                  <xsl:with-param name="inLine" select="true()"/>
                </xsl:call-template>

              </xsl:with-param>
              <xsl:with-param name="method" select="'getObjectByName'"/>
              <xsl:with-param name="args" as="xs:string*" select="jw:quote(@objectName)"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message>
          <xsl:text>No objekttype for </xsl:text>
          <xsl:value-of select="local-name()"/>
          <xsl:text> (name: </xsl:text>
          <xsl:value-of select="@objectName"/>
          <xsl:text>) found. [</xsl:text>
          <xsl:value-of select="../@name"/>
          <xsl:text>]</xsl:text>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!-- calls by objectVariablesDeclaration -->
  <xsl:template match="aorsml:FOR" mode="createRules.helper.method.execute.fixedByRef">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:variable name="objType">
      <xsl:apply-templates select="." mode="assistents.getVariableType"/>
    </xsl:variable>
    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="castType" select="if (fn:exists(@objectType)) then jw:upperWord($objType) else ''"/>
      <xsl:with-param name="name">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="varName" select="@objectVariable"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="value" select="aorsml:ObjectRef[@language = $output.language]"/>
    </xsl:call-template>

  </xsl:template>

  <!-- set collections -->
  <!-- calls by objectVariablesDeclaration -->
  <xsl:template match="aorsml:FOR" mode="createRules.helper.method.execute.fixedCollection">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="varName" select="@objectVariable"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="value">
        <xsl:choose>
          <xsl:when test="@objectIdRef">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="objInstance">

                <!-- be shure that AbstractSimulator and environmentrule have the method getEnvironmentSimulator -->
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="objInstance">
                    <xsl:choose>
                      <!-- for environmentrules or activities -->
                      <xsl:when test="local-name(..) eq 'EnvironmentRule' or local-name(..) eq 'ActivityType'">
                        <xsl:value-of select="'this'"/>
                      </xsl:when>
                      <!-- for initialisationrules -->
                      <xsl:otherwise>
                        <xsl:call-template name="java:varByDotNotation">
                          <xsl:with-param name="name" select="$sim.class.simulatorMain"/>
                          <xsl:with-param name="varName" select="'this'"/>
                        </xsl:call-template>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:with-param>
                  <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
                  <xsl:with-param name="inLine" select="true()"/>
                </xsl:call-template>

              </xsl:with-param>
              <xsl:with-param name="method" select="'getCollectionById'"/>
              <xsl:with-param name="args" select="@objectIdRef"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:when>
          <xsl:when test="@objectName">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="objInstance">
                <!-- be shure that AbstractSimulator and environmentrule have the method getEnvironmentSimulator -->
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="objInstance">
                    <xsl:choose>
                      <!-- for environmentrules -->
                      <xsl:when test="local-name(..) eq 'EnvironmentRule'">
                        <xsl:value-of select="'this'"/>
                      </xsl:when>
                      <!-- for initialisationrules -->
                      <xsl:otherwise>
                        <xsl:call-template name="java:varByDotNotation">
                          <xsl:with-param name="name" select="$sim.class.simulatorMain"/>
                          <xsl:with-param name="varName" select="'this'"/>
                        </xsl:call-template>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:with-param>
                  <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
                  <xsl:with-param name="inLine" select="true()"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="method" select="'getCollectionByName'"/>
              <xsl:with-param name="args" select="jw:quote(@objectName)"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:when>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="castType">
        <xsl:call-template name="java:classWithGenericType">
          <xsl:with-param name="genericType">
            <xsl:choose>
              <xsl:when test="@objectName">
                <xsl:value-of select="//aorsml:Collections/aorsml:Collection[@name = current()/@objectName]/@itemType"/>
              </xsl:when>
              <xsl:when test="@objectIdRef">
                <xsl:value-of select="//aorsml:Collections/aorsml:Collection[@id = current()/@objectIdRef]/@itemType"/>
              </xsl:when>
            </xsl:choose>
          </xsl:with-param>
          <xsl:with-param name="class" select="$collection.class.aORCollection"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- calls by objectVariablesDeclaration -->
  <xsl:template match="aorsml:FOR" mode="createRules.helper.method.execute.setLoopObjectVariable">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="loopPostfix" required="yes" as="xs:string"/>
    <xsl:param name="subSet" as="xs:boolean" required="yes"/>
    <xsl:call-template name="java:newLine"/>
    <xsl:choose>
      <xsl:when test="$subSet">

        <!-- e.g. this.variable = ([@objectType]) this.getEnvironmentSimulator.getPhysAgentById(id) -->
        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="name">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="varName" select="@objectVariable"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="value">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="typecast" select="jw:upperWord(@objectType)"/>
              <xsl:with-param name="objInstance">
                <!-- be shure that AbstractSimulator and environmentrule have the method getEnvironmentSimulator -->
                <xsl:call-template name="java:callGetterMethod">
                  <xsl:with-param name="objInstance">
                    <xsl:choose>
                      <!-- for environmentrules -->
                      <xsl:when test="local-name(..) eq 'EnvironmentRule'">
                        <xsl:value-of select="'this'"/>
                      </xsl:when>
                      <!-- for initialisationrules -->
                      <xsl:otherwise>
                        <xsl:call-template name="java:varByDotNotation">
                          <xsl:with-param name="name" select="$sim.class.simulatorMain"/>
                          <xsl:with-param name="varName" select="'this'"/>
                        </xsl:call-template>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:with-param>
                  <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
                  <xsl:with-param name="inLine" select="true()"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="method">
                <xsl:choose>
                  <xsl:when test="fn:exists(//aorsml:PhysicalAgentType[@name = current()/@objectType])">
                    <xsl:value-of select="'getPhysAgentById'"/>
                  </xsl:when>
                  <xsl:when test="fn:exists(//aorsml:PhysicalObjectType[@name = current()/@objectType])">
                    <xsl:value-of select="'getPhysicalObjectById'"/>
                  </xsl:when>
                  <xsl:when test="fn:exists(//aorsml:ObjectType[@name = current()/@objectType])">
                    <xsl:value-of select="'getObjectById'"/>
                  </xsl:when>
                  <xsl:when test="fn:exists(//aorsml:AgentType[@name = current()/@objectType])">
                    <xsl:value-of select="'getAgentById'"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:message>
                      <xsl:text>[ERROR] No associated ObjectType found in </xsl:text>
                      <xsl:value-of select="fn:concat(local-name(.), ' objectType: [', @objectType, '] in ',local-name(..), ': [', ../@name, ']!')"/>
                    </xsl:message>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
              <xsl:with-param name="args" as="xs:string*">
                <xsl:value-of select="fn:concat(@objectVariable, $loopPostfix)"/>
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="name" select="jw:upperWord(@objectType)"/>
                  <xsl:with-param name="varName" select="'class'"/>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:when>

      <xsl:otherwise>

        <!-- e.g. this.variable = (SomePhysicalAgentType) someLoopVariable-->
        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="name">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="varName" select="@objectVariable"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="castType" select="jw:upperWord(@objectType)"/>
          <xsl:with-param name="value">
            <xsl:value-of select="fn:concat(@objectVariable, $loopPostfix)"/>
          </xsl:with-param>
        </xsl:call-template>

      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <!-- calls by objectVariablesDeclaration -->
  <xsl:template match="aorsml:FOR" mode="createRules.helper.method.execute.setLoopVariableFromSet">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="forEachLoopPostfix" required="yes" as="xs:string"/>

    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="varName" select="@objectVariable"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="castType" select="jw:upperWord(@objectType)"/>
      <xsl:with-param name="value">
        <xsl:value-of select="fn:concat(@objectVariable, $forEachLoopPostfix)"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!--sets loop variables for a subset-->
  <!-- calls by objectVariablesDeclaration -->
  <xsl:template match="aorsml:FOR" mode="createRules.helper.method.execute.setLoopVariableSet">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="iteratorPostfix" required="yes" as="xs:string"/>

    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="name" select="'this'"/>
          <xsl:with-param name="varName" select="@objectVariable"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="value">
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="inLine" select="true()"/>
          <xsl:with-param name="typecast" select="jw:upperWord(@objectType)"/>
          <xsl:with-param name="objInstance">
            <!-- be shure that AbstractSimulator and environmentrule have the method getEnvironmentSimulator -->
            <xsl:call-template name="java:callGetterMethod">
              <xsl:with-param name="objInstance">
                <xsl:choose>
                  <!-- for environmentrules -->
                  <xsl:when test="local-name(..) eq 'EnvironmentRule'">
                    <xsl:value-of select="'this'"/>
                  </xsl:when>
                  <!-- for initialisationrules -->
                  <xsl:otherwise>
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="name" select="$sim.class.simulatorMain"/>
                      <xsl:with-param name="varName" select="'this'"/>
                    </xsl:call-template>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
              <xsl:with-param name="instVariable" select="$core.class.environmentSimulator"/>
              <xsl:with-param name="inLine" select="true()"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="method">
            <xsl:choose>
              <xsl:when test="fn:exists(//aorsml:PhysicalAgentType[@name = current()/@objectType])">
                <xsl:value-of select="'getPhysAgentById'"/>
              </xsl:when>
              <xsl:when test="fn:exists(//aorsml:PhysicalObjectType[@name = current()/@objectType])">
                <xsl:value-of select="'getPhysicalObjectById'"/>
              </xsl:when>
              <xsl:when test="fn:exists(//aorsml:ObjectType[@name = current()/@objectType])">
                <xsl:value-of select="'getObjectById'"/>
              </xsl:when>
              <xsl:when test="fn:exists(//aorsml:AgentType[@name = current()/@objectType])">
                <xsl:value-of select="'getAgentById'"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:message>
                  <xsl:text>[ERROR] No associated ObjectType found in </xsl:text>
                  <xsl:value-of select="fn:concat(local-name(.), ' objectType: [', @objectType, '] in ',local-name(..), ': [', ../@name, ']!')"/>
                </xsl:message>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
          <xsl:with-param name="args" as="xs:string*">
            <xsl:value-of select="fn:concat(@objectVariable, $iteratorPostfix)"/>
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="name" select="jw:upperWord(@objectType)"/>
              <xsl:with-param name="varName" select="'class'"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- create the neccessary method-calls -->
  <xsl:template match="aorsml:EnvironmentRule" mode="createRules.helper.method.execute.setProcessMethods">
    <xsl:param name="indent" required="yes"/>

    <!-- select all entities they can be null in the javasimulator (used envSim.getPhysAgentById()/ getObjectsById())-->
    <xsl:variable name="entityList"
      select="aorsml:FOR[@objectVariable]
      [fn:exists(@rangeStartID) or fn:exists(@rangeEndID) or fn:exists(@objectIdRef) or fn:exists(aorsml:ObjectRef) or fn:exists(aorsml:ObjectIdRef)]"/>

    <xsl:call-template name="java:if">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="condition">
        <xsl:choose>
          <xsl:when test="fn:count($entityList) > 0">
            <xsl:for-each select="$entityList">
              <xsl:call-template name="java:boolExpr">
                <xsl:with-param name="value1">
                  <xsl:call-template name="java:varByDotNotation">
                    <xsl:with-param name="name" select="'this'"/>
                    <xsl:with-param name="varName" select="@objectVariable"/>
                  </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="value2" select="'null'"/>
                <xsl:with-param name="operator" select="'!='"/>
              </xsl:call-template>
              <xsl:if test="position() &lt; last()">
                <xsl:value-of select="' &amp;&amp; '"/>
              </xsl:if>
            </xsl:for-each>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'true'"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="thenContent">
        <xsl:call-template name="java:newLine"/>

        <!-- doStatechanges -->
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="objInstance" select="'this'"/>
          <xsl:with-param name="method" select="'doStateEffects'"/>
        </xsl:call-template>

        <!-- doResulting events -->
        <!-- TODO: delete the second part of expression if the deprecated compatibilty is deleted -->
        <xsl:if test="fn:exists(aorsml:DO/aorsml:SCHEDULE-EVT) or fn:exists(aorsml:SCHEDULE-EVT)">
          <xsl:call-template name="java:callMethod">
            <xsl:with-param name="indent" select="$indent + 1"/>
            <xsl:with-param name="objInstance" select="'result'"/>
            <xsl:with-param name="method" select="'addAll'"/>
            <xsl:with-param name="args">
              <xsl:call-template name="java:callMethod">
                <xsl:with-param name="inLine" select="true()"/>
                <xsl:with-param name="objInstance" select="'this'"/>
                <xsl:with-param name="method" select="'doResultingEvents'"/>
              </xsl:call-template>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:if>

        <!-- doDestroy -->
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="objInstance" select="'this'"/>
          <xsl:with-param name="method" select="'doDestroyObjekt'"/>
        </xsl:call-template>
        <xsl:call-template name="java:newLine"/>

        <!-- is used for all exlude the IF-ELSE-part -->
        <!--
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="method" select="'stateEffects'"/>
        </xsl:call-template>


        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="objInstance" select="'result'"/>
          <xsl:with-param name="method" select="'addAll'"/>
          <xsl:with-param name="args">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="method" select="'resultingEvents'"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>


        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="method" select="'destroyObjekt'"/>
        </xsl:call-template>
        <xsl:call-template name="java:newLine"/> -->

        <xsl:call-template name="java:if">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="condition">
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="method" select="'condition'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="thenContent">
            <xsl:call-template name="java:newLine"/>

            <!-- call additional methods -->

            <!-- statechanges -->
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="indent" select="$indent + 2"/>
              <xsl:with-param name="objInstance" select="'this'"/>
              <xsl:with-param name="method" select="'thenStateEffects'"/>
            </xsl:call-template>

            <!-- statistics 
            <xsl:if test="fn:exists(aorsml:UpdateStatistics)">
              <xsl:call-template name="java:callMethod">
                <xsl:with-param name="indent" select="$indent + 2"/>
                <xsl:with-param name="objInstance" select="'this'"/>
                <xsl:with-param name="method" select="'updateStatistic'"/>
              </xsl:call-template>
              </xsl:if>-->

            <!-- resulting events -->
            <xsl:if test="fn:exists(aorsml:THEN/aorsml:SCHEDULE-EVT)">
              <xsl:call-template name="java:callMethod">
                <xsl:with-param name="indent" select="$indent + 2"/>
                <xsl:with-param name="objInstance" select="'result'"/>
                <xsl:with-param name="method" select="'addAll'"/>
                <xsl:with-param name="args">
                  <xsl:call-template name="java:callMethod">
                    <xsl:with-param name="inLine" select="true()"/>
                    <xsl:with-param name="objInstance" select="'this'"/>
                    <xsl:with-param name="method" select="'thenResultingEvents'"/>
                  </xsl:call-template>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:if>


            <!-- destroy -->
            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="indent" select="$indent + 2"/>
              <xsl:with-param name="objInstance" select="'this'"/>
              <xsl:with-param name="method" select="'thenDestroyObjekt'"/>
            </xsl:call-template>
            <xsl:call-template name="java:newLine"/>

          </xsl:with-param>
          <xsl:with-param name="elseContent">
            <xsl:if test="fn:exists(aorsml:ELSE)">
              <xsl:call-template name="java:newLine"/>

              <!-- statechanges -->
              <xsl:call-template name="java:callMethod">
                <xsl:with-param name="indent" select="$indent + 2"/>
                <xsl:with-param name="objInstance" select="'this'"/>
                <xsl:with-param name="method" select="'elseStateEffects'"/>
              </xsl:call-template>

              <!-- resulting events -->
              <xsl:if test="fn:exists(aorsml:ELSE/aorsml:SCHEDULE-EVT)">
                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="indent" select="$indent + 2"/>
                  <xsl:with-param name="objInstance" select="'result'"/>
                  <xsl:with-param name="method" select="'addAll'"/>
                  <xsl:with-param name="args">
                    <xsl:call-template name="java:callMethod">
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="objInstance" select="'this'"/>
                      <xsl:with-param name="method" select="'elseResultingEvents'"/>
                    </xsl:call-template>
                  </xsl:with-param>
                </xsl:call-template>
              </xsl:if>

              <!-- destroy -->
              <xsl:call-template name="java:callMethod">
                <xsl:with-param name="indent" select="$indent + 2"/>
                <xsl:with-param name="objInstance" select="'this'"/>
                <xsl:with-param name="method" select="'elseDestroyObjekt'"/>
              </xsl:call-template>
              <xsl:call-template name="java:newLine"/>

            </xsl:if>


          </xsl:with-param>

        </xsl:call-template>

      </xsl:with-param>

    </xsl:call-template>
  </xsl:template>

  <!-- create the neccessary method-calls -->
  <xsl:template match="aorsml:InitializationRule" mode="createRules.helper.method.execute.setProcessMethods">
    <xsl:param name="indent" required="yes"/>

    <!-- select all entities they can be null in the javasimulator (used envSim.getPhysAgentById()/ getObjectsById())-->
    <xsl:variable name="entityList"
      select="aorsml:FOR[@objectVariable][fn:exists(@rangeStartID) or fn:exists(@rangeEndID) or fn:exists(@objectIdRef) or fn:exists(aorsml:ObjectRef)]"/>

    <xsl:call-template name="java:if">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="condition">
        <xsl:for-each select="$entityList">
          <xsl:call-template name="java:boolExpr">
            <xsl:with-param name="value1">
              <xsl:call-template name="java:varByDotNotation">
                <xsl:with-param name="name" select="'this'"/>
                <xsl:with-param name="varName" select="@objectVariable"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="value2" select="'null'"/>
            <xsl:with-param name="operator" select="'!='"/>
          </xsl:call-template>
          <xsl:value-of select="' &amp;&amp; '"/>
        </xsl:for-each>
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="inLine" select="true()"/>
          <xsl:with-param name="objInstance" select="'this'"/>
          <xsl:with-param name="method" select="'condition'"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="thenContent">
        <xsl:call-template name="java:newLine"/>

        <!-- call additional methods -->
        <!-- statechanges -->
        <xsl:call-template name="java:callMethod">
          <xsl:with-param name="indent" select="$indent+1"/>
          <xsl:with-param name="objInstance" select="'this'"/>
          <xsl:with-param name="method" select="'stateEffects'"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- ++++++++++++++++++++++ -->
  <!-- List implementations -->
  <!-- ++++++++++++++++++++++ -->

  <!-- init a list with ArrayList-Implementation - used in constructors of objekts -->
  <xsl:template match="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty" mode="assistent.initList">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="name">
        <xsl:call-template name="java:varByDotNotation">
          <xsl:with-param name="varName" select="@name"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="value">
        <xsl:call-template name="java:newObject">
          <xsl:with-param name="inLine" select="true()"/>
          <xsl:with-param name="class" select="'ArrayList'"/>
          <xsl:with-param name="generic" select="jw:upperWord(jw:mappeDataType(@type))"/>
          <xsl:with-param name="isVariable" select="true()"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <xsl:template match="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty" mode="assistents.listMethods">
    <xsl:param name="indent" required="yes" as="xs:integer"/>

    <!-- <xsl:variable name="methodPrefix" select="fn:concat(jw:upperWord(@name), 'Element')"/> -->
    <xsl:variable name="methodPrefix" select="jw:upperWord(@name)"/>

    <!-- get(int index) -> it is without stateChange -->
    <xsl:apply-templates select="." mode="assistents.listMethods.get">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="methodPrefix" select="$methodPrefix"/>
    </xsl:apply-templates>

    <!-- remove(int index) -->
    <xsl:apply-templates select="." mode="assistents.listMethods.removeByIndex">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="methodPrefix" select="$methodPrefix"/>
    </xsl:apply-templates>

    <!-- remove(Object o) -->
    <xsl:apply-templates select="." mode="assistents.listMethods.removeByObj">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="methodPrefix" select="$methodPrefix"/>
    </xsl:apply-templates>

    <!-- add(Object o) -->
    <xsl:apply-templates select="." mode="assistents.listMethods.add">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="methodPrefix" select="$methodPrefix"/>
    </xsl:apply-templates>

    <!-- get() -->
    <xsl:apply-templates select="." mode="assistents.listMethods.getList">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="methodPrefix" select="if (fn:ends-with($methodPrefix,'s')) then $methodPrefix else fn:concat($methodPrefix, 's')"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- get#prefix#(int index) -->
  <xsl:template match="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty" mode="assistents.listMethods.get">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="methodPrefix" required="yes" as="xs:string"/>

    <xsl:apply-templates select="." mode="assistents.listMethods.getOrRemoveByIndex">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="methodPrefix" select="$methodPrefix"/>
      <xsl:with-param name="method" select="'get'"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- remove#prefix#(int index) -->
  <xsl:template match="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty" mode="assistents.listMethods.removeByIndex">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="methodPrefix" required="yes" as="xs:string"/>

    <xsl:apply-templates select="." mode="assistents.listMethods.getOrRemoveByIndex">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="methodPrefix" select="$methodPrefix"/>
      <xsl:with-param name="method" select="'remove'"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- get(int index) and remove(int index) have a simular structure -->
  <xsl:template match="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty" mode="assistents.listMethods.getOrRemoveByIndex">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="methodPrefix" required="yes" as="xs:string"/>
    <xsl:param name="method" required="yes" as="xs:string"/>

    <xsl:variable name="indexVarName" select="'index'"/>
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="jw:upperWord(@type)"/>
      <xsl:with-param name="name" select="fn:concat($method, $methodPrefix)"/>
      <xsl:with-param name="parameterList" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="'int'"/>
          <xsl:with-param name="name" select="$indexVarName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:variable name="returnObjVarName" select="'result'"/>
        <xsl:call-template name="java:newObject">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="class" select="jw:upperWord(jw:mappeDataType(@type))"/>
          <xsl:with-param name="varName" select="$returnObjVarName"/>
          <xsl:with-param name="initWithNull" select="true()"/>
        </xsl:call-template>

        <!-- check if the index in range (save for OutOfBoundsException) -->
        <xsl:call-template name="java:if">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="condition">
            <xsl:call-template name="java:boolExpr">
              <xsl:with-param name="value1">
                <xsl:call-template name="java:boolExpr">
                  <xsl:with-param name="value1" select="$indexVarName"/>
                  <xsl:with-param name="value2" select="'0'"/>
                  <xsl:with-param name="operator" select="'&gt;'"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="value2">
                <xsl:call-template name="java:boolExpr">
                  <xsl:with-param name="value1" select="$indexVarName"/>
                  <xsl:with-param name="value2">
                    <xsl:call-template name="java:callMethod">
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="objInstance">
                        <xsl:call-template name="java:varByDotNotation">
                          <xsl:with-param name="varName" select="@name"/>
                        </xsl:call-template>
                      </xsl:with-param>
                      <xsl:with-param name="method" select="'size'"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="operator" select="'&lt;'"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="operator" select="'&amp;&amp;'"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="thenContent">

            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent + 2"/>
              <xsl:with-param name="name" select="$returnObjVarName"/>
              <xsl:with-param name="value">
                <xsl:call-template name="java:callMethod">
                  <xsl:with-param name="inLine" select="true()"/>
                  <xsl:with-param name="objInstance">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="@name"/>
                    </xsl:call-template>
                  </xsl:with-param>
                  <xsl:with-param name="method" select="$method"/>
                  <xsl:with-param name="args" select="$indexVarName"/>
                </xsl:call-template>
              </xsl:with-param>
            </xsl:call-template>

          </xsl:with-param>
        </xsl:call-template>

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value" select="$returnObjVarName"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>


  <!-- remove#prefix#(Object o) -->
  <xsl:template match="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty" mode="assistents.listMethods.removeByObj">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="methodPrefix" required="yes" as="xs:string"/>

    <xsl:apply-templates select="." mode="assistents.listMethods.addOrRemoveByObj">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="methodPrefix" select="$methodPrefix"/>
      <xsl:with-param name="method" select="'remove'"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- add#prefix#(Object o) -->
  <xsl:template match="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty" mode="assistents.listMethods.add">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="methodPrefix" required="yes" as="xs:string"/>

    <xsl:apply-templates select="." mode="assistents.listMethods.addOrRemoveByObj">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="methodPrefix" select="$methodPrefix"/>
      <xsl:with-param name="method" select="'add'"/>
    </xsl:apply-templates>

  </xsl:template>

  <!-- add(Object o) and remove(Object o) have a simular structure -->
  <xsl:template match="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty" mode="assistents.listMethods.addOrRemoveByObj">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="methodPrefix" required="yes" as="xs:string"/>
    <xsl:param name="method" required="yes" as="xs:string"/>

    <xsl:variable name="objVarName" select="fn:concat(@name, 'Obj')"/>
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'boolean'"/>
      <xsl:with-param name="name" select="fn:concat($method, $methodPrefix)"/>
      <xsl:with-param name="parameterList" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="jw:upperWord(@type)"/>
          <xsl:with-param name="name" select="$objVarName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">

            <xsl:call-template name="java:callMethod">
              <xsl:with-param name="inLine" select="true()"/>
              <xsl:with-param name="objInstance">
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="varName" select="@name"/>
                </xsl:call-template>
              </xsl:with-param>
              <xsl:with-param name="method" select="$method"/>
              <xsl:with-param name="args" select="$objVarName"/>
            </xsl:call-template>

          </xsl:with-param>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <!-- get#prefix#[s]() -->
  <xsl:template match="aorsml:Attribute | aorsml:ReferenceProperty | aorsml:ComplexDataProperty" mode="assistents.listMethods.getList">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="methodPrefix" required="yes" as="xs:string"/>

    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="'ArrayList'"/>
      <xsl:with-param name="genericType" select="jw:upperWord(jw:mappeDataType(@type))"/>
      <xsl:with-param name="name" select="fn:concat('get', $methodPrefix)"/>
      <xsl:with-param name="content">

        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">
            <xsl:call-template name="java:varByDotNotation">
              <xsl:with-param name="varName" select="@name"/>
            </xsl:call-template>
          </xsl:with-param>
          <xsl:with-param name="returnType" select="fn:concat('ArrayList&lt;', jw:upperWord(jw:mappeDataType(@type)), '&gt;')"/>
        </xsl:call-template>

      </xsl:with-param>
    </xsl:call-template>

  </xsl:template>

  <!-- create the output for resulting events / messages; surrounded by an java-if. if necessary -->
  <!-- TODO: change all to use this -->
  <xsl:template
    match="aorsml:OutMessageEventExpr | aorsml:ReminderEventExpr | aorsml:ActionEventExpr |
    aorsml:CausedEventExpr | aorsml:PerceptionEventExpr | aorsml:InMessageEventExpr | 
    aorsml:ActivityStartEventExpr | aorsml:ActivityEndEventExpr"
    mode="assistent.resultingEvent.output">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="output" as="xs:string"/>

    <xsl:choose>
      <xsl:when test="fn:exists(aorsml:Condition[@language eq $output.language])">

        <xsl:call-template name="java:if">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="condition" select="aorsml:Condition[@language eq $output.language][1]"/>

          <xsl:with-param name="thenContent">
            <xsl:value-of select="$output"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$output"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- ++++++++++++++++++++++++++++++++++++++++++++++++++++ -->
  <!--   distributions for Delay, Duration and Periodicity  -->
  <!-- ++++++++++++++++++++++++++++++++++++++++++++++++++++ -->
  <!-- Uniform -->
  <xsl:template match="aorsml:Uniform | aorsml:UniformInt" mode="assistents.distribution">

    <!-- lowerBound; default is 0 -->
    <xsl:variable name="lowerBound" as="xs:string">
      <xsl:choose>
        <xsl:when test="@lowerBound">
          <xsl:value-of select="@lowerBound"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:LowerBoundExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:LowerBoundExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <!-- set the default value 0 -->
          <xsl:value-of select="'0'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- upperBound; default is 1 -->
    <xsl:variable name="upperBound" as="xs:string">
      <xsl:choose>
        <xsl:when test="@upperBound">
          <xsl:value-of select="@upperBound"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:UpperBoundExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:UpperBoundExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <!-- set the default value 1 -->
          <xsl:value-of select="'1'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- uniformInt(a, b) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="inLine" select="true()"/>
      <xsl:with-param name="objInstance" select="'Random'"/>
      <xsl:with-param name="method">
        <xsl:choose>
          <xsl:when test="ends-with(local-name(.), 'Int')">
            <xsl:value-of select="'uniformInt'"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'uniform'"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="args" as="xs:string*" select="($lowerBound, $upperBound)"/>
    </xsl:call-template>

  </xsl:template>

  <!-- Normal -->
  <xsl:template match="aorsml:Normal | aorsml:NormalInt" mode="assistents.distribution">

    <!-- expectedValue the default is 0 -->
    <xsl:variable name="expectedValue" as="xs:string">
      <xsl:choose>
        <xsl:when test="@mean">
          <xsl:value-of select="@mean"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:MeanExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:MeanExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'0'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- standardDeviation the default is 1 -->
    <xsl:variable name="standardDeviation" as="xs:string">
      <xsl:choose>
        <xsl:when test="@standardDeviation">
          <xsl:value-of select="@standardDeviation"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:StandardDeviationExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:StandardDeviationExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'1'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- normalInt(mean, stDev) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="inLine" select="true()"/>
      <xsl:with-param name="objInstance" select="'Random'"/>
      <xsl:with-param name="method">
        <xsl:choose>
          <xsl:when test="ends-with(local-name(.), 'Int')">
            <xsl:value-of select="'normalInt'"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'normal'"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="args" as="xs:string*" select="($expectedValue, $standardDeviation)"/>
    </xsl:call-template>

  </xsl:template>

  <!-- Binomial -->
  <xsl:template match="aorsml:Binomial" mode="assistents.distribution">

    <!-- n; default is 25 -->
    <xsl:variable name="n" as="xs:string">
      <xsl:choose>
        <xsl:when test="@n">
          <xsl:value-of select="@n"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:N-Expr[@language eq $output.language])">
          <xsl:value-of select="aorsml:N-Expr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <!-- set the default value 25 -->
          <xsl:value-of select="'25'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- p; default is .5 -->
    <xsl:variable name="p" as="xs:string">
      <xsl:choose>
        <xsl:when test="@p">
          <xsl:value-of select="@p"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:P-Expr[@language eq $output.language])">
          <xsl:value-of select="aorsml:P-Expr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <!-- set the default value 0.5 -->
          <xsl:value-of select="'0.5'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- binomial(n, p) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="inLine" select="true()"/>
      <xsl:with-param name="objInstance" select="'Random'"/>
      <xsl:with-param name="method" select="'binomial'"/>
      <xsl:with-param name="args" as="xs:string*" select="($n, $p)"/>
    </xsl:call-template>

  </xsl:template>

  <!-- NegBinomial -->
  <xsl:template match="aorsml:NegBinomial" mode="assistents.distribution">

    <!-- r; default is 25 -->
    <xsl:variable name="r" as="xs:string">
      <xsl:choose>
        <xsl:when test="@r">
          <xsl:value-of select="@r"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:R-Expr[@language eq $output.language])">
          <xsl:value-of select="aorsml:R-Expr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <!-- set the default value ??? -->
          <xsl:value-of select="'25'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- p; default is .5 -->
    <xsl:variable name="p" as="xs:string">
      <xsl:choose>
        <xsl:when test="@p">
          <xsl:value-of select="@p"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:P-Expr[@language eq $output.language])">
          <xsl:value-of select="aorsml:P-Expr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <!-- set the default value ..5 -->
          <xsl:value-of select="'0.5'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- negBinomial(n, p) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="inLine" select="true()"/>
      <xsl:with-param name="objInstance" select="'Random'"/>
      <xsl:with-param name="method" select="'negBinomial'"/>
      <xsl:with-param name="args" as="xs:string*" select="($r, $p)"/>
    </xsl:call-template>

  </xsl:template>

  <!-- Poisson -->
  <xsl:template match="aorsml:Poisson" mode="assistents.distribution">

    <!-- lambda; default is 1.0 -->
    <xsl:variable name="lambda" as="xs:string">
      <xsl:choose>
        <xsl:when test="@lambda">
          <xsl:value-of select="@lambda"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:LambdaExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:LambdaExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <!-- set the default value 1.0 -->
          <xsl:value-of select="'1.0'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- poisson(lambda)) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="inLine" select="true()"/>
      <xsl:with-param name="objInstance" select="'Random'"/>
      <xsl:with-param name="method" select="'poisson'"/>
      <xsl:with-param name="args" as="xs:string*" select="($lambda)"/>
    </xsl:call-template>

  </xsl:template>

  <!-- Exponential -->
  <xsl:template match="aorsml:Exponential | aorsml:ExponentialInt" mode="assistents.distribution">

    <!-- lambda; default is 1.0 -->
    <xsl:variable name="lambda" as="xs:string">
      <xsl:choose>
        <xsl:when test="@lambda">
          <xsl:value-of select="@lambda"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:LambdaExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:LambdaExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <!-- set the default value 1.0 -->
          <xsl:value-of select="'1.0'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- exponentialInt(lambda)) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="inLine" select="true()"/>
      <xsl:with-param name="objInstance" select="'Random'"/>
      <xsl:with-param name="method">
        <xsl:choose>
          <xsl:when test="ends-with(local-name(.), 'Int')">
            <xsl:value-of select="'exponentialInt'"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'exponential'"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="args" as="xs:string*" select="($lambda)"/>
    </xsl:call-template>

  </xsl:template>

  <!-- Student_T -->
  <xsl:template match="aorsml:Student_T" mode="assistents.distribution">

    <!-- degreesOfFreedom; default is 100 -->
    <xsl:variable name="degreesOfFreedom" as="xs:string">
      <xsl:choose>
        <xsl:when test="@degreesOfFreedom">
          <xsl:value-of select="@degreesOfFreedom"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:DegreesOfFreedomExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:DegreesOfFreedomExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <!-- set the default value ??? -->
          <xsl:value-of select="'100'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- studentTInt(degreesOfFreedom)) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="inLine" select="true()"/>
      <xsl:with-param name="objInstance" select="'Random'"/>
      <xsl:with-param name="method" select="'studentTInt'"/>
      <xsl:with-param name="args" as="xs:string*" select="($degreesOfFreedom)"/>
    </xsl:call-template>

  </xsl:template>

  <!-- Logarithmic -->
  <xsl:template match="aorsml:Logarithmic" mode="assistents.distribution">

    <!-- p; default is .5 -->
    <xsl:variable name="p" as="xs:string">
      <xsl:choose>
        <xsl:when test="@p">
          <xsl:value-of select="@p"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:P-Expr[@language eq $output.language])">
          <xsl:value-of select="aorsml:P-Expr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <!-- set the default value ??? -->
          <xsl:value-of select="'0.5'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- logarithmicInt(p)) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="inLine" select="true()"/>
      <xsl:with-param name="objInstance" select="'Random'"/>
      <xsl:with-param name="method" select="'logarithmic'"/>
      <xsl:with-param name="args" as="xs:string*" select="($p)"/>
    </xsl:call-template>

  </xsl:template>

  <!-- ChiSquare -->
  <xsl:template match="aorsml:ChiSquare" mode="assistents.distribution">

    <!-- degreesOfFreedom; default is 5 -->
    <xsl:variable name="degreesOfFreedom" as="xs:string">
      <xsl:choose>
        <xsl:when test="@degreesOfFreedom">
          <xsl:value-of select="@degreesOfFreedom"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:DegreesOfFreedomExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:DegreesOfFreedomExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <!-- set the default value ??? -->
          <xsl:value-of select="'5'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- chisquareInt(degreesOfFreedom)) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="inLine" select="true()"/>
      <xsl:with-param name="objInstance" select="'Random'"/>
      <xsl:with-param name="method" select="'chisquareInt'"/>
      <xsl:with-param name="args" as="xs:string*" select="($degreesOfFreedom)"/>
    </xsl:call-template>

  </xsl:template>


  <!-- LogNormal -->
  <xsl:template match="aorsml:LogNormal | aorsml:LogNormalInt" mode="assistents.distribution">

    <!-- mean the default is 0 -->
    <xsl:variable name="mean" as="xs:string">
      <xsl:choose>
        <xsl:when test="@mean">
          <xsl:value-of select="@mean"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:MeanExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:MeanExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'0'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- standardDeviation the default is 1 -->
    <xsl:variable name="standardDeviation" as="xs:string">
      <xsl:choose>
        <xsl:when test="@standardDeviation">
          <xsl:value-of select="@standardDeviation"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:StandardDeviationExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:StandardDeviationExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'1'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- logNormalInt(mean, stDev) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="inLine" select="true()"/>
      <xsl:with-param name="objInstance" select="'Random'"/>
      <xsl:with-param name="method">
        <xsl:choose>
          <xsl:when test="ends-with(local-name(.), 'Int')">
            <xsl:value-of select="'logNormalInt'"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'logNormal'"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="args" as="xs:string*" select="($mean, $standardDeviation)"/>
    </xsl:call-template>

  </xsl:template>

  <!-- HyperGeometric -->
  <xsl:template match="aorsml:HyperGeometric" mode="assistents.distribution">

    <!-- totalPopulationSize the default is 10 -->
    <xsl:variable name="totalPopulationSize" as="xs:string">
      <xsl:choose>
        <xsl:when test="@totalPopulationSize">
          <xsl:value-of select="@totalPopulationSize"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:TotalPopulationSizeExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:TotalPopulationSizeExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'10'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- successPopulationSize the default is 1 -->
    <xsl:variable name="successPopulationSize" as="xs:string">
      <xsl:choose>
        <xsl:when test="@successPopulationSize">
          <xsl:value-of select="@successPopulationSize"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:SuccessesPopulationSizeExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:SuccessesPopulationSizeExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'1'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- numberOfDraws the default is 10 -->
    <xsl:variable name="numberOfDraws" as="xs:string">
      <xsl:choose>
        <xsl:when test="@numberOfDraws">
          <xsl:value-of select="@numberOfDraws"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:NumberOfDrawsExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:NumberOfDrawsExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'10'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- hyperGeometric(totalPopulationSize, successPopulationSize, numberOfDraws) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="inLine" select="true()"/>
      <xsl:with-param name="objInstance" select="'Random'"/>
      <xsl:with-param name="method" select="'hyperGeometric'"/>
      <xsl:with-param name="args" as="xs:string*" select="($totalPopulationSize, $successPopulationSize, $numberOfDraws)"/>
    </xsl:call-template>

  </xsl:template>

  <!-- Gamma -->
  <xsl:template match="aorsml:Gamma | aorsml:GammaInt" mode="assistents.distribution">

    <!-- shape the default is 1 -->
    <xsl:variable name="shape" as="xs:string">
      <xsl:choose>
        <xsl:when test="@shape">
          <xsl:value-of select="@shape"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:ShapeExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:ShapeExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'1'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- rate the default is 1 -->
    <xsl:variable name="rate" as="xs:string">
      <xsl:choose>
        <xsl:when test="@rate">
          <xsl:value-of select="@rate"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:RateExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:RateExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'1'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- gammaInt(shape, rate) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="inLine" select="true()"/>
      <xsl:with-param name="objInstance" select="'Random'"/>
      <xsl:with-param name="method">
        <xsl:choose>
          <xsl:when test="ends-with(local-name(.), 'Int')">
            <xsl:value-of select="'gammaInt'"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'gamma'"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="args" as="xs:string*" select="($shape, $rate)"/>
    </xsl:call-template>

  </xsl:template>

  <!-- Erlang -->
  <xsl:template match="aorsml:Erlang | aorsml:ErlangInt" mode="assistents.distribution">

    <!-- shape the default is 1 -->
    <xsl:variable name="shape" as="xs:string">
      <xsl:choose>
        <xsl:when test="@shape">
          <xsl:value-of select="@shape"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:ShapeExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:ShapeExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'1'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- rate the default is 1 -->
    <xsl:variable name="rate" as="xs:string">
      <xsl:choose>
        <xsl:when test="@rate">
          <xsl:value-of select="@rate"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:ShapeExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:ShapeExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'1'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- erlangInt(shape, rate) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="inLine" select="true()"/>
      <xsl:with-param name="objInstance" select="'Random'"/>
      <xsl:with-param name="method">
        <xsl:choose>
          <xsl:when test="ends-with(local-name(.), 'Int')">
            <xsl:value-of select="'erlangInt'"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'erlang'"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="args" as="xs:string*" select="($shape, $rate)"/>
    </xsl:call-template>

  </xsl:template>

  <!-- Weibull -->
  <xsl:template match="aorsml:Weibull | aorsml:WeibullInt" mode="assistents.distribution">

    <!-- shape the default is 1 -->
    <xsl:variable name="shape" as="xs:string">
      <xsl:choose>
        <xsl:when test="@shape">
          <xsl:value-of select="@shape"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:ShapeExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:ShapeExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'1'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- scale the default is 1 -->
    <xsl:variable name="scale" as="xs:string">
      <xsl:choose>
        <xsl:when test="@scale">
          <xsl:value-of select="@scale"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:ScaleExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:ScaleExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'1'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- weibullInt(shape, rate) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="inLine" select="true()"/>
      <xsl:with-param name="objInstance" select="'Random'"/>
      <xsl:with-param name="method">
        <xsl:choose>
          <xsl:when test="ends-with(local-name(.), 'Int')">
            <xsl:value-of select="'weibullInt'"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'weibull'"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="args" as="xs:string*" select="($shape, $scale)"/>
    </xsl:call-template>

  </xsl:template>

  <!-- Triangular -->
  <xsl:template match="aorsml:Triangular | aorsml:TriangularInt" mode="assistents.distribution">

    <!-- lowerBound the default is 0 -->
    <xsl:variable name="lowerBound" as="xs:string">
      <xsl:choose>
        <xsl:when test="@lowerBound">
          <xsl:value-of select="@lowerBound"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:LowerBoundExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:LowerBoundExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'0'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- upperBound the default is 1 -->
    <xsl:variable name="upperBound" as="xs:string">
      <xsl:choose>
        <xsl:when test="@upperBound">
          <xsl:value-of select="@upperBound"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:UpperBoundExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:UpperBoundExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'1'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- mode the default is 0 -->
    <xsl:variable name="mode" as="xs:string">
      <xsl:choose>
        <xsl:when test="@mode">
          <xsl:value-of select="@mode"/>
        </xsl:when>
        <xsl:when test="exists(aorsml:ModeExpr[@language eq $output.language])">
          <xsl:value-of select="aorsml:ModeExpr[@language eq $output.language][1]"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="'0'"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <!-- triangularInt(lowerBound, upperBound, mode) -->
    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="inLine" select="true()"/>
      <xsl:with-param name="objInstance" select="'Random'"/>
      <xsl:with-param name="method">
        <xsl:choose>
          <xsl:when test="ends-with(local-name(.), 'Int')">
            <xsl:value-of select="'triangularInt'"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'triangular'"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="args" as="xs:string*" select="($lowerBound, $upperBound, $mode)"/>
    </xsl:call-template>

  </xsl:template>

  <!-- Periodicity (ExogenousEvent and PeriodicTimeEvent)
  <xsl:template match="aorsml:Periodicity" mode="assistents.periodicity">

    <xsl:choose>
      <xsl:when test="fn:exists(aorsml:ValueExpr[@language eq $output.language])">
        <xsl:value-of select="aorsml:ValueExpr[@language eq $output.language][1]"/>
      </xsl:when>
      <xsl:when test="fn:exists(aorsml:RandomVariable)">
        <xsl:apply-templates select="aorsml:RandomVariable/aorsml:*" mode="assistents.distribution"/>
      </xsl:when>

    </xsl:choose>
    </xsl:template> -->

  <!-- Periodicity (ExogenousEvent and PeriodicTimeEvent) -->
  <xsl:template match="aorsml:ExogenousEventType | aorsml:PeriodicTimeEventType" mode="assistents.periodicity">

    <xsl:choose>
      <xsl:when test="@periodicity">
        <xsl:value-of select="@periodicity"/>
      </xsl:when>
      <xsl:when test="fn:exists(aorsml:Periodicity)">
        <xsl:choose>
          <xsl:when test="fn:exists(aorsml:Periodicity/aorsml:ValueExpr[@language eq $output.language])">
            <xsl:value-of select="aorsml:Periodicity/aorsml:ValueExpr[@language eq $output.language][1]"/>
          </xsl:when>
          <xsl:when test="fn:exists(aorsml:Periodicity/aorsml:DiscreteRandomVariable)">
            <xsl:apply-templates select="aorsml:Periodicity/aorsml:DiscreteRandomVariable/aorsml:*" mode="assistents.distribution"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'0'"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="'0'"/>
      </xsl:otherwise>
    </xsl:choose>


  </xsl:template>

</xsl:stylesheet>
