begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.store
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
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
name|IndexShardStats
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
name|IndexStats
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
name|admin
operator|.
name|indices
operator|.
name|stats
operator|.
name|ShardStats
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
name|BulkRequestBuilder
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
name|search
operator|.
name|SearchResponse
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
name|xcontent
operator|.
name|XContentBuilder
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
name|SegmentsStats
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|Plugin
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
name|SearchHit
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
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|transport
operator|.
name|MockTransportService
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
name|ConnectTransportException
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
name|TransportRequest
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
name|TransportRequestOptions
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
name|IOException
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
name|Collection
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
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
name|stream
operator|.
name|Collectors
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
name|xcontent
operator|.
name|XContentFactory
operator|.
name|jsonBuilder
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
name|query
operator|.
name|QueryBuilders
operator|.
name|termQuery
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
name|assertHitCount
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
name|assertSearchResponse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
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

begin_class
annotation|@
name|ESIntegTestCase
operator|.
name|ClusterScope
argument_list|(
name|scope
operator|=
name|ESIntegTestCase
operator|.
name|Scope
operator|.
name|SUITE
argument_list|,
name|numDataNodes
operator|=
literal|2
argument_list|,
name|supportsDedicatedMasters
operator|=
literal|false
argument_list|,
name|numClientNodes
operator|=
literal|1
argument_list|,
name|transportClientRatio
operator|=
literal|0.0
argument_list|)
DECL|class|ExceptionRetryIT
specifier|public
class|class
name|ExceptionRetryIT
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Override
DECL|method|nodePlugins
specifier|protected
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|nodePlugins
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|MockTransportService
operator|.
name|TestPlugin
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|beforeIndexDeletion
specifier|protected
name|void
name|beforeIndexDeletion
parameter_list|()
block|{
comment|// a write operation might still be in flight when the test has finished
comment|// so we should not check the operation counter here
block|}
comment|/**      * Tests retry mechanism when indexing. If an exception occurs when indexing then the indexing request is tried again before finally      * failing. If auto generated ids are used this must not lead to duplicate ids      * see https://github.com/elastic/elasticsearch/issues/8788      */
DECL|method|testRetryDueToExceptionOnNetworkLayer
specifier|public
name|void
name|testRetryDueToExceptionOnNetworkLayer
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
specifier|final
name|AtomicBoolean
name|exceptionThrown
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|int
name|numDocs
init|=
name|scaledRandomIntBetween
argument_list|(
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|Client
name|client
init|=
name|internalCluster
argument_list|()
operator|.
name|coordOnlyNodeClient
argument_list|()
decl_stmt|;
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
name|get
argument_list|()
decl_stmt|;
name|NodeStats
name|unluckyNode
init|=
name|randomFrom
argument_list|(
name|nodeStats
operator|.
name|getNodes
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
operator|.
name|getNode
argument_list|()
operator|.
name|isDataNode
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
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
literal|"index"
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
literal|"index.number_of_replicas"
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|(
literal|"index"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"unlucky node: {}"
argument_list|,
name|unluckyNode
operator|.
name|getNode
argument_list|()
argument_list|)
expr_stmt|;
comment|//create a transport service that throws a ConnectTransportException for one bulk request and therefore triggers a retry.
for|for
control|(
name|NodeStats
name|dataNode
range|:
name|nodeStats
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|MockTransportService
name|mockTransportService
init|=
operator|(
operator|(
name|MockTransportService
operator|)
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|TransportService
operator|.
name|class
argument_list|,
name|dataNode
operator|.
name|getNode
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|)
decl_stmt|;
name|mockTransportService
operator|.
name|addDelegate
argument_list|(
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|TransportService
operator|.
name|class
argument_list|,
name|unluckyNode
operator|.
name|getNode
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
operator|new
name|MockTransportService
operator|.
name|DelegateTransport
argument_list|(
name|mockTransportService
operator|.
name|original
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|sendRequest
parameter_list|(
name|Connection
name|connection
parameter_list|,
name|long
name|requestId
parameter_list|,
name|String
name|action
parameter_list|,
name|TransportRequest
name|request
parameter_list|,
name|TransportRequestOptions
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|sendRequest
argument_list|(
name|connection
argument_list|,
name|requestId
argument_list|,
name|action
argument_list|,
name|request
argument_list|,
name|options
argument_list|)
expr_stmt|;
if|if
condition|(
name|action
operator|.
name|equals
argument_list|(
name|TransportShardBulkAction
operator|.
name|ACTION_NAME
argument_list|)
operator|&&
name|exceptionThrown
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Throw ConnectTransportException"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ConnectTransportException
argument_list|(
name|connection
operator|.
name|getNode
argument_list|()
argument_list|,
name|action
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|BulkRequestBuilder
name|bulkBuilder
init|=
name|client
operator|.
name|prepareBulk
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|XContentBuilder
name|doc
init|=
literal|null
decl_stmt|;
name|doc
operator|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|bulkBuilder
operator|.
name|add
argument_list|(
name|client
operator|.
name|prepareIndex
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|)
operator|.
name|setSource
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|BulkResponse
name|response
init|=
name|bulkBuilder
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|hasFailures
argument_list|()
condition|)
block|{
for|for
control|(
name|BulkItemResponse
name|singleIndexRespons
range|:
name|response
operator|.
name|getItems
argument_list|()
control|)
block|{
if|if
condition|(
name|singleIndexRespons
operator|.
name|isFailed
argument_list|()
condition|)
block|{
name|fail
argument_list|(
literal|"None of the bulk items should fail but got "
operator|+
name|singleIndexRespons
operator|.
name|getFailureMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|refresh
argument_list|()
expr_stmt|;
name|SearchResponse
name|searchResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"index"
argument_list|)
operator|.
name|setSize
argument_list|(
name|numDocs
operator|*
literal|2
argument_list|)
operator|.
name|addStoredField
argument_list|(
literal|"_id"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|uniqueIds
init|=
operator|new
name|HashSet
argument_list|()
decl_stmt|;
name|long
name|dupCounter
init|=
literal|0
decl_stmt|;
name|boolean
name|found_duplicate_already
init|=
literal|false
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
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getHits
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|uniqueIds
operator|.
name|add
argument_list|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getHits
argument_list|()
index|[
name|i
index|]
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|found_duplicate_already
condition|)
block|{
name|SearchResponse
name|dupIdResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"index"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|termQuery
argument_list|(
literal|"_id"
argument_list|,
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getHits
argument_list|()
index|[
name|i
index|]
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setExplain
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|dupIdResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"found a duplicate id:"
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchHit
name|hit
range|:
name|dupIdResponse
operator|.
name|getHits
argument_list|()
control|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Doc {} was found on shard {}"
argument_list|,
name|hit
operator|.
name|getId
argument_list|()
argument_list|,
name|hit
operator|.
name|getShard
argument_list|()
operator|.
name|getShardId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"will not print anymore in case more duplicates are found."
argument_list|)
expr_stmt|;
name|found_duplicate_already
operator|=
literal|true
expr_stmt|;
block|}
name|dupCounter
operator|++
expr_stmt|;
block|}
block|}
name|assertSearchResponse
argument_list|(
name|searchResponse
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|dupCounter
argument_list|,
name|equalTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
name|IndicesStatsResponse
name|index
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
argument_list|(
literal|"index"
argument_list|)
operator|.
name|clear
argument_list|()
operator|.
name|setSegments
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|IndexStats
name|indexStats
init|=
name|index
operator|.
name|getIndex
argument_list|(
literal|"index"
argument_list|)
decl_stmt|;
name|long
name|maxUnsafeAutoIdTimestamp
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
for|for
control|(
name|IndexShardStats
name|indexShardStats
range|:
name|indexStats
control|)
block|{
for|for
control|(
name|ShardStats
name|shardStats
range|:
name|indexShardStats
control|)
block|{
name|SegmentsStats
name|segments
init|=
name|shardStats
operator|.
name|getStats
argument_list|()
operator|.
name|getSegments
argument_list|()
decl_stmt|;
name|maxUnsafeAutoIdTimestamp
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxUnsafeAutoIdTimestamp
argument_list|,
name|segments
operator|.
name|getMaxUnsafeAutoIdTimestamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"exception must have been thrown otherwise setup is broken"
argument_list|,
name|exceptionThrown
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"maxUnsafeAutoIdTimestamp must be> than 0 we have at least one retry"
argument_list|,
name|maxUnsafeAutoIdTimestamp
operator|>
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

