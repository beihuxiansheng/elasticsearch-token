begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
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
name|Maps
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
name|LuceneTestCase
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
name|Nullable
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
name|index
operator|.
name|shard
operator|.
name|IndexShardState
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
name|index
operator|.
name|shard
operator|.
name|service
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
name|test
operator|.
name|ElasticsearchIntegrationTest
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
name|Map
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
name|ConcurrentMap
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
name|CopyOnWriteArrayList
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
name|TimeUnit
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
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|allocation
operator|.
name|decider
operator|.
name|DisableAllocationDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_DISABLE_ALLOCATION
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
name|builder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|IndexShardState
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
name|test
operator|.
name|ElasticsearchIntegrationTest
operator|.
name|ClusterScope
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ElasticsearchIntegrationTest
operator|.
name|Scope
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertAcked
import|;
end_import

begin_class
annotation|@
name|ClusterScope
argument_list|(
name|scope
operator|=
name|Scope
operator|.
name|TEST
argument_list|,
name|numDataNodes
operator|=
literal|0
argument_list|)
annotation|@
name|LuceneTestCase
operator|.
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"fails due to old ping responses confusing master elections, bleskes investigating"
argument_list|)
DECL|class|IndicesLifecycleListenerTests
specifier|public
class|class
name|IndicesLifecycleListenerTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
DECL|method|testIndexStateShardChanged
specifier|public
name|void
name|testIndexStateShardChanged
parameter_list|()
throws|throws
name|Throwable
block|{
comment|//start with a single node
name|String
name|node1
init|=
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|()
decl_stmt|;
name|IndexShardStateChangeListener
name|stateChangeListenerNode1
init|=
operator|new
name|IndexShardStateChangeListener
argument_list|()
decl_stmt|;
comment|//add a listener that keeps track of the shard state changes
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|IndicesLifecycle
operator|.
name|class
argument_list|,
name|node1
argument_list|)
operator|.
name|addListener
argument_list|(
name|stateChangeListenerNode1
argument_list|)
expr_stmt|;
comment|//create an index
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|6
argument_list|,
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
comment|//new shards got started
name|assertShardStatesMatch
argument_list|(
name|stateChangeListenerNode1
argument_list|,
literal|6
argument_list|,
name|CREATED
argument_list|,
name|RECOVERING
argument_list|,
name|POST_RECOVERY
argument_list|,
name|STARTED
argument_list|)
expr_stmt|;
comment|//add a node: 3 out of the 6 shards will be relocated to it
comment|//disable allocation before starting a new node, as we need to register the listener first
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|()
operator|.
name|setPersistentSettings
argument_list|(
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|CLUSTER_ROUTING_ALLOCATION_DISABLE_ALLOCATION
argument_list|,
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|node2
init|=
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|()
decl_stmt|;
name|IndexShardStateChangeListener
name|stateChangeListenerNode2
init|=
operator|new
name|IndexShardStateChangeListener
argument_list|()
decl_stmt|;
comment|//add a listener that keeps track of the shard state changes
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|IndicesLifecycle
operator|.
name|class
argument_list|,
name|node2
argument_list|)
operator|.
name|addListener
argument_list|(
name|stateChangeListenerNode2
argument_list|)
expr_stmt|;
comment|//re-enable allocation
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|()
operator|.
name|setPersistentSettings
argument_list|(
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|CLUSTER_ROUTING_ALLOCATION_DISABLE_ALLOCATION
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
comment|//the 3 relocated shards get closed on the first node
name|assertShardStatesMatch
argument_list|(
name|stateChangeListenerNode1
argument_list|,
literal|3
argument_list|,
name|CLOSED
argument_list|)
expr_stmt|;
comment|//the 3 relocated shards get created on the second node
name|assertShardStatesMatch
argument_list|(
name|stateChangeListenerNode2
argument_list|,
literal|3
argument_list|,
name|CREATED
argument_list|,
name|RECOVERING
argument_list|,
name|POST_RECOVERY
argument_list|,
name|STARTED
argument_list|)
expr_stmt|;
comment|//increase replicas from 0 to 1
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
comment|//3 replicas are allocated to the first node
name|assertShardStatesMatch
argument_list|(
name|stateChangeListenerNode1
argument_list|,
literal|3
argument_list|,
name|CREATED
argument_list|,
name|RECOVERING
argument_list|,
name|POST_RECOVERY
argument_list|,
name|STARTED
argument_list|)
expr_stmt|;
comment|//3 replicas are allocated to the second node
name|assertShardStatesMatch
argument_list|(
name|stateChangeListenerNode2
argument_list|,
literal|3
argument_list|,
name|CREATED
argument_list|,
name|RECOVERING
argument_list|,
name|POST_RECOVERY
argument_list|,
name|STARTED
argument_list|)
expr_stmt|;
comment|//close the index
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertShardStatesMatch
argument_list|(
name|stateChangeListenerNode1
argument_list|,
literal|6
argument_list|,
name|CLOSED
argument_list|)
expr_stmt|;
name|assertShardStatesMatch
argument_list|(
name|stateChangeListenerNode2
argument_list|,
literal|6
argument_list|,
name|CLOSED
argument_list|)
expr_stmt|;
block|}
DECL|method|assertShardStatesMatch
specifier|private
specifier|static
name|void
name|assertShardStatesMatch
parameter_list|(
specifier|final
name|IndexShardStateChangeListener
name|stateChangeListener
parameter_list|,
specifier|final
name|int
name|numShards
parameter_list|,
specifier|final
name|IndexShardState
modifier|...
name|shardStates
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|Predicate
argument_list|<
name|Object
argument_list|>
name|waitPredicate
init|=
operator|new
name|Predicate
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Object
name|input
parameter_list|)
block|{
if|if
condition|(
name|stateChangeListener
operator|.
name|shardStates
operator|.
name|size
argument_list|()
operator|!=
name|numShards
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|List
argument_list|<
name|IndexShardState
argument_list|>
name|indexShardStates
range|:
name|stateChangeListener
operator|.
name|shardStates
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|indexShardStates
operator|==
literal|null
operator|||
name|indexShardStates
operator|.
name|size
argument_list|()
operator|!=
name|shardStates
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|shardStates
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|indexShardStates
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|!=
name|shardStates
index|[
name|i
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
if|if
condition|(
operator|!
name|awaitBusy
argument_list|(
name|waitPredicate
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"failed to observe expect shard states\n"
operator|+
literal|"expected: ["
operator|+
name|numShards
operator|+
literal|"] shards with states: "
operator|+
name|Strings
operator|.
name|arrayToCommaDelimitedString
argument_list|(
name|shardStates
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"observed:\n"
operator|+
name|stateChangeListener
argument_list|)
expr_stmt|;
block|}
name|stateChangeListener
operator|.
name|shardStates
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|class|IndexShardStateChangeListener
specifier|private
specifier|static
class|class
name|IndexShardStateChangeListener
extends|extends
name|IndicesLifecycle
operator|.
name|Listener
block|{
comment|//we keep track of all the states (ordered) a shard goes through
DECL|field|shardStates
specifier|final
name|ConcurrentMap
argument_list|<
name|ShardId
argument_list|,
name|List
argument_list|<
name|IndexShardState
argument_list|>
argument_list|>
name|shardStates
init|=
name|Maps
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|indexShardStateChanged
specifier|public
name|void
name|indexShardStateChanged
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|,
annotation|@
name|Nullable
name|IndexShardState
name|previousState
parameter_list|,
name|IndexShardState
name|newState
parameter_list|,
annotation|@
name|Nullable
name|String
name|reason
parameter_list|)
block|{
name|List
argument_list|<
name|IndexShardState
argument_list|>
name|shardStates
init|=
name|this
operator|.
name|shardStates
operator|.
name|putIfAbsent
argument_list|(
name|indexShard
operator|.
name|shardId
argument_list|()
argument_list|,
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|(
operator|new
name|IndexShardState
index|[]
block|{
name|newState
block|}
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardStates
operator|!=
literal|null
condition|)
block|{
name|shardStates
operator|.
name|add
argument_list|(
name|newState
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ShardId
argument_list|,
name|List
argument_list|<
name|IndexShardState
argument_list|>
argument_list|>
name|entry
range|:
name|shardStates
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" --> "
argument_list|)
operator|.
name|append
argument_list|(
name|Strings
operator|.
name|collectionToCommaDelimitedString
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

