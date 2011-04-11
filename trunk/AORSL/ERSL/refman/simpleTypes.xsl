<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:x1f="http://www.informatik.tu-cottbus.de/~tgrundm1/projects/xslt-framework/xslt-1.0/" version="1.0" exclude-result-prefixes="xs xsl x1f">

	<xsl:import href="XSLT-1.0-Framework/ADTs/List.xsl"/>

	<!--####################-->
	<!--### local macros ###-->
	<!--####################-->

	<xsl:template name="getMemberTypes">
		<xsl:param name="memberTypes"/>		
		<xsl:if test="normalize-space($memberTypes) != ''">		
			<xsl:call-template name="x1f:List.Entry.createEntry">
				<xsl:with-param name="value" select="substring-before($memberTypes,' ')"/>
			</xsl:call-template>
			<xsl:call-template name="getMemberTypes">
				<xsl:with-param name="memberTypes" select="substring-after($memberTypes,' ')"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--########################-->
	<!--### mode:simpleTypes ###-->
	<!--########################-->

	<!-- xs:element -->

	<xsl:template match="xs:element" mode="simpleTypes">
		<xsl:param name="documentNodes"/>
		<xsl:variable name="simpleType">
			<xsl:choose>
				<xsl:when test="@type and $documentNodes/xs:simpleType[@name = current()/@type or @name = substring-after(current()/@type,':')]">
					<xsl:apply-templates select="$documentNodes/xs:simpleType[@name = current()/@type or @name = substring-after(current()/@type,':')]" mode="simpleTypes">
						<xsl:with-param name="first" select="true()"/>
						<xsl:with-param name="documentNodes" select="$documentNodes"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:when test="@type and $documentNodes/xs:complexType[@name = current()/@type or @name = substring-after(current()/@type,':')]">
					<xsl:apply-templates select="$documentNodes/xs:complexType[@name = current()/@type or @name = substring-after(current()/@type,':')]" mode="simpleTypes">
						<xsl:with-param name="first" select="true()"/>
						<xsl:with-param name="documentNodes" select="$documentNodes"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:when test="xs:simpleType">
					<xsl:apply-templates select="xs:simpleType" mode="simpleTypes">
						<xsl:with-param name="first" select="true()"/>
						<xsl:with-param name="documentNodes" select="$documentNodes"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:when test="xs:complexType">
					<xsl:apply-templates select="xs:complexType" mode="simpleTypes">
						<xsl:with-param name="first" select="true()"/>
						<xsl:with-param name="documentNodes" select="$documentNodes"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<dt>type: </dt>
					<dd>
						<xsl:value-of select="@type"/>
					</dd>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="normalize-space($simpleType) != ''">
			<dl>
				<xsl:copy-of select="$simpleType"/>
			</dl>
		</xsl:if>
	</xsl:template>

	<!-- xs:complexType -->

	<xsl:template match="xs:complexType" mode="simpleTypes">
		<xsl:param name="first" select="false()"/>
		<xsl:param name="documentNodes"/>
		<xsl:apply-templates select="xs:simpleContent" mode="simpleTypes">
			<xsl:with-param name="first" select="$first"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- xs:simpleContent -->

	<xsl:template match="xs:simpleContent" mode="simpleTypes">
		<xsl:param name="first"/>
		<xsl:param name="documentNodes"/>
		<xsl:apply-templates select="xs:extension | xs:restriction" mode="simpleTypes">
			<xsl:with-param name="first" select="$first"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="xs:simpleContent/xs:extension" mode="simpleTypes">
		<xsl:param name="first"/>
		<xsl:param name="documentNodes"/>
		<xsl:choose>
			<xsl:when test="$documentNodes/xs:simpleType[@name = current()/@base or @name = substring-after(current()/@base,':')]">
				<xsl:apply-templates select="$documentNodes/xs:simpleType[@name = current()/@base or @name = substring-after(current()/@base,':')]" mode="simpleTypes">
					<xsl:with-param name="first" select="$first"/>
					<xsl:with-param name="documentNodes" select="$documentNodes"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="$documentNodes/xs:complexType[@name = current()/@base or @name = substring-after(current()/@base,':')]">
				<xsl:apply-templates select="$documentNodes/xs:complexType[@name = current()/@base or @name = substring-after(current()/@base,':')]" mode="simpleTypes">
					<xsl:with-param name="first" select="$first"/>
					<xsl:with-param name="documentNodes" select="$documentNodes"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<dt>type: </dt>
				<dd>
					<xsl:value-of select="@base"/>
				</dd>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="xs:simpleContent/xs:restriction" mode="simpleTypes">
		<xsl:param name="first"/>
		<xsl:param name="documentNodes"/>
		<xsl:choose>
			<xsl:when test="@base and $documentNodes/xs:simpleType[@name = current()/@base or @name = substring-after(current()/@base,':')]">
				<xsl:apply-templates select="$documentNodes/xs:simpleType[@name = current()/@base or @name = substring-after(current()/@base,':')]">
					<xsl:with-param name="documentNodes" select="$documentNodes"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="xs:simpleType">
				<xsl:apply-templates select="xs:simpleType" mode="simpleTypes">
					<xsl:with-param name="documentNodes" select="$documentNodes"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="@base and $documentNodes/xs:complexType[@name = current()/@base or @name = substring-after(current()/@base,':')]">
				<xsl:apply-templates select="$documentNodes/xs:complexType[@name = current()/@base or @name = substring-after(current()/@base,':')]">
					<xsl:with-param name="documentNodes" select="$documentNodes"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<dt>type: </dt>
				<dd>
					<xsl:value-of select="@base"/>
				</dd>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="$first = 'true'">
			<xsl:apply-templates select="." mode="facetts">
				<xsl:with-param name="documentNodes" select="$documentNodes"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>

	<!-- xs:simpleType -->

	<xsl:template match="xs:simpleType" mode="simpleTypes">
		<xsl:param name="first" select="false()"/>
		<xsl:param name="prefix"/>
		<xsl:param name="documentNodes">
		</xsl:param>
		<xsl:apply-templates select="xs:restriction | xs:list | xs:union" mode="simpleTypes">
			<xsl:with-param name="first" select="$first"/>
			<xsl:with-param name="prefix" select="$prefix"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- xs:restriction -->

	<xsl:template match="xs:simpleType/xs:restriction" mode="simpleTypes">
		<xsl:param name="first"/>
		<xsl:param name="prefix"/>
		<xsl:param name="documentNodes"/>

		<xsl:choose>
			<xsl:when test="@base and $documentNodes/xs:simpleType[@name = current()/@base or @name = substring-after(current()/@base,':')]">
				<xsl:apply-templates select="$documentNodes/xs:simpleType[@name = current()/@base or @name = substring-after(current()/@base,':')]" mode="simpleTypes">
					<xsl:with-param name="prefix" select="$prefix"/>
					<xsl:with-param name="documentNodes" select="$documentNodes"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="@base">
				<dt>type: </dt>
				<dd>
					<xsl:value-of select="concat($prefix,@base)"/>
				</dd>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="xs:simpleType" mode="simpleTypes">
					<xsl:with-param name="prefix" select="$prefix"/>
					<xsl:with-param name="documentNodes" select="$documentNodes"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="$first = 'true'">
			<xsl:apply-templates select="." mode="facetts">
				<xsl:with-param name="documentNodes" select="$documentNodes"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>

	<!-- xs:list -->

	<xsl:template match="xs:list" mode="simpleTypes">
		<xsl:param name="prefix"/>
		<xsl:param name="documentNodes"/>
		<xsl:variable name="newPrefix" select="concat($prefix,'list of ')"/>
		<xsl:choose>
			<xsl:when test="@itemType and $documentNodes/xs:simpleType[@name = current()/@itemType or @name = substring-after(current()/@itemType,':')]">
				<xsl:apply-templates select="$documentNodes/xs:simpleType[@name = current()/@itemType or @name = substring-after(current()/@itemType,':')]" mode="simpleTypes">
					<xsl:with-param name="first" select="true()"/>
					<xsl:with-param name="prefix" select="$newPrefix"/>
					<xsl:with-param name="documentNodes" select="$documentNodes"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="@itemType">
				<dt>type: </dt>
				<dd>
					<xsl:value-of select="concat($newPrefix,@itemType)"/>
				</dd>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="xs:simpleType" mode="simpleTypes">
					<xsl:with-param name="first" select="true()"/>
					<xsl:with-param name="prefix" select="$newPrefix"/>
					<xsl:with-param name="documentNodes" select="$documentNodes"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- xs:union -->
	
	<xsl:template match="xs:union" mode="simpleTypes">
		<xsl:param name="prefix"/>
		<xsl:param name="documentNodes"/>
		<xsl:variable name="memberTypesList">
			<xsl:call-template name="x1f:List.appendEntries">
				<xsl:with-param name="list">
					<xsl:call-template name="x1f:List.createEmptyList">
						<xsl:with-param name="duplicate-free" select="true()"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="entries">
					<xsl:call-template name="getMemberTypes">
						<xsl:with-param name="memberTypes" select="concat(normalize-space(@memberTypes),' ')"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:variable name="isEmpty">
			<xsl:call-template name="x1f:List.isEmpty">
				<xsl:with-param name="list" select="$memberTypesList"/>
			</xsl:call-template>
		</xsl:variable>
		
		<dt>type: </dt>
		<dd>
			<xsl:value-of select="concat($prefix, 'union of')"/>
			<xsl:if test="$isEmpty = 'false' or xs:simpleType">
				<ul>
					<xsl:apply-templates select="." mode="union">
						<xsl:with-param name="documentNodes" select="$documentNodes"/>
						<xsl:with-param name="memberTypesList" select="$memberTypesList"/>
					</xsl:apply-templates>
				</ul>
			</xsl:if>
		</dd>
	</xsl:template>
	
	<xsl:template match="xs:union" mode="union">
		<xsl:param name="documentNodes"/>
		<xsl:param name="memberTypesList"/>
		
		<xsl:variable name="hasNext">
			<xsl:call-template name="x1f:List.hasNext">
				<xsl:with-param name="list" select="$memberTypesList"/>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="$hasNext = 'true'">
				<xsl:variable name="memberTypesListWithNextIndex">
					<xsl:call-template name="x1f:List.nextIndex">
						<xsl:with-param name="list" select="$memberTypesList"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="memberType">
					<xsl:call-template name="x1f:List.getValue">
						<xsl:with-param name="list" select="$memberTypesListWithNextIndex"/>
					</xsl:call-template>
				</xsl:variable>
				
				<xsl:choose>
					<xsl:when test="$documentNodes/xs:simpleType[@name = $memberType or @name = substring-after($memberType,':')]">
						<xsl:apply-templates select="$documentNodes/xs:simpleType[@name = $memberType or @name = substring-after($memberType,':')]" mode="union">
							<xsl:with-param name="documentNodes" select="$documentNodes"/>
						</xsl:apply-templates>
					</xsl:when>
					<xsl:otherwise>
						<li>
							<dt>type: </dt>
							<dd>
								<xsl:value-of select="$memberType"/>
							</dd>
						</li>
					</xsl:otherwise>
				</xsl:choose>
				
				<xsl:apply-templates select="." mode="union">
					<xsl:with-param name="documentNodes" select="$documentNodes"/>
					<xsl:with-param name="memberTypesList" select="$memberTypesListWithNextIndex"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="xs:simpleType" mode="union">
					<xsl:with-param name="documentNodes" select="$documentNodes"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>		
	</xsl:template>
	
	<xsl:template match="xs:simpleType" mode="union">
		<xsl:param name="documentNodes"/>		
		<li>
			<xsl:apply-templates select="." mode="simpleTypes">
				<xsl:with-param name="first" select="true()"/>
				<xsl:with-param name="prefix" select="''"/>
				<xsl:with-param name="documentNodes" select="$documentNodes"/>
			</xsl:apply-templates>
		</li>
	</xsl:template>

	<!--#####################-->
	<!--### mode:facettes ###-->
	<!--#####################-->

	<!-- xs:simpleType -->

	<xsl:template match="xs:simpleType" mode="facetts">
		<xsl:param name="usedFacetts"/>
		<xsl:param name="documentNodes"/>
		<xsl:apply-templates select="xs:restriction" mode="facetts">
			<xsl:with-param name="usedFacetts" select="$usedFacetts"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- xs:restriction -->

	<xsl:template match="xs:simpleType/xs:restriction" mode="facetts">
		<xsl:param name="usedFacetts">
			<xsl:call-template name="x1f:List.createEmptyList">
				<xsl:with-param name="duplicate-free" select="true()"/>
			</xsl:call-template>
		</xsl:param>
		<xsl:param name="documentNodes"/>
		<xsl:variable name="newUsedFacetts">
			<xsl:call-template name="x1f:List.appendEntries">
				<xsl:with-param name="list" select="$usedFacetts"/>
				<xsl:with-param name="entries">
					<xsl:for-each select="xs:*[local-name() != 'annoation' and local-name() != 'simpleType']">
						<xsl:call-template name="x1f:List.Entry.createEntry">
							<xsl:with-param name="value" select="local-name()"/>
						</xsl:call-template>
					</xsl:for-each>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="@base and $documentNodes/xs:simpleType[@name = current()/@base or @name = substring-after(current()/@base,':')]">
				<xsl:apply-templates select="$documentNodes/xs:simpleType[@name = current()/@base or @name = substring-after(current()/@base,':')]" mode="facetts">
					<xsl:with-param name="usedFacetts" select="$newUsedFacetts"/>
					<xsl:with-param name="documentNodes" select="$documentNodes"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="xs:simpleType" mode="facetts">
					<xsl:with-param name="usedFacetts" select="$newUsedFacetts"/>
					<xsl:with-param name="documentNodes" select="$documentNodes"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates select="xs:minInclusive | xs:maxInclusive | xs:minExclusive | xs:maxExclusive | xs:totalDigits | xs:fractionDigits | xs:length | xs:minLength | xs:maxLength | xs:whiteSpace | xs:enumeration[count(preceding-sibling::xs:enumeration) = 0] | xs:pattern[count(preceding-sibling::xs:pattern) = 0]" mode="facetts">
			<xsl:with-param name="usedFacetts" select="$usedFacetts"/>
			<xsl:with-param name="baseType" select="$documentNodes/xs:simpleType[@name = current()/@base or @name = substring-after(current()/@base,':')] | xs:simpleType"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- xs:minInclusive | xs:maxInclusive | xs:minExclusive | xs:maxExclusive | xs:totalDigits | xs:fractionDigits| xs:length | xs:minLength | xs:maxLength | xs:whitespace -->

	<xsl:template match="xs:minInclusive | xs:maxInclusive | xs:minExclusive | xs:maxExclusive | xs:totalDigits | xs:fractionDigits| xs:length | xs:minLength | xs:maxLength | xs:whitespace" mode="facetts">
		<xsl:param name="usedFacetts"/>
		<xsl:param name="documentNodes"/>
		<xsl:variable name="alreadyUsed">
			<xsl:call-template name="x1f:List.containsValue">
				<xsl:with-param name="list" select="$usedFacetts"/>
				<xsl:with-param name="value" select="local-name()"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:if test="$alreadyUsed = 'false'">
			<dt>
				<xsl:value-of select="concat(local-name(),': ')"/>
			</dt>
			<dd>
				<xsl:value-of select="@value"/>
			</dd>
		</xsl:if>
	</xsl:template>

	<!-- xs:enumeration -->

	<xsl:template match="xs:enumeration[count(preceding-sibling::xs:enumeration) = 0]" mode="facetts">
		<xsl:param name="usedFacetts"/>
		<xsl:param name="documentNodes"/>
		<xsl:variable name="alreadyUsed">
			<xsl:call-template name="x1f:List.containsValue">
				<xsl:with-param name="list" select="$usedFacetts"/>
				<xsl:with-param name="value" select="local-name()"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:if test="$alreadyUsed = 'false'">
			<dt>
				<xsl:value-of select="concat(local-name(),': ')"/>
			</dt>
			<dd>
				<xsl:value-of select="@value"/>
				<xsl:apply-templates select="following-sibling::xs:enumeration" mode="facetts">
					<xsl:with-param name="documentNodes" select="$documentNodes"/>
				</xsl:apply-templates>
			</dd>
		</xsl:if>
	</xsl:template>

	<xsl:template match="xs:enumeration[count(preceding-sibling::xs:enumeration) > 0]" mode="facetts">
		<xsl:value-of select="concat(' | ',@value)"/>
	</xsl:template>

	<!-- xs:pattern -->

	<xsl:template match="xs:pattern[count(preceding-sibling::xs:pattern) = 0]" mode="facetts">
		<xsl:param name="usedFacetts"/>
		<xsl:param name="baseType"/>
		<xsl:param name="documentNodes"/>
		<xsl:variable name="alreadyUsed">
			<xsl:call-template name="x1f:List.containsValue">
				<xsl:with-param name="list" select="$usedFacetts"/>
				<xsl:with-param name="value" select="local-name()"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:if test="$alreadyUsed = 'false'">
			<dt>
				<xsl:value-of select="concat(local-name(),': ')"/>
			</dt>
			<dd>
				<xsl:apply-templates select="." mode="pattern">
					<xsl:with-param name="documentNodes" select="$documentNodes"/>
				</xsl:apply-templates>
				<xsl:if test="$baseType">
					<xsl:apply-templates select="$baseType" mode="pattern">
						<xsl:with-param name="documentNodes" select="$documentNodes"/>
					</xsl:apply-templates>
				</xsl:if>
			</dd>
		</xsl:if>
	</xsl:template>

	<!--####################-->
	<!--### mode:pattern ###-->
	<!--####################-->

	<!-- xs:simpleType -->

	<xsl:template match="xs:simpleType" mode="pattern">
		<xsl:param name="documentNodes"/>
		<xsl:apply-templates select="xs:restriction" mode="pattern">
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- xs:restriction -->

	<xsl:template match="xs:simpleType/xs:restriction" mode="pattern">
		<xsl:param name="documentNodes"/>
		<xsl:apply-templates select="xs:pattern[count(preceding-sibling::xs:pattern) = 0]" mode="pattern">
			<xsl:with-param name="prefix" select="' &amp; '"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
		<xsl:if test="@base or xs:simpleType">
			<xsl:apply-templates select="$documentNodes/xs:simpleType[@name = current()/@base or @name = substring-after(current()/@base,':')] | xs:simpleType" mode="pattern">
				<xsl:with-param name="documentNodes" select="$documentNodes"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>

	<!-- xs:pattern -->

	<xsl:template match="xs:pattern[count(preceding-sibling::xs:pattern) = 0]" mode="pattern">
		<xsl:param name="documentNodes"/>
		<xsl:param name="prefix"/>
		<xsl:value-of select="concat($prefix,'((',@value,')')"/>
		<xsl:apply-templates select="following-sibling::xs:pattern" mode="pattern">
			<xsl:with-param name="prefix" select="' || '"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
		<xsl:text>)</xsl:text>
	</xsl:template>

	<xsl:template match="xs:pattern[count(preceding-sibling::xs:pattern) > 0]" mode="pattern">
		<xsl:param name="prefix"/>
		<xsl:value-of select="concat($prefix,'(',@value,')')"/>
	</xsl:template>
</xsl:stylesheet>
