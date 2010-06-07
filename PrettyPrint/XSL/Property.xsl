<?xml version="1.0" encoding="UTF-8"?>

<!--
	This transformation is responsible for processing the
	aors:Property elements.
-->
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:aors="http://aor-simulation.org"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	exclude-result-prefixes="aors xsl">

	<xsl:import href="Slot.xsl"/>

	<xsl:include href="keys_macros_params.xsl"/>

	<!--####################-->
	<!--### local macros ###-->
	<!--####################-->

	<!-- getAttributeData -->

	<xsl:template name="getAttributeData">
		<xsl:param name="entity"/>
		<xsl:param name="name"/>
		<xsl:param name="value"/>
		<xsl:choose>
			<xsl:when test="$entity/aors:Attribute[@name=$name]">
				<xsl:value-of select="$name"/>
				<xsl:if test="$entity/aors:Attribute[@name=$name]/@upperMultiplicity">
					<xsl:choose>
						<xsl:when
							test="$entity/aors:Attribute[@name=$name]/@upperMultiplicity='unbounded'">
							<xsl:value-of select="'[&#8727;]'"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
								select="concat(' [',$entity/aors:Attribute[@name=$name]/@upperMultiplicity,']')"
							/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
				<xsl:value-of
					select="concat(' : ',$entity/aors:Attribute[@name=$name]/@type,' = ',$value)"/>
			</xsl:when>
			<xsl:when test="$entity/../*[@name=$entity/@superType]">
				<xsl:call-template name="getAttributeData">
					<xsl:with-param name="entity" select="$entity/../*[@name=$entity/@superType]"/>
					<xsl:with-param name="name" select="$name"/>
					<xsl:with-param name="value" select="$value"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat($name,' = ',$value)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template
		match="@*[local-name(..) != 'AgentRule' and local-name(..) != 'CommunicationRule' and local-name(..) != 'ActualPerceptionRule']|aors:InitialAttributeValue|aors:Attribute|aors:ReferenceProperty|aors:ComplexDataProperty|aors:EnumerationProperty|aors:Slot|aors:BeliefSlot|aors:ReceiverIdRef|aors:ReminderMsg|aors:Delay|aors:SenderIdRef|aors:PerceiverIdRef|aors:CorrelationValue|aors:StartEventCorrelationProperty|aors:EndEventCorrelationProperty|aors:GridCellProperty"
		mode="classSectionHeading">
		<xsl:text>Properties</xsl:text>
	</xsl:template>

	<xsl:template
		match="aors:SelfBeliefAttribute|aors:SelfBeliefReferenceProperty|aors:SelfBeliefSlot"
		mode="classSectionHeading">
		<xsl:text>Self-Belief Properties</xsl:text>
	</xsl:template>

	<xsl:template match="aors:GlobalVariable" mode="classSectionHeading">
		<xsl:text>Variables</xsl:text>
	</xsl:template>

	<!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template
		match="@*[local-name(..)!='AgentRule' and local-name(..)!='CommunicationRule' and local-name()!='ActualPerceptionRule']|aors:InitialAttributeValue|aors:Attribute|aors:ReferenceProperty|aors:ComplexDataProperty|aors:EnumerationProperty|aors:SelfBeliefAttribute|aors:SelfBeliefReferenceProperty|aors:BeliefAttribute|aors:BeliefReferenceProperty|aors:GlobalVariable|aors:Slot|aors:BeliefSlot|aors:SelfBeliefSlot|aors:ReceiverIdRef|aors:ReminderMsg|aors:Delay|aors:SenderIdRef|aors:PerceiverIdRef|aors:CorrelationValue|aors:StartEventCorrelationProperty|aors:EndEventCorrelationProperty|aors:GridCellProperty"
		mode="classSectionContent">
		<xsl:param name="content"/>
		<ul>
			<xsl:apply-templates select="$content" mode="property"/>
		</ul>
	</xsl:template>

	<!-- @ -->

	<xsl:template match="@*" mode="property">
		<li>
			<xsl:value-of select="concat(local-name(),' = ',.)"/>
		</li>
	</xsl:template>

	<xsl:template match="@autoPerception" mode="property">
		<li>
			<xsl:value-of select="concat(local-name(),' : Boolean = ',.)"/>
		</li>
	</xsl:template>

	<xsl:template match="@idPerceivable" mode="property">
		<li>
			<xsl:value-of select="concat(local-name(),' : Boolean = ',.)"/>
		</li>
	</xsl:template>

	<xsl:template match="@memorySize" mode="property">
		<li>
			<xsl:value-of select="concat(local-name(),' : Integer = ',.)"/>
		</li>
	</xsl:template>

	<!-- InitialAttributeValue -->

	<xsl:template match="aors:InitialAttributeValue" mode="property">
		<li>
			<xsl:call-template name="getAttributeData">
				<xsl:with-param name="entity" select=".."/>
				<xsl:with-param name="name" select="@attribute"/>
				<xsl:with-param name="value" select="@value"/>
			</xsl:call-template>
		</li>
	</xsl:template>

	<!-- Attribute and SelfBeliefAttribute and BeliefAttribute-->

	<xsl:template match="aors:Attribute|aors:SelfBeliefAttribute|aors:BeliefAttribute|aors:GridCellProperty"
		mode="property">
		<li>
			<xsl:choose>

				<xsl:when test="@isStatic = 'true'">
					<span class="static">
						<xsl:value-of select="@name"/>
					</span>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@name"/>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="@upperMultiplicity">
				<xsl:choose>
					<xsl:when test="@upperMultiplicity='unbounded'">
						<xsl:text> [&#8727;]</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat(' [',@upperMultiplicity,']')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			<xsl:value-of select="concat(' : ',@type)"/>
			<xsl:if test="@initialValue">
				<xsl:value-of select="concat(' = ',@initialValue)"/>
			</xsl:if>
		</li>
	</xsl:template>

	<!-- ReferenceProperty and SelfBeliefReferenceProperty and BeliefReferenceProperty-->

	<xsl:template
		match="aors:ReferenceProperty|aors:SelfBeliefReferenceProperty|aors:BeliefReferenceProperty"
		mode="property">
		<li>
			<xsl:if test="@isStatic = 'true'">
				<xsl:attribute name="class">
					<xsl:text>static</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="@name"/>
			<xsl:if test="@upperMultiplicity">
				<xsl:choose>
					<xsl:when test="@upperMultiplicity='unbounded'">
						<xsl:text> [&#8727;]</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat(' [',@upperMultiplicity,']')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			<xsl:text> : </xsl:text>
			<xsl:call-template name="createOptionalLink">
				<xsl:with-param name="node" select="key('EntityTypes',@type)"/>
				<xsl:with-param name="text" select="@type"/>
			</xsl:call-template>
		</li>
	</xsl:template>

	<!-- ComplexDataProperty and EnumerationProperty -->

	<xsl:template match="aors:ComplexDataProperty|aors:EnumerationProperty" mode="property">
		<li>
			<xsl:if test="@isStatic = 'true'">
				<xsl:attribute name="class">
					<xsl:text>static</xsl:text>
				</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="@name"/>
			<xsl:if test="@upperMultiplicity">
				<xsl:choose>
					<xsl:when test="@upperMultiplicity='unbounded'">
						<xsl:text> [&#8727;]</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat(' [',@upperMultiplicity,']')"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
			<xsl:text> : </xsl:text>
			<xsl:call-template name="createOptionalLink">
				<xsl:with-param name="node" select="key('DataTypes',@type)"/>
				<xsl:with-param name="text" select="@type"/>
			</xsl:call-template>
			<xsl:if test="@initialValue">
				<xsl:value-of select="concat(' = ',@initialValue)"/>
			</xsl:if>
		</li>
	</xsl:template>

	<!-- GlobalVariable -->

	<xsl:template match="aors:GlobalVariable" mode="property">
		<li>
			<xsl:value-of select="@name"/>
			<xsl:text> : </xsl:text>
			<xsl:call-template name="getOptionalValue">
				<xsl:with-param name="node">
					<xsl:if test="@refDataType">
						<xsl:call-template name="createOptionalLink">
							<xsl:with-param name="node" select="key('Types',@refDataType)"/>
							<xsl:with-param name="text" select="@refDataType"/>
						</xsl:call-template>
					</xsl:if>
					<xsl:if test="@dataType">
						<xsl:value-of select="@dataType"/>
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="copy" select="true()"/>
			</xsl:call-template>
		</li>
	</xsl:template>

	<!-- Slot -->

	<xsl:template match="aors:Slot|aors:BeliefSlot|aors:SelfBeliefSlot" mode="property">
		<li>
			<xsl:apply-templates select="." mode="slot"/>
		</li>
	</xsl:template>

	<!-- ReceiverIdRef -->

	<xsl:template match="aors:ReceiverIdRef" mode="property">
		<li>
			<xsl:text>receiverIdRef = </xsl:text>
			<xsl:call-template name="hideContent">
				<xsl:with-param name="content">
					<xsl:call-template name="copyCode">
						<xsl:with-param name="code" select=".|following-sibling::aors:ReceiverIdRef"
						/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="heading" select="'Expression'"/>
				<xsl:with-param name="headingPrefix"
					select="concat('Code of ',local-name(..),'.receiverIdRef.')"/>
			</xsl:call-template>
		</li>
	</xsl:template>

	<!-- ReminderMsg -->

	<xsl:template match="aors:ReminderMsg" mode="property">
		<li>
			<xsl:text>reminderMsg = </xsl:text>
			<xsl:call-template name="hideContent">
				<xsl:with-param name="content">
					<xsl:call-template name="copyCode">
						<xsl:with-param name="code" select=".|following-sibling::aors:ReminderMsg"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="heading" select="'Expression'"/>
				<xsl:with-param name="headingPrefix"
					select="concat('Code of ',local-name(..),'.reminderMsg.')"/>
			</xsl:call-template>
		</li>
	</xsl:template>

	<!-- Delay -->

	<xsl:template match="aors:Delay" mode="property">
		<li>
			<xsl:text>delay = </xsl:text>
			<xsl:call-template name="hideContent">
				<xsl:with-param name="content">
					<xsl:call-template name="copyCode">
						<xsl:with-param name="code" select=".|following-sibling::aors:Delay"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="heading" select="'Expression'"/>
				<xsl:with-param name="headingPrefix"
					select="concat('Code of ',local-name(..),'.delay.')"/>
			</xsl:call-template>
		</li>
	</xsl:template>

	<!-- SenderIdRef -->

	<xsl:template match="aors:SenderIdRef" mode="property">
		<li>
			<xsl:text>senderIdRef = </xsl:text>
			<xsl:call-template name="hideContent">
				<xsl:with-param name="content">
					<xsl:call-template name="copyCode">
						<xsl:with-param name="code" select=".|following-sibling::aors:SenderIdRef"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="heading" select="'Expression'"/>
				<xsl:with-param name="headingPrefix"
					select="concat('Code of ',local-name(..),'.senderIdRef.')"/>
			</xsl:call-template>
		</li>
	</xsl:template>

	<!-- PerceiverIdRef -->
	<xsl:template match="aors:PerceiverIdRef" mode="property">
		<li>
			<xsl:text>perceiverIdRef = </xsl:text>
			<xsl:call-template name="hideContent">
				<xsl:with-param name="content">
					<xsl:call-template name="copyCode">
						<xsl:with-param name="code"
							select=".|following-sibling::aors:PerceiverIdRef"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="heading" select="'Expression'"/>
				<xsl:with-param name="headingPrefix"
					select="concat('Code of ',local-name(..),'.perceiverIdRef.')"/>
			</xsl:call-template>
		</li>
	</xsl:template>

	<!-- CorrelationValue -->

	<xsl:template match="aors:CorrelationValue" mode="property">
		<li>
			<xsl:text>correlationValue = </xsl:text>
			<xsl:call-template name="hideContent">
				<xsl:with-param name="content">
					<xsl:call-template name="copyCode">
						<xsl:with-param name="code"
							select=".|following-sibling::aors:CorrelationValue"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="heading" select="'Expression'"/>
				<xsl:with-param name="headingPrefix"
					select="concat('Code of ',local-name(..),'.correlationValue.')"/>
			</xsl:call-template>
		</li>
	</xsl:template>

	<!-- StartEventCorrelationProperty -->

	<xsl:template match="aors:StartEventCorrelationProperty" mode="property">
		<li>
			<xsl:text>startEventCorrelationProperty = </xsl:text>
			<xsl:call-template name="hideContent">
				<xsl:with-param name="content">
					<xsl:call-template name="copyCode">
						<xsl:with-param name="code"
							select=".|following-sibling::aors:StartEventCorrelationProperty"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="heading" select="'Expression'"/>
				<xsl:with-param name="headingPrefix"
					select="concat('Code of ',local-name(..),'.startEventCorrelationProperty.')"/>
			</xsl:call-template>
		</li>
	</xsl:template>

	<!-- EndEventCorrelationProperty -->

	<xsl:template match="aors:EndEventCorrelationProperty" mode="property">
		<li>
			<xsl:text>endEventCorrelationProperty = </xsl:text>
			<xsl:call-template name="hideContent">
				<xsl:with-param name="content">
					<xsl:call-template name="copyCode">
						<xsl:with-param name="code"
							select=".|following-sibling::aors:EndEventCorrelationProperty"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="heading" select="'Expression'"/>
				<xsl:with-param name="headingPrefix"
					select="concat('Code of ',local-name(..),'.endEventCorrelationProperty.')"/>
			</xsl:call-template>
		</li>
	</xsl:template>
</xsl:stylesheet>
