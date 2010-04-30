<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:log="http://aor-simulation.org/log"
    xmlns:ers="http://aor-simulation.org"
    xmlns:aors="http://aor-simulation.org"
    xmlns:html="http://www.w3.org/1999/xhtml"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:x1f="http://www.informatik.tu-cottbus.de/~tgrundm1/projects/xslt-framework/xslt-1.0/"
    version="1.0"
    exclude-result-prefixes="xsl log ers aors html x1f">
   
    <xsl:import href="XSLT-1.0-Framework/createXHTMLDocument.xsl"/>
   
    <xsl:output method="xml" doctype-public="-//W3C//DTD XHTML 1.1//EN"
    doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd" encoding="UTF-8" indent="yes"/>
   
    <xsl:template name="getTitle">
        <xsl:text>Log: </xsl:text>
        <xsl:choose>
            <xsl:when test="/log:SimulationLog/log:SimulationScenario/@scenarioTitle">
                <xsl:value-of select="concat(/log:SimulationLog/log:SimulationScenario/@scenarioTitle,' (',/log:SimulationLog/log:SimulationScenario/@scenarioName,')')"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="/log:SimulationLog/log:SimulationScenario/@scenarioName"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
   
    <xsl:template match="/">
        <html>
            <head>
                <title>
                    <xsl:call-template name="getTitle"/>
                </title>
                <meta http-equiv="Content-Type" content="application/xml+xhtml; charset=UTF-8"/>
            	<style type="text/css">
            		<xsl:text disable-output-escaping="yes">
            			table {
            				border-collapse:collapse;
            			}
            			td, th {
            				border: 1px solid black;
            			}s
            			dt:after {
            				content: ": ";
            			}
            		</xsl:text>
            	</style>
            </head>
            <body>
                <xsl:apply-templates select="log:SimulationLog"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="log:SimulationLog">
        <xsl:call-template name="x1f:createDocument">
            <xsl:with-param name="heading">
                <xsl:call-template name="getTitle"/>
            </xsl:with-param>
            <!--xsl:with-param name="preface">
                <xsl:apply-templates select="." mode="annotations"/>
                </xsl:with-param-->
            <!--xsl:with-param name="navigation">
                <xsl:apply-templates select="." mode="navigation">
                <xsl:with-param name="chapterList" select="$chapterList"/>
                </xsl:apply-templates>
                </xsl:with-param-->
            <xsl:with-param name="body">
            	<!--xsl:apply-templates select="log:SimulationScenario" mode="log"/-->
            	<!--xsl:apply-templates select="log:SpaceModel" mode="log"/-->
            	<xsl:apply-templates select="." mode="getSimulationStep"/>
            </xsl:with-param>
        </xsl:call-template>        
    </xsl:template>
    
    <!--#########################-->
    <!--### getSimulationStep ###-->
	<!--#########################-->
    
    <xsl:template match="log:SimulationLog" mode="getSimulationStep">
    	<table>
    		<thead>
    			<tr>
    				<th>steptime</th>
    				<th>steptype</th>
    				<th>triggering event / physics simulation</th>
    				<th>resulting state changes</th>
    				<th>resulting events</th>
    				<!--th>activities</th-->
    			</tr>
    		</thead>
   			<xsl:apply-templates select="log:SimulationStep" mode="getSimulationStep">
   				<xsl:sort select="@stepTime" data-type="number"/>
   			</xsl:apply-templates>
		</table>
    </xsl:template>
	
	<xsl:template match="log:SimulationStep" mode="getSimulationStep">
		<tbody>
			<xsl:attribute name="id">
				<xsl:call-template name="x1f:createID"/>
			</xsl:attribute>
			<xsl:variable name="envEvents" select="log:EnvironmentSimulatorStep/log:ExogenousEvent | log:EnvironmentSimulatorStep/log:ActionEvent | log:EnvironmentSimulatorStep/log:OutMessageEvent | log:EnvironmentSimulatorStep/log:CausedEvent | log:EnvironmentSimulatorStep/log:ActivityStartEvent | log:EnvironmentSimulatorStep/log:ActivityEndEvent"/>
			<xsl:variable name="agtEvents" select="log:AgentSimulatorStep/log:PerceptionEvent | log:AgentSimulatorStep/log:PhysicalObjectPerceptionEvent | log:AgentSimulatorStep/log:ActualPerceptionEvent | log:AgentSimulatorStep/log:InMessageEvent | log:AgentSimulatorStep/log:ActualInMessageEvent | log:AgentSimulatorStep/log:PeriodicTimeEvent"/>
			<xsl:variable name="substeps" select="log:EnvironmentSimulatorStep[log:ExogenousEvent | log:ActionEvent | log:OutMessageEvent | log:CausedEvent | log:ActivityStartEvent | log:ActivityEndEvent] | log:AgentSimulatorStep[log:PerceptionEvent | log:PhysicalObjectPerceptionEvent | log:ActualPerceptionEvent | log:InMessageEvent | log:ActualInMessageEvent | log:PeriodicTimeEvent]"/>
			<xsl:apply-templates select="$substeps[1]" mode="getSimulationStep">
				<xsl:with-param name="stepTime">
					<td rowspan="{count($envEvents) + count($agtEvents)}">
						<xsl:value-of select="@stepTime"/>
					</td>
				</xsl:with-param>
			</xsl:apply-templates>
			<xsl:apply-templates select="$substeps[position()>1]" mode="getSimulationStep"/>
			<!--xsl:apply-templates select="log:AgentSimResultingStateChanges" mode="getSimulationStep"/-->
		</tbody>
	</xsl:template>
	
	<xsl:template match="log:EnvironmentSimulatorStep" mode="getSimulationStep">
		<xsl:param name="stepTime"/>
		<xsl:variable name="events" select="log:PhysicsSimulation | log:ExogenousEvent | log:ActionEvent | log:OutMessageEvent | log:CausedEvent | log:ActivityStartEvent | log:ActivityEndEvent"/>
		<xsl:variable name="first" select="$events[1]"/>
		<xsl:apply-templates select="$first" mode="getSimulationStep">
			<xsl:with-param name="stepTime" select="$stepTime"/>
			<xsl:with-param name="stepType">
				<td rowspan="{count($events)}">
					<xsl:value-of select="local-name()"/>
				</td>
			</xsl:with-param>
		</xsl:apply-templates>
		<xsl:apply-templates select="$events[position() > 1]" mode="getSimulationStep"/>
	</xsl:template>
	
	<xsl:template match="log:AgentSimulatorStep" mode="getSimulationStep">
		<xsl:param name="stepTime"/>
		<xsl:variable name="events" select="log:PerceptionEvent | log:PhysicalObjectPerceptionEvent | log:ActualPerceptionEvent | log:InMessageEvent | log:ActualInMessageEvent | log:PeriodicTimeEvent"/>
		<xsl:variable name="first" select="$events[1]"/>
		<xsl:apply-templates select="$first" mode="getSimulationStep">
			<xsl:with-param name="stepTime" select="$stepTime"/>
			<xsl:with-param name="stepType">
				<td rowspan="{count($events)}">
					<dl>
						<dt>
							<xsl:value-of select="local-name()"/>
						</dt>
						<dd>
							<xsl:if test="@agentName">
								<xsl:value-of select="concat(@agentName,' ')"/>
							</xsl:if>
							<xsl:value-of select="concat('[',@agent,'] : ',@agentType)"/>
						</dd>
					</dl>
				</td>
			</xsl:with-param>
		</xsl:apply-templates>
		<xsl:apply-templates select="$events[position() > 1]" mode="getSimulationStep"/>
	</xsl:template>
	
	<xsl:template match="log:PhysicsSimulation | log:ExogenousEvent | log:ActionEvent | log:OutMessageEvent | log:CausedEvent | log:ActivityStartEvent | log:ActivityEndEvent | log:PerceptionEvent | log:PhysicalObjectPerceptionEvent | log:ActualPerceptionEvent | log:InMessageEvent | log:ActualInMessageEvent | log:PeriodicTimeEvent" mode="getSimulationStep">
		<xsl:param name="stepTime"/>
		<xsl:param name="stepType"/>
		<tr>
			<xsl:copy-of select="$stepTime"/>
			<xsl:copy-of select="$stepType"/>
			<td>
				<xsl:apply-templates select="." mode="getTriggeringEvent"/>
			</td>
			<td>
				<xsl:call-template name="x1f:getOptionalValue">
					<xsl:with-param name="node">
						<xsl:apply-templates select="log:ResultingStateChanges" mode="getResultingStateChanges"/>
					</xsl:with-param>
					<xsl:with-param name="copy" select="true()"/>
				</xsl:call-template>
			</td>
			<td>
				<xsl:call-template name="x1f:getOptionalValue">
					<xsl:with-param name="node">
						<xsl:apply-templates select="log:ResultingEvents" mode="getResultingEvents"/>
					</xsl:with-param>
					<xsl:with-param name="copy" select="true()"/>
				</xsl:call-template>
			</td>
		</tr>
	</xsl:template>
	
	<!--##########################-->
	<!--### getTriggeringEvent ###-->
	<!--##########################-->
	
	<xsl:template match="log:PhysicsSimulation" mode="getTriggeringEvent">
		<dl>
			<dt>
				<xsl:value-of select="local-name()"/>
			</dt>
			<xsl:apply-templates select="@*[local-name()!='type' and local-name()!='name'] | log:Slot | log:Message" mode="getProperties"/>
		</dl>
	</xsl:template>

	<xsl:template match="log:ExogenousEvent | log:ActionEvent | log:OutMessageEvent | log:CausedEvent | log:ActivityStartEvent | log:ActivityEndEvent | log:PerceptionEvent | log:PhysicalObjectPerceptionEvent | log:ActualPerceptionEvent | log:InMessageEvent | log:ActualInMessageEvent | log:PeriodicTimeEvent" mode="getTriggeringEvent">
		<dl>
			<dt>
				<xsl:value-of select="local-name()"/>
				<xsl:if test="@name">
					<xsl:value-of select="concat(' ',@name)"/>
				</xsl:if>
				<xsl:if test="@type">
					<xsl:value-of select="concat(' : ',@type)"/>
				</xsl:if>
			</dt>
			<xsl:apply-templates select="@*[local-name()!='type' and local-name()!='name'] | log:Slot | log:Message" mode="getProperties"/>
		</dl>
	</xsl:template>
		
	<!--################################-->
	<!--### getResultingStateChanges ###-->
	<!--################################-->
	
	<xsl:template match="log:*" mode="getResultingStateChanges"/>
	
	<xsl:template match="log:ResultingStateChanges" mode="getResultingStateChanges">
		<xsl:variable name="stateChanges">
			<xsl:apply-templates select="log:*" mode="getResultingStateChanges"/>
		</xsl:variable>
		<xsl:if test="$stateChanges != ''">
			<dl>
				<xsl:copy-of select="$stateChanges"/>
			</dl>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="log:Create" mode="getResultingStateChanges">
		<xsl:apply-templates select="log:*" mode="getResultingStateChanges">
			<xsl:with-param name="prefix" select="'create '"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="log:DestroyObjects" mode="getResultingStateChanges">
		<xsl:apply-templates select="log:*" mode="getResultingStateChanges">
			<xsl:with-param name="prefix" select="'destroy '"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="log:Collections" mode="getResultingStateChanges">
		<xsl:apply-templates select="log:*" mode="getResultingStateChanges"/>
	</xsl:template>
	
	<xsl:template match="log:Objects | log:Agents | log:PhysicalObjects | log:PhysicalAgents | log:GridCells" mode="getResultingStateChanges">
		<xsl:param name="prefix"/>
		<xsl:apply-templates select="log:*" mode="getResultingStateChanges">
			<xsl:with-param name="prefix" select="$prefix"/>
		</xsl:apply-templates>
	</xsl:template>

	<xsl:template match="log:Coll" mode="getResultingStateChanges">
		<dt>
			<xsl:value-of select="'Collection '"/>
			<xsl:if test="@name">
				<xsl:value-of select="concat(' ',@name)"/>
			</xsl:if>
			<xsl:if test="@id">
				<xsl:value-of select="concat(' [',@id,']')"/>
			</xsl:if>
			<xsl:if test="@type">
				<xsl:value-of select="concat(' : ',@type)"/>
			</xsl:if>
			<xsl:if test="@itemType">
				<xsl:value-of select="concat('&lt;',@itemType,'&gt;')"/>
			</xsl:if>
		</dt>
		<xsl:apply-templates select="@*[local-name()!='name' and local-name()!='id' and local-name()!='type' and local-name()!='itemType']" mode="getResultingStateChanges"/>
	</xsl:template>

	<xsl:template match="log:Obj | log:Agt | log:PhysObj | log:PhysAgt" mode="getResultingStateChanges">
		<xsl:param name="prefix"/>
		<dt>
			<xsl:value-of select="concat($prefix,local-name())"/>
			<xsl:if test="@name">
				<xsl:value-of select="concat(' ',@name)"/>
			</xsl:if>
			<xsl:if test="@id">
				<xsl:value-of select="concat(' [',@id,']')"/>
			</xsl:if>
			<xsl:if test="@type">
				<xsl:value-of select="concat(' : ',@type)"/>
			</xsl:if>
		</dt>
		<xsl:apply-templates select="@*[local-name()!='name' and local-name()!='id' and local-name()!='type'] | log:Slot | log:SelfBeliefSlot" mode="getResultingStateChanges"/>
	</xsl:template>
	
	<xsl:template match="@action" mode="getResultingStateChanges">
		<dd>
			<xsl:value-of select="concat(.,' Obj')"/>
			<xsl:if test="../log:Obj/@name">
				<xsl:value-of select="concat(' ',../log:Obj/@name)"/>
			</xsl:if>
			<xsl:if test="../log:Obj/@id">
				<xsl:value-of select="concat(' [',../log:Obj/@id,']')"/>
			</xsl:if>
			<xsl:if test="../log:Obj/@type">
				<xsl:value-of select="concat(' : ',../log:Obj/@type)"/>
			</xsl:if>
		</dd>
	</xsl:template>

	<xsl:template match="log:GridCell" mode="getResultingStateChanges">
		<dt>
			<xsl:value-of select="concat(local-name(),'[',@x,',',@y,']')"/>
		</dt>
		<dd>
			<xsl:apply-templates select="log:Slot" mode="getResultingStateChanges"/>
		</dd>
	</xsl:template>

	<xsl:template match="log:DestroyObj" mode="getResultingStateChanges">
		<xsl:param name="prefix"/>
		<dt>
			<xsl:value-of select="concat($prefix,local-name())"/>
			<xsl:if test="@objectName">
				<xsl:value-of select="concat(' ',@objectName)"/>
			</xsl:if>
			<xsl:value-of select="concat(' [',@id,']')"/>
			<xsl:if test="@objectType">
				<xsl:value-of select="concat(' : ',@type)"/>
			</xsl:if>
		</dt>
	</xsl:template>

	<xsl:template match="log:Slot" mode="getResultingStateChanges">
		<dd>
			<xsl:value-of select="@property"/>
			<xsl:if test="@value">
				<xsl:value-of select="concat(' = ',@value)"/>
			</xsl:if>
			<xsl:if test="@refId">
				<xsl:value-of select="concat(' = [',@refId,']')"/>
			</xsl:if>
		</dd>
	</xsl:template>

	<xsl:template match="log:SelfBeliefSlot" mode="getResultingStateChanges">
		<dd>
			<xsl:value-of select="concat(@selfBeliefProperty,' = ',@value)"/>
		</dd>
	</xsl:template>

	<xsl:template match="@*" mode="getResultingStateChanges">
		<dd>
			<xsl:value-of select="concat(local-name(),' = ',.)"/>
		</dd>
	</xsl:template>
	
	<!--#######################-->
	<!--### getResultEvents ###-->
	<!--#######################-->
	
	<xsl:template match="log:ResultingEvents" mode="getResultingEvents">
		<xsl:variable name="events">
			<xsl:apply-templates select="log:*" mode="getResultingEvents"/>
		</xsl:variable>
		<xsl:if test="$events != ''">
			<dl>
				<xsl:copy-of select="$events"/>
			</dl>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="log:CausedEvent | log:PerceptionEvent | log:InMessageEvent | log:ActivityStartEvent | log:ActivityEndEvent | log:ActionEvent | log:OutMessageEvent" mode="getResultingEvents">
		<dt>
			<xsl:value-of select="local-name()"/>
			<xsl:if test="@name">
				<xsl:value-of select="concat(' ',@name)"/>
			</xsl:if>
			<xsl:if test="@id">
				<xsl:value-of select="concat(' [',@id,']')"/>
			</xsl:if>
			<xsl:if test="@type">
				<xsl:value-of select="concat(' : ',@type)"/>
			</xsl:if>
		</dt>
		<xsl:apply-templates select="@*[local-name()!='name' and local-name()!='id' and local-name()!='type'] | log:Slot | log:Message" mode="getProperties"/>
	</xsl:template>
		
	<!--#####################-->
	<!--### getProperties ###-->
	<!--#####################-->
	
	<xsl:template match="@*" mode="getProperties">
		<dd>
			<xsl:value-of select="concat(local-name(),' = ',.)"/>
		</dd>
	</xsl:template>
	
	<xsl:template match="@nextOccurrenceTime | @occurrenceTime" mode="getProperties">
		<dd>
			<xsl:value-of select="concat(local-name(),' = ')"/>
			<xsl:call-template name="x1f:createOptionalLink">
				<xsl:with-param name="node" select="//log:SimulationStep[number(@stepTime) = number(current())]"/>
				<xsl:with-param name="text" select="."/>
			</xsl:call-template>
		</dd>
	</xsl:template>
	
	<xsl:template match="log:Slot" mode="getProperties">
		<dd>
			<xsl:value-of select="@property"/>
			<xsl:if test="@value">
				<xsl:value-of select="concat(' = ',@value)"/>
			</xsl:if>
			<xsl:if test="@refId">
				<xsl:value-of select="concat(' = [',@refId,']')"/>
			</xsl:if>
		</dd>
	</xsl:template>
	
	<xsl:template match="log:Message" mode="getProperties">
		<dd>
			<dl>
				<dt>
					<xsl:value-of select="local-name()"/>
					<xsl:if test="@name">
						<xsl:value-of select="concat(' ',@name)"/>
					</xsl:if>
					<xsl:if test="@id">
						<xsl:value-of select="concat(' [',@id,']')"/>
					</xsl:if>
					<xsl:if test="@type">
						<xsl:value-of select="concat(' : ',@type)"/>
					</xsl:if>
				</dt>
				<xsl:apply-templates select="log:Slot" mode="getProperties"/>
			</dl>
		</dd>
	</xsl:template>
	
		
