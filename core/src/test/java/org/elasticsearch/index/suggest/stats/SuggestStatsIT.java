begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.suggest.stats
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|suggest
operator|.
name|stats
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
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|stats
operator|.
name|NodeStats
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
name|node
operator|.
name|stats
operator|.
name|NodesStatsResponse
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
name|stats
operator|.
name|IndicesStatsResponse
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
name|suggest
operator|.
name|SuggestRequestBuilder
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
name|suggest
operator|.
name|SuggestResponse
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
name|search
operator|.
name|suggest
operator|.
name|phrase
operator|.
name|PhraseSuggestionBuilder
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
name|suggest
operator|.
name|term
operator|.
name|TermSuggestionBuilder
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
name|ESIntegTestCase
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
name|Set
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
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertAcked
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
name|assertAllSuccessful
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|greaterThan
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|greaterThanOrEqualTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|lessThanOrEqualTo
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
annotation|@
name|ESIntegTestCase
operator|.
name|ClusterScope
argument_list|(
name|minNumDataNodes
operator|=
literal|2
argument_list|)
DECL|class|SuggestStatsIT
specifier|public
class|class
name|SuggestStatsIT
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Override
DECL|method|numberOfReplicas
specifier|protected
name|int
name|numberOfReplicas
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|testSimpleStats
specifier|public
name|void
name|testSimpleStats
parameter_list|()
throws|throws
name|Exception
block|{
comment|// clear all stats first
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareStats
argument_list|()
operator|.
name|clear
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
specifier|final
name|int
name|numNodes
init|=
name|cluster
argument_list|()
operator|.
name|numDataNodes
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|numNodes
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|shardsIdx1
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
comment|// we make sure each node gets at least a single shard...
specifier|final
name|int
name|shardsIdx2
init|=
name|Math
operator|.
name|max
argument_list|(
name|numNodes
operator|-
name|shardsIdx1
argument_list|,
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|totalShards
init|=
name|shardsIdx1
operator|+
name|shardsIdx2
decl_stmt|;
name|assertThat
argument_list|(
name|numNodes
argument_list|,
name|lessThanOrEqualTo
argument_list|(
name|totalShards
argument_list|)
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
name|shardsIdx1
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
literal|"f"
argument_list|,
literal|"type=string"
argument_list|)
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
name|shardsIdx2
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
literal|"f"
argument_list|,
literal|"type=string"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shardsIdx1
operator|+
name|shardsIdx2
argument_list|,
name|equalTo
argument_list|(
name|numAssignedShards
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|numAssignedShards
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|)
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
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
literal|20
argument_list|,
literal|100
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
name|index
argument_list|(
literal|"test"
operator|+
operator|(
operator|(
name|i
operator|%
literal|2
operator|)
operator|+
literal|1
operator|)
argument_list|,
literal|"type"
argument_list|,
literal|""
operator|+
name|i
argument_list|,
literal|"f"
argument_list|,
literal|"test"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|refresh
argument_list|()
expr_stmt|;
name|int
name|suggestAllIdx
init|=
name|scaledRandomIntBetween
argument_list|(
literal|20
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|int
name|suggestIdx1
init|=
name|scaledRandomIntBetween
argument_list|(
literal|20
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|int
name|suggestIdx2
init|=
name|scaledRandomIntBetween
argument_list|(
literal|20
argument_list|,
literal|50
argument_list|)
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
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
name|suggestAllIdx
condition|;
name|i
operator|++
control|)
block|{
name|SuggestResponse
name|suggestResponse
init|=
name|addSuggestions
argument_list|(
name|internalCluster
argument_list|()
operator|.
name|clientNodeClient
argument_list|()
operator|.
name|prepareSuggest
argument_list|()
argument_list|,
name|i
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertAllSuccessful
argument_list|(
name|suggestResponse
argument_list|)
expr_stmt|;
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
name|suggestIdx1
condition|;
name|i
operator|++
control|)
block|{
name|SuggestResponse
name|suggestResponse
init|=
name|addSuggestions
argument_list|(
name|internalCluster
argument_list|()
operator|.
name|clientNodeClient
argument_list|()
operator|.
name|prepareSuggest
argument_list|(
literal|"test1"
argument_list|)
argument_list|,
name|i
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertAllSuccessful
argument_list|(
name|suggestResponse
argument_list|)
expr_stmt|;
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
name|suggestIdx2
condition|;
name|i
operator|++
control|)
block|{
name|SuggestResponse
name|suggestResponse
init|=
name|addSuggestions
argument_list|(
name|internalCluster
argument_list|()
operator|.
name|clientNodeClient
argument_list|()
operator|.
name|prepareSuggest
argument_list|(
literal|"test2"
argument_list|)
argument_list|,
name|i
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertAllSuccessful
argument_list|(
name|suggestResponse
argument_list|)
expr_stmt|;
block|}
name|long
name|endTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|IndicesStatsResponse
name|indicesStats
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareStats
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
comment|// check current
name|assertThat
argument_list|(
name|indicesStats
operator|.
name|getTotal
argument_list|()
operator|.
name|getSuggest
argument_list|()
operator|.
name|getCurrent
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
comment|// check suggest count
name|assertThat
argument_list|(
name|indicesStats
operator|.
name|getTotal
argument_list|()
operator|.
name|getSuggest
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
call|(
name|long
call|)
argument_list|(
name|suggestAllIdx
operator|*
name|totalShards
operator|+
name|suggestIdx1
operator|*
name|shardsIdx1
operator|+
name|suggestIdx2
operator|*
name|shardsIdx2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indicesStats
operator|.
name|getIndices
argument_list|()
operator|.
name|get
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|getTotal
argument_list|()
operator|.
name|getSuggest
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
call|(
name|long
call|)
argument_list|(
operator|(
name|suggestAllIdx
operator|+
name|suggestIdx1
operator|)
operator|*
name|shardsIdx1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indicesStats
operator|.
name|getIndices
argument_list|()
operator|.
name|get
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|getTotal
argument_list|()
operator|.
name|getSuggest
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
call|(
name|long
call|)
argument_list|(
operator|(
name|suggestAllIdx
operator|+
name|suggestIdx2
operator|)
operator|*
name|shardsIdx2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"iter {}, iter1 {}, iter2 {}, {}"
argument_list|,
name|suggestAllIdx
argument_list|,
name|suggestIdx1
argument_list|,
name|suggestIdx2
argument_list|,
name|endTime
operator|-
name|startTime
argument_list|)
expr_stmt|;
comment|// check suggest time
name|assertThat
argument_list|(
name|indicesStats
operator|.
name|getTotal
argument_list|()
operator|.
name|getSuggest
argument_list|()
operator|.
name|getTimeInMillis
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
comment|// the upperbound is num shards * total time since we do searches in parallel
name|assertThat
argument_list|(
name|indicesStats
operator|.
name|getTotal
argument_list|()
operator|.
name|getSuggest
argument_list|()
operator|.
name|getTimeInMillis
argument_list|()
argument_list|,
name|lessThanOrEqualTo
argument_list|(
name|totalShards
operator|*
operator|(
name|endTime
operator|-
name|startTime
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|NodesStatsResponse
name|nodeStats
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesStats
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|NodeStats
index|[]
name|nodes
init|=
name|nodeStats
operator|.
name|getNodes
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|nodeIdsWithIndex
init|=
name|nodeIdsWithIndex
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|)
decl_stmt|;
name|int
name|num
init|=
literal|0
decl_stmt|;
for|for
control|(
name|NodeStats
name|stat
range|:
name|nodes
control|)
block|{
name|SuggestStats
name|suggestStats
init|=
name|stat
operator|.
name|getIndices
argument_list|()
operator|.
name|getSuggest
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"evaluating {}"
argument_list|,
name|stat
operator|.
name|getNode
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|nodeIdsWithIndex
operator|.
name|contains
argument_list|(
name|stat
operator|.
name|getNode
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|suggestStats
operator|.
name|getCount
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|suggestStats
operator|.
name|getTimeInMillis
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
name|num
operator|++
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|suggestStats
operator|.
name|getCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|suggestStats
operator|.
name|getTimeInMillis
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertThat
argument_list|(
name|num
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addSuggestions
specifier|private
name|SuggestRequestBuilder
name|addSuggestions
parameter_list|(
name|SuggestRequestBuilder
name|request
parameter_list|,
name|int
name|i
parameter_list|)
block|{
for|for
control|(
name|int
name|s
init|=
literal|0
init|;
name|s
operator|<
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
condition|;
name|s
operator|++
control|)
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|request
operator|.
name|addSuggestion
argument_list|(
operator|new
name|PhraseSuggestionBuilder
argument_list|(
literal|"s"
operator|+
name|s
argument_list|)
operator|.
name|field
argument_list|(
literal|"f"
argument_list|)
operator|.
name|text
argument_list|(
literal|"test"
operator|+
name|i
operator|+
literal|" test"
operator|+
operator|(
name|i
operator|-
literal|1
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|request
operator|.
name|addSuggestion
argument_list|(
operator|new
name|TermSuggestionBuilder
argument_list|(
literal|"s"
operator|+
name|s
argument_list|)
operator|.
name|field
argument_list|(
literal|"f"
argument_list|)
operator|.
name|text
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|request
return|;
block|}
DECL|method|nodeIdsWithIndex
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|nodeIdsWithIndex
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|ClusterState
name|state
init|=
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
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|getState
argument_list|()
decl_stmt|;
name|GroupShardsIterator
name|allAssignedShardsGrouped
init|=
name|state
operator|.
name|routingTable
argument_list|()
operator|.
name|allAssignedShardsGrouped
argument_list|(
name|indices
argument_list|,
literal|true
argument_list|)
decl_stmt|;
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
for|for
control|(
name|ShardIterator
name|shardIterator
range|:
name|allAssignedShardsGrouped
control|)
block|{
for|for
control|(
name|ShardRouting
name|routing
range|:
name|shardIterator
operator|.
name|asUnordered
argument_list|()
control|)
block|{
if|if
condition|(
name|routing
operator|.
name|active
argument_list|()
condition|)
block|{
name|nodes
operator|.
name|add
argument_list|(
name|routing
operator|.
name|currentNodeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|nodes
return|;
block|}
DECL|method|numAssignedShards
specifier|protected
name|int
name|numAssignedShards
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|ClusterState
name|state
init|=
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
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|getState
argument_list|()
decl_stmt|;
name|GroupShardsIterator
name|allAssignedShardsGrouped
init|=
name|state
operator|.
name|routingTable
argument_list|()
operator|.
name|allAssignedShardsGrouped
argument_list|(
name|indices
argument_list|,
literal|true
argument_list|)
decl_stmt|;
return|return
name|allAssignedShardsGrouped
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

