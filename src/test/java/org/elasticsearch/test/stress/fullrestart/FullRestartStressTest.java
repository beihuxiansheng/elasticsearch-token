begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.stress.fullrestart
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|stress
operator|.
name|fullrestart
package|;
end_package

begin_import
import|import
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
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|matchAllQuery
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|FullRestartStressTest
specifier|public
class|class
name|FullRestartStressTest
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
DECL|field|numberOfNodes
specifier|private
name|int
name|numberOfNodes
init|=
literal|4
decl_stmt|;
DECL|field|clearNodeWork
specifier|private
name|boolean
name|clearNodeWork
init|=
literal|false
decl_stmt|;
DECL|field|numberOfIndices
specifier|private
name|int
name|numberOfIndices
init|=
literal|5
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
DECL|field|bulkSize
specifier|private
name|int
name|bulkSize
init|=
literal|1000
decl_stmt|;
DECL|field|numberOfDocsPerRound
specifier|private
name|int
name|numberOfDocsPerRound
init|=
literal|50000
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
DECL|field|indexCounter
specifier|private
name|AtomicLong
name|indexCounter
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|method|numberOfNodes
specifier|public
name|FullRestartStressTest
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
DECL|method|numberOfIndices
specifier|public
name|FullRestartStressTest
name|numberOfIndices
parameter_list|(
name|int
name|numberOfIndices
parameter_list|)
block|{
name|this
operator|.
name|numberOfIndices
operator|=
name|numberOfIndices
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|textTokens
specifier|public
name|FullRestartStressTest
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
name|FullRestartStressTest
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
DECL|method|bulkSize
specifier|public
name|FullRestartStressTest
name|bulkSize
parameter_list|(
name|int
name|bulkSize
parameter_list|)
block|{
name|this
operator|.
name|bulkSize
operator|=
name|bulkSize
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|numberOfDocsPerRound
specifier|public
name|FullRestartStressTest
name|numberOfDocsPerRound
parameter_list|(
name|int
name|numberOfDocsPerRound
parameter_list|)
block|{
name|this
operator|.
name|numberOfDocsPerRound
operator|=
name|numberOfDocsPerRound
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|settings
specifier|public
name|FullRestartStressTest
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
DECL|method|period
specifier|public
name|FullRestartStressTest
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
DECL|method|clearNodeWork
specifier|public
name|FullRestartStressTest
name|clearNodeWork
parameter_list|(
name|boolean
name|clearNodeWork
parameter_list|)
block|{
name|this
operator|.
name|clearNodeWork
operator|=
name|clearNodeWork
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
name|long
name|numberOfRounds
init|=
literal|0
decl_stmt|;
name|long
name|testStart
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
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
name|Node
name|client
init|=
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
decl_stmt|;
comment|// verify that the indices are there
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numberOfIndices
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
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
operator|+
name|i
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// might already exists, fine
block|}
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"*** Waiting for GREEN status"
argument_list|)
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
literal|"*** index_count [{}], expected_count [{}]"
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
argument_list|)
expr_stmt|;
comment|// verify count
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
name|count
operator|=
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
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"index_count [{}], expected_count [{}]"
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
literal|"!!! count does not match, index_count [{}], expected_count [{}]"
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
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|Exception
argument_list|(
literal|"failed test, count does not match..."
argument_list|)
throw|;
block|}
block|}
comment|// verify search
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
comment|// do a search with norms field, so we don't rely on match all filtering cache
name|SearchResponse
name|search
init|=
name|client
operator|.
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
operator|.
name|normsField
argument_list|(
literal|"field"
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
name|debug
argument_list|(
literal|"index_count [{}], expected_count [{}]"
argument_list|,
name|search
operator|.
name|hits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|indexCounter
operator|.
name|get
argument_list|()
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
literal|"!!! search does not match, index_count [{}], expected_count [{}]"
argument_list|,
name|search
operator|.
name|hits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|indexCounter
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|Exception
argument_list|(
literal|"failed test, count does not match..."
argument_list|)
throw|;
block|}
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"*** ROUND {}"
argument_list|,
operator|++
name|numberOfRounds
argument_list|)
expr_stmt|;
comment|// bulk index data
name|int
name|numberOfBulks
init|=
name|numberOfDocsPerRound
operator|/
name|bulkSize
decl_stmt|;
for|for
control|(
name|int
name|b
init|=
literal|0
init|;
name|b
operator|<
name|numberOfBulks
condition|;
name|b
operator|++
control|)
block|{
name|BulkRequestBuilder
name|bulk
init|=
name|client
operator|.
name|client
argument_list|()
operator|.
name|prepareBulk
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|bulkSize
condition|;
name|k
operator|++
control|)
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
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextInt
argument_list|()
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
name|bulk
operator|.
name|add
argument_list|(
name|Requests
operator|.
name|indexRequest
argument_list|(
literal|"test"
operator|+
operator|(
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
name|numberOfIndices
operator|)
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|source
argument_list|(
name|json
argument_list|)
argument_list|)
expr_stmt|;
name|indexCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|bulk
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
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareGatewaySnapshot
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
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
name|File
index|[]
name|nodeDatas
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
name|NodeEnvironment
operator|.
name|class
argument_list|)
operator|.
name|nodeDataLocations
argument_list|()
decl_stmt|;
name|node
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|clearNodeWork
operator|&&
operator|!
name|settings
operator|.
name|get
argument_list|(
literal|"gateway.type"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"local"
argument_list|)
condition|)
block|{
name|FileSystemUtils
operator|.
name|deleteRecursively
argument_list|(
name|nodeDatas
argument_list|)
expr_stmt|;
block|}
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
literal|"test finished, full_restart_rounds [{}]"
argument_list|,
name|numberOfRounds
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
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
name|int
name|numberOfNodes
init|=
literal|2
decl_stmt|;
name|Settings
name|settings
init|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.shard.check_on_startup"
argument_list|,
literal|true
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
literal|"gateway.recover_after_nodes"
argument_list|,
name|numberOfNodes
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.data"
argument_list|,
literal|"data/data1,data/data2"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|FullRestartStressTest
name|test
init|=
operator|new
name|FullRestartStressTest
argument_list|()
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
operator|.
name|period
argument_list|(
name|TimeValue
operator|.
name|timeValueMinutes
argument_list|(
literal|20
argument_list|)
argument_list|)
operator|.
name|clearNodeWork
argument_list|(
literal|false
argument_list|)
comment|// only applies to shared gateway
operator|.
name|numberOfNodes
argument_list|(
name|numberOfNodes
argument_list|)
operator|.
name|numberOfIndices
argument_list|(
literal|1
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
name|bulkSize
argument_list|(
literal|1000
argument_list|)
operator|.
name|numberOfDocsPerRound
argument_list|(
literal|10000
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

