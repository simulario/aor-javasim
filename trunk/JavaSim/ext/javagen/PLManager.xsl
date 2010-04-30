<?xml version="1.0" encoding="UTF-8"?>
<!--
  This transformation manage the target languages
  
  $Rev: 3295 $
  $Date: 2009-08-17 11:44:16 +0200 (Mon, 17 Aug 2009) $
  
  @author:   Jens Werner (jens.werner@tu-cottbus.de)
  @license:  GNU General Public License version 2 or higher
  @last changed by $Author: jewerner $
-->

<xsl:stylesheet version="2.0" xmlns:aorsml="http://aor-simulation.org" xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:java="http://www.sun.com/java" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:jw="http://www.informatik.tu-cottbus.de/~jwerner/">

  <xsl:import href="java.xsl"/>

  <!--***************************************-->
  <!--templates for creating aorsml-code-->
  <!--***************************************-->

  <!--class file-->
  <xsl:template name="aorsml:classFile">
    <xsl:param name="path" required="yes"/>
    <xsl:param name="name" required="yes"/>
    <xsl:param name="content" required="yes"/>

    <xsl:choose>
      <xsl:when test="$output.language eq 'Java'">

        <xsl:variable name="outputPath">
          <xsl:value-of select="fn:concat($sim.package.root, $file.separator, $path, $file.separator, $name, '.', $output.fileExtension)"/>
        </xsl:variable>

        <xsl:result-document href="{$outputPath}">
          <xsl:call-template name="java:package">
            <xsl:with-param name="indent" select="0"/>
            <xsl:with-param name="name" select="fn:replace($path, $file.separator, '.')"/>
          </xsl:call-template>
          <xsl:call-template name="java:newLine"/>
          <xsl:value-of select="$content"/>
        </xsl:result-document>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          <xsl:text>[ERROR] Unknown output language </xsl:text>
          <xsl:value-of select="$output.language"/>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- class -->
  <xsl:template name="aorsml:class">
    <xsl:param name="indent" required="yes" as="xs:integer"/>
    <xsl:param name="modifier"/>
    <xsl:param name="annotation" as="xs:string" select="''"/>
    <xsl:param name="name" required="yes"/>
    <xsl:param name="extends"/>
    <xsl:param name="implements"/>
    <xsl:param name="throws"/>
    <xsl:param name="content" required="yes"/>

    <xsl:choose>
      <xsl:when test="$output.language eq 'Java'">
        <xsl:call-template name="java:class">
          <xsl:with-param name="indent" select="$indent"/>
          <xsl:with-param name="modifier" select="$modifier"/>
          <xsl:with-param name="annotation" select="$annotation"/>
          <xsl:with-param name="name" select="$name"/>
          <xsl:with-param name="extends" select="$extends"/>
          <xsl:with-param name="implements" select="$implements"/>
          <xsl:with-param name="throws" select="$throws"/>
          <xsl:with-param name="content" select="$content"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:message terminate="yes">
          <xsl:text>[ERROR] Unknown output language </xsl:text>
          <xsl:value-of select="$output.language"/>
        </xsl:message>
      </xsl:otherwise>
    </xsl:choose>

  </xsl:template>

</xsl:stylesheet>
