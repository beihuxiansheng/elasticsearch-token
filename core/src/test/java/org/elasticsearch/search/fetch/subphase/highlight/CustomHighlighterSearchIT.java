begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.fetch.subphase.highlight
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
operator|.
name|subphase
operator|.
name|highlight
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
name|fetch
operator|.
name|subphase
operator|.
name|highlight
operator|.
name|HighlightBuilder
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
name|ESIntegTestCase
operator|.
name|ClusterScope
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
operator|.
name|Scope
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
name|HashMap
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
comment|/**  * Integration test for highlighters registered by a plugin.  */
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
name|supportsDedicatedMasters
operator|=
literal|false
argument_list|,
name|numDataNodes
operator|=
literal|1
argument_list|)
DECL|class|CustomHighlighterSearchIT
specifier|public
class|class
name|CustomHighlighterSearchIT
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
name|CustomHighlighterPlugin
operator|.
name|class
argument_list|)
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
name|indexRandom
argument_list|(
literal|true
argument_list|,
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
literal|"name"
argument_list|,
literal|"arbitrary content"
argument_list|,
literal|"other_name"
argument_list|,
literal|"foo"
argument_list|,
literal|"other_other_name"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"other_name"
argument_list|,
literal|"foo"
argument_list|,
literal|"other_other_name"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|highlighter
argument_list|(
operator|new
name|HighlightBuilder
argument_list|()
operator|.
name|field
argument_list|(
literal|"name"
argument_list|)
operator|.
name|highlighterType
argument_list|(
literal|"test-custom"
argument_list|)
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
literal|"standard response for name at position 1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
operator|new
name|HashMap
argument_list|<>
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
name|highlighter
argument_list|(
operator|new
name|HighlightBuilder
argument_list|()
operator|.
name|field
argument_list|(
name|highlightConfig
argument_list|)
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
literal|"standard response for name at position 1"
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
operator|new
name|HashMap
argument_list|<>
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
name|highlighter
argument_list|(
operator|new
name|HighlightBuilder
argument_list|()
operator|.
name|field
argument_list|(
literal|"name"
argument_list|)
operator|.
name|highlighterType
argument_list|(
literal|"test-custom"
argument_list|)
operator|.
name|options
argument_list|(
name|options
argument_list|)
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
literal|"standard response for name at position 1"
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
DECL|method|testThatCustomHighlighterReceivesFieldsInOrder
specifier|public
name|void
name|testThatCustomHighlighterReceivesFieldsInOrder
parameter_list|()
throws|throws
name|Exception
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
name|boolQuery
argument_list|()
operator|.
name|must
argument_list|(
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|should
argument_list|(
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
literal|"name"
argument_list|,
literal|"arbitrary"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|highlighter
argument_list|(
operator|new
name|HighlightBuilder
argument_list|()
operator|.
name|highlighterType
argument_list|(
literal|"test-custom"
argument_list|)
operator|.
name|field
argument_list|(
literal|"name"
argument_list|)
operator|.
name|field
argument_list|(
literal|"other_name"
argument_list|)
operator|.
name|field
argument_list|(
literal|"other_other_name"
argument_list|)
operator|.
name|useExplicitFieldOrder
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|get
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
literal|"standard response for name at position 1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertHighlight
argument_list|(
name|searchResponse
argument_list|,
literal|0
argument_list|,
literal|"other_name"
argument_list|,
literal|0
argument_list|,
name|equalTo
argument_list|(
literal|"standard response for other_name at position 2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertHighlight
argument_list|(
name|searchResponse
argument_list|,
literal|0
argument_list|,
literal|"other_other_name"
argument_list|,
literal|0
argument_list|,
name|equalTo
argument_list|(
literal|"standard response for other_other_name at position 3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertHighlight
argument_list|(
name|searchResponse
argument_list|,
literal|1
argument_list|,
literal|"name"
argument_list|,
literal|0
argument_list|,
name|equalTo
argument_list|(
literal|"standard response for name at position 1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertHighlight
argument_list|(
name|searchResponse
argument_list|,
literal|1
argument_list|,
literal|"other_name"
argument_list|,
literal|0
argument_list|,
name|equalTo
argument_list|(
literal|"standard response for other_name at position 2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertHighlight
argument_list|(
name|searchResponse
argument_list|,
literal|1
argument_list|,
literal|"other_other_name"
argument_list|,
literal|0
argument_list|,
name|equalTo
argument_list|(
literal|"standard response for other_other_name at position 3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

