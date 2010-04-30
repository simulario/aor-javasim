<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation is responsible for processing the
	aors:documentation elements.
-->
<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:aors="http://aor-simulation.org"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:html="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0"
	exclude-result-prefixes="aors dc html xsl">

	<xsl:include href="keys_macros_params.xsl"/>

	<!-- #################### -->
	<!-- ### local macros ### -->
	<!-- #################### -->

	<!-- getNameList -->

	<xsl:template name="getNameList">
		<xsl:apply-templates select="preceding-sibling::dc:*" mode="getNameList">
			<xsl:sort select="position()" order="descending"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="*" mode="getNameList"/>

	<xsl:template match="dc:*" mode="getNameList">
		<xsl:value-of select="name()"/>
		<xsl:text> </xsl:text>
	</xsl:template>

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:documentation" mode="heading">
		<xsl:text>Documentation</xsl:text>
	</xsl:template>

	<!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:documentation" mode="navigation">
		<xsl:apply-templates select="." mode="navigationEntry"/>
	</xsl:template>

	<!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template match="aors:documentation" mode="content">
		<xsl:variable name="documentation">
			<xsl:apply-templates select="aors:*|dc:*" mode="documentation"/>
		</xsl:variable>
		<xsl:if test="normalize-space($documentation) != ''">
			<dl class="documentation">
				<xsl:copy-of select="$documentation"/>
			</dl>
		</xsl:if>
	</xsl:template>

	<xsl:template match="aors:*" mode="documentation"/>

	<xsl:template match="aors:informationModelDiagram|aors:processModelDiagram" mode="documentation">
		<dt>
			<xsl:value-of select="concat(substring-before(local-name(),'Diagram'),':')"/>
		</dt>
		<dd>
			<a href="{normalize-space(text())}">
				<img alt="{local-name()}" src="{normalize-space(text())}"/>
			</a>
		</dd>
	</xsl:template>

	<xsl:template match="aors:description" mode="documentation">
		<xsl:variable name="prevNames">
			<xsl:call-template name="getNameList"/>
		</xsl:variable>
		<xsl:if test="substring-before($prevNames,' ') != name()">
			<dt>
				<xsl:value-of select="concat(local-name(),':')"/>
			</dt>
		</xsl:if>
		<dd>
			<xsl:copy-of select="text()|html:*"/>
		</dd>
	</xsl:template>

	<xsl:template match="dc:*" mode="documentation">
		<xsl:variable name="prevNames">
			<xsl:call-template name="getNameList"/>
		</xsl:variable>
		<xsl:if test="substring-before($prevNames,' ') != name()">
			<dt>
				<xsl:value-of select="concat(local-name(),':')"/>
			</dt>
		</xsl:if>
		<dd>
			<xsl:copy-of select="text()"/>
		</dd>
	</xsl:template>
</xsl:stylesheet>