begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.percolator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|percolator
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
name|percolate
operator|.
name|PercolateResponse
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
name|AlreadyExpiredException
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
name|ElasticsearchIntegrationTest
operator|.
name|ClusterScope
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
name|common
operator|.
name|settings
operator|.
name|Settings
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
name|percolator
operator|.
name|PercolatorTests
operator|.
name|convertFromTextArray
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
name|assertMatchCount
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
name|assertNoFailures
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
comment|/**  */
end_comment

begin_class
annotation|@
name|ClusterScope
argument_list|(
name|scope
operator|=
name|ElasticsearchIntegrationTest
operator|.
name|Scope
operator|.
name|TEST
argument_list|)
DECL|class|TTLPercolatorTests
specifier|public
class|class
name|TTLPercolatorTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
DECL|field|PURGE_INTERVAL
specifier|private
specifier|static
specifier|final
name|long
name|PURGE_INTERVAL
init|=
literal|200
decl_stmt|;
annotation|@
name|Override
DECL|method|beforeIndexDeletion
specifier|protected
name|void
name|beforeIndexDeletion
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|nodeSettings
specifier|protected
name|Settings
name|nodeSettings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
return|return
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|super
operator|.
name|nodeSettings
argument_list|(
name|nodeOrdinal
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"indices.ttl.interval"
argument_list|,
name|PURGE_INTERVAL
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testPercolatingWithTimeToLive
specifier|public
name|void
name|testPercolatingWithTimeToLive
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|String
name|percolatorMapping
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
name|PercolatorService
operator|.
name|TYPE_NAME
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_ttl"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|true
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"_timestamp"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|true
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
name|string
argument_list|()
decl_stmt|;
name|String
name|typeMapping
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
literal|"type1"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_ttl"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|true
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"_timestamp"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|true
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"field1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
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
name|client
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
literal|2
argument_list|)
argument_list|)
operator|.
name|addMapping
argument_list|(
name|PercolatorService
operator|.
name|TYPE_NAME
argument_list|,
name|percolatorMapping
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type1"
argument_list|,
name|typeMapping
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
specifier|final
name|NumShards
name|test
init|=
name|getNumShards
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|long
name|ttl
init|=
literal|1500
decl_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|client
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
name|PercolatorService
operator|.
name|TYPE_NAME
argument_list|,
literal|"kuku"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"query"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"term"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
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
name|setRefresh
argument_list|(
literal|true
argument_list|)
operator|.
name|setTTL
argument_list|(
name|ttl
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|IndicesStatsResponse
name|response
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareStats
argument_list|(
literal|"test"
argument_list|)
operator|.
name|clear
argument_list|()
operator|.
name|setIndexing
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
name|assertThat
argument_list|(
name|response
operator|.
name|getIndices
argument_list|()
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getTotal
argument_list|()
operator|.
name|getIndexing
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|getIndexCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|test
operator|.
name|dataCopies
argument_list|)
argument_list|)
expr_stmt|;
name|PercolateResponse
name|percolateResponse
init|=
name|client
operator|.
name|preparePercolate
argument_list|()
operator|.
name|setIndices
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setDocumentType
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"doc"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
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
decl_stmt|;
name|assertNoFailures
argument_list|(
name|percolateResponse
argument_list|)
expr_stmt|;
if|if
condition|(
name|percolateResponse
operator|.
name|getMatches
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// OK, ttl + purgeInterval has passed (slow machine or many other tests were running at the same time
name|GetResponse
name|getResponse
init|=
name|client
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
name|PercolatorService
operator|.
name|TYPE_NAME
argument_list|,
literal|"kuku"
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
name|getResponse
operator|.
name|isExists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareStats
argument_list|(
literal|"test"
argument_list|)
operator|.
name|clear
argument_list|()
operator|.
name|setIndexing
argument_list|(
literal|true
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|long
name|currentDeleteCount
init|=
name|response
operator|.
name|getIndices
argument_list|()
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getTotal
argument_list|()
operator|.
name|getIndexing
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|getDeleteCount
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|currentDeleteCount
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|test
operator|.
name|dataCopies
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|assertThat
argument_list|(
name|convertFromTextArray
argument_list|(
name|percolateResponse
operator|.
name|getMatches
argument_list|()
argument_list|,
literal|"test"
argument_list|)
argument_list|,
name|arrayContaining
argument_list|(
literal|"kuku"
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|timeSpent
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|now
decl_stmt|;
name|long
name|waitTime
init|=
name|ttl
operator|+
name|PURGE_INTERVAL
operator|-
name|timeSpent
decl_stmt|;
if|if
condition|(
name|waitTime
operator|>=
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|waitTime
argument_list|)
expr_stmt|;
comment|// Doesn't make sense to check the deleteCount before ttl has expired
block|}
comment|// See comment in SimpleTTLTests
name|logger
operator|.
name|info
argument_list|(
literal|"Checking if the ttl purger has run"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|awaitBusy
argument_list|(
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
name|IndicesStatsResponse
name|indicesStatsResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareStats
argument_list|(
literal|"test"
argument_list|)
operator|.
name|clear
argument_list|()
operator|.
name|setIndexing
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
comment|// TTL deletes one doc, but it is indexed in the primary shard and replica shards
return|return
name|indicesStatsResponse
operator|.
name|getIndices
argument_list|()
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getTotal
argument_list|()
operator|.
name|getIndexing
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|getDeleteCount
argument_list|()
operator|==
name|test
operator|.
name|dataCopies
return|;
block|}
block|}
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|percolateResponse
operator|=
name|client
operator|.
name|preparePercolate
argument_list|()
operator|.
name|setIndices
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setDocumentType
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"doc"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
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
name|assertMatchCount
argument_list|(
name|percolateResponse
argument_list|,
literal|0l
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|percolateResponse
operator|.
name|getMatches
argument_list|()
argument_list|,
name|emptyArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEnsureTTLDoesNotCreateIndex
specifier|public
name|void
name|testEnsureTTLDoesNotCreateIndex
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|ensureGreen
argument_list|()
expr_stmt|;
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
name|setTransientSettings
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"indices.ttl.interval"
argument_list|,
literal|60
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
comment|// 60 sec
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|String
name|typeMapping
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
literal|"type1"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"_ttl"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|true
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
name|string
argument_list|()
decl_stmt|;
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
literal|1
argument_list|)
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type1"
argument_list|,
name|typeMapping
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
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
name|setTransientSettings
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"indices.ttl.interval"
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|get
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"index doc {} "
argument_list|,
name|i
argument_list|)
expr_stmt|;
try|try
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"query"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"term"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
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
name|setTTL
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|500
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
catch|catch
parameter_list|(
name|MapperParsingException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"failed indexing {}"
argument_list|,
name|i
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// if we are unlucky the TTL is so small that we see the expiry date is already in the past when
comment|// we parse the doc ignore those...
name|assertThat
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|,
name|Matchers
operator|.
name|instanceOf
argument_list|(
name|AlreadyExpiredException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|refresh
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|awaitBusy
argument_list|(
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
name|IndicesStatsResponse
name|indicesStatsResponse
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
literal|"test"
argument_list|)
operator|.
name|clear
argument_list|()
operator|.
name|setIndexing
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"delete count [{}]"
argument_list|,
name|indicesStatsResponse
operator|.
name|getIndices
argument_list|()
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getTotal
argument_list|()
operator|.
name|getIndexing
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|getDeleteCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// TTL deletes one doc, but it is indexed in the primary shard and replica shards
return|return
name|indicesStatsResponse
operator|.
name|getIndices
argument_list|()
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getTotal
argument_list|()
operator|.
name|getIndexing
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|getDeleteCount
argument_list|()
operator|!=
literal|0
return|;
block|}
block|}
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|wipeIndices
argument_list|(
literal|"test"
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
name|addMapping
argument_list|(
literal|"type1"
argument_list|,
name|typeMapping
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

