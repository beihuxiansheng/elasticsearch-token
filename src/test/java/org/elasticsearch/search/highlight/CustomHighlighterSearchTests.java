begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.highlight
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|highlight
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
name|collect
operator|.
name|Maps
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
name|common
operator|.
name|Priority
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
name|junit
operator|.
name|Before
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
name|Map
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
name|ElasticsearchIntegrationTest
operator|.
name|ClusterScope
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
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertHighlight
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

begin_comment
comment|/**  *  */
end_comment

begin_class
annotation|@
name|ClusterScope
argument_list|(
name|scope
operator|=
name|Scope
operator|.
name|SUITE
argument_list|,
name|numNodes
operator|=
literal|1
argument_list|)
DECL|class|CustomHighlighterSearchTests
specifier|public
class|class
name|CustomHighlighterSearchTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
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
literal|"plugin.types"
argument_list|,
name|CustomHighlighterPlugin
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
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
name|build
argument_list|()
return|;
block|}
annotation|@
name|Before
DECL|method|setup
specifier|protected
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
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
name|field
argument_list|(
literal|"name"
argument_list|,
literal|"arbitrary content"
argument_list|)
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
name|setWaitForEvents
argument_list|(
name|Priority
operator|.
name|LANGUID
argument_list|)
operator|.
name|setWaitForYellowStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testThatCustomHighlightersAreSupported
specifier|public
name|void
name|testThatCustomHighlightersAreSupported
parameter_list|()
throws|throws
name|IOException
block|{
name|SearchResponse
name|searchResponse
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
literal|"test"
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
name|addHighlightedField
argument_list|(
literal|"name"
argument_list|)
operator|.
name|setHighlighterType
argument_list|(
literal|"test-custom"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertHighlight
argument_list|(
name|searchResponse
argument_list|,
literal|0
argument_list|,
literal|"name"
argument_list|,
literal|0
argument_list|,
name|equalTo
argument_list|(
literal|"standard response"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testThatCustomHighlighterCanBeConfiguredPerField
specifier|public
name|void
name|testThatCustomHighlighterCanBeConfiguredPerField
parameter_list|()
throws|throws
name|Exception
block|{
name|HighlightBuilder
operator|.
name|Field
name|highlightConfig
init|=
operator|new
name|HighlightBuilder
operator|.
name|Field
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|highlightConfig
operator|.
name|highlighterType
argument_list|(
literal|"test-custom"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|options
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"myFieldOption"
argument_list|,
literal|"someValue"
argument_list|)
expr_stmt|;
name|highlightConfig
operator|.
name|options
argument_list|(
name|options
argument_list|)
expr_stmt|;
name|SearchResponse
name|searchResponse
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
literal|"test"
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
name|addHighlightedField
argument_list|(
name|highlightConfig
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertHighlight
argument_list|(
name|searchResponse
argument_list|,
literal|0
argument_list|,
literal|"name"
argument_list|,
literal|0
argument_list|,
name|equalTo
argument_list|(
literal|"standard response"
argument_list|)
argument_list|)
expr_stmt|;
name|assertHighlight
argument_list|(
name|searchResponse
argument_list|,
literal|0
argument_list|,
literal|"name"
argument_list|,
literal|1
argument_list|,
name|equalTo
argument_list|(
literal|"field:myFieldOption:someValue"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testThatCustomHighlighterCanBeConfiguredGlobally
specifier|public
name|void
name|testThatCustomHighlighterCanBeConfiguredGlobally
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|options
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|options
operator|.
name|put
argument_list|(
literal|"myGlobalOption"
argument_list|,
literal|"someValue"
argument_list|)
expr_stmt|;
name|SearchResponse
name|searchResponse
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
literal|"test"
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
name|setHighlighterOptions
argument_list|(
name|options
argument_list|)
operator|.
name|setHighlighterType
argument_list|(
literal|"test-custom"
argument_list|)
operator|.
name|addHighlightedField
argument_list|(
literal|"name"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertHighlight
argument_list|(
name|searchResponse
argument_list|,
literal|0
argument_list|,
literal|"name"
argument_list|,
literal|0
argument_list|,
name|equalTo
argument_list|(
literal|"standard response"
argument_list|)
argument_list|)
expr_stmt|;
name|assertHighlight
argument_list|(
name|searchResponse
argument_list|,
literal|0
argument_list|,
literal|"name"
argument_list|,
literal|1
argument_list|,
name|equalTo
argument_list|(
literal|"field:myGlobalOption:someValue"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

