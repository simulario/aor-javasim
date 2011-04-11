<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" exclude-result-prefixes="xs xsl x1f" xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:x1f="http://www.informatik.tu-cottbus.de/~tgrundm1/projects/xslt-framework/xslt-1.0/">
	
	<xsl:import href="XSLT-1.0-Framework/ADTs/Map.xsl"/>
	<xsl:import href="attributes.xsl"/>
	
	<!--####################-->
	<!--### local macros ###-->
	<!--####################-->
	
	<!-- addSeperator -->
	<xsl:template name="addSeperator">
		<xsl:param name="node" select="."/>
		<xsl:param name="seperator"/>
		<xsl:param name="condition" select="count($node/following-sibling::xs:sequence|$node/following-sibling::xs:choice|$node/following-sibling::xs:all|$node/following-sibling::xs:element|$node/following-sibling::xs:group) &gt; 0"/>
		<xsl:if test="$condition">
			<xsl:copy-of select="$seperator"/>
		</xsl:if>
	</xsl:template>
	
	<!-- getMultiplicities -->
	<xsl:template name="getMultiplicities">
		<xsl:param name="node" select="."/>
		<xsl:choose>
			<xsl:when test="$node/@minOccurs = '0' and $node/@maxOccurs = 'unbounded'">
				<sup>∗</sup>
			</xsl:when>
			<xsl:when test="($node/@minOccurs = '1' or not($node/@minOccurs)) and $node/@maxOccurs = 'unbounded'">
				<sup>+</sup>
			</xsl:when>
			<xsl:when test="$node/@minOccurs = '0' and ($node/@maxOccurs = '1' or not($node/@maxOccurs))">
				<sup>?</sup>
			</xsl:when>
			<xsl:when test="$node/@minOccurs != '1' and $node/@maxOccurs != '1'">
				<sup>
					<xsl:value-of select="concat('{',$node/@minOccurs,',',$node/@maxOccurs,'}')"/>
				</sup>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="getType">
		<xsl:param name="node" select="."/>
		<xsl:choose>
			<xsl:when test="$node/@type">
				<xsl:value-of select="@type"/>
			</xsl:when>
			<xsl:when test="$node/xs:complexType/xs:complexContent/xs:extension">
				<xsl:value-of select="$node/xs:complexType/xs:complexContent/xs:extension/@base"/>
			</xsl:when>
			<xsl:when test="$node/xs:complexType/xs:complexContent/xs:restriction">
				<xsl:value-of select="$node/xs:complexType/xs:complexContent/xs:restriction/@base"/>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	
	<!--#####################-->
	<!--### mode:elements ###-->
	<!--#####################-->
	
	<!-- xs:schema -->
	<xsl:template match="xs:schema" mode="elements">
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

			<xsl:call-template name="x1f:createChapter">
				<xsl:with-param name="id" select="concat('chapter_',$value_upperCase)"/>
				<xsl:with-param name="heading" select="$value_upperCase"/>
				<xsl:with-param name="body">
					<xsl:apply-templates select="$documentNodes/xs:element[not(@abstract = 'true') and (starts-with(@name,$value_upperCase) or starts-with(@name,$value_lowerCase))]" mode="elements">
						<xsl:with-param name="documentNodes" select="$documentNodes"/>
						<xsl:sort select="@name"/>
					</xsl:apply-templates>
				</xsl:with-param>
			</xsl:call-template>

			<xsl:apply-templates select="." mode="elements">
				<xsl:with-param name="chapterList" select="$chapterListWithNextIndex"/>
				<xsl:with-param name="documentNodes" select="$documentNodes"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>
	
	<!-- xs:element -->
	<xsl:template match="xs:element[@name]" mode="elements">
		<xsl:param name="prefix"/>
		<xsl:param name="localTypes">
			<xsl:call-template name="x1f:Map.createEmptyMap"/>
		</xsl:param>
		<xsl:param name="documentNodes"/>
		<xsl:variable name="id">
			<xsl:call-template name="x1f:createID">
				<xsl:with-param name="prefix" select="translate($prefix,'/ ','_')"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="type">
			<xsl:call-template name="getType"/>
		</xsl:variable>
		<xsl:variable name="key">
			<xsl:value-of select="generate-id()"/>
		</xsl:variable>
		<xsl:variable name="updatedLocalTypes">
			<xsl:choose>
				<xsl:when test="normalize-space($type) != ''">
					<xsl:call-template name="x1f:Map.putEntry">
						<xsl:with-param name="map" select="$localTypes"/>
						<xsl:with-param name="key" select="$key"/>
						<xsl:with-param name="value" select="$id"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$localTypes"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="alreadyKnownLocalType">
			<xsl:call-template name="x1f:Map.containsKey">
				<xsl:with-param name="map" select="$localTypes"/>
				<xsl:with-param name="key" select="$key"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:if test="$alreadyKnownLocalType = 'false'">
			<xsl:call-template name="x1f:createSection">
				<xsl:with-param name="id" select="$id"/>
				<xsl:with-param name="class" select="'element'"/>
				<xsl:with-param name="heading" select="concat($prefix,@name)"/>
				<xsl:with-param name="headingElement" select="$x1f:section1Heading"/>
				<xsl:with-param name="body">
					<xsl:apply-templates select="." mode="annotations">
						<xsl:with-param name="documentNodes" select="$documentNodes"/>					
					</xsl:apply-templates>
					<xsl:apply-templates select="." mode="elements.superelements">
						<xsl:with-param name="documentNodes" select="$documentNodes"/>
					</xsl:apply-templates>
					<xsl:apply-templates select="." mode="content">
						<xsl:with-param name="documentNodes" select="$documentNodes"/>
					</xsl:apply-templates>
					<xsl:apply-templates select="." mode="elements.subelements">
						<xsl:with-param name="prefix" select="concat($prefix,@name,' / ')"/>
						<xsl:with-param name="localTypes" select="$updatedLocalTypes"/>
						<xsl:with-param name="documentNodes" select="$documentNodes"/>
					</xsl:apply-templates>
					<xsl:apply-templates select="." mode="attributes">
						<xsl:with-param name="documentNodes" select="$documentNodes"/>
					</xsl:apply-templates>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:choose>
				<xsl:when test="@type">
					<xsl:apply-templates select="$documentNodes/xs:complexType[@name = current()/@type or @name = substring-after(current()/@type,':')]" mode="elements.children">
						<xsl:with-param name="prefix" select="concat($prefix,@name,' / ')"/>
						<xsl:with-param name="parent" select="."/>
						<xsl:with-param name="localTypes" select="$updatedLocalTypes"/>
						<xsl:with-param name="documentNodes" select="$documentNodes"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="xs:complexType" mode="elements.children">
						<xsl:with-param name="prefix" select="concat($prefix,@name,' / ')"/>
						<xsl:with-param name="parent" select="."/>
						<xsl:with-param name="localTypes" select="$updatedLocalTypes"/>
						<xsl:with-param name="documentNodes" select="$documentNodes"/>
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	
	<!--##############################-->
	<!--### mode:elements.children ###-->
	<!--##############################-->
	
	<!-- xs:element -->
	<xsl:template match="xs:element[@name]" mode="elements.children">
		<xsl:param name="prefix"/>
		<xsl:param name="parent"/>
		<xsl:param name="localTypes"/>
		<xsl:param name="documentNodes"/>
		<xsl:variable name="isChild">
			<xsl:choose>
				<xsl:when test="$parent/@type">
					<xsl:apply-templates select="$documentNodes/xs:complexType[@name = $parent/@type or @name = substring-after($parent/@type,':')]" mode="children">
						<xsl:with-param name="element" select="."/>
						<xsl:with-param name="documentNodes" select="$documentNodes"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="$parent/xs:complexType" mode="children">
						<xsl:with-param name="element" select="."/>
						<xsl:with-param name="documentNodes" select="$documentNodes"/>
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="normalize-space($isChild) != ''">
			<xsl:apply-templates select="." mode="elements">
				<xsl:with-param name="prefix" select="$prefix"/>
				<xsl:with-param name="localTypes" select="$localTypes"/>
				<xsl:with-param name="documentNodes" select="$documentNodes"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>
	
	<!-- xs:complexType -->
	<xsl:template match="xs:complexType" mode="elements.children">
		<xsl:param name="prefix"/>
		<xsl:param name="parent"/>
		<xsl:param name="localTypes"/>
		<xsl:param name="documentNodes"/>
		<xsl:variable name="candidateElements">
			<xsl:apply-templates select="." mode="candidates">
				<xsl:with-param name="mode" select="'elements'"/>
				<xsl:with-param name="documentNodes" select="$documentNodes"/>
			</xsl:apply-templates>
		</xsl:variable>
		<xsl:variable name="candidateGroups">
			<xsl:apply-templates select="." mode="candidates">
				<xsl:with-param name="mode" select="'groups'"/>
				<xsl:with-param name="documentNodes" select="$documentNodes"/>
			</xsl:apply-templates>
		</xsl:variable>
		<xsl:variable name="candidateTypes">
			<xsl:apply-templates select="." mode="candidates">
				<xsl:with-param name="mode" select="'types'"/>
				<xsl:with-param name="documentNodes" select="$documentNodes"/>
			</xsl:apply-templates>
		</xsl:variable>
		<xsl:apply-templates select=".//xs:element[@name and contains($candidateElements,concat(@name,' '))] | $documentNodes/xs:group[contains($candidateGroups,concat(@name,' '))]//xs:element[@name and contains($candidateElements,concat(@name,' '))] | $documentNodes/xs:complexType[contains($candidateTypes,concat(@name,' '))]//xs:element[@name and contains($candidateElements,concat(@name,' '))]" mode="elements.children">
			<xsl:sort select="@name"/>
			<xsl:with-param name="prefix" select="$prefix"/>
			<xsl:with-param name="parent" select="$parent"/>
			<xsl:with-param name="localTypes" select="$localTypes"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<!--#######################-->
	<!--### mode:candidates ###-->
	<!--#######################-->
	
	<!-- xs:element -->
	<xsl:template match="xs:element[@name]" mode="candidates">
		<xsl:param name="mode"/>
		<xsl:param name="documentNodes"/>
		<xsl:if test="$mode = 'elements'">
			<xsl:value-of select="concat(@name,' ')"/>
		</xsl:if>
	</xsl:template>
	
	<!-- xs:group -->
	<xsl:template match="xs:group[@ref]" mode="candidates">
		<xsl:param name="mode"/>
		<xsl:param name="documentNodes"/>
		<xsl:apply-templates select="$documentNodes/xs:group[@name = current()/@ref or @name = substring-after(current()/@ref,':')]" mode="candidates">
			<xsl:with-param name="mode" select="$mode"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="xs:group[@name]" mode="candidates">
		<xsl:param name="mode"/>
		<xsl:param name="documentNodes"/>
		<xsl:choose>
			<xsl:when test="$mode = 'groups'">
				<xsl:value-of select="concat(@name,' ')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="xs:sequence | xs:choice | xs:all" mode="candidates">
					<xsl:with-param name="mode" select="$mode"/>
					<xsl:with-param name="documentNodes" select="$documentNodes"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- xs:complexType -->
	<xsl:template match="xs:complexType" mode="candidates">
		<xsl:param name="mode"/>
		<xsl:param name="documentNodes"/>
		<xsl:apply-templates select="xs:complexContent | xs:simpleContent | xs:sequence | xs:choice | xs:all | xs:group" mode="candidates">
			<xsl:with-param name="mode" select="$mode"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<!-- xs:complexContent -->
	<xsl:template match="xs:complexContent" mode="candidates">
		<xsl:param name="mode"/>
		<xsl:param name="documentNodes"/>
		<xsl:apply-templates select="xs:extension | xs:restriction" mode="candidates">
			<xsl:with-param name="mode" select="$mode"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="xs:complexContent/xs:extension | xs:complexContent/xs:restriction" mode="candidates">
		<xsl:param name="mode"/>
		<xsl:param name="documentNodes"/>
		<xsl:if test="$mode = 'types'">
			<xsl:value-of select="concat(@base,' ')"/>
		</xsl:if>
		<xsl:apply-templates select="$documentNodes/xs:complexType[@name = current()/@base or @name = substring-after(current()/@base,':')] | xs:sequence | xs:choice | xs:all | xs:group" mode="candidates">
			<xsl:with-param name="mode" select="$mode"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<!-- xs:sequnce | xs:choice | xs:all -->
	<xsl:template match="xs:sequence | xs:choice | xs:all" mode="candidates">
		<xsl:param name="mode"/>
		<xsl:param name="documentNodes"/>
		<xsl:apply-templates select="xs:sequence | xs:choice | xs:all | xs:group | xs:element[@name]" mode="candidates">
			<xsl:with-param name="mode" select="$mode"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<!-- xs:simpleContent -->
	<xsl:template match="xs:simpleContent" mode="candidates">
		<xsl:param name="mode"/>
		<xsl:param name="documentNodes"/>
	</xsl:template>
	
	<!--#####################-->
	<!--### mode:children ###-->
	<!--#####################-->
	
	<!-- xs:element -->
	<xsl:template match="xs:element[@name]" mode="children">
		<xsl:param name="element"/>
		<xsl:param name="documentNodes"/>
		<xsl:if test="generate-id($element) = generate-id(.)">
			<xsl:text>true</xsl:text>
		</xsl:if>
	</xsl:template>
	
	<!-- xs:group -->
	<xsl:template match="xs:group[@ref]" mode="children">
		<xsl:param name="element"/>
		<xsl:param name="documentNodes"/>
		<xsl:apply-templates select="$documentNodes/xs:group[@name = current()/@ref or @name = substring-after(current()/@ref,':')]" mode="children">
			<xsl:with-param name="element" select="$element"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="xs:group[@name]" mode="children">
		<xsl:param name="element"/>
		<xsl:param name="documentNodes"/>
		<xsl:apply-templates select="xs:sequence | xs:choice | xs:all" mode="children">
			<xsl:with-param name="element" select="$element"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<!-- xs:complexType -->
	<xsl:template match="xs:complexType" mode="children">
		<xsl:param name="element"/>
		<xsl:param name="documentNodes"/>
		<xsl:apply-templates select="xs:complexContent | xs:simpleContent | xs:sequence | xs:choice | xs:all | xs:group" mode="children">
			<xsl:with-param name="element" select="$element"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<!-- xs:complexContent -->
	<xsl:template match="xs:complexContent" mode="children">
		<xsl:param name="element"/>
		<xsl:param name="documentNodes"/>
		<xsl:apply-templates select="xs:extension | xs:restriction" mode="children">
			<xsl:with-param name="element" select="$element"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="xs:complexContent/xs:extension | xs:complexContent/xs:restriction" mode="children">
		<xsl:param name="element"/>
		<xsl:param name="documentNodes"/>
		<xsl:apply-templates select="$documentNodes/xs:complexType[@name = current()/@base or @name = substring-after(current()/@base,':')] | xs:sequence | xs:choice | xs:all | xs:group" mode="children">
			<xsl:with-param name="element" select="$element"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<!-- xs:sequnce | xs:choice | xs:all -->
	<xsl:template match="xs:sequence | xs:choice | xs:all" mode="children">
		<xsl:param name="element"/>
		<xsl:param name="documentNodes"/>
		<xsl:apply-templates select="xs:sequence | xs:choice | xs:all | xs:group | xs:element[@name]" mode="children">
			<xsl:with-param name="element" select="$element"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<!-- xs:simpleContent -->
	<xsl:template match="xs:simpleContent" mode="children">
		<xsl:param name="element"/>
		<xsl:param name="documentNodes"/>
	</xsl:template>
	
	<!--#################################-->
	<!--### mode:elements.subelements ###-->
	<!--#################################-->
	
	<!-- xs:element -->
	<xsl:template match="xs:element[@name]" mode="elements.subelements">
		<xsl:param name="prefix"/>
		<xsl:param name="localTypes"/>
		<xsl:param name="documentNodes"/>
		<xsl:variable name="subelements">
			<xsl:choose>
				<xsl:when test="@type">
					<xsl:apply-templates select="$documentNodes/xs:complexType[@name = current()/@type or @name = substring-after(current()/@type,':')]" mode="subelements">
						<xsl:with-param name="prefix" select="$prefix"/>
						<xsl:with-param name="localTypes" select="$localTypes"/>
						<xsl:with-param name="documentNodes" select="$documentNodes"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="xs:complexType" mode="subelements">
						<xsl:with-param name="prefix" select="$prefix"/>
						<xsl:with-param name="localTypes" select="$localTypes"/>
						<xsl:with-param name="documentNodes" select="$documentNodes"/>
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="$subelements != ''">
			<xsl:call-template name="x1f:createSection">
				<xsl:with-param name="class" select="'subelements'"/>
				<xsl:with-param name="heading" select="'Sub-Elements'"/>
				<xsl:with-param name="headingElement" select="$x1f:section2Heading"/>
				<xsl:with-param name="body" select="$subelements"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!--########################-->
	<!--### mode:subelements ###-->
	<!--########################-->
	
	<xsl:template match="xs:element[@name and not(@abstract = 'true')]" mode="subelements">
		<xsl:param name="prefix"/>
		<xsl:param name="seperator"/>
		<xsl:param name="localTypes"/>
		<xsl:param name="documentNodes"/>
		<xsl:variable name="key">
			<xsl:value-of select="generate-id()"/>
		</xsl:variable>
		<xsl:variable name="alreadyKnownType">
			<xsl:call-template name="x1f:Map.containsKey">
				<xsl:with-param name="map" select="$localTypes"/>
				<xsl:with-param name="key" select="$key"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="id">
			<xsl:choose>
				<xsl:when test="$alreadyKnownType = 'true'">
					<xsl:call-template name="x1f:Map.getValue">
						<xsl:with-param name="map" select="$localTypes"/>
						<xsl:with-param name="key" select="$key"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="x1f:createID">
						<xsl:with-param name="prefix" select="translate($prefix,'/ ','_')"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="substitutes">
			<xsl:variable name="candidates" select="$documentNodes/xs:element[@substitutionGroup = current()/@name or substring-after(@substitutionGroup,':') = current()/@name]"/>
			<xsl:apply-templates select="$candidates[1]" mode="substitution.elements">
				<xsl:with-param name="hasPreviousContent" select="true()"/>
				<xsl:with-param name="documentNodes" select="$documentNodes"/>
			</xsl:apply-templates>
		</xsl:variable>
		<xsl:if test="$substitutes != ''">
			<xsl:text>(</xsl:text>
		</xsl:if>
		<a href="{concat('#',$id)}">
			<xsl:value-of select="@name"/>
		</a>
		<xsl:copy-of select="$substitutes"/>
		<xsl:if test="$substitutes != ''">
			<xsl:text>)</xsl:text>
			<sub>S</sub>
		</xsl:if>
		<xsl:if test="$alreadyKnownType = 'true'">
			<sub>R</sub>
		</xsl:if>
		<xsl:if test="local-name(..) != 'schema'">
			<xsl:call-template name="getMultiplicities"/>
			<xsl:call-template name="addSeperator">
				<xsl:with-param name="seperator" select="$seperator"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="xs:element[@name and @abstract = 'true']" mode="subelements">
		<xsl:param name="prefix"/>
		<xsl:param name="seperator"/>
		<xsl:param name="localTypes"/>
		<xsl:param name="documentNodes"/>
		<xsl:variable name="substitutes">
			<xsl:variable name="candidates" select="$documentNodes/xs:element[@substitutionGroup = current()/@name or substring-after(@substitutionGroup,':') = current()/@name]"/>
			<xsl:apply-templates select="$candidates[1]" mode="substitution.elements">
				<xsl:with-param name="documentNodes" select="$documentNodes"/>
			</xsl:apply-templates>
		</xsl:variable>
		<xsl:if test="$substitutes != ''">
			<xsl:text>(</xsl:text>
			<xsl:copy-of select="$substitutes"/>
			<xsl:text>)</xsl:text>
			<sub>S</sub>
		</xsl:if>
	</xsl:template>
	<xsl:template match="xs:element[@ref]" mode="subelements">
		<xsl:param name="prefix"/>
		<xsl:param name="seperator"/>
		<xsl:param name="documentNodes"/>
		<xsl:variable name="refElement">
			<xsl:apply-templates select="$documentNodes/xs:element[@name = current()/@ref or @name = substring-after(current()/@ref,':')]" mode="subelements">
				<xsl:with-param name="documentNodes" select="$documentNodes"/>
			</xsl:apply-templates>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$refElement != ''">
				<xsl:copy-of select="$refElement"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="@ref"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:call-template name="getMultiplicities"/>
		<xsl:call-template name="addSeperator">
			<xsl:with-param name="seperator" select="$seperator"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- xs:group -->
	<xsl:template match="xs:group[@ref]" mode="subelements">
		<xsl:param name="prefix"/>
		<xsl:param name="seperator"/>
		<xsl:param name="localTypes"/>
		<xsl:param name="documentNodes"/>
		<xsl:if test="@minOccurs != 1 or @maxOccurs != 1">
			<xsl:text>(</xsl:text>
		</xsl:if>
		<xsl:apply-templates select="$documentNodes/xs:group[@name = current()/@ref or @name = substring-after(current()/@ref,':')]" mode="subelements">
			<xsl:with-param name="prefix" select="$prefix"/>
			<xsl:with-param name="localTypes" select="$localTypes"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
		<xsl:if test="@minOccurs != 1 or @maxOccurs != 1">
			<xsl:text>)</xsl:text>
		</xsl:if>
		<xsl:call-template name="getMultiplicities"/>
		<xsl:call-template name="addSeperator">
			<xsl:with-param name="seperator" select="$seperator"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template match="xs:group[@name]" mode="subelements">
		<xsl:param name="prefix"/>
		<xsl:param name="localTypes"/>
		<xsl:param name="documentNodes"/>
		<xsl:apply-templates select="xs:sequence|xs:choice|xs:all" mode="subelements">
			<xsl:with-param name="prefix" select="$prefix"/>
			<xsl:with-param name="localTypes" select="$localTypes"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<!-- xs:complexType -->
	<xsl:template match="xs:complexType" mode="subelements">
		<xsl:param name="prefix"/>
		<xsl:param name="extensionElements"/>
		<xsl:param name="localTypes"/>
		<xsl:param name="documentNodes"/>
		<xsl:choose>
			<xsl:when test="xs:complexContent | xs:sequence | xs:choice | xs:all | xs:group">
				<xsl:apply-templates select="xs:complexContent | xs:sequence | xs:choice | xs:all | xs:group" mode="subelements">
					<xsl:with-param name="prefix" select="$prefix"/>
					<xsl:with-param name="extensionElements" select="$extensionElements"/>
					<xsl:with-param name="localTypes" select="$localTypes"/>
					<xsl:with-param name="documentNodes" select="$documentNodes"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$extensionElements"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- xs:complexContent -->
	<xsl:template match="xs:complexContent" mode="subelements">
		<xsl:param name="prefix"/>
		<xsl:param name="extensionElements"/>
		<xsl:param name="localTypes"/>
		<xsl:param name="documentNodes"/>
		<xsl:apply-templates select="xs:extension | xs:restriction" mode="subelements">
			<xsl:with-param name="prefix" select="$prefix"/>
			<xsl:with-param name="extensionElements" select="$extensionElements"/>
			<xsl:with-param name="localTypes" select="$localTypes"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<!-- xs:extension -->
	<xsl:template match="xs:extension" mode="subelements">
		<xsl:param name="prefix"/>
		<xsl:param name="extensionElements"/>
		<xsl:param name="localTypes"/>
		<xsl:param name="documentNodes"/>

		<xsl:apply-templates select="$documentNodes/xs:complexType[@name = current()/@base or @name = substring-after(current()/@base,':')]" mode="subelements">
			<xsl:with-param name="prefix" select="$prefix"/>
			<xsl:with-param name="extensionElements">
				<xsl:choose>
					<xsl:when test="xs:sequence|xs:choice|xs:all|xs:group">
						<xsl:apply-templates select="xs:sequence|xs:choice|xs:all|xs:group" mode="subelements">
							<xsl:with-param name="prefix" select="$prefix"/>
							<xsl:with-param name="extensionElements" select="$extensionElements"/>
							<xsl:with-param name="localTypes" select="$localTypes"/>
							<xsl:with-param name="documentNodes" select="$documentNodes"/>
						</xsl:apply-templates>
					</xsl:when>
					<xsl:otherwise>
						<xsl:copy-of select="$extensionElements"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="localTypes" select="$localTypes"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<!-- xs:restriction -->
	<xsl:template match="xs:restriction" mode="subelements">
		<xsl:param name="prefix"/>
		<xsl:param name="extensionElements"/>
		<xsl:param name="localTypes"/>
		<xsl:param name="documentNodes"/>
		<xsl:choose>
			<xsl:when test="xs:sequence|xs:choice|xs:all|xs:group">
				<xsl:apply-templates select="xs:sequence|xs:choice|xs:all|xs:group" mode="subelements">
					<xsl:with-param name="prefix" select="$prefix"/>
					<xsl:with-param name="extensionElements" select="$extensionElements"/>
					<xsl:with-param name="localTypes" select="$localTypes"/>
					<xsl:with-param name="documentNodes" select="$documentNodes"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$extensionElements"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- xs:sequence -->
	<xsl:template match="xs:sequence" mode="subelements">
		<xsl:param name="prefix"/>
		<xsl:param name="seperator"/>
		<xsl:param name="extensionElements"/>
		<xsl:param name="localTypes"/>
		<xsl:param name="documentNodes"/>
		<xsl:text>(</xsl:text>
		<xsl:apply-templates select="xs:sequence|xs:choice|xs:all|xs:element|xs:group" mode="subelements">
			<xsl:with-param name="prefix" select="$prefix"/>
			<xsl:with-param name="seperator" select="' '"/>
			<xsl:with-param name="localTypes" select="$localTypes"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
		<xsl:if test="normalize-space($extensionElements) != ''">
			<xsl:call-template name="addSeperator">
				<xsl:with-param name="seperator" select="' '"/>
				<xsl:with-param name="condition" select="true()"/>
			</xsl:call-template>
			<xsl:copy-of select="$extensionElements"/>
		</xsl:if>
		<xsl:text>)</xsl:text>
		<xsl:call-template name="getMultiplicities"/>
		<xsl:call-template name="addSeperator">
			<xsl:with-param name="seperator" select="$seperator"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- xs:choice -->
	<xsl:template match="xs:choice" mode="subelements">
		<xsl:param name="prefix"/>
		<xsl:param name="seperator"/>
		<xsl:param name="extensionElements"/>
		<xsl:param name="localTypes"/>
		<xsl:param name="documentNodes"/>
		<xsl:text>(</xsl:text>
		<xsl:apply-templates select="xs:sequence|xs:choice|xs:all|xs:element|xs:group" mode="subelements">
			<xsl:with-param name="prefix" select="$prefix"/>
			<xsl:with-param name="seperator" select="' | '"/>
			<xsl:with-param name="localTypes" select="$localTypes"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
		<xsl:if test="normalize-space($extensionElements) != ''">
			<xsl:call-template name="addSeperator">
				<xsl:with-param name="seperator" select="' | '"/>
				<xsl:with-param name="condition" select="true()"/>
			</xsl:call-template>
			<xsl:copy-of select="$extensionElements"/>
		</xsl:if>
		<xsl:text>)</xsl:text>
		<xsl:call-template name="getMultiplicities"/>
		<xsl:call-template name="addSeperator">
			<xsl:with-param name="seperator" select="$seperator"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- xs:all -->
	<xsl:template match="xs:all" mode="subelements">
		<xsl:param name="prefix"/>
		<xsl:param name="seperator"/>
		<xsl:param name="extensionElements"/>
		<xsl:param name="localTypes"/>
		<xsl:param name="documentNodes"/>
		<xsl:text>(</xsl:text>
		<xsl:apply-templates select="xs:sequence|xs:choice|xs:all|xs:element|xs:group" mode="subelements">
			<xsl:with-param name="prefix" select="$prefix"/>
			<xsl:with-param name="seperator" select="' + '"/>
			<xsl:with-param name="localTypes" select="$localTypes"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
		<xsl:if test="normalize-space($extensionElements) != ''">
			<xsl:call-template name="addSeperator">
				<xsl:with-param name="seperator" select="' + '"/>
				<xsl:with-param name="condition" select="true()"/>
			</xsl:call-template>
			<xsl:copy-of select="$extensionElements"/>
		</xsl:if>
		<xsl:text>)</xsl:text>
		<xsl:call-template name="getMultiplicities"/>
		<xsl:call-template name="addSeperator">
			<xsl:with-param name="seperator" select="$seperator"/>
		</xsl:call-template>
	</xsl:template>
	
	<!--#####################################-->
	<!--### mode:substitution.subelements ###-->
	<!--#####################################-->
	
	<xsl:template match="xs:schema/xs:element[@name and not(@abstract = 'true')]" mode="substitution.elements">
		<xsl:param name="hasPreviousContent" select="false()"/>
		<xsl:param name="documentNodes"/>
		<xsl:if test="$hasPreviousContent = 'true'">
			<xsl:text> | </xsl:text>
		</xsl:if>
		<xsl:variable name="id">
			<xsl:call-template name="x1f:createID"/>
		</xsl:variable>
		<a href="{concat('#',$id)}">
			<xsl:value-of select="@name"/>
		</a>
		<xsl:variable name="candidates" select="$documentNodes/xs:element[@substitutionGroup = current()/@name or substring-after(@substitutionGroup,':') = current()/@name]"/>
		<xsl:apply-templates select="$candidates[1]" mode="substitution.elements">
			<xsl:with-param name="hasPreviousContent" select="true()"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
		<xsl:variable name="nextSiblings" select="following-sibling::xs:element[@substitutionGroup = current()/@substitutionGroup or @substitutionGroup = substring-after(current()/@substitutionGroup,':') or substring-after(@substitutionGroup,':') = current()/@substitutionGroup or substring-after(@substitutionGroup ,':')= substring-after(current()/@substitutionGroup,':')]"/>
		<xsl:apply-templates select="$nextSiblings[1]" mode="substitution.elements">
			<xsl:with-param name="hasPreviousContent" select="true()"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="xs:schema/xs:element[@name and @abstract = 'true']" mode="substitution.elements">
		<xsl:param name="hasPreviousContent" select="false()"/>
		<xsl:param name="documentNodes"/>
		<xsl:variable name="substitutes">
			<xsl:variable name="candidates" select="$documentNodes/xs:element[@substitutionGroup = current()/@name or substring-after(@substitutionGroup,':') = current()/@name]"/>
			<xsl:apply-templates select="$candidates[1]" mode="substitution.elements">
				<xsl:with-param name="hasPreviousContent" select="$hasPreviousContent"/>
				<xsl:with-param name="documentNodes" select="$documentNodes"/>
			</xsl:apply-templates>
		</xsl:variable>
		<xsl:copy-of select="$substitutes"/>
		<xsl:variable name="nextSiblings" select="following-sibling::xs:element[@substitutionGroup = current()/@substitutionGroup or @substitutionGroup = substring-after(current()/@substitutionGroup,':') or substring-after(@substitutionGroup,':') = current()/@substitutionGroup or substring-after(@substitutionGroup ,':')= substring-after(current()/@substitutionGroup,':')]"/>
		<xsl:apply-templates select="$nextSiblings[1]" mode="substitution.elements">
			<xsl:with-param name="hasPreviousContent" select="$substitutes = 'true'"/>
			<xsl:with-param name="documentNodes" select="$documentNodes"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<!--####################-->
	<!--### mode:content ###-->
	<!--####################-->
	
	<xsl:template match="xs:element[@name]" mode="content">
		<xsl:param name="documentNodes"/>
		<xsl:variable name="content">
			<xsl:apply-templates select="." mode="simpleTypes">
				<xsl:with-param name="documentNodes" select="$documentNodes"/>
			</xsl:apply-templates>
		</xsl:variable>
		<xsl:if test="normalize-space($content) != ''">
			<xsl:call-template name="x1f:createSection">
				<xsl:with-param name="class" select="'content'"/>
				<xsl:with-param name="heading" select="'Content'"/>
				<xsl:with-param name="headingElement" select="$x1f:section2Heading"/>
				<xsl:with-param name="body" select="$content"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
