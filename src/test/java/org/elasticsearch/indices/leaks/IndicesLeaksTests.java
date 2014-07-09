begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.leaks
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|leaks
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
name|util
operator|.
name|LuceneTestCase
operator|.
name|BadApple
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
name|inject
operator|.
name|Injector
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
name|index
operator|.
name|mapper
operator|.
name|DocumentMapper
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
name|index
operator|.
name|service
operator|.
name|IndexService
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
name|indices
operator|.
name|IndicesService
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|WeakReference
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
name|List
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|nullValue
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
name|Scope
operator|.
name|TEST
argument_list|,
name|numDataNodes
operator|=
literal|1
argument_list|)
DECL|class|IndicesLeaksTests
specifier|public
class|class
name|IndicesLeaksTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"ConstantConditions"
block|,
literal|"unchecked"
block|}
argument_list|)
annotation|@
name|Test
annotation|@
name|BadApple
argument_list|(
name|bugUrl
operator|=
literal|"https://github.com/elasticsearch/elasticsearch/issues/3232"
argument_list|)
DECL|method|testIndexShardLifecycleLeak
specifier|public
name|void
name|testIndexShardLifecycleLeak
parameter_list|()
throws|throws
name|Exception
block|{
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
name|ImmutableSettings
operator|.
name|builder
argument_list|()
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
literal|"index.number_of_replicas"
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
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
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|IndicesService
name|indicesService
init|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|)
decl_stmt|;
name|IndexService
name|indexService
init|=
name|indicesService
operator|.
name|indexServiceSafe
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|Injector
name|indexInjector
init|=
name|indexService
operator|.
name|injector
argument_list|()
decl_stmt|;
name|IndexShard
name|shard
init|=
name|indexService
operator|.
name|shardSafe
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Injector
name|shardInjector
init|=
name|indexService
operator|.
name|shardInjector
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|performCommonOperations
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|WeakReference
argument_list|>
name|indexReferences
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|WeakReference
argument_list|>
name|shardReferences
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// TODO if we could iterate over the already created classes on the injector, we can just add them here to the list
comment|// for now, we simple add some classes that make sense
comment|// add index references
name|indexReferences
operator|.
name|add
argument_list|(
operator|new
name|WeakReference
argument_list|(
name|indexService
argument_list|)
argument_list|)
expr_stmt|;
name|indexReferences
operator|.
name|add
argument_list|(
operator|new
name|WeakReference
argument_list|(
name|indexInjector
argument_list|)
argument_list|)
expr_stmt|;
name|indexReferences
operator|.
name|add
argument_list|(
operator|new
name|WeakReference
argument_list|(
name|indexService
operator|.
name|mapperService
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|DocumentMapper
name|documentMapper
range|:
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|docMappers
argument_list|(
literal|true
argument_list|)
control|)
block|{
name|indexReferences
operator|.
name|add
argument_list|(
operator|new
name|WeakReference
argument_list|(
name|documentMapper
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexReferences
operator|.
name|add
argument_list|(
operator|new
name|WeakReference
argument_list|(
name|indexService
operator|.
name|aliasesService
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|indexReferences
operator|.
name|add
argument_list|(
operator|new
name|WeakReference
argument_list|(
name|indexService
operator|.
name|analysisService
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|indexReferences
operator|.
name|add
argument_list|(
operator|new
name|WeakReference
argument_list|(
name|indexService
operator|.
name|fieldData
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|indexReferences
operator|.
name|add
argument_list|(
operator|new
name|WeakReference
argument_list|(
name|indexService
operator|.
name|queryParserService
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// add shard references
name|shardReferences
operator|.
name|add
argument_list|(
operator|new
name|WeakReference
argument_list|(
name|shard
argument_list|)
argument_list|)
expr_stmt|;
name|shardReferences
operator|.
name|add
argument_list|(
operator|new
name|WeakReference
argument_list|(
name|shardInjector
argument_list|)
argument_list|)
expr_stmt|;
name|indexService
operator|=
literal|null
expr_stmt|;
name|indexInjector
operator|=
literal|null
expr_stmt|;
name|shard
operator|=
literal|null
expr_stmt|;
name|shardInjector
operator|=
literal|null
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
name|prepareDelete
argument_list|()
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
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|int
name|indexNotCleared
init|=
literal|0
decl_stmt|;
for|for
control|(
name|WeakReference
name|indexReference
range|:
name|indexReferences
control|)
block|{
if|if
condition|(
name|indexReference
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|indexNotCleared
operator|++
expr_stmt|;
block|}
block|}
name|int
name|shardNotCleared
init|=
literal|0
decl_stmt|;
for|for
control|(
name|WeakReference
name|shardReference
range|:
name|shardReferences
control|)
block|{
if|if
condition|(
name|shardReference
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|shardNotCleared
operator|++
expr_stmt|;
block|}
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"round {}, indices {}/{}, shards {}/{}"
argument_list|,
name|i
argument_list|,
name|indexNotCleared
argument_list|,
name|indexReferences
operator|.
name|size
argument_list|()
argument_list|,
name|shardNotCleared
argument_list|,
name|shardReferences
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexNotCleared
operator|==
literal|0
operator|&&
name|shardNotCleared
operator|==
literal|0
condition|)
block|{
break|break;
block|}
block|}
comment|//Thread.sleep(1000000);
for|for
control|(
name|WeakReference
name|indexReference
range|:
name|indexReferences
control|)
block|{
name|assertThat
argument_list|(
literal|"dangling index reference: "
operator|+
name|indexReference
operator|.
name|get
argument_list|()
argument_list|,
name|indexReference
operator|.
name|get
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|WeakReference
name|shardReference
range|:
name|shardReferences
control|)
block|{
name|assertThat
argument_list|(
literal|"dangling shard reference: "
operator|+
name|shardReference
operator|.
name|get
argument_list|()
argument_list|,
name|shardReference
operator|.
name|get
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|performCommonOperations
specifier|private
name|void
name|performCommonOperations
parameter_list|()
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
literal|"field1"
argument_list|,
literal|"value"
argument_list|,
literal|"field2"
argument_list|,
literal|2
argument_list|,
literal|"field3"
argument_list|,
literal|3.0f
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
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
name|prepareRefresh
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|queryString
argument_list|(
literal|"field1:value"
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"value"
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
block|}
end_class

end_unit

