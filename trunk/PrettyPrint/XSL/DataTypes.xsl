<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation is responsible for processing the
	aors:DataTypes element.
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

	<xsl:template match="aors:DataTypes" mode="heading">
		<xsl:text>Datatypes</xsl:text>
	</xsl:template>

	<xsl:template match="aors:DataTypes/aors:*" mode="classHeading">
		<span class="stereotype small">
			<xsl:value-of select="concat('&#171;',local-name(),'&#187;')"/>
		</span>
		<span class="name">
			<xsl:value-of select="concat(' ',@name,' ')"/>
		</span>
		<xsl:if test="@superType">
			<span class="supertype small">
				<xsl:text>(extends </xsl:text>
				<xsl:call-template name="createOptionalLink">
					<xsl:with-param name="node" select="key('DataTypes',@superType)"/>
					<xsl:with-param name="text" select="@superType"/>
				</xsl:call-template>
				<xsl:text>)</xsl:text>
			</span>
		</xsl:if>
	</xsl:template>

	<xsl:template match="aors:EnumerationLiteral" mode="classSectionHeading">
		<xsl:text>Literals</xsl:text>
	</xsl:template>
	
	<xsl:template match="aors:ClassDef" mode="classSectionHeading">
		<xsl:text>Class-Definition</xsl:text>
	</xsl:template>

	<!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:DataTypes" mode="navigation">
		<xsl:apply-templates select="." mode="navigationEntry">
			<xsl:with-param name="subEntries">
				<xsl:apply-templates select="aors:Enumeration|aors:ComplexDataType" mode="navigation">
					<xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
				</xsl:apply-templates>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:Enumeration|aors:ComplexDataType" mode="navigation">
		<xsl:apply-templates select="." mode="navigationEntry"/>
	</xsl:template>

	<xsl:template match="aors:Enumeration|aors:ComplexDataType" mode="navigationEntryTitle">
		<xsl:value-of select="@name"/>
	</xsl:template>

	<!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template match="aors:DataTypes" mode="content">
		<xsl:apply-templates select="aors:Enumeration|aors:ComplexDataType" mode="class">
			<xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
			<xsl:with-param name="headingElement" select="$section1Heading"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- Enumeration -->

	<xsl:template match="aors:Enumeration" mode="classContent">
		<xsl:apply-templates select="aors:EnumerationLiteral[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'literals'"/>
			<xsl:with-param name="content" select="aors:EnumerationLiteral"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:EnumerationLiteral" mode="classSectionContent">
		<xsl:param name="content"/>
		<ul>
			<xsl:apply-templates select="$content" mode="enumerationLiteral"/>
		</ul>
	</xsl:template>

	<xsl:template match="aors:EnumerationLiteral" mode="enumerationLiteral">
		<li>
			<xsl:copy-of select="text()"/>
		</li>
	</xsl:template>

	<!-- ComplexDataType -->

	<xsl:template match="aors:ComplexDataType" mode="classContent">
		<xsl:variable name="properties" select="aors:Attribute|aors:ReferenceProperty|aors:ComplexDataProperty|aors:EnmuerationProperty"/>
		<xsl:apply-templates select="$properties[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'properties'"/>
			<xsl:with-param name="content" select="$properties"/>
		</xsl:apply-templates>
		<xsl:variable name="functions" select="aors:DefaultConstructor|aors:Function"/>
		<xsl:apply-templates select="$functions[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'functions'"/>
			<xsl:with-param name="content" select="$functions"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:ClassDef[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'classdefinition'"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="aors:ClassDef" mode="classSectionContent">
		<ul>
			<li>
				<xsl:call-template name="hideContent">
					<xsl:with-param name="content">
						<xsl:call-template name="copyCode">
							<xsl:with-param name="code" select=".|following-sibling::aors:ClassDef"/>
						</xsl:call-template>
					</xsl:with-param>
					<xsl:with-param name="heading" select="'definition'"/>
					<xsl:with-param name="headingPrefix" select="concat('Code of ',../@name,'.')"/>
				</xsl:call-template>
			</li>
		</ul>
	</xsl:template>
</xsl:stylesheet>