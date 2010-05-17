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
					<th scope="col">value</th>
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
		  <xsl:attribute name="id">
		    <xsl:call-template name="getId"/>
		  </xsl:attribute>
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
					  <xsl:apply-templates select="aors:Source" mode="statistics"/>
					</xsl:with-param>
				  <xsl:with-param name="copy" select="true()"/>
				</xsl:call-template>
			</td>
		  <td>
		    <xsl:call-template name="getBooleanValue">
		      <xsl:with-param name="value" select="aors:Source/@computeOnlyAtEnd"/>
		    </xsl:call-template>
		  </td>
		</tr>
	</xsl:template>
  
  <xsl:template match="aors:Source" mode="statistics">
    <xsl:variable name="content">
      <xsl:apply-templates select="." mode="statisticsSource"/>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="@aggregationFunction">
        <code>
          <xsl:value-of select="concat(@aggregationFunction,'(')"/>
          <xsl:copy-of select="$content"/>
          <xsl:text>)</xsl:text>
        </code>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="$content"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="aors:StatisticsVariable" mode="statisticsSource">
    <xsl:call-template name="createOptionalLink">
      <xsl:with-param name="node" select="key('StatisticVariables',@name)"/>
      <xsl:with-param name="text" select="@name"/>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="aors:GlobalVariable" mode="statisticsSource">
    <xsl:call-template name="createOptionalLink">
      <xsl:with-param name="node" select="key('GlobalVariables',@name)"/>
      <xsl:with-param name="text" select="@name"/>
    </xsl:call-template>    
  </xsl:template>
  
  <xsl:template match="aors:ObjectProperty" mode="statisticsSource">    
    <xsl:call-template name="createOptionalLink">
      <xsl:with-param name="node" select="key('EntityTypes',@objectType)"/>
      <xsl:with-param name="text" select="@objectType"/>
    </xsl:call-template>
    <xsl:if test="@objectIdRef">
      <xsl:value-of select="concat('[',@objectIdRef,']')"/>
    </xsl:if>
    <xsl:value-of select="concat('.', @property)"/>
  </xsl:template>
  
  <xsl:template match="aors:ValueExpr" mode="statisticsSource">
    <xsl:if test="count(preceding-sibling::aors:ValueExpr) = 0">
      <xsl:choose>
        <xsl:when test="count(../aors:ValueExpr) > 1 or string-length(normalize-space(text())) > number($smallExpressionLength)">
          <xsl:call-template name="hideContent">
            <xsl:with-param name="content">
              <xsl:call-template name="copyCode">
                <xsl:with-param name="code" select="../aors:ValueExpr"/>
              </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="heading" select="'Expression'"/>
            <xsl:with-param name="headingPrefix" select="concat('Code of ',@name,'.')"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="copyCode">
            <xsl:with-param name="code" select="../aors:ValueExpr"/>
            <xsl:with-param name="class" select="'inline'"/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>      
    </xsl:if>
  </xsl:template>
  
  <xsl:template match="aors:ObjectTypeExtensionSize" mode="statisticsSource">
    <xsl:text>size-of(</xsl:text>
    <xsl:call-template name="createOptionalLink">
      <xsl:with-param name="node" select="key('EntityTypes',@objectType)"/>
      <xsl:with-param name="text" select="@objectType"/>
    </xsl:call-template>
    <xsl:text>)</xsl:text>
  </xsl:template>
  
  <xsl:template match="aors:ResourceUtilization" mode="statisticsSource">
    <xsl:call-template name="createOptionalLink">
      <xsl:with-param name="node" select="key('EntityTypes',@activityType)"/>
      <xsl:with-param name="text" select="@activityType"/>
    </xsl:call-template>
    <xsl:text>.</xsl:text>    
    <xsl:call-template name="createOptionalLink">
      <xsl:with-param name="node" select="key('EntityTypes',@resourceObjectType)"/>
      <xsl:with-param name="text" select="@resourceObjectType"/>
    </xsl:call-template>
    <xsl:if test="@objectIdRef">
      <xsl:value-of select="concat('[',@resourceObjectIdRef,']')"/>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>