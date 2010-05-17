<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:aors="http://aor-simulation.org"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0"
                exclude-result-prefixes="aors xsl">
   <xsl:output method="xml" doctype-public="-//W3C//DTD XHTML 1.1//EN"
               doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"
               encoding="UTF-8"
               indent="yes"/>
   <!--copied from .-->

	
	
<!--
	<xsl:import href="Views.xsl"/>
	<xsl:import href="Scales.xsl"/>
-->

	

	

	<!--#######################-->
	<!--### basic structure ###-->
	<!--#######################-->

	<xsl:template match="/" mode="title" priority="-1">
		    <xsl:choose>
			      <xsl:when test="aors:SimulationScenario/@scenarioTitle">
				        <xsl:value-of select="concat(aors:SimulationScenario/@scenarioTitle,' (',aors:SimulationScenario/@scenarioName,')')"/>
			      </xsl:when>
			      <xsl:otherwise>
				        <xsl:value-of select="aors:SimulationScenario/@scenarioName"/>
			      </xsl:otherwise>
		    </xsl:choose>
	  </xsl:template>

	  <xsl:template match="/" mode="body" priority="-1">
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

	<xsl:template match="*" mode="documentNavigation" priority="-1"/>

	  <xsl:template match="aors:SimulationScenario" mode="documentNavigation" priority="-1">
		    <h1 class="heading">
			      <span>N</span>
         <span>a</span>
         <span>v</span>
         <span>i</span>
         <span>g</span>
         <span>a</span>
         <span>t</span>
         <span>i</span>
         <span>o</span>
         <span>n</span>
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

	  <xsl:template match="aors:SimulationModel" mode="documentNavigation" priority="-1">
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
				        <xsl:variable name="agentRules"
                          select="aors:EntityTypes/aors:*/aors:ActualPerceptionRule|aors:EntityTypes/aors:*/aors:ReactionRule|aors:EntityTypes/aors:*/aors:CommunicationRule"/>
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

	<xsl:template match="aors:SimulationScenario" mode="documentBody" priority="-1">
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

	<xsl:template match="aors:SimulationModel" mode="partHeading" priority="-1">
		    <xsl:text>Simulation Model: </xsl:text>
		    <xsl:call-template name="getModelTitle"/>
	  </xsl:template>

	  <xsl:template match="aors:SimulationModel" mode="partContent" priority="-1">
		    <xsl:apply-templates select="." mode="documentBody"/>
	  </xsl:template>

	  <!-- SimulationParameters -->

	<xsl:template match="aors:SimulationParameters" mode="heading" priority="-1">
		    <xsl:text>Simulation Parameters</xsl:text>
	  </xsl:template>

	  <xsl:template match="aors:SimulationParameters" mode="navigation" priority="-1">
		    <xsl:apply-templates select="." mode="navigationEntry"/>
	  </xsl:template>

	  <xsl:template match="aors:SimulationParameters" mode="content" priority="-1">
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

	  <xsl:template match="aors:Parameter" mode="simulationParameters" priority="-1">
		    <tr>
			      <td>
            <xsl:value-of select="@name"/>
         </td>
			      <td>
            <xsl:value-of select="@value"/>
         </td>
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
<!--copied from keys_macros_params.xsl-->

	

	<!--############-->
	<!--### Keys ###-->
	<!--############-->

	<xsl:key name="EntityTypes" match="//aors:EntityTypes//aors:*" use="@name"/>
	  <xsl:key name="DataTypes" match="//aors:DataTypes//aors:*" use="@name"/>
	  <xsl:key name="Collections" match="//aors:Collections//aors:*" use="@name"/>
	  <xsl:key name="Types"
            match="//aors:EntityTypes//aors:*|//aors:DataTypes//aors:*|//aors:Collections//aors:*"
            use="@name"/>
  <xsl:key name="StatisticsVariables" match="//aors:Statistics/aors:Variable" use="@name"/>
  <xsl:key name="GlobalVariables" match="//aors:Globals/aors:GlobalVariable" use="@name"/>
  
	  <!--##############-->
	<!--### Macros ###-->
	<!--##############-->

	<!-- createOptionalLink -->

	<xsl:template name="createOptionalLink">
		    <xsl:param name="node"/>
		    <xsl:param name="text"/>
		    <xsl:param name="copy" select="false()"/>
		    <xsl:choose>
			      <xsl:when test="$node">
				        <a>
					          <xsl:attribute name="href">
						            <xsl:call-template name="getId">
							              <xsl:with-param name="node" select="$node"/>
							              <xsl:with-param name="prefix" select="'#'"/>
						            </xsl:call-template>
					          </xsl:attribute>
					          <xsl:choose>
						            <xsl:when test="$copy">
							              <xsl:copy-of select="$text"/>
						            </xsl:when>
						            <xsl:otherwise>
							              <xsl:value-of select="$text"/>
						            </xsl:otherwise>
					          </xsl:choose>
				        </a>
			      </xsl:when>
			      <xsl:otherwise>
				        <xsl:choose>
					          <xsl:when test="$copy">
						            <xsl:copy-of select="$text"/>
					          </xsl:when>
					          <xsl:otherwise>
						            <xsl:value-of select="$text"/>
					          </xsl:otherwise>
				        </xsl:choose>
			      </xsl:otherwise>
		    </xsl:choose>
	  </xsl:template>

	  <!-- getBooleanValue -->

	<xsl:template name="getBooleanValue">
		    <xsl:param name="value"/>
		    <xsl:param name="showNegative" select="false()"/>
		    <xsl:choose>
			      <xsl:when test="$value = 'true'">
				        <xsl:text>✓</xsl:text>
			      </xsl:when>
			      <xsl:otherwise>
				        <xsl:if test="$showNegative">
					          <xsl:text>no</xsl:text>
				        </xsl:if>
			      </xsl:otherwise>
		    </xsl:choose>
	  </xsl:template>

	  <!-- getExpandedName -->

	<xsl:template name="getExpandedName">
		    <xsl:param name="name"/>
		    <xsl:text>http://aor-simulation.org#</xsl:text>
		    <xsl:choose>
			      <xsl:when test="contains($name,':')">
				        <xsl:value-of select="substring-after($name,':')"/>
			      </xsl:when>
			      <xsl:otherwise>
				        <xsl:value-of select="$name"/>
			      </xsl:otherwise>
		    </xsl:choose>
	  </xsl:template>

	  <!-- getId -->

	<xsl:template name="getId">
		    <xsl:param name="node" select="."/>
		    <xsl:param name="prefix" select="''"/>
		    <xsl:value-of select="concat($prefix,'id_',generate-id($node))"/>
	  </xsl:template>

	  <!-- getOptionalValue -->

	<xsl:template name="getOptionalValue">
		    <xsl:param name="node" select="''"/>
		    <xsl:param name="defaultValue" select="'n/a'"/>
		    <xsl:param name="copy" select="false()"/>
		    <xsl:choose>
			      <xsl:when test="$node != '' and $copy">
				        <xsl:copy-of select="$node"/>
			      </xsl:when>
			      <xsl:when test="$node != ''">
				        <xsl:value-of select="$node"/>
			      </xsl:when>
			      <xsl:otherwise>
				        <xsl:value-of select="$defaultValue"/>
			      </xsl:otherwise>
		    </xsl:choose>
	  </xsl:template>

	  <!-- hideContent -->

	<xsl:template name="hideContent">
		    <xsl:param name="content"/>
		    <xsl:param name="heading"/>
		    <xsl:param name="headingPrefix"/>
		    <xsl:param name="headingSuffix"/>
		    <xsl:if test="$content">
			      <div class="hiddenContent">
				        <span class="hover">
					          <xsl:copy-of select="$heading"/>
				        </span>
				        <div class="hide">
					          <div class="resize">
						            <div class="content">
							              <div class="label">
								                <xsl:copy-of select="$headingPrefix"/>
								                <xsl:copy-of select="$heading"/>
								                <xsl:copy-of select="$headingSuffix"/>
							              </div>
							              <xsl:copy-of select="$content"/>
						            </div>
					          </div>
				        </div>
			      </div>
		    </xsl:if>
	  </xsl:template>

	  <!--##############-->
	<!--### Params ###-->
	<!--##############-->

	<xsl:param name="partHeading" select="'h1'"/>
	  <xsl:param name="chapterHeading" select="'h2'"/>
	  <xsl:param name="section1Heading" select="'h3'"/>
	  <xsl:param name="section2Heading" select="'h4'"/>
	  <xsl:param name="section3Heading" select="'h5'"/>
	  <xsl:param name="section4Heading" select="'h6'"/>
	  <xsl:param name="section5Heading" select="'div'"/>
	  <xsl:param name="expressionLength" select="'100'"/>
	  <xsl:param name="smallExpressionLength" select="'70'"/>

   <!--copied from code.xsl-->

	<!-- copyCode -->

	<xsl:template xmlns:allex="http://aor-simulation.org/allex"
                 xmlns:aorsel="http://aor-simulation.org/aorsel"
                 name="copyCode">
		    <xsl:param name="code" select="."/>
		    <xsl:param name="class"/>
		    <xsl:if test="$code">
			      <dl>
				        <xsl:attribute name="class">
					          <xsl:text>code</xsl:text>
					          <xsl:if test="normalize-space($class) != ''">
						            <xsl:value-of select="concat(' ',$class)"/>
					          </xsl:if>
				        </xsl:attribute>
				        <xsl:apply-templates select="$code" mode="copyCode"/>
			      </dl>
		    </xsl:if>
	  </xsl:template>

	  <xsl:template xmlns:allex="http://aor-simulation.org/allex"
                 xmlns:aorsel="http://aor-simulation.org/aorsel"
                 match="*"
                 mode="copyCode"
                 priority="-3"/>

	  <xsl:template xmlns:allex="http://aor-simulation.org/allex"
                 xmlns:aorsel="http://aor-simulation.org/aorsel"
                 match="aors:*"
                 mode="copyCode"
                 priority="-3">
		    <dt>
			      <xsl:value-of select="concat(@language,': ')"/>
		    </dt>
		    <dd>
			      <xsl:call-template name="getFormattedCode">
				        <xsl:with-param name="code" select="text()"/>
				        <xsl:with-param name="language" select="@language"/>
			      </xsl:call-template>
		    </dd>
	  </xsl:template>

	  <xsl:template xmlns:allex="http://aor-simulation.org/allex"
                 xmlns:aorsel="http://aor-simulation.org/aorsel"
                 match="allex:*|aorsel:*"
                 mode="copyCode"
                 priority="-3">
		    <dt>AORSEL:</dt>
		    <dd>
			      <xsl:call-template name="getFormattedCode"/>
		    </dd>
	  </xsl:template>

	  <!-- getFormattedCode -->

	<xsl:template xmlns:allex="http://aor-simulation.org/allex"
                 xmlns:aorsel="http://aor-simulation.org/aorsel"
                 name="getFormattedCode">
		    <xsl:param name="code" select="."/>
		    <xsl:param name="language" select="'AORSEL'"/>
		    <xsl:choose>
			      <xsl:when test="$language = 'AORSEL' and $code">
				        <ol class="formattedCode">
					          <xsl:call-template name="getFormattedCode_AORSEL">
						            <xsl:with-param name="code" select="$code"/>
					          </xsl:call-template>
				        </ol>
			      </xsl:when>
			      <xsl:when test="$language = 'Java' and string-length(normalize-space($code)) &gt; 0">
				        <ol class="formattedCode">
					          <xsl:call-template name="getFormattedCode_Java">
						            <xsl:with-param name="code" select="normalize-space(translate(concat($code,'&#xA;'),'&#xA;',' '))"/>
					          </xsl:call-template>
				        </ol>
			      </xsl:when>
			      <xsl:otherwise>
				        <code>
					          <xsl:value-of select="$code"/>
				        </code>
			      </xsl:otherwise>
		    </xsl:choose>
	  </xsl:template>

	  <!-- getFormattedCode_AORSEL -->

	<xsl:template xmlns:allex="http://aor-simulation.org/allex"
                 xmlns:aorsel="http://aor-simulation.org/aorsel"
                 name="getFormattedCode_AORSEL">
		    <xsl:param name="code"/>
		    <xsl:apply-templates select="$code" mode="getFormattedCode_AORSEL"/>
	  </xsl:template>

	  <xsl:template xmlns:allex="http://aor-simulation.org/allex"
                 xmlns:aorsel="http://aor-simulation.org/aorsel"
                 match="*"
                 mode="getFormattedCode_AORSEL"
                 priority="-3"/>

	  <xsl:template xmlns:allex="http://aor-simulation.org/allex"
                 xmlns:aorsel="http://aor-simulation.org/aorsel"
                 match="allex:*|aorsel:*"
                 mode="getFormattedCode_AORSEL"
                 priority="-3">
		    <li>
			      <code>
				        <xsl:variable name="attrs">
					          <xsl:apply-templates select="@*" mode="getFormattedCode_AROSEL"/>
				        </xsl:variable>
				        <xsl:value-of select="concat('&lt;',local-name(),$attrs)"/>
				        <xsl:if test="not(*)">
					          <xsl:value-of select="'/'"/>
				        </xsl:if>
				        <xsl:value-of select="'&gt;'"/>
				        <xsl:if test="allex:*|aorsel:*">
					          <ol>
						            <xsl:apply-templates mode="getFormattedCode_AORSEL"/>
					          </ol>
					          <xsl:value-of select="concat('&lt;/',local-name(),'&gt;')"/>
				        </xsl:if>
			      </code>
		    </li>
	  </xsl:template>

	  <xsl:template xmlns:allex="http://aor-simulation.org/allex"
                 xmlns:aorsel="http://aor-simulation.org/aorsel"
                 match="@*"
                 mode="getFormattedCode_AORSEL"
                 priority="-3">
		    <xsl:value-of select="concat(' ',name(),'=&#34;',.,'&#34;')"/>
	  </xsl:template>

	  <!-- getFormattedCode_Java -->

	<xsl:template xmlns:allex="http://aor-simulation.org/allex"
                 xmlns:aorsel="http://aor-simulation.org/aorsel"
                 name="getFormattedCode_Java">
		    <xsl:param name="code"/>
		    <xsl:variable name="codeLine" select="substring-before($code,' ')"/>
		    <xsl:variable name="restCode" select="substring-after($code,' ')"/>
		    <xsl:if test="string-length(normalize-space($codeLine)) &gt; 0">
			      <li>
				        <code>
					          <xsl:call-template name="mapToEntities">
						            <xsl:with-param name="code" select="$codeLine"/>
					          </xsl:call-template>
				        </code>
			      </li>
		    </xsl:if>
		    <xsl:if test="string-length(normalize-space($restCode)) &gt; 0">
			      <xsl:call-template name="getFormattedCode_Java">
				        <xsl:with-param name="code" select="$restCode"/>
			      </xsl:call-template>
		    </xsl:if>
	  </xsl:template>
	
	  <!-- mapToEntities -->
	
	<xsl:template xmlns:allex="http://aor-simulation.org/allex"
                 xmlns:aorsel="http://aor-simulation.org/aorsel"
                 name="mapToEntities">
		    <xsl:param name="code"/>
		    <xsl:choose>
			      <xsl:when test="contains($code,'&amp;')">
				        <xsl:call-template name="mapToEntities">
					          <xsl:with-param name="code" select="substring-before($code,'&amp;')"/>
				        </xsl:call-template>
				        <xsl:text>&amp;</xsl:text>
				        <xsl:call-template name="mapToEntities">
					          <xsl:with-param name="code" select="substring-after($code,'&amp;')"/>
				        </xsl:call-template>
			      </xsl:when>
			      <xsl:when test="contains($code,'&gt;')">
				        <xsl:call-template name="mapToEntities">
					          <xsl:with-param name="code" select="substring-before($code,'&gt;')"/>
				        </xsl:call-template>
				        <xsl:text>&gt;</xsl:text>
				        <xsl:call-template name="mapToEntities">
					          <xsl:with-param name="code" select="substring-after($code,'&gt;')"/>
				        </xsl:call-template>
			      </xsl:when>
			      <xsl:when test="contains($code,'&lt;')">
				        <xsl:call-template name="mapToEntities">
					          <xsl:with-param name="code" select="substring-before($code,'&lt;')"/>
				        </xsl:call-template>
				        <xsl:text>&lt;</xsl:text>
				        <xsl:call-template name="mapToEntities">
					          <xsl:with-param name="code" select="substring-after($code,'&lt;')"/>
				        </xsl:call-template>
			      </xsl:when>
			      <xsl:otherwise>
				        <xsl:value-of select="$code"/>
			      </xsl:otherwise>
		    </xsl:choose>
	  </xsl:template>
   <!--copied from InitialState.xsl-->

	

	

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:InitialState" mode="heading" priority="-4">
		    <xsl:text>Initial State</xsl:text>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:GlobalVariable|aors:InitialState/aors:GridCells"
                 mode="heading"
                 priority="-4">
		    <xsl:text>Globals</xsl:text>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:Object|aors:InitialState/aors:Objects|aors:InitialState/aors:PhysicalObject|aors:InitialState/aors:PhysicalObjects|aors:InitialState/aors:Agent|aors:InitialState/aors:Agents|aors:InitialState/aors:PhysicalAgent|aors:InitialState/aors:PhysicalAgents|aors:InitialState/aors:Collection"
                 mode="heading"
                 priority="-4">
		    <xsl:text>Objects</xsl:text>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:*/aors:BeliefEntity" mode="classSectionHeading"
                 priority="-4">
		    <xsl:text>Belief Entities</xsl:text>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:*/aors:ReminderEvent|aors:InitialState/aors:*/aors:TimeEvent|aors:InitialState/aors:*/aors:PeriodicTimeEvent"
                 mode="classSectionHeading"
                 priority="-4">
		    <xsl:text>Events</xsl:text>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:ExogenousEvent | aors:InitialState/aors:CausedEvent"
                 mode="heading"
                 priority="-4">
		    <xsl:text>Caused and Exogenous Events</xsl:text>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:InitializationRule" mode="chapterHeading"
                 priority="-4">
		    <xsl:text>Initialization Rules</xsl:text>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:InitializationRule" mode="heading" priority="-4">
		    <xsl:value-of select="@name"/>
	  </xsl:template>

	  <!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:InitialState" mode="navigation" priority="-4">
		    <xsl:apply-templates select="." mode="navigationEntry">
			      <xsl:with-param name="subEntries">
				        <xsl:variable name="globals" select="aors:GlobalVariable|aors:GridCells"/>
				        <xsl:apply-templates select="$globals[1]" mode="navigation"/>
				        <xsl:variable name="objects"
                          select="aors:Object|aors:Objects|aors:PhysicalObject|aors:PhysicalObjects|aors:Agent|aors:Agents|aors:PhysicalAgent|aors:PhysicalAgents|aors:Collection"/>
				        <xsl:apply-templates select="$objects[1]" mode="navigation"/>
				        <xsl:variable name="events" select="aors:ExogenousEvent|aors:CausedEvent"/>
				        <xsl:apply-templates select="$events[1]" mode="navigation"/>
				        <xsl:apply-templates select="aors:InitializationRule[1]" mode="navigation"/>
			      </xsl:with-param>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:*" mode="navigation" priority="-4">
		    <xsl:apply-templates select="." mode="navigationEntry"/>
	  </xsl:template>
	
	  <xsl:template match="aors:InitialState/aors:InitializationRule" mode="navigationEntryTitle"
                 priority="-4">
		    <xsl:apply-templates select="." mode="chapterHeading"/>
	  </xsl:template>

	  <!--################-->
	<!--###  content ###-->
	<!--################-->

	<xsl:template match="aors:InitialState" mode="content" priority="-4">
		    <xsl:variable name="globals" select="aors:GlobalVariable|aors:GridCells"/>
		    <xsl:apply-templates select="$globals[1]" mode="chapter">
			      <xsl:with-param name="content" select="$globals"/>
		    </xsl:apply-templates>
		    <xsl:variable name="objects"
                    select="aors:Object|aors:Objects|aors:PhysicalObject|aors:PhysicalObjects|aors:Agent|aors:Agents|aors:PhysicalAgent|aors:PhysicalAgents|aors:Collection"/>
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

	<xsl:template match="aors:InitialState/aors:GlobalVariable|aors:InitialState/aors:GridCells"
                 mode="chapterContent"
                 priority="-4">
		    <xsl:param name="content"/>
		    <dl>
			      <xsl:apply-templates select="$content" mode="update"/>
		    </dl>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:GlobalVariable" mode="update" priority="-4">
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
								                <xsl:when test="count(aors:ValueExpr) &gt; 1 or string-length(normalize-space(aors:ValueExpr/text())) &gt; 70">
							
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

	  <xsl:template match="aors:InitialState/aors:GridCells" mode="update" priority="-4">
		    <xsl:apply-templates select="aors:*" mode="update"/>
	  </xsl:template>
	
	  <xsl:template match="aors:InitialState/aors:GridCells/aors:Slot" mode="update" priority="-4"/>

	  <xsl:template match="aors:InitialState/aors:GridCells/aors:Slot[1]" mode="update"
                 priority="-4">
		    <dt>
			      <xsl:text>[GRID-CELL] (all cells)</xsl:text>
		    </dt>
		    <xsl:apply-templates select=".|following-sibling::aors:Slot" mode="update2"/>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:GridCells/aors:GridCell" mode="update"
                 priority="-4">
		    <dt>
			      <xsl:value-of select="concat('[GRID-CELL] (',@x,',',@y,')')"/>
		    </dt>
		    <xsl:apply-templates select="aors:Slot" mode="update2"/>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:GridCells/aors:GridCellSet" mode="update"
                 priority="-4">
		    <dt>
			      <xsl:value-of select="concat('[GRID-CELLS] [(',@startX,',',@startY,') ; (',@endX,',',@endY,')]')"/>
			      <xsl:if test="@creationLoopVar">
				        <xsl:value-of select="concat(' (variable: ',@creationLoopVar,')')"/>
			      </xsl:if>
		    </dt>
		    <xsl:apply-templates select="aors:Slot" mode="update2"/>
	  </xsl:template>

	  <!-- aors:GridCells//Slot -->

	<xsl:template match="aors:InitialState/aors:GridCells/aors:*/aors:Slot|aors:InitialState/aors:GridCells/aors:Slot"
                 mode="update2"
                 priority="-4">
		    <dd>
			      <xsl:apply-templates select="." mode="slot">
				        <xsl:with-param name="maybeInline" select="true()"/>
			      </xsl:apply-templates>
		    </dd>
	  </xsl:template>

	  <!-- Objects -->

	<xsl:template match="aors:InitialState/aors:Object|aors:InitialState/aors:Objects|aors:InitialState/aors:PhysicalObject|aors:InitialState/aors:PhysicalObjects|aors:InitialState/aors:Agent|aors:InitialState/aors:Agents|aors:InitialState/aors:PhysicalAgent|aors:InitialState/aors:PhysicalAgents|aors:InitialState/aors:Collection"
                 mode="content"
                 priority="-4">
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="$content" mode="create">
			      <xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:Object|aors:InitialState/aors:PhysicalObject|aors:InitialState/aors:Agent|aors:InitialState/aors:PhysicalAgent|aors:InitialState/aors:Collection"
                 mode="create"
                 priority="-4">
		    <xsl:apply-templates select="." mode="class">
			      <xsl:with-param name="headingElement" select="$section1Heading"/>
			      <xsl:with-param name="kind" select="'object'"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:Collection" mode="create" priority="-4">
		    <xsl:apply-templates select="." mode="class">
			      <xsl:with-param name="headingElement" select="$section1Heading"/>
			      <xsl:with-param name="class" select="'parameterized'"/>
			      <xsl:with-param name="kind" select="'object'"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:Objects|aors:InitialState/aors:PhysicalObjects|aors:InitialState/aors:Agents|aors:InitialState/aors:PhysicalAgents"
                 mode="create"
                 priority="-4">
		    <xsl:apply-templates select="." mode="class">
			      <xsl:with-param name="headingElement" select="$section1Heading"/>
			      <xsl:with-param name="kind" select="'objects'"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:Object|aors:InitialState/aors:Objects|aors:InitialState/aors:PhysicalObject|aors:InitialState/aors:PhysicalObjects|aors:InitialState/aors:Agent|aors:InitialState/aors:Agents|aors:InitialState/aors:PhysicalAgent|aors:InitialState/aors:PhysicalAgents|aors:InitialState/aors:Collection"
                 mode="class"
                 priority="-4">
		    <xsl:param name="headingElement"/>
		    <xsl:param name="class"/>
		    <xsl:param name="kind"/>
		    <xsl:apply-templates select="." mode="classBody">
			      <xsl:with-param name="headingElement" select="$headingElement"/>
			      <xsl:with-param name="class" select="$class"/>
			      <xsl:with-param name="kind" select="$kind"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="aors:BeliefEntity|aors:ReminderEvent|aors:TimeEvent|aors:PeriodicTimeEvent"
                           mode="class">
			      <xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
			      <xsl:with-param name="headingElement" select="$headingElement"/>
			      <xsl:with-param name="class" select="$class"/>
			      <xsl:with-param name="kind" select="'object'"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:Object|aors:InitialState/aors:Objects|aors:InitialState/aors:PhysicalObject|aors:InitialState/aors:PhysicalObjects|aors:InitialState/aors:Agent|aors:InitialState/aors:Agents|aors:InitialState/aors:PhysicalAgent|aors:InitialState/aors:PhysicalAgents|aors:InitialState/aors:Collection"
                 mode="classContent"
                 priority="-4">
		    <xsl:variable name="properties"
                    select="@*[local-name()!='type' and local-name()!='id' and local-name()!='name' and local-name()!='addToCollection' and local-name()!='rangeStartID' and local-name()!='rangeEndID' and local-name()!='objectVariable' and local-name()!='creationLoopVar' and local-name()!='itemType']|aors:Slot"/>
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

	  <xsl:template match="aors:InitialState/aors:*/aors:BeliefEntity" mode="classContent"
                 priority="-4">
		    <xsl:apply-templates select="aors:BeliefSlot[1]" mode="classSection">
			      <xsl:with-param name="headingElement" select="$section2Heading"/>
			      <xsl:with-param name="class" select="'properties'"/>
			      <xsl:with-param name="content" select="aors:BeliefSlot"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:*/aors:ReminderEvent|aors:InitialState/aors:*/aors:TimeEvent|aors:InitialState/aors:*/aors:PeriodicTimeEvent"
                 mode="classContent"
                 priority="-4">
		    <xsl:variable name="properties"
                    select="@*[local-name()!='type' and local-name()!='id' and local-name()!='name']|aors:Slot"/>
		    <xsl:apply-templates select="$properties[1]" mode="classSection">
			      <xsl:with-param name="headingElement" select="$section2Heading"/>
			      <xsl:with-param name="class" select="'properties'"/>
			      <xsl:with-param name="content" select="$properties"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:*/aors:BeliefEntity|aors:InitialState/aors:*/aors:ReminderEvent|aors:InitialState/aors:*/aors:TimeEvent|aors:InitialState/aors:*/aors:PeriodicTimeEvent"
                 mode="classSectionContent"
                 priority="-4">
		    <xsl:param name="content"/>
		    <ul>
			      <xsl:apply-templates select="$content" mode="objectComponent"/>
		    </ul>
	  </xsl:template>

	  <!-- EnvironmentEvents -->

	<xsl:template match="aors:InitialState/aors:ExogenousEvent|aors:InitialState/aors:CausedEvent"
                 mode="content"
                 priority="-4">
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="$content" mode="create">
			      <xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:ExogenousEvent|aors:InitialState/aors:CausedEvent"
                 mode="create"
                 priority="-4">
		    <xsl:apply-templates select="." mode="class">
			      <xsl:with-param name="headingElement" select="$section1Heading"/>
			      <xsl:with-param name="kind" select="'object'"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:InitialState/aors:ExogenousEvent|aors:InitialState/aors:CausedEvent"
                 mode="classContent"
                 priority="-4">
		    <xsl:variable name="properties"
                    select="@*[local-name()!='name' and local-name()!='id' and local-name()!='type']|aors:Slot"/>
		    <xsl:apply-templates select="$properties[1]" mode="classSection">
			      <xsl:with-param name="headingElement" select="$section2Heading"/>
			      <xsl:with-param name="class" select="'properties'"/>
			      <xsl:with-param name="content" select="$properties"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <!-- InitializationRule -->

	<xsl:template match="aors:InitializationRule" mode="chapterContent" priority="-4">
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="$content" mode="section1"/>
	  </xsl:template>

	  <xsl:template match="aors:InitializationRule" mode="section1" priority="-4">
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

	  <xsl:template match="aors:InitializationRule" mode="content" priority="-4">
		    <xsl:apply-templates select="aors:documentation" mode="content"/>
		    <xsl:apply-templates select="aors:FOR[1]" mode="section2">
			      <xsl:with-param name="content" select="aors:FOR"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="aors:IF[1]" mode="section2">
			      <xsl:with-param name="content" select="aors:IF"/>
		    </xsl:apply-templates>
		    <xsl:variable name="then"
                    select="aors:UpdateObject|aors:UpdateObjects|aors:UpdateGridCell|aors:UpdateGridCells"/>
		    <xsl:apply-templates select="$then[1]" mode="section2">
			      <xsl:with-param name="content" select="$then"/>
		    </xsl:apply-templates>
	  </xsl:template>
   <!--copied from Rules.xsl-->

	
	
	
	

	

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<!-- WHEN -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:WHEN"
                 mode="heading"
                 priority="-5">
		    <xsl:text>When</xsl:text>
	  </xsl:template>
	
	  <!-- ON-EACH-SIMULATION-STEP -->
	
	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:ON-EACH-SIMULATION-STEP"
                 mode="heading"
                 priority="-5">
		    <xsl:text>On</xsl:text>
	  </xsl:template>

	  <!-- FOR -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="@agentVariable|aors:FOR"
                 mode="heading"
                 priority="-5">
		    <xsl:text>For</xsl:text>
	  </xsl:template>
	
	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:DO"
                 mode="heading"
                 priority="-5">
		    <xsl:text>Do</xsl:text>
	  </xsl:template>

	  <!-- IF -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:IF"
                 mode="heading"
                 priority="-5">
		    <xsl:text>If</xsl:text>
	  </xsl:template>

	  <!-- THEN and ELSE -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:THEN|aors:UPDATE-AGT|aors:UPDATE-ENV|aors:SCHEDULE-EVT|aors:CREATE-EVT|aors:InitializationRule/aors:UpdateObject|aors:InitializationRule/aors:UpdateObjects|aors:InitializationRule/aors:UpdateGridCell"
                 mode="section2Heading"
                 priority="-5">
		    <xsl:text>Then</xsl:text>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:ELSE"
                 mode="heading"
                 priority="-5">
		    <xsl:text>Else</xsl:text>
	  </xsl:template>

	  <!-- UPDATE-AGT -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-AGT/aors:Slot|aors:UPDATE-AGT/aors:SelfBeliefSlot"
                 mode="heading"
                 priority="-5">
		    <xsl:text>Update Slots</xsl:text>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-AGT/aors:UpdateComplexDataPropertyValue"
                 mode="heading"
                 priority="-5">
		    <xsl:text>Update Complex Data Property</xsl:text>
	  </xsl:template>
	
	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-AGT/aors:UpdateBeliefEntity"
                 mode="heading"
                 priority="-5">
		    <xsl:text>Update Belief Entities</xsl:text>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-AGT/aors:CreateBeliefEntity"
                 mode="section3Heading"
                 priority="-5">
		    <xsl:text>Create Belief Entities</xsl:text>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-AGT/aors:DestroyBeliefEntity"
                 mode="heading"
                 priority="-5">
		    <xsl:text>Destroy Belief Entities</xsl:text>
	  </xsl:template>

	  <!-- UPDATE-ENV -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:UpdateObject|aors:UPDATE-ENV/aors:UpdateObjects"
                 mode="heading"
                 priority="-5">
		    <xsl:text>Update Objects</xsl:text>
	  </xsl:template>
	
	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:UpdateGridCell"
                 mode="heading"
                 priority="-5">
		    <xsl:text>Updage Grid-Cells</xsl:text>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:UpdateGlobalVariable|aors:UPDATE-ENV/aors:IncrementGlobalVariable"
                 mode="heading"
                 priority="-5">
		    <xsl:text>Update Global Variables</xsl:text>
	  </xsl:template>
	
	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:UpdateStatisticsVariable"
                 mode="heading"
                 priority="-5">
		    <xsl:text>Update Statistics Variables</xsl:text>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:AddObjectToCollection|aors:UPDATE-ENV/aors:RemoveObjectFromCollection"
                 mode="heading"
                 priority="-5">
		    <xsl:text>Update Collections</xsl:text>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:Create"
                 mode="heading"
                 priority="-5">
		    <xsl:text>Create Objects</xsl:text>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:Create/aors:*/aors:BeliefEntity"
                 mode="classSectionHeading"
                 priority="-5">
		    <xsl:text>Belief Entities</xsl:text>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:Create/aors:*/aors:ReminderEvent|aors:UPDATE-ENV/aors:Create/aors:*/aors:TimeEvent|aors:UPDATE-ENV/aors:Create/aors:*/aors:PeriodicTimeEvent"
                 mode="classSectionHeading"
                 priority="-5">
		    <xsl:text>Events</xsl:text>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:DestroyObject|aors:UPDATE-ENV/aors:DestroyObjects"
                 mode="heading"
                 priority="-5">
		    <xsl:text>Destroy Objects</xsl:text>
	  </xsl:template>

	  <!-- InitRule/Update -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:InitializationRule/aors:UpdateObject|aors:InitializationRule/aors:UpdateObjects|aors:InitializationRule/aors:UpdateGridCell"
                 mode="heading"
                 priority="-5">
		    <xsl:text>Update Objects</xsl:text>
	  </xsl:template>

	  <!-- SCHEDULE-EVT -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:SCHEDULE-EVT"
                 mode="section3Heading"
                 priority="-5">
		    <xsl:text>Schedule Event</xsl:text>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:CreateDescription"
                 mode="heading"
                 priority="-5">
		    <xsl:text>Create Descriptions</xsl:text>
	  </xsl:template>

	  <!-- CREATE-EVT -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:CREATE-EVT"
                 mode="section3Heading"
                 priority="-5">
		    <xsl:text>Create Event</xsl:text>
	  </xsl:template>

	  <!--###############-->
	<!--### content ###-->
	<!--###############-->

	<!-- WHEN -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:WHEN"
                 mode="content"
                 priority="-5">
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

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:ActualPerceptionRule/aors:WHEN"
                 mode="content"
                 priority="-5">
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
	
	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:ON-EACH-SIMULATION-STEP"
                 mode="content"
                 priority="-5">
		    <xsl:text>each simulation step</xsl:text>
	  </xsl:template>

	  <!-- FOR -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="@agentVariable|aors:FOR"
                 mode="content"
                 priority="-5">
		    <xsl:param name="content"/>
		    <ul class="for">
			      <xsl:apply-templates select="$content" mode="for"/>
		    </ul>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="@agentVariable"
                 mode="for"
                 priority="-5">
		    <li>
			      <xsl:value-of select="concat(.,' : ')"/>
			      <xsl:call-template name="createOptionalLink">
				        <xsl:with-param name="node" select="../.."/>
				        <xsl:with-param name="text" select="../../@name"/>
			      </xsl:call-template>
		    </li>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:CommunicationRule/aors:FOR|aors:ReactionRule/aors:FOR"
                 mode="for"
                 priority="-5">
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
									                  <xsl:when test="count(aors:ValueExpr) &gt; 1 or string-length(normalize-space(aors:ValueExpr/text())) &gt; number($expressionLength)">
										
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
												                        <xsl:when test="count(aors:BeliefEntityType) &gt; 1 or string-length(normalize-space(aors:BeliefEntityType/text())) &gt; number($expressionLength)">
													
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
										                    <xsl:when test="count(aors:BeliefEntityIdRef) &gt; 1 or string-length(normalize-space(aors:BeliefEntityIdRef/text())) &gt; number($expressionLength)">

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

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:EnvironmentRule/aors:FOR|aors:InitializationRule/aors:FOR"
                 mode="for"
                 priority="-5">
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
									                  <xsl:when test="count(aors:ValueExpr) &gt; 1 or string-length(normalize-space(aors:ValueExpr/text())) &gt; number($expressionLength)">
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
										                    <xsl:when test="count(aors:ObjectIdRef) &gt; 1 or string-length(normalize-space(aors:ObjectIdRef/text())) &gt; number($expressionLength)">
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
										                    <xsl:when test="count(aors:ObjectRef) &gt; 1 or string-length(normalize-space(aors:ObjectRef/text())) &gt; number($expressionLength)">
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
	
	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:DO"
                 mode="content"
                 priority="-5">
		    <xsl:apply-templates select="aors:*" mode="doThenElse"/>
	  </xsl:template>

	  <!-- IF -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:IF"
                 mode="content"
                 priority="-5">
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
	
	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:THEN"
                 mode="content"
                 priority="-5">
		    <xsl:apply-templates select="aors:*" mode="doThenElse"/>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-AGT|aors:UPDATE-ENV|aors:SCHEDULE-EVT|aors:CREATE-EVT"
                 mode="section2Content"
                 priority="-5">
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="$content" mode="doThenElse"/>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:InitializationRule/aors:UpdateObject|aors:InitializationRule/aors:UpdateObjects|aors:InitializationRule/aors:UpdateGridCell"
                 mode="section2Content"
                 priority="-5">
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="$content[1]" mode="doThenElse">
			      <xsl:with-param name="content" select="$content"/>
		    </xsl:apply-templates>
	  </xsl:template>
	
	  <!-- ELSE -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:ELSE"
                 mode="content"
                 priority="-5">
		    <xsl:apply-templates select="aors:*" mode="doThenElse"/>
	  </xsl:template>

	  <!-- UPDATE-AGT -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:UPDATE-AGT"
                 mode="doThenElse"
                 priority="-5">
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

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-AGT/aors:Slot|aors:UPDATE-AGT/aors:SelfBeliefSlot"
                 mode="content"
                 priority="-5">
		    <xsl:param name="content"/>
		    <ul class="update">
			      <xsl:apply-templates select="$content" mode="update"/>
		    </ul>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-AGT/aors:Slot"
                 mode="update"
                 priority="-5">
		    <li>
			      <xsl:apply-templates select="." mode="slot">
				        <xsl:with-param name="maybeInline" select="true()"/>
			      </xsl:apply-templates>
		    </li>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-AGT/aors:SelfBeliefSlot"
                 mode="update"
                 priority="-5">
		    <li>
			      <xsl:text>[SB] </xsl:text>
			      <xsl:apply-templates select="." mode="slot">
				        <xsl:with-param name="maybeInline" select="true()"/>
			      </xsl:apply-templates>
		    </li>
	  </xsl:template>
	
	  <!-- UPDATE-AGT/UpdateComplexDataProperty -->
	
	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-AGT/aors:UpdateComplexDataPropertyValue"
                 mode="content"
                 priority="-5">
		    <xsl:param name="content"/>
		    <ul class="update">
			      <xsl:apply-templates select="." mode="complexDataProperty"/>
		    </ul>
	  </xsl:template>
	
	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UpdateComplexDataPropertyValue"
                 mode="complexDataProperty"
                 priority="-5">
		    <li>
			      <xsl:value-of select="concat(@complexDataProperty,'.',@procedure,'(')"/>
			      <xsl:apply-templates select="aors:Argument" mode="complexDataProperty"/>
			      <xsl:text>)</xsl:text>
		    </li>
	  </xsl:template>
	
	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UpdateComplexDataPropertyValue/aors:Argument"
                 mode="complexDataProperty"
                 priority="-5">
		    <xsl:variable name="argument"
                    select="concat('Argument',count(preceding-sibling::aors:Argument) + 1)"/>
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
			      <xsl:with-param name="headingPrefix"
                         select="concat('Code of ',../@complexDataProperty,'.',../@procedure)"/>
		    </xsl:call-template>
	  </xsl:template>

	  <!-- UPDATE-AGT/UpdateBeliefEntity -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-AGT/aors:UpdateBeliefEntity"
                 mode="content"
                 priority="-5">
		    <xsl:param name="content"/>
		    <ul class="update">
			      <xsl:apply-templates select="$content" mode="update"/>
		    </ul>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-AGT/aors:UpdateBeliefEntity"
                 mode="update"
                 priority="-5">
		    <xsl:apply-templates select="aors:BeliefSlot|aors:Increment|aors:Decrement" mode="update"/>
	  </xsl:template>
	
	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-AGT/aors:UpdateBeliefEntity/aors:BeliefSlot|aors:UPDATE-AGT/aors:UpdateBeliefEntity/aors:Increment|aors:UPDATE-AGT/aors:UpdateBeliefEntity/aors:Decrement"
                 mode="update"
                 priority="-5">
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

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-AGT/aors:CreateBeliefEntity"
                 mode="section3Content"
                 priority="-5">
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="$content" mode="class">
			      <xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
			      <xsl:with-param name="headingElement" select="$section4Heading"/>
			      <xsl:with-param name="kind" select="'object'"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-AGT/aors:CreateBeliefEntity"
                 mode="classContent"
                 priority="-5">
		    <xsl:apply-templates select="aors:BeliefSlot[1]" mode="classSection">
			      <xsl:with-param name="headingElement" select="$section5Heading"/>
			      <xsl:with-param name="class" select="'properties'"/>
			      <xsl:with-param name="content" select="aors:BeliefSlot"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <!-- UPDATE-AGT/DestroyBeliefEntity -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-AGT/aors:DestroyBeliefEntity"
                 mode="content"
                 priority="-5">
		    <xsl:param name="content"/>
		    <ul class="destroy">
			      <xsl:apply-templates select="$content" mode="destroy"/>
		    </ul>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-AGT/aors:DestroyBeliefEntity"
                 mode="destroy"
                 priority="-5">
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

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:UPDATE-ENV"
                 mode="doThenElse"
                 priority="-5">
		    <xsl:variable name="updateObjects" select="aors:UpdateObject|aors:UpdateObjects"/>
		    <xsl:apply-templates select="$updateObjects[1]" mode="section3">
			      <xsl:with-param name="content" select="$updateObjects"/>
		    </xsl:apply-templates>
		    <xsl:variable name="updateGridCells" select="aors:UpdateGridCell|aors:UpdateGridCells"/>
		    <xsl:apply-templates select="$updateGridCells[1]" mode="section3">
			      <xsl:with-param name="content" select="$updateGridCells"/>
		    </xsl:apply-templates>
		    <xsl:variable name="updateGlobalVariables"
                    select="aors:UpdateGlobalVariable|aors:IncrementGlobalVariable"/>
		    <xsl:apply-templates select="$updateGlobalVariables[1]" mode="section3">
			      <xsl:with-param name="content" select="$updateGlobalVariables"/>
		    </xsl:apply-templates>
		    <xsl:variable name="updateStatisticsVariables" select="aors:UpdateStatisticsVariable"/>
		    <xsl:apply-templates select="$updateStatisticsVariables[1]" mode="section3">
			      <xsl:with-param name="content" select="$updateStatisticsVariables"/>
		    </xsl:apply-templates>
		    <xsl:variable name="updateCollections"
                    select="aors:AddObjectToCollection|aors:RemoveObjectFromCollection"/>
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

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:InitializationRule/aors:UpdateObject|aors:InitializationRule/aors:UpdateObjects|aors:InitializationRule/aors:UpdateGridCell"
                 mode="doThenElse"
                 priority="-5">
		    <xsl:param name="content"/>
		    <ul class="update">
			      <xsl:apply-templates select="$content" mode="update"/>
		    </ul>
	  </xsl:template>
	
	  <!-- UPDATE-ENV/UpdateObject | UPDATE-ENV/UpdateObjects | UPDATE-ENV/UpdateGridCell | UPDATE-ENV/UpdateGridCells -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:UpdateObject|aors:UPDATE-ENV/aors:UpdateObjects|aors:UPDATE-ENV/aors:UpdateGridCell|aors:UPDATE-ENV/aors:UpdateGridCells|aors:InitializationRule/aors:UpdateObject|aors:InitializationRule/aors:UpdateObjects|aors:InitializationRule/aors:UpdateGridCell|aors:InitializationRule/aors:UpdateGridCells"
                 mode="content"
                 priority="-5">
		    <xsl:param name="content"/>
		    <ul class="update">
			      <xsl:apply-templates select="$content" mode="update"/>
		    </ul>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:UpdateObject|aors:InitializationRule/aors:UpdateObject"
                 mode="update"
                 priority="-5">
		    <xsl:apply-templates select="aors:Slot|aors:Increment|aors:Decrement" mode="update"/>
	  </xsl:template>
	
	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:UpdateObject/aors:Slot|aors:UPDATE-ENV/aors:UpdateObject/aors:Increment|aors:UPDATE-ENV/aors:UpdateObject/aors:Decrement|aors:InitializationRule/aors:UpdateObject/aors:Slot|aors:InitializationRule/aors:UpdateObject/aors:Increment|aors:InitializationRule/aors:UpdateObject/aors:Decrement"
                 mode="update"
                 priority="-5">
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

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:UpdateObjects|aors:InitializationRule/aors:UpdateObjects"
                 mode="update"
                 priority="-5">
		    <xsl:if test="aors:Slot|aors:Increment|aors:Decrement">
			      <li>
				        <xsl:value-of select="concat('FOR EACH ',@objectVariable,' ∈ [')"/>
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
	
	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:UpdateObjects/aors:Slot|aors:UPDATE-ENV/aors:UpdateObjects/aors:Increment|aors:UPDATE-ENV/aors:UpdateObjects/aors:Decrement|aors:InitializationRule/aors:UpdateObjects/aors:Slot|aors:InitializationRule/aors:UpdateObjects/aors:Increment|aors:InitializationRule/aors:UpdateObjects/aors:Decrement"
                 mode="update"
                 priority="-5">
		    <li>
			      <xsl:value-of select="concat(../@objectVariable,'.')"/>
			      <xsl:apply-templates select="aors:Slot|aors:Increment|aors:Decrement" mode="slot">
				        <xsl:with-param name="maybeInline" select="true()"/>
			      </xsl:apply-templates>
		    </li>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:UpdateGridCell|aors:InitializationRule/aors:UpdateGridCell"
                 mode="update"
                 priority="-5">
		    <xsl:apply-templates select="aors:Slot|aors:Increment|aors:Decrement" mode="update"/>
	  </xsl:template>
	
		
	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:UpdateGridCell/aors:Slot|aors:UPDATE-ENV/aors:UpdateGridCell/aors:Increment|aors:UPDATE-ENV/aors:UpdateGridCell/aors:Decrement|aors:InitializationRule/aors:UpdateGridCell/aors:Slot|aors:InitializationRule/aors:UpdateGridCell/aors:Increment|aors:InitializationRule/aors:UpdateGridCell/aors:Decrement"
                 mode="update"
                 priority="-5">
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
	
	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:UpdateGridCells|aors:InitializationRule/aors:UpdateGridCells"
                 mode="update"
                 priority="-5">
		    <xsl:if test="aors:Slot|aors:Decrement|aors:Increment">
			      <li>
				        <xsl:text>FOR EACH  </xsl:text>
				        <xsl:if test="@gridCellVariable">
					          <xsl:value-of select="concat(@gridCellVariable,' ∈ ')"/>
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

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:UpdateGridCells/aors:Slot|aors:UPDATE-ENV/aors:UpdateGridCells/aors:Increment|aors:UPDATE-ENV/aors:UpdateGridCells/aors:Decrement|aors:InitializationRule/aors:UpdateGridCells/aors:Slot|aors:InitializationRule/aors:UpdateGridCells/aors:Increment|aors:InitializationRule/aors:UpdateGridCells/aors:Decrement"
                 mode="update"
                 priority="-5">
		    <li>
			      <xsl:apply-templates select="." mode="slot">
				        <xsl:with-param name="maybeInline" select="true()"/>
			      </xsl:apply-templates>
		    </li>
	  </xsl:template>

	  <!-- UPDATE-ENV/UpdateGloalVariable | UPDATE-ENV/IncrementGlobalVariable | UPDATE-ENV/UpdateStatistcsVariable -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:UpdateGlobalVariable|aors:UPDATE-ENV/aors:IncrementGlobalVariable|aors:UPDATE-ENV/aors:UpdateStatisticsVariable"
                 mode="content"
                 priority="-5">
		    <xsl:param name="content"/>
		    <ul class="update">
			      <xsl:apply-templates select="$content" mode="update"/>
		    </ul>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:UpdateGlobalVariable"
                 mode="update"
                 priority="-5">
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
								                <xsl:when test="count(aors:ValueExpr) &gt; 1 or string-length(normalize-space(aors:ValueExpr/text())) &gt; number($expressionLength)">
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

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:IncrementGlobalVariable"
                 mode="update"
                 priority="-5">
		    <li>
			      <xsl:value-of select="concat(@name,' + ',@value)"/>
		    </li>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:UpdateStatisticsVariable"
                 mode="update"
                 priority="-5">
		    <li>
			      <xsl:value-of select="concat(@variable,' = ')"/>
			      <xsl:choose>
				        <xsl:when test="count(aors:ValueExpr) &gt; 1 or string-length(normalize-space(aors:ValueExpr/text())) &gt; number($expressionLength)">
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

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:AddObjectToCollection|aors:UPDATE-ENV/aors:RemoveObjectFromCollection"
                 mode="content"
                 priority="-5">
		    <xsl:param name="content"/>
		    <ul class="update">
			      <xsl:apply-templates select="$content" mode="update"/>
		    </ul>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:AddObjectToCollection"
                 mode="update"
                 priority="-5">
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

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:RemoveObjectFromCollection"
                 mode="update"
                 priority="-5">
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

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:Create"
                 mode="content"
                 priority="-5">
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="$content" mode="create">
			      <xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:Create/aors:Object|aors:UPDATE-ENV/aors:Create/aors:PhysicalObject|aors:UPDATE-ENV/aors:Create/aors:Agent|aors:UPDATE-ENV/aors:Create/aors:PhysicalAgent|aors:UPDATE-ENV/aors:Create/aors:Collection"
                 mode="create"
                 priority="-5">
		    <xsl:apply-templates select="." mode="class">
			      <xsl:with-param name="headingElement" select="$section4Heading"/>
			      <xsl:with-param name="kind" select="'object'"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:Create/aors:Collection"
                 mode="create"
                 priority="-5">
		    <xsl:apply-templates select="." mode="class">
			      <xsl:with-param name="headingElement" select="$section4Heading"/>
			      <xsl:with-param name="class" select="'parameterized'"/>
			      <xsl:with-param name="kind" select="'object'"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:Create/aors:Objects|aors:UPDATE-ENV/aors:Create/aors:PhysicalObjects|aors:UPDATE-ENV/aors:Create/aors:Agents|aors:UPDATE-ENV/aors:Create/aors:PhysicalAgents"
                 mode="create"
                 priority="-5">
		    <xsl:apply-templates select="." mode="class">
			      <xsl:with-param name="headingElement" select="$section4Heading"/>
			      <xsl:with-param name="kind" select="'objects'"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:Create/aors:*"
                 mode="class"
                 priority="-5">
		    <xsl:param name="headingElement"/>
		    <xsl:param name="class"/>
		    <xsl:param name="kind"/>
		    <xsl:apply-templates select="." mode="classBody">
			      <xsl:with-param name="headingElement" select="$headingElement"/>
			      <xsl:with-param name="class" select="$class"/>
			      <xsl:with-param name="kind" select="$kind"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="aors:BeliefEntity|aors:ReminderEvent|aors:TimeEvent|aors:PeriodicTimeEvent"
                           mode="class">
			      <xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
			      <xsl:with-param name="headingElement" select="$headingElement"/>
			      <xsl:with-param name="class" select="$class"/>
			      <xsl:with-param name="kind" select="'object'"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:Create/aors:*"
                 mode="classContent"
                 priority="-5">
		    <xsl:variable name="properties"
                    select="@*[local-name()!='type' and local-name()!='id' and local-name()!='name' and local-name()!='addToCollection' and local-name()!='rangeStartID' and local-name()!='rangeEndID' and local-name()!='objectVariable' and local-name()!='creationLoopVar' and local-name()!='itemType']|aors:Slot"/>
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

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:Create/aors:*/aors:BeliefEntity"
                 mode="classContent"
                 priority="-5">
		    <xsl:apply-templates select="aors:BeliefSlot[1]" mode="classSection">
			      <xsl:with-param name="headingElement" select="$section5Heading"/>
			      <xsl:with-param name="class" select="'properties'"/>
			      <xsl:with-param name="content" select="aors:BeliefSlot"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:Create/aors:*/aors:ReminderEvent|aors:UPDATE-ENV/aors:Create/aors:*/aors:TimeEvent|aors:UPDATE-ENV/aors:Create/aors:*/aors:PeriodicTimeEvent"
                 mode="classContent"
                 priority="-5">
		    <xsl:variable name="properties"
                    select="@*[local-name()!='type' and local-name()!='id' and local-name()!='name']|aors:Slot"/>
		    <xsl:apply-templates select="$properties[1]" mode="classSection">
			      <xsl:with-param name="headingElement" select="$section5Heading"/>
			      <xsl:with-param name="class" select="'properties'"/>
			      <xsl:with-param name="content" select="$properties"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:Create/aors:*/aors:BeliefEntity|aors:UPDATE-ENV/aors:Create/aors:*/aors:ReminderEvent|aors:UPDATE-ENV/aors:Create/aors:*/aors:TimeEvent|aors:UPDATE-ENV/aors:Create/aors:*/aors:PeriodicTimeEvent"
                 mode="classSectionContent"
                 priority="-5">
		    <xsl:param name="content"/>
		    <ul>
			      <xsl:apply-templates select="$content" mode="objectComponent"/>
		    </ul>
	  </xsl:template>

	  <!-- UPDATE-ENV/DestroyObject | UPDATE-ENV/DestroyObjects -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:DestroyObject|aors:UPDATE-ENV/aors:DestroyObjects"
                 mode="content"
                 priority="-5">
		    <xsl:param name="content"/>
		    <ul class="destroy">
			      <xsl:apply-templates select="$content" mode="destroy"/>
		    </ul>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:DestroyObject"
                 mode="destroy"
                 priority="-5">
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

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:UPDATE-ENV/aors:DestroyObjects"
                 mode="destroy"
                 priority="-5">
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

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:Increment|aors:Decrement"
                 mode="update"
                 priority="-5">
		    <dd>
			      <xsl:apply-templates select="." mode="slot"/>
		    </dd>
	  </xsl:template>

	  <!-- SCHEDULE-EVT -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:SCHEDULE-EVT"
                 mode="doThenElse"
                 priority="-5">
		    <xsl:apply-templates select="." mode="section3"/>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:SCHEDULE-EVT"
                 mode="section3Content"
                 priority="-5">
		    <xsl:apply-templates select="aors:ActionEventExpr|aors:OutMessageEventExpr|aors:ReminderEventExpr|aors:CausedEventExpr|aors:PerceptionEventExpr|aors:InMessageEventExpr|aors:ActivityStartEventExpr|aors:ActivityEndEventExpr"
                           mode="class">
			      <xsl:with-param name="headingElement" select="$section4Heading"/>
			      <xsl:with-param name="kind" select="'object'"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:ActionEventExpr|aors:OutMessageEventExpr|aors:ReminderEventExpr|aors:CausedEventExpr|aors:PerceptionEventExpr|aors:InMessageEventExpr|aors:ActivityStartEventExpr|aors:ActivityEndEventExpr"
                 mode="classContent"
                 priority="-5">
		    <xsl:variable name="properties"
                    select="@*[local-name()!='actionEventType' and local-name()!='messageType' and local-name()!='eventType' and local-name()!='activityType']|aors:Slot|aors:ReceiverIdRef[1]|aors:ReminderMsg[1]|aors:Delay[1]|aors:PerceiverIdRef[1]|aors:SenderIdRef[1]|aors:CorrelationValue[1]|aors:StartEventCorrelationProperty[1]|aors:EndEventCorrelationProperty[1]"/>
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

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:CREATE-EVT"
                 mode="doThenElse"
                 priority="-5">
		    <xsl:apply-templates select="." mode="section3"/>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:CREATE-EVT"
                 mode="section3Content"
                 priority="-5">
		    <xsl:apply-templates select="." mode="class">
			      <xsl:with-param name="headingElement" select="$section4Heading"/>
			      <xsl:with-param name="kind" select="'object'"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:CREATE-EVT"
                 mode="classContent"
                 priority="-5">
		    <xsl:variable name="properties"
                    select="@*[local-name()!='actualPercEvtType']|aors:Slot|aors:SenderIdRef[1]"/>
		    <xsl:apply-templates select="$properties[1]" mode="classSection">
			      <xsl:with-param name="headingElement" select="$section5Heading"/>
			      <xsl:with-param name="class" select="'properties'"/>
			      <xsl:with-param name="content" select="$properties"/>
		    </xsl:apply-templates>
	  </xsl:template>
   <!--copied from Condition.xsl-->

	

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:StopCondition|aors:Periodicity|aors:Condition"
                 mode="classSectionHeading"
                 priority="-6">
		    <xsl:text>Conditions</xsl:text>
	  </xsl:template>

	  <!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template match="aors:Periodicity|aors:StopCondition|aors:Condition"
                 mode="classSectionContent"
                 priority="-6">
		    <xsl:param name="content"/>
		    <ul>
			      <xsl:apply-templates select="$content" mode="condition"/>
		    </ul>
	  </xsl:template>

	  <xsl:template match="aors:Periodicity" mode="condition" priority="-6">
		    <li>
			      <xsl:call-template name="hideContent">
				        <xsl:with-param name="content">
					          <xsl:call-template name="copyCode">
						            <xsl:with-param name="code" select=".|following-sibling::aors:Periodicity"/>
					          </xsl:call-template>
				        </xsl:with-param>
				        <xsl:with-param name="heading" select="'Periodicity'"/>
				        <xsl:with-param name="headingPrefix" select="concat('Code of ',../@name,'.')"/>
			      </xsl:call-template>
		    </li>
	  </xsl:template>

	  <xsl:template match="aors:StopCondition" mode="condition" priority="-6">
		    <li>
			      <xsl:call-template name="hideContent">
				        <xsl:with-param name="content">
					          <xsl:call-template name="copyCode">
						            <xsl:with-param name="code" select=".|following-sibling::aors:StopCondition"/>
					          </xsl:call-template>
				        </xsl:with-param>
				        <xsl:with-param name="heading" select="'StopCondition'"/>
				        <xsl:with-param name="headingPrefix" select="concat('Code of ',../@name,'.')"/>
			      </xsl:call-template>
		    </li>
	  </xsl:template>

	  <xsl:template match="aors:Condition" mode="condition" priority="-6">
		    <li>
			      <xsl:call-template name="hideContent">
				        <xsl:with-param name="content">
					          <xsl:call-template name="copyCode">
						            <xsl:with-param name="code" select=".|following-sibling::aors:Condition"/>
					          </xsl:call-template>
				        </xsl:with-param>
				        <xsl:with-param name="heading" select="'Condition'"/>
				        <xsl:with-param name="headingPrefix" select="concat('Code of ',local-name(..),'.')"/>
			      </xsl:call-template>
		    </li>
	  </xsl:template>
   <!--copied from Property.xsl-->

	

	

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
						            <xsl:when test="$entity/aors:Attribute[@name=$name]/@upperMultiplicity='unbounded'">
							              <xsl:value-of select="'[∗]'"/>
						            </xsl:when>
						            <xsl:otherwise>
							              <xsl:value-of select="concat(' [',$entity/aors:Attribute[@name=$name]/@upperMultiplicity,']')"/>
						            </xsl:otherwise>
					          </xsl:choose>
				        </xsl:if>
				        <xsl:value-of select="concat(' : ',$entity/aors:Attribute[@name=$name]/@type,' = ',$value)"/>
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

	<xsl:template match="@*[local-name(..) != 'AgentRule' and local-name(..) != 'CommunicationRule' and local-name(..) != 'ActualPerceptionRule']|aors:InitialAttributeValue|aors:Attribute|aors:ReferenceProperty|aors:ComplexDataProperty|aors:EnumerationProperty|aors:Slot|aors:BeliefSlot|aors:ReceiverIdRef|aors:ReminderMsg|aors:Delay|aors:SenderIdRef|aors:PerceiverIdRef|aors:CorrelationValue|aors:StartEventCorrelationProperty|aors:EndEventCorrelationProperty|aors:GridCellProperty"
                 mode="classSectionHeading"
                 priority="-7">
		    <xsl:text>Properties</xsl:text>
	  </xsl:template>

	  <xsl:template match="aors:SelfBeliefAttribute|aors:SelfBeliefReferenceProperty|aors:SelfBeliefSlot"
                 mode="classSectionHeading"
                 priority="-7">
		    <xsl:text>Self-Belief Properties</xsl:text>
	  </xsl:template>

	  <xsl:template match="aors:GlobalVariable" mode="classSectionHeading" priority="-7">
		    <xsl:text>Variables</xsl:text>
	  </xsl:template>

	  <!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template match="@*[local-name(..)!='AgentRule' and local-name(..)!='CommunicationRule' and local-name()!='ActualPerceptionRule']|aors:InitialAttributeValue|aors:Attribute|aors:ReferenceProperty|aors:ComplexDataProperty|aors:EnumerationProperty|aors:SelfBeliefAttribute|aors:SelfBeliefReferenceProperty|aors:BeliefAttribute|aors:BeliefReferenceProperty|aors:GlobalVariable|aors:Slot|aors:BeliefSlot|aors:SelfBeliefSlot|aors:ReceiverIdRef|aors:ReminderMsg|aors:Delay|aors:SenderIdRef|aors:PerceiverIdRef|aors:CorrelationValue|aors:StartEventCorrelationProperty|aors:EndEventCorrelationProperty|aors:GridCellProperty"
                 mode="classSectionContent"
                 priority="-7">
		    <xsl:param name="content"/>
		    <ul>
			      <xsl:apply-templates select="$content" mode="property"/>
		    </ul>
	  </xsl:template>

	  <!-- @ -->

	<xsl:template match="@*" mode="property" priority="-7">
		    <li>
			      <xsl:value-of select="concat(local-name(),' = ',.)"/>
		    </li>
	  </xsl:template>

	  <xsl:template match="@autoPerception" mode="property" priority="-7">
		    <li>
			      <xsl:value-of select="concat(local-name(),' : Boolean = ',.)"/>
		    </li>
	  </xsl:template>

	  <xsl:template match="@idPerceivable" mode="property" priority="-7">
		    <li>
			      <xsl:value-of select="concat(local-name(),' : Boolean = ',.)"/>
		    </li>
	  </xsl:template>

	  <xsl:template match="@memorySize" mode="property" priority="-7">
		    <li>
			      <xsl:value-of select="concat(local-name(),' : Integer = ',.)"/>
		    </li>
	  </xsl:template>

	  <!-- InitialAttributeValue -->

	<xsl:template match="aors:InitialAttributeValue" mode="property" priority="-7">
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
                 mode="property"
                 priority="-7">
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
						            <xsl:text> [∗]</xsl:text>
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

	<xsl:template match="aors:ReferenceProperty|aors:SelfBeliefReferenceProperty|aors:BeliefReferenceProperty"
                 mode="property"
                 priority="-7">
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
						            <xsl:text> [∗]</xsl:text>
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

	<xsl:template match="aors:ComplexDataProperty|aors:EnumerationProperty" mode="property"
                 priority="-7">
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
						            <xsl:text> [∗]</xsl:text>
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

	<xsl:template match="aors:GlobalVariable" mode="property" priority="-7">
		    <li>
		       <xsl:attribute name="id">
		          <xsl:call-template name="getId"/>
		       </xsl:attribute>
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

	<xsl:template match="aors:Slot|aors:BeliefSlot|aors:SelfBeliefSlot" mode="property"
                 priority="-7">
		    <li>
			      <xsl:apply-templates select="." mode="slot"/>
		    </li>
	  </xsl:template>

	  <!-- ReceiverIdRef -->

	<xsl:template match="aors:ReceiverIdRef" mode="property" priority="-7">
		    <li>
			      <xsl:text>receiverIdRef = </xsl:text>
			      <xsl:call-template name="hideContent">
				        <xsl:with-param name="content">
					          <xsl:call-template name="copyCode">
						            <xsl:with-param name="code" select=".|following-sibling::aors:ReceiverIdRef"/>
					          </xsl:call-template>
				        </xsl:with-param>
				        <xsl:with-param name="heading" select="'Expression'"/>
				        <xsl:with-param name="headingPrefix"
                            select="concat('Code of ',local-name(..),'.receiverIdRef.')"/>
			      </xsl:call-template>
		    </li>
	  </xsl:template>

	  <!-- ReminderMsg -->

	<xsl:template match="aors:ReminderMsg" mode="property" priority="-7">
		    <li>
			      <xsl:text>reminderMsg = </xsl:text>
			      <xsl:call-template name="hideContent">
				        <xsl:with-param name="content">
					          <xsl:call-template name="copyCode">
						            <xsl:with-param name="code" select=".|following-sibling::aors:ReminderMsg"/>
					          </xsl:call-template>
				        </xsl:with-param>
				        <xsl:with-param name="heading" select="'Expression'"/>
				        <xsl:with-param name="headingPrefix" select="concat('Code of ',local-name(..),'.reminderMsg.')"/>
			      </xsl:call-template>
		    </li>
	  </xsl:template>

	  <!-- Delay -->

	<xsl:template match="aors:Delay" mode="property" priority="-7">
		    <li>
			      <xsl:text>delay = </xsl:text>
			      <xsl:call-template name="hideContent">
				        <xsl:with-param name="content">
					          <xsl:call-template name="copyCode">
						            <xsl:with-param name="code" select=".|following-sibling::aors:Delay"/>
					          </xsl:call-template>
				        </xsl:with-param>
				        <xsl:with-param name="heading" select="'Expression'"/>
				        <xsl:with-param name="headingPrefix" select="concat('Code of ',local-name(..),'.delay.')"/>
			      </xsl:call-template>
		    </li>
	  </xsl:template>

	  <!-- SenderIdRef -->

	<xsl:template match="aors:SenderIdRef" mode="property" priority="-7">
		    <li>
			      <xsl:text>senderIdRef = </xsl:text>
			      <xsl:call-template name="hideContent">
				        <xsl:with-param name="content">
					          <xsl:call-template name="copyCode">
						            <xsl:with-param name="code" select=".|following-sibling::aors:SenderIdRef"/>
					          </xsl:call-template>
				        </xsl:with-param>
				        <xsl:with-param name="heading" select="'Expression'"/>
				        <xsl:with-param name="headingPrefix" select="concat('Code of ',local-name(..),'.senderIdRef.')"/>
			      </xsl:call-template>
		    </li>
	  </xsl:template>

	  <!-- PerceiverIdRef -->
	<xsl:template match="aors:PerceiverIdRef" mode="property" priority="-7">
		    <li>
			      <xsl:text>perceiverIdRef = </xsl:text>
			      <xsl:call-template name="hideContent">
				        <xsl:with-param name="content">
					          <xsl:call-template name="copyCode">
						            <xsl:with-param name="code" select=".|following-sibling::aors:PerceiverIdRef"/>
					          </xsl:call-template>
				        </xsl:with-param>
				        <xsl:with-param name="heading" select="'Expression'"/>
				        <xsl:with-param name="headingPrefix"
                            select="concat('Code of ',local-name(..),'.perceiverIdRef.')"/>
			      </xsl:call-template>
		    </li>
	  </xsl:template>

	  <!-- CorrelationValue -->

	<xsl:template match="aors:CorrelationValue" mode="property" priority="-7">
		    <li>
			      <xsl:text>correlationValue = </xsl:text>
			      <xsl:call-template name="hideContent">
				        <xsl:with-param name="content">
					          <xsl:call-template name="copyCode">
						            <xsl:with-param name="code" select=".|following-sibling::aors:CorrelationValue"/>
					          </xsl:call-template>
				        </xsl:with-param>
				        <xsl:with-param name="heading" select="'Expression'"/>
				        <xsl:with-param name="headingPrefix"
                            select="concat('Code of ',local-name(..),'.correlationValue.')"/>
			      </xsl:call-template>
		    </li>
	  </xsl:template>

	  <!-- StartEventCorrelationProperty -->

	<xsl:template match="aors:StartEventCorrelationProperty" mode="property" priority="-7">
		    <li>
			      <xsl:text>startEventCorrelationProperty = </xsl:text>
			      <xsl:call-template name="hideContent">
				        <xsl:with-param name="content">
					          <xsl:call-template name="copyCode">
						            <xsl:with-param name="code" select=".|following-sibling::aors:StartEventCorrelationProperty"/>
					          </xsl:call-template>
				        </xsl:with-param>
				        <xsl:with-param name="heading" select="'Expression'"/>
				        <xsl:with-param name="headingPrefix"
                            select="concat('Code of ',local-name(..),'.startEventCorrelationProperty.')"/>
			      </xsl:call-template>
		    </li>
	  </xsl:template>

	  <!-- EndEventCorrelationProperty -->

	<xsl:template match="aors:EndEventCorrelationProperty" mode="property" priority="-7">
		    <li>
			      <xsl:text>endEventCorrelationProperty = </xsl:text>
			      <xsl:call-template name="hideContent">
				        <xsl:with-param name="content">
					          <xsl:call-template name="copyCode">
						            <xsl:with-param name="code" select=".|following-sibling::aors:EndEventCorrelationProperty"/>
					          </xsl:call-template>
				        </xsl:with-param>
				        <xsl:with-param name="heading" select="'Expression'"/>
				        <xsl:with-param name="headingPrefix"
                            select="concat('Code of ',local-name(..),'.endEventCorrelationProperty.')"/>
			      </xsl:call-template>
		    </li>
	  </xsl:template>
   <!--copied from Object.xsl-->

	

	

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<!-- Object, PhysicalObject, Agent and PhysicalAgent -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:Object|aors:PhysicalObject|aors:Agent|aors:PhysicalAgent"
                 mode="classHeading"
                 priority="-8">
		    <span class="stereotype small">
			      <xsl:value-of select="concat('«',local-name(),'»')"/>
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

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:Object|aors:PhysicalObject|aors:Agent|aors:PhysicalAgent"
                 mode="headingName"
                 priority="-8">
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

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:Object|aors:PhysicalObject|aors:Agent|aors:PhysicalAgent"
                 mode="headingType"
                 priority="-8">
		    <xsl:call-template name="createOptionalLink">
			      <xsl:with-param name="node" select="key('EntityTypes',@type)"/>
			      <xsl:with-param name="text" select="@type"/>
		    </xsl:call-template>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:Create/aors:Collection"
                 mode="classHeading"
                 priority="-8">
		    <span class="stereotype small">
			      <xsl:value-of select="concat('«',@type,'»')"/>
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

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:Objects|aors:PhysicalObjects|aors:Agents|aors:PhysicalAgents"
                 mode="classHeading"
                 priority="-8">
		    <span class="stereotype small">
			      <xsl:value-of select="concat('«',local-name(),'»')"/>
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

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:Objects|aors:PhysicalObjects|aors:Agents|aors:PhysicalAgents"
                 mode="headingName"
                 priority="-8">
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

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:Objects|aors:PhysicalObjects|aors:Agents|aors:PhysicalAgents"
                 mode="headingType"
                 priority="-8">
		    <xsl:call-template name="createOptionalLink">
			      <xsl:with-param name="node" select="key('EntityTypes',@type)"/>
			      <xsl:with-param name="text" select="@type"/>
		    </xsl:call-template>
	  </xsl:template>

	  <!-- BeliefEntity -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:BeliefEntity"
                 mode="classHeading"
                 priority="-8">
		    <span class="stereotype small">
			      <xsl:value-of select="concat('«',local-name(),'»')"/>
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
			      <xsl:text/>
			      <xsl:text>::</xsl:text>
			      <xsl:apply-templates select="." mode="headingName"/>
			      <xsl:text> : </xsl:text>
			      <xsl:apply-templates select=".." mode="headingType"/>
			      <xsl:text>::</xsl:text>
			      <xsl:apply-templates select="." mode="headingType"/>
		    </span>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:BeliefEntity"
                 mode="headingName"
                 priority="-8">
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

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:BeliefEntity"
                 mode="headingType"
                 priority="-8">
		    <xsl:call-template name="createOptionalLink">
			      <xsl:with-param name="node" select="key('EntityTypes',@type)"/>
			      <xsl:with-param name="text" select="@type"/>
		    </xsl:call-template>
		    <xsl:text> </xsl:text>
	  </xsl:template>

	  <!-- CreateBeliefEntity -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:CreateBeliefEntity"
                 mode="classHeading"
                 priority="-8">
		    <span class="stereotype small">
			      <xsl:text>«BeliefEntity»</xsl:text>
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

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:TimeEvent|aors:PeriodicTimeEvent|aors:ReminderEvent"
                 mode="classHeading"
                 priority="-8">
		    <span class="stereotype small">
			      <xsl:value-of select="concat('«',local-name(),'»')"/>
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
			      <xsl:text/>
			      <xsl:text>::</xsl:text>
			      <xsl:apply-templates select="." mode="headingName"/>
			      <xsl:text> : </xsl:text>
			      <xsl:apply-templates select=".." mode="headingType"/>
			      <xsl:text>::</xsl:text>
			      <xsl:apply-templates select="." mode="headingType"/>
			      <xsl:text> </xsl:text>
		    </span>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:ReminderEvent|aors:TimeEvent|aors:PeriodicTimeEvent"
                 mode="headingName"
                 priority="-8">
		    <xsl:call-template name="getOptionalValue">
			      <xsl:with-param name="node">
				        <xsl:value-of select="@name"/>
				        <xsl:if test="@id">
					          <xsl:value-of select="concat('[',@id,']')"/>
				        </xsl:if>
			      </xsl:with-param>
		    </xsl:call-template>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:ReminderEvent|aors:TimeEvent|aors:PeriodicTimeEvent"
                 mode="headingType"
                 priority="-8">
		    <xsl:call-template name="createOptionalLink">
			      <xsl:with-param name="node" select="key('EntityTypes',@type)"/>
			      <xsl:with-param name="text" select="@type"/>
		    </xsl:call-template>
	  </xsl:template>

	  <!-- ExogenousEvent, CausedEvent -->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:ExogenousEvent | aors:CausedEvent"
                 mode="classHeading"
                 priority="-8">
		    <span class="stereotype small">
			      <xsl:value-of select="concat('«',local-name(),'»')"/>
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

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:ActionEventExpr"
                 mode="classHeading"
                 priority="-8">
		    <span class="stereotype small">
			      <xsl:value-of select="concat('«',substring-before(local-name(),'Expr'),'»')"/>
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

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:CausedEventExpr|aors:PerceptionEventExpr"
                 mode="classHeading"
                 priority="-8">
		    <span class="stereotype small">
			      <xsl:value-of select="concat('«',substring-before(local-name(),'Expr'),'»')"/>
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

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:ReminderEventExpr"
                 mode="classHeading"
                 priority="-8">
		    <span class="name">
			      <xsl:text> ReminderEvent </xsl:text>
		    </span>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:OutMessageEventExpr|aors:InMessageEventExpr"
                 mode="classHeading"
                 priority="-8">
		    <span class="stereotype small">
			      <xsl:value-of select="concat('«',substring-before(local-name(),'Expr'),'»')"/>
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

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:ActivityStartEventExpr|aors:ActivityEndEventExpr"
                 mode="classHeading"
                 priority="-8">
		    <span class="stereotype small">
			      <xsl:value-of select="concat('«',substring-before(local-name(),'Expr'),'»')"/>
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

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:CREATE-EVT"
                 mode="classHeading"
                 priority="-8">
		    <span class="stereotype small">
			      <xsl:text>«ActualPerceptionEvent»</xsl:text>
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

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:BeliefEntity|aors:ReminderEvent|aors:TimeEvent|aors:PeriodicTimeEvent"
                 mode="objectComponent"
                 priority="-8">
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
   <!--copied from Slot.xsl-->

	

	<!--###############-->
	<!--### content ###-->
	<!--###############-->

	<xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:Slot[@xsi:type = 'SimpleSlot' or substring-after(@xsi:type,':') = 'SimpleSlot' or @value]|aors:BeliefSlot[@xsi:type = 'SimpleSlot' or substring-after(@xsi:type,':') = 'SimpleSlot' or @value]|aors:SelfBeliefSlot[@xsi:type = 'SimpleSlot' or substring-after(@xsi:type,':') = 'SimpleSlot' or @value]"
                 mode="slot"
                 priority="-9">
		    <xsl:value-of select="concat(@property,' = ',@value)"/>
	  </xsl:template>

	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:Slot[@xsi:type = 'OpaqueExprSlot' or substring-after(@xsi:type,':') = 'OpaqueExprSlot' or aors:ValueExpr]|aors:BeliefSlot[@xsi:type = 'OpaqueExprSlot' or substring-after(@xsi:type,':') = 'OpaqueExprSlot' or aors:ValueExpr]|aors:SelfBeliefSlot[@xsi:type = 'OpaqueExprSlot' or substring-after(@xsi:type,':') = 'OpaqueExprSlot' or aors:ValueExpr]"
                 mode="slot"
                 priority="-9">
		    <xsl:param name="maybeInline" select="false()"/>
		    <xsl:value-of select="concat(@property,' = ')"/>
		    <xsl:choose>
			      <xsl:when test="not($maybeInline) or (count(aors:ValueExpr) &gt; 1 or string-length(normalize-space(aors:ValueExpr/text())) &gt; 70)">
				        <xsl:call-template name="hideContent">
					          <xsl:with-param name="content">
						            <xsl:call-template name="copyCode">
							              <xsl:with-param name="code" select="aors:ValueExpr"/>
						            </xsl:call-template>
					          </xsl:with-param>
					          <xsl:with-param name="heading" select="'Expression'"/>
					          <xsl:with-param name="headingPrefix" select="concat('Code of ',@property,'.')"/>
				        </xsl:call-template>
			      </xsl:when>
			      <xsl:otherwise>
				        <xsl:call-template name="copyCode">
					          <xsl:with-param name="code" select="aors:ValueExpr"/>
					          <xsl:with-param name="class" select="'inline'"/>
				        </xsl:call-template>
			      </xsl:otherwise>
		    </xsl:choose>
	  </xsl:template>
	
	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:Increment"
                 mode="slot"
                 priority="-9">
		    <xsl:value-of select="concat(@property,' + ',@value)"/>
	  </xsl:template>
	
	  <xsl:template xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" match="aors:Decrement"
                 mode="slot"
                 priority="-9">
		    <xsl:value-of select="concat(@property,' - ',@value)"/>
	  </xsl:template>
   <!--copied from prettyprint_withoutScenarioData.xsl-->

	
	
	
	
	
	
	
	
	
	
	
	

	

	<!--####################-->
	<!--### local macros ###-->
	<!--####################-->

	<xsl:template xmlns:html="http://www.w3.org/1999/xhtml"
                 xmlns:allex="http://aor-simulation.org/allex"
                 xmlns:aorsel="http://aor-simulation.org/aorsel"
                 xmlns:dc="http://purl.org/dc/elements/1.1/"
                 xmlns:xs="http://www.w3.org/2001/XMLSchema"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 name="getModelTitle">
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

	<xsl:template xmlns:html="http://www.w3.org/1999/xhtml"
                 xmlns:allex="http://aor-simulation.org/allex"
                 xmlns:aorsel="http://aor-simulation.org/aorsel"
                 xmlns:dc="http://purl.org/dc/elements/1.1/"
                 xmlns:xs="http://www.w3.org/2001/XMLSchema"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="*"
                 priority="-10"/>

	  <xsl:template xmlns:html="http://www.w3.org/1999/xhtml"
                 xmlns:allex="http://aor-simulation.org/allex"
                 xmlns:aorsel="http://aor-simulation.org/aorsel"
                 xmlns:dc="http://purl.org/dc/elements/1.1/"
                 xmlns:xs="http://www.w3.org/2001/XMLSchema"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="/"
                 priority="-10">
		    <html>
			      <head>
				        <xsl:apply-templates select="." mode="head"/>
			      </head>
			      <body>
				        <xsl:apply-templates select="." mode="body"/>
			      </body>
		    </html>
	  </xsl:template>

	  <xsl:template xmlns:html="http://www.w3.org/1999/xhtml"
                 xmlns:allex="http://aor-simulation.org/allex"
                 xmlns:aorsel="http://aor-simulation.org/aorsel"
                 xmlns:dc="http://purl.org/dc/elements/1.1/"
                 xmlns:xs="http://www.w3.org/2001/XMLSchema"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="/"
                 mode="head"
                 priority="-10">
		    <title>
			      <xsl:call-template name="getModelTitle"/>
		    </title>
		    <meta http-equiv="Content-Type" content="application/xml; charset=UTF-8"/>
		    <link rel="stylesheet">
			      <xsl:attribute name="href">
				        <xsl:choose>
					          <xsl:when test="aors:SimulationScenario/@simulationManagerDirectory">
						            <xsl:variable name="simulatorDir"
                                select="aors:SimulationScenario/@simulationManagerDirectory"/>
						            <xsl:value-of select="$simulatorDir"/>
						            <xsl:if test="translate(substring($simulatorDir,string-length($simulatorDir)),'\','/') != '/'">
							              <xsl:text>/</xsl:text>
						            </xsl:if>
					          </xsl:when>
					          <xsl:otherwise>
						            <xsl:text>../</xsl:text>
					          </xsl:otherwise>
				        </xsl:choose>
				        <xsl:text>PrettyPrint/css/prettyprint.css</xsl:text>
			      </xsl:attribute>
		    </link>
	  </xsl:template>

	

	  <!--##########################-->
	<!--### documentNavigation ###-->
	<!--##########################-->

	

	

	<!--####################-->
	<!--### documentBody ###-->
	<!--####################-->

	<xsl:template xmlns:html="http://www.w3.org/1999/xhtml"
                 xmlns:allex="http://aor-simulation.org/allex"
                 xmlns:aorsel="http://aor-simulation.org/aorsel"
                 xmlns:dc="http://purl.org/dc/elements/1.1/"
                 xmlns:xs="http://www.w3.org/2001/XMLSchema"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="*"
                 mode="documentBody"
                 priority="-10"/>

	  <xsl:template xmlns:html="http://www.w3.org/1999/xhtml"
                 xmlns:allex="http://aor-simulation.org/allex"
                 xmlns:aorsel="http://aor-simulation.org/aorsel"
                 xmlns:dc="http://purl.org/dc/elements/1.1/"
                 xmlns:xs="http://www.w3.org/2001/XMLSchema"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 match="aors:SimulationModel"
                 mode="documentBody"
                 priority="-10">
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
		    <xsl:variable name="agentRules"
                    select="aors:EntityTypes/aors:*/aors:ActualPerceptionRule|aors:EntityTypes/aors:*/aors:ReactionRule|aors:EntityTypes/aors:*/aors:CommunicationRule"/>
		    <xsl:apply-templates select="$agentRules[1]" mode="chapter">
			      <xsl:with-param name="content" select="$agentRules"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="aors:EnvironmentRules" mode="chapter"/>
	  </xsl:template>
   <!--copied from EnvironmentRules.xsl-->

	

	

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:EnvironmentRules" mode="heading" priority="-11">
		    <xsl:text>Environment Rules</xsl:text>
	  </xsl:template>

	  <xsl:template match="aors:EnvironmentRule" mode="heading" priority="-11">
		    <xsl:value-of select="@name"/>
	  </xsl:template>

	  <!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:EnvironmentRules" mode="navigation" priority="-11">
		    <xsl:apply-templates select="." mode="navigationEntry">
			      <xsl:with-param name="subEntries">
				        <xsl:apply-templates select="*" mode="navigation"/>
			      </xsl:with-param>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:EnvironmentRule" mode="navigation" priority="-11">
		    <xsl:apply-templates select="." mode="navigationEntry"/>
	  </xsl:template>

	  <!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template match="aors:EnvironmentRules" mode="content" priority="-11">
		    <xsl:apply-templates select="aors:*" mode="section1"/>
	  </xsl:template>

	  <xsl:template match="aors:EnvironmentRule" mode="section1" priority="-11">
		    <xsl:param name="class"/>
		    <xsl:apply-templates select="." mode="section1Body">
			      <xsl:with-param name="class">
				        <xsl:text> rule</xsl:text>
				        <xsl:choose>
					          <xsl:when test="count(preceding-sibling::aors:EnvironmentRule) mod 2 = 0">
						            <xsl:text> odd</xsl:text>
					          </xsl:when>
					          <xsl:otherwise>
						            <xsl:text> even</xsl:text>
					          </xsl:otherwise>
				        </xsl:choose>
			      </xsl:with-param>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:EnvironmentRule" mode="content" priority="-11">
		    <xsl:apply-templates select="aors:documentation" mode="content"/>
		    <xsl:apply-templates select="aors:WHEN" mode="section2">
			      <xsl:with-param name="class" select="'when'"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="aors:ON-EACH-SIMULATION-STEP" mode="section2">
			      <xsl:with-param name="class" select="'on-each-simulation-step'"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="aors:FOR[1]" mode="section2">
			      <xsl:with-param name="content" select="aors:FOR"/>
			      <xsl:with-param name="class" select="'for'"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="aors:DO" mode="section2">
			      <xsl:with-param name="class" select="'do'"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="aors:IF[1]" mode="section2">
			      <xsl:with-param name="content" select="aors:IF"/>
			      <xsl:with-param name="class" select="'if'"/>
		    </xsl:apply-templates>
		    <xsl:variable name="then" select="aors:THEN|aors:UPDATE-ENV|aors:SCHEDULE-EVT"/>
		    <xsl:apply-templates select="$then[1]" mode="section2">
			      <xsl:with-param name="content" select="$then"/>
			      <xsl:with-param name="class" select="'then'"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="aors:ELSE" mode="section2">
			      <xsl:with-param name="class" select="'else'"/>
		    </xsl:apply-templates>
	  </xsl:template>
   <!--copied from AgentRules.xsl-->

	

	

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:ActualPerceptionRule|aors:ReactionRule|aors:CommunicationRule"
                 mode="chapterHeading"
                 priority="-12">
		    <xsl:text>Agent Rules</xsl:text>
	  </xsl:template>

	  <xsl:template match="aors:ActualPerceptionRule|aors:ReactionRule|aors:CommunicationRule"
                 mode="heading"
                 priority="-12">
		    <xsl:value-of select="concat(../@name,'::',@name)"/>
	  </xsl:template>

	  <!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:ActualPerceptionRule|aors:ReactionRule|aors:CommunicationRule"
                 mode="navigation"
                 priority="-12">
		    <xsl:param name="content"/>
		    <li>
			      <a>
				        <xsl:attribute name="href">
					          <xsl:call-template name="getId">
						            <xsl:with-param name="prefix" select="'#'"/>
					          </xsl:call-template>
				        </xsl:attribute>
				        <xsl:apply-templates select="." mode="chapterHeading"/>
			      </a>
			      <ul>
				        <xsl:apply-templates select="$content" mode="navigationEntry"/>
			      </ul>
		    </li>
	  </xsl:template>


	  <!--################-->
	<!--###  content ###-->
	<!--################-->

	<xsl:template match="aors:ActualPerceptionRule|aors:ReactionRule|aors:CommunicationRule"
                 mode="chapterContent"
                 priority="-12">
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="$content" mode="section1"/>
	  </xsl:template>

	  <xsl:template match="aors:ActualPerceptionRule|aors:ReactionRule|aors:CommunicationRule"
                 mode="section1"
                 priority="-12">
		    <xsl:param name="class"/>
		    <xsl:apply-templates select="." mode="section1Body">
			      <xsl:with-param name="class">
				        <xsl:text> rule</xsl:text>
				        <xsl:choose>
					          <xsl:when test="count(preceding::aors:ActualPerceptionRule|preceding::aors:ReactionRule|preceding::aors:CommunicationRule) mod 2 = 0">
						            <xsl:text> odd</xsl:text>
					          </xsl:when>
					          <xsl:otherwise>
						            <xsl:text> even</xsl:text>
					          </xsl:otherwise>
				        </xsl:choose>
			      </xsl:with-param>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:ReactionRule|aors:CommunicationRule" mode="content" priority="-12">
		    <xsl:apply-templates select="aors:documentation" mode="content"/>
		    <xsl:apply-templates select="aors:WHEN" mode="section2">
			      <xsl:with-param name="class" select="'when'"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="aors:ON-EACH-SIMULATION-STEP" mode="section2">
			      <xsl:with-param name="class" select="'on-each-simulation-step'"/>
		    </xsl:apply-templates>
		    <xsl:variable name="for" select="@agentVariable|aors:FOR"/>
		    <xsl:apply-templates select="$for[1]" mode="section2">
			      <xsl:with-param name="content" select="$for"/>
			      <xsl:with-param name="class" select="'for'"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="aors:DO" mode="section2">
			      <xsl:with-param name="class" select="'do'"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="aors:IF[1]" mode="section2">
			      <xsl:with-param name="content" select="aors:IF"/>
			      <xsl:with-param name="class" select="'if'"/>
		    </xsl:apply-templates>
		    <xsl:variable name="then" select="aors:THEN|aors:UPDATE-ENV|aors:SCHEDULE-EVT"/>
		    <xsl:apply-templates select="$then[1]" mode="section2">
			      <xsl:with-param name="content" select="$then"/>
			      <xsl:with-param name="class" select="'then'"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="aors:ELSE" mode="section2">
			      <xsl:with-param name="class" select="'else'"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:ActualPerceptionRule" mode="content" priority="-12">
		    <xsl:apply-templates select="aors:documentation" mode="content"/>
		    <xsl:apply-templates select="aors:WHEN" mode="section2">
			      <xsl:with-param name="class" select="'when'"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="@agentVariable" mode="section2">
			      <xsl:with-param name="content" select="@agentVariable"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="aors:IF[1]" mode="section2">
			      <xsl:with-param name="content" select="aors:IF"/>
			      <xsl:with-param name="class" select="'if'"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="aors:CREATE-EVT" mode="section2">
			      <xsl:with-param name="content" select="aors:CREATE-EVT"/>
			      <xsl:with-param name="class" select="'then'"/>
		    </xsl:apply-templates>
	  </xsl:template>
   <!--copied from EntityTypes.xsl-->

	
	
	

	

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:EntityTypes" mode="heading" priority="-13">
		    <xsl:text>Entity Types</xsl:text>
	  </xsl:template>

	  <xsl:template match="aors:EntityTypes/aors:*" mode="classHeading" priority="-13">
		    <span class="stereotype small">
			      <xsl:value-of select="concat('«',local-name(),'»')"/>
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

	<xsl:template match="aors:BeliefEntityType" mode="classSectionHeading" priority="-13">
		    <xsl:text>Belief Entity Types</xsl:text>
	  </xsl:template>

	  <xsl:template match="aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType"
                 mode="classSectionHeading"
                 priority="-13">
		    <xsl:text>Event Types</xsl:text>
	  </xsl:template>

	  <xsl:template match="aors:BeliefEntityType|aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType"
                 mode="classHeading"
                 priority="-13">
		    <span class="stereotype small">
			      <xsl:value-of select="concat('«',local-name(),'»')"/>
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

	<xsl:template match="aors:ActualPerceptionRule|aors:ReactionRule|aors:CommunicationRule"
                 mode="classSectionHeading"
                 priority="-13">
		    <xsl:text>Rules</xsl:text>
	  </xsl:template>

	  <!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:EntityTypes" mode="navigation" priority="-13">
		    <xsl:apply-templates select="." mode="navigationEntry">
			      <xsl:with-param name="subEntries">
				        <xsl:apply-templates select="*" mode="navigation">
					          <xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
				        </xsl:apply-templates>
			      </xsl:with-param>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:EntityTypes/aors:*" mode="navigation" priority="-13">
		    <xsl:apply-templates select="." mode="navigationEntry">
			      <xsl:with-param name="subEntries">
				        <xsl:apply-templates select="aors:BeliefEntityType|aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType"
                                 mode="navigation">
					          <xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
				        </xsl:apply-templates>
			      </xsl:with-param>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:EntityTypes/aors:*" mode="navigationEntryTitle" priority="-13">
		    <xsl:value-of select="@name"/>
	  </xsl:template>

	  <!-- BeliefEntityTypes and InternalEventTypes -->

	<xsl:template match="aors:BeliefEntityType|aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType"
                 mode="navigation"
                 priority="-13">
		    <xsl:apply-templates select="." mode="navigationEntry"/>
	  </xsl:template>

	  <xsl:template match="aors:BeliefEntityType|aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType"
                 mode="navigationEntryTitle"
                 priority="-13">
		    <xsl:value-of select="concat(../@name,'::',@name)"/>
	  </xsl:template>

	  <!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template match="aors:EntityTypes" mode="content" priority="-13">
		    <xsl:apply-templates select="aors:*" mode="class">
			      <xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
			      <xsl:with-param name="headingElement" select="$section1Heading"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:EntityTypes/aors:*" mode="class" priority="-13">
		    <xsl:param name="headingElement"/>
		    <xsl:apply-templates select="." mode="classBody">
			      <xsl:with-param name="headingElement" select="$headingElement"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="aors:BeliefEntityType|aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType"
                           mode="class">
			      <xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
			      <xsl:with-param name="headingElement" select="$headingElement"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:ActivityType" mode="class" priority="-13"/>

	  <xsl:template match="aors:EntityTypes/*|aors:BeliefEntityType|aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType"
                 mode="classContent"
                 priority="-13">
		    <xsl:variable name="properties"
                    select="@*[local-name()!='name' and local-name()!='superType']|aors:InitialAttributeValue|aors:Attribute|aors:ReferenceProperty|aors:ComplexDataProperty|aors:EnumerationProperty|aors:BeliefAttribute|aors:BeliefReferenceProperty"/>
		    <xsl:apply-templates select="$properties[1]" mode="classSection">
			      <xsl:with-param name="headingElement" select="$section2Heading"/>
			      <xsl:with-param name="class" select="'properties'"/>
			      <xsl:with-param name="content" select="$properties"/>
		    </xsl:apply-templates>
		    <xsl:variable name="selfBeliefProperties"
                    select="aors:SelfBeliefAttribute|aors:SelfBeliefReferenceProperty"/>
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
		    <xsl:variable name="events"
                    select="aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType"/>
		    <xsl:apply-templates select="$events[1]" mode="classSection">
			      <xsl:with-param name="headingElement" select="$section2Heading"/>
			      <xsl:with-param name="class" select="'events'"/>
			      <xsl:with-param name="content" select="$events"/>
		    </xsl:apply-templates>
		    <xsl:variable name="rules"
                    select="aors:ActualPerceptionRule|aors:ReactionRule|aors:CommunicationRule"/>
		    <xsl:apply-templates select="$rules[1]" mode="classSection">
			      <xsl:with-param name="headingElement" select="$section2Heading"/>
			      <xsl:with-param name="class" select="'rules'"/>
			      <xsl:with-param name="content" select="$rules"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:BeliefEntityType|aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType|aors:ActualPerceptionRule|aors:ReactionRule|aors:CommunicationRule"
                 mode="classSectionContent"
                 priority="-13">
		    <xsl:param name="content"/>
		    <ul>
			      <xsl:apply-templates select="$content" mode="entityTypeComponent"/>
		    </ul>
	  </xsl:template>

	  <xsl:template match="aors:BeliefEntityType|aors:ActualPerceptionEventType|aors:TimeEventType|aors:PeriodicTimeEventType|aors:ActualPerceptionRule|aors:ReactionRule|aors:CommunicationRule"
                 mode="entityTypeComponent"
                 priority="-13">
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
   <!--copied from Function.xsl-->

	

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:Function|aors:GlobalFunction|aors:GridCellFunction|aors:DefaultConstructor"
                 mode="classSectionHeading"
                 priority="-14">
		    <xsl:text>Functions</xsl:text>
	  </xsl:template>

	  <xsl:template match="aors:SubjectiveFunction" mode="classSectionHeading" priority="-14">
		    <xsl:text>Subjective Functions</xsl:text>
	  </xsl:template>

	  <!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template match="aors:Function|aors:SubjectiveFunction|aors:GlobalFunction|aors:GridCellFunction|aors:DefaultConstructor"
                 mode="classSectionContent"
                 priority="-14">
		    <xsl:param name="content"/>
		    <ul>
			      <xsl:apply-templates select="$content" mode="function"/>
		    </ul>
	  </xsl:template>
	
	  <xsl:template match="aors:DefaultConstructor" mode="function" priority="-14">
		    <li>
			      <xsl:call-template name="hideContent">
				        <xsl:with-param name="content">
					          <xsl:call-template name="copyCode">
						            <xsl:with-param name="code" select="aors:Def"/>
					          </xsl:call-template>
				        </xsl:with-param>
				        <xsl:with-param name="heading" select="local-name()"/>
				        <xsl:with-param name="headingPrefix" select="concat('Code of ',../@name,'.')"/>
			      </xsl:call-template>
		    </li>
	  </xsl:template>

	  <xsl:template match="aors:Function|aors:SubjectiveFunction|aors:GlobalFunction|aors:GridCellFunction"
                 mode="function"
                 priority="-14">
		    <li>
			      <xsl:call-template name="hideContent">
				        <xsl:with-param name="content">
					          <xsl:apply-templates select="aors:documentation" mode="content"/>
					          <xsl:call-template name="copyCode">
						            <xsl:with-param name="code" select="aors:Body"/>
					          </xsl:call-template>
				        </xsl:with-param>
				        <xsl:with-param name="heading">
					          <xsl:variable name="heading">
						            <xsl:value-of select="concat(@name,'(')"/>
						            <xsl:apply-templates select="aors:Parameter" mode="function"/>
						            <xsl:value-of select="concat(') : ',@resultType)"/>
					          </xsl:variable>
					          <xsl:choose>
						            <xsl:when test="@isStatic = 'true'">
							              <span class="static">
								                <xsl:copy-of select="$heading"/>
							              </span>
						            </xsl:when>
						            <xsl:otherwise>
							              <xsl:copy-of select="$heading"/>
						            </xsl:otherwise>
					          </xsl:choose>
				        </xsl:with-param>
				        <xsl:with-param name="headingPrefix">
					          <xsl:text>Code of </xsl:text>
					          <xsl:if test="../@name">
						            <xsl:value-of select="concat(../@name,'.')"/>
					          </xsl:if>
				        </xsl:with-param>
			      </xsl:call-template>
		    </li>
	  </xsl:template>

	  <xsl:template match="aors:Parameter" mode="function" priority="-14">
		    <xsl:value-of select="concat(@name,': ',@type)"/>
		    <xsl:if test="following-sibling::aors:Parameter">
			      <xsl:text>, </xsl:text>
		    </xsl:if>
	  </xsl:template>
   <!--copied from Globals.xsl-->

	
	

	

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:Globals" mode="heading" priority="-15">
		    <xsl:text>Globals</xsl:text>
	  </xsl:template>

	  <xsl:template match="aors:Globals" mode="classHeading" priority="-15">
		    <span class="name">
			      <xsl:apply-templates select="." mode="heading"/>
		    </span>
	  </xsl:template>

	  <!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:Globals" mode="navigation" priority="-15">
		    <xsl:apply-templates select="." mode="navigationEntry"/>
	  </xsl:template>

	  <!--################-->
	<!--###  content ###-->
	<!--################-->

	<xsl:template match="aors:Globals" mode="chapterContent" priority="-15">
		    <xsl:apply-templates select="." mode="class">
			      <xsl:with-param name="headingElement" select="$section1Heading"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:Globals" mode="classContent" priority="-15">
		    <xsl:apply-templates select="aors:GlobalVariable[1]" mode="classSection">
			      <xsl:with-param name="headingElement" select="$section2Heading"/>
			      <xsl:with-param name="class" select="'properties'"/>
			      <xsl:with-param name="content" select="aors:GlobalVariable"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="aors:GlobalFunction[1]" mode="classSection">
			      <xsl:with-param name="headingElement" select="$section2Heading"/>
			      <xsl:with-param name="class" select="'functions'"/>
			      <xsl:with-param name="content" select="aors:GlobalFunction"/>
		    </xsl:apply-templates>
	  </xsl:template>
   <!--copied from Collections.xsl-->

	

	

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:Collections" mode="heading" priority="-16">
		    <xsl:text>Collections</xsl:text>
	  </xsl:template>

	  <xsl:template match="aors:Collections/aors:Collection" mode="classHeading" priority="-16">
		    <span class="stereotype small">
			      <xsl:value-of select="concat('«',@type,'»')"/>
		    </span>
		    <span class="name">
			      <xsl:text> </xsl:text>
			      <xsl:call-template name="getOptionalValue">
				        <xsl:with-param name="node">
					          <xsl:if test="@name">
						            <xsl:value-of select="@name"/>
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


	  <!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:Collections" mode="navigation" priority="-16">
		    <xsl:apply-templates select="." mode="navigationEntry"/>
	  </xsl:template>

	  <!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template match="aors:Collections" mode="content" priority="-16">
		    <xsl:apply-templates select="aors:Collection" mode="class">
			      <xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
			      <xsl:with-param name="headingElement" select="$section1Heading"/>
			      <xsl:with-param name="class" select="'parameterized'"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:Collections/aors:Collection" mode="classContent" priority="-16">
		    <xsl:apply-templates select="aors:Slot[1]" mode="classSection">
			      <xsl:with-param name="headingElement" select="$section2Heading"/>
			      <xsl:with-param name="content" select="aors:Slot"/>
		    </xsl:apply-templates>
	  </xsl:template>
   <!--copied from DataTypes.xsl-->

	
	

	

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:DataTypes" mode="heading" priority="-17">
		    <xsl:text>Datatypes</xsl:text>
	  </xsl:template>

	  <xsl:template match="aors:DataTypes/aors:*" mode="classHeading" priority="-17">
		    <span class="stereotype small">
			      <xsl:value-of select="concat('«',local-name(),'»')"/>
		    </span>
		    <span class="name">
			      <xsl:value-of select="concat(' ',@name,' ')"/>
		    </span>
		    <xsl:if test="@superType">
			      <span class="supertype small">
				        <xsl:text>(extends </xsl:text>
				        <xsl:call-template name="createOptionalLink">
					          <xsl:with-param name="node" select="key('DataTypes',@superType)"/>
					          <xsl:with-param name="text" select="@superType"/>
				        </xsl:call-template>
				        <xsl:text>)</xsl:text>
			      </span>
		    </xsl:if>
	  </xsl:template>

	  <xsl:template match="aors:EnumerationLiteral" mode="classSectionHeading" priority="-17">
		    <xsl:text>Literals</xsl:text>
	  </xsl:template>
	
	  <xsl:template match="aors:ClassDef" mode="classSectionHeading" priority="-17">
		    <xsl:text>Class-Definition</xsl:text>
	  </xsl:template>

	  <!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:DataTypes" mode="navigation" priority="-17">
		    <xsl:apply-templates select="." mode="navigationEntry">
			      <xsl:with-param name="subEntries">
				        <xsl:apply-templates select="aors:Enumeration|aors:ComplexDataType" mode="navigation">
					          <xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
				        </xsl:apply-templates>
			      </xsl:with-param>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:Enumeration|aors:ComplexDataType" mode="navigation" priority="-17">
		    <xsl:apply-templates select="." mode="navigationEntry"/>
	  </xsl:template>

	  <xsl:template match="aors:Enumeration|aors:ComplexDataType" mode="navigationEntryTitle"
                 priority="-17">
		    <xsl:value-of select="@name"/>
	  </xsl:template>

	  <!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template match="aors:DataTypes" mode="content" priority="-17">
		    <xsl:apply-templates select="aors:Enumeration|aors:ComplexDataType" mode="class">
			      <xsl:sort select="count(aors:*) + count(@*)" order="descending" data-type="number"/>
			      <xsl:with-param name="headingElement" select="$section1Heading"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <!-- Enumeration -->

	<xsl:template match="aors:Enumeration" mode="classContent" priority="-17">
		    <xsl:apply-templates select="aors:EnumerationLiteral[1]" mode="classSection">
			      <xsl:with-param name="headingElement" select="$section2Heading"/>
			      <xsl:with-param name="class" select="'literals'"/>
			      <xsl:with-param name="content" select="aors:EnumerationLiteral"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:EnumerationLiteral" mode="classSectionContent" priority="-17">
		    <xsl:param name="content"/>
		    <ul>
			      <xsl:apply-templates select="$content" mode="enumerationLiteral"/>
		    </ul>
	  </xsl:template>

	  <xsl:template match="aors:EnumerationLiteral" mode="enumerationLiteral" priority="-17">
		    <li>
			      <xsl:copy-of select="text()"/>
		    </li>
	  </xsl:template>

	  <!-- ComplexDataType -->

	<xsl:template match="aors:ComplexDataType" mode="classContent" priority="-17">
		    <xsl:variable name="properties"
                    select="aors:Attribute|aors:ReferenceProperty|aors:ComplexDataProperty|aors:EnmuerationProperty"/>
		    <xsl:apply-templates select="$properties[1]" mode="classSection">
			      <xsl:with-param name="headingElement" select="$section2Heading"/>
			      <xsl:with-param name="class" select="'properties'"/>
			      <xsl:with-param name="content" select="$properties"/>
		    </xsl:apply-templates>
		    <xsl:variable name="functions" select="aors:DefaultConstructor|aors:Function"/>
		    <xsl:apply-templates select="$functions[1]" mode="classSection">
			      <xsl:with-param name="headingElement" select="$section2Heading"/>
			      <xsl:with-param name="class" select="'functions'"/>
			      <xsl:with-param name="content" select="$functions"/>
		    </xsl:apply-templates>
		    <xsl:apply-templates select="aors:ClassDef[1]" mode="classSection">
			      <xsl:with-param name="headingElement" select="$section2Heading"/>
			      <xsl:with-param name="class" select="'classdefinition'"/>
		    </xsl:apply-templates>
	  </xsl:template>
	
	  <xsl:template match="aors:ClassDef" mode="classSectionContent" priority="-17">
		    <ul>
			      <li>
				        <xsl:call-template name="hideContent">
					          <xsl:with-param name="content">
						            <xsl:call-template name="copyCode">
							              <xsl:with-param name="code" select=".|following-sibling::aors:ClassDef"/>
						            </xsl:call-template>
					          </xsl:with-param>
					          <xsl:with-param name="heading" select="'definition'"/>
					          <xsl:with-param name="headingPrefix" select="concat('Code of ',../@name,'.')"/>
				        </xsl:call-template>
			      </li>
		    </ul>
	  </xsl:template>
   <!--copied from Statistics.xsl-->

	

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:Statistics" mode="heading" priority="-18">
		    <xsl:text>Statistics Variables</xsl:text>
	  </xsl:template>

	  <!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:Statistics" mode="navigation" priority="-18">
		    <xsl:apply-templates select="." mode="navigationEntry"/>
	  </xsl:template>

	  <!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template match="aors:Statistics" mode="content" priority="-18">
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

	  <xsl:template match="aors:Variable" mode="statistics" priority="-18">
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
  
  <xsl:template match="aors:Source" mode="statistics" priority="-18">
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
  
  <xsl:template match="aors:StatisticsVariable" mode="statisticsSource" priority="-18">
      <xsl:call-template name="createOptionalLink">
         <xsl:with-param name="node" select="key('StatisticVariables',@name)"/>
         <xsl:with-param name="text" select="@name"/>
      </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="aors:GlobalVariable" mode="statisticsSource" priority="-18">
      <xsl:call-template name="createOptionalLink">
         <xsl:with-param name="node" select="key('GlobalVariables',@name)"/>
         <xsl:with-param name="text" select="@name"/>
      </xsl:call-template>    
  </xsl:template>
  
  <xsl:template match="aors:ObjectProperty" mode="statisticsSource" priority="-18">    
      <xsl:call-template name="createOptionalLink">
         <xsl:with-param name="node" select="key('EntityTypes',@objectType)"/>
         <xsl:with-param name="text" select="@objectType"/>
      </xsl:call-template>
      <xsl:if test="@objectIdRef">
         <xsl:value-of select="concat('[',@objectIdRef,']')"/>
      </xsl:if>
      <xsl:value-of select="concat('.', @property)"/>
  </xsl:template>
  
  <xsl:template match="aors:ValueExpr" mode="statisticsSource" priority="-18">
      <xsl:if test="count(preceding-sibling::aors:ValueExpr) = 0">
         <xsl:choose>
            <xsl:when test="count(../aors:ValueExpr) &gt; 1 or string-length(normalize-space(text())) &gt; number($smallExpressionLength)">
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
  
  <xsl:template match="aors:ObjectTypeExtensionSize" mode="statisticsSource" priority="-18">
      <xsl:text>size-of(</xsl:text>
      <xsl:call-template name="createOptionalLink">
         <xsl:with-param name="node" select="key('EntityTypes',@objectType)"/>
         <xsl:with-param name="text" select="@objectType"/>
      </xsl:call-template>
      <xsl:text>)</xsl:text>
  </xsl:template>
  
  <xsl:template match="aors:ResourceUtilization" mode="statisticsSource" priority="-18">
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
   <!--copied from SpaceModel.xsl-->

	

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:SpaceModel" mode="heading" priority="-19">
		    <xsl:text>Space Model</xsl:text>
	  </xsl:template>
	
	  <xsl:template match="aors:SpaceModel" mode="classHeading" priority="-19">
		    <span class="name">Grid-Cell</span>
	  </xsl:template>

	  <!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:SpaceModel" mode="navigation" priority="-19">
		    <xsl:apply-templates select="." mode="navigationEntry"/>
	  </xsl:template>

	  <!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template match="aors:SpaceModel" mode="content" priority="-19">
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
					          <td>
                  <xsl:value-of select="@dimensions"/>
               </td>
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
	
	  <xsl:template match="aors:SpaceModel" mode="classContent" priority="-19">
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
   <!--copied from SimulationParameterDeclaration.xsl-->

	

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:SimulationParameterDeclaration" mode="heading" priority="-20">
		    <xsl:text>Simulation-Parameter Declarations</xsl:text>
	  </xsl:template>

	  <!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:SimulationParameterDeclaration" mode="navigation" priority="-20">
		    <xsl:apply-templates select="." mode="navigationEntry"/>
	  </xsl:template>

	  <!--################-->
	<!--###  content ###-->
	<!--################-->

	<xsl:template match="aors:SimulationParameterDeclaration" mode="content" priority="-20">
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

	  <xsl:template match="aors:SimulationParameterDeclaration"
                 mode="simulationParameterDeclaration"
                 priority="-20">
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
   <!--copied from SimulationModel.xsl-->

	

	<!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template match="aors:SimulationModel" mode="heading" priority="-21">
		    <xsl:text>Model Properties</xsl:text>
	  </xsl:template>

	  <!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:SimulationModel" mode="navigation" priority="-21">
		    <xsl:apply-templates select="." mode="navigationEntry"/>
	  </xsl:template>

	  <!--################-->
	<!--###  content ###-->
	<!--################-->

	<xsl:template match="aors:SimulationModel" mode="content" priority="-21">
		    <dl class="modelProperties">
			      <xsl:if test="@autoKinematics or @autoGravitation or @autoImpulse or @autoCollision">
				        <dt>Physics Simulation:</dt>
				        <xsl:apply-templates select="@autoKinematics|@autoGravitation|@autoImpulse|@autoCollision"
                                 mode="modelProperties"/>
			      </xsl:if>
			      <xsl:if test="@baseURI">
				        <dt>BaseURI:</dt>
				        <xsl:apply-templates select="@baseURI" mode="modelProperties"/>
			      </xsl:if>
		    </dl>
	  </xsl:template>

	  <xsl:template match="@*" mode="modelProperties" priority="-21">
		    <xsl:if test=". = 'true'">
			      <dd>
            <xsl:value-of select="local-name()"/>
         </dd>
		    </xsl:if>
	  </xsl:template>

	  <xsl:template match="@baseURI" mode="modelProperties" priority="-21">
		    <dd>
         <xsl:value-of select="."/>
      </dd>
	  </xsl:template>
   <!--copied from documentation.xsl-->

	

	<!-- #################### -->
	<!-- ### local macros ### -->
	<!-- #################### -->

	<!-- getNameList -->

	<xsl:template xmlns:dc="http://purl.org/dc/elements/1.1/"
                 xmlns:html="http://www.w3.org/1999/xhtml"
                 name="getNameList">
		    <xsl:apply-templates select="preceding-sibling::dc:*" mode="getNameList">
			      <xsl:sort select="position()" order="descending"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template xmlns:dc="http://purl.org/dc/elements/1.1/"
                 xmlns:html="http://www.w3.org/1999/xhtml"
                 match="*"
                 mode="getNameList"
                 priority="-22"/>

	  <xsl:template xmlns:dc="http://purl.org/dc/elements/1.1/"
                 xmlns:html="http://www.w3.org/1999/xhtml"
                 match="dc:*"
                 mode="getNameList"
                 priority="-22">
		    <xsl:value-of select="name()"/>
		    <xsl:text> </xsl:text>
	  </xsl:template>

	  <!--###############-->
	<!--### heading ###-->
	<!--###############-->

	<xsl:template xmlns:dc="http://purl.org/dc/elements/1.1/"
                 xmlns:html="http://www.w3.org/1999/xhtml"
                 match="aors:documentation"
                 mode="heading"
                 priority="-22">
		    <xsl:text>Documentation</xsl:text>
	  </xsl:template>

	  <!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template xmlns:dc="http://purl.org/dc/elements/1.1/"
                 xmlns:html="http://www.w3.org/1999/xhtml"
                 match="aors:documentation"
                 mode="navigation"
                 priority="-22">
		    <xsl:apply-templates select="." mode="navigationEntry"/>
	  </xsl:template>

	  <!-- ################ -->
	<!-- ###  content ### -->
	<!-- ################ -->

	<xsl:template xmlns:dc="http://purl.org/dc/elements/1.1/"
                 xmlns:html="http://www.w3.org/1999/xhtml"
                 match="aors:documentation"
                 mode="content"
                 priority="-22">
		    <xsl:variable name="documentation">
			      <xsl:apply-templates select="aors:*|dc:*" mode="documentation"/>
		    </xsl:variable>
		    <xsl:if test="normalize-space($documentation) != ''">
			      <dl class="documentation">
				        <xsl:copy-of select="$documentation"/>
			      </dl>
		    </xsl:if>
	  </xsl:template>

	  <xsl:template xmlns:dc="http://purl.org/dc/elements/1.1/"
                 xmlns:html="http://www.w3.org/1999/xhtml"
                 match="aors:*"
                 mode="documentation"
                 priority="-22"/>

	  <xsl:template xmlns:dc="http://purl.org/dc/elements/1.1/"
                 xmlns:html="http://www.w3.org/1999/xhtml"
                 match="aors:informationModelDiagram|aors:processModelDiagram"
                 mode="documentation"
                 priority="-22">
		    <dt>
			      <xsl:value-of select="concat(substring-before(local-name(),'Diagram'),':')"/>
		    </dt>
		    <dd>
			      <a href="{normalize-space(text())}">
				        <img alt="{local-name()}" src="{normalize-space(text())}"/>
			      </a>
		    </dd>
	  </xsl:template>

	  <xsl:template xmlns:dc="http://purl.org/dc/elements/1.1/"
                 xmlns:html="http://www.w3.org/1999/xhtml"
                 match="aors:description"
                 mode="documentation"
                 priority="-22">
		    <xsl:variable name="prevNames">
			      <xsl:call-template name="getNameList"/>
		    </xsl:variable>
		    <xsl:if test="substring-before($prevNames,' ') != name()">
			      <dt>
				        <xsl:value-of select="concat(local-name(),':')"/>
			      </dt>
		    </xsl:if>
		    <dd>
			      <xsl:copy-of select="text()|html:*"/>
		    </dd>
	  </xsl:template>

	  <xsl:template xmlns:dc="http://purl.org/dc/elements/1.1/"
                 xmlns:html="http://www.w3.org/1999/xhtml"
                 match="dc:*"
                 mode="documentation"
                 priority="-22">
		    <xsl:variable name="prevNames">
			      <xsl:call-template name="getNameList"/>
		    </xsl:variable>
		    <xsl:if test="substring-before($prevNames,' ') != name()">
			      <dt>
				        <xsl:value-of select="concat(local-name(),':')"/>
			      </dt>
		    </xsl:if>
		    <dd>
			      <xsl:copy-of select="text()"/>
		    </dd>
	  </xsl:template>
   <!--copied from structure.xsl-->

	

	<!--####################-->
	<!--### local macros ###-->
	<!--####################-->

	<!-- createSection -->

	<xsl:template name="createSection">
		    <xsl:param name="headingElement"/>
		    <xsl:param name="heading"/>
		    <xsl:param name="content"/>
		    <xsl:param name="class"/>
		    <div>
			      <xsl:attribute name="id">
				        <xsl:call-template name="getId"/>
			      </xsl:attribute>
			      <xsl:attribute name="class">
				        <xsl:text>section</xsl:text>
				        <xsl:if test="$class">
					          <xsl:value-of select="concat(' ',$class)"/>
				        </xsl:if>
			      </xsl:attribute>
			      <xsl:element name="{$headingElement}">
				        <xsl:attribute name="class">
					          <xsl:text>heading</xsl:text>
				        </xsl:attribute>
				        <xsl:copy-of select="$heading"/>
			      </xsl:element>
			      <div class="content">
				        <xsl:copy-of select="$content"/>
			      </div>
		    </div>
	  </xsl:template>

	  <!--###################-->
	<!--### basic modes ###-->
	<!--###################-->

	<xsl:template match="aors:*" mode="heading" priority="-23"/>
	  <xsl:template match="aors:*" mode="content" priority="-23"/>
	  <xsl:template match="aors:*" mode="navigation" priority="-23"/>

	  <!--##################-->
	<!--### navigation ###-->
	<!--##################-->

	<xsl:template match="aors:*" mode="navigationEntry" priority="-23">
		    <xsl:param name="subEntries" select="''"/>
		    <li>
			      <a>
				        <xsl:attribute name="href">
					          <xsl:call-template name="getId">
						            <xsl:with-param name="prefix" select="'#'"/>
					          </xsl:call-template>
				        </xsl:attribute>
				        <xsl:apply-templates select="." mode="navigationEntryTitle"/>
			      </a>
			      <xsl:if test="$subEntries != ''">
				        <ul>
					          <xsl:copy-of select="$subEntries"/>
				        </ul>
			      </xsl:if>
		    </li>
	  </xsl:template>

	  <xsl:template match="aors:*" mode="navigationEntryTitle" priority="-23">
		    <xsl:apply-templates select="." mode="heading"/>
	  </xsl:template>

	  <!--############-->
	<!--### part ###-->
	<!--############-->

	<xsl:template match="aors:*" mode="part" priority="-23">
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="." mode="partBody">
			      <xsl:with-param name="content" select="$content"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:*" mode="partBody" priority="-23">
		    <xsl:param name="content"/>
		    <div class="part">
			      <xsl:attribute name="id">
				        <xsl:call-template name="getId"/>
			      </xsl:attribute>
			      <xsl:element name="{$partHeading}">
				        <xsl:attribute name="class">
					          <xsl:text>heading</xsl:text>
				        </xsl:attribute>
				        <xsl:apply-templates select="." mode="partHeading"/>
			      </xsl:element>
			      <div class="content">
				        <xsl:apply-templates select="." mode="partContent">
					          <xsl:with-param name="content" select="$content"/>
				        </xsl:apply-templates>
			      </div>
		    </div>
	  </xsl:template>

	  <xsl:template match="aors:*" mode="partHeading" priority="-23">
		    <xsl:apply-templates select="." mode="heading"/>
	  </xsl:template>

	  <xsl:template match="aors:*" mode="partContent" priority="-23">
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="." mode="content">
			      <xsl:with-param name="content" select="$content"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <!--###############-->
	<!--### chapter ###-->
	<!--###############-->

	<xsl:template match="aors:*" mode="chapter" priority="-23">
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="." mode="chapterBody">
			      <xsl:with-param name="content" select="$content"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="aors:*" mode="chapterBody" priority="-23">
		    <xsl:param name="content"/>
		    <div class="chapter">
			      <xsl:attribute name="id">
				        <xsl:call-template name="getId"/>
			      </xsl:attribute>
			      <xsl:element name="{$chapterHeading}">
				        <xsl:attribute name="class">
					          <xsl:text>heading</xsl:text>
				        </xsl:attribute>
				        <xsl:apply-templates select="." mode="chapterHeading"/>
			      </xsl:element>
			      <div class="content">
				        <xsl:apply-templates select="." mode="chapterContent">
					          <xsl:with-param name="content" select="$content"/>
				        </xsl:apply-templates>
			      </div>
		    </div>
	  </xsl:template>

	  <xsl:template match="aors:*" mode="chapterHeading" priority="-23">
		    <xsl:apply-templates select="." mode="heading"/>
	  </xsl:template>

	  <xsl:template match="aors:*" mode="chapterContent" priority="-23">
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="." mode="content">
			      <xsl:with-param name="content" select="$content"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <!--###############-->
	<!--### section ###-->
	<!--###############-->

	<!-- section1 -->

	<xsl:template match="@*|aors:*" mode="section1" priority="-23">
		    <xsl:param name="class"/>
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="." mode="section1Body">
			      <xsl:with-param name="class" select="$class"/>
			      <xsl:with-param name="content" select="$content"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="@*|aors:*" mode="section1Body" priority="-23">
		    <xsl:param name="class"/>
		    <xsl:param name="content"/>
		    <xsl:call-template name="createSection">
			      <xsl:with-param name="headingElement" select="$section1Heading"/>
			      <xsl:with-param name="heading">
				        <xsl:apply-templates select="." mode="section1Heading"/>
			      </xsl:with-param>
			      <xsl:with-param name="content">
				        <xsl:apply-templates select="." mode="section1Content">
					          <xsl:with-param name="content" select="$content"/>
				        </xsl:apply-templates>
			      </xsl:with-param>
			      <xsl:with-param name="class" select="$class"/>
		    </xsl:call-template>
	  </xsl:template>

	  <xsl:template match="@*|aors:*" mode="section1Heading" priority="-23">
		    <xsl:apply-templates select="." mode="heading"/>
	  </xsl:template>

	  <xsl:template match="@*|aors:*" mode="section1Content" priority="-23">
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="." mode="content">
			      <xsl:with-param name="content" select="$content"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <!-- section2 -->

	<xsl:template match="@*|aors:*" mode="section2" priority="-23">
		    <xsl:param name="class"/>
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="." mode="section2Body">
			      <xsl:with-param name="class" select="$class"/>
			      <xsl:with-param name="content" select="$content"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="@*|aors:*" mode="section2Body" priority="-23">
		    <xsl:param name="class"/>
		    <xsl:param name="content"/>
		    <xsl:call-template name="createSection">
			      <xsl:with-param name="headingElement" select="$section2Heading"/>
			      <xsl:with-param name="heading">
				        <xsl:apply-templates select="." mode="section2Heading"/>
			      </xsl:with-param>
			      <xsl:with-param name="content">
				        <xsl:apply-templates select="." mode="section2Content">
					          <xsl:with-param name="content" select="$content"/>
				        </xsl:apply-templates>
			      </xsl:with-param>
			      <xsl:with-param name="class" select="$class"/>
		    </xsl:call-template>
	  </xsl:template>

	  <xsl:template match="@*|aors:*" mode="section2Heading" priority="-23">
		    <xsl:apply-templates select="." mode="heading"/>
	  </xsl:template>

	  <xsl:template match="@*|aors:*" mode="section2Content" priority="-23">
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="." mode="content">
			      <xsl:with-param name="content" select="$content"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <!-- section3 -->

	<xsl:template match="@*|aors:*" mode="section3" priority="-23">
		    <xsl:param name="class"/>
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="." mode="section3Body">
			      <xsl:with-param name="class" select="$class"/>
			      <xsl:with-param name="content" select="$content"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="@*|aors:*" mode="section3Body" priority="-23">
		    <xsl:param name="class"/>
		    <xsl:param name="content"/>
		    <xsl:call-template name="createSection">
			      <xsl:with-param name="headingElement" select="$section3Heading"/>
			      <xsl:with-param name="heading">
				        <xsl:apply-templates select="." mode="section3Heading"/>
			      </xsl:with-param>
			      <xsl:with-param name="content">
				        <xsl:apply-templates select="." mode="section3Content">
					          <xsl:with-param name="content" select="$content"/>
				        </xsl:apply-templates>
			      </xsl:with-param>
			      <xsl:with-param name="class" select="$class"/>
		    </xsl:call-template>
	  </xsl:template>

	  <xsl:template match="@*|aors:*" mode="section3Heading" priority="-23">
		    <xsl:apply-templates select="." mode="heading"/>
	  </xsl:template>

	  <xsl:template match="@*|aors:*" mode="section3Content" priority="-23">
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="." mode="content">
			      <xsl:with-param name="content" select="$content"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <!-- section4 -->

	<xsl:template match="@*|aors:*" mode="section4" priority="-23">
		    <xsl:param name="class"/>
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="." mode="section4Body">
			      <xsl:with-param name="class" select="$class"/>
			      <xsl:with-param name="content" select="$content"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="@*|aors:*" mode="section4Body" priority="-23">
		    <xsl:param name="class"/>
		    <xsl:param name="content"/>
		    <xsl:call-template name="createSection">
			      <xsl:with-param name="headingElement" select="$section4Heading"/>
			      <xsl:with-param name="heading">
				        <xsl:apply-templates select="." mode="section4Heading"/>
			      </xsl:with-param>
			      <xsl:with-param name="content">
				        <xsl:apply-templates select="." mode="section4Content">
					          <xsl:with-param name="content" select="$content"/>
				        </xsl:apply-templates>
			      </xsl:with-param>
			      <xsl:with-param name="class" select="$class"/>
		    </xsl:call-template>
	  </xsl:template>

	  <xsl:template match="@*|aors:*" mode="section4Heading" priority="-23">
		    <xsl:apply-templates select="." mode="heading"/>
	  </xsl:template>

	  <xsl:template match="@*|aors:*" mode="section4Content" priority="-23">
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="." mode="content">
			      <xsl:with-param name="content" select="$content"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <!-- section5 -->

	<xsl:template match="@*|aors:*" mode="section5" priority="-23">
		    <xsl:param name="class"/>
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="." mode="section5Body">
			      <xsl:with-param name="class" select="$class"/>
			      <xsl:with-param name="content" select="$content"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="@*|aors:*" mode="section5Body" priority="-23">
		    <xsl:param name="class"/>
		    <xsl:param name="content"/>
		    <xsl:call-template name="createSection">
			      <xsl:with-param name="headingElement" select="$section5Heading"/>
			      <xsl:with-param name="heading">
				        <xsl:apply-templates select="." mode="section5Heading"/>
			      </xsl:with-param>
			      <xsl:with-param name="content">
				        <xsl:apply-templates select="." mode="section5Content">
					          <xsl:with-param name="content" select="$content"/>
				        </xsl:apply-templates>
			      </xsl:with-param>
			      <xsl:with-param name="class" select="$class"/>
		    </xsl:call-template>
	  </xsl:template>

	  <xsl:template match="@*|aors:*" mode="section5Heading" priority="-23">
		    <xsl:apply-templates select="." mode="heading"/>
	  </xsl:template>

	  <xsl:template match="@*|aors:*" mode="section5Content" priority="-23">
		    <xsl:param name="content"/>
		    <xsl:apply-templates select="." mode="content">
			      <xsl:with-param name="content" select="$content"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <!--#############-->
	<!--### Class ###-->
	<!--#############-->

	<xsl:template match="@*|aors:*" mode="class" priority="-23">
		    <xsl:param name="headingElement"/>
		    <xsl:param name="content"/>
		    <xsl:param name="class"/>
		    <xsl:param name="kind" select="'class'"/>
		    <xsl:apply-templates select="." mode="classBody">
			      <xsl:with-param name="headingElement" select="$headingElement"/>
			      <xsl:with-param name="content" select="$content"/>
			      <xsl:with-param name="class" select="$class"/>
			      <xsl:with-param name="kind" select="$kind"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="@*|aors:*" mode="classBody" priority="-23">
		    <xsl:param name="headingElement"/>
		    <xsl:param name="content"/>
		    <xsl:param name="class"/>
		    <xsl:param name="kind" select="'class'"/>
		    <xsl:call-template name="createSection">
			      <xsl:with-param name="headingElement" select="$headingElement"/>
			      <xsl:with-param name="heading">
				        <xsl:apply-templates select="." mode="classHeading"/>
			      </xsl:with-param>
			      <xsl:with-param name="content">
				        <xsl:apply-templates select="." mode="classContent">
					          <xsl:with-param name="content" select="$content"/>
				        </xsl:apply-templates>
			      </xsl:with-param>
			      <xsl:with-param name="class">
				        <xsl:if test="$class">
					          <xsl:value-of select="concat($class,' ')"/>
				        </xsl:if>
				        <xsl:value-of select="$kind"/>
			      </xsl:with-param>
		    </xsl:call-template>
	  </xsl:template>

	  <xsl:template match="@*|aors:*" mode="classHeading" priority="-23"/>
	  <xsl:template match="@*|aors:*" mode="classContent" priority="-23"/>

	  <xsl:template match="@*|aors:*" mode="classSection" priority="-23">
		    <xsl:param name="headingElement"/>
		    <xsl:param name="content"/>
		    <xsl:param name="class"/>
		    <xsl:apply-templates select="." mode="classSectionBody">
			      <xsl:with-param name="headingElement" select="$headingElement"/>
			      <xsl:with-param name="content" select="$content"/>
			      <xsl:with-param name="class" select="$class"/>
		    </xsl:apply-templates>
	  </xsl:template>

	  <xsl:template match="@*|aors:*" mode="classSectionBody" priority="-23">
		    <xsl:param name="headingElement"/>
		    <xsl:param name="content"/>
		    <xsl:param name="class"/>
		    <xsl:call-template name="createSection">
			      <xsl:with-param name="headingElement" select="$headingElement"/>
			      <xsl:with-param name="heading">
				        <xsl:apply-templates select="." mode="classSectionHeading"/>
			      </xsl:with-param>
			      <xsl:with-param name="content">
				        <xsl:apply-templates select="." mode="classSectionContent">
					          <xsl:with-param name="content" select="$content"/>
				        </xsl:apply-templates>
			      </xsl:with-param>
			      <xsl:with-param name="class" select="$class"/>
		    </xsl:call-template>
	  </xsl:template>

	  <xsl:template match="@*|aors:*" mode="classSectionHeading" priority="-23"/>
	  <xsl:template match="@*|aors:*" mode="classSectionContent" priority="-23"/>
</xsl:stylesheet>