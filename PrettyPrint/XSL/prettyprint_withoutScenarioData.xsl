<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:html="http://www.w3.org/1999/xhtml"
	xmlns:aors="http://aor-simulation.org"
	xmlns:allex="http://aor-simulation.org/allex"
	xmlns:aorsel="http://aor-simulation.org/aorsel"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0"
	exclude-result-prefixes="html aors allex aorsel dc xs xsi xsl">

	<xsl:import href="structure.xsl"/>
	<xsl:import href="documentation.xsl"/>
	<xsl:import href="SimulationModel.xsl"/>
	<xsl:import href="SimulationParameterDeclaration.xsl"/>
	<xsl:import href="SpaceModel.xsl"/>
	<xsl:import href="Statistics.xsl"/>
	<xsl:import href="DataTypes.xsl"/>
	<xsl:import href="Collections.xsl"/>
	<xsl:import href="Globals.xsl"/>
	<xsl:import href="EntityTypes.xsl"/>
	<xsl:import href="AgentRules.xsl"/>
	<xsl:import href="EnvironmentRules.xsl"/>

	<xsl:output
		method="xml"
		doctype-public="-//W3C//DTD XHTML 1.1//EN"
		doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"
		encoding="UTF-8"
		indent="yes"/>

	<!--####################-->
	<!--### local macros ###-->
	<!--####################-->

	<xsl:template name="getModelTitle">
		<xsl:choose>
			<xsl:when test="/aors:SimulationScenario/aors:SimulationModel/@modelTitle">
				<xsl:value-of select="concat(/aors:SimulationScenario/aors:SimulationModel/@modelTitle,' (',/aors:SimulationScenario/aors:SimulationModel/@modelName,')')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="/aors:SimulationScenario/aors:SimulationModel/@modelName"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--#######################-->
	<!--### basic structure ###-->
	<!--#######################-->

	<xsl:template match="*"/>

	<xsl:template match="/">
		<html>
			<head>
				<xsl:apply-templates select="." mode="head"/>
			</head>
			<body>
				<xsl:apply-templates select="." mode="body"/>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="/" mode="head">
		<title>
			<xsl:call-template name="getModelTitle"/>
		</title>
		<meta http-equiv="Content-Type" content="application/xml; charset=UTF-8"/>
		<link rel="stylesheet">
			<xsl:attribute name="href">
				<xsl:choose>
					<xsl:when test="aors:SimulationScenario/@simulationManagerDirectory">
						<xsl:variable name="simulatorDir" select="aors:SimulationScenario/@simulationManagerDirectory"/>
						<xsl:value-of select="$simulatorDir"/>
						<xsl:if test="translate(substring($simulatorDir,string-length($simulatorDir)),'\','/') != '/'">
							<xsl:text>/</xsl:text>
						</xsl:if>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>../</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>ext/prettyPrint/css/prettyprint.css</xsl:text>
			</xsl:attribute>
		</link>
	</xsl:template>

	<xsl:template match="/" mode="body">
		<div id="documentHead">
			<div class="title">
				<xsl:call-template name="getModelTitle"/>
			</div>
		</div>
		<div id="documentNavigation">
			<xsl:apply-templates select="aors:SimulationScenario/aors:SimulationModel" mode="documentNavigation"/>
		</div>
		<div id="documentBody">
			<xsl:apply-templates select="aors:SimulationScenario/aors:SimulationModel" mode="documentBody"/>
		</div>
	</xsl:template>

	<!--##########################-->
	<!--### documentNavigation ###-->
	<!--##########################-->

	<xsl:template match="*" mode="documentNavigation"/>

	<xsl:template match="aors:SimulationModel" mode="documentNavigation">
		<h2 class="heading">
			<span>N</span><span>a</span><span>v</span><span>i</span><span>g</span><span>a</span><span>t</span><span>i</span><span>o</span><span>n</span>
		</h2>
		<ul>
			<xsl:apply-templates select="aors:documentation" mode="navigation"/>
			<xsl:if test="@*[local-name()!='modelName' and local-name()!='modelTitle']">
				<xsl:apply-templates select="." mode="navigation"/>
			</xsl:if>
			<xsl:apply-templates select="aors:SimulationParameterDeclaration[1]" mode="navigation"/>
			<xsl:apply-templates select="aors:SpaceModel" mode="navigation"/>
			<xsl:apply-templates select="aors:Statistics" mode="navigation"/>
			<xsl:apply-templates select="aors:DataTypes" mode="navigation"/>
			<xsl:apply-templates select="aors:Collections" mode="navigation"/>
			<xsl:apply-templates select="aors:Globals" mode="navigation"/>
			<xsl:apply-templates select="aors:EntityTypes" mode="navigation"/>
			<xsl:variable name="agentRules" select="aors:EntityTypes/aors:*/aors:ActualPerceptionRule|aors:EntityTypes/aors:*/aors:AgentRule|aors:EntityTypes/aors:*/aors:CommunicationRule"/>
			<xsl:apply-templates select="$agentRules[1]" mode="navigation">
				<xsl:with-param name="content" select="$agentRules"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="aors:EnvironmentRules" mode="navigation"/>
		</ul>
	</xsl:template>

	<!--####################-->
	<!--### documentBody ###-->
	<!--####################-->

	<xsl:template match="*" mode="documentBody"/>

	<xsl:template match="aors:SimulationModel" mode="documentBody">
		<xsl:apply-templates select="aors:documentation" mode="chapter"/>
			<xsl:if test="@*[local-name()!='modelName' and local-name()!='modelTitle']">
				<xsl:apply-templates select="." mode="chapter"/>
			</xsl:if>
		<xsl:apply-templates select="aors:SimulationParameterDeclaration[1]" mode="chapter">
			<xsl:with-param name="content" select="aors:SimulationParameterDeclaration"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:SpaceModel" mode="chapter"/>
		<xsl:apply-templates select="aors:Statistics" mode="chapter"/>
		<xsl:apply-templates select="aors:DataTypes" mode="chapter"/>
		<xsl:apply-templates select="aors:Collections" mode="chapter"/>
		<xsl:apply-templates select="aors:Globals" mode="chapter"/>
		<xsl:apply-templates select="aors:EntityTypes" mode="chapter"/>
		<xsl:variable name="agentRules" select="aors:EntityTypes/aors:*/aors:ActualPerceptionRule|aors:EntityTypes/aors:*/aors:AgentRule|aors:EntityTypes/aors:*/aors:CommunicationRule"/>
		<xsl:apply-templates select="$agentRules[1]" mode="chapter">
			<xsl:with-param name="content" select="$agentRules"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:EnvironmentRules" mode="chapter"/>
	</xsl:template>
</xsl:stylesheet>