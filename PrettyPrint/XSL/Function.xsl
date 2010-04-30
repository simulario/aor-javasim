<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation is responsible for processing the
	aors:Function elements.
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

	<xsl:template match="aors:Function|aors:GlobalFunction|aors:GridCellFunction|aors:DefaultConstructor" mode="classSectionHeading">
		<xsl:text>Functions</xsl:text>
	</xsl:template>

	<xsl:template match="aors:SubjectiveFunction" mode="classSectionHeading">
		<xsl:text>Subjective Functions</xsl:text>
	</xsl:template>

	<!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template match="aors:Function|aors:SubjectiveFunction|aors:GlobalFunction|aors:GridCellFunction|aors:DefaultConstructor" mode="classSectionContent">
		<xsl:param name="content"/>
		<ul>
			<xsl:apply-templates select="$content" mode="function"/>
		</ul>
	</xsl:template>
	
	<xsl:template match="aors:DefaultConstructor" mode="function">
		<li>
			<xsl:call-template name="hideContent">
				<xsl:with-param name="content">
					<xsl:call-template name="copyCode">
						<xsl:with-param name="code" select="aors:Def"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="heading" select="local-name()"/>
				<xsl:with-param name="headingPrefix" select="concat('Code of ',../@name,'.')"/>
			</xsl:call-template>
		</li>
	</xsl:template>

	<xsl:template match="aors:Function|aors:SubjectiveFunction|aors:GlobalFunction|aors:GridCellFunction" mode="function">
		<li>
			<xsl:call-template name="hideContent">
				<xsl:with-param name="content">
					<xsl:apply-templates select="aors:documentation" mode="content"/>
					<xsl:call-template name="copyCode">
						<xsl:with-param name="code" select="aors:Body"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="heading">
					<xsl:variable name="heading">
						<xsl:value-of select="concat(@name,'(')"/>
						<xsl:apply-templates select="aors:Parameter" mode="function"/>
						<xsl:value-of select="concat(') : ',@resultType)"/>
					</xsl:variable>
					<xsl:choose>
						<xsl:when test="@isStatic = 'true'">
							<span class="static">
								<xsl:copy-of select="$heading"/>
							</span>
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy-of select="$heading"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="headingPrefix">
					<xsl:text>Code of </xsl:text>
					<xsl:if test="../@name">
						<xsl:value-of select="concat(../@name,'.')"/>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
		</li>
	</xsl:template>

	<xsl:template match="aors:Parameter" mode="function">
		<xsl:value-of select="concat(@name,': ',@type)"/>
		<xsl:if test="following-sibling::aors:Parameter">
			<xsl:text>, </xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>