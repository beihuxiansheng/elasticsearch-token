begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.cluster
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|cluster
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|admin
operator|.
name|cluster
operator|.
name|reroute
operator|.
name|ClusterRerouteRequest
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
name|admin
operator|.
name|indices
operator|.
name|close
operator|.
name|CloseIndexRequest
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
name|admin
operator|.
name|indices
operator|.
name|create
operator|.
name|CreateIndexRequest
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
name|admin
operator|.
name|indices
operator|.
name|delete
operator|.
name|DeleteIndexRequest
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
name|admin
operator|.
name|indices
operator|.
name|open
operator|.
name|OpenIndexRequest
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
name|admin
operator|.
name|indices
operator|.
name|settings
operator|.
name|put
operator|.
name|UpdateSettingsRequest
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
name|ClusterStateCreationUtils
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
name|ClusterChangedEvent
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
name|IndexMetaData
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
name|node
operator|.
name|DiscoveryNode
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
name|node
operator|.
name|DiscoveryNodes
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
name|ShardRouting
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
name|FailedRerouteAllocation
operator|.
name|FailedShard
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
name|transport
operator|.
name|LocalTransportAddress
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
name|util
operator|.
name|set
operator|.
name|Sets
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
name|recovery
operator|.
name|RecoveryTargetService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|RepositoriesService
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|Executor
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
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
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
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
DECL|class|IndicesClusterStateServiceRandomUpdatesTests
specifier|public
class|class
name|IndicesClusterStateServiceRandomUpdatesTests
extends|extends
name|AbstractIndicesClusterStateServiceTestCase
block|{
DECL|field|cluster
specifier|private
specifier|final
name|ClusterStateChanges
name|cluster
init|=
operator|new
name|ClusterStateChanges
argument_list|()
decl_stmt|;
DECL|method|testRandomClusterStateUpdates
specifier|public
name|void
name|testRandomClusterStateUpdates
parameter_list|()
block|{
comment|// we have an IndicesClusterStateService per node in the cluster
specifier|final
name|Map
argument_list|<
name|DiscoveryNode
argument_list|,
name|IndicesClusterStateService
argument_list|>
name|clusterStateServiceMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|ClusterState
name|state
init|=
name|randomInitialClusterState
argument_list|(
name|clusterStateServiceMap
argument_list|)
decl_stmt|;
comment|// each of the following iterations represents a new cluster state update processed on all nodes
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|30
condition|;
name|i
operator|++
control|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Iteration {}"
argument_list|,
name|i
argument_list|)
expr_stmt|;
specifier|final
name|ClusterState
name|previousState
init|=
name|state
decl_stmt|;
comment|// calculate new cluster state
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|randomInt
argument_list|(
literal|3
argument_list|)
condition|;
name|j
operator|++
control|)
block|{
comment|// multiple iterations to simulate batching of cluster states
name|state
operator|=
name|randomlyUpdateClusterState
argument_list|(
name|state
argument_list|,
name|clusterStateServiceMap
argument_list|)
expr_stmt|;
block|}
comment|// apply cluster state to nodes (incl. master)
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|state
operator|.
name|nodes
argument_list|()
control|)
block|{
name|IndicesClusterStateService
name|indicesClusterStateService
init|=
name|clusterStateServiceMap
operator|.
name|get
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|ClusterState
name|localState
init|=
name|adaptClusterStateToLocalNode
argument_list|(
name|state
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|ClusterState
name|previousLocalState
init|=
name|adaptClusterStateToLocalNode
argument_list|(
name|previousState
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|indicesClusterStateService
operator|.
name|clusterChanged
argument_list|(
operator|new
name|ClusterChangedEvent
argument_list|(
literal|"simulated change "
operator|+
name|i
argument_list|,
name|localState
argument_list|,
name|previousLocalState
argument_list|)
argument_list|)
expr_stmt|;
comment|// check that cluster state has been properly applied to node
name|assertClusterStateMatchesNodeState
argument_list|(
name|localState
argument_list|,
name|indicesClusterStateService
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TODO: check if we can go to green by starting all shards and finishing all iterations
name|logger
operator|.
name|info
argument_list|(
literal|"Final cluster state: {}"
argument_list|,
name|state
operator|.
name|prettyPrint
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|randomInitialClusterState
specifier|public
name|ClusterState
name|randomInitialClusterState
parameter_list|(
name|Map
argument_list|<
name|DiscoveryNode
argument_list|,
name|IndicesClusterStateService
argument_list|>
name|clusterStateServiceMap
parameter_list|)
block|{
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|allNodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|DiscoveryNode
name|localNode
init|=
name|createNode
argument_list|(
name|DiscoveryNode
operator|.
name|Role
operator|.
name|MASTER
argument_list|)
decl_stmt|;
comment|// local node is the master
name|allNodes
operator|.
name|add
argument_list|(
name|localNode
argument_list|)
expr_stmt|;
comment|// at least two nodes that have the data role so that we can allocate shards
name|allNodes
operator|.
name|add
argument_list|(
name|createNode
argument_list|(
name|DiscoveryNode
operator|.
name|Role
operator|.
name|DATA
argument_list|)
argument_list|)
expr_stmt|;
name|allNodes
operator|.
name|add
argument_list|(
name|createNode
argument_list|(
name|DiscoveryNode
operator|.
name|Role
operator|.
name|DATA
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|5
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|allNodes
operator|.
name|add
argument_list|(
name|createNode
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ClusterState
name|state
init|=
name|ClusterStateCreationUtils
operator|.
name|state
argument_list|(
name|localNode
argument_list|,
name|localNode
argument_list|,
name|allNodes
operator|.
name|toArray
argument_list|(
operator|new
name|DiscoveryNode
index|[
name|allNodes
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
comment|// add nodes to clusterStateServiceMap
name|updateNodes
argument_list|(
name|state
argument_list|,
name|clusterStateServiceMap
argument_list|)
expr_stmt|;
return|return
name|state
return|;
block|}
DECL|method|updateNodes
specifier|private
name|void
name|updateNodes
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|Map
argument_list|<
name|DiscoveryNode
argument_list|,
name|IndicesClusterStateService
argument_list|>
name|clusterStateServiceMap
parameter_list|)
block|{
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|state
operator|.
name|nodes
argument_list|()
control|)
block|{
name|clusterStateServiceMap
operator|.
name|computeIfAbsent
argument_list|(
name|node
argument_list|,
name|discoveryNode
lambda|->
block|{
name|IndicesClusterStateService
name|ics
init|=
name|createIndicesClusterStateService
argument_list|()
decl_stmt|;
name|ics
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|ics
return|;
block|}
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|DiscoveryNode
argument_list|,
name|IndicesClusterStateService
argument_list|>
argument_list|>
name|it
init|=
name|clusterStateServiceMap
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DiscoveryNode
name|node
init|=
name|it
operator|.
name|next
argument_list|()
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|nodeExists
argument_list|(
name|node
argument_list|)
operator|==
literal|false
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|randomlyUpdateClusterState
specifier|public
name|ClusterState
name|randomlyUpdateClusterState
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|Map
argument_list|<
name|DiscoveryNode
argument_list|,
name|IndicesClusterStateService
argument_list|>
name|clusterStateServiceMap
parameter_list|)
block|{
comment|// randomly create new indices (until we have 200 max)
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|randomInt
argument_list|(
literal|5
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|200
condition|)
block|{
break|break;
block|}
name|String
name|name
init|=
literal|"index_"
operator|+
name|randomAsciiOfLength
argument_list|(
literal|15
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|CreateIndexRequest
name|request
init|=
operator|new
name|CreateIndexRequest
argument_list|(
name|name
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
name|randomInt
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|state
operator|=
name|cluster
operator|.
name|createIndex
argument_list|(
name|state
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// randomly delete indices
name|Set
argument_list|<
name|String
argument_list|>
name|indicesToDelete
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numberOfIndicesToDelete
init|=
name|randomInt
argument_list|(
name|Math
operator|.
name|min
argument_list|(
literal|2
argument_list|,
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|randomSubsetOf
argument_list|(
name|numberOfIndicesToDelete
argument_list|,
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|keys
argument_list|()
operator|.
name|toArray
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
control|)
block|{
name|indicesToDelete
operator|.
name|add
argument_list|(
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
operator|.
name|getIndex
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indicesToDelete
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|DeleteIndexRequest
name|deleteRequest
init|=
operator|new
name|DeleteIndexRequest
argument_list|(
name|indicesToDelete
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|indicesToDelete
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|state
operator|=
name|cluster
operator|.
name|deleteIndices
argument_list|(
name|state
argument_list|,
name|deleteRequest
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indicesToDelete
control|)
block|{
name|assertFalse
argument_list|(
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// randomly close indices
name|int
name|numberOfIndicesToClose
init|=
name|randomInt
argument_list|(
name|Math
operator|.
name|min
argument_list|(
literal|1
argument_list|,
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|randomSubsetOf
argument_list|(
name|numberOfIndicesToClose
argument_list|,
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|keys
argument_list|()
operator|.
name|toArray
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
control|)
block|{
name|CloseIndexRequest
name|closeIndexRequest
init|=
operator|new
name|CloseIndexRequest
argument_list|(
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
operator|.
name|getIndex
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|state
operator|=
name|cluster
operator|.
name|closeIndices
argument_list|(
name|state
argument_list|,
name|closeIndexRequest
argument_list|)
expr_stmt|;
block|}
comment|// randomly open indices
name|int
name|numberOfIndicesToOpen
init|=
name|randomInt
argument_list|(
name|Math
operator|.
name|min
argument_list|(
literal|1
argument_list|,
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|randomSubsetOf
argument_list|(
name|numberOfIndicesToOpen
argument_list|,
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|keys
argument_list|()
operator|.
name|toArray
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
control|)
block|{
name|OpenIndexRequest
name|openIndexRequest
init|=
operator|new
name|OpenIndexRequest
argument_list|(
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
operator|.
name|getIndex
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|state
operator|=
name|cluster
operator|.
name|openIndices
argument_list|(
name|state
argument_list|,
name|openIndexRequest
argument_list|)
expr_stmt|;
block|}
comment|// randomly update settings
name|Set
argument_list|<
name|String
argument_list|>
name|indicesToUpdate
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|boolean
name|containsClosedIndex
init|=
literal|false
decl_stmt|;
name|int
name|numberOfIndicesToUpdate
init|=
name|randomInt
argument_list|(
name|Math
operator|.
name|min
argument_list|(
literal|2
argument_list|,
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|randomSubsetOf
argument_list|(
name|numberOfIndicesToUpdate
argument_list|,
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|keys
argument_list|()
operator|.
name|toArray
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
control|)
block|{
name|indicesToUpdate
operator|.
name|add
argument_list|(
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
operator|.
name|getIndex
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
operator|.
name|getState
argument_list|()
operator|==
name|IndexMetaData
operator|.
name|State
operator|.
name|CLOSE
condition|)
block|{
name|containsClosedIndex
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|indicesToUpdate
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|UpdateSettingsRequest
name|updateSettingsRequest
init|=
operator|new
name|UpdateSettingsRequest
argument_list|(
name|indicesToUpdate
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|indicesToUpdate
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|Settings
operator|.
name|Builder
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
decl_stmt|;
if|if
condition|(
name|containsClosedIndex
operator|==
literal|false
condition|)
block|{
name|settings
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
name|randomInt
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|settings
operator|.
name|put
argument_list|(
literal|"index.refresh_interval"
argument_list|,
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
operator|+
literal|"s"
argument_list|)
expr_stmt|;
name|updateSettingsRequest
operator|.
name|settings
argument_list|(
name|settings
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|state
operator|=
name|cluster
operator|.
name|updateSettings
argument_list|(
name|state
argument_list|,
name|updateSettingsRequest
argument_list|)
expr_stmt|;
block|}
comment|// randomly reroute
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
name|state
operator|=
name|cluster
operator|.
name|reroute
argument_list|(
name|state
argument_list|,
operator|new
name|ClusterRerouteRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// randomly start and fail allocated shards
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|startedShards
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FailedShard
argument_list|>
name|failedShards
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|state
operator|.
name|nodes
argument_list|()
control|)
block|{
name|IndicesClusterStateService
name|indicesClusterStateService
init|=
name|clusterStateServiceMap
operator|.
name|get
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|MockIndicesService
name|indicesService
init|=
operator|(
name|MockIndicesService
operator|)
name|indicesClusterStateService
operator|.
name|indicesService
decl_stmt|;
for|for
control|(
name|MockIndexService
name|indexService
range|:
name|indicesService
control|)
block|{
for|for
control|(
name|MockIndexShard
name|indexShard
range|:
name|indexService
control|)
block|{
name|ShardRouting
name|persistedShardRouting
init|=
name|indexShard
operator|.
name|routingEntry
argument_list|()
decl_stmt|;
if|if
condition|(
name|persistedShardRouting
operator|.
name|initializing
argument_list|()
operator|&&
name|randomBoolean
argument_list|()
condition|)
block|{
name|startedShards
operator|.
name|add
argument_list|(
name|persistedShardRouting
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
name|failedShards
operator|.
name|add
argument_list|(
operator|new
name|FailedShard
argument_list|(
name|persistedShardRouting
argument_list|,
literal|"fake shard failure"
argument_list|,
operator|new
name|Exception
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|state
operator|=
name|cluster
operator|.
name|applyFailedShards
argument_list|(
name|state
argument_list|,
name|failedShards
argument_list|)
expr_stmt|;
name|state
operator|=
name|cluster
operator|.
name|applyStartedShards
argument_list|(
name|state
argument_list|,
name|startedShards
argument_list|)
expr_stmt|;
comment|// randomly add and remove nodes (except current master)
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
comment|// add node
if|if
condition|(
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|getSize
argument_list|()
operator|<
literal|10
condition|)
block|{
name|DiscoveryNodes
name|newNodes
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|(
name|state
operator|.
name|nodes
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|createNode
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|state
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|state
argument_list|)
operator|.
name|nodes
argument_list|(
name|newNodes
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|state
operator|=
name|cluster
operator|.
name|reroute
argument_list|(
name|state
argument_list|,
operator|new
name|ClusterRerouteRequest
argument_list|()
argument_list|)
expr_stmt|;
comment|// always reroute after node leave
name|updateNodes
argument_list|(
name|state
argument_list|,
name|clusterStateServiceMap
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// remove node
if|if
condition|(
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|getDataNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|3
condition|)
block|{
name|DiscoveryNode
name|discoveryNode
init|=
name|randomFrom
argument_list|(
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|getNodes
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
name|DiscoveryNode
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|discoveryNode
operator|.
name|equals
argument_list|(
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|getMasterNode
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
name|DiscoveryNodes
name|newNodes
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|(
name|state
operator|.
name|nodes
argument_list|()
argument_list|)
operator|.
name|remove
argument_list|(
name|discoveryNode
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|state
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|state
argument_list|)
operator|.
name|nodes
argument_list|(
name|newNodes
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|state
operator|=
name|cluster
operator|.
name|reroute
argument_list|(
name|state
argument_list|,
operator|new
name|ClusterRerouteRequest
argument_list|()
argument_list|)
expr_stmt|;
comment|// always reroute after node join
name|updateNodes
argument_list|(
name|state
argument_list|,
name|clusterStateServiceMap
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// TODO: go masterless?
return|return
name|state
return|;
block|}
DECL|method|createNode
specifier|private
name|DiscoveryNode
name|createNode
parameter_list|(
name|DiscoveryNode
operator|.
name|Role
modifier|...
name|mustHaveRoles
parameter_list|)
block|{
name|Set
argument_list|<
name|DiscoveryNode
operator|.
name|Role
argument_list|>
name|roles
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|randomSubsetOf
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|DiscoveryNode
operator|.
name|Role
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|DiscoveryNode
operator|.
name|Role
name|mustHaveRole
range|:
name|mustHaveRoles
control|)
block|{
name|roles
operator|.
name|add
argument_list|(
name|mustHaveRole
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|DiscoveryNode
argument_list|(
literal|"node_"
operator|+
name|randomAsciiOfLength
argument_list|(
literal|8
argument_list|)
argument_list|,
name|LocalTransportAddress
operator|.
name|buildUnique
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|roles
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
return|;
block|}
DECL|method|adaptClusterStateToLocalNode
specifier|private
specifier|static
name|ClusterState
name|adaptClusterStateToLocalNode
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|DiscoveryNode
name|node
parameter_list|)
block|{
return|return
name|ClusterState
operator|.
name|builder
argument_list|(
name|state
argument_list|)
operator|.
name|nodes
argument_list|(
name|DiscoveryNodes
operator|.
name|builder
argument_list|(
name|state
operator|.
name|nodes
argument_list|()
argument_list|)
operator|.
name|localNodeId
argument_list|(
name|node
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|createIndicesClusterStateService
specifier|private
name|IndicesClusterStateService
name|createIndicesClusterStateService
parameter_list|()
block|{
specifier|final
name|ThreadPool
name|threadPool
init|=
name|mock
argument_list|(
name|ThreadPool
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Executor
name|executor
init|=
name|mock
argument_list|(
name|Executor
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|threadPool
operator|.
name|generic
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|executor
argument_list|)
expr_stmt|;
specifier|final
name|MockIndicesService
name|indicesService
init|=
operator|new
name|MockIndicesService
argument_list|()
decl_stmt|;
specifier|final
name|TransportService
name|transportService
init|=
operator|new
name|TransportService
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|null
argument_list|,
name|threadPool
argument_list|)
decl_stmt|;
specifier|final
name|ClusterService
name|clusterService
init|=
name|mock
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|RepositoriesService
name|repositoriesService
init|=
operator|new
name|RepositoriesService
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|clusterService
argument_list|,
name|transportService
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|RecoveryTargetService
name|recoveryTargetService
init|=
operator|new
name|RecoveryTargetService
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|threadPool
argument_list|,
name|transportService
argument_list|,
literal|null
argument_list|,
name|clusterService
argument_list|)
decl_stmt|;
specifier|final
name|ShardStateAction
name|shardStateAction
init|=
name|mock
argument_list|(
name|ShardStateAction
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
operator|new
name|IndicesClusterStateService
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|indicesService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|,
name|recoveryTargetService
argument_list|,
name|shardStateAction
argument_list|,
literal|null
argument_list|,
name|repositoriesService
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|shardId
lambda|->
block|{             }
argument_list|)
return|;
block|}
block|}
end_class

end_unit

