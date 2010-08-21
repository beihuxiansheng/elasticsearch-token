begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ClusterStateUpdateTask
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
name|index
operator|.
name|NodeIndexDeletedAction
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
name|IndexRoutingTable
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
name|ShardsAllocation
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
name|timer
operator|.
name|Timeout
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
name|timer
operator|.
name|TimerTask
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
name|timer
operator|.
name|TimerService
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
name|AtomicBoolean
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
name|AtomicInteger
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterState
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|MetaData
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|MetaDataDeleteIndexService
specifier|public
class|class
name|MetaDataDeleteIndexService
extends|extends
name|AbstractComponent
block|{
DECL|field|timerService
specifier|private
specifier|final
name|TimerService
name|timerService
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|shardsAllocation
specifier|private
specifier|final
name|ShardsAllocation
name|shardsAllocation
decl_stmt|;
DECL|field|nodeIndexDeletedAction
specifier|private
specifier|final
name|NodeIndexDeletedAction
name|nodeIndexDeletedAction
decl_stmt|;
DECL|method|MetaDataDeleteIndexService
annotation|@
name|Inject
specifier|public
name|MetaDataDeleteIndexService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TimerService
name|timerService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|ShardsAllocation
name|shardsAllocation
parameter_list|,
name|NodeIndexDeletedAction
name|nodeIndexDeletedAction
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|timerService
operator|=
name|timerService
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|shardsAllocation
operator|=
name|shardsAllocation
expr_stmt|;
name|this
operator|.
name|nodeIndexDeletedAction
operator|=
name|nodeIndexDeletedAction
expr_stmt|;
block|}
DECL|method|deleteIndex
specifier|public
name|void
name|deleteIndex
parameter_list|(
specifier|final
name|Request
name|request
parameter_list|,
specifier|final
name|Listener
name|userListener
parameter_list|)
block|{
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"delete-index ["
operator|+
name|request
operator|.
name|index
operator|+
literal|"]"
argument_list|,
operator|new
name|ClusterStateUpdateTask
argument_list|()
block|{
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
specifier|final
name|DeleteIndexListener
name|listener
init|=
operator|new
name|DeleteIndexListener
argument_list|(
name|request
argument_list|,
name|userListener
argument_list|)
decl_stmt|;
try|try
block|{
name|RoutingTable
name|routingTable
init|=
name|currentState
operator|.
name|routingTable
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|routingTable
operator|.
name|hasIndex
argument_list|(
name|request
operator|.
name|index
argument_list|)
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
name|request
operator|.
name|index
argument_list|)
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
literal|"[{}] deleting index"
argument_list|,
name|request
operator|.
name|index
argument_list|)
expr_stmt|;
name|RoutingTable
operator|.
name|Builder
name|routingTableBuilder
init|=
operator|new
name|RoutingTable
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexRoutingTable
name|indexRoutingTable
range|:
name|currentState
operator|.
name|routingTable
argument_list|()
operator|.
name|indicesRouting
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|indexRoutingTable
operator|.
name|index
argument_list|()
operator|.
name|equals
argument_list|(
name|request
operator|.
name|index
argument_list|)
condition|)
block|{
name|routingTableBuilder
operator|.
name|add
argument_list|(
name|indexRoutingTable
argument_list|)
expr_stmt|;
block|}
block|}
name|MetaData
name|newMetaData
init|=
name|newMetaDataBuilder
argument_list|()
operator|.
name|metaData
argument_list|(
name|currentState
operator|.
name|metaData
argument_list|()
argument_list|)
operator|.
name|remove
argument_list|(
name|request
operator|.
name|index
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RoutingTable
name|newRoutingTable
init|=
name|shardsAllocation
operator|.
name|reroute
argument_list|(
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTableBuilder
argument_list|)
operator|.
name|metaData
argument_list|(
name|newMetaData
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|ClusterBlocks
name|blocks
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
operator|.
name|removeIndexBlocks
argument_list|(
name|request
operator|.
name|index
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|(
name|currentState
operator|.
name|nodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|NodeIndexDeletedAction
operator|.
name|Listener
name|nodeIndexDeleteListener
init|=
operator|new
name|NodeIndexDeletedAction
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onNodeIndexDeleted
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|nodeId
parameter_list|)
block|{
if|if
condition|(
name|index
operator|.
name|equals
argument_list|(
name|request
operator|.
name|index
argument_list|)
condition|)
block|{
if|if
condition|(
name|counter
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
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
name|nodeIndexDeletedAction
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
name|nodeIndexDeletedAction
operator|.
name|add
argument_list|(
name|nodeIndexDeleteListener
argument_list|)
expr_stmt|;
name|Timeout
name|timeoutTask
init|=
name|timerService
operator|.
name|newTimeout
argument_list|(
operator|new
name|TimerTask
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|(
name|Timeout
name|timeout
parameter_list|)
throws|throws
name|Exception
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|Response
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|nodeIndexDeletedAction
operator|.
name|remove
argument_list|(
name|nodeIndexDeleteListener
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|request
operator|.
name|timeout
argument_list|,
name|TimerService
operator|.
name|ExecutionType
operator|.
name|THREADED
argument_list|)
decl_stmt|;
name|listener
operator|.
name|timeout
operator|=
name|timeoutTask
expr_stmt|;
return|return
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|newRoutingTable
argument_list|)
operator|.
name|metaData
argument_list|(
name|newMetaData
argument_list|)
operator|.
name|blocks
argument_list|(
name|blocks
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
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
return|return
name|currentState
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|class|DeleteIndexListener
class|class
name|DeleteIndexListener
implements|implements
name|Listener
block|{
DECL|field|notified
specifier|private
name|AtomicBoolean
name|notified
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|field|request
specifier|private
specifier|final
name|Request
name|request
decl_stmt|;
DECL|field|listener
specifier|private
specifier|final
name|Listener
name|listener
decl_stmt|;
DECL|field|timeout
specifier|volatile
name|Timeout
name|timeout
decl_stmt|;
DECL|method|DeleteIndexListener
specifier|private
name|DeleteIndexListener
parameter_list|(
name|Request
name|request
parameter_list|,
name|Listener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
DECL|method|onResponse
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
specifier|final
name|Response
name|response
parameter_list|)
block|{
if|if
condition|(
name|notified
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
if|if
condition|(
name|timeout
operator|!=
literal|null
condition|)
block|{
name|timeout
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
name|listener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|onFailure
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|notified
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
if|if
condition|(
name|timeout
operator|!=
literal|null
condition|)
block|{
name|timeout
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
name|listener
operator|.
name|onFailure
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
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
DECL|field|index
specifier|final
name|String
name|index
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
DECL|method|Request
specifier|public
name|Request
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
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

