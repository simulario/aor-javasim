<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation is responsible for processing the
	aors:EnvironmentRules element.
-->
<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:aors="http://aor-simulation.org"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0"
	exclude-result-prefixes="aors xsl">

	<xsl:import href="Rules.xsl"/>

	<xsl:include href="keys_macros_params.xsl"/>

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:EnvironmentRules" mode="heading">
		<xsl:text>Environment Rules</xsl:text>
	</xsl:template>

	<xsl:template match="aors:EnvironmentRule" mode="heading">
		<xsl:value-of select="@name"/>
	</xsl:template>

	<!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:EnvironmentRules" mode="navigation">
		<xsl:apply-templates select="." mode="navigationEntry">
			<xsl:with-param name="subEntries">
				<xsl:apply-templates select="*" mode="navigation"/>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:EnvironmentRule" mode="navigation">
		<xsl:apply-templates select="." mode="navigationEntry"/>
	</xsl:template>

	<!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template match="aors:EnvironmentRules" mode="content">
		<xsl:apply-templates select="aors:*" mode="section1"/>
	</xsl:template>

	<xsl:template match="aors:EnvironmentRule" mode="section1">
		<xsl:param name="class"/>
		<xsl:apply-templates select="." mode="section1Body">
			<xsl:with-param name="class">
				<xsl:text> rule</xsl:text>
				<xsl:choose>
					<xsl:when test="count(preceding-sibling::aors:EnvironmentRule) mod 2 = 0">
						<xsl:text> odd</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text> even</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:EnvironmentRule" mode="content">
		<xsl:apply-templates select="aors:documentation" mode="content"/>
		<xsl:apply-templates select="aors:WHEN" mode="section2">
			<xsl:with-param name="class" select="'when'"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:ON-EACH-SIMULATION-STEP" mode="section2">
			<xsl:with-param name="class" select="'on-each-simulation-step'"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:FOR[1]" mode="section2">
			<xsl:with-param name="content" select="aors:FOR"/>
			<xsl:with-param name="class" select="'for'"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:DO" mode="section2">
			<xsl:with-param name="class" select="'do'"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:IF[1]" mode="section2">
			<xsl:with-param name="content" select="aors:IF"/>
			<xsl:with-param name="class" select="'if'"/>
		</xsl:apply-templates>
		<xsl:variable name="then" select="aors:THEN|aors:UPDATE-ENV|aors:SCHEDULE-EVT"/>
		<xsl:apply-templates select="$then[1]" mode="section2">
			<xsl:with-param name="content" select="$then"/>
			<xsl:with-param name="class" select="'then'"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:ELSE" mode="section2">
			<xsl:with-param name="class" select="'else'"/>
		</xsl:apply-templates>
	</xsl:template>
</xsl:stylesheet>