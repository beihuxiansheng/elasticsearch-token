begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|ParseFieldMatcher
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
name|BytesArray
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
name|ModuleTestCase
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
name|stream
operator|.
name|NamedWriteableRegistry
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
name|XContentHelper
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
name|XContentParser
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
name|QueryParser
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
name|TermQueryBuilder
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
name|IndicesQueriesRegistry
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
name|highlight
operator|.
name|CustomHighlighter
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
name|highlight
operator|.
name|Highlighter
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
name|highlight
operator|.
name|PlainHighlighter
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
name|suggest
operator|.
name|CustomSuggester
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
name|suggest
operator|.
name|phrase
operator|.
name|PhraseSuggester
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
name|ArrayList
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsInAnyOrder
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
name|notNullValue
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|SearchModuleTests
specifier|public
class|class
name|SearchModuleTests
extends|extends
name|ModuleTestCase
block|{
DECL|method|testDoubleRegister
specifier|public
name|void
name|testDoubleRegister
parameter_list|()
block|{
name|SearchModule
name|module
init|=
operator|new
name|SearchModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
operator|new
name|NamedWriteableRegistry
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|module
operator|.
name|registerHighlighter
argument_list|(
literal|"fvh"
argument_list|,
name|PlainHighlighter
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Can't register the same [highlighter] more than once for [fvh]"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|module
operator|.
name|registerSuggester
argument_list|(
literal|"term"
argument_list|,
name|PhraseSuggester
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Can't register the same [suggester] more than once for [term]"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRegisterSuggester
specifier|public
name|void
name|testRegisterSuggester
parameter_list|()
block|{
name|SearchModule
name|module
init|=
operator|new
name|SearchModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
operator|new
name|NamedWriteableRegistry
argument_list|()
argument_list|)
decl_stmt|;
name|module
operator|.
name|registerSuggester
argument_list|(
literal|"custom"
argument_list|,
name|CustomSuggester
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|module
operator|.
name|registerSuggester
argument_list|(
literal|"custom"
argument_list|,
name|CustomSuggester
operator|.
name|INSTANCE
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Can't register the same [suggester] more than once for [custom]"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegisterHighlighter
specifier|public
name|void
name|testRegisterHighlighter
parameter_list|()
block|{
name|SearchModule
name|module
init|=
operator|new
name|SearchModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
operator|new
name|NamedWriteableRegistry
argument_list|()
argument_list|)
decl_stmt|;
name|module
operator|.
name|registerHighlighter
argument_list|(
literal|"custom"
argument_list|,
name|CustomHighlighter
operator|.
name|class
argument_list|)
expr_stmt|;
try|try
block|{
name|module
operator|.
name|registerHighlighter
argument_list|(
literal|"custom"
argument_list|,
name|CustomHighlighter
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Can't register the same [highlighter] more than once for [custom]"
argument_list|)
expr_stmt|;
block|}
name|assertMapMultiBinding
argument_list|(
name|module
argument_list|,
name|Highlighter
operator|.
name|class
argument_list|,
name|CustomHighlighter
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegisterQueryParserDuplicate
specifier|public
name|void
name|testRegisterQueryParserDuplicate
parameter_list|()
block|{
name|SearchModule
name|module
init|=
operator|new
name|SearchModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
operator|new
name|NamedWriteableRegistry
argument_list|()
argument_list|)
decl_stmt|;
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|module
operator|.
name|registerQuery
argument_list|(
name|TermQueryBuilder
operator|::
operator|new
argument_list|,
name|TermQueryBuilder
operator|::
name|fromXContent
argument_list|,
name|TermQueryBuilder
operator|.
name|QUERY_NAME_FIELD
argument_list|)
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
literal|"] already registered for [query][term] while trying to register [org.elasticsearch."
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegisteredQueries
specifier|public
name|void
name|testRegisteredQueries
parameter_list|()
throws|throws
name|IOException
block|{
name|SearchModule
name|module
init|=
operator|new
name|SearchModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
operator|new
name|NamedWriteableRegistry
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|allSupportedQueries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|allSupportedQueries
argument_list|,
name|NON_DEPRECATED_QUERIES
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|allSupportedQueries
argument_list|,
name|DEPRECATED_QUERIES
argument_list|)
expr_stmt|;
name|String
index|[]
name|supportedQueries
init|=
name|allSupportedQueries
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|allSupportedQueries
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|module
operator|.
name|getQueryParserRegistry
argument_list|()
operator|.
name|getNames
argument_list|()
argument_list|,
name|containsInAnyOrder
argument_list|(
name|supportedQueries
argument_list|)
argument_list|)
expr_stmt|;
name|IndicesQueriesRegistry
name|indicesQueriesRegistry
init|=
name|module
operator|.
name|getQueryParserRegistry
argument_list|()
decl_stmt|;
name|XContentParser
name|dummyParser
init|=
name|XContentHelper
operator|.
name|createParser
argument_list|(
operator|new
name|BytesArray
argument_list|(
literal|"{}"
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|queryName
range|:
name|supportedQueries
control|)
block|{
name|indicesQueriesRegistry
operator|.
name|lookup
argument_list|(
name|queryName
argument_list|,
name|dummyParser
argument_list|,
name|ParseFieldMatcher
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|queryName
range|:
name|NON_DEPRECATED_QUERIES
control|)
block|{
name|QueryParser
argument_list|<
name|?
argument_list|>
name|queryParser
init|=
name|indicesQueriesRegistry
operator|.
name|lookup
argument_list|(
name|queryName
argument_list|,
name|dummyParser
argument_list|,
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|queryParser
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|queryName
range|:
name|DEPRECATED_QUERIES
control|)
block|{
try|try
block|{
name|indicesQueriesRegistry
operator|.
name|lookup
argument_list|(
name|queryName
argument_list|,
name|dummyParser
argument_list|,
name|ParseFieldMatcher
operator|.
name|STRICT
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"query is deprecated, getQueryParser should have failed in strict mode"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"Deprecated field ["
operator|+
name|queryName
operator|+
literal|"] used"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|NON_DEPRECATED_QUERIES
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|NON_DEPRECATED_QUERIES
init|=
operator|new
name|String
index|[]
block|{
literal|"bool"
block|,
literal|"boosting"
block|,
literal|"common"
block|,
literal|"constantScore"
block|,
literal|"constant_score"
block|,
literal|"disMax"
block|,
literal|"dis_max"
block|,
literal|"exists"
block|,
literal|"fieldMaskingSpan"
block|,
literal|"field_masking_span"
block|,
literal|"functionScore"
block|,
literal|"function_score"
block|,
literal|"fuzzy"
block|,
literal|"geoBoundingBox"
block|,
literal|"geoDistance"
block|,
literal|"geoDistanceRange"
block|,
literal|"geoPolygon"
block|,
literal|"geoShape"
block|,
literal|"geo_bounding_box"
block|,
literal|"geo_distance"
block|,
literal|"geo_distance_range"
block|,
literal|"geo_polygon"
block|,
literal|"geo_shape"
block|,
literal|"geohashCell"
block|,
literal|"geohash_cell"
block|,
literal|"hasChild"
block|,
literal|"hasParent"
block|,
literal|"has_child"
block|,
literal|"has_parent"
block|,
literal|"ids"
block|,
literal|"indices"
block|,
literal|"match"
block|,
literal|"matchAll"
block|,
literal|"matchNone"
block|,
literal|"matchPhrase"
block|,
literal|"matchPhrasePrefix"
block|,
literal|"match_all"
block|,
literal|"match_none"
block|,
literal|"match_phrase"
block|,
literal|"match_phrase_prefix"
block|,
literal|"moreLikeThis"
block|,
literal|"more_like_this"
block|,
literal|"multiMatch"
block|,
literal|"multi_match"
block|,
literal|"nested"
block|,
literal|"parentId"
block|,
literal|"parent_id"
block|,
literal|"percolate"
block|,
literal|"prefix"
block|,
literal|"queryString"
block|,
literal|"query_string"
block|,
literal|"range"
block|,
literal|"regexp"
block|,
literal|"script"
block|,
literal|"simpleQueryString"
block|,
literal|"simple_query_string"
block|,
literal|"spanContaining"
block|,
literal|"spanFirst"
block|,
literal|"spanMulti"
block|,
literal|"spanNear"
block|,
literal|"spanNot"
block|,
literal|"spanOr"
block|,
literal|"spanTerm"
block|,
literal|"spanWithin"
block|,
literal|"span_containing"
block|,
literal|"span_first"
block|,
literal|"span_multi"
block|,
literal|"span_near"
block|,
literal|"span_not"
block|,
literal|"span_or"
block|,
literal|"span_term"
block|,
literal|"span_within"
block|,
literal|"template"
block|,
literal|"term"
block|,
literal|"terms"
block|,
literal|"type"
block|,
literal|"wildcard"
block|,
literal|"wrapper"
block|}
decl_stmt|;
DECL|field|DEPRECATED_QUERIES
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|DEPRECATED_QUERIES
init|=
operator|new
name|String
index|[]
block|{
literal|"fuzzyMatch"
block|,
literal|"fuzzy_match"
block|,
literal|"geoBbox"
block|,
literal|"geo_bbox"
block|,
literal|"in"
block|,
literal|"matchFuzzy"
block|,
literal|"match_fuzzy"
block|,
literal|"mlt"
block|}
decl_stmt|;
block|}
end_class

end_unit

