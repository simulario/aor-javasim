<?xml version="1.0" encoding="UTF-8"?>

<!--
	This stylesheet provides a string based pair.
	@autor   Thomas Grundmann
	@version 1.0
	@created 2009-07-23
-->
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:x1f="http://www.informatik.tu-cottbus.de/~tgrundm1/projects/xslt-framework/xslt-1.0/">

	<!--#####################-->
	<!--### pair creation ###-->
	<!--#####################-->

	<!--
		This templates creates a pair.
		@param  value1 - The pair's first value.
		@param  value2 - The pair's second value.
		@return The created pair.
	-->
	<xsl:template name="x1f:Pair.createPair">
		<xsl:param name="value1"/>
		<xsl:param name="value2"/>
		<xsl:value-of select="concat('pair:',string-length($value1)+1,'#',$value1,'#',$value2)"/>
	</xsl:template>

	<!--##############-->
	<!--### getter ###-->
	<!--##############-->

	<!--
		This template returns a pairs first value.
		@param  pair - The pair whose first value shall be returned.
		@return The pair's first value.
	-->
	<xsl:template name="x1f:Pair.getValue1">
		<xsl:param name="pair"/>
		<xsl:value-of select="substring(substring-after($pair,'#'),1,substring-after(substring-before($pair,'#'),'pair:')-1)"/>
	</xsl:template>

	<!--
		This template returns a pairs second value.
		@param  pair - The pair whose second value shall be returned.
		@return The pair's second value.
	-->
	<xsl:template name="x1f:Pair.getValue2">
		<xsl:param name="pair"/>
		<xsl:value-of select="substring(substring-after($pair,'#'),substring-after(substring-before($pair,'#'),'pair:')+1)"/>
	</xsl:template>
</xsl:stylesheet>