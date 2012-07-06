begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.unit.index.percolator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|unit
operator|.
name|index
operator|.
name|percolator
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterService
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
name|bytes
operator|.
name|BytesReference
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
name|AbstractModule
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
name|inject
operator|.
name|ModulesBuilder
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
name|util
operator|.
name|Providers
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
name|settings
operator|.
name|SettingsModule
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
name|index
operator|.
name|Index
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
name|IndexNameModule
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
name|analysis
operator|.
name|AnalysisModule
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
name|cache
operator|.
name|IndexCacheModule
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
name|IndexEngineModule
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
name|MapperServiceModule
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
name|percolator
operator|.
name|PercolatorExecutor
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
name|FilterBuilders
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
name|IndexQueryParserModule
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
name|settings
operator|.
name|IndexSettingsModule
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
name|similarity
operator|.
name|SimilarityModule
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
name|query
operator|.
name|IndicesQueriesModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPoolModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|testng
operator|.
name|annotations
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|testng
operator|.
name|annotations
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|testng
operator|.
name|annotations
operator|.
name|Test
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
name|constantScoreQuery
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
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
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
comment|/**  *  */
end_comment

begin_class
annotation|@
name|Test
DECL|class|PercolatorExecutorTests
specifier|public
class|class
name|PercolatorExecutorTests
block|{
DECL|field|injector
specifier|private
name|Injector
name|injector
decl_stmt|;
DECL|field|percolatorExecutor
specifier|private
name|PercolatorExecutor
name|percolatorExecutor
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|buildPercolatorService
specifier|public
name|void
name|buildPercolatorService
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
comment|//.put("index.cache.filter.type", "none")
operator|.
name|build
argument_list|()
decl_stmt|;
name|Index
name|index
init|=
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|injector
operator|=
operator|new
name|ModulesBuilder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|SettingsModule
argument_list|(
name|settings
argument_list|)
argument_list|,
operator|new
name|ThreadPoolModule
argument_list|(
name|settings
argument_list|)
argument_list|,
operator|new
name|ScriptModule
argument_list|(
name|settings
argument_list|)
argument_list|,
operator|new
name|IndicesQueriesModule
argument_list|()
argument_list|,
operator|new
name|MapperServiceModule
argument_list|()
argument_list|,
operator|new
name|IndexSettingsModule
argument_list|(
name|index
argument_list|,
name|settings
argument_list|)
argument_list|,
operator|new
name|IndexCacheModule
argument_list|(
name|settings
argument_list|)
argument_list|,
operator|new
name|AnalysisModule
argument_list|(
name|settings
argument_list|)
argument_list|,
operator|new
name|IndexEngineModule
argument_list|(
name|settings
argument_list|)
argument_list|,
operator|new
name|SimilarityModule
argument_list|(
name|settings
argument_list|)
argument_list|,
operator|new
name|IndexQueryParserModule
argument_list|(
name|settings
argument_list|)
argument_list|,
operator|new
name|IndexNameModule
argument_list|(
name|index
argument_list|)
argument_list|,
operator|new
name|AbstractModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|PercolatorExecutor
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|Providers
operator|.
name|of
argument_list|(
operator|(
name|ClusterService
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
operator|.
name|createInjector
argument_list|()
expr_stmt|;
name|percolatorExecutor
operator|=
name|injector
operator|.
name|getInstance
argument_list|(
name|PercolatorExecutor
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|injector
operator|.
name|getInstance
argument_list|(
name|ThreadPool
operator|.
name|class
argument_list|)
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimplePercolator
specifier|public
name|void
name|testSimplePercolator
parameter_list|()
throws|throws
name|Exception
block|{
comment|// introduce the doc
name|XContentBuilder
name|doc
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
literal|"doc"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|BytesReference
name|source
init|=
name|doc
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|XContentBuilder
name|docWithType
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
literal|"doc"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|"value"
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
decl_stmt|;
name|BytesReference
name|sourceWithType
init|=
name|docWithType
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|PercolatorExecutor
operator|.
name|Response
name|percolate
init|=
name|percolatorExecutor
operator|.
name|percolate
argument_list|(
operator|new
name|PercolatorExecutor
operator|.
name|SourceRequest
argument_list|(
literal|"type1"
argument_list|,
name|source
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|percolate
operator|.
name|matches
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// add a query
name|percolatorExecutor
operator|.
name|addQuery
argument_list|(
literal|"test1"
argument_list|,
name|termQuery
argument_list|(
literal|"field2"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|percolate
operator|=
name|percolatorExecutor
operator|.
name|percolate
argument_list|(
operator|new
name|PercolatorExecutor
operator|.
name|SourceRequest
argument_list|(
literal|"type1"
argument_list|,
name|source
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|percolate
operator|.
name|matches
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|percolate
operator|.
name|matches
argument_list|()
argument_list|,
name|hasItem
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|percolate
operator|=
name|percolatorExecutor
operator|.
name|percolate
argument_list|(
operator|new
name|PercolatorExecutor
operator|.
name|SourceRequest
argument_list|(
literal|"type1"
argument_list|,
name|sourceWithType
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|percolate
operator|.
name|matches
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|percolate
operator|.
name|matches
argument_list|()
argument_list|,
name|hasItem
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|percolatorExecutor
operator|.
name|addQuery
argument_list|(
literal|"test2"
argument_list|,
name|termQuery
argument_list|(
literal|"field1"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|percolate
operator|=
name|percolatorExecutor
operator|.
name|percolate
argument_list|(
operator|new
name|PercolatorExecutor
operator|.
name|SourceRequest
argument_list|(
literal|"type1"
argument_list|,
name|source
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|percolate
operator|.
name|matches
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|percolate
operator|.
name|matches
argument_list|()
argument_list|,
name|hasItems
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|percolatorExecutor
operator|.
name|removeQuery
argument_list|(
literal|"test2"
argument_list|)
expr_stmt|;
name|percolate
operator|=
name|percolatorExecutor
operator|.
name|percolate
argument_list|(
operator|new
name|PercolatorExecutor
operator|.
name|SourceRequest
argument_list|(
literal|"type1"
argument_list|,
name|source
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|percolate
operator|.
name|matches
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|percolate
operator|.
name|matches
argument_list|()
argument_list|,
name|hasItems
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// add a range query (cached)
comment|// add a query
name|percolatorExecutor
operator|.
name|addQuery
argument_list|(
literal|"test1"
argument_list|,
name|constantScoreQuery
argument_list|(
name|FilterBuilders
operator|.
name|rangeFilter
argument_list|(
literal|"field2"
argument_list|)
operator|.
name|from
argument_list|(
literal|"value"
argument_list|)
operator|.
name|includeLower
argument_list|(
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|percolate
operator|=
name|percolatorExecutor
operator|.
name|percolate
argument_list|(
operator|new
name|PercolatorExecutor
operator|.
name|SourceRequest
argument_list|(
literal|"type1"
argument_list|,
name|source
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|percolate
operator|.
name|matches
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|percolate
operator|.
name|matches
argument_list|()
argument_list|,
name|hasItem
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

