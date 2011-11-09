<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation is responsible for processing the
	aors:SpaceModel element.
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

	<xsl:template match="aors:SpaceModel" mode="heading">
		<xsl:text>Space Model</xsl:text>
	</xsl:template>
	
	<xsl:template match="aors:SpaceModel" mode="classHeading">
		<span class="name">Grid-Cell</span>
	</xsl:template>

	<!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:SpaceModel" mode="navigation">
		<xsl:apply-templates select="." mode="navigationEntry"/>
	</xsl:template>

	<!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template match="aors:SpaceModel" mode="content">
		<table class="horizontal">
			<colgroup>
				<col width="11%"/>
			</colgroup>
			<colgroup>
				<col width="11%"/>
			</colgroup>
			<colgroup>
				<col width="11%"/>
			</colgroup>
			<colgroup>
				<col width="8%"/>
				<col width="8%"/>
				<col width="8%"/>
			</colgroup>
			<xsl:choose>
				<xsl:when test="@discrete = 'true'">
					<colgroup>
						<col width="11%"/>
					</colgroup>
					<colgroup>
						<col width="20%"/>
						<col width="8%"/>
					</colgroup>
				</xsl:when>
				<xsl:otherwise>
					<colgroup>
						<col width="39%"/>
					</colgroup>
				</xsl:otherwise>
			</xsl:choose>
			<thead>
				<tr>
					<th rowspan="2" scope="col">dimensions</th>
					<th rowspan="2" scope="col">geometry</th>
					<th rowspan="2" scope="col">spatial distance unit</th>
					<th colspan="3" scope="colgroup">Maximum</th>
					<th rowspan="2" scope="col">discrete</th>
					<xsl:if test="@discrete = 'true'">
						<th colspan="2" scope="colgroup">GridCells</th>
					</xsl:if>
				</tr>
				<tr>
					<th scope="col">x</th>
					<th scope="col">y</th>
					<th scope="col">z</th>
					<xsl:if test="@discrete = 'true'">
						<th scope="col">maxOccupancy</th>
						<th scope="col">width</th>
					</xsl:if>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td><xsl:value-of select="@dimensions"/></td>
					<td>
						<xsl:call-template name="getOptionalValue">
							<xsl:with-param name="node" select="@geometry"/>
							<xsl:with-param name="defaultValue" select="'Euclidean'"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:call-template name="getOptionalValue">
							<xsl:with-param name="node" select="@spatialDistanceUnit"/>
							<xsl:with-param name="defaultValue" select="'m'"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:call-template name="getOptionalValue">
							<xsl:with-param name="node" select="@xMax"/>
							<xsl:with-param name="defaultValue" select="'0'"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:call-template name="getOptionalValue">
							<xsl:with-param name="node" select="@yMax"/>
							<xsl:with-param name="defaultValue" select="'0'"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:call-template name="getOptionalValue">
							<xsl:with-param name="node" select="@zMax"/>
							<xsl:with-param name="defaultValue" select="'0'"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:call-template name="getBooleanValue">
							<xsl:with-param name="value" select="@discrete"/>
							<xsl:with-param name="showNegative" select="true()"/>
						</xsl:call-template>
					</td>
					<xsl:if test="@discrete = 'true'">
						<td>
							<xsl:call-template name="getOptionalValue">
								<xsl:with-param name="node" select="@gridCellMaxOccupancy"/>
								<xsl:with-param name="defaultValue" select="'unbounded'"/>
							</xsl:call-template>
						</td>
						<td>
							<xsl:call-template name="getOptionalValue">
								<xsl:with-param name="node" select="@gridCellWidth"/>
								<xsl:with-param name="defaultValue" select="'1'"/>
							</xsl:call-template>
						</td>
					</xsl:if>
				</tr>
			</tbody>
		</table>
		
		<xsl:if test="aors:GridCellProperty | aors:GridCellFunction">
			<xsl:apply-templates select="." mode="class">
				<xsl:with-param name="headingElement" select="$section1Heading"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="aors:SpaceModel" mode="classContent">
		<xsl:apply-templates select="aors:GridCellProperty[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'properties'"/>
			<xsl:with-param name="content" select="aors:GridCellProperty"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:GridCellFunction[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'functions'"/>
			<xsl:with-param name="content" select="aors:GridCellFunction"/>
		</xsl:apply-templates>
	</xsl:template>
</xsl:stylesheet>