<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation is responsible for processing the
	aors:Condition elements (StopCondition and Periodicity).
-->
<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:aors="http://aor-simulation.org"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0"
	exclude-result-prefixes="aors xsl">

	<xsl:include href="keys_macros_params.xsl"/>

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:StopCondition|aors:Periodicity|aors:Condition" mode="classSectionHeading">
		<xsl:text>Conditions</xsl:text>
	</xsl:template>

	<!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template match="aors:Periodicity|aors:StopCondition|aors:Condition" mode="classSectionContent">
		<xsl:param name="content"/>
		<ul>
			<xsl:apply-templates select="$content" mode="condition"/>
		</ul>
	</xsl:template>

	<xsl:template match="aors:Periodicity" mode="condition">
		<li>
			<xsl:call-template name="hideContent">
				<xsl:with-param name="content">
					<xsl:call-template name="copyCode">
						<xsl:with-param name="code" select=".|following-sibling::aors:Periodicity"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="heading" select="'Periodicity'"/>
				<xsl:with-param name="headingPrefix" select="concat('Code of ',../@name,'.')"/>
			</xsl:call-template>
		</li>
	</xsl:template>

	<xsl:template match="aors:StopCondition" mode="condition">
		<li>
			<xsl:call-template name="hideContent">
				<xsl:with-param name="content">
					<xsl:call-template name="copyCode">
						<xsl:with-param name="code" select=".|following-sibling::aors:StopCondition"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="heading" select="'StopCondition'"/>
				<xsl:with-param name="headingPrefix" select="concat('Code of ',../@name,'.')"/>
			</xsl:call-template>
		</li>
	</xsl:template>

	<xsl:template match="aors:Condition" mode="condition">
		<li>
			<xsl:call-template name="hideContent">
				<xsl:with-param name="content">
					<xsl:call-template name="copyCode">
						<xsl:with-param name="code" select=".|following-sibling::aors:Condition"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="heading" select="'Condition'"/>
				<xsl:with-param name="headingPrefix" select="concat('Code of ',local-name(..),'.')"/>
			</xsl:call-template>
		</li>
	</xsl:template>
</xsl:stylesheet>