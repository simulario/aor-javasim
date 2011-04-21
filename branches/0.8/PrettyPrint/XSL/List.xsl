<?xml version="1.0" encoding="UTF-8"?>

<!--
	This stylesheet provides string based lists and some basic list operations.
	@autor   Thomas Grundmann
	@version 1.0
	@created 2009-07-21
-->
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:x1f="http://www.informatik.tu-cottbus.de/~tgrundm1/projects/xslt-1.0-framework/">
	
	<!--#####################-->
	<!--### list creation ###-->
	<!--#####################-->
	
	<!--
		This templates creates an empty list.
		@param  duplicate-free - If 'true' the list is duplicate free.
		                         Otherwise it can contain duplicates. Its
		                         default value is 'false'.
		@return An empty list.
	-->
	<xsl:template name="x1f:List.createEmptyList">
		<xsl:param name="duplicate-free" select="'false'"/>
		<xsl:choose>
			<xsl:when test="$duplicate-free = 'true'">
				<xsl:text>list:true#0#</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>list:false#0#</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--#########################-->
	<!--### support functions ###-->
	<!--#########################-->

	<!--
		This template returns a list's body.
		@param  list - The list whose body shall be returned.
		@return The list's body.
	-->
	<xsl:template name="x1f:List.getBody">
		<xsl:param name="list"/>
		<xsl:value-of select="substring-after(substring-after($list,'#'),'#')"/>
	</xsl:template>
	
	<!--#######################-->
	<!--### entry functions ###-->
	<!--#######################-->
	
	<!--
		This template creates a list entry.
		@param  value - The entry's value.
		@return The list entry.
	-->
	<xsl:template name="x1f:List.Entry.createEntry">
		<xsl:param name="value"/>
		<xsl:value-of select="concat('#',string-length($value),':',$value)"/>
	</xsl:template>
		
	<!--
		This template returns a list entry's value.
		@param  entry - The entry whose value shall be returned.
		@return The list entry's value.
	-->
	<xsl:template name="x1f:List.Entry.getValue">
		<xsl:param name="entry"/>
		<xsl:value-of select="substring-after($entry,':')"/>
	</xsl:template>

	<!--######################-->
	<!--### test functions ###-->
	<!--######################-->

	<!--
		This template checks if a given value is part of the given list.
		@param  list  - The list that shall be checked.
		@param  value - The value that is searched.
		@return 'true' if the value is already in the list, otherwise 'false'.
	-->
	<xsl:template name="x1f:List.containsValue">
		<xsl:param name="list"/>
		<xsl:param name="value"/>
		
		<xsl:variable name="empty">
			<xsl:call-template name="x1f:List.isEmpty">
				<xsl:with-param name="list" select="$list"/>
			</xsl:call-template>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="$empty = 'false'">
				<xsl:variable name="list2">
					<xsl:call-template name="x1f:List.firstIndex">
						<xsl:with-param name="list" select="$list"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="listValue">
					<xsl:call-template name="x1f:List.getValue">
						<xsl:with-param name="list" select="$list2"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="$listValue = $value">
						<xsl:value-of select="true()"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="x1f:List.containsValue">
							<xsl:with-param name="list">
								<xsl:call-template name="x1f:List.removeValue">
									<xsl:with-param name="list" select="$list2"/>
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="value" select="$value"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		This template checks if a list is duplicate free.
		@param  list - The list which shall be checked.
		@return 'true' if the list is duplicate free, otherwise 'false'.
	-->
	<xsl:template name="x1f:List.isDuplicateFree">
		<xsl:param name="list"/>
		<xsl:value-of select="substring-before(substring-after($list,'list:'),'#')"/>
	</xsl:template>

	<!--
		This template checks if a list is empty.
		@param  list - The list which shall be checked.
		@return 'true' if the list is empty, otherwise 'false'.
	-->
	<xsl:template name="x1f:List.isEmpty">
		<xsl:param name="list"/>
		<xsl:variable name="body">
			<xsl:call-template name="x1f:List.getBody">
				<xsl:with-param name="list" select="$list"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:value-of select="string-length($body) = 0"/>
	</xsl:template>
	
	<!--
		This template checks if the list's current value has a decendant. If
		the index is 0 the list.isEmpty function is called.
		@param  list - The list which shall be checked.
		@return 'true' if the current value is not the last, otherwise 'false'.
	-->
	<xsl:template name="x1f:List.hasNext">
		<xsl:param name="list"/>
		<xsl:variable name="index">
			<xsl:call-template name="x1f:List.getIndex">
				<xsl:with-param name="list" select="$list"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="body">
			<xsl:call-template name="x1f:List.getBody">
				<xsl:with-param name="list" select="$list"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$index > 0">
				<xsl:variable name="length" select="substring-before(substring-after(substring($body,$index),'#'),':')"/>
				<xsl:value-of select="string-length(substring($body,$index)) - 2 - string-length($length) > $length"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="empty">
					<xsl:call-template name="x1f:List.isEmpty">
						<xsl:with-param name="list" select="$list"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:value-of select="$empty = 'false'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--#######################-->
	<!--### index functions ###-->
	<!--#######################-->

	<!--
		This template returns a list's index.
		@param  list - The list whose index shall be returned.
		@return The list's index or '0' if the list is empty.
	-->
	<xsl:template name="x1f:List.getIndex">
		<xsl:param name="list"/>		
		<xsl:value-of select="substring-before(substring-after($list,'#'),'#')"/>
	</xsl:template>
	
	<!--
		Sets the index to the given position. If the index is invalid it's set
		to 0.
		@param  list  - The list whose index shall be updated.
		@param  index - The new index.
		@return The updated list.
	-->
	<xsl:template name="x1f:List.setIndex">
		<xsl:param name="list"/>
		<xsl:param name="index"/>
		<xsl:variable name="body">
			<xsl:call-template name="x1f:List.getBody">
				<xsl:with-param name="list" select="$list"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$index > string-length($body)">
				<xsl:call-template name="x1f:List.resetIndex">
					<xsl:with-param name="list" select="$list"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat(substring-before($list,'#'),'#',$index,'#',$body)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!--
		Resets the list's index to 0.
		@param  list - Thi list whose index shall be reseted.
		@return The list with the new index.
	-->
	<xsl:template name="x1f:List.resetIndex">
		<xsl:param name="list"/>
		<xsl:value-of select="concat(substring-before($list,'#'),'#0#')"/>
		<xsl:call-template name="x1f:List.getBody">
			<xsl:with-param name="list" select="$list"/>
		</xsl:call-template>
	</xsl:template>	

	<!--
		This template sets a list's index to its first element. If the list is
		empty the original list is returned.
		@param  list - The list whose index shall be changed.
		@return The list with the new index.
	-->
	<xsl:template name="x1f:List.firstIndex">
		<xsl:param name="list"/>
		<xsl:variable name="empty">
			<xsl:call-template name="x1f:List.isEmpty">
				<xsl:with-param name="list" select="$list"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$empty = 'false'">
				<xsl:value-of select="concat(substring-before($list,'#'),'#1#')"/>
				<xsl:call-template name="x1f:List.getBody">
					<xsl:with-param name="list" select="$list"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$list"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		This template sets a list's index to its next element. If the list's
		end is reached, its value is set to '0'. If the index is not set, it's
		set to the first index.
		@param  list - The list whose index shall be changed.
		@return The list with the new index.
	-->	
	<xsl:template name="x1f:List.nextIndex">
		<xsl:param name="list"/>
		<xsl:variable name="index">
			<xsl:call-template name="x1f:List.getIndex">
				<xsl:with-param name="list" select="$list"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$index != 0">
				<xsl:variable name="body">
					<xsl:call-template name="x1f:List.getBody">
						<xsl:with-param name="list" select="$list"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="length" select="substring-before(substring-after(substring($body,$index),'#'),':')"/>
				<xsl:variable name="newIndex" select="$index + string-length($length) + 1 + $length + 1"/>
				<xsl:choose>
					<xsl:when test="$newIndex > string-length($body)">
						<xsl:value-of select="concat(substring-before($list,'#'),'#0#',$body)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat(substring-before($list,'#'),'#',$newIndex,'#',$body)"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="x1f:List.firstIndex">
					<xsl:with-param name="list" select="$list"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--#########################-->
	<!--### list manipulation ###-->
	<!--#########################-->
	
	<!--
		This template appends a new list entry to the list's end.
		@param  list  - The list to that the value shall be added.
		@param  entry - The entry that shall be added.
		@return The new list.
	-->
	<xsl:template name="x1f:List.appendEntry">
		<xsl:param name="list"/>
		<xsl:param name="entry"/>
		<xsl:variable name="duplicateFree">
			<xsl:call-template name="x1f:List.isDuplicateFree">
				<xsl:with-param name="list" select="$list"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="notAppend">
			<xsl:choose>
				<xsl:when test="$duplicateFree = 'true'">
					<xsl:call-template name="x1f:List.containsValue">
						<xsl:with-param name="list" select="$list"/>
						<xsl:with-param name="value">
							<xsl:call-template name="x1f:List.Entry.getValue">
								<xsl:with-param name="entry" select="$entry"/>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$duplicateFree"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="$list"/>
		<xsl:if test="$notAppend = 'false'">
			<xsl:value-of select="$entry"/>
		</xsl:if>
	</xsl:template>

	<!--
		This template appends a new value to the list's end.
		@param  list  - The list to that the value shall be added.
		@param  value - The value that shall be added.
		@return The new list.
	-->
	<xsl:template name="x1f:List.appendValue">
		<xsl:param name="list"/>
		<xsl:param name="value"/>
		<xsl:call-template name="x1f:List.appendEntry">
			<xsl:with-param name="list" select="$list"/>
			<xsl:with-param name="entry">
				<xsl:call-template name="x1f:List.Entry.createEntry">
					<xsl:with-param name="value" select="$value"/>
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<!--
		This template appends a list of entries to another list's end.
		@param  list    - The list to that the values shall be added.
		@param  entries - The list of entries that shall be added.
		@return The new list.
	-->
	<xsl:template name="x1f:List.appendEntries">
		<xsl:param name="list"/>
		<xsl:param name="entries"/>
		<xsl:call-template name="x1f:List.appendList">
			<xsl:with-param name="list1" select="$list"/>
			<xsl:with-param name="list2">
				<xsl:call-template name="x1f:List.createEmptyList"/>
				<xsl:value-of select="$entries"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!--
		This template appends a list to another list's end.
		@param  list1 - The list to that the values shall be added.
		@param  list2 - The list that shall be added.
		@return The new list.
	-->
	<xsl:template name="x1f:List.appendList">
		<xsl:param name="list1"/>
		<xsl:param name="list2"/>
		<xsl:variable name="duplicateFree">
			<xsl:call-template name="x1f:List.isDuplicateFree">
				<xsl:with-param name="list" select="$list1"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="empty">
			<xsl:call-template name="x1f:List.isEmpty">
				<xsl:with-param name="list" select="$list2"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$duplicateFree = 'true' and $empty = 'false'">
				<xsl:variable name="list2.withFirstIndex">
					<xsl:call-template name="x1f:List.firstIndex">
						<xsl:with-param name="list" select="$list2"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:call-template name="x1f:List.appendList">
					<xsl:with-param name="list1">
						<xsl:call-template name="x1f:List.appendValue">
							<xsl:with-param name="list" select="$list1"/>
							<xsl:with-param name="value">
								<xsl:call-template name="x1f:List.getValue">
									<xsl:with-param name="list" select="$list2.withFirstIndex"/>
								</xsl:call-template>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:with-param>
					<xsl:with-param name="list2">
						<xsl:call-template name="x1f:List.removeValue">
							<xsl:with-param name="list" select="$list2.withFirstIndex"/>
						</xsl:call-template>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$list1"/>
				<xsl:call-template name="x1f:List.getBody">
					<xsl:with-param name="list" select="$list2"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		This template inserts a new value after the the list's current index.
		If the index is 0 the new value is added as the list's first value.
		@param  list  - The list to that the value shall added.
		@param  value - The value that shall be added.
		@return The new list.
	-->
	<xsl:template name="x1f:List.insertValue">
		<xsl:param name="list"/>
		<xsl:param name="value"/>
		<xsl:variable name="duplicateFree">
			<xsl:call-template name="x1f:List.isDuplicateFree">
				<xsl:with-param name="list" select="$list"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="notAppend">
			<xsl:choose>
				<xsl:when test="$duplicateFree = 'true'">
					<xsl:call-template name="x1f:List.containsValue">
						<xsl:with-param name="list" select="$list"/>
						<xsl:with-param name="value" select="$value"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$duplicateFree"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$notAppend = 'false'">
				<xsl:variable name="index">
					<xsl:call-template name="x1f:List.getIndex">
						<xsl:with-param name="list" select="$list"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="body">
					<xsl:call-template name="x1f:List.getBody">
						<xsl:with-param name="list" select="$list"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="$index > 0">
						<xsl:variable name="index2">
							<xsl:call-template name="x1f:List.getIndex">
								<xsl:with-param name="list">
									<xsl:call-template name="x1f:List.nextIndex">
										<xsl:with-param name="list" select="$list"/>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:variable>
						<xsl:choose>
							<xsl:when test="$index2 > 0">
								<xsl:value-of select="substring-before($list,substring($body,$index2))"/>
								<xsl:value-of select="concat('#',string-length($value),':',$value)"/>
								<xsl:value-of select="substring($body,$index2)"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:call-template name="x1f:List.appendValue">
									<xsl:with-param name="list" select="$list"/>
									<xsl:with-param name="value" select="$value"/>
								</xsl:call-template>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="x1f:List.appendList">
							<xsl:with-param name="list1">
								<xsl:call-template name="x1f:List.appendValue">
									<xsl:with-param name="list">
										<xsl:call-template name="x1f:List.createEmptyList">
											<xsl:with-param name="duplicate-free">
												<xsl:call-template name="x1f:List.isDuplicateFree">
													<xsl:with-param name="list" select="$list"/>
												</xsl:call-template>
											</xsl:with-param>
										</xsl:call-template>
									</xsl:with-param>
									<xsl:with-param name="value" select="$value"/>
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="list2" select="$list"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$list"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!--
		This template inserts a list after the the list's current index. If the
		index is 0 the new values are added as the list's first values.
		@param  list1 - The list to that the values shall be added.
		@param  lsit2 - The list that shall be added.
		@return The new list.
	-->
	<xsl:template name="x1f:List.insertList">
		<xsl:param name="list1"/>
		<xsl:param name="list2"/>
		<xsl:variable name="empty">
			<xsl:call-template name="x1f:List.isEmpty">
				<xsl:with-param name="list" select="$list2"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$empty = 'false'">
				<xsl:variable name="list2.withFirstIndex">
					<xsl:call-template name="x1f:List.firstIndex">
						<xsl:with-param name="list" select="$list2"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="body">
					<xsl:call-template name="x1f:List.getBody">
						<xsl:with-param name="list" select="$list1"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="list1.withValue">
					<xsl:call-template name="x1f:List.insertValue">
						<xsl:with-param name="list" select="$list1"/>
						<xsl:with-param name="value">
							<xsl:call-template name="x1f:List.getValue">
								<xsl:with-param name="list" select="$list2.withFirstIndex"/>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="newBody">
					<xsl:call-template name="x1f:List.getBody">
						<xsl:with-param name="list">
							<xsl:call-template name="x1f:List.insertList">
								<xsl:with-param name="list1">
									<xsl:choose>
										<xsl:when test="$list1 = $list1.withValue">
											<xsl:value-of select="$list1"/>
										</xsl:when>
										<xsl:otherwise>											
											<xsl:call-template name="x1f:List.nextIndex">
												<xsl:with-param name="list" select="$list1.withValue"/>
											</xsl:call-template>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:with-param>
								<xsl:with-param name="list2">
									<xsl:call-template name="x1f:List.removeValue">
										<xsl:with-param name="list" select="$list2.withFirstIndex"/>
									</xsl:call-template>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:variable>
				<xsl:value-of select="concat(substring-before($list1,$body),$newBody)"/>
			</xsl:when>	
			<xsl:otherwise>
				<xsl:value-of select="$list1"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>	
	
	<!--
		This template removes the list entry identified by the list's index and
		sets the index to the next entry. If the last entry was removes, the
		the index is set to 0.
		@param  list - The list whose current value shall be removed.
		@return The modified list.
	-->
	<xsl:template name="x1f:List.removeValue">
		<xsl:param name="list"/>
		<xsl:variable name="index">
			<xsl:call-template name="x1f:List.getIndex">
				<xsl:with-param name="list" select="$list"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="body">
			<xsl:call-template name="x1f:List.getBody">
				<xsl:with-param name="list" select="$list"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$index > 0">
				<xsl:variable name="length" select="substring-after(substring-before(substring($body,$index),':'),'#')"/>
				<xsl:variable name="body2" select="concat(substring($body,1,$index - 1),substring($body,$index + 1 + string-length($length) + 1 + $length))"/>
				<xsl:variable name="list2" select="concat(substring-before($list,$body),$body2)"/>
				<xsl:choose>
					<xsl:when test="$index > string-length($body2)">
						<xsl:call-template name="x1f:List.resetIndex">
							<xsl:with-param name="list" select="$list2"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$list2"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$list"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--########################-->
	<!--### result functions ###-->
	<!--########################-->

	<!--
		This template return the value of the list entry identified by the
		list's index. If the index is 0 the empty string is returned.
		@param  list - The list whose current value shall be returned.
		@return The list entry's value.
	-->
	<xsl:template name="x1f:List.getValue">
		<xsl:param name="list"/>
		<xsl:variable name="index">
			<xsl:call-template name="x1f:List.getIndex">
				<xsl:with-param name="list" select="$list"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="body">
			<xsl:call-template name="x1f:List.getBody">
				<xsl:with-param name="list" select="$list"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:if test="$index > 0">
			<xsl:variable name="length" select="substring-after(substring-before(substring($body,$index),':'),'#')"/>
			<xsl:value-of select="substring(substring-after(substring($body,$index),':'),1,$length)"/>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>