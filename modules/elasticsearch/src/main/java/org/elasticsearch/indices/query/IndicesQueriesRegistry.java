begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|query
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
name|collect
operator|.
name|ImmutableMap
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
name|common
operator|.
name|inject
operator|.
name|Inject
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
name|query
operator|.
name|*
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|IndicesQueriesRegistry
specifier|public
class|class
name|IndicesQueriesRegistry
block|{
DECL|field|queryParsers
specifier|private
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|QueryParser
argument_list|>
name|queryParsers
decl_stmt|;
DECL|field|filterParsers
specifier|private
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|FilterParser
argument_list|>
name|filterParsers
decl_stmt|;
DECL|method|IndicesQueriesRegistry
annotation|@
name|Inject
specifier|public
name|IndicesQueriesRegistry
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|QueryParser
argument_list|>
name|queryParsers
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|TextQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|NestedQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|HasChildQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|TopChildrenQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|DisMaxQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|IdsQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|MatchAllQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|QueryStringQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|BoostingQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|BoolQueryParser
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|TermQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|TermsQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|FuzzyQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|FieldQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|RangeQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|PrefixQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|WildcardQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|FilteredQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|ConstantScoreQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|CustomBoostFactorQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|CustomScoreQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|CustomFiltersScoreQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|SpanTermQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|SpanNotQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|SpanFirstQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|SpanNearQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|SpanOrQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|MoreLikeThisQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|MoreLikeThisFieldQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|FuzzyLikeThisQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|FuzzyLikeThisFieldQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
operator|new
name|WrapperQueryParser
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryParsers
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|queryParsers
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|FilterParser
argument_list|>
name|filterParsers
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|HasChildFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|NestedFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|TypeFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|IdsFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|LimitFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|TermFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|TermsFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|RangeFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|NumericRangeFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|PrefixFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|ScriptFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|GeoDistanceFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|GeoDistanceRangeFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|GeoBoundingBoxFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|GeoPolygonFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|QueryFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|FQueryFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|BoolFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|AndFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|OrFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|NotFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|MatchAllFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|ExistsFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
operator|new
name|MissingFilterParser
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|filterParsers
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|filterParsers
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds a global query parser.      */
DECL|method|addQueryParser
specifier|public
name|void
name|addQueryParser
parameter_list|(
name|QueryParser
name|queryParser
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|QueryParser
argument_list|>
name|queryParsers
init|=
name|Maps
operator|.
name|newHashMap
argument_list|(
name|this
operator|.
name|queryParsers
argument_list|)
decl_stmt|;
name|addQueryParser
argument_list|(
name|queryParsers
argument_list|,
name|queryParser
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryParsers
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|queryParsers
argument_list|)
expr_stmt|;
block|}
DECL|method|addFilterParser
specifier|public
name|void
name|addFilterParser
parameter_list|(
name|FilterParser
name|filterParser
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|FilterParser
argument_list|>
name|filterParsers
init|=
name|Maps
operator|.
name|newHashMap
argument_list|(
name|this
operator|.
name|filterParsers
argument_list|)
decl_stmt|;
name|addFilterParser
argument_list|(
name|filterParsers
argument_list|,
name|filterParser
argument_list|)
expr_stmt|;
name|this
operator|.
name|filterParsers
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|filterParsers
argument_list|)
expr_stmt|;
block|}
DECL|method|queryParsers
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|QueryParser
argument_list|>
name|queryParsers
parameter_list|()
block|{
return|return
name|queryParsers
return|;
block|}
DECL|method|filterParsers
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|FilterParser
argument_list|>
name|filterParsers
parameter_list|()
block|{
return|return
name|filterParsers
return|;
block|}
DECL|method|addQueryParser
specifier|private
name|void
name|addQueryParser
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|QueryParser
argument_list|>
name|queryParsers
parameter_list|,
name|QueryParser
name|queryParser
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|queryParser
operator|.
name|names
argument_list|()
control|)
block|{
name|queryParsers
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|queryParser
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addFilterParser
specifier|private
name|void
name|addFilterParser
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|FilterParser
argument_list|>
name|filterParsers
parameter_list|,
name|FilterParser
name|filterParser
parameter_list|)
block|{
for|for
control|(
name|String
name|name
range|:
name|filterParser
operator|.
name|names
argument_list|()
control|)
block|{
name|filterParsers
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|filterParser
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

