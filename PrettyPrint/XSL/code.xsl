<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation provides templates to copy and highlight source
	code. It also provides a structure to hide the copied code.
-->
<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:aors="http://aor-simulation.org"
	xmlns:allex="http://aor-simulation.org/allex"
	xmlns:aorsel="http://aor-simulation.org/aorsel"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0"
	exclude-result-prefixes="aors allex aorsel xsl">

	<!-- copyCode -->

	<xsl:template name="copyCode">
		<xsl:param name="code" select="."/>
		<xsl:param name="class"/>
		<xsl:if test="$code">
			<dl>
				<xsl:attribute name="class">
					<xsl:text>code</xsl:text>
					<xsl:if test="normalize-space($class) != ''">
						<xsl:value-of select="concat(' ',$class)"/>
					</xsl:if>
				</xsl:attribute>
				<xsl:apply-templates select="$code" mode="copyCode"/>
			</dl>
		</xsl:if>
	</xsl:template>

	<xsl:template match="*" mode="copyCode"/>

	<xsl:template match="aors:*" mode="copyCode">
		<dt>
			<xsl:value-of select="concat(@language,': ')"/>
		</dt>
		<dd>
			<xsl:call-template name="getFormattedCode">
				<xsl:with-param name="code" select="text()"/>
				<xsl:with-param name="language" select="@language"/>
			</xsl:call-template>
		</dd>
	</xsl:template>

	<xsl:template match="allex:*|aorsel:*" mode="copyCode">
		<dt>AORSEL:</dt>
		<dd>
			<xsl:call-template name="getFormattedCode"/>
		</dd>
	</xsl:template>

	<!-- getFormattedCode -->

	<xsl:template name="getFormattedCode">
		<xsl:param name="code" select="."/>
		<xsl:param name="language" select="'AORSEL'"/>
		<xsl:choose>
			<xsl:when test="$language = 'AORSEL' and $code">
				<ol class="formattedCode">
					<xsl:call-template name="getFormattedCode_AORSEL">
						<xsl:with-param name="code" select="$code"/>
					</xsl:call-template>
				</ol>
			</xsl:when>
			<xsl:when test="$language = 'Java' and string-length(normalize-space($code)) &gt; 0">
				<ol class="formattedCode">
					<xsl:call-template name="getFormattedCode_Java">
						<xsl:with-param name="code" select="normalize-space(translate(concat($code,'&#xA;'),'&#xA;','&#xA0;'))"/>
					</xsl:call-template>
				</ol>
			</xsl:when>
			<xsl:otherwise>
				<code>
					<xsl:value-of select="$code"/>
				</code>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- getFormattedCode_AORSEL -->

	<xsl:template name="getFormattedCode_AORSEL">
		<xsl:param name="code"/>
		<xsl:apply-templates select="$code" mode="getFormattedCode_AORSEL"/>
	</xsl:template>

	<xsl:template match="*" mode="getFormattedCode_AORSEL"/>

	<xsl:template match="allex:*|aorsel:*" mode="getFormattedCode_AORSEL">
		<li>
			<code>
				<xsl:variable name="attrs">
					<xsl:apply-templates select="@*" mode="getFormattedCode_AROSEL"/>
				</xsl:variable>
				<xsl:value-of select="concat('&lt;',local-name(),$attrs)"/>
				<xsl:if test="not(*)">
					<xsl:value-of select="'/'"/>
				</xsl:if>
				<xsl:value-of select="'&gt;'"/>
				<xsl:if test="allex:*|aorsel:*">
					<ol>
						<xsl:apply-templates mode="getFormattedCode_AORSEL"/>
					</ol>
					<xsl:value-of select="concat('&lt;/',local-name(),'&gt;')"/>
				</xsl:if>
			</code>
		</li>
	</xsl:template>

	<xsl:template match="@*" mode="getFormattedCode_AORSEL">
		<xsl:value-of select="concat(' ',name(),'=&quot;',.,'&quot;')"/>
	</xsl:template>

	<!-- getFormattedCode_Java -->

	<xsl:template name="getFormattedCode_Java">
		<xsl:param name="code"/>
		<xsl:variable name="codeLine" select="substring-before($code,'&#xA0;')"/>
		<xsl:variable name="restCode" select="substring-after($code,'&#xA0;')"/>
		<xsl:if test="string-length(normalize-space($codeLine)) &gt; 0">
			<li>
				<code>
					<xsl:call-template name="mapToEntities">
						<xsl:with-param name="code" select="$codeLine"/>
					</xsl:call-template>
				</code>
			</li>
		</xsl:if>
		<xsl:if test="string-length(normalize-space($restCode)) &gt; 0">
			<xsl:call-template name="getFormattedCode_Java">
				<xsl:with-param name="code" select="$restCode"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!-- mapToEntities -->
	
	<xsl:template name="mapToEntities">
		<xsl:param name="code"/>
		<xsl:choose>
			<xsl:when test="contains($code,'&amp;')">
				<xsl:call-template name="mapToEntities">
					<xsl:with-param name="code" select="substring-before($code,'&amp;')"/>
				</xsl:call-template>
				<xsl:text>&amp;</xsl:text>
				<xsl:call-template name="mapToEntities">
					<xsl:with-param name="code" select="substring-after($code,'&amp;')"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="contains($code,'&gt;')">
				<xsl:call-template name="mapToEntities">
					<xsl:with-param name="code" select="substring-before($code,'&gt;')"/>
				</xsl:call-template>
				<xsl:text>&gt;</xsl:text>
				<xsl:call-template name="mapToEntities">
					<xsl:with-param name="code" select="substring-after($code,'&gt;')"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="contains($code,'&lt;')">
				<xsl:call-template name="mapToEntities">
					<xsl:with-param name="code" select="substring-before($code,'&lt;')"/>
				</xsl:call-template>
				<xsl:text>&lt;</xsl:text>
				<xsl:call-template name="mapToEntities">
					<xsl:with-param name="code" select="substring-after($code,'&lt;')"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$code"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>