<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation is responsible for processing the
	aors:Globals element.
-->
<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:aors="http://aor-simulation.org"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0"
	exclude-result-prefixes="aors xsl">

	<xsl:import href="Property.xsl"/>
	<xsl:import href="Function.xsl"/>

	<xsl:include href="keys_macros_params.xsl"/>

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:Globals" mode="heading">
		<xsl:text>Globals</xsl:text>
	</xsl:template>

	<xsl:template match="aors:Globals" mode="classHeading">
		<span class="name">
			<xsl:apply-templates select="." mode="heading"/>
		</span>
	</xsl:template>

	<!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:Globals" mode="navigation">
		<xsl:apply-templates select="." mode="navigationEntry"/>
	</xsl:template>

	<!--################-->
	<!--###  content ###-->
	<!--################-->

	<xsl:template match="aors:Globals" mode="chapterContent">
		<xsl:apply-templates select="." mode="class">
			<xsl:with-param name="headingElement" select="$section1Heading"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:Globals" mode="classContent">
		<xsl:apply-templates select="aors:GlobalVariable[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'properties'"/>
			<xsl:with-param name="content" select="aors:GlobalVariable"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:GlobalFunction[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'functions'"/>
			<xsl:with-param name="content" select="aors:GlobalFunction"/>
		</xsl:apply-templates>
	</xsl:template>
</xsl:stylesheet>