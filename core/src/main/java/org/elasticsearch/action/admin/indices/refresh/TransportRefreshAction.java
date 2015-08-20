begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.refresh
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|refresh
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
name|ActionWriteResponse
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
name|ShardOperationFailedException
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
name|ActionFilters
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
name|ReplicationRequest
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
name|TransportBroadcastReplicationAction
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
name|metadata
operator|.
name|IndexNameExpressionResolver
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
name|index
operator|.
name|shard
operator|.
name|ShardId
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
name|List
import|;
end_import

begin_comment
comment|/**  * Refresh action.  */
end_comment

begin_class
DECL|class|TransportRefreshAction
specifier|public
class|class
name|TransportRefreshAction
extends|extends
name|TransportBroadcastReplicationAction
argument_list|<
name|RefreshRequest
argument_list|,
name|RefreshResponse
argument_list|,
name|ReplicationRequest
argument_list|,
name|ActionWriteResponse
argument_list|>
block|{
annotation|@
name|Inject
DECL|method|TransportRefreshAction
specifier|public
name|TransportRefreshAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ActionFilters
name|actionFilters
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|,
name|TransportShardRefreshAction
name|shardRefreshAction
parameter_list|)
block|{
name|super
argument_list|(
name|RefreshAction
operator|.
name|NAME
argument_list|,
name|RefreshRequest
operator|.
name|class
argument_list|,
name|settings
argument_list|,
name|threadPool
argument_list|,
name|clusterService
argument_list|,
name|transportService
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|shardRefreshAction
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newShardResponse
specifier|protected
name|ActionWriteResponse
name|newShardResponse
parameter_list|()
block|{
return|return
operator|new
name|ActionWriteResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newShardRequest
specifier|protected
name|ReplicationRequest
name|newShardRequest
parameter_list|(
name|RefreshRequest
name|request
parameter_list|,
name|ShardId
name|shardId
parameter_list|)
block|{
return|return
operator|new
name|ReplicationRequest
argument_list|(
name|request
argument_list|)
operator|.
name|setShardId
argument_list|(
name|shardId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|RefreshResponse
name|newResponse
parameter_list|(
name|int
name|successfulShards
parameter_list|,
name|int
name|failedShards
parameter_list|,
name|int
name|totalNumCopies
parameter_list|,
name|List
argument_list|<
name|ShardOperationFailedException
argument_list|>
name|shardFailures
parameter_list|)
block|{
return|return
operator|new
name|RefreshResponse
argument_list|(
name|totalNumCopies
argument_list|,
name|successfulShards
argument_list|,
name|failedShards
argument_list|,
name|shardFailures
argument_list|)
return|;
block|}
block|}
end_class

end_unit
