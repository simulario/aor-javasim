<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation is responsible for processing the
	aors:InitialState element.
-->
<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:aors="http://aor-simulation.org"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0"
	exclude-result-prefixes="aors xsl">

	<xsl:import href="Rules.xsl"/>

	<xsl:include href="keys_macros_params.xsl"/>

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:InitialState" mode="heading">
		<xsl:text>Initial State</xsl:text>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:GlobalVariable|aors:InitialState/aors:GridCells" mode="heading">
		<xsl:text>Globals</xsl:text>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:Object|aors:InitialState/aors:Objects|aors:InitialState/aors:PhysicalObject|aors:InitialState/aors:PhysicalObjects|aors:InitialState/aors:Agent|aors:InitialState/aors:Agents|aors:InitialState/aors:PhysicalAgent|aors:InitialState/aors:PhysicalAgents|aors:InitialState/aors:Collection" mode="heading">
		<xsl:text>Objects</xsl:text>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:*/aors:BeliefEntity" mode="classSectionHeading">
		<xsl:text>Belief Entities</xsl:text>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:*/aors:ReminderEvent|aors:InitialState/aors:*/aors:TimeEvent|aors:InitialState/aors:*/aors:PeriodicTimeEvent" mode="classSectionHeading">
		<xsl:text>Events</xsl:text>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:ExogenousEvent | aors:InitialState/aors:CausedEvent" mode="heading">
		<xsl:text>Caused and Exogenous Events</xsl:text>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:InitializationRule" mode="chapterHeading">
		<xsl:text>Initialization Rules</xsl:text>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:InitializationRule" mode="heading">
		<xsl:value-of select="@name"/>
	</xsl:template>

	<!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:InitialState" mode="navigation">
		<xsl:apply-templates select="." mode="navigationEntry">
			<xsl:with-param name="subEntries">
				<xsl:variable name="globals" select="aors:GlobalVariable|aors:GridCells"/>
				<xsl:apply-templates select="$globals[1]" mode="navigation"/>
				<xsl:variable name="objects" select="aors:Object|aors:Objects|aors:PhysicalObject|aors:PhysicalObjects|aors:Agent|aors:Agents|aors:PhysicalAgent|aors:PhysicalAgents|aors:Collection"/>
				<xsl:apply-templates select="$objects[1]" mode="navigation"/>
				<xsl:variable name="events" select="aors:ExogenousEvent|aors:CausedEvent"/>
				<xsl:apply-templates select="$events[1]" mode="navigation"/>
				<xsl:apply-templates select="aors:InitializationRule[1]" mode="navigation"/>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:*" mode="navigation">
		<xsl:apply-templates select="." mode="navigationEntry"/>
	</xsl:template>
	
	<xsl:template match="aors:InitialState/aors:InitializationRule" mode="navigationEntryTitle">
		<xsl:apply-templates select="." mode="chapterHeading"/>
	</xsl:template>

	<!--################-->
	<!--###  content ###-->
	<!--################-->

	<xsl:template match="aors:InitialState" mode="content">
		<xsl:variable name="globals" select="aors:GlobalVariable|aors:GridCells"/>
		<xsl:apply-templates select="$globals[1]" mode="chapter">
			<xsl:with-param name="content" select="$globals"/>
		</xsl:apply-templates>
		<xsl:variable name="objects" select="aors:Object|aors:Objects|aors:PhysicalObject|aors:PhysicalObjects|aors:Agent|aors:Agents|aors:PhysicalAgent|aors:PhysicalAgents|aors:Collection"/>
		<xsl:apply-templates select="$objects[1]" mode="chapter">
			<xsl:with-param name="content" select="$objects"/>
		</xsl:apply-templates>
		<xsl:variable name="events" select="aors:ExogenousEvent|aors:CausedEvent"/>
		<xsl:apply-templates select="$events[1]" mode="chapter">
			<xsl:with-param name="content" select="$events"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:InitializationRule[1]" mode="chapter">
			<xsl:with-param name="content" select="aors:InitializationRule"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- Globals -->

	<xsl:template match="aors:InitialState/aors:GlobalVariable|aors:InitialState/aors:GridCells" mode="chapterContent">
		<xsl:param name="content"/>
		<dl>
			<xsl:apply-templates select="$content" mode="update"/>
		</dl>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:GlobalVariable" mode="update">
		<dt>
			<xsl:value-of select="concat('[GLOBAL] ',@name,' = ')"/>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:choose>
						<xsl:when test="@value">
							<xsl:value-of select="@value"/>
						</xsl:when>
						<xsl:when test="aors:ValueExpr">
							
							<xsl:choose>
								<xsl:when test="count(aors:ValueExpr) > 1 or string-length(normalize-space(aors:ValueExpr/text())) > 70">
							
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
							
						</xsl:when>
					</xsl:choose>
				</xsl:with-param>
			</xsl:call-template>
		</dt>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:GridCells" mode="update">
		<xsl:apply-templates select="aors:*" mode="update"/>
	</xsl:template>
	
	<xsl:template match="aors:InitialState/aors:GridCells/aors:Slot" mode="update"/>

	<xsl:template match="aors:InitialState/aors:GridCells/aors:Slot[1]" mode="update">
		<dt>
			<xsl:text>[GRID-CELL] (all cells)</xsl:text>
		</dt>
		<xsl:apply-templates select=".|following-sibling::aors:Slot" mode="update2"/>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:GridCells/aors:GridCell" mode="update">
		<dt>
			<xsl:value-of select="concat('[GRID-CELL] (',@x,',',@y,')')"/>
		</dt>
		<xsl:apply-templates select="aors:Slot" mode="update2"/>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:GridCells/aors:GridCellSet" mode="update">
		<dt>
			<xsl:value-of select="concat('[GRID-CELLS] [(',@startX,',',@startY,') ; (',@endX,',',@endY,')]')"/>
			<xsl:if test="@creationLoopVar">
				<xsl:value-of select="concat(' (variable: ',@creationLoopVar,')')"/>
			</xsl:if>
		</dt>
		<xsl:apply-templates select="aors:Slot" mode="update2"/>
	</xsl:template>

	<!-- aors:GridCells//Slot -->

	<xsl:template match="aors:InitialState/aors:GridCells/aors:*/aors:Slot|aors:InitialState/aors:GridCells/aors:Slot" mode="update2">
		<dd>
			<xsl:apply-templates select="." mode="slot">
				<xsl:with-param name="maybeInline" select="true()"/>
			</xsl:apply-templates>
		</dd>
	</xsl:template>

	<!-- Objects -->

	<xsl:template match="aors:InitialState/aors:Object|aors:InitialState/aors:Objects|aors:InitialState/aors:PhysicalObject|aors:InitialState/aors:PhysicalObjects|aors:InitialState/aors:Agent|aors:InitialState/aors:Agents|aors:InitialState/aors:PhysicalAgent|aors:InitialState/aors:PhysicalAgents|aors:InitialState/aors:Collection" mode="content">
		<xsl:param name="content"/>
		<xsl:apply-templates select="$content" mode="create">
			<xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:Object|aors:InitialState/aors:PhysicalObject|aors:InitialState/aors:Agent|aors:InitialState/aors:PhysicalAgent|aors:InitialState/aors:Collection" mode="create">
		<xsl:apply-templates select="." mode="class">
			<xsl:with-param name="headingElement" select="$section1Heading"/>
			<xsl:with-param name="kind" select="'object'"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:Collection" mode="create">
		<xsl:apply-templates select="." mode="class">
			<xsl:with-param name="headingElement" select="$section1Heading"/>
			<xsl:with-param name="class" select="'parameterized'"/>
			<xsl:with-param name="kind" select="'object'"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:Objects|aors:InitialState/aors:PhysicalObjects|aors:InitialState/aors:Agents|aors:InitialState/aors:PhysicalAgents" mode="create">
		<xsl:apply-templates select="." mode="class">
			<xsl:with-param name="headingElement" select="$section1Heading"/>
			<xsl:with-param name="kind" select="'objects'"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:Object|aors:InitialState/aors:Objects|aors:InitialState/aors:PhysicalObject|aors:InitialState/aors:PhysicalObjects|aors:InitialState/aors:Agent|aors:InitialState/aors:Agents|aors:InitialState/aors:PhysicalAgent|aors:InitialState/aors:PhysicalAgents|aors:InitialState/aors:Collection" mode="class">
		<xsl:param name="headingElement"/>
		<xsl:param name="class"/>
		<xsl:param name="kind"/>
		<xsl:apply-templates select="." mode="classBody">
			<xsl:with-param name="headingElement" select="$headingElement"/>
			<xsl:with-param name="class" select="$class"/>
			<xsl:with-param name="kind" select="$kind"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:BeliefEntity|aors:ReminderEvent|aors:TimeEvent|aors:PeriodicTimeEvent" mode="class">
			<xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
			<xsl:with-param name="headingElement" select="$headingElement"/>
			<xsl:with-param name="class" select="$class"/>
			<xsl:with-param name="kind" select="'object'"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:Object|aors:InitialState/aors:Objects|aors:InitialState/aors:PhysicalObject|aors:InitialState/aors:PhysicalObjects|aors:InitialState/aors:Agent|aors:InitialState/aors:Agents|aors:InitialState/aors:PhysicalAgent|aors:InitialState/aors:PhysicalAgents|aors:InitialState/aors:Collection" mode="classContent">
		<xsl:variable name="properties" select="@*[local-name()!='type' and local-name()!='id' and local-name()!='name' and local-name()!='addToCollection' and local-name()!='rangeStartID' and local-name()!='rangeEndID' and local-name()!='objectVariable' and local-name()!='creationLoopVar' and local-name()!='itemType']|aors:Slot"/>
		<xsl:apply-templates select="$properties[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'properties'"/>
			<xsl:with-param name="content" select="$properties"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:SelfBeliefSlot[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'selfBeliefProperties'"/>
			<xsl:with-param name="content" select="aors:SelfBeliefSlot"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:BeliefEntity[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'beliefEntities'"/>
			<xsl:with-param name="content" select="aors:BeliefEntity"/>
		</xsl:apply-templates>
		<xsl:variable name="events" select="aors:ReminderEvent|aors:TimeEvent|aors:PeriodicTimeEvent"/>
		<xsl:apply-templates select="$events[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'events'"/>
			<xsl:with-param name="content" select="$events"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:*/aors:BeliefEntity" mode="classContent">
		<xsl:apply-templates select="aors:BeliefSlot[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'properties'"/>
			<xsl:with-param name="content" select="aors:BeliefSlot"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:*/aors:ReminderEvent|aors:InitialState/aors:*/aors:TimeEvent|aors:InitialState/aors:*/aors:PeriodicTimeEvent" mode="classContent">
		<xsl:variable name="properties" select="@*[local-name()!='type' and local-name()!='id' and local-name()!='name']|aors:Slot"/>
		<xsl:apply-templates select="$properties[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'properties'"/>
			<xsl:with-param name="content" select="$properties"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:*/aors:BeliefEntity|aors:InitialState/aors:*/aors:ReminderEvent|aors:InitialState/aors:*/aors:TimeEvent|aors:InitialState/aors:*/aors:PeriodicTimeEvent" mode="classSectionContent">
		<xsl:param name="content"/>
		<ul>
			<xsl:apply-templates select="$content" mode="objectComponent"/>
		</ul>
	</xsl:template>

	<!-- EnvironmentEvents -->

	<xsl:template match="aors:InitialState/aors:ExogenousEvent|aors:InitialState/aors:CausedEvent" mode="content">
		<xsl:param name="content"/>
		<xsl:apply-templates select="$content" mode="create">
			<xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:ExogenousEvent|aors:InitialState/aors:CausedEvent" mode="create">
		<xsl:apply-templates select="." mode="class">
			<xsl:with-param name="headingElement" select="$section1Heading"/>
			<xsl:with-param name="kind" select="'object'"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:InitialState/aors:ExogenousEvent|aors:InitialState/aors:CausedEvent" mode="classContent">
		<xsl:variable name="properties" select="@*[local-name()!='name' and local-name()!='id' and local-name()!='type']|aors:Slot"/>
		<xsl:apply-templates select="$properties[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'properties'"/>
			<xsl:with-param name="content" select="$properties"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- InitializationRule -->

	<xsl:template match="aors:InitializationRule" mode="chapterContent">
		<xsl:param name="content"/>
		<xsl:apply-templates select="$content" mode="section1"/>
	</xsl:template>

	<xsl:template match="aors:InitializationRule" mode="section1">
		<xsl:param name="class"/>
		<xsl:apply-templates select="." mode="section1Body">
			<xsl:with-param name="class">
				<xsl:text> rule</xsl:text>
				<xsl:choose>
					<xsl:when test="count(preceding-sibling::aors:InitializationRule) mod 2 = 0">
						<xsl:text> odd</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text> even</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:InitializationRule" mode="content">
		<xsl:apply-templates select="aors:documentation" mode="content"/>
		<xsl:apply-templates select="aors:FOR[1]" mode="section2">
			<xsl:with-param name="content" select="aors:FOR"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:IF[1]" mode="section2">
			<xsl:with-param name="content" select="aors:IF"/>
		</xsl:apply-templates>
		<xsl:variable name="then" select="aors:UpdateObject|aors:UpdateObjects|aors:UpdateGridCell|aors:UpdateGridCells"/>
		<xsl:apply-templates select="$then[1]" mode="section2">
			<xsl:with-param name="content" select="$then"/>
		</xsl:apply-templates>
	</xsl:template>
</xsl:stylesheet>