<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:fn="http://www.w3.org/2005/xpath-functions"
	xmlns:log="http://aor-simulation.org/log"
	xmlns="http://www.w3.org/1999/xhtml"
	version="2.0"
	exclude-result-prefixes="xsl xs fn log">
	
	<xsl:output method="xhtml" doctype-public="-//W3C//DTD XHTML 1.1//EN" doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd" encoding="UTF-8" indent="yes"/>
	
	<xsl:variable name="day_Name"          as="xs:string" select="'day'"/>
	<xsl:variable name="price_Name"        as="xs:string" select="'price'"/>
	<xsl:variable name="totalFood_Name"    as="xs:string" select="'totalFood'"/>
	<xsl:variable name="validBuyers_Name"  as="xs:string" select="'validBuyers'"/>
	<xsl:variable name="validSellers_Name" as="xs:string" select="'validSellers'"/>
  <xsl:variable name="messageType_Name"  as="xs:string" select="'messageType'"/>
  <xsl:variable name="bid_Name"          as="xs:string" select="'bid'"/>
	
	<xsl:variable name="price_Time"        as="xs:integer" select="19"/>
	<xsl:variable name="totalFood_Time"    as="xs:integer" select="21"/>
	<xsl:variable name="validBuyers_Time"  as="xs:integer" select="21"/>
	<xsl:variable name="validSellers_Time" as="xs:integer" select="21"/>
  <xsl:variable name="messageType_Time"  as="xs:integer" select="12"/>
  <xsl:variable name="bid_Time"          as="xs:integer" select="2"/>
    
	<xsl:variable name="priceWasEstablished_Message" as="xs:string" select="'PriceWasEstablished'"/>
	<xsl:variable name="decideToSell_Message"        as="xs:string" select="'DecideToSell'"/>
	<xsl:variable name="decideToBuy_Message"         as="xs:string" select="'DecideToBuy'"/>
  <xsl:variable name="sell_Message"                as="xs:string" select="'Sell'"/>
  <xsl:variable name="buy_Message"                 as="xs:string" select="'Buy'"/>
	
	
	<xsl:template match="/log:SimulationLog">
		<html>
			<head>
				<title>GoldFoodEconomic</title>
				<style type="text/css">
					<xsl:text disable-output-escaping="yes">
						table {
						border-collapse:collapse;
						}
						
						th,td {
						border: 1px solid black;
						}
					</xsl:text>				  
				</style>
			</head>
			<body>
				<table>
					<thead>
						<tr>
							<th scope="col">
								<xsl:value-of select="$day_Name"/>
							</th>
							<th scope="col">
								<xsl:value-of select="$price_Name"/>
							</th>
							<th scope="col">
								<xsl:value-of select="$totalFood_Name"/>
							</th>
							<th scope="col">
								<xsl:value-of select="$validBuyers_Name"/>
							</th>
							<th scope="col">
								<xsl:value-of select="$validSellers_Name"/>
							</th>
						  <xsl:for-each select="log:InitialState/log:Agents/log:Agt[@type='Worker']">
						    <xsl:sort select="@id" data-type="number"/>
						    <th scope="col">
						      <xsl:value-of select="fn:concat('ID_',@id)"/>
						    </th>
						  </xsl:for-each>
						  <xsl:for-each select="log:InitialState/log:Agents/log:Agt[@type='Worker']">
						    <xsl:sort select="@id" data-type="number"/>
						    <th scope="col">
						      <xsl:value-of select="fn:concat($bid_Name,'_',@id)"/>
						    </th>
						  </xsl:for-each>
						</tr>
					</thead>
					<tbody>
						<xsl:apply-templates select="log:SimulationStep" mode="nextDay"/>						
					</tbody>
				</table>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="log:SimulationStep" mode="nextDay"/>
	
	<xsl:template match="log:SimulationStep[number(@stepTime) mod 24 = 1]" mode="nextDay">
		<tr>
			<td>
				<xsl:value-of select="((number(@stepTime) - 1) div 24) + 1"/>
			</td>
			<td>
				<xsl:apply-templates select="following-sibling::log:SimulationStep[number(@stepTime) = (number(current()/@stepTime) + $price_Time - 1)]"       mode="getPrice"/>
			</td>
			<td>
				<xsl:apply-templates select="following-sibling::log:SimulationStep[number(@stepTime) = (number(current()/@stepTime) + $totalFood_Time - 1)]"   mode="getTotalFood"/>
			</td>
			<td>
				<xsl:apply-templates select="following-sibling::log:SimulationStep[number(@stepTime) = (number(current()/@stepTime) + $validBuyers_Time - 1)]"  mode="getValidBuyers"/>
			</td>
			<td>
				<xsl:apply-templates select="following-sibling::log:SimulationStep[number(@stepTime) = (number(current()/@stepTime) + $validSellers_Time - 1)]" mode="getValidSellers"/>
			</td>
		  <xsl:apply-templates select="following-sibling::log:SimulationStep[number(@stepTime) = (number(current()/@stepTime) + $messageType_Time -1)]/log:EnvironmentSimulatorStep/log:OutMessageEvent" mode="getMessageType">
		    <xsl:sort select="@senderIdRef" data-type="number"/>
		  </xsl:apply-templates>
		  <xsl:apply-templates select="following-sibling::log:SimulationStep[number(@stepTime) = (number(current()/@stepTime) + $bid_Time - 1)]/log:AgentSimResultingStateChanges/log:Agents/log:Agt" mode="getBid">
		    <xsl:sort select="@id" data-type="number"/>
		  </xsl:apply-templates>
		</tr>
	</xsl:template>
	
	<xsl:template match="log:SimulationStep" mode="getPrice">
		<xsl:variable name="OutMessageEvents" select="log:EnvironmentSimulatorStep/log:OutMessageEvent[@messageType = $priceWasEstablished_Message and log:ResultingStateChanges/log:Agents/log:Agt/log:Slot/@property = $price_Name]"/>
		<xsl:variable name="Agents" select="$OutMessageEvents[last()]/log:ResultingStateChanges/log:Agents/log:Agt[log:Slot/@property = $price_Name]"/>
		<xsl:variable name="Slots" select="$Agents[last()]/log:Slot[@property = $price_Name]"/>
		<xsl:value-of select="$Slots[last()]/@value"/>
	</xsl:template>

	<xsl:template match="log:SimulationStep" mode="getTotalFood">
		<xsl:variable name="OutMessageEvents" select="log:EnvironmentSimulatorStep/log:OutMessageEvent[@messageType = $decideToBuy_Message and log:ResultingStateChanges/log:Agents/log:Agt/log:Slot/@property = $totalFood_Name]"/>
		<xsl:variable name="Agents" select="$OutMessageEvents[last()]/log:ResultingStateChanges/log:Agents/log:Agt[log:Slot/@property = $totalFood_Name]"/>
		<xsl:variable name="Slots" select="$Agents[last()]/log:Slot[@property = $totalFood_Name]"/>
		<xsl:value-of select="$Slots[last()]/@value"/>
	</xsl:template>

	<xsl:template match="log:SimulationStep" mode="getValidBuyers">
		<xsl:variable name="OutMessageEvents" select="log:EnvironmentSimulatorStep/log:OutMessageEvent[@messageType = $decideToBuy_Message and log:ResultingStateChanges/log:Agents/log:Agt/log:Slot/@property = $validBuyers_Name]"/>
		<xsl:variable name="Agents" select="$OutMessageEvents[last()]/log:ResultingStateChanges/log:Agents/log:Agt[log:Slot/@property = $validBuyers_Name]"/>
		<xsl:variable name="Slots" select="$Agents[last()]/log:Slot[@property = $validBuyers_Name]"/>
		<xsl:value-of select="$Slots[last()]/@value"/>
	</xsl:template>
	
	<xsl:template match="log:SimulationStep" mode="getValidSellers">
		<xsl:variable name="OutMessageEvents" select="log:EnvironmentSimulatorStep/log:OutMessageEvent[@messageType = $decideToSell_Message and log:ResultingStateChanges/log:Agents/log:Agt/log:Slot/@property = $validSellers_Name]"/>
		<xsl:variable name="Agents" select="$OutMessageEvents[last()]/log:ResultingStateChanges/log:Agents/log:Agt[log:Slot/@property = $validSellers_Name]"/>
		<xsl:variable name="Slots" select="$Agents[last()]/log:Slot[@property = $validSellers_Name]"/>
		<xsl:value-of select="$Slots[last()]/@value"/>
	</xsl:template>
  
  <xsl:template match="log:OutMessageEvent[@messageType = $sell_Message or @messageType = $buy_Message]" mode="getMessageType">
    <td>
      <xsl:value-of select="@messageType"/>
    </td>
  </xsl:template>
  
  <xsl:template match="log:Agt" mode="getBid">
    <td>
      <xsl:value-of select="log:Slot[@property = $bid_Name]/@value"/>
    </td>
  </xsl:template>
</xsl:stylesheet>
