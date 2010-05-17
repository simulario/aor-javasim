<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:x1f="http://www.informatik.tu-cottbus.de/~tgrundm1/projects/xslt-1.0-framework/">

	<xsl:import href="List.xsl"/>
	<xsl:import href="Pair.xsl"/>

	<!--#########################-->
	<!--### initial functions ###-->
	<!--#########################-->

	<xsl:template name="x1f:mergeDocuments.mergeByURI">
		<xsl:param name="documentURI"/>
		<xsl:param name="maxDepth" select="''"/>
		<xsl:param name="parameters"/>
		<xsl:if test="$documentURI != ''">
			<xsl:call-template name="x1f:mergeDocuments.processDocument">
				<xsl:with-param name="document" select="document(string($documentURI))"/>
				<xsl:with-param name="maxDepth" select="$maxDepth"/>
				<xsl:with-param name="listOfURIs">
					<xsl:call-template name="x1f:Pair.createPair">
						<xsl:with-param name="value1">
							<xsl:call-template name="x1f:List.firstIndex">
								<xsl:with-param name="list">
									<xsl:call-template name="x1f:List.appendValue">
										<xsl:with-param name="list">
											<xsl:call-template name="x1f:List.createEmptyList">
												<xsl:with-param name="duplicate-free" select="true()"/>
											</xsl:call-template>
										</xsl:with-param>
										<xsl:with-param name="value" select="$documentURI"/>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:with-param>
						<xsl:with-param name="value2">
							<xsl:call-template name="x1f:List.firstIndex">
								<xsl:with-param name="list">
									<xsl:call-template name="x1f:List.appendValue">
										<xsl:with-param name="list">
											<xsl:call-template name="x1f:List.createEmptyList"/>
										</xsl:with-param>
										<xsl:with-param name="value" select="1"/>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="parameters" select="$parameters"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="x1f:mergeDocuments.mergeByNode">
		<xsl:param name="documentNode"/>
		<xsl:param name="documentURI" select="'.'"/>
		<xsl:param name="maxDepth" select="''"/>
		<xsl:param name="parameters"/>
		<xsl:if test="$documentNode">
			<xsl:call-template name="x1f:mergeDocuments.processDocument">
				<xsl:with-param name="document" select="$documentNode"/>
				<xsl:with-param name="maxDepth" select="$maxDepth"/>
				<xsl:with-param name="listOfURIs">
					<xsl:call-template name="x1f:Pair.createPair">
						<xsl:with-param name="value1">
							<xsl:call-template name="x1f:List.firstIndex">
								<xsl:with-param name="list">
									<xsl:call-template name="x1f:List.appendValue">
										<xsl:with-param name="list">
											<xsl:call-template name="x1f:List.createEmptyList">
												<xsl:with-param name="duplicate-free" select="true()"/>
											</xsl:call-template>
										</xsl:with-param>
										<xsl:with-param name="value" select="'.'"/>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:with-param>
						<xsl:with-param name="value2">
							<xsl:call-template name="x1f:List.firstIndex">
								<xsl:with-param name="list">
									<xsl:call-template name="x1f:List.appendValue">
										<xsl:with-param name="list">
											<xsl:call-template name="x1f:List.createEmptyList"/>
										</xsl:with-param>
										<xsl:with-param name="value" select="1"/>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="parameters" select="$parameters"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--########################-->
	<!--### process document ###-->
	<!--########################-->

	<xsl:template name="x1f:mergeDocuments.processDocument">
		<xsl:param name="document"/>
		<xsl:param name="maxDepth"/>
		<xsl:param name="listOfURIs"/>
		<xsl:param name="parameters"/>

		<!-- get current values -->
		<xsl:variable name="currentURI">
			<xsl:call-template name="x1f:List.getValue">
				<xsl:with-param name="list">
					<xsl:call-template name="x1f:Pair.getValue1">
						<xsl:with-param name="pair" select="$listOfURIs"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="currentDepth">
			<xsl:call-template name="x1f:List.getValue">
				<xsl:with-param name="list">
					<xsl:call-template name="x1f:Pair.getValue2">
						<xsl:with-param name="pair" select="$listOfURIs"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<!-- depth condition -->
		<xsl:variable name="depthCondition" select="$maxDepth = '' or not($currentDepth > $maxDepth)"/>

		<!-- process the document -->
		<xsl:if test="$depthCondition">
			<xsl:apply-templates select="$document" mode="x1f:mergeDocuments.processDocument">
				<xsl:with-param name="URI" select="$currentURI"/>
				<xsl:with-param name="parameters" select="$parameters"/>
			</xsl:apply-templates>
		</xsl:if>
		
		<!-- update user-defined parameters -->
		<xsl:variable name="updatedParameters">
			<xsl:choose>
				<xsl:when test="$depthCondition">
					<xsl:apply-templates select="$document" mode="x1f:mergeDocuments.updateParameters">
						<xsl:with-param name="URI" select="$currentURI"/>
						<xsl:with-param name="parameters" select="$parameters"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$parameters"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- update the list -->
		<xsl:variable name="updatedListOfURIs">
			<xsl:variable name="newList">
				<xsl:choose>
					<xsl:when test="$depthCondition">
						<xsl:apply-templates select="$document" mode="x1f:mergeDocuments.addURIs">
							<xsl:with-param name="listOfURIs" select="$listOfURIs"/>
							<xsl:with-param name="depth" select="$currentDepth + 1"/>
						</xsl:apply-templates>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$listOfURIs"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:call-template name="x1f:Pair.createPair">
				<xsl:with-param name="value1">
					<xsl:call-template name="x1f:List.nextIndex">
						<xsl:with-param name="list">
							<xsl:call-template name="x1f:Pair.getValue1">
								<xsl:with-param name="pair" select="$newList"/>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="value2">
					<xsl:call-template name="x1f:List.nextIndex">
						<xsl:with-param name="list">
							<xsl:call-template name="x1f:Pair.getValue2">
								<xsl:with-param name="pair" select="$newList"/>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<!-- get next values -->
		<xsl:variable name="nextURI">
			<xsl:call-template name="x1f:List.getValue">
				<xsl:with-param name="list">
					<xsl:call-template name="x1f:Pair.getValue1">
						<xsl:with-param name="pair" select="$updatedListOfURIs"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>				
		<xsl:variable name="nextDepth">
			<xsl:call-template name="x1f:List.getValue">
				<xsl:with-param name="list">
					<xsl:call-template name="x1f:Pair.getValue2">
						<xsl:with-param name="pair" select="$updatedListOfURIs"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:variable>

		<!-- process the next document -->
		<xsl:if test="$nextURI != '' and not($nextDepth > $maxDepth)">
			<xsl:call-template name="x1f:mergeDocuments.processDocument">
				<xsl:with-param name="document" select="document(string($nextURI))"/>
				<xsl:with-param name="maxDepth" select="$maxDepth"/>
				<xsl:with-param name="listOfURIs" select="$updatedListOfURIs"/>
				<xsl:with-param name="parameters" select="$updatedParameters"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="/|*" mode="x1f:mergeDocuments.processDocument">
		<xsl:param name="URI"/>
		<xsl:param name="parameters"/>
	</xsl:template>
	
	<!--######################################-->
	<!--### update user-defined parameters ###-->
	<!--######################################-->
	
	<xsl:template match="/|*" mode="x1f:mergeDocuments.updateParameters">
		<xsl:param name="URI"/>
		<xsl:param name="parameters"/>
	</xsl:template>
	
	<!--###############-->
	<!--### addURIs ###-->
	<!--###############-->

	<xsl:template match="/|*" mode="x1f:mergeDocuments.addURIs">
		<xsl:param name="listOfURIs"/>
		<xsl:param name="depth"/>
	</xsl:template>

	<!--
		This template appends a set of new URIs to the given list of URIs.
		It uses the beadth first search strategy.
		@param  listOfURIs the current list of URIs
		@param  newURIs    the set of new URIs
		@param  depth      the new URIs' depth
		@return the new list of URIs
	-->
	<xsl:template name="x1f:mergeDocuments.addURIsByBreadthFirstSearch">
		<xsl:param name="listOfURIs"/>
		<xsl:param name="listOfNewURIs"/>
		<xsl:param name="depth"/>
		<xsl:variable name="empty">
			<xsl:call-template name="x1f:List.isEmpty">
				<xsl:with-param name="list" select="$listOfNewURIs"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$empty = 'false'">
				<xsl:variable name="listOfURIs.entry1">
					<xsl:call-template name="x1f:Pair.getValue1">
						<xsl:with-param name="pair" select="$listOfURIs"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="updatedListOfURIs">
					<xsl:call-template name="x1f:List.appendValue">
						<xsl:with-param name="list" select="$listOfURIs.entry1"/>
						<xsl:with-param name="value">
							<xsl:call-template name="x1f:List.getValue">
								<xsl:with-param name="list">
									<xsl:call-template name="x1f:List.firstIndex">
										<xsl:with-param name="list" select="$listOfNewURIs"/>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:variable>
				<xsl:call-template name="x1f:mergeDocuments.addURIsByBreadthFirstSearch">
					<xsl:with-param name="listOfURIs">
						<xsl:choose>
							<xsl:when test="string-length($updatedListOfURIs) > string-length($listOfURIs.entry1)">
								<xsl:call-template name="x1f:Pair.createPair">
									<xsl:with-param name="value1" select="$updatedListOfURIs"/>
									<xsl:with-param name="value2">
										<xsl:call-template name="x1f:List.appendValue">
											<xsl:with-param name="list">
												<xsl:call-template name="x1f:Pair.getValue2">
													<xsl:with-param name="pair" select="$listOfURIs"/>
												</xsl:call-template>
											</xsl:with-param>
											<xsl:with-param name="value" select="$depth"/>
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$listOfURIs"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="listOfNewURIs">
						<xsl:call-template name="x1f:List.removeValue">
							<xsl:with-param name="list">
								<xsl:call-template name="x1f:List.firstIndex">
									<xsl:with-param name="list" select="$listOfNewURIs"/>
								</xsl:call-template>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:with-param>
					<xsl:with-param name="depth" select="$depth"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="$listOfURIs"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>	
	
	<!--
		This template appends a set of new URIs to the given list of URIs.
		It uses the depth first search strategy.
		@param  listOfURIs the current list of URIs
		@param  listIndex  the index of the element after that the new URI
                           shall be added
		@param  newURIs    the set of new URIs
		@param  depth      the new URIs' depth
		@return the new list of URIs
	-->
	<xsl:template name="x1f:mergeDocuments.addURIsByDepthFirstSearch">
		<xsl:param name="listOfURIs"/>
		<xsl:param name="listOfNewURIs"/>
		<xsl:param name="depth"/>
		<xsl:param name="currentIndex">
			<xsl:call-template name="x1f:List.getIndex">
				<xsl:with-param name="list">
					<xsl:call-template name="x1f:Pair.getValue1">
						<xsl:with-param name="pair" select="$listOfURIs"/>
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:param>
		<xsl:variable name="empty">
			<xsl:call-template name="x1f:List.isEmpty">
				<xsl:with-param name="list" select="$listOfNewURIs"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$empty = 'false'">
				<xsl:variable name="listOfURIs.entry1">
					<xsl:call-template name="x1f:Pair.getValue1">
						<xsl:with-param name="pair" select="$listOfURIs"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="updatedListOfURIs">
					<xsl:call-template name="x1f:List.nextIndex">
						<xsl:with-param name="list">
							<xsl:call-template name="x1f:List.insertValue">
								<xsl:with-param name="list" select="$listOfURIs.entry1"/>
								<xsl:with-param name="value">
									<xsl:call-template name="x1f:List.getValue">
										<xsl:with-param name="list">
											<xsl:call-template name="x1f:List.firstIndex">
												<xsl:with-param name="list" select="$listOfNewURIs"/>
											</xsl:call-template>
										</xsl:with-param>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:variable>
				<xsl:call-template name="x1f:mergeDocuments.addURIsByDepthFirstSearch">
					<xsl:with-param name="listOfURIs">
						<xsl:choose>
							<xsl:when test="string-length($updatedListOfURIs) > string-length($listOfURIs.entry1)">
								<xsl:call-template name="x1f:Pair.createPair">
									<xsl:with-param name="value1" select="$updatedListOfURIs"/>
									<xsl:with-param name="value2">
										<xsl:call-template name="x1f:List.nextIndex">
											<xsl:with-param name="list">
												<xsl:call-template name="x1f:List.insertValue">
													<xsl:with-param name="list">
														<xsl:call-template name="x1f:Pair.getValue2">
															<xsl:with-param name="pair" select="$listOfURIs"/>
														</xsl:call-template>
													</xsl:with-param>
													<xsl:with-param name="value" select="$depth"/>
												</xsl:call-template>
											</xsl:with-param>
										</xsl:call-template>
									</xsl:with-param>
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$listOfURIs"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
					<xsl:with-param name="listOfNewURIs">
						<xsl:call-template name="x1f:List.removeValue">
							<xsl:with-param name="list">
								<xsl:call-template name="x1f:List.firstIndex">
									<xsl:with-param name="list" select="$listOfNewURIs"/>
								</xsl:call-template>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:with-param>
					<xsl:with-param name="depth" select="$depth"/>
					<xsl:with-param name="currentIndex" select="$currentIndex"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="x1f:Pair.createPair">
					<xsl:with-param name="value1">
						<xsl:call-template name="x1f:List.setIndex">
							<xsl:with-param name="list">
								<xsl:call-template name="x1f:Pair.getValue1">
									<xsl:with-param name="pair" select="$listOfURIs"/>
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="index" select="$currentIndex"/>
						</xsl:call-template>
					</xsl:with-param>
					<xsl:with-param name="value2">
						<xsl:call-template name="x1f:List.setIndex">
							<xsl:with-param name="list">
								<xsl:call-template name="x1f:Pair.getValue2">
									<xsl:with-param name="pair" select="$listOfURIs"/>
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="index" select="$currentIndex"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>