<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation is responsible for processing the
	aors:ReactionRules element.
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

	<xsl:template match="aors:ActualPerceptionRule|aors:ReactionRule|aors:CommunicationRule" mode="chapterHeading">
		<xsl:text>Agent Rules</xsl:text>
	</xsl:template>

	<xsl:template match="aors:ActualPerceptionRule|aors:ReactionRule|aors:CommunicationRule" mode="heading">
		<xsl:value-of select="concat(../@name,'::',@name)"/>
	</xsl:template>

	<!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:ActualPerceptionRule|aors:ReactionRule|aors:CommunicationRule" mode="navigation">
		<xsl:param name="content"/>
		<li>
			<a>
				<xsl:attribute name="href">
					<xsl:call-template name="getId">
						<xsl:with-param name="prefix" select="'#'"/>
					</xsl:call-template>
				</xsl:attribute>
				<xsl:apply-templates select="." mode="chapterHeading"/>
			</a>
			<ul>
				<xsl:apply-templates select="$content" mode="navigationEntry"/>
			</ul>
		</li>
	</xsl:template>


	<!--################-->
	<!--###  content ###-->
	<!--################-->

	<xsl:template match="aors:ActualPerceptionRule|aors:ReactionRule|aors:CommunicationRule" mode="chapterContent">
		<xsl:param name="content"/>
		<xsl:apply-templates select="$content" mode="section1"/>
	</xsl:template>

	<xsl:template match="aors:ActualPerceptionRule|aors:ReactionRule|aors:CommunicationRule" mode="section1">
		<xsl:param name="class"/>
		<xsl:apply-templates select="." mode="section1Body">
			<xsl:with-param name="class">
				<xsl:text> rule</xsl:text>
				<xsl:choose>
					<xsl:when test="count(preceding::aors:ActualPerceptionRule|preceding::aors:ReactionRule|preceding::aors:CommunicationRule) mod 2 = 0">
						<xsl:text> odd</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text> even</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:ReactionRule|aors:CommunicationRule" mode="content">
		<xsl:apply-templates select="aors:documentation" mode="content"/>
		<xsl:apply-templates select="aors:WHEN" mode="section2">
			<xsl:with-param name="class" select="'when'"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:ON-EACH-SIMULATION-STEP" mode="section2">
			<xsl:with-param name="class" select="'on-each-simulation-step'"/>
		</xsl:apply-templates>
		<xsl:variable name="for" select="@agentVariable|aors:FOR"/>
		<xsl:apply-templates select="$for[1]" mode="section2">
			<xsl:with-param name="content" select="$for"/>
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

	<xsl:template match="aors:ActualPerceptionRule" mode="content">
		<xsl:apply-templates select="aors:documentation" mode="content"/>
		<xsl:apply-templates select="aors:WHEN" mode="section2">
			<xsl:with-param name="class" select="'when'"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="@agentVariable" mode="section2">
			<xsl:with-param name="content" select="@agentVariable"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:IF[1]" mode="section2">
			<xsl:with-param name="content" select="aors:IF"/>
			<xsl:with-param name="class" select="'if'"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:CREATE-EVT" mode="section2">
			<xsl:with-param name="content" select="aors:CREATE-EVT"/>
			<xsl:with-param name="class" select="'then'"/>
		</xsl:apply-templates>
	</xsl:template>
</xsl:stylesheet>