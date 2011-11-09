<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation provides templates and parameters for structuring the
	pretty printing output.
-->
<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:aors="http://aor-simulation.org"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0"
	exclude-result-prefixes="aors xsl">

	<xsl:include href="keys_macros_params.xsl"/>

	<!--####################-->
	<!--### local macros ###-->
	<!--####################-->

	<!-- createSection -->

	<xsl:template name="createSection">
		<xsl:param name="headingElement"/>
		<xsl:param name="heading"/>
		<xsl:param name="content"/>
		<xsl:param name="class"/>
		<div>
			<xsl:attribute name="id">
				<xsl:call-template name="getId"/>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:text>section</xsl:text>
				<xsl:if test="$class">
					<xsl:value-of select="concat(' ',$class)"/>
				</xsl:if>
			</xsl:attribute>
			<xsl:element name="{$headingElement}">
				<xsl:attribute name="class">
					<xsl:text>heading</xsl:text>
				</xsl:attribute>
				<xsl:copy-of select="$heading"/>
			</xsl:element>
			<div class="content">
				<xsl:copy-of select="$content"/>
			</div>
		</div>
	</xsl:template>

	<!--###################-->
	<!--### basic modes ###-->
	<!--###################-->

	<xsl:template match="aors:*" mode="heading"/>
	<xsl:template match="aors:*" mode="content"/>
	<xsl:template match="aors:*" mode="navigation"/>

	<!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:*" mode="navigationEntry">
		<xsl:param name="subEntries" select="''"/>
		<li>
			<a>
				<xsl:attribute name="href">
					<xsl:call-template name="getId">
						<xsl:with-param name="prefix" select="'#'"/>
					</xsl:call-template>
				</xsl:attribute>
				<xsl:apply-templates select="." mode="navigationEntryTitle"/>
			</a>
			<xsl:if test="$subEntries != ''">
				<ul>
					<xsl:copy-of select="$subEntries"/>
				</ul>
			</xsl:if>
		</li>
	</xsl:template>

	<xsl:template match="aors:*" mode="navigationEntryTitle">
		<xsl:apply-templates select="." mode="heading"/>
	</xsl:template>

	<!--############-->
	<!--### part ###-->
	<!--############-->

	<xsl:template match="aors:*" mode="part">
		<xsl:param name="content"/>
		<xsl:apply-templates select="." mode="partBody">
			<xsl:with-param name="content" select="$content"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:*" mode="partBody">
		<xsl:param name="content"/>
		<div class="part">
			<xsl:attribute name="id">
				<xsl:call-template name="getId"/>
			</xsl:attribute>
			<xsl:element name="{$partHeading}">
				<xsl:attribute name="class">
					<xsl:text>heading</xsl:text>
				</xsl:attribute>
				<xsl:apply-templates select="." mode="partHeading"/>
			</xsl:element>
			<div class="content">
				<xsl:apply-templates select="." mode="partContent">
					<xsl:with-param name="content" select="$content"/>
				</xsl:apply-templates>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="aors:*" mode="partHeading">
		<xsl:apply-templates select="." mode="heading"/>
	</xsl:template>

	<xsl:template match="aors:*" mode="partContent">
		<xsl:param name="content"/>
		<xsl:apply-templates select="." mode="content">
			<xsl:with-param name="content" select="$content"/>
		</xsl:apply-templates>
	</xsl:template>

	<!--###############-->
	<!--### chapter ###-->
	<!--###############-->

	<xsl:template match="aors:*" mode="chapter">
		<xsl:param name="content"/>
		<xsl:apply-templates select="." mode="chapterBody">
			<xsl:with-param name="content" select="$content"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:*" mode="chapterBody">
		<xsl:param name="content"/>
		<div class="chapter">
			<xsl:attribute name="id">
				<xsl:call-template name="getId"/>
			</xsl:attribute>
			<xsl:element name="{$chapterHeading}">
				<xsl:attribute name="class">
					<xsl:text>heading</xsl:text>
				</xsl:attribute>
				<xsl:apply-templates select="." mode="chapterHeading"/>
			</xsl:element>
			<div class="content">
				<xsl:apply-templates select="." mode="chapterContent">
					<xsl:with-param name="content" select="$content"/>
				</xsl:apply-templates>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="aors:*" mode="chapterHeading">
		<xsl:apply-templates select="." mode="heading"/>
	</xsl:template>

	<xsl:template match="aors:*" mode="chapterContent">
		<xsl:param name="content"/>
		<xsl:apply-templates select="." mode="content">
			<xsl:with-param name="content" select="$content"/>
		</xsl:apply-templates>
	</xsl:template>

	<!--###############-->
	<!--### section ###-->
	<!--###############-->

	<!-- section1 -->

	<xsl:template match="@*|aors:*" mode="section1">
		<xsl:param name="class"/>
		<xsl:param name="content"/>
		<xsl:apply-templates select="." mode="section1Body">
			<xsl:with-param name="class" select="$class"/>
			<xsl:with-param name="content" select="$content"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="@*|aors:*" mode="section1Body">
		<xsl:param name="class"/>
		<xsl:param name="content"/>
		<xsl:call-template name="createSection">
			<xsl:with-param name="headingElement" select="$section1Heading"/>
			<xsl:with-param name="heading">
				<xsl:apply-templates select="." mode="section1Heading"/>
			</xsl:with-param>
			<xsl:with-param name="content">
				<xsl:apply-templates select="." mode="section1Content">
					<xsl:with-param name="content" select="$content"/>
				</xsl:apply-templates>
			</xsl:with-param>
			<xsl:with-param name="class" select="$class"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="@*|aors:*" mode="section1Heading">
		<xsl:apply-templates select="." mode="heading"/>
	</xsl:template>

	<xsl:template match="@*|aors:*" mode="section1Content">
		<xsl:param name="content"/>
		<xsl:apply-templates select="." mode="content">
			<xsl:with-param name="content" select="$content"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- section2 -->

	<xsl:template match="@*|aors:*" mode="section2">
		<xsl:param name="class"/>
		<xsl:param name="content"/>
		<xsl:apply-templates select="." mode="section2Body">
			<xsl:with-param name="class" select="$class"/>
			<xsl:with-param name="content" select="$content"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="@*|aors:*" mode="section2Body">
		<xsl:param name="class"/>
		<xsl:param name="content"/>
		<xsl:call-template name="createSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="heading">
				<xsl:apply-templates select="." mode="section2Heading"/>
			</xsl:with-param>
			<xsl:with-param name="content">
				<xsl:apply-templates select="." mode="section2Content">
					<xsl:with-param name="content" select="$content"/>
				</xsl:apply-templates>
			</xsl:with-param>
			<xsl:with-param name="class" select="$class"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="@*|aors:*" mode="section2Heading">
		<xsl:apply-templates select="." mode="heading"/>
	</xsl:template>

	<xsl:template match="@*|aors:*" mode="section2Content">
		<xsl:param name="content"/>
		<xsl:apply-templates select="." mode="content">
			<xsl:with-param name="content" select="$content"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- section3 -->

	<xsl:template match="@*|aors:*" mode="section3">
		<xsl:param name="class"/>
		<xsl:param name="content"/>
		<xsl:apply-templates select="." mode="section3Body">
			<xsl:with-param name="class" select="$class"/>
			<xsl:with-param name="content" select="$content"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="@*|aors:*" mode="section3Body">
		<xsl:param name="class"/>
		<xsl:param name="content"/>
		<xsl:call-template name="createSection">
			<xsl:with-param name="headingElement" select="$section3Heading"/>
			<xsl:with-param name="heading">
				<xsl:apply-templates select="." mode="section3Heading"/>
			</xsl:with-param>
			<xsl:with-param name="content">
				<xsl:apply-templates select="." mode="section3Content">
					<xsl:with-param name="content" select="$content"/>
				</xsl:apply-templates>
			</xsl:with-param>
			<xsl:with-param name="class" select="$class"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="@*|aors:*" mode="section3Heading">
		<xsl:apply-templates select="." mode="heading"/>
	</xsl:template>

	<xsl:template match="@*|aors:*" mode="section3Content">
		<xsl:param name="content"/>
		<xsl:apply-templates select="." mode="content">
			<xsl:with-param name="content" select="$content"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- section4 -->

	<xsl:template match="@*|aors:*" mode="section4">
		<xsl:param name="class"/>
		<xsl:param name="content"/>
		<xsl:apply-templates select="." mode="section4Body">
			<xsl:with-param name="class" select="$class"/>
			<xsl:with-param name="content" select="$content"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="@*|aors:*" mode="section4Body">
		<xsl:param name="class"/>
		<xsl:param name="content"/>
		<xsl:call-template name="createSection">
			<xsl:with-param name="headingElement" select="$section4Heading"/>
			<xsl:with-param name="heading">
				<xsl:apply-templates select="." mode="section4Heading"/>
			</xsl:with-param>
			<xsl:with-param name="content">
				<xsl:apply-templates select="." mode="section4Content">
					<xsl:with-param name="content" select="$content"/>
				</xsl:apply-templates>
			</xsl:with-param>
			<xsl:with-param name="class" select="$class"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="@*|aors:*" mode="section4Heading">
		<xsl:apply-templates select="." mode="heading"/>
	</xsl:template>

	<xsl:template match="@*|aors:*" mode="section4Content">
		<xsl:param name="content"/>
		<xsl:apply-templates select="." mode="content">
			<xsl:with-param name="content" select="$content"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- section5 -->

	<xsl:template match="@*|aors:*" mode="section5">
		<xsl:param name="class"/>
		<xsl:param name="content"/>
		<xsl:apply-templates select="." mode="section5Body">
			<xsl:with-param name="class" select="$class"/>
			<xsl:with-param name="content" select="$content"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="@*|aors:*" mode="section5Body">
		<xsl:param name="class"/>
		<xsl:param name="content"/>
		<xsl:call-template name="createSection">
			<xsl:with-param name="headingElement" select="$section5Heading"/>
			<xsl:with-param name="heading">
				<xsl:apply-templates select="." mode="section5Heading"/>
			</xsl:with-param>
			<xsl:with-param name="content">
				<xsl:apply-templates select="." mode="section5Content">
					<xsl:with-param name="content" select="$content"/>
				</xsl:apply-templates>
			</xsl:with-param>
			<xsl:with-param name="class" select="$class"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="@*|aors:*" mode="section5Heading">
		<xsl:apply-templates select="." mode="heading"/>
	</xsl:template>

	<xsl:template match="@*|aors:*" mode="section5Content">
		<xsl:param name="content"/>
		<xsl:apply-templates select="." mode="content">
			<xsl:with-param name="content" select="$content"/>
		</xsl:apply-templates>
	</xsl:template>

	<!--#############-->
	<!--### Class ###-->
	<!--#############-->

	<xsl:template match="@*|aors:*" mode="class">
		<xsl:param name="headingElement"/>
		<xsl:param name="content"/>
		<xsl:param name="class"/>
		<xsl:param name="kind" select="'class'"/>
		<xsl:apply-templates select="." mode="classBody">
			<xsl:with-param name="headingElement" select="$headingElement"/>
			<xsl:with-param name="content" select="$content"/>
			<xsl:with-param name="class" select="$class"/>
			<xsl:with-param name="kind" select="$kind"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="@*|aors:*" mode="classBody">
		<xsl:param name="headingElement"/>
		<xsl:param name="content"/>
		<xsl:param name="class"/>
		<xsl:param name="kind" select="'class'"/>
		<xsl:call-template name="createSection">
			<xsl:with-param name="headingElement" select="$headingElement"/>
			<xsl:with-param name="heading">
				<xsl:apply-templates select="." mode="classHeading"/>
			</xsl:with-param>
			<xsl:with-param name="content">
				<xsl:apply-templates select="." mode="classContent">
					<xsl:with-param name="content" select="$content"/>
				</xsl:apply-templates>
			</xsl:with-param>
			<xsl:with-param name="class">
				<xsl:if test="$class">
					<xsl:value-of select="concat($class,' ')"/>
				</xsl:if>
				<xsl:value-of select="$kind"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="@*|aors:*" mode="classHeading"/>
	<xsl:template match="@*|aors:*" mode="classContent"/>

	<xsl:template match="@*|aors:*" mode="classSection">
		<xsl:param name="headingElement"/>
		<xsl:param name="content"/>
		<xsl:param name="class"/>
		<xsl:apply-templates select="." mode="classSectionBody">
			<xsl:with-param name="headingElement" select="$headingElement"/>
			<xsl:with-param name="content" select="$content"/>
			<xsl:with-param name="class" select="$class"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="@*|aors:*" mode="classSectionBody">
		<xsl:param name="headingElement"/>
		<xsl:param name="content"/>
		<xsl:param name="class"/>
		<xsl:call-template name="createSection">
			<xsl:with-param name="headingElement" select="$headingElement"/>
			<xsl:with-param name="heading">
				<xsl:apply-templates select="." mode="classSectionHeading"/>
			</xsl:with-param>
			<xsl:with-param name="content">
				<xsl:apply-templates select="." mode="classSectionContent">
					<xsl:with-param name="content" select="$content"/>
				</xsl:apply-templates>
			</xsl:with-param>
			<xsl:with-param name="class" select="$class"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="@*|aors:*" mode="classSectionHeading"/>
	<xsl:template match="@*|aors:*" mode="classSectionContent"/>
</xsl:stylesheet>