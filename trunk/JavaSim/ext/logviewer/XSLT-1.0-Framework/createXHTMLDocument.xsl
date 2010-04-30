<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation provides templates and parameters for structuring the
	pretty printing output.
-->
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:x1f="http://www.informatik.tu-cottbus.de/~tgrundm1/projects/xslt-framework/xslt-1.0/"
	version="1.0" exclude-result-prefixes="xs xsl x1f">

	<!--##################-->
	<!--### parameters ###-->
	<!--##################-->

	<xsl:param name="x1f:documentHeading" select="'h1'"/>
	<xsl:param name="x1f:chapterHeading" select="'h2'"/>
	<xsl:param name="x1f:section1Heading" select="'h3'"/>
	<xsl:param name="x1f:section2Heading" select="'h4'"/>
	<xsl:param name="x1f:section3Heading" select="'h5'"/>
	<xsl:param name="x1f:section4Heading" select="'h6'"/>
	<xsl:param name="x1f:sectionHeading" select="'div'"/>


	<!--############################-->
	<!--### supportive functions ###-->
	<!--############################-->
	
	<!-- createOptionalLink -->
	
	<xsl:template name="x1f:createOptionalLink">
		<xsl:param name="node"/>
		<xsl:param name="text"/>
		<xsl:param name="copy" select="false()"/>
		<xsl:choose>
			<xsl:when test="$node">
				<a>
					<xsl:attribute name="href">
						<xsl:call-template name="x1f:createID">
							<xsl:with-param name="node" select="$node"/>
							<xsl:with-param name="prefix" select="'#'"/>
						</xsl:call-template>
					</xsl:attribute>
					<xsl:choose>
						<xsl:when test="$copy">
							<xsl:copy-of select="$text"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$text"/>
						</xsl:otherwise>
					</xsl:choose>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="$copy">
						<xsl:copy-of select="$text"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$text"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- getBooleanValue -->
	
	<xsl:template name="x1f:getBooleanValue">
		<xsl:param name="value"/>
		<xsl:param name="showNegative" select="false()"/>
		<xsl:choose>
			<xsl:when test="$value = 'true'">
				<xsl:text>&#x2713;</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="$showNegative">
					<xsl:text>no</xsl:text>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- getOptionalValue -->
	
	<xsl:template name="x1f:getOptionalValue">
		<xsl:param name="node" select="''"/>
		<xsl:param name="defaultValue" select="'n/a'"/>
		<xsl:param name="copy" select="false()"/>
		<xsl:choose>
			<xsl:when test="$node != '' and $copy">
				<xsl:copy-of select="$node"/>
			</xsl:when>
			<xsl:when test="$node != ''">
				<xsl:value-of select="$node"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$defaultValue"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- hideContent -->
	
	<xsl:template name="x1f:hideContent">
		<xsl:param name="content"/>
		<xsl:param name="heading"/>
		<xsl:param name="headingPrefix"/>
		<xsl:param name="headingSuffix"/>
		<xsl:if test="$content">
			<div class="hiddenContent">
				<span class="hover">
					<xsl:copy-of select="$heading"/>
				</span>
				<div class="hide">
					<div class="resize">
						<div class="content">
							<div class="label">
								<xsl:copy-of select="$headingPrefix"/>
								<xsl:copy-of select="$heading"/>
								<xsl:copy-of select="$headingSuffix"/>
							</div>
							<xsl:copy-of select="$content"/>
						</div>
					</div>
				</div>
			</div>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="x1f:toUpperCase">
		<xsl:param name="string"/>
		<xsl:value-of select="translate($string,'abcdefghijklmnopqrstuvwxyzäöü','ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ')"/>
	</xsl:template>

	<xsl:template name="x1f:toLowerCase">
		<xsl:param name="string"/>
		<xsl:value-of select="translate($string,'ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ','abcdefghijklmnopqrstuvwxyzäöü')"/>
	</xsl:template>
	
	<!--
		This template creates an ID for a given node based on its position in the source document.
		@param  node - The node.
		@return The id.
	-->
	<xsl:template name="x1f:createID">
		<xsl:param name="node" select="."/>
		<xsl:param name="prefix"/>
		<xsl:value-of select="concat($prefix,generate-id($node))"/>
	</xsl:template>

	<!--##########################-->
	<!--### component creation ###-->
	<!--##########################-->

	<!--
		This template creates a heading.
		@param  element - The heading element.
		@param  content - The heading's content.
		@return The heading.
	-->
	<xsl:template name="x1f:createHeading">
		<xsl:param name="element"/>
		<xsl:param name="content"/>
		<xsl:element name="{$element}" namespace="http://www.w3.org/1999/xhtml">
			<xsl:attribute name="class">
				<xsl:text>heading</xsl:text>
			</xsl:attribute>
			<xsl:copy-of select="$content"/>
		</xsl:element>
	</xsl:template>
	
	<!--
		This template create a preface.
		@param  content - The preface's content.
		@return The preface.
	-->
	<xsl:template name="x1f:createPreface">
		<xsl:param name="content"/>
		<div class="preface">
			<xsl:copy-of select="$content"/>
		</div>
	</xsl:template>

	<!--
		This template creates a navigation.
		@param  content - The navigation's content.
		@return The navigation.
	-->
	<xsl:template name="x1f:createNavigation">
		<xsl:param name="content"/>
		<ol class="navigation">
			<xsl:copy-of select="$content"/>
		</ol>
	</xsl:template>

	<!--
		This template create a navigation entry.
		@param  content - the navigation entry's content.
		@return The navigation entry.
	-->
	<xsl:template name="x1f:createNavigationEntry">
		<xsl:param name="content"/>
		<li>
			<xsl:copy-of select="$content"/>
		</li>
	</xsl:template>

	<!--
		This template creates a content body.
		@param  content - The body's content.
		@return The content body.
	-->
	<xsl:template name="x1f:createBody">
		<xsl:param name="content"/>
		<div class="body">
			<xsl:copy-of select="$content"/>
		</div>
	</xsl:template>

	<!--
		This template creates a footer.
		@param  content - The footer's content.
		@return The footer.
	-->
	<xsl:template name="x1f:createFooter">
		<xsl:param name="content"/>
		<div class="footer">
			<xsl:copy-of select="$content"/>
		</div>
	</xsl:template>


	<!--#########################-->
	<!--### document creation ###-->
	<!--#########################-->

	<!--
		This templates creates a document.
		@param  id         - The document's ID. If not given, no ID will be created.
		@param  heading    - The document's heading content. If not given, no heading will be created.
		@param  preface    - The document's preface content. If not given, no preface will be created.
		@param  navigation - The document's navigation. If not given, no navigation will be created.
		@param  body       - The document's body.
		@param  footer     - The document's footer. If not given, no footer will be created.
		@return The created document.
	-->
	<xsl:template name="x1f:createDocument">
		<xsl:param name="id"/>
		<xsl:param name="heading"/>
		<xsl:param name="preface"/>
		<xsl:param name="navigation"/>
		<xsl:param name="body"/>
		<xsl:param name="footer"/>
		<div>
			<xsl:if test="$id != ''">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="class">
				<xsl:text>document</xsl:text>
			</xsl:attribute>
			<xsl:if test="$heading != ''">
				<xsl:call-template name="x1f:createHeading">
					<xsl:with-param name="element" select="$x1f:documentHeading"/>
					<xsl:with-param name="content" select="$heading"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$preface != ''">
				<xsl:call-template name="x1f:createPreface">
					<xsl:with-param name="content" select="$preface"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$navigation != ''">
				<xsl:call-template name="x1f:createNavigation">
					<xsl:with-param name="content" select="$navigation"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:call-template name="x1f:createBody">
				<xsl:with-param name="content" select="$body"/>
			</xsl:call-template>
			<xsl:if test="$footer != ''">
				<xsl:call-template name="x1f:createFooter">
					<xsl:with-param name="content" select="$footer"/>
				</xsl:call-template>
			</xsl:if>
		</div>
	</xsl:template>

	<!--########################-->
	<!--### chapter creation ###-->
	<!--########################-->

	<!--
		This templates creates a chapter.
		@param  id         - The chapter's ID. If not given, no ID will be created.
		@param  class      - Additional class information for the chapter.
		@param  heading    - The chapter's heading content. If not given, no heading will be created.
		@param  preface    - The chapter's preface content. If not given, no preface will be created.
		@param  navigation - The chapter's navigation. If not given, no navigation will be created.
		@param  body       - The chapter's body.
		@param  footer     - The chapter's footer. If not given, no footer will be created.
		@return The created chapter.
	-->
	<xsl:template name="x1f:createChapter">
		<xsl:param name="id"/>
		<xsl:param name="class"/>
		<xsl:param name="heading"/>
		<xsl:param name="preface"/>
		<xsl:param name="navigation"/>
		<xsl:param name="body"/>
		<xsl:param name="footer"/>
		<div>
			<xsl:if test="$id != ''">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="class">
				<xsl:text>chapter</xsl:text>
				<xsl:if test="$class">
					<xsl:value-of select="concat(' ',$class)"/>
				</xsl:if>
			</xsl:attribute>
			<xsl:if test="$heading != ''">
				<xsl:call-template name="x1f:createHeading">
					<xsl:with-param name="element" select="$x1f:chapterHeading"/>
					<xsl:with-param name="content" select="$heading"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$preface != ''">
				<xsl:call-template name="x1f:createPreface">
					<xsl:with-param name="content" select="$preface"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$navigation != ''">
				<xsl:call-template name="x1f:createNavigation">
					<xsl:with-param name="content" select="$navigation"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:call-template name="x1f:createBody">
				<xsl:with-param name="content" select="$body"/>
			</xsl:call-template>
			<xsl:if test="$footer != ''">
				<xsl:call-template name="x1f:createFooter">
					<xsl:with-param name="content" select="$footer"/>
				</xsl:call-template>
			</xsl:if>
		</div>
	</xsl:template>

	<!--########################-->
	<!--### section creation ###-->
	<!--########################-->

	<!--
		This templates creates a section.
		@param  id         - The section's ID. If not given, no ID will be created.
		@param  class      - Additional class information for the section.
		@param  heading    - The section's heading content. If not given, no heading will be created.
		@param  preface    - The section's preface content. If not given, no preface will be created.
		@param  navigation - The section's navigation. If not given, no navigation will be created.
		@param  body       - The section's body.
		@param  footer     - The section's footer. If not given, no footer will be created.
		@return The created section.
	-->
	<xsl:template name="x1f:createSection">
		<xsl:param name="id"/>
		<xsl:param name="class"/>
		<xsl:param name="headingElement"/>
		<xsl:param name="heading"/>
		<xsl:param name="preface"/>
		<xsl:param name="navigation"/>
		<xsl:param name="body"/>
		<xsl:param name="footer"/>
		<div>
			<xsl:if test="$id != ''">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:attribute name="class">
				<xsl:text>section</xsl:text>
				<xsl:if test="$class">
					<xsl:value-of select="concat(' ',$class)"/>
				</xsl:if>
			</xsl:attribute>
			<xsl:if test="$heading != '' and $headingElement != ''">
				<xsl:call-template name="x1f:createHeading">
					<xsl:with-param name="element" select="$headingElement"/>
					<xsl:with-param name="content" select="$heading"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$preface != ''">
				<xsl:call-template name="x1f:createPreface">
					<xsl:with-param name="content" select="$preface"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$navigation != ''">
				<xsl:call-template name="x1f:createNavigation">
					<xsl:with-param name="content" select="$navigation"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:call-template name="x1f:createBody">
				<xsl:with-param name="content" select="$body"/>
			</xsl:call-template>
			<xsl:if test="$footer != ''">
				<xsl:call-template name="x1f:createFooter">
					<xsl:with-param name="content" select="$footer"/>
				</xsl:call-template>
			</xsl:if>
		</div>
	</xsl:template>
</xsl:stylesheet>
