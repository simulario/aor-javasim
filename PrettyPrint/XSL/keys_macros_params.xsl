<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation provides some macros for the pretty printing.
-->
<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:aors="http://aor-simulation.org"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0"
	exclude-result-prefixes="aors xsl">

	<xsl:include href="code.xsl"/>

	<!--############-->
	<!--### Keys ###-->
	<!--############-->

	<xsl:key name="EntityTypes" match="//aors:EntityTypes//aors:*" use="@name"/>
	<xsl:key name="DataTypes" match="//aors:DataTypes//aors:*" use="@name"/>
	<xsl:key name="Collections" match="//aors:Collections//aors:*" use="@name"/>
	<xsl:key name="Types" match="//aors:EntityTypes//aors:*|//aors:DataTypes//aors:*|//aors:Collections//aors:*" use="@name"/>
  <xsl:key name="StatisticsVariables" match="//aors:Statistics/aors:Variable" use="@name"/>
  <xsl:key name="GlobalVariables" match="//aors:Globals/aors:GlobalVariable" use="@name"/>
  
	<!--##############-->
	<!--### Macros ###-->
	<!--##############-->

	<!-- createOptionalLink -->

	<xsl:template name="createOptionalLink">
		<xsl:param name="node"/>
		<xsl:param name="text"/>
		<xsl:param name="copy" select="false()"/>
		<xsl:choose>
			<xsl:when test="$node">
				<a>
					<xsl:attribute name="href">
						<xsl:call-template name="getId">
							<xsl:with-param name="node" select="$node"/>
							<xsl:with-param name="prefix" select="'#'"/>
						</xsl:call-template>
					</xsl:attribute>
					<xsl:choose>
						<xsl:when test="$copy">
							<xsl:copy-of select="$text"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$text"/>
						</xsl:otherwise>
					</xsl:choose>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$copy">
						<xsl:copy-of select="$text"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$text"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- getBooleanValue -->

	<xsl:template name="getBooleanValue">
		<xsl:param name="value"/>
		<xsl:param name="showNegative" select="false()"/>
		<xsl:choose>
			<xsl:when test="$value = 'true'">
				<xsl:text>&#x2713;</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="$showNegative">
					<xsl:text>no</xsl:text>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- getExpandedName -->

	<xsl:template name="getExpandedName">
		<xsl:param name="name"/>
		<xsl:text>http://aor-simulation.org#</xsl:text>
		<xsl:choose>
			<xsl:when test="contains($name,':')">
				<xsl:value-of select="substring-after($name,':')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$name"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- getId -->

	<xsl:template name="getId">
		<xsl:param name="node" select="."/>
		<xsl:param name="prefix" select="''"/>
		<xsl:value-of select="concat($prefix,'id_',generate-id($node))"/>
	</xsl:template>

	<!-- getOptionalValue -->

	<xsl:template name="getOptionalValue">
		<xsl:param name="node" select="''"/>
		<xsl:param name="defaultValue" select="'n/a'"/>
		<xsl:param name="copy" select="false()"/>
		<xsl:choose>
			<xsl:when test="$node != '' and $copy">
				<xsl:copy-of select="$node"/>
			</xsl:when>
			<xsl:when test="$node != ''">
				<xsl:value-of select="$node"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$defaultValue"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- hideContent -->

	<xsl:template name="hideContent">
		<xsl:param name="content"/>
		<xsl:param name="heading"/>
		<xsl:param name="headingPrefix"/>
		<xsl:param name="headingSuffix"/>
		<xsl:if test="$content">
			<div class="hiddenContent">
				<span class="hover">
					<xsl:copy-of select="$heading"/>
				</span>
				<div class="hide">
					<div class="resize">
						<div class="content">
							<div class="label">
								<xsl:copy-of select="$headingPrefix"/>
								<xsl:copy-of select="$heading"/>
								<xsl:copy-of select="$headingSuffix"/>
							</div>
							<xsl:copy-of select="$content"/>
						</div>
					</div>
				</div>
			</div>
		</xsl:if>
	</xsl:template>

	<!--##############-->
	<!--### Params ###-->
	<!--##############-->

	<xsl:param name="partHeading" select="'h1'"/>
	<xsl:param name="chapterHeading" select="'h2'"/>
	<xsl:param name="section1Heading" select="'h3'"/>
	<xsl:param name="section2Heading" select="'h4'"/>
	<xsl:param name="section3Heading" select="'h5'"/>
	<xsl:param name="section4Heading" select="'h6'"/>
	<xsl:param name="section5Heading" select="'div'"/>
	<xsl:param name="expressionLength" select="'100'"/>
	<xsl:param name="smallExpressionLength" select="'70'"/>

</xsl:stylesheet>