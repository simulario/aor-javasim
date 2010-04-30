<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation is responsible for processing the
	aors:EntityTypes element.
-->
<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:aors="http://aor-simulation.org"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0"
	exclude-result-prefixes="aors xsl">

	<xsl:import href="Property.xsl"/>
	<xsl:import href="Function.xsl"/>
	<xsl:import href="Condition.xsl"/>

	<xsl:include href="keys_macros_params.xsl"/>

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:EntityTypes" mode="heading">
		<xsl:text>Entity Types</xsl:text>
	</xsl:template>

	<xsl:template match="aors:EntityTypes/aors:*" mode="classHeading">
		<span class="stereotype small">
			<xsl:value-of select="concat('&#171;',local-name(),'&#187;')"/>
		</span>
		<span class="name">
			<xsl:value-of select="concat(' ',@name,' ')"/>
		</span>
		<xsl:if test="@superType">
			<span class="supertype small">
				<xsl:text>(extends </xsl:text>
				<xsl:call-template name="createOptionalLink">
					<xsl:with-param name="node" select="key('EntityTypes',@superType)"/>
					<xsl:with-param name="text" select="@superType"/>
				</xsl:call-template>
				<xsl:text>)</xsl:text>
			</span>
		</xsl:if>
	</xsl:template>

	<!-- BeliefEntityTypes and InternalEventTypes -->

	<xsl:template match="aors:BeliefEntityType" mode="classSectionHeading">
		<xsl:text>Belief Entity Types</xsl:text>
	</xsl:template>

	<xsl:template match="aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType" mode="classSectionHeading">
		<xsl:text>Event Types</xsl:text>
	</xsl:template>

	<xsl:template match="aors:BeliefEntityType|aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType" mode="classHeading">
		<span class="stereotype small">
			<xsl:value-of select="concat('&#171;',local-name(),'&#187;')"/>
		</span>
		<span class="name">
			<xsl:text> </xsl:text>
			<xsl:call-template name="createOptionalLink">
				<xsl:with-param name="node" select=".."/>
				<xsl:with-param name="text" select="../@name"/>
			</xsl:call-template>
			<xsl:value-of select="concat('::',@name,' ')"/>
		</span>
		<xsl:if test="@superType">
			<span class="supertype small">
				<xsl:text>(extends </xsl:text>
				<xsl:call-template name="createOptionalLink">
					<xsl:with-param name="node" select="key('EntityTypes',@superType)"/>
					<xsl:with-param name="text" select="@superType"/>
				</xsl:call-template>
				<xsl:text>)</xsl:text>
			</span>
		</xsl:if>
	</xsl:template>

	<!-- AgentRules -->

	<xsl:template match="aors:ActualPerceptionRule|aors:AgentRule|aors:CommunicationRule" mode="classSectionHeading">
		<xsl:text>Rules</xsl:text>
	</xsl:template>

	<!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:EntityTypes" mode="navigation">
		<xsl:apply-templates select="." mode="navigationEntry">
			<xsl:with-param name="subEntries">
				<xsl:apply-templates select="*" mode="navigation">
					<xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
				</xsl:apply-templates>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:EntityTypes/aors:*" mode="navigation">
		<xsl:apply-templates select="." mode="navigationEntry">
			<xsl:with-param name="subEntries">
				<xsl:apply-templates select="aors:BeliefEntityType|aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType" mode="navigation">
					<xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
				</xsl:apply-templates>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:EntityTypes/aors:*" mode="navigationEntryTitle">
		<xsl:value-of select="@name"/>
	</xsl:template>

	<!-- BeliefEntityTypes and InternalEventTypes -->

	<xsl:template match="aors:BeliefEntityType|aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType" mode="navigation">
		<xsl:apply-templates select="." mode="navigationEntry"/>
	</xsl:template>

	<xsl:template match="aors:BeliefEntityType|aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType" mode="navigationEntryTitle">
		<xsl:value-of select="concat(../@name,'::',@name)"/>
	</xsl:template>

	<!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template match="aors:EntityTypes" mode="content">
		<xsl:apply-templates select="aors:*" mode="class">
			<xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
			<xsl:with-param name="headingElement" select="$section1Heading"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:EntityTypes/aors:*" mode="class">
		<xsl:param name="headingElement"/>
		<xsl:apply-templates select="." mode="classBody">
			<xsl:with-param name="headingElement" select="$headingElement"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:BeliefEntityType|aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType" mode="class">
			<xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
			<xsl:with-param name="headingElement" select="$headingElement"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:ActivityType" mode="class"/>

	<xsl:template match="aors:EntityTypes/*|aors:BeliefEntityType|aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType" mode="classContent">
		<xsl:variable name="properties" select="@*[local-name()!='name' and local-name()!='superType']|aors:InitialAttributeValue|aors:Attribute|aors:ReferenceProperty|aors:ComplexDataProperty|aors:EnumerationProperty|aors:BeliefAttribute|aors:BeliefReferenceProperty"/>
		<xsl:apply-templates select="$properties[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'properties'"/>
			<xsl:with-param name="content" select="$properties"/>
		</xsl:apply-templates>
		<xsl:variable name="selfBeliefProperties" select="aors:SelfBeliefAttribute|aors:SelfBeliefReferenceProperty"/>
		<xsl:apply-templates select="$selfBeliefProperties[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'selfBeliefProperties'"/>
			<xsl:with-param name="content" select="$selfBeliefProperties"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:BeliefEntityType[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'beliefEntities'"/>
			<xsl:with-param name="content" select="aors:BeliefEntityType"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:Function[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'functions'"/>
			<xsl:with-param name="content" select="aors:Function"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:SubjectiveFunction[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'subjectiveFunctions'"/>
			<xsl:with-param name="content" select="aors:SubjectiveFunction"/>
		</xsl:apply-templates>
		<xsl:variable name="conditions" select="aors:StopCondition|aors:Periodicity"/>
		<xsl:apply-templates select="$conditions[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'conditions'"/>
			<xsl:with-param name="content" select="$conditions"/>
		</xsl:apply-templates>
		<xsl:variable name="events" select="aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType"/>
		<xsl:apply-templates select="$events[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'events'"/>
			<xsl:with-param name="content" select="$events"/>
		</xsl:apply-templates>
		<xsl:variable name="rules" select="aors:ActualPerceptionRule|aors:AgentRule|aors:CommunicationRule"/>
		<xsl:apply-templates select="$rules[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section2Heading"/>
			<xsl:with-param name="class" select="'rules'"/>
			<xsl:with-param name="content" select="$rules"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:BeliefEntityType|aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType|aors:ActualPerceptionRule|aors:AgentRule|aors:CommunicationRule" mode="classSectionContent">
		<xsl:param name="content"/>
		<ul>
			<xsl:apply-templates select="$content" mode="entityTypeComponent"/>
		</ul>
	</xsl:template>

	<xsl:template match="aors:BeliefEntityType|aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType|aors:ActualPerceptionRule|aors:AgentRule|aors:CommunicationRule" mode="entityTypeComponent">
		<li>
			<a>
				<xsl:attribute name="href">
					<xsl:call-template name="getId">
						<xsl:with-param name="prefix" select="'#'"/>
					</xsl:call-template>
				</xsl:attribute>
				<xsl:value-of select="@name"/>
			</a>
		</li>
	</xsl:template>
</xsl:stylesheet>