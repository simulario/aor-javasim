<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="Map.xsl"?>

<!--
    This stylesheet provides a string based map and some basic map
    operations.
    @autor   Thomas Grundmann
    @version 1.0
    @created 2009-10-06
-->
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:x1f="http://www.informatik.tu-cottbus.de/~tgrundm1/projects/xslt-framework/xslt-1.0/">
    
    <xsl:import href="Pair.xsl"/>
    <xsl:import href="List.xsl"/>
    
    <!--####################-->
    <!--### map creation ###-->
    <!--####################-->
    
    <!--
        This templates creates an empty map.
        @return An empty map.
    -->
    <xsl:template name="x1f:Map.createEmptyMap">
        <xsl:text>map:false#</xsl:text>
        <xsl:call-template name="x1f:Pair.createPair">
            <xsl:with-param name="value1">
                <xsl:call-template name="x1f:List.createEmptyList">
                    <xsl:with-param name="duplicate-free" select="true()"/>
                </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="value2">
                <xsl:call-template name="x1f:List.createEmptyList"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
    
    <!--#########################-->
    <!--### support functions ###-->
    <!--#########################-->
    
    <!--
        This template returns a map's body.
        @param  map - The map whose body shall be returned.
        @return The map's body.
    -->
    <xsl:template name="x1f:Map.getBody">
        <xsl:param name="map"/>
        <xsl:value-of select="substring-after($map,'#')"/>
    </xsl:template>
    
    <!--
        This template returns a map's processing state.
        @param  map - The map whose processing state shall be returned.
        @return 'true' if the map is in local processing, othterwise 'false'.
    -->
    <xsl:template name="x1f:Map.getState">
        <xsl:param name="map"/>
        <xsl:value-of select="substring-after(substring-before($map,'#'),'map:')"/>
    </xsl:template>
    
    <!--######################-->
    <!--### test functions ###-->
    <!--######################-->
    
    <!--
        This template checks if a given key exists.
        @param  map - The map that shall be checked.
        @param  key - The key that is searched.
        @return 'true' if the key is already in the map, otherwise 'false'.
    -->
    <xsl:template name="x1f:Map.containsKey">
        <xsl:param name="map"/>
        <xsl:param name="key"/>
        <xsl:call-template name="x1f:List.containsValue">
            <xsl:with-param name="list">
                <xsl:call-template name="x1f:Pair.getValue1">
                    <xsl:with-param name="pair">
                        <xsl:call-template name="x1f:Map.getBody">
                            <xsl:with-param name="map" select="$map"/>
                        </xsl:call-template>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="value" select="$key"/>
        </xsl:call-template>
    </xsl:template>
    
	<!--########################-->
	<!--### map manipulation ###-->
	<!--########################-->

	<!--
		This template adds a new key value pair to the map. If the key already
		exists the corresponding value will be updated.
		@param  map   - The map to that the key value pair shall be added.
		@param  key   - The key that shall be added.
		@param  value - The value that shall be added.
		@return The new map.
	-->
	<xsl:template name="x1f:Map.putEntry">
		<xsl:param name="map"/>
		<xsl:param name="key"/>
	    <xsl:param name="value"/>
	    
	    <xsl:variable name="state">
	        <xsl:call-template name="x1f:Map.getState">
	            <xsl:with-param name="map" select="$map"/>
	        </xsl:call-template>
	    </xsl:variable>

	    <xsl:variable name="body">
	        <xsl:call-template name="x1f:Map.getBody">
	            <xsl:with-param name="map" select="$map"/>
	        </xsl:call-template>
	    </xsl:variable>
	    
	    <xsl:choose>
	        <xsl:when test="$state = 'false'">
	            
	            <!-- add key to key set (if new) and recall the template for value update -->
	            <xsl:call-template name="x1f:Map.putEntry">
	                <xsl:with-param name="map">
	                    <xsl:text>map:true#</xsl:text>
	                    <xsl:call-template name="x1f:Pair.createPair">
	                        <xsl:with-param name="value1">
	                            <xsl:call-template name="x1f:List.appendValue">
	                                <xsl:with-param name="list">
	                                    <xsl:call-template name="x1f:Pair.getValue1">
	                                        <xsl:with-param name="pair" select="$body"/>
	                                    </xsl:call-template>
	                                </xsl:with-param>
	                                <xsl:with-param name="value" select="$key"/>
	                            </xsl:call-template>
	                        </xsl:with-param>
	                        <xsl:with-param name="value2">
	                            <xsl:call-template name="x1f:Pair.getValue2">
	                                <xsl:with-param name="pair" select="$body"/>
	                            </xsl:call-template>
                            </xsl:with-param>
	                    </xsl:call-template>
	                </xsl:with-param>
	                <xsl:with-param name="key" select="$key"/>
	                <xsl:with-param name="value" select="$value"/>
	            </xsl:call-template>
	        </xsl:when>
	        
	        <!-- update the value -->
	        <xsl:otherwise>
	            <xsl:variable name="keys">
	                <xsl:call-template name="x1f:List.nextIndex">
	                    <xsl:with-param name="list">
	                        <xsl:call-template name="x1f:Pair.getValue1">
	                            <xsl:with-param name="pair" select="$body"/>
	                        </xsl:call-template>
	                    </xsl:with-param>
	                </xsl:call-template>
	            </xsl:variable>
	            <xsl:variable name="key2">
	                <xsl:call-template name="x1f:List.getValue">
	                    <xsl:with-param name="list" select="$keys"/>
	                </xsl:call-template>
	            </xsl:variable>
	            <xsl:choose>
	                
	                <!-- key found -> update the value -->
	                <xsl:when test="$key = $key2">
	                    <xsl:variable name="oldValues">
	                        <xsl:call-template name="x1f:Pair.getValue2">
	                            <xsl:with-param name="pair" select="$body"/>
	                        </xsl:call-template>
	                    </xsl:variable>
	                    <xsl:variable name="newValues">
	                        <xsl:call-template name="x1f:List.insertValue">
	                            <xsl:with-param name="list" select="$oldValues"/>
	                            <xsl:with-param name="value" select="$value"/>
	                        </xsl:call-template>
	                    </xsl:variable>
	                    <xsl:variable name="oldValues.hasNext">
	                        <xsl:call-template name="x1f:List.hasNext">
	                            <xsl:with-param name="list" select="$oldValues"/>
	                        </xsl:call-template>
	                    </xsl:variable>  	             
	                    
	                    <!-- return the updated map -->
	                    <xsl:text>map:false#</xsl:text>
	                    <xsl:call-template name="x1f:Pair.createPair">
	                        <xsl:with-param name="value1">
	                            <xsl:call-template name="x1f:List.resetIndex">
	                                <xsl:with-param name="list" select="$keys"/>
	                            </xsl:call-template>
	                        </xsl:with-param>
	                        <xsl:with-param name="value2">
	                            <xsl:call-template name="x1f:List.resetIndex">
	                                <xsl:with-param name="list">
	                                    <xsl:choose>
	                                        <xsl:when test="$oldValues.hasNext = 'true'">
	                                            <xsl:call-template name="x1f:List.removeValue">
	                                                <xsl:with-param name="list">
	                                                    <xsl:call-template name="x1f:List.nextIndex">
	                                                        <xsl:with-param name="list">
	                                                            <xsl:call-template name="x1f:List.nextIndex">
	                                                                <xsl:with-param name="list" select="$newValues"/>
	                                                            </xsl:call-template>
	                                                        </xsl:with-param>
	                                                    </xsl:call-template>                                                  
	                                                </xsl:with-param>
	                                            </xsl:call-template>
	                                        </xsl:when>
	                                        <xsl:otherwise>
	                                            <xsl:value-of select="$newValues"/>
	                                        </xsl:otherwise>
	                                    </xsl:choose>
	                                </xsl:with-param>
	                            </xsl:call-template>
	                        </xsl:with-param>
	                    </xsl:call-template>
	                </xsl:when>
	                
	                <!-- key not found -> investigate the next key value pair -->
	                <xsl:otherwise>
	                    <xsl:call-template name="x1f:Map.putEntry">
	                        <xsl:with-param name="map">
	                            <xsl:text>map:true#</xsl:text>
	                            <xsl:call-template name="x1f:Pair.createPair">
	                                <xsl:with-param name="value1" select="$keys"/>	                                
	                                <xsl:with-param name="value2">
	                                    <xsl:call-template name="x1f:List.nextIndex">
	                                        <xsl:with-param name="list">
	                                            <xsl:call-template name="x1f:Pair.getValue2">
	                                                <xsl:with-param name="pair" select="$body"/>
	                                            </xsl:call-template>
	                                        </xsl:with-param>
	                                    </xsl:call-template>
	                                </xsl:with-param>
	                            </xsl:call-template>
	                        </xsl:with-param>
	                        <xsl:with-param name="key" select="$key"/>
	                        <xsl:with-param name="value" select="$value"/>
	                    </xsl:call-template> 
	                </xsl:otherwise>
	            </xsl:choose>
	        </xsl:otherwise>
	    </xsl:choose>
	</xsl:template>
    
    <!--
        This template removes a key value pair, identified by the key, from the map.
        @param  map   - The map from that the key value pair shall be removed.
        @param  key   - The key that shall be removed.
        @return The modified map.
    -->
    <xsl:template name="x1f:Map.removeEntry">
        <xsl:param name="map"/>
        <xsl:param name="key"/>
        
        <xsl:variable name="state">
            <xsl:call-template name="x1f:Map.getState">
                <xsl:with-param name="map" select="$map"/>
            </xsl:call-template>
        </xsl:variable>
        
        <xsl:variable name="body">
            <xsl:call-template name="x1f:Map.getBody">
                <xsl:with-param name="map" select="$map"/>
            </xsl:call-template>
        </xsl:variable>
        
        <xsl:choose>
            <xsl:when test="$state = 'false'">
                
                <xsl:variable name="keys">
                    <xsl:call-template name="x1f:List.firstIndex">
                        <xsl:with-param name="list">
                            <xsl:call-template name="x1f:Pair.getValue1">
                                <xsl:with-param name="pair" select="$body"/>
                            </xsl:call-template>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:variable name="values">
                    <xsl:call-template name="x1f:List.firstIndex">
                        <xsl:with-param name="list">
                            <xsl:call-template name="x1f:Pair.getValue2">
                                <xsl:with-param name="pair" select="$body"/>
                            </xsl:call-template>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:choose>
                    <!-- If the key could be part of the map, call the template in modification mode. -->
                    <xsl:when test="contains($keys,$key)">
                        <xsl:call-template name="x1f:Map.removeEntry">
                            <xsl:with-param name="map">
                                <xsl:text>map:true#</xsl:text>
                                <xsl:call-template name="x1f:Pair.createPair">
                                    <xsl:with-param name="value1" select="$keys"/>
                                    <xsl:with-param name="value2" select="$values"/>
                                </xsl:call-template>
                            </xsl:with-param>
                            <xsl:with-param name="key" select="$key"/>
                        </xsl:call-template>
                    </xsl:when>
                    <!-- Otherwise (there is no possibility of being the key part of the map), return the unmodified map. -->
                    <xsl:otherwise>
                        <xsl:value-of select="$map"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            
            <!-- Modify the map. -->
            <xsl:otherwise>
                
                <xsl:variable name="keys">
                    <xsl:call-template name="x1f:Pair.getValue1">
                        <xsl:with-param name="pair" select="$body"/>
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:variable name="keys.hasNext">
                    <xsl:call-template name="x1f:List.hasNext">
                        <xsl:with-param name="list" select="$keys"/>
                    </xsl:call-template>
                </xsl:variable>
                                
                <xsl:variable name="key2">
                    <xsl:call-template name="x1f:List.getValue">
                        <xsl:with-param name="list" select="$keys"/>
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:variable name="values">
                    <xsl:call-template name="x1f:Pair.getValue2">
                        <xsl:with-param name="pair" select="$body"/>
                    </xsl:call-template>
                </xsl:variable>
                
                <xsl:choose>
                    
                    <!-- key found -> remove it and its value -->
                    <xsl:when test="$key = $key2">
                        <xsl:text>map:false#</xsl:text>
                        <xsl:call-template name="x1f:Pair.createPair">
                            <xsl:with-param name="value1">
                                <xsl:call-template name="x1f:List.resetIndex">
                                    <xsl:with-param name="list">
                                        <xsl:call-template name="x1f:List.removeValue">
                                            <xsl:with-param name="list" select="$keys"/>
                                        </xsl:call-template>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </xsl:with-param>
                            <xsl:with-param name="value2">
                                <xsl:call-template name="x1f:List.resetIndex">
                                    <xsl:with-param name="list">
                                        <xsl:call-template name="x1f:List.removeValue">
                                            <xsl:with-param name="list" select="$values"/>
                                        </xsl:call-template>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </xsl:with-param>
                        </xsl:call-template>
                    </xsl:when>
                    
                    <!-- key not found -> investigate the next key value pair -->
                    <xsl:when test="$keys.hasNext = 'true'">
                        <xsl:call-template name="x1f:Map.removeEntry">
                            <xsl:with-param name="map">
                                <xsl:text>map:true#</xsl:text>
                                <xsl:call-template name="x1f:Pair.createPair">
                                    <xsl:with-param name="value1">
                                        <xsl:call-template name="x1f:List.nextIndex">
                                            <xsl:with-param name="list" select="$keys"/>
                                        </xsl:call-template>
                                    </xsl:with-param>	                                
                                    <xsl:with-param name="value2">
                                        <xsl:call-template name="x1f:List.nextIndex">
                                            <xsl:with-param name="list" select="$values"/>                                            
                                        </xsl:call-template>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </xsl:with-param>
                            <xsl:with-param name="key" select="$key"/>
                        </xsl:call-template>                         
                    </xsl:when>
                    
                    <!-- key not found and no other pair available -->
                    <xsl:otherwise>
                        <xsl:text>map:false#</xsl:text>
                        <xsl:call-template name="x1f:Pair.createPair">
                            <xsl:with-param name="value1">
                                <xsl:call-template name="x1f:List.resetIndex">
                                    <xsl:with-param name="list" select="$keys"/>
                                </xsl:call-template>
                            </xsl:with-param>	                                
                            <xsl:with-param name="value2">
                                <xsl:call-template name="x1f:List.resetIndex">
                                    <xsl:with-param name="list" select="$values"/>                                            
                                </xsl:call-template>
                            </xsl:with-param>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!--########################-->
    <!--### result functions ###-->
    <!--########################-->
    
    <!--
        This template return a map's value for a give key.
        @param  map - The map whose value shall be returned.
        @param  key - The key whose value shall be returned.
        @return The map's value or the empty string if the key was not found.
    -->
    <xsl:template name="x1f:Map.getValue">
        <xsl:param name="map"/>
        <xsl:param name="key"/>
        <xsl:variable name="state">
            <xsl:call-template name="x1f:Map.getState">
                <xsl:with-param name="map" select="$map"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="body">
            <xsl:call-template name="x1f:Map.getBody">
                <xsl:with-param name="map" select="$map"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="$state = 'false'">
                <xsl:variable name="keys">
                    <xsl:call-template name="x1f:List.firstIndex">
                        <xsl:with-param name="list">
                            <xsl:call-template name="x1f:Pair.getValue1">
                                <xsl:with-param name="pair" select="$body"/>
                            </xsl:call-template>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="values">
                    <xsl:call-template name="x1f:List.firstIndex">
                        <xsl:with-param name="list">
                            <xsl:call-template name="x1f:Pair.getValue2">
                                <xsl:with-param name="pair" select="$body"/>
                            </xsl:call-template>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="contains($keys,$key)">
                        <xsl:call-template name="x1f:Map.getValue">
                            <xsl:with-param name="map">
                                <xsl:text>map:true#</xsl:text>
                                <xsl:call-template name="x1f:Pair.createPair">
                                    <xsl:with-param name="value1" select="$keys"/>
                                    <xsl:with-param name="value2" select="$values"/>
                                </xsl:call-template>
                            </xsl:with-param>
                            <xsl:with-param name="key" select="$key"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="''"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="keys">
                    <xsl:call-template name="x1f:Pair.getValue1">
                        <xsl:with-param name="pair" select="$body"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="keys.hasNext">
                    <xsl:call-template name="x1f:List.hasNext">
                        <xsl:with-param name="list" select="$keys"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="key2">
                    <xsl:call-template name="x1f:List.getValue">
                        <xsl:with-param name="list" select="$keys"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="values">
                    <xsl:call-template name="x1f:Pair.getValue2">
                        <xsl:with-param name="pair" select="$body"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="$key = $key2">
                        <xsl:call-template name="x1f:List.getValue">
                            <xsl:with-param name="list" select="$values"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$keys.hasNext = 'true'">
                        <xsl:call-template name="x1f:Map.getValue">
                            <xsl:with-param name="map">
                                <xsl:text>map:true#</xsl:text>
                                <xsl:call-template name="x1f:Pair.createPair">
                                    <xsl:with-param name="value1">
                                        <xsl:call-template name="x1f:List.nextIndex">
                                            <xsl:with-param name="list" select="$keys"/>
                                        </xsl:call-template>
                                    </xsl:with-param>	                                
                                    <xsl:with-param name="value2">
                                        <xsl:call-template name="x1f:List.nextIndex">
                                            <xsl:with-param name="list" select="$values"/>                                            
                                        </xsl:call-template>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </xsl:with-param>
                            <xsl:with-param name="key" select="$key"/>
                        </xsl:call-template>                         
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="''"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>