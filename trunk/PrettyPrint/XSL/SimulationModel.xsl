<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation is responsible for processing the
	aors:SimulationModel element.
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

	<xsl:template match="aors:SimulationModel" mode="heading">
		<xsl:text>Model Properties</xsl:text>
	</xsl:template>

	<!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:SimulationModel" mode="navigation">
		<xsl:apply-templates select="." mode="navigationEntry"/>
	</xsl:template>

	<!--################-->
	<!--###  content ###-->
	<!--################-->

	<xsl:template match="aors:SimulationModel" mode="content">
		<dl class="modelProperties">
			<xsl:if test="@autoKinematics or @autoGravitation or @autoImpulse or @autoCollision">
				<dt>Physics Simulation:</dt>
				<xsl:apply-templates select="@autoKinematics|@autoGravitation|@autoImpulse|@autoCollision" mode="modelProperties"/>
			</xsl:if>
			<xsl:if test="@baseURI">
				<dt>BaseURI:</dt>
				<xsl:apply-templates select="@baseURI" mode="modelProperties"/>
			</xsl:if>
		</dl>
	</xsl:template>

	<xsl:template match="@*" mode="modelProperties">
		<xsl:if test=". = 'true'">
			<dd><xsl:value-of select="local-name()"/></dd>
		</xsl:if>
	</xsl:template>

	<xsl:template match="@baseURI" mode="modelProperties">
		<dd><xsl:value-of select="."/></dd>
	</xsl:template>
</xsl:stylesheet>