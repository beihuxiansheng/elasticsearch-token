begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.allocation
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|allocation
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
name|Charsets
import|;
end_import

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
name|metadata
operator|.
name|MetaData
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
name|*
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
name|index
operator|.
name|engine
operator|.
name|internal
operator|.
name|InternalEngine
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
name|test
operator|.
name|ElasticsearchAllocationTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|routing
operator|.
name|ShardRoutingState
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
name|common
operator|.
name|settings
operator|.
name|ImmutableSettings
operator|.
name|settingsBuilder
import|;
end_import

begin_comment
comment|/**  * A base testscase that allows to run tests based on the output of the CAT API  * The input is a line based cat/shards output like:  *   kibana-int           0 p STARTED       2  24.8kb 10.202.245.2 r5-9-35  *  * the test builds up a clusterstate from the cat input and optionally runs a full balance on it.  * This can be used to debug cluster allocation decisions.  */
end_comment

begin_class
annotation|@
name|Ignore
DECL|class|CatAllocationTestBase
specifier|public
specifier|abstract
class|class
name|CatAllocationTestBase
extends|extends
name|ElasticsearchAllocationTestCase
block|{
DECL|method|getCatPath
specifier|protected
specifier|abstract
name|Path
name|getCatPath
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Test
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|nodes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Idx
argument_list|>
name|indices
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
try|try
init|(
name|BufferedReader
name|reader
init|=
name|Files
operator|.
name|newBufferedReader
argument_list|(
name|getCatPath
argument_list|()
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
init|)
block|{
name|String
name|line
init|=
literal|null
decl_stmt|;
comment|// regexp FTW
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^(.+)\\s+(\\d)\\s+([rp])\\s+(STARTED|RELOCATING|INITIALIZING|UNASSIGNED)\\s+\\d+\\s+[0-9.a-z]+\\s+(\\d+\\.\\d+\\.\\d+\\.\\d+).*$"
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Matcher
name|matcher
decl_stmt|;
if|if
condition|(
operator|(
name|matcher
operator|=
name|pattern
operator|.
name|matcher
argument_list|(
name|line
argument_list|)
operator|)
operator|.
name|matches
argument_list|()
condition|)
block|{
specifier|final
name|String
name|index
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Idx
name|idx
init|=
name|indices
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|==
literal|null
condition|)
block|{
name|idx
operator|=
operator|new
name|Idx
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|indices
operator|.
name|put
argument_list|(
name|index
argument_list|,
name|idx
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|shard
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|primary
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
operator|.
name|equals
argument_list|(
literal|"p"
argument_list|)
decl_stmt|;
name|ShardRoutingState
name|state
init|=
name|ShardRoutingState
operator|.
name|valueOf
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|4
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|ip
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|ip
argument_list|)
expr_stmt|;
name|MutableShardRouting
name|routing
init|=
operator|new
name|MutableShardRouting
argument_list|(
name|index
argument_list|,
name|shard
argument_list|,
name|ip
argument_list|,
name|primary
argument_list|,
name|state
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|idx
operator|.
name|add
argument_list|(
name|routing
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Add routing {}"
argument_list|,
name|routing
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"can't read line: "
operator|+
name|line
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"Building initial routing table"
argument_list|)
expr_stmt|;
name|MetaData
operator|.
name|Builder
name|builder
init|=
name|MetaData
operator|.
name|builder
argument_list|()
decl_stmt|;
name|RoutingTable
operator|.
name|Builder
name|routingTableBuilder
init|=
name|RoutingTable
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Idx
name|idx
range|:
name|indices
operator|.
name|values
argument_list|()
control|)
block|{
name|IndexMetaData
name|idxMeta
init|=
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|idx
operator|.
name|name
argument_list|)
operator|.
name|settings
argument_list|(
name|settings
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
operator|.
name|numberOfShards
argument_list|(
name|idx
operator|.
name|numShards
argument_list|()
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
name|idx
operator|.
name|numReplicas
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|idxMeta
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|IndexRoutingTable
operator|.
name|Builder
name|tableBuilder
init|=
operator|new
name|IndexRoutingTable
operator|.
name|Builder
argument_list|(
name|idx
operator|.
name|name
argument_list|)
operator|.
name|initializeAsRecovery
argument_list|(
name|idxMeta
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|IndexShardRoutingTable
argument_list|>
name|shardIdToRouting
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|MutableShardRouting
name|r
range|:
name|idx
operator|.
name|routing
control|)
block|{
name|IndexShardRoutingTable
name|refData
init|=
operator|new
name|IndexShardRoutingTable
operator|.
name|Builder
argument_list|(
operator|new
name|ShardId
argument_list|(
name|idx
operator|.
name|name
argument_list|,
name|r
operator|.
name|id
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
operator|.
name|addShard
argument_list|(
name|r
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
name|shardIdToRouting
operator|.
name|containsKey
argument_list|(
name|r
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|refData
operator|=
operator|new
name|IndexShardRoutingTable
operator|.
name|Builder
argument_list|(
name|shardIdToRouting
operator|.
name|get
argument_list|(
name|r
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|addShard
argument_list|(
name|r
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
name|shardIdToRouting
operator|.
name|put
argument_list|(
name|r
operator|.
name|getId
argument_list|()
argument_list|,
name|refData
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|IndexShardRoutingTable
name|t
range|:
name|shardIdToRouting
operator|.
name|values
argument_list|()
control|)
block|{
name|tableBuilder
operator|.
name|addIndexShard
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
name|IndexRoutingTable
name|table
init|=
name|tableBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|routingTableBuilder
operator|.
name|add
argument_list|(
name|table
argument_list|)
expr_stmt|;
block|}
name|MetaData
name|metaData
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|RoutingTable
name|routingTable
init|=
name|routingTableBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|DiscoveryNodes
operator|.
name|Builder
name|builderDiscoNodes
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|node
range|:
name|nodes
control|)
block|{
name|builderDiscoNodes
operator|.
name|put
argument_list|(
name|newNode
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ClusterState
name|clusterState
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterName
operator|.
name|DEFAULT
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaData
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|nodes
argument_list|(
name|builderDiscoNodes
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
name|balanceFirst
argument_list|()
condition|)
block|{
name|clusterState
operator|=
name|rebalance
argument_list|(
name|clusterState
argument_list|)
expr_stmt|;
block|}
name|clusterState
operator|=
name|allocateNew
argument_list|(
name|clusterState
argument_list|)
expr_stmt|;
block|}
DECL|method|allocateNew
specifier|protected
specifier|abstract
name|ClusterState
name|allocateNew
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
function_decl|;
DECL|method|balanceFirst
specifier|protected
name|boolean
name|balanceFirst
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|rebalance
specifier|private
name|ClusterState
name|rebalance
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
block|{
name|RoutingTable
name|routingTable
decl_stmt|;
name|AllocationService
name|strategy
init|=
name|createAllocationService
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|RoutingAllocation
operator|.
name|Result
name|reroute
init|=
name|strategy
operator|.
name|reroute
argument_list|(
name|clusterState
argument_list|)
decl_stmt|;
name|routingTable
operator|=
name|reroute
operator|.
name|routingTable
argument_list|()
expr_stmt|;
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|routingTable
operator|=
name|clusterState
operator|.
name|routingTable
argument_list|()
expr_stmt|;
name|int
name|numRelocations
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|initializing
init|=
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
decl_stmt|;
if|if
condition|(
name|initializing
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
break|break;
block|}
name|logger
operator|.
name|debug
argument_list|(
name|initializing
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|numRelocations
operator|+=
name|initializing
operator|.
name|size
argument_list|()
expr_stmt|;
name|routingTable
operator|=
name|strategy
operator|.
name|applyStartedShards
argument_list|(
name|clusterState
argument_list|,
name|initializing
argument_list|)
operator|.
name|routingTable
argument_list|()
expr_stmt|;
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"--> num relocations to get balance: "
operator|+
name|numRelocations
argument_list|)
expr_stmt|;
return|return
name|clusterState
return|;
block|}
DECL|class|Idx
specifier|public
class|class
name|Idx
block|{
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|routing
specifier|final
name|List
argument_list|<
name|MutableShardRouting
argument_list|>
name|routing
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|Idx
specifier|public
name|Idx
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|MutableShardRouting
name|r
parameter_list|)
block|{
name|routing
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
DECL|method|numReplicas
specifier|public
name|int
name|numReplicas
parameter_list|()
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|MutableShardRouting
name|msr
range|:
name|routing
control|)
block|{
if|if
condition|(
name|msr
operator|.
name|primary
argument_list|()
operator|==
literal|false
operator|&&
name|msr
operator|.
name|id
argument_list|()
operator|==
literal|0
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
DECL|method|numShards
specifier|public
name|int
name|numShards
parameter_list|()
block|{
name|int
name|max
init|=
literal|0
decl_stmt|;
for|for
control|(
name|MutableShardRouting
name|msr
range|:
name|routing
control|)
block|{
if|if
condition|(
name|msr
operator|.
name|primary
argument_list|()
condition|)
block|{
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|msr
operator|.
name|getId
argument_list|()
operator|+
literal|1
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|max
return|;
block|}
block|}
block|}
end_class

end_unit
