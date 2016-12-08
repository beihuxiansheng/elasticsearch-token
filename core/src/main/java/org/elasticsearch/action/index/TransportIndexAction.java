begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|index
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
name|bulk
operator|.
name|BulkItemResponse
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
name|bulk
operator|.
name|BulkRequest
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
name|bulk
operator|.
name|BulkResponse
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
name|bulk
operator|.
name|TransportBulkAction
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
name|bulk
operator|.
name|TransportShardBulkAction
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
name|WriteRequest
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
name|TransportWriteAction
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
name|action
operator|.
name|shard
operator|.
name|ShardStateAction
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
name|cluster
operator|.
name|service
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
name|IndexShard
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|IndicesService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|Task
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

begin_comment
comment|/**  * Performs the index operation.  *  * Allows for the following settings:  *<ul>  *<li><b>autoCreateIndex</b>: When set to<tt>true</tt>, will automatically create an index if one does not exists.  * Defaults to<tt>true</tt>.  *<li><b>allowIdGeneration</b>: If the id is set not, should it be generated. Defaults to<tt>true</tt>.  *</ul>  */
end_comment

begin_class
DECL|class|TransportIndexAction
specifier|public
class|class
name|TransportIndexAction
extends|extends
name|TransportWriteAction
argument_list|<
name|IndexRequest
argument_list|,
name|IndexRequest
argument_list|,
name|IndexResponse
argument_list|>
block|{
DECL|field|bulkAction
specifier|private
specifier|final
name|TransportBulkAction
name|bulkAction
decl_stmt|;
DECL|field|shardBulkAction
specifier|private
specifier|final
name|TransportShardBulkAction
name|shardBulkAction
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportIndexAction
specifier|public
name|TransportIndexAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|IndicesService
name|indicesService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ShardStateAction
name|shardStateAction
parameter_list|,
name|ActionFilters
name|actionFilters
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|,
name|TransportBulkAction
name|bulkAction
parameter_list|,
name|TransportShardBulkAction
name|shardBulkAction
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|IndexAction
operator|.
name|NAME
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|indicesService
argument_list|,
name|threadPool
argument_list|,
name|shardStateAction
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|IndexRequest
operator|::
operator|new
argument_list|,
name|IndexRequest
operator|::
operator|new
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|INDEX
argument_list|)
expr_stmt|;
name|this
operator|.
name|bulkAction
operator|=
name|bulkAction
expr_stmt|;
name|this
operator|.
name|shardBulkAction
operator|=
name|shardBulkAction
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|Task
name|task
parameter_list|,
specifier|final
name|IndexRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|IndexResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|BulkRequest
name|bulkRequest
init|=
operator|new
name|BulkRequest
argument_list|()
decl_stmt|;
name|bulkRequest
operator|.
name|add
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|bulkRequest
operator|.
name|setRefreshPolicy
argument_list|(
name|request
operator|.
name|getRefreshPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|bulkRequest
operator|.
name|timeout
argument_list|(
name|request
operator|.
name|timeout
argument_list|()
argument_list|)
expr_stmt|;
name|bulkRequest
operator|.
name|waitForActiveShards
argument_list|(
name|request
operator|.
name|waitForActiveShards
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|setRefreshPolicy
argument_list|(
name|WriteRequest
operator|.
name|RefreshPolicy
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|bulkAction
operator|.
name|execute
argument_list|(
name|task
argument_list|,
name|bulkRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|BulkResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|BulkResponse
name|bulkItemResponses
parameter_list|)
block|{
assert|assert
name|bulkItemResponses
operator|.
name|getItems
argument_list|()
operator|.
name|length
operator|==
literal|1
operator|:
literal|"expected only one item in bulk request"
assert|;
name|BulkItemResponse
name|bulkItemResponse
init|=
name|bulkItemResponses
operator|.
name|getItems
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|bulkItemResponse
operator|.
name|isFailed
argument_list|()
operator|==
literal|false
condition|)
block|{
name|IndexResponse
name|response
init|=
name|bulkItemResponse
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|bulkItemResponse
operator|.
name|getFailure
argument_list|()
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newResponseInstance
specifier|protected
name|IndexResponse
name|newResponseInstance
parameter_list|()
block|{
return|return
operator|new
name|IndexResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|shardOperationOnPrimary
specifier|protected
name|WritePrimaryResult
argument_list|<
name|IndexRequest
argument_list|,
name|IndexResponse
argument_list|>
name|shardOperationOnPrimary
parameter_list|(
name|IndexRequest
name|request
parameter_list|,
name|IndexShard
name|primary
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|shardBulkAction
operator|.
name|executeSingleItemBulkRequestOnPrimary
argument_list|(
name|request
argument_list|,
name|primary
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|shardOperationOnReplica
specifier|protected
name|WriteReplicaResult
argument_list|<
name|IndexRequest
argument_list|>
name|shardOperationOnReplica
parameter_list|(
name|IndexRequest
name|request
parameter_list|,
name|IndexShard
name|replica
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|shardBulkAction
operator|.
name|executeSingleItemBulkRequestOnReplica
argument_list|(
name|request
argument_list|,
name|replica
argument_list|)
return|;
block|}
block|}
end_class

end_unit

