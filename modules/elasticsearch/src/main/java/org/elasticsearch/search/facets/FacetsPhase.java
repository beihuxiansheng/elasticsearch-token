begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facets
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facets
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
name|search
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
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
name|Lists
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
name|lucene
operator|.
name|search
operator|.
name|NoopCollector
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
name|lucene
operator|.
name|search
operator|.
name|Queries
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
name|lucene
operator|.
name|search
operator|.
name|TermFilter
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
name|search
operator|.
name|SearchParseElement
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
name|SearchPhase
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
name|facets
operator|.
name|collector
operator|.
name|FacetCollector
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
name|facets
operator|.
name|internal
operator|.
name|InternalFacets
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
name|internal
operator|.
name|SearchContext
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
name|query
operator|.
name|QueryPhaseExecutionException
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
name|List
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|FacetsPhase
specifier|public
class|class
name|FacetsPhase
implements|implements
name|SearchPhase
block|{
DECL|method|parseElements
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|SearchParseElement
argument_list|>
name|parseElements
parameter_list|()
block|{
return|return
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"facets"
argument_list|,
operator|new
name|FacetsParseElement
argument_list|()
argument_list|)
return|;
block|}
DECL|method|preProcess
annotation|@
name|Override
specifier|public
name|void
name|preProcess
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{     }
DECL|method|execute
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|SearchContext
name|context
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
if|if
condition|(
name|context
operator|.
name|facets
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|context
operator|.
name|queryResult
argument_list|()
operator|.
name|facets
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// no need to compute the facets twice, they should be computed on a per context basis
return|return;
block|}
comment|// run global facets ...
if|if
condition|(
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|globalCollectors
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|Query
name|query
init|=
operator|new
name|DeletionAwareConstantScoreQuery
argument_list|(
name|Queries
operator|.
name|MATCH_ALL_FILTER
argument_list|)
decl_stmt|;
comment|// no need to cache a MATCH ALL FILTER
if|if
condition|(
name|context
operator|.
name|types
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|context
operator|.
name|types
argument_list|()
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|String
name|type
init|=
name|context
operator|.
name|types
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|DocumentMapper
name|docMapper
init|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|Filter
name|typeFilter
init|=
operator|new
name|TermFilter
argument_list|(
name|docMapper
operator|.
name|typeMapper
argument_list|()
operator|.
name|term
argument_list|(
name|docMapper
operator|.
name|type
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|typeFilter
operator|=
name|context
operator|.
name|filterCache
argument_list|()
operator|.
name|cache
argument_list|(
name|typeFilter
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|FilteredQuery
argument_list|(
name|query
argument_list|,
name|typeFilter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BooleanFilter
name|booleanFilter
init|=
operator|new
name|BooleanFilter
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|type
range|:
name|context
operator|.
name|types
argument_list|()
control|)
block|{
name|DocumentMapper
name|docMapper
init|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|Filter
name|typeFilter
init|=
operator|new
name|TermFilter
argument_list|(
name|docMapper
operator|.
name|typeMapper
argument_list|()
operator|.
name|term
argument_list|(
name|docMapper
operator|.
name|type
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|typeFilter
operator|=
name|context
operator|.
name|filterCache
argument_list|()
operator|.
name|cache
argument_list|(
name|typeFilter
argument_list|)
expr_stmt|;
name|booleanFilter
operator|.
name|add
argument_list|(
operator|new
name|FilterClause
argument_list|(
name|typeFilter
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|query
operator|=
operator|new
name|FilteredQuery
argument_list|(
name|query
argument_list|,
name|booleanFilter
argument_list|)
expr_stmt|;
block|}
block|}
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|useGlobalCollectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|NoopCollector
operator|.
name|NOOP_COLLECTOR
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryPhaseExecutionException
argument_list|(
name|context
argument_list|,
literal|"Failed to execute global facets"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|useGlobalCollectors
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
name|SearchContextFacets
name|contextFacets
init|=
name|context
operator|.
name|facets
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Facet
argument_list|>
name|facets
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|contextFacets
operator|.
name|facetCollectors
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|FacetCollector
name|facetCollector
range|:
name|contextFacets
operator|.
name|facetCollectors
argument_list|()
control|)
block|{
name|facets
operator|.
name|add
argument_list|(
name|facetCollector
operator|.
name|facet
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|context
operator|.
name|queryResult
argument_list|()
operator|.
name|facets
argument_list|(
operator|new
name|InternalFacets
argument_list|(
name|facets
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

