<?xml version="1.0" encoding="UTF-8"?>

<!-- 
	TODO:
		- elements
			- handle informations (block)
		- group | attributeGroup
			- annotations
		- xs:any
		- support for more dc-elements
-->

<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:x1f="http://www.informatik.tu-cottbus.de/~tgrundm1/projects/xslt-framework/xslt-1.0/" xmlns:dc="http://purl.org/dc/elements/1.1/" version="1.0" exclude-result-prefixes="xs xsl x1f dc">

	<xsl:import href="elements.xsl"/>	
	<xsl:import href="XSLT-1.0-Framework/mergeDocuments.xsl"/>

	<xsl:output method="xml" doctype-public="-//W3C//DTD XHTML 1.1//EN" doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd" encoding="UTF-8" indent="yes"/>

	<!--####################-->
	<!--### local macros ###-->
	<!--####################-->

	<!-- getChapters -->

	<xsl:template name="getChapters">
		<xsl:param name="node" select="."/>
		<xsl:param name="otherDocuments"/>
		
		<xsl:call-template name="x1f:List.appendEntries">
			<xsl:with-param name="list">
				<xsl:call-template name="x1f:List.createEmptyList">
					<xsl:with-param name="duplicate-free" select="true()"/>
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="entries">
				<xsl:apply-templates select="$node/xs:element[not(@abstract = 'true') and @name]" mode="getChapters">
					<xsl:sort select="@name"/>
				</xsl:apply-templates>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="xs:element[@name]" mode="getChapters">
		<xsl:call-template name="x1f:List.Entry.createEntry">
			<xsl:with-param name="value">
				<xsl:call-template name="x1f:toUpperCase">
					<xsl:with-param name="string" select="substring(@name,1,1)"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- getTitle -->

	<xsl:template name="getTitle">
		<xsl:text>Reference Manual</xsl:text>
		<xsl:variable name="titles" select="/xs:schema/xs:annotation/xs:documentation/dc:title"/>
		<xsl:apply-templates select="$titles[1]" mode="getTitle"/>
	</xsl:template>

	<xsl:template match="dc:title" mode="getTitle">
		<xsl:value-of select="concat(' for ',text())"/>
	</xsl:template>

	<!--##########################-->
	<!--### root and xs:schema ###-->
	<!--##########################-->

	<xsl:template match="/">
		<html>
			<head>
				<title>
					<xsl:call-template name="getTitle"/>
				</title>
				<meta http-equiv="Content-Type" content="application/xml; charset=UTF-8"/>
				<style type="text/css">
					<xsl:text disable-output-escaping="yes">
						/* navigation */
											
						div.document > ol.navigation {
							display: block;
							position: fixed;
							top:      0;
							left:     0;
							height:   100%;
							width:  28%;
							overflow: auto;
							border-right: 1px solid black;
							
							list-style: none;
							margin: 0;
							padding:0;
						}
						
						div.document > ol.navigation > li {
							margin: 0;
							padding:0 10px 20px;
						}
						
						div.document > ol.navigation ol.navigation {
							list-style: inherit;
							margin: 0;
							padding: 0;
						}
						
						div.document > ol.navigation ol.navigation > li {
							margin: 0;
							padding: 0 0 0 20px;
						}
						
						/* document */
						
						div.document > * {
							display: block;
							margin: 0 0 0 28%;
							padding: 0 10px;
						}
	
						div.document > .heading {
							margin-top: 0;
							margin-bottom: 10px;
							font-size: 200%;
							font-weight: bold;
						}
						
						/* chapter */
	
						div.chapter > .heading {
							display: none;
						}
	
						/* section */
						
						div.section .heading {
							margin-top: 20px;
							margin-bottom: 10px;
							font-size: 160%;
							font-weight: bold;
						}
						
						div.section div.section .heading {
							display: none;
						}
								
						div.subelements, div.attributes, div.content, div.documentation, div.substiution {
							margin: 15px 0;
						}
						
						/* content */
											
						table {
							width: 100%;
							border-collapse: collapse;
							margin: 0 0 15px;
						}
						
						table.optional {
							background-color: #dddddd;
						}
						
						caption {
							display: none;
						}
						
						th, td {
							padding: 5px;
							text-align: left;
							vertical-align: top;
							border: 1px solid black;
						}
						
						ul {
							margin: 0;
							padding: 0 0 0 15px;
						}
						
						ul ul {
							margin: 0;
							padding: 0 0 0 35px;
						}
						
						ul li {
							margin: 0 0 5px;
							padding: 0;
						}
						
						dl {
							position: relative;
							margin: -5px 0 5px;
							padding: 0;
						}
						
						ul + dl {
							margin-top: 0;
						}
						
						dt {
							float: left;
							margin: 0px 10px 0 0;
							padding: 0;
							font-style: italic;
						}
						
						dt + dt, dd + dt {
							margin-top: 5px;
							clear: both;
						}
						
						dd {
							margin: 5px 0 0;
							padding: 0;
						}
						</xsl:text>
				</style>
			</head>
			<body>
				<xsl:apply-templates select="xs:schema"/>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="/" mode="x1f:mergeDocuments.addURIs">
		<xsl:param name="listOfURIs"/>
		<xsl:param name="depth"/>
		<xsl:call-template name="x1f:mergeDocuments.addURIsByDepthFirstSearch">
			<xsl:with-param name="listOfURIs" select="$listOfURIs"/>
			<xsl:with-param name="listOfNewURIs">
				<xsl:call-template name="x1f:List.appendEntries">
					<xsl:with-param name="list">
						<xsl:call-template name="x1f:List.createEmptyList">
							<xsl:with-param name="duplicate-free" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
					<xsl:with-param name="entries">
						<xsl:apply-templates select="xs:schema/xs:include" mode="x1f:mergeDocuments.addURIs">
							<xsl:with-param name="listOfURIs" select="$listOfURIs"/>
							<xsl:with-param name="depth" select="$depth"/>
						</xsl:apply-templates>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="depth" select="$depth + 1"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="xs:schema/xs:include" mode="x1f:mergeDocuments.addURIs">
		<xsl:param name="listOfURIs"/>
		<xsl:param name="depth"/>
		<xsl:call-template name="x1f:List.Entry.createEntry">
			<xsl:with-param name="value" select="@schemaLocation"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="/" mode="x1f:mergeDocuments.processDocument">
		<xsl:param name="URI"/>
		<xsl:param name="parameters"/>
		<xsl:call-template name="x1f:List.Entry.createEntry">
			<xsl:with-param name="value" select="$URI"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="/xs:schema">
		<xsl:call-template name="createContent">
			<xsl:with-param name="documentList">
				<xsl:call-template name="x1f:List.appendEntries">
					<xsl:with-param name="list">
						<xsl:call-template name="x1f:List.createEmptyList">
							<xsl:with-param name="duplicate-free" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
					<xsl:with-param name="entries">
						<xsl:call-template name="x1f:mergeDocuments.mergeByNode">
							<xsl:with-param name="documentNode" select="/"/>
							<xsl:with-param name="relativeURIs" select="true()"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="createContent">
		<xsl:param name="nodes" select="." />
		<xsl:param name="documentList"/>
		
		<xsl:variable name="moreDocuments">
			<xsl:call-template name="x1f:List.hasNext">
				<xsl:with-param name="list" select="$documentList"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$moreDocuments = 'true'">
				<xsl:variable name="documentListWithNextIndex">
					<xsl:call-template name="x1f:List.nextIndex">
						<xsl:with-param name="list" select="$documentList"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="newDocument">
					<xsl:call-template name="x1f:List.getValue">
						<xsl:with-param name="list" select="$documentListWithNextIndex"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:call-template name="createContent">
					<xsl:with-param name="nodes" select="$nodes | document(string($newDocument),.)//xs:schema"/>
					<xsl:with-param name="documentList" select="$documentListWithNextIndex"/>
				</xsl:call-template>
			</xsl:when>
			
			<xsl:otherwise>
				<xsl:variable name="chapterList">
					<xsl:call-template name="getChapters">
						<xsl:with-param name="node" select="$nodes"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:call-template name="x1f:createDocument">
					<xsl:with-param name="heading">
						<xsl:call-template name="getTitle"/>
					</xsl:with-param>
					<xsl:with-param name="preface">
						<xsl:apply-templates select="." mode="annotations">
							<xsl:with-param name="documentNodes" select="$nodes"/>
						</xsl:apply-templates>
					</xsl:with-param>
					<xsl:with-param name="navigation">
						<xsl:apply-templates select="." mode="navigation">
							<xsl:with-param name="chapterList" select="$chapterList"/>
							<xsl:with-param name="documentNodes" select="$nodes"/>
						</xsl:apply-templates>
					</xsl:with-param>
					<xsl:with-param name="body">
						<xsl:apply-templates select="." mode="elements">
							<xsl:with-param name="chapterList" select="$chapterList"/>
							<xsl:with-param name="documentNodes" select="$nodes"/>
						</xsl:apply-templates>
					</xsl:with-param>
				</xsl:call-template>

			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="xs:schema" mode="navigation">
		<xsl:param name="chapterList"/>
		<xsl:param name="documentNodes"/>

		<xsl:variable name="hasNext">
			<xsl:call-template name="x1f:List.hasNext">
				<xsl:with-param name="list" select="$chapterList"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="$hasNext = 'true'">
			<xsl:variable name="chapterListWithNextIndex">
				<xsl:call-template name="x1f:List.nextIndex">
					<xsl:with-param name="list" select="$chapterList"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="value_upperCase">
				<xsl:call-template name="x1f:List.getValue">
					<xsl:with-param name="list" select="$chapterListWithNextIndex"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="value_lowerCase">
				<xsl:call-template name="x1f:toLowerCase">
					<xsl:with-param name="string" select="$value_upperCase"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:call-template name="x1f:createNavigationEntry">
				<xsl:with-param name="content">
					<a href="{concat('#chapter_',$value_upperCase)}">
						<xsl:value-of select="$value_upperCase"/>
					</a>
					<xsl:call-template name="x1f:createNavigation">
						<xsl:with-param name="content">
							<xsl:apply-templates select="$documentNodes/xs:element[not(@abstract = 'true') and (starts-with(@name,$value_upperCase) or starts-with(@name,$value_lowerCase))]" mode="navigation">
								<xsl:sort select="@name"/>
							</xsl:apply-templates>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:apply-templates select="." mode="navigation">
				<xsl:with-param name="chapterList" select="$chapterListWithNextIndex"/>
				<xsl:with-param name="documentNodes" select="$documentNodes"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>

	<xsl:template match="xs:element[@name]" mode="navigation">
		<xsl:param name="prefix"/>
		<xsl:variable name="id">
			<xsl:call-template name="x1f:createID">
				<xsl:with-param name="prefix" select="translate($prefix,' ','')"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:call-template name="x1f:createNavigationEntry">
			<xsl:with-param name="content">
				<a href="{concat('#',$id)}">
					<xsl:value-of select="@name"/>
				</a>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