<!--    
    <xsl:template match="log:SimulationScenario" mode="log">
        <xsl:call-template name="x1f:createChapter">
            <xsl:with-param name="heading" select="local-name()"/>
            <xsl:with-param name="body">
                <xsl:apply-templates select="log:SimulationParameters" mode="log"/>
                <xsl:apply-templates select="log:SimulationModel" mode="log"/>
                <xsl:apply-templates select="log:InitialState" mode="log"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="log:SimulationParameters" mode="log">
        <xsl:call-template name="x1f:createSection">
            <xsl:with-param name="heading" select="local-name()"/>
            <xsl:with-param name="headingElement" select="$x1f:section1Heading"/>
            <xsl:with-param name="body">
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
                               <xsl:call-template name="x1f:getOptionalValue">
                                   <xsl:with-param name="node" select="@simulationSteps"/>
                               </xsl:call-template>
                           </td>
                           <td>
                               <xsl:call-template name="x1f:getOptionalValue">
                                   <xsl:with-param name="node" select="@stepDuration"/>
                                   <xsl:with-param name="defaultValue" select="'1'"/>
                               </xsl:call-template>
                           </td>
                           <td>
                               <xsl:call-template name="x1f:getOptionalValue">
                                   <xsl:with-param name="node" select="@timeUnit"/>
                                   <xsl:with-param name="defaultValue" select="'s'"/>
                               </xsl:call-template>
                           </td>
                           <td>
                               <xsl:call-template name="x1f:getOptionalValue">
                                   <xsl:with-param name="node" select="@stepTimeDelay"/>
                               </xsl:call-template>
                           </td>
                           <td>
                               <xsl:call-template name="x1f:getOptionalValue">
                                   <xsl:with-param name="node" select="@randomSeed"/>
                               </xsl:call-template>
                           </td>
                       </tr>
                   </tbody>
               </table>
               <xsl:if test="log:Slot">
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
                           <xsl:apply-templates select="log:Slot" mode="simulationParamters"/>
                       </tbody>
                   </table>
               </xsl:if>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="log:Slot" mode="simulationParameters">
        <tr>
            <td><xsl:value-of select="@name"/></td>
            <td><xsl:value-of select="@value"/></td>
        </tr>
    </xsl:template>
    
    <xsl:template match="log:SimulationModel" mode="log">
        <xsl:call-template name="x1f:createSection">
            <xsl:with-param name="heading" select="local-name()"/>
            <xsl:with-param name="headingElement" select="$x1f:section1Heading"/>
            <xsl:with-param name="body">
                <xsl:variable name="properties1">
                    <xsl:apply-templates select="@modelName|@modelTitle|@baseURI" mode="simulationModel"/>
                </xsl:variable>
                <xsl:if test="properties1 != ''">
                    <dl>
                        <xsl:copy-of select="$properties1"/>
                    </dl>
                </xsl:if>
                <table>
                    <colgroup>
                        <col width="25%"/>
                    </colgroup>
                    <colgroup>
                        <col width="25%"/>
                    </colgroup>
                    <colgroup>
                        <col width="25%"/>
                    </colgroup>
                    <colgroup>
                        <col width="25%"/>
                    </colgroup>
                    <colgroup>
                        <col width="25%"/>
                    </colgroup>
                    <thead>
                        <tr>
                            <th scope="col">autoKinematics</th>
                            <th scope="col">autoGravitation</th>
                            <th scope="col">autoImpulse</th>
                            <th scope="col">autoCollision</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>
                                <xsl:call-template name="x1f:getBooleanValue">
                                    <xsl:with-param name="node" select="@autoKinematics"/>
                                    <xsl:with-param name="showNegative" select="true()"/>
                                </xsl:call-template>
                            </td>
                            <td>
                                <xsl:call-template name="x1f:getBooleanValue">
                                    <xsl:with-param name="node" select="@autoGravitation"/>
                                    <xsl:with-param name="showNegative" select="true()"/>
                                </xsl:call-template>
                            </td>
                            <td>
                                <xsl:call-template name="x1f:getBooleanValue">
                                    <xsl:with-param name="node" select="@autoImpulse"/>
                                    <xsl:with-param name="showNegative" select="true()"/>
                                </xsl:call-template>
                            </td>
                            <td>
                                <xsl:call-template name="x1f:getBooleanValue">
                                    <xsl:with-param name="node" select="@autoCollision"/>
                                    <xsl:with-param name="showNegative" select="true()"/>
                                </xsl:call-template>
                            </td>
                        </tr>
                    </tbody>
                </table>        
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="@modelName|@modelTitle|@baseURI" mode="simulationModel">
        <dt>
            <xsl:value-of select="local-name()"/>
        </dt>
        <dd>
            <xsl:value-of select="."/>
        </dd>
    </xsl:template>    
    
    <xsl:template match="log:SpaceModel" mode="log">
        <xsl:call-template name="x1f:createChapter">
            <xsl:with-param name="heading" select="local-name()"/>
            <xsl:with-param name="body">
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
                                <xsl:call-template name="x1f:getOptionalValue">
                                    <xsl:with-param name="node" select="@geometry"/>
                                    <xsl:with-param name="defaultValue" select="'Euclidean'"/>
                                </xsl:call-template>
                            </td>
                            <td>
                                <xsl:call-template name="x1f:getOptionalValue">
                                    <xsl:with-param name="node" select="@spatialDistanceUnit"/>
                                    <xsl:with-param name="defaultValue" select="'m'"/>
                                </xsl:call-template>
                            </td>
                            <td>
                                <xsl:call-template name="x1f:getOptionalValue">
                                    <xsl:with-param name="node" select="@xMax"/>
                                    <xsl:with-param name="defaultValue" select="'0'"/>
                                </xsl:call-template>
                            </td>
                            <td>
                                <xsl:call-template name="x1f:getOptionalValue">
                                    <xsl:with-param name="node" select="@yMax"/>
                                    <xsl:with-param name="defaultValue" select="'0'"/>
                                </xsl:call-template>
                            </td>
                            <td>
                                <xsl:call-template name="x1f:getOptionalValue">
                                    <xsl:with-param name="node" select="@zMax"/>
                                    <xsl:with-param name="defaultValue" select="'0'"/>
                                </xsl:call-template>
                            </td>
                            <td>
                                <xsl:call-template name="x1f:getBooleanValue">
                                    <xsl:with-param name="value" select="@discrete"/>
                                    <xsl:with-param name="showNegative" select="true()"/>
                                </xsl:call-template>
                            </td>
                            <xsl:if test="@discrete = 'true'">
                                <td>
                                    <xsl:call-template name="x1f:getOptionalValue">
                                        <xsl:with-param name="node" select="@gridCellMaxOccupancy"/>
                                        <xsl:with-param name="defaultValue" select="'unbounded'"/>
                                    </xsl:call-template>
                                </td>
                                <td>
                                    <xsl:call-template name="x1f:getOptionalValue">
                                        <xsl:with-param name="node" select="@gridCellWidth"/>
                                        <xsl:with-param name="defaultValue" select="'1'"/>
                                    </xsl:call-template>
                                </td>
                            </xsl:if>
                        </tr>
                    </tbody>
                </table>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
-->   
</xsl:stylesheet>