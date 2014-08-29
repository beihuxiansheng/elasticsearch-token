begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.basic
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|basic
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|AtomicReader
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
name|index
operator|.
name|DirectoryReader
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
name|English
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|admin
operator|.
name|indices
operator|.
name|refresh
operator|.
name|RefreshResponse
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
name|index
operator|.
name|IndexResponse
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
name|SearchPhaseExecutionException
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
name|settings
operator|.
name|ImmutableSettings
operator|.
name|Builder
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
name|XContentFactory
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
name|query
operator|.
name|QueryBuilders
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
name|elasticsearch
operator|.
name|test
operator|.
name|engine
operator|.
name|MockInternalEngine
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
name|engine
operator|.
name|ThrowingAtomicReaderWrapper
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
name|junit
operator|.
name|annotations
operator|.
name|TestLogging
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
name|store
operator|.
name|MockDirectoryHelper
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
name|store
operator|.
name|MockFSDirectoryService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
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
name|IOException
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

begin_class
DECL|class|SearchWithRandomExceptionsTests
specifier|public
class|class
name|SearchWithRandomExceptionsTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
DECL|method|testRandomDirectoryIOExceptions
specifier|public
name|void
name|testRandomDirectoryIOExceptions
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|ExecutionException
block|{
name|String
name|mapping
init|=
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
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"test"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"not_analyzed"
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
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
specifier|final
name|double
name|exceptionRate
decl_stmt|;
specifier|final
name|double
name|exceptionOnOpenRate
decl_stmt|;
if|if
condition|(
name|frequently
argument_list|()
condition|)
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|exceptionOnOpenRate
operator|=
literal|1.0
operator|/
name|between
argument_list|(
literal|5
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|exceptionRate
operator|=
literal|0.0d
expr_stmt|;
block|}
else|else
block|{
name|exceptionRate
operator|=
literal|1.0
operator|/
name|between
argument_list|(
literal|5
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|exceptionOnOpenRate
operator|=
literal|0.0d
expr_stmt|;
block|}
block|}
else|else
block|{
name|exceptionOnOpenRate
operator|=
literal|1.0
operator|/
name|between
argument_list|(
literal|5
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|exceptionRate
operator|=
literal|1.0
operator|/
name|between
argument_list|(
literal|5
argument_list|,
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// rarely no exception
name|exceptionRate
operator|=
literal|0d
expr_stmt|;
name|exceptionOnOpenRate
operator|=
literal|0d
expr_stmt|;
block|}
name|boolean
name|createIndexWithoutErrors
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|long
name|numInitialDocs
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|createIndexWithoutErrors
condition|)
block|{
name|Builder
name|settings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.number_of_replicas"
argument_list|,
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|MockFSDirectoryService
operator|.
name|CHECK_INDEX_ON_CLOSE
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
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"creating index: [test] using settings: [{}]"
argument_list|,
name|settings
operator|.
name|build
argument_list|()
operator|.
name|getAsMap
argument_list|()
argument_list|)
expr_stmt|;
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
name|settings
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
name|mapping
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|numInitialDocs
operator|=
name|between
argument_list|(
literal|10
argument_list|,
literal|100
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
name|numInitialDocs
condition|;
name|i
operator|++
control|)
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"initial"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"test"
argument_list|,
literal|"init"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
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
argument_list|(
literal|"test"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareFlush
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setWaitIfOngoing
argument_list|(
literal|true
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
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
operator|.
name|execute
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
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
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|MockFSDirectoryService
operator|.
name|CHECK_INDEX_ON_CLOSE
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
name|MockDirectoryHelper
operator|.
name|RANDOM_IO_EXCEPTION_RATE
argument_list|,
name|exceptionRate
argument_list|)
operator|.
name|put
argument_list|(
name|MockDirectoryHelper
operator|.
name|RANDOM_IO_EXCEPTION_RATE_ON_OPEN
argument_list|,
name|exceptionOnOpenRate
argument_list|)
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
literal|"test"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Builder
name|settings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.number_of_replicas"
argument_list|,
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|MockFSDirectoryService
operator|.
name|CHECK_INDEX_ON_CLOSE
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
name|MockDirectoryHelper
operator|.
name|RANDOM_IO_EXCEPTION_RATE
argument_list|,
name|exceptionRate
argument_list|)
operator|.
name|put
argument_list|(
name|MockDirectoryHelper
operator|.
name|RANDOM_IO_EXCEPTION_RATE_ON_OPEN
argument_list|,
name|exceptionOnOpenRate
argument_list|)
decl_stmt|;
comment|// we cannot expect that the index will be valid
name|logger
operator|.
name|info
argument_list|(
literal|"creating index: [test] using settings: [{}]"
argument_list|,
name|settings
operator|.
name|build
argument_list|()
operator|.
name|getAsMap
argument_list|()
argument_list|)
expr_stmt|;
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
name|settings
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
name|mapping
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
name|ClusterHealthResponse
name|clusterHealthResponse
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
name|health
argument_list|(
name|Requests
operator|.
name|clusterHealthRequest
argument_list|()
operator|.
name|waitForYellowStatus
argument_list|()
operator|.
name|timeout
argument_list|(
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
comment|// it's OK to timeout here
specifier|final
name|int
name|numDocs
decl_stmt|;
specifier|final
name|boolean
name|expectAllShardsFailed
decl_stmt|;
if|if
condition|(
name|clusterHealthResponse
operator|.
name|isTimedOut
argument_list|()
condition|)
block|{
comment|/* some seeds just won't let you create the index at all and we enter a ping-pong mode              * trying one node after another etc. that is ok but we need to make sure we don't wait              * forever when indexing documents so we set numDocs = 1 and expecte all shards to fail              * when we search below.*/
name|logger
operator|.
name|info
argument_list|(
literal|"ClusterHealth timed out - only index one doc and expect searches to fail"
argument_list|)
expr_stmt|;
name|numDocs
operator|=
literal|1
expr_stmt|;
name|expectAllShardsFailed
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|numDocs
operator|=
name|between
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|expectAllShardsFailed
operator|=
literal|false
expr_stmt|;
block|}
name|long
name|numCreated
init|=
literal|0
decl_stmt|;
name|boolean
index|[]
name|added
init|=
operator|new
name|boolean
index|[
name|numDocs
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|added
index|[
name|i
index|]
operator|=
literal|false
expr_stmt|;
try|try
block|{
name|IndexResponse
name|indexResponse
init|=
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|setTimeout
argument_list|(
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"test"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexResponse
operator|.
name|isCreated
argument_list|()
condition|)
block|{
name|numCreated
operator|++
expr_stmt|;
name|added
index|[
name|i
index|]
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ElasticsearchException
name|ex
parameter_list|)
block|{             }
block|}
name|NumShards
name|numShards
init|=
name|getNumShards
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Start Refresh"
argument_list|)
expr_stmt|;
specifier|final
name|RefreshResponse
name|refreshResponse
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
name|prepareRefresh
argument_list|(
literal|"test"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
comment|// don't assert on failures here
specifier|final
name|boolean
name|refreshFailed
init|=
name|refreshResponse
operator|.
name|getShardFailures
argument_list|()
operator|.
name|length
operator|!=
literal|0
operator|||
name|refreshResponse
operator|.
name|getFailedShards
argument_list|()
operator|!=
literal|0
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Refresh failed [{}] numShardsFailed: [{}], shardFailuresLength: [{}], successfulShards: [{}], totalShards: [{}] "
argument_list|,
name|refreshFailed
argument_list|,
name|refreshResponse
operator|.
name|getFailedShards
argument_list|()
argument_list|,
name|refreshResponse
operator|.
name|getShardFailures
argument_list|()
operator|.
name|length
argument_list|,
name|refreshResponse
operator|.
name|getSuccessfulShards
argument_list|()
argument_list|,
name|refreshResponse
operator|.
name|getTotalShards
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|numSearches
init|=
name|scaledRandomIntBetween
argument_list|(
literal|10
argument_list|,
literal|20
argument_list|)
decl_stmt|;
comment|// we don't check anything here really just making sure we don't leave any open files or a broken index behind.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numSearches
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|int
name|docToQuery
init|=
name|between
argument_list|(
literal|0
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|)
decl_stmt|;
name|long
name|expectedResults
init|=
name|added
index|[
name|docToQuery
index|]
condition|?
literal|1
else|:
literal|0
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Searching for [test:{}]"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|docToQuery
argument_list|)
argument_list|)
expr_stmt|;
name|SearchResponse
name|searchResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setTypes
argument_list|(
literal|"type"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|matchQuery
argument_list|(
literal|"test"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|docToQuery
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Successful shards: [{}]  numShards: [{}]"
argument_list|,
name|searchResponse
operator|.
name|getSuccessfulShards
argument_list|()
argument_list|,
name|numShards
operator|.
name|numPrimaries
argument_list|)
expr_stmt|;
if|if
condition|(
name|searchResponse
operator|.
name|getSuccessfulShards
argument_list|()
operator|==
name|numShards
operator|.
name|numPrimaries
operator|&&
operator|!
name|refreshFailed
condition|)
block|{
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|expectedResults
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check match all
name|searchResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setTypes
argument_list|(
literal|"type"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Match all Successful shards: [{}]  numShards: [{}]"
argument_list|,
name|searchResponse
operator|.
name|getSuccessfulShards
argument_list|()
argument_list|,
name|numShards
operator|.
name|numPrimaries
argument_list|)
expr_stmt|;
if|if
condition|(
name|searchResponse
operator|.
name|getSuccessfulShards
argument_list|()
operator|==
name|numShards
operator|.
name|numPrimaries
operator|&&
operator|!
name|refreshFailed
condition|)
block|{
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|numCreated
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SearchPhaseExecutionException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"SearchPhaseException: [{}]"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
comment|// if a scheduled refresh or flush fails all shards we see all shards failed here
if|if
condition|(
operator|!
operator|(
name|expectAllShardsFailed
operator|||
name|refreshResponse
operator|.
name|getSuccessfulShards
argument_list|()
operator|==
literal|0
operator|||
name|ex
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"all shards failed"
argument_list|)
operator|)
condition|)
block|{
throw|throw
name|ex
throw|;
block|}
block|}
block|}
if|if
condition|(
name|createIndexWithoutErrors
condition|)
block|{
comment|// check the index still contains the records that we indexed without errors
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
operator|.
name|execute
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
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
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|MockDirectoryHelper
operator|.
name|RANDOM_IO_EXCEPTION_RATE
argument_list|,
literal|0
argument_list|)
operator|.
name|put
argument_list|(
name|MockDirectoryHelper
operator|.
name|RANDOM_IO_EXCEPTION_RATE_ON_OPEN
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
literal|"test"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|SearchResponse
name|searchResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setTypes
argument_list|(
literal|"initial"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|matchQuery
argument_list|(
literal|"test"
argument_list|,
literal|"init"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
name|numInitialDocs
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
annotation|@
name|TestLogging
argument_list|(
literal|"action.admin.indices.refresh:TRACE,action.search.type:TRACE,cluster.service:TRACE"
argument_list|)
DECL|method|testRandomExceptions
specifier|public
name|void
name|testRandomExceptions
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|ExecutionException
block|{
name|String
name|mapping
init|=
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
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"test"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"not_analyzed"
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
operator|.
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
specifier|final
name|double
name|lowLevelRate
decl_stmt|;
specifier|final
name|double
name|topLevelRate
decl_stmt|;
if|if
condition|(
name|frequently
argument_list|()
condition|)
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|lowLevelRate
operator|=
literal|1.0
operator|/
name|between
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|topLevelRate
operator|=
literal|0.0d
expr_stmt|;
block|}
else|else
block|{
name|topLevelRate
operator|=
literal|1.0
operator|/
name|between
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|lowLevelRate
operator|=
literal|0.0d
expr_stmt|;
block|}
block|}
else|else
block|{
name|lowLevelRate
operator|=
literal|1.0
operator|/
name|between
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|topLevelRate
operator|=
literal|1.0
operator|/
name|between
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// rarely no exception
name|topLevelRate
operator|=
literal|0d
expr_stmt|;
name|lowLevelRate
operator|=
literal|0d
expr_stmt|;
block|}
name|Builder
name|settings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|indexSettings
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|MockInternalEngine
operator|.
name|READER_WRAPPER_TYPE
argument_list|,
name|RandomExceptionDirectoryReaderWrapper
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|EXCEPTION_TOP_LEVEL_RATIO_KEY
argument_list|,
name|topLevelRate
argument_list|)
operator|.
name|put
argument_list|(
name|EXCEPTION_LOW_LEVEL_RATIO_KEY
argument_list|,
name|lowLevelRate
argument_list|)
operator|.
name|put
argument_list|(
name|MockInternalEngine
operator|.
name|WRAP_READER_RATIO
argument_list|,
literal|1.0d
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"creating index: [test] using settings: [{}]"
argument_list|,
name|settings
operator|.
name|build
argument_list|()
operator|.
name|getAsMap
argument_list|()
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|settings
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
name|mapping
argument_list|)
argument_list|)
expr_stmt|;
name|ensureSearchable
argument_list|()
expr_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|between
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|long
name|numCreated
init|=
literal|0
decl_stmt|;
name|boolean
index|[]
name|added
init|=
operator|new
name|boolean
index|[
name|numDocs
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
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|IndexResponse
name|indexResponse
init|=
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
operator|.
name|setTimeout
argument_list|(
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"test"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexResponse
operator|.
name|isCreated
argument_list|()
condition|)
block|{
name|numCreated
operator|++
expr_stmt|;
name|added
index|[
name|i
index|]
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ElasticsearchException
name|ex
parameter_list|)
block|{             }
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"Start Refresh"
argument_list|)
expr_stmt|;
name|RefreshResponse
name|refreshResponse
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
name|prepareRefresh
argument_list|(
literal|"test"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
comment|// don't assert on failures here
specifier|final
name|boolean
name|refreshFailed
init|=
name|refreshResponse
operator|.
name|getShardFailures
argument_list|()
operator|.
name|length
operator|!=
literal|0
operator|||
name|refreshResponse
operator|.
name|getFailedShards
argument_list|()
operator|!=
literal|0
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Refresh failed [{}] numShardsFailed: [{}], shardFailuresLength: [{}], successfulShards: [{}], totalShards: [{}] "
argument_list|,
name|refreshFailed
argument_list|,
name|refreshResponse
operator|.
name|getFailedShards
argument_list|()
argument_list|,
name|refreshResponse
operator|.
name|getShardFailures
argument_list|()
operator|.
name|length
argument_list|,
name|refreshResponse
operator|.
name|getSuccessfulShards
argument_list|()
argument_list|,
name|refreshResponse
operator|.
name|getTotalShards
argument_list|()
argument_list|)
expr_stmt|;
name|NumShards
name|test
init|=
name|getNumShards
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numSearches
init|=
name|scaledRandomIntBetween
argument_list|(
literal|100
argument_list|,
literal|200
argument_list|)
decl_stmt|;
comment|// we don't check anything here really just making sure we don't leave any open files or a broken index behind.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numSearches
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|int
name|docToQuery
init|=
name|between
argument_list|(
literal|0
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|)
decl_stmt|;
name|long
name|expectedResults
init|=
name|added
index|[
name|docToQuery
index|]
condition|?
literal|1
else|:
literal|0
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Searching for [test:{}]"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|docToQuery
argument_list|)
argument_list|)
expr_stmt|;
name|SearchResponse
name|searchResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|matchQuery
argument_list|(
literal|"test"
argument_list|,
name|English
operator|.
name|intToEnglish
argument_list|(
name|docToQuery
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Successful shards: [{}]  numShards: [{}]"
argument_list|,
name|searchResponse
operator|.
name|getSuccessfulShards
argument_list|()
argument_list|,
name|test
operator|.
name|numPrimaries
argument_list|)
expr_stmt|;
if|if
condition|(
name|searchResponse
operator|.
name|getSuccessfulShards
argument_list|()
operator|==
name|test
operator|.
name|numPrimaries
operator|&&
operator|!
name|refreshFailed
condition|)
block|{
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|expectedResults
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check match all
name|searchResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Match all Successful shards: [{}]  numShards: [{}]"
argument_list|,
name|searchResponse
operator|.
name|getSuccessfulShards
argument_list|()
argument_list|,
name|test
operator|.
name|numPrimaries
argument_list|)
expr_stmt|;
if|if
condition|(
name|searchResponse
operator|.
name|getSuccessfulShards
argument_list|()
operator|==
name|test
operator|.
name|numPrimaries
operator|&&
operator|!
name|refreshFailed
condition|)
block|{
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|numCreated
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SearchPhaseExecutionException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"expected SearchPhaseException: [{}]"
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|EXCEPTION_TOP_LEVEL_RATIO_KEY
specifier|public
specifier|static
specifier|final
name|String
name|EXCEPTION_TOP_LEVEL_RATIO_KEY
init|=
literal|"index.engine.exception.ratio.top"
decl_stmt|;
DECL|field|EXCEPTION_LOW_LEVEL_RATIO_KEY
specifier|public
specifier|static
specifier|final
name|String
name|EXCEPTION_LOW_LEVEL_RATIO_KEY
init|=
literal|"index.engine.exception.ratio.low"
decl_stmt|;
DECL|class|RandomExceptionDirectoryReaderWrapper
specifier|public
specifier|static
class|class
name|RandomExceptionDirectoryReaderWrapper
extends|extends
name|MockInternalEngine
operator|.
name|DirectoryReaderWrapper
block|{
DECL|field|settings
specifier|private
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|class|ThrowingSubReaderWrapper
specifier|static
class|class
name|ThrowingSubReaderWrapper
extends|extends
name|SubReaderWrapper
implements|implements
name|ThrowingAtomicReaderWrapper
operator|.
name|Thrower
block|{
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|topLevelRatio
specifier|private
specifier|final
name|double
name|topLevelRatio
decl_stmt|;
DECL|field|lowLevelRatio
specifier|private
specifier|final
name|double
name|lowLevelRatio
decl_stmt|;
DECL|method|ThrowingSubReaderWrapper
name|ThrowingSubReaderWrapper
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
specifier|final
name|long
name|seed
init|=
name|settings
operator|.
name|getAsLong
argument_list|(
name|SETTING_INDEX_SEED
argument_list|,
literal|0l
argument_list|)
decl_stmt|;
name|this
operator|.
name|topLevelRatio
operator|=
name|settings
operator|.
name|getAsDouble
argument_list|(
name|EXCEPTION_TOP_LEVEL_RATIO_KEY
argument_list|,
literal|0.1d
argument_list|)
expr_stmt|;
name|this
operator|.
name|lowLevelRatio
operator|=
name|settings
operator|.
name|getAsDouble
argument_list|(
name|EXCEPTION_LOW_LEVEL_RATIO_KEY
argument_list|,
literal|0.1d
argument_list|)
expr_stmt|;
name|this
operator|.
name|random
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|wrap
specifier|public
name|AtomicReader
name|wrap
parameter_list|(
name|AtomicReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|ThrowingAtomicReaderWrapper
argument_list|(
name|reader
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|maybeThrow
specifier|public
name|void
name|maybeThrow
parameter_list|(
name|ThrowingAtomicReaderWrapper
operator|.
name|Flags
name|flag
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|flag
condition|)
block|{
case|case
name|Fields
case|:
case|case
name|TermVectors
case|:
case|case
name|Terms
case|:
case|case
name|TermsEnum
case|:
case|case
name|Intersect
case|:
case|case
name|Norms
case|:
case|case
name|NumericDocValues
case|:
case|case
name|BinaryDocValues
case|:
case|case
name|SortedDocValues
case|:
case|case
name|SortedSetDocValues
case|:
if|if
condition|(
name|random
operator|.
name|nextDouble
argument_list|()
operator|<
name|topLevelRatio
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Forced top level Exception on ["
operator|+
name|flag
operator|.
name|name
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
break|break;
case|case
name|DocsEnum
case|:
case|case
name|DocsAndPositionsEnum
case|:
if|if
condition|(
name|random
operator|.
name|nextDouble
argument_list|()
operator|<
name|lowLevelRatio
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Forced low level Exception on ["
operator|+
name|flag
operator|.
name|name
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
break|break;
block|}
block|}
DECL|method|wrapTerms
specifier|public
name|boolean
name|wrapTerms
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
DECL|method|RandomExceptionDirectoryReaderWrapper
specifier|public
name|RandomExceptionDirectoryReaderWrapper
parameter_list|(
name|DirectoryReader
name|in
parameter_list|,
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|,
operator|new
name|ThrowingSubReaderWrapper
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doWrapDirectoryReader
specifier|protected
name|DirectoryReader
name|doWrapDirectoryReader
parameter_list|(
name|DirectoryReader
name|in
parameter_list|)
block|{
return|return
operator|new
name|RandomExceptionDirectoryReaderWrapper
argument_list|(
name|in
argument_list|,
name|settings
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

