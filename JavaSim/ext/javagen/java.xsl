<?xml version="1.0" encoding="UTF-8"?>
<!--
    This transformation creates java code for the aorsml simulator v2
    
    $Rev$
    $Date$

    @author:   Jens Werner (jens.werner@tu-cottbus.de)
    @license:
    @last changed by $Author$
-->

<xsl:stylesheet version="2.0" xmlns:aorsml="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">

  <xsl:function name="jw:mappeDataType">
    <xsl:param name="xmlType" as="xs:string"/>
    <xsl:choose>
      <xsl:when test="$output.language = 'Java'">
        <xsl:choose>
          <xsl:when test="$xmlType = 'Float'">
            <xsl:text>double</xsl:text>
          </xsl:when>
          <xsl:when test="$xmlType = 'Boolean'">
            <xsl:text>boolean</xsl:text>
          </xsl:when>
          <xsl:when test="$xmlType = 'Integer'">
            <xsl:text>long</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="$xmlType"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
    </xsl:choose>
  </xsl:function>

  <!-- TODO: to complete -->
  <xsl:function name="jw:isSimpleJavaType" as="xs:boolean">
    <xsl:param name="javaType" as="xs:string"/>
    <xsl:choose>
      <xsl:when test="$javaType = 'double'">
        <xsl:value-of select="true()"/>
      </xsl:when>
      <xsl:when test="$javaType = 'boolean'">
        <xsl:value-of select="true()"/>
      </xsl:when>
      <!--<xsl:when test="$javaType = 'int'">
        <xsl:value-of select="true()"/>
      </xsl:when>-->
      <xsl:when test="$javaType = 'long'">
        <xsl:value-of select="true()"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="false()"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:function>

  <!--***************-->
  <!--basic functions-->
  <!--***************-->

  <!--new line-->
  <xsl:template name="java:newLine">
    <xsl:text>&#xA;</xsl:text>
  </xsl:template>

  <!--indent-->
  <xsl:template name="java:indent">
    <xsl:param name="size" required="yes" as="xs:integer"/>
    <xsl:if test="$size>0">
      <xsl:text>&#32;&#32;</xsl:text>
      <xsl:call-template name="java:indent">
        <xsl:with-param name="size" select="$size - 1"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <!--line of code-->
  <xsl:template name="java:codeLine">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="semicolon" as="xs:boolean" select="true()"/>
    <xsl:param name="newLine" select="true()"/>
    <xsl:param name="content" required="yes"/>
    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>
    <xsl:value-of select="$content"/>
    <xsl:if test="$semicolon">
      <xsl:text>;</xsl:text>
    </xsl:if>
    <xsl:if test="$newLine = true()">
      <xsl:call-template name="java:newLine"/>
    </xsl:if>
  </xsl:template>

  <!-- a code block -  a block of code surrounded by {} -->
  <xsl:template name="java:codeBlock">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="content" required="yes"/>

    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>
    <xsl:text>{</xsl:text>
    <xsl:call-template name="java:newLine"/>
    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>

    <xsl:value-of select="$content"/>

    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>
    <xsl:text>}</xsl:text>
    <xsl:call-template name="java:newLine"/>
  </xsl:template>

  <!--*************************************************************-->
  <!--templates for classes, variables, loops, conditions ...      -->
  <!--*************************************************************-->

  <!--class-->
  <xsl:template name="java:class">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="modifier"/>
    <xsl:param name="annotation" as="xs:string" select="''"/>
    <xsl:param name="name" required="yes"/>
    <xsl:param name="extends"/>
    <xsl:param name="implements"/>
    <xsl:param name="throws"/>
    <xsl:param name="content" required="yes"/>

    <xsl:call-template name="java:newLine"/>

    <!--class header-->
    <xsl:if test="$annotation">
      <xsl:value-of select="$annotation"/>
      <xsl:call-template name="java:newLine"/>
      <xsl:call-template name="java:indent">
        <xsl:with-param name="size" select="$indent"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>
    <xsl:if test="fn:string($modifier)">
      <xsl:value-of select="$modifier"/>
    </xsl:if>
    <xsl:value-of select="fn:concat(' class ', $name)"/>
    <xsl:if test="fn:string($extends)">
      <xsl:value-of select="fn:concat(' extends ', $extends)"/>
    </xsl:if>
    <xsl:if test="fn:string($implements)">
      <xsl:value-of select="fn:concat(' implements ', $implements)"/>
    </xsl:if>
    <xsl:if test="fn:string($throws)">
      <xsl:value-of select="fn:concat(' throws ', $throws)"/>
    </xsl:if>
    <xsl:text> {</xsl:text>
    <xsl:call-template name="java:newLine"/>
    <xsl:call-template name="java:newLine"/>

    <!--class body-->
    <xsl:value-of select="$content"/>

    <!--class footer-->
    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>
    <xsl:text>}</xsl:text>
    <xsl:call-template name="java:newLine"/>

  </xsl:template>

  <xsl:template name="java:enum">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="name" required="yes"/>
    <xsl:param name="enumerationLiteral" required="yes" as="xs:string*"/>

    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>
    <xsl:value-of select="fn:concat('public enum ', $name)"/>
    <xsl:text> {</xsl:text>
    <xsl:call-template name="java:newLine"/>
    <xsl:call-template name="java:newLine"/>

    <!--class body-->
    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent + 1"/>
    </xsl:call-template>
    <xsl:for-each select="$enumerationLiteral">
      <xsl:value-of select="."/>
      <xsl:if test="position() &lt; last()">
        <xsl:text>, </xsl:text>
      </xsl:if>
    </xsl:for-each>
    <xsl:call-template name="java:newLine"/>
    <xsl:call-template name="java:newLine"/>

    <!--class footer-->
    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>
    <xsl:text>}</xsl:text>
    <xsl:call-template name="java:newLine"/>
  </xsl:template>

  <!-- simple for-loop -->
  <xsl:template name="java:for-loop">
    <xsl:param name="indent" select="0"/>
    <xsl:param name="withThenBrackets" select="true()"/>
    <xsl:param name="loopVariable" required="yes"/>
    <xsl:param name="loopVarType" select="'int'"/>
    <xsl:param name="start" required="yes"/>
    <xsl:param name="condition" required="yes"/>
    <xsl:param name="increment" as="xs:integer" select="1"/>
    <xsl:param name="optionalContent" select="''"/>
    <xsl:param name="content" required="yes"/>

    <!--for header-->
    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>
    <xsl:value-of select="fn:concat('for (', $loopVarType, ' ', $loopVariable, ' = ', $start, '; ')"/>
    <xsl:value-of select="fn:concat($condition, '; ')"/>
    <xsl:if test="$optionalContent != ''">
      <xsl:value-of select="$optionalContent"/>
      <xsl:text>,</xsl:text>
    </xsl:if>
    <xsl:choose>
      <xsl:when test="$increment = 1">
        <xsl:value-of select="fn:concat($loopVariable, '++')"/>
      </xsl:when>
      <xsl:when test="$increment = -1">
        <xsl:value-of select="fn:concat($loopVariable, '--')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="fn:concat($loopVariable, ' = ', $loopVariable, fn:string($increment))"/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>)</xsl:text>
    <xsl:if test="$withThenBrackets = true()">
      <xsl:text> {</xsl:text>
    </xsl:if>
    <xsl:call-template name="java:newLine"/>

    <!--for body-->
    <xsl:value-of select="$content"/>

    <!--for footer-->
    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>
    <xsl:if test="$withThenBrackets = true()">
      <xsl:text>}</xsl:text>
      <xsl:call-template name="java:newLine"/>
    </xsl:if>
  </xsl:template>

  <!-- for(-each)-loop -->
  <xsl:template name="java:for-each-loop">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="elementType" required="yes" as="xs:string"/>
    <xsl:param name="elementVarName" required="yes" as="xs:string"/>
    <xsl:param name="listVarName" required="yes"/>
    <xsl:param name="withBrackets" required="no" as="xs:boolean" select="true()"/>
    <xsl:param name="content"/>

    <!-- head -->
    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>
    <xsl:value-of select="fn:concat('for (', $elementType, ' ', $elementVarName, ' : ', $listVarName, ')  ')"/>
    <xsl:if test="$withBrackets = true()">
      <xsl:text>{</xsl:text>
    </xsl:if>
    <xsl:call-template name="java:newLine"/>

    <!-- body -->
    <xsl:value-of select="$content"/>

    <!-- footer -->
    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>
    <xsl:if test="$withBrackets = true()">
      <xsl:text>}</xsl:text>
    </xsl:if>
    <xsl:call-template name="java:newLine"/>
  </xsl:template>

  <xsl:template name="java:while-loop">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="breakCondition" required="yes"/>
    <xsl:param name="content" required="yes"/>

    <!-- while-header -->
    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>
    <xsl:value-of select="fn:concat('while (', $breakCondition, ') {')"/>
    <xsl:call-template name="java:newLine"/>

    <!--while body-->
    <xsl:value-of select="$content"/>

    <!--while footer-->
    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>
    <xsl:text>}</xsl:text>
    <xsl:call-template name="java:newLine"/>
  </xsl:template>


  <!--if-->
  <xsl:template name="java:if">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="condition" required="yes"/>
    <xsl:param name="withThenBrackets" select="true()"/>
    <xsl:param name="thenContent" required="yes"/>
    <xsl:param name="elseContent"/>

    <!--if header-->
    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>
    <xsl:text>if(</xsl:text>
    <xsl:value-of select="$condition"/>
    <xsl:text>) </xsl:text>
    <xsl:if test="$withThenBrackets = true()">
      <xsl:text>{</xsl:text>
    </xsl:if>
    <xsl:call-template name="java:newLine"/>

    <!--if body - then-->
    <xsl:value-of select="$thenContent"/>
    <xsl:if test="$withThenBrackets = true()">
      <xsl:call-template name="java:indent">
        <xsl:with-param name="size" select="$indent"/>
      </xsl:call-template>
      <xsl:text>} </xsl:text>
    </xsl:if>


    <!--if body - else-->
    <xsl:if test="fn:string($elseContent)">
      <xsl:text> else {</xsl:text>
      <xsl:call-template name="java:newLine"/>
      <xsl:value-of select="$elseContent"/>
      <xsl:call-template name="java:indent">
        <xsl:with-param name="size" select="$indent"/>
      </xsl:call-template>
      <xsl:text>}</xsl:text>
      <xsl:call-template name="java:newLine"/>
    </xsl:if>
    <xsl:call-template name="java:newLine"/>
  </xsl:template>

  <!--import-->
  <xsl:template name="java:imports">
    <xsl:param name="importList" required="yes"/>
    <xsl:for-each select="$importList">
      <xsl:value-of select="fn:concat('import ', ., ';')"/>
      <xsl:call-template name="java:newLine"/>
    </xsl:for-each>
  </xsl:template>

  <!--method-->
  <xsl:template name="java:method">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="annotation" as="xs:string" select="''"/>
    <xsl:param name="modifier"/>
    <xsl:param name="static" select="false()" as="xs:boolean"/>
    <xsl:param name="type" select="'void'"/>
    <xsl:param name="genericType"/>
    <xsl:param name="name" required="yes"/>
    <xsl:param name="parameterList" as="xs:string*"/>
    <xsl:param name="throws"/>
    <xsl:param name="content" required="yes"/>

    <!--method header-->
    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>
    <xsl:if test="$annotation">
      <xsl:value-of select="$annotation"/>
      <xsl:call-template name="java:newLine"/>
      <xsl:call-template name="java:indent">
        <xsl:with-param name="size" select="$indent"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:if test="fn:string($modifier)">
      <xsl:value-of select="$modifier"/>
    </xsl:if>
    <xsl:value-of select="if ($static) then ' static' else ''"/>
    <xsl:text> </xsl:text>
    <xsl:value-of select="jw:mappeDataType($type)"/>
    <xsl:if test="$genericType">
      <xsl:value-of select="fn:concat('&lt;', $genericType, '&gt;')"/>
    </xsl:if>
    <xsl:if test="$type != ''">
      <xsl:text> </xsl:text>
    </xsl:if>
    <xsl:value-of select="$name"/>
    <xsl:text>(</xsl:text>
    <xsl:for-each select="$parameterList">
      <xsl:choose>
        <xsl:when test="position() != last()">
          <xsl:value-of select="fn:concat(., ', ')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
    <xsl:text>)</xsl:text>
    <xsl:if test="$throws">
      <xsl:text> throws </xsl:text>
      <xsl:value-of select="$throws"/>
    </xsl:if>
    <xsl:text> {</xsl:text>
    <xsl:call-template name="java:newLine"/>

    <!--method body-->
    <xsl:value-of select="$content"/>

    <!--method footer-->
    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>
    <xsl:text>}</xsl:text>
    <xsl:call-template name="java:newLine"/>
    <xsl:call-template name="java:newLine"/>
  </xsl:template>

  <xsl:template name="java:constructor">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="annotation" as="xs:string" select="''"/>
    <xsl:param name="modifier" select="'public'"/>
    <xsl:param name="name" required="yes"/>
    <xsl:param name="parameters" as="xs:string*"/>
    <xsl:param name="throws"/>
    <xsl:param name="content" required="yes"/>
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="annotation" select="$annotation"/>
      <xsl:with-param name="modifier" select="$modifier"/>
      <xsl:with-param name="type" select="''"/>
      <xsl:with-param name="name" select="$name"/>
      <xsl:with-param name="parameterList" select="$parameters" as="xs:string*"/>
      <xsl:with-param name="throws" select="$throws"/>
      <xsl:with-param name="content" select="$content"/>
    </xsl:call-template>
  </xsl:template>

  <!--package-->
  <xsl:template name="java:package">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="name" required="yes"/>
    <xsl:call-template name="java:codeLine">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="content" select="fn:concat('package ',$name)"/>
    </xsl:call-template>
  </xsl:template>

  <!--***************************************-->
  <!--templates for creating try-catch-java-code-->
  <!--***************************************-->
  <xsl:template name="java:tryCatch">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="tryContent"/>
    <xsl:param name="catchContent"/>
    <xsl:param name="exceptionType" required="yes"/>
    <xsl:param name="exceptionVariable" required="yes"/>

    <xsl:call-template name="java:codeLine">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="semicolon" select="false()"/>
      <xsl:with-param name="content" select="'try {'"/>
    </xsl:call-template>
    <xsl:value-of select="$tryContent"/>
    <xsl:call-template name="java:codeLine">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="semicolon" select="false()"/>
      <xsl:with-param name="content">
        <xsl:text>} catch (</xsl:text>
        <xsl:value-of select="fn:concat($exceptionType, ' ', $exceptionVariable,') {')"/>
      </xsl:with-param>
    </xsl:call-template>
    <xsl:value-of select="$catchContent"/>
    <xsl:call-template name="java:codeLine">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="semicolon" select="false()"/>
      <xsl:with-param name="content" select="'}'"/>
    </xsl:call-template>
  </xsl:template>

  <!--variable-->
  <xsl:template name="java:variable">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="static" select="false()" as="xs:boolean"/>
    <xsl:param name="final" select="false()" as="xs:boolean"/>
    <xsl:param name="modifier"/>
    <xsl:param name="type"/>
    <xsl:param name="castType"/>
    <xsl:param name="name" required="yes"/>
    <xsl:param name="value"/>

    <xsl:call-template name="java:codeLine">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="content">
        <xsl:if test="fn:string($modifier)">
          <xsl:value-of select="$modifier"/>
          <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:if test="$final">
          <xsl:value-of select="fn:concat('final', ' ')"/>
        </xsl:if>
        <xsl:if test="$static">
          <xsl:value-of select="fn:concat('static', ' ')"/>
        </xsl:if>
        <xsl:if test="fn:string($type)">
          <xsl:value-of select="jw:mappeDataType($type)"/>
          <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:value-of select="$name"/>
        <xsl:if test="fn:string($value)">
          <xsl:text> = </xsl:text>
          <xsl:if test="$castType != ''">
            <xsl:value-of select="fn:concat('(', $castType, ')')"/>
          </xsl:if>
          <xsl:choose>
            <xsl:when test="$type = 'String'">
              <xsl:value-of select="jw:quote($value)"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="$value"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:if>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="java:createAnnotation">
    <xsl:param name="annotationName" required="yes" as="xs:string"/>
    <xsl:param name="annotationValues" required="no" as="xs:string*" select="''"/>

    <xsl:variable name="annotation">
      <xsl:choose>
        <xsl:when test="$annotationName != ''">
          <xsl:value-of select="fn:concat('@', $annotationName)"/>
          <xsl:if test="count($annotationValues) &gt; 0">
            <xsl:value-of select="'(&quot;'"/>
            <xsl:for-each select="$annotationValues">
              <xsl:value-of select="."/>
              <xsl:if test="position() &lt; last()">
                <xsl:value-of select="','"/>
              </xsl:if>
            </xsl:for-each>
          </xsl:if>
          <xsl:value-of select="'&quot;)'"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="''"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:value-of select="$annotation"/>
  </xsl:template>

  <xsl:template name="java:createParam">
    <xsl:param name="type" required="yes" as="xs:string"/>
    <xsl:param name="name" required="yes" as="xs:string"/>
    <xsl:param name="typeMapping" select="true()" as="xs:boolean"/>
    <xsl:choose>
      <xsl:when test="$typeMapping">
        <xsl:value-of select="fn:concat(jw:mappeDataType($type), ' ', $name)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="fn:concat($type, ' ', $name)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- varByDotNotation -->
  <!-- notice: the default name is 'this' -->
  <xsl:template name="java:varByDotNotation">
    <xsl:param name="indent" select="0"/>
    <xsl:param name="name" as="xs:string" select="'this'"/>
    <xsl:param name="varName" as="xs:string" required="yes"/>
    <xsl:param name="isInline" select="true()" as="xs:boolean"/>
    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>
    <xsl:value-of select="fn:concat($name, '.', $varName)"/>
    <xsl:if test="not ($isInline)">
      <xsl:call-template name="java:newLine"/>
    </xsl:if>
  </xsl:template>

  <xsl:template name="java:newObject">
    <xsl:param name="indent" select="0"/>
    <xsl:param name="modifier" select="''"/>
    <xsl:param name="class" required="yes"/>
    <xsl:param name="generic" as="xs:string" select="''"/>
    <xsl:param name="varName"/>
    <xsl:param name="args" as="xs:string *"/>
    <!-- if true: new Object(); otherwise Object o = new Object() -->
    <xsl:param name="isVariable" select="false()" as="xs:boolean"/>
    <!-- if true: Object o = new Object(); otherwise Object o; -->
    <xsl:param name="withDeclaration" select="true()" as="xs:boolean"/>
    <xsl:param name="inLine" select="false()" as="xs:boolean"/>
    <!-- if true: new Object() in a sepearte line; otherwise Object o = newObject() -->
    <xsl:param name="onlyInitialization" select="false()" as="xs:boolean"/>
    <xsl:param name="initWithNull" select="false()" as="xs:boolean"/>

    <xsl:variable name="arguments">
      <xsl:for-each select="$args">
        <xsl:choose>
          <xsl:when test="position() != last()">
            <xsl:value-of select="fn:concat(., ', ')"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="."/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </xsl:variable>

    <xsl:variable name="generics" as="xs:string">
      <xsl:choose>
        <xsl:when test="$generic !=''">
          <xsl:value-of select="fn:concat('&lt;', $generic, '&gt;')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="''"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>

    <xsl:choose>
      <xsl:when test="$inLine = true()">
        <xsl:value-of select="fn:concat('new ', $class, $generics, '(', $arguments, ')')"/>
      </xsl:when>
      <xsl:when test="$onlyInitialization">
        <xsl:call-template name="java:codeLine">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="content">
            <xsl:value-of select="fn:concat('new ', $class, $generics, '(', $arguments, ')')"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="java:variable">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="$modifier"/>
          <xsl:with-param name="type" select="if ($isVariable) then '' else fn:concat($class, $generics)"/>
          <xsl:with-param name="name" select="$varName"/>
          <xsl:with-param name="value"
            select="if ($withDeclaration) then (if ($initWithNull) then 'null' else fn:concat('new ', $class, $generics,'(', $arguments, ')')) else ''"
          />
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="java:newArrayObject">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="class" required="yes"/>
    <xsl:param name="varName" required="yes"/>
    <xsl:param name="arrSize" required="yes"/>
    <xsl:param name="isVariable" select="false()" as="xs:boolean"/>
    <xsl:call-template name="java:variable">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="type" select="if ($isVariable) then '' else fn:concat($class, '[]')"/>
      <xsl:with-param name="name" select="$varName"/>
      <xsl:with-param name="value" select="fn:concat('new ', $class,'[', fn:string($arrSize), ']')"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="java:newHashMap">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="varName" required="yes" as="xs:string"/>
    <xsl:param name="withDeclaration" select="true()" as="xs:boolean"/>
    <xsl:param name="keyType" required="yes"/>
    <xsl:param name="valueType" required="yes"/>
    <xsl:param name="modifier" select="''"/>
    <xsl:call-template name="java:newObject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="class" select="fn:concat('HashMap', '&lt;', $keyType, ', ', $valueType, '&gt;')"/>
      <xsl:with-param name="varName" select="$varName"/>
      <xsl:with-param name="withDeclaration" select="$withDeclaration"/>
      <xsl:with-param name="modifier" select="$modifier"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="java:newArrayListObject">
    <xsl:param name="indent" select="0"/>
    <!-- if this is true then it is only without declaration (List<$generic> $name) -->
    <xsl:param name="useInterfaceAsTypeName" select="false()" as="xs:boolean"/>
    <xsl:param name="modifier" select="''"/>
    <xsl:param name="generic" as="xs:string"/>
    <xsl:param name="name"/>
    <xsl:param name="isVariable" select="false()" as="xs:boolean"/>
    <xsl:param name="inLine" select="false()" as="xs:boolean"/>
    <xsl:param name="withDeclaration" select="true()" as="xs:boolean"/>
    <xsl:call-template name="java:newObject">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="$modifier"/>
      <xsl:with-param name="class">
        <xsl:choose>
          <xsl:when test="$useInterfaceAsTypeName">
            <xsl:value-of select="'List'"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="'ArrayList'"/>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="$generic">
          <xsl:value-of select="fn:concat('&lt;', $generic, '&gt;')"/>
        </xsl:if>
      </xsl:with-param>
      <xsl:with-param name="varName" select="$name"/>
      <xsl:with-param name="isVariable" select="$isVariable"/>
      <xsl:with-param name="inLine" select="$inLine"/>
      <xsl:with-param name="withDeclaration" select="if ($useInterfaceAsTypeName) then false() else $withDeclaration"/>
    </xsl:call-template>
  </xsl:template>

  <!--getter-->
  <xsl:template name="java:createGetter">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="variableType" required="yes"/>
    <xsl:param name="variableName" required="yes"/>
    <xsl:param name="returnType" as="xs:string" select="''"/>
    <xsl:param name="static" select="false()"/>
    <xsl:param name="extraContent"/>
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="static" select="$static"/>
      <xsl:with-param name="modifier" select="'public'"/>
      <xsl:with-param name="type" select="$variableType"/>
      <xsl:with-param name="name">
        <xsl:choose>
          <xsl:when test="$variableType != 'Boolean'">
            <xsl:value-of select="fn:concat('get', jw:upperWord($variableName))"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="fn:concat('is', jw:upperWord($variableName))"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
      <xsl:with-param name="content">
        <xsl:call-template name="java:return">
          <xsl:with-param name="indent" select="$indent + 1"/>
          <xsl:with-param name="value">
            <xsl:choose>
              <xsl:when test="$static">
                <xsl:value-of select="$variableName"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:call-template name="java:varByDotNotation">
                  <xsl:with-param name="varName" select="$variableName"/>
                </xsl:call-template>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
          <xsl:with-param name="returnType" select="$returnType"/>
        </xsl:call-template>
        <xsl:value-of select="$extraContent"/>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="java:callGetter">
    <xsl:param name="variableName" required="yes"/>
    <xsl:value-of select="fn:concat('get', jw:upperWord($variableName), '()')"/>
  </xsl:template>

  <xsl:template name="java:callIs">
    <xsl:param name="variableName" required="yes"/>
    <xsl:value-of select="fn:concat('is', jw:upperWord($variableName), '()')"/>
  </xsl:template>

  <!--setter-->
  <xsl:template name="java:createSetter">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="variableType" required="yes"/>
    <xsl:param name="variableName" required="yes"/>
    <xsl:param name="static" select="false()"/>
    <xsl:param name="staticClassName" select="''"/>
    <xsl:param name="modifier" select="'public'"/>
    <xsl:param name="changeCheck" select="false()" as="xs:boolean"/>
    <xsl:param name="notNullCheck" select="false()" as="xs:boolean"/>
    <xsl:param name="constrainContent"/>
    <xsl:param name="extraContent"/>
    <xsl:call-template name="java:method">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="modifier" select="$modifier"/>
      <xsl:with-param name="static" select="$static"/>
      <xsl:with-param name="name">
        <xsl:text>set</xsl:text>
        <xsl:value-of select="jw:upperWord($variableName)"/>
      </xsl:with-param>
      <xsl:with-param name="parameterList" as="xs:string*">
        <xsl:call-template name="java:createParam">
          <xsl:with-param name="type" select="jw:mappeDataType($variableType)"/>
          <xsl:with-param name="name" select="$variableName"/>
        </xsl:call-template>
      </xsl:with-param>
      <xsl:with-param name="content">

        <xsl:value-of select="$constrainContent"/>

        <xsl:choose>
          <xsl:when test="$changeCheck">
            <xsl:call-template name="java:if">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="condition">
                <xsl:choose>
                  <xsl:when test="not (jw:isSimpleJavaType(jw:mappeDataType($variableType))) and false()">
                    <xsl:call-template name="java:boolExpr">
                      <xsl:with-param name="value1">
                        <xsl:call-template name="java:varByDotNotation">
                          <xsl:with-param name="name" select="'this'"/>
                          <xsl:with-param name="varName" select="$variableName"/>
                        </xsl:call-template>
                      </xsl:with-param>
                      <xsl:with-param name="value2" select="'null'"/>
                      <xsl:with-param name="operator" as="xs:string*" select="'=='"/>
                    </xsl:call-template>
                    <xsl:value-of select="' || !'"/>
                    <xsl:call-template name="java:callMethod">
                      <xsl:with-param name="inLine" select="true()"/>
                      <xsl:with-param name="objInstance">
                        <xsl:call-template name="java:varByDotNotation">
                          <xsl:with-param name="name" select="'this'"/>
                          <xsl:with-param name="varName" select="$variableName"/>
                        </xsl:call-template>
                      </xsl:with-param>
                      <xsl:with-param name="method" select="'equals'"/>
                      <xsl:with-param name="args" as="xs:string*" select="$variableName"/>
                    </xsl:call-template>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:call-template name="java:boolExpr">
                      <xsl:with-param name="value1">
                        <xsl:call-template name="java:varByDotNotation">
                          <xsl:with-param name="name" select="'this'"/>
                          <xsl:with-param name="varName" select="$variableName"/>
                        </xsl:call-template>
                      </xsl:with-param>
                      <xsl:with-param name="value2" select="$variableName"/>
                      <xsl:with-param name="operator" as="xs:string*" select="'!='"/>
                    </xsl:call-template>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
              <xsl:with-param name="thenContent">
                <xsl:call-template name="java:variable">
                  <xsl:with-param name="indent" select="$indent + 2"/>
                  <xsl:with-param name="name">
                    <xsl:choose>
                      <xsl:when test="$static">
                        <xsl:call-template name="java:varByDotNotation">
                          <xsl:with-param name="name" select="$staticClassName"/>
                          <xsl:with-param name="varName" select="$variableName"/>
                        </xsl:call-template>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:call-template name="java:varByDotNotation">
                          <xsl:with-param name="varName" select="$variableName"/>
                        </xsl:call-template>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:with-param>
                  <xsl:with-param name="value" select="$variableName"/>
                </xsl:call-template>
                <xsl:value-of select="$extraContent"/>
              </xsl:with-param>
            </xsl:call-template>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="java:variable">
              <xsl:with-param name="indent" select="$indent + 1"/>
              <xsl:with-param name="name">
                <xsl:choose>
                  <xsl:when test="$static">
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="name" select="$staticClassName"/>
                      <xsl:with-param name="varName" select="$variableName"/>
                    </xsl:call-template>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:call-template name="java:varByDotNotation">
                      <xsl:with-param name="varName" select="$variableName"/>
                    </xsl:call-template>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:with-param>
              <xsl:with-param name="value" select="$variableName"/>
            </xsl:call-template>
            <xsl:value-of select="$extraContent"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="java:callSetter">
    <xsl:param name="variableName" required="yes"/>
    <xsl:param name="variableValue" required="yes"/>
    <xsl:param name="type"/>
    <xsl:text>set</xsl:text>
    <xsl:value-of select="jw:upperWord($variableName)"/>
    <xsl:text>(</xsl:text>
    <xsl:if test="$type='String'">
      <xsl:text>&quot;</xsl:text>
    </xsl:if>
    <xsl:value-of select="$variableValue"/>
    <xsl:if test="$type='String'">
      <xsl:text>&quot;</xsl:text>
    </xsl:if>
    <xsl:text>)</xsl:text>
  </xsl:template>

  <xsl:template name="java:callSetterMethod">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="objInstance" select="'this'"/>
    <xsl:param name="instVariable" required="yes"/>
    <xsl:param name="value" required="yes"/>
    <xsl:param name="valueType"/>
    <xsl:call-template name="java:codeLine">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="content">
        <xsl:value-of select="fn:concat($objInstance, '.')"/>
        <xsl:call-template name="java:callSetter">
          <xsl:with-param name="variableName" select="$instVariable"/>
          <xsl:with-param name="variableValue" select="$value"/>
          <xsl:with-param name="type" select="$valueType"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="java:callSetterMethodOuterClass">
    <xsl:param name="indent" select="0"/>
    <xsl:param name="outerClass" required="yes" as="xs:string"/>
    <xsl:param name="instVariable" required="yes"/>
    <xsl:param name="value" as="xs:string" select="''"/>
    <xsl:param name="valueType" as="xs:string" select="''"/>

    <xsl:call-template name="java:callSetterMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="objInstance" select="fn:concat($outerClass, '.this')"/>
      <xsl:with-param name="instVariable" select="$instVariable"/>
      <xsl:with-param name="value" select="$value"/>
      <xsl:with-param name="valueType" select="$valueType"/>
    </xsl:call-template>

  </xsl:template>

  <xsl:template name="java:callGetterMethod">
    <xsl:param name="indent" select="0"/>
    <xsl:param name="objInstance" select="'this'"/>
    <xsl:param name="instVariable" required="yes"/>
    <!-- used for distinction between 'is' (for boolean) and 'get' -->
    <xsl:param name="type" as="xs:string" select="''"/>
    <xsl:param name="castType" as="xs:string" select="''"/>
    <xsl:param name="inLine" select="false()" as="xs:boolean"/>

    <xsl:choose>
      <xsl:when test="not ($inLine)">
        <xsl:call-template name="java:codeLine">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="content">
            <xsl:if test="$castType != ''">
              <xsl:value-of select="fn:concat('((', $castType, ')')"/>
            </xsl:if>
            <xsl:value-of select="fn:concat($objInstance, '.')"/>
            <xsl:choose>
              <xsl:when test="$type = 'Boolean'">
                <xsl:call-template name="java:callIs">
                  <xsl:with-param name="variableName" select="$instVariable"/>
                </xsl:call-template>
              </xsl:when>
              <xsl:otherwise>
                <xsl:call-template name="java:callGetter">
                  <xsl:with-param name="variableName" select="$instVariable"/>
                </xsl:call-template>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="$castType != ''">
              <xsl:value-of select="')'"/>
            </xsl:if>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="cg">
          <xsl:choose>
            <xsl:when test="$type = 'Boolean'">
              <xsl:call-template name="java:callIs">
                <xsl:with-param name="variableName" select="$instVariable"/>
              </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
              <xsl:call-template name="java:callGetter">
                <xsl:with-param name="variableName" select="$instVariable"/>
              </xsl:call-template>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:if test="$castType != ''">
          <xsl:value-of select="fn:concat('((', $castType, ')')"/>
        </xsl:if>
        <xsl:value-of select="fn:concat($objInstance, '.', $cg)"/>
        <xsl:if test="$castType != ''">
          <xsl:value-of select="')'"/>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="java:callGetterMethodOuterClass">
    <xsl:param name="indent" select="0"/>
    <xsl:param name="outerClass" required="yes" as="xs:string"/>
    <xsl:param name="instVariable" required="yes"/>
    <xsl:param name="type" as="xs:string" select="''"/>
    <xsl:param name="castType" as="xs:string" select="''"/>
    <xsl:param name="inLine" select="false()" as="xs:boolean"/>

    <xsl:call-template name="java:callGetterMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="inLine" select="$inLine"/>
      <xsl:with-param name="objInstance" select="fn:concat($outerClass, '.this')"/>
      <xsl:with-param name="instVariable" select="$instVariable"/>
      <xsl:with-param name="type" select="$type"/>
      <xsl:with-param name="castType" select="$castType"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="java:callMethod">
    <xsl:param name="indent" select="0"/>
    <xsl:param name="inLine" select="false()" as="xs:boolean"/>
    <xsl:param name="typecast" select="''"/>
    <xsl:param name="objInstance" select="'this'"/>
    <xsl:param name="method" required="yes"/>
    <xsl:param name="args" as="xs:string*"/>
    <xsl:variable name="arguments">
      <xsl:for-each select="$args">
        <xsl:choose>
          <xsl:when test="position() != last()">
            <xsl:value-of select="fn:concat(., ', ')"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="."/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </xsl:variable>

    <xsl:variable name="output">
      <xsl:value-of select="if ($typecast = '') then '' else fn:concat('(', $typecast,')')"/>
      <xsl:choose>
        <xsl:when test="$inLine">
          <xsl:value-of select="fn:concat($objInstance, '.', $method, '(', $arguments, ')' )"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="java:codeLine">
            <xsl:with-param name="indent" select="$indent"/>
            <xsl:with-param name="content">
              <xsl:value-of select="fn:concat($objInstance, '.', $method, '(', $arguments, ')' )"/>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:value-of select="$output"/>
  </xsl:template>

  <xsl:template name="java:callMethodOuterClass">
    <xsl:param name="indent" select="0"/>
    <xsl:param name="inLine" select="false()" as="xs:boolean"/>
    <xsl:param name="typecast" select="''"/>
    <xsl:param name="outerClass" required="yes" as="xs:string"/>
    <xsl:param name="method" required="yes"/>
    <xsl:param name="args" as="xs:string*"/>

    <xsl:call-template name="java:callMethod">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="inLine" select="$inLine"/>
      <xsl:with-param name="objInstance" select="fn:concat($outerClass, '.this')"/>
      <xsl:with-param name="method" select="$method"/>
      <xsl:with-param name="typecast" select="$typecast"/>
      <xsl:with-param name="args" select="$args"/>
    </xsl:call-template>

  </xsl:template>

  <xsl:template name="java:callSuper">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="paramList" as="xs:string*"/>
    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>
    <xsl:value-of select="'super('"/>
    <xsl:for-each select="$paramList">
      <xsl:choose>
        <xsl:when test="position() != last()">
          <xsl:value-of select="fn:concat(., ', ')"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="."/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
    <xsl:value-of select="');'"/>
    <xsl:call-template name="java:newLine"/>
  </xsl:template>

  <xsl:template name="java:return">
    <xsl:param name="indent" required="yes"/>
    <xsl:param name="value" as="xs:string" select="''"/>
    <xsl:param name="returnType" as="xs:string" select="''"/>
    <xsl:call-template name="java:codeLine">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="content">
        <xsl:value-of select="'return'"/>
        <xsl:if test="$value != ''">
          <xsl:value-of select="' ('"/>
          <xsl:if test="$returnType != ''">
            <xsl:value-of select="fn:concat('(', $returnType, ')')"/>
          </xsl:if>
          <xsl:value-of select="fn:concat($value, ')')"/>
        </xsl:if>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="java:boolExpr">
    <xsl:param name="value1" required="yes" as="xs:string"/>
    <xsl:param name="not" as="xs:boolean" select="false()"/>
    <xsl:param name="value2" as="xs:string" select="''"/>
    <xsl:param name="operator" as="xs:string" select="''"/>
    <xsl:variable name="result"
      select="fn:concat($value1, if ($operator != '') then ' ' else '', 
      if ($operator != '') then $operator else '', if ($operator != '') then ' ' else '', 
      if ($operator != '') then $value2 else '')"> </xsl:variable>
    <xsl:choose>
      <xsl:when test="$not = true()">
        <xsl:value-of select="fn:concat('!(', $result, ')')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$result"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="java:systemPrintln">
    <xsl:param name="indent" as="xs:integer" required="yes"/>
    <xsl:param name="err" select="false()" as="xs:boolean"/>
    <xsl:param name="value" as="xs:string"/>
    <xsl:call-template name="java:codeLine">
      <xsl:with-param name="indent" select="$indent"/>
      <xsl:with-param name="content">
        <xsl:text>System.</xsl:text>
        <xsl:choose>
          <xsl:when test="$err">
            <xsl:text>err</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>out</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:text>.println(</xsl:text>
        <xsl:if test="$value">
          <xsl:value-of select="jw:quote($value)"/>
        </xsl:if>
        <xsl:text>)</xsl:text>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="java:plus">
    <xsl:param name="inLine" select="true()"/>
    <xsl:param name="indent" as="xs:integer" select="1"/>
    <xsl:param name="value1" required="yes" as="xs:string"/>
    <xsl:param name="value2" required="yes" as="xs:string"/>
    <xsl:choose>
      <xsl:when test="$inLine">
        <xsl:value-of select="concat($value1, ' + ', $value2)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="java:codeLine">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="content">
            <xsl:value-of select="concat($value1, ' + ', $value2)"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="java:minus">
    <xsl:param name="inLine" select="true()"/>
    <xsl:param name="indent" as="xs:integer" select="1"/>
    <xsl:param name="value1" required="yes" as="xs:string"/>
    <xsl:param name="value2" required="yes" as="xs:string"/>
    <xsl:choose>
      <xsl:when test="$inLine">
        <xsl:value-of select="concat($value1, ' - ', $value2)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="java:codeLine">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="content">
            <xsl:value-of select="concat($value1, ' - ', $value2)"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="java:division">
    <xsl:param name="inLine" select="true()"/>
    <xsl:param name="indent" as="xs:integer" select="1"/>
    <xsl:param name="divisor" required="yes" as="xs:string"/>
    <xsl:param name="quotient" required="yes" as="xs:string"/>
    <xsl:choose>
      <xsl:when test="$inLine">
        <xsl:value-of select="concat($divisor, ' / ', $quotient)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="java:codeLine">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="content">
            <xsl:value-of select="concat($divisor, ' / ', $quotient)"/>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- insert here default/initialvalues for datatypes -->
  <xsl:template name="java:setDefaultValue">
    <xsl:param name="type" required="yes"/>
    <xsl:choose>
      <xsl:when test="$type = 'String'">
        <xsl:value-of select="'&quot;&quot;'"/>
      </xsl:when>
      <xsl:when test="$type = 'Boolean'">
        <xsl:value-of select="'false'"/>
      </xsl:when>
      <xsl:when test="$type = 'Integer'">
        <xsl:value-of select="'0'"/>
      </xsl:when>
      <xsl:when test="$type = 'Float'">
        <xsl:value-of select="'0'"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="'null'"/>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

  <xsl:template name="java:arrayValue">
    <xsl:param name="varName" required="yes" as="xs:string"/>
    <xsl:param name="index" required="yes" as="xs:string"/>
    <xsl:value-of select="fn:concat($varName, '[', $index, ']')"/>
  </xsl:template>

  <xsl:template name="java:classWithGenericType">
    <xsl:param name="class" as="xs:string" required="yes"/>
    <xsl:param name="genericType" as="xs:string" required="yes"/>
    <xsl:value-of select="fn:concat($class, '&lt;', $genericType, '&gt;')"/>
  </xsl:template>

  <!--**********************-->
  <!--templates for comments-->
  <!--**********************-->

  <!--inline comment-->
  <xsl:template name="java:inlineComment">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="content" required="yes"/>
    <xsl:call-template name="java:indent">
      <xsl:with-param name="size" select="$indent"/>
    </xsl:call-template>
    <xsl:text>// </xsl:text>
    <xsl:value-of select="$content"/>
    <xsl:call-template name="java:newLine"/>
  </xsl:template>

</xsl:stylesheet>
