begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.benchmark.search.child
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|benchmark
operator|.
name|search
operator|.
name|child
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
name|health
operator|.
name|ClusterHealthResponse
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
name|client
operator|.
name|Requests
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
name|StopWatch
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
name|SizeValue
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
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
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
name|Random
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Requests
operator|.
name|createIndexRequest
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
name|FilterBuilders
operator|.
name|hasChildFilter
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
name|*
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ChildSearchAndIndexingBenchmark
specifier|public
class|class
name|ChildSearchAndIndexingBenchmark
block|{
DECL|field|COUNT
specifier|static
name|long
name|COUNT
init|=
name|SizeValue
operator|.
name|parseSizeValue
argument_list|(
literal|"1m"
argument_list|)
operator|.
name|singles
argument_list|()
decl_stmt|;
DECL|field|CHILD_COUNT
specifier|static
name|int
name|CHILD_COUNT
init|=
literal|5
decl_stmt|;
DECL|field|BATCH
specifier|static
name|int
name|BATCH
init|=
literal|100
decl_stmt|;
DECL|field|QUERY_COUNT
specifier|static
name|int
name|QUERY_COUNT
init|=
literal|50
decl_stmt|;
DECL|field|indexName
specifier|static
name|String
name|indexName
init|=
literal|"test"
decl_stmt|;
DECL|field|random
specifier|static
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|Settings
name|settings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.engine.robin.refreshInterval"
argument_list|,
literal|"-1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"gateway.type"
argument_list|,
literal|"local"
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|clusterName
init|=
name|ChildSearchAndIndexingBenchmark
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|Node
name|node1
init|=
name|nodeBuilder
argument_list|()
operator|.
name|settings
argument_list|(
name|settingsBuilder
argument_list|()
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
literal|"node1"
argument_list|)
argument_list|)
operator|.
name|clusterName
argument_list|(
name|clusterName
argument_list|)
operator|.
name|node
argument_list|()
decl_stmt|;
name|Client
name|client
init|=
name|node1
operator|.
name|client
argument_list|()
decl_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|(
name|indexName
argument_list|)
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setTimeout
argument_list|(
literal|"10s"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
try|try
block|{
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|create
argument_list|(
name|createIndexRequest
argument_list|(
name|indexName
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|preparePutMapping
argument_list|(
name|indexName
argument_list|)
operator|.
name|setType
argument_list|(
literal|"child"
argument_list|)
operator|.
name|setSource
argument_list|(
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"child"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_parent"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"parent"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
name|StopWatch
name|stopWatch
init|=
operator|new
name|StopWatch
argument_list|()
operator|.
name|start
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> Indexing ["
operator|+
name|COUNT
operator|+
literal|"] parent document and ["
operator|+
operator|(
name|COUNT
operator|*
name|CHILD_COUNT
operator|)
operator|+
literal|" child documents"
argument_list|)
expr_stmt|;
name|long
name|ITERS
init|=
name|COUNT
operator|/
name|BATCH
decl_stmt|;
name|long
name|i
init|=
literal|1
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<=
name|ITERS
condition|;
name|i
operator|++
control|)
block|{
name|BulkRequestBuilder
name|request
init|=
name|client
operator|.
name|prepareBulk
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|BATCH
condition|;
name|j
operator|++
control|)
block|{
name|counter
operator|++
expr_stmt|;
name|request
operator|.
name|add
argument_list|(
name|Requests
operator|.
name|indexRequest
argument_list|(
name|indexName
argument_list|)
operator|.
name|type
argument_list|(
literal|"parent"
argument_list|)
operator|.
name|id
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|counter
argument_list|)
argument_list|)
operator|.
name|source
argument_list|(
name|parentSource
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|counter
argument_list|)
argument_list|,
literal|"test"
operator|+
name|counter
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|CHILD_COUNT
condition|;
name|k
operator|++
control|)
block|{
name|request
operator|.
name|add
argument_list|(
name|Requests
operator|.
name|indexRequest
argument_list|(
name|indexName
argument_list|)
operator|.
name|type
argument_list|(
literal|"child"
argument_list|)
operator|.
name|id
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|counter
argument_list|)
operator|+
literal|"_"
operator|+
name|k
argument_list|)
operator|.
name|parent
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|counter
argument_list|)
argument_list|)
operator|.
name|source
argument_list|(
name|childSource
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|counter
argument_list|)
argument_list|,
literal|"tag"
operator|+
name|k
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|BulkResponse
name|response
init|=
name|request
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
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
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"--> failures..."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
operator|(
name|i
operator|*
name|BATCH
operator|)
operator|%
literal|10000
operator|)
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> Indexed "
operator|+
operator|(
name|i
operator|*
name|BATCH
operator|)
operator|*
operator|(
literal|1
operator|+
name|CHILD_COUNT
operator|)
operator|+
literal|" took "
operator|+
name|stopWatch
operator|.
name|stop
argument_list|()
operator|.
name|lastTaskTime
argument_list|()
argument_list|)
expr_stmt|;
name|stopWatch
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> Indexing took "
operator|+
name|stopWatch
operator|.
name|totalTime
argument_list|()
operator|+
literal|", TPS "
operator|+
operator|(
operator|(
call|(
name|double
call|)
argument_list|(
name|COUNT
operator|*
operator|(
literal|1
operator|+
name|CHILD_COUNT
operator|)
argument_list|)
operator|)
operator|/
name|stopWatch
operator|.
name|totalTime
argument_list|()
operator|.
name|secondsFrac
argument_list|()
operator|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> Index already exists, ignoring indexing phase, waiting for green"
argument_list|)
expr_stmt|;
name|ClusterHealthResponse
name|clusterHealthResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|(
name|indexName
argument_list|)
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setTimeout
argument_list|(
literal|"10m"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|clusterHealthResponse
operator|.
name|isTimedOut
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"--> Timed out waiting for cluster health"
argument_list|)
expr_stmt|;
block|}
block|}
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareRefresh
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> Number of docs in index: "
operator|+
name|client
operator|.
name|prepareCount
argument_list|()
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|SearchThread
name|searchThread
init|=
operator|new
name|SearchThread
argument_list|(
name|client
argument_list|)
decl_stmt|;
operator|new
name|Thread
argument_list|(
name|searchThread
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|IndexThread
name|indexThread
init|=
operator|new
name|IndexThread
argument_list|(
name|client
argument_list|)
decl_stmt|;
operator|new
name|Thread
argument_list|(
name|indexThread
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|System
operator|.
name|in
operator|.
name|read
argument_list|()
expr_stmt|;
name|indexThread
operator|.
name|stop
argument_list|()
expr_stmt|;
name|searchThread
operator|.
name|stop
argument_list|()
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|node1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|parentSource
specifier|private
specifier|static
name|XContentBuilder
name|parentSource
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|nameValue
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
name|nameValue
argument_list|)
operator|.
name|endObject
argument_list|()
return|;
block|}
DECL|method|childSource
specifier|private
specifier|static
name|XContentBuilder
name|childSource
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|tag
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|)
operator|.
name|field
argument_list|(
literal|"tag"
argument_list|,
name|tag
argument_list|)
operator|.
name|endObject
argument_list|()
return|;
block|}
DECL|class|IndexThread
specifier|static
class|class
name|IndexThread
implements|implements
name|Runnable
block|{
DECL|field|client
specifier|private
specifier|final
name|Client
name|client
decl_stmt|;
DECL|field|run
specifier|private
specifier|volatile
name|boolean
name|run
init|=
literal|true
decl_stmt|;
DECL|method|IndexThread
name|IndexThread
parameter_list|(
name|Client
name|client
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|run
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|run
operator|&&
name|i
operator|<
name|COUNT
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|client
operator|.
name|prepareIndex
argument_list|(
name|indexName
argument_list|,
literal|"parent"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|setSource
argument_list|(
name|parentSource
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"test"
operator|+
name|i
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|CHILD_COUNT
condition|;
name|j
operator|++
control|)
block|{
name|client
operator|.
name|prepareIndex
argument_list|(
name|indexName
argument_list|,
literal|"child"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
operator|+
literal|"_"
operator|+
name|j
argument_list|)
operator|.
name|setParent
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|setSource
argument_list|(
name|childSource
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|j
argument_list|)
argument_list|,
literal|"tag"
operator|+
name|j
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareRefresh
argument_list|(
name|indexName
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|500
operator|==
literal|0
condition|)
block|{
name|NodesStatsResponse
name|statsResponse
init|=
name|client
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
name|clear
argument_list|()
operator|.
name|setIndices
argument_list|(
literal|true
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Deleted docs: "
operator|+
name|statsResponse
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getIndices
argument_list|()
operator|.
name|getDocs
argument_list|()
operator|.
name|getDeleted
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|stop
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|run
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|class|SearchThread
specifier|static
class|class
name|SearchThread
implements|implements
name|Runnable
block|{
DECL|field|client
specifier|private
specifier|final
name|Client
name|client
decl_stmt|;
DECL|field|run
specifier|private
specifier|volatile
name|boolean
name|run
init|=
literal|true
decl_stmt|;
DECL|method|SearchThread
name|SearchThread
parameter_list|(
name|Client
name|client
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|run
condition|)
block|{
try|try
block|{
name|long
name|totalQueryTime
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|QUERY_COUNT
condition|;
name|j
operator|++
control|)
block|{
name|SearchResponse
name|searchResponse
init|=
name|client
operator|.
name|prepareSearch
argument_list|(
name|indexName
argument_list|)
operator|.
name|setQuery
argument_list|(
name|filteredQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|,
name|hasChildFilter
argument_list|(
literal|"child"
argument_list|,
name|termQuery
argument_list|(
literal|"tag"
argument_list|,
literal|"tag"
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|CHILD_COUNT
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|searchResponse
operator|.
name|getFailedShards
argument_list|()
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Search Failures "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|searchResponse
operator|.
name|getShardFailures
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
operator|!=
name|COUNT
condition|)
block|{
comment|//                            System.err.println("--> mismatch on hits [" + j + "], got [" + searchResponse.getHits().totalHits() + "], expected [" + COUNT + "]");
block|}
name|totalQueryTime
operator|+=
name|searchResponse
operator|.
name|getTookInMillis
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> has_child filter with term filter Query Avg: "
operator|+
operator|(
name|totalQueryTime
operator|/
name|QUERY_COUNT
operator|)
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
name|totalQueryTime
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|QUERY_COUNT
condition|;
name|j
operator|++
control|)
block|{
name|SearchResponse
name|searchResponse
init|=
name|client
operator|.
name|prepareSearch
argument_list|(
name|indexName
argument_list|)
operator|.
name|setQuery
argument_list|(
name|filteredQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|,
name|hasChildFilter
argument_list|(
literal|"child"
argument_list|,
name|matchAllQuery
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|searchResponse
operator|.
name|getFailedShards
argument_list|()
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Search Failures "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|searchResponse
operator|.
name|getShardFailures
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|long
name|expected
init|=
operator|(
name|COUNT
operator|/
name|BATCH
operator|)
operator|*
name|BATCH
decl_stmt|;
if|if
condition|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
operator|!=
name|expected
condition|)
block|{
comment|//                            System.err.println("--> mismatch on hits [" + j + "], got [" + searchResponse.getHits().totalHits() + "], expected [" + expected + "]");
block|}
name|totalQueryTime
operator|+=
name|searchResponse
operator|.
name|getTookInMillis
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> has_child filter with match_all child query, Query Avg: "
operator|+
operator|(
name|totalQueryTime
operator|/
name|QUERY_COUNT
operator|)
operator|+
literal|"ms"
argument_list|)
expr_stmt|;
name|NodesStatsResponse
name|statsResponse
init|=
name|client
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
name|setJvm
argument_list|(
literal|true
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> Committed heap size: "
operator|+
name|statsResponse
operator|.
name|getNodes
argument_list|()
index|[
literal|0
index|]
operator|.
name|getJvm
argument_list|()
operator|.
name|getMem
argument_list|()
operator|.
name|getHeapCommitted
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"--> Used heap size: "
operator|+
name|statsResponse
operator|.
name|getNodes
argument_list|()
index|[
literal|0
index|]
operator|.
name|getJvm
argument_list|()
operator|.
name|getMem
argument_list|()
operator|.
name|getHeapUsed
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|stop
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|run
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

