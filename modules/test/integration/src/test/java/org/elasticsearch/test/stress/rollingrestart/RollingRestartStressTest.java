begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.stress.rollingrestart
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|stress
operator|.
name|rollingrestart
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
name|indices
operator|.
name|status
operator|.
name|IndexShardStatus
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
name|status
operator|.
name|IndicesStatusResponse
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
name|status
operator|.
name|ShardStatus
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
name|count
operator|.
name|CountResponse
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
name|get
operator|.
name|GetResponse
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
name|action
operator|.
name|search
operator|.
name|SearchType
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
name|UUID
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
name|FileSystemUtils
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
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|jsr166y
operator|.
name|ThreadLocalRandom
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
name|env
operator|.
name|NodeEnvironment
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
name|NodeBuilder
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
name|search
operator|.
name|SearchHit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|AtomicLong
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
name|*
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
name|xcontent
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
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|*
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
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|RollingRestartStressTest
specifier|public
class|class
name|RollingRestartStressTest
block|{
DECL|field|logger
specifier|private
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
DECL|field|numberOfShards
specifier|private
name|int
name|numberOfShards
init|=
literal|5
decl_stmt|;
DECL|field|numberOfReplicas
specifier|private
name|int
name|numberOfReplicas
init|=
literal|1
decl_stmt|;
DECL|field|numberOfNodes
specifier|private
name|int
name|numberOfNodes
init|=
literal|4
decl_stmt|;
DECL|field|textTokens
specifier|private
name|int
name|textTokens
init|=
literal|150
decl_stmt|;
DECL|field|numberOfFields
specifier|private
name|int
name|numberOfFields
init|=
literal|10
decl_stmt|;
DECL|field|initialNumberOfDocs
specifier|private
name|long
name|initialNumberOfDocs
init|=
literal|100000
decl_stmt|;
DECL|field|indexers
specifier|private
name|int
name|indexers
init|=
literal|0
decl_stmt|;
DECL|field|indexerThrottle
specifier|private
name|TimeValue
name|indexerThrottle
init|=
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|100
argument_list|)
decl_stmt|;
DECL|field|settings
specifier|private
name|Settings
name|settings
init|=
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
decl_stmt|;
DECL|field|period
specifier|private
name|TimeValue
name|period
init|=
name|TimeValue
operator|.
name|timeValueMinutes
argument_list|(
literal|20
argument_list|)
decl_stmt|;
DECL|field|clearNodeData
specifier|private
name|boolean
name|clearNodeData
init|=
literal|true
decl_stmt|;
DECL|field|client
specifier|private
name|Node
name|client
decl_stmt|;
DECL|field|indexCounter
specifier|private
name|AtomicLong
name|indexCounter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|idCounter
specifier|private
name|AtomicLong
name|idCounter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|method|numberOfNodes
specifier|public
name|RollingRestartStressTest
name|numberOfNodes
parameter_list|(
name|int
name|numberOfNodes
parameter_list|)
block|{
name|this
operator|.
name|numberOfNodes
operator|=
name|numberOfNodes
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|numberOfShards
specifier|public
name|RollingRestartStressTest
name|numberOfShards
parameter_list|(
name|int
name|numberOfShards
parameter_list|)
block|{
name|this
operator|.
name|numberOfShards
operator|=
name|numberOfShards
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|numberOfReplicas
specifier|public
name|RollingRestartStressTest
name|numberOfReplicas
parameter_list|(
name|int
name|numberOfReplicas
parameter_list|)
block|{
name|this
operator|.
name|numberOfReplicas
operator|=
name|numberOfReplicas
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|initialNumberOfDocs
specifier|public
name|RollingRestartStressTest
name|initialNumberOfDocs
parameter_list|(
name|long
name|initialNumberOfDocs
parameter_list|)
block|{
name|this
operator|.
name|initialNumberOfDocs
operator|=
name|initialNumberOfDocs
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|textTokens
specifier|public
name|RollingRestartStressTest
name|textTokens
parameter_list|(
name|int
name|textTokens
parameter_list|)
block|{
name|this
operator|.
name|textTokens
operator|=
name|textTokens
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|numberOfFields
specifier|public
name|RollingRestartStressTest
name|numberOfFields
parameter_list|(
name|int
name|numberOfFields
parameter_list|)
block|{
name|this
operator|.
name|numberOfFields
operator|=
name|numberOfFields
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|indexers
specifier|public
name|RollingRestartStressTest
name|indexers
parameter_list|(
name|int
name|indexers
parameter_list|)
block|{
name|this
operator|.
name|indexers
operator|=
name|indexers
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|indexerThrottle
specifier|public
name|RollingRestartStressTest
name|indexerThrottle
parameter_list|(
name|TimeValue
name|indexerThrottle
parameter_list|)
block|{
name|this
operator|.
name|indexerThrottle
operator|=
name|indexerThrottle
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|period
specifier|public
name|RollingRestartStressTest
name|period
parameter_list|(
name|TimeValue
name|period
parameter_list|)
block|{
name|this
operator|.
name|period
operator|=
name|period
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|cleanNodeData
specifier|public
name|RollingRestartStressTest
name|cleanNodeData
parameter_list|(
name|boolean
name|clearNodeData
parameter_list|)
block|{
name|this
operator|.
name|clearNodeData
operator|=
name|clearNodeData
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|settings
specifier|public
name|RollingRestartStressTest
name|settings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
index|[]
name|nodes
init|=
operator|new
name|Node
index|[
name|numberOfNodes
index|]
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
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|nodes
index|[
name|i
index|]
operator|=
name|NodeBuilder
operator|.
name|nodeBuilder
argument_list|()
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
operator|.
name|node
argument_list|()
expr_stmt|;
block|}
name|client
operator|=
name|NodeBuilder
operator|.
name|nodeBuilder
argument_list|()
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
operator|.
name|client
argument_list|(
literal|true
argument_list|)
operator|.
name|node
argument_list|()
expr_stmt|;
name|client
operator|.
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
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
name|numberOfShards
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_replicas"
argument_list|,
name|numberOfReplicas
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"********** [START] INDEXING INITIAL DOCS"
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|initialNumberOfDocs
condition|;
name|i
operator|++
control|)
block|{
name|indexDoc
argument_list|()
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"********** [DONE ] INDEXING INITIAL DOCS"
argument_list|)
expr_stmt|;
name|Indexer
index|[]
name|indexerThreads
init|=
operator|new
name|Indexer
index|[
name|indexers
index|]
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
name|indexerThreads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|indexerThreads
index|[
name|i
index|]
operator|=
operator|new
name|Indexer
argument_list|()
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
name|indexerThreads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|indexerThreads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|long
name|testStart
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// start doing the rolling restart
name|int
name|nodeIndex
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|File
name|nodeData
init|=
operator|(
operator|(
name|InternalNode
operator|)
name|nodes
index|[
name|nodeIndex
index|]
operator|)
operator|.
name|injector
argument_list|()
operator|.
name|getInstance
argument_list|(
name|NodeEnvironment
operator|.
name|class
argument_list|)
operator|.
name|nodeDataLocation
argument_list|()
decl_stmt|;
name|nodes
index|[
name|nodeIndex
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|clearNodeData
condition|)
block|{
name|FileSystemUtils
operator|.
name|deleteRecursively
argument_list|(
name|nodeData
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|ClusterHealthResponse
name|clusterHealth
init|=
name|client
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
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setWaitForNodes
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|numberOfNodes
operator|+
literal|0
comment|/* client node*/
argument_list|)
argument_list|)
operator|.
name|setWaitForRelocatingShards
argument_list|(
literal|0
argument_list|)
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
name|clusterHealth
operator|.
name|timedOut
argument_list|()
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"timed out waiting for green status...."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to execute cluster health...."
argument_list|)
expr_stmt|;
block|}
name|nodes
index|[
name|nodeIndex
index|]
operator|=
name|NodeBuilder
operator|.
name|nodeBuilder
argument_list|()
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
operator|.
name|node
argument_list|()
expr_stmt|;
try|try
block|{
name|ClusterHealthResponse
name|clusterHealth
init|=
name|client
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
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setWaitForNodes
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|numberOfNodes
operator|+
literal|1
comment|/* client node*/
argument_list|)
argument_list|)
operator|.
name|setWaitForRelocatingShards
argument_list|(
literal|0
argument_list|)
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
name|clusterHealth
operator|.
name|timedOut
argument_list|()
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"timed out waiting for green status...."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to execute cluster health...."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|++
name|nodeIndex
operator|==
name|nodes
operator|.
name|length
condition|)
block|{
name|nodeIndex
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|testStart
operator|)
operator|>
name|period
operator|.
name|millis
argument_list|()
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"test finished"
argument_list|)
expr_stmt|;
break|break;
block|}
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
name|indexerThreads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|indexerThreads
index|[
name|i
index|]
operator|.
name|close
operator|=
literal|true
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|indexerThrottle
operator|.
name|millis
argument_list|()
operator|+
literal|10000
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
name|indexerThreads
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
name|indexerThreads
index|[
name|i
index|]
operator|.
name|closed
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"thread not closed!"
argument_list|)
expr_stmt|;
block|}
block|}
name|client
operator|.
name|client
argument_list|()
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
comment|// check the status
name|IndicesStatusResponse
name|status
init|=
name|client
operator|.
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareStatus
argument_list|(
literal|"test"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexShardStatus
name|shardStatus
range|:
name|status
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
control|)
block|{
name|ShardStatus
name|shard
init|=
name|shardStatus
operator|.
name|shards
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"shard [{}], docs [{}]"
argument_list|,
name|shard
operator|.
name|shardId
argument_list|()
argument_list|,
name|shard
operator|.
name|getDocs
argument_list|()
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardStatus
name|shardStatu
range|:
name|shardStatus
control|)
block|{
if|if
condition|(
name|shard
operator|.
name|docs
argument_list|()
operator|.
name|numDocs
argument_list|()
operator|!=
name|shardStatu
operator|.
name|docs
argument_list|()
operator|.
name|numDocs
argument_list|()
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"shard doc number does not match!, got {} and {}"
argument_list|,
name|shard
operator|.
name|docs
argument_list|()
operator|.
name|numDocs
argument_list|()
argument_list|,
name|shardStatu
operator|.
name|docs
argument_list|()
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// check the count
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
operator|(
name|nodes
operator|.
name|length
operator|*
literal|5
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|CountResponse
name|count
init|=
name|client
operator|.
name|client
argument_list|()
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
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"indexed [{}], count [{}], [{}]"
argument_list|,
name|count
operator|.
name|count
argument_list|()
argument_list|,
name|indexCounter
operator|.
name|get
argument_list|()
argument_list|,
name|count
operator|.
name|count
argument_list|()
operator|==
name|indexCounter
operator|.
name|get
argument_list|()
condition|?
literal|"OK"
else|:
literal|"FAIL"
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|.
name|count
argument_list|()
operator|!=
name|indexCounter
operator|.
name|get
argument_list|()
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"count does not match!"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// scan all the docs, verify all have the same version based on the number of replicas
name|SearchResponse
name|searchResponse
init|=
name|client
operator|.
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setSearchType
argument_list|(
name|SearchType
operator|.
name|SCAN
argument_list|)
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|setSize
argument_list|(
literal|50
argument_list|)
operator|.
name|setScroll
argument_list|(
name|TimeValue
operator|.
name|timeValueMinutes
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Verifying versions for {} hits..."
argument_list|,
name|searchResponse
operator|.
name|hits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|searchResponse
operator|=
name|client
operator|.
name|client
argument_list|()
operator|.
name|prepareSearchScroll
argument_list|(
name|searchResponse
operator|.
name|scrollId
argument_list|()
argument_list|)
operator|.
name|setScroll
argument_list|(
name|TimeValue
operator|.
name|timeValueMinutes
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|failedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchHit
name|hit
range|:
name|searchResponse
operator|.
name|hits
argument_list|()
control|)
block|{
name|long
name|version
init|=
operator|-
literal|1
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
operator|(
name|numberOfReplicas
operator|+
literal|1
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|GetResponse
name|getResponse
init|=
name|client
operator|.
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
name|hit
operator|.
name|index
argument_list|()
argument_list|,
name|hit
operator|.
name|type
argument_list|()
argument_list|,
name|hit
operator|.
name|id
argument_list|()
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
name|version
operator|==
operator|-
literal|1
condition|)
block|{
name|version
operator|=
name|getResponse
operator|.
name|version
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|version
operator|!=
name|getResponse
operator|.
name|version
argument_list|()
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Doc {} has different version numbers {} and {}"
argument_list|,
name|hit
operator|.
name|id
argument_list|()
argument_list|,
name|version
argument_list|,
name|getResponse
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|searchResponse
operator|.
name|hits
argument_list|()
operator|.
name|hits
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
break|break;
block|}
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"Done verifying versions"
argument_list|)
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
for|for
control|(
name|Node
name|node
range|:
name|nodes
control|)
block|{
name|node
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|Indexer
specifier|private
class|class
name|Indexer
extends|extends
name|Thread
block|{
DECL|field|close
specifier|volatile
name|boolean
name|close
init|=
literal|false
decl_stmt|;
DECL|field|closed
specifier|volatile
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|method|run
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|close
condition|)
block|{
name|closed
operator|=
literal|true
expr_stmt|;
return|return;
block|}
try|try
block|{
name|indexDoc
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|indexerThrottle
operator|.
name|millis
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to index / sleep"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|indexDoc
specifier|private
name|void
name|indexDoc
parameter_list|()
throws|throws
name|Exception
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|XContentBuilder
name|json
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"value"
operator|+
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|fields
init|=
name|Math
operator|.
name|abs
argument_list|(
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
operator|%
name|numberOfFields
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
name|fields
condition|;
name|i
operator|++
control|)
block|{
name|json
operator|.
name|field
argument_list|(
literal|"num_"
operator|+
name|i
argument_list|,
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextDouble
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|tokens
init|=
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextInt
argument_list|()
operator|%
name|textTokens
decl_stmt|;
name|sb
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
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
name|tokens
condition|;
name|j
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|UUID
operator|.
name|randomBase64UUID
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|json
operator|.
name|field
argument_list|(
literal|"text_"
operator|+
name|i
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|json
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|String
name|id
init|=
name|Long
operator|.
name|toString
argument_list|(
name|idCounter
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
name|id
argument_list|)
operator|.
name|setCreate
argument_list|(
literal|true
argument_list|)
operator|.
name|setSource
argument_list|(
name|json
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|indexCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
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
name|System
operator|.
name|setProperty
argument_list|(
literal|"es.logger.prefix"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|Settings
name|settings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.shard.check_index"
argument_list|,
literal|true
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
decl_stmt|;
name|RollingRestartStressTest
name|test
init|=
operator|new
name|RollingRestartStressTest
argument_list|()
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
operator|.
name|numberOfNodes
argument_list|(
literal|4
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|5
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|1
argument_list|)
operator|.
name|initialNumberOfDocs
argument_list|(
literal|1000
argument_list|)
operator|.
name|textTokens
argument_list|(
literal|150
argument_list|)
operator|.
name|numberOfFields
argument_list|(
literal|10
argument_list|)
operator|.
name|cleanNodeData
argument_list|(
literal|false
argument_list|)
operator|.
name|indexers
argument_list|(
literal|5
argument_list|)
operator|.
name|indexerThrottle
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|50
argument_list|)
argument_list|)
operator|.
name|period
argument_list|(
name|TimeValue
operator|.
name|timeValueMinutes
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|test
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

