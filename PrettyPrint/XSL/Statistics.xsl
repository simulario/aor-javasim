<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation is responsible for processing the
	aors:Statistics element.
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

	<xsl:template match="aors:Statistics" mode="heading">
		<xsl:text>Statistics Variables</xsl:text>
	</xsl:template>

	<!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:Statistics" mode="navigation">
		<xsl:apply-templates select="." mode="navigationEntry"/>
	</xsl:template>

	<!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template match="aors:Statistics" mode="content">
		<table class="horizontal">
			<colgroup>
				<col width="32%"/>
			</colgroup>
			<colgroup>
				<col width="60%"/>
			</colgroup>
			<colgroup>
				<col width="8%"/>
			</colgroup>
			<thead>
				<tr>
					<th scope="col">variable</th>
					<th scope="col">function</th>
					<th scope="col">compute only at end</th>
				</tr>
			</thead>
			<tbody>
				<xsl:apply-templates select="aors:Variable" mode="statistics"/>
			</tbody>
		</table>
	</xsl:template>

	<xsl:template match="aors:Variable" mode="statistics">
		<tr>
			<td class="left">
				<ul>
					<li>
						<code class="proportional">
							<xsl:value-of select="concat(@name,' : ',@dataType)"/>
							<xsl:if test="@initialValue">
								<xsl:value-of select="concat(' = ',@initialValue)"/>
							</xsl:if>
						</code>
					</li>
					<li>
						<span class="small">
							<xsl:call-template name="getOptionalValue">
								<xsl:with-param name="node" select="@displayName"/>
							</xsl:call-template>
						</span>
					</li>
				</ul>
			</td>
			<td class="left">
				<xsl:call-template name="getOptionalValue">
					<xsl:with-param name="node">
						<xsl:if test="@aggregationFunction">
							<code>
								<xsl:value-of select="concat(@aggregationFunction,'(')"/>
								<xsl:call-template name="createOptionalLink">
									<xsl:with-param name="node" select="key('EntityTypes',@sourceObjectType)"/>
									<xsl:with-param name="text" select="@sourceObjectType"/>
								</xsl:call-template>
								<xsl:value-of select="concat('[',@sourceObjectRef,'].',@sourceObjectProperty,')')"/>
							</code>
						</xsl:if>
						<xsl:if test="aors:ValueExpr">
							<xsl:choose>
								<xsl:when test="count(aors:ValueExpr) > 1 or string-length(normalize-space(aors:ValueExpr/text())) > number($smallExpressionLength)">
									<xsl:call-template name="hideContent">
										<xsl:with-param name="content">
											<xsl:call-template name="copyCode">
												<xsl:with-param name="code" select="aors:ValueExpr"/>
											</xsl:call-template>
										</xsl:with-param>
										<xsl:with-param name="heading" select="'Expression'"/>
										<xsl:with-param name="headingPrefix" select="concat('Code of ',@name,'.')"/>
									</xsl:call-template>
								</xsl:when>
								<xsl:otherwise>
									<xsl:call-template name="copyCode">
										<xsl:with-param name="code" select="aors:ValueExpr"/>
										<xsl:with-param name="class" select="'inline'"/>
									</xsl:call-template>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
					</xsl:with-param>
					<xsl:with-param name="copy" select="true()"/>
				</xsl:call-template>
			</td>
			<td>
				<xsl:call-template name="getBooleanValue">
					<xsl:with-param name="value" select="@computeOnlyAtEnd"/>
				</xsl:call-template>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>