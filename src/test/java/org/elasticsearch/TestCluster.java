begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch
package|package
name|org
operator|.
name|elasticsearch
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterators
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
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
name|transport
operator|.
name|TransportClient
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
name|common
operator|.
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|Loggers
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
name|network
operator|.
name|NetworkUtils
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
name|ImmutableSettings
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
name|TransportAddress
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
name|store
operator|.
name|mock
operator|.
name|MockFSIndexStoreModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|internal
operator|.
name|InternalNode
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
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|ImmutableSettings
operator|.
name|settingsBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|NodeBuilder
operator|.
name|nodeBuilder
import|;
end_import

begin_class
DECL|class|TestCluster
specifier|public
class|class
name|TestCluster
block|{
comment|/* some random options to consider      *  "action.auto_create_index"      *  "node.local"      */
DECL|field|logger
specifier|protected
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|nodes
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|NodeAndClient
argument_list|>
name|nodes
init|=
name|newHashMap
argument_list|()
decl_stmt|;
DECL|field|clusterName
specifier|private
specifier|final
name|String
name|clusterName
decl_stmt|;
DECL|field|open
specifier|private
specifier|final
name|AtomicBoolean
name|open
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
DECL|field|defaultSettings
specifier|private
specifier|final
name|Settings
name|defaultSettings
decl_stmt|;
DECL|field|clientNode
specifier|private
name|NodeAndClient
name|clientNode
decl_stmt|;
DECL|field|random
specifier|private
name|Random
name|random
decl_stmt|;
DECL|field|clientFactory
specifier|private
name|ClientFactory
name|clientFactory
decl_stmt|;
DECL|field|nextNodeId
specifier|private
name|AtomicInteger
name|nextNodeId
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|method|TestCluster
specifier|public
name|TestCluster
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|this
argument_list|(
name|random
argument_list|,
literal|"shared-test-cluster-"
operator|+
name|NetworkUtils
operator|.
name|getLocalAddress
argument_list|()
operator|.
name|getHostName
argument_list|()
operator|+
literal|"CHILD_VM=["
operator|+
name|ElasticsearchTestCase
operator|.
name|CHILD_VM_ID
operator|+
literal|"]"
operator|+
literal|"_"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|TestCluster
specifier|private
name|TestCluster
parameter_list|(
name|Random
name|random
parameter_list|,
name|String
name|clusterName
parameter_list|,
name|Settings
name|defaultSettings
parameter_list|)
block|{
name|this
operator|.
name|random
operator|=
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
name|clientFactory
operator|=
operator|new
name|RandomClientFactory
argument_list|(
name|random
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterName
operator|=
name|clusterName
expr_stmt|;
if|if
condition|(
name|defaultSettings
operator|.
name|get
argument_list|(
literal|"gateway.type"
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// default to non gateway
name|defaultSettings
operator|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|defaultSettings
argument_list|)
operator|.
name|put
argument_list|(
literal|"gateway.type"
argument_list|,
literal|"none"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|defaultSettings
operator|.
name|get
argument_list|(
literal|"cluster.routing.schedule"
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// decrease the routing schedule so new nodes will be added quickly
name|defaultSettings
operator|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|defaultSettings
argument_list|)
operator|.
name|put
argument_list|(
literal|"cluster.routing.schedule"
argument_list|,
literal|"50ms"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|// TODO once we are reproducible here use MockRamIndexStoreModule
name|this
operator|.
name|defaultSettings
operator|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.store.type"
argument_list|,
name|MockFSIndexStoreModule
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|defaultSettings
argument_list|)
operator|.
name|put
argument_list|(
literal|"cluster.name"
argument_list|,
name|clusterName
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|ensureOpen
specifier|private
name|void
name|ensureOpen
parameter_list|()
block|{
if|if
condition|(
operator|!
name|open
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cluster is already closed"
argument_list|)
throw|;
block|}
block|}
DECL|method|getOneNode
specifier|public
name|Node
name|getOneNode
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|Collection
argument_list|<
name|NodeAndClient
argument_list|>
name|values
init|=
name|nodes
operator|.
name|values
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeAndClient
name|nodeAndClient
range|:
name|values
control|)
block|{
return|return
name|nodeAndClient
operator|.
name|node
argument_list|()
return|;
block|}
return|return
name|buildNode
argument_list|()
operator|.
name|start
argument_list|()
return|;
block|}
DECL|method|ensureAtLeastNumNodes
specifier|public
name|void
name|ensureAtLeastNumNodes
parameter_list|(
name|int
name|num
parameter_list|)
block|{
name|int
name|size
init|=
name|nodes
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|size
init|;
name|i
operator|<
name|num
condition|;
name|i
operator|++
control|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"increasing cluster size from {} to {}"
argument_list|,
name|size
argument_list|,
name|num
argument_list|)
expr_stmt|;
name|buildNode
argument_list|()
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|ensureAtLeastNumNodes
specifier|public
name|void
name|ensureAtLeastNumNodes
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|int
name|num
parameter_list|)
block|{
name|int
name|size
init|=
name|nodes
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|size
init|;
name|i
operator|<
name|num
condition|;
name|i
operator|++
control|)
block|{
name|buildNode
argument_list|(
name|settings
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|ensureAtMostNumNodes
specifier|public
name|void
name|ensureAtMostNumNodes
parameter_list|(
name|int
name|num
parameter_list|)
block|{
if|if
condition|(
name|nodes
operator|.
name|size
argument_list|()
operator|<=
name|num
condition|)
block|{
return|return;
block|}
name|Collection
argument_list|<
name|NodeAndClient
argument_list|>
name|values
init|=
name|nodes
operator|.
name|values
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|NodeAndClient
argument_list|>
name|limit
init|=
name|Iterators
operator|.
name|limit
argument_list|(
name|values
operator|.
name|iterator
argument_list|()
argument_list|,
name|nodes
operator|.
name|size
argument_list|()
operator|-
name|num
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"reducing cluster size from {} to {}"
argument_list|,
name|nodes
operator|.
name|size
argument_list|()
operator|-
name|num
argument_list|,
name|num
argument_list|)
expr_stmt|;
while|while
condition|(
name|limit
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|NodeAndClient
name|next
init|=
name|limit
operator|.
name|next
argument_list|()
decl_stmt|;
name|limit
operator|.
name|remove
argument_list|()
expr_stmt|;
name|next
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|startNode
specifier|public
name|Node
name|startNode
parameter_list|(
name|Settings
operator|.
name|Builder
name|settings
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|startNode
argument_list|(
name|settings
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
DECL|method|startNode
specifier|public
name|Node
name|startNode
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|buildNode
argument_list|(
name|settings
argument_list|)
operator|.
name|start
argument_list|()
return|;
block|}
DECL|method|buildNode
specifier|public
name|Node
name|buildNode
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|buildNode
argument_list|(
name|EMPTY_SETTINGS
argument_list|)
return|;
block|}
DECL|method|buildNode
specifier|public
name|Node
name|buildNode
parameter_list|(
name|Settings
operator|.
name|Builder
name|settings
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|buildNode
argument_list|(
name|settings
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
DECL|method|buildNode
specifier|public
name|Node
name|buildNode
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|String
name|name
init|=
name|buildNodeName
argument_list|()
decl_stmt|;
name|String
name|settingsSource
init|=
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
operator|+
literal|".yml"
decl_stmt|;
name|Settings
name|finalSettings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|loadFromClasspath
argument_list|(
name|settingsSource
argument_list|)
operator|.
name|put
argument_list|(
name|defaultSettings
argument_list|)
operator|.
name|put
argument_list|(
name|settings
argument_list|)
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
operator|.
name|put
argument_list|(
literal|"discovery.id.seed"
argument_list|,
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Node
name|node
init|=
name|nodeBuilder
argument_list|()
operator|.
name|settings
argument_list|(
name|finalSettings
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|nodes
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|NodeAndClient
argument_list|(
name|name
argument_list|,
name|node
argument_list|,
name|clientFactory
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
DECL|method|buildNodeName
specifier|private
name|String
name|buildNodeName
parameter_list|()
block|{
return|return
literal|"node_"
operator|+
name|nextNodeId
operator|.
name|getAndIncrement
argument_list|()
return|;
block|}
DECL|method|setClientFactory
specifier|public
name|void
name|setClientFactory
parameter_list|(
name|ClientFactory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|clientFactory
operator|=
name|factory
expr_stmt|;
block|}
DECL|method|closeNode
specifier|public
name|void
name|closeNode
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|NodeAndClient
name|remove
init|=
name|nodes
operator|.
name|remove
argument_list|(
name|node
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
decl_stmt|;
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|remove
argument_list|)
expr_stmt|;
comment|// quiet
block|}
DECL|method|client
specifier|public
name|Client
name|client
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
return|return
name|getOneNode
argument_list|()
operator|.
name|client
argument_list|()
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|open
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|nodes
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|clientNode
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|clientNode
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|waitForNoBlocks
specifier|public
name|ImmutableSet
argument_list|<
name|ClusterBlock
argument_list|>
name|waitForNoBlocks
parameter_list|(
name|TimeValue
name|timeout
parameter_list|,
name|Node
name|node
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ImmutableSet
argument_list|<
name|ClusterBlock
argument_list|>
name|blocks
decl_stmt|;
do|do
block|{
name|blocks
operator|=
name|node
operator|.
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|setLocal
argument_list|(
literal|true
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|getState
argument_list|()
operator|.
name|blocks
argument_list|()
operator|.
name|global
argument_list|(
name|ClusterBlockLevel
operator|.
name|METADATA
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
operator|!
name|blocks
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|<
name|timeout
operator|.
name|millis
argument_list|()
condition|)
do|;
return|return
name|blocks
return|;
block|}
DECL|class|NodeAndClient
specifier|public
class|class
name|NodeAndClient
implements|implements
name|Closeable
block|{
DECL|field|node
specifier|final
name|Node
name|node
decl_stmt|;
DECL|field|client
name|Client
name|client
decl_stmt|;
DECL|field|closed
specifier|final
name|AtomicBoolean
name|closed
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|clientFactory
specifier|final
name|ClientFactory
name|clientFactory
decl_stmt|;
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|NodeAndClient
specifier|public
name|NodeAndClient
parameter_list|(
name|String
name|name
parameter_list|,
name|Node
name|node
parameter_list|,
name|ClientFactory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|clientFactory
operator|=
name|factory
expr_stmt|;
block|}
DECL|method|node
specifier|public
name|Node
name|node
parameter_list|()
block|{
if|if
condition|(
name|closed
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"already closed"
argument_list|)
throw|;
block|}
return|return
name|node
return|;
block|}
DECL|method|client
specifier|public
name|Client
name|client
parameter_list|()
block|{
if|if
condition|(
name|closed
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"already closed"
argument_list|)
throw|;
block|}
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
return|return
name|client
return|;
block|}
return|return
name|client
operator|=
name|clientFactory
operator|.
name|client
argument_list|(
name|node
argument_list|,
name|clusterName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|closed
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|node
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|ClientFactory
specifier|public
specifier|static
class|class
name|ClientFactory
block|{
DECL|method|client
specifier|public
name|Client
name|client
parameter_list|(
name|Node
name|node
parameter_list|,
name|String
name|clusterName
parameter_list|)
block|{
return|return
name|node
operator|.
name|client
argument_list|()
return|;
block|}
block|}
DECL|class|TransportClientFactory
specifier|public
specifier|static
class|class
name|TransportClientFactory
extends|extends
name|ClientFactory
block|{
DECL|field|sniff
specifier|private
name|boolean
name|sniff
decl_stmt|;
DECL|field|NO_SNIFF_CLIENT_FACTORY
specifier|public
specifier|static
name|TransportClientFactory
name|NO_SNIFF_CLIENT_FACTORY
init|=
operator|new
name|TransportClientFactory
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|SNIFF_CLIENT_FACTORY
specifier|public
specifier|static
name|TransportClientFactory
name|SNIFF_CLIENT_FACTORY
init|=
operator|new
name|TransportClientFactory
argument_list|(
literal|true
argument_list|)
decl_stmt|;
DECL|method|TransportClientFactory
specifier|public
name|TransportClientFactory
parameter_list|(
name|boolean
name|sniff
parameter_list|)
block|{
name|this
operator|.
name|sniff
operator|=
name|sniff
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|client
specifier|public
name|Client
name|client
parameter_list|(
name|Node
name|node
parameter_list|,
name|String
name|clusterName
parameter_list|)
block|{
name|TransportAddress
name|addr
init|=
operator|(
operator|(
name|InternalNode
operator|)
name|node
operator|)
operator|.
name|injector
argument_list|()
operator|.
name|getInstance
argument_list|(
name|TransportService
operator|.
name|class
argument_list|)
operator|.
name|boundAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
decl_stmt|;
name|TransportClient
name|client
init|=
operator|new
name|TransportClient
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"client.transport.nodes_sampler_interval"
argument_list|,
literal|"30s"
argument_list|)
operator|.
name|put
argument_list|(
literal|"cluster.name"
argument_list|,
name|clusterName
argument_list|)
operator|.
name|put
argument_list|(
literal|"client.transport.sniff"
argument_list|,
name|sniff
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|addTransportAddress
argument_list|(
name|addr
argument_list|)
expr_stmt|;
return|return
name|client
return|;
block|}
block|}
DECL|class|RandomClientFactory
specifier|public
specifier|static
class|class
name|RandomClientFactory
extends|extends
name|ClientFactory
block|{
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|method|RandomClientFactory
specifier|public
name|RandomClientFactory
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|client
specifier|public
name|Client
name|client
parameter_list|(
name|Node
name|node
parameter_list|,
name|String
name|clusterName
parameter_list|)
block|{
switch|switch
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
condition|)
block|{
case|case
literal|5
case|:
return|return
name|TransportClientFactory
operator|.
name|NO_SNIFF_CLIENT_FACTORY
operator|.
name|client
argument_list|(
name|node
argument_list|,
name|clusterName
argument_list|)
return|;
case|case
literal|3
case|:
return|return
name|TransportClientFactory
operator|.
name|SNIFF_CLIENT_FACTORY
operator|.
name|client
argument_list|(
name|node
argument_list|,
name|clusterName
argument_list|)
return|;
default|default:
return|return
name|node
operator|.
name|client
argument_list|()
return|;
block|}
block|}
block|}
DECL|method|reset
name|void
name|reset
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|this
operator|.
name|random
operator|=
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|clientFactory
operator|=
operator|new
name|RandomClientFactory
argument_list|(
name|this
operator|.
name|random
argument_list|)
expr_stmt|;
block|}
DECL|method|clusterService
specifier|public
name|ClusterService
name|clusterService
parameter_list|()
block|{
return|return
operator|(
operator|(
name|InternalNode
operator|)
name|getOneNode
argument_list|()
operator|)
operator|.
name|injector
argument_list|()
operator|.
name|getInstance
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|numNodes
specifier|public
name|int
name|numNodes
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodes
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|stopRandomNode
specifier|public
name|void
name|stopRandomNode
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
comment|// TODO randomize
name|Set
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|NodeAndClient
argument_list|>
argument_list|>
name|entrySet
init|=
name|nodes
operator|.
name|entrySet
argument_list|()
decl_stmt|;
if|if
condition|(
name|entrySet
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|Entry
argument_list|<
name|String
argument_list|,
name|NodeAndClient
argument_list|>
name|next
init|=
name|entrySet
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|nodes
operator|.
name|remove
argument_list|(
name|next
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|next
operator|.
name|getValue
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|clients
specifier|public
name|Iterable
argument_list|<
name|Client
argument_list|>
name|clients
parameter_list|()
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NodeAndClient
argument_list|>
name|nodes
init|=
name|this
operator|.
name|nodes
decl_stmt|;
return|return
operator|new
name|Iterable
argument_list|<
name|Client
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Client
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|NodeAndClient
argument_list|>
name|iterator
init|=
name|nodes
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|Client
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Client
name|next
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|next
argument_list|()
operator|.
name|client
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|""
argument_list|)
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
DECL|method|allButN
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|allButN
parameter_list|(
name|int
name|numNodes
parameter_list|)
block|{
return|return
name|nRandomNodes
argument_list|(
name|numNodes
argument_list|()
operator|-
name|numNodes
argument_list|)
return|;
block|}
DECL|method|nRandomNodes
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|nRandomNodes
parameter_list|(
name|int
name|numNodes
parameter_list|)
block|{
assert|assert
name|numNodes
argument_list|()
operator|>=
name|numNodes
assert|;
return|return
name|Sets
operator|.
name|newHashSet
argument_list|(
name|Iterators
operator|.
name|limit
argument_list|(
name|this
operator|.
name|nodes
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|,
name|numNodes
argument_list|)
argument_list|)
return|;
block|}
DECL|method|nodeClient
specifier|public
name|Client
name|nodeClient
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
if|if
condition|(
name|clientNode
operator|==
literal|null
condition|)
block|{
name|String
name|name
init|=
literal|"client_"
operator|+
name|buildNodeName
argument_list|()
decl_stmt|;
name|String
name|settingsSource
init|=
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
operator|+
literal|".yml"
decl_stmt|;
name|Settings
name|finalSettings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|loadFromClasspath
argument_list|(
name|settingsSource
argument_list|)
operator|.
name|put
argument_list|(
name|defaultSettings
argument_list|)
operator|.
name|put
argument_list|(
literal|"node.client"
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Node
name|node
init|=
name|nodeBuilder
argument_list|()
operator|.
name|settings
argument_list|(
name|finalSettings
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|node
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|clientNode
operator|=
operator|new
name|NodeAndClient
argument_list|(
name|name
argument_list|,
name|node
argument_list|,
name|clientFactory
argument_list|)
expr_stmt|;
block|}
return|return
name|clientNode
operator|.
name|client
argument_list|()
return|;
block|}
DECL|method|nodesInclude
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|nodesInclude
parameter_list|(
name|String
name|index
parameter_list|)
block|{
if|if
condition|(
name|clusterService
argument_list|()
operator|.
name|state
argument_list|()
operator|.
name|routingTable
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|index
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|allShards
init|=
name|clusterService
argument_list|()
operator|.
name|state
argument_list|()
operator|.
name|routingTable
argument_list|()
operator|.
name|allShards
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|DiscoveryNodes
name|discoveryNodes
init|=
name|clusterService
argument_list|()
operator|.
name|state
argument_list|()
operator|.
name|getNodes
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|nodes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ShardRouting
name|shardRouting
range|:
name|allShards
control|)
block|{
if|if
condition|(
name|shardRouting
operator|.
name|assignedToNode
argument_list|()
condition|)
block|{
name|DiscoveryNode
name|discoveryNode
init|=
name|discoveryNodes
operator|.
name|get
argument_list|(
name|shardRouting
operator|.
name|currentNodeId
argument_list|()
argument_list|)
decl_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|discoveryNode
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|nodes
return|;
block|}
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
DECL|method|nodeExclude
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|nodeExclude
parameter_list|(
name|String
name|index
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|nodesInclude
init|=
name|nodesInclude
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
name|Sets
operator|.
name|newHashSet
argument_list|(
name|Iterators
operator|.
name|transform
argument_list|(
name|Iterators
operator|.
name|filter
argument_list|(
name|nodes
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|,
operator|new
name|Predicate
argument_list|<
name|NodeAndClient
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|NodeAndClient
name|nodeAndClient
parameter_list|)
block|{
return|return
operator|!
name|nodesInclude
operator|.
name|contains
argument_list|(
name|nodeAndClient
operator|.
name|name
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|,
operator|new
name|Function
argument_list|<
name|NodeAndClient
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|NodeAndClient
name|nodeAndClient
parameter_list|)
block|{
return|return
name|nodeAndClient
operator|.
name|name
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

