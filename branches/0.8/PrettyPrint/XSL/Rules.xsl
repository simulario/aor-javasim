<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation is responsible for processing the
	common aors:Rule elements.
-->
<xsl:stylesheet
	xmlns="http://www.w3.org/1999/xhtml"
	xmlns:aors="http://aor-simulation.org"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	version="1.0"
	exclude-result-prefixes="aors xsl">

	<xsl:import href="Slot.xsl"/>
	<xsl:import href="Object.xsl"/>
	<xsl:import href="Property.xsl"/>
	<xsl:import href="Condition.xsl"/>

	<xsl:include href="keys_macros_params.xsl"/>

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<!-- WHEN -->

	<xsl:template match="aors:WHEN" mode="heading">
		<xsl:text>When</xsl:text>
	</xsl:template>
	
	<!-- ON-EACH-SIMULATION-STEP -->
	
	<xsl:template match="aors:ON-EACH-SIMULATION-STEP" mode="heading">
		<xsl:text>On</xsl:text>
	</xsl:template>

	<!-- FOR -->

	<xsl:template match="@agentVariable|aors:FOR" mode="heading">
		<xsl:text>For</xsl:text>
	</xsl:template>
	
	<xsl:template match="aors:DO" mode="heading">
		<xsl:text>Do</xsl:text>
	</xsl:template>

	<!-- IF -->

	<xsl:template match="aors:IF" mode="heading">
		<xsl:text>If</xsl:text>
	</xsl:template>

	<!-- THEN and ELSE -->

	<xsl:template match="aors:THEN|aors:UPDATE-AGT|aors:UPDATE-ENV|aors:SCHEDULE-EVT|aors:CREATE-EVT|aors:InitializationRule/aors:UpdateObject|aors:InitializationRule/aors:UpdateObjects|aors:InitializationRule/aors:UpdateGridCell" mode="section2Heading">
		<xsl:text>Then</xsl:text>
	</xsl:template>

	<xsl:template match="aors:ELSE" mode="heading">
		<xsl:text>Else</xsl:text>
	</xsl:template>

	<!-- UPDATE-AGT -->

	<xsl:template match="aors:UPDATE-AGT/aors:Slot|aors:UPDATE-AGT/aors:SelfBeliefSlot" mode="heading">
		<xsl:text>Update Slots</xsl:text>
	</xsl:template>

	<xsl:template match="aors:UPDATE-AGT/aors:UpdateComplexDataPropertyValue" mode="heading">
		<xsl:text>Update Complex Data Property</xsl:text>
	</xsl:template>
	
	<xsl:template match="aors:UPDATE-AGT/aors:UpdateBeliefEntity" mode="heading">
		<xsl:text>Update Belief Entities</xsl:text>
	</xsl:template>

	<xsl:template match="aors:UPDATE-AGT/aors:CreateBeliefEntity" mode="section3Heading">
		<xsl:text>Create Belief Entities</xsl:text>
	</xsl:template>

	<xsl:template match="aors:UPDATE-AGT/aors:DestroyBeliefEntity" mode="heading">
		<xsl:text>Destroy Belief Entities</xsl:text>
	</xsl:template>

	<!-- UPDATE-ENV -->

	<xsl:template match="aors:UPDATE-ENV/aors:UpdateObject|aors:UPDATE-ENV/aors:UpdateObjects" mode="heading">
		<xsl:text>Update Objects</xsl:text>
	</xsl:template>
	
	<xsl:template match="aors:UPDATE-ENV/aors:UpdateGridCell" mode="heading">
		<xsl:text>Updage Grid-Cells</xsl:text>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:UpdateGlobalVariable|aors:UPDATE-ENV/aors:IncrementGlobalVariable" mode="heading">
		<xsl:text>Update Global Variables</xsl:text>
	</xsl:template>
	
	<xsl:template match="aors:UPDATE-ENV/aors:UpdateStatisticsVariable" mode="heading">
		<xsl:text>Update Statistics Variables</xsl:text>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:AddObjectToCollection|aors:UPDATE-ENV/aors:RemoveObjectFromCollection" mode="heading">
		<xsl:text>Update Collections</xsl:text>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:Create" mode="heading">
		<xsl:text>Create Objects</xsl:text>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:Create/aors:*/aors:BeliefEntity" mode="classSectionHeading">
		<xsl:text>Belief Entities</xsl:text>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:Create/aors:*/aors:ReminderEvent|aors:UPDATE-ENV/aors:Create/aors:*/aors:TimeEvent|aors:UPDATE-ENV/aors:Create/aors:*/aors:PeriodicTimeEvent" mode="classSectionHeading">
		<xsl:text>Events</xsl:text>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:DestroyObject|aors:UPDATE-ENV/aors:DestroyObjects" mode="heading">
		<xsl:text>Destroy Objects</xsl:text>
	</xsl:template>

	<!-- InitRule/Update -->

	<xsl:template match="aors:InitializationRule/aors:UpdateObject|aors:InitializationRule/aors:UpdateObjects|aors:InitializationRule/aors:UpdateGridCell" mode="heading">
		<xsl:text>Update Objects</xsl:text>
	</xsl:template>

	<!-- SCHEDULE-EVT -->

	<xsl:template match="aors:SCHEDULE-EVT" mode="section3Heading">
		<xsl:text>Schedule Event</xsl:text>
	</xsl:template>

	<xsl:template match="aors:CreateDescription" mode="heading">
		<xsl:text>Create Descriptions</xsl:text>
	</xsl:template>

	<!-- CREATE-EVT -->

	<xsl:template match="aors:CREATE-EVT" mode="section3Heading">
		<xsl:text>Create Event</xsl:text>
	</xsl:template>

	<!--###############-->
	<!--### content ###-->
	<!--###############-->

	<!-- WHEN -->

	<xsl:template match="aors:WHEN" mode="content">
		<xsl:if test="@eventVariable">
			<xsl:value-of select="concat(@eventVariable,' : ')"/>
		</xsl:if>
		<xsl:call-template name="createOptionalLink">
			<xsl:with-param name="node" select="key('EntityTypes',@eventType)"/>
			<xsl:with-param name="text" select="@eventType"/>
		</xsl:call-template>
		<xsl:choose>
			<xsl:when test="@messageType or @messageVariable">
				<xsl:value-of select="concat('&lt;',@messageVariable)"/>
				<xsl:if test="@messageType and @messageVariable">
					<xsl:text> : </xsl:text>
				</xsl:if>
				<xsl:call-template name="createOptionalLink">
					<xsl:with-param name="node" select="key('EntityTypes',@messageType)"/>
					<xsl:with-param name="text" select="@messageType"/>
				</xsl:call-template>
				<xsl:text>&gt;</xsl:text>
			</xsl:when>
			<xsl:when test="@physicalObjectType">
				<xsl:text>&lt;</xsl:text>
				<xsl:call-template name="createOptionalLink">
					<xsl:with-param name="node" select="key('EntityTypes',@physicalObjectType)"/>
					<xsl:with-param name="text" select="@physicalObjectType"/>
				</xsl:call-template>
				<xsl:text>&gt;</xsl:text>
			</xsl:when>
			<xsl:when test="@reminderMsg">
				<xsl:text>&lt;</xsl:text>
				<xsl:call-template name="createOptionalLink">
					<xsl:with-param name="node" select="key('EntityTypes',@reminderMsg)"/>
					<xsl:with-param name="text" select="@reminderMsg"/>
				</xsl:call-template>
				<xsl:text>&gt;</xsl:text>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="aors:ActualPerceptionRule/aors:WHEN" mode="content">
		<xsl:call-template name="getOptionalValue">
			<xsl:with-param name="node">
				<xsl:if test="@eventVariable">
					<xsl:value-of select="concat(@eventVariable,' : ')"/>					
				</xsl:if>
				<xsl:choose>
					<xsl:when test="@perceptionEventType">
						<xsl:call-template name="createOptionalLink">
							<xsl:with-param name="node" select="key('EntityTypes',@perceptionEventType)"/>
							<xsl:with-param name="text" select="@perceptionEventType"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="@messageType">
						<xsl:call-template name="createOptionalLink">
							<xsl:with-param name="node" select="key('EntityTypes',@messageType)"/>
							<xsl:with-param name="text" select="@messageType"/>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:with-param>
			<xsl:with-param name="copy" select="true()"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- ON-EACH-SIMULATION-STEP -->
	
	<xsl:template match="aors:ON-EACH-SIMULATION-STEP" mode="content">
		<xsl:text>each simulation step</xsl:text>
	</xsl:template>

	<!-- FOR -->

	<xsl:template match="@agentVariable|aors:FOR" mode="content">
		<xsl:param name="content"/>
		<ul class="for">
			<xsl:apply-templates select="$content" mode="for"/>
		</ul>
	</xsl:template>

	<xsl:template match="@agentVariable" mode="for">
		<li>
			<xsl:value-of select="concat(.,' : ')"/>
			<xsl:call-template name="createOptionalLink">
				<xsl:with-param name="node" select="../.."/>
				<xsl:with-param name="text" select="../../@name"/>
			</xsl:call-template>
		</li>
	</xsl:template>

	<xsl:template match="aors:CommunicationRule/aors:FOR|aors:ReactionRule/aors:FOR" mode="for">
		<li>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:choose>
						<xsl:when test="@dataVariable">
							<xsl:value-of select="concat(@dataVariable,' : ')"/>
							<xsl:choose>
								<xsl:when test="@dataType">
									<xsl:value-of select="@dataType"/>
								</xsl:when>
								<xsl:when test="@refDataType">
									<xsl:call-template name="createOptionalLink">
										<xsl:with-param name="node" select="key('DataTypes',@refDataType)"/>
										<xsl:with-param name="text" select="@refDataType"/>
									</xsl:call-template>
								</xsl:when>
							</xsl:choose>
							<xsl:if test="aors:ValueExpr">
								<xsl:text> = </xsl:text>
								<xsl:choose>
									<xsl:when test="count(aors:ValueExpr) > 1 or string-length(normalize-space(aors:ValueExpr/text())) > number($expressionLength)">
										
										<xsl:call-template name="hideContent">
											<xsl:with-param name="content">
												<xsl:call-template name="copyCode">
													<xsl:with-param name="code" select="aors:ValueExpr"/>
												</xsl:call-template>
											</xsl:with-param>
											<xsl:with-param name="heading" select="'Expression'"/>
											<xsl:with-param name="headingPrefix" select="concat('Code of ',@dataVariable,'.')"/>
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
						</xsl:when>
						<xsl:when test="@beliefEntityVariable">
							<xsl:value-of select="concat(@beliefEntityVariable,' : ')"/>
							<xsl:call-template name="getOptionalValue">
								<xsl:with-param name="node">
									<xsl:choose>
										<xsl:when test="@beliefEntityType">
											<xsl:call-template name="createOptionalLink">
												<xsl:with-param name="node" select="key('EntityTypes',@beliefEntityType)"/>
												<xsl:with-param name="text" select="@beliefEntityType"/>
											</xsl:call-template>
										</xsl:when>
										<xsl:when test="aors:BeliefEntityType">
											
											<xsl:choose>
												<xsl:when test="count(aors:BeliefEntityType) > 1 or string-length(normalize-space(aors:BeliefEntityType/text())) > number($expressionLength)">
													
													<xsl:call-template name="hideContent">
														<xsl:with-param name="content">
															<xsl:call-template name="copyCode">
																<xsl:with-param name="code" select="aors:BeliefEntityType"/>
															</xsl:call-template>
														</xsl:with-param>
														<xsl:with-param name="heading" select="'type'"/>
														<xsl:with-param name="headingPrefix" select="concat('Code of ',@beliefEntityVariable,'.')"/>
													</xsl:call-template>
													
												</xsl:when>
												<xsl:otherwise>
													<xsl:call-template name="copyCode">
														<xsl:with-param name="code" select="aors:BeliefEntityType"/>
														<xsl:with-param name="class" select="'inline'"/>
													</xsl:call-template>
												</xsl:otherwise>
											</xsl:choose>
											
										</xsl:when>
									</xsl:choose>
								</xsl:with-param>
								<xsl:with-param name="copy" select="true()"/>
							</xsl:call-template>
							<xsl:choose>
								<xsl:when test="@beliefEntityIdRef">
									<xsl:value-of select="concat(' = getBeliefEntityById(',@beliefEntityIdRef,')')"/>
								</xsl:when>
								<xsl:when test="aors:BeliefEntityIdRef">
									<xsl:text> = </xsl:text>
									
									<xsl:choose>
										<xsl:when test="count(aors:BeliefEntityIdRef) > 1 or string-length(normalize-space(aors:BeliefEntityIdRef/text())) > number($expressionLength)">

											<xsl:call-template name="hideContent">
												<xsl:with-param name="content">
													<xsl:call-template name="copyCode">
														<xsl:with-param name="code" select="aors:BeliefEntityIdRef"/>
													</xsl:call-template>
												</xsl:with-param>
												<xsl:with-param name="heading" select="'getBeliefEntityById(Expression)'"/>
												<xsl:with-param name="headingPrefix" select="concat('Code of ',@beliefEntityVariable,'.')"/>
											</xsl:call-template>
											
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="copyCode">
												<xsl:with-param name="code" select="aors:BeliefEntityIdRef"/>
												<xsl:with-param name="class" select="'inline'"/>
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>
									
								</xsl:when>
							</xsl:choose>
						</xsl:when>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="copy" select="true()"/>
			</xsl:call-template>
		</li>
	</xsl:template>

	<xsl:template match="aors:EnvironmentRule/aors:FOR|aors:InitializationRule/aors:FOR" mode="for">
		<li>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:choose>
						<xsl:when test="@dataVariable">
							<xsl:value-of select="@dataVariable"/>
							<xsl:choose>
								<xsl:when test="@dataType">
									<xsl:value-of select="concat(' : ',@dataType)"/>
								</xsl:when>
								<xsl:when test="@refDataType">
									<xsl:text> : </xsl:text>
									<xsl:call-template name="createOptionalLink">
										<xsl:with-param name="node" select="key('DataTypes',@refDataType)"/>
										<xsl:with-param name="text" select="@refDataType"/>
									</xsl:call-template>
								</xsl:when>
							</xsl:choose>
							<xsl:if test="aors:ValueExpr">
								<xsl:text> = </xsl:text>
								<xsl:choose>
									<xsl:when test="count(aors:ValueExpr) > 1 or string-length(normalize-space(aors:ValueExpr/text())) > number($expressionLength)">
										<xsl:call-template name="hideContent">
											<xsl:with-param name="content">
												<xsl:call-template name="copyCode">
													<xsl:with-param name="code" select="aors:ValueExpr"/>
												</xsl:call-template>
											</xsl:with-param>
											<xsl:with-param name="heading" select="'Expression'"/>
											<xsl:with-param name="headingPrefix" select="concat('Code of ',@dataVariable,'.')"/>
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
						</xsl:when>
						<xsl:when test="@objectVariable">
							<xsl:value-of select="@objectVariable"/>
							<xsl:if test="@objectType">
								<xsl:text> : </xsl:text>
								<xsl:call-template name="createOptionalLink">
									<xsl:with-param name="node" select="key('Types',@objectType)"/>
									<xsl:with-param name="text" select="@objectType"/>
								</xsl:call-template>
							</xsl:if>
							<xsl:choose>
								<xsl:when test="@rangeStartId and @rangeEndId">
									<xsl:value-of select="concat(' = [',@rangeStartId,',',@rangeEndId,']')"/>
								</xsl:when>
								<xsl:when test="@objectIdRef">
									<xsl:value-of select="concat(' = getObjectById(',@objectIdRef,')')"/>
								</xsl:when>
								<xsl:when test="aors:ObjectIdRef">
									<xsl:text> = </xsl:text>
									<xsl:choose>
										<xsl:when test="count(aors:ObjectIdRef) > 1 or string-length(normalize-space(aors:ObjectIdRef/text())) > number($expressionLength)">
											<xsl:call-template name="hideContent">
												<xsl:with-param name="content">
													<xsl:call-template name="copyCode">
														<xsl:with-param name="code" select="aors:ObjectIdRef"/>
													</xsl:call-template>
												</xsl:with-param>
												<xsl:with-param name="heading" select="'getObjectById(Expression)'"/>
												<xsl:with-param name="headingPrefix" select="concat('Code of ',@objectVariable,'.')"/>
											</xsl:call-template>
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="copyCode">
												<xsl:with-param name="code" select="aors:ObjectIdRef"/>
												<xsl:with-param name="class" select="'inline'"/>
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
								<xsl:when test="@objectName">
									<xsl:text> = </xsl:text>
									<xsl:call-template name="createOptionalLink">
										<xsl:with-param name="node" select="key('Types',@objectName)"/>
										<xsl:with-param name="text" select="@objectName"/>
									</xsl:call-template>
								</xsl:when>
								<xsl:when test="aors:ObjectRef">
									<xsl:text> = </xsl:text>
									<xsl:choose>
										<xsl:when test="count(aors:ObjectRef) > 1 or string-length(normalize-space(aors:ObjectRef/text())) > number($expressionLength)">
											<xsl:call-template name="hideContent">
												<xsl:with-param name="content">
													<xsl:call-template name="copyCode">
														<xsl:with-param name="code" select="aors:ObjectRef"/>
													</xsl:call-template>
												</xsl:with-param>
												<xsl:with-param name="heading" select="'Expression'"/>
												<xsl:with-param name="headingPrefix" select="concat('Code of ',@objectVariable,'.')"/>
											</xsl:call-template>
										</xsl:when>
										<xsl:otherwise>
											<xsl:call-template name="copyCode">
												<xsl:with-param name="code" select="aors:ObjectRef"/>
												<xsl:with-param name="class" select="'inline'"/>
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:when>
							</xsl:choose>
						</xsl:when>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="copy" select="true()"/>
			</xsl:call-template>
		</li>
	</xsl:template>
	
	<!-- DO -->
	
	<xsl:template match="aors:DO" mode="content">
		<xsl:apply-templates select="aors:*" mode="doThenElse"/>
	</xsl:template>

	<!-- IF -->

	<xsl:template match="aors:IF" mode="content">
		<xsl:param name="content"/>
		<!--xsl:choose>
			<xsl:when test="count($content) > 1 or string-length(normalize-space($content/text())) > number($expressionLength)">
				<xsl:call-template name="hideContent">
					<xsl:with-param name="content">
						<xsl:call-template name="copyCode">
							<xsl:with-param name="code" select="$content"/>
						</xsl:call-template>
					</xsl:with-param>
					<xsl:with-param name="heading" select="'Condition'"/>
					<xsl:with-param name="headingPrefix" select="concat('Code of ',../@name,'.')"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise-->
				<xsl:call-template name="copyCode">
					<xsl:with-param name="code" select="$content"/>
				</xsl:call-template>
			<!--/xsl:otherwise>
		</xsl:choose-->
	</xsl:template>

	<!-- THEN -->
	
	<xsl:template match="aors:THEN" mode="content">
		<xsl:apply-templates select="aors:*" mode="doThenElse"/>
	</xsl:template>

	<xsl:template match="aors:UPDATE-AGT|aors:UPDATE-ENV|aors:SCHEDULE-EVT|aors:CREATE-EVT" mode="section2Content">
		<xsl:param name="content"/>
		<xsl:apply-templates select="$content" mode="doThenElse"/>
	</xsl:template>

	<xsl:template match="aors:InitializationRule/aors:UpdateObject|aors:InitializationRule/aors:UpdateObjects|aors:InitializationRule/aors:UpdateGridCell" mode="section2Content">
		<xsl:param name="content"/>
		<xsl:apply-templates select="$content[1]" mode="doThenElse">
			<xsl:with-param name="content" select="$content"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<!-- ELSE -->

	<xsl:template match="aors:ELSE" mode="content">
		<xsl:apply-templates select="aors:*" mode="doThenElse"/>
	</xsl:template>

	<!-- UPDATE-AGT -->

	<xsl:template match="aors:UPDATE-AGT" mode="doThenElse">
		<xsl:variable name="updateSlots" select="aors:Slot|aors:SelfBeliefSlot"/>
		<xsl:apply-templates select="$updateSlots[1]" mode="section3">
			<xsl:with-param name="content" select="$updateSlots"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:UpdateComplexDataPropertyValue[1]" mode="section3">
			<xsl:with-param name="content" select="aors:UpdateComplexDataPropertyValue"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:UpdateBeliefEntity[1]" mode="section3">
			<xsl:with-param name="content" select="aors:UpdateBeliefEntity"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:CreateBeliefEntity[1]" mode="section3">
			<xsl:with-param name="content" select="aors:CreateBeliefEntity"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:DestroyBeliefEntity[1]" mode="section3">
			<xsl:with-param name="content" select="aors:DestroyBeliefEntity"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- UPDATE-AGT/Slot | UPDATE-AGT/SelfBeliefSlot -->

	<xsl:template match="aors:UPDATE-AGT/aors:Slot|aors:UPDATE-AGT/aors:SelfBeliefSlot" mode="content">
		<xsl:param name="content"/>
		<ul class="update">
			<xsl:apply-templates select="$content" mode="update"/>
		</ul>
	</xsl:template>

	<xsl:template match="aors:UPDATE-AGT/aors:Slot" mode="update">
		<li>
			<xsl:apply-templates select="." mode="slot">
				<xsl:with-param name="maybeInline" select="true()"/>
			</xsl:apply-templates>
		</li>
	</xsl:template>

	<xsl:template match="aors:UPDATE-AGT/aors:SelfBeliefSlot" mode="update">
		<li>
			<xsl:text>[SB] </xsl:text>
			<xsl:apply-templates select="." mode="slot">
				<xsl:with-param name="maybeInline" select="true()"/>
			</xsl:apply-templates>
		</li>
	</xsl:template>
	
	<!-- UPDATE-AGT/UpdateComplexDataProperty -->
	
	<xsl:template match="aors:UPDATE-AGT/aors:UpdateComplexDataPropertyValue" mode="content">
		<xsl:param name="content"/>
		<ul class="update">
			<xsl:apply-templates select="." mode="complexDataProperty"/>
		</ul>
	</xsl:template>
	
	<xsl:template match="aors:UpdateComplexDataPropertyValue" mode="complexDataProperty">
		<li>
			<xsl:value-of select="concat(@complexDataProperty,'.',@procedure,'(')"/>
			<xsl:apply-templates select="aors:Argument" mode="complexDataProperty"/>
			<xsl:text>)</xsl:text>
		</li>
	</xsl:template>
	
	<xsl:template match="aors:UpdateComplexDataPropertyValue/aors:Argument" mode="complexDataProperty">
		<xsl:variable name="argument" select="concat('Argument',count(preceding-sibling::aors:Argument) + 1)"/>
		<xsl:if test="$argument != 'Argument1'">
			<xsl:text>, </xsl:text>
		</xsl:if>
		<xsl:call-template name="hideContent">
			<xsl:with-param name="content">
				<xsl:call-template name="copyCode">
					<xsl:with-param name="code" select="aors:ValueExpr"/>
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="heading" select="$argument"/>
			<xsl:with-param name="headingPrefix" select="concat('Code of ',../@complexDataProperty,'.',../@procedure)"/>
		</xsl:call-template>
	</xsl:template>

	<!-- UPDATE-AGT/UpdateBeliefEntity -->

	<xsl:template match="aors:UPDATE-AGT/aors:UpdateBeliefEntity" mode="content">
		<xsl:param name="content"/>
		<ul class="update">
			<xsl:apply-templates select="$content" mode="update"/>
		</ul>
	</xsl:template>

	<xsl:template match="aors:UPDATE-AGT/aors:UpdateBeliefEntity" mode="update">
		<xsl:apply-templates select="aors:BeliefSlot|aors:Increment|aors:Decrement" mode="update"/>
	</xsl:template>
	
	<xsl:template match="aors:UPDATE-AGT/aors:UpdateBeliefEntity/aors:BeliefSlot|aors:UPDATE-AGT/aors:UpdateBeliefEntity/aors:Increment|aors:UPDATE-AGT/aors:UpdateBeliefEntity/aors:Decrement" mode="update">
		<li>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:choose>
						<xsl:when test="../@beliefEntityVariable">
							<xsl:value-of select="../@beliefEntityVariable"/>
						</xsl:when>
						<xsl:when test="../@beliefEntityIfRef">
							<xsl:value-of select="concat('[',../@beliefEntityIdRef,']')"/>
						</xsl:when>
						<xsl:when test="../aors:BeliefEntityIdRef">
							<xsl:text>[</xsl:text>
							<xsl:call-template name="hideContent">
								<xsl:with-param name="content">
									<xsl:call-template name="copyCode">
										<xsl:with-param name="code" select="../aors:BeliefEntityIdRef"/>
									</xsl:call-template>
								</xsl:with-param>
								<xsl:with-param name="heading" select="'id'"/>
								<xsl:with-param name="headingPrefix" select="'Code of '"/>
							</xsl:call-template>
							<xsl:text>]</xsl:text>
						</xsl:when>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="copy" select="true()"/>
			</xsl:call-template>			
			<xsl:apply-templates select="." mode="slot">
				<xsl:with-param name="maybeInline" select="true()"/>
			</xsl:apply-templates>
		</li>
	</xsl:template>

	<!-- UPDATE-AGT/CreateBeliefEntity -->

	<xsl:template match="aors:UPDATE-AGT/aors:CreateBeliefEntity" mode="section3Content">
		<xsl:param name="content"/>
		<xsl:apply-templates select="$content" mode="class">
			<xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
			<xsl:with-param name="headingElement" select="$section4Heading"/>
			<xsl:with-param name="kind" select="'object'"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:UPDATE-AGT/aors:CreateBeliefEntity" mode="classContent">
		<xsl:apply-templates select="aors:BeliefSlot[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section5Heading"/>
			<xsl:with-param name="class" select="'properties'"/>
			<xsl:with-param name="content" select="aors:BeliefSlot"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- UPDATE-AGT/DestroyBeliefEntity -->

	<xsl:template match="aors:UPDATE-AGT/aors:DestroyBeliefEntity" mode="content">
		<xsl:param name="content"/>
		<ul class="destroy">
			<xsl:apply-templates select="$content" mode="destroy"/>
		</ul>
	</xsl:template>

	<xsl:template match="aors:UPDATE-AGT/aors:DestroyBeliefEntity" mode="destroy">
		<li>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:choose>
						<xsl:when test="@beliefEntityVariable">
							<xsl:value-of select="@beliefEntityVariable"/>
						</xsl:when>
						<xsl:when test="@beliefEntityIdRef">
							<xsl:value-of select="concat('[',@beliefEntityIdRef,']')"/>
						</xsl:when>
						<xsl:when test="aors:BeliefEntityIdRef">
							<xsl:text>[</xsl:text>
							<xsl:call-template name="hideContent">
								<xsl:with-param name="content">
									<xsl:call-template name="copyCode">
										<xsl:with-param name="code" select="aors:BeliefEntityIdRef"/>
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
		</li>
	</xsl:template>

	<!-- UPDATE-ENV || InitializationRule-->

	<xsl:template match="aors:UPDATE-ENV" mode="doThenElse">
		<xsl:variable name="updateObjects" select="aors:UpdateObject|aors:UpdateObjects"/>
		<xsl:apply-templates select="$updateObjects[1]" mode="section3">
			<xsl:with-param name="content" select="$updateObjects"/>
		</xsl:apply-templates>
		<xsl:variable name="updateGridCells" select="aors:UpdateGridCell|aors:UpdateGridCells"/>
		<xsl:apply-templates select="$updateGridCells[1]" mode="section3">
			<xsl:with-param name="content" select="$updateGridCells"/>
		</xsl:apply-templates>
		<xsl:variable name="updateGlobalVariables" select="aors:UpdateGlobalVariable|aors:IncrementGlobalVariable"/>
		<xsl:apply-templates select="$updateGlobalVariables[1]" mode="section3">
			<xsl:with-param name="content" select="$updateGlobalVariables"/>
		</xsl:apply-templates>
		<xsl:variable name="updateStatisticsVariables" select="aors:UpdateStatisticsVariable"/>
		<xsl:apply-templates select="$updateStatisticsVariables[1]" mode="section3">
			<xsl:with-param name="content" select="$updateStatisticsVariables"/>
		</xsl:apply-templates>
		<xsl:variable name="updateCollections" select="aors:AddObjectToCollection|aors:RemoveObjectFromCollection"/>
		<xsl:apply-templates select="$updateCollections[1]" mode="section3">
			<xsl:with-param name="content" select="$updateCollections"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:Create[1]" mode="section3">
			<xsl:with-param name="content" select="aors:Create/aors:*"/>
		</xsl:apply-templates>
		<xsl:variable name="destroyObjects" select="aors:DestroyObject|aors:DestroyObjects"/>
		<xsl:apply-templates select="$destroyObjects[1]" mode="section3">
			<xsl:with-param name="content" select="$destroyObjects"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:InitializationRule/aors:UpdateObject|aors:InitializationRule/aors:UpdateObjects|aors:InitializationRule/aors:UpdateGridCell" mode="doThenElse">
		<xsl:param name="content"/>
		<ul class="update">
			<xsl:apply-templates select="$content" mode="update"/>
		</ul>
	</xsl:template>
	
	<!-- UPDATE-ENV/UpdateObject | UPDATE-ENV/UpdateObjects | UPDATE-ENV/UpdateGridCell | UPDATE-ENV/UpdateGridCells -->

	<xsl:template match="aors:UPDATE-ENV/aors:UpdateObject|aors:UPDATE-ENV/aors:UpdateObjects|aors:UPDATE-ENV/aors:UpdateGridCell|aors:UPDATE-ENV/aors:UpdateGridCells|aors:InitializationRule/aors:UpdateObject|aors:InitializationRule/aors:UpdateObjects|aors:InitializationRule/aors:UpdateGridCell|aors:InitializationRule/aors:UpdateGridCells" mode="content">
		<xsl:param name="content"/>
		<ul class="update">
			<xsl:apply-templates select="$content" mode="update"/>
		</ul>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:UpdateObject|aors:InitializationRule/aors:UpdateObject" mode="update">
		<xsl:apply-templates select="aors:Slot|aors:Increment|aors:Decrement" mode="update"/>
	</xsl:template>
	
	<xsl:template match="aors:UPDATE-ENV/aors:UpdateObject/aors:Slot|aors:UPDATE-ENV/aors:UpdateObject/aors:Increment|aors:UPDATE-ENV/aors:UpdateObject/aors:Decrement|aors:InitializationRule/aors:UpdateObject/aors:Slot|aors:InitializationRule/aors:UpdateObject/aors:Increment|aors:InitializationRule/aors:UpdateObject/aors:Decrement" mode="update">
		<li>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:choose>
						<xsl:when test="../@objectVariable">
							<xsl:value-of select="concat(../@objectVariable,'.')"/>
						</xsl:when>
						<xsl:when test="../aors:ObjectRef">
							<xsl:text>(</xsl:text>
							<xsl:call-template name="createOptionalLink">
								<xsl:with-param name="node" select="key('EntityTypes',../aors:ObjectRef[1]/@objectType)"/>
								<xsl:with-param name="text" select="../aors:ObjectRef[1]/@objectType"/>
							</xsl:call-template>
							<xsl:text>)</xsl:text>
							<xsl:call-template name="hideContent">
								<xsl:with-param name="content">
									<xsl:call-template name="copyCode">
										<xsl:with-param name="code" select="../aors:ObjectRef"/>
									</xsl:call-template>
								</xsl:with-param>
								<xsl:with-param name="heading" select="'object'"/>
								<xsl:with-param name="headingPrefix" select="'Code of '"/>
							</xsl:call-template>
							<xsl:text>.</xsl:text>
						</xsl:when>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="copy" select="true()"/>
			</xsl:call-template>
			<xsl:apply-templates select="." mode="slot">
				<xsl:with-param name="maybeInline" select="true()"/>
			</xsl:apply-templates>
		</li>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:UpdateObjects|aors:InitializationRule/aors:UpdateObjects" mode="update">
		<xsl:if test="aors:Slot|aors:Increment|aors:Decrement">
			<li>
				<xsl:value-of select="concat('FOR EACH ',@objectVariable,' &#8712; [')"/>
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
				<xsl:text>] AS </xsl:text>
				<xsl:call-template name="createOptionalLink">
					<xsl:with-param name="node" select="key('EntityTypes',@objectType)"/>
					<xsl:with-param name="text" select="@objectType"/>
				</xsl:call-template>
				<xsl:if test="@loopVariable">
					<xsl:value-of select="concat(' (loop variable: ',@loopVariable,')')"/>
				</xsl:if>
				<xsl:text> DO</xsl:text>
				<ul>
					<xsl:apply-templates select="aors:Slot|aors:Increment|aors:Decrement" mode="update"/>
				</ul>
			</li>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="aors:UPDATE-ENV/aors:UpdateObjects/aors:Slot|aors:UPDATE-ENV/aors:UpdateObjects/aors:Increment|aors:UPDATE-ENV/aors:UpdateObjects/aors:Decrement|aors:InitializationRule/aors:UpdateObjects/aors:Slot|aors:InitializationRule/aors:UpdateObjects/aors:Increment|aors:InitializationRule/aors:UpdateObjects/aors:Decrement" mode="update">
		<li>
			<xsl:value-of select="concat(../@objectVariable,'.')"/>
			<xsl:apply-templates select="aors:Slot|aors:Increment|aors:Decrement" mode="slot">
				<xsl:with-param name="maybeInline" select="true()"/>
			</xsl:apply-templates>
		</li>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:UpdateGridCell|aors:InitializationRule/aors:UpdateGridCell" mode="update">
		<xsl:apply-templates select="aors:Slot|aors:Increment|aors:Decrement" mode="update"/>
	</xsl:template>
	
		
	<xsl:template match="aors:UPDATE-ENV/aors:UpdateGridCell/aors:Slot|aors:UPDATE-ENV/aors:UpdateGridCell/aors:Increment|aors:UPDATE-ENV/aors:UpdateGridCell/aors:Decrement|aors:InitializationRule/aors:UpdateGridCell/aors:Slot|aors:InitializationRule/aors:UpdateGridCell/aors:Increment|aors:InitializationRule/aors:UpdateGridCell/aors:Decrement" mode="update">
		<li>
			<xsl:if test="../@gridCellVariable">
				<xsl:value-of select="concat('{',../@gridCellVariable,'=')"/>
			</xsl:if>
			<xsl:text>(</xsl:text>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:call-template name="hideContent">
						<xsl:with-param name="content">
							<xsl:call-template name="copyCode">
								<xsl:with-param name="code" select="../aors:XCoordinate"/>
							</xsl:call-template>
						</xsl:with-param>
						<xsl:with-param name="heading" select="'x'"/>
						<xsl:with-param name="headingPrefix" select="'Code of '"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="copy" select="true()"/>
			</xsl:call-template>
			<xsl:text>,</xsl:text>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:call-template name="hideContent">
						<xsl:with-param name="content">
							<xsl:call-template name="copyCode">
								<xsl:with-param name="code" select="../aors:YCoordinate"/>
							</xsl:call-template>
						</xsl:with-param>
						<xsl:with-param name="heading" select="'y'"/>
						<xsl:with-param name="headingPrefix" select="'Code of '"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="copy" select="true()"/>
			</xsl:call-template>
			<xsl:text>)</xsl:text>
			<xsl:if test="../@gridCellVariable">
				<xsl:text>}</xsl:text>
			</xsl:if>
			<xsl:text>.</xsl:text>
			<xsl:apply-templates select="." mode="slot">
				<xsl:with-param name="maybeInline" select="true()"/>
			</xsl:apply-templates>
		</li>
	</xsl:template>
	
	<xsl:template match="aors:UPDATE-ENV/aors:UpdateGridCells|aors:InitializationRule/aors:UpdateGridCells" mode="update">
		<xsl:if test="aors:Slot|aors:Decrement|aors:Increment">
			<li>
				<xsl:text>FOR EACH  </xsl:text>
				<xsl:if test="@gridCellVariable">
					<xsl:value-of select="concat(@gridCellVariable,' &#8712; ')"/>
				</xsl:if>
				<xsl:text>[(</xsl:text>
				<xsl:call-template name="getOptionalValue">
					<xsl:with-param name="node" select="@startX"/>				
				</xsl:call-template>
				<xsl:text>,</xsl:text>
				<xsl:call-template name="getOptionalValue">
					<xsl:with-param name="node" select="@startY"/>
				</xsl:call-template>
				<xsl:text>) , (</xsl:text>
				<xsl:call-template name="getOptionalValue">
					<xsl:with-param name="node" select="@endX"/>				
				</xsl:call-template>
				<xsl:text>,</xsl:text>
				<xsl:call-template name="getOptionalValue">
					<xsl:with-param name="node" select="@endY"/>
				</xsl:call-template>
				<xsl:text>)] DO</xsl:text>
				<ul>
					<xsl:apply-templates select="aors:Slot|aors:Increment|aors:Decrement" mode="update"/>
				</ul>
			</li>
		</xsl:if>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:UpdateGridCells/aors:Slot|aors:UPDATE-ENV/aors:UpdateGridCells/aors:Increment|aors:UPDATE-ENV/aors:UpdateGridCells/aors:Decrement|aors:InitializationRule/aors:UpdateGridCells/aors:Slot|aors:InitializationRule/aors:UpdateGridCells/aors:Increment|aors:InitializationRule/aors:UpdateGridCells/aors:Decrement" mode="update">
		<li>
			<xsl:apply-templates select="." mode="slot">
				<xsl:with-param name="maybeInline" select="true()"/>
			</xsl:apply-templates>
		</li>
	</xsl:template>

	<!-- UPDATE-ENV/UpdateGloalVariable | UPDATE-ENV/IncrementGlobalVariable | UPDATE-ENV/UpdateStatistcsVariable -->

	<xsl:template match="aors:UPDATE-ENV/aors:UpdateGlobalVariable|aors:UPDATE-ENV/aors:IncrementGlobalVariable|aors:UPDATE-ENV/aors:UpdateStatisticsVariable" mode="content">
		<xsl:param name="content"/>
		<ul class="update">
			<xsl:apply-templates select="$content" mode="update"/>
		</ul>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:UpdateGlobalVariable" mode="update">
		<li>
			<xsl:value-of select="concat(@name,' = ')"/>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:choose>
						<xsl:when test="@value">
							<xsl:value-of select="@value"/>
						</xsl:when>
						<xsl:when test="aors:ValueExpr">
							<xsl:choose>
								<xsl:when test="count(aors:ValueExpr) > 1 or string-length(normalize-space(aors:ValueExpr/text())) > number($expressionLength)">
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
		</li>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:IncrementGlobalVariable" mode="update">
		<li>
			<xsl:value-of select="concat(@name,' + ',@value)"/>
		</li>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:UpdateStatisticsVariable" mode="update">
		<li>
			<xsl:value-of select="concat(@variable,' = ')"/>
			<xsl:choose>
				<xsl:when test="count(aors:ValueExpr) > 1 or string-length(normalize-space(aors:ValueExpr/text())) > number($expressionLength)">
					<xsl:call-template name="hideContent">
						<xsl:with-param name="content">
							<xsl:call-template name="copyCode">
								<xsl:with-param name="code" select="aors:ValueExpr"/>
							</xsl:call-template>
						</xsl:with-param>
						<xsl:with-param name="heading" select="'Expression'"/>
						<xsl:with-param name="headingPrefix" select="concat('Code of ',@variable,'.')"/>
					</xsl:call-template>					
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="copyCode">
						<xsl:with-param name="code" select="aors:ValueExpr"/>
						<xsl:with-param name="class" select="'inline'"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</li>
	</xsl:template>

	<!-- UPDATE-ENV/AddObjectToCollection | UPDATE-ENV/RemoveObjectFromCollection -->

	<xsl:template match="aors:UPDATE-ENV/aors:AddObjectToCollection|aors:UPDATE-ENV/aors:RemoveObjectFromCollection" mode="content">
		<xsl:param name="content"/>
		<ul class="update">
			<xsl:apply-templates select="$content" mode="update"/>
		</ul>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:AddObjectToCollection" mode="update">
		<li>
			<xsl:text>ADD </xsl:text>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:choose>
						<xsl:when test="@itemObjectVariable">
							<xsl:value-of select="@itemObjectVariable"/>
						</xsl:when>
						<xsl:when test="aors:ItemObjectRef">
							<xsl:call-template name="hideContent">
								<xsl:with-param name="content">
									<xsl:call-template name="copyCode">
										<xsl:with-param name="code" select="aors:ItemObjectRef"/>
									</xsl:call-template>
								</xsl:with-param>
								<xsl:with-param name="heading" select="'object'"/>
								<xsl:with-param name="headingPrefix" select="'Code of '"/>
							</xsl:call-template>
						</xsl:when>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="defaultValue" select="'object'"/>
				<xsl:with-param name="copy" select="true()"/>
			</xsl:call-template>
			<xsl:text> TO </xsl:text>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:choose>
						<xsl:when test="@collectionName">
							<xsl:call-template name="createOptionalLink">
								<xsl:with-param name="node" select="key('Collections',@collectionName)"/>
								<xsl:with-param name="text" select="@collectionName"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:when test="@collectionID">
							<xsl:value-of select="concat('collection[',@collectionID,']')"/>
						</xsl:when>
						<xsl:when test="@collectionObjectVariable">
							<xsl:value-of select="@collectionObjectVariable"/>
						</xsl:when>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="defaultValue" select="'collection'"/>
				<xsl:with-param name="copy" select="true()"/>
			</xsl:call-template>
		</li>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:RemoveObjectFromCollection" mode="update">
		<li>
			<xsl:text>REMOVE</xsl:text>
			<xsl:if test="@destroyObject = 'true'">
				<xsl:text> AND DESTROY</xsl:text>
			</xsl:if>
			<xsl:text> </xsl:text>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:choose>
						<xsl:when test="@itemObjectVariable">
							<xsl:value-of select="@itemObjectVariable"/>
						</xsl:when>
						<xsl:when test="aors:ItemObjectRef">
							<xsl:call-template name="hideContent">
								<xsl:with-param name="content">
									<xsl:call-template name="copyCode">
										<xsl:with-param name="code" select="aors:ItemObjectRef"/>
									</xsl:call-template>
								</xsl:with-param>
								<xsl:with-param name="heading" select="'object'"/>
								<xsl:with-param name="headingPrefix" select="'Code of '"/>
							</xsl:call-template>
						</xsl:when>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="defaultValue" select="'object'"/>
				<xsl:with-param name="copy" select="true()"/>
			</xsl:call-template>
			<xsl:text> FROM </xsl:text>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:choose>
						<xsl:when test="@collectionName">
							<xsl:call-template name="createOptionalLink">
								<xsl:with-param name="node" select="key('Collections',@collectionName)"/>
								<xsl:with-param name="text" select="@collectionName"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:when test="@collectionID">
							<xsl:value-of select="concat('collection[',@collectionID,']')"/>
						</xsl:when>
						<xsl:when test="@collectionObjectVariable">
							<xsl:value-of select="@collectionObjectVariable"/>
						</xsl:when>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="defaultValue" select="'collection'"/>
				<xsl:with-param name="copy" select="true()"/>
			</xsl:call-template>
		</li>
	</xsl:template>

	<!-- UPDATE-ENV/Create -->

	<xsl:template match="aors:UPDATE-ENV/aors:Create" mode="content">
		<xsl:param name="content"/>
		<xsl:apply-templates select="$content" mode="create">
			<xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:Create/aors:Object|aors:UPDATE-ENV/aors:Create/aors:PhysicalObject|aors:UPDATE-ENV/aors:Create/aors:Agent|aors:UPDATE-ENV/aors:Create/aors:PhysicalAgent|aors:UPDATE-ENV/aors:Create/aors:Collection" mode="create">
		<xsl:apply-templates select="." mode="class">
			<xsl:with-param name="headingElement" select="$section4Heading"/>
			<xsl:with-param name="kind" select="'object'"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:Create/aors:Collection" mode="create">
		<xsl:apply-templates select="." mode="class">
			<xsl:with-param name="headingElement" select="$section4Heading"/>
			<xsl:with-param name="class" select="'parameterized'"/>
			<xsl:with-param name="kind" select="'object'"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:Create/aors:Objects|aors:UPDATE-ENV/aors:Create/aors:PhysicalObjects|aors:UPDATE-ENV/aors:Create/aors:Agents|aors:UPDATE-ENV/aors:Create/aors:PhysicalAgents" mode="create">
		<xsl:apply-templates select="." mode="class">
			<xsl:with-param name="headingElement" select="$section4Heading"/>
			<xsl:with-param name="kind" select="'objects'"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:Create/aors:*" mode="class">
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

	<xsl:template match="aors:UPDATE-ENV/aors:Create/aors:*" mode="classContent">
		<xsl:variable name="properties" select="@*[local-name()!='type' and local-name()!='id' and local-name()!='name' and local-name()!='addToCollection' and local-name()!='rangeStartID' and local-name()!='rangeEndID' and local-name()!='objectVariable' and local-name()!='creationLoopVar' and local-name()!='itemType']|aors:Slot"/>
		<xsl:apply-templates select="$properties[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section5Heading"/>
			<xsl:with-param name="class" select="'properties'"/>
			<xsl:with-param name="content" select="$properties"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:SelfBeliefSlot[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section5Heading"/>
			<xsl:with-param name="class" select="'selfBeliefProperties'"/>
			<xsl:with-param name="content" select="aors:SelfBeliefSlot"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:BeliefEntity[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section5Heading"/>
			<xsl:with-param name="class" select="'beliefEntities'"/>
			<xsl:with-param name="content" select="aors:BeliefEntity"/>
		</xsl:apply-templates>
		<xsl:variable name="events" select="aors:ReminderEvent|aors:TimeEvent|aors:PeriodicTimeEvent"/>
		<xsl:apply-templates select="$events[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section5Heading"/>
			<xsl:with-param name="class" select="'events'"/>
			<xsl:with-param name="content" select="$events"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:Create/aors:*/aors:BeliefEntity" mode="classContent">
		<xsl:apply-templates select="aors:BeliefSlot[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section5Heading"/>
			<xsl:with-param name="class" select="'properties'"/>
			<xsl:with-param name="content" select="aors:BeliefSlot"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:Create/aors:*/aors:ReminderEvent|aors:UPDATE-ENV/aors:Create/aors:*/aors:TimeEvent|aors:UPDATE-ENV/aors:Create/aors:*/aors:PeriodicTimeEvent" mode="classContent">
		<xsl:variable name="properties" select="@*[local-name()!='type' and local-name()!='id' and local-name()!='name']|aors:Slot"/>
		<xsl:apply-templates select="$properties[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section5Heading"/>
			<xsl:with-param name="class" select="'properties'"/>
			<xsl:with-param name="content" select="$properties"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:Create/aors:*/aors:BeliefEntity|aors:UPDATE-ENV/aors:Create/aors:*/aors:ReminderEvent|aors:UPDATE-ENV/aors:Create/aors:*/aors:TimeEvent|aors:UPDATE-ENV/aors:Create/aors:*/aors:PeriodicTimeEvent" mode="classSectionContent">
		<xsl:param name="content"/>
		<ul>
			<xsl:apply-templates select="$content" mode="objectComponent"/>
		</ul>
	</xsl:template>

	<!-- UPDATE-ENV/DestroyObject | UPDATE-ENV/DestroyObjects -->

	<xsl:template match="aors:UPDATE-ENV/aors:DestroyObject|aors:UPDATE-ENV/aors:DestroyObjects" mode="content">
		<xsl:param name="content"/>
		<ul class="destroy">
			<xsl:apply-templates select="$content" mode="destroy"/>
		</ul>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:DestroyObject" mode="destroy">
		<li>
			<xsl:text>DESTROY</xsl:text>
			<xsl:text> </xsl:text>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:choose>
						<xsl:when test="@objectVariable">
							<xsl:value-of select="@objectVariable"/>
						</xsl:when>
						<xsl:when test="aors:ObjectRef">
							<xsl:call-template name="hideContent">
								<xsl:with-param name="content">
									<xsl:call-template name="copyCode">
										<xsl:with-param name="code" select="aors:ObjectRef"/>
									</xsl:call-template>
								</xsl:with-param>
								<xsl:with-param name="heading" select="'object'"/>
								<xsl:with-param name="headingPrefix" select="'Code of '"/>
							</xsl:call-template>
							<xsl:text> AS </xsl:text>
							<xsl:call-template name="createOptionalLink">
								<xsl:with-param name="node" select="key('EntityTypes',@objectType)"/>
								<xsl:with-param name="text" select="@objectType"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:when test="@objectIdRef">
							<xsl:value-of select="concat('[',@objectIdRef,'] as ')"/>
							<xsl:call-template name="createOptionalLink">
								<xsl:with-param name="node" select="key('EntityTypes',@objectType)"/>
								<xsl:with-param name="text" select="@objectType"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:when test="aors:ObjectIdRef">
							<xsl:text>[</xsl:text>
							<xsl:call-template name="hideContent">
								<xsl:with-param name="content">
									<xsl:call-template name="copyCode">
										<xsl:with-param name="code" select="aors:ObjectIdRef"/>
									</xsl:call-template>
								</xsl:with-param>
								<xsl:with-param name="heading" select="'ID'"/>
								<xsl:with-param name="headingPrefix" select="'Code of '"/>
							</xsl:call-template>
							<xsl:text>] AS </xsl:text>
							<xsl:call-template name="createOptionalLink">
								<xsl:with-param name="node" select="key('EntityTypes',@objectType)"/>
								<xsl:with-param name="text" select="@objectType"/>
							</xsl:call-template>
						</xsl:when>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="defaultValue" select="'object'"/>
				<xsl:with-param name="copy" select="true()"/>
			</xsl:call-template>
			<xsl:if test="@deferred = 'true'">
				<xsl:text> AT END</xsl:text>
			</xsl:if>
			<xsl:if test="@removeFromCollection">
				<xsl:text> AND REMOVE FROM </xsl:text>
				<xsl:call-template name="createOptionalLink">
					<xsl:with-param name="node" select="key('Collections',@removeFromCollection)"/>
					<xsl:with-param name="text" select="@removeFromCollection"/>
				</xsl:call-template>
			</xsl:if>
		</li>
	</xsl:template>

	<xsl:template match="aors:UPDATE-ENV/aors:DestroyObjects" mode="destroy">
		<li>
			<xsl:text>DESTROY</xsl:text>
			<xsl:text> [</xsl:text>
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
			<xsl:text>] AS </xsl:text>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:call-template name="createOptionalLink">
						<xsl:with-param name="node" select="key('EntityTypes',@objectType)"/>
						<xsl:with-param name="text" select="@objectType"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="copy" select="true()"/>
			</xsl:call-template>
			<xsl:if test="@deferred = 'true'">
				<xsl:text> AT END</xsl:text>
			</xsl:if>
			<xsl:if test="@removeFromCollection">
				<xsl:text> AND REMOVE FROM </xsl:text>
				<xsl:call-template name="createOptionalLink">
					<xsl:with-param name="node" select="key('Collections',@removeFromCollection)"/>
					<xsl:with-param name="text" select="@removeFromCollection"/>
				</xsl:call-template>
			</xsl:if>
		</li>
	</xsl:template>

	<!-- UPDATE//Increment | UPDATE//Decrement -->

	<xsl:template match="aors:Increment|aors:Decrement" mode="update">
		<dd>
			<xsl:apply-templates select="." mode="slot"/>
		</dd>
	</xsl:template>

	<!-- SCHEDULE-EVT -->

	<xsl:template match="aors:SCHEDULE-EVT" mode="doThenElse">
		<xsl:apply-templates select="." mode="section3"/>
	</xsl:template>

	<xsl:template match="aors:SCHEDULE-EVT" mode="section3Content">
		<xsl:apply-templates select="aors:ActionEventExpr|aors:OutMessageEventExpr|aors:ReminderEventExpr|aors:CausedEventExpr|aors:PerceptionEventExpr|aors:InMessageEventExpr|aors:ActivityStartEventExpr|aors:ActivityEndEventExpr" mode="class">
			<xsl:with-param name="headingElement" select="$section4Heading"/>
			<xsl:with-param name="kind" select="'object'"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:ActionEventExpr|aors:OutMessageEventExpr|aors:ReminderEventExpr|aors:CausedEventExpr|aors:PerceptionEventExpr|aors:InMessageEventExpr|aors:ActivityStartEventExpr|aors:ActivityEndEventExpr" mode="classContent">
		<xsl:variable name="properties" select="@*[local-name()!='actionEventType' and local-name()!='messageType' and local-name()!='eventType' and local-name()!='activityType']|aors:Slot|aors:ReceiverIdRef[1]|aors:ReminderMsg[1]|aors:Delay[1]|aors:PerceiverIdRef[1]|aors:SenderIdRef[1]|aors:CorrelationValue[1]|aors:StartEventCorrelationProperty[1]|aors:EndEventCorrelationProperty[1]"/>
		<xsl:apply-templates select="$properties[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section5Heading"/>
			<xsl:with-param name="class" select="'properties'"/>
			<xsl:with-param name="content" select="$properties"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="aors:Condition[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section5Heading"/>
			<xsl:with-param name="class" select="'conditions'"/>
			<xsl:with-param name="content" select="aors:Condition[1]"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- CREATE-EVT -->

	<xsl:template match="aors:CREATE-EVT" mode="doThenElse">
		<xsl:apply-templates select="." mode="section3"/>
	</xsl:template>

	<xsl:template match="aors:CREATE-EVT" mode="section3Content">
		<xsl:apply-templates select="." mode="class">
			<xsl:with-param name="headingElement" select="$section4Heading"/>
			<xsl:with-param name="kind" select="'object'"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="aors:CREATE-EVT" mode="classContent">
		<xsl:variable name="properties" select="@*[local-name()!='actualPercEvtType']|aors:Slot|aors:SenderIdRef[1]"/>
		<xsl:apply-templates select="$properties[1]" mode="classSection">
			<xsl:with-param name="headingElement" select="$section5Heading"/>
			<xsl:with-param name="class" select="'properties'"/>
			<xsl:with-param name="content" select="$properties"/>
		</xsl:apply-templates>
	</xsl:template>
</xsl:stylesheet>