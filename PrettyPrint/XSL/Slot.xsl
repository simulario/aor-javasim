<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation is responsible for processing the
	aors:Slot elements.
-->
<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:aors="http://aor-simulation.org"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	version="1.0"
	exclude-result-prefixes="aors xsl">

	<xsl:include href="keys_macros_params.xsl"/>

	<!--###############-->
	<!--### content ###-->
	<!--###############-->

	<xsl:template match="aors:Slot[@xsi:type = 'SimpleSlot' or substring-after(@xsi:type,':') = 'SimpleSlot' or @value]|aors:BeliefSlot[@xsi:type = 'SimpleSlot' or substring-after(@xsi:type,':') = 'SimpleSlot' or @value]|aors:SelfBeliefSlot[@xsi:type = 'SimpleSlot' or substring-after(@xsi:type,':') = 'SimpleSlot' or @value]" mode="slot">
		<xsl:value-of select="concat(@property,' = ',@value)"/>
	</xsl:template>

	<xsl:template match="aors:Slot[@xsi:type = 'OpaqueExprSlot' or substring-after(@xsi:type,':') = 'OpaqueExprSlot' or aors:ValueExpr]|aors:BeliefSlot[@xsi:type = 'OpaqueExprSlot' or substring-after(@xsi:type,':') = 'OpaqueExprSlot' or aors:ValueExpr]|aors:SelfBeliefSlot[@xsi:type = 'OpaqueExprSlot' or substring-after(@xsi:type,':') = 'OpaqueExprSlot' or aors:ValueExpr]" mode="slot">
		<xsl:param name="maybeInline" select="false()"/>
		<xsl:value-of select="concat(@property,' = ')"/>
		<xsl:choose>
			<xsl:when test="not($maybeInline) or (count(aors:ValueExpr) > 1 or string-length(normalize-space(aors:ValueExpr/text())) > 70)">
				<xsl:call-template name="hideContent">
					<xsl:with-param name="content">
						<xsl:call-template name="copyCode">
							<xsl:with-param name="code" select="aors:ValueExpr"/>
						</xsl:call-template>
					</xsl:with-param>
					<xsl:with-param name="heading" select="'Expression'"/>
					<xsl:with-param name="headingPrefix" select="concat('Code of ',@property,'.')"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="copyCode">
					<xsl:with-param name="code" select="aors:ValueExpr"/>
					<xsl:with-param name="class" select="'inline'"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="aors:Increment" mode="slot">
		<xsl:value-of select="concat(@property,' + ',@value)"/>
	</xsl:template>
	
	<xsl:template match="aors:Decrement" mode="slot">
		<xsl:value-of select="concat(@property,' - ',@value)"/>
	</xsl:template>
</xsl:stylesheet>