<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:aors="http://aor-simulation.org"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0"
	exclude-result-prefixes="aors xsl">

	<xsl:import href="prettyprint_withoutScenarioData.xsl"/>
	<xsl:import href="InitialState.xsl"/>
<!--
	<xsl:import href="Views.xsl"/>
	<xsl:import href="Scales.xsl"/>
-->

	<xsl:include href="keys_macros_params.xsl"/>

	<xsl:output
		method="xml"
		doctype-public="-//W3C//DTD XHTML 1.1//EN"
		doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"
		encoding="UTF-8"
		indent="yes"/>

	<!--#######################-->
	<!--### basic structure ###-->
	<!--#######################-->

	<xsl:template match="/" mode="title">
		<xsl:choose>
			<xsl:when test="aors:SimulationScenario/@scenarioTitle">
				<xsl:value-of select="concat(aors:SimulationScenario/@scenarioTitle,' (',aors:SimulationScenario/@scenarioName,')')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="aors:SimulationScenario/@scenarioName"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="/" mode="body">
		<div id="documentHead">
			<div class="title">
				<xsl:apply-templates select="." mode="title"/>
			</div>
		</div>
		<div id="documentNavigation">
			<xsl:apply-templates select="aors:SimulationScenario" mode="documentNavigation"/>
		</div>
		<div id="documentBody">
			<xsl:apply-templates select="aors:SimulationScenario" mode="documentBody"/>
		</div>
	</xsl:template>

	<!--##########################-->
	<!--### documentNavigation ###-->
	<!--##########################-->

	<xsl:template match="*" mode="documentNavigation"/>

	<xsl:template match="aors:SimulationScenario" mode="documentNavigation">
		<h1 class="heading">
			<span>N</span><span>a</span><span>v</span><span>i</span><span>g</span><span>a</span><span>t</span><span>i</span><span>o</span><span>n</span>
		</h1>
		<ul>
			<xsl:apply-templates select="aors:documentation" mode="navigation"/>
			<xsl:apply-templates select="aors:SimulationParameters" mode="navigation"/>
			<xsl:apply-templates select="aors:SimulationModel" mode="documentNavigation"/>
			<xsl:apply-templates select="aors:InitialState" mode="navigation"/>
<!--
			<xsl:apply-templates select="aors:Scales" mode="navigation"/>
			<xsl:apply-templates select="aors:Views" mode="navigation"/>
-->
		</ul>
	</xsl:template>

	<xsl:template match="aors:SimulationModel" mode="documentNavigation">
		<li>
			<a>
				<xsl:attribute name="href">
					<xsl:call-template name="getId">
						<xsl:with-param name="prefix" select="'#'"/>
					</xsl:call-template>
				</xsl:attribute>
				<xsl:text>Simulation Model</xsl:text>
			</a>
			<ul>
				<xsl:apply-templates select="aors:documentation" mode="navigation"/>
				<xsl:if test="@*[local-name()!='modelName' and local-name()!='modelTitle']">
					<xsl:apply-templates select="." mode="navigation"/>
				</xsl:if>
				<xsl:apply-templates select="aors:SimulationParameterDeclaration[1]" mode="navigation"/>
				<xsl:apply-templates select="aors:SpaceModel" mode="navigation"/>
				<xsl:apply-templates select="aors:Statistics" mode="navigation"/>
				<xsl:apply-templates select="aors:DataTypes" mode="navigation"/>
				<xsl:apply-templates select="aors:Globals" mode="navigation"/>
				<xsl:apply-templates select="aors:EntityTypes" mode="navigation"/>
				<xsl:variable name="agentRules" select="aors:EntityTypes/aors:*/aors:ActualPerceptionRule|aors:EntityTypes/aors:*/aors:AgentRule|aors:EntityTypes/aors:*/aors:CommunicationRule"/>
				<xsl:apply-templates select="$agentRules[1]" mode="navigation">
					<xsl:with-param name="content" select="$agentRules"/>
				</xsl:apply-templates>
				<xsl:apply-templates select="aors:EnvironmentRules" mode="navigation"/>
			</ul>
		</li>
	</xsl:template>

	<!--####################-->
	<!--### documentBody ###-->
	<!--####################-->

	<xsl:template match="aors:SimulationScenario" mode="documentBody">
		<xsl:apply-templates select="aors:documentation" mode="part"/>
		<xsl:apply-templates select="aors:SimulationParameters" mode="part"/>
		<xsl:apply-templates select="aors:SimulationModel" mode="part"/>
		<xsl:apply-templates select="aors:InitialState" mode="part"/>
