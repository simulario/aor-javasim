<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:x1f="http://www.informatik.tu-cottbus.de/~tgrundm1/projects/xslt-framework/xslt-1.0/" version="1.0" exclude-result-prefixes="xs xsl x1f">

	<xsl:import href="XSLT-1.0-Framework/createXHTMLDocument.xsl"/>

	<!--####################-->
	<!--### local macros ###-->
	<!--####################-->

	<!--
        This template returns the annotations of an union's member types, that are given by a list.
        @param  memberTypes - The space-seperated list of member types.
        @param  level       - The type level for the member types.
        @return The member types' annotations.
    -->
	<xsl:template name="getMemberTypesAnnotations">
		<xsl:param name="memberTypes"/>
		<xsl:param name="level"/>
		<xsl:variable name="currentType" select="substring-before($memberTypes,' ')"/>
		<xsl:variable name="restTypes" select="substring-after($memberTypes,' ')"/>
		<xsl:if test="normalize-space($currentType) != ''">
			<xsl:apply-templates select="/xs:schema/xs:simpleType[@name = $currentType or @name = substring-after($currentType,':')]" mode="annotations">
				<xsl:with-param name="prefix" select="'membertype'"/>
				<xsl:with-param name="level" select="$level"/>
			</xsl:apply-templates>
			<xsl:if test="normalize-space($restTypes) != ''">
				<xsl:call-template name="getMemberTypesAnnotations">
					<xsl:with-param name="memberTypes" select="$restTypes"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!--
        This template creates the prefix for a type based on its derivation level.
        @param  level  - The derivation level.
        @return The prefix.
    -->
	<xsl:template name="getTypePrefix">
		<xsl:param name="prefix" select="'type'"/>
		<xsl:param name="level"/>
		<xsl:value-of select="$prefix"/>
		<xsl:if test="$level > 1">
			<xsl:value-of select="concat(' ',$level)"/>
		</xsl:if>
	</xsl:template>

	<!--####################-->
	<!--### xs:annoation ###-->
	<!--####################-->

	<xsl:template match="xs:annotation" mode="annotations">
		<xsl:param name="prefix"/>
		<xsl:apply-templates select="xs:documentation" mode="annotations">
			<xsl:with-param name="prefix" select="$prefix"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="xs:documentation" mode="annotations">
		<xsl:param name="prefix"/>
		<xsl:if test="*[namespace-uri() = 'http://www.w3.org/1999/xhtml'] or normalize-space(text()) != ''">
			<li>
				<xsl:if test="normalize-space($prefix) != ''">
					<xsl:value-of select="concat('[',$prefix,'] ')"/>
				</xsl:if>
				<xsl:apply-templates select="*[namespace-uri() = 'http://www.w3.org/1999/xhtml'] | text()" mode="annonations"/>
			</li>
		</xsl:if>
	</xsl:template>

	<!--#################-->
	<!--### xs:schema ###-->
	<!--#################-->

	<xsl:template match="xs:schema" mode="annotations">
		<xsl:variable name="annotations">
			<xsl:apply-templates select="xs:annotation" mode="annotations"/>
		</xsl:variable>
		<xsl:if test="normalize-space($annotations) != ''">
			<xsl:element name="ul">
				<xsl:copy-of select="$annotations"/>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<!--##################-->
	<!--### xs:element ###-->
	<!--##################-->

	<xsl:template match="xs:element[@name]" mode="annotations">
		<xsl:variable name="annotations">
			<xsl:apply-templates select="xs:annotation" mode="annotations"/>
			<xsl:choose>
				<xsl:when test="@type">
					<xsl:apply-templates select="/xs:schema/xs:complexType[@name = current()/@name or @name = substring-after(current()/@name,':')] | /xs:schema/xs:simpleType[@name = current()/@name or @name = substring-after(current()/@name,':')]" mode="annotations"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="xs:complexType | xs:simpleType" mode="annotations"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="$annotations != ''">
			<ul>
				<xsl:copy-of select="$annotations"/>
			</ul>
		</xsl:if>
	</xsl:template>

	<!--####################-->
	<!--### xs:attribute ###-->
	<!--####################-->

	<xsl:template match="xs:attribute[@name]" mode="annotations">
		<xsl:variable name="annotations">
			<xsl:apply-templates select="xs:annotation" mode="annotations"/>
			<xsl:choose>
				<xsl:when test="@type">
					<xsl:apply-templates select="/xs:schema/xs:simpleType[@name = current()/@type or @name = substring-after(current()/@type,':')]" mode="annotations"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="xs:simpleType" mode="annotations"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="normalize-space($annotations) != ''">
			<ul>
				<xsl:copy-of select="$annotations"/>
			</ul>
		</xsl:if>
	</xsl:template>

	<!--######################-->
	<!--### xs:complexType ###-->
	<!--######################-->

	<xsl:template match="xs:complexType" mode="annotations">
		<xsl:param name="level" select="1"/>
		<xsl:apply-templates select="xs:annoation" mode="annotations">
			<xsl:with-param name="prefix">
				<xsl:call-template name="getTypePrefix">
					<xsl:with-param name="level" select="$level"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:apply-templates>
		<xsl:apply-templates select="xs:complexContent | xs:simpleContent" mode="annotations">
			<xsl:with-param name="level" select="$level"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- xs:complexContent -->

	<xsl:template match="xs:complexContent" mode="annotations">
		<xsl:param name="level"/>
		<xsl:apply-templates select="xs:annoation" mode="annotations">
			<xsl:with-param name="prefix">
				<xsl:call-template name="getTypePrefix">
					<xsl:with-param name="level" select="$level"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:apply-templates>
		<xsl:apply-templates select="xs:extension | xs:restriction" mode="annotations">
			<xsl:with-param name="level" select="$level"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="xs:complexContent/xs:extension | xs:complexContent/xs:restriction" mode="annotations">
		<xsl:param name="level"/>
		<xsl:apply-templates select="xs:annoation" mode="annotations">
			<xsl:with-param name="prefix">
				<xsl:call-template name="getTypePrefix">
					<xsl:with-param name="level" select="$level"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:apply-templates>
		<xsl:apply-templates select="/xs:schema/xs:complexType[@name = current()/@base or @name = substring-after(current()/@base,':')]" mode="annotations">
			<xsl:with-param name="level" select="$level + 1"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- xs:simpleContent -->

	<xsl:template match="xs:simpleContent" mode="annotations">
		<xsl:param name="level"/>
		<xsl:apply-templates select="xs:annoation" mode="annotations">
			<xsl:with-param name="prefix">
				<xsl:call-template name="getTypePrefix">
					<xsl:with-param name="level" select="$level"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:apply-templates>
		<xsl:apply-templates select="xs:extension | xs:restriction" mode="annotations">
			<xsl:with-param name="level" select="$level"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="xs:simpleContent/xs:extension | xs:simpleContent/xs:restriction" mode="annotations">
		<xsl:param name="level"/>
		<xsl:apply-templates select="xs:annoation" mode="annotations">
			<xsl:with-param name="prefix">
				<xsl:call-template name="getTypePrefix">
					<xsl:with-param name="level" select="$level"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:apply-templates>
		<xsl:apply-templates select="/xs:schema/xs:simpleType[@name = current()/@base or @name = substring-after(current()/@base,':')]" mode="annotations">
			<xsl:with-param name="level" select="$level + 1"/>
		</xsl:apply-templates>
	</xsl:template>

	<!--#####################-->
	<!--### xs:simpleType ###-->
	<!--#####################-->

	<xsl:template match="xs:simpleType" mode="annotations">
		<xsl:param name="level" select="1"/>
		<xsl:param name="prefix"/>
		<xsl:apply-templates select="xs:annotation" mode="annotations">
			<xsl:with-param name="prefix">
				<xsl:choose>
					<xsl:when test="normalize-space($prefix) != ''">
						<xsl:call-template name="getTypePrefix">
							<xsl:with-param name="prefix" select="$prefix"/>
							<xsl:with-param name="level" select="$level"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="getTypePrefix">
							<xsl:with-param name="level" select="$level"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:apply-templates>
		<xsl:apply-templates select="xs:list | xs:union | xs:restriction" mode="annotations">
			<xsl:with-param name="level" select="$level"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- xs:list -->

	<xsl:template match="xs:list" mode="annotations">
		<xsl:param name="level"/>
		<xsl:apply-templates select="xs:annotation">
			<xsl:with-param name="prefix" select="'list'"/>
		</xsl:apply-templates>
		<xsl:choose>
			<xsl:when test="@itemType">
				<xsl:apply-templates select="/xs:schema/xs:simpleType[@name = current()/@itemType or @name = substring-after(current()/@itemType,':')]" mode="annotations">
					<xsl:with-param name="prefix" select="'listtype'"/>
					<xsl:with-param name="level" select="$level + 1"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="xs:simpleType" mode="annotations">
					<xsl:with-param name="prefix" select="'listtype'"/>
					<xsl:with-param name="level" select="$level + 1"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- xs:union -->

	<xsl:template match="xs:union" mode="annotations">
		<xsl:param name="level"/>
		<xsl:apply-templates select="xs:annotation">
			<xsl:with-param name="prefix" select="'union'"/>
		</xsl:apply-templates>
		<xsl:call-template name="getMemberTypesAnnotations">
			<xsl:with-param name="memberTypes" select="concat(normalize-space(@memberTypes),' ')"/>
			<xsl:with-param name="level" select="$level + 1"/>
		</xsl:call-template>
		<xsl:apply-templates select="xs:simpleType" mode="annotations">
			<xsl:with-param name="prefix" select="'membertype'"/>
			<xsl:with-param name="level" select="$level + 1"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- xs:restriction -->

	<xsl:template match="xs:simpleType/xs:restriction" mode="annotations">
		<xsl:param name="level"/>
		<xsl:apply-templates select="xs:annotation" mode="annotations">
			<xsl:with-param name="prefix" select="'restriction'"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="/xs:schema/xs:simpleType[@name = current()/@base or @name = substring-after(current()/@base,':')]" mode="annonations">
			<xsl:with-param name="level" select="$level + 1"/>
		</xsl:apply-templates>
		<xsl:apply-templates mode="annotations"/>
	</xsl:template>

	<xsl:template match="xs:minInclusive|xs:maxInclusive|xs:minExclusive|xs:maxExclusive|xs:totalDigits|xs:fractionDigits|xs:length|xs:minLength|xs:maxLength|xs:enumeration|xs:whitespace|xs:pattern" mode="annotations">
		<xsl:apply-templates select="xs:annotation" mode="annotations">
			<xsl:with-param name="prefix" select="local-name()"/>
		</xsl:apply-templates>
	</xsl:template>

	<!--#######################-->
	<!--### html:* | text() ###-->
	<!--#######################-->

	<xsl:template match="*[namespace-uri() = 'http://www.w3.org/1999/xhtml']" mode="annonations">
		<xsl:choose>
			<xsl:when test="local-name() = 'a' and @href and starts-with(@href,'#')">
				<xsl:variable name="id">
					<xsl:call-template name="x1f:createID">
						<xsl:with-param name="node" select="/xs:schema//xs:*[@id = substring-after(current()/@href,'#')]"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="normalize-space($id) != ''">
						<a href="{concat('#',$id)}">
							<xsl:apply-templates select="*[namespace-uri() = 'http://www.w3.org/1999/xhtml'] | text()" mode="annonations"/>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="*[namespace-uri() = 'http://www.w3.org/1999/xhtml'] | text()" mode="annonations"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="{local-name()}">
					<xsl:copy-of select="@*"/>
					<xsl:apply-templates select="*[namespace-uri() = 'http://www.w3.org/1999/xhtml'] | text()" mode="annonations"/>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="text()" mode="annotations">
		<xsl:copy-of select="."/>
	</xsl:template>
</xsl:stylesheet>
