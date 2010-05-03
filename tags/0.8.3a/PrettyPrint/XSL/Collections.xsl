<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation is responsible for processing the
	aors:Collections element.
-->
<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:aors="http://aor-simulation.org"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0"
	exclude-result-prefixes="aors xsl">

	<xsl:import href="Property.xsl"/>

	<xsl:include href="keys_macros_params.xsl"/>

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:Collections" mode="heading">
		<xsl:text>Collections</xsl:text>
	</xsl:template>

	<xsl:template match="aors:Collections/aors:Collection" mode="classHeading">
		<span class="stereotype small">
			<xsl:value-of select="concat('&#171;',@type,'&#187;')"/>
		</span>
		<span class="name">
			<xsl:text> </xsl:text>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:if test="@name">
						<xsl:value-of select="@name"/>
					</xsl:if>
					<xsl:if test="@id">
						<xsl:value-of select="concat('[',@id,']')"/>
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="copy" select="true()"/>
			</xsl:call-template>
			<xsl:text> </xsl:text>
		</span>
		<span class="parameter small">
			<xsl:call-template name="createOptionalLink">
				<xsl:with-param name="node" select="key('Types',@itemType)"/>
				<xsl:with-param name="text" select="@itemType"/>
			</xsl:call-template>
		</span>
		<xsl:if test="@addToCollection">
			<span class="collection small">
				<xsl:text>(Collection: </xsl:text>
				<xsl:call-template name="createOptionalLink">
					<xsl:with-param name="node" select="key('Collections',@addToCollection)"/>
					<xsl:with-param name="text" select="@addToCollection"/>
				</xsl:call-template>
				<xsl:text>)</xsl:text>
			</span>
		</xsl:if>
		<xsl:if test="@objectVariable">
			<span class="variables small">
				<xsl:value-of select="concat('(obj-var: ',@objectVariable,')')"/>
			</span>
		</xsl:if>
	</xsl:template>


	<!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:Collections" mode="navigation">
		<xsl:apply-templates select="." mode="navigationEntry"/>
	</xsl:template>

	<!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template match="aors:Collections" mode="content">
		<xsl:apply-templates select="aors:Collection" mode="class">
			<xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
			<xsl:with-param name="headingElement" select="$section1Heading"/>
			<xsl:with-param name="class" select="'parameterized'"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:Collections/aors:Collection" mode="classContent">
		<xsl:apply-templates select="aors:Slot[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="content" select="aors:Slot"/>
		</xsl:apply-templates>
	</xsl:template>
</xsl:stylesheet>