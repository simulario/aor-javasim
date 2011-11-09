<?xml version="1.0" encoding="UTF-8"?>

<!--
	This stylesheet provides a functions to merge an XML document with other
	XML documents references by the base document.
	@autor   Thomas Grundmann
	@version 1.0
	@created 2009-07-23
-->
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:x1f="http://www.informatik.tu-cottbus.de/~tgrundm1/projects/xslt-framework/xslt-1.0/">

	<xsl:import href="ADTs/List.xsl"/>
	<xsl:import href="ADTs/Pair.xsl"/>

	<!--#########################-->
	<!--### initial functions ###-->
	<!--#########################-->

	<!--
		This template merges the document which is given by its URI with its
		referenced documents.
		@param  documentURI - The URI of the base document.
		@param  maxDepth    - The maximum depth until the referenced documents
                              are merged. (optional)
		@param  parameters  - A set of user-defined parameters for the document
                              processing. (optional)
		@return The merged document.
	-->
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

	<!--
		This template merges the document which is given by its document node
		with its referenced documents.
		@param  documentNode - The document's document node.
		@param  documentURI  - The URI of the base document. By default its
                               value is ".".
		@param  maxDepth     - The maximum depth until the referenced documents
                               are merged. (optional)
		@param  parameters   - A set of user-defined parameters for the
		                       document processing. (optional)
		@return The merged document.
	-->
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

	<!--########################-->
	<!--### process document ###-->
	<!--########################-->

	<!--
		This template performs the document processing for a given document.
		@param  document   - The document.
		@param  maxDepth   - The maximum depth until that referenced documents
                             are merged.
		@param  listOfURIs - The list of all known documents that shall be
		                     processed. The list's index point to the current
                             documents entry.
		@param  parameters - A set of user-defined parameters for the document
                             processing.
		@return The result of the document processing.
	-->
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
				<xsl:with-param name="userDefinedParameters" select="$updatedParameters"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--
		This template defines an interface for the document processing.
		@param  URI        - The document's URI.
		@param  parameters - A set of user-defined parameters for the document
                             processing.
		@return The result of the document processing.
	-->
	<xsl:template match="/|*" mode="x1f:mergeDocuments.processDocument">
		<xsl:param name="URI"/>
		<xsl:param name="parameters"/>
	</xsl:template>
	
	<!--######################################-->
	<!--### update user-defined parameters ###-->
	<!--######################################-->
	
	<!--
		This template defines an interface for the update of the user-defined
		parameters.
		@param  URI        - The document's URI.
		@param  parameters - The set of user-defined parameters.
		@return The updated user-defined parameters.
	-->
	<xsl:template match="/|*" mode="x1f:mergeDocuments.updateParameters">
		<xsl:param name="URI"/>
		<xsl:param name="parameters"/>
	</xsl:template>
	
	<!--###############-->
	<!--### addURIs ###-->
	<!--###############-->

	<!--
		This template defines an interface for adding URIs to the list of URIs.
		@param  listOfURIs - The list of URIs.
		@param  depth      - The depth for the new entries.
		@return The updated list of URIs.
	-->
	<xsl:template match="/|*" mode="x1f:mergeDocuments.addURIs">
		<xsl:param name="listOfURIs"/>
		<xsl:param name="depth"/>
	</xsl:template>

	<!--
		This template adds a set of new URIs to the given list of URIs.
		It appends the new values to the lists end (the list's entries will be
		sorted like atree linearization with beadth first search strategy).
		@param  listOfURIs    - the current list of URIs
		@param  listOfNewURIs - the set of new URIs
		@param  depth         - the new URIs' depth
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
		This template adds a set of new URIs to the given list of URIs.
		It appends the new values directly after the entry for the current
        documents URI (the list's entries will be sorted like atree
		linearization with depth first search strategy).
		@param  listOfURIs    - the current list of URIs
		@param  listOfNewURIs - the set of new URIs
		@param  depth         - the new URIs' depth
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