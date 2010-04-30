<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation is responsible for processing the
	aors:SimulationParameterDeclaration elements.
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

	<xsl:template match="aors:SimulationParameterDeclaration" mode="heading">
		<xsl:text>Simulation-Parameter Declarations</xsl:text>
	</xsl:template>

	<!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:SimulationParameterDeclaration" mode="navigation">
		<xsl:apply-templates select="." mode="navigationEntry"/>
	</xsl:template>

	<!--################-->
	<!--###  content ###-->
	<!--################-->

	<xsl:template match="aors:SimulationParameterDeclaration" mode="content">
		<xsl:param name="content"/>
		<table class="left">
			<colgroup>
				<col width="20%"/>
			</colgroup>
			<colgroup>
				<col width="20%"/>
			</colgroup>
			<colgroup>
				<col width="60%"/>
			</colgroup>
			<thead>
				<tr>
					<th scope="col">name</th>
					<th scope="col">type</th>
					<th scope="col">documentation</th>
				</tr>
			</thead>
			<tbody>
				<xsl:apply-templates select="$content" mode="simulationParameterDeclaration"/>
			</tbody>
		</table>
	</xsl:template>

	<xsl:template match="aors:SimulationParameterDeclaration" mode="simulationParameterDeclaration">
		<tr>
			<td>
				<xsl:value-of select="@name"/>
			</td>
			<td>
				<xsl:value-of select="@type"/>
			</td>
			<td>
				<xsl:choose>
					<xsl:when test="aors:documentation">
						<xsl:apply-templates select="aors:documentation" mode="content"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>n/a</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>