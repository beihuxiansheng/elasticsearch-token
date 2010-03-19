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
name|search
operator|.
name|highlight
operator|.
name|HighlightField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
operator|.
name|json
operator|.
name|ToJson
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
name|ToJson
extends|,
name|Iterable
argument_list|<
name|SearchHitField
argument_list|>
block|{
comment|/**      * The index of the hit.      */
DECL|method|index
name|String
name|index
parameter_list|()
function_decl|;
comment|/**      * The id of the document.      */
DECL|method|id
name|String
name|id
parameter_list|()
function_decl|;
comment|/**      * The type of the document.      */
DECL|method|type
name|String
name|type
parameter_list|()
function_decl|;
comment|/**      * The source of the document (can be<tt>null</tt>).      */
DECL|method|source
name|byte
index|[]
name|source
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
comment|/**      * The shard of the search hit.      */
DECL|method|shard
name|SearchShardTarget
name|shard
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

