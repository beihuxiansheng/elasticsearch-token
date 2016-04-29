begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ElasticsearchParseException
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
name|bytes
operator|.
name|BytesReference
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
name|text
operator|.
name|Text
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
comment|/**  * A single search hit.  *  * @see SearchHits  */
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
comment|/**      * If this is a nested hit then nested reference information is returned otherwise<code>null</code> is returned.      */
DECL|method|getNestedIdentity
name|NestedIdentity
name|getNestedIdentity
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
comment|/**      * Returns bytes reference, also un compress the source if needed.      */
DECL|method|sourceRef
name|BytesReference
name|sourceRef
parameter_list|()
function_decl|;
comment|/**      * Returns bytes reference, also un compress the source if needed.      */
DECL|method|getSourceRef
name|BytesReference
name|getSourceRef
parameter_list|()
function_decl|;
comment|/**      * The source of the document (can be<tt>null</tt>). Note, its a copy of the source      * into a byte array, consider using {@link #sourceRef()} so there won't be a need to copy.      */
DECL|method|source
name|byte
index|[]
name|source
parameter_list|()
function_decl|;
comment|/**      * Is the source available or not. A source with no fields will return true. This will return false if {@code fields} doesn't contain      * {@code _source} or if source is disabled in the mapping.      */
DECL|method|hasSource
name|boolean
name|hasSource
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
comment|/**      * The source of the document as string (can be<tt>null</tt>).      */
DECL|method|getSourceAsString
name|String
name|getSourceAsString
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
name|ElasticsearchParseException
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
comment|/**      * The set of query and filter names the query matched with. Mainly makes sense for compound filters and queries.      */
DECL|method|matchedQueries
name|String
index|[]
name|matchedQueries
parameter_list|()
function_decl|;
comment|/**      * The set of query and filter names the query matched with. Mainly makes sense for compound filters and queries.      */
DECL|method|getMatchedQueries
name|String
index|[]
name|getMatchedQueries
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
comment|/**      * @return Inner hits or<code>null</code> if there are none      */
DECL|method|getInnerHits
name|Map
argument_list|<
name|String
argument_list|,
name|SearchHits
argument_list|>
name|getInnerHits
parameter_list|()
function_decl|;
comment|/**      * Encapsulates the nested identity of a hit.      */
DECL|interface|NestedIdentity
specifier|public
interface|interface
name|NestedIdentity
block|{
comment|/**          * Returns the nested field in the source this hit originates from          */
DECL|method|getField
specifier|public
name|Text
name|getField
parameter_list|()
function_decl|;
comment|/**          * Returns the offset in the nested array of objects in the source this hit          */
DECL|method|getOffset
specifier|public
name|int
name|getOffset
parameter_list|()
function_decl|;
comment|/**          * Returns the next child nested level if there is any, otherwise<code>null</code> is returned.          *          * In the case of mappings with multiple levels of nested object fields          */
DECL|method|getChild
specifier|public
name|NestedIdentity
name|getChild
parameter_list|()
function_decl|;
block|}
block|}
end_interface

end_unit

