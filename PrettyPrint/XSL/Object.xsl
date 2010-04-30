<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation is responsible for processing the
	aors:Object and aors:Objects elements.
-->
<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:aors="http://aor-simulation.org"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	version="1.0"
	exclude-result-prefixes="aors xsl">

	<xsl:import href="Property.xsl"/>

	<xsl:include href="keys_macros_params.xsl"/>

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<!-- Object, PhysicalObject, Agent and PhysicalAgent -->

	<xsl:template match="aors:Object|aors:PhysicalObject|aors:Agent|aors:PhysicalAgent" mode="classHeading">
		<span class="stereotype small">
			<xsl:value-of select="concat('&#171;',local-name(),'&#187;')"/>
		</span>
		<span class="name">
			<xsl:text> </xsl:text>
			<xsl:apply-templates select="." mode="headingName"/>
			<xsl:text> : </xsl:text>
			<xsl:apply-templates select="." mode="headingType"/>
			<xsl:text> </xsl:text>
		</span>
		<xsl:if test="@addToCollection">
			<span class="collection small">
				<xsl:text>(Collection: </xsl:text>
				<xsl:call-template name="createOptionalLink">
					<xsl:with-param name="node" select="key('Collections',@addToCollection)"/>
					<xsl:with-param name="text" select="@addToCollection"/>
				</xsl:call-template>
				<xsl:text>)</xsl:text>
			</span>
		</xsl:if>
		<xsl:if test="@objectVariable">
			<span class="variables small">
				<xsl:value-of select="concat('(obj-var: ',@objectVariable,')')"/>
			</span>
		</xsl:if>
	</xsl:template>

	<xsl:template match="aors:Object|aors:PhysicalObject|aors:Agent|aors:PhysicalAgent" mode="headingName">
		<xsl:call-template name="getOptionalValue">
			<xsl:with-param name="node">
				<xsl:value-of select="@name"/>
				<xsl:choose>
					<xsl:when test="@id">
						<xsl:value-of select="concat('[',@id,']')"/>
					</xsl:when>
					<xsl:when test="aors:ObjectID">
						<xsl:text>[</xsl:text>
						<xsl:call-template name="hideContent">
							<xsl:with-param name="content">
								<xsl:call-template name="copyCode">
									<xsl:with-param name="code" select="aors:ObjectID"/>
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="heading" select="'ID'"/>
							<xsl:with-param name="headingPrefix" select="'Code of '"/>
						</xsl:call-template>
						<xsl:text>]</xsl:text>
					</xsl:when>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="copy" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="aors:Object|aors:PhysicalObject|aors:Agent|aors:PhysicalAgent" mode="headingType">
		<xsl:call-template name="createOptionalLink">
			<xsl:with-param name="node" select="key('EntityTypes',@type)"/>
			<xsl:with-param name="text" select="@type"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="aors:Create/aors:Collection" mode="classHeading">
		<span class="stereotype small">
			<xsl:value-of select="concat('&#171;',@type,'&#187;')"/>
		</span>
		<span class="name">
			<xsl:text> </xsl:text>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:if test="@name">
						<xsl:call-template name="createOptionalLink">
							<xsl:with-param name="node" select="key('Collections',@name)"/>
							<xsl:with-param name="text" select="@name"/>
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="@id">
						<xsl:value-of select="concat('[',@id,']')"/>
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="copy" select="true()"/>
			</xsl:call-template>
			<xsl:text> </xsl:text>
		</span>
		<span class="parameter small">
			<xsl:call-template name="createOptionalLink">
				<xsl:with-param name="node" select="key('Types',@itemType)"/>
				<xsl:with-param name="text" select="@itemType"/>
			</xsl:call-template>
		</span>
		<xsl:if test="@addToCollection">
			<span class="collection small">
				<xsl:text>(Collection: </xsl:text>
				<xsl:call-template name="createOptionalLink">
					<xsl:with-param name="node" select="key('Collections',@addToCollection)"/>
					<xsl:with-param name="text" select="@addToCollection"/>
				</xsl:call-template>
				<xsl:text>)</xsl:text>
			</span>
		</xsl:if>
		<xsl:if test="@objectVariable">
			<span class="variables small">
				<xsl:value-of select="concat('(obj-var: ',@objectVariable,')')"/>
			</span>
		</xsl:if>
	</xsl:template>

	<!-- Objects, PhysicalObjects, Agents and PhysicalAgents -->

	<xsl:template match="aors:Objects|aors:PhysicalObjects|aors:Agents|aors:PhysicalAgents" mode="classHeading">
		<span class="stereotype small">
			<xsl:value-of select="concat('&#171;',local-name(),'&#187;')"/>
		</span>
		<span class="name">
			<xsl:text> </xsl:text>
			<xsl:apply-templates select="." mode="headingName"/>
			<xsl:text> : </xsl:text>
			<xsl:apply-templates select="." mode="headingType"/>
			<xsl:text> </xsl:text>
		</span>
		<xsl:if test="@objectVariable or @creationLoopVar">
			<span class="variables small">
				<xsl:text>(</xsl:text>
				<xsl:if test="@objectVariable">
					<xsl:value-of select="concat('obj-var: ',@objectVariable)"/>
				</xsl:if>
				<xsl:if test="@objectVariable and @creationLoopVar">
					<xsl:text> </xsl:text>
				</xsl:if>
				<xsl:if test="@creationLoopVar">
					<xsl:value-of select="concat('loop-var: ',@creationLoopVar)"/>
				</xsl:if>
				<xsl:text>)</xsl:text>
			</span>
		</xsl:if>
	</xsl:template>

	<xsl:template match="aors:Objects|aors:PhysicalObjects|aors:Agents|aors:PhysicalAgents" mode="headingName">
		<xsl:text>[</xsl:text>
		<xsl:call-template name="getOptionalValue">
			<xsl:with-param name="node">
				<xsl:choose>
					<xsl:when test="@rangeStartID">
						<xsl:value-of select="@rangeStartID"/>
					</xsl:when>
					<xsl:when test="aors:RangeStartID">
						<xsl:call-template name="hideContent">
							<xsl:with-param name="content">
								<xsl:call-template name="copyCode">
									<xsl:with-param name="code" select="aors:RangeStartID"/>
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="heading" select="'Start-ID'"/>
							<xsl:with-param name="headingPrefix" select="'Code of '"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="copy" select="true()"/>
		</xsl:call-template>
		<xsl:text>,</xsl:text>
		<xsl:call-template name="getOptionalValue">
			<xsl:with-param name="node">
				<xsl:choose>
					<xsl:when test="@rangeEndID">
						<xsl:value-of select="@rangeEndID"/>
					</xsl:when>
					<xsl:when test="aors:RangeEndID">
						<xsl:call-template name="hideContent">
							<xsl:with-param name="content">
								<xsl:call-template name="copyCode">
									<xsl:with-param name="code" select="aors:RangeEndID"/>
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="heading" select="'End-ID'"/>
							<xsl:with-param name="headingPrefix" select="'Code of '"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="copy" select="true()"/>
		</xsl:call-template>
		<xsl:text>]</xsl:text>
	</xsl:template>

	<xsl:template match="aors:Objects|aors:PhysicalObjects|aors:Agents|aors:PhysicalAgents" mode="headingType">
		<xsl:call-template name="createOptionalLink">
			<xsl:with-param name="node" select="key('EntityTypes',@type)"/>
			<xsl:with-param name="text" select="@type"/>
		</xsl:call-template>
	</xsl:template>

	<!-- BeliefEntity -->

	<xsl:template match="aors:BeliefEntity" mode="classHeading">
		<span class="stereotype small">
			<xsl:value-of select="concat('&#171;',local-name(),'&#187;')"/>
		</span>
		<span class="name">
			<xsl:text> </xsl:text>
			<xsl:call-template name="createOptionalLink">
				<xsl:with-param name="node" select=".."/>
				<xsl:with-param name="text">
					<xsl:apply-templates select=".." mode="headingName"/>
				</xsl:with-param>
				<xsl:with-param name="copy" select="true()"/>
			</xsl:call-template>
			<xsl:text></xsl:text>
			<xsl:text>::</xsl:text>
			<xsl:apply-templates select="." mode="headingName"/>
			<xsl:text> : </xsl:text>
			<xsl:apply-templates select=".." mode="headingType"/>
			<xsl:text>::</xsl:text>
			<xsl:apply-templates select="." mode="headingType"/>
		</span>
	</xsl:template>

	<xsl:template match="aors:BeliefEntity" mode="headingName">
		<xsl:call-template name="getOptionalValue">
			<xsl:with-param name="node">
				<xsl:value-of select="@name"/>
				<xsl:choose>
					<xsl:when test="@idRef">
						<xsl:value-of select="concat('[',@idRef,']')"/>
					</xsl:when>
					<xsl:when test="aors:IdRef">
						<xsl:call-template name="hideContent">
							<xsl:with-param name="content">
								<xsl:call-template name="copyCode">
									<xsl:with-param name="code" select="aors:IdRef"/>
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="heading" select="'ID-Ref'"/>
							<xsl:with-param name="headingPrefix" select="'Code of '"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="copy" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="aors:BeliefEntity" mode="headingType">
		<xsl:call-template name="createOptionalLink">
			<xsl:with-param name="node" select="key('EntityTypes',@type)"/>
			<xsl:with-param name="text" select="@type"/>
		</xsl:call-template>
		<xsl:text> </xsl:text>
	</xsl:template>

	<!-- CreateBeliefEntity -->

	<xsl:template match="aors:CreateBeliefEntity" mode="classHeading">
		<span class="stereotype small">
			<xsl:text>&#171;BeliefEntity&#187;</xsl:text>
		</span>
		<span class="name">
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:if test="aors:BeliefEntityIdRef">
						<xsl:text>[</xsl:text>
						<xsl:call-template name="hideContent">
							<xsl:with-param name="content">
								<xsl:call-template name="copyCode">
									<xsl:with-param name="code" select="aors:BeliefEntityIdRef"/>
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="heading" select="'ID-Ref'"/>
							<xsl:with-param name="headingPrefix" select="'Code of '"/>
						</xsl:call-template>
						<xsl:text>]</xsl:text>
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="copy" select="true()"/>
			</xsl:call-template>
			<xsl:text> : </xsl:text>
			<xsl:call-template name="createOptionalLink">
				<xsl:with-param name="node" select="../../.."/>
				<xsl:with-param name="text" select="../../../@name"/>
			</xsl:call-template>
			<xsl:text>::</xsl:text>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:choose>
						<xsl:when test="@beliefEntityType">
							<xsl:call-template name="createOptionalLink">
								<xsl:with-param name="node" select="../../../aors:BeliefEntityType[@name = @beliefEntityType]"/>
								<xsl:with-param name="text" select="@beliefEntityType"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:when test="aors:BeliefEntityType">
							<xsl:call-template name="hideContent">
								<xsl:with-param name="content">
									<xsl:call-template name="copyCode">
										<xsl:with-param name="code" select="aors:BeliefEntityType"/>
									</xsl:call-template>
								</xsl:with-param>
								<xsl:with-param name="heading" select="'type'"/>
								<xsl:with-param name="headingPrefix" select="'Code of '"/>
							</xsl:call-template>
						</xsl:when>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="copy" select="true()"/>
			</xsl:call-template>
		</span>
	</xsl:template>

	<!-- ReminderEvent, TimeEvent and PeriodicTimeEvent -->

	<xsl:template match="aors:TimeEvent|aors:PeriodicTimeEvent|aors:ReminderEvent" mode="classHeading">
		<span class="stereotype small">
			<xsl:value-of select="concat('&#171;',local-name(),'&#187;')"/>
		</span>
		<span class="name">
			<xsl:text> </xsl:text>
			<xsl:call-template name="createOptionalLink">
				<xsl:with-param name="node" select=".."/>
				<xsl:with-param name="text">
					<xsl:apply-templates select=".." mode="headingName"/>
				</xsl:with-param>
				<xsl:with-param name="copy" select="true()"/>
			</xsl:call-template>
			<xsl:text></xsl:text>
			<xsl:text>::</xsl:text>
			<xsl:apply-templates select="." mode="headingName"/>
			<xsl:text> : </xsl:text>
			<xsl:apply-templates select=".." mode="headingType"/>
			<xsl:text>::</xsl:text>
			<xsl:apply-templates select="." mode="headingType"/>
			<xsl:text> </xsl:text>
		</span>
	</xsl:template>

	<xsl:template match="aors:ReminderEvent|aors:TimeEvent|aors:PeriodicTimeEvent" mode="headingName">
		<xsl:call-template name="getOptionalValue">
			<xsl:with-param name="node">
				<xsl:value-of select="@name"/>
				<xsl:if test="@id">
					<xsl:value-of select="concat('[',@id,']')"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="aors:ReminderEvent|aors:TimeEvent|aors:PeriodicTimeEvent" mode="headingType">
		<xsl:call-template name="createOptionalLink">
			<xsl:with-param name="node" select="key('EntityTypes',@type)"/>
			<xsl:with-param name="text" select="@type"/>
		</xsl:call-template>
	</xsl:template>

	<!-- ExogenousEvent, CausedEvent -->

	<xsl:template match="aors:ExogenousEvent | aors:CausedEvent" mode="classHeading">
		<span class="stereotype small">
			<xsl:value-of select="concat('&#171;',local-name(),'&#187;')"/>
		</span>
		<span class="name">
			<xsl:text> </xsl:text>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:value-of select="@name"/>
					<xsl:if test="@id">
						<xsl:value-of select="concat('[',@id,']')"/>
					</xsl:if>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text> : </xsl:text>
			<xsl:call-template name="createOptionalLink">
				<xsl:with-param name="node" select="key('EntityTypes',@type)"/>
				<xsl:with-param name="text" select="@type"/>
			</xsl:call-template>
			<xsl:text> </xsl:text>
		</span>
	</xsl:template>

	<!-- ActionEventExpr, OutMessageEventExpr, ReminderEventExpr, CausedEventExpr, InMessageEventExpr, PerceptionEventExpr, ActivityStartEventExpr, ActivityEndEventExpr -->

	<xsl:template match="aors:ActionEventExpr" mode="classHeading">
		<span class="stereotype small">
			<xsl:value-of select="concat('&#171;',substring-before(local-name(),'Expr'),'&#187;')"/>
		</span>
		<span class="name">
			<xsl:text> </xsl:text>
			<xsl:call-template name="createOptionalLink">
				<xsl:with-param name="node" select="key('EntityTypes',@actionEventType)"/>
				<xsl:with-param name="text" select="@actionEventType"/>
			</xsl:call-template>
			<xsl:text> </xsl:text>
		</span>
	</xsl:template>

	<xsl:template match="aors:CausedEventExpr|aors:PerceptionEventExpr" mode="classHeading">
		<span class="stereotype small">
			<xsl:value-of select="concat('&#171;',substring-before(local-name(),'Expr'),'&#187;')"/>
		</span>
		<span class="name">
			<xsl:text> </xsl:text>
			<xsl:call-template name="createOptionalLink">
				<xsl:with-param name="node" select="key('EntityTypes',@eventType)"/>
				<xsl:with-param name="text" select="@eventType"/>
			</xsl:call-template>
			<xsl:text> </xsl:text>
		</span>
	</xsl:template>

	<xsl:template match="aors:ReminderEventExpr" mode="classHeading">
		<span class="name">
			<xsl:text> ReminderEvent </xsl:text>
		</span>
	</xsl:template>

	<xsl:template match="aors:OutMessageEventExpr|aors:InMessageEventExpr" mode="classHeading">
		<span class="stereotype small">
			<xsl:value-of select="concat('&#171;',substring-before(local-name(),'Expr'),'&#187;')"/>
		</span>
		<span class="name">
			<xsl:text> </xsl:text>
			<xsl:call-template name="createOptionalLink">
				<xsl:with-param name="node" select="key('EntityTypes',@messageType)"/>
				<xsl:with-param name="text" select="@messageType"/>
			</xsl:call-template>
			<xsl:text> </xsl:text>
		</span>
	</xsl:template>

	<xsl:template match="aors:ActivityStartEventExpr|aors:ActivityEndEventExpr" mode="classHeading">
		<span class="stereotype small">
			<xsl:value-of select="concat('&#171;',substring-before(local-name(),'Expr'),'&#187;')"/>
		</span>
		<span class="name">
			<xsl:text> </xsl:text>
			<xsl:call-template name="createOptionalLink">
				<xsl:with-param name="node" select="key('EntityTypes',@activityType)"/>
				<xsl:with-param name="text" select="@activityType"/>
			</xsl:call-template>
			<xsl:text> </xsl:text>
		</span>
	</xsl:template>

	<!-- CREATE-EVT -->

	<xsl:template match="aors:CREATE-EVT" mode="classHeading">
		<span class="stereotype small">
			<xsl:text>&#171;ActualPerceptionEvent&#187;</xsl:text>
		</span>
		<span class="name">
			<xsl:text> </xsl:text>
			<xsl:call-template name="createOptionalLink">
				<xsl:with-param name="node" select="key('EntityTypes',@actualPercEvtType)"/>
				<xsl:with-param name="text" select="@actualPercEvtType"/>
			</xsl:call-template>
			<xsl:text> </xsl:text>
		</span>
	</xsl:template>

	<!--###############-->
	<!--### content ###-->
	<!--###############-->

	<xsl:template match="aors:BeliefEntity|aors:ReminderEvent|aors:TimeEvent|aors:PeriodicTimeEvent" mode="objectComponent">
		<li>
			<a>
				<xsl:attribute name="href">
					<xsl:call-template name="getId">
						<xsl:with-param name="prefix" select="'#'"/>
					</xsl:call-template>
				</xsl:attribute>
				<xsl:apply-templates select="." mode="headingName"/>
			</a>
			<xsl:text> : </xsl:text>
			<xsl:apply-templates select="." mode="headingType"/>
		</li>
	</xsl:template>
</xsl:stylesheet>