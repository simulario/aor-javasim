<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:x1f="http://www.informatik.tu-cottbus.de/~tgrundm1/projects/xslt-framework/xslt-1.0/" version="1.0" exclude-result-prefixes="xs xsl x1f">

	<xsl:import href="simpleTypes.xsl"/>
	<xsl:import href="annotations.xsl"/>

	<!--#######################-->
	<!--### mode:attributes ###-->
	<!--#######################-->

	<!-- xs:element -->

	<xsl:template match="xs:element" mode="attributes">
		<xsl:variable name="requiredAttributes">
			<xsl:choose>
				<xsl:when test="@type">
					<xsl:apply-templates select="/xs:schema/xs:complexType[@name = current()/@type or @name = substring-after(current()/@type,':')]" mode="attributes">
						<xsl:with-param name="use" select="'required'"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="xs:complexType" mode="attributes">
						<xsl:with-param name="use" select="'required'"/>
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="optionalAttributes">
			<xsl:choose>
				<xsl:when test="@type">
					<xsl:apply-templates select="/xs:schema/xs:complexType[@name = current()/@type or @name = substring-after(current()/@type,':')]" mode="attributes"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="xs:complexType" mode="attributes"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="normalize-space($requiredAttributes) != '' or normalize-space($optionalAttributes) != ''">
			<xsl:call-template name="x1f:createSection">
				<xsl:with-param name="class" select="'attributes'"/>
				<xsl:with-param name="heading" select="'Attributes'"/>
				<xsl:with-param name="headingElement" select="$x1f:section2Heading"/>
				<xsl:with-param name="body">
					<xsl:if test="normalize-space($requiredAttributes) != ''">
						<table class="required">
							<caption>required attributes</caption>
							<col width="25%"/>
							<col width="75%"/>
							<thead>
								<tr>
									<th scope="col">Required Attribute</th>
									<th scope="col">Documentation / Facetts</th>
								</tr>
							</thead>
							<tbody>
								<xsl:copy-of select="$requiredAttributes"/>
							</tbody>
						</table>
					</xsl:if>
					<xsl:if test="normalize-space($optionalAttributes) != ''">
						<table class="optional">
							<caption>optional attributes</caption>
							<col width="25%"/>
							<col width="75%"/>
							<thead>
								<tr>
									<th scope="col">Optional Attribute</th>
									<th scope="col">Documentation / Facetts</th>
								</tr>
							</thead>
							<tbody>
								<xsl:copy-of select="$optionalAttributes"/>
							</tbody>
						</table>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- xs:complexType -->

	<xsl:template match="xs:complexType" mode="attributes">
		<xsl:param name="use" select="'optional'"/>
		<xsl:param name="restrictedAttributes"/>
		<xsl:apply-templates select="xs:complexContent | xs:simpleContent | xs:attributeGroup | xs:attribute" mode="attributes">
			<xsl:with-param name="use" select="$use"/>
			<xsl:with-param name="restrictedAttributes" select="$restrictedAttributes"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- xs:complexContent -->

	<xsl:template match="xs:complexContent" mode="attributes">
		<xsl:param name="use"/>
		<xsl:param name="restrictedAttributes"/>
		<xsl:apply-templates select="xs:extension | xs:restriction" mode="attributes">
			<xsl:with-param name="use" select="$use"/>
			<xsl:with-param name="restrictedAttributes" select="$restrictedAttributes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="xs:complexContent/xs:extension" mode="attributes">
		<xsl:param name="use"/>
		<xsl:param name="restrictedAttributes"/>
		<xsl:apply-templates select="/xs:schema/xs:complexType[@name = current()/@base or @name = substring-after(current()/@base,':')] | xs:attributeGroup | xs:attribute" mode="attributes">
			<xsl:with-param name="use" select="$use"/>
			<xsl:with-param name="restrictedAttributes" select="$restrictedAttributes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="xs:complexContent/xs:restriction" mode="attributes">
		<xsl:param name="use"/>
		<xsl:param name="restrictedAttributes">
			<xsl:call-template name="x1f:List.createEmptyList">
				<xsl:with-param name="duplicate-free" select="true()"/>
			</xsl:call-template>
		</xsl:param>
		<xsl:variable name="newRestrictedAttributes">
			<xsl:choose>
				<xsl:when test="string-length(normalize-space($restrictedAttributes)) = 0">
					<xsl:call-template name="x1f:List.createEmptyList">
						<xsl:with-param name="duplicate-free" select="true()"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$restrictedAttributes"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:apply-templates select="xs:attributeGroup | xs:attribute" mode="attributes">
			<xsl:with-param name="use" select="$use"/>
			<xsl:with-param name="restrictedAttributes" select="$newRestrictedAttributes"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="/xs:schema/xs:complexType[@name = current()/@base or @name = substring-after(current()/@base,':')]" mode="attributes">
			<xsl:with-param name="use" select="$use"/>
			<xsl:with-param name="restrictedAttributes">
				<xsl:call-template name="x1f:List.appendEntries">
					<xsl:with-param name="list" select="$newRestrictedAttributes"/>
					<xsl:with-param name="entries">
						<xsl:apply-templates select="xs:attribute|xs:attributeGroup" mode="restrictedAttributes"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<!-- xs:simpleContent -->

	<xsl:template match="xs:simpleContent" mode="attributes">
		<xsl:param name="use"/>
		<xsl:param name="restrictedAttributes"/>
		<xsl:apply-templates select="xs:extension | xs:restriction" mode="attributes">
			<xsl:with-param name="use" select="$use"/>
			<xsl:with-param name="restrictedAttributes" select="$restrictedAttributes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="xs:simpleContent/xs:extension" mode="attributes">
		<xsl:param name="use"/>
		<xsl:param name="restrictedAttributes"/>
		<xsl:apply-templates select="/xs:schema/xs:simpleType[@name = current()/@base or @name = substring-after(current()/@base,':')] | /xs:schema/xs:complexType[@name = current()/@base or @name = substring-after(current()/@base,':')] | xs:attributeGroup | xs:attribute" mode="attributes">
			<xsl:with-param name="use" select="$use"/>
			<xsl:with-param name="restrictedAttributes" select="$restrictedAttributes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="xs:simpleContent/xs:restriction" mode="attributes">
		<xsl:param name="use"/>
		<xsl:param name="restrictedAttributes">
			<xsl:call-template name="x1f:List.createEmptyList">
				<xsl:with-param name="duplicate-free" select="true()"/>
			</xsl:call-template>
		</xsl:param>
		<xsl:variable name="newRestrictedAttributes">
			<xsl:choose>
				<xsl:when test="string-length(normalize-space($restrictedAttributes)) = 0">
					<xsl:call-template name="x1f:List.createEmptyList">
						<xsl:with-param name="duplicate-free" select="true()"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$restrictedAttributes"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:apply-templates select="xs:attributeGroup | xs:attribute" mode="attributes">
			<xsl:with-param name="use" select="$use"/>
			<xsl:with-param name="restrictedAttributes" select="$newRestrictedAttributes"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="/xs:schema/xs:simpleType[@name = current()/@base or @name = substring-after(current()/@base,':')]" mode="attributes">
			<xsl:with-param name="use" select="$use"/>
			<xsl:with-param name="restrictedAttributes">
				<xsl:call-template name="x1f:List.appendEntries">
					<xsl:with-param name="list" select="$newRestrictedAttributes"/>
					<xsl:with-param name="entries">
						<xsl:apply-templates select="xs:attribute|xs:attributeGroup" mode="restrictedAttributes"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<!-- xs:attribute -->

	<xsl:template match="xs:attribute[@use = 'prohibited']" mode="attributes"/>

	<xsl:template match="xs:attribute[@ref and not(@use = 'prohibited')]" mode="attributes">
		<xsl:param name="use"/>
		<xsl:param name="restrictedAttributes"/>
		<xsl:apply-templates select="/xs:schema/xs:attribute[(@name = current()/@ref or @name = substring-after(current()/@ref,':')) and @use = 'required']" mode="attributes">
		  <xsl:with-param name="use" select="$use"/>
			<xsl:with-param name="restrictedAttributes" select="$restrictedAttributes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="xs:attribute[@name and not(@use = 'prohibited')]" mode="attributes">
		<xsl:param name="use"/>
		<xsl:param name="restrictedAttributes"/>	  
	  <xsl:variable name="restricted">
			<xsl:call-template name="x1f:List.containsValue">
				<xsl:with-param name="list" select="$restrictedAttributes"/>
				<xsl:with-param name="value" select="@name"/>
			</xsl:call-template>
	  </xsl:variable>
	  
	  <xsl:if test="$restricted = 'false'">	    
	    <xsl:if test="@use = $use or (not(@use) and $use = 'optional')">
				<tr>
					<th scope="row">
						<xsl:value-of select="@name"/>
					</th>
					<td>
						<xsl:variable name="type">
							<xsl:choose>
								<xsl:when test="@type and /xs:schema/xs:simpleType[@name = current()/@type or @name = substring-after(current()/@type,':')]">
									<xsl:apply-templates select="/xs:schema/xs:simpleType[@name = current()/@type or @name = substring-after(current()/@type,':')]" mode="attributes"/>
								</xsl:when>
								<xsl:when test="@type">
									<dt>type: </dt>
									<dd>
										<xsl:value-of select="@type"/>
									</dd>
								</xsl:when>
								<xsl:otherwise>
									<xsl:apply-templates select="xs:simpleType" mode="attributes"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:apply-templates select="." mode="annotations"/>
						<xsl:if test="$type != ''">
							<dl>
								<xsl:copy-of select="$type"/>
							</dl>
						</xsl:if>
					</td>
				</tr>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!-- xs:attributeGroup -->

	<xsl:template match="xs:attributeGroup[@ref]" mode="attributes">
		<xsl:param name="use"/>
		<xsl:param name="restrictedAttributes"/>
		<xsl:apply-templates select="/xs:schema/xs:attributeGroup[@name = current()/@ref or @name = substring-after(current()/@ref,':')]" mode="attributes">
		  <xsl:with-param name="use" select="$use"/>
			<xsl:with-param name="restrictedAttributes" select="$restrictedAttributes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="xs:attributeGroup[@name]" mode="attributes">
		<xsl:param name="use"/>
		<xsl:param name="restrictedAttributes"/>
		<xsl:apply-templates select="xs:attributeGroup | xs:attribute" mode="attributes">
		  <xsl:with-param name="use" select="$use"/>
		  <xsl:with-param name="restrictedAttributes" select="$restrictedAttributes"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- xs:simpleType -->

	<xsl:template match="xs:simpleType" mode="attributes">
		<xsl:apply-templates select="." mode="simpleTypes">
			<xsl:with-param name="first" select="true()"/>
		</xsl:apply-templates>
	</xsl:template>

	<!--#################################-->
	<!--### mode:restrictedAttributes ###-->
	<!--#################################-->

	<!-- xs:attribute -->

	<xsl:template match="xs:attribute[@ref]" mode="restrictedAttributes">
		<xsl:apply-templates select="/xs:schema/xs:attribute[@name = current()/@ref or @name = substring-after(current()/@ref,':')]" mode="restrictedAttributes"/>
	</xsl:template>

	<xsl:template match="xs:attribute[@name]" mode="restrictedAttributes">
		<xsl:call-template name="x1f:List.Entry.createEntry">
			<xsl:with-param name="value" select="@name"/>
		</xsl:call-template>
	</xsl:template>

	<!-- xs:attributeGroup -->

	<xsl:template match="xs:attributeGroup[@ref]" mode="restrictedAttributes">
		<xsl:apply-templates select="/xs:schema/xs:attributeGroup[@name = current()/@ref or @name = substring-after(current()/@ref,':')]" mode="restrictedAttributes"/>
	</xsl:template>

	<xsl:template match="xs:attributeGroup[@name]" mode="restrictedAttributes">
		<xsl:apply-templates select="xs:attribute|xs:attributeGroup" mode="restrictedAttributes"/>
	</xsl:template>

</xsl:stylesheet>
