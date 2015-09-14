begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.suggest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
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
name|SearchRequestBuilder
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
name|util
operator|.
name|CollectionUtils
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
name|ToXContent
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
name|ESIntegTestCase
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
name|Collection
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
name|test
operator|.
name|ESIntegTestCase
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
name|hasSize
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
name|is
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
name|numDataNodes
operator|=
literal|1
argument_list|)
DECL|class|CustomSuggesterSearchIT
specifier|public
class|class
name|CustomSuggesterSearchIT
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
name|pluginList
argument_list|(
name|CustomSuggesterPlugin
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testThatCustomSuggestersCanBeRegisteredAndWork
specifier|public
name|void
name|testThatCustomSuggestersCanBeRegisteredAndWork
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
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
name|ensureYellow
argument_list|()
expr_stmt|;
name|String
name|randomText
init|=
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|String
name|randomField
init|=
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|String
name|randomSuffix
init|=
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|SearchRequestBuilder
name|searchRequestBuilder
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
name|setFrom
argument_list|(
literal|0
argument_list|)
operator|.
name|setSize
argument_list|(
literal|1
argument_list|)
operator|.
name|addSuggestion
argument_list|(
operator|new
name|SuggestBuilder
operator|.
name|SuggestionBuilder
argument_list|<
name|SuggestBuilder
operator|.
name|SuggestionBuilder
argument_list|>
argument_list|(
literal|"someName"
argument_list|,
literal|"custom"
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|XContentBuilder
name|innerToXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
name|randomField
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"suffix"
argument_list|,
name|randomSuffix
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
operator|.
name|text
argument_list|(
name|randomText
argument_list|)
argument_list|)
decl_stmt|;
name|SearchResponse
name|searchResponse
init|=
name|searchRequestBuilder
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
comment|// TODO: infer type once JI-9019884 is fixed
comment|// TODO: see also JDK-8039214
name|List
argument_list|<
name|Suggest
operator|.
name|Suggestion
operator|.
name|Entry
argument_list|<
name|?
extends|extends
name|Suggest
operator|.
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
argument_list|>
argument_list|>
name|suggestions
init|=
name|CollectionUtils
operator|.
expr|<
name|Suggest
operator|.
name|Suggestion
operator|.
name|Entry
argument_list|<
name|?
extends|extends
name|Suggest
operator|.
name|Suggestion
operator|.
name|Entry
operator|.
name|Option
argument_list|>
operator|>
name|iterableAsArrayList
argument_list|(
name|searchResponse
operator|.
name|getSuggest
argument_list|()
operator|.
name|getSuggestion
argument_list|(
literal|"someName"
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|suggestions
argument_list|,
name|hasSize
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|suggestions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getText
argument_list|()
operator|.
name|string
argument_list|()
argument_list|,
name|is
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%s-%s-%s-12"
argument_list|,
name|randomText
argument_list|,
name|randomField
argument_list|,
name|randomSuffix
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|suggestions
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getText
argument_list|()
operator|.
name|string
argument_list|()
argument_list|,
name|is
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%s-%s-%s-123"
argument_list|,
name|randomText
argument_list|,
name|randomField
argument_list|,
name|randomSuffix
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

