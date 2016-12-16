begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
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
name|ActionListener
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
name|routing
operator|.
name|GroupShardsIterator
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
name|PlainShardIterator
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
name|RecoverySource
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
name|ShardIterator
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
name|UnassignedInfo
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
name|Strings
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|search
operator|.
name|SearchPhaseResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|SearchShardTarget
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|AliasFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|ShardSearchTransportRequest
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
name|ESTestCase
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
name|Map
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
name|CountDownLatch
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_class
DECL|class|SearchAsyncActionTests
specifier|public
class|class
name|SearchAsyncActionTests
extends|extends
name|ESTestCase
block|{
DECL|method|testFanOutAndCollect
specifier|public
name|void
name|testFanOutAndCollect
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|SearchRequest
name|request
init|=
operator|new
name|SearchRequest
argument_list|()
decl_stmt|;
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|AtomicReference
argument_list|<
name|TestSearchResponse
argument_list|>
name|response
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|responseListener
init|=
operator|new
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|SearchResponse
name|searchResponse
parameter_list|)
block|{
name|response
operator|.
name|set
argument_list|(
operator|(
name|TestSearchResponse
operator|)
name|searchResponse
argument_list|)
expr_stmt|;
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
name|logger
operator|.
name|warn
argument_list|(
literal|"test failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|DiscoveryNode
name|primaryNode
init|=
operator|new
name|DiscoveryNode
argument_list|(
literal|"node_1"
argument_list|,
name|buildNewFakeTransportAddress
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
name|DiscoveryNode
name|replicaNode
init|=
operator|new
name|DiscoveryNode
argument_list|(
literal|"node_2"
argument_list|,
name|buildNewFakeTransportAddress
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|DiscoveryNode
argument_list|,
name|Set
argument_list|<
name|Long
argument_list|>
argument_list|>
name|nodeToContextMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|AtomicInteger
name|contextIdGenerator
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|GroupShardsIterator
name|shardsIter
init|=
name|getShardsIter
argument_list|(
literal|"idx"
argument_list|,
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|primaryNode
argument_list|,
name|replicaNode
argument_list|)
decl_stmt|;
name|AtomicInteger
name|numFreedContext
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|SearchTransportService
name|transportService
init|=
operator|new
name|SearchTransportService
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|sendFreeContext
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|long
name|contextId
parameter_list|,
name|SearchRequest
name|request
parameter_list|)
block|{
name|numFreedContext
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeToContextMap
operator|.
name|containsKey
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeToContextMap
operator|.
name|get
argument_list|(
name|node
argument_list|)
operator|.
name|remove
argument_list|(
name|contextId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|DiscoveryNode
argument_list|>
name|lookup
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|lookup
operator|.
name|put
argument_list|(
name|primaryNode
operator|.
name|getId
argument_list|()
argument_list|,
name|primaryNode
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|AliasFilter
argument_list|>
name|aliasFilters
init|=
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"_na_"
argument_list|,
operator|new
name|AliasFilter
argument_list|(
literal|null
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
decl_stmt|;
name|AbstractSearchAsyncAction
name|asyncAction
init|=
operator|new
name|AbstractSearchAsyncAction
argument_list|<
name|TestSearchPhaseResult
argument_list|>
argument_list|(
name|logger
argument_list|,
name|transportService
argument_list|,
name|lookup
operator|::
name|get
argument_list|,
name|aliasFilters
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
literal|null
argument_list|,
name|request
argument_list|,
name|responseListener
argument_list|,
name|shardsIter
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
block|{
name|TestSearchResponse
name|response
init|=
operator|new
name|TestSearchResponse
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|sendExecuteFirstPhase
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|ShardSearchTransportRequest
name|request
parameter_list|,
name|ActionListener
name|listener
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"shard: "
operator|+
name|request
operator|.
name|shardId
argument_list|()
operator|+
literal|" has been queried twice"
argument_list|,
name|response
operator|.
name|queried
operator|.
name|add
argument_list|(
name|request
operator|.
name|shardId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|TestSearchPhaseResult
name|testSearchPhaseResult
init|=
operator|new
name|TestSearchPhaseResult
argument_list|(
name|contextIdGenerator
operator|.
name|incrementAndGet
argument_list|()
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Long
argument_list|>
name|ids
init|=
name|nodeToContextMap
operator|.
name|computeIfAbsent
argument_list|(
name|node
argument_list|,
parameter_list|(
name|n
parameter_list|)
lambda|->
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
name|ids
operator|.
name|add
argument_list|(
name|testSearchPhaseResult
operator|.
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
name|testSearchPhaseResult
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
name|listener
operator|.
name|onResponse
argument_list|(
name|testSearchPhaseResult
argument_list|)
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|moveToSecondPhase
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|firstResults
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|TestSearchPhaseResult
name|result
init|=
name|firstResults
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|result
operator|.
name|node
operator|.
name|getId
argument_list|()
argument_list|,
name|result
operator|.
name|shardTarget
argument_list|()
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|sendReleaseSearchContext
argument_list|(
name|result
operator|.
name|id
argument_list|()
argument_list|,
name|result
operator|.
name|node
argument_list|)
expr_stmt|;
block|}
name|responseListener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|firstPhaseName
parameter_list|()
block|{
return|return
literal|"test"
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Executor
name|getExecutor
parameter_list|()
block|{
name|fail
argument_list|(
literal|"no executor in this class"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
name|asyncAction
operator|.
name|start
argument_list|()
expr_stmt|;
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|response
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|nodeToContextMap
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeToContextMap
operator|.
name|containsKey
argument_list|(
name|primaryNode
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|shardsIter
operator|.
name|size
argument_list|()
argument_list|,
name|numFreedContext
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeToContextMap
operator|.
name|get
argument_list|(
name|primaryNode
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|nodeToContextMap
operator|.
name|get
argument_list|(
name|primaryNode
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getShardsIter
specifier|private
name|GroupShardsIterator
name|getShardsIter
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|numShards
parameter_list|,
name|boolean
name|doReplicas
parameter_list|,
name|DiscoveryNode
name|primaryNode
parameter_list|,
name|DiscoveryNode
name|replicaNode
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|ShardIterator
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|numShards
condition|;
name|i
operator|++
control|)
block|{
name|ArrayList
argument_list|<
name|ShardRouting
argument_list|>
name|started
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|ShardRouting
argument_list|>
name|initializing
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|ShardRouting
argument_list|>
name|unassigned
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ShardRouting
name|routing
init|=
name|ShardRouting
operator|.
name|newUnassigned
argument_list|(
operator|new
name|ShardId
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|,
literal|"_na_"
argument_list|)
argument_list|,
name|i
argument_list|)
argument_list|,
literal|true
argument_list|,
name|RecoverySource
operator|.
name|StoreRecoverySource
operator|.
name|EMPTY_STORE_INSTANCE
argument_list|,
operator|new
name|UnassignedInfo
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|INDEX_CREATED
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
decl_stmt|;
name|routing
operator|=
name|routing
operator|.
name|initialize
argument_list|(
name|primaryNode
operator|.
name|getId
argument_list|()
argument_list|,
name|i
operator|+
literal|"p"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|routing
operator|.
name|started
argument_list|()
expr_stmt|;
name|started
operator|.
name|add
argument_list|(
name|routing
argument_list|)
expr_stmt|;
if|if
condition|(
name|doReplicas
condition|)
block|{
name|routing
operator|=
name|ShardRouting
operator|.
name|newUnassigned
argument_list|(
operator|new
name|ShardId
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|,
literal|"_na_"
argument_list|)
argument_list|,
name|i
argument_list|)
argument_list|,
literal|false
argument_list|,
name|RecoverySource
operator|.
name|PeerRecoverySource
operator|.
name|INSTANCE
argument_list|,
operator|new
name|UnassignedInfo
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|INDEX_CREATED
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|replicaNode
operator|!=
literal|null
condition|)
block|{
name|routing
operator|=
name|routing
operator|.
name|initialize
argument_list|(
name|replicaNode
operator|.
name|getId
argument_list|()
argument_list|,
name|i
operator|+
literal|"r"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|routing
operator|.
name|started
argument_list|()
expr_stmt|;
name|started
operator|.
name|add
argument_list|(
name|routing
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|initializing
operator|.
name|add
argument_list|(
name|routing
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|unassigned
operator|.
name|add
argument_list|(
name|routing
argument_list|)
expr_stmt|;
comment|// unused yet
block|}
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|started
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|started
operator|.
name|addAll
argument_list|(
name|initializing
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
operator|new
name|PlainShardIterator
argument_list|(
operator|new
name|ShardId
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|,
literal|"_na_"
argument_list|)
argument_list|,
name|i
argument_list|)
argument_list|,
name|started
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|GroupShardsIterator
argument_list|(
name|list
argument_list|)
return|;
block|}
DECL|class|TestSearchResponse
specifier|public
specifier|static
class|class
name|TestSearchResponse
extends|extends
name|SearchResponse
block|{
DECL|field|queried
specifier|public
specifier|final
name|Set
argument_list|<
name|ShardId
argument_list|>
name|queried
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
block|}
DECL|class|TestSearchPhaseResult
specifier|public
specifier|static
class|class
name|TestSearchPhaseResult
implements|implements
name|SearchPhaseResult
block|{
DECL|field|id
specifier|final
name|long
name|id
decl_stmt|;
DECL|field|node
specifier|final
name|DiscoveryNode
name|node
decl_stmt|;
DECL|field|shardTarget
name|SearchShardTarget
name|shardTarget
decl_stmt|;
DECL|method|TestSearchPhaseResult
specifier|public
name|TestSearchPhaseResult
parameter_list|(
name|long
name|id
parameter_list|,
name|DiscoveryNode
name|node
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|id
specifier|public
name|long
name|id
parameter_list|()
block|{
return|return
name|id
return|;
block|}
annotation|@
name|Override
DECL|method|shardTarget
specifier|public
name|SearchShardTarget
name|shardTarget
parameter_list|()
block|{
return|return
name|this
operator|.
name|shardTarget
return|;
block|}
annotation|@
name|Override
DECL|method|shardTarget
specifier|public
name|void
name|shardTarget
parameter_list|(
name|SearchShardTarget
name|shardTarget
parameter_list|)
block|{
name|this
operator|.
name|shardTarget
operator|=
name|shardTarget
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{          }
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{          }
block|}
block|}
end_class

end_unit

