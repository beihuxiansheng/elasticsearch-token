begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indexing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indexing
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
name|DocWriteResponse
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
name|index
operator|.
name|IndexRequestBuilder
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
name|SearchResponse
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
name|MetaDataCreateIndexService
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
name|VersionType
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
name|mapper
operator|.
name|MapperParsingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|InvalidIndexNameException
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
name|InternalSettingsPlugin
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
name|VersionUtils
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
name|hamcrest
operator|.
name|ElasticsearchAssertions
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
name|Collection
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|Callable
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
name|ExecutorService
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
name|Executors
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
name|AtomicIntegerArray
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
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
name|lessThanOrEqualTo
import|;
end_import

begin_class
DECL|class|IndexActionIT
specifier|public
class|class
name|IndexActionIT
extends|extends
name|ESIntegTestCase
block|{
comment|/**      * This test tries to simulate load while creating an index and indexing documents      * while the index is being created.      */
annotation|@
name|TestLogging
argument_list|(
literal|"_root:DEBUG,org.elasticsearch.index.shard.IndexShard:TRACE,org.elasticsearch.action.search:TRACE"
argument_list|)
DECL|method|testAutoGenerateIdNoDuplicates
specifier|public
name|void
name|testAutoGenerateIdNoDuplicates
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numberOfIterations
init|=
name|scaledRandomIntBetween
argument_list|(
literal|10
argument_list|,
literal|50
argument_list|)
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
name|numberOfIterations
condition|;
name|i
operator|++
control|)
block|{
name|Exception
name|firstError
init|=
literal|null
decl_stmt|;
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|int
name|numOfDocs
init|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"indexing [{}] docs"
argument_list|,
name|numOfDocs
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|builders
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numOfDocs
argument_list|)
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
name|numOfDocs
condition|;
name|j
operator|++
control|)
block|{
name|builders
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"value_"
operator|+
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|builders
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"verifying indexed content"
argument_list|)
expr_stmt|;
name|int
name|numOfChecks
init|=
name|randomIntBetween
argument_list|(
literal|8
argument_list|,
literal|12
argument_list|)
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
name|numOfChecks
condition|;
name|j
operator|++
control|)
block|{
try|try
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"running search with all types"
argument_list|)
expr_stmt|;
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
operator|!=
name|numOfDocs
condition|)
block|{
specifier|final
name|String
name|message
init|=
literal|"Count is "
operator|+
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
operator|+
literal|" but "
operator|+
name|numOfDocs
operator|+
literal|" was expected. "
operator|+
name|ElasticsearchAssertions
operator|.
name|formatShardStatus
argument_list|(
name|response
argument_list|)
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"{}. search response: \n{}"
argument_list|,
name|message
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|message
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
name|error
argument_list|(
literal|"search for all docs types failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|firstError
operator|==
literal|null
condition|)
block|{
name|firstError
operator|=
name|e
expr_stmt|;
block|}
block|}
try|try
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"running search with a specific type"
argument_list|)
expr_stmt|;
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setTypes
argument_list|(
literal|"type"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
operator|!=
name|numOfDocs
condition|)
block|{
specifier|final
name|String
name|message
init|=
literal|"Count is "
operator|+
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
operator|+
literal|" but "
operator|+
name|numOfDocs
operator|+
literal|" was expected. "
operator|+
name|ElasticsearchAssertions
operator|.
name|formatShardStatus
argument_list|(
name|response
argument_list|)
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"{}. search response: \n{}"
argument_list|,
name|message
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|message
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
name|error
argument_list|(
literal|"search for all docs of a specific type failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|firstError
operator|==
literal|null
condition|)
block|{
name|firstError
operator|=
name|e
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|firstError
operator|!=
literal|null
condition|)
block|{
name|fail
argument_list|(
name|firstError
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|internalCluster
argument_list|()
operator|.
name|wipeIndices
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testCreatedFlag
specifier|public
name|void
name|testCreatedFlag
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
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
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field1"
argument_list|,
literal|"value1_1"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|DocWriteResponse
operator|.
name|Result
operator|.
name|CREATED
argument_list|,
name|indexResponse
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
name|indexResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field1"
argument_list|,
literal|"value1_2"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|DocWriteResponse
operator|.
name|Result
operator|.
name|UPDATED
argument_list|,
name|indexResponse
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareDelete
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|indexResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field1"
argument_list|,
literal|"value1_2"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|DocWriteResponse
operator|.
name|Result
operator|.
name|CREATED
argument_list|,
name|indexResponse
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreatedFlagWithFlush
specifier|public
name|void
name|testCreatedFlagWithFlush
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
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
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field1"
argument_list|,
literal|"value1_1"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|DocWriteResponse
operator|.
name|Result
operator|.
name|CREATED
argument_list|,
name|indexResponse
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareDelete
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
name|indexResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field1"
argument_list|,
literal|"value1_2"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|DocWriteResponse
operator|.
name|Result
operator|.
name|CREATED
argument_list|,
name|indexResponse
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreatedFlagParallelExecution
specifier|public
name|void
name|testCreatedFlagParallelExecution
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|int
name|threadCount
init|=
literal|20
decl_stmt|;
specifier|final
name|int
name|docCount
init|=
literal|300
decl_stmt|;
name|int
name|taskCount
init|=
name|docCount
operator|*
name|threadCount
decl_stmt|;
specifier|final
name|AtomicIntegerArray
name|createdCounts
init|=
operator|new
name|AtomicIntegerArray
argument_list|(
name|docCount
argument_list|)
decl_stmt|;
name|ExecutorService
name|threadPool
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|threadCount
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|>
name|tasks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|taskCount
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|random
init|=
name|random
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
name|taskCount
condition|;
name|i
operator|++
control|)
block|{
name|tasks
operator|.
name|add
argument_list|(
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|docId
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|docCount
argument_list|)
decl_stmt|;
name|IndexResponse
name|indexResponse
init|=
name|index
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|docId
argument_list|)
argument_list|,
literal|"field1"
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexResponse
operator|.
name|getResult
argument_list|()
operator|==
name|DocWriteResponse
operator|.
name|Result
operator|.
name|CREATED
condition|)
block|{
name|createdCounts
operator|.
name|incrementAndGet
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|threadPool
operator|.
name|invokeAll
argument_list|(
name|tasks
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
name|docCount
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|createdCounts
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|lessThanOrEqualTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|terminate
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreatedFlagWithExternalVersioning
specifier|public
name|void
name|testCreatedFlagWithExternalVersioning
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
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
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field1"
argument_list|,
literal|"value1_1"
argument_list|)
operator|.
name|setVersion
argument_list|(
literal|123
argument_list|)
operator|.
name|setVersionType
argument_list|(
name|VersionType
operator|.
name|EXTERNAL
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|DocWriteResponse
operator|.
name|Result
operator|.
name|CREATED
argument_list|,
name|indexResponse
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreateFlagWithBulk
specifier|public
name|void
name|testCreateFlagWithBulk
parameter_list|()
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|BulkResponse
name|bulkResponse
init|=
name|client
argument_list|()
operator|.
name|prepareBulk
argument_list|()
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field1"
argument_list|,
literal|"value1_1"
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|bulkResponse
operator|.
name|hasFailures
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bulkResponse
operator|.
name|getItems
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|IndexResponse
name|indexResponse
init|=
name|bulkResponse
operator|.
name|getItems
argument_list|()
index|[
literal|0
index|]
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|DocWriteResponse
operator|.
name|Result
operator|.
name|CREATED
argument_list|,
name|indexResponse
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testCreateIndexWithLongName
specifier|public
name|void
name|testCreateIndexWithLongName
parameter_list|()
block|{
name|int
name|min
init|=
name|MetaDataCreateIndexService
operator|.
name|MAX_INDEX_NAME_BYTES
operator|+
literal|1
decl_stmt|;
name|int
name|max
init|=
name|MetaDataCreateIndexService
operator|.
name|MAX_INDEX_NAME_BYTES
operator|*
literal|2
decl_stmt|;
try|try
block|{
name|createIndex
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
name|min
argument_list|,
name|max
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"exception should have been thrown on too-long index name"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidIndexNameException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
literal|"exception contains message about index name too long: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"index name is too long,"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
name|min
argument_list|,
name|max
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
literal|"mytype"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"exception should have been thrown on too-long index name"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidIndexNameException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
literal|"exception contains message about index name too long: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"index name is too long,"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// Catch chars that are more than a single byte
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
name|randomAsciiOfLength
argument_list|(
name|MetaDataCreateIndexService
operator|.
name|MAX_INDEX_NAME_BYTES
operator|-
literal|1
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|+
literal|"Ï"
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
literal|"mytype"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"exception should have been thrown on too-long index name"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidIndexNameException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
literal|"exception contains message about index name too long: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"index name is too long,"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// we can create an index of max length
name|createIndex
argument_list|(
name|randomAsciiOfLength
argument_list|(
name|MetaDataCreateIndexService
operator|.
name|MAX_INDEX_NAME_BYTES
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidIndexName
specifier|public
name|void
name|testInvalidIndexName
parameter_list|()
block|{
try|try
block|{
name|createIndex
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"exception should have been thrown on dot index name"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidIndexNameException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
literal|"exception contains message about index name is dot "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Invalid index name [.], must not be \'.\' or '..'"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|createIndex
argument_list|(
literal|".."
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"exception should have been thrown on dot index name"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidIndexNameException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
literal|"exception contains message about index name is dot "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Invalid index name [..], must not be \'.\' or '..'"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testDocumentWithBlankFieldName
specifier|public
name|void
name|testDocumentWithBlankFieldName
parameter_list|()
block|{
name|MapperParsingException
name|e
init|=
name|expectThrows
argument_list|(
name|MapperParsingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|""
argument_list|,
literal|"value1_2"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"failed to parse"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getRootCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"name cannot be empty string"
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|Collections
operator|.
name|singleton
argument_list|(
name|InternalSettingsPlugin
operator|.
name|class
argument_list|)
return|;
comment|// uses index.version.created
block|}
DECL|method|testDocumentWithBlankFieldName2x
specifier|public
name|void
name|testDocumentWithBlankFieldName2x
parameter_list|()
block|{
name|Version
name|version
init|=
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|Version
operator|.
name|V_2_0_0
argument_list|,
name|Version
operator|.
name|V_2_3_4
argument_list|)
decl_stmt|;
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|version
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|IndexResponse
name|indexResponse
init|=
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test1"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|""
argument_list|,
literal|"value1_2"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|DocWriteResponse
operator|.
name|Result
operator|.
name|CREATED
argument_list|,
name|indexResponse
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

