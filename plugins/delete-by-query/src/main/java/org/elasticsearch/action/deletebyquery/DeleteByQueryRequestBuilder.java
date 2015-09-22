begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.deletebyquery
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|deletebyquery
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionRequestBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ListenableActionFuture
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|IndicesOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|QuerySourceBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|ElasticsearchClient
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
name|unit
operator|.
name|TimeValue
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
name|XContentBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilder
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
comment|/**  * Creates a new {@link DeleteByQueryRequestBuilder}  * @see DeleteByQueryRequest  */
end_comment

begin_class
DECL|class|DeleteByQueryRequestBuilder
specifier|public
class|class
name|DeleteByQueryRequestBuilder
extends|extends
name|ActionRequestBuilder
argument_list|<
name|DeleteByQueryRequest
argument_list|,
name|DeleteByQueryResponse
argument_list|,
name|DeleteByQueryRequestBuilder
argument_list|>
block|{
DECL|field|sourceBuilder
specifier|private
name|QuerySourceBuilder
name|sourceBuilder
decl_stmt|;
DECL|method|DeleteByQueryRequestBuilder
specifier|public
name|DeleteByQueryRequestBuilder
parameter_list|(
name|ElasticsearchClient
name|client
parameter_list|,
name|DeleteByQueryAction
name|action
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|,
name|action
argument_list|,
operator|new
name|DeleteByQueryRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setIndices
specifier|public
name|DeleteByQueryRequestBuilder
name|setIndices
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|request
operator|.
name|indices
argument_list|(
name|indices
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Specifies what type of requested indices to ignore and wildcard indices expressions.      *<p>      * For example indices that don't exist.      */
DECL|method|setIndicesOptions
specifier|public
name|DeleteByQueryRequestBuilder
name|setIndicesOptions
parameter_list|(
name|IndicesOptions
name|options
parameter_list|)
block|{
name|request
operator|.
name|indicesOptions
argument_list|(
name|options
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The query used to delete documents.      *      * @see org.elasticsearch.index.query.QueryBuilders      */
DECL|method|setQuery
specifier|public
name|DeleteByQueryRequestBuilder
name|setQuery
parameter_list|(
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|queryBuilder
parameter_list|)
block|{
name|sourceBuilder
argument_list|()
operator|.
name|setQuery
argument_list|(
name|queryBuilder
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The query binary used to delete documents.      */
DECL|method|setQuery
specifier|public
name|DeleteByQueryRequestBuilder
name|setQuery
parameter_list|(
name|BytesReference
name|queryBinary
parameter_list|)
block|{
name|sourceBuilder
argument_list|()
operator|.
name|setQuery
argument_list|(
name|queryBinary
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Constructs a new builder with a raw search query.      */
DECL|method|setQuery
specifier|public
name|DeleteByQueryRequestBuilder
name|setQuery
parameter_list|(
name|XContentBuilder
name|query
parameter_list|)
block|{
return|return
name|setQuery
argument_list|(
name|query
operator|.
name|bytes
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * A comma separated list of routing values to control the shards the action will be executed on.      */
DECL|method|setRouting
specifier|public
name|DeleteByQueryRequestBuilder
name|setRouting
parameter_list|(
name|String
name|routing
parameter_list|)
block|{
name|request
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The routing values to control the shards that the action will be executed on.      */
DECL|method|setRouting
specifier|public
name|DeleteByQueryRequestBuilder
name|setRouting
parameter_list|(
name|String
modifier|...
name|routing
parameter_list|)
block|{
name|request
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The source to execute. It is preferable to use either {@link #setSource(byte[])}      * or {@link #setQuery(QueryBuilder)}.      */
DECL|method|setSource
specifier|public
name|DeleteByQueryRequestBuilder
name|setSource
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|request
argument_list|()
operator|.
name|source
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The source to execute in the form of a map.      */
DECL|method|setSource
specifier|public
name|DeleteByQueryRequestBuilder
name|setSource
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
parameter_list|)
block|{
name|request
argument_list|()
operator|.
name|source
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The source to execute in the form of a builder.      */
DECL|method|setSource
specifier|public
name|DeleteByQueryRequestBuilder
name|setSource
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|)
block|{
name|request
argument_list|()
operator|.
name|source
argument_list|(
name|builder
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The source to execute.      */
DECL|method|setSource
specifier|public
name|DeleteByQueryRequestBuilder
name|setSource
parameter_list|(
name|byte
index|[]
name|source
parameter_list|)
block|{
name|request
argument_list|()
operator|.
name|source
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The source to execute.      */
DECL|method|setSource
specifier|public
name|DeleteByQueryRequestBuilder
name|setSource
parameter_list|(
name|BytesReference
name|source
parameter_list|)
block|{
name|request
argument_list|()
operator|.
name|source
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * An optional timeout to control how long the delete by query is allowed to take.      */
DECL|method|setTimeout
specifier|public
name|DeleteByQueryRequestBuilder
name|setTimeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
name|request
operator|.
name|timeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * An optional timeout to control how long the delete by query is allowed to take.      */
DECL|method|setTimeout
specifier|public
name|DeleteByQueryRequestBuilder
name|setTimeout
parameter_list|(
name|String
name|timeout
parameter_list|)
block|{
name|request
operator|.
name|timeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The types of documents the query will run against. Defaults to all types.      */
DECL|method|setTypes
specifier|public
name|DeleteByQueryRequestBuilder
name|setTypes
parameter_list|(
name|String
modifier|...
name|types
parameter_list|)
block|{
name|request
operator|.
name|types
argument_list|(
name|types
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|ListenableActionFuture
argument_list|<
name|DeleteByQueryResponse
argument_list|>
name|execute
parameter_list|()
block|{
if|if
condition|(
name|sourceBuilder
operator|!=
literal|null
condition|)
block|{
name|request
operator|.
name|source
argument_list|(
name|sourceBuilder
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|execute
argument_list|()
return|;
block|}
DECL|method|sourceBuilder
specifier|private
name|QuerySourceBuilder
name|sourceBuilder
parameter_list|()
block|{
if|if
condition|(
name|sourceBuilder
operator|==
literal|null
condition|)
block|{
name|sourceBuilder
operator|=
operator|new
name|QuerySourceBuilder
argument_list|()
expr_stmt|;
block|}
return|return
name|sourceBuilder
return|;
block|}
block|}
end_class

end_unit

