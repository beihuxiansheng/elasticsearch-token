begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|TransportActions
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
name|TransportIndicesReplicationOperationAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|block
operator|.
name|ClusterBlockLevel
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
name|inject
operator|.
name|Inject
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
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReferenceArray
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|TransportDeleteByQueryAction
specifier|public
class|class
name|TransportDeleteByQueryAction
extends|extends
name|TransportIndicesReplicationOperationAction
argument_list|<
name|DeleteByQueryRequest
argument_list|,
name|DeleteByQueryResponse
argument_list|,
name|IndexDeleteByQueryRequest
argument_list|,
name|IndexDeleteByQueryResponse
argument_list|,
name|ShardDeleteByQueryRequest
argument_list|,
name|ShardDeleteByQueryRequest
argument_list|,
name|ShardDeleteByQueryResponse
argument_list|>
block|{
annotation|@
name|Inject
DECL|method|TransportDeleteByQueryAction
specifier|public
name|TransportDeleteByQueryAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|TransportIndexDeleteByQueryAction
name|indexDeleteByQueryAction
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|,
name|indexDeleteByQueryAction
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newRequestInstance
specifier|protected
name|DeleteByQueryRequest
name|newRequestInstance
parameter_list|()
block|{
return|return
operator|new
name|DeleteByQueryRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newResponseInstance
specifier|protected
name|DeleteByQueryResponse
name|newResponseInstance
parameter_list|(
name|DeleteByQueryRequest
name|request
parameter_list|,
name|AtomicReferenceArray
name|indexResponses
parameter_list|)
block|{
name|DeleteByQueryResponse
name|response
init|=
operator|new
name|DeleteByQueryResponse
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indexResponses
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|IndexDeleteByQueryResponse
name|indexResponse
init|=
operator|(
name|IndexDeleteByQueryResponse
operator|)
name|indexResponses
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexResponse
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|indices
argument_list|()
operator|.
name|put
argument_list|(
name|indexResponse
operator|.
name|index
argument_list|()
argument_list|,
name|indexResponse
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|accumulateExceptions
specifier|protected
name|boolean
name|accumulateExceptions
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|transportAction
specifier|protected
name|String
name|transportAction
parameter_list|()
block|{
return|return
name|TransportActions
operator|.
name|DELETE_BY_QUERY
return|;
block|}
annotation|@
name|Override
DECL|method|checkBlock
specifier|protected
name|void
name|checkBlock
parameter_list|(
name|DeleteByQueryRequest
name|request
parameter_list|,
name|String
index|[]
name|concreteIndices
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
for|for
control|(
name|String
name|index
range|:
name|concreteIndices
control|)
block|{
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|indexBlockedRaiseException
argument_list|(
name|ClusterBlockLevel
operator|.
name|WRITE
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|newIndexRequestInstance
specifier|protected
name|IndexDeleteByQueryRequest
name|newIndexRequestInstance
parameter_list|(
name|DeleteByQueryRequest
name|request
parameter_list|,
name|String
name|index
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|routing
parameter_list|)
block|{
name|String
index|[]
name|filteringAliases
init|=
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|filteringAliases
argument_list|(
name|index
argument_list|,
name|request
operator|.
name|indices
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|IndexDeleteByQueryRequest
argument_list|(
name|request
argument_list|,
name|index
argument_list|,
name|routing
argument_list|,
name|filteringAliases
argument_list|)
return|;
block|}
block|}
end_class

end_unit

