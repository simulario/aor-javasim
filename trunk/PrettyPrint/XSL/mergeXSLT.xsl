<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:x1f="http://www.informatik.tu-cottbus.de/~tgrundm1/projects/xslt-1.0-framework/">
	
	<xsl:include href="framework.xsl"/>

	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	
	<xsl:template match="*"/>

	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="xsl:stylesheet|xsl:transform">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:copy-of select="xsl:output"/>
			<xsl:call-template name="x1f:mergeDocuments.mergeByNode">
				<xsl:with-param name="documentNode" select="/"/>
				<xsl:with-param name="parameters">
					<xsl:call-template name="x1f:List.appendEntries">
						<xsl:with-param name="list">
							<xsl:call-template name="x1f:List.createEmptyList"/>
						</xsl:with-param>
						<xsl:with-param name="entries">
							<xsl:call-template name="x1f:List.Entry.createEntry">
								<xsl:with-param name="value" select="-1"/>
							</xsl:call-template>
							<xsl:call-template name="x1f:List.Entry.createEntry">
								<xsl:with-param name="value">
									<xsl:call-template name="x1f:List.createEmptyList"/>
								</xsl:with-param>
							</xsl:call-template>
							<xsl:call-template name="x1f:List.Entry.createEntry">
								<xsl:with-param name="value">
									<xsl:call-template name="x1f:List.createEmptyList"/>
								</xsl:with-param>
							</xsl:call-template>
							<xsl:call-template name="x1f:List.Entry.createEntry">
								<xsl:with-param name="value">
									<xsl:call-template name="x1f:List.createEmptyList"/>
								</xsl:with-param>
							</xsl:call-template>
							<xsl:call-template name="x1f:List.Entry.createEntry">
								<xsl:with-param name="value">
									<xsl:call-template name="x1f:List.createEmptyList"/>
								</xsl:with-param>
							</xsl:call-template>
							<xsl:call-template name="x1f:List.Entry.createEntry">
								<xsl:with-param name="value">
									<xsl:call-template name="x1f:List.createEmptyList"/>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:copy>
	</xsl:template>
	
	<!--###############-->
	<!--### addURIs ###-->
	<!--###############-->

	<xsl:template match="/" mode="x1f:mergeDocuments.addURIs">
		<xsl:param name="listOfURIs"/>
		<xsl:param name="depth"/>
		<xsl:apply-templates select="xsl:stylesheet|xsl:transform" mode="addURIs">
			<xsl:with-param name="listOfURIs" select="$listOfURIs"/>
			<xsl:with-param name="depth" select="$depth"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="xsl:stylesheet|xsl:transform" mode="addURIs">
		<xsl:param name="listOfURIs"/>
		<xsl:param name="depth"/>
		<xsl:call-template name="x1f:mergeDocuments.addURIsByDepthFirstSearch">
			<xsl:with-param name="listOfURIs" select="$listOfURIs"/>
			<xsl:with-param name="listOfNewURIs">
				<xsl:call-template name="x1f:List.appendEntries">
					<xsl:with-param name="list">
						<xsl:call-template name="x1f:List.createEmptyList"/>
					</xsl:with-param>
					<xsl:with-param name="entries">
						<xsl:apply-templates select="xsl:import|xsl:include" mode="addURIs">
							<xsl:sort select="position()" order="descending" data-type="number"/>
						</xsl:apply-templates>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="depth" select="$depth"/>
		</xsl:call-template>
	</xsl:template>
		
	<xsl:template match="xsl:import|xsl:include" mode="addURIs">
		<xsl:call-template name="x1f:List.Entry.createEntry">
			<xsl:with-param name="value" select="@href"/>
		</xsl:call-template>
	</xsl:template>

	<!--#######################-->
	<!--### processDocument ###-->
	<!--#######################-->

	<xsl:template match="/" mode="x1f:mergeDocuments.processDocument">
		<xsl:param name="URI"/>
		<xsl:param name="parameters"/>
		<xsl:message>
			<xsl:value-of select="$URI"/>
		</xsl:message>
		<xsl:comment>
			<xsl:value-of select="concat('copied from ',$URI)"/>
		</xsl:comment>
		<xsl:apply-templates select="xsl:stylesheet|xsl:transform" mode="processDocument">
			<xsl:with-param name="parameters" select="$parameters"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="xsl:stylesheet|xsl:transform" mode="processDocument">
		<xsl:param name="parameters"/>
		<xsl:apply-templates select="comment()|text()|xsl:template|xsl:param|xsl:variable|xsl:key" mode="processDocument">
			<xsl:with-param name="parameters" select="$parameters"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="*" mode="processDocument"/>
	
	<xsl:template match="comment()|text()" mode="processDocument">
		<xsl:copy-of select="."/>
	</xsl:template>
	
	<xsl:template match="xsl:template[@match]" mode="processDocument">
		<xsl:param name="parameters"/>
		
		<xsl:variable name="parameters.priority">
			<xsl:call-template name="x1f:List.firstIndex">
				<xsl:with-param name="list" select="$parameters"/>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:variable name="parameters.matchTemplateList">
			<xsl:call-template name="x1f:List.nextIndex">
				<xsl:with-param name="list" select="$parameters.priority"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="contains">
			<xsl:call-template name="x1f:List.containsValue">
				<xsl:with-param name="list">
					<xsl:call-template name="x1f:List.getValue">
						<xsl:with-param name="list" select="$parameters.matchTemplateList"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="value">
					<xsl:call-template name="x1f:Pair.createPair">
						<xsl:with-param name="value1" select="@match"/>
						<xsl:with-param name="value2" select="@mode"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="$contains = 'false'">
			<xsl:copy>
				<xsl:copy-of select="@*[local-name()!='priority']"/>
				<xsl:attribute name="priority">
					<xsl:call-template name="x1f:List.getValue">
						<xsl:with-param name="list" select="$parameters.priority"/>
					</xsl:call-template>
				</xsl:attribute>
				<xsl:copy-of select="*|comment()|text()"/>
			</xsl:copy>
		</xsl:if>	
	</xsl:template>
	
	<xsl:template match="xsl:template[@name]" mode="processDocument">
		<xsl:param name="parameters"/>
				
		<xsl:variable name="parameters.nameTemplateList">
			<xsl:call-template name="x1f:List.nextIndex">
				<xsl:with-param name="list">
					<xsl:call-template name="x1f:List.nextIndex">
						<xsl:with-param name="list">
							<xsl:call-template name="x1f:List.nextIndex">
								<xsl:with-param name="list">
									<xsl:call-template name="x1f:List.firstIndex">
										<xsl:with-param name="list" select="$parameters"/>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="contains">
			<xsl:call-template name="x1f:List.containsValue">
				<xsl:with-param name="list">
					<xsl:call-template name="x1f:List.getValue">
						<xsl:with-param name="list" select="$parameters.nameTemplateList"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="value" select="@name"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="$contains = 'false'">
			<xsl:copy-of select="."/>
		</xsl:if>	
	</xsl:template>
	
	<xsl:template match="xsl:param|xsl:variable" mode="processDocument">
		<xsl:param name="parameters"/>

		<xsl:variable name="parameters.VariableList">
			<xsl:call-template name="x1f:List.nextIndex">
				<xsl:with-param name="list">
					<xsl:call-template name="x1f:List.nextIndex">
						<xsl:with-param name="list">
							<xsl:call-template name="x1f:List.nextIndex">
								<xsl:with-param name="list">
									<xsl:call-template name="x1f:List.nextIndex">
										<xsl:with-param name="list">
											<xsl:call-template name="x1f:List.firstIndex">
												<xsl:with-param name="list" select="$parameters"/>
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:variable name="contains">
			<xsl:call-template name="x1f:List.containsValue">
				<xsl:with-param name="list">
					<xsl:call-template name="x1f:List.getValue">
						<xsl:with-param name="list" select="$parameters.VariableList"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="value" select="@name"/>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:if test="$contains = 'false'">
			<xsl:copy-of select="."/>
		</xsl:if>			
	</xsl:template>
	
	<xsl:template match="xsl:key" mode="processDocument">
		<xsl:param name="parameters"/>

		<xsl:variable name="parameters.keyList">
			<xsl:call-template name="x1f:List.nextIndex">
				<xsl:with-param name="list">
					<xsl:call-template name="x1f:List.nextIndex">
						<xsl:with-param name="list">
							<xsl:call-template name="x1f:List.nextIndex">
								<xsl:with-param name="list">
									<xsl:call-template name="x1f:List.nextIndex">
										<xsl:with-param name="list">
											<xsl:call-template name="x1f:List.nextIndex">
												<xsl:with-param name="list">
													<xsl:call-template name="x1f:List.firstIndex">
														<xsl:with-param name="list" select="$parameters"/>
													</xsl:call-template>
												</xsl:with-param>
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:variable name="contains">
			<xsl:call-template name="x1f:List.containsValue">
				<xsl:with-param name="list">
					<xsl:call-template name="x1f:List.getValue">
						<xsl:with-param name="list" select="$parameters.keyList"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="value" select="@name"/>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:if test="$contains = 'false'">
			<xsl:copy-of select="."/>
		</xsl:if>
	</xsl:template>

	<!--########################-->
	<!--### updateParameters ###-->
	<!--########################-->
	
	<xsl:template match="/" mode="x1f:mergeDocuments.updateParameters">
		<xsl:param name="URI"/>
		<xsl:param name="parameters"/>
		
		<xsl:apply-templates select="xsl:stylesheet|xsl:transform" mode="updateParameters">
			<xsl:with-param name="URI" select="$URI"/>
			<xsl:with-param name="parameters" select="$parameters"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="xsl:stylesheet|xsl:transform" mode="updateParameters">
		<xsl:param name="URI"/>
		<xsl:param name="parameters"/>
		
		<!-- priority -->
		<xsl:variable name="parameters.priority">
			<xsl:call-template name="x1f:List.firstIndex">
				<xsl:with-param name="list" select="$parameters"/>
			</xsl:call-template>
		</xsl:variable>
				
		<xsl:variable name="updatedPriority">
			<xsl:variable name="priority">
				<xsl:call-template name="x1f:List.getValue">
					<xsl:with-param name="list" select="$parameters.priority"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:value-of select="$priority - 1"/>
		</xsl:variable>

		<!-- matchTemplateList -->
		<xsl:variable name="parameters.matchTemplateList">
			<xsl:call-template name="x1f:List.nextIndex">
				<xsl:with-param name="list" select="$parameters.priority"/>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:variable name="updatedMatchTemplateList">
			<xsl:call-template name="x1f:List.appendEntries">
				<xsl:with-param name="list">
					<xsl:call-template name="x1f:List.getValue">
						<xsl:with-param name="list" select="$parameters.matchTemplateList"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="entries">
					<xsl:apply-templates select="xsl:template[@match]" mode="updateParameters"/>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- applyImportsList -->
		<xsl:variable name="parameters.applyImportsList">
			<xsl:call-template name="x1f:List.nextIndex">
				<xsl:with-param name="list" select="$parameters.matchTemplateList"/>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:variable name="updatedApplyImportsList">
			<xsl:call-template name="x1f:List.getValue">
				<xsl:with-param name="list" select="$parameters.applyImportsList"/>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- nameTemplateList -->
		<xsl:variable name="parameters.nameTemplateList">
			<xsl:call-template name="x1f:List.nextIndex">
				<xsl:with-param name="list" select="$parameters.applyImportsList"/>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:variable name="updatedNameTemplateList">
			<xsl:call-template name="x1f:List.appendEntries">
				<xsl:with-param name="list">
					<xsl:call-template name="x1f:List.getValue">
						<xsl:with-param name="list" select="$parameters.nameTemplateList"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="entries">
					<xsl:apply-templates select="xsl:template[@name]" mode="updateParameters"/>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- variableList -->
		<xsl:variable name="parameters.variableList">
			<xsl:call-template name="x1f:List.nextIndex">
				<xsl:with-param name="list" select="$parameters.nameTemplateList"/>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:variable name="updatedVariableList">
			<xsl:call-template name="x1f:List.appendEntries">
				<xsl:with-param name="list">
					<xsl:call-template name="x1f:List.getValue">
						<xsl:with-param name="list" select="$parameters.variableList"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="entries">
					<xsl:apply-templates select="xsl:param|xsl:variable" mode="updateParameters"/>
				</xsl:with-param>
			</xsl:call-template>			
		</xsl:variable>

		<!-- keyList -->
		<xsl:variable name="parameters.keyList">
			<xsl:call-template name="x1f:List.nextIndex">
				<xsl:with-param name="list" select="$parameters.variableList"/>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:variable name="updatedKeyList">
			<xsl:call-template name="x1f:List.getValue">
				<xsl:with-param name="list" select="$parameters.keyList"/>
			</xsl:call-template>
		</xsl:variable>

		<!-- updated parameters -->
		<xsl:call-template name="x1f:List.appendEntries">
			<xsl:with-param name="list">
				<xsl:call-template name="x1f:List.createEmptyList"/>
			</xsl:with-param>
			<xsl:with-param name="entries">
				<xsl:call-template name="x1f:List.Entry.createEntry">
					<xsl:with-param name="value" select="$updatedPriority"/>
				</xsl:call-template>
				<xsl:call-template name="x1f:List.Entry.createEntry">
					<xsl:with-param name="value" select="$updatedMatchTemplateList"/>
				</xsl:call-template>
				<xsl:call-template name="x1f:List.Entry.createEntry">
					<xsl:with-param name="value" select="$updatedApplyImportsList"/>
				</xsl:call-template>
				<xsl:call-template name="x1f:List.Entry.createEntry">
					<xsl:with-param name="value" select="$updatedNameTemplateList"/>
				</xsl:call-template>
				<xsl:call-template name="x1f:List.Entry.createEntry">
					<xsl:with-param name="value" select="$updatedVariableList"/>
				</xsl:call-template>
				<xsl:call-template name="x1f:List.Entry.createEntry">
					<xsl:with-param name="value" select="$updatedKeyList"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="*" mode="updateParameters"/>
	
	<xsl:template match="xsl:template[@match]" mode="updateParameters">
		<xsl:call-template name="x1f:List.Entry.createEntry">
			<xsl:with-param name="value">
				<xsl:call-template name="x1f:Pair.createPair">
					<xsl:with-param name="value1" select="@match"/>
					<xsl:with-param name="value2" select="@mode"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="xsl:template[@name]" mode="updateParameters">
		<xsl:call-template name="x1f:List.Entry.createEntry">
			<xsl:with-param name="value" select="@name"/>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="xsl:param|xsl:variable" mode="updateParamters">
		<xsl:call-template name="x1f:List.Entry.createEntry">
			<xsl:with-param name="value" select="@name"/>
		</xsl:call-template>
	</xsl:template>
</xsl:stylesheet>