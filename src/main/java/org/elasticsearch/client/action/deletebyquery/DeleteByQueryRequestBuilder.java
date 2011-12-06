begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.action.deletebyquery
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
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
name|ActionListener
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
name|WriteConsistencyLevel
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
name|deletebyquery
operator|.
name|DeleteByQueryRequest
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
name|deletebyquery
operator|.
name|DeleteByQueryResponse
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
name|replication
operator|.
name|ReplicationType
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
name|Client
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
name|action
operator|.
name|support
operator|.
name|BaseRequestBuilder
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
comment|/**  *  */
end_comment

begin_class
DECL|class|DeleteByQueryRequestBuilder
specifier|public
class|class
name|DeleteByQueryRequestBuilder
extends|extends
name|BaseRequestBuilder
argument_list|<
name|DeleteByQueryRequest
argument_list|,
name|DeleteByQueryResponse
argument_list|>
block|{
DECL|method|DeleteByQueryRequestBuilder
specifier|public
name|DeleteByQueryRequestBuilder
parameter_list|(
name|Client
name|client
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|,
operator|new
name|DeleteByQueryRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * The indices the delete by query will run against.      */
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
comment|/**      * The query source to execute.      *      * @see org.elasticsearch.index.query.QueryBuilders      */
DECL|method|setQuery
specifier|public
name|DeleteByQueryRequestBuilder
name|setQuery
parameter_list|(
name|QueryBuilder
name|queryBuilder
parameter_list|)
block|{
name|request
operator|.
name|query
argument_list|(
name|queryBuilder
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The query source to execute. It is preferable to use either {@link #setQuery(byte[])}      * or {@link #setQuery(org.elasticsearch.index.query.QueryBuilder)}.      */
DECL|method|setQuery
specifier|public
name|DeleteByQueryRequestBuilder
name|setQuery
parameter_list|(
name|String
name|querySource
parameter_list|)
block|{
name|request
operator|.
name|query
argument_list|(
name|querySource
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The query source to execute in the form of a map.      */
DECL|method|setQuery
specifier|public
name|DeleteByQueryRequestBuilder
name|setQuery
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|querySource
parameter_list|)
block|{
name|request
operator|.
name|query
argument_list|(
name|querySource
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The query source to execute in the form of a builder.      */
DECL|method|setQuery
specifier|public
name|DeleteByQueryRequestBuilder
name|setQuery
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|)
block|{
name|request
operator|.
name|query
argument_list|(
name|builder
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The query source to execute.      */
DECL|method|setQuery
specifier|public
name|DeleteByQueryRequestBuilder
name|setQuery
parameter_list|(
name|byte
index|[]
name|querySource
parameter_list|)
block|{
name|request
operator|.
name|query
argument_list|(
name|querySource
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The query source to execute.      */
DECL|method|setQuery
specifier|public
name|DeleteByQueryRequestBuilder
name|setQuery
parameter_list|(
name|byte
index|[]
name|querySource
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|boolean
name|unsafe
parameter_list|)
block|{
name|request
operator|.
name|query
argument_list|(
name|querySource
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|unsafe
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * A timeout to wait if the delete by query operation can't be performed immediately. Defaults to<tt>1m</tt>.      */
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
comment|/**      * A timeout to wait if the delete by query operation can't be performed immediately. Defaults to<tt>1m</tt>.      */
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
comment|/**      * The replication type to use with this operation.      */
DECL|method|setReplicationType
specifier|public
name|DeleteByQueryRequestBuilder
name|setReplicationType
parameter_list|(
name|ReplicationType
name|replicationType
parameter_list|)
block|{
name|request
operator|.
name|replicationType
argument_list|(
name|replicationType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The replication type to use with this operation.      */
DECL|method|setReplicationType
specifier|public
name|DeleteByQueryRequestBuilder
name|setReplicationType
parameter_list|(
name|String
name|replicationType
parameter_list|)
block|{
name|request
operator|.
name|replicationType
argument_list|(
name|replicationType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setConsistencyLevel
specifier|public
name|DeleteByQueryRequestBuilder
name|setConsistencyLevel
parameter_list|(
name|WriteConsistencyLevel
name|consistencyLevel
parameter_list|)
block|{
name|request
operator|.
name|consistencyLevel
argument_list|(
name|consistencyLevel
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the listener be called on a separate thread if needed.      */
DECL|method|setListenerThreaded
specifier|public
name|DeleteByQueryRequestBuilder
name|setListenerThreaded
parameter_list|(
name|boolean
name|threadedListener
parameter_list|)
block|{
name|request
operator|.
name|listenerThreaded
argument_list|(
name|threadedListener
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|ActionListener
argument_list|<
name|DeleteByQueryResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|client
operator|.
name|deleteByQuery
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