<!--
		<xsl:apply-templates select="aors:Scales" mode="part"/>
		<xsl:apply-templates select="aors:Views" mode="part"/>
-->
	</xsl:template>

	<!-- SimulationModel -->

	<xsl:template match="aors:SimulationModel" mode="partHeading">
		<xsl:text>Simulation Model: </xsl:text>
		<xsl:call-template name="getModelTitle"/>
	</xsl:template>

	<xsl:template match="aors:SimulationModel" mode="partContent">
		<xsl:apply-templates select="." mode="documentBody"/>
	</xsl:template>

	<!-- SimulationParameters -->

	<xsl:template match="aors:SimulationParameters" mode="heading">
		<xsl:text>Simulation Parameters</xsl:text>
	</xsl:template>

	<xsl:template match="aors:SimulationParameters" mode="navigation">
		<xsl:apply-templates select="." mode="navigationEntry"/>
	</xsl:template>

	<xsl:template match="aors:SimulationParameters" mode="content">
		<table>
			<colgroup>
				<col width="20%"/>
			</colgroup>
			<colgroup>
				<col width="20%"/>
			</colgroup>
			<colgroup>
				<col width="20%"/>
			</colgroup>
			<colgroup>
				<col width="20%"/>
			</colgroup>
			<colgroup>
				<col width="20%"/>
			</colgroup>
			<thead>
				<tr>
					<th scope="col">simulationSteps</th>
					<th scope="col">stepDuration</th>
					<th scope="col">timeUnit</th>
					<th scope="col">stepTimeDelay</th>
					<th scope="col">randomSeed</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>
						<xsl:call-template name="getOptionalValue">
							<xsl:with-param name="node" select="@simulationSteps"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:call-template name="getOptionalValue">
							<xsl:with-param name="node" select="@stepDuration"/>
							<xsl:with-param name="defaultValue" select="'1'"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:call-template name="getOptionalValue">
							<xsl:with-param name="node" select="@timeUnit"/>
							<xsl:with-param name="defaultValue" select="'s'"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:call-template name="getOptionalValue">
							<xsl:with-param name="node" select="@stepTimeDelay"/>
						</xsl:call-template>
					</td>
					<td>
						<xsl:call-template name="getOptionalValue">
							<xsl:with-param name="node" select="@randomSeed"/>
						</xsl:call-template>
					</td>
				</tr>
			</tbody>
		</table>
		<xsl:if test="aors:Parameter">
			<table class="left">
				<colgroup>
					<col width="20%"/>
				</colgroup>
				<colgroup>
					<col width="80%"/>
				</colgroup>
				<thead>
					<tr>
						<th scope="col">name</th>
						<th scope="col">value</th>
					</tr>
				</thead>
				<tbody>
					<xsl:apply-templates select="aors:Parameter" mode="simulationParamters"/>
				</tbody>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template match="aors:Parameter" mode="simulationParameters">
		<tr>
			<td><xsl:value-of select="@name"/></td>
			<td><xsl:value-of select="@value"/></td>
		</tr>
	</xsl:template>

	<!-- Scales -->
<!--
	<xsl:template match="aors:Scales" mode="heading">
		<xsl:text>Scales</xsl:text>
	</xsl:template>

	<xsl:template match="aors:Scales" mode="navigation">
		<xsl:apply-templates select="." mode="navigationEntry"/>
	</xsl:template>
-->
	<!-- Views -->
<!--
	<xsl:template match="aors:Views" mode="heading">
		<xsl:text>Views</xsl:text>
	</xsl:template>

	<xsl:template match="aors:Views" mode="navigation">
		<xsl:apply-templates select="." mode="navigationEntry"/>
	</xsl:template>
-->
</xsl:stylesheet>