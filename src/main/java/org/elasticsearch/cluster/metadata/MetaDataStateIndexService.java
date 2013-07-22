begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.metadata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|master
operator|.
name|MasterNodeOperationRequest
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
name|TimeoutClusterStateUpdateTask
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
name|ClusterBlock
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
name|cluster
operator|.
name|block
operator|.
name|ClusterBlocks
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
name|routing
operator|.
name|RoutingTable
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
name|routing
operator|.
name|allocation
operator|.
name|AllocationService
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
name|routing
operator|.
name|allocation
operator|.
name|RoutingAllocation
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
name|Priority
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
name|component
operator|.
name|AbstractComponent
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
name|index
operator|.
name|Index
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
name|IndexMissingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
comment|/**  *  */
end_comment

begin_class
DECL|class|MetaDataStateIndexService
specifier|public
class|class
name|MetaDataStateIndexService
extends|extends
name|AbstractComponent
block|{
DECL|field|INDEX_CLOSED_BLOCK
specifier|public
specifier|static
specifier|final
name|ClusterBlock
name|INDEX_CLOSED_BLOCK
init|=
operator|new
name|ClusterBlock
argument_list|(
literal|4
argument_list|,
literal|"index closed"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|RestStatus
operator|.
name|FORBIDDEN
argument_list|,
name|ClusterBlockLevel
operator|.
name|READ_WRITE
argument_list|)
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|allocationService
specifier|private
specifier|final
name|AllocationService
name|allocationService
decl_stmt|;
annotation|@
name|Inject
DECL|method|MetaDataStateIndexService
specifier|public
name|MetaDataStateIndexService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|AllocationService
name|allocationService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|allocationService
operator|=
name|allocationService
expr_stmt|;
block|}
DECL|method|closeIndex
specifier|public
name|void
name|closeIndex
parameter_list|(
specifier|final
name|Request
name|request
parameter_list|,
specifier|final
name|Listener
name|listener
parameter_list|)
block|{
if|if
condition|(
name|request
operator|.
name|indices
operator|==
literal|null
operator|||
name|request
operator|.
name|indices
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Index name is required"
argument_list|)
throw|;
block|}
specifier|final
name|String
name|indicesAsString
init|=
name|Arrays
operator|.
name|toString
argument_list|(
name|request
operator|.
name|indices
argument_list|)
decl_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"close-indices "
operator|+
name|indicesAsString
argument_list|,
name|Priority
operator|.
name|URGENT
argument_list|,
operator|new
name|TimeoutClusterStateUpdateTask
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TimeValue
name|timeout
parameter_list|()
block|{
return|return
name|request
operator|.
name|masterTimeout
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onTimeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|,
name|String
name|source
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|ProcessClusterEventTimeoutException
argument_list|(
name|timeout
argument_list|,
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ClusterState
name|execute
parameter_list|(
name|ClusterState
name|currentState
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|indicesToClose
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|request
operator|.
name|indices
control|)
block|{
name|IndexMetaData
name|indexMetaData
init|=
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMetaData
operator|==
literal|null
condition|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|IndexMissingException
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|currentState
return|;
block|}
if|if
condition|(
name|indexMetaData
operator|.
name|state
argument_list|()
operator|!=
name|IndexMetaData
operator|.
name|State
operator|.
name|CLOSE
condition|)
block|{
name|indicesToClose
operator|.
name|add
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|indicesToClose
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|Response
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|currentState
return|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"closing indices [{}]"
argument_list|,
name|indicesAsString
argument_list|)
expr_stmt|;
name|MetaData
operator|.
name|Builder
name|mdBuilder
init|=
name|MetaData
operator|.
name|builder
argument_list|()
operator|.
name|metaData
argument_list|(
name|currentState
operator|.
name|metaData
argument_list|()
argument_list|)
decl_stmt|;
name|ClusterBlocks
operator|.
name|Builder
name|blocksBuilder
init|=
name|ClusterBlocks
operator|.
name|builder
argument_list|()
operator|.
name|blocks
argument_list|(
name|currentState
operator|.
name|blocks
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indicesToClose
control|)
block|{
name|mdBuilder
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|newIndexMetaDataBuilder
argument_list|(
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
argument_list|)
operator|.
name|state
argument_list|(
name|IndexMetaData
operator|.
name|State
operator|.
name|CLOSE
argument_list|)
argument_list|)
expr_stmt|;
name|blocksBuilder
operator|.
name|addIndexBlock
argument_list|(
name|index
argument_list|,
name|INDEX_CLOSED_BLOCK
argument_list|)
expr_stmt|;
block|}
name|ClusterState
name|updatedState
init|=
name|ClusterState
operator|.
name|builder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|metaData
argument_list|(
name|mdBuilder
argument_list|)
operator|.
name|blocks
argument_list|(
name|blocksBuilder
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RoutingTable
operator|.
name|Builder
name|rtBuilder
init|=
name|RoutingTable
operator|.
name|builder
argument_list|()
operator|.
name|routingTable
argument_list|(
name|currentState
operator|.
name|routingTable
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indicesToClose
control|)
block|{
name|rtBuilder
operator|.
name|remove
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
name|RoutingAllocation
operator|.
name|Result
name|routingResult
init|=
name|allocationService
operator|.
name|reroute
argument_list|(
name|ClusterState
operator|.
name|builder
argument_list|()
operator|.
name|state
argument_list|(
name|updatedState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|rtBuilder
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ClusterState
operator|.
name|builder
argument_list|()
operator|.
name|state
argument_list|(
name|updatedState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|routingResult
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clusterStateProcessed
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|Response
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|openIndex
specifier|public
name|void
name|openIndex
parameter_list|(
specifier|final
name|Request
name|request
parameter_list|,
specifier|final
name|Listener
name|listener
parameter_list|)
block|{
if|if
condition|(
name|request
operator|.
name|indices
operator|==
literal|null
operator|||
name|request
operator|.
name|indices
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Index name is required"
argument_list|)
throw|;
block|}
specifier|final
name|String
name|indicesAsString
init|=
name|Arrays
operator|.
name|toString
argument_list|(
name|request
operator|.
name|indices
argument_list|)
decl_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"open-indices "
operator|+
name|indicesAsString
argument_list|,
name|Priority
operator|.
name|URGENT
argument_list|,
operator|new
name|TimeoutClusterStateUpdateTask
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|TimeValue
name|timeout
parameter_list|()
block|{
return|return
name|request
operator|.
name|masterTimeout
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onTimeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|,
name|String
name|source
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|ProcessClusterEventTimeoutException
argument_list|(
name|timeout
argument_list|,
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ClusterState
name|execute
parameter_list|(
name|ClusterState
name|currentState
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|indicesToOpen
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|request
operator|.
name|indices
control|)
block|{
name|IndexMetaData
name|indexMetaData
init|=
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMetaData
operator|==
literal|null
condition|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|IndexMissingException
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|currentState
return|;
block|}
if|if
condition|(
name|indexMetaData
operator|.
name|state
argument_list|()
operator|!=
name|IndexMetaData
operator|.
name|State
operator|.
name|OPEN
condition|)
block|{
name|indicesToOpen
operator|.
name|add
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|indicesToOpen
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|Response
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|currentState
return|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"opening indices [{}]"
argument_list|,
name|indicesAsString
argument_list|)
expr_stmt|;
name|MetaData
operator|.
name|Builder
name|mdBuilder
init|=
name|MetaData
operator|.
name|builder
argument_list|()
operator|.
name|metaData
argument_list|(
name|currentState
operator|.
name|metaData
argument_list|()
argument_list|)
decl_stmt|;
name|ClusterBlocks
operator|.
name|Builder
name|blocksBuilder
init|=
name|ClusterBlocks
operator|.
name|builder
argument_list|()
operator|.
name|blocks
argument_list|(
name|currentState
operator|.
name|blocks
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indicesToOpen
control|)
block|{
name|mdBuilder
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|newIndexMetaDataBuilder
argument_list|(
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
argument_list|)
operator|.
name|state
argument_list|(
name|IndexMetaData
operator|.
name|State
operator|.
name|OPEN
argument_list|)
argument_list|)
expr_stmt|;
name|blocksBuilder
operator|.
name|removeIndexBlock
argument_list|(
name|index
argument_list|,
name|INDEX_CLOSED_BLOCK
argument_list|)
expr_stmt|;
block|}
name|ClusterState
name|updatedState
init|=
name|ClusterState
operator|.
name|builder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|metaData
argument_list|(
name|mdBuilder
argument_list|)
operator|.
name|blocks
argument_list|(
name|blocksBuilder
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RoutingTable
operator|.
name|Builder
name|rtBuilder
init|=
name|RoutingTable
operator|.
name|builder
argument_list|()
operator|.
name|routingTable
argument_list|(
name|updatedState
operator|.
name|routingTable
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indicesToOpen
control|)
block|{
name|rtBuilder
operator|.
name|addAsRecovery
argument_list|(
name|updatedState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|RoutingAllocation
operator|.
name|Result
name|routingResult
init|=
name|allocationService
operator|.
name|reroute
argument_list|(
name|ClusterState
operator|.
name|builder
argument_list|()
operator|.
name|state
argument_list|(
name|updatedState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|rtBuilder
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ClusterState
operator|.
name|builder
argument_list|()
operator|.
name|state
argument_list|(
name|updatedState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|routingResult
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clusterStateProcessed
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|Response
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|interface|Listener
specifier|public
specifier|static
interface|interface
name|Listener
block|{
DECL|method|onResponse
name|void
name|onResponse
parameter_list|(
name|Response
name|response
parameter_list|)
function_decl|;
DECL|method|onFailure
name|void
name|onFailure
parameter_list|(
name|Throwable
name|t
parameter_list|)
function_decl|;
block|}
DECL|class|Request
specifier|public
specifier|static
class|class
name|Request
block|{
DECL|field|indices
specifier|final
name|String
index|[]
name|indices
decl_stmt|;
DECL|field|timeout
name|TimeValue
name|timeout
init|=
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|10
argument_list|)
decl_stmt|;
DECL|field|masterTimeout
name|TimeValue
name|masterTimeout
init|=
name|MasterNodeOperationRequest
operator|.
name|DEFAULT_MASTER_NODE_TIMEOUT
decl_stmt|;
DECL|method|Request
specifier|public
name|Request
parameter_list|(
name|String
index|[]
name|indices
parameter_list|)
block|{
name|this
operator|.
name|indices
operator|=
name|indices
expr_stmt|;
block|}
DECL|method|timeout
specifier|public
name|Request
name|timeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|masterTimeout
specifier|public
name|Request
name|masterTimeout
parameter_list|(
name|TimeValue
name|masterTimeout
parameter_list|)
block|{
name|this
operator|.
name|masterTimeout
operator|=
name|masterTimeout
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
DECL|class|Response
specifier|public
specifier|static
class|class
name|Response
block|{
DECL|field|acknowledged
specifier|private
specifier|final
name|boolean
name|acknowledged
decl_stmt|;
DECL|method|Response
specifier|public
name|Response
parameter_list|(
name|boolean
name|acknowledged
parameter_list|)
block|{
name|this
operator|.
name|acknowledged
operator|=
name|acknowledged
expr_stmt|;
block|}
DECL|method|acknowledged
specifier|public
name|boolean
name|acknowledged
parameter_list|()
block|{
return|return
name|acknowledged
return|;
block|}
block|}
block|}
end_class

end_unit

