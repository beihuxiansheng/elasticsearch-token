begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Explanation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|Streamable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|ToXContent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|highlight
operator|.
name|HighlightField
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * A single search hit.  *  * @author kimchy (shay.banon)  * @see SearchHits  */
end_comment

begin_interface
DECL|interface|SearchHit
specifier|public
interface|interface
name|SearchHit
extends|extends
name|Streamable
extends|,
name|ToXContent
extends|,
name|Iterable
argument_list|<
name|SearchHitField
argument_list|>
block|{
comment|/**      * The score.      */
DECL|method|score
name|float
name|score
parameter_list|()
function_decl|;
comment|/**      * The score.      */
DECL|method|getScore
name|float
name|getScore
parameter_list|()
function_decl|;
comment|/**      * The index of the hit.      */
DECL|method|index
name|String
name|index
parameter_list|()
function_decl|;
comment|/**      * The index of the hit.      */
DECL|method|getIndex
name|String
name|getIndex
parameter_list|()
function_decl|;
comment|/**      * The id of the document.      */
DECL|method|id
name|String
name|id
parameter_list|()
function_decl|;
comment|/**      * The id of the document.      */
DECL|method|getId
name|String
name|getId
parameter_list|()
function_decl|;
comment|/**      * The type of the document.      */
DECL|method|type
name|String
name|type
parameter_list|()
function_decl|;
comment|/**      * The type of the document.      */
DECL|method|getType
name|String
name|getType
parameter_list|()
function_decl|;
comment|/**      * The version of the hit.      */
DECL|method|version
name|long
name|version
parameter_list|()
function_decl|;
comment|/**      * The version of the hit.      */
DECL|method|getVersion
name|long
name|getVersion
parameter_list|()
function_decl|;
comment|/**      * The source of the document (can be<tt>null</tt>).      */
DECL|method|source
name|byte
index|[]
name|source
parameter_list|()
function_decl|;
comment|/**      * Is the source empty (not available) or not.      */
DECL|method|isSourceEmpty
name|boolean
name|isSourceEmpty
parameter_list|()
function_decl|;
comment|/**      * The source of the document as a map (can be<tt>null</tt>).      */
DECL|method|getSource
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getSource
parameter_list|()
function_decl|;
comment|/**      * The source of the document as string (can be<tt>null</tt>).      */
DECL|method|sourceAsString
name|String
name|sourceAsString
parameter_list|()
function_decl|;
comment|/**      * The source of the document as a map (can be<tt>null</tt>).      */
DECL|method|sourceAsMap
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|sourceAsMap
parameter_list|()
throws|throws
name|ElasticSearchParseException
function_decl|;
comment|/**      * If enabled, the explanation of the search hit.      */
DECL|method|explanation
name|Explanation
name|explanation
parameter_list|()
function_decl|;
comment|/**      * If enabled, the explanation of the search hit.      */
DECL|method|getExplanation
name|Explanation
name|getExplanation
parameter_list|()
function_decl|;
comment|/**      * The hit field matching the given field name.      */
DECL|method|field
specifier|public
name|SearchHitField
name|field
parameter_list|(
name|String
name|fieldName
parameter_list|)
function_decl|;
comment|/**      * A map of hit fields (from field name to hit fields) if additional fields      * were required to be loaded.      */
DECL|method|fields
name|Map
argument_list|<
name|String
argument_list|,
name|SearchHitField
argument_list|>
name|fields
parameter_list|()
function_decl|;
comment|/**      * A map of hit fields (from field name to hit fields) if additional fields      * were required to be loaded.      */
DECL|method|getFields
name|Map
argument_list|<
name|String
argument_list|,
name|SearchHitField
argument_list|>
name|getFields
parameter_list|()
function_decl|;
comment|/**      * A map of highlighted fields.      */
DECL|method|highlightFields
name|Map
argument_list|<
name|String
argument_list|,
name|HighlightField
argument_list|>
name|highlightFields
parameter_list|()
function_decl|;
comment|/**      * A map of highlighted fields.      */
DECL|method|getHighlightFields
name|Map
argument_list|<
name|String
argument_list|,
name|HighlightField
argument_list|>
name|getHighlightFields
parameter_list|()
function_decl|;
comment|/**      * An array of the sort values used.      */
DECL|method|sortValues
name|Object
index|[]
name|sortValues
parameter_list|()
function_decl|;
comment|/**      * An array of the sort values used.      */
DECL|method|getSortValues
name|Object
index|[]
name|getSortValues
parameter_list|()
function_decl|;
comment|/**      * The set of filter names the query matched. Mainly makes sense for OR filters.      */
DECL|method|matchedFilters
name|String
index|[]
name|matchedFilters
parameter_list|()
function_decl|;
comment|/**      * The set of filter names the query matched. Mainly makes sense for OR filters.      */
DECL|method|getMatchedFilters
name|String
index|[]
name|getMatchedFilters
parameter_list|()
function_decl|;
comment|/**      * The shard of the search hit.      */
DECL|method|shard
name|SearchShardTarget
name|shard
parameter_list|()
function_decl|;
comment|/**      * The shard of the search hit.      */
DECL|method|getShard
name|SearchShardTarget
name|getShard
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

